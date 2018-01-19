package com.jhorje18.quicktrade;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jhorje18.quicktrade.model.Producto;
import com.jhorje18.quicktrade.model.Usuario;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ProductoView extends AppCompatActivity {

    //Variables
    TextView txtNombre, txtUser, txtDescripcion, txtCategoria, txtPrecio;
    ProgressBar progressBar, progressIMG;
    ImageView imgView;
    String claveProducto;

    FirebaseUser user;
    Producto actualProducto;
    DatabaseReference refProducto, bbddUsers;
    StorageReference imagenesRef;
    AlertDialog.Builder dialogoEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_view);

        //Obtiene Intent del producto a mostrar
        claveProducto = getIntent().getStringExtra("clave");

        //Vista
        txtNombre = (TextView) findViewById(R.id.txtProductNombre);
        txtUser = (TextView) findViewById(R.id.txtProductUser);
        txtDescripcion = (TextView) findViewById(R.id.txtProductDescrpcion);
        txtCategoria = (TextView) findViewById(R.id.txtProductCategoria);
        txtPrecio = (TextView) findViewById(R.id.txtProductPrecio);
        progressBar = (ProgressBar) findViewById(R.id.progressProductoLoad);
        imgView = (ImageView) findViewById(R.id.imgProductView);
        progressIMG = (ProgressBar) findViewById(R.id.progressIMGView);

        //Si no ha recibido nada sacalo de esta pantalla
        if (claveProducto.isEmpty()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        //Obtener usuario actual
        user = FirebaseAuth.getInstance().getCurrentUser();
        bbddUsers = FirebaseDatabase.getInstance().getReference("/usuarios");

        //Obtener conexion almacenamiento
        imagenesRef = FirebaseStorage.getInstance().getReference("/imagenes/productos");

        //Obtener producto!
        refProducto = FirebaseDatabase.getInstance().getReference("productos/" + claveProducto);
        refProducto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                actualProducto = dataSnapshot.getValue(Producto.class);
                progressBar.setVisibility(View.GONE);
                cargarImagen();
                recargarVista();
                invalidateOptionsMenu();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        //Creamos dialog eliminar producto
        dialogoEliminar = new AlertDialog.Builder(this);
        dialogoEliminar.setIcon(getDrawable(R.drawable.alert_icon))
                .setTitle(getString(R.string.delete_product))
                .setMessage(getString(R.string.question_delete_product))
                .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Elimina producto actual
                        Toast.makeText(ProductoView.this, getString(R.string.deleted_products), Toast.LENGTH_SHORT).show();
                        refProducto.removeValue();
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {Toast.makeText(ProductoView.this, getString(R.string.cancel), Toast.LENGTH_SHORT).show();
                    }
                });

        //Evento mostrar categorias
        txtCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cargarCategoria = new Intent(ProductoView.this, BusquedaArticulos.class);
                String categoriaEnviar = txtCategoria.getText().toString().replace("#","");
                cargarCategoria.putExtra("categoria", categoriaEnviar);
                startActivity(cargarCategoria);
            }
        });

        //Evento mostrar usuario
        txtUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query qUser = bbddUsers.orderByChild("usuario").equalTo(txtUser.getText().toString());
                qUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Intent cargaUser = new Intent(ProductoView.this,Perfil.class);
                        cargaUser.putExtra("clave", dataSnapshot.getKey());
                        startActivity(cargaUser);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    //Carga de imagenes
    private void cargarImagen() {
        StorageReference imgRefProduct = imagenesRef.child(claveProducto + ".jpg");

        //Cargar imagen usando Glide
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(imgRefProduct)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        //Si no carga fichero oculta imagen
                        imgView.setVisibility(View.GONE);
                        progressIMG.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        imgView.setVisibility(View.VISIBLE);
                        progressIMG.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imgView);

        imgView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Ocultar elementos si no es su producto
        if (!user.getDisplayName().equals(actualProducto.getUsuario())){
            menu.removeItem(R.id.mnEliminar);
            menu.removeItem(R.id.mnEdit);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_perfil, menu);

        menu.removeItem(R.id.mnNuevoProducto);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mnEliminar:
                dialogoEliminar.show();
                break;
            case R.id.mnEdit:
                //Procede a editar articulo con la clave del producto
                Intent ediarProduct = new Intent(ProductoView.this,EditarProducto.class);
                ediarProduct.putExtra("clave",claveProducto);
                startActivity(ediarProduct);
                break;
        }
        return super.onOptionsItemSelected(item);
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
