package com.jhorje18.quicktrade;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jhorje18.quicktrade.model.Usuario;

import java.util.ArrayList;

public class EditarUser extends AppCompatActivity {

    //Variables
    ProgressBar progressBar;
    TextView txtUsuario;
    EditText editCorreo, editNombre, editApedillos, editDireccion;

    Usuario usuarioEdit;
    String claveUsuario;
    FirebaseUser user;
    DatabaseReference bbddUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_user);

        //Vista
        txtUsuario = (TextView) findViewById(R.id.txtEditUsuario);
        editCorreo = (EditText) findViewById(R.id.editEditCorreo);
        editNombre = (EditText) findViewById(R.id.editEditNombre);
        editApedillos = (EditText) findViewById(R.id.editEditApedillos);
        editDireccion = (EditText) findViewById(R.id.editEditDireccion);
        progressBar = (ProgressBar) findViewById(R.id.progressEditLoad);

        //Obtener usuario actual
        user = FirebaseAuth.getInstance().getCurrentUser();

        //Obtener BBDD FireBase
        bbddUser = FirebaseDatabase.getInstance().getReference("usuarios");

        //Buscar al usuario de la sesión actual
        Query q = bbddUser.orderByChild("usuario").equalTo((String) user.getDisplayName());
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datasnapshot: dataSnapshot.getChildren()){
                    usuarioEdit = datasnapshot.getValue(Usuario.class);
                    claveUsuario = datasnapshot.getKey();
                    progressBar.setVisibility(View.GONE);
                }

                //Cargamos datos usuario actual
                cargarUsuario(usuarioEdit);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Eventos botones
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnEditEditar:
                if (validarCampos()){
                    Log.i("#FUNCION","Procediendo a editar campos");
                    //Comprobamos que datos han sido cambiados

                    //Nombre
                    if (!editNombre.getText().toString().equals(usuarioEdit.getNombre())){
                        Log.i("#FUNCION","El nombre es diferente");
                        cambiarValor("nombre",editNombre.getText().toString());
                        Toast.makeText(this, "Nombre cambiado a " + editNombre.getText().toString(), Toast.LENGTH_SHORT).show();
                    }

                    //Apedillos
                    if (!editApedillos.getText().toString().equals(usuarioEdit.getApedillos())){
                        Log.i("#FUNCION","Los apedillos son diferentes");
                        cambiarValor("apedillos",editApedillos.getText().toString());
                        Toast.makeText(this, "Apedillos cambiados a " + editApedillos.getText().toString(), Toast.LENGTH_SHORT).show();
                    }

                    //Dirección
                    if (!editDireccion.getText().toString().equals(usuarioEdit.getDireccion())){
                        Log.i("#FUNCION","La dirección es diferente");
                        cambiarValor("direccion",editDireccion.getText().toString());
                        Toast.makeText(this, "Dirección cambiada a " + editDireccion.getText().toString(), Toast.LENGTH_SHORT).show();
                    }

                    //Correo electrónico
                    if (!editCorreo.getText().toString().equals(usuarioEdit.getCorreo())){
                        Log.i("#FUNCION","El correo es diferente");

                    }
                }
                break;
        }
    }

    private void cambiarValor(String campo, String valor) {
        //Procedemos a cambiar el valor
        bbddUser.child(claveUsuario).child(campo).setValue(valor);
    }

    //Cargar datos del usuario
    private void cargarUsuario(Usuario user){
        txtUsuario.setText("@" + user.getUsuario());
        editCorreo.setText(user.getCorreo());
        editNombre.setText(user.getNombre());
        editApedillos.setText(user.getApedillos());
        editDireccion.setText(user.getDireccion());

        Toast.makeText(this, "Datos de " + user.getUsuario() + " cargados correctamente", Toast.LENGTH_SHORT).show();
    }

    //Validar campos introducidos
    private boolean validarCampos(){
        //Evalua campos no vacios
        if (editNombre.getText().toString().isEmpty() || editApedillos.getText().toString().isEmpty() || editCorreo.getText().toString().isEmpty() || editDireccion.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),"No puede haber campos vacios!",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
