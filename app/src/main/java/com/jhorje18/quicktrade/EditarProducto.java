package com.jhorje18.quicktrade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jhorje18.quicktrade.model.Categoria;
import com.jhorje18.quicktrade.model.Producto;

import java.util.ArrayList;

public class EditarProducto extends AppCompatActivity {

    //Variables
    EditText editNombre, editDescripcion, editPrecio;
    Spinner spnCategorias;
    ProgressBar progressBar;

    Producto actualProducto;
    ArrayList<String> listaCategorias;
    String claveProducto;
    DatabaseReference refProducto, bbddCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_producto);

        //Vista
        editNombre = (EditText) findViewById(R.id.editEditProductNombre);
        editDescripcion = (EditText) findViewById(R.id.editEditProductDescripcion);
        editPrecio = (EditText) findViewById(R.id.editEditProductPrecio);
        spnCategorias = (Spinner) findViewById(R.id.spnEditProductCategoria);
        progressBar = (ProgressBar) findViewById(R.id.progresEditProduct);

        //Obtiene clave del producto
        claveProducto = getIntent().getStringExtra("clave");

        //Iniciamos arrayList
        listaCategorias = new ArrayList<String>();

        //Cargamos producto
        refProducto = FirebaseDatabase.getInstance().getReference("productos/" + claveProducto);
        refProducto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                actualProducto = dataSnapshot.getValue(Producto.class);
                progressBar.setVisibility(View.GONE);

                recargarVista();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        //Cargamos categorias
        bbddCategorias = FirebaseDatabase.getInstance().getReference("categorias");
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

    private void recargarVista() {
        //Recargamos elementos vista
        if (actualProducto != null){
            editNombre.setText(actualProducto.getNombre());
            editDescripcion.setText(actualProducto.getDescripcion());
            editPrecio.setText(actualProducto.getPrecio());
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnEditProductGuardar:
                Toast.makeText(this, "Guardando", Toast.LENGTH_SHORT).show();
                if (validarDatos()){
                    //Procedemos a editar producto

                    //Nombre
                    if (!editNombre.getText().toString().equals(actualProducto.getNombre())){
                        cambiarValor("nombre",editNombre.getText().toString());
                        Toast.makeText(this, getString(R.string.edit_name_changed) + " " + editNombre.getText().toString(), Toast.LENGTH_SHORT).show();
                    }

                    //Descripción
                    if (!editDescripcion.getText().toString().equals(actualProducto.getDescripcion())){
                        cambiarValor("descripcion",editDescripcion.getText().toString());
                        Toast.makeText(this, getString(R.string.edit_description_changed) + " " + editDescripcion.getText().toString(), Toast.LENGTH_SHORT).show();
                    }

                    //Precio
                    if (!editPrecio.getText().toString().equals(actualProducto.getPrecio())){
                        cambiarValor("precio",editPrecio.getText().toString());
                        Toast.makeText(this, getString(R.string.edit_price_changed) + " " + editPrecio.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void cambiarValor(String campo, String valor) {
        //Procedemos a cambiar el valor
        refProducto.child(campo).setValue(valor);
    }

    //Validacion de datos
    private boolean validarDatos() {
        //Validar Nombre
        boolean valido = true;

        if (editNombre.getText().toString().isEmpty()){
            editNombre.setError(getString(R.string.error_input_nameproduct));
            valido = false;
        }

        //Validar Descripción
        if (editDescripcion.getText().toString().isEmpty()){
            editDescripcion.setError(getString(R.string.error_input_descriptionproduct));
            valido = false;
        }

        //Validar precio
        if (editPrecio.getText().toString().isEmpty()){
            editPrecio.setError(getString(R.string.error_input_precioproduct));
            valido = false;
        }

        return valido;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_nuevo,menu);

        menu.removeItem(R.id.mnEdit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mnCancel:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
