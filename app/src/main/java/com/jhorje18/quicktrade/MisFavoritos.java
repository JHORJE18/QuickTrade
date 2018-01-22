package com.jhorje18.quicktrade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jhorje18.quicktrade.model.AdaptadorProductos;
import com.jhorje18.quicktrade.model.Producto;

import java.util.ArrayList;

public class MisFavoritos extends AppCompatActivity {

    //Variables
    ListView listadoView;
    ArrayList<String> listaFavs;

    DatabaseReference refFavoritos, refProducto;
    Producto productoCargando;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_favoritos);

        //Vista
        listadoView = (ListView) findViewById(R.id.listadoFavoritos);

        //Obtener usuario actual
        user = FirebaseAuth.getInstance().getCurrentUser();

        //Obtener BBDD Favoritos
        refFavoritos = FirebaseDatabase.getInstance().getReference("favoritos/" + user.getDisplayName());

        //Iniciamos Arrays
        listaFavs = new ArrayList<String>();

        //Consultar favoritos
        refFavoritos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayAdapter<String> adaptador;
                listaFavs.clear();

                //Obtenemos productos favoritos
                for (DataSnapshot datasnapshot : dataSnapshot.getChildren()){
                    listaFavs.add((String) datasnapshot.getValue());
                }

                //Adaptador personalizado
                adaptador = new ArrayAdapter<String>(MisFavoritos.this, android.R.layout.simple_list_item_1, listaFavs);
                listadoView.setAdapter(adaptador);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String cargarProductos(String claveProducto) {
        //Obtener producto!
        //TODO Obtener productos para mostrarlos en la lista

        return null;
    }
}
