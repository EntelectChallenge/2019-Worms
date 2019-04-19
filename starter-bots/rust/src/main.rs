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
    
   
    /*
    1. If one of the opponent's worms is within range fire at it
     */

    /*
    2. Otherwise choose a block in a random direction and do one of the following things

    2.1. If the chosen block is air, move to that block
    2.2. If the chosen block is dirt, dig out that block
    2.3. If the chosen block is deep space, do nothing
     */

    let choices = valid_adjacent_positions(&state, &worm.position);
    let choice = choices.choose(&mut rand::thread_rng()).unwrap();
    let chosen_cell = state.cell_at(&choice);
    
    match chosen_cell.cell_type {
        CellType::Air => Command::Move(choice.x, choice.y),
        CellType::Dirt => Command::Dig(choice.x, choice.y),
        CellType::DeepSpace => Command::DoNothing
    }
}

fn valid_adjacent_positions(state: &State, pos: &Position) -> Vec<Position> {
    let mut choices = Vec::new();
    if pos.x > 0 {
        choices.push(pos.west());
        if pos.y > 0 {
            choices.push(pos.west().north());
        }
        if pos.y+1 < state.map_size {
            choices.push(pos.west().south());
        }
    }
    if pos.x+1 < state.map_size {
        choices.push(pos.east());
        if pos.y > 0 {
            choices.push(pos.east().north());
        }
        if pos.y+1 < state.map_size {
            choices.push(pos.east().south());
        }
    }
    if pos.y > 0 {
        choices.push(pos.north());
    }
    if pos.y+1 < state.map_size {
        choices.push(pos.south());
    }
    choices
}
