package com.example.sosgui;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.lang.Math;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Random;

import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors

import static com.example.sosgui.EditableButton.isUser1Turn;

public class GameLogic {

    // Holds all buttons in an array to test game win
    static EditableButton[][] buttonArray = new EditableButton[10][10];

    static int[][] blueSOSArr;
    static int[][] redSOSArr;

    static int blueScore = 0;
    static int redScore = 0;

    static boolean computer1;
    static boolean computer2;

    public static boolean recordGame(String move){
        try {
            Files.write(Paths.get("gameOutput.txt"), move.getBytes(), StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return false;
        }
    }

    // user1 and user2 will keep track if the selected player is a computer
    public static boolean computerMakeMove(boolean user1, boolean user2){
        int compRow = (int) (Math.random() * finBoardSize);
        int compCol = (int) (Math.random() * finBoardSize);
        String compMove = "S";

        computer1 = user1;
        computer2 = user2;

        if(user1 && user2){
            // Will make both computers go over the whole board
            for(int i = 0; i < finBoardSize * finBoardSize; i++){
                // If the computer makes an illegal move
                while(buttonArray[compRow][compCol].getText().equals("S") || buttonArray[compRow][compCol].getText().equals("O")){
                    compRow = (int) (Math.random() * finBoardSize);
                    compCol = (int) (Math.random() * finBoardSize);
                }

                // Will determine the computers selection
                if(Math.random() >= 0.5){
                    if(isUser1Turn){
                        RadioButtons.setUser1Move("S");
                    } else {
                        RadioButtons.setUser2Move("S");
                    }
                } else {
                    if(isUser1Turn){
                        RadioButtons.setUser1Move("O");
                    } else {
                        RadioButtons.setUser2Move("O");
                    }
                }

                // Will make the computers move
                makeUserMove(null, compRow, compCol, true);
            }
            // if only user 1 is a computer
        } else if(!user2){
            // If the computer makes an illegal move
            while(buttonArray[compRow][compCol].getText().equals("S") || buttonArray[compRow][compCol].getText().equals("O")){
                compRow = (int) (Math.random() * finBoardSize);
                compCol = (int) (Math.random() * finBoardSize);
            }

            // Will determine the computers selection
            if(Math.random() >= 0.5){
                RadioButtons.setUser1Move("S");
            } else {
                RadioButtons.setUser1Move("O");
            }
            // Will make the computers move
            makeUserMove(null, compRow, compCol, true);
            // if only user 2 is computer
        }
        return true;
    }

    public static void makeUserMove(Text currTurn, int row, int col, boolean isCompMove){
        // Guard clause to make sure that the move the user is attempting is a valid move
        if(!Objects.equals(buttonArray[row][col].getText(), "")){
            System.out.println("Invalid Move");
            return;
        }

        // Will put the players S or O in the spot they chose
            if(isUser1Turn){

                // This stuff is correct

                buttonArray[row][col].setText(RadioButtons.getUser1Move());
                System.out.println("Blue placed an " + RadioButtons.getUser1Move() + " at " + row + "," + col);
                recordGame("Blue placed an " + RadioButtons.getUser1Move() + " at " + row + "," + col + "\n");

                if(currTurn != null)
                    currTurn.setText("Red Player's Turn");
            } else{
                buttonArray[row][col].setText(RadioButtons.getUser2Move());
                System.out.println("Red placed an " + RadioButtons.getUser2Move() + " at " + row + "," + col);
                recordGame("Red placed an " + RadioButtons.getUser2Move() + " at " + row + "," + col + "\n");

                if(currTurn != null)
                    currTurn.setText("Blue Player's Turn");
            }

        if(SOSGui.gameselTG.getSelectedToggle().toString().contains("Simple Game")){
            checkWin(isUser1Turn, true);
        } else {
            checkWin(isUser1Turn, false);
        }

        isUser1Turn = !isUser1Turn;
    }

    // final board size in terms of rows or columns
    static int finBoardSize = 0;

    public static boolean CreateBoard(int boardSize, Text currTurnText, GridPane gameGrid){

        if(boardSize < 3){
            return false;
        } else if(boardSize > 10){
            return false;
        } else {

            finBoardSize = boardSize;

            // Initializes an array of 0s for each player to keep track of their SOS points
            blueSOSArr = new int[boardSize][boardSize];
            redSOSArr = new int[boardSize][boardSize];

            redScore = 0;
            blueScore = 0;

            // Keeps track of the number of buttons in the game
            int count = 0;
            // Will place buttons in each row and column for the user defined board size
            for (int row = 0; row < boardSize; row++) {
                for (int column = 0; column < boardSize; column++) {
                    EditableButton btn = new EditableButton(currTurnText);
                    // Array to keep track of all buttons
                    buttonArray[row][column] = btn;
                    count++;
                    // sets the size of each button so that it fills the gameGrid
                    btn.setPrefSize(500, 500);
                    // Adds a new button at column, row
                    gameGrid.add(btn, column, row);
                }
            }
            return true;
        }
    }

    static boolean checkDraw(boolean isSimpleGame){
        int turnNum = 0;

        for(int row = 0; row < finBoardSize; row++){
            for(int col = 0; col < finBoardSize; col++){
                if(buttonArray[row][col].getText().equals("S") || buttonArray[row][col].getText().equals("O")){
                    turnNum++;
                }
                // General game is over
                if(turnNum == finBoardSize * finBoardSize){
                    if(blueScore == redScore){
                        if(isSimpleGame)
                            recordGame("Draw simple game with a score of "  + blueScore + ":" + redScore + "\n");
                        else
                            recordGame("Draw general game with a score of "  + blueScore + ":" + redScore + "\n");
                        System.out.println("Draw Game with a score of "  + blueScore + ":" + redScore);
                    } else if(blueScore > redScore){
                        if(isSimpleGame)
                            recordGame("Blue player has won a simple game with a score of " + blueScore + ":" + redScore + "\n");
                        else
                            recordGame("Blue player has won a general game with a score of " + blueScore + ":" + redScore + "\n");
                        System.out.println("Blue player has won with a score of " + blueScore + ":" + redScore);

                    } else {
                        if(isSimpleGame)
                            recordGame("Red player has won a simple game with a score of " + redScore + ":" + blueScore + "\n");
                        else
                            recordGame("Red player has won a general game with a score of " + redScore + ":" + blueScore + "\n");
                        System.out.println("Red player has won with a score of " + redScore + ":" + blueScore);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    // Will check if the user won
    // param isP1Turn: the player who just made a move, true = p1, false = p2

    public static boolean checkWin(boolean isP1Turn, boolean isSimpleGame){
        // In order to check for a win on a variable board size, we need an abstract method that will work for any size

        // Checks for a horizontal win
        for(int row = 0; row < finBoardSize; row++){
            for(int col = 0; col < finBoardSize - 2; col++) {
                // checks each button in the row
                if(buttonArray[row][col].getText().equals("S") && buttonArray[row][col + 1].getText().equals("O") && buttonArray[row][col + 2].getText().equals("S")){                    // Checks if the SOS is already accounted for
                    if(blueSOSArr[row][col] == 1 && blueSOSArr[row][col + 1] == 1 && blueSOSArr[row][col + 2] == 1){
                    } else if(redSOSArr[row][col] == 1 && redSOSArr[row][col + 1] == 1 && redSOSArr[row][col + 2] == 1){
                } else {
                    if(isP1Turn) {
                        buttonArray[row][col].setTextFill(Color.rgb(0, 0, 255));
                        buttonArray[row][col + 1].setTextFill(Color.rgb(0, 0, 255));
                        buttonArray[row][col + 2].setTextFill(Color.rgb(0, 0, 255));
                        blueSOSArr[row][col] = 1;
                        blueSOSArr[row][col + 1] = 1;
                        blueSOSArr[row][col + 2] = 1;
                        blueScore++;
                    } else {
                        buttonArray[row][col].setTextFill(Color.rgb(255, 0, 0));
                        buttonArray[row][col + 1].setTextFill(Color.rgb(255, 0, 0));
                        buttonArray[row][col + 2].setTextFill(Color.rgb(255, 0, 0));
                        redSOSArr[row][col] = 1;
                        redSOSArr[row][col + 1] = 1;
                        redSOSArr[row][col + 1] = 1;
                        redScore++;
                    }
                }
                    if(isSimpleGame){
                        if(isUser1Turn){
                            System.out.println("Blue Player has won");
                            return true;
                        } else {
                            System.out.println("Red Player has won");
                            return false;
                        }
                    }
            }
        }
    }
        // Checks for a vertical win
        for(int row = 0; row < finBoardSize - 2; row++){
            for(int col = 0; col < finBoardSize; col++) {
                if(buttonArray[row][col].getText().equals("S") && buttonArray[row + 1][col].getText().equals("O") && buttonArray[row + 2][col].getText().equals("S")){

                    // Will check if the SOS is already accounted for
                    if(blueSOSArr[row][col] == 1 && blueSOSArr[row + 1][col] == 1 && blueSOSArr[row + 2][col] == 1){

                    } else if(redSOSArr[row][col] == 1 && redSOSArr[row + 1][col] == 1 && redSOSArr[row + 2][col] == 1){

                    } else {

                        if(isP1Turn) {
                            buttonArray[row][col].setTextFill(Color.rgb(0, 0, 255));
                            buttonArray[row + 1][col].setTextFill(Color.rgb(0, 0, 255));
                            buttonArray[row + 2][col].setTextFill(Color.rgb(0, 0, 255));
                            blueSOSArr[row][col] = 1;
                            blueSOSArr[row + 1][col] = 1;
                            blueSOSArr[row + 2][col] = 1;
                            blueScore++;
                        } else {
                            buttonArray[row][col].setTextFill(Color.rgb(255, 0, 0));
                            buttonArray[row + 1][col].setTextFill(Color.rgb(255, 0, 0));
                            buttonArray[row + 2][col].setTextFill(Color.rgb(255, 0, 0));
                            redSOSArr[row][col] = 1;
                            redSOSArr[row + 1][col] = 1;
                            redSOSArr[row + 2][col] = 1;
                            redScore++;
                        }
                    }
                    if(isSimpleGame){
                        if(isUser1Turn){
                            System.out.println("Blue Player has won");
                            return true;
                        } else {
                            System.out.println("Red Player has won");
                            return false;
                        }
                    }
                }
            }
        }

        // Checks for a diagonal win
        for(int row = 0; row < finBoardSize - 2; row++){
            for(int col = 0; col < finBoardSize - 2; col++) {
                if(buttonArray[row][col].getText().equals("S") && buttonArray[row + 1][col + 1].getText().equals("O") && buttonArray[row + 2][col + 2].getText().equals("S")){

                    // Will check if the SOS is already accounted for
                    if(blueSOSArr[row][col] == 1 && blueSOSArr[row + 1][col + 1] == 1 && blueSOSArr[row + 2][col + 2] == 1){

                    } else if(redSOSArr[row][col] == 1 && redSOSArr[row + 1][col + 1] == 1 && redSOSArr[row + 2][col + 2] == 1){

                    } else {
                        if(isP1Turn) {
                            buttonArray[row][col].setTextFill(Color.rgb(0, 0, 255));
                            buttonArray[row + 1][col + 1].setTextFill(Color.rgb(0, 0, 255));
                            buttonArray[row + 2][col + 2].setTextFill(Color.rgb(0, 0, 255));
                            blueSOSArr[row][col] = 1;
                            blueSOSArr[row + 1][col + 1] = 1;
                            blueSOSArr[row + 2][col + 2] = 1;
                            blueScore++;
                        } else {
                            buttonArray[row][col].setTextFill(Color.rgb(255, 0, 0));
                            buttonArray[row + 1][col + 1].setTextFill(Color.rgb(255, 0, 0));
                            buttonArray[row + 2][col + 2].setTextFill(Color.rgb(255, 0, 0));
                            redSOSArr[row][col] = 1;
                            redSOSArr[row + 1][col + 1] = 1;
                            redSOSArr[row + 2][col + 2] = 1;
                            redScore++;
                        }
                    }
                    if(isSimpleGame){
                        if(isUser1Turn){
                            System.out.println("Blue Player has won");
                            return true;
                        } else {
                            System.out.println("Red Player has won");
                            return false;
                        }
                    }
                }
            }
        }

        // Will set the shared SOS's to a purple color
        for(int row = 0; row < finBoardSize; row++){
            for(int col = 0; col < finBoardSize; col++) {
                if(blueSOSArr[row][col] == 1 && redSOSArr[row][col] == 1){
                    buttonArray[row][col].setTextFill(Color.rgb(196,0,255));
                }
            }
        }

        checkDraw(isSimpleGame);

        return true;
    }

}
