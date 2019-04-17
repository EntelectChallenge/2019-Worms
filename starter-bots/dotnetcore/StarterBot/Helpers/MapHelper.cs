using System;
using System.Collections.Generic;
using System.Text;
using StarterBot.Entities;
using StarterBot.Enums;

namespace StarterBot.Helpers
{
    public static class MapHelper
    {
        public static bool IsValidCell(CellStateContainer targetCell)
        {
            return targetCell.Type != CellType.DEEP_SPACE && targetCell.Type != CellType.DIRT;
        }

        public static bool IsWithinRange(MapPosition maxMapPosition, int weaponRange)
        {
            throw new NotImplementedException();
        }

        public static bool IsValidCoordinate(MapPosition targetMapPosition, int mapSize)
        {
            return targetMapPosition.X >= 0 &&
                   targetMapPosition.X < mapSize &&
                   targetMapPosition.Y >= 0 &&
                   targetMapPosition.Y < mapSize;
        }

        public static int GetFlooredEuclideanDistance(MapPosition currentMapPosition, MapPosition targetMapPosition)
        {
            var xDifference = Math.Pow((targetMapPosition.X - currentMapPosition.X), 2);
            var yDifference = Math.Pow((targetMapPosition.Y - currentMapPosition.Y), 2);

            return (int)Math.Floor(Math.Sqrt(xDifference + yDifference));
        }
    }
}
