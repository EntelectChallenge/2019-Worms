package za.co.entelect.challenge.game.engine.player

interface Worm {
    var health: Int

    val dead: Boolean
        get() = health == 0

}
