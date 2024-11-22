package com.example.snake01;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {
    private Thread thread;
    private boolean isPlaying;
    private Paint paint;
    private SurfaceHolder surfaceHolder;
    private ArrayList<SnakeSegments> snake;
    private SnakeSegments apple;
    private int score;
    private int direction = 1; // 1: up, 2: right, 3: down, 4: left
    private int currentDirection = 2; // default starting direction: right
    private int speed = 200; // cang cao ran cang cham

    // Number of units in the grid
    private final int gridWidth = 15; // 15 units for width
    private final int gridHeight = 25; // 25 units for height
    private final int unitSize = 40; // Each unit is 40dp

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        surfaceHolder = getHolder();
        snake = new ArrayList<>();
        // Initialize the snake with 3 segments
        snake.add(new SnakeSegments(7, 12));  // Head
        snake.add(new SnakeSegments(6, 12));  // Body segment 1
        snake.add(new SnakeSegments(5, 12));  // Body segment 2
        generateApple();
    }

    public void setSpeed(String speed) {
        switch (speed) {
            case "slow":
                this.speed = 300; // Slow speed
                break;
            case "medium":
                this.speed = 200; // Medium speed (1.5x faster than slow)
                break;
            case "fast":
                this.speed = 100; // Fast speed (2x faster than medium)
                break;
            default:
                this.speed = 200; // Default to medium if not specified
        }
    }

    private void generateApple() {
        Random random = new Random();
        boolean validPosition = false;

        while (!validPosition) {
            int x = random.nextInt(gridWidth);
            int y = random.nextInt(gridHeight);

            // Kiểm tra xem vị trí mới có trùng với bất kỳ khúc thân nào của rắn không
            boolean collision = false;
            for (SnakeSegments segment : snake) {
                if (segment.getPositionX() == x && segment.getPositionY() == y) {
                    collision = true;
                    break;
                }
            }

            if (!collision) {
                apple = new SnakeSegments(x, y);
                validPosition = true;
            }
        }
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        SnakeSegments head = snake.get(0);
        int newX = head.getPositionX();
        int newY = head.getPositionY();

        switch (currentDirection) {
            case 1: newY--; break; // Up
            case 2: newX++; break; // Right
            case 3: newY++; break; // Down
            case 4: newX--; break; // Left
        }

        // Wrap the snake around the screen
        if (newX < 0) {
            newX = gridWidth - 1; // Wrap to the other side
        } else if (newX >= gridWidth) {
            newX = 0;
        }

        if (newY < 0) {
            newY = gridHeight - 1; // Wrap to the other side
        } else if (newY >= gridHeight) {
            newY = 0;
        }

        // Check if the snake collides with itself after updating position
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(i).getPositionX() == newX && snake.get(i).getPositionY() == newY) {
                isPlaying = false;
                showGameOver();
                return;
            }
        }

        // Move the head to the new position
        SnakeSegments newHead = new SnakeSegments(newX, newY);
        snake.add(0, newHead);

        // Check if the head has eaten the apple
        if (newX == apple.getPositionX() && newY == apple.getPositionY()) {
            generateApple();
            score++;
        } else {
            // Remove the last segment if not eating
            snake.remove(snake.size() - 1);
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);

            paint.setColor(Color.GREEN);
            for (SnakeSegments segment : snake) {
                canvas.drawRect(segment.getPositionX() * unitSize, segment.getPositionY() * unitSize,
                        (segment.getPositionX() + 1) * unitSize, (segment.getPositionY() + 1) * unitSize, paint);
            }

            paint.setColor(Color.RED);
            canvas.drawRect(apple.getPositionX() * unitSize, apple.getPositionY() * unitSize,
                    (apple.getPositionX() + 1) * unitSize, (apple.getPositionY() + 1) * unitSize, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(35);
            canvas.drawText("Score: " + score, 20, 40, paint);

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(speed); // Sleep duration based on speed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause() {
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setDirection(int newDirection) {
        // Prevent the snake from turning 180 degrees
        if ((currentDirection == 1 && newDirection == 3) || // Up to Down
                (currentDirection == 3 && newDirection == 1) || // Down to Up
                (currentDirection == 2 && newDirection == 4) || // Right to Left
                (currentDirection == 4 && newDirection == 2)) { // Left to Right
            return;
        }
        currentDirection = newDirection;
    }

    private void showGameOver() {
        post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "Game Over! Score: " + score, Toast.LENGTH_LONG).show();
                // Implementing replay mechanism
                ((PlayActivity) getContext()).showGameOverDialog(score);
            }
        });
    }

    public void resetGame() {
        snake.clear();
        // Initialize the snake with 3 segments
        snake.add(new SnakeSegments(7, 12));  // Head
        snake.add(new SnakeSegments(6, 12));  // Body segment 1
        snake.add(new SnakeSegments(5, 12));  // Body segment 2
        generateApple();
        score = 0;
        currentDirection = 2; // Reset to initial direction
    }
}
