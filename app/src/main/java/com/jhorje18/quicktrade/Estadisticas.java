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
import com.jhorje18.quicktrade.model.Producto;
import com.jhorje18.quicktrade.model.Usuario;

import java.util.ArrayList;

public class Estadisticas extends AppCompatActivity {

    //Variables
    ListView vistaListaUsers, vistaListaProducts;
    ArrayList<String> listaUsuarios, listaProductos;

    DatabaseReference bbddUser;
    DatabaseReference bbddProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        //Vista
        vistaListaUsers = (ListView) findViewById(R.id.listaMostrarUsuarios);
        vistaListaProducts = (ListView) findViewById(R.id.listaMostrarProductos);

        //Iniciamos ArrayList
        listaUsuarios = new ArrayList<String>();
        listaProductos = new ArrayList<String>();

        //Obtener BBDD FireBase
        bbddUser = FirebaseDatabase.getInstance().getReference("usuarios");
        bbddProduct = FirebaseDatabase.getInstance().getReference("productos");

        //Añadir evento al detectar nuevo valor en BBDD Usuarios
        bbddUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayAdapter<String> adaptador;
                ArrayList<String> listado = new ArrayList<String>();

                //Obtenemos nombres de usuario
                for (DataSnapshot datasnapshot : dataSnapshot.getChildren()) {
                    Usuario usuarioTEMP = datasnapshot.getValue(Usuario.class);
                    String userUsuario = usuarioTEMP.getUsuario();
                    listaUsuarios.add(userUsuario);
                    listado.add(userUsuario);
                }

                adaptador = new ArrayAdapter<String>(Estadisticas.this, android.R.layout.simple_list_item_1, listado);
                vistaListaUsers.setAdapter(adaptador);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Añadir evento al detectar nuevo valor en BBDD Productos
        bbddProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayAdapter<String> adaptador;
                ArrayList<String> listado = new ArrayList<String>();

                //Obtenemos nombres de productos
                for (DataSnapshot datasnapshot : dataSnapshot.getChildren()) {
                    Producto productoTEMP = datasnapshot.getValue(Producto.class);
                    String nameProducto = productoTEMP.getNombre();

                    listaProductos.add(nameProducto);
                    listado.add(nameProducto);
                }

                adaptador = new ArrayAdapter<String>(Estadisticas.this, android.R.layout.simple_list_item_1, listado);
                vistaListaProducts.setAdapter(adaptador);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
