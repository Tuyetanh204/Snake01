package com.example.snake01;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int SPEED_REQUEST_CODE = 1;
    private String selectedSpeed = "medium"; // Default speed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button playButton = findViewById(R.id.Play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                intent.putExtra("speed", selectedSpeed);
                startActivity(intent);
            }
        });

        Button speedButton = findViewById(R.id.Speed);
        speedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SpeedActivity.class);
                startActivityForResult(intent, SPEED_REQUEST_CODE);
            }
        });

        Button exitButton = findViewById(R.id.Exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitConfirmationDialog();
            }
        });

        Button highScoreButton = findViewById(R.id.HiScore);
        highScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int highScore = loadHighScore(); // Lấy điểm cao từ file
                showHighScoreDialog(highScore); // Hiển thị điểm cao
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEED_REQUEST_CODE && resultCode == RESULT_OK) {
            selectedSpeed = data.getStringExtra("speed");
        }
    }

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit the app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity(); // Exit the application
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showHighScoreDialog(int highScore) {
        new AlertDialog.Builder(this)
                .setTitle("High Score")
                .setMessage("Your High Score: " + highScore)
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private int loadHighScore() {
        try {
            FileInputStream fis = openFileInput("highscore.txt");
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();
            return Integer.parseInt(new String(data));
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
