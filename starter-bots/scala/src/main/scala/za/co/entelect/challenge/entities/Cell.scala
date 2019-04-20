package za.co.entelect.challenge.entities

import za.co.entelect.challenge.enums.CellType

case class Cell(x: Int,
                y: Int,
                `type`: CellType,
                powerUp: PowerUp)