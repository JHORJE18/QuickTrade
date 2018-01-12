package com.jhorje18.quicktrade;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jhorje18.quicktrade.model.Usuario;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {

    //Variables
    EditText editUsuario, editCorreo, editContraseña, editNombre, editApedillos, editDireccion;
    ArrayList<String> listaUsuarios;

    private FirebaseAuth mAuth;
    DatabaseReference bbdd;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Vista
        editUsuario = (EditText) findViewById(R.id.editRegUsuario);
        editCorreo = (EditText) findViewById(R.id.editRegCorreo);
        editContraseña = (EditText) findViewById(R.id.editRegContraseña);
        editNombre = (EditText) findViewById(R.id.editRegNombre);
        editApedillos = (EditText) findViewById(R.id.editRegApedillos);
        editDireccion = (EditText) findViewById(R.id.editRegDireccion);

        //Iniciamos ArrayList
        listaUsuarios = new ArrayList<String>();

        //Obtener BBDD FireBase
        bbdd = FirebaseDatabase.getInstance().getReference("usuarios");

        //Añadir evento al detectar nuevo valor en BBDD
        bbdd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Obtenemos nombres de usuario
                for(DataSnapshot datasnapshot: dataSnapshot.getChildren()){
                    Usuario usuarioTEMP = datasnapshot.getValue(Usuario.class);

                    String userUsuario = usuarioTEMP.getUsuario();
                    listaUsuarios.add(userUsuario);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Validar campos
    private boolean validarCampos(){
        boolean valido = true;

        //Validar Usuario
        if (editUsuario.getText().toString().isEmpty()){
            editUsuario.setError(getString(R.string.error_input_value_empty));
            valido = false;
        } else if (!usuarioUnico(editUsuario.getText().toString())){
            //Nombre de usuario en uso!
            editUsuario.setError( getString(R.string.user) + " " + editUsuario.getText().toString() + " " + getString(R.string.error_user_existe));
            valido = false;
        }

        //Validar Correo
        if (editCorreo.getText().toString().isEmpty()){
            editCorreo.setError(getString(R.string.error_input_email));
            valido = false;
        } else {
            //Valida estructura String
            Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
            Matcher matcher = pattern.matcher(editCorreo.getText().toString());

            if (!matcher.matches()){
                //No valido!
                editCorreo.setError(getString(R.string.error_input_emailincorrect));
                valido = false;
            }
        }

        //Validar Contraseña
        if (editContraseña.getText().toString().isEmpty()){
            editContraseña.setError(getString(R.string.error_input_value_empty));
            valido = false;
        }

        //Validar Nombre
        if (editNombre.getText().toString().isEmpty()){
            editNombre.setError(getString(R.string.error_input_value_empty));
            valido = false;
        }

        //Validar Apedillos
        if (editApedillos.getText().toString().isEmpty()){
            editApedillos.setError(getString(R.string.error_input_value_empty));
            valido = false;
        }

        //Validar Dirección
        if (editDireccion.getText().toString().isEmpty()){
            editDireccion.setError(getString(R.string.error_input_value_empty));
            valido = false;
        }

        //Devolver resultado
        return valido;
    }

    private boolean usuarioUnico(String nuevoUser) {
        //Evalua usuario UNICO
        for (int i=0;i<listaUsuarios.size();i++){
            if (nuevoUser.equals(listaUsuarios.get(i))){
                //Coincide con otro usuario!
                return false;
            }
        }

        //Usuario unico
        return true;
    }

    //Proceder a registro
    private void registrarBBDD(){
        //Obtener valores
        String usuario = editUsuario.getText().toString();
        String correo = editCorreo.getText().toString();
        String nombre = editNombre.getText().toString();
        String apedillos = editApedillos.getText().toString();
        String direccion = editDireccion.getText().toString();

        //Creamos objeto usuario con sus valores
        Usuario nuevo = new Usuario(usuario,nombre,apedillos,correo,direccion);

        //Creamos clave del "Registro"
        String clave = nuevo.getUsuario();

        //Enviamos el objeto a la BBDD de FireBase
        bbdd.child(clave).setValue(nuevo);

        Toast.makeText(getApplicationContext(), getString(R.string.user) + nuevo.getUsuario() + " registed", Toast.LENGTH_LONG).show();

        //Añadimos info perfil
        user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(editUsuario.getText().toString())
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Se ha cambiado el nombre de pantalla
                        }
                    }
                });

        //Iniciamos actividad Login
        Intent login = new Intent(this, Login.class);
        startActivity(login);
        finish();
    }

    private void registroUser(String email, String password){
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Registro.this, getString(R.string.login_correct) + user.getUid(), Toast.LENGTH_SHORT).show();

                            //Procedemos a crear BBDD
                            registrarBBDD();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Registro.this, getString(R.string.error) + "\n" + task.getException(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //Eventos botones
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnRegCrear:
                //Valida primero los campos
                if (validarCampos()){
                    //Entonces vamos a intentar registrarlo!
                    registroUser(editCorreo.getText().toString(), editContraseña.getText().toString());
                }
                break;
            case R.id.btnRegIniciar:
                Intent nueva = new Intent(this, Login.class);
                startActivity(nueva);
                finish();
                break;
        }
    }
}
