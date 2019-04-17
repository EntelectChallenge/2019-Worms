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

// x,y represents the cartesian direction of each
let directions = [
    {name: 'E', dx: 1, dy: 0},
    {name: 'NE', dx: 1, dy: -1},
    {name: 'N', dx: 0, dy: -1},
    {name: 'NW', dx: -1, dy: -1},
    {name: 'W', dx: -1, dy: 0},
    {name: 'SW', dx: -1, dy: 1},
    {name: 'S', dx: 0, dy: 1},
    {name: 'SE', dx: 1, dy: 1}
];
let surfaceTypes = {
    DEEP_SPACE: 'DEEP_SPACE',
    AIR: 'AIR',
    DIRT: 'DIRT'
};
let commandNames = {
    nothing: 'nothing',
    shoot: 'shoot',
    move: 'move',
    dig: 'dig',
};

function getNothingCommand() {
    return commandNames.nothing;
}

function getShootCommand(direction) {
    return `${commandNames.shoot} ${direction}`;
}

function getMoveCommand(x, y) {
    return `${commandNames.move} ${x} ${y}`;
}

function getDigCommand(x, y) {
    return `${commandNames.dig} ${x} ${y}`;
}

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
    cells = getFlatMapOf(gameMap);
    myCurrentWorm = myPlayer.worms.find(worm => worm.id === stateFile.currentWormId);
}

function runStrategy() {
    let cellAndTarget = getShootableOpponent();
    if (cellAndTarget) {
        return getShootCommand(cellAndTarget.direction);
    }

    let cellToMoveDigInto = getRandomMoveCell();
    if (cellToMoveDigInto) {
        if (cellToMoveDigInto.type === surfaceTypes.DIRT) {
            return getDigCommand(cellToMoveDigInto.x, cellToMoveDigInto.y);
        } else if (cellToMoveDigInto.type === surfaceTypes.AIR) {
            return getMoveCommand(cellToMoveDigInto.x, cellToMoveDigInto.y);
        }
    }

    return getNothingCommand();
}

function getRandomMoveCell() {
    let center = getPositionOf(myCurrentWorm);
    let randomCellCoordinate = {
        x: center.x + Math.floor(Math.random() * 3) - 1,
        y: center.y + Math.floor(Math.random() * 3) - 1
    };
    if (isCoordinateOutOfBounds(randomCellCoordinate)
        || (randomCellCoordinate.x === center.x && randomCellCoordinate.y === center.y)) {
        return null;
    }

    return gameMap[randomCellCoordinate.y][randomCellCoordinate.x];
}

function isCoordinateOutOfBounds(coordinateToCheck) {
    return (coordinateToCheck.x < 0
        || coordinateToCheck.x >= mapSize
        || coordinateToCheck.y < 0
        || coordinateToCheck.y >= mapSize);
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

    // Use templates to find the 'shooting lines' around the current worm, test each of those cells to be open
    for (let template of shootTemplates) {

        for (let deltaCoordinate of template.coordinates) {

            let coordinateToCheck = getCoordinateAddition(center, deltaCoordinate);
            if (isCoordinateOutOfBounds(coordinateToCheck)
                || getEuclideanDistanceOf(coordinateToCheck, center) > myCurrentWorm.weapon.range) {
                break;
            }

            let cellToInspect = gameMap[coordinateToCheck.y][coordinateToCheck.x];
            if (cellToInspect.type === surfaceTypes.DIRT || cellToInspect.type === surfaceTypes.DEEP_SPACE) {
                break;
            }

            let isOccupiedByOpponentWorm = (cellToInspect.occupier && cellToInspect.occupier.playerId !== myPlayer.id);
            if (isOccupiedByOpponentWorm) {
                return {cell: cellToInspect, direction: template.name};
            }
        }
    }

    return null;
}

// Returns a list of templates, each containing all the coordinates from the current worm in one of the directions
function getShootTemplates() {
    let shootTemplates = [];

    for (let direction of directions) {
        let currentDirectionLine = [];

        for (let i = 1; i <= myCurrentWorm.weapon.range; i++) {
            let cellOfLine = {x: i * direction.dx, y: i * direction.dy};
            currentDirectionLine.push(cellOfLine);
        }
        shootTemplates.push({name: direction.name, coordinates: currentDirectionLine});
    }
    return shootTemplates;
}

// Returns the distance between positionA and positionB. https://en.wikipedia.org/wiki/Euclidean_distance
function getEuclideanDistanceOf(positionA, positionB) {
    return Math.sqrt(Math.pow(positionA.x - positionB.x, 2) + Math.pow(positionA.y - positionB.y, 2));
}

// Returns an array with one less level of nesting
function getFlatMapOf(array) {
    return array.reduce((acc, x) => acc.concat(x), []);
}
