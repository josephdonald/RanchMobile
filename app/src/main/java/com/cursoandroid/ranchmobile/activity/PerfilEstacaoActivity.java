package com.cursoandroid.ranchmobile.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cursoandroid.ranchmobile.R;
import com.cursoandroid.ranchmobile.classes.DadosEstacao;
import com.cursoandroid.ranchmobile.classes.Estacao;
import com.cursoandroid.ranchmobile.classes.Valvulas;
import com.cursoandroid.ranchmobile.database.ConectaBanco;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class PerfilEstacaoActivity extends AppCompatActivity {

    private int idEstacao;
    private TextView idEstacaoText, nomeEstacaoText, descricaoEstacaoText, latitudeEstacaoText,
            longitudeEstacaoText, statusEstacaoText, valvulaEstacaoText, nomeValvEst;

    private TextView umidade1Text, umidade2Text, umidade3Text, temperaturaSoloText, tensaoBateriaText, tempoLeituraText;

    private Button btLocalizar;

    private String nomeEstacao, latitudeEstacao, longitudeEstacao;
    private float umidadeMediaPerfil;
    private float umidade1Perfil, umidade2Perfil, umidade3Perfil;
    private ConectaBanco conectaBanco = new ConectaBanco();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_estacao);

        /** IDENTIFICAÇÃO DA ESTACAO**/
        idEstacaoText = findViewById(R.id.tv_id_estacao);
        nomeEstacaoText = findViewById(R.id.tv_nome_estacao);
        descricaoEstacaoText = findViewById(R.id.tv_dados_descricao_est);
        latitudeEstacaoText = findViewById(R.id.tv_dados_latitude_est);
        longitudeEstacaoText = findViewById(R.id.tv_dados_longitude_est);
        statusEstacaoText = findViewById(R.id.tv_dados_status_est);
        valvulaEstacaoText = findViewById(R.id.tv_id_valv_est);
        nomeValvEst = findViewById(R.id.tv_nome_valv_est);

        /** DADOS DA ESTACAO **/
        umidade1Text = findViewById(R.id.tv_dados_umid_1);
        umidade2Text = findViewById(R.id.tv_dados_umid_2);
        umidade3Text = findViewById(R.id.tv_dados_umid_3);
        temperaturaSoloText = findViewById(R.id.tv_dados_temp_solo);
        tensaoBateriaText = findViewById(R.id.tv_dados_bateria);
        tempoLeituraText = findViewById(R.id.tv_dados_tempo_leitura);

        btLocalizar = findViewById(R.id.bt_localizar);

        Intent intentListaEstacao = getIntent();
        idEstacao = intentListaEstacao.getIntExtra("idEstacao", 0);
        /** METODO PARA PESQUISAR O PERFIL POR ID **/
        pesquisaPerfil( idEstacao );

        btLocalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentEstacaoMap = new Intent(PerfilEstacaoActivity.this, EstacaoMapsActivity.class);

                intentEstacaoMap.putExtra("origem", "perfil");
                intentEstacaoMap.putExtra("idEstacaoMaps", idEstacao);
                intentEstacaoMap.putExtra("latitudeEstacao", latitudeEstacao );
                intentEstacaoMap.putExtra("longitudeEstacao", longitudeEstacao );
                intentEstacaoMap.putExtra("nomeEstacaoMaps", nomeEstacao );
                Log.i("TestePerfil", String.valueOf(umidadeMediaPerfil) );
                intentEstacaoMap.putExtra("umidadeMedia", umidadeMediaPerfil);
