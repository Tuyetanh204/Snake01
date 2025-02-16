package com.example.snake01;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public class SpeedActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);
        sharedPreferences = getSharedPreferences("game_prefs", MODE_PRIVATE);

        Button btnSlow = findViewById(R.id.btnSlow);
        Button btnMedium = findViewById(R.id.btnMedium);
        Button btnFast = findViewById(R.id.btnFast);

        btnSlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSpeed("slow");
            }
        });

        btnMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSpeed("medium");
            }
        });

        btnFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSpeed("fast");
            }
        });

        // Minh họa tốc độ rắn
        ImageView previewSlow = findViewById(R.id.previewSlow);
        ImageView previewMedium = findViewById(R.id.previewMedium);
        ImageView previewFast = findViewById(R.id.previewFast);

        setupPreviewAnimation(previewSlow, 300); // Slow speed
        setupPreviewAnimation(previewMedium, 200); // Medium speed
        setupPreviewAnimation(previewFast, 100); // Fast speed
    }

    private void setupPreviewAnimation(final ImageView preview, final int duration) {
        final int distance = 300; //px
        final Runnable[] moveRight = new Runnable[1];
        final Runnable[] moveLeft = new Runnable[1];

        moveRight[0] = new Runnable() {
            @Override
            public void run() {
                preview.animate().translationXBy(distance).setDuration(duration).withEndAction(moveLeft[0]);
            }
        };
        moveLeft[0] = new Runnable() {
            @Override
            public void run() {
                preview.animate().translationXBy(-distance).setDuration(duration).withEndAction(moveRight[0]);
            }
        };
        // Bắt đầu chuyển động
        handler.post(moveRight[0]);
    }

    private void setSpeed(String speed) {
        // Lưu lại tốc độ đã chọn
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("speed", speed);
        editor.apply();

        // Trở về màn hình chính
        Intent intent = new Intent();
        intent.putExtra("speed", speed);
        setResult(RESULT_OK, intent);
        finish();
    }
}
