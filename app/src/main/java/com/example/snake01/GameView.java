package com.example.snake01;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {
    private Thread thread;
    private boolean isPlaying = true;
    private Paint paint;
    private SurfaceHolder surfaceHolder;
    private ArrayList<SnakeSegments> snake;
    private SnakeSegments apple;
    private int score;

    private int highScore = 0; // Lưu điểm cao nhất
    private final String highScoreFileName = "highscore.txt"; // Tên file lưu điểm cao

    private int direction = 1; // 1: up, 2: right, 3: down, 4: left
    private int currentDirection = 2; // default: right
    private int speed = 200; // higher=slower

    // Number of units in the grid
    private final int gridWidth = 14;
    private final int gridHeight = 21;
    private final int unitSize = 50;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        surfaceHolder = getHolder();
        snake = new ArrayList<>();

        // Initialize the snake with 3 segments
        snake.add(new SnakeSegments(7, 12));  // Head
        snake.add(new SnakeSegments(6, 12));  // Body segment 1
        snake.add(new SnakeSegments(5, 12));  // Body segment 2
        highScore = loadHighScore();
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

        // Move the head, newX,Y__above
        SnakeSegments newHead = new SnakeSegments(newX, newY);
        snake.add(0, newHead);

        // Check: head = apple?
        if (newX == apple.getPositionX() && newY == apple.getPositionY()) {
            generateApple();
            score++;
            if (score > highScore) {
                highScore = score;
                saveHighScore(); // Lưu điểm cao mới vào file
            }

        } else {
            // Remove the last segment if not eating
            snake.remove(snake.size() - 1);
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);
//snake
            paint.setColor(Color.GREEN);
            for (SnakeSegments segment : snake) {
                canvas.drawRect(segment.getPositionX() * unitSize, segment.getPositionY() * unitSize,
                        (segment.getPositionX() + 1) * unitSize, (segment.getPositionY() + 1) * unitSize, paint);
            }
//apple
            paint.setColor(Color.RED);
            canvas.drawCircle(
                    (apple.getPositionX() + 0.5f) * unitSize,
                    (apple.getPositionY() + 0.5f) * unitSize,
                    unitSize / 2,
                    paint
            );
//text: score, high score
            paint.setColor(Color.WHITE);
            paint.setTextSize(42);
            canvas.drawText("Score: " + score, 20, 40, paint);
            canvas.drawText("High Score: " + highScore, 20, 80, paint);

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
// Button to control snake use
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
                Toast.makeText(getContext(), "Game Over! Score: " + score + " | High Score: " + highScore, Toast.LENGTH_LONG).show();
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
        highScore = loadHighScore();
    }

    public void saveHighScore() {
        try {
            FileOutputStream fos = getContext().openFileOutput(highScoreFileName, Context.MODE_PRIVATE);
            fos.write(String.valueOf(highScore).getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int loadHighScore() {
        try {
            FileInputStream fis = getContext().openFileInput(highScoreFileName);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();
            return Integer.parseInt(new String(data));
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getHighScore() {
        return highScore;
    }
}
