package za.co.entelect.challenge.game.engine.player

data class Bananas(val damage: Int,
                   val range: Int,
                   var count: Int,
                   val damageRadius: Int) {

    companion object {
        fun fromBananas(bananas: Bananas?): Bananas? {
            return when {
                bananas != null -> Bananas(
                        bananas.damage,
                        bananas.range,
                        bananas.count,
                        bananas.damageRadius)
                else -> null
            }
        }
    }

}
