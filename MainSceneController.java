import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;

public class MainSceneController {

    File wordList;
    File validList;
    Scanner fileRead1;
    Scanner fileRead2;
    ArrayList<String> answerWords = new ArrayList<String>();
    ArrayList<String> validWords = new ArrayList<String>();

    // Main window
    @FXML private AnchorPane background;

    // In-game text
    @FXML private TextField currentWord;
    @FXML private Text gameEndText;
    @FXML private Text outcomeText;
    @FXML private Text statText;    // Not to be confused with stats screen text, this text either displays your guess count or the correct word depending on a win/loss
    @FXML private Label guessFail;  // Text that tells the player that the word they guessed is not recognized

    // Stats screen text
    @FXML private Text gamesPlayed;
    @FXML private Text gamesWonTotal;
    @FXML private Text guess1;
    @FXML private Text guess2;
    @FXML private Text guess3;
    @FXML private Text guess4;
    @FXML private Text guess5;
    @FXML private Text guess6;
    @FXML private Text winPercent;
    @FXML private Text winStreak;

    // Controllers, shapes, and containers
    @FXML private Button settingsButton;
    @FXML private Button submitButton;
    @FXML private Button statBackButton;
    @FXML private Rectangle statBG;
    @FXML private AnchorPane playButtonBackground;
    @FXML private GridPane labelGrid;
    @FXML private Pane settingsOverlay;
    @FXML private Pane gameEndOverlay;
    @FXML private Pane statsOverlay;

    // Initialize the 2D shadow data array
    private char[][] guessArray = new char[6][5];

    private int guess = 0;      // Current guess number for this game
    private int gameNum = 0;    // How many times you've completed a game (either successfully for failed).
    private int[] gamesWon = {0, 0, 0, 0, 0, 0, 0};    // Array that tracks how many guesses it has taken for each win. The last value tracks how many losses you've gotten.
    private int streak = 0;

    private String currWord;    // Not to be confused with the currentWord TextField! This is the current word you're trying to guess

    ArrayList<Character> answerLetters = new ArrayList<Character>(); // Helper object for tracking your guesses

    private boolean canStartTyping = false;
    private boolean canSubmit = true;

// ------------------------------------------ HELPERS ------------------------------------------

    void fileInit() {
        // Give the 2D shadow data array default values
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                guessArray[i][j] = ' ';
            }
        }

        try {
            wordList = new File("src\\answerlist.txt");
            fileRead1 = new Scanner(wordList);
        } catch (FileNotFoundException e) {
            System.out.println("No answer list found!");
            e.printStackTrace();
        }

        try {
            validList = new File("src\\valid-wordle-words.txt");
            fileRead2 = new Scanner(validList);
        } catch (FileNotFoundException e) {
            System.out.println("No valid word list found!");
            e.printStackTrace();
        }

        while (fileRead1.hasNextLine()) {
            answerWords.add(fileRead1.nextLine());
        }
        while (fileRead2.hasNextLine()) {
            validWords.add(fileRead2.nextLine().toUpperCase());
        }
        // Randomize the word bank
        Collections.shuffle(answerWords);

        // So basically every word you get right the game will move on the next word in the shuffled array. Force uppercase on all words
        currWord = answerWords.get(gameNum % answerWords.size()).toUpperCase();

        // DEBUG, comment out when not testing
        // currWord = "MAMMA";

        for (int i = 0; i < 5; i++) {   // We're assuming that the word we've submitted must be 5 letters long
                answerLetters.add(currWord.charAt(i));
        }
        
    }

    void guessUpdate() {
        // If the previous action was guessing an unrecognized word, clear the associated text
        guessFail.setVisible(false);
        // Update the guess array
        for (int i = 0; i < 5; i++) {
            try {
                guessArray[guess][i] = currentWord.getText().charAt(i);
            } catch(Exception e) {
                guessArray[guess][i] = ' ';
            }
        }

        // Account for all guesses when transferring array data
        for (Node label : labelGrid.getChildren()) {
            int row = GridPane.getRowIndex(label) == null ? 0 : GridPane.getRowIndex(label);
            int col = GridPane.getColumnIndex(label) == null ? 0 : GridPane.getColumnIndex(label);
            if (row == guess) {
                switch (col) {
                    case 0:
                        ((Label) label).setText(guessArray[row][0] + "");
                        break;
                    case 1:
                        ((Label) label).setText(guessArray[row][1] + "");
                        break;
                    case 2:
                        ((Label) label).setText(guessArray[row][2] + "");
                        break;
                    case 3:
                        ((Label) label).setText(guessArray[row][3] + "");
                        break;
                    case 4:
                        ((Label) label).setText(guessArray[row][4] + "");
                        break;
                
                    default:
                        break;
                }
            }
        }
    }

    void addLetter(String word, char letter) {
        if (!Character.isLetter(letter)) {
            // Do nothing if the input is not a letter
        } else { // Otherwise...
            letter = Character.toUpperCase(letter);
            String newWord = word + letter;

            if (newWord.length() > 5) {
                newWord = word;
            }
            currentWord.setText(newWord);

            guessUpdate();
        }
    }

    void prepStats() {
        gamesPlayed.setText(gameNum + "");

        int total = 0;
        for (int i = 0; i < 6; i++) {
            total += gamesWon[i];
        }
        gamesWonTotal.setText(total + "");

        guess1.setText(gamesWon[0] + "");
        guess2.setText(gamesWon[1] + "");
        guess3.setText(gamesWon[2] + "");
        guess4.setText(gamesWon[3] + "");
        guess5.setText(gamesWon[4] + "");
        guess6.setText(gamesWon[5] + "");

        if (gameNum == 0) {
            winPercent.setText("0");
        } else {
            winPercent.setText((Math.round((total * 1.0) / gameNum * 10000) / 100) + "");
        }

        winStreak.setText(streak + "");
        
    }

    @FXML
    void resetRound() {
        // Reset guess counter
        guess = 0;
        // Clear the current typed word
        currentWord.setText("");
        // New word
        currWord = answerWords.get(gameNum % answerWords.size()).toUpperCase();
        // Clear the 2D guess array
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                guessArray[i][j] = ' ';
            }
        }

        // Reset colors
        for (Node label : labelGrid.getChildren()) {
            ((Label) label).setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            ((Label) label).setTextFill(Color.rgb(86, 86, 86));
            ((Label) label).setText(" ");
        }

        // Reset the on-screen keys of nonexistent letters
        for (Node keyButton : ((AnchorPane) (labelGrid.getParent())).getChildren()) {
            if (keyButton instanceof Button) {
                ((Button) keyButton).setTextFill(Color.rgb(86, 86, 86)); // Gray out the text
            }
        }

        // Reset helper object
        for (int i = 0; i < 5; i++) {
            answerLetters.set(i, currWord.charAt(i));
        }

        settingsOverlay.setVisible(false);
        gameEndOverlay.setVisible(false);
        canSubmit = true;
        canStartTyping = true;
        System.out.println("\n" + currWord);
        
    }

// ------------------------------------------ EVENT HANDLERS ------------------------------------------

    @FXML
    void onPlay(ActionEvent event) {
        fileInit();
        playButtonBackground.setVisible(false);
        canStartTyping = true;
        System.out.println("\n" + currWord); // DEBUG
    }

    @FXML
    void buttonEnterHover(MouseEvent event) {
        ((Button) event.getSource()).setScaleX(1.05);
        ((Button) event.getSource()).setScaleY(1.05);
        ((Button) event.getSource()).setScaleZ(1.05);
    }
    @FXML
    void buttonExitHover(MouseEvent event) {
        ((Button) event.getSource()).setScaleX(1);
        ((Button) event.getSource()).setScaleY(1);
        ((Button) event.getSource()).setScaleZ(1);
    }

    @FXML
    void onKeyPress(KeyEvent event) {
        if (!canStartTyping) {
            return;
        }

        String word = currentWord.getText();
        // In the case of a backspace:
        if (event.getCode() == KeyCode.BACK_SPACE) {
            if (word.length() == 0) {
                word = "";
            } else {
                word = word.substring(0, word.length() - 1);
            }
            currentWord.setText(word);

            guessUpdate();
        }
    }

    @FXML
    void onKeyType(KeyEvent event) {
        if (!canStartTyping) {
            return;
        }

        String word = currentWord.getText();
        char letter = event.getCharacter().charAt(0);

        addLetter(word, letter);
    }

    @FXML
    void onScreenKey(ActionEvent event) {
        if (!canStartTyping) {
            return;
        }

        String word = currentWord.getText();
        char letter = ((Button) event.getSource()).getText().charAt(0);

        addLetter(word, letter);
    }

    @FXML
    void onScreenBackspace(ActionEvent event) {
        if (!canStartTyping) {
            return;
        }

        String word = currentWord.getText();
        if (word.length() == 0) {
            word = "";
        } else {
            word = word.substring(0, word.length() - 1);
        }
        currentWord.setText(word);

        guessUpdate();
    }

    @FXML
    void onSettingsClicked(ActionEvent event) {
        canStartTyping = false;
        canSubmit = false;
        settingsOverlay.setVisible(true);
    }

    @FXML
    void settingsBack(ActionEvent event) {
        canStartTyping = true;
        canSubmit = true;
        settingsOverlay.setVisible(false);
    }

    @FXML
    void resetButton(ActionEvent event) {
        // Increment round count because of reset
        gameNum++;
        resetRound();
    }

    @FXML
    void statViewSettings(ActionEvent event) {
        prepStats();
        statBG.setVisible(false);
        statsOverlay.setVisible(true);
        
    }

    @FXML
    void statViewResults(ActionEvent event) {
        prepStats();
        statBG.setVisible(true);
        statsOverlay.setVisible(true);
    }

    @FXML
    void statBack(ActionEvent event) {
        statsOverlay.setVisible(false);
    }

    @FXML
    void saveProgress(ActionEvent event) {
        // Try creating a new file
        try {
            File saveData = new File("saveData.txt");
            FileWriter saveWrite = new FileWriter(saveData);
                
                // Line 1, number of games played. Defaults to 0.
                saveWrite.write(gameNum + "\n");
                // Lines 2-7, number of games won in one through six tries
                for (int i = 0; i < 6; i++) {
                    saveWrite.write(gamesWon[i] + "\n");
                }
                // Line 8, number of games lost
                saveWrite.write(gamesWon[6] + "\n");

                // Line 9, current guess number
                saveWrite.write(guess + "\n");

                // Line 10, current win streak
                saveWrite.write(streak + "\n");

                // Label grid parser so we can save the state of the game. Oh boy.
                for (Node label : labelGrid.getChildren()) {
                    // Yellow background's toString: 0xffd700ff
                    // Green background's toString: 0x2e8b57ff
                    // Dark background's toString: 0x8c8c8cff
                    // Gray background's toString: 0xd3d3d3ff

                    // So basically get the string format of the paint of the background fill of the background of the label in the grid
                    switch (((Label) label).getBackground().getFills().get(0).getFill().toString()) {
                        case "0xd3d3d3ff":  // Gray BG case
                            saveWrite.write(0 + ((Label) label).getText() + "\n");
                            break;
                        case "0x8c8c8cff":  // Dark BG case
                            saveWrite.write(1 + ((Label) label).getText() + "\n");
                            break;
                        case "0xffd700ff":  // Yellow BG case
                            saveWrite.write(2 + ((Label) label).getText() + "\n");
                            break;
                        case "0x2e8b57ff":  // Green BG case
                            saveWrite.write(3 + ((Label) label).getText() + "\n");
                            break;
                    
                        default:
                            break;
                    }
                }

                // Save current word
                saveWrite.write(currWord + "\n");
                // Save current guess
                saveWrite.write(currentWord.getText());
                
                saveWrite.close();
        } catch (IOException e) {
            System.out.println("Oops. There was an error with saving your progress.");
        }
    }

    @FXML
    void loadProgress(ActionEvent event) {
        // Try reading a save file
        try {
            File saveData = new File("saveData.txt");
            Scanner saveRead = new Scanner(saveData);
            gameNum = Integer.valueOf(saveRead.nextLine());

            // Load won/lost game stats
            for (int i = 0; i < 7; i++) {
                gamesWon[i] = Integer.valueOf(saveRead.nextLine());
            }

            // Load guess
            guess = Integer.valueOf(saveRead.nextLine());

            // Load win streak
            streak = Integer.valueOf(saveRead.nextLine());

            // Load the strings of the next 30 lines.
            for (Node label : labelGrid.getChildren()) {
                int row = GridPane.getRowIndex(label) == null ? 0 : GridPane.getRowIndex(label);
                int col = GridPane.getColumnIndex(label) == null ? 0 : GridPane.getColumnIndex(label);

                String parse = saveRead.nextLine();
                // So basically get the string format of the paint of the background fill of the background of the label in the grid
                switch (parse.charAt(0) - '0') {
                    case 0:  // Gray BG case
                        ((Label) label).setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
                        ((Label) label).setTextFill(Color.rgb(86, 86, 86));
                        break;
                    case 1:  // Dark BG case
                        ((Label) label).setBackground(new Background(new BackgroundFill(Color.rgb(140, 140, 140), CornerRadii.EMPTY, Insets.EMPTY)));
                        ((Label) label).setTextFill(Color.rgb(180, 180, 180));
                        
                        break;
                    case 2:  // Yellow BG case
                        ((Label) label).setBackground(new Background(new BackgroundFill(Color.GOLD, CornerRadii.EMPTY, Insets.EMPTY)));
                        ((Label) label).setTextFill(Color.WHITE);
                        break;
                    case 3:  // Green BG case
                        ((Label) label).setBackground(new Background(new BackgroundFill(Color.SEAGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                        ((Label) label).setTextFill(Color.WHITE);
                        break;
                
                    default:
                        break;
                }
                ((Label) label).setText(parse.charAt(1) + "");
                guessArray[row][col] = parse.charAt(1);
            }

            // Load the saved word and guess
            currWord = saveRead.nextLine();
            if (saveRead.hasNextLine()) {
                currentWord.setText(saveRead.nextLine());
            } else {
                currentWord.setText("");
            }
            

            // Place the loaded word in the answerLetters array
            for (int i = 0; i < 5; i++) {
                answerLetters.set(i, currWord.charAt(i));
            }

            // Gray out onscreen keyboard buttons as needed
            for (Node keyButton : ((AnchorPane) (labelGrid.getParent())).getChildren()) {
                if (keyButton instanceof Button) {
                    for (int row = 0; row < 6; row++) {
                        for (int col = 0; col < 5; col++) {
                            if (((Button) keyButton).getText().compareTo(guessArray[row][col] + "") == 0   // Manually calculate the position of the desired letter to gray out, see if it's equal to a button.
                                && !answerLetters.contains(guessArray[row][col])) {   // If the manually calculated position's label's letter does not exist in the answer character array
                            ((Button) keyButton).setTextFill(Color.rgb(180, 180, 180)); // Gray out the text
                            }
                        }
                    }
                    
                }
            }

            // Reshuffle the answer bank to prevent cheating
            Collections.shuffle(answerWords);

            saveRead.close();
        } catch (FileNotFoundException e) {
            System.out.println("No save data found. Save your progress to create save data!");
        }
    }

    @FXML
    void clearProgress(ActionEvent event) {
        // This only resets your statistics, not your in-game state.
        gameNum = 0;
        for (int i = 0; i < 7; i++) {
            gamesWon[i] = 0;
        }
        streak = 0;
    }




    // ------------------------------------------------------------------------------
    // The submission function is its own beast. Just gonna visually separate it here.
    // ------------------------------------------------------------------------------





    @FXML
    void submit(ActionEvent event) {

        // Check if submitting should be done
        if (!canSubmit) {
            return;
        }

        // Check for a full length word, if not don't do anything
        if (currentWord.getText().length() >= 5) {
            // Check that the word is part of the submitable words list
            if (!validWords.contains(currentWord.getText())) {
                guessFail.setVisible(true);
                return;
            }
            canStartTyping = false;

            // Helper lists for determining green/yellow letters
            ArrayList<Character> guessLetters = new ArrayList<Character>();
            ArrayList<Character> answerLetters2 = new ArrayList<Character>();


            for (int i = 0; i < 5; i++) {   // We're assuming that the word we've submitted must be 5 letters long
                guessLetters.add(currentWord.getText().charAt(i));
                answerLetters2.add(currWord.charAt(i));
            }

            // First check for matching (green) letters.
            for (Node label : labelGrid.getChildren()) {
                int row = GridPane.getRowIndex(label) == null ? 0 : GridPane.getRowIndex(label);
                int col = GridPane.getColumnIndex(label) == null ? 0 : GridPane.getColumnIndex(label);
                if (label == null) {
                    continue;
                }
                if (row == guess && ((Label) label).getText().charAt(0) == answerLetters.get(col)) {  // We have row == guess as a condition because we don't want to overwrite previous guesses
                    ((Label) label).setBackground(new Background(new BackgroundFill(Color.SEAGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                    ((Label) label).setTextFill(Color.WHITE);
                    answerLetters2.remove((Object) (((Label) label).getText().charAt(0)));
                }
            }

            // Then check for a game win
            if (answerLetters2.isEmpty()) {
                gameEndText.setText("You Win!");
                outcomeText.setText("You guessed the word in");
                if (guess + 1 == 1) {
                    statText.setText("1 attempt");
                } else {
                    statText.setText((guess + 1) + " attempts");
                }

                // Track how many games for each guess amount you've won
                gamesWon[guess]++;

                // Increment round number
                gameNum++;

                // Increment the win streak
                streak++;
                
                gameEndOverlay.setVisible(true);
                return;
            }

            Collections.sort(answerLetters2);

            // Otherwise, check for existing (yellow) letters
            for (Node label : labelGrid.getChildren()) {
                int row = GridPane.getRowIndex(label) == null ? 0 : GridPane.getRowIndex(label);
                int col = GridPane.getColumnIndex(label) == null ? 0 : GridPane.getColumnIndex(label);
                if (label == null) {
                    continue;
                }
                
                if (row == guess) {  // We have row == guess as a condition because we don't want to overwrite previous guesses
                    for (int i = 0; i < answerLetters2.size(); i++) {
                        if (((Label) label).getText().charAt(0) != answerLetters.get(col) // If the letter in the label is the same as the answer's letter in the same column (prevents overwriting green tiles)
                                && ((Label) label).getText().charAt(0) == answerLetters2.get(i)) {  // If the letter in the label exists in the sorted list of letters in the answer
                            
                            // Yellow tile colors
                            ((Label) label).setBackground(new Background(new BackgroundFill(Color.GOLD, CornerRadii.EMPTY, Insets.EMPTY)));
                            ((Label) label).setTextFill(Color.WHITE);
                            answerLetters2.remove(i);
                        }
                    }
                }

                // This is a kinda wacky approach but if the text color wasn't changed, then the letter is not in the word
                if (row == guess && ((Label) label).getTextFill() != Color.WHITE) {
                    // Gray tile colors
                    ((Label) label).setBackground(new Background(new BackgroundFill(Color.rgb(140, 140, 140), CornerRadii.EMPTY, Insets.EMPTY)));
                    ((Label) label).setTextFill(Color.rgb(180, 180, 180));

                }

            }


            // Darken the on-screen keys of nonexistent letters
            for (Node keyButton : ((AnchorPane) (labelGrid.getParent())).getChildren()) {
                if (keyButton instanceof Button) {
                    for (int i = 0; i < 5; i++) {
                        if (((Button) keyButton).getText().compareTo(((Label) (labelGrid.getChildren().get((guess * 5) + i))).getText()) == 0   // Manually calculate the position of the desired letter to gray out, see if it's equal to a button.
                            && !answerLetters.contains(((Label) (labelGrid.getChildren().get((guess * 5) + i))).getText().charAt(0))) {   // If the manually calculated position's label's letter does not exist in the answer character array
                        ((Button) keyButton).setTextFill(Color.rgb(180, 180, 180)); // Gray out the text
                        }
                    }
                }
            }

            guess++;
            currentWord.clear();

            // Check for game loss (if the guess count is 6 (starts at 0) then end the game)
            if (guess >= 6) {
                gameEndText.setText("Game Over!");
                outcomeText.setText("The correct word is");
                statText.setText(currWord);

                // The 6th position of the gamesWon array actually tracks losses instead
                gamesWon[6]++;

                // Increment round number
                gameNum++;

                // Reset win streak
                streak = 0;

                gameEndOverlay.setVisible(true);
            } else {
                canStartTyping = true;
            }
        }
    }

}
