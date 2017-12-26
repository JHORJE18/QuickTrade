package com.jhorje18.quicktrade;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jhorje18.quicktrade.model.Producto;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ProductoView extends AppCompatActivity {

    //Variables
    TextView txtNombre, txtUser, txtDescripcion, txtCategoria, txtPrecio;

    Producto actualProducto;
    DatabaseReference refProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_view);

        //Obtiene Intent del producto a mostrar
        String claveProducto = getIntent().getStringExtra("clave");

        //Vista
        txtNombre = (TextView) findViewById(R.id.txtProductNombre);
        txtUser = (TextView) findViewById(R.id.txtProductUser);
        txtDescripcion = (TextView) findViewById(R.id.txtProductDescrpcion);
        txtCategoria = (TextView) findViewById(R.id.txtProductCategoria);
        txtPrecio = (TextView) findViewById(R.id.txtProductPrecio);

        //Si no ha recibido nada sacalo de esta pantalla
        if (claveProducto.isEmpty()){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }

        //Obtener producto!
        refProducto = FirebaseDatabase.getInstance().getReference("productos/" + claveProducto);
        refProducto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                actualProducto = dataSnapshot.getValue(Producto.class);
                recargarVista();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    //Mostrar datos en pantalla
    private void recargarVista() {
        txtNombre.setText(actualProducto.getNombre());
        txtUser.setText("@" + actualProducto.getUsuario());
        txtPrecio.setText(actualProducto.getPrecio());
        txtCategoria.setText("#" + actualProducto.getCategoria());
        txtDescripcion.setText(actualProducto.getDescripcion());
    }
}
