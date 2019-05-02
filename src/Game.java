import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.sound.sampled.*;
import javax.swing.JOptionPane;

public class Game {
    private Grid grid;

    private int userRow;
    private int userCol;
    private int userDirection;
    private int key;

    private int msElapsed;
    private int timesGet;
    private int timesAvoid;
    public int score;

    private Map map;

    private static Clip clipEatDot, clipEatGhost;

    public Game() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        grid = new Grid(25, 25);
        userRow = 1;
        userCol = 1;
        userDirection = -1;
        msElapsed = 0;
        timesGet = 0;
        timesAvoid = 0;
        score = 0;
        updateTitle();
        grid.setImage(new Location(userRow, userCol), "pacRight.PNG");
        map = new Map();
        map.drawSelf(grid);

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/eatDotSound.wav").getAbsoluteFile());
        clipEatDot = AudioSystem.getClip();
        clipEatDot.open(audioInputStream);
    }

    public void handleKeyPress() {
        key = grid.checkLastKeyPressed();
        calcUserDirection();
        changeUserDirection();
    }

    public void play() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        introMusic();
        while (!isGameOver()) {
            grid.pause(100);
            handleKeyPress();
            updateTitle();
            msElapsed += 100;
            addPoint();
        }
    }

    private void calcUserDirection() {
        if (grid.isValid(new Location(userRow - 1, userCol)) && map.isValid(new Location(userRow - 1, userCol)) && key == 38) { //up
            userDirection = 0;
        } else if (grid.isValid(new Location(userRow + 1, userCol)) && map.isValid(new Location(userRow + 1, userCol)) && key == 40) { //down
            userDirection = 1;
        } else if (grid.isValid(new Location(userRow, userCol - 1)) && map.isValid(new Location(userRow, userCol - 1)) && key == 37) { //left
            userDirection = 2;
        } else if (grid.isValid(new Location(userRow, userCol + 1)) && map.isValid(new Location(userRow, userCol + 1)) && key == 39) { //right
            userDirection = 3;
        }
    }

    private void changeUserDirection() {
        if (verifyAllValid(new Location(userRow - 1, userCol)) && userDirection == 0) {
            grid.setImage(new Location(userRow, userCol), null);
            userRow--;
            grid.setImage(new Location(userRow, userCol), "pacUp.PNG");
        } else if (verifyAllValid(new Location(userRow + 1, userCol)) && userDirection == 1) {
            grid.setImage(new Location(userRow, userCol), null);
            userRow++;
            grid.setImage(new Location(userRow, userCol), "pacDown.PNG");
        } else if (verifyAllValid(new Location(userRow, userCol - 1)) && userDirection == 2) {
            grid.setImage(new Location(userRow, userCol), null);
            userCol--;
            grid.setImage(new Location(userRow, userCol), "pacLeft.PNG");
        } else if (verifyAllValid(new Location(userRow, userCol + 1)) && userDirection == 3) {
            grid.setImage(new Location(userRow, userCol), null);
            userCol++;
            grid.setImage(new Location(userRow, userCol), "pacRight.PNG");
        }
    }

    private boolean verifyAllValid(Location loc) {
        return grid.isValid(loc) && map.isValid(loc);
    }

    public void handleCollision(Location loc) {
    }

    public void addPoint() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        for (int x = 0; x < map.getLilDot().dotLocation.size(); x++) //eating lil dots
            if (map.getLilDot().dotLocation.get(x).equals(new Location(userRow, userCol))) {
                map.getLilDot().dotLocation.remove(x);
                score++;
                eatDot();
            }
        for (int y = 0; y < map.getBigDot().dotLocation.size(); y++) //eating big dots
            if (map.getBigDot().dotLocation.get(y).equals(new Location(userRow, userCol))) {
                map.getBigDot().dotLocation.remove(y);
                score += 10;
                // once the ghosts are created, uncomment this section
                // grid.setImage(new Location(pinkRow, pinkCol), "EATghost.gif");
                // grid.setImage(new Location(redRow, redCol), "EATghost.gif");
                // grid.setImage(new Location(blueRow, blueCol), "EATghost.gif");
                // grid.setImage(new Location(greenRow, greenCol), "EATghost.gif");
            }
    }

    public void updateTitle() {
        grid.setTitle("Pac-Boy (The prequel to Pac-Man)       Score: " + getScore());
    }

    public boolean isGameOver() {
        return false;
    }

    public int getScore() {
        return score;
    }

    public static void test() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        Game game = new Game();
        game.play();
    }

    public static void introMusic() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/introMusic.wav").getAbsoluteFile());
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.loop(0);
    }

    public static void eatDot() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        System.out.println(clipEatDot.isActive());
        if (!clipEatDot.isActive()) {
            clipEatDot.flush();
            clipEatDot.setFramePosition(0);
            clipEatDot.start();
        }
    }

    public static void eatGhost() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/eatGhostSound.wav").getAbsoluteFile());
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.loop(0);
    }

    public static void deathSound() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/death.wav").getAbsoluteFile());
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.loop(0);
    }

    public static void vulnerableGhosts() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/allBlueGhosts.wav").getAbsoluteFile());
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.loop(-1);
    }

    public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        Game.test();
    }
}