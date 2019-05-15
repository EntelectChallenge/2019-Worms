package za.co.entelect.challenge

import za.co.entelect.challenge.commands.{Command, DigCommand, DoNothingCommand, MoveCommand, ShootCommand}
import za.co.entelect.challenge.entities.{Cell, GameState, MyWorm, Opponent, Position, Worm}
import za.co.entelect.challenge.enums.CellType.CellTypes
import za.co.entelect.challenge.enums.{CellType, Direction}
import za.co.entelect.challenge.enums.Direction.{Direction, DirectionObject, Directions}

import scala.collection.mutable.ListBuffer
import scala.util.Random

class Bot(gameState: GameState, random: Random){
  private val opponent: Opponent  = gameState.opponents.head
  private val currentWorm: MyWorm = getCurrentWorm(gameState)

  private def euclideanDistance(aX: Int, aY: Int, bX: Int, bY: Int): Int = {
    Math.sqrt(
      Math.pow(aX - bX, 2) + Math.pow(aY - bY, 2)
    ).toInt
  }

  private def isValidCoordinate(x: Int, y: Int): Boolean = {
    x >= 0 && x < gameState.mapSize && y >= 0 && y < gameState.mapSize
  }

  private def getCurrentWorm(gameState: GameState): MyWorm = {
    gameState.myPlayer.worms
      .filter(myWorm => myWorm.id == gameState.currentWormId)
      .head
  }

  private def resolveDirection(a: Position, b: Position): Direction = {
    val verticalComponent = b.y - a.y
    val horizontalComponent = b.x - a.x

    val builder: StringBuilder = new StringBuilder()

    if     (verticalComponent < 0) builder.append('N')
    else if(verticalComponent > 0) builder.append('S')

    if     (horizontalComponent < 0) builder.append('W')
    else if(horizontalComponent > 0) builder.append('E')

    Directions.withNameInsensitive(builder.toString())
  }

  private def getSurroundingCells(x: Int, y: Int): List[Cell] = {
    val cells: ListBuffer[Cell] = ListBuffer.empty[Cell]

    for (
      i <- Range(x - 1, x + 2);
      j <- Range(y - 1, y + 2)
    ) {
        if(i != x && j != y && isValidCoordinate(i, j)) {
          cells += gameState.map(j)(i)
        }
      }

    cells.toList
  }

  private def constructFireDirectionLines(range: Int): List[List[Cell]] = {
    val directionLines: ListBuffer[List[Cell]] = ListBuffer.empty[List[Cell]]

    for (direction: Direction <- Directions.values) {
      val directionLine: ListBuffer[Cell] = ListBuffer.empty[Cell]
      val directionObject: DirectionObject = Direction.matchDirection(direction)

      for (directionMultiplier <- Range(1, range + 1)) {
        val coordinateX: Int = currentWorm.position.x + (directionMultiplier * directionObject.x)
        val coordinateY: Int = currentWorm.position.y + (directionMultiplier * directionObject.y)

        val checkValidCoordinate           = isValidCoordinate(coordinateX, coordinateY)
        val checkOutOfRange                = !(euclideanDistance(currentWorm.position.x, currentWorm.position.y, coordinateX, coordinateY) > range)
        val checkCellType: Cell => Boolean = { cell => cell.tpe == CellTypes.AIR }

        if (checkValidCoordinate && checkOutOfRange) {
          val cell: Cell = gameState.map(coordinateY)(coordinateX)
          if (checkCellType(cell)) {
            directionLine += gameState.map(coordinateY)(coordinateX)

          }
        }
      }
      directionLines += directionLine.toList
    }
    directionLines.toList
  }

  private def getFirstWormInRange(): Option[Worm] = {
    val cells: List[String] =
      for {
        cells <- constructFireDirectionLines(currentWorm.weapon.range)
        cell  <- cells
      } yield s"${cell.x}_${cell.y}"

    opponent.worms.find(enemyWorm => {
      val enemyPosition = s"${enemyWorm.position.x}_${enemyWorm.position.y}"
      cells.contains(enemyPosition)
    })
  }

  def run(): Command = {
    val enemyWorm = getFirstWormInRange()
    enemyWorm match {
      case Some(worm) => {
        val direction: Direction = resolveDirection(currentWorm.position, worm.position)
        ShootCommand(direction)
      }
      case None       => {
        val surroundingBlocks: List[Cell] = getSurroundingCells(currentWorm.position.x, currentWorm.position.y)
        println(s"\n =========== \n ${surroundingBlocks.size} \n =========== \n")
        val cellIdx = random.nextInt(surroundingBlocks.length)
        println(s"\n =========== \n ${cellIdx} \n =========== \n")

        val block: Cell = surroundingBlocks(cellIdx)
        block.tpe match {
          case CellTypes.AIR        => MoveCommand(block.x, block.y)
          case CellTypes.DIRT       => DigCommand(block.x, block.y)
          case CellTypes.DEEP_SPACE => DoNothingCommand()
        }
      }
    }
  }
}

object Bot {
  def apply(gameState: GameState, random: Random): Bot = {
    new Bot(gameState, random)
  }
}

