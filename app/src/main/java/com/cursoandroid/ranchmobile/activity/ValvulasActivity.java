package com.cursoandroid.ranchmobile.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cursoandroid.ranchmobile.MainActivity;
import com.cursoandroid.ranchmobile.R;
import com.cursoandroid.ranchmobile.adapter.ValvulasAdapter;
import com.cursoandroid.ranchmobile.classes.Valvulas;
import com.cursoandroid.ranchmobile.database.ConectaBanco;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class ValvulasActivity extends AppCompatActivity {

    private ListView viewListaValvulas;
    private ConectaBanco conectaBanco = new ConectaBanco();
    private ValvulasAdapter valvulasAdapter;
    private List<Valvulas> listaValvulasMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valvulas);

        /** ASSOCIANDO A LIST VIEW AO XML DO LAYOUT **/
//        viewListaValvulas = findViewById(R.id.lista_valvulas);

        /** CRIANDO A LISTA DE CONVIDADOS PRINCIPAL **/
        listaValvulasMain = new ArrayList<Valvulas>();

        /** CRIANDO A LISTA DE CONVIDADOS PRINCIPAL **/
        valvulasAdapter = new ValvulasAdapter( getBaseContext(), listaValvulasMain );
        viewListaValvulas.setAdapter( valvulasAdapter );
        listaValvulasBD();

        viewListaValvulas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /** PESQUISANDO AS ESTACOES PELA VALVULA CLICADA **/
                /** listaEstacaoValvula( position ); **/

//                Toast.makeText( getBaseContext(), "ID: " + position, Toast.LENGTH_SHORT ).show();

            }
        });

    }

    /** OBTEM OS DADOS DAS VALVULAS DO BANCO DE DADOS **/
    private void listaValvulasBD (){

        try {
            Log.i("erroFor", "Entrou no try");

            new Thread(){
                public void run(){

                    String url = conectaBanco.HOST + "/valvula_search.php";

                    Ion.with( getBaseContext() )
                            .load(url)
                            .asJsonArray()
                            .setCallback(new FutureCallback<JsonArray>() {
                                @Override
                                public void onCompleted(Exception e, final JsonArray result) {
                                    Log.i("erroFor", "Entrou no Ion");

                                    for (int i = 0; i < result.size(); i++) {

                                        JsonObject object = result.get(i).getAsJsonObject();

                                        Valvulas valvulas = new Valvulas();

                                        valvulas.setNomeValvula( object.get("NOME").getAsString() );
                                        valvulas.setDescricaoValvula( object.get("DESCRICAO").getAsString() );
                                        valvulas.setStatusValvula( object.get("STATUS").getAsString() );

                                        listaValvulasMain.add( valvulas );

                                    }

                                    valvulasAdapter.notifyDataSetChanged();

                                }
                            });

                }
            }.start();

        } catch (Exception e) {

            Log.i("ErroExcecao", e.getMessage() ) ;
            Toast.makeText(getBaseContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();

        }

    }
}
