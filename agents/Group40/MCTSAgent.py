import numpy as np
import math
from timeit import default_timer as timer

class MCTSNode():
    def __init__(self, move = None):
        self.children = []
        # N = number of simulations that was played involving this node
        self.N = 0
        # Q = Sum of all backpropogated payouts
        self.Q = 0
        # Last move that was made on parent board to get to this node
        # Used to reconstruct the board without saving the entire board in the nodes
        self.move = move

    def is_leaf(self):
        return len(self.children) == 0
    
class UCT():
    def __init__(self):
        self.C = math.sqrt(2)

    def calculate_value(self, node: MCTSNode, parent: MCTSNode):
        return (node.Q / node.N) + (self.C * math.sqrt(math.log(parent.N) / node.N))
    
class MCTSAgent():
    def __init__(self):
        self.simulations_count = 10
        self.selection_policy = UCT()
        self.time_limit_seconds = 60

    def select(self, root: MCTSNode):
        bestValue = -float("inf")
        bestChild = None
        for child in root.children:
            value = self.selection_policy.calculate_value(child, root)
            if value > bestValue:
                bestValue = value
                bestChild = child
        return bestChild
        
    def MCTS(self, current_board, colour, turn_count):
        self.root = MCTSNode()
        start_time = timer()
        cur_time = timer() 
        # While not reached time limit, run MCTS 
        while (cur_time - start_time) < self.time_limit_seconds:
            node = self.root
            # Selection phase
            while not node.is_leaf():
                bestChild = self.select(node)
