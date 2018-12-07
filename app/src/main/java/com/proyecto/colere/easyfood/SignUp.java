package com.proyecto.colere.easyfood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.proyecto.colere.easyfood.Common.Common;
import com.proyecto.colere.easyfood.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUp extends AppCompatActivity {

    MaterialEditText edtPhone,edtName,edtPassword, edtSecureCode;
    Button btnSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtName = (MaterialEditText)findViewById(R.id.edtName);
        edtPhone=(MaterialEditText)findViewById(R.id.edtPhone);
        edtPassword=(MaterialEditText)findViewById(R.id.edtPassword);
        edtSecureCode=(MaterialEditText)findViewById(R.id.edtSecureCode);



        //Boton Registrarse
        btnSignUp = (Button)findViewById(R.id.btnSignUp);

        //Iniciamos Firebase

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //uso final para definir una entidad que solo se puede asignar una vez
        final DatabaseReference table_user = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Comprobamos conexion a internet
                if(Common.isConnectedToInterner(getBaseContext())) {


                    final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                    mDialog.setMessage("Porfavor espera...");
                    mDialog.show();
                    //Enviamos datos
                    Intent i = new Intent(SignUp.this, SignIn.class);
                    i.putExtra("Telefono", edtPhone.getText().toString());
                    i.putExtra("password", edtPassword.getText().toString());
                    startActivity(i);


                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Chqueeamos si esta el telefono movil del user
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {

                                mDialog.dismiss();
                                Toast.makeText(SignUp.this, "Numero ya registrado", Toast.LENGTH_SHORT).show();

                            } else {

                                mDialog.dismiss();
                                User user = new User(edtName.getText().toString(),
                                        edtPassword.getText().toString(),
                                        edtSecureCode.getText().toString());
                                table_user.child(edtPhone.getText().toString()).setValue(user);
                                Toast.makeText(SignUp.this, "Registro Completo", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    Toast.makeText(SignUp.this,"Please Check your connection!",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }
}
