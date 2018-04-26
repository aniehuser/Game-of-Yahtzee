package gameRunner;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

public class GameScreen {

    //Display fields:
    private Text playerText; //Display's whose turn it currently is
    private Text currentHandScore; //Display's the hand's current score
    private GridPane playerDiceButtons; //container for toggleButtons that allow user to select which dice they want to keep or reroll

    private Button roll; //click and roll selected dice
    private Button keepHand; //keeps the entire hand
    private Button quit; //exit's the game
    private Button titleScreen; //returns to titlescreen

    private Player currentPlayer; //player whose turn it is
    private int currentPlayerTracker; //tracks what players have played and who is up next
    private int maxPlayers; //how many players are playing
    private ArrayList<Player> players; //array containing all players

    private StackPane root = new StackPane();
    private static final Game game = new Game(); //container for game logic

    /**
     * @params stage currently being used and an array of player names
     * initializes and starts the logic of the actual game being played
     */
    public void start(Stage primaryStage, ArrayList<String> names) {
        primaryStage.setTitle("Game Of Yahtzee");

        //initialize fields
        currentHandScore = new Text();
        playerText = new Text(names.get(0) + "'s turn");
        playerDiceButtons = new GridPane();

        //initialize player variables and logic
        game.start();
        //if problem starting game, ends program
        if (!game.isValidInstance()) {
            System.exit(0);
        }
        currentPlayerTracker = 0;
        maxPlayers = names.size() - 1;

        //create our player's container
        players = new ArrayList<>();
        for (String string : names) {
            players.add(new Player(game.getDieSides(), game.getDieNum(), game.getRollsPerRound(), string));
        }

        //intialize the current player
        currentPlayer = players.get(currentPlayerTracker);
        currentPlayer.rollInit();

        //creates our quit button which exits the game
        quit = new Button("Quit");
        quit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        //creates our titlescreen button which returns to the title screen
        titleScreen = new Button("Title Screen");
        titleScreen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TitleScreen titleScreen = new TitleScreen();
                titleScreen.start(primaryStage);
            }
        });

        //creates our roll button which rolls the selected dice
        roll = new Button("Roll");
        roll.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (currentPlayer != null) {
                    playerRoll();
                }
            }
        });

        //create sour keephand button which keeps the whole hand
        keepHand = new Button("Keep");
        keepHand.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (currentPlayer != null) {
                    boolean[] choice = new boolean[game.getDieNum()];
                    for (int i = 0; i < choice.length; i++) {
                        choice[i] = true;
                    }
                    currentPlayer.rollOnce(choice);
                    if (currentPlayer.isRoundOver()){
                        turnOver();
                    }
                }
            }
        });

        //TODO: DELETE THIS
        //debugging stuff
        System.out.println(names);
        System.out.println(names.size());
        System.out.println(playerText.getText());

        //TODO: DELETE THIS
        //add css file later for this stuff to clean up code
        quit.setTranslateX(200);
        titleScreen.setTranslateX(-200);
        roll.setTranslateX(350);
        keepHand.setTranslateX(-350);
        playerText.setStyle("-fx-text-fill: black; -fx-font-size: 16;");

        //grab the initial player's hand and instantiate our toggleButtons that control the die selection process
        Die[] playerHand = currentPlayer.getDie();
        for (int i = 0; i < game.getDieNum(); i++) {
            playerDiceButtons.add(new ToggleButton(playerHand[i].toString()), i, 0, 1, currentPlayer.getHand().getRolls().length);
        }

        //create our UI
        root.getChildren().addAll(playerText, quit, titleScreen, playerDiceButtons, roll, keepHand, currentHandScore);
        primaryStage.setScene(new Scene(root, 1100, 1000, Color.BLACK));
        primaryStage.show();

        //start our game loop
        gameDisplayController();
    }

    /**
     * initailizes the views for a player
     */
    private void gameDisplayController() {
        resetToggleButtons();
        int i = 0;
        //display currentPlayer
        playerText.setText(currentPlayer.getName() + "'s turn");

        //TODO: Change to images instead of text
        //set text on toggle buttons to reflect the current dice value
        Die[] playerHand = currentPlayer.getDie();
        for (Node node : playerDiceButtons.getChildren()) {
            ToggleButton toggleButton = (ToggleButton) node;
            toggleButton.setText(playerHand[i].toString());
            i++;
        }
    }
    /**
     * Simulates a player rolling their hand
     */
    private void playerRoll() {
        //tracks the die we are currently at
        int i = 0;

        //checks to see what toggelbuttons are selected
        //selected togglebuttons are die we do not want to keep so that indice is set to false
        boolean[] choice = new boolean[game.getDieNum()];
        for (Node node : playerDiceButtons.getChildren()) {
            ToggleButton playerButton = (ToggleButton) node;
            if (playerButton.isSelected()) {
                choice[i] = false;
            } else {
                choice[i] = true;
            }
            i++;
        }
        currentPlayer.rollOnce(choice);
        if (currentPlayer.isRoundOver()) {
            turnOver();
        } else {
            gameDisplayController();
        }
    }

    /**
     * when a player's turn is over the preceding logic is handled in this method
     * there are multiple avenues of logic that could occur here see comments
     */
    private void turnOver() {
        //TODO: This method needs a lot of work
        // to print out players current score, call the player objects toString() method
        System.out.println(currentPlayer.toString());

//                for (Node button: playerDiceButtons.getChildren()) {

//                    ToggleButton playerButton = (ToggleButton) button;
//                    playerButton.setDisable(true);
//                }
        currentHandScore.setText(currentPlayer.getScorer().toString());
        // first check to make sure the player hasnt already assigned the score to their scorecard.
        // this step is important because due to our special rules, i will not be doing checks in
        // setScore(key) to make sure that an already assigned score
//                if(p.isScoreSet(keepScore)){
//                    // set the player's score by inputting the string key of what the user chose
//                    p.setScore(keepScore);
//                }
        currentPlayerTracker++;
        game.incrementRound();
        if (game.isGameOver()) {
            //TODO: move to victory screen instead of ending game
            // close globals
            game.end();
            System.exit(0);
        }
        if (currentPlayerTracker > maxPlayers) {
            //start of new round
            currentPlayerTracker = 0;
        }
        currentPlayer = players.get(currentPlayerTracker);
        game.incrementRound();
        currentPlayer.rollInit();
        gameDisplayController();
    }

    /**
     * Clears toggle buttons so users can make a new choice on what die to keep
     * use this method in between rolls
     */
    private void resetToggleButtons() {
        for (Node node : playerDiceButtons.getChildren()) {
            ToggleButton playerButton = (ToggleButton) node;
            if (playerButton.isSelected()) {
                playerButton.setSelected(false);
            }
        }
    }
}
