#include <iostream>
#include "./rapidjson/document.h"
#include <fstream>
#include <string>
#include <sstream>
#include <vector>
#include <functional>
#include <cmath>
#include <random>

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

std::string RandomStrategy(rapidjson::Document& roundJSON)
{
  std::random_device rd;  //Will be used to obtain a seed for the random number engine
  std::mt19937 gen(rd()); //Standard mersenne_twister_engine seeded with rd()
  std::uniform_int_distribution<> dis(-1, 1);
  
  POINT dest = GetMyCurrentWormPoint(roundJSON);
  dest.x += dis(gen);
  dest.y += dis(gen);
  int maxMap = roundJSON["mapSize"].GetInt() - 1;
  if (dest.x < 0) dest.x = 0;
  if (dest.x > maxMap) dest.x = maxMap;

  if (dest.y < 0) dest.y = 0;
  if (dest.y > maxMap) dest.y = maxMap;

  auto cell2 = roundJSON["map"].GetArray()[dest.y].GetArray()[dest.x].GetObject();
  auto str = cell2["type"].GetString();
  if (dirt == str)
  {
    return "dig " + std::to_string(dest.x) + " " + std::to_string(dest.y);
  }
  if (air == str)
  {
    return "move " + std::to_string(dest.x) + " " + std::to_string(dest.y);
  }
  if (space == str)
  {
    return "nothing";
  }
  return "nothing";
}

std::string runStrategy(rapidjson::Document& roundJSON)
{
  const auto shootable = getShootableOpponent(roundJSON);
  if (shootable.second >= 0)
  {
    return "shoot " + directionNames[shootable.second];
  }

  return RandomStrategy(roundJSON);
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
    std::string stateJson = buffer.str();
    rapidjson::Document roundJSON;
    const bool parsed = !roundJSON.Parse(stateJson.c_str()).HasParseError();
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

int main(int argc, char** argv)
{
  for (std::string roundNumber; std::getline(std::cin, roundNumber);) 
  {
    std::cout << executeRound(roundNumber);
  }
}
