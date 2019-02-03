package com.cursoandroid.ranchmobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cursoandroid.ranchmobile.R;
import com.cursoandroid.ranchmobile.classes.Estacao;

import java.util.List;

public class EstacaoPorValvulaAdapter extends BaseAdapter {

    private Context contextLocal;
    private List<Estacao> listaLocal;


    public EstacaoPorValvulaAdapter(Context contextExterno, List<Estacao> listaExterna) {

        this.contextLocal = contextExterno;
        this.listaLocal = listaExterna;

    }

    @Override
    public int getCount() {
        return listaLocal.size();
    }

    @Override
    public Object getItem(int position) {
        return listaLocal.get( position );
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        if (convertView == null){
            LayoutInflater layoutInflater = ((Activity) contextLocal).getLayoutInflater();
            view = layoutInflater.inflate(R.layout.item_lista_estacao_valvula, null);
        } else {
            view = convertView;
        }

        Estacao estacao = (Estacao) getItem( position );

        TextView itemId = view.findViewById(R.id.item_id_estacao);
        TextView itemNome = view.findViewById(R.id.item_nome_estacao);
        TextView itemDescricao = view.findViewById(R.id.item_descricao_estacao);

        String statusAdapter = estacao.getStatusEstacao();

        itemId.setText( String.valueOf( estacao.getIdEstacao() ) );
        itemNome.setText( estacao.getNomeEstacao() );
        itemDescricao.setText( estacao.getDescricaoEstacao() );

        Log.i("statusAdapter", "Identificou o status como " + statusAdapter);
        if ( statusAdapter.equals("1") ){
            itemNome.setTextColor(Color.parseColor("#32CD32"));
        } else if ( statusAdapter.equals("0") ){
            itemNome.setTextColor(Color.parseColor("#8B0000"));
        }

        return view;
    }
}
