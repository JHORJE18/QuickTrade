package com.jhorje18.quicktrade;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    //Variables
    EditText editCorreo, editPass;
    Button btnRegistro, btnLogin, btnRecup;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Vista
        editCorreo = (EditText) findViewById(R.id.editLoginCorreo);
        editPass = (EditText) findViewById(R.id.editLoginContraseña);
        btnRegistro = (Button) findViewById(R.id.btnLoginRegistrarse);
        btnLogin = (Button) findViewById(R.id.btnLoginLogin);
        btnRecup = (Button) findViewById(R.id.btnLoginRecup);

        //Cerramos sesion si hay alguna abierta!
        FirebaseAuth.getInstance().signOut();

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ventanaRegistro();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(editCorreo.getText().toString(), editPass.getText().toString());
            }
        });

        btnRecup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Comprueba correo válido!
                if (correoValido(editCorreo.getText().toString())){
                    recuperarContraseña(editCorreo.getText().toString());
                }
            }
        });
    }

    private boolean correoValido(String correo) {
        if (correo.isEmpty()){
            //Vacio!
            editCorreo.setError(getString(R.string.error_input_email));
            return false;
        }

        //Valida estructura String
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = pattern.matcher(editCorreo.getText().toString());

        if (!matcher.matches()){
            //No valido!
            editCorreo.setError(getString(R.string.error_input_emailincorrect));
            return false;
        }

        return true;
    }

    private void ventanaRegistro() {
        Intent nueva = new Intent(this, Registro.class);
        startActivity(nueva);
        finish();
    }

    private void inicio() {
        Intent inicio = new Intent(this,MainActivity.class);
        startActivity(inicio);
        finish();
    }

    private void login(final String email, String password) {

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Login.this, getString(R.string.login_correct), Toast.LENGTH_SHORT).show();

                            inicio();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void recuperarContraseña(final String email){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, getString(R.string.send_password) + email, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, getString(R.string.error) + " " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
