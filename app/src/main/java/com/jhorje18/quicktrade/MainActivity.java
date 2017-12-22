package com.jhorje18.quicktrade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jhorje18.quicktrade.model.Usuario;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        }
        return super.onOptionsItemSelected(item);
    }
}
