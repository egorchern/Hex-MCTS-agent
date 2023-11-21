import socket
from random import choice
from time import sleep
import numpy as np
from MCTSAgent import MCTSAgent

class Index():


    HOST = "127.0.0.1"
    PORT = 1234

    def __init__(self, board_size=11):
        self.s = socket.socket(
            socket.AF_INET, socket.SOCK_STREAM
        )

        self.s.connect((self.HOST, self.PORT))

        self.board_size = board_size
        self.board = []
        self.colour = ""
        self.turn_count = 0
        self.agent = MCTSAgent()

    def run(self):
        """Reads data until it receives an END message or the socket closes."""

        while True:
            data = self.s.recv(1024)
            if not data:
                break
            # print(f"{self.colour} {data.decode('utf-8')}", end="")
            if (self.interpret_data(data)):
                break

        # print(f"Naive agent {self.colour} terminated")

    def interpret_data(self, data):
        """Checks the type of message and responds accordingly. Returns True
        if the game ended, False otherwise.
        """

        messages = data.decode("utf-8").strip().split("\n")
        messages = [x.split(";") for x in messages]
        # print(messages)
        for s in messages:
            if s[0] == "START":
                self.board_size = int(s[1])
                self.colour = s[2]
                self.board = np.full((self.board_size, self.board_size), '0')

                if self.colour == "R":
                    self.make_move()

            elif s[0] == "END":
                return True

            elif s[0] == "CHANGE":
                if s[3] == "END":
                    return True

                elif s[1] == "SWAP":
                    self.colour = self.opp_colour()
                    if s[3] == self.colour:
                        self.make_move()

                elif s[3] == self.colour:
                    action = [int(x) for x in s[1].split(",")]
                    self.board[action[0]][action[1]] = self.opp_colour()

                    self.make_move()

        return False
    
    def decide_move(self):
        return self.agent.MCTS(self.board, self.colour, self.turn_count)
    
    def make_move(self):
        """Makes a random move from the available pool of choices. If it can
        swap, chooses to do so 50% of the time.
        """
        # TODO swap rule
        move = self.decide_move()
        print(move)
        self.s.sendall(bytes(f"{move[0]},{move[1]}\n", "utf-8"))
        self.board[move[0]][move[1]] = self.colour
        # # print(f"{self.colour} making move")
        # if self.colour == "B" and self.turn_count == 0:
        #     if choice([0, 1]) == 1:
        #         self.s.sendall(bytes("SWAP\n", "utf-8"))
        #     else:
        #         # same as below
        #         choices = []
        #         for i in range(self.board_size):
        #             for j in range(self.board_size):
        #                 if self.board[i][j] == 0:
        #                     choices.append((i, j))
        #         pos = choice(choices)
        #         self.s.sendall(bytes(f"{pos[0]},{pos[1]}\n", "utf-8"))
        #         self.board[pos[0]][pos[1]] = self.colour
        # else:
        #     choices = []
        #     for i in range(self.board_size):
        #         for j in range(self.board_size):
        #             if self.board[i][j] == 0:
        #                 choices.append((i, j))
        #     pos = choice(choices)

        #     self.s.sendall(bytes(f"{pos[0]},{pos[1]}\n", "utf-8"))
        #     self.board[pos[0]][pos[1]] = self.colour
        self.turn_count += 1

    def opp_colour(self):
        """Returns the char representation of the colour opposite to the
        current one.
        """
        if self.colour == "R":
            return "B"
        elif self.colour == "B":
            return "R"
        else:
            return "None"


if (__name__ == "__main__"):
    agent = Index()
    agent.run()
