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
import com.jhorje18.quicktrade.model.Producto;
import com.jhorje18.quicktrade.model.Usuario;

public class Perfil extends AppCompatActivity {

    //Variables
    TextView txtUsuario;

    Usuario actualUsuario;
    String claveUsuario;

    DatabaseReference refUsuario;
    FirebaseUser user;
    DatabaseReference bbddUser;
    AlertDialog.Builder dialogoEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        //Vista
        txtUsuario = (TextView) findViewById(R.id.txtPerfilUsuario);

        //Obtenemos clave del nodo a mostrar
        claveUsuario = getIntent().getStringExtra("clave");

        //Obtener BBDD FireBase
        bbddUser = FirebaseDatabase.getInstance().getReference("usuarios");

        //Obtener usuario actual
        user = FirebaseAuth.getInstance().getCurrentUser();

        //Si no recibe usuario a mostrar...
        if (claveUsuario != null) {
            refUsuario = FirebaseDatabase.getInstance().getReference("usuarios/" + claveUsuario);
            refUsuario.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    actualUsuario = dataSnapshot.getValue(Usuario.class);
                    recargar();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        } else {
            txtUsuario.setText(user.getDisplayName());
        }

        //Creamos dialogo eliminar cuenta
        dialogoEliminar = new AlertDialog.Builder(this);
        dialogoEliminar.setIcon(getDrawable(R.drawable.alert_icon))
                .setTitle("Eliminar perfil")
                .setMessage("¿Seguro que quieres eliminar tu cuenta?")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent confirmaEliminar = new Intent(Perfil.this, Confirmar.class);
                        confirmaEliminar.putExtra("eliminar", true);
                        startActivity(confirmaEliminar);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {Toast.makeText(Perfil.this, "Cancelado", Toast.LENGTH_SHORT).show();
                        }
                    });

         //Obtenemos info user actual
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void recargar() {
        if (actualUsuario != null){
            txtUsuario.setText(actualUsuario.getUsuario());
        } else {
            txtUsuario.setText(user.getDisplayName());
        }
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
                break;
            case R.id.mnEliminar:
                dialogoEliminar.show();
                break;
            case R.id.mnNuevoProducto:
                startActivity(new Intent(this,ProductoView.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
