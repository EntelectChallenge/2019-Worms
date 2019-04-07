# -*- coding: utf-8 -*-
'''
Entelect StarterBot for Python3
'''
from time import sleep
import json
import os
import random
import sys
import logging
import numpy as np
import pandas as pd
from scipy.spatial import distance

logging.basicConfig(filename='sample_python_bot.log', filemode='w', level=logging.DEBUG)
logger = logging.getLogger(__name__)


class StarterBot:
    
    def __init__(self, ):
        '''
        Initialize Bot .
        '''

        self.valid_directions = ['N','S','E','W','SE','SW','NE','NW']

        self.current_round = None
        self.command = ''

        self.game_state = None
        self.full_map = None
        self.flattened_map = None
        self.player_info = None
        self.enemy_info = None
        self.columns = None
        self.rows = None
        self.max_rounds = None
        self.current_worm_id = None
        self.consecutive_no_commands = None
        self.current_worm_info = None
        self.worms = None

        self.augmented_map = None

        return None

    def get_current_round_details(self):
        """
        Reads in all relevant information required for the round.
        """

        state_location = os.path.join('rounds',str(self.current_round),'state.json')
        self.game_state = self.load_state_json(state_location)

        self.command = ''

        self.full_map = self.game_state['map']
        self.flattened_map = self.get_flattened_map()
        self.rows = self.game_state['mapSize']
        self.columns = self.game_state['mapSize']

        self.max_rounds = self.game_state['maxRounds']
        self.current_worm_id = self.game_state['currentWormId']
        self.consecutive_no_commands = self.game_state

        self.player_info = self.game_state['myPlayer']
        self.enemy_info = self.game_state['opponents']

        self.worms = self.player_info['worms']
        self.current_worm_info = self.get_worm_player_info(self.current_worm_id)

        self.augmented_map = self.get_augmented_map()
        logger.info('Done loading new round info')

        return None

    def get_worm_player_info(self, worm_id):
        """
        Retrieve info for requested worm id
        """
        for worm in self.worms:
            if worm['id'] == worm_id:
                break
        return worm

    def get_flattened_map(self):
        """
        Generates a flattened game map, and creates a pandas Dataframe.
        This is used to easily filter cells without iteration.
        """
        flattened_map = []
        for row in self.full_map:
            flattened_map.extend(row)
        dataframe = pd.DataFrame.from_dict(flattened_map)

        return dataframe

    def starter_bot_logic(self):
        '''
        If one of the opponent's worms is within range fire at it.
            - Must be in range of current worm's weapon range.
            - No obstacles can be in the path.

        Otherwise choose a block in a random direction and do one of the following things
            - If the chosen block is air, move to that block
            - If the chosen block is dirt, dig out that block
            - If the chosen block is deep space, do nothing

        Commands in the format :
            MOVE - move <x> <y>
            DIG - dig <x> <y>
            SHOOT - shoot <direction { N, NE, E, SE, S, SW, W, NW }>
            DO NOTHING - nothing
        '''

        worms_in_range = self.get_worms_in_range()

        if len(worms_in_range) > 0:
            number_worms = len(worms_in_range)
            choice = np.random.randint(number_worms)
            attack_x = worms_in_range[choice][0]
            attack_y = worms_in_range[choice][1]
            direction = worms_in_range[choice][2]
            self.command = f'shoot {direction}'

        else:
            current_x = self.current_worm_info['position']['x']
            current_y = self.current_worm_info['position']['y']
            move_options = ['move','dig','nothing']
            choice = np.random.randint(len(move_options))
            selected_move = move_options[choice]
            if selected_move == 'nothing':
                self.command = selected_move
            elif selected_move == 'dig':
                valid_cells = self.augmented_map.dropna(axis = 'rows', subset=['Direction'])
                available_cells = self.augmented_map.loc[(self.augmented_map['Distance'] <= self.current_worm_info['diggingRange'])
                                                         & (self.augmented_map['type'] == 'DIRT')].reset_index(drop=False)
                number_avail_cells = len(available_cells)
                if number_avail_cells == 0:
                    self.command = f'nothing'
                else:
                    choice = np.random.randint(len(available_cells))
                    selected_cell = available_cells.iloc[choice]
                    self.command = f"dig {selected_cell['x']} {selected_cell['y']}"
            elif selected_move == 'move':
                valid_cells = self.augmented_map.dropna(axis = 'rows', subset=['Direction'])
                available_cells = self.augmented_map.loc[(self.augmented_map['Distance'] <= self.current_worm_info['movementRange'])
                                                         & (self.augmented_map['type'] == 'AIR')].reset_index(drop=False)
                number_avail_cells = len(available_cells)
                if number_avail_cells == 0:
                    self.command = f'nothing'
                else:
                    choice = np.random.randint(len(available_cells))
                    selected_cell = available_cells.iloc[choice]
                    self.command = f"move {selected_cell['x']} {selected_cell['y']}"
            else:
                self.command = f'nothing'


        return None


    def get_worms_in_range(self):
        """
        Returns a list of coordinates with worms in shooting range
        Takes into account if there are any obstacles in the way.
        If there is an obstacle, the worm is seen as not in range.
        """
        max_range = self.current_worm_info['weapon']['range']
        current_x = self.current_worm_info['position']['x']
        current_y = self.current_worm_info['position']['y']

        cells_in_range = []
        for opponent in self.enemy_info:
            for w in opponent['worms']:
                worm = w['position']
                dist = np.floor(distance.euclidean([worm['x'], worm['y']], [current_x, current_y]))
                if dist <= max_range:
                    direction = self.get_cardinal_direction([current_x, current_y], [worm['x'], worm['y']])
                    obstacles = self.check_for_obstacles_in_path([current_x, current_y], [worm['x'], worm['y']], direction)
                    if (direction is None) or (obstacles is True):
                        continue
                    else:
                        cells_in_range.append([worm['x'], worm['y'], direction])

        return cells_in_range

    def get_cardinal_direction(self, myself, opponent):
        '''
        If this function returns None, then the 'opponent' coordinates are not in a cardinal direction.
        Else, this function will return a valid cardinal direction.

        N.B. The order of the inputs are essential. This function is using 'myself' as a reference point,
        and the opponent as the target.

        This calculation is based on cartesian coordinate logic:
        If the gradient is undefined ( i.e. x_diff == 0 )
            - check if opponent is above ( y_diff < 0 )
                => return North
            - check if opponent is below ( y_diff > 0 )
                => return South
        Else If the gradient is 0 ( i.e. y_diff == 0 )
            - check if opponent is on the right ( x_diff > 0 )
                => return East
            - check if opponent is on the left ( x_diff < 0 )
                => return West

        North West and South East have a gradient of exactly +1 ( for this coordinate system )
            - The logic used, only checks
                - above for North West
                - below for South East
        North East and South West have a gradient of exactly -1 ( for this coordinate system )
            - The logic used, only checks
                - above for North East
                - below for South West
        '''

        x_diff = opponent[0] - myself[0]
        y_diff = opponent[1] - myself[1]
        direction = None
        if x_diff == 0:
            if y_diff < 0:
                direction = 'N'
            elif y_diff > 0:
                direction = 'S'
        elif y_diff == 0:
            if x_diff > 0:
                direction = 'E'
            elif x_diff < 0:
                direction = 'W'
        else:
            gradient = ( x_diff / y_diff )
            if gradient == 1:
                if y_diff < 0 :
                    direction = 'NW'
                elif y_diff > 0 :
                    direction = 'SE'
            if gradient == -1:
                if y_diff < 0 :
                    direction = 'NE'
                elif y_diff > 0 :
                    direction = 'SW'

        return direction

    def check_for_obstacles_in_path(self, reference, target, direction):
        """
        Takes in coordinates for a reference cell [x,y] ,target cell [x,y], and the direction.
        Checks if there are any DIRT cells within the path from reference and target in the specified direction.
        """
        if direction is not None:
            x_diff = target[0] - reference[0]
            y_diff = target[1] - reference[1]

            line_points = self.get_straight_line(reference, target, direction)

            obstacle_in_path = False

            for cell in line_points:
                type = self.flattened_map.loc[ (self.flattened_map['x'] == cell[0]) & (self.flattened_map['y'] == cell[1]), 'type']
                if len(type) == 0:
                    continue
                elif type.iloc[0] == "DIRT":
                    obstacle_in_path = True
                    break
        else:
            obstacle_in_path = True
        return obstacle_in_path


    def get_straight_line(self, start_point, end_point, direction):
        """
        This function returns all cells in a straight line from start to end point.
        Should only be used in one of the cardinal directions.
        """

        shift = np.array(self.get_shift(direction))
        start_point = np.array(start_point)
        end_point = np.array(end_point)

        cell_set = []
        done = False
        i = 0
        while not done:
            next_cell = start_point + (i*shift)
            if (next_cell[0] == end_point[0]) and (next_cell[1] == end_point[1]):
                done = True
            cell_set.append(next_cell)
            i = i + 1
        return np.array(cell_set)

    def get_shift(self, direction):
        '''
        Each cardinal direction, has a unique gradient and direction.
        These shift values, helps to generate a list of valid cells between two cells, in a specific cardinal direction.
        Since actions cannot be applied to cells not in a cardinal direction with reference to the selected worm.
        with reference to
        '''
        if direction == 'N':
            return [0, -1]
        elif direction == 'S':
            return [0, 1]
        elif direction == 'E':
            return [1, 0]
        elif direction == 'W':
            return [-1, 0]
        elif direction == 'NE':
            return [1, -1]
        elif direction == 'SE':
            return [1, 1]
        elif direction == 'SW':
            return [-1, 1]
        elif direction == 'NW':
            return [-1, -1]
        else:
            return None

    def get_augmented_map(self):
        """
        1. Calculates the distance to every cell from current cell.
        2. Checks the cardinal direction for cell with reference to current cell.
        3. Removes all cells that are 'DEEP_SPACE', since these cannot be interacted with.
        Returns a dataframe of self.flattened_map with additional columns ['x','y','type',''Distance', 'Direction']
        """
        augmented_map = self.flattened_map[['x','y','type']]

        current_x = self.current_worm_info['position']['x']
        current_y = self.current_worm_info['position']['y']

        augmented_map['Distance'] = augmented_map.apply(lambda row : np.floor(distance.euclidean([row['x'], row['y']], [current_x, current_y])), axis=1)
        augmented_map['Direction'] = augmented_map.apply(lambda row: self.get_cardinal_direction([current_x, current_y], [row['x'], row['y']]), axis=1)
        augmented_map = augmented_map.loc[~ (augmented_map['type'] == 'DEEP_SPACE')]

        return augmented_map

    def write_action(self):
        '''
        command in form : C;<round number>;<command>
        '''

        print(f'C;{self.current_round};{self.command}')
        logger.info(f'Writing command : C;{self.current_round};{self.command};')

        return None

    def load_state_json(self, state_location):
        '''
        Gets the current Game State json file.
        '''
        try:
            json_map = json.load(open(state_location, 'r'))
        except IOError:
            logger.error("Cannot load Game State")
        return json_map

    def wait_for_round_start(self):
        next_round = int(input())
        return next_round

    def run_bot(self):
        logger.info("Bot has started Running")
        while True:
            logger.info('Waiting for next round.')
            next_round_number = self.wait_for_round_start()
            logger.info('Starting Round : ' + str(next_round_number))
            self.current_round = next_round_number
            self.get_current_round_details()
            logger.info('Beginning StarterBot Logic Sequence')
            self.starter_bot_logic()
            self.write_action()

        return None


if __name__ == '__main__':

    bot = StarterBot()
    bot.run_bot()
