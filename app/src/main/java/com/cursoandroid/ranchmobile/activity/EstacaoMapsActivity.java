package com.cursoandroid.ranchmobile.activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cursoandroid.ranchmobile.R;
import com.cursoandroid.ranchmobile.classes.DadosEstacao;
import com.cursoandroid.ranchmobile.classes.Estacao;
import com.cursoandroid.ranchmobile.database.ConectaBanco;
import com.cursoandroid.ranchmobile.fragments.EstacoesFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class EstacaoMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GoogleMapOptions mapOptions = new GoogleMapOptions();

    /** VARIAVEL QUE VERIFICA DE ONDE VEM A SOLICITAÇÃO DO MAPA **/
    private String origem;

    /** ARRAYS USADOS QUANDO A SOLICITAÇÃO VIER DO MAIN **/
    private int [] idEstacaoMap;
    private float [] latitudeEstacaoMap;
    private float [] longitudeEstacaoMap;
    private String [] nomeEstacaoMap;
    private float[] umidadeMediaFinal;

    /** VARIAVEIS QUE RECEBERAM OS DADOS QUANDO A SOLICITAÇÃO VIEW DO PERFIL **/
    private int idEstacaoPerfil;
    private String latitudeEstacaoPerfil;
    private String longitudeEstacaoPerfil;
    private String nomeEstacaoPerfil;
    private float umidadeEstacaoPerfil;

    private ConectaBanco conectaBanco = new ConectaBanco();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estacao_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intentEstacaoMap = getIntent();

        origem = intentEstacaoMap.getStringExtra("origem");

        /** CASO A ORIGEM DA SOLICITAÇÃO VENHA DO MAIN, SERÃO RECEBIDOS OS ARRAYS EXTERNOS NOS ARRAYS LOCAIS **/
        if (origem.equals("main")){

            int [] idEstacaoRecebida = intentEstacaoMap.getIntArrayExtra("idEstacaoMaps");
            String [] nomeEstacaoRecebido = intentEstacaoMap.getStringArrayExtra("nomeEstacaoMaps");
            float [] latitudeEstacaoRecebida = intentEstacaoMap.getFloatArrayExtra("latitudeEstacao");
            float [] longitudeEstacaoRecebida = intentEstacaoMap.getFloatArrayExtra("longitudeEstacao");
            float [] umidadeMediaFinalRecebida = intentEstacaoMap.getFloatArrayExtra("umidadeMedia");

            Log.i("TESTE", "########################################");
            /** MIGRAÇÃO PARA O ARRAY LOCAL **/
            /** IDS **/
            idEstacaoMap = new int[idEstacaoRecebida.length];
            for (int i = 0; i < idEstacaoRecebida.length; i++){
                idEstacaoMap[i] = idEstacaoRecebida[i];
                Log.i("TESTE", "ID: (transferido)" + idEstacaoMap[i]);
            }

            /** NOME **/
            nomeEstacaoMap = new String[nomeEstacaoRecebido.length];
            for (int i = 0; i < nomeEstacaoRecebido.length; i++){
                nomeEstacaoMap[i] = nomeEstacaoRecebido[i];
                Log.i("TESTE", "NOME: (transferido)" + nomeEstacaoMap[i]);
            }

            /** LATITUDE **/
            latitudeEstacaoMap = new float[latitudeEstacaoRecebida.length];
            for (int i = 0; i < latitudeEstacaoRecebida.length; i++){
                latitudeEstacaoMap[i] = latitudeEstacaoRecebida[i];
                Log.i("TESTE", "LATITUDE: (transferido)" + latitudeEstacaoMap[i]);
            }

            /** LONGITUDE **/
            longitudeEstacaoMap = new float[longitudeEstacaoRecebida.length];
            for (int i = 0; i < longitudeEstacaoRecebida.length; i++){
                longitudeEstacaoMap[i] = longitudeEstacaoRecebida[i];
                Log.i("TESTE", "LONGITUDE: (transferido)" + longitudeEstacaoMap[i]);
            }

            /** UMIDADE **/
            umidadeMediaFinal = new float[umidadeMediaFinalRecebida.length];
            for (int i = 0; i < umidadeMediaFinalRecebida.length; i++){
                umidadeMediaFinal[i] = umidadeMediaFinalRecebida[i];
                Log.i("TESTE", "UMIDADE: (transferido)" + umidadeMediaFinal[i]);
            }
            Log.i("TESTE", "########################################");


        }
        /** CASO A ORIGEM DA SOLICITAÇÃO VENHA DA TELA DE PERFIL, SERÃO RECEBIDOS OS DADOS EXTERNOS NAS VARIÁVEIS LOCAIS **/
        else if (origem.equals("perfil")){

            idEstacaoPerfil = intentEstacaoMap.getIntExtra("idEstacaoMaps",0);
            nomeEstacaoPerfil = intentEstacaoMap.getStringExtra("nomeEstacaoMaps");
            latitudeEstacaoPerfil = intentEstacaoMap.getStringExtra("latitudeEstacao");
            longitudeEstacaoPerfil = intentEstacaoMap.getStringExtra("longitudeEstacao");
            umidadeEstacaoPerfil = intentEstacaoMap.getFloatExtra("umidadeMedia",0);

            Log.i("dadosObtidos", "Nome: " + nomeEstacaoPerfil);
            Log.i("dadosObtidos", "Latitude: " + latitudeEstacaoPerfil);
            Log.i("dadosObtidos", "Longitude: " + longitudeEstacaoPerfil);
            Log.i("dadosObtidos", "Umidade: " + umidadeEstacaoPerfil);

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getCameraPosition();
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        float zoom = 17.2f;
        MarkerOptions markerOptions = new MarkerOptions();

        if (origem.equals("main")){
            for (int i = 0; i < idEstacaoMap.length; i++){

                LatLng posicaoEstacao = new LatLng(latitudeEstacaoMap[i], longitudeEstacaoMap[i] );

                markerOptions.position( posicaoEstacao );
                markerOptions.title("Estação: " + nomeEstacaoMap[i] + " - " + "Umidade média: " + umidadeMediaFinal[i] + "%" );
                mMap.addMarker( markerOptions );
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( posicaoEstacao, zoom ) );
            }
        } else if (origem.equals("perfil")){

            LatLng posicaoEstacao = new LatLng(Double.parseDouble(latitudeEstacaoPerfil), Double.parseDouble(longitudeEstacaoPerfil));

            markerOptions.position( posicaoEstacao );
            markerOptions.title("Estação: " + nomeEstacaoPerfil + " - " + "Umidade média: " + umidadeEstacaoPerfil + "%" );
            mMap.addMarker( markerOptions );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( posicaoEstacao, zoom ) );
        }

    }

}