"use strict";

let fs = require('fs');
let readline = require('readline');
let stateFileName = "state.json";

let stateFile = "";
let myPlayer = "";
let opponent = "";
let gameMap = "";
let mapSize = "";
let cells = "";
let myCurrentWorm = "";

let directions = [
    {name: 'E', x: 1, y: 0},
    {name: 'NE', x: 1, y: -1},
    {name: 'N', x: 0, y: -1},
    {name: 'NW', x: -1, y: -1},
    {name: 'W', x: -1, y: 0},
    {name: 'SW', x: -1, y: 1},
    {name: 'S', x: 0, y: 1},
    {name: 'SE', x: 1, y: 1}
];
let surfaceTypes = {
    DEEP_SPACE: 'DEEP_SPACE',
    AIR: 'AIR',
    DIRT: 'DIRT'
};


let consoleReader = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

consoleReader.on("line", (roundNumber) => {
    executeRound(roundNumber); // Read in the current round number
});

function executeRound(roundNumber) {
    // Read the current state and choose an action
    stateFile = fs.readFileSync(`./rounds/${roundNumber}/${stateFileName}`);
    stateFile = JSON.parse(stateFile);

    initEntities();

    let command = runStrategy();
    console.log(`C;${roundNumber};${command}`);
}

function initEntities() {
    myPlayer = stateFile.myPlayer;
    opponent = stateFile.opponents[0];
    mapSize = stateFile.mapSize;
    gameMap = stateFile.map;
    cells = flatMap(gameMap);
    myCurrentWorm = myPlayer.worms.find(worm => worm.id === stateFile.currentWormId);
}

function runStrategy() {
    let command = doNothingCommand();

    let cellAndTarget = getShootableOpponent();
    if (cellAndTarget) {
        return command = `shoot ${cellAndTarget.target}`;
    }

    let cellToMoveDigInto = getRandomMoveCell();
    if (cellToMoveDigInto) {
        if (cellToMoveDigInto.type === surfaceTypes.DIRT) {
            return command = `dig ${cellToMoveDigInto.x} ${cellToMoveDigInto.y}`;
        } else if (cellToMoveDigInto.type === surfaceTypes.AIR) {
            return command = `move ${cellToMoveDigInto.x} ${cellToMoveDigInto.y}`;
        }
    }

    return command;
}

function getRandomMoveCell() {
    let center = getPositionOf(myCurrentWorm);
    let randomCellCoordinate = {
        x: center.x + Math.floor(Math.random() * 3) - 1,
        y: center.y + Math.floor(Math.random() * 3) - 1
    };
    if (coordinateIsOutOfBounds(randomCellCoordinate)
        || (randomCellCoordinate.x === center.x
            && randomCellCoordinate.y === center.y)) {
        return null;
    }

    return gameMap[randomCellCoordinate.y][randomCellCoordinate.x];
}

function coordinateIsOutOfBounds(coordinateToCheck) {
    return coordinateToCheck.x < 0
        || coordinateToCheck.x >= mapSize
        || coordinateToCheck.y < 0
        || coordinateToCheck.y >= mapSize;
}

function getPositionOf(entity) {
    return {x: entity.position.x, y: entity.position.y};
}

function getCoordinateAddition(coordinateA, coordinateB) {
    return {
        x: coordinateA.x + coordinateB.x,
        y: coordinateA.y + coordinateB.y
    };
}

function getShootableOpponent() {
    let center = getPositionOf(myCurrentWorm);
    let shootTemplates = getShootTemplates();

    for (let template of shootTemplates) {
        for (let deltaCoordinate of template.coordinates) {
            let coordinateToCheck = getCoordinateAddition(center, deltaCoordinate);
            if (coordinateIsOutOfBounds(coordinateToCheck)
                || euclideanDistance(coordinateToCheck, center) > myCurrentWorm.weapon.range) {
                break;
            }
            let cellToInspect = gameMap[coordinateToCheck.y][coordinateToCheck.x];
            if (cellToInspect.type === surfaceTypes.DIRT || cellToInspect.type === surfaceTypes.DEEP_SPACE) {
                break;
            }

            let isOccupiedByOpponentWorm = (cellToInspect.occupier && cellToInspect.occupier.playerId !== myPlayer.id);
            if (isOccupiedByOpponentWorm) {
                return {cell: cellToInspect, target: template.name};
            }
        }
    }

    return null;
}

function getShootTemplates() {
    let shootTemplates = [];

    for (let direction of directions) {
        let currentDirectionLine = [];
        for (let i = 1; i <= myCurrentWorm.weapon.range; i++) {
            let cellOfLine = {x: i * direction.x, y: i * direction.y};
            currentDirectionLine.push(cellOfLine);
        }
        shootTemplates.push({name: direction.name, coordinates: currentDirectionLine});
    }
    return shootTemplates;
}

function doNothingCommand() {
    return `nothing`;
}

function euclideanDistance(positionA, positionB) {
    return Math.sqrt(Math.pow(positionA.x - positionB.x, 2) + Math.pow(positionA.y - positionB.y, 2));
}

/***
 * Returns an array with one less level of nesting
 * @param array
 * @returns {Array}
 */
function flatMap(array) {
    return array.reduce((acc, x) => acc.concat(x), []);
}
