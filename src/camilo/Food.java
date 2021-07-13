package camilo;

import java.util.Random;

public class Food {
    static Random random = new Random();
    //coordinates for the food and food color
    static int foodColor = 0;
    static int food_x = 0;
    static int food_y = 0;

    //food
    public static void newFood() {
        //random location at food_x,food_y on the canvas (if there is no snake)
        beginning:
        while (true) {
            food_x = random.nextInt(Main.boardWidth);
            food_y = random.nextInt(Main.boardHeight);

            for (SnakeBody c : Main.snake) {
                if (c.x == food_x && c.y == food_y) {
                    continue beginning;
                }
            }

            foodColor = random.nextInt(3);//foodColor between 0 and 2
            Main.SPEED++;
            break;
        }
    }
}
