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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class NuevoProducto extends AppCompatActivity {

    //Variables
    Spinner spnCategorias;
    EditText editNombre, editDescripción, editPrecio;
    ImageView imgProducto;

    ArrayList<String> listaCategorias;
    DatabaseReference bbddCategorias, bbddProductos;
    Bitmap imagenBTMP;
    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference imagenesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_producto);

        //Vista
        spnCategorias = (Spinner) findViewById(R.id.spnNuevoProductCategorias);
        editNombre = (EditText) findViewById(R.id.editNuevoProductNombre);
        editDescripción = (EditText) findViewById(R.id.editNuevoProductDescripcion);
        editPrecio = (EditText) findViewById(R.id.editNuevoProductPrecio);
        imgProducto = (ImageView) findViewById(R.id.imgNuevoProducto);

        //Iniciamos ArrayList
        listaCategorias = new ArrayList<String>();

        //Obtenemos BBDD Firebase
        bbddCategorias = FirebaseDatabase.getInstance().getReference("categorias");
        bbddProductos = FirebaseDatabase.getInstance().getReference("productos");

        //Obten usuario sesión actual
        user = FirebaseAuth.getInstance().getCurrentUser();

        //Conectar almacenamiento online
        storage = FirebaseStorage.getInstance();
        imagenesRef = FirebaseStorage.getInstance().getReference("/imagenes/productos");

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

    //Botones
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnNuevoProductoGuardar:
                //Valida datos y si estan correctos, procede a guardar producto
                if (validarDatos()){
                    guardarProducto();
                }
                break;
            case R.id.btnNuevoProductoCancelar:
                finish();
                break;
            case R.id.imgNuevoProducto:
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

    //Procede a guardar producto
    private void guardarProducto(){
        //Preparamos valores
        String usuario = user.getDisplayName();
        String nombre = editNombre.getText().toString();
        String descripcion = editDescripción.getText().toString();
        String categoria = (String) spnCategorias.getSelectedItem();
        String precio = editPrecio.getText().toString() + "€";

        //Creamos producto local
        Producto nuevoProducto = new Producto(usuario,nombre,descripcion,categoria,precio);

        //Generamos clave para nuevo registro
        String clave = bbddCategorias.push().getKey();

        //Insertamos registro
        bbddProductos.child(clave).setValue(nuevoProducto);

        //Subimos imagen
        subirImagen(clave);

        Toast.makeText(this, getString(R.string.add_product) + " " + nuevoProducto.getNombre(), Toast.LENGTH_LONG).show();

        finish();
    }

    //Sube imagen
    private void subirImagen(String claveProducto){
        StorageReference mountainsRef = imagenesRef.child("/" + claveProducto + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagenBTMP.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
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
                            imgProducto.setImageBitmap(imagenBTMP);
                        }
                    }
                }
                break;
        }
    }

    private boolean validarDatos(){
        //Validar Nombre
        boolean valido = true;

        if (editNombre.getText().toString().isEmpty()){
            editNombre.setError(getString(R.string.error_input_nameproduct));
            valido = false;
        }

        //Validar Descripción
        if (editDescripción.getText().toString().isEmpty()){
            editDescripción.setError(getString(R.string.error_input_descriptionproduct));
            valido = false;
        }

        //Validar precio
        if (editPrecio.getText().toString().isEmpty()){
            editPrecio.setError(getString(R.string.error_input_precioproduct));
            valido = false;
        }

        return valido;
    }
}
