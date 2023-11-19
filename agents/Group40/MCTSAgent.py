import numpy as np
import math
from timeit import default_timer as timer
from random import shuffle

class MCTSNode():
    def __init__(self, colour, move = None):
        self.children = []
        # N = number of simulations that was played involving this node
        self.N = 0
        # Q = Sum of all backpropogated payouts
        self.Q = 0
        # Last move that was made on parent board to get to this node
        # Used to reconstruct the board without saving the entire board in the nodes
        self.move = move
        # Whose turn it is on that node
        self.colour = colour

    def is_leaf(self):
        return len(self.children) == 0
    
class UCT():
    def __init__(self):
        self.C = math.sqrt(2)

    def calculate_value(self, node: MCTSNode, parent: MCTSNode):
        return (node.Q / node.N) + (self.C * math.sqrt(math.log(parent.N) / node.N))

class ExpandAll():
    def __init__(self):
        pass
    
    @staticmethod
    def generate_new_nodes(board, colour):
        moves = get_legal_moves(board)
        nodes = [None] * len(moves)
        for i, move in enumerate(moves):
            node = MCTSNode(colour, move)
            nodes[i] = node
        return nodes
    
def get_opp_colour(colour):
        """Returns the char representation of the colour opposite to the
        current one.
        """
        if colour == "R":
            return "B"
        elif colour == "B":
            return "R"
        else:
            return "None"
        
def get_legal_moves(board):
    moves = []
    for rowIndex in range(len(board)):
        curRow = board[rowIndex]
        for colIndex in range(len(curRow)):
            cell_color = curRow[colIndex]
            if cell_color == "0":
                moves.append((rowIndex, colIndex))
    return moves

def convert_board_to_string(board):
    pass

def get_winner(board):
    # Use the engine's code to get the winner of a board.
    from src.Board import Board as engineBoard
    ref_board = engineBoard()
    ref_board.from_string(convert_board_to_string(board))
    ref_board.has_ended()
    return ref_board.get_winner()

class RandomPlayout():
    def __init__(self):
        pass
    
    @staticmethod
    def playout(board, colour):
        # Optimised random playout: get legal moves, shuffle that array and play in that order
        # Then check who won, no need to check after each move
        moves = get_legal_moves(board)
        # Shuffle in place
        shuffle(moves)
        cur_colour = colour
        current_board = np.copy(board)
        for i, move in enumerate(moves):
            current_board[move[0]][move[1]] = cur_colour
            cur_colour = get_opp_colour(cur_colour)
        return get_winner(current_board)


class MCTSAgent():
    def __init__(self):
        # Parameters
        self.simulations_count = 10
        self.time_limit_seconds = 60
        # Policies
        self.selection_policy = UCT()
        self.expansion_policy = ExpandAll()
        self.simulation_policy = RandomPlayout(self.simulations_count)

        
    def expand(self, root: MCTSNode, board: np.ndarray):
       newNodes = self.expansion_policy.generate_new_nodes(board)
       # Link the newly generated children with the parent
       root.children += newNodes
       return newNodes

    def simulate(self, node):
        for i in range(self.simulations_count):
            winner = self.simulation_policy.playout(node.colour)

    def select(self, root: MCTSNode) -> MCTSNode:
        bestValue = -float("inf")
        bestChild = None
        for child in root.children:
            value = self.selection_policy.calculate_value(child, root)
            if value > bestValue:
                bestValue = value
                bestChild = child
        return bestChild
        
    def MCTS(self, initial_board: np.ndarray, colour: str, turn_count: int):
        self.root = MCTSNode(colour)
        self.colour = colour
        self.opp_colour = get_opp_colour(colour)
        start_time = timer()
        # While not reached time limit, run MCTS 
        while (timer()  - start_time) < self.time_limit_seconds:
            node = self.root
            current_board = np.copy(initial_board)
            # Selection phase
            path = [node]
            while not node.is_leaf():
                bestChild = self.select(node)
                path.append(bestChild)
                move = bestChild.move
                current_board[move[0]][move[1]] = bestChild.colour
            # Expansion phase
            newNodes = self.expand(node, current_board, get_opp_colour(node.colour))
            # Simulation phase
            for node in newNodes:
                (rWins, bWins) = self.simulate(node)
                # Back-propogation phase