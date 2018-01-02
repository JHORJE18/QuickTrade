package com.jhorje18.quicktrade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class NuevoProducto extends AppCompatActivity {

    //Variables
    Spinner spnCategorias;
    EditText editNombre, editDescripción, editPrecio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_producto);

        //Vista
        spnCategorias = (Spinner) findViewById(R.id.spnNuevoProductCategorias);
        editNombre = (EditText) findViewById(R.id.editNuevoProductNombre);
        editDescripción = (EditText) findViewById(R.id.editNuevoProductDescripcion);
        editPrecio = (EditText) findViewById(R.id.editNuevoProductPrecio);

        //TODO Cargar lista categorias
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnNuevoProductoGuardar:
                //TODO Validar datos y proceder a guardar producto
                break;
            case R.id.btnNuevoProductoCancelar:
                finish();
                break;
        }
    }

    private void guardarProducto(){

    }
}