//                intentEstacaoMap.putExtra("umidade1Perfil", umidade1Perfil);
//                intentEstacaoMap.putExtra("umidade2Perfil", umidade2Perfil);
//                intentEstacaoMap.putExtra("umidade3Perfil", umidade3Perfil);

                startActivity( intentEstacaoMap );

            }
        });

    }

    private void pesquisaPerfil(final int idEstacaoPerfil){

        final int idEstacaoClicada;
        idEstacaoClicada = idEstacaoPerfil;

        try {

            new Thread(){
                public void run(){

                    String url = conectaBanco.HOST + "/estacao_search.php";

                    Ion.with( getBaseContext() )
                            .load( url )
                            .setBodyParameter("id", String.valueOf( idEstacaoClicada ))
                            .asJsonArray()
                            .setCallback(new FutureCallback<JsonArray>() {
                                @Override
                                public void onCompleted(Exception e, JsonArray result) {

                                    /** VERIFICA DE O RETORNO É NULO **/
                                    if (result != null){
                                        if ( result.size() > 0 ){

                                            for (int i = 0; i < 1; i++){

                                                JsonObject object = result.get ( i ).getAsJsonObject();

                                                String retorno = object.get("RETORNO").getAsString();

                                                if ( retorno.equals("TRUE") ){
                                                    /** OBTENDO O ID DA VALVULA **/
                                                    Log.i("idValv", "TRUE" );

                                                    Estacao estacao = new Estacao();

                                                    estacao.setIdEstacao( object.get("IDESTACAO").getAsInt() );
                                                    estacao.setNomeEstacao( object.get("NOME").getAsString() );
                                                    estacao.setDescricaoEstacao( object.get("DESCRICAO").getAsString() );
                                                    estacao.setLatitudeEstacao( object.get("LATITUDE_S").getAsString() );
                                                    estacao.setLongitudeEstacao( object.get("LONGITUDE_O").getAsString() );
                                                    estacao.setStatusEstacao( object.get("STATUS").getAsString() );
                                                    estacao.setIdValvula( object.get("ID_VALVULA").getAsInt() );

                                                    idEstacaoText.setText( String.valueOf( estacao.getIdEstacao() ) );
                                                    nomeEstacaoText.setText( estacao.getNomeEstacao() );
                                                    descricaoEstacaoText.setText( estacao.getDescricaoEstacao() );
                                                    latitudeEstacaoText.setText( estacao.getLatitudeEstacao() );
                                                    longitudeEstacaoText.setText( estacao.getLongitudeEstacao() );

                                                    nomeEstacao = estacao.getNomeEstacao();
                                                    latitudeEstacao = estacao.getLatitudeEstacao();
                                                    longitudeEstacao = estacao.getLongitudeEstacao();

                                                    if (estacao.getStatusEstacao().equals("1")){
                                                        statusEstacaoText.setText( "Ativada" );
                                                    }else if (estacao.getStatusEstacao().equals("0")) {
                                                        statusEstacaoText.setText( "Desativada" );
                                                    }

//                                                    statusEstacaoText.setText( estacao.getStatusEstacao() );
                                                    valvulaEstacaoText.setText( String.valueOf( estacao.getIdValvula() ) );

                                                    int idValvula = estacao.getIdValvula();

//                                                    idValvulaPerfil = idValvula;

                                                    /** CHAMA O METODO QUE ENCONTRA O ID DA VALVULA **/
                                                    Log.i("idValvPerfil", String.valueOf(idValvula));
                                                    pesquisaNomeValvula( idValvula );

                                                    /** CHAMA O METODO QUE ENCONTRA OS DADOS DAS ESTACOES **/
                                                    Log.i("idValvPerfil", String.valueOf(estacao.getIdEstacao() ) );
                                                    pesquisaDadosEstacao( estacao.getIdEstacao() );

                                                }
                                            }

                                        } else {

                                            onStop();
                                        }
                                    } else {
                                        Toast.makeText( getBaseContext(), "Não foram encontradas estacoes nessa valvula. ID: " + String.valueOf( idEstacaoClicada ) , Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

                }
            }.start();

        } catch (Exception e) {

            Log.i("ErroExcecao", e.getMessage() ) ;
            Toast.makeText(getBaseContext(), "Erro ao pesquisar o Perfil: " + e.getMessage(), Toast.LENGTH_LONG).show();

        }

    }

    private void pesquisaNomeValvula(final int valvulaEstacao){

        final int idValvulaDaEstacao;
        idValvulaDaEstacao = valvulaEstacao;

        Log.i("idValv", "ANTES DO IF" + idValvulaDaEstacao );

        try {

            new Thread(){
                public void run(){

                    String url = conectaBanco.HOST + "/valvula_search.php";

                    Ion.with( getBaseContext() )
                            .load( url )
                            .setBodyParameter("id", String.valueOf( idValvulaDaEstacao ))
                            .asJsonArray()
                            .setCallback(new FutureCallback<JsonArray>() {
                                @Override
                                public void onCompleted(Exception e, JsonArray result) {

                                    /** VERIFICA DE O RETORNO É NULO **/
                                    if (result != null){
                                        if ( result.size() > 0 ){

                                            for (int i = 0; i < 1; i++){

                                                JsonObject object = result.get ( i ).getAsJsonObject();

                                                String retorno = object.get("RETORNO").getAsString();

                                                Log.i("idValv", "ANTES DO IF" );

                                                if ( retorno.equals("TRUE") ){
                                                    /** OBTENDO O ID DA VALVULA **/
                                                    Log.i("idValv", "Nome valvula" );

                                                    Valvulas valvulas = new Valvulas();

                                                    valvulas.setIdValvulas( object.get("IDVALVULA").getAsInt() );
                                                    valvulas.setNomeValvula( object.get("NOME").getAsString() );

                                                    nomeValvEst.setText( valvulas.getNomeValvula() );

                                                }
                                            }

                                        } else {

                                            onStop();
                                        }
                                    } else {
                                        Toast.makeText( getBaseContext(), "Não foram encontradas estacoes nessa valvula. ID: " + String.valueOf( idValvulaDaEstacao ) , Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

                }
            }.start();

        } catch (Exception e) {

            Log.i("ErroExcecao", e.getMessage() ) ;
            Toast.makeText(getBaseContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();

        }

    }

    private void pesquisaDadosEstacao(int idEstacao){

        final int idEstacaoDados;

        idEstacaoDados = idEstacao;

        try{

            new Thread(){

                public void run(){

                    String url = conectaBanco.HOST + "/dados_estacao_search.php";

                    Ion.with( getBaseContext() )
                            .load( url )
                            .setBodyParameter("estacao", String.valueOf( idEstacaoDados ))
                            .asJsonArray()
                            .setCallback(new FutureCallback<JsonArray>() {
                                @Override
                                public void onCompleted(Exception e, JsonArray result) {

                                    /** VERIFICA DE O RETORNO É NULO **/
                                    if (result != null){
                                        if ( result.size() > 0 ){

                                            for (int i = 0; i < 1; i++){

                                                JsonObject object = result.get ( i ).getAsJsonObject();

                                                String retorno = object.get("RETORNO").getAsString();

                                                if ( retorno.equals("TRUE") ){

                                                    DadosEstacao dadosEstacao = new DadosEstacao();

//                                                    dadosEstacao.setIdDadosEstacao( object.get("IDDADOSESTACAO").getAsInt() );
                                                    dadosEstacao.setUmidade1( object.get("UMIDADE_1").getAsFloat() );
                                                    dadosEstacao.setUmidade2( object.get("UMIDADE_2").getAsFloat() );
                                                    dadosEstacao.setUmidade3( object.get("UMIDADE_3").getAsFloat() );
                                                    dadosEstacao.setTemperatura_solo( object.get("TEMPERATURA_SOLO").getAsFloat() );
                                                    dadosEstacao.setTensao_bateria( object.get("TENSAO_BATERIA").getAsFloat() );
                                                    dadosEstacao.setTempo_leitura( object.get("TEMP_AQUISICAO").getAsString() );

                                                    umidade1Perfil = dadosEstacao.getUmidade1();
                                                    umidade2Perfil = dadosEstacao.getUmidade2();
                                                    umidade3Perfil = dadosEstacao.getUmidade3();

                                                    float umidadeMediaBruta = ((umidade1Perfil + umidade2Perfil + umidade3Perfil) / 3);
                                                    double umidadeArredondada = Double.valueOf(umidadeMediaBruta) ;
                                                    umidadeArredondada *= (Math.pow(10, 2));
                                                    umidadeArredondada = Math.ceil(umidadeArredondada);
                                                    umidadeArredondada /= (Math.pow(10,2));

                                                    umidadeMediaBruta = Float.valueOf((float) umidadeArredondada);

//                                                    Log.i("testeMap", "Umidade: " + String.valueOf(umidadeMediaBruta) );

                                                    umidadeMediaPerfil = umidadeMediaBruta;

                                                    umidade1Text.setText( String.valueOf( dadosEstacao.getUmidade1() + " %") );
                                                    umidade2Text.setText( String.valueOf( dadosEstacao.getUmidade2() + " %") );
                                                    umidade3Text.setText( String.valueOf( dadosEstacao.getUmidade3() + " %") );
                                                    temperaturaSoloText.setText( String.valueOf( dadosEstacao.getTemperatura_solo() + " ºC" ) );
                                                    tensaoBateriaText.setText( String.valueOf( dadosEstacao.getTensao_bateria() + " V") );
                                                    tempoLeituraText.setText( String.valueOf( dadosEstacao.getTempo_leitura() ) );

                                                }
                                            }

                                        } else {

                                            onStop();
                                        }
                                    } else {
                                        Toast.makeText( getBaseContext(), "Não foram encontradas estacoes nessa valvula. ID: " + String.valueOf( idEstacaoDados ) , Toast.LENGTH_LONG).show();
                                    }

                                }
                            });


                }

            }.start();


        } catch (Exception e){
            Toast.makeText(getBaseContext(), "Erro ao carregar os dados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

}