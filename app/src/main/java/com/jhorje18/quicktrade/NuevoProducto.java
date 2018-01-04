package com.jhorje18.quicktrade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jhorje18.quicktrade.model.Categoria;
import com.jhorje18.quicktrade.model.Producto;

import java.util.ArrayList;

public class NuevoProducto extends AppCompatActivity {

    //Variables
    Spinner spnCategorias;
    EditText editNombre, editDescripción, editPrecio;

    ArrayList<String> listaCategorias;
    DatabaseReference bbddCategorias;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_producto);

        //Vista
        spnCategorias = (Spinner) findViewById(R.id.spnNuevoProductCategorias);
        editNombre = (EditText) findViewById(R.id.editNuevoProductNombre);
        editDescripción = (EditText) findViewById(R.id.editNuevoProductDescripcion);
        editPrecio = (EditText) findViewById(R.id.editNuevoProductPrecio);

        //Iniciamos ArrayList
        listaCategorias = new ArrayList<String>();

        //Obtenemos BBDD Firebase
        bbddCategorias = FirebaseDatabase.getInstance().getReference("categorias");

        //Listado categorias
        bbddCategorias.addValueEventListener(new ValueEventListener() {
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
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnNuevoProductoGuardar:
                //TODO Validar datos y proceder a guardar producto
                if (validarDatos()){
                    //TODO Proceder a guardar producto

                }
                break;
            case R.id.btnNuevoProductoCancelar:
                finish();
                break;
        }
    }

    private void guardarProducto(){

    }

    private boolean validarDatos(){
        //Validar Nombre
        boolean valido = true;

        if (editNombre.getText().toString().isEmpty()){
            editNombre.setError("El nombre del producto es obligatorio");
            valido = false;
        }

        //Validar Descripción
        if (editDescripción.getText().toString().isEmpty()){
            editDescripción.setError("Es obligatoria la descripción del producto");
            valido = false;
        }

        //Validar precio
        if (editPrecio.getText().toString().isEmpty()){
            editPrecio.setError("Has de especificar un precio del producto");
            valido = false;
        }

        return valido;
    }
}
