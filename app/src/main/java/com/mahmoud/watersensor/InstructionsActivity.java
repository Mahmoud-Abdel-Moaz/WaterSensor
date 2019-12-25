package com.mahmoud.watersensor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class InstructionsActivity extends AppCompatActivity {

    TextView txt_body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        txt_body=findViewById(R.id.txt_body);
    }
}
