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
    boolean eliminar, cambiarMail;
    EditText editCorreo, editPass;
    TextView txtAccion, txtConsecuencias;
    Button btnConfirmar, btnCancelar;
    String claveUsuario, nuevoCorreo;

    FirebaseUser user;
    private FirebaseAuth mAuth;
    DatabaseReference bbddUser, bbddProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar);

        //Orden para eliminar cuenta
        eliminar = getIntent().getExtras().getBoolean("eliminar",false);

        //Orden para cambiar correo
        cambiarMail = getIntent().getExtras().getBoolean("cambiarMail",false);

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

        //Pantalla especifica Cambiar Correo
        if (cambiarMail){
            //Obtenemos clave usuario y nuevoCorreo
            claveUsuario = getIntent().getExtras().getString("claveUsuario");
            nuevoCorreo = getIntent().getExtras().getString("nuevoCorreo");

            pantallaCambiarEmail();
        }

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Obtener BBDD FireBase
        bbddUser = FirebaseDatabase.getInstance().getReference("usuarios");
        bbddProductos = FirebaseDatabase.getInstance().getReference("productos");

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

    private void pantallaCambiarEmail(){
        txtAccion.setText("Para cambiar el correo de tu cuenta.");
        txtConsecuencias.setText("Aviso! \nSe va a cambiar tu dirección de email y deberas de Iniciar Sesión con tu nueva dirección de Correo Electrónico.");

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarCampos()){
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(editCorreo.getText().toString(), editPass.getText().toString());

                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(Confirmar.this, "Sesión iniciada correctamente", Toast.LENGTH_SHORT).show();
                                    cambiarCorreo();
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
                            //Procedemos a eliminar
                            Query q = bbddUser.orderByChild("correo").equalTo((String) editCorreo.getText().toString());

                            //Si ha encontrado algun registro unico
                            q.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                        String clave = dataSnapshot1.getKey();
                                        DatabaseReference ref = bbddUser.child(clave);

                                        ref.removeValue();
                                        Toast.makeText(Confirmar.this, "Usuario " + user.getDisplayName() + " elimiando.", Toast.LENGTH_LONG).show();
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            //Eliminamos sus productos
                            Query qProducts = bbddProductos.orderByChild("usuario").equalTo(user.getDisplayName());
                            qProducts.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    //Recorremos todos los productos
                                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                        //Eliminamos este producto
                                        String clave = dataSnapshot1.getKey();
                                        DatabaseReference ref = bbddProductos.child(clave);

                                        ref.removeValue();
                                        Log.i("#FUNCTION","Producto con clave " + clave + " eliminado");
                                    }

                                    Toast.makeText(Confirmar.this, "Productos eliminados", Toast.LENGTH_SHORT).show();
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

    //Cambiamos correo al usuario del Sistema
    private void cambiarCorreo(){
        user.updateEmail(nuevoCorreo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i("#FUNCTION","Se ha cambiado el correo de acceso!");
                            bbddUser.child(claveUsuario).child("correo").setValue(nuevoCorreo);

                            Toast.makeText(Confirmar.this, "Correo cambiado a " + editCorreo.getText().toString(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Confirmar.this, Login.class));
                            finish();
                        } else {
                            Toast.makeText(Confirmar.this, "Se ha producido un error al intentar cambiar el correo: " + task.getException(), Toast.LENGTH_SHORT).show();
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
