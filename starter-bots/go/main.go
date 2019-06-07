package main

import (
	"bufio"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"math/rand"
	"os"
	"path"
	"strconv"
	"strings"
	"time"
)

func main() {
	rand.Seed(time.Now().Unix())

	reader := bufio.NewReader(os.Stdin)
	for true {
		roundNum, err := reader.ReadString('\n')
		if err != nil {
			fmt.Printf("Error reading from STDIN: %s\n", err)
			os.Exit(1)
		}
		roundNumber, err := strconv.Atoi(strings.Trim(roundNum, "\r\n"))
		if err != nil {
			fmt.Printf("Cannot parse input as integer: %s\n", err)
			os.Exit(1)
		}
		currentState := loadState(roundNumber)

		action, actionArgument := botLogic(currentState)
		fmt.Printf("C;%d;%s %s\r\n", roundNumber, action, actionArgument)
	}
}

func loadState(roundNumber int) *InputState {
	stateLocation := path.Join("./rounds", strconv.Itoa(roundNumber), "state.json")
	inputText, err := ioutil.ReadFile(stateLocation)
	if err != nil {
		fmt.Printf("Error reading state.json: %s\n", err)
		os.Exit(1)
	}

	var inputState InputState
	err = json.Unmarshal([]byte(inputText), &inputState)
	if err != nil {
		fmt.Printf("Error unmarshaling to JSON schema: %s\n", err)
		os.Exit(1)
	}
	return &inputState
}

func botLogic(currentState *InputState) (string, string) {

	action := ""
	actionArgument := ""
	opponentWorms := currentState.Opponents[0].Worms

	for _, worm := range currentState.MyPlayer.Worms {

		if worm.ID == currentState.CurrentWormID {
			//get all coordinates within range
			positionsInRange := getClosePositions(worm.Position, currentState.MapSize, worm.Weapon.Range)


			for _, opponent := range opponentWorms {
				if opponent.Health > 0 {
					for _, candidate := range positionsInRange {
						if candidate.Position == opponent.Position {
							//opponent is within range
							if !isObstructed(worm.Position, opponent.Position, candidate.DirectionUnit, currentState.Map) {
								action = "SHOOT"
								actionArgument = candidate.Direction
							}
						}
					}
				}
			}
			if action != "SHOOT" {

				//get a random direction
				directionName := directionsList[rand.Intn(len(directionsList))]
				positionOffset := directionsMap[directionName]
				candidatePosition := Position{X: positionOffset.X + worm.Position.X, Y: positionOffset.Y + worm.Position.Y}

				if currentState.Map[candidatePosition.Y][candidatePosition.X].Type == "AIR" {
					action = "MOVE"
					actionArgument = candidatePosition.String()
				} else if currentState.Map[candidatePosition.Y][candidatePosition.X].Type == "DIRT" {
					action = "DIG"
					actionArgument = candidatePosition.String()
				} else {
					action = "NOTHING"
				}
			}
		}
	}
	return action, actionArgument
}
