package gg.astromc.agones

import com.charleskorn.kaml.Yaml
import dev.cubxity.libs.agones.AgonesSDK
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import me.codyq.envschema.EnvSchema
import net.minestom.server.MinecraftServer
import net.minestom.server.extensions.Extension
import net.minestom.server.utils.time.TimeUnit
import java.io.File

class AgonesExtension : Extension() {

    private lateinit var agones: AgonesSDK

    override fun initialize(): LoadStatus {
        // Load the config
        dataDirectory().toFile().mkdirs()

        val configFile = File(dataDirectory().toFile(), "config.yml")
        if (!configFile.exists()) {
            configFile.writeText(Yaml.default.encodeToString(Config()))
        }

        val config = EnvSchema.load(
            Yaml.default.decodeFromString<Config>(configFile.readText()),
            prefix = "EXT_AGONES_",
        )

        // Setup the SDK
        agones = AgonesSDK(
            config.agonesSDK.sdkGrpcHost,
            config.agonesSDK.sdkGrpcPort,
        )

        // Prepare the server
        runBlocking {
            agones.ready()
            if (config.debugLogging) {
                logger().info("Server marked as 'ready' with agones")
            }
            if (config.allocate) {
                agones.allocate()
                if (config.debugLogging) {
                    logger().info("Server marked as 'allocated' with agones")
                }
            }
        }

        // If the user has health checks enabled in their config, start running them
        if (config.health.enableHealthChecks) {
            MinecraftServer.getSchedulerManager().buildTask {
                if (config.debugLogging) {
                    logger().info("Sending out a health check")
                }
                runBlocking {
                    agones.health(flow {})
                }
            }
                .repeat(config.health.healthCheckInterval, TimeUnit.SECOND)
                .delay(config.health.healthCheckStartDelay, TimeUnit.SECOND)
                .schedule()
        }

        return LoadStatus.SUCCESS
    }

    override fun terminate() {
        runBlocking {
            agones.shutdown()
        }

        agones.close()
    }

}
