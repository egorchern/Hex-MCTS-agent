package javaV;

import javaV.common.Common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class Index{
    public static String HOST = "127.0.0.1";
    public static int PORT = 1234;

    private Socket s;
    private PrintWriter out;
    private BufferedReader in;

    private char colour = 'R';
    private int turn = 0;
    private int boardSize = 11;
    public static MCTSAgent agent = new MCTSAgent();
    private final int[][] dontSwapArray = new int[][]{
            {0, 0}, {0, 1}, {0, 2}, {0, 3}, {0, 4}, {0, 5}, {0, 6}, {0, 7}, {0, 8}, {0, 9},
            {1, 5},
            {2, 10},
            {8, 0},
            {10, 1}, {10, 2}, {10, 3}, {10, 4}, {10, 5}, {10, 6}, {10, 7}, {10, 8}, {10, 9}, {10, 10}
    };
    private void initializeAgent(){
        Common.boardSize = boardSize;
        Common.initializeRefVisited();
    }
    private void Connect() throws IOException{
        s = new Socket(HOST, PORT);
        out = new PrintWriter(s.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    private String getMessage() throws IOException{
        return in.readLine();
    }

    private void sendMessage(String msg){
        out.print(msg); out.flush();
    }

    private void closeConnection() throws IOException{
        s.close();
        out.close();
        in.close();
    }

    public void run(){
        // connect to the engine
        try{
            Connect();
        } catch (UnknownHostException e){
            System.out.println("ERROR: Host not found.");
            return;
        } catch (IOException e){
            System.out.println("ERROR: Could not establish I/O.");
            return;
        }

        while (true){
            // receive messages
            try{
                String msg = getMessage();
                boolean res = interpretMessage(msg);
                if (!res) break;
            } catch (IOException e){
                System.out.println("ERROR: Could not establish I/O.");
                return;
            }
        }

        try{
            closeConnection();
        } catch (IOException e){
            System.out.println("ERROR: Connection was already closed.");
        }
    }

    private boolean interpretMessage(String s){
        turn++;

        String[] msg = s.strip().split(";");
        switch (msg[0]){
            case "START":
                boardSize = Integer.parseInt(msg[1]);


                colour = msg[2].charAt(0);
                if (colour == 'R'){
                    String board = "";
                    for (int i = 0; i < boardSize; i++){
                        String line = "";
                        for (int j = 0; j < boardSize; j++)
                            line += "0";
                        board += line;
                        if (i < boardSize - 1) board += ",";
                    }
                    makeMove(board, "-1,-1");
                }
                initializeAgent();
                break;

            case "CHANGE":
                if (msg[3].equals("END")) return false;
                if (msg[1].equals("SWAP")) colour = Common.opp_colour.get(colour);
                if (msg[3].equals("" + colour)) makeMove(msg[2], msg[1]);

                break;

            default:
                return false;
        }
        return true;
    }

    private boolean shouldSwap(String lastMove){
        String[] moves = lastMove.split(",");
        int[] move = new int[]{Integer.parseInt(moves[0]), Integer.parseInt(moves[1])};

        // If move is in dontSwapArray, return false
        for (int[] position : dontSwapArray){
            if (position[0] == move[0] && position[1] == move[1]){
                return false;
            }
        }

        return true;
    }

    public static int[] decideMove(char[][] board, char colour, int turn){
        return agent.MCTS(board, colour, turn);
    }
    private int[] decideFirstRedMove(){
        int[] move = new int[2];
        Random cur = ThreadLocalRandom.current();
        int firstChoice = cur.nextInt(2);
        int secondChoice = cur.nextInt(2);
        int xOffset = cur.nextInt(2);
        int yOffset = cur.nextInt(2);

        if(firstChoice == 0){
            // Positive x offset
            move[1] = xOffset;
        }
        else{
            move[1] = boardSize - 1 - xOffset;
        }

        if(secondChoice == 0){
            move[0] = yOffset;
        }
        else{
            move[1] = boardSize - 1 - yOffset;
        }
        return move;
    }


    public void makeMove(String board, String lastMove){
        int[] move;
        String msg;

        String[] lines = board.split(",");
        // Interpret board
        char[][] curBoard = new char[boardSize][boardSize];
        for (int idy = 0; idy < boardSize; idy++){
            for (int idx = 0; idx < boardSize; idx++){
                char cell = lines[idy].charAt(idx);
                curBoard[idy][idx] = cell;
            }
        }
        if (colour == 'R' && turn == 1){
            move = decideFirstRedMove();
            msg = move[0] + "," + move[1] + "\n";
        }
        else if (colour == 'B' && turn == 2 && shouldSwap(lastMove)){
            msg = "SWAP\n";
        }
        else{
            move = decideMove(curBoard, colour, turn);
            msg = move[0] + "," + move[1] + "\n";
        }

        // Send the move
        sendMessage(msg);
        System.gc();
    }

    public static void main(String[] args){
        // Set parameters
        if(args.length >= 1){
            MCTSAgent.timeLimitSeconds = Double.parseDouble(args[0]);
        }
        if (args.length >= 2){
            MCTSAgent.simulationsCntPerCore = Integer.parseInt(args[1]);
        }
        if(args.length >= 3){
            MCTSAgent.C = Double.parseDouble(args[2]);
        }
        Index intr = new Index();
        intr.run();
//        char[][] testBoard = new char[11][11];
//        for (int i = 0; i < 11; i++){
//            for (int j = 0; j < 11; j++){
//                testBoard[i][j] = '0';
//            }
//        }
//        Common.boardSize = 11;
//        Common.initializeRefVisited();
//        System.out.println(agent.MCTS(testBoard, 'R', 3));
    }
}