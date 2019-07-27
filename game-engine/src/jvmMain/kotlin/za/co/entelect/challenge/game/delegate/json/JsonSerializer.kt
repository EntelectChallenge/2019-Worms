package za.co.entelect.challenge.game.delegate.json

import com.google.gson.Gson

actual class JsonSerializer {

    private val gson = Gson()

    actual fun toJson(obj: Any): String {
        return gson.toJson(obj)
    }

}
