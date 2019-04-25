use std::fmt;

#[derive(Clone, Copy, Debug, PartialEq, Eq)]
pub enum Command {
    Move(u32, u32),
    Dig(u32, u32),
    Shoot(Direction),
    DoNothing,
}

impl fmt::Display for Command {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        use Command::*;
        match self {
            Move(x, y) => write!(f, "move {} {}", x, y),
            Dig(x, y) => write!(f, "dig {} {}", x, y),
            Shoot(dir) => write!(f, "shoot {}", dir),
            DoNothing => write!(f, "nothing"),
        }
    }
}

#[derive(Clone, Copy, Debug, PartialEq, Eq)]
pub enum Direction {
    North,
    NorthEast,
    East,
    SouthEast,
    South,
    SouthWest,
    West,
    NorthWest,
}

impl fmt::Display for Direction {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        use Direction::*;
        let s = match self {
            North => "N",
            NorthEast => "NE",
            East => "E",
            SouthEast => "SE",
            South => "S",
            SouthWest => "SW",
            West => "W",
            NorthWest => "NW",
        };
        f.write_str(s)
    }
}

impl Direction {
    pub fn is_diagonal(&self) -> bool {
        use Direction::*;

        match self {
            NorthEast | SouthEast | SouthWest | NorthWest => true,
            _ => false,
        }
    }
}
