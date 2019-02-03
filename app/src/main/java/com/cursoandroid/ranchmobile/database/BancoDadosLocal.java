package com.cursoandroid.ranchmobile.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BancoDadosLocal extends SQLiteOpenHelper {

    /** NOME DO BANCO **/
    public static final String NOME_BANCO = "sysfarm.db";

    /** VERSAO BANCO **/
    public static final int VERSAO = 1;

    /**DADOS DA TABELA COMARCA**/
    public static final String ID_ENDERECO = "_id";
    public static final String TABELA_ENDERECO = "enderecos";
    public static final String ENDERECO = "endereco";

    public BancoDadosLocal (Context context){

        super(context, NOME_BANCO, null, VERSAO);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sqlCreateTableEnderecos = "CREATE TABLE "
                + TABELA_ENDERECO
                + "(" + ID_ENDERECO + " integer primary key autoincrement,"
                + ENDERECO + " text )";

        db.execSQL( sqlCreateTableEnderecos );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlUpgradeTableEnderecos = "DROP TABLE IF EXISTS " + TABELA_ENDERECO;

        db.execSQL( sqlUpgradeTableEnderecos );

        onCreate( db );
    }
}
