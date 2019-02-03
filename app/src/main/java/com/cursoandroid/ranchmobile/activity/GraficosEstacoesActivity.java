package com.cursoandroid.ranchmobile.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.cursoandroid.ranchmobile.R;
import com.cursoandroid.ranchmobile.classes.DadosEstacao;
import com.cursoandroid.ranchmobile.classes.Estacao;
import com.cursoandroid.ranchmobile.database.ConectaBanco;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class GraficosEstacoesActivity extends AppCompatActivity {

    private String tempoInicial;
    private String tempoFinal;
    private String estacao;
    private Boolean umidade1;
    private Boolean umidade2;
    private Boolean umidade3;
    private Boolean temperaturaSolo;
    private Boolean tensaoBateria;

    private ConectaBanco conectaBanco = new ConectaBanco();

    private static final String TAG = "GraficosEstacoesActivity";
    private LineChart lineChartUmidade;

    private DadosEstacao dadosEstacao = new DadosEstacao();
    private int idEstacaoEncontrada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficos_estacoes);

        lineChartUmidade = findViewById(R.id.line_grafico_umidade);

        lineChartUmidade.setDragEnabled( true );
        lineChartUmidade.setScaleEnabled( false );

        Intent intentEstatisticas = getIntent();

        tempoInicial = intentEstatisticas.getStringExtra("tempoInicial");
        tempoFinal = intentEstatisticas.getStringExtra("tempoFinal");
        estacao = intentEstatisticas.getStringExtra("estacaoEscolhida");
        umidade1 = intentEstatisticas.getExtras().getBoolean("umidade1");
        umidade2 = intentEstatisticas.getExtras().getBoolean("umidade2");
        umidade3 = intentEstatisticas.getExtras().getBoolean("umidade3");
        temperaturaSolo = intentEstatisticas.getExtras().getBoolean("temperaturaSolo");
        tensaoBateria = intentEstatisticas.getExtras().getBoolean("tensaoBateria");

        pesquisaIdEstacao(estacao);

    }

    public void pesquisaIdEstacao(String estacaoBusca){

        String nomeEstacaoBusca = estacaoBusca;

        try {

            final String finalEstacaoBusca = nomeEstacaoBusca;

            new Thread(){

                public void run(){

                    String url = conectaBanco.HOST + "/estacao_search.php";

                    Ion.with( getBaseContext() )
                            .load( url )
                            .setBodyParameter("nome", finalEstacaoBusca)
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
                                                //estacao.setNomeEstacao( object.get("NOME").getAsString() );
                                                idEstacaoEncontrada = estacao.getIdEstacao();

                                                pesquisaEstacaoPeriodo(idEstacaoEncontrada, tempoInicial, tempoFinal );

                                            }

                                        }

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }
            }.start();

        } catch (Exception e) {

            Log.i("ErroExcecao", e.getMessage() ) ;
            Toast.makeText(getApplicationContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();

        }

    }

    public void pesquisaEstacaoPeriodo(int idEstacaoEncontrada, String tempoInicialBusca, String tempoFinalBusca){

        try{
//            tempoInicialBusca = "2018-11-06 19:00:07";
//            tempoFinalBusca = "2018-11-06 19:05:00";
            final int idEstacaoDados = idEstacaoEncontrada;
            final String finalTempoInicialBusca = tempoInicialBusca;
            final String finalTempoFinalBusca = tempoFinalBusca;

            new Thread(){

                String tempoInicialEstacao = finalTempoInicialBusca;
                String tempoFinalEstacao = finalTempoFinalBusca;

                public void run(){

                    String url = conectaBanco.HOST + "/dados_estacao_search.php";

                    Ion.with( getBaseContext() )
                            .load( url )
                            .setBodyParameter("estacao", String.valueOf(idEstacaoDados))
                            .setBodyParameter("tempo_inicial", tempoInicialEstacao)
                            .setBodyParameter("tempo_final", tempoFinalEstacao)
                            .asJsonArray()
                            .setCallback(new FutureCallback<JsonArray>() {
                                @Override
                                public void onCompleted(Exception e, JsonArray result) {

                                    /** VERIFICA DE O RETORNO É NULO **/
                                    if (result != null){
                                        if ( (umidade1) || (umidade2) || (umidade3) ){
                                            gerarGraficoUmidade( result );
                                        } else if( temperaturaSolo ){
                                            gerarGraficoTemperatura( result );
                                        } else if ( tensaoBateria ){
                                            gerarGraficoTensaoBateria( result );
                                        }

                                    } else {
                                        Toast.makeText( getBaseContext(), "Não foram encontradas dados  para essa estação. ID: " + String.valueOf( idEstacaoDados ) , Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

                }

            }.start();

        } catch (Exception e){
            Toast.makeText(getBaseContext(), "Erro para gerar o grafico: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void gerarGraficoUmidade( JsonArray resultExterno ){

        ArrayList<Entry> valoresUmidade1 = new ArrayList<>();
        ArrayList<Entry> valoresUmidade2 = new ArrayList<>();
        ArrayList<Entry> valoresUmidade3 = new ArrayList<>();

        String [] dataTempo = new String [resultExterno.size()];
        int [] idDadosArray = new int[resultExterno.size()];

        Float [] umidade1Array = new Float [resultExterno.size()];
        Float [] umidade2Array = new Float [resultExterno.size()];
        Float [] umidade3Array = new Float [resultExterno.size()];


        /** CRIA A LINHA DA UMIDADE 1 CASO ESTEJA SELECIONADA **/
        for (int i = 0; i < resultExterno.size(); i++){

                JsonObject object = resultExterno.get ( i ).getAsJsonObject();
                String retorno = object.get("RETORNO").getAsString();

                if ( retorno.equals("TRUE") ){

                    dadosEstacao.setIdDadosEstacao(object.get("IDDADOS").getAsInt() );
                    dadosEstacao.setUmidade1( object.get("UMIDADE_1").getAsFloat() );
                    dadosEstacao.setTempo_leitura( object.get("TEMP_AQUISICAO").getAsString() );

                    dataTempo[i] = dadosEstacao.getTempo_leitura();
                    idDadosArray[i] = dadosEstacao.getIdDadosEstacao();
                    umidade1Array [i] = dadosEstacao.getUmidade1();

                } else if ( retorno.equals("FALSE") ) {
                    Toast.makeText(GraficosEstacoesActivity.this, "Não foram encontrados os dados desejados.", Toast.LENGTH_SHORT).show();
                }

        }

        for (int i = 0; i < idDadosArray.length; i++){
            valoresUmidade1.add( new Entry(i, umidade1Array[i] ) );
            Log.i("TesteDados", "IDArray: " + idDadosArray[i] + " - " + "Umidade1Array: " + umidade1Array[i] + " - " + "TempoLeitura: " + dataTempo[i]);
        }


        /** CRIA A LINHA DA UMIDADE 2 CASO ESTEJA SELECIONADA **/
        for (int i = 0; i < resultExterno.size(); i++){

            JsonObject object = resultExterno.get ( i ).getAsJsonObject();
            String retorno = object.get("RETORNO").getAsString();

            if ( retorno.equals("TRUE") ){

                dadosEstacao.setIdDadosEstacao(object.get("IDDADOS").getAsInt() );
                dadosEstacao.setUmidade2( object.get("UMIDADE_2").getAsFloat() );
                dadosEstacao.setTempo_leitura( object.get("TEMP_AQUISICAO").getAsString() );

                dataTempo[i] = dadosEstacao.getTempo_leitura();
                idDadosArray[i] = dadosEstacao.getIdDadosEstacao();
                umidade2Array [i] = dadosEstacao.getUmidade2();

            } else if ( retorno.equals("FALSE") ) {
                Toast.makeText(GraficosEstacoesActivity.this, "Não foram encontrados os dados desejados.", Toast.LENGTH_SHORT).show();
            }

        }

        for (int i = 0; i < idDadosArray.length; i++){
            valoresUmidade2.add( new Entry(i, umidade2Array[i] ) );
            Log.i("TesteDados", "IDArray: " + idDadosArray[i] + " - " + "Umidade1Array: " + umidade2Array[i] + " - " + "TempoLeitura: " + dataTempo[i]);
        }

        /** CRIA A LINHA DA UMIDADE 3 CASO ESTEJA SELECIONADA **/
        for (int i = 0; i < resultExterno.size(); i++){

            JsonObject object = resultExterno.get ( i ).getAsJsonObject();
            String retorno = object.get("RETORNO").getAsString();

            if ( retorno.equals("TRUE") ){

                dadosEstacao.setIdDadosEstacao(object.get("IDDADOS").getAsInt() );
                dadosEstacao.setUmidade3( object.get("UMIDADE_3").getAsFloat() );
                dadosEstacao.setTempo_leitura( object.get("TEMP_AQUISICAO").getAsString() );

                dataTempo[i] = dadosEstacao.getTempo_leitura();
                idDadosArray[i] = dadosEstacao.getIdDadosEstacao();
                umidade3Array [i] = dadosEstacao.getUmidade3();

            } else if ( retorno.equals("FALSE") ) {
                Toast.makeText(GraficosEstacoesActivity.this, "Não foram encontrados os dados desejados.", Toast.LENGTH_SHORT).show();
            }

        }

        for (int i = 0; i < idDadosArray.length; i++){
            valoresUmidade3.add( new Entry(i, umidade3Array[i] ) );
            Log.i("TesteDados", "IDArray: " + idDadosArray[i] + " - " + "Umidade1Array: " + umidade3Array[i] + " - " + "TempoLeitura: " + dataTempo[i]);
        }

        LineDataSet set1 = new LineDataSet(valoresUmidade1, "Sensor 1");
        LineDataSet set2 = new LineDataSet(valoresUmidade2, "Sensor 2");
        LineDataSet set3 = new LineDataSet(valoresUmidade3, "Sensor 3");

        set1.setFillAlpha(110);
        set1.setColor(Color.GREEN);
        set1.setLineWidth(2f);
        set1.setValueTextSize(10f);

        set2.setFillAlpha(110);
        set2.setColor(Color.BLUE);
        set2.setLineWidth(2f);
        set2.setValueTextSize(10f);

        set3.setFillAlpha(110);
        set3.setColor(Color.YELLOW);
        set3.setLineWidth(2f);
        set3.setValueTextSize(10f);


        Description description = new Description();
        description.setText( "Estação: " + estacao );
        description.setTextSize( 15 );
        lineChartUmidade.setDescription( description );
        lineChartUmidade.getAxisRight().setEnabled(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        /** INSERINDO AS LINHAS NO GRAFICO **/
        if (umidade1){
            dataSets.add(set1);
        }
        if (umidade2){
            dataSets.add(set2);
        }
        if (umidade3){
            dataSets.add(set3);
        }

        LineData data = new LineData(dataSets);

        /** CONFIGURACOES DO LAYOUT DO GRAFICO **/
        //HABILITA O ZOOM
        lineChartUmidade.setDoubleTapToZoomEnabled(true);

        //HABILITA A ANIMAÇÃO
        lineChartUmidade.animateXY(2000,2000);
//        lineChartUmidade.fitScreen();
        lineChartUmidade.setScaleEnabled(true);
//        lineChartUmidade.setPinchZoom(false);
        lineChartUmidade.setVisibleXRangeMaximum(idDadosArray.length);
        lineChartUmidade.getXAxis().setLabelCount(idDadosArray.length);
        lineChartUmidade.setData( data );

        //INSERIR DOS DADOS DA DATA NO GRAFICO
        XAxis xAxis = lineChartUmidade.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter( dataTempo ));
//        float numGranularidade = (granularidade * 4);
        xAxis.setGranularity(1f);

    }

    public void gerarGraficoTemperatura(JsonArray resultExterno) {

        ArrayList<Entry> valoresTemperatura = new ArrayList<>();

        String[] dataTempo = new String[resultExterno.size()];
        int[] idDadosArray = new int[resultExterno.size()];
        Float[] temperaturaArray = new Float[resultExterno.size()];

        /** CRIA A LINHA DA UMIDADE 1 CASO ESTEJA SELECIONADA **/
        for (int i = 0; i < resultExterno.size(); i++) {

            JsonObject object = resultExterno.get(i).getAsJsonObject();
            String retorno = object.get("RETORNO").getAsString();

            if (retorno.equals("TRUE")) {

                dadosEstacao.setIdDadosEstacao(object.get("IDDADOS").getAsInt());
                dadosEstacao.setTemperatura_solo(object.get("TEMPERATURA_SOLO").getAsFloat());
                dadosEstacao.setTempo_leitura(object.get("TEMP_AQUISICAO").getAsString());

                dataTempo[i] = dadosEstacao.getTempo_leitura();
                idDadosArray[i] = dadosEstacao.getIdDadosEstacao();
                temperaturaArray[i] = dadosEstacao.getTemperatura_solo();

            } else if (retorno.equals("FALSE")) {
                Toast.makeText(GraficosEstacoesActivity.this, "Não foram encontrados os dados desejados.", Toast.LENGTH_SHORT).show();
            }

        }

        for (int i = 0; i < idDadosArray.length; i++) {
            valoresTemperatura.add(new Entry(i, temperaturaArray[i]));
            Log.i("TesteDados", "IDArray: " + idDadosArray[i] + " - " + "Temperatura1Array: " + temperaturaArray[i] + " - " + "TempoLeitura: " + dataTempo[i]);
        }

        LineDataSet set1 = new LineDataSet(valoresTemperatura, "Temperatura do solo na estação: " + estacao);

        set1.setFillAlpha(110);
        set1.setColor(Color.GREEN);
        set1.setLineWidth(2f);
        set1.setValueTextSize(10f);

        Description description = new Description();
        description.setText( "Estação: " + estacao );
        description.setTextSize( 15 );
        lineChartUmidade.setDescription( description );
        lineChartUmidade.getAxisRight().setEnabled(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        /** INSERINDO AS LINHAS NO GRAFICO **/
        if (temperaturaSolo){
            dataSets.add(set1);
        }

        LineData data = new LineData(dataSets);

        /** CONFIGURACOES DO LAYOUT DO GRAFICO **/
        //HABILITA O ZOOM
        lineChartUmidade.setDoubleTapToZoomEnabled(true);

        //HABILITA A ANIMAÇÃO
        lineChartUmidade.animateXY(2000,2000);
//        lineChartUmidade.fitScreen();
        lineChartUmidade.setScaleEnabled(true);
//        lineChartUmidade.setPinchZoom(false);
        lineChartUmidade.setVisibleXRangeMaximum(idDadosArray.length);
        lineChartUmidade.getXAxis().setLabelCount(idDadosArray.length);
        lineChartUmidade.setData( data );

        //INSERIR DOS DADOS DA DATA NO GRAFICO
        XAxis xAxis = lineChartUmidade.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter( dataTempo ));
//        float numGranularidade = (granularidade * 4);
        xAxis.setGranularity(1f);

    }

        public void gerarGraficoTensaoBateria(JsonArray resultExterno){

            ArrayList<Entry> valoresTensaoBateria = new ArrayList<>();

            String[] dataTempo = new String[resultExterno.size()];
            int[] idDadosArray = new int[resultExterno.size()];
            Float[] tensaoBateriaArray = new Float[resultExterno.size()];

            /** CRIA A LINHA DA UMIDADE 1 CASO ESTEJA SELECIONADA **/
            for (int i = 0; i < resultExterno.size(); i++) {

                JsonObject object = resultExterno.get(i).getAsJsonObject();
                String retorno = object.get("RETORNO").getAsString();

                if (retorno.equals("TRUE")) {

                    dadosEstacao.setIdDadosEstacao(object.get("IDDADOS").getAsInt());
                    dadosEstacao.setTensao_bateria(object.get("TENSAO_BATERIA").getAsFloat());
                    dadosEstacao.setTempo_leitura(object.get("TEMP_AQUISICAO").getAsString());

                    dataTempo[i] = dadosEstacao.getTempo_leitura();
                    idDadosArray[i] = dadosEstacao.getIdDadosEstacao();
                    tensaoBateriaArray[i] = dadosEstacao.getTensao_bateria();

                } else if (retorno.equals("FALSE")) {
                    Toast.makeText(GraficosEstacoesActivity.this, "Não foram encontrados os dados desejados.", Toast.LENGTH_SHORT).show();
                }

            }

            for (int i = 0; i < idDadosArray.length; i++) {
                valoresTensaoBateria.add(new Entry(i, tensaoBateriaArray[i]));
                Log.i("TesteDados", "IDArray: " + idDadosArray[i] + " - " + "Temperatura1Array: " + tensaoBateriaArray[i] + " - " + "TempoLeitura: " + dataTempo[i]);
            }

            LineDataSet set1 = new LineDataSet(valoresTensaoBateria, "Tensão da bateria na estação: " + estacao);

            set1.setFillAlpha(110);
            set1.setColor(Color.GREEN);
            set1.setLineWidth(2f);
            set1.setValueTextSize(10f);

            Description description = new Description();
            description.setText( "Estação: " + estacao );
            description.setTextSize( 15 );
            lineChartUmidade.setDescription( description );
            lineChartUmidade.getAxisRight().setEnabled(false);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();

            /** INSERINDO AS LINHAS NO GRAFICO **/
            if (tensaoBateria){
                dataSets.add(set1);
            }

            LineData data = new LineData(dataSets);

            /** CONFIGURACOES DO LAYOUT DO GRAFICO **/
            //HABILITA O ZOOM
            lineChartUmidade.setDoubleTapToZoomEnabled(true);

            //HABILITA A ANIMAÇÃO
            lineChartUmidade.animateXY(2000,2000);
//        lineChartUmidade.fitScreen();
            lineChartUmidade.setScaleEnabled(true);
//        lineChartUmidade.setPinchZoom(false);
            lineChartUmidade.setVisibleXRangeMaximum(idDadosArray.length);
            lineChartUmidade.getXAxis().setLabelCount(idDadosArray.length);
            lineChartUmidade.setData( data );

            //INSERIR DOS DADOS DA DATA NO GRAFICO
            XAxis xAxis = lineChartUmidade.getXAxis();
            xAxis.setValueFormatter(new MyXAxisValueFormatter( dataTempo ));
//        float numGranularidade = (granularidade * 4);
            xAxis.setGranularity(1f);

        }


    public class MyXAxisValueFormatter implements IAxisValueFormatter{

        //RECEBE O ARRAY DE STRING COM AS STRINGS A SEREM FORMATADAS
        private String [] mValues;

        public MyXAxisValueFormatter (String[] mesesExterno){
            this.mValues = mesesExterno;
        }

        //ASSOCIA AS STRINGS COM OS ÍNDICES
        @Override
        public String getFormattedValue (float value, AxisBase axis){
//            Log.i("TesteFormatter", mValues[(int) value]);
            return mValues[(int) value];
        }

    }

}