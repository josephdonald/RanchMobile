package com.cursoandroid.ranchmobile.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cursoandroid.ranchmobile.MainActivity;
import com.cursoandroid.ranchmobile.R;
import com.cursoandroid.ranchmobile.activity.EstacaoActivity;
import com.cursoandroid.ranchmobile.adapter.ValvulasAdapter;
import com.cursoandroid.ranchmobile.classes.Estacao;
import com.cursoandroid.ranchmobile.classes.Valvulas;
import com.cursoandroid.ranchmobile.database.BancoControllerEnderecos;
import com.cursoandroid.ranchmobile.database.ConectaBanco;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class ValvulasFragment extends Fragment {

    private ListView viewListaValvulas;
    private ConectaBanco conectaBanco = new ConectaBanco();
    private ValvulasAdapter valvulasAdapter;
    private List<Valvulas> listaValvulasMain;

    public ValvulasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CRIA VIEW LAYOUT DO FRAGMENT VALVULAS **/
        View view = inflater.inflate(R.layout.fragment_valvulas, container, false);

        /** ASSOCIANDO A LIST VIEW AO XML DO LAYOUT **/
        viewListaValvulas = view.findViewById(R.id.lista_valvulas);

        /** CRIANDO A LISTA DE CONVIDADOS PRINCIPAL **/
        listaValvulasMain = new ArrayList<Valvulas>();

        /** CRIANDO A LISTA DE CONVIDADOS PRINCIPAL **/
        valvulasAdapter = new ValvulasAdapter(getContext(), listaValvulasMain);
        viewListaValvulas.setAdapter( valvulasAdapter );
        listaTodasValvulasBD();

        viewListaValvulas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Valvulas valvulas = (Valvulas) adapterView.getAdapter().getItem( position );

                int idValv = valvulas.getIdValvulas() ;

                listaEstacaoValvula( idValv );

            }
        });

        /** RETORNA DA VISAO CRIADA **/
        return view;
    }

    /** OBTEM OS DADOS DAS VALVULAS DO BANCO DE DADOS **/
    private void listaTodasValvulasBD (){

    //if ( verificaConexao( getContext() ) ){
            Log.i("erroFor", "Entrou no try");
        try{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String url = conectaBanco.HOST + "/valvula_search.php";

                    Log.i("testePreferencias", url);

                    try{
                        Ion.with( getActivity() )
                                .load(url)
                                .asJsonArray()
                                .setCallback(new FutureCallback<JsonArray>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonArray result) {
                                        Log.i("erroFor", "Entrou no Ion");
                                        //listaValvulasMain.clear();

                                        if (result != null) {

                                            for (int i = 0; i < result.size(); i++) {

                                                JsonObject object = result.get(i).getAsJsonObject();

                                                Valvulas valvulas = new Valvulas();

                                                valvulas.setIdValvulas(object.get("IDVALVULA").getAsInt());
                                                valvulas.setNomeValvula(object.get("NOME").getAsString());
                                                valvulas.setDescricaoValvula(object.get("DESCRICAO").getAsString());
                                                valvulas.setStatusValvula(object.get("STATUS").getAsString());

                                                listaValvulasMain.add(valvulas);

                                            }

                                            Log.i("testeFor", "dentro do for ID: " + listaValvulasMain.size());
                                            valvulasAdapter.notifyDataSetChanged();

                                            //Toast.makeText(getContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText( getContext(), "Há uma valvula com erro no cadastro" , Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                    } catch (Exception e){
                        Toast.makeText(getContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            }).start();
        } catch (Exception e){
            Toast.makeText(getContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void listaEstacaoValvula(int idValvula){

        final int idValvulaEstacao;
        idValvulaEstacao = idValvula;

//        if ( verificaConexao( getContext() ) ){
        if ( idValvula != 0 ){
            try {
                Log.i("erroFor", "Entrou no try");

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        String url = conectaBanco.HOST + "/estacao_search.php";

                        Ion.with( getContext() )
                                .load( url )
                                .setBodyParameter("valvula", String.valueOf(idValvulaEstacao))
                                .asJsonArray()
                                .setCallback(new FutureCallback<JsonArray>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonArray result) {
                                        /** VERIFICA DE O RETORNO É NULO **/
                                        if (result != null){

                                                /** VERIFICA SE ENCONTROU ALGUMA LINHA CORRESPONDENTE **/
                                                if ( result.size() > 0 ){

                                                        for (int i = 0; i < 1;i++){

                                                                JsonObject object = result.get ( i ).getAsJsonObject();

                                                                int valvulaEncontrada = object.get("ID_VALVULA").getAsInt();
                                                                String retorno = object.get("RETORNO").getAsString();

                                                                if ( retorno.equals("TRUE") ){
                                                                    /** OBTENDO O ID DA VALVULA **/
                                                                    Log.i("idValv", "TRUE" );

                                                                    EstacoesFragment estacoesFragment = new EstacoesFragment();

                                                                    Bundle bundle = new Bundle();
                                                                    bundle.putString("valvula", String.valueOf(idValvulaEstacao) );
                                                                    estacoesFragment.setArguments( bundle );

                                                                    getFragmentManager().beginTransaction().replace(R.id.conteudo_fragment, estacoesFragment).addToBackStack(null).commit();
                                                                }

                                                        }

                                                } else {

                                                    Toast.makeText( getContext(), "Não foram encontradas estacoes nessa valvula. ID: " + String.valueOf(idValvulaEstacao) , Toast.LENGTH_LONG).show();
    //                                                onStop();
                                                }
                                        } else {
                                            Toast.makeText( getContext(), "Não foram encontradas estacoes nessa valvula. ID: " + String.valueOf(idValvulaEstacao) , Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                }).start();

            } catch (Exception e) {
                Log.i("ErroExcecao", e.getMessage() ) ;
                Toast.makeText(getContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        } else {
            Log.i("ErroMain", "Erro de conexao na rede");
        }

    }

    public  boolean verificaConexao(Context context) {

        boolean conectado;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected()
                && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()
//                && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()
//                && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE_DUN).isConnected()
                ) {
            conectado = true;

            Toast.makeText(context, "Conectado na rede", Toast.LENGTH_LONG).show();

        } else {

            Toast toast = Toast.makeText(getContext(), "Não está conectado ao banco. Verifique sua conexão na rede.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();

            conectado = false;
        }

        return conectado;
    }

}