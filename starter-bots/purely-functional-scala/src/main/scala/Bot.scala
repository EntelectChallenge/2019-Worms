package bot

import scalaz.zio._
import scalaz.zio.console._
import scalaz.zio.random._
import cats._
import cats.implicits._
import io.circe.parser._
import io.circe.generic.auto._

object GameState {
  final case class Cell(x: Int, y: Int, `type`: String)

  type GameMap = Seq[Seq[Cell]]

  final case class State(
    currentRound: Int,
    maxRounds: Int,
    mapSize: Int,
    currentWormId: Int,
    consecutiveDoNothingCount: Int,
    myPlayer: Player,
    opponents: Seq[Opponent],
    map: GameMap)

  final case class Player(id: Int, score: Int, health: Int, worms: Seq[Worm])

  final case class Opponent(id: Int, score: Int, worms: Seq[OpponentWorm])

  final case class Worm(
    id: Int,
    health: Int,
    position: Coord,
    weapon: Weapon,
    diggingRange: Int,
    movementRange: Int)

  final case class OpponentWorm(
    id: Int,
    health: Int,
    position: Coord,
    diggingRange: Int,
    movementRange: Int)

  final case class Coord(x: Int, y: Int)

  final case class Weapon(damage: Int, range: Int)
}

trait FileReader {
  def reader: FileReader.Service[FileReader]
}

object FileReader {
  trait Service[R] {
    def readFile(path: String): ZIO[FileReader, Nothing, String]
  }

  trait Live extends FileReader {
    val reader: Service[FileReader] = new Service[FileReader] {
      override def readFile(path: String): ZIO[FileReader, Nothing, String] =
        ZIO.succeed(scala.io.Source.fromFile(path).mkString)
    }
  }
  object Live extends Live
}

object Bot {
  import GameState._

  def readGameState(round: Int): ZIO[FileReader, Any, State] =
    ZIO.accessM((_: FileReader).reader.readFile(show"./rounds/$round/state.json")) flatMap
  (fileContents => ZIO.fromEither(decode[State](fileContents)))

  sealed trait Direction
  object Directions {
    final case object East      extends Direction
    final case object NorthEast extends Direction
    final case object North     extends Direction
    final case object NorthWest extends Direction
    final case object West      extends Direction
    final case object SouthWest extends Direction
    final case object South     extends Direction
    final case object SouthEast extends Direction
  }

  import Directions._
  implicit val showDirection: Show[Direction] = Show.show {
    case East      => "E"
    case NorthEast => "NE"
    case North     => "N"
    case NorthWest => "NW"
    case West      => "W"
    case SouthWest => "SW"
    case South     => "S"
    case SouthEast => "SE"
  }

  sealed trait Decision
  object Decisions {
    final case object DoNothing extends Decision
    final case class  Shoot(direction: Direction) extends Decision
    final case class  Move(location: Coord) extends Decision
    final case class  Dig(location: Coord) extends Decision
  }

  import Decisions._
  implicit val showDecision: Show[Decision] = Show.show {
    case DoNothing        => "nothing"
    case Shoot(direction) => show"shoot $direction"
    case Move(location)   => show"move ${location.x} ${location.y}"
    case Dig(location)    => show"dig ${location.x} ${location.y}"
  }

  def hits(worm: Worm, opponent: Opponent): Seq[Direction] = {
    val position   = worm.position
    val weapon     = worm.weapon
    def doesHit(opponentWorm: OpponentWorm): Boolean = {
      val opPosition = opponentWorm.position
      aligns(position, opPosition) &&
      inRange(position, opPosition, weapon.range)
    }
    opponent.worms filter (doesHit _) map (opponent => directionFrom(position, opponent.position))
  }

  def directionFrom(from: Coord, to: Coord): Direction =
    (from.y == to.y, from.x == to.x, from.x > to.x, from.y > to.y) match {
      case (true,  false, true,  false) => West
      case (true,  false, false, false) => East
      case (false, true,  false, true)  => North
      case (false, true,  false, false) => South
      case (false, false, true,  true)  => NorthWest
      case (false, false, false, true)  => NorthEast
      case (false, false, true,  false) => SouthWest
      case (false, false, false, false) => SouthEast
    }

  def aligns(from: Coord, to: Coord): Boolean =
    from.x == to.x ||
  from.y == to.y ||
  (Math.abs(from.x - to.x) == Math.abs(from.y - to.y))

  def inRange(from: Coord, to: Coord, range: Int): Boolean = {
    val dx = from.x - to.x
    val dy = from.y - to.y
    Math.sqrt((dx * dx) + (dy * dy)) < range
  }

  def tryToShoot(currentWormId: Int, me: Player)(opponent: Opponent): Option[Decision] = for {
    currentWorm   <- me.worms find (_.id == currentWormId)
    val enemiesHit = hits(currentWorm, opponent)
    if (!enemiesHit.isEmpty)
  } yield Shoot(enemiesHit.head)

  def collides(coord: Coord, opponent: OpponentWorm): Boolean =
    coord.x == opponent.position.x &&
  coord.y == opponent.position.y

  import random._
  def tryToMoveRandomly(
    currentWormId: Int,
    me: Player,
    map: GameMap,
    opponent: Opponent): ZIO[Random, Nothing, Option[Decision]] = (for {
      x <- nextInt(3) map (_ - 1)
      y <- nextInt(3) map (_ - 1)
    } yield (x, y)) map { case (dx, dy) => for {
      currentWorm     <- me.worms find (_.id == currentWormId)
      val wormPosition = currentWorm.position
      val x            = wormPosition.x + dx
      val y            = wormPosition.y + dy
      val mapLength    = map.length
      if (!(x < 0 || y < 0 || x >= mapLength || y >= mapLength))
      if (!(wormPosition.x == x && wormPosition.y == y))
      result          <- translateToMoveOrDig(map(y)(x))
    } yield result }

  def translateToMoveOrDig: Cell => Option[Decision] = {
    case (Cell(x, y, "AIR"))        => Some(Move(Coord(x, y)))
    case (Cell(x, y, "DIRT"))       => Some(Dig(Coord(x, y)))
    case (Cell(x, y, "DEEP_SPACE")) => None
    case _                          => None
  }

  def makeMove(state: State): ZIO[Random, Any, Decision] = {
    val opponent = state.opponents.headOption

    (opponent match {
      case Some(op) => tryToMoveRandomly(state.currentWormId, state.myPlayer, state.map, op)
      case None     => ZIO.succeed(None)
    }) map ((randomMove: Option[Decision]) =>
    (Seq(opponent flatMap tryToShoot(state.currentWormId, state.myPlayer),
      randomMove,
      Some(DoNothing)) collect { case Some(x) => x }).head)
  }

  def startBot(roundNumber: Int): ZIO[Console with Random with FileReader, Any, Unit] = for {
    _      <- getStrLn map (_.toInt)
    state  <- readGameState(roundNumber)
    move   <- makeMove(state)
    _      <- console.putStrLn(show"C;$roundNumber;$move")
    result <- startBot(roundNumber + 1)
  } yield result
}

object BotRunner extends App {
  val environment = new Console.Live with Random.Live with FileReader.Live

  def handleError(error: Any): ZIO[Console, Nothing, Int] = for {
    _      <- console.putStrLn(s"An error occured: ${error}\nWith type: ${error.getClass}")
    result <- run(List.empty)
  } yield result

  def run(args: List[String]) =
    Bot.startBot(1)
      .provide(environment)
      .foldM(handleError(_), _ => ZIO.succeed(0))
}
