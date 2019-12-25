package com.mahmoud.watersensor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddServiceActivity extends AppCompatActivity {
    EditText edit_name;
    Button but_add;

    FirebaseFirestore firestore;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        edit_name=findViewById(R.id.edit_name);
        but_add=findViewById(R.id.but_add);

        firestore=FirebaseFirestore.getInstance();

        but_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setService();
            }
        });
    }
    private void setService(){
        name=edit_name.getText().toString().trim();
        if (name==null||name.isEmpty()){
            Toast.makeText(this, "Name Is Required", Toast.LENGTH_SHORT).show();
            return;
        }else {
            Service service=new Service(name,"0");
            firestore.collection("Services").document(name).set(service).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(AddServiceActivity.this, "The Service was Added Successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
