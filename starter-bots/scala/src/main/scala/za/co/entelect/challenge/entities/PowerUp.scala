package za.co.entelect.challenge.entities

import za.co.entelect.challenge.enums.PowerUpType
import za.co.entelect.challenge.enums.PowerUpType.PowerUpType

case class PowerUp(`type`: PowerUpType,
                   value: Int)