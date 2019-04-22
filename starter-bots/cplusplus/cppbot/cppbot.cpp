#include <iostream>
#include "./rapidjson/document.h"
#include <fstream>
#include <string>
#include <sstream>
#include <vector>
#include <functional>
#include <algorithm>
#include <cmath>

static std::string dirt = "DIRT";
static std::string air = "AIR";
static std::string space = "DEEP_SPACE";

struct POINT
{
  int x;
  int y;
};

enum DIRECTIONS { E = 0, NE, N, NW, W, SW, S, SE };
std::vector<POINT> directions = { {1,0},{1,-1},{0,-1},{-1,-1},{-1,0},{-1,1},{0,1},{1,1} };
std::vector<std::string> directionNames = { "E", "NE", "N", "NW","W","SW","S","SE" };


std::string PowerupStrategy(rapidjson::Document&);
std::string AttackStrategy(rapidjson::Document&);
std::string FollowStrategy(int targetWormId, rapidjson::Document&);
std::string HuntStrategy(int targetWormId, rapidjson::Document&);

/**
 * Maps worm ids to strategies
 */


using namespace std::placeholders;

std::vector<std::vector<std::function<std::string(rapidjson::Document&)>>> strategyPriorities = {
  {std::bind(PowerupStrategy, _1), std::bind(HuntStrategy,1, _1), std::bind(AttackStrategy, _1)},  // NOLINT(modernize-avoid-bind)
  {std::bind(PowerupStrategy, _1), std::bind(HuntStrategy,1, _1), std::bind(FollowStrategy,1, _1), std::bind(AttackStrategy, _1)}, // NOLINT(modernize-avoid-bind)
  {std::bind(PowerupStrategy, _1), std::bind(HuntStrategy,1, _1), std::bind(FollowStrategy,1, _1), std::bind(FollowStrategy,2, _1), std::bind(AttackStrategy, _1)} // NOLINT(modernize-avoid-bind)
};

POINT GetMyCurrentWormPoint(const rapidjson::Document& roundJSON)
{
  const auto currentWormId = roundJSON["currentWormId"].GetInt();
  const auto curWorm = roundJSON["myPlayer"].GetObject()["worms"].GetArray()[currentWormId - 1].GetObject()["position"].GetObject();
  return { curWorm["x"].GetInt() , curWorm["y"].GetInt() };
}

/**
 * Build a list of all cells in a specific direction withing shooting range of my active worm
 */
std::vector<POINT> buildDirectionLine(rapidjson::Document& roundJSON, DIRECTIONS direction)
{
  const int weaponRange = roundJSON["myPlayer"].GetObject()["worms"].GetArray()[roundJSON["currentWormId"].GetInt()-1].GetObject()["weapon"].GetObject()["range"].GetInt();
  std::vector<POINT> currentDirectionLine;
  for (int i = 1; i <= weaponRange; i++) 
  {
    currentDirectionLine.push_back({ i * directions[direction].x, i * directions[direction].y });
  }
  return currentDirectionLine;
}

/**
 * Get the lines the active worm can shoot in
 */
auto getShootTemplates(rapidjson::Document& roundJSON)
{
  std::vector<std::vector<POINT>> shootTemplates;

  for (int direction = E; direction <= SE; direction++) 
  {
    shootTemplates.push_back(buildDirectionLine(roundJSON, static_cast<DIRECTIONS>(direction)));
  }
  return shootTemplates;
}

/**
 * Add the x and y values of two coordinates together
 * @return Position
 */
POINT sumCoordinates(const POINT coordinateA, const POINT coordinateB) {
  return {
      coordinateA.x + coordinateB.x,
      coordinateA.y + coordinateB.y
  };
}

/**
 * Check if a coordinate is in the map bounds
 * @param coordinateToCheck {Point}
 * @param mapSize
 * @return {boolean}
 */
bool coordinateIsOutOfBounds(const POINT coordinateToCheck, const int mapSize) {
  return coordinateToCheck.x < 0
    || coordinateToCheck.x >= mapSize
    || coordinateToCheck.y < 0
    || coordinateToCheck.y >= mapSize;
}

/**
 * Calculate the distance between two points
 * https://en.wikipedia.org/wiki/Euclidean_distance
 *
 * @param positionA {Point}
 * @param positionB {Point}
 * @return {number}
 */
int euclideanDistance(POINT positionA, POINT positionB) {
  return static_cast<int>(std::floor(
    std::sqrt(std::pow(positionA.x - positionB.x, 2) + std::pow(positionA.y - positionB.y, 2))));
}

/**
 * Get any opponent worm that is in range and can be shot without being blocked
 */
