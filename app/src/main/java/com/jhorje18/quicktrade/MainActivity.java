package com.jhorje18.quicktrade;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    //Variables
    FirebaseUser user;
    TextView txt;
    AlertDialog.Builder dialogoCerrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Vista
        txt = (TextView) findViewById(R.id.txtPrincipal);

        //Creamos dialogo cerrar sesión
        dialogoCerrar = new AlertDialog.Builder(this);
        dialogoCerrar.setIcon(getDrawable(R.drawable.alert_icon))
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que quieres cerrar sesión?")
                .setPositiveButton("Cerrar sesión", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseAuth.getInstance().signOut();
                        recargar();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                });

        recargar();
    }

    @Override
    protected void onResume() {
        recargar();
        super.onResume();
    }

    private void recargar() {

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            txt.setText("Hola " + user.getDisplayName());
        } else {
            //si no esta Logueado, llevale a que inicie sesión
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }

    //Crear menu opciones superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_opciones, menu);

        return true;
    }

    //Eventos botones menu opciones superior
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mnPerfil:
                startActivity(new Intent(this, Perfil.class));
                break;
            case R.id.mnCerrar:
                dialogoCerrar.show();
                break;
            case R.id.mnEstadisticas:
                startActivity(new Intent(this, Estadisticas.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
