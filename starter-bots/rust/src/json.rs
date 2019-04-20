use std::error::Error;
use std::fs::File;
use std::io::prelude::*;

use serde::{Deserialize, Serialize};
use serde_json;

pub fn read_state_from_json_file(filename: &str) -> Result<State, Box<Error>> {
    let mut file = File::open(filename)?;
    let mut content = String::new();
    file.read_to_string(&mut content)?;
    let state: State = serde_json::from_str(content.as_ref())?;

    Ok(state)
}

#[derive(Serialize, Deserialize, Clone, Debug, PartialEq, Eq)]
#[serde(rename_all = "camelCase")]
pub struct State {
    pub current_round: u32,
    pub max_rounds: u32,
    pub map_size: u32,
    pub current_worm_id: i32,
    pub consecutive_do_nothing_count: u32,
    pub my_player: Player,
    pub opponents: Vec<Opponent>,
    pub map: Vec<Vec<Cell>>,
}

#[derive(Serialize, Deserialize, Clone, Debug, PartialEq, Eq)]
#[serde(rename_all = "camelCase")]
pub struct Player {
    pub id: i32,
    pub score: i32,
    pub health: i32,
    pub worms: Vec<PlayerWorm>,
}

#[derive(Serialize, Deserialize, Clone, Debug, PartialEq, Eq)]
#[serde(rename_all = "camelCase")]
pub struct PlayerWorm {
    pub id: i32,
    pub health: i32,
    pub position: Position,
    pub digging_range: u32,
    pub movement_range: u32,
    pub weapon: Weapon,
}

#[derive(Serialize, Deserialize, Clone, Debug, PartialEq, Eq)]
#[serde(rename_all = "camelCase")]
pub struct Opponent {
    pub id: i32,
    pub score: i32,
    pub worms: Vec<OpponentWorm>,
}

#[derive(Serialize, Deserialize, Clone, Debug, PartialEq, Eq)]
#[serde(rename_all = "camelCase")]
pub struct OpponentWorm {
    pub id: i32,
    pub health: i32,
    pub position: Position,
    pub digging_range: u32,
    pub movement_range: u32,
}

#[derive(Serialize, Deserialize, Clone, Debug, PartialEq, Eq)]
#[serde(rename_all = "camelCase")]
pub struct Cell {
    pub x: u32,
    pub y: u32,
    #[serde(rename = "type")]
    pub cell_type: CellType,
    pub occupier: Option<CellWorm>,
    pub powerup: Option<Powerup>,
}

#[derive(Serialize, Deserialize, Clone, Debug, PartialEq, Eq)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum CellType {
    Air,
    Dirt,
    DeepSpace,
}

#[derive(Serialize, Deserialize, Clone, Debug, PartialEq, Eq)]
#[serde(untagged)]
#[serde(rename_all = "camelCase")]
pub enum CellWorm {
    #[serde(rename_all = "camelCase")]
    PlayerWorm {
        id: i32,
        player_id: i32,
        health: i32,
        position: Position,
        digging_range: u32,
        movement_range: u32,
        weapon: Weapon,
    },
    #[serde(rename_all = "camelCase")]
    OpponentWorm {
        id: i32,
        player_id: i32,
        health: i32,
        position: Position,
        digging_range: u32,
        movement_range: u32,
    },
}

#[derive(Serialize, Deserialize, Clone, Debug, PartialEq, Eq)]
#[serde(rename_all = "camelCase")]
pub struct Powerup {
    #[serde(rename = "type")]
    pub powerup_type: PowerupType,
    pub value: i32,
}

#[derive(Serialize, Deserialize, Clone, Debug, PartialEq, Eq)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum PowerupType {
    HealthPack,
}

#[derive(Serialize, Deserialize, Clone, Debug, PartialEq, Eq)]
#[serde(rename_all = "camelCase")]
pub struct Position {
    pub x: u32,
    pub y: u32,
}

#[derive(Serialize, Deserialize, Clone, Debug, PartialEq, Eq)]
#[serde(rename_all = "camelCase")]
pub struct Weapon {
    pub damage: u32,
    pub range: u32,
}

impl State {
    pub fn active_worm(&self) -> Option<&PlayerWorm> {
        self.my_player
            .worms
            .iter()
            .find(|w| w.id == self.current_worm_id)
    }

    pub fn cell_at(&self, pos: &Position) -> Option<&Cell> {
        self.map
            .iter()
            .flatten()
            .find(|c| c.x == pos.x && c.y == pos.y)
    }
}

impl Position {
    pub fn west(&self, distance: u32) -> Option<Position> {
        self.x
            .checked_sub(distance)
            .map(|x| Position { x, y: self.y })
    }
    pub fn east(&self, distance: u32, max: u32) -> Option<Position> {
        self.x
            .checked_add(distance)
            .filter(|&x| x < max)
            .map(|x| Position { x, y: self.y })
    }
    pub fn north(&self, distance: u32) -> Option<Position> {
        self.y
            .checked_sub(distance)
            .map(|y| Position { x: self.x, y })
    }
    pub fn south(&self, distance: u32, max: u32) -> Option<Position> {
        self.y
            .checked_add(distance)
            .filter(|&y| y < max)
            .map(|y| Position { x: self.x, y })
    }
}

#[cfg(test)]
mod test {
    use super::*;

