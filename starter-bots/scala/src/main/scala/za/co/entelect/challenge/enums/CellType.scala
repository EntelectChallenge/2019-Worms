package za.co.entelect.challenge.enums

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable.IndexedSeq

object CellType{
  sealed trait CellTypes extends EnumEntry

  object CellTypes extends Enum[CellTypes] {
    case object DEEP_SPACE extends CellTypes
    case object DIRT       extends CellTypes
    case object AIR        extends CellTypes

    override def values: IndexedSeq[CellTypes] = findValues
  }

}
