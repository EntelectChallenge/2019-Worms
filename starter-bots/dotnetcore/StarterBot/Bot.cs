using System;
using System.Collections.Generic;
using System.Linq;
using StarterBot.Entities;
using StarterBot.Entities.Commands;
using StarterBot.Enums;
using StarterBot.Exceptions;

namespace StarterBot
{
    public class Bot
    {
        private readonly GameState gameState;

        public Bot(GameState newGameState)
        {
            gameState = newGameState;
        }

        public string Run()
        {
            ICommand command;

            var currentActiveWorm = gameState.MyPlayer.Worms.FirstOrDefault(x => x.Id == gameState.CurrentWormId);
            if (currentActiveWorm == null)
            {
                throw new InvalidWormIdException(
                    $"Could not find Worm with id={gameState.CurrentWormId} the worm does not exist or an invalid id was used.");
            }

            var opponentWorms = gameState.Opponents.First().Worms.Where(worm => worm.Health > 0);

            var opponentWormsInRangeOfActiveWorm =
                GetOpponentWormsInRangeOfActiveWorm(opponentWorms, currentActiveWorm);

            var opponentWormsWithoutObstaclesInRange =
                GetOpponentWormsWithoutObstacles(opponentWormsInRangeOfActiveWorm, currentActiveWorm);

            if (opponentWormsWithoutObstaclesInRange.Any())
            {
                var targetWorm = opponentWormsWithoutObstaclesInRange.First();
                var shotDirection = GetShootDirection(targetWorm, currentActiveWorm);

                command = new ShootCommand() {Direction = shotDirection};
            }
            else
            {
                command = GetRandomCommand(currentActiveWorm);
            }


            return command?.RenderCommand();
        }

        private ICommand GetRandomCommand(Worm currentActiveWorm)
        {
            ICommand command;
            var random = new Random();

            var validCells = GetValidAdjacentCells(currentActiveWorm);

            if (!validCells.Any())
            {
                return new DoNothingCommand();
            }

            var randomCell = validCells[random.Next(0, validCells.Length)];
            var randomCellPosition = new MapPosition() {X = randomCell.X, Y = randomCell.Y};

            switch (randomCell.Type)
            {
                case CellType.AIR:
                    command = new MoveCommand()
                    {
                        MapPosition = randomCellPosition
                    };
                    break;
                case CellType.DIRT:
                    command = new DigCommand()
                    {
                        MapPosition = randomCellPosition
                    };
                    break;
                default:
                    command = new DoNothingCommand();
                    break;
            }

            return command;
        }

        private CellStateContainer[] GetValidAdjacentCells(Worm currentActiveWorm)
        {
            var adjacentCells = new List<CellStateContainer>();

            var currentY = currentActiveWorm.Position.Y;
            var currentX = currentActiveWorm.Position.X;

            var map = gameState.Map;

            for (var i = -1; i <= 1; i++)
            {
                for (var j = -1; j <= 1; j++)
                {
                    var nextY = currentY + i;
                    var nextX = currentX + j;

                    if (nextY < 0 || nextY >= map.Length || nextX < 0 || nextX >= map.Length)
                    {
                        continue;
                    }

                    var adjacentCell = map[nextY][nextX];

                    if (adjacentCell.Type != CellType.DEEP_SPACE && adjacentCell.Occupier == null)
                    {
                        adjacentCells.Add(adjacentCell);
                    }
                }
            }

            return adjacentCells.ToArray();
        }

        private string GetShootDirection(Worm targetWorm, Worm currentActiveWorm)
        {
            var directionString = "";

            if (targetWorm.Position.Y > currentActiveWorm.Position.Y)
            {
                directionString += "S";
            }
            else if (targetWorm.Position.Y < currentActiveWorm.Position.Y)
            {
                directionString += "N";
            }

            if (targetWorm.Position.X > currentActiveWorm.Position.X)
            {
                directionString += "E";
            }
            else if (targetWorm.Position.X < currentActiveWorm.Position.X)
            {
                directionString += "W";
            }

            return directionString;
        }

        private IEnumerable<Worm> GetOpponentWormsWithoutObstacles(IEnumerable<Worm> opponentWorms,
            Worm activeWorm)
        {
            var map = gameState.Map;
            var wormsWithoutObstaclesInRange = new List<Worm>();
            foreach (var worm in opponentWorms)
            {
                var x = worm.Position.X;
                var y = worm.Position.Y;

                while (x != activeWorm.Position.X || y != activeWorm.Position.Y)
                {
                    if (x > activeWorm.Position.X)
                    {
                        x -= 1;
                    }
                    else if (x < activeWorm.Position.X)
                    {
                        x += 1;
                    }

                    if (y > activeWorm.Position.Y)
                    {
                        y -= 1;
                    }
                    else if (y < activeWorm.Position.Y)
                    {
                        y += 1;
                    }

                    var cellType = map[y][x].Type;

                    if (cellType == CellType.DIRT || cellType == CellType.DEEP_SPACE)
                    {
                        break;
                    }
                }

                if (x == activeWorm.Position.X && y == activeWorm.Position.Y)
                {
                    wormsWithoutObstaclesInRange.Add(worm);
                }
            }

            return wormsWithoutObstaclesInRange;
        }

        private IEnumerable<Worm> GetOpponentWormsInRangeOfActiveWorm(IEnumerable<Worm> opponentWorms, Worm activeWorm)
        {
            var wormsInRange = new List<Worm>();

            foreach (var worm in opponentWorms)
            {
                var distanceFromActiveWorm = GetDistanceFromActiveWorm(worm.Position, activeWorm.Position);

                if (distanceFromActiveWorm >= activeWorm.Weapon.Range)
                {
                    continue;
                }

                if (worm.Position.X == activeWorm.Position.X ||
                    worm.Position.Y == activeWorm.Position.Y ||
                    Math.Abs(worm.Position.X - activeWorm.Position.X) ==
                    Math.Abs(worm.Position.Y - activeWorm.Position.Y))
                {
                    wormsInRange.Add(worm);
                }
            }

            return wormsInRange;
        }

        private double GetDistanceFromActiveWorm(MapPosition opponentPosition, MapPosition activeWormPosition)
        {
            return Math.Floor(Math.Sqrt(Math.Pow((opponentPosition.X - activeWormPosition.X), 2) + Math.Pow((opponentPosition.Y - activeWormPosition.Y), 2)));
        }
    }
}