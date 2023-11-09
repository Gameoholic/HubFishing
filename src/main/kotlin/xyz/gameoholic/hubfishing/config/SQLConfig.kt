package xyz.gameoholic.hubfishing.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SQLConfig(
    @SerialName("sql-ip") val sqlIP: String,
    @SerialName("sql-port") val sqlPort: Int,
    @SerialName("sql-database-name") val sqlDatabaseName: String,
    @SerialName("sql-username") val sqlUsername: String,
    @SerialName("sql-password") val sqlPassword: String,
    @SerialName("sql-query-timeout") val sqlQueryTimeout: Long,
)
