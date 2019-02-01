package ec.game.engine

class App {
    val greeting: String
        get() = "Hello world."
}

fun main(args: Array<String>) {
    println(App().greeting)
}