std::pair<POINT, int>getShootableOpponent(rapidjson::Document& roundJSON)
{
  POINT currentWorm = GetMyCurrentWormPoint(roundJSON);
  const int weaponRange = roundJSON["myPlayer"].GetObject()["worms"].GetArray()[roundJSON["currentWormId"].GetInt() - 1].GetObject()["weapon"].GetObject()["range"].GetInt();

  auto shootTemplates = getShootTemplates(roundJSON);

  for (int i = E; i < SE; i++)
  {
    for(auto& deltaCoordinate : shootTemplates[i])
    {
      const POINT coordinateToCheck = sumCoordinates(currentWorm, deltaCoordinate);
      if (coordinateIsOutOfBounds(coordinateToCheck, roundJSON["mapSize"].GetInt())
        || euclideanDistance(coordinateToCheck, currentWorm) > weaponRange)
      {
        break;
      }
      
      auto cellToInspect = roundJSON["map"].GetArray()[coordinateToCheck.y].GetArray()[coordinateToCheck.x].GetObject();
      if (!std::strcmp(cellToInspect["type"].GetString(), dirt.c_str())
        || !std::strcmp(cellToInspect["type"].GetString(), space.c_str())
        || cellToInspect.HasMember("occupier") && cellToInspect["occupier"].GetObject()["playerId"].GetInt() == roundJSON["myPlayer"].GetObject()["id"].GetInt()
        ) 
      {
        break;
      }

      const auto isOccupiedByOpponentWorm = cellToInspect.HasMember("occupier") && cellToInspect["occupier"].GetObject()["playerId"].GetInt() != roundJSON["myPlayer"].GetObject()["id"].GetInt();
      if (isOccupiedByOpponentWorm) 
      {
        POINT pt = { cellToInspect["x"].GetInt(), cellToInspect["y"].GetInt() };
        return std::make_pair(pt, i);
      }
    }
  }
  POINT pt = { -1,-1 };
  return std::make_pair(pt, -1);
}

std::string runStrategy(rapidjson::Document& roundJSON)
{
  const auto shootable = getShootableOpponent(roundJSON);
  if (shootable.second >= 0)
  {
    return "shoot " + directionNames[shootable.second];
  }


  for (auto& strategy : strategyPriorities[roundJSON["currentWormId"].GetInt()-1]) 
  {
    std::string strategyResult = strategy(roundJSON);
    if (strategyResult.length()>0)
    {
      return strategyResult;
    }
  }
  return "error runStrategy";
}

std::string executeRound(std::string& roundNumber)
{
  std::string ret;
  const std::string filePath = "./rounds/" + roundNumber + "/state.json";
  std::ifstream dataIn;
  dataIn.open(filePath, std::ifstream::in);
  if (dataIn.is_open())
  {
    std::stringstream buffer;
    buffer << dataIn.rdbuf();
    std::string sOrderBookString = buffer.str();
    rapidjson::Document roundJSON;
    const bool parsed = !roundJSON.Parse(sOrderBookString.c_str()).HasParseError();
    if (parsed)
    {
      ret = "C;" + roundNumber + ";" + runStrategy(roundJSON) + "\n";
    }
    else
    {
      ret = "C;" + roundNumber + ";error executeRound \n";
    }
  }

  return ret;
}

/**
 * Find the cell adjacent to the origin that is the closest to the  destination
 * @param current {Point}
 * @param target {Point}
 * @param roundJSON {rapidjson}
 * @return Cell
 */
POINT FindNextCellInPath(const POINT& current, const POINT& target, const rapidjson::Document& roundJSON)
{
  std::vector<std::pair<POINT,int>> candidates;
  for (auto& y : roundJSON["map"].GetArray())
  {
    for (auto& x : y.GetArray())
    {
      if (std::abs(current.x - x.GetObject()["x"].GetInt()) == 1 && std::abs(current.y - x.GetObject()["y"].GetInt()) == 1)
      {
        POINT pt = { x.GetObject()["x"].GetInt(),x.GetObject()["y"].GetInt() };
        candidates.emplace_back(std::make_pair(pt,euclideanDistance(target,pt)));
      }
    }
  }

  std::sort(candidates.begin(), candidates.end(), [](const std::pair<POINT, int> a, const std::pair<POINT, int> b) { return a.second < b.second; });

  if (candidates.empty())
    return { 0,0 };

  return candidates[0].first;
}


/**
 * Get a random cell from all cells adjacent to my active worm
 * @return {Cell}
 */
POINT GetRandomAdjacentCell(const POINT& current, rapidjson::Document& roundJSON)
{
  std::vector<POINT> candidates;
  
  for (auto& y : roundJSON["map"].GetArray()[current.y].GetArray())
  {
    for (auto& x : y.GetArray())
    {
      if (std::abs(current.x - x.GetObject()["x"].GetInt()) == 1 && std::abs(current.y - x.GetObject()["y"].GetInt()) == 1)
      {
        POINT pt = POINT{ x.GetObject()["x"].GetInt(),x.GetObject()["y"].GetInt() };
        candidates.emplace_back(pt);
      }
    }
  }
  
  return candidates[std::abs(std::floor(std::rand() * candidates.size()))];
}


/**
 * Returns a dig or move command towards the destination
 * @param roundJSON
 * @param target {Point}
 * @return {string}
 */
std::string digAndMoveTo(rapidjson::Document& roundJSON, const POINT& target)
{
  POINT current = GetMyCurrentWormPoint(roundJSON);
  POINT shortestPathCell = FindNextCellInPath(current, target, roundJSON);

  auto cell = roundJSON["map"].GetArray()[shortestPathCell.y].GetArray()[shortestPathCell.x].GetObject();
  if (cell.HasMember("occupier") && cell["occupier"].GetObject()["playerId"].GetInt() == roundJSON["myPlayer"].GetObject()["id"])
  {
    shortestPathCell = GetRandomAdjacentCell(current, roundJSON);
  }

  auto cell2 = roundJSON["map"].GetArray()[shortestPathCell.y].GetArray()[shortestPathCell.x].GetObject();
  auto str = cell2["type"].GetString();
  if (dirt == str)
  {
    return "dig " + std::to_string(shortestPathCell.x) + " " + std::to_string(shortestPathCell.y);
  }
  if (air == str)
  {
    return "move " + std::to_string(shortestPathCell.x) + " " + std::to_string(shortestPathCell.y);
  }
  return "error digAndMoveTo";
}



/**
 * A worm strategy that moves towards powerups
 */
std::string PowerupStrategy(rapidjson::Document& roundJSON)
{
  std::vector<POINT> powerupCells;
  for(auto& y : roundJSON["map"].GetArray())
  {
    for (auto&x : y.GetArray())
    {
      if (x.GetObject().HasMember("powerup"))
      {
        POINT pt = { x.GetObject()["x"].GetInt(),x.GetObject()["y"].GetInt() };
        powerupCells.push_back(pt);
      }
    }
  }
  if (powerupCells.empty())
  {
    return "";
  }
  int dist = -1;
  POINT target = { 0,0 };
  POINT myWorm = GetMyCurrentWormPoint(roundJSON);

  for (auto& i : powerupCells)
  {
    const int distance = euclideanDistance(i, myWorm);
    if (distance < dist || dist == -1)
    {
      dist = distance;
      target = i;
    }
  }
  return digAndMoveTo(roundJSON, target);
}

POINT getApproachableOpponent(const rapidjson::Document& roundJSON)
{
  auto opponentWorms = roundJSON["opponents"].GetArray()[0].GetObject()["worms"].GetArray();
  POINT myWorm = GetMyCurrentWormPoint(roundJSON);
  int distance = -1;
  POINT pt = { 0,0 };
  for (auto& worm : opponentWorms)
  {
    auto targetWormXY = worm.GetObject()["position"].GetObject();
    POINT targetPt = { targetWormXY["x"].GetInt() , targetWormXY["y"].GetInt() };
    int dist = euclideanDistance(myWorm, targetPt);
    if (distance == -1 || dist < distance)
    {
      distance = dist;
      pt = targetPt;
    }
  }

  return pt;
}

/**
 * A worm strategy that moves towards the closest opponent
 */
std::string AttackStrategy(rapidjson::Document& roundJSON)
{
  const POINT nearTarget = getApproachableOpponent(roundJSON);
  return digAndMoveTo(roundJSON, nearTarget);
}

/**
 * A worm strategy that follows another of my own worms
 */
std::string FollowStrategy(const int targetWormId, rapidjson::Document& roundJSON)
{
  auto targetWorm = roundJSON["myPlayer"].GetObject()["worms"].GetArray()[targetWormId].GetObject();
  if (targetWorm["health"].GetInt() <= 0)
  {
    return "";
  }
  const POINT targetPt = { targetWorm["position"].GetObject()["x"].GetInt() , targetWorm["position"].GetObject()["y"].GetInt() };
  const POINT current = GetMyCurrentWormPoint(roundJSON);
  if (euclideanDistance(current, targetPt) > 3)
  {
    return digAndMoveTo(roundJSON, targetPt);
  }

  const POINT nearTarget = getApproachableOpponent(roundJSON);
  return digAndMoveTo(roundJSON, nearTarget);
}

/**
 * A worm strategy that moves towards a specific enemy worm
 * @param targetWormId
 * @param roundJSON
 */
std::string HuntStrategy(int targetWormId, rapidjson::Document& roundJSON)
{
  const auto targetWorm = roundJSON["opponents"].GetArray()[0].GetObject()["worms"].GetArray()[targetWormId].GetObject();
  if (targetWorm["health"].GetInt() <= 0)
  {
    return "";
  }
  const POINT targetPt = { targetWorm["position"].GetObject()["x"].GetInt() , targetWorm["position"].GetObject()["y"].GetInt() };
  return digAndMoveTo(roundJSON, targetPt);
}


int main(int argc, char** argv)
{
  for (std::string roundNumber; std::getline(std::cin, roundNumber);) 
  {
    std::cout << executeRound(roundNumber);
  }
}
