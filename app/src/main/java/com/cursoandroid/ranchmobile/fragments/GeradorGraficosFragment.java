package com.cursoandroid.ranchmobile.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cursoandroid.ranchmobile.R;
import com.cursoandroid.ranchmobile.activity.GraficosEstacoesActivity;
import com.cursoandroid.ranchmobile.classes.Estacao;
import com.cursoandroid.ranchmobile.database.ConectaBanco;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Calendar;

public class GeradorGraficosFragment extends Fragment {

    private EditText editDataInicial;
    private EditText editDataFinal;
    private EditText editHoraInicial;
    private EditText editHoraFinal;
    private Calendar dataInicialCalendario;
    private Calendar dataFinalCalendario;
    private Calendar horarioInicialRelogio;
    private Calendar horarioFinalRelogio;
    private Spinner spnEstacoes;
    private CheckBox checkUmid1;
    private CheckBox checkUmid2;
    private CheckBox checkUmid3;
    private CheckBox checkTempSolo;
    private CheckBox checkTensBateria;
    private Button btGerarGrafico;

    private String dataInicial;
    private String dataFinal;
    private String horarioInicial;
    private String horarioFinal;
    private String estacaoEscolhidaSpinner;

    private Boolean booleanDataInicial = false;
    private Boolean booleanDataFinal = false;
    private Boolean booleanHorarioInicial = false;
    private Boolean booleanHorarioFinal = false;
    private Boolean booleanUmidade1 = false;
    private Boolean booleanUmidade2 = false;
    private Boolean booleanUmidade3 = false;
    private Boolean booleanTempSolo = false;
    private Boolean booleanTensBateria = false;

    private ConectaBanco conectaBanco = new ConectaBanco();

    public GeradorGraficosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gerador_graficos, container, false);

        editDataInicial = view.findViewById(R.id.edit_data_inicio);
        editDataFinal = view.findViewById(R.id.edit_data_final);
        editHoraInicial = view.findViewById(R.id.edit_horario_inicio);
        editHoraFinal = view.findViewById(R.id.edit_horario_final);
        spnEstacoes = view.findViewById(R.id.spn_estacoes);
        checkUmid1 = view.findViewById(R.id.ck_umidade_1);
        checkUmid2 = view.findViewById(R.id.ck_umidade_2);
        checkUmid3 = view.findViewById(R.id.ck_umidade_3);
        checkTempSolo = view.findViewById(R.id.ck_temp_solo);
        checkTensBateria = view.findViewById(R.id.ck_tens_bateria);
        btGerarGrafico = view.findViewById(R.id.bt_gerar_graf);

