package com.jhorje18.quicktrade;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jhorje18.quicktrade.model.Categoria;
import com.jhorje18.quicktrade.model.Producto;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class EditarProducto extends AppCompatActivity {

    //Variables
    EditText editNombre, editDescripcion, editPrecio;
    Spinner spnCategorias;
    ImageView imgView;
    ProgressBar progressBar;

    Producto actualProducto;
    Bitmap imagenBTMP;
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
        imgView = (ImageView) findViewById(R.id.imgEditProducto);
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

                cargarImagen();
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

    //Carga de imagenes
    private void cargarImagen() {
        //TODO Comprobar que existe fichero

        StorageReference imagenesRef = FirebaseStorage.getInstance().getReference("/imagenes/productos/" + claveProducto + ".jpg");

        //Comprueba si existe imagen
        Log.d("#VARIABLE",imagenesRef.getPath());

        //Cargar imagen usando Glide
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(imagenesRef)
                .into(imgView);

        imgView.setVisibility(View.VISIBLE);
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

                    //Categoria
                    if (!((String) spnCategorias.getSelectedItem()).equals(actualProducto.getCategoria())){
                        cambiarValor("categoria",(String) spnCategorias.getSelectedItem());
                        Toast.makeText(this, getString(R.string.edit_category_changed) + " " + (String) spnCategorias.getSelectedItem(), Toast.LENGTH_SHORT).show();
                    }

                    //Precio
                    if (!editPrecio.getText().toString().equals(actualProducto.getPrecio())){
                        cambiarValor("precio",editPrecio.getText().toString());
                        Toast.makeText(this, getString(R.string.edit_price_changed) + " " + editPrecio.getText().toString(), Toast.LENGTH_SHORT).show();
                    }

                    //Imagen
                    //Subimos imagen si ha seleccionado alguna
                    if (imagenBTMP != null){
                        subirImagen(claveProducto);
                    }
                }
                break;
            case R.id.imgEditProducto:
                //Inicia seleccionar imagen
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, "Seleccione una imagen"),
                        1);
                break;

        }
    }

    private void cambiarValor(String campo, String valor) {
        //Procedemos a cambiar el valor
        refProducto.child(campo).setValue(valor);
    }

    //Sube imagen
    private void subirImagen(String claveProducto){
        StorageReference imagenesRef = FirebaseStorage.getInstance().getReference("/imagenes/productos/" + claveProducto + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagenBTMP.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagenesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d("#ERROR", "Fallo en la subida " + exception.getCause().toString());
                Log.d("#ERROR", "Fallo en la subida " + exception.getLocalizedMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d("#ERROR", "Exito! " + taskSnapshot.getBytesTransferred());
            }
        });
    }


    //Al recibir una imagen seleccionada
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Uri selectedImageUri = null;
        Uri selectedImage;

        String filePath = null;
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    String selectedPath=selectedImage.getPath();
                    if (requestCode == 1) {

                        if (selectedPath != null) {
                            InputStream imageStream = null;
                            try {
                                imageStream = getContentResolver().openInputStream(
                                        selectedImage);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            // Transformamos la URI de la imagen a inputStream y este a un Bitmap
                            Bitmap bmp = BitmapFactory.decodeStream(imageStream);

                            //Establece imagen a la cargada
                            imagenBTMP = bmp;
                            imgView.setImageBitmap(imagenBTMP);
                        }
                    }
                }
                break;
        }
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
