package com.example.snake01;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;

public class PlayActivity extends AppCompatActivity {
    private GameView gameView;
    private String speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        SharedPreferences preferences = getSharedPreferences("game_data", MODE_PRIVATE);
        int highScore = preferences.getInt("high_score", 0); // Mặc định là 0 nếu không có giá trị

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
                .setMessage("Would u like to resume?")
                .setPositiveButton("Resume", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // dem nguoc 3s -> resume()
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
                .setCancelable(false) // Cái này để lúc bấm ra ngoài nó không bị mất dialog
                .show();
    }

    private void countDownAndResume(TextView tvCountDown) {
        // Đặt lại TextView thành VISIBLE, k lần sau lại đ hiện tv nữa
        tvCountDown.setVisibility(View.VISIBLE);

        new CountDownTimer(3000, 1000) { // 3 giây (3000ms), đếm mỗi 1 giây (1000ms)

            //onTick được gọi mỗi khi bộ đếm ngược giảm xuống (dựa trên tham số truyền vào CountDownTimer)
            @Override
            public void onTick(long millisUntilFinished) {
                // Hiển thị số giây còn lại len màn hình
                tvCountDown.setText(String.valueOf((int) millisUntilFinished / 1000));
            }

            //phương thức được gọi khi bộ đếm ngược kết thúc, tức là khi thgian còn lại = 0.
            @Override
            public void onFinish() {
                // Resume the game
                gameView.resume();
                // Ẩn TextView sau khi đếm xonng
                tvCountDown.setVisibility(View.GONE);
            }
        }.start();
    }
    // Trong PlayActivity, khi game over
    public void showGameOverDialog(int score) {
        int highScore = gameView.getHighScore(); // Lấy điểm cao từ GameView

        // Kiểm tra nếu điểm vừa chơi lớn hơn điểm cao đã lưu
        SharedPreferences preferences = getSharedPreferences("game_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int savedHighScore = preferences.getInt("high_score", 0); // Lấy điểm cao hiện tại

        if (score > savedHighScore) {
            editor.putInt("high_score", score);  // Lưu lại điểm cao mới nếu lớn hơn
            editor.apply();
        }

        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Score: " + score)
                .setPositiveButton("Replay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        gameView.resetGame();
                        gameView.resume();
                    }
                })
                .setNegativeButton("Exit to Main Menu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Chuyển về MainActivity
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
    private void saveHighScore(int score) {
        SharedPreferences preferences = getSharedPreferences("game_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("high_score", score);
        editor.apply();  // Hoặc editor.commit(); để lưu thay đổi
    }
}
