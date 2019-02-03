package com.cursoandroid.ranchmobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cursoandroid.ranchmobile.R;
import com.cursoandroid.ranchmobile.classes.Valvulas;

import java.util.List;

public class ValvulasAdapter extends BaseAdapter {

    private Context contextLocal;
    private List<Valvulas> listaLocal;

    public ValvulasAdapter (Context contextExterno, List<Valvulas> listExterno){

        this.contextLocal = contextExterno;
        this.listaLocal = listExterno;

    }


    @Override
    public int getCount() {
        return listaLocal.size();
    }

    @Override
    public Valvulas getItem(int position) {
        return listaLocal.get( position );
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    /** CRIA UMA VIEW CASO ELA SEJA NULA E ASSOCIA O NOME E RFID À LISTA DO ADAPTADOR **/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) contextLocal).getLayoutInflater();
            view = layoutInflater.inflate(R.layout.item_lista_valvula, null);
        } else {
            view = convertView;
        }

        Valvulas valvulas = getItem( position );

        TextView itemId = view.findViewById(R.id.item_id_valvula);
        TextView itemNome = view.findViewById(R.id.item_nome);
        TextView itemDescricao = view.findViewById(R.id.item_descricao);
        TextView itemIdValvula = view.findViewById(R.id.item_id_valvula);

        String statusAdapter = valvulas.getStatusValvula();
        Log.i("statusAdapter", statusAdapter);

        itemId.setText( String.valueOf( valvulas.getIdValvulas() )  );
        itemNome.setText( valvulas.getNomeValvula() );
        itemDescricao.setText( valvulas.getDescricaoValvula() );

        if (statusAdapter.equals("1") ) {
            itemNome.setTextColor(Color.parseColor("#32CD32"));
            Log.i("statusAdapter", "Identificou o status como " + statusAdapter);
        } else if ( statusAdapter.equals("0") ){
            itemNome.setTextColor(Color.parseColor("#8B0000"));
        }

        return view;
    }
}
