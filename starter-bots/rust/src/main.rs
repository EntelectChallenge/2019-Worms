use std::io::prelude::*;
use std::io::stdin;

use rand::prelude::*;

mod command;
mod json;

use command::*;
use json::*;

fn main() {
    for line in stdin().lock().lines() {
        let round_number = line.expect("Failed to read line from stdin: {}");
        let command =
            match read_state_from_json_file(&format!("./rounds/{}/state.json", round_number)) {
                Ok(state) => choose_command(state),
                Err(e) => {
                    eprintln!("WARN: State file could not be parsed: {}", e);
                    Command::DoNothing
                }
            };
        println!("C;{};{}", round_number, command);
    }
}

fn choose_command(state: State) -> Command {
    match state.active_worm() {
        Some(worm) => {
            if let Some(direction) = find_worm_in_firing_distance(&state, worm) {
                Command::Shoot(direction)
            } else {
                let choices = valid_adjacent_positions(&state, &worm.position);
                let choice = choices
                    .choose(&mut rand::thread_rng())
                    .expect("No valid directions to move in");
                let chosen_cell = state.cell_at(&choice);

                match chosen_cell.map(|c| &c.cell_type) {
                    Some(CellType::Air) => Command::Move(choice.x, choice.y),
                    Some(CellType::Dirt) => Command::Dig(choice.x, choice.y),
                    Some(CellType::DeepSpace) | None => Command::DoNothing,
                }
            }
        }
        None => {
            eprintln!("WARN: The active worm did not appear in the state file");
            Command::DoNothing
        }
    }
}

fn find_worm_in_firing_distance(state: &State, worm: &PlayerWorm) -> Option<Direction> {
    let directions: [(Direction, Box<dyn Fn(&Position, u32) -> Option<Position>>); 8] = [
        (Direction::West, Box::new(|p, d| p.west(d))),
        (Direction::NorthWest, Box::new(|p, d| p.north(d).and_then(|p| p.west(d)))),
        (Direction::North, Box::new(|p, d| p.north(d))),
        (Direction::NorthEast, Box::new(|p, d| p.north(d).and_then(|p| p.east(d, state.map_size)))),
        (Direction::East, Box::new(|p, d| p.east(d, state.map_size))),
        (Direction::SouthEast, Box::new(|p, d| p.south(d, state.map_size).and_then(|p| p.east(d, state.map_size)))),
        (Direction::South, Box::new(|p, d| p.south(d, state.map_size))),
        (Direction::SouthWest, Box::new(|p, d| p.south(d, state.map_size).and_then(|p| p.west(d)))),
    ];

    for (dir, dir_fn) in &directions {
        let range = adjust_range_for_diagonals(dir, worm.weapon.range);

        for distance in 1..=range {
            let target = dir_fn(&worm.position, distance);
            match target.and_then(|t| state.cell_at(&t)) {
                Some(Cell {
                    occupier: Some(CellWorm::OpponentWorm { .. }),
                    ..
                }) => return Some(*dir),
                Some(Cell {
                    cell_type: CellType::Air,
                    ..
                }) => continue,
                _ => break,
            }
        }
    }
    None
}

fn adjust_range_for_diagonals(dir: &Direction, straight_range: u32) -> u32 {
    if dir.is_diagonal() {
        ((straight_range as f32 + 1.) / 2f32.sqrt()).floor() as u32
    } else {
        straight_range
    }
}

fn valid_adjacent_positions(state: &State, pos: &Position) -> Vec<Position> {
    let choices = [
        pos.west(1),
        pos.west(1).and_then(|p| p.north(1)),
        pos.north(1),
        pos.north(1).and_then(|p| p.east(1, state.map_size)),
        pos.east(1, state.map_size),
        pos.east(1, state.map_size)
            .and_then(|p| p.south(1, state.map_size)),
        pos.south(1, state.map_size),
        pos.south(1, state.map_size).and_then(|p| p.west(1)),
    ];
    choices.iter().flatten().cloned().collect()
}

#[cfg(test)]
mod test {
    use super::*;