//        /** CRIANDO A MASCARA DE DADOS PARA AS DATAS **/
//        SimpleMaskFormatter simpleMaskDataInicio = new SimpleMaskFormatter("NN/NN/NNNN");
//        MaskTextWatcher maskDataInicio = new MaskTextWatcher(editDataInicial, simpleMaskDataInicio);
//        editDataInicial.addTextChangedListener( maskDataInicio );
        /** LISTA AS ESTACOES PARA O SPINNER **/
        listaEstacoesSpinner();

        /** Ativa o calendário para a inserção da DATA INICIAL no Edit **/
        editDataInicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dataInicialCalendario = Calendar.getInstance();
                int ano = dataInicialCalendario.get(Calendar.YEAR);
                int mes = dataInicialCalendario.get(Calendar.MONTH);
                int dia = dataInicialCalendario.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int anoEscolhido, int mesEscolhido, int diaEscolhido) {

                        mesEscolhido ++;

                        editDataInicial.setText(diaEscolhido + "/" + mesEscolhido + "/" + anoEscolhido);
//                        editDataInicial.setText( anoEscolhido + "/" + (mesEscolhido + 1) +"/"+ diaEscolhido);

                        dataInicialCalendario.set(anoEscolhido, mesEscolhido, diaEscolhido);
//                        dataInicial = editDataInicial.getText().toString();
                        dataInicial = String.valueOf(anoEscolhido +"-"+ mesEscolhido +"-"+ diaEscolhido);

                        Log.i("testeData", dataInicial);

                    }
                }, ano, mes, dia);
                datePickerDialog.setTitle("Escolha a data inicial");
                datePickerDialog.show();

                booleanDataInicial = true;
            }
        });

        /** Ativa o calendário para a inserção da DATA FINAL no Edit **/
        editDataFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataFinalCalendario = Calendar.getInstance();
                int ano = dataFinalCalendario.get(Calendar.YEAR);
                int mes = dataFinalCalendario.get(Calendar.MONTH);
                int dia = dataFinalCalendario.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int anoEscolhido, int mesEscolhido, int diaEscolhido) {
                        mesEscolhido ++;
                        editDataFinal.setText(diaEscolhido + "/" + mesEscolhido + "/" + anoEscolhido);
//                        editDataInicial.setText( anoEscolhido + "/" + (mesEscolhido + 1) +"/"+ diaEscolhido);

                        dataFinalCalendario.set(anoEscolhido, mesEscolhido, diaEscolhido);

//                        dataFinal = editDataFinal.getText().toString();
                        dataFinal = String.valueOf(anoEscolhido +"-"+ mesEscolhido +"-"+ diaEscolhido);
                        Log.i("testeData", dataFinal);

                    }
                }, ano, mes, dia);
                datePickerDialog.show();
                booleanDataFinal = true;
            }
        });


        /** Ativar o relegio para a inserção do HORARIO INICIAL **/
        editHoraInicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                horarioInicialRelogio = Calendar.getInstance();
                int hora = horarioInicialRelogio.get(Calendar.HOUR_OF_DAY);
                int minuto = horarioInicialRelogio.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        editHoraInicial.setText(hourOfDay + ":" + minute + ":00");
                        horarioInicial = editHoraInicial.getText().toString();

                    }
                }, hora, minuto, true);

                timePickerDialog.setTitle("Escolha o horário inicial");
                timePickerDialog.show();

                booleanHorarioInicial = true;

            }
        });

        /** Ativar o relegio para a inserção do HORARIO FINAL **/
        editHoraFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                horarioFinalRelogio = Calendar.getInstance();
                int hora = horarioFinalRelogio.get(Calendar.HOUR_OF_DAY);
                int minuto = horarioFinalRelogio.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        editHoraFinal.setText(hourOfDay + ":" + minute + ":00");
                        horarioFinal = editHoraFinal.getText().toString();

                    }
                }, hora, minuto, true);

                timePickerDialog.setTitle("Escolha o horário inicial");
                timePickerDialog.show();

                booleanHorarioFinal = true;

            }
        });

        /** Verifica o click dos CheckBox **/
        checkUmid1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                booleanUmidade1 = checkUmid1.isChecked();
                Log.i("testeBoolean", "Umidade 1: " + String.valueOf(booleanUmidade1) );
                if (booleanUmidade1){
                    checkTempSolo.setEnabled(false);
                    checkTensBateria.setEnabled(false);

                    checkTempSolo.setTextColor(Color.parseColor("#808080"));
                    checkTensBateria.setTextColor(Color.parseColor("#808080"));

//                    checkTempSolo.setVisibility(View.INVISIBLE);
//                    checkTensBateria.setVisibility(View.INVISIBLE);
                } else {
                    if ( (booleanUmidade2 == false) && (booleanUmidade3 == false) ){

                        checkTempSolo.setEnabled(true);
                        checkTensBateria.setEnabled(true);

                        checkTempSolo.setTextColor(Color.parseColor("#F5FFFA"));
                        checkTensBateria.setTextColor(Color.parseColor("#F5FFFA"));

//                        checkTempSolo.setVisibility(View.VISIBLE);
//                        checkTensBateria.setVisibility(View.VISIBLE);

                    }

                }

            }
        });

        checkUmid2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                booleanUmidade2 = checkUmid2.isChecked();
                Log.i("testeBoolean", "Umidade 2: " + String.valueOf(booleanUmidade2) );
                Log.i("testeBoolean", "Umidade 1: " + String.valueOf(booleanUmidade1) );
                if (booleanUmidade2){
                    checkTempSolo.setEnabled(false);
                    checkTensBateria.setEnabled(false);

                    checkTempSolo.setTextColor(Color.parseColor("#808080"));
                    checkTensBateria.setTextColor(Color.parseColor("#808080"));

//                    checkTempSolo.setVisibility(View.INVISIBLE);
//                    checkTensBateria.setVisibility(View.INVISIBLE);
                } else {
                    if ( (booleanUmidade1 == false) && (booleanUmidade3 == false) ){

                        checkTempSolo.setEnabled(true);
                        checkTensBateria.setEnabled(true);

                        checkTempSolo.setTextColor(Color.parseColor("#F5FFFA"));
                        checkTensBateria.setTextColor(Color.parseColor("#F5FFFA"));

//                        checkTempSolo.setVisibility(View.VISIBLE);
//                        checkTensBateria.setVisibility(View.VISIBLE);

                    }

                }

            }
        });

        checkUmid3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                booleanUmidade3 = checkUmid3.isChecked();
                Log.i("testeBoolean", "Umidade 3: " + String.valueOf(booleanUmidade3) );
                Log.i("testeBoolean", "Umidade 1: " + String.valueOf(booleanUmidade1) );
                if (booleanUmidade3){
                    checkTempSolo.setEnabled(false);
                    checkTensBateria.setEnabled(false);

                    checkTempSolo.setTextColor(Color.parseColor("#808080"));
                    checkTensBateria.setTextColor(Color.parseColor("#808080"));

//                    checkTempSolo.setVisibility(View.INVISIBLE);
//                    checkTensBateria.setVisibility(View.INVISIBLE);

                } else {

                    if ( (booleanUmidade1 == false) && (booleanUmidade2 == false) ){
                        checkTempSolo.setEnabled(true);
                        checkTensBateria.setEnabled(true);

                        checkTempSolo.setTextColor(Color.parseColor("#F5FFFA"));
                        checkTensBateria.setTextColor(Color.parseColor("#F5FFFA"));

//                        checkTempSolo.setVisibility(View.VISIBLE);
//                        checkTensBateria.setVisibility(View.VISIBLE);
                    }

                }

            }
        });

        checkTempSolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                booleanTempSolo = checkTempSolo.isChecked();
                Log.i("testeBoolean", "Temperatura: " + String.valueOf(booleanTempSolo) );

                Log.i("testeBoolean", "Umidade 1: " + String.valueOf(booleanUmidade1) );
                if (booleanTempSolo){
                    checkUmid1.setEnabled(false);
                    checkUmid2.setEnabled(false);
                    checkUmid3.setEnabled(false);
                    checkTensBateria.setEnabled(false);

                    checkUmid1.setTextColor(Color.parseColor("#808080"));
                    checkUmid2.setTextColor(Color.parseColor("#808080"));
                    checkUmid3.setTextColor(Color.parseColor("#808080"));
                    checkTensBateria.setTextColor(Color.parseColor("#808080"));

//                    checkUmid1.setVisibility(View.INVISIBLE);
//                    checkUmid2.setVisibility(View.INVISIBLE);
//                    checkUmid3.setVisibility(View.INVISIBLE);
//                    checkTensBateria.setVisibility(View.INVISIBLE);

                } else {
                    checkUmid1.setEnabled(true);
                    checkUmid2.setEnabled(true);
                    checkUmid3.setEnabled(true);
                    checkTensBateria.setEnabled(true);

                    checkUmid1.setTextColor(Color.parseColor("#F5FFFA"));
                    checkUmid2.setTextColor(Color.parseColor("#F5FFFA"));
                    checkUmid3.setTextColor(Color.parseColor("#F5FFFA"));
                    checkTensBateria.setTextColor(Color.parseColor("#F5FFFA"));

//                    checkUmid1.setVisibility(View.VISIBLE);
//                    checkUmid2.setVisibility(View.VISIBLE);
//                    checkUmid3.setVisibility(View.VISIBLE);
//                    checkTensBateria.setVisibility(View.VISIBLE);
                }

            }
        });

        checkTensBateria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                booleanTensBateria = checkTensBateria.isChecked();
                Log.i("testeBoolean", "Tensão bateria: " + String.valueOf(booleanTensBateria) );

                if (booleanTensBateria){
                    checkUmid1.setEnabled(false);
                    checkUmid2.setEnabled(false);
                    checkUmid3.setEnabled(false);
                    checkTempSolo.setEnabled(false);

                    checkUmid1.setTextColor(Color.parseColor("#808080"));
                    checkUmid2.setTextColor(Color.parseColor("#808080"));
                    checkUmid3.setTextColor(Color.parseColor("#808080"));
                    checkTempSolo.setTextColor(Color.parseColor("#808080"));

//                    checkUmid1.setVisibility(View.INVISIBLE);
//                    checkUmid2.setVisibility(View.INVISIBLE);
//                    checkUmid3.setVisibility(View.INVISIBLE);
//                    checkTempSolo.setVisibility(View.INVISIBLE);
                } else {
                    checkUmid1.setEnabled(true);
                    checkUmid2.setEnabled(true);
                    checkUmid3.setEnabled(true);
                    checkTempSolo.setEnabled(true);

                    checkUmid1.setTextColor(Color.parseColor("#F5FFFA"));
                    checkUmid2.setTextColor(Color.parseColor("#F5FFFA"));
                    checkUmid3.setTextColor(Color.parseColor("#F5FFFA"));
                    checkTempSolo.setTextColor(Color.parseColor("#F5FFFA"));

//                    checkUmid1.setVisibility(View.VISIBLE);
//                    checkUmid2.setVisibility(View.VISIBLE);
//                    checkUmid3.setVisibility(View.VISIBLE);
//                    checkTempSolo.setVisibility(View.VISIBLE);
                }

            }
        });


        /** LENDO ESTACAO ESCOLHIDA NA SPINNER **/
        spnEstacoes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                estacaoEscolhidaSpinner = (String) parent.getItemAtPosition( position );
