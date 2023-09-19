package net.topstrix.hubinteractions.fishing.config

import com.charleskorn.kaml.Yaml
import net.topstrix.hubinteractions.HubInteractions
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

object FishingFileParser {

    /**
     * Parses the fishing.yml file and returns the config for it.
     * @return The config for elytra spots
     */
    fun parseFile(): FishingConfig {
        val filePath = File(HubInteractions.plugin.dataFolder, "fishing.yml").path
        val input = Files.readString(Path.of(filePath))

        return Yaml.default.decodeFromString(FishingConfig.serializer(), input)
    }


}