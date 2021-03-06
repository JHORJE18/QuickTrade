package com.jhorje18.quicktrade;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jhorje18.quicktrade.model.AdaptadorProductos;
import com.jhorje18.quicktrade.model.Producto;
import com.jhorje18.quicktrade.model.Usuario;

import java.util.ArrayList;

public class Perfil extends AppCompatActivity {

    //Variables
    TextView txtUsuario, txtSinProductos;
    ListView listViewProductos;

    Usuario actualUsuario;
    String claveUsuario;
    ArrayList<String> claveProductos;
    ArrayList<Producto> listadoProductos;

    DatabaseReference refUsuario;
    FirebaseUser user;
    DatabaseReference bbddUser, bbddProductos;
    AlertDialog.Builder dialogoEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        //Vista
        txtUsuario = (TextView) findViewById(R.id.txtPerfilUsuario);
        listViewProductos = (ListView) findViewById(R.id.listPerfilArticulos);
        txtSinProductos = (TextView) findViewById(R.id.txtPerfilSinProductos);

        //Obtenemos clave del nodo a mostrar
        claveUsuario = getIntent().getStringExtra("clave");

        //Obtener BBDD FireBase
        bbddUser = FirebaseDatabase.getInstance().getReference("usuarios");
        bbddProductos = FirebaseDatabase.getInstance().getReference("productos");

        //Obtener usuario actual
        user = FirebaseAuth.getInstance().getCurrentUser();

        //Iniciamos ArrayList
        claveProductos = new ArrayList<String>();
        listadoProductos = new ArrayList<Producto>();

        //Si no recibe usuario a mostrar...
        if (claveUsuario != null) {
            refUsuario = FirebaseDatabase.getInstance().getReference("usuarios/" + claveUsuario);
            refUsuario.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    actualUsuario = dataSnapshot.getValue(Usuario.class);

                    //Forzamos a recargar el usuario
                    recargar();
                    invalidateOptionsMenu();
                    cargarListadoProductos();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        } else {
            recargar();
            //Cargamos listado productos
            cargarListadoProductos();
        }

        //Creamos dialogo eliminar cuenta
        dialogoEliminar = new AlertDialog.Builder(this);
        dialogoEliminar.setIcon(getDrawable(R.drawable.alert_icon))
                .setTitle(getString(R.string.delete_profile))
                .setMessage(getString(R.string.question_delete))
                .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent confirmaEliminar = new Intent(Perfil.this, Confirmar.class);
                        confirmaEliminar.putExtra("eliminar", true);
                        startActivity(confirmaEliminar);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {Toast.makeText(Perfil.this, getString(R.string.cancel), Toast.LENGTH_SHORT).show();
                        }
                    });
    }

    private void recargar() {
        if (actualUsuario != null){
            txtUsuario.setText(actualUsuario.getUsuario());
        } else {
            txtUsuario.setText(user.getDisplayName());
        }

        //Si no hay productos no muestres el List
        if (listadoProductos.size() > 0){
            listViewProductos.setVisibility(View.VISIBLE);
            txtSinProductos.setVisibility(View.GONE);
        } else {
            listViewProductos.setVisibility(View.GONE);
            txtSinProductos.setVisibility(View.VISIBLE);
        }
    }

    //Cargar Lista productos del usuario
    private void cargarListadoProductos(){
        //Consultamos los productos del usuario
        Query q = bbddProductos.orderByChild("usuario").equalTo((String) txtUsuario.getText().toString());

        //Si encuentra registros...
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Creamos lista de los productos
                AdaptadorProductos adaptador;

                listadoProductos.clear();
                claveProductos.clear();

                //Obtenemos nombres de productos
                for (DataSnapshot datasnapshot : dataSnapshot.getChildren()) {
                    Producto productoTEMP = datasnapshot.getValue(Producto.class);

                    listadoProductos.add(productoTEMP);
                    claveProductos.add(datasnapshot.getKey());
                }

                //Adaptador personalizado
                adaptador = new AdaptadorProductos(Perfil.this, listadoProductos);
                listViewProductos.setAdapter(adaptador);

                recargar();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listViewProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Iniciamos mostrar producto con su clave
                Intent mostrar = new Intent(Perfil.this,ProductoView.class);
                mostrar.putExtra("clave",claveProductos.get(position));
                startActivity(mostrar);
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (actualUsuario != null) {
            if (!actualUsuario.getUsuario().equals(user.getDisplayName())) {
                menu.removeItem(R.id.mnEdit);
                menu.removeItem(R.id.mnEliminar);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    //Menu opciones superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_perfil, menu);

        return true;
    }

    //Eventos botones menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mnEdit:
                startActivity(new Intent(this, EditarUser.class));
                break;
            case R.id.mnEliminar:
                dialogoEliminar.show();
                break;
            case R.id.mnNuevoProducto:
                startActivity(new Intent(this,NuevoProducto.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
