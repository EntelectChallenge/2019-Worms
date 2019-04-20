package za.co.entelect.challenge.enums

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable.IndexedSeq

sealed trait CellType extends EnumEntry

object CellTypes extends Enum[CellType] {
  case object DEEP_SPACE extends CellType
  case object DIRT       extends CellType
  case object AIR        extends CellType

  override def values: IndexedSeq[CellType] = findValues
}
