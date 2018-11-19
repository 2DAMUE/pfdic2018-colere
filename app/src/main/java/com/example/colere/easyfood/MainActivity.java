package com.example.colere.easyfood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity {

 

   FButton btnSignIn, btnSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = (FButton) findViewById(R.id.btnSignIn);
        btnSignUp = (FButton)findViewById(R.id.btnSignUp);

        btnSignIn.setOnClickListener(new View.OnClickListener() { //llamamos al botón
            @Override
            public void onClick(View v) {

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() { //llamamos al botón
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent (MainActivity.this,SignUp.class);
                startActivity(signUp);

            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIn = new Intent(MainActivity.this,SignIn.class);
                startActivity(signIn);

            }
        });

    }
}
