package gg.astromc.agones

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val debugLogging: Boolean = false,
    val allocate: Boolean = true,
    val health: HealthSection = HealthSection(),
    val agonesSDK: AgonesSDKSection = AgonesSDKSection(),
)

@Serializable
data class HealthSection(
    val enableHealthChecks: Boolean = true,
    val healthCheckInterval: Long = 15,
    val healthCheckStartDelay: Long = 0,
)

@Serializable
data class AgonesSDKSection(
    val sdkGrpcPort: Int = 9357,
    val sdkGrpcHost: String = "localhost"
)
