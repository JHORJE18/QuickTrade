package com.jhorje18.quicktrade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jhorje18.quicktrade.model.AdaptadorProductos;
import com.jhorje18.quicktrade.model.Producto;
import com.jhorje18.quicktrade.model.Usuario;

import java.util.ArrayList;

public class Estadisticas extends AppCompatActivity {

    //Variables
    ListView vistaListaUsers, vistaListaProducts;
    ArrayList<String> listaUsuarios, clavesUsuarios, clavesProductos;
    ArrayList<Producto> listaProductos;
    TextView txtUsuarios, txtProductos;
    ProgressBar progressBar;

    DatabaseReference bbddUser;
    DatabaseReference bbddProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        //Vista
        vistaListaUsers = (ListView) findViewById(R.id.listaMostrarUsuarios);
        vistaListaProducts = (ListView) findViewById(R.id.listaMostrarProductos);
        txtUsuarios = (TextView) findViewById(R.id.txtEstUsers);
        txtProductos = (TextView) findViewById(R.id.txtEsProductos);
        progressBar = (ProgressBar) findViewById(R.id.progressEstadisticasLoad);

        //Iniciamos ArrayList
        listaUsuarios = new ArrayList<String>();
        listaProductos = new ArrayList<Producto>();
        clavesProductos = new ArrayList<String>();
        clavesUsuarios = new ArrayList<String>();

        //Obtener BBDD FireBase
        bbddUser = FirebaseDatabase.getInstance().getReference("usuarios");
        bbddProduct = FirebaseDatabase.getInstance().getReference("productos");

        //Añadir evento al detectar nuevo valor en BBDD Usuarios
        bbddUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayAdapter<String> adaptador;
                listaUsuarios.clear();
                clavesUsuarios.clear();

                //Obtenemos nombres de usuario
                for (DataSnapshot datasnapshot : dataSnapshot.getChildren()) {
                    Usuario usuarioTEMP = datasnapshot.getValue(Usuario.class);
                    String userUsuario = usuarioTEMP.getUsuario();

                    listaUsuarios.add(userUsuario);
                    clavesUsuarios.add(datasnapshot.getKey());
                    progressBar.setVisibility(View.GONE);
                }

                adaptador = new ArrayAdapter<String>(Estadisticas.this, android.R.layout.simple_list_item_1, listaUsuarios);
                vistaListaUsers.setAdapter(adaptador);

                txtUsuarios.setText(listaUsuarios.size() + getString(R.string.count_users));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Añadir evento al detectar nuevo valor en BBDD Productos
        bbddProduct.addValueEventListener(new ValueEventListener() {
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
                    progressBar.setVisibility(View.GONE);
                }

                //Adaptador personalizado
                adaptador = new AdaptadorProductos(Estadisticas.this, listaProductos);
                vistaListaProducts.setAdapter(adaptador);

                txtProductos.setText(listaProductos.size() + getString(R.string.count_products));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Evento clicks listas
        vistaListaUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Iniciamos Mostrar producto con su clave
                Intent mostrar = new Intent(Estadisticas.this,Perfil.class);
                mostrar.putExtra("clave",clavesUsuarios.get(position));
                startActivity(mostrar);
            }
        });
        vistaListaProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Iniciamos Mostrar producto con su clave
                Intent mostrar = new Intent(Estadisticas.this,ProductoView.class);
                mostrar.putExtra("clave",clavesProductos.get(position));
                startActivity(mostrar);
            }
        });
    }
}
