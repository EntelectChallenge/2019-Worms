package za.co.entelect.challenge.enums

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable.IndexedSeq

object PowerUpType {
  sealed trait PowerUpType extends EnumEntry

  object PowerUpTypes extends Enum[PowerUpType] {
    case object HEALTH_PACK extends PowerUpType

    override def values: IndexedSeq[PowerUpType] = findValues
  }
}
