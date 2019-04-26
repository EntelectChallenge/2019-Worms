package za.co.entelect.challenge.enums

import enumeratum.Json4s
import za.co.entelect.challenge.enums.CellType.CellTypes
import za.co.entelect.challenge.enums.PowerUpType.PowerUpTypes
import za.co.entelect.challenge.enums.Direction.Directions

object Serializers {
    val enumSerializers = List(
      Json4s.serializer(Directions),
      Json4s.serializer(CellTypes),
      Json4s.serializer(PowerUpTypes),
    )
}
