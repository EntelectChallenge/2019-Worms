package za.co.entelect.challenge.enums

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable.IndexedSeq

object Direction {
  sealed trait Direction extends EnumEntry

  object Directions extends Enum[Direction] {
    case object N extends Direction
    case object NE extends Direction
    case object E extends Direction
    case object SE extends Direction
    case object S extends Direction
    case object SW extends Direction
    case object W extends Direction
    case object NW extends Direction

    override def values: IndexedSeq[Direction] = findValues
  }

  def matchDirection(direction: Direction): DirectionObject = {
    direction.entryName match {
      case "N"  => DirectionObject(0, -1)
      case "NE" => DirectionObject(1, -1)
      case "E"  => DirectionObject(1, 0)
      case "SE" => DirectionObject(1, 1)
      case "S"  => DirectionObject(0, 1)
      case "SW" => DirectionObject(-1, 1)
      case "W"  => DirectionObject(-1, 0)
      case "NW" => DirectionObject(-1, -1)
    }
  }

  case class DirectionObject(x: Int, y: Int)
}
