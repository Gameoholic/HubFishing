package net.topstrix.hubinteractions.fishing.sql
import java.sql.DriverManager
object SQLUtil {
    private val ip = "localhost"
    private val port = 3307
    private val dbName = "fishing"

    private val url = "jdbc:mysql://$ip:$port/$dbName"
    private val username = "user"
    private val password = "pass"


    fun execQuery() {
        val connection = DriverManager
            .getConnection(url, username, password)

        val statement = connection.prepareStatement("")
        val result = statement.executeQuery()

        while (result.next()) {

        }
    }
}