# The Game

The Game for Entelect Challenge 2019 is **Worms**. 

In a match **2** players with **3** worms each will play against each other. The goal is to be the last player with any conscious worms left.

## Contents
- [The Game](https://github.com/EntelectChallenge/2019-Worms/blob/master/game-engine/game-rules.md#the-game)
  - [The Map](https://github.com/EntelectChallenge/2019-Worms/blob/master/game-engine/game-rules.md#the-map)
  - [The commands](https://github.com/EntelectChallenge/2019-Worms/blob/master/game-engine/game-rules.md#the-commands)
    - [Move](https://github.com/EntelectChallenge/2019-Worms/blob/master/game-engine/game-rules.md#move)
    - [Dig](https://github.com/EntelectChallenge/2019-Worms/blob/master/game-engine/game-rules.md#dig)
    - [Shoot](https://github.com/EntelectChallenge/2019-Worms/blob/master/game-engine/game-rules.md#shoot)
    - [Do Nothing](https://github.com/EntelectChallenge/2019-Worms/blob/master/game-engine/game-rules.md#do-nothing)
  - [Command Order](https://github.com/EntelectChallenge/2019-Worms/blob/master/game-engine/game-rules.md#command-order)
  - [Scores](https://github.com/EntelectChallenge/2019-Worms/blob/master/game-engine/game-rules.md#scores)

## The Map

The game is played on a grid of **33x33** cells. Every cell is one of the following types:
* Air - worms can move into and shoot through air cells
* Dirt - worms cannot move into or shoot through dirt cells, it has be dug out first
* Deep Space - worms cannot interact with deep space cells
* Lava - worms can move and shoot into lava cells, worms will sustain damage every round that they are in a lava cell

Cells can contain powerups. Powerups are picked up when a worm moves onto a cell. 
* A Healthpack will immediately restore **10** health to the worm who picks it up. 

Every cell has a coordinate in the form `X,Y` starting from `0,0` in the top left corner and increasing downwards and to the right. 

Two map files will be a available: A json file `state.json` and a text file `state.txt`. In addition the map will be rendered on the console during a running game. 

State files are explained in detail [here](https://github.com/EntelectChallenge/2019-Worms/blob/master/game-engine/state-files.md "Detailed explanation of the state files").

## The commands

In every round each player can submit one command for their active worm. The active worm will be determined by the game engine and will be indicated in the map files as described above. 

All player commands are validated before executing any commands. Invalid commands (eg. Invalid syntax, moving to an occupied cell) result in the worm doing nothing.

Both player's commands are executed at the same time (in a single round), and not sequentially. 

### Move
The format of the move command is `move x y`

* `x y` is the coordinate of the cell the worm is moving to
* Worms can move to any adjacent air cells (including diagonally)
* Worms cannot move to cells occupied by another worm
* Worms cannot move to dirt or deep space cells
* If two worms move to same cell in the same turn:
    * Both worms will take damage
    * Worms will either swap places or stay in their current positions (with an equal probability)

### Dig
The format of the dig command is `dig x y`

* `x y` is the coordinate of the cell the worm is digging out
* Worms can dig any adjacent dirt cells (including diagonally)
* Digging a dirt cells will change its type to air
* Two worms digging the same cell in the same turn is a valid move 

### Shoot
The format of the shoot command is `shoot {direction}`

* `{direction}` can be any of the eight principal directions: N (North), NE (North-East), E (East), SE (South-East), S (South), SW (South-West), W (West), NW (North-West)

![Compass](https://github.com/EntelectChallenge/2019-Worms/blob/master/game-engine/assets/images/compass-rose.png "The 8 principal wind directions https://en.wikipedia.org/wiki/File:Compass_rose_en_08p.svg")

* Shooting distance is measured in [euclidean distance](https://en.wikipedia.org/wiki/Euclidean_distance). To determine if a cell is in range, calculate its euclidean distance from the worm's position, round it downwards to the nearest integer (floor), and check if it is less than or equal to the max range

<a href="https://www.codecogs.com/eqnedit.php?latex=distance&space;=&space;\left&space;\lfloor&space;\sqrt{(x_{a}-x_{b})^{2}&space;+&space;(y_{a}-y_{2})^{2}}&space;\right&space;\rfloor" target="_blank"><img src="https://latex.codecogs.com/gif.latex?distance&space;=&space;\left&space;\lfloor&space;\sqrt{(x_{a}-x_{b})^{2}&space;+&space;(y_{a}-y_{2})^{2}}&space;\right&space;\rfloor" title="Euclidean Distance" /></a>

* Shots are blocked by dirt and deep space cells
* The first worm in range in the shooting direction will lose health equal to the weapon's damage
* When a worm's health is 0 or lower, it will fall unconscious and be removed from the map 
* Be careful! Friendly fire could hit your own worms

The two diagrams below illustrates how a worm can shoot with a maximum range of 3 and 4 respectively:

![Shooting Range 3](https://github.com/EntelectChallenge/2019-Worms/blob/master/game-engine/assets/images/shooting-range-3.PNG "Maximum Shooting Range 3")
![Shooting Range 4](https://github.com/EntelectChallenge/2019-Worms/blob/master/game-engine/assets/images/shooting-range-4.PNG "Maximum Shooting Range 4")

### Do Nothing
The `nothing` command can be used when a Player does not want to do anything. Any invalid commands will also be considered as doing nothing. 

If a player does nothing for **12** consecutive turns, their bot will be considered invalid and they will be disqualified from the match.

### Banana Bomb
The format of the Banana Bomb command is `banana x y`

* `x y` is the target coordinate where the Banana Bomb will be thrown
* Throwing distance is measured in [euclidean distance](https://en.wikipedia.org/wiki/Euclidean_distance). To determine if a cell is in range, calculate its euclidean distance from the worm's position, round it downwards to the nearest integer (floor), and check if it is less than or equal to the max range

<a href="https://www.codecogs.com/eqnedit.php?latex=distance&space;=&space;\left&space;\lfloor&space;\sqrt{(x_{a}-x_{b})^{2}&space;+&space;(y_{a}-y_{2})^{2}}&space;\right&space;\rfloor" target="_blank"><img src="https://latex.codecogs.com/gif.latex?distance&space;=&space;\left&space;\lfloor&space;\sqrt{(x_{a}-x_{b})^{2}&space;+&space;(y_{a}-y_{2})^{2}}&space;\right&space;\rfloor" title="Euclidean Distance" /></a>

* The Banana Bomb has a maximum throw range of **5**
* Banana Bombs can be thrown over dirt
* If a Banana Bomb is thrown into deep space, the Banana Bomb will be lost
* The Banana Bomb has a damage radius of **2**
  * The damage radius can be represented like this:
    * ▓▓▓▓▓▓▓▓▓▓▓▓▓▓
    * ▓▓▓▓▓▓░░▓▓▓▓▓▓
    * ▓▓▓▓░░░░░░▓▓▓▓
    * ▓▓░░░░██░░░░▓▓
    * ▓▓▓▓░░░░░░▓▓▓▓
    * ▓▓▓▓▓▓░░▓▓▓▓▓▓
    * ▓▓▓▓▓▓▓▓▓▓▓▓▓▓
  * The Banana bomb has a peak damage of **20**
  * Any worm caught within this radius during the impact, will be dealt damage to
    * Worms in the impact cell "██" are dealt peak damage
    * Worms within the damage radius "░░" will be dealt less damage, the further away from the impact cell they are
    * You will be awarded score based on the damage done to all worms in this radius
  * The Banana bomb will destroy any dirt in the damage radius
    * You will be rewarded the same score as if you dug out all the affected dirt blocks
    * If your opponent dug out 1 of these dirt blocks, your banana bomb will not get score for that block, since dig commands are executed before banana bomb commands 
    * When both players throw a banana bomb onto the same area, both players will get score for the dirt blocks destroyed as if their banana bomb was the first to hit
  * The Banana bomb will destroy any powerups in the damage radius
* When a worm's health is 0 or lower, it will fall unconscious and be removed from the map 
* Be careful! Friendly fire will damage your own worms
  *  You will be penalised with negative score based on the damage dealt

### Snowball
The format of the Snowball command is `snowball x y`

* `x y` is the target coordinate where the Snowball will be thrown
* Throwing distance is measured in [euclidean distance](https://en.wikipedia.org/wiki/Euclidean_distance). To determine if a cell is in range, calculate its euclidean distance from the worm's position, round it downwards to the nearest integer (floor), and check if it is less than or equal to the max range

<a href="https://www.codecogs.com/eqnedit.php?latex=distance&space;=&space;\left&space;\lfloor&space;\sqrt{(x_{a}-x_{b})^{2}&space;+&space;(y_{a}-y_{2})^{2}}&space;\right&space;\rfloor" target="_blank"><img src="https://latex.codecogs.com/gif.latex?distance&space;=&space;\left&space;\lfloor&space;\sqrt{(x_{a}-x_{b})^{2}&space;+&space;(y_{a}-y_{2})^{2}}&space;\right&space;\rfloor" title="Euclidean Distance" /></a>

* The Snowball has a maximum throw range of **5**
* Snowballs can be thrown over dirt
* If a Snowball is thrown into deep space, the Snowball will be lost
* The Snowball has an effect radius of **1**
  * The effect radius can be represented like this:
    * ░░░░░░░░░░
    * ░░██████░░
    * ░░██▓▓██░░
    * ░░██████░░
    * ░░░░░░░░░░
  * Any worm caught within this radius during the impact, will be frozen for **5** rounds
    * A frozen worm will not respond to any commands
      * These commands will not be considered invalid, and will not penalise your score
      * You can still successfully issue a **Select** command to use a different worm     
  * The Snowball hit at the "▓▓" cell.
    * Worms in the impact cell "▓▓", as well as those in the radius cells "██", are all frozen
    * You will be awarded **17** points for each worm you froze
  * The Snowball will not destroy powerups
* Be careful! Friendly fire will freeze your own worms
  *  You will be penalised with negative points based on the amount of worms frozen

### Select
The format of the select command is `select {worm id};{command}`

* `{worm id}` is the id number of your worm that you want to select
  * You can only select a living worm
* `{command}` is any of the above mentioned commands
* You must use this in combination with another action command, that will be executed by the selected worm
  * The following are examples for valid commands, hypothetically for round 5, selecting worm 1
    * ` C;5;select 1;move 1 1`
    * ` C;5;select 1;dig 1 1`
    * ` C;5;select 1;shoot N`
    * ` C;5;select 1;banana 1 1`
    * ` C;5;select 1;snowball 1 1`
* This will override the selected worm for your player, meaning that in the next round your 
selected worm index will start cycling from this selected worm 

## Command Order

All commands submitted in a round will be evaluated in the following order:
1. Select
1. Movement
2. Digging
3. Banana
4. Shooting
5. Snowball

This implies the following regarding command interaction:
* A worm cannot move into a cell that another worm is digging open in this round
* A worm can dig open a path for another worm's shot
* A worm can move into range of another worm's shot
* A worm can move out of range of another worm's shot 
* Two worms can dig open the same dirt cell in a single round
* A worm can dig a block right before a banana bomb destroys that block
* Any issued command that is valid at the start of the round, will be executed
  * If the enemy Agent worm knocks out your Technologist worm, your worm can still throw a 
 snowball if the command was issued in the same round

## Worm Profession

All worms have a profession, which will give it special attributes and weapons.
Worms can have only one of the following professions:
* `Commando`
  * Health → **150**
  * Can use the basic weapon to shoot
* `Agent`
  * Health → **100**
  * Can use the basic weapon to shoot
  * Trained to use **Banana Bombs**
* `Technologist`
  * Health → **100**
  * Can use the basic weapon to shoot
  * Trained to use **Snowballs**

## Scores

Player scores will only be considered in the case of a tie:
* The maximum amount of rounds (**400**) have passed and there is more than one player left
* If both players lost their last worm in the same round 

The total score value is determined by adding together the player's average worm health and the points for every single command the they played: 
* Attack:
  * Damaging any worm's health to zero or less gives **40** points
  * Damaging any worm gives points equal to damage dealt multiplied by **2**
  * Any of the above will **reduce** your points if they have been done to your own worm
  * A missed attack gives **2** points  
* Moving gives **5** point
* Digging gives **7** points
* Freezing gives **17** points
* Doing nothing gives **0** points
* An invalid command will **reduce** your points by **4**

## Endgame

The worms' disagreements here have initiated geological activity. A slow flood of lava will
 engulf the entire map if they cannot sort out their differences
* From round **100** lava will slowly creep onto the edges of the map
* At round **350** lava will have filled the entire map except for a small circular region 
in the center of the map, with a radius of **4** cells
* Lava will replace most cells as it floods over the map
  * Air cells are replaced by lava cells
  * Dirt cells are not replaced
  * Deep space cells are not replaced
  * When a digging worm forms new air cell in the flooded region, it is replaced by lava at the start of the next round
    * The same happens for destroyed dirt cells via a Banana Bomb
* Any worm standing on top of a lava cell, will sustain **3** damage every round
