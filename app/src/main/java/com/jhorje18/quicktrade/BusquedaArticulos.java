package com.jhorje18.quicktrade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jhorje18.quicktrade.model.AdaptadorProductos;
import com.jhorje18.quicktrade.model.Categoria;
import com.jhorje18.quicktrade.model.Producto;

import java.util.ArrayList;

public class BusquedaArticulos extends AppCompatActivity {

    //Variables
    Spinner spnCategorias;
    ListView vistaProductos;
    LinearLayout lyFiltros;

    ArrayList<String> listaCategorias, clavesProductos;
    ArrayList<Producto> listaProductos;
    DatabaseReference bbddProduct, bbddCategoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_articulos);

        //Vista
        spnCategorias = (Spinner) findViewById(R.id.spnCategoriasFiltros);
        lyFiltros = (LinearLayout) findViewById(R.id.layFiltros);
        vistaProductos = (ListView) findViewById(R.id.listProductBusqueda) ;

        //Iniciamos ArrayList
        listaCategorias = new ArrayList<String>();
        listaProductos = new ArrayList<Producto>();
        clavesProductos = new ArrayList<String>();

        //Obtenemos BBDD
        bbddProduct = FirebaseDatabase.getInstance().getReference("productos");
        bbddCategoria = FirebaseDatabase.getInstance().getReference("categorias");

        //Cargamos categorias
        bbddCategoria.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Creamos adaptador y eliminamos anteriores valores para evitar duplicarlos por accidente
                ArrayAdapter<String> adaptador;
                listaCategorias.clear();

                //Obtenemos nombres de productos
                for (DataSnapshot datasnapshot : dataSnapshot.getChildren()) {
                    Producto productoTEMP = datasnapshot.getValue(Producto.class);
                    String nameProducto = productoTEMP.getNombre();

                    Categoria categoriaTEMP = datasnapshot.getValue(Categoria.class);
                    String nameCategoria = categoriaTEMP.getNombre();

                    listaCategorias.add(nameCategoria);
                }

                adaptador = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,listaCategorias);
                spnCategorias.setAdapter(adaptador);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Si tiene que mostrar una categoria muestrala
        String categoriaRecibe = getIntent().getStringExtra("categoria");

        if (categoriaRecibe != null){
            //Aplicar categoria a mostrar
            cargarProductos(categoriaRecibe);
            //Buscamos categoria en el List
            spnCategorias.setSelection(posicionCategoria(categoriaRecibe));
        }
    }

    private int posicionCategoria(String categoriaRecibe) {
        for (int i=0; i<listaCategorias.size(); i++){
            Toast.makeText(this, "Comparando " + categoriaRecibe + " VS " + listaCategorias.get(i), Toast.LENGTH_SHORT).show();
            if (categoriaRecibe.equals(listaCategorias.get(i))){
                //Se ha encontrado!
                return i;
            }
        }
        return 0;
    }

    public void cargarProductos(String categoria){
        Query q =  bbddProduct.orderByChild("categoria").equalTo(categoria);

        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Carga Valores encontrados
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
                adaptador = new AdaptadorProductos(BusquedaArticulos.this, listaProductos);
                vistaProductos.setAdapter(adaptador);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnFiltrar:
                cargarProductos((String) spnCategorias.getSelectedItem());
                break;
        }
    }
}
