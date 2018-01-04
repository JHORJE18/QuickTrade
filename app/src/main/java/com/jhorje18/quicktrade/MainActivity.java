package com.jhorje18.quicktrade;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jhorje18.quicktrade.model.AdaptadorProductos;
import com.jhorje18.quicktrade.model.Producto;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Variables
    TextView txt;
    ListView vistaProductos;
    ArrayList<Producto> listaProductos;
    ArrayList<String> clavesProductos;

    FirebaseUser user;
    DatabaseReference bbddProductos;
    AlertDialog.Builder dialogoCerrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Vista
        txt = (TextView) findViewById(R.id.txtPrincipal);
        vistaProductos = (ListView) findViewById(R.id.listPrincipal);

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

        //Iniciamos ArrayList
        listaProductos = new ArrayList<Producto>();
        clavesProductos = new ArrayList<String>();

        //Obtenemos BBDD Productos
        bbddProductos = FirebaseDatabase.getInstance().getReference("productos");

        //Cargar listado productos
        bbddProductos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                AdaptadorProductos adaptador;
                listaProductos.clear();
                clavesProductos.clear();

                //Obtenemos nombres de productos
                for (DataSnapshot datasnapshot : dataSnapshot.getChildren()) {
                    Producto productoTEMP = datasnapshot.getValue(Producto.class);

                    listaProductos.add(productoTEMP);
                    clavesProductos.add(datasnapshot.getKey());
                }

                //Adaptador personalizado
                adaptador = new AdaptadorProductos(MainActivity.this, listaProductos);
                vistaProductos.setAdapter(adaptador);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