    #[test]
    fn adjacent_positions_give_valid_positions() {
        let dummy_state = State {
            current_round: 0,
            max_rounds: 0,
            map_size: 3,
            current_worm_id: 0,
            consecutive_do_nothing_count: 0,
            my_player: Player {
                id: 0,
                score: 0,
                health: 0,
                worms: Vec::new(),
            },
            opponents: Vec::new(),
            map: Vec::new(),
        };

        assert_eq!(
            3,
            valid_adjacent_positions(&dummy_state, &Position { x: 0, y: 0 }).len()
        );
        assert_eq!(
            5,
            valid_adjacent_positions(&dummy_state, &Position { x: 1, y: 0 }).len()
        );
        assert_eq!(
            3,
            valid_adjacent_positions(&dummy_state, &Position { x: 2, y: 0 }).len()
        );
        assert_eq!(
            5,
            valid_adjacent_positions(&dummy_state, &Position { x: 0, y: 1 }).len()
        );
        assert_eq!(
            8,
            valid_adjacent_positions(&dummy_state, &Position { x: 1, y: 1 }).len()
        );
        assert_eq!(
            5,
            valid_adjacent_positions(&dummy_state, &Position { x: 2, y: 1 }).len()
        );
        assert_eq!(
            3,
            valid_adjacent_positions(&dummy_state, &Position { x: 0, y: 2 }).len()
        );
        assert_eq!(
            5,
            valid_adjacent_positions(&dummy_state, &Position { x: 1, y: 2 }).len()
        );
        assert_eq!(
            3,
            valid_adjacent_positions(&dummy_state, &Position { x: 2, y: 2 }).len()
        );
    }

    #[test]
    fn range_adjustment_matches_examples() {
        assert_eq!(1, adjust_range_for_diagonals(&Direction::East, 1));
        assert_eq!(2, adjust_range_for_diagonals(&Direction::East, 2));
        assert_eq!(3, adjust_range_for_diagonals(&Direction::East, 3));
        assert_eq!(4, adjust_range_for_diagonals(&Direction::East, 4));

        assert_eq!(1, adjust_range_for_diagonals(&Direction::SouthEast, 1));
        assert_eq!(2, adjust_range_for_diagonals(&Direction::SouthEast, 2));
        assert_eq!(2, adjust_range_for_diagonals(&Direction::SouthEast, 3));
        assert_eq!(3, adjust_range_for_diagonals(&Direction::SouthEast, 4));
    }

    mod find_worm_in_firing_distance {
        use super::super::*;

        fn worm_shooting_dummy_state() -> (State, PlayerWorm) {
            let dummy_state = State {
                current_round: 0,
                max_rounds: 0,
                map_size: 5,
                current_worm_id: 0,
                consecutive_do_nothing_count: 0,
                my_player: Player {
                    id: 0,
                    score: 0,
                    health: 0,
                    worms: Vec::new(),
                },
                opponents: Vec::new(),
                map: vec![Vec::new()],
            };
            let active_worm = PlayerWorm {
                id: 0,
                health: 100,
                position: Position { x: 2, y: 2 },
                digging_range: 1,
                movement_range: 1,
                weapon: Weapon {
                    range: 3,
                    damage: 1,
                },
            };

            (dummy_state, active_worm)
        }

        #[test]
        fn finds_a_worm_that_can_be_shot() {
            let (mut dummy_state, active_worm) = worm_shooting_dummy_state();
            dummy_state.map[0].push(Cell {
                x: 3,
                y: 2,
                cell_type: CellType::Air,
                occupier: None,
                powerup: None,
            });
            dummy_state.map[0].push(Cell {
                x: 4,
                y: 2,
                cell_type: CellType::Air,
                occupier: Some(CellWorm::OpponentWorm {
                    id: 0,
                    player_id: 1,
                    health: 0,
                    position: Position { x: 4, y: 2 },
                    digging_range: 1,
                    movement_range: 1,
                }),
                powerup: None,
            });

            let firing_dir = find_worm_in_firing_distance(&dummy_state, &active_worm);
            assert_eq!(Some(Direction::East), firing_dir);
        }

        #[test]
        fn worm_cant_shoot_through_dirt() {
            let (mut dummy_state, active_worm) = worm_shooting_dummy_state();
            dummy_state.map[0].push(Cell {
                x: 3,
                y: 2,
                cell_type: CellType::Dirt,
                occupier: None,
                powerup: None,
            });
            dummy_state.map[0].push(Cell {
                x: 4,
                y: 2,
                cell_type: CellType::Air,
                occupier: Some(CellWorm::OpponentWorm {
                    id: 0,
                    player_id: 1,
                    health: 0,
                    position: Position { x: 4, y: 2 },
                    digging_range: 1,
                    movement_range: 1,
                }),
                powerup: None,
            });

            let firing_dir = find_worm_in_firing_distance(&dummy_state, &active_worm);
            assert_eq!(None, firing_dir);
        }

        #[test]
        fn identifies_lack_of_worms_to_shoot() {
            let (mut dummy_state, active_worm) = worm_shooting_dummy_state();
            dummy_state.map[0].push(Cell {
                x: 3,
                y: 2,
                cell_type: CellType::Air,
                occupier: None,
                powerup: None,
            });
            dummy_state.map[0].push(Cell {
                x: 4,
                y: 2,
                cell_type: CellType::Air,
                occupier: None,
                powerup: None,
            });

            let firing_dir = find_worm_in_firing_distance(&dummy_state, &active_worm);
            assert_eq!(None, firing_dir);
        }
    }
}
