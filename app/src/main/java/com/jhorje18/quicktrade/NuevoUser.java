package com.jhorje18.quicktrade;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jhorje18.quicktrade.model.Usuario;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class NuevoUser extends AppCompatActivity {

    //Variables
    EditText editUsuario, editCorreo, editNombre, editApedillos, editDireccion;
    Spinner spin_users;
    ArrayList<String> listaUsuarios;

    DatabaseReference bbdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_user);

        //Conectar vista - controlador
        editUsuario = (EditText) findViewById(R.id.editNuevoUsuario);
        editCorreo = (EditText) findViewById(R.id.editNuevoCorreo);
        editNombre = (EditText) findViewById(R.id.editNuevoNombre);
        editApedillos = (EditText) findViewById(R.id.editNuevoApedillos);
        editDireccion = (EditText) findViewById(R.id.editNuevoDireccion);
        spin_users = (Spinner) findViewById(R.id.lista);

        listaUsuarios = new ArrayList<String>();

        //Obtener BBDD FireBase
        bbdd = FirebaseDatabase.getInstance().getReference("usuarios");

        //Añadir evento al detectar nuevo valor en BBDD
        bbdd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayAdapter<String> adaptador;
                ArrayList<String> listado = new ArrayList<String>();

                //Obtenemos nombres de usuario
                for(DataSnapshot datasnapshot: dataSnapshot.getChildren()){
                    Usuario usuarioTEMP = datasnapshot.getValue(Usuario.class);
                    String userUsuario = usuarioTEMP.getUsuario();
                    listaUsuarios.add(userUsuario);
                    listado.add(userUsuario);
                }

                adaptador = new ArrayAdapter<String>(NuevoUser.this,android.R.layout.simple_list_item_1,listado);
                spin_users.setAdapter(adaptador);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnNuevoAñadir:
                Toast.makeText(getApplicationContext(), "Guardando nuevo usuario...", Toast.LENGTH_SHORT).show();

                //Procedemos a guardar nuevo usuario
                guardarNuevoUser();
                break;
        }
    }

    //Menu opciones para guardar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_nuevo, menu);

        return true;
    }

    //Gestion eventos botones menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mnSave:
                Toast.makeText(getApplicationContext(),"Guardando nuevo usuario...",Toast.LENGTH_SHORT).show();

                //Procedemos a guardar nuevo usuario
                guardarNuevoUser();
                break;
            case R.id.mnCancel:
                Toast.makeText(getApplicationContext(), "Cancelando...", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.mmEdit:
                Intent nueva = new Intent(this, EditarUser.class);
                startActivity(nueva);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Metodo guardar nuevo usuario
    private void guardarNuevoUser() {
        //Obtener valores
        String usuario = editUsuario.getText().toString();
        String correo = editCorreo.getText().toString();
        String nombre = editNombre.getText().toString();
        String apedillos = editApedillos.getText().toString();
        String direccion = editDireccion.getText().toString();

        //Metodo validar campos
        if (validarDatos()){
            //Creamos objeto usuario con sus valores
            Usuario nuevo = new Usuario(usuario,nombre,apedillos,correo,direccion);

            //Creamos clave del "Registro"
            String clave = nuevo.getUsuario();

            //Enviamos el objeto a la BBDD de FireBase
            bbdd.child(clave).setValue(nuevo);

            Toast.makeText(getApplicationContext(), "Usuario " + nuevo.getUsuario() + " registrado", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validarDatos() {

        //Evalua campos no vacios
        if (editUsuario.getText().toString().isEmpty() || editNombre.getText().toString().isEmpty() || editApedillos.getText().toString().isEmpty() || editCorreo.getText().toString().isEmpty() || editDireccion.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),"Todos los campos son obligatorios",Toast.LENGTH_SHORT).show();
            return false;
        }

        //Evalua usuario UNICO
        for (int i=0;i<listaUsuarios.size();i++){
            if (editUsuario.getText().toString().equals(listaUsuarios.get(i))){
                Snackbar.make(getCurrentFocus(),"Usuario " + editUsuario.getText().toString() + " ya existe", Snackbar.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Ya existe el usuario " + listaUsuarios.get(i) , Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }
}
