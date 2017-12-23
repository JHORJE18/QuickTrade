package com.jhorje18.quicktrade;

import android.content.Intent;
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

    FirebaseUser user;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = (TextView) findViewById(R.id.txtPrincipal);

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
                FirebaseAuth.getInstance().signOut();
                recargar();
                break;
            case R.id.mnEstadisticas:
                startActivity(new Intent(this, Estadisticas.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
