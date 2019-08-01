package za.co.entelect.challenge.entities

import za.co.entelect.challenge.enums.CellType.CellTypes

case class Cell(x: Int,
                y: Int,
                tpe: CellTypes,
                powerUp: Option[PowerUp])