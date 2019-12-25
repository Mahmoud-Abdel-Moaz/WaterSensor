package com.mahmoud.watersensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StartActivty extends AppCompatActivity {

    Button but_add,but_services,but_help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_activty);

        but_add=findViewById(R.id.but_add);
        but_services=findViewById(R.id.but_services);
        but_help=findViewById(R.id.but_help);

        but_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivty.this,AddServiceActivity.class));
            }
        });
        but_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivty.this,InstructionsActivity.class));
            }
        });
        but_services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivty.this,MainActivity.class));
            }
        });

    }
}
