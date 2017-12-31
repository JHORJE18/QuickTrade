package com.jhorje18.quicktrade;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
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
    Usuario usuarioEdit;
    TextView txtUsuario;
    EditText editCorreo, editNombre, editApedillos, editDireccion;

    FirebaseUser user;
    DatabaseReference bbdd;

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

        //Obtener usuario actual
        user = FirebaseAuth.getInstance().getCurrentUser();

        //Obtener BBDD FireBase
        bbdd = FirebaseDatabase.getInstance().getReference("usuarios");

        //Buscar al usuario de la sesión actual
        Query q = bbdd.orderByChild("usuario").equalTo((String) user.getDisplayName());
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datasnapshot: dataSnapshot.getChildren()){
                    usuarioEdit = datasnapshot.getValue(Usuario.class);
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
                    //Procedemos para editar
                    Query q = bbdd.orderByChild("usuario").equalTo((String) txtUsuario.getText().toString());

                    //Si ha encontrado algun registro unico
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                String clave = dataSnapshot1.getKey();

                                //Editamos todos los campos
                                //TODO Modificar datos no funciona
                                bbdd.child(clave).child("correo").setValue(editCorreo.getText().toString());
                                bbdd.child(clave).child("nombre").setValue(editNombre.getText().toString());
                                bbdd.child(clave).child("apedillos").setValue(editApedillos.getText().toString());
                                bbdd.child(clave).child("direccion").setValue(editDireccion.getText().toString());

                                Toast.makeText(EditarUser.this, "Valores editados correctamente", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                break;
        }
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
