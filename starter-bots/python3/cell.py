# -*- coding: utf-8 -*-
"""
Entelect StarterBot for Python3
"""


class Cell:

    def __init__(self, x, y, type, occupied):
        self.x = x
        self.y = y
        self.type = type
        self.occupied = occupied


class AugmentedCell:

    def __init__(self, x, y, type, distance, direction):
        self.x = x
        self.y = y
        self.type = type
        self.distance = distance
        self.direction = direction
