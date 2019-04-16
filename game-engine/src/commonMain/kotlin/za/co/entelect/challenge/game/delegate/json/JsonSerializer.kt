package za.co.entelect.challenge.game.delegate.json

expect class JsonSerializer() {

    fun toJson(obj: Any): String
}