package com.example.samplefingerprint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // Button btn_verify = findViewById(R.id.btn_verify);
//        btn_verify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
//                        .setTitle("Please Verify")
//                        .setDescription("User Authentication is required to Proceed")
//                        .setNegativeButtonText("Cancel")
//                        .build();
//                getPrompts().authenticate(promptInfo);
//            }
//        });

        Button btn_open = findViewById(R.id.btn_open);
        btn_open.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,SecretAccess.class);
            startActivity(intent);

        });
        final EditText edit_name = findViewById(R.id.edit_name);
      final   EditText edit_position = findViewById(R.id.edit_position);
        Button btn = findViewById(R.id.btn_submit);
        DAOEmployee dao = new DAOEmployee();
        Employee emp_edit = (Employee)getIntent().getSerializableExtra("EDIT");
        if(emp_edit !=null){
            btn.setText("UPDATE");
            edit_name.setText(emp_edit.getName());
            edit_position.setText(emp_edit.getPosition());
            btn_open.setVisibility(View.GONE);
        }else{
            btn.setText("SUMBIT");
            btn_open.setVisibility(View.VISIBLE);

        }


        btn.setOnClickListener(v -> {

             Employee emp = new Employee(edit_name.getText().toString(),edit_position.getText().toString());
             if(emp_edit == null) {
                 dao.add(emp).addOnCompleteListener(suc -> {
                     Toast.makeText(this, "Record Inserted", Toast.LENGTH_SHORT).show();
                 }).addOnFailureListener(er -> {
                     Toast.makeText(this, "Record Failure" + er.getMessage(), Toast.LENGTH_SHORT).show();
                 });
             }else {
                 HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("name",edit_name.getText().toString());
            hashMap.put("position",edit_position.getText().toString());
            dao.update(emp_edit.getKey(),hashMap).addOnCompleteListener(suc ->{

                Toast.makeText(this, "Record Inserted", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(er->{
                Toast.makeText(this, "Record Failure"+er.getMessage(), Toast.LENGTH_SHORT).show();
            });


             }
//


        });




    }
    private BiometricPrompt getPrompts(){
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                    notifyUser(errString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                notifyUser("Authentication Success");
               // Intent intent = new Intent(MainActivity.this,SecretAccess.class);
                //startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                notifyUser("Authentication Failed");
            }
        };
        BiometricPrompt biometricPrompt = new BiometricPrompt(this,executor,callback);
        return biometricPrompt;

    }
    private void notifyUser(String message){
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Please Verify")
                .setDescription("User Authentication is required to Proceed")
                .setNegativeButtonText("Cancel")
                .build();
        getPrompts().authenticate(promptInfo);
    }
}