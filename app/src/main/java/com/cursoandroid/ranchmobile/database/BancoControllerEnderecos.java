package com.cursoandroid.ranchmobile.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BancoControllerEnderecos {

    private SQLiteDatabase sqLiteDatabase;
    private BancoDadosLocal banco;

    public BancoControllerEnderecos (Context context){
        banco = new BancoDadosLocal( context );
    }

    public String salvaDados( String endereco ){
        ContentValues valores;
        long resultado;

        sqLiteDatabase = banco.getWritableDatabase();

        valores = new ContentValues();

        valores.put(banco.ENDERECO, endereco);

        resultado = sqLiteDatabase.insert(banco.TABELA_ENDERECO, null, valores);

        sqLiteDatabase.close();

        if ( resultado == -1 ) {
            return "Erro ao inserir o registro!";
        } else {
            return "Registrado com sucesso!";
        }

    }

    public Cursor consultaEndereco(){

        Cursor cursor;

        String[] campos = {
                banco.ID_ENDERECO,
                banco.ENDERECO
        };

        sqLiteDatabase = banco.getReadableDatabase();

        cursor = sqLiteDatabase.query(banco.TABELA_ENDERECO , campos, null, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        sqLiteDatabase.close();

        return cursor;
    }

    public void atualizaDados(final String id, final String endereco){

        new Thread(){
            public void run(){
                ContentValues valores;
                String where;

                sqLiteDatabase = banco.getWritableDatabase();

                where =  BancoDadosLocal.ID_ENDERECO + " = " + id;

                try{

                    valores = new ContentValues();
                    valores.put(banco.ENDERECO, endereco);

                    sqLiteDatabase.update(banco.TABELA_ENDERECO, valores, where, null);

                    sqLiteDatabase.close();

                } catch (Exception e){

                }

            }

        }.start();

    }

}
