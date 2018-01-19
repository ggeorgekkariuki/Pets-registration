package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by PROBOOK on 16-Jan-18.
 */

public final class PetDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Shelter.db";
    public static final int DATABASE_VERSION = 1;

    //The CRUD Strings
    public String STRING_SQL_CREATE_PETS_TABLE = "CREATE TABLE " + PetContract.PetEntry.TABLE_NAME +
            " (" + PetContract.PetEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PetContract.PetEntry.COLUMN_NAME + " TEXT NOT NULL, "
            + PetContract.PetEntry.COLUMN_BREED + " TEXT, "
            + PetContract.PetEntry.COLUMN_GENDER + " INTEGER NOT NULL, "
            + PetContract.PetEntry.COLUMN_WEIGHT + " INTEGER DEFAULT 0" + ")";


    public PetDBHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(STRING_SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ PetContract.PetEntry.TABLE_NAME);
        onCreate(db);
    }
}
