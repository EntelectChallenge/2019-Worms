"use strict";

let fs = require('fs');
let readline = require('readline');
const stateFileName = 'state.json';

let myPlayer;
let opponent;
let gameMap;
let mapSize;

/**
 * @typedef Point {x: number, y: number}
 * @typedef Cell {x: number, y: number, type: string, occupier : object, powerup: object}
 * */

/**
 * @type {Cell[]}
 */
let cells;
let myCurrentWorm;

let strategies = {
    powerups: powerupStrategy,
    attack: attackStrategy,
    follow: followStrategy,
    hunt: huntStrategy,
};

/**
 * Maps worm ids to strategies
 */
let strategyPriorities = {
    1: [strategies.powerups(), strategies.hunt(1), strategies.attack()],
    2: [strategies.powerups(), strategies.hunt(1), strategies.follow(1), strategies.attack()],
    3: [strategies.powerups(), strategies.hunt(1), strategies.follow(1), strategies.follow(2), strategies.attack()]
};

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
    DIRT: 'DIRT',
    LAVA: 'LAVA'
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
    let stateFile = fs.readFileSync(`./rounds/${roundNumber}/${stateFileName}`);
    stateFile = JSON.parse(stateFile);

    initializeEntities(stateFile);

    let command = runStrategy() || doNothingCommand();
    console.log(`C;${roundNumber};${command}`);
}

/**
 * Initialize global variables from parsed state file
 */
function initializeEntities(gameState) {
    myPlayer = gameState.myPlayer;
    opponent = gameState.opponents[0];
    mapSize = gameState.mapSize;
    gameMap = gameState.map;
    cells = flatMap(gameMap);
    myCurrentWorm = myPlayer.worms.find(worm => worm.id === gameState.currentWormId);
}

/**
 * Run bot logic
 * @return {string} The bot's command
 */
function runStrategy() {
    let nearTarget = getApproachableOpponent();
    let targetPosition = nearTarget.worm.position;

    if (canBananaBombThem(nearTarget)) {
        return `banana ${targetPosition.x} ${targetPosition.y}`;
    } else if (canSnowballThem(nearTarget)) {
        return `snowball ${targetPosition.x} ${targetPosition.y}`;
    }

    // If other worms are in danger, we could SELECT them and give them a fighting chance
    if (myPlayer.remainingWormSelections > 0) {
        let otherWorms = myPlayer.worms.filter(worm => worm !== myCurrentWorm && worm.health > 0);
        for (let worm of otherWorms) {
            let cellAndTarget = getShootableOpponent(worm);
            if (cellAndTarget) {
                return `select ${worm.id};shoot ${cellAndTarget.direction}`;
            }
        }
    }

    // If myCurrentWorm is in danger, defend yourself
    let cellAndTarget = getShootableOpponent(myCurrentWorm);
    if (cellAndTarget) {
        return `shoot ${cellAndTarget.direction}`;
    }

    for (let strategy of strategyPriorities[myCurrentWorm.id]) {
        if (strategy.shouldExecute()) {
            return strategy.execute()
        }
    }
}

function canBananaBombThem(target) {
    return myCurrentWorm.bananaBombs
        && myCurrentWorm.bananaBombs.count > 0
        && target.distance <= myCurrentWorm.bananaBombs.range
        && target.distance > myCurrentWorm.bananaBombs.damageRadius * 0.75;
}

function canSnowballThem(target) {
    return myCurrentWorm.snowballs
        && myCurrentWorm.snowballs.count > 0
        && target.worm.roundsUntilUnfrozen == 0
        && target.distance <= myCurrentWorm.snowballs.range
        && target.distance > myCurrentWorm.snowballs.freezeRadius * Math.sqrt(2);
}

function doNothingCommand() {
    return `nothing`;
}

/**
 * A worm strategy that moves towards powerups
 */
function powerupStrategy() {
    return {
        name: 'powerups',
        shouldExecute: function () {
            return cells.some(c => c.powerup);
        },
        execute: function () {
            let nearPowerup = cells.filter(c => c.powerup)
                .map(c => ({
                    cell: c,
                    distance: euclideanDistance(myCurrentWorm.position, c)
                }))
                .sort((a, b) => a.distance - b.distance)[0];
            return digAndMoveTo(nearPowerup.cell);
        }
    };
}

/**
 * A worm strategy that moves towards the closest opponent
 */
function attackStrategy() {
    return {
        name: 'attack',
        shouldExecute: () => true,
        execute: function () {
            let nearTarget = getApproachableOpponent();
            return digAndMoveTo(nearTarget.worm.position);
        }
    }
}

/**
 * A worm strategy that follows another of my own worms
 */
