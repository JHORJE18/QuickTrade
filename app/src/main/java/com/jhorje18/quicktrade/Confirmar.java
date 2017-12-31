package com.jhorje18.quicktrade;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Confirmar extends AppCompatActivity {

    //Variables
    boolean eliminar;
    EditText editCorreo, editPass;
    TextView txtAccion, txtConsecuencias;
    Button btnConfirmar, btnCancelar;

    FirebaseUser user;
    private FirebaseAuth mAuth;
    DatabaseReference bbdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar);

        //Orden para eliminar cuenta
        eliminar = getIntent().getExtras().getBoolean("eliminar",false);

        //Vista
        editCorreo = (EditText) findViewById(R.id.editConfirmCorreo);
        editPass = (EditText) findViewById(R.id.editConfirmContraseña);
        btnConfirmar = (Button) findViewById(R.id.btnConfirmConfirmar);
        btnCancelar = (Button) findViewById(R.id.btnConfirmCancelar);
        txtAccion = (TextView) findViewById(R.id.txtConfirmAccion);
        txtConsecuencias = (TextView) findViewById(R.id.txtConfirmConsecuencias);

        //Pantalla especifica Eliminar
        if (eliminar){
            pantallaEliminar();
        }

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Obtener BBDD FireBase
        bbdd = FirebaseDatabase.getInstance().getReference("usuarios");

        //Obtenemos info user actual
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void pantallaEliminar() {
        txtAccion.setText("Para eliminar tu cuenta.");
        txtConsecuencias.setText("Aviso! \nSe van a borrar todos tus datos asociados a su dirección de correo electrónico. No podras recuperar la información.");

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()){
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(editCorreo.getText().toString(), editPass.getText().toString());

                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(Confirmar.this, "Sesión iniciada correctamente", Toast.LENGTH_SHORT).show();
                                    eliminarUsuario();
                                }
                            });
                }
            }
        });
    }

    //Eliminamos al usuario del Sistema
    private void eliminarUsuario() {
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //TODO Eliminar productos del usuario

                            //Procedemos a eliminar
                            Query q = bbdd.orderByChild("correo").equalTo((String) editCorreo.getText().toString());

                            //Si ha encontrado algun registro unico
                            q.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                        String clave = dataSnapshot1.getKey();
                                        DatabaseReference ref = bbdd.child(clave);

                                        ref.removeValue();
                                        Toast.makeText(Confirmar.this, "Usuario " + user.getDisplayName() + " elimiando.", Toast.LENGTH_LONG).show();
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Toast.makeText(Confirmar.this, "Usuario " + user.getDisplayName() + " elimiando.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Confirmar.this, Login.class));
                            finish();
                        } else {
                            Toast.makeText(Confirmar.this, "Error al eliminar: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //Validar campos
    private boolean validarCampos() {
        boolean valido = true;

        //Validar correo
        if (editCorreo.getText().toString().isEmpty()){
            editCorreo.setError("Introduce tu dirección de correo electrónico.");
            valido = false;
        }

        if (!editCorreo.getText().toString().equals(user.getEmail())){
            editCorreo.setError("Email incorrecto!");
            valido = false;
        }

        if (editPass.getText().toString().isEmpty()){
            editPass.setError("Introduce tu contraseña.");
            valido = false;
        }

        return valido;
    }
}
