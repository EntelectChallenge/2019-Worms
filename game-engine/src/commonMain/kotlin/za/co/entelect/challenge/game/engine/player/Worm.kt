package za.co.entelect.challenge.game.engine.player

import za.co.entelect.challenge.game.engine.interfaces.Printable
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.map.WormsMap

open class Worm(val id: Int,
                var health: Int,
                val weapon: Weapon,
                var bananas: Bananas? = null,
                var snowballs: Snowballs? = null,
                val diggingRange: Int,
                val movementRange: Int,
                var roundsUntilUnfrozen: Int = 0,
                val profession: String) : Printable {

    constructor(id: Int,
                health: Int,
                position: Point,
                weapon: Weapon,
                bananas: Bananas? = null,
                snowballs: Snowballs? = null,
                diggingRange: Int = 1,
                movementRange: Int = 1,
                profession: String)
            : this(id, health, weapon, bananas, snowballs, diggingRange, movementRange, 0, profession) {
        this.position = position
        this.previousPosition = position
    }

    override val printable
        get() = "${player.id}$id"

    var roundMoved: Int = -1
        private set

    var roundHit: Int = -1
        private set

    lateinit var position: Point
        private set

    lateinit var previousPosition: Point
        private set

    lateinit var player: WormsPlayer

    val dead: Boolean
        get() = health <= 0

    val lastAttackedBy: MutableList<WormsPlayer> = mutableListOf()

    /**
     * Set position and previous position to the same value
     */
    fun initPositions(position: Point) {
        this.position = position
        this.previousPosition = position
    }

    /**
     * Apply movement logic
     * - Clear occupier of current map cell
     * - Set occupier of target map cell
     * - Update previous position
     * - Update current position
     * - Set last movement round
     * - Pick up powerups
     */
    fun moveTo(gameMap: WormsMap, target: Point) {
        val originCell = gameMap[position]
        val targetCell = gameMap[target]

        originCell.occupier = null

        previousPosition = position
        roundMoved = gameMap.currentRound
        position = target

        targetCell.occupier = this

    }

    /**
     * Applies damage to worm, and returns the amount of damage that was done
     */
    fun takeDamage(damage: Int, round: Int, attacker: WormsPlayer? = null): Int {
        health -= damage
        roundHit = round

        if (attacker != null) lastAttackedBy.add(attacker)
        return damage
    }

    /**
     * If this worm is given a command, they will *let it go* until they thaw out
     */
    fun setAsFrozen(freezeDuration: Int) {
        roundsUntilUnfrozen = freezeDuration
    }

    override fun toString(): String {
        return "Worm(player=${player.id}, id=$id)"
    }

    fun tickFrozenTimer() {
        roundsUntilUnfrozen = (--roundsUntilUnfrozen).coerceAtLeast(0)
    }

}