function followStrategy(targetWormId) {
    return {
        name: 'follow',
        data: targetWormId,
        shouldExecute: function () {
            return myPlayer.worms
                .filter(w => w.health > 0)
                .find(w => w.id === targetWormId);
        },
        execute: function () {
            let leaderWorm = myPlayer.worms.find(w => w.id === targetWormId);
            if (euclideanDistance(myCurrentWorm.position, leaderWorm.position) > 3) {
                return digAndMoveTo(leaderWorm.position);
            }
            let nearTarget = getApproachableOpponent();
            return digAndMoveTo(nearTarget.worm.position);
        }
    };
}

/**
 * A worm strategy that moves towards a specific enemy worm
 * @param targetWormId
 */
function huntStrategy(targetWormId) {
    return {
        name: 'hunt',
        data: targetWormId,
        shouldExecute: function () {
            return opponent.worms
                .filter(w => w.health > 0)
                .find(w => w.id === targetWormId);
        },
        execute: function () {
            let preyWorm = opponent.worms.find(w => w.id === targetWormId);
            return digAndMoveTo(preyWorm.position);
        }
    }
}

/**
 * Returns a dig or move command towards the destination
 * @param destination {Point}
 * @return {string}
 */
function digAndMoveTo(destination) {
    let shortestPathCell = findNextCellInPath(myCurrentWorm.position, destination);

    if (shortestPathCell.occupier && shortestPathCell.occupier.playerId === myPlayer.id) {
        shortestPathCell = getRandomAdjacentCell();
    }

    if (shortestPathCell.type === surfaceTypes.DIRT) {
        return `dig ${shortestPathCell.x} ${shortestPathCell.y}`;

    } else if (shortestPathCell.type === surfaceTypes.AIR) {
        return `move ${shortestPathCell.x} ${shortestPathCell.y}`;
    }
}

/**
 * Find the cell adjacent to the origin that is the closest to the  destination
 * @param origin {Point}
 * @param destination {Point}
 * @return Cell
 */
function findNextCellInPath(origin, destination) {
    return cells.filter(c => !(c.x === origin.x && c.y === origin.y)
        && Math.abs(c.x - origin.x) <= 1
        && Math.abs(c.y - origin.y) <= 1)
        .map(c => ({
            cell: c,
            distance: euclideanDistance(destination, c)
        }))
        .sort((a, b) => a.distance - b.distance)[0]
        .cell;
}

/**
 * Find the closest opponent worm
 */
function getApproachableOpponent() {
    return opponent.worms.filter(w => w.health > 0)
        .map(w => ({
            worm: w,
            distance: euclideanDistance(myCurrentWorm.position, w.position)
        }))
        .sort((a, b) => a.distance - b.distance)[0];
}

/**
 * Get a random cell from all cells adjacent to my active worm
 * @return {Cell}
 */
function getRandomAdjacentCell() {
    let center = myCurrentWorm.position;
    let cellsAround = cells.filter(c => (c.x !== center.x || c.y !== center.y)
        && Math.abs(c.x - center.x) <= 1
        && Math.abs(c.y - center.y) <= 1);
    return cellsAround[Math.floor(Math.random() * cellsAround.length)];

}

/**
 * Add the x and y values of two coordinates together
 * @return Position
 */
function sumCoordinates(coordinateA, coordinateB) {
    return {
        x: coordinateA.x + coordinateB.x,
        y: coordinateA.y + coordinateB.y
    };
}

/**
 * Get any opponent worm that is in range and can be shot without being blocked
 */
function getShootableOpponent(worm) {
    let center = worm.position;
    let shootTemplates = getShootTemplates();

    for (let template of shootTemplates) {
        for (let deltaCoordinate of template.coordinates) {
            let coordinateToCheck = sumCoordinates(center, deltaCoordinate);
            if (coordinateIsOutOfBounds(coordinateToCheck)
                || euclideanDistance(coordinateToCheck, center) > worm.weapon.range) {
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

/**
 * Check if a coordinate is in the map bounds
 * @param coordinateToCheck {Point}
 * @return {boolean}
 */
function coordinateIsOutOfBounds(coordinateToCheck) {
    return coordinateToCheck.x < 0
        || coordinateToCheck.x >= mapSize
        || coordinateToCheck.y < 0
        || coordinateToCheck.y >= mapSize;
}

/**
 * Get the lines the active worm can shoot in
 */
function getShootTemplates() {
    let shootTemplates = [];

    for (let direction of directions) {
        let currentDirectionLine = buildDirectionLine(direction);
        shootTemplates.push({name: direction.name, coordinates: currentDirectionLine});
    }
    return shootTemplates;
}

/**
 * Build a list of all cells in a specific direction withing shooting range of my active worm
 * @param direction
 */
function buildDirectionLine(direction) {
    let currentDirectionLine = [];
    for (let i = 1; i <= myCurrentWorm.weapon.range; i++) {
        let cellOfLine = {x: i * direction.x, y: i * direction.y};
        currentDirectionLine.push(cellOfLine);
    }
    return currentDirectionLine;
}


/**
 * Calculate the distance between two points
 * https://en.wikipedia.org/wiki/Euclidean_distance
 *
 * @param positionA {Point}
 * @param positionB {Point}
 * @return {number}
 */
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
