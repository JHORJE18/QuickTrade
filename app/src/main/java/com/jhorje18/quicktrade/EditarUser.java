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
import android.widget.Toast;

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
    Spinner spin_users;
    ArrayList<String> listaUsuarios;
    ArrayList<Usuario> infoUsuarios;
    ImageButton imgReload;
    EditText editCorreo, editNombre, editApedillos, editDireccion;

    DatabaseReference bbdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_user);

        //Vista
        spin_users = (Spinner) findViewById(R.id.listaEditar);
        imgReload = (ImageButton) findViewById(R.id.btnEditReload);
        editCorreo = (EditText) findViewById(R.id.editEditCorreo);
        editNombre = (EditText) findViewById(R.id.editEditNombre);
        editApedillos = (EditText) findViewById(R.id.editEditApedillos);
        editDireccion = (EditText) findViewById(R.id.editEditDireccion);

        //Iniciamos ArrayLists
        listaUsuarios = new ArrayList<String>();
        infoUsuarios = new ArrayList<Usuario>();

        //Obtener BBDD FireBase
        bbdd = FirebaseDatabase.getInstance().getReference("usuarios");

        //Añadir evento al detectar nuevo valor en BBDD
        bbdd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayAdapter<String> adaptador;

                //Obtenemos nombres de usuario
                for(DataSnapshot datasnapshot: dataSnapshot.getChildren()){
                    //Obtenemos el usuario
                    Usuario usuarioTEMP = datasnapshot.getValue(Usuario.class);

                    //Guardamos nombre de usuario
                    String userUsuario = usuarioTEMP.getUsuario();
                    listaUsuarios.add(userUsuario);

                    //Guardamos info del usuario
                    infoUsuarios.add(usuarioTEMP);
                }

                adaptador = new ArrayAdapter<String>(EditarUser.this,android.R.layout.simple_list_item_1,listaUsuarios);
                spin_users.setAdapter(adaptador);
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
                    Query q = bbdd.orderByChild("usuario").equalTo((String) spin_users.getSelectedItem());

                    //Si ha encontrado algun registro unico
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                String clave = dataSnapshot1.getKey();

                                //Editamos todos los campos
                                bbdd.child(clave).child("correo").setValue(editCorreo.getText().toString());
                                bbdd.child(clave).child("nombre").setValue(editNombre.getText().toString());
                                bbdd.child(clave).child("apedillos").setValue(editApedillos.getText().toString());
                                bbdd.child(clave).child("direccion").setValue(editDireccion.getText().toString());
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                break;
            case R.id.btnEditEliminar:

                break;
            case R.id.btnEditReload:
                cargarUsuario(infoUsuarios.get(spin_users.getSelectedItemPosition()));
                break;
        }
    }

    //Cargar datos del usuario
    private void cargarUsuario(Usuario user){
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
