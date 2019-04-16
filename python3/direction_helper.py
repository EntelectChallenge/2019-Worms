import numpy as np


def get_shift(direction):
    """
    Each cardinal direction, has a unique gradient and direction.
    These shift values, helps to generate a list of valid cells between two cells, in a specific cardinal direction.
    Since actions cannot be applied to cells not in a cardinal direction with reference to the selected worm.
    with reference to
    """
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


def get_cardinal_direction(myself, opponent):
    """
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
    """

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
        gradient = (x_diff / y_diff)
        if gradient == 1:
            if y_diff < 0:
                direction = 'NW'
            elif y_diff > 0:
                direction = 'SE'
        if gradient == -1:
            if y_diff < 0:
                direction = 'NE'
            elif y_diff > 0:
                direction = 'SW'

    return direction


def get_straight_line(start_point, end_point, direction):
    """
    This function returns all cells in a straight line from start to end point.
    Should only be used in one of the cardinal directions.
    """

    shift = np.array(get_shift(direction))
    start_point = np.array(start_point)
    end_point = np.array(end_point)

    cell_set = []
    done = False
    i = 0
    while not done:
        next_cell = start_point + (i * shift)
        if (next_cell[0] == end_point[0]) and (next_cell[1] == end_point[1]):
            done = True
        cell_set.append(next_cell)
        i = i + 1
    return np.array(cell_set)
