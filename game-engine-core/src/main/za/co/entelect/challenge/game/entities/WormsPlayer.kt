package za.co.entelect.challenge.game.entities

class WormsPlayer(val id: Int) {
    fun getHealth(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getScore(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var consecutiveDoNothings = 0

    val dead
        get() = false
}