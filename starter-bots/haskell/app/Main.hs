module Main where

import Bot

import System.IO

main :: IO ()
main = do
  hSetBuffering stdout NoBuffering
  startBot
