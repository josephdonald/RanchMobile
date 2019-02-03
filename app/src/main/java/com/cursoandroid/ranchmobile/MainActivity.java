package com.cursoandroid.ranchmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.cursoandroid.ranchmobile.activity.EstacaoMapsActivity;
import com.cursoandroid.ranchmobile.classes.DadosEstacao;
import com.cursoandroid.ranchmobile.classes.Estacao;
import com.cursoandroid.ranchmobile.database.ConectaBanco;
import com.cursoandroid.ranchmobile.fragments.ConfiguraAppFragment;
import com.cursoandroid.ranchmobile.fragments.EstacoesFragment;
import com.cursoandroid.ranchmobile.fragments.GeradorGraficosFragment;
import com.cursoandroid.ranchmobile.fragments.ValvulasFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView imageEstacoes, imageValvulas, imageMapa, imageEstatisticas;

    /** VETORES QUE RECEBEM OS DADOS DAS PESQUISAS**/
    private int [] idsEstacaoArray;
    private String [] nomeEstacaoArray;
    private float [] umidadeMediaArray;

    private int [] idsArrayFinal;

    private float []  latitudeEstacaoArray;
    private float []  longitudeEstacaoArray;

    private ConectaBanco conectaBanco = new ConectaBanco();
    private DadosEstacao dadosEstacao = new DadosEstacao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageEstacoes = findViewById(R.id.img_estacoes);
        imageValvulas = findViewById(R.id.img_valvulas);
        imageMapa = findViewById(R.id.img_maps);
        imageEstatisticas = findViewById(R.id.img_estatisticas);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                Toast.makeText(MainActivity.this, "CLICK", Toast.LENGTH_LONG).show();

            }
        });

        imageEstacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EstacoesFragment estacoesFragment = new EstacoesFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.conteudo_fragment, estacoesFragment).addToBackStack(null).commit();
            }
        });

        imageValvulas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValvulasFragment valvulasFragment = new ValvulasFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.conteudo_fragment, valvulasFragment).addToBackStack(null).commit();
            }
        });

        /** OBTEM O ID DE TODAS AS ESTACOES E INSERE ELAS NO MAPA **/
        listarEstacoes();

        imageMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0 ; i < idsEstacaoArray.length; i++){
                    Log.i("UmidadeGlobal", "ID POR FAVOR: " + idsEstacaoArray[i]);
                    Log.i("UmidadeGlobal", "NOME POR FAVOR: " + nomeEstacaoArray[i]);
                    Log.i("UmidadeGlobal", "LATITUDE POR FAVOR: " + latitudeEstacaoArray[i]);
                    Log.i("UmidadeGlobal", "LONGITUDE POR FAVOR: " + longitudeEstacaoArray[i]);
                    Log.i("UmidadeGlobal", "UMIDADE POR FAVOR: " + umidadeMediaArray[i]);
                }

                Intent intentEstacaoMap = new Intent(getBaseContext(), EstacaoMapsActivity.class);
                /** INSERIR ARRAYS NA INTENT **/
                    intentEstacaoMap.putExtra("origem", "main");
                    intentEstacaoMap.putExtra("idEstacaoMaps", idsEstacaoArray);
                    intentEstacaoMap.putExtra("nomeEstacaoMaps", nomeEstacaoArray);
                    intentEstacaoMap.putExtra("latitudeEstacao", latitudeEstacaoArray );
                    intentEstacaoMap.putExtra("longitudeEstacao", longitudeEstacaoArray );
                    intentEstacaoMap.putExtra("umidadeMedia", umidadeMediaArray);

                startActivity( intentEstacaoMap );

            }
        });

        imageEstatisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeradorGraficosFragment geradorGraficosFragment = new GeradorGraficosFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.conteudo_fragment, geradorGraficosFragment).addToBackStack(null).commit();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_estacoes) {

            EstacoesFragment estacoesFragment = new EstacoesFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.conteudo_fragment, estacoesFragment).addToBackStack(null).commit();

        } else if (id == R.id.nav_valvulas) {

            ValvulasFragment valvulasFragment = new ValvulasFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.conteudo_fragment, valvulasFragment).addToBackStack(null).commit();

        } else if (id == R.id.nav_estatisticas) {

            GeradorGraficosFragment geradorGraficosFragment = new GeradorGraficosFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.conteudo_fragment, geradorGraficosFragment).addToBackStack(null).commit();

        } else if (id == R.id.nav_configuracao){

            ConfiguraAppFragment configuraAppFragment = new ConfiguraAppFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.conteudo_fragment, configuraAppFragment).addToBackStack(null).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void listarEstacoes(){

        try{

                    String url = conectaBanco.HOST + "/estacao_search.php";

                    Ion.with( getBaseContext() )
                            .load( url )
                            .setBodyParameter("estacao", "")
                            .asJsonArray()
                            .setCallback(new FutureCallback<JsonArray>() {
                                @Override
                                public void onCompleted(Exception e, JsonArray result) {

                                    if (result.size() > 0){

                                        idsEstacaoArray = new int [ result.size() ];
                                        nomeEstacaoArray = new String [ result.size() ];
                                        latitudeEstacaoArray = new float [ result.size() ];
                                        longitudeEstacaoArray = new float [ result.size() ];
                                        umidadeMediaArray = new float [result.size()];

                                        int [] idsEstacaoArrayLocal = new int [ result.size() ];
                                        String [] nomeEstacaoArrayLocal = new String [ result.size() ];
                                        float [] latitudeEstacaoArrayLocal = new float [ result.size() ];
                                        float [] longitudeEstacaoArrayLocal = new float [ result.size() ];
//                                        float [] umidadeMediaArrayLocal = new float[ result.size() ];

                                        for ( int i = 0; i < result.size(); i++ ){

                                            JsonObject jsonObject = result.get( i ).getAsJsonObject();
                                            String retorno = jsonObject.get("RETORNO").getAsString();

                                            if ( retorno.equals("TRUE") ){

                                                Estacao estacao = new Estacao();

                                                estacao.setIdEstacao( jsonObject.get("IDESTACAO").getAsInt() );
                                                estacao.setNomeEstacao( jsonObject.get("NOME").getAsString() );
                                                estacao.setLatitudeEstacao( jsonObject.get("LATITUDE_S").getAsString() );
                                                estacao.setLongitudeEstacao( jsonObject.get("LONGITUDE_O").getAsString() );

                                                idsEstacaoArrayLocal [i] = estacao.getIdEstacao();
                                                nomeEstacaoArrayLocal [i] = estacao.getNomeEstacao();
                                                latitudeEstacaoArrayLocal [i] = Float.parseFloat( estacao.getLatitudeEstacao() );
                                                longitudeEstacaoArrayLocal [i] = Float.parseFloat( estacao.getLongitudeEstacao() );
                                            }

                                            idsEstacaoArray[i] = idsEstacaoArrayLocal [i];
                                            nomeEstacaoArray [i] = nomeEstacaoArrayLocal [i];
                                            latitudeEstacaoArray [i] = latitudeEstacaoArrayLocal [i];
                                            longitudeEstacaoArray [i] = longitudeEstacaoArrayLocal [i];

                                        }
                                        pesquisaDadosEstacao( idsEstacaoArray );

                                    } else {
                                        Toast.makeText(MainActivity.this, "Erro: ", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

        } catch (Exception e){

            Toast.makeText(this, "Erro; " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }

    public void pesquisaDadosEstacao(int [] idEstacaoExterno) {

        umidadeMediaArray = new float [idEstacaoExterno.length];
        final int [] idsEstacaoLocal = idEstacaoExterno;

        for (int i = 0; i < idEstacaoExterno.length; i++){

            try {
                final float[] umidadeRetorno = new float[1];
                String url = conectaBanco.HOST + "/dados_estacao_search.php";

                final int finalI = i;
                Ion.with(getBaseContext())
                        .load(url)
                        .setBodyParameter("estacao", String.valueOf(idsEstacaoLocal[finalI]))
                        .asJsonArray()
                        .setCallback(new FutureCallback<JsonArray>() {
                            @Override
                            public void onCompleted(Exception e, JsonArray result) {
                                if (result != null) {

                                    umidadeMediaArray[finalI] = calculaUmidadeMedia(result);

                                } else {
                                    Log.i("UmidadeGlobal", "RETORNO NULO ");
                                }
                            }

                        });

            } catch (Exception e) {
                Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    public float calculaUmidadeMedia(JsonArray resultExterno){

        float umidadeMediaCalculada = 0;

        for (int i = 0; i < resultExterno.size(); i++){

            JsonObject object = resultExterno.get(i).getAsJsonObject();
            String retorno = object.get("RETORNO").getAsString();

            if (retorno.equals("TRUE")){
                dadosEstacao.setUmidade1( object.get("UMIDADE_1").getAsFloat() );
                dadosEstacao.setUmidade2( object.get("UMIDADE_2").getAsFloat() );
                dadosEstacao.setUmidade3( object.get("UMIDADE_3").getAsFloat() );

                float umidade1 = dadosEstacao.getUmidade1();
                float umidade2 = dadosEstacao.getUmidade2();
                float umidade3 = dadosEstacao.getUmidade3();

                umidadeMediaCalculada = ((umidade1 + umidade2 + umidade3) / 3);

                double umidadeArredondada = Double.valueOf(umidadeMediaCalculada) ;
                umidadeArredondada *= (Math.pow(10, 2));
                umidadeArredondada = Math.ceil(umidadeArredondada);
                umidadeArredondada /= (Math.pow(10,2));

                umidadeMediaCalculada = Float.valueOf((float) umidadeArredondada);

                Log.i("TESTE", "Umidade convertida: " + umidadeMediaCalculada);

            }

        }

        return umidadeMediaCalculada;

    }

}