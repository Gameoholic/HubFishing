package xyz.gameoholic.hubfishing.player.data.sql

import com.zaxxer.hikari.HikariDataSource
import xyz.gameoholic.hubfishing.player.data.PlayerData
import xyz.gameoholic.hubfishing.HubFishingPlugin
import xyz.gameoholic.hubfishing.fish.FishVariant
import xyz.gameoholic.hubfishing.injection.inject
import xyz.gameoholic.hubfishing.util.LoggerUtil
import java.lang.RuntimeException
import java.util.UUID


/**
 * Class managing SQL database operations.
 * Creates all needed tables and columns on initialization.
 */
class SQLManager {
    private val plugin: HubFishingPlugin by inject()

    private val url = "jdbc:mysql://${plugin.config.sql.sqlIP}:" +
        "${plugin.config.sql.sqlPort}/${plugin.config.sql.sqlDatabaseName}"

    private val dataSource = HikariDataSource()

    init {
        createDataSource()
        createTable().onFailure {
            LoggerUtil.error("Could not create table! Cause: ${it.cause} Message: ${it.message}")
            it.printStackTrace()
        }
        createFishVariantsColumns().onFailure {
            LoggerUtil.error("Could not create fish variants columns! Cause: ${it.cause} Message: ${it.message}")
            it.printStackTrace()
        }
    }

    private fun createDataSource() {
        dataSource.jdbcUrl = url
        dataSource.username = plugin.config.sql.sqlUsername
        dataSource.password = plugin.config.sql.sqlPassword
    }

    private fun execIntQuery(query: String): Result<Int> {
        try {
            dataSource.connection.use {
                val statement = it.prepareStatement(query)
                val result = statement.executeQuery()
                if (result.next())
                    return Result.success(result.getInt(1))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
        return Result.failure(RuntimeException("Neither value, nor exception was assigned a value."))
    }

    private fun execUpdateQuery(query: String): Result<Unit> {
        try {
            dataSource.connection.use {
                val statement = it.prepareStatement(query)
                statement.executeUpdate()
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
        return Result.success(Unit)
    }


    private fun createFishVariantsColumns(): Result<Unit> {
        plugin.config.fishVariants.variants.forEach {
            createColumnIfNotExists("${it.id}_fishes_caught").onFailure { throwable ->
                return Result.failure(throwable)
            }
            createColumnIfNotExists("${it.id}_fishes_uncaught").onFailure { throwable ->
                return Result.failure(throwable)
            }
        }
        return Result.success(Unit)
    }

    private fun createColumnIfNotExists(columnName: String): Result<Unit> {
        val result = execIntQuery(
            """
                SELECT 
                  COUNT(*) 
                FROM 
                  INFORMATION_SCHEMA.COLUMNS 
                WHERE 
                  TABLE_NAME = 'fishing_player_data' 
                  AND COLUMN_NAME = '$columnName';
            """.trimIndent()
        )

        if (result.getOrElse { return Result.failure(it) } == 0) {
            execUpdateQuery(
                """
                ALTER TABLE 
                  fishing_player_data 
                ADD 
                  COLUMN $columnName INT NOT NULL DEFAULT 0;
            """.trimIndent()
            ).onFailure { return Result.failure(it) }
        }
        return Result.success(Unit)
    }

    private fun createTable(): Result<Unit> {
        execUpdateQuery(
            """
            CREATE TABLE IF NOT EXISTS fishing_player_data (
                uuid VARCHAR(36) NOT NULL,
                xp INT NOT NULL DEFAULT 0,
                playtime INT NOT NULL DEFAULT 0,
                PRIMARY KEY(uuid)
            );
        """.trimIndent()
        ).onFailure { return Result.failure(it) }
        return Result.success(Unit)
    }

    /**
     * Inserts a player into the table. If already exists, nothing happens.
     */
    private fun insertPlayer(playerUUID: UUID): Result<Unit> {
        execUpdateQuery(
            """
            INSERT IGNORE INTO fishing_player_data
             (uuid) values('$playerUUID');
        """.trimIndent()
        ).onFailure { return Result.failure(it) }
        return Result.success(Unit)
    }

    fun fetchPlayerData(playerUUID: UUID): Result<PlayerData> {
        insertPlayer(playerUUID).onFailure { return Result.failure(it) }

        var playerData: PlayerData? = null
        try {
            dataSource.connection.use {connection ->
                val statement = connection.prepareStatement(
                """
                SELECT 
                  *
                FROM 
                  fishing_player_data
                WHERE 
                  uuid = '${playerUUID}';
            """.trimIndent()
                )
                val result = statement.executeQuery()

                if (result.next()) {
                    var xp: Int = -1
                    var playtime: Int = -1
                    var fishesCaught: HashMap<FishVariant, Int> = hashMapOf()
                    var fishesUncaught: HashMap<FishVariant, Int> = hashMapOf()

                    val metaData = result.metaData
                    val columnCount = metaData.columnCount
                    for (i in 1..columnCount) {
                        val column = metaData.getColumnName(i)
                        if (column == "xp")
                            xp = result.getInt(i)
                        else if (column == "playtime")
                            playtime = result.getInt(i)
                        else if (column.endsWith("_fishes_caught")) {
                            val fishVariantId = column.split("_fishes_caught")[0]
                            val amount = result.getInt(i)
                            plugin.config.fishVariants.variants.firstOrNull { it.id == fishVariantId }?.let {
                                fishesCaught[it] = amount
                            }
                        } else if (column.endsWith("_fishes_uncaught")) {
                            val fishVariantId = column.split("_fishes_uncaught")[0]
                            val amount = result.getInt(i)
                            plugin.config.fishVariants.variants.firstOrNull { it.id == fishVariantId }?.let {
                                fishesUncaught[it] = amount
                            }
                        }
                    }

                    playerData = PlayerData(xp, playtime, fishesCaught, fishesUncaught)
                }
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
        playerData?.let {
            return Result.success(it)
        }
        return Result.failure(RuntimeException("Playerdata is null"))
    }

    /**
     * Uploads the player data for the given player.
     * @return True if the operation succeeded, false otherwise.
     */
    fun uploadPlayerData(playerData: PlayerData, playerUUID: UUID): Result<Unit> {
        var query = """
            UPDATE fishing_player_data 
            SET xp = ${playerData.xp}, playtime = ${playerData.playtime}
            """.trimIndent()
        playerData.fishesCaught.forEach {
            query += ", ${it.key.id}_fishes_caught = ${it.value}"
        }
        playerData.fishesUncaught.forEach {
            query += ", ${it.key.id}_fishes_uncaught = ${it.value}"
        }
        query += """
             WHERE 
            uuid = '${playerUUID}';
        """.trimIndent()
        return execUpdateQuery(query)
    }

    fun closeDataSource() {
        dataSource.close()
    }
}