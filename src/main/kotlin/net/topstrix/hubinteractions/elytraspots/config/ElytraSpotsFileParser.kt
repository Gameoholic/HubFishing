package net.topstrix.hubinteractions.elytraspots.config

import com.charleskorn.kaml.Yaml
import net.topstrix.hubinteractions.HubInteractions
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

object ElytraSpotsFileParser {

    /**
     * Parses the elytraspots.yml file and returns the config for it.
     * @return The config for elytra spots
     */
    fun parseFile(): ElytraSpotsConfig {
        val filePath = File(HubInteractions.plugin.dataFolder, "elytraspots.yml").path
        val input = Files.readString(Path.of(filePath))

        return Yaml.default.decodeFromString(ElytraSpotsConfig.serializer(), input)
    }


}