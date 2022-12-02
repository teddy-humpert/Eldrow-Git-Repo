import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.Scanner;

//print main menu
//one player or two player
//prompt for first guess
//loop until guess is correct
//print results
//log results

public class Eldrow {

    private final Scanner keyboard = new Scanner(System.in);

    private List<String> dataset = new ArrayList<>();

    public static void main(String[] args) {
        Eldrow eldrow = new Eldrow();
        eldrow.loadData();
        eldrow.run();
    }

    private void loadData() {
        String[] tempArray = WordList.load();
        for (int i = 0; i < tempArray.length; i++) {
            dataset.add(tempArray[i]);
        }
    }

    private void run() {

        Collections.shuffle(dataset);
        String playWord = dataset.get(0).toUpperCase();
        char[] playWordSplit = playWord.toCharArray();
//                                                                                                                      System.out.println(dataset.get(0) + " preWhile");

        while (true) {
            printTitleCard();
            printMainMenu();
            int mainMenuSelection = promptForMenuSelection("Please choose an option: ");
            if (mainMenuSelection == 1) {
                playSinglePlayer(playWordSplit, playWord);
            }
            if (mainMenuSelection == 2) {
//                playTwoPlayer();
                break;
            }
            if (mainMenuSelection == 3) {
                displayPastResults();
            }
            if (mainMenuSelection == 0) {
                break;
            }
            break;
        }

    }

    private void printTitleCard() {
        System.out.println("*****************");
        System.out.println("WELCOME TO ELDROW");
        System.out.println("*****************");
        System.out.println();
    }

    private void printMainMenu() {
        System.out.println("1: Play One Player Game");
        System.out.println("2: Play Two Player Game");
        System.out.println("3: Display Past Results");
        System.out.println("0: Exit");
        System.out.println();
    }

    private void playSinglePlayer(char[] playWordSplit, String playWord) {
        String resultString = "";

//                                                                                                                      System.out.println(playWord + " test");
        String playerGuess = "";
        String playerGuessLog = "";
        int guessCount = 0;
        char[] playerGuessSplit = playerGuess.toCharArray();
        while (true) {
            playerGuess = promptForString("Input guess (5 characters) ").toUpperCase();
            if (playerGuess.length() != 5) {
                System.out.println("Guess must be 5 characters.");
                System.out.println("You've made " + guessCount + " guess(es) so far.");
//                                                                                                                      break;
//                                                                                                                      playSinglePlayer(playWordSplit, playWord);
                continue;
            }
            resultString = checkGuessVsPlayWord(playerGuess, playWord);
            playerGuessLog += resultString + "\n";
            System.out.println(playerGuessLog);
            System.out.println(letterCountString(playerGuess, playWord));
            guessCount++;
//                                                                                                                      System.out.println(guessCount);
            if (guessCount > 6) {
                System.out.println("*insert sad tuba sound here*");
                System.out.println("TOO BAD!");
                System.out.println("The word you were looking for was: " + playWord + ".");
                logFailResults(guessCount);
                break;
            }
            if (playerGuess.equalsIgnoreCase(playWord)) {
                displayResults(guessCount);
                logSuccessResults(guessCount);
                break;
            }
        }
        promptForReturn();
    }

    private void playTwoPlayer() {

    }

    private String checkGuessVsPlayWord(String playerGuess, String playWord) {
        String guessResultString = "";
        char[] playerGuessArray = playerGuess.toCharArray();
        char[] playWordArray = playWord.toCharArray();
        int rightCount = 0;
        int wrongCount = 0;
        int nearCount = 0;
        String nearString = "";

                                                                                                                        // so the issue here is that if there's a word with more than one of a single letter
                                                                                                                        // it will 'print' problematically
                                                                                                                        // feel like i need to map word first, and get values of each letter.
        Map<Character, Integer> playWordMap = WordMap(playWord);
        Map<Character, Integer> guessWordMap = WordMap(playerGuess);
                                                                                                                        // so now with the near stuff it's a matter of testing maps against each other.
                                                                                                                         // i think i need to build a map as i go
        Map<Character, Integer> buildingMap = new HashMap<>();
        for (int i = 0; i < playerGuessArray.length; i++) {
            char guessLetter = playerGuessArray[i];
            if (!buildingMap.containsKey(guessLetter)) {
                buildingMap.put(guessLetter, 1);
            } else {
                buildingMap.put(guessLetter, buildingMap.get(guessLetter) + 1);
            }

            if (playerGuessArray[i] == playWordArray[i]) {
                guessResultString += "|+" + playerGuessArray[i] + "+|";
                rightCount++;
            }
                                                                                                                        // so it's really here that i have an issue.
                                                                                                                        // nullpointerexception by way of mapping. fixed.
                                                                                                                        // STILL NOT WORKING
                                                                                                                        // ok so the playWordMap will in a word like apple be : a 1 p 2 l 1 e 3
                                                                                                                        // so for a guess against apple like plant it would say yes there's a p
                                                                                                                        // had the map building in the wrong spot
            if (playerGuessArray[i] != playWordArray[i] &&
                    (new String(playWordArray).indexOf(playerGuessArray[i]) > -1)) { // if guessLetter isn't in the right place, but it is in the word...

                if (buildingMap.get(guessLetter) <= playWordMap.get(guessLetter)) {
                    guessResultString += "!?" + playerGuessArray[i] + "?|";
                    nearCount++;
                } else {
                    guessResultString += "|-" + playerGuessArray[i] + "-|";
                    wrongCount++;
                }
            }
            if (playerGuessArray[i] != playWordArray[i] &&
                    (new String(playWordArray).indexOf(playerGuessArray[i]) < 0)) {
                guessResultString += "|-" + playerGuessArray[i] + "-|";
                wrongCount++;
            }
        }
        return guessResultString;
    }

