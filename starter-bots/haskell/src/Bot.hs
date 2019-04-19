{-# LANGUAGE ScopedTypeVariables #-}
{-# LANGUAGE DeriveGeneric #-}
{-# LANGUAGE OverloadedStrings #-}
{-# LANGUAGE FlexibleInstances #-}

module Bot
  where

import qualified Data.Vector as V
import GHC.Generics (Generic)
import qualified Data.ByteString.Lazy as B
import qualified Data.ByteString.Lazy.Char8 as B8
import System.IO

import Data.Aeson (decode,
                   withObject,
                   (.:),
                   encode,
                   FromJSON,
                   ToJSON,
                   parseJSON)

data State = State { currentRound :: Int,
                     maxRounds :: Int,
                     mapSize :: Int,
                     currentWormId :: Int,
                     consecutiveDoNothingCount :: Int,
                     myPlayer :: Player,
                     opponents :: V.Vector Opponent,
                     map :: V.Vector (V.Vector Cell) }
             deriving (Show, Generic, Eq)

instance FromJSON State
instance ToJSON   State

data Player = Player { id :: Int,
                       score :: Int,
                       health :: Int,
                       worms :: V.Vector Worm }
              deriving (Show, Generic, Eq)

instance FromJSON Player
instance ToJSON   Player

data Opponent = Opponent { opponentsId :: Int,
                           opponentsScore :: Int }
                           -- opponentsWorms :: V.Vector OpponentsWorm }
              deriving (Show, Generic, Eq)

instance ToJSON   Opponent
instance FromJSON Opponent where
  parseJSON = withObject "Opponent" $ \ v ->
    Opponent <$> v .: "id"
             <*> v .: "score"
             -- <*> v .: "worms"

data Worm = Worm { wormId :: Int,
                   wormHealth :: Int,
                   position :: Coord,
                   weapon :: Weapon,
                   diggingRange :: Int,
                   movementRange :: Int }
            deriving (Show, Generic, Eq)

instance ToJSON   Worm
instance FromJSON Worm where
  parseJSON = withObject "Worm" $ \ v ->
    Worm <$> v .: "id"
         <*> v .: "health"
         <*> v .: "position"
         <*> v .: "weapon"
         <*> v .: "diggingRange"
         <*> v .: "movementRange"

data OpponentWorm = OpponentWorm { opWormId :: Int,
                                   opWormHealth :: Int,
                                   opPosition :: Coord,
                                   opDiggingRange :: Int,
                                   opMovementRange :: Int }
            deriving (Show, Generic, Eq)

instance ToJSON   OpponentWorm
instance FromJSON OpponentWorm where
  parseJSON = withObject "OpponentWorm" $ \ v ->
    OpponentWorm <$> v .: "id"
                 <*> v .: "health"
                 <*> v .: "position"
                 <*> v .: "diggingRange"
                 <*> v .: "movementRange"

data Coord = Coord { xCoord :: Int, yCoord :: Int }
  deriving (Show, Generic, Eq)

instance ToJSON   Coord
instance FromJSON Coord where
  parseJSON = withObject "Coord" $ \ v ->
    Coord <$> v .: "x"
          <*> v .: "y"

data Weapon = Weapon { damage :: Int,
                       range :: Int }
              deriving (Show, Generic, Eq)

instance FromJSON Weapon
instance ToJSON   Weapon

data Cell = Cell { x :: Int,
                   y :: Int,
                   cellType :: String }
            deriving (Show, Generic, Eq)

instance ToJSON   Cell
instance FromJSON Cell where
  parseJSON = withObject "Cell" $ \ v ->
    Cell <$> v .: "x"
         <*> v .: "y"
         <*> v .: "type"

readGameState :: Int -> IO State
readGameState r = do
  stateString <- B.readFile $ "./rounds/" ++ show r ++ "/state.json"
  let Just state = decode stateString
  return state

startBot :: IO ()
startBot = do
  round :: Int <- readLn
  state <- readGameState round
  B.hPutStr stderr $ encode state
  B8.putStrLn "nothing"
  startBot
