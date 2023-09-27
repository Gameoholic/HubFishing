package net.topstrix.hubinteractions.fishing.data.sql
import com.zaxxer.hikari.HikariDataSource
import net.topstrix.hubinteractions.fishing.data.PlayerData
import net.topstrix.hubinteractions.fishing.fish.FishVariant
import net.topstrix.hubinteractions.fishing.util.FishingUtil
import net.topstrix.hubinteractions.fishing.util.LoggerUtil
import java.sql.Connection
import java.util.UUID

object SQLUtil {
    private val url = "jdbc:mysql://${FishingUtil.fishingConfig.sqlIP}:" +
        "${FishingUtil.fishingConfig.sqlPort}/${FishingUtil.fishingConfig.sqlDatabaseName}"

    private val dataSource = HikariDataSource()

    fun load(fishVariants: List<FishVariant>) {
        createDataSource()
        createTable()
        createFishVariantsColumns(fishVariants)
    }

    private fun createDataSource() {
        dataSource.jdbcUrl = url
        dataSource.username = FishingUtil.fishingConfig.sqlUsername
        dataSource.password = FishingUtil.fishingConfig.sqlPassword
    }
    private fun execIntQuery(query: String): Int? {
        var connection: Connection? = null
        var value: Int? = null
        try {
            connection = dataSource.connection

            val statement = connection.prepareStatement(query)
            val result = statement.executeQuery()

            if (result.next())
                value = result.getInt(1)
        }
        catch (e: Exception) {
            LoggerUtil.error("Couldn't execute query: $query. Error: $e")
            e.printStackTrace()
        }
        connection?.close()
        return value
    }
    private fun execUpdateQuery(query: String): Boolean {
        var connection: Connection? = null
        var succees = true
        try {
            connection = dataSource.connection


            val statement = connection.prepareStatement(query)
            statement.executeUpdate()
        }
        catch (e: Exception) {
            succees = false
            LoggerUtil.error("Couldn't execute update query: $query. Error: $e")
        }
        connection?.close()
        return succees
    }


    private fun createFishVariantsColumns(fishVariants: List<FishVariant>) {
        fishVariants.forEach {
            createColumnIfNotExists("${it.id}_fishes_caught")
            createColumnIfNotExists("${it.id}_fishes_uncaught")
        }
    }
    private fun createColumnIfNotExists(columnName: String) {
        val result = execIntQuery("""
                SELECT 
                  COUNT(*) 
                FROM 
                  INFORMATION_SCHEMA.COLUMNS 
                WHERE 
                  TABLE_NAME = 'fishing_player_data' 
                  AND COLUMN_NAME = '$columnName';
            """.trimIndent())

        if (result == 0) {
            execUpdateQuery("""
                ALTER TABLE 
                  fishing_player_data 
                ADD 
                  COLUMN $columnName INT NOT NULL DEFAULT 0;
            """.trimIndent())
        }
    }

    private fun createTable() {
        execUpdateQuery("""
            CREATE TABLE IF NOT EXISTS fishing_player_data (
                uuid VARCHAR(36) NOT NULL,
                xp INT NOT NULL DEFAULT 0,
                playtime INT NOT NULL DEFAULT 0,
                PRIMARY KEY(uuid)
            );
        """.trimIndent())
    }

    /**
     * Inserts a player into the table. If already exists, nothing happens.
     */
    private fun insertPlayer(playerUUID: UUID) {
        execUpdateQuery("""
            INSERT IGNORE INTO fishing_player_data
             (uuid) values('$playerUUID');
        """.trimIndent())
    }
    fun fetchPlayerData(playerData: PlayerData) {
        insertPlayer(playerData.playerUUID)
        var connection: Connection? = null
        try {
            connection = dataSource.connection

            val statement = connection.prepareStatement("""
                SELECT 
                  *
                FROM 
                  fishing_player_data
                WHERE 
                  uuid = '${playerData.playerUUID}';
            """.trimIndent())
            val result = statement.executeQuery()

            if (result.next()) {
                playerData.fishesCaught = hashMapOf()
                playerData.fishesUncaught = hashMapOf()

                val metaData = result.metaData
                val columnCount = metaData.columnCount
                for (i in 1..columnCount) {
                    val column = metaData.getColumnName(i)
                    if (column == "xp")
                        playerData.xp = result.getInt(i)
                    else if (column == "playtime")
                        playerData.playtime = result.getInt(i)
                    else if (column.endsWith("_fishes_caught")) {
                        val fishVariantId = column.split("_fishes_caught")[0]
                        val amount = result.getInt(i)
                        FishingUtil.fishingConfig.fishVariants.firstOrNull { it.id == fishVariantId }?.let {
                            playerData.fishesCaught!![it] = amount
                        }
                    }
                    else if (column.endsWith("_fishes_uncaught")) {
                        val fishVariantId = column.split("_fishes_uncaught")[0]
                        val amount = result.getInt(i)
                        FishingUtil.fishingConfig.fishVariants.firstOrNull { it.id == fishVariantId }?.let {
                            playerData.fishesUncaught!![it] = amount
                        }
                    }

                }
            }
        }
        catch (e: Exception) {
            LoggerUtil.error("Couldn't execute get player fishing data. Error: $e")
            e.printStackTrace()
        }
        connection?.close()
    }
    fun uploadPlayerData(playerData: PlayerData) {
        var query = """
            UPDATE fishing_player_data 
            SET xp = ${playerData.xp}, playtime = ${playerData.playtime}
            """.trimIndent()
        playerData.fishesCaught!!.forEach {
            query += ", ${it.key.id}_fishes_caught = ${it.value}"
        }
        playerData.fishesUncaught!!.forEach {
            query += ", ${it.key.id}_fishes_uncaught = ${it.value}"
        }
        query += """
             WHERE 
            uuid = '${playerData.playerUUID}';
        """.trimIndent()
        execUpdateQuery(query)
    }
}