package com.jhorje18.quicktrade;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Perfil extends AppCompatActivity {

    //Variables
    TextView txtUsuario;

    FirebaseUser user;
    AlertDialog.Builder dialogoEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        //Vista
        txtUsuario = (TextView) findViewById(R.id.txtPerfilUsuario);

        //Creamos dialogo eliminar cuenta
        dialogoEliminar = new AlertDialog.Builder(this);
        dialogoEliminar.setIcon(getDrawable(R.drawable.alert_icon))
                .setTitle("Eliminar perfil")
                .setMessage("¿Seguro que quieres eliminar tu cuenta?")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        eliminarCuenta();
                        Intent confirmaEliminar = new Intent(Perfil.this,Confirmar.class);
                        confirmaEliminar.putExtra("eliminar",true);
                        startActivity(confirmaEliminar);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(Perfil.this, "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                });

        //Obtenemos info user actual
        user = FirebaseAuth.getInstance().getCurrentUser();

        //Recargamos vista
        recargar();
    }

    private void recargar() {
        txtUsuario.setText(user.getDisplayName());
    }

    //Menu opciones superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_perfil, menu);

        return true;
    }

    //Eventos botones menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mnEdit:
                break;
            case R.id.mnEliminar:
                dialogoEliminar.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void eliminarCuenta(){

    }
}
