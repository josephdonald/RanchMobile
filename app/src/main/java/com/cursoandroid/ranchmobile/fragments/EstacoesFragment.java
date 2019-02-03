package com.cursoandroid.ranchmobile.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cursoandroid.ranchmobile.R;
import com.cursoandroid.ranchmobile.activity.EstacaoActivity;
import com.cursoandroid.ranchmobile.activity.PerfilEstacaoActivity;
import com.cursoandroid.ranchmobile.adapter.EstacaoPorValvulaAdapter;
import com.cursoandroid.ranchmobile.classes.Estacao;
import com.cursoandroid.ranchmobile.database.ConectaBanco;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class EstacoesFragment extends Fragment {

    int idValvulaEstacao;
    private ListView viewListaEstacaoValvulas;
    private List<Estacao> listaEstacoesEncontradasMain;
    private EstacaoPorValvulaAdapter estacaoPorValvulaAdapter;

    private ConectaBanco conectaBanco = new ConectaBanco();


    public EstacoesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_estacoes, container, false);

        /** ASSOCIANDO A LIST VIEW AO XML DO LAYOUT **/
        viewListaEstacaoValvulas = view.findViewById(R.id.lista_estacao_valvulas);

        /** CRIANDO UMA LISTA PARA RECEBER AS ESTACOES ENCONTRADAS **/
        listaEstacoesEncontradasMain = new ArrayList<Estacao>();

        /** ATRIBUINDO A LISTA AO ADAPTER **/
        estacaoPorValvulaAdapter = new EstacaoPorValvulaAdapter(getActivity(), listaEstacoesEncontradasMain);
        viewListaEstacaoValvulas.setAdapter( estacaoPorValvulaAdapter );


        /** RECEBE O ID DA VALVULA CLICADA NA FRAGMENT DE VALVULAS **/
        Bundle bundle = getArguments();
        if (bundle != null){

            String idValvulva = bundle.getString("valvula");

            idValvulaEstacao = Integer.parseInt( idValvulva );

            Log.i("idValvulaTeste", String.valueOf(idValvulaEstacao) );
        }



        listarEstacoes( idValvulaEstacao );

        viewListaEstacaoValvulas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                try{
                    Estacao estacao = (Estacao) adapterView.getAdapter().getItem( position );

                    int idEstacao = estacao.getIdEstacao();

                    Intent intentPerfilEstacao = new Intent(getActivity(), PerfilEstacaoActivity.class);
                    intentPerfilEstacao.putExtra("idEstacao", idEstacao);

                    startActivity( intentPerfilEstacao );
                } catch (Exception e){
                    Toast.makeText(getActivity(), "Erro (ListView Estacoes): " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

        return view;
    }

    public void listarEstacoes(int idValvulaEstacao){

        String idValvulaClicada;

        if ( String.valueOf( idValvulaEstacao ).equals("0") ){
            idValvulaClicada = "";
        } else{
            idValvulaClicada = String.valueOf( idValvulaEstacao );
        }

        try {

            final String finalIdValvulaClicada = idValvulaClicada;
             new Thread(){
                public void run(){

                    String url = conectaBanco.HOST + "/estacao_search.php";

                    Ion.with( getActivity() )
                            .load( url )
                            .setBodyParameter("valvula", finalIdValvulaClicada)
                            .asJsonArray()
                            .setCallback(new FutureCallback<JsonArray>() {
                                @Override
                                public void onCompleted(Exception e, JsonArray result) {
                                    /** VERIFICA DE O RETORNO É NULO **/

                                    if ( result.size() > 0 ){

                                        for (int i = 0; i < result.size(); i++){

                                            JsonObject object = result.get ( i ).getAsJsonObject();

                                            String retorno = object.get("RETORNO").getAsString();

                                            if ( retorno.equals("TRUE") ){

                                                Estacao estacao = new Estacao();

                                                estacao.setIdEstacao( object.get("IDESTACAO").getAsInt() );
                                                estacao.setNomeEstacao( object.get("NOME").getAsString() );
                                                estacao.setDescricaoEstacao( object.get("DESCRICAO").getAsString() );
                                                estacao.setStatusEstacao( object.get("STATUS").getAsString() );

                                                listaEstacoesEncontradasMain.add( estacao );
                                            }

                                            estacaoPorValvulaAdapter.notifyDataSetChanged();
                                        }

                                    } else {
                                        Toast.makeText(getActivity(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }
            }.start();

        } catch (Exception e) {

            Log.i("ErroExcecao", e.getMessage() ) ;
            Toast.makeText(getActivity(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();

        }

    }

}