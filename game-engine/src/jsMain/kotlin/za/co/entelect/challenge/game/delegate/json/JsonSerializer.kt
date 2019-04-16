package za.co.entelect.challenge.game.delegate.json

actual class JsonSerializer {

    actual fun toJson(obj: Any): String {
        return JSON.stringify(obj)
    }

}