package com.example.colere.easyfood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.colere.easyfood.Common.Common;
import com.example.colere.easyfood.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
   FButton btnSignIn, btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = (FButton) findViewById(R.id.btnSignIn);
        btnSignUp = (FButton)findViewById(R.id.btnSignUp);

        //Init Paper
        Paper.init(this);

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
        //Check Recordar
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);
        if(user != null && pwd != null){
            if(!user.isEmpty() && !pwd.isEmpty())
                login(user, pwd);
        }
    }

    private void login(final String phone, final String pwd) {
        //Iniciamos Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //uso final para definir una entidad que solo se puede asignar una vez
        final DatabaseReference table_user = database.getReference("User");

        if (Common.isConnectedToInterner(getBaseContext())){
            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Porfavor espera...");
            mDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Chqueamos si el usuario no existe en el db
                    if (dataSnapshot.child(phone).exists()) {
                        //Get User Information
                        mDialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone); //setPhone
                        if (user.getPassword().equals(pwd)) {
                            Intent homeIntent = new Intent(MainActivity.this, Home.class);
                            Common.currentUser = user;
                            startActivity(homeIntent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Password Incorrecto", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "El usuario no existe", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else
        {
            Toast.makeText(MainActivity.this,"Please Check your connection!",Toast.LENGTH_SHORT).show();
            return;
        }
    }
}