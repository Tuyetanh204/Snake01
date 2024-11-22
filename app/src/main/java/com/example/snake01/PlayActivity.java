package com.example.snake01;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class PlayActivity extends AppCompatActivity {
        private GameView gameView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_play);

            gameView = findViewById(R.id.gameView);

            // Gán chức năng cho các nút điều khiển rắn
            ImageButton btnUp = findViewById(R.id.btnUp);
            ImageButton btnDown = findViewById(R.id.btnDown);
            ImageButton btnLeft = findViewById(R.id.btnLeft);
            ImageButton btnRight = findViewById(R.id.btnRight);

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

        public void showGameOverDialog(int score) {
            new AlertDialog.Builder(this)
                    .setTitle("Game Over")
                    .setMessage("Score: " + score)
                    .setPositiveButton("Replay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Reset and restart the game
                            gameView.resetGame();
                            gameView.resume();
                        }
                    })
                    .setNegativeButton("Exit to Main Menu", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Exit to main menu
                            Intent intent = new Intent(PlayActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
