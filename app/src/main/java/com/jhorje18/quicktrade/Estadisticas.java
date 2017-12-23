package com.jhorje18.quicktrade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jhorje18.quicktrade.model.Usuario;

import java.util.ArrayList;

public class Estadisticas extends AppCompatActivity {

    //Variables
    ListView vistaLista;
    ArrayList<String> listaUsuarios;

    DatabaseReference bbdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        //Vista
        vistaLista = (ListView) findViewById(R.id.listaMostrarUsuarios);

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

                adaptador = new ArrayAdapter<String>(Estadisticas.this,android.R.layout.simple_list_item_1,listado);
                vistaLista.setAdapter(adaptador);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
