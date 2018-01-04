package com.jhorje18.quicktrade.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jhorje18.quicktrade.R;

import java.util.ArrayList;

/**
 * Created by wiijl on 04/01/2018.
 */

public class AdaptadorProductos extends BaseAdapter {

    Activity activity;
    ArrayList<Producto> listaProductos;

    public AdaptadorProductos(Activity activity, ArrayList<Producto> elementos){
        this.activity = activity;
        this.listaProductos = elementos;
    }

    @Override
    public int getCount() {
        return this.listaProductos.size();
    }

    public void clear(){
        listaProductos.clear();
    }

    public void addAll(ArrayList<Producto> productos){
        for (int i=0; i<productos.size(); i++){
            listaProductos.add(productos.get(i));
        }
    }

    @Override
    public Object getItem(int i) {
        return listaProductos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (convertView == null){
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.product_list, null);
        }

        Producto productActual = listaProductos.get(position);

        //Variables
        TextView txtNombre, txtCategoria, txtDescripcion, txtPrecio, txtUsuario;

        //Vista
        txtNombre = (TextView) v.findViewById(R.id.tctListProductNombre);
        txtCategoria = (TextView) v.findViewById(R.id.tctListProductCategoria);
        txtDescripcion = (TextView) v.findViewById(R.id.tctListProductDescripcion);
        txtPrecio = (TextView) v.findViewById(R.id.tctListProductPrecio);
        txtUsuario = (TextView) v.findViewById(R.id.tctListProductUser);

        //Asignación valores
        txtNombre.setText(productActual.getNombre());
        txtCategoria.setText("#" + productActual.getCategoria());
        txtDescripcion.setText(productActual.getDescripcion());
        txtPrecio.setText(productActual.getPrecio());
        txtUsuario.setText("@" + productActual.getUsuario());

        return v;
    }
}