    private String letterCountString(String playerGuess, String playWord) {
        char[] playerGuessArray = playerGuess.toCharArray();
        char[] playWordArray = playWord.toCharArray();
        int rightCount = 0;
        int wrongCount = 0;
        int nearCount = 0;
        for (int i = 0; i < playerGuessArray.length; i++) {
            if (playerGuessArray[i] == playWordArray[i]) {
                rightCount++;
            }
            if (playerGuessArray[i] != playWordArray[i] &&
                    (new String(playWordArray).indexOf(playerGuessArray[i]) > -1)) {
                nearCount++;
                                                                                                                        // ok but then do i need to make another map and compare the two?
            }
            if (playerGuessArray[i] != playWordArray[i] &&
                    (new String(playWordArray).indexOf(playerGuessArray[i]) < 0)) {
                wrongCount++;
            }
        }
        String letterCountString = "Your last guess has: ";
        if (rightCount > 0) {
            letterCountString += "\n" + rightCount + " letters in the right place (+).";
        }
        if (nearCount > 0) {
            letterCountString += "\n" + nearCount + " letters in the word but in the wrong place (?).";
        }
        if (wrongCount > 0) {
            letterCountString += "\n" + wrongCount + " letters not in the word at all (-).";
        }
        return letterCountString;
    }

    private void displayResults(int guessCount) {
        System.out.println("******************");
        if (guessCount == 1) {
            System.out.println("YOU GOT IT IN " + guessCount + " GUESS!!");
            System.out.println("THAT'S FREAKING INCREDIBLE!");
        }
        if (guessCount > 1 && guessCount < 6) {
            System.out.println("YOU GOT IT IN " + guessCount + " GUESSES!!");
            System.out.println("VERY NICE!");
        }
        if (guessCount >= 6) {
            System.out.println("YOU GOT IT IN " + guessCount + " GUESSES!!");
            System.out.println("THAT WAS CLOSE!");
        }
        System.out.println("******************");
    }

    private int promptForMenuSelection(String prompt) {
        System.out.print(prompt);
        int menuSelection;
        try {
            menuSelection = Integer.parseInt(keyboard.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    private String promptForString(String prompt) {
        System.out.print(prompt);
        return keyboard.nextLine();
    }

    public Map<Character, Integer> WordMap(String word) {
                                                                                                                        //idea here is to generate a map that can be used to test existence of letters in guess.
        List<Character> wordSplit = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            wordSplit.add(word.charAt(i));
        }
        Map<Character, Integer> output = new HashMap<>();

        for (Character letter : wordSplit) {
            if (!output.containsKey(letter)) {
                output.put(letter, 1);
            } else {
                output.put(letter, output.get(letter) + 1);
            }
        }
        return output;
    }

    private void promptForReturn() {
        System.out.println("Press RETURN to continue.");
        keyboard.nextLine();
        run();
    }

    private void logSuccessResults(int guessCount ) {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        String auditPath = "resultsLog.txt";
        File logFile = new File(auditPath);
        // Using a FileOutputStream with true passed into the constructor opens the file for append.
        try (PrintWriter log = new PrintWriter(new FileOutputStream(logFile, true))) {
            log.println("Successful in " + guessCount + " guesses on " + strDate);
        } catch (
                FileNotFoundException fnfe) {
            System.out.println("*** Unable to open log file: " + logFile.getAbsolutePath());
        }
    }

    private void logFailResults(int guessCount ) {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        String auditPath = "resultsLog.txt";
        File logFile = new File(auditPath);
        // Using a FileOutputStream with true passed into the constructor opens the file for append.
        try (PrintWriter log = new PrintWriter(new FileOutputStream(logFile, true))) {
            log.println("Unsuccessful on " + strDate);
        } catch (FileNotFoundException e) {
            System.out.println("*** Unable to open log file: " + logFile.getAbsolutePath());
        }
    }

    private void displayPastResults(){
        String filePath = "C:\\Users\\Student\\teddyCode\\Edlrow-Git-Repo\\target\\classes\\resultsLog.txt";
        File logFile = new File(filePath);
        try (Scanner fileInput = new Scanner(logFile)) {
            while (fileInput.hasNextLine()) {
                System.out.println(fileInput.nextLine());
            }
        }catch (FileNotFoundException fnfe) {
            System.out.println("The file was not found: " + logFile.getAbsolutePath());
        }
    }

}
