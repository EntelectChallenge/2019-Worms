use std::io::stdin;
use std::io::prelude::*;

use rand::prelude::*;

mod json;
mod command;

use json::*;
use command::*;

fn main() {
    for line in stdin().lock().lines() {
        let round_number = line.expect("Failed to read line from stdin: {}");
        let state = read_state_from_json_file(&format!("./rounds/{}/state.json", round_number)).unwrap();
        let command = choose_command(state);
        println!("C;{};{}", round_number, command);
    }
}


fn choose_command(state: State) -> Command {
    let worm = state.active_worm();
    
    if let Some(direction) = find_worm_in_firing_distance(&state, worm) {
        Command::Shoot(direction)
    } else {
        let choices = valid_adjacent_positions(&state, &worm.position);
        let choice = choices.choose(&mut rand::thread_rng()).unwrap();
        let chosen_cell = state.cell_at(&choice);
        
        match chosen_cell.cell_type {
            CellType::Air => Command::Move(choice.x, choice.y),
            CellType::Dirt => Command::Dig(choice.x, choice.y),
            CellType::DeepSpace => Command::DoNothing
        }
    }
}

fn find_worm_in_firing_distance(state: &State, worm: &PlayerWorm) -> Option<Direction> {
    let directions: [(Direction, Box<dyn Fn(&Position, u32) -> Option<Position>>); 8] = [
        (Direction::West, Box::new(|p, d| p.west(d))),
        (Direction::NorthWest, Box::new(|p, d| p.north(d).and_then(|p| p.west(d)))),
        (Direction::North,  Box::new(|p, d| p.north(d))),
        (Direction::NorthEast, Box::new(|p, d| p.north(d).and_then(|p| p.east(d, state.map_size)))),
        (Direction::East,  Box::new(|p, d| p.east(d, state.map_size))),
        (Direction::SouthEast, Box::new(|p, d| p.south(d, state.map_size).and_then(|p| p.east(d, state.map_size)))),
        (Direction::South,  Box::new(|p, d| p.south(d, state.map_size))),
        (Direction::SouthWest, Box::new(|p, d| p.south(d, state.map_size).and_then(|p| p.west(d)))),
    ];

    for (dir, dir_fn) in &directions {
        let straight_range = worm.weapon.range;
        let range = if dir.is_diagonal() {
            ((straight_range as f32 + 1.) / 2f32.sqrt()).floor() as u32
        } else {
            straight_range
        };

        for distance in 1..=range {
            let target = dir_fn(&worm.position, distance);
            match target.map(|t| state.cell_at(&t)) {
                Some(Cell { occupier: Some(CellWorm::OpponentWorm{..}), ..}) => return Some(*dir),
                Some(Cell { cell_type: CellType::Air, ..}) => continue,
                _ => break
            }
        }
    }
    None
}

fn valid_adjacent_positions(state: &State, pos: &Position) -> Vec<Position> {
    let choices = [
        pos.west(1),
        pos.west(1).and_then(|p| p.north(1)),
        pos.north(1),
        pos.north(1).and_then(|p| p.east(1, state.map_size)),
        pos.east(1, state.map_size),
        pos.east(1, state.map_size).and_then(|p| p.south(1, state.map_size)),
        pos.south(1, state.map_size),
        pos.south(1, state.map_size).and_then(|p| p.west(1))
    ];
    choices.iter().flatten().cloned().collect()
}
