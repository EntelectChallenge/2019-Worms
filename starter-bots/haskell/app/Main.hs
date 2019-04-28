module Main where

import Bot
import System.Random

import System.IO

main :: IO ()
main = do
  hSetBuffering stdout NoBuffering
  g <- getStdGen
  startBot g 1
