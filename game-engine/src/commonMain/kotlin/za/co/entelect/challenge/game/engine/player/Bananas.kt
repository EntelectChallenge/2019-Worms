package za.co.entelect.challenge.game.engine.player

class Bananas(val damage: Int,
              val range: Int,
              var count: Int,
              val damageRadius: Int) {

    constructor(bananas: Bananas) : this(bananas.damage, bananas.range, bananas.count, bananas.damageRadius)

}
