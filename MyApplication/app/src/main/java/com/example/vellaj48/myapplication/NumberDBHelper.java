package com.example.vellaj48.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by vellaj48 on 3/30/2015.
 */
public class NumberDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NumberReader.db";

    public NumberDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //All you need to do is call getWritableDatabase() or getReadableDatabase().
    //Note: Because they can be long-running, be sure that you call getWritableDatabase() or getReadableDatabase() in a background thread, such as with AsyncTask or IntentService.
    //To use SQLiteOpenHelper, create a subclass that overrides the onCreate(), onUpgrade() and onOpen() callback methods. You may also want to implement onDowngrade(), but it's not required.
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " +
                NumberContract.NumberEntry.USER_TABLE_NAME + " (" +
                NumberContract.NumberEntry._ID + " INTEGER PRIMARY KEY," +
                NumberContract.NumberEntry.COLUMN_NAME_ID + " TEXT," +
                NumberContract.NumberEntry.COLUMN_NAME_VALUE + " TEXT" + " )");

        db.execSQL("CREATE TABLE " +
                NumberContract.VariableEntry.VARIABLE_TABLE_NAME + " (" +
                NumberContract.VariableEntry.VARIABLE_NAME + " TEXT," +
                NumberContract.VariableEntry.VARIABLE_CURRENT_USER + " TEXT," +
                NumberContract.VariableEntry.VARIABLE_EQUATION + " TEXT" + " )");

        db.execSQL("CREATE TABLE " +
                NumberContract.EquationEntry.EQUATION_TABLE_NAME + " (" +
                NumberContract.EquationEntry.EQUATION_CURRENT_USER + " TEXT," +
                NumberContract.EquationEntry.SAVED_EQUATION_ONE + " TEXT," +
                NumberContract.EquationEntry.SAVED_EQUATION_ONE_NAME + " TEXT," +
              //  NumberContract.EquationEntry.SAVED_EQUATION_ONE__USED_VARIABLES + " TEXT," +
                NumberContract.EquationEntry.SAVED_EQUATION_TWO + " TEXT," +
                NumberContract.EquationEntry.SAVED_EQUATION_TWO_NAME + " TEXT," +
                //  NumberContract.EquationEntry.SAVED_EQUATION_TWO__USED_VARIABLES + " TEXT," +
                NumberContract.EquationEntry.SAVED_EQUATION_THREE + " TEXT," +
                NumberContract.EquationEntry.SAVED_EQUATION_THREE_NAME + " TEXT" +" )");
        //  NumberContract.EquationEntry.SAVED_EQUATION_THREE__USED_VARIABLES + " TEXT," +

        /*
           public static final String EQUATION_TABLE_NAME = "equations";
        public static final String EQUATION_CURRENT_USER = "equation_current_user";
        public static final String SAVED_EQUATION_ONE = "equation1";
        public static final String SAVED_EQUATION_ONE_NAME = "equation1_name";
        public static final String SAVED_EQUATION_TWO = "equation2";
        public static final String SAVED_EQUATION_TWO_NAME  = "equation2_name";
        public static final String SAVED_EQUATION_THREE = "equation3";
        public static final String SAVED_EQUATION_THREE_NAME  = "equation3_name";
        // SELECT equations, equation_current_user, equation1, equation1_name, equation2, equation2_name, equation3, equation3_name FROM equations
         */
        //
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + NumberContract.NumberEntry.USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NumberContract.VariableEntry.VARIABLE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NumberContract.EquationEntry.EQUATION_TABLE_NAME);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}

