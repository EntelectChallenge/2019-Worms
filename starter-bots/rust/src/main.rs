use std::io::stdin;
use std::io::prelude::*;

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
    Command::DoNothing
}
