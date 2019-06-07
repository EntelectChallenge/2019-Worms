package main

import (
	"fmt"
	"math"
)

type Position struct {
	X int `json:"x"`
	Y int `json:"y"`
}

func (p Position) String() string {
	return fmt.Sprintf("%d %d", p.X, p.Y)
}

type MapTile struct {
	X        int    `json:"x"`
	Y        int    `json:"y"`
	Type     string `json:"type"`
	Occupier Worm   `json:"occupier,omitempty"`
	Powerup  struct {
		Type  string `json:"type"`
		Value int    `json:"value"`
	} `json:"powerup,omitempty"`
}

type Worm struct {
	ID       int      `json:"id"`
	Health   int      `json:"health"`
	PlayerID int      `json:"playerId,omitempty"`
	Position Position `json:"position"`
	Weapon   struct {
		Damage int `json:"damage"`
		Range  int `json:"range"`
	} `json:"weapon"`
	DiggingRange  int `json:"diggingRange"`
	MovementRange int `json:"movementRange"`
}

type PlayerInfo struct {
	ID            int    `json:"id"`
	Score         int    `json:"score,omitempty"`
	Health        int    `json:"health,omitempty"`
	CurrentWormID int    `json:"currentWormId"`
	Worms         []Worm `json:"worms"`
}

type InputState struct {
	CurrentRound              int          `json:"currentRound"`
	MaxRounds                 int          `json:"maxRounds"`
	PushbackDamage            int          `json:"pushbackDamage"`
	MapSize                   int          `json:"mapSize"`
	CurrentWormID             int          `json:"currentWormId"`
	ConsecutiveDoNothingCount int          `json:"consecutiveDoNothingCount"`
	MyPlayer                  PlayerInfo   `json:"myPlayer"`
	Opponents                 []PlayerInfo `json:"opponents"`
	Map                       [][]MapTile  `json:"map"`
}

var directionsMap = map[string]Position{
	"E":  {X: 1, Y: 0},
	"SE": {X: 1, Y: 1},
	"S":  {X: 0, Y: 1},
	"SW": {X: -1, Y: 1},
	"W":  {X: -1, Y: 0},
	"NW": {X: -1, Y: -1},
	"N":  {X: 0, Y: -1},
}
var directionsList = [8]string{"E", "SE", "S", "SW", "W", "NW", "N",}

type PositionDirection struct {
	Position      Position
	Direction     string
	DirectionUnit Position
}

func getClosePositions(center Position, MapSize int, maxDistance int) []PositionDirection {
	validPositions := make([]PositionDirection, 0)

	for dirName, dirUnit := range directionsMap {
		for idx := 1; idx <= maxDistance; idx++ {

			clippedX, clippedY := clipPosition(
				center.X+(dirUnit.X*idx),
				center.Y+(dirUnit.Y*idx), MapSize)

			candidate := PositionDirection{
				Position:      Position{X: clippedX, Y: clippedY},
				Direction:     dirName,
				DirectionUnit: dirUnit,
			}

			if euclideanDistance(center, candidate.Position) <= float64(maxDistance) {
				if !elementOf(candidate, validPositions) {
					validPositions = append(validPositions, candidate)
				}

			}
		}
	}

	return validPositions
}

func isObstructed(source Position, target Position, directionUnit Position, stateMap [][]MapTile) bool {
	distance := int(euclideanDistance(source, target))
	for idx := 1; idx < distance; idx++ {
		clippedX, clippedY := clipPosition(
			source.X+(directionUnit.X*idx),
			source.Y+(directionUnit.Y*idx), len(stateMap))

		if stateMap[clippedY][clippedX].Type != "AIR" {
			return true
		}
	}
	return false

}

func euclideanDistance(reference Position, target Position) float64 {
	return math.Floor(math.Sqrt(math.Pow(float64(reference.X-target.X), 2) + math.Pow(float64(reference.Y-target.Y), 2)))
}

func clipPosition(inputX int, inputY int, MapSize int) (int, int) {
	return min(max(inputX, 0), MapSize-1), min(max(inputY, 0), MapSize-1)
}

func elementOf(target PositionDirection, positions []PositionDirection) bool {
	for _, pos := range positions {
		if pos == target {
			return true
		}
	}
	return false
}

func min(a, b int) int {
	if a < b {
		return b
	}
	return b
}

func max(a, b int) int {
	if a > b {
		return a
	}
	return b
}