    #[test]
    fn example_parses_correctly() {
        let example = r#"
{
  "currentRound": 0,
  "maxRounds": 200,
  "mapSize": 33,
  "currentWormId": 1,
  "consecutiveDoNothingCount": 0,
  "myPlayer": {
    "id": 1,
    "score": 100,
    "health": 300,
    "worms": [
      {
        "id": 1,
        "health": 100,
        "position": {
          "x": 24,
          "y": 29
        },
        "weapon": {
          "damage": 1,
          "range": 3
        },
        "diggingRange": 1,
        "movementRange": 1
      }
    ]
  },
  "opponents": [
    {
      "id": 2,
      "score": 100,
      "worms": [
        {
          "id": 1,
          "health": 100,
          "position": {
            "x": 31,
            "y": 16
          },
          "diggingRange": 1,
          "movementRange": 1
        }
      ]
    }
  ],
  "map": [
    [
      {
        "x": 0,
        "y": 0,
        "type": "DEEP_SPACE"
      },
      {
        "x": 1,
        "y": 0,
        "type": "AIR"
      },
      {
        "x": 2,
        "y": 0,
        "type": "DIRT"
      }
    ],
    [
      {
        "x": 0,
        "y": 1,
        "type": "AIR",
        "powerup": {
          "type": "HEALTH_PACK",
          "value": 5
        }
      },
      {
        "x": 1,
        "y": 1,
        "type": "AIR",
        "occupier": {
          "id": 1,
          "playerId": 2,
          "health": 100,
          "position": {
            "x": 1,
            "y": 1
          },
          "diggingRange": 1,
          "movementRange": 1
        }
      },
      {
        "x": 2,
        "y": 1,
        "type": "AIR",
        "occupier": {
          "id": 1,
          "playerId": 1,
          "health": 100,
          "position": {
            "x": 2,
            "y": 1
          },
          "weapon": {
            "damage": 1,
            "range": 3
          },
          "diggingRange": 1,
          "movementRange": 1
        }
      }
    ]
  ]
}"#;

        let expected = State {
            current_round: 0,
            max_rounds: 200,
            map_size: 33,
            current_worm_id: 1,
            consecutive_do_nothing_count: 0,
            my_player: Player {
                id: 1,
                score: 100,
                health: 300,
                worms: vec![PlayerWorm {
                    id: 1,
                    health: 100,
                    position: Position { x: 24, y: 29 },
                    weapon: Weapon {
                        damage: 1,
                        range: 3,
                    },
                    digging_range: 1,
                    movement_range: 1,
                }],
            },
            opponents: vec![Opponent {
                id: 2,
                score: 100,
                worms: vec![OpponentWorm {
                    id: 1,
                    health: 100,
                    position: Position { x: 31, y: 16 },
                    digging_range: 1,
                    movement_range: 1,
                }],
            }],
            map: vec![
                vec![
                    Cell {
                        x: 0,
                        y: 0,
                        cell_type: CellType::DeepSpace,
                        occupier: None,
                        powerup: None,
                    },
                    Cell {
                        x: 1,
                        y: 0,
                        cell_type: CellType::Air,
                        occupier: None,
                        powerup: None,
                    },
                    Cell {
                        x: 2,
                        y: 0,
                        cell_type: CellType::Dirt,
                        occupier: None,
                        powerup: None,
                    },
                ],
                vec![
                    Cell {
                        x: 0,
                        y: 1,
                        cell_type: CellType::Air,
                        occupier: None,
                        powerup: Some(Powerup {
                            powerup_type: PowerupType::HealthPack,
                            value: 5,
                        }),
                    },
                    Cell {
                        x: 1,
                        y: 1,
                        cell_type: CellType::Air,
                        occupier: Some(CellWorm::OpponentWorm {
                            id: 1,
                            player_id: 2,
                            health: 100,
                            position: Position { x: 1, y: 1 },
                            digging_range: 1,
                            movement_range: 1,
                        }),
                        powerup: None,
                    },
                    Cell {
                        x: 2,
                        y: 1,
                        cell_type: CellType::Air,
                        occupier: Some(CellWorm::PlayerWorm {
                            id: 1,
                            player_id: 1,
                            health: 100,
                            position: Position { x: 2, y: 1 },
                            digging_range: 1,
                            movement_range: 1,
                            weapon: Weapon {
                                damage: 1,
                                range: 3,
                            },
                        }),
                        powerup: None,
                    },
                ],
            ],
        };

        let parsed: State = serde_json::from_str(example).unwrap();

        assert_eq!(
            parsed, expected,
            "Parsed value did not match the expected value.\nParsed = {:#?}\nExpected = {:#?}",
            parsed, expected
        );
    }

    #[test]
    fn west_moving_stays_in_bounds() {
        let pos = Position { x: 1, y: 1 };
        assert_eq!(pos.west(1), Some(Position { x: 0, y: 1 }));
        assert_eq!(pos.west(2), None);
    }

    #[test]
    fn east_moving_stays_in_bounds() {
        let pos = Position { x: 1, y: 1 };
        assert_eq!(pos.east(1, 3), Some(Position { x: 2, y: 1 }));
        assert_eq!(pos.east(2, 3), None);
    }

    #[test]
    fn north_moving_stays_in_bounds() {
        let pos = Position { x: 1, y: 1 };
        assert_eq!(pos.north(1), Some(Position { x: 1, y: 0 }));
        assert_eq!(pos.north(2), None);
    }

    #[test]
    fn south_moving_stays_in_bounds() {
        let pos = Position { x: 1, y: 1 };
        assert_eq!(pos.south(1, 3), Some(Position { x: 1, y: 2 }));
        assert_eq!(pos.south(2, 3), None);
    }
}
