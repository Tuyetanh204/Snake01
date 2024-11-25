package com.example.snake01;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

public class PlayActivity extends AppCompatActivity {
    private GameView gameView;
    private String speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Intent intent = getIntent();
        speed = intent.getStringExtra("speed");

        gameView = findViewById(R.id.gameView);
        gameView.setSpeed(speed);

        // Gán chức năng cho các nút điều khiển rắn
        ImageButton btnUp = findViewById(R.id.btnUp);
        ImageButton btnDown = findViewById(R.id.btnDown);
        ImageButton btnLeft = findViewById(R.id.btnLeft);
        ImageButton btnRight = findViewById(R.id.btnRight);
        ImageButton btnPause = findViewById(R.id.btnPause);

        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.setDirection(1);
            }
        });

        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.setDirection(3);
            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.setDirection(4);
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.setDirection(2);
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameView.pause();
                showPauseDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    public void showPauseDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Game Paused")
                .setMessage("Would you like to resume?")
                .setPositiveButton("Resume", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // đếm ngược 3 giây -> resume()
                        TextView tvCountDown = findViewById(R.id.tvCountDown);
                        countDownAndResume(tvCountDown);
                    }
                })
                .setNeutralButton("Restart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // restart
                        gameView.resetGame();
                        gameView.resume();
                    }
                })
                .setNegativeButton("Main Menu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Exit to main menu
                        Intent intent = new Intent(PlayActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_media_pause)
                .setCancelable(false)
                .show();
    }

    private void countDownAndResume(TextView tvCountDown) {
        tvCountDown.setVisibility(View.VISIBLE);

        new CountDownTimer(3000, 1000) { // 3 giây (3000ms), đếm mỗi 1 giây (1000ms)
            @Override
            public void onTick(long millisUntilFinished) {
                tvCountDown.setText(String.valueOf((int) millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                gameView.resume();
                tvCountDown.setVisibility(View.GONE);
            }
        }.start();
    }

    public void showGameOverDialog(int score) {
        int highScore = gameView.getHighScore(); // Lấy điểm cao từ GameView

        if (score > highScore) {
            highScore = score;
            gameView.saveHighScore(); // Lưu lại điểm cao mới nếu lớn hơn
        }

        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Score: " + score + "\nHigh Score: " + highScore)
                .setPositiveButton("Replay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        gameView.resetGame();
                        gameView.resume();
                    }
                })
                .setNegativeButton("Exit to Main Menu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(PlayActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
    }
}
