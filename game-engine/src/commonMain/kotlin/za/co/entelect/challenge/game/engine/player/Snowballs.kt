package za.co.entelect.challenge.game.engine.player

data class Snowballs(val freezeDuration: Int,
                     val range: Int,
                     var count: Int,
                     val freezeRadius: Int) {

    companion object {
        fun fromSnowballs(snowballs: Snowballs?): Snowballs? {
            return when {
                snowballs != null -> Snowballs(
                        snowballs.freezeDuration,
                        snowballs.range,
                        snowballs.count,
                        snowballs.freezeRadius)
                else -> null
            }
        }
    }

}