//                String estacao = parent.getSelectedItem().toString();
                Log.i("testeSpinner", estacaoEscolhidaSpinner);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btGerarGrafico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("testeBoolean", String.valueOf(booleanUmidade1) );

                try{

                    if ( ( (booleanDataInicial != true) && (booleanDataFinal != true) && (booleanHorarioInicial != true) && (booleanHorarioFinal != true) ) && ( ( booleanUmidade1 == false ) || ( booleanUmidade2 == false ) || ( booleanUmidade3 == false ) || ( booleanTensBateria == false ) || ( booleanTempSolo == false ))  ){

                        Toast.makeText(getActivity(), "Verifique as datas e horarios ou uma das opções acima. ", Toast.LENGTH_LONG).show();

                    } else {

                        String tempoInicial = (dataInicial + " " + horarioInicial);
                        String tempoFinal = (dataFinal + " " + horarioFinal);
                        Log.i("testeTempo", tempoInicial);
                        Log.i("testeTempo", tempoFinal);

                        Intent intentGraficos = new Intent(getActivity(), GraficosEstacoesActivity.class);

//                intentGraficos.putExtra("dataInicial", dataInicial);
//                intentGraficos.putExtra("dataFinal", dataFinal);
//                intentGraficos.putExtra("horarioInicial", horarioInicial);
//                intentGraficos.putExtra("horarioFinal", horarioFinal);
                        intentGraficos.putExtra("tempoInicial", tempoInicial);
                        intentGraficos.putExtra("tempoFinal", tempoFinal);
                        intentGraficos.putExtra("estacaoEscolhida", estacaoEscolhidaSpinner);
                        intentGraficos.putExtra("umidade1", booleanUmidade1);
                        intentGraficos.putExtra("umidade2", booleanUmidade2);
                        intentGraficos.putExtra("umidade3", booleanUmidade3);
                        intentGraficos.putExtra("temperaturaSolo", booleanTempSolo);
                        intentGraficos.putExtra("tensaoBateria", booleanTensBateria);

                        startActivity( intentGraficos );
                    }

                } catch (Exception e){
                    Toast.makeText(getActivity(), "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

             }

        });
        return view;
    }
    /**####################**/
    /** METODOS DA CLASSE **/
    /**####################**/
    private void listaEstacoesSpinner(){

        final ArrayList<String> arrayListEstacao = new ArrayList<String>();
        try {

            final String finalIdValvulaClicada = "";
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

                                                arrayListEstacao.add(estacao.getNomeEstacao());

                                            }

                                        }

                                        ArrayAdapter<String> arrayAdapterEstacao = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, arrayListEstacao);

                                        spnEstacoes.getBackground().setColorFilter(Color.parseColor("#FFFFFF"),PorterDuff.Mode.SRC_ATOP);
                                        spnEstacoes.setAdapter( arrayAdapterEstacao );
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
