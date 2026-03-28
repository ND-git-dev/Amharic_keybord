package com.example.namh;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a quick button to open settings
        Button btn = new Button(this);
        btn.setText("Enable Amharic Keyboard");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This takes you directly to the menu where you toggle the keyboard
                startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
            }
        });
        setContentView(btn);
    }
}