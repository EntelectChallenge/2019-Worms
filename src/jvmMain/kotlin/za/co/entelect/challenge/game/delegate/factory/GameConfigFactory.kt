package za.co.entelect.challenge.game.delegate.factory

import com.google.gson.Gson
import za.co.entelect.challenge.game.engine.config.GameConfig
import java.nio.file.Paths

object GameConfigFactory {

    private val gson = Gson()

    fun getConfig(filePath: String): GameConfig {
        val path = Paths.get(filePath)
        return gson.fromJson(path.toFile().readText(), GameConfig::class.java)
    }

}
