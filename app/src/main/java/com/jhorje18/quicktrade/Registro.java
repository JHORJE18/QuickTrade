package com.jhorje18.quicktrade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {

    //Variables
    EditText editUsuario, editCorreo, editContraseña, editNombre, editApedillos, editDireccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Vista
        editUsuario = (EditText) findViewById(R.id.editRegUsuario);
        editCorreo = (EditText) findViewById(R.id.editRegCorreo);
        editContraseña = (EditText) findViewById(R.id.editRegContraseña);
        editNombre = (EditText) findViewById(R.id.editRegNombre);
        editApedillos = (EditText) findViewById(R.id.editRegApedillos);
        editDireccion = (EditText) findViewById(R.id.editRegDireccion);
    }

    //Validar campos
    private boolean validarCampos(){
        boolean valido = true;

        //Validar Usuario
        if (editUsuario.getText().toString().isEmpty()){
            editUsuario.setError("No puedes dejar este campo en blanco.");
            valido = false;
        }

        //Validar Correo
        if (editCorreo.getText().toString().isEmpty()){
            editCorreo.setError("No puedes dejar este campo en blanco.");
            valido = false;
        } else {
            //Valida estructura String
            Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
            Matcher matcher = pattern.matcher(editCorreo.getText().toString());

            if (!matcher.matches()){
                //No valido!
                editCorreo.setError("Correo no valido.");
                valido = false;
            }
        }

        //Validar Contraseña
        if (editContraseña.getText().toString().isEmpty()){
            editContraseña.setError("No puedes dejar este campo en blanco.");
            valido = false;
        }

        //Validar Nombre
        if (editNombre.getText().toString().isEmpty()){
            editNombre.setError("No puedes dejar este campo en blanco.");
            valido = false;
        }

        //Validar Apedillos
        if (editApedillos.getText().toString().isEmpty()){
            editApedillos.setError("No puedes dejar este campo en blanco.");
            valido = false;
        }

        //Validar Dirección
        if (editDireccion.getText().toString().isEmpty()){
            editDireccion.setError("No puedes dejar este campo en blanco.");
            valido = false;
        }

        //Devolver resultado
        return valido;
    }

    //Eventos botones
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnRegCrear:
                boolean ok = validarCampos();
                break;
            case R.id.btnRegIniciar:
                Intent nueva = new Intent(this, Login.class);
                startActivity(nueva);
                finish();
                break;
        }
    }
}
