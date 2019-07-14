package dab.scuffedbots;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class subdatabasenignog extends SQLiteOpenHelper {

    //variables
    public static final String DATABASE_NAME = "Laya.db"; //not case sensitive
    public static final String TABLE_NAME = "Parameters";
    public static final String COL_1 = "_ID";
    public static final String COL_2 = "CHECKED";

    //constructor functions (selected 1st one)
    //Database creator function
    public subdatabasenignog(Context context) {
        super(context, DATABASE_NAME, null, 1);

        //line just for checking
        //SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    }

    //impelment functions
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
                sqLiteDatabase.execSQL("CREATE TABLE " + "Parameters" + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_2 + " TEXT " + ");");
                //create a table whenever oncreate is called
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "Parameters");
        onCreate(sqLiteDatabase);
    }

    //inputting data
    public boolean insertData(String CHECKED){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, CHECKED);
        long result = sqLiteDatabase.insert("Parameters", null, contentValues); //returns -1 if failed to add
        if(result == -1) return false;
        else return true;
    }

    //outputting data
    public Cursor getAllDate() {
        //create database class
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //instance
        return sqLiteDatabase.rawQuery("select * from " + "Parameters" + ";", null);
    }

    //modify data
    public boolean updateData(String _ID, String CHECKED){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, _ID);
        contentValues.put(COL_2, CHECKED);
        sqLiteDatabase.update("Parameters", contentValues, COL_1 + "=?", new String[] { _ID });
        return true;
    }
}
