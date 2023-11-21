import numpy as np
import math
from timeit import default_timer as timer
from random import shuffle
# good article to read: https://medium.com/@quasimik/monte-carlo-tree-search-applied-to-letterpress-34f41c86e238
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
        else: 
            return "R"
        
def get_legal_moves(board: np.ndarray):
    moves = []
    for rowIndex in range(len(board)):
        curRow = board[rowIndex]
        for colIndex in range(len(curRow)):
            cell_color = curRow[colIndex]
            if cell_color == '0':
                moves.append((rowIndex, colIndex))
    return moves

def convert_board_to_string(board):
    stringBoard = ""
    for rowIndex in range(len(board)):
        separator = ","
        if rowIndex == len(board) - 1:
            separator = ""
        stringBoard += "".join(board[rowIndex]) + separator
    return stringBoard
        

def get_winner(board):
    # Use the engine's code to get the winner of a board.
    from Board import Board as engineBoard
    from Colour import Colour 
    ref_board = engineBoard.from_string(convert_board_to_string(board))
    ref_board.has_ended()
    return Colour.get_char(ref_board.get_winner())

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


class MCTSBackPropogate():
    def __init__(self):
        pass
    
    @staticmethod
    def update(node: MCTSNode, rWins, bWins):
        totalSimulaitons = rWins + bWins
        node.N += totalSimulaitons
        # Update using opposite colour. On blue nodes, add red wins. On red nodes, add blue wins
        # Because on blue colored nodes, the children are red, so we are choosing the move that results in red node that produces the best outcome for blue
        # So we add blue win on red nodes
        if node.colour == "R":
            node.Q += bWins
        else:
            node.Q += rWins

class RobustChild():
    def __init__(self):
        pass

    @staticmethod
    def get_best_child(root: MCTSNode):
        children = root.children
        bestChild = None
        bestValue = float("-inf")
        for node in children:
            value = node.N
            if value > bestValue:
                bestChild = node
                bestValue = value
        return bestChild

class MCTSAgent():
    def __init__(self):
        # Parameters
        self.simulations_count = 10
        self.time_limit_seconds = 5
        # Policies
        self.selection_policy = UCT()
        self.expansion_policy = ExpandAll()
        self.simulation_policy = RandomPlayout()
        self.back_propogation_policy = MCTSBackPropogate()
        self.root_move_selection_policy = RobustChild()
        
    def expand(self, root: MCTSNode, board: np.ndarray):
       newNodes = self.expansion_policy.generate_new_nodes(board, get_opp_colour(root.colour))
       # Link the newly generated children with the parent
       root.children += newNodes
       return newNodes
    
    def select_best_move(self, root):
        return self.root_move_selection_policy.get_best_child(root).move

    def simulate(self, node, board):
        rWins = 0
        bWins = 0
        for i in range(self.simulations_count):
            winner = self.simulation_policy.playout(board, get_opp_colour(node.colour))
            if winner == "R":
                rWins += 1
            else:
                bWins += 1
        return (rWins, bWins)

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
                node = self.select(node)
                path.append(node)
                move = node.move
                current_board[move[0]][move[1]] = get_opp_colour(node.colour)
            # Expansion phase
            newNodes = self.expand(node, current_board)
            # Simulation phase
            for expandedNode in newNodes:
                simulation_board = np.copy(current_board)
                simulation_board[expandedNode.move[0]][expandedNode.move[1]] = get_opp_colour(expandedNode.colour)
                (rWins, bWins) = self.simulate(expandedNode, simulation_board)
                # Back-propogation phase
                # Update the newly expanded node first
                self.back_propogation_policy.update(expandedNode, rWins, bWins)
                # Update all nodes on path, going from latest node (LIFO)
                for i in range(len(path) - 1, -1, -1):
                    nodeOnPath = path[i]
                    self.back_propogation_policy.update(nodeOnPath, rWins, bWins)
        # Select final move
        return self.select_best_move(self.root)


if __name__ == "__main__":
    agent = MCTSAgent()
    board_size = 11
    initial_board = np.full((board_size, board_size), '0')
    print(agent.MCTS(initial_board, "R", 0))