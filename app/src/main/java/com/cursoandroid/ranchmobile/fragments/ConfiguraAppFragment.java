package com.cursoandroid.ranchmobile.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cursoandroid.ranchmobile.R;
import com.cursoandroid.ranchmobile.database.ConectaBanco;

public class ConfiguraAppFragment extends Fragment {

    private EditText enderecoEdit;
    private Button btSalvarEndereco;
//    private ConectaBanco conectaBanco = new ConectaBanco();

    public static String endereco;

    private static final String ARQUIVO_CONFIGURACAO = "ArquivoConfiguracao";

    public ConfiguraAppFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_configura_app, container, false);

        btSalvarEndereco = view.findViewById(R.id.bt_salvar_config);
        enderecoEdit = view.findViewById(R.id.edit_endereco);

        btSalvarEndereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if ( enderecoEdit.getText().toString().equals("") ){
                    Toast.makeText(getContext(), "Por favor, insira um endereço", Toast.LENGTH_LONG).show();
                } else {

                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(ARQUIVO_CONFIGURACAO, 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString( "endereco", enderecoEdit.getText().toString() );
                    editor.commit();

                    SharedPreferences sharedPreferencesResposta = getContext().getSharedPreferences(ARQUIVO_CONFIGURACAO, 0);

                    endereco = sharedPreferencesResposta.getString("endereco", null);

                    Log.i("testePreferencias", endereco);

                    Toast.makeText(getActivity(), "Configurado para o IP: " + endereco, Toast.LENGTH_LONG).show();
                }



            }
        });


        return view;
    }



}
