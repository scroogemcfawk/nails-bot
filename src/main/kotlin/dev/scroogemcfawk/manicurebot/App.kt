package dev.scroogemcfawk.manicurebot

import dev.scroogemcfawk.manicurebot.config.Config
import kotlinx.serialization.json.Json
import org.tinylog.Logger
import java.io.File

/**
 * This method by default expects one argument in [args] field: telegram bot configuration
 */
suspend fun main(args: Array<String>) {

    val json = Json { ignoreUnknownKeys = true }

    val configFile = File(args.first())

    val config = try {
        json.decodeFromString(Config.serializer(), configFile.readText())
    } catch (e: Exception) {
        Logger.error{ "Failed get config: $e" }
        return
    }

    val con = try {
        val dbManager = DbManager(config.databaseName)
        dbManager.initDataBase()
//        dbManager.dropData()

         dbManager.con
    } catch (e: Exception) {
        Logger.error{ "Failed database initialization: $e" }
        return
    }

    try {
        val bot = Bot(config, con, configFile)

        bot.run().join()
    } catch (e: Exception) {
        Logger.error{ e.message }
    }
}
