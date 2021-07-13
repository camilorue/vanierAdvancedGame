package camilo;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {
    //this ArrayList holds all the snake parts.  It is of class type SnakeBody
    static ArrayList<SnakeBody> snake = new ArrayList<>();

    // variables
    static int blockSize = 15;//px size of each block of the Snake
    static int boardWidth = 30;
    static int boardHeight = 30;
    static int SPEED = 5;
    static Dir direction = Dir.left; //will start heading left
    static boolean terminated = false;
    static boolean paused = false;

    // collection of constants for the Snake direction
    public enum Dir {
        left, right, up, down
    }

    @Override
    public void start(Stage primaryStage) {

        Food.newFood();

        Pane pane = new Pane();
        Canvas canvas = new Canvas(boardWidth * blockSize, boardHeight * blockSize);//this is the background
        GraphicsContext gc = canvas.getGraphicsContext2D();// to paint the snake, food
        pane.getChildren().add(canvas);//put the canvas on the pane

        AnimationTimer animation = new AnimationTimer() {
            long lastTick = 0;

            @Override
            public void handle(long now) {
                if (lastTick == 0) {
                    lastTick = now;
                    tick(gc);
                    return;
                }

                if (now - lastTick > 1000000000 / SPEED) { //1 Mio ticks = new frame every speed
                    // second
                    lastTick = now;
                    tick(gc);
                }

            }

        };

        animation.start();

        Scene scene = new Scene(pane, boardWidth * blockSize, boardHeight * blockSize);

        //direction control
        scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            if (key.getCode() == KeyCode.UP && direction == Dir.down) {
                direction = Dir.down;
            } else if (key.getCode() == KeyCode.UP) {
                direction = Dir.up;
            }

            if (key.getCode() == KeyCode.LEFT && direction == Dir.right) {
                direction = Dir.right;
            } else if (key.getCode() == KeyCode.LEFT) {
                direction = Dir.left;
            }

            if (key.getCode() == KeyCode.RIGHT && direction == Dir.left) {
                direction = Dir.left;
            } else if (key.getCode() == KeyCode.RIGHT) {
                direction = Dir.right;
            }

            if (key.getCode() == KeyCode.DOWN && direction == Dir.up) {
                direction = Dir.up;
            } else if (key.getCode() == KeyCode.DOWN) {
                direction = Dir.down;
            }

            if (key.getCode() == KeyCode.SPACE && !paused) {
                animation.stop();
                paused=true;
            } else if (key.getCode() == KeyCode.SPACE) {
                animation.start();
                paused=false;
            }
        });

        //add initial sneak blocks
        snake.add(new SnakeBody(boardWidth / 2, boardHeight / 2));
        snake.add(new SnakeBody(boardWidth / 2, boardHeight / 2));
        snake.add(new SnakeBody(boardWidth / 2, boardHeight / 2));

        primaryStage.setScene(scene);
        primaryStage.setTitle("SnakeGame Camilo");
        primaryStage.show();
    }

    public static void tick(GraphicsContext gc) {
        if (terminated) {
            gc.setFill(Color.DARKSLATEBLUE);
            gc.setFont(new Font("", 50));
            gc.fillText("Game Over", 125, 250);
            return;
        }
        for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }
        //snake direction
        switch (direction) {
            case up:
                snake.get(0).y--;
                if (snake.get(0).y < 0) {
                    snake.set(0, new SnakeBody(snake.get(0).x,
                            boardHeight - 1));
                }
                break;
            case down:
                snake.get(0).y++;
                if (snake.get(0).y > boardHeight - 1) {
                    snake.set(0, new SnakeBody(snake.get(0).x, 0));
                }
                break;
            case left:
                snake.get(0).x--;
                if (snake.get(0).x < 0) {
                    snake.set(0, new SnakeBody(boardWidth - 1,
                            snake.get(0).y));
                }
                break;
            case right:
                snake.get(0).x++;
                if (snake.get(0).x > boardWidth - 1) {
                    snake.set(0, new SnakeBody(0, snake.get(0).y));
                }
                break;
        }

        //eat food
        if (Food.food_x == snake.get(0).x && Food.food_y == snake.get(0).y) {
            snake.add(new SnakeBody(-1, -1));//adds the block in the last ArrayList position
            Food.newFood();
        }

        //self eat
        for (int i = 1; i < snake.size(); i++) {
            //if head block = any other snake block position
            if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
                terminated = true;
            }
        }
        //background fill
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, boardWidth * blockSize, boardHeight * blockSize);

        //score
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("", 15));
        gc.fillText("score: " + (SPEED - 6), 10, 440);

        //foodColor
        Color fc = Color.YELLOW;
        switch (Food.foodColor) {
            case 0:
                fc = Color.BLUE;
                break;
            case 1:
                fc = Color.RED;
                break;
            case 2:
                fc = Color.YELLOW;
                break;
        }

        //food
        gc.setFill(fc);
        gc.fillOval(Food.food_x * blockSize, Food.food_y * blockSize, blockSize, blockSize);

        //snake  (At first I did this but I changed to the code below to have the head in a
        // different color
//        for (SnakeBody c : snake) {
//            gc.setFill(Color.ORANGE);
//            gc.fillRect(c.x * blockSize, c.y * blockSize, blockSize - 1, blockSize - 1);
//            gc.setFill(Color.GREEN);
//            gc.fillRect(c.x * blockSize, c.y * blockSize, blockSize - 2, blockSize - 2);
//        }

        // snake

        for (int i = 0; i < snake.size(); i++) {
            if (i == 0) {
                gc.setFill(Color.BROWN);
                gc.fillOval(snake.get(i).x * blockSize, snake.get(i).y * blockSize,
                        blockSize,
                        blockSize);
            }
            else {
                gc.setFill(Color.GREEN);
                gc.fillRect(snake.get(i).x * blockSize, snake.get(i).y * blockSize,
                        blockSize - 1,
                        blockSize - 1);
            }
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
