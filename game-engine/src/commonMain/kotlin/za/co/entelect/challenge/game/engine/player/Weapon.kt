package za.co.entelect.challenge.game.engine.player

class Weapon(val damage: Int,
             val range: Int) {

    companion object {
        fun fromWeapon(weapon: Weapon): Weapon = Weapon(
                weapon.damage,
                weapon.range)
    }

}
