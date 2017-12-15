package com.jhorje18.quicktrade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jhorje18.quicktrade.model.Usuario;

import java.util.zip.Inflater;

public class NuevoUser extends AppCompatActivity {

    //Variables
    EditText editUsuario, editCorreo, editNombre, editApedillos, editDireccion;

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

        //Obtener BBDD FireBase
        bbdd = FirebaseDatabase.getInstance().getReference("usuarios");
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

        //Creamos objeto usuario con sus valores
        Usuario nuevo = new Usuario(usuario,nombre,apedillos,correo,direccion);

        //Creamos clave del "Registro"
        String clave = nuevo.getUsuario();

        //Enviamos el objeto a la BBDD de FireBase
        bbdd.child(clave).setValue(nuevo);

        Toast.makeText(getApplicationContext(), "Usuario " + nuevo.getUsuario() + " registrado", Toast.LENGTH_LONG).show();
    }
}
