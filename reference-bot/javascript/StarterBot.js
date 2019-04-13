"use strict";

let priorities = {
    powerups: {name: 'powerups'},
    attack: {name: 'attack'},
    follow: id => ({name: 'follow', data: id}),
    hunt: id => ({name: 'hunt', data: id}),
};
let strategyConfig = {
    1: [priorities.powerups, priorities.hunt(1), priorities.attack],
    2: [priorities.powerups, priorities.hunt(1), priorities.follow(1), priorities.attack],
    3: [priorities.powerups, priorities.hunt(1), priorities.follow(1), priorities.follow(2), priorities.attack]
};

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

// executeRound(2);

function executeRound(roundNumber) {
    // Read the current state and choose an action
    stateFile = fs.readFileSync(`./rounds/${roundNumber}/${stateFileName}`);
    stateFile = JSON.parse(stateFile);

    initEntities();

    let command = runStrategy() || doNothingCommand();
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
    let cellAndTarget = getShootableOpponent();
    if (cellAndTarget) {
        return `shoot ${cellAndTarget.direction}`;
    }

    for (let nowStrategy of strategyConfig[myCurrentWorm.id]) {
        if (nowStrategy === priorities.powerups && cells.some(c => c.powerup)) {
            let nearPowerup = cells.filter(c => c.powerup)
                .map(c => ({
                    cell: c,
                    distance: euclideanDistance(myCurrentWorm.position, getPositionOf(c))
                }))
                .sort((a, b) => a.distance - b.distance)[0];
            return digAndMoveTo(getPositionOf(nearPowerup.cell));

        } else if (nowStrategy === priorities.attack) {
            let nearTarget = getApproachableOpponent();
            return digAndMoveTo(nearTarget.worm.position);

        } else if (nowStrategy.name === priorities.follow().name
            && myPlayer.worms
                .filter(w => w.health > 0)
                .find(w => w.id === nowStrategy.data)) {
            let leaderWorm = myPlayer.worms.find(w => w.id === nowStrategy.data);
            if (euclideanDistance(myCurrentWorm.position, leaderWorm.position) > 3) {
                return digAndMoveTo(leaderWorm.position);
            }
            let nearTarget = getApproachableOpponent();
            return digAndMoveTo(nearTarget.worm.position);

        } else if (nowStrategy.name === priorities.hunt().name
            && opponent.worms
                .filter(w => w.health > 0)
                .find(w => w.id === nowStrategy.data)) {
            let preyWorm = opponent.worms.find(w => w.id === nowStrategy.data);
            return digAndMoveTo(preyWorm.position);

        }
    }

    return;
}

function getApproachableOpponent() {
    return opponent.worms.filter(w => w.health > 0)
        .map(w => ({
            worm: w,
            distance: euclideanDistance(myCurrentWorm.position, w.position)
        }))
        .sort((a, b) => a.distance - b.distance)[0];
}

function digAndMoveTo(destination) {
    let center = myCurrentWorm.position;
    let shortestPathCell = cells.filter(c => !(c.x === center.x && c.y === center.y)
        && Math.abs(c.x - center.x) <= 1
        && Math.abs(c.y - center.y) <= 1)
        .map(c => ({
            cell: c,
            distance: euclideanDistance(destination, getPositionOf(c))
        }))
        .sort((a, b) => a.distance - b.distance)[0]
        .cell;

    if (shortestPathCell.occupier && shortestPathCell.occupier.playerId === myPlayer.id) {
        shortestPathCell = getRandomMoveCell();
    }

    if (shortestPathCell.type === surfaceTypes.DIRT) {
        return `dig ${shortestPathCell.x} ${shortestPathCell.y}`;

    } else if (shortestPathCell.type === surfaceTypes.AIR) {
        return `move ${shortestPathCell.x} ${shortestPathCell.y}`;
    }
}

function getRandomMoveCell() {
    let center = getPositionOf(myCurrentWorm);
    let cellsAround = cells.filter(c => !(c.x === center.x && c.y === center.y)
        && Math.abs(c.x - center.x) <= 1
        && Math.abs(c.y - center.y) <= 1);
    return cellsAround[Math.floor(Math.random() * cellsAround.length)];

}

function coordinateIsOutOfBounds(coordinateToCheck) {
    return coordinateToCheck.x < 0
        || coordinateToCheck.x >= mapSize
        || coordinateToCheck.y < 0
        || coordinateToCheck.y >= mapSize;
}

function getPositionOf(entity) {
    return entity.position
        ? {x: entity.position.x, y: entity.position.y}
        : {x: entity.x, y: entity.y};
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
            if (cellToInspect.type === surfaceTypes.DIRT
                || cellToInspect.type === surfaceTypes.DEEP_SPACE
                || (cellToInspect.occupier && cellToInspect.occupier.playerId === myPlayer.id)) {
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
