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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jhorje18.quicktrade.model.Usuario;

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
            Toast.makeText(this, "Usuario detectado! " + user.getEmail(), Toast.LENGTH_SHORT).show();
            txt.setText("Hola " + user.getEmail());
        } else {
            Toast.makeText(this, "Ningun usuario", Toast.LENGTH_SHORT).show();
            txt.setText("Nadie detectado!");
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
            case R.id.mnNuevoUser:
                Intent nueva = new Intent(this, NuevoUser.class);
                startActivity(nueva);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                break;
            case R.id.mnMostrarUser:
                Intent mostrar = new Intent(this, MostrarUsers.class);
                startActivity(mostrar);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                break;
            case R.id.mnLogin:
                Intent login = new Intent(this, Login.class);
                startActivity(login);
                break;
            case R.id.mnCerrar:
                FirebaseAuth.getInstance().signOut();
                recargar();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
