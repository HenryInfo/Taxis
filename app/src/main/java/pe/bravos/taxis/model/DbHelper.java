package pe.bravos.taxis.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hbs on 13/12/16.
 */

public class DbHelper extends SQLiteOpenHelper {
    final static String BD="CREATE TABLE Alumnos(_idalumno INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombres TEXT, apellidos TEXT, n1 int, n2 int, n3 int, n4 int, pm DECIMAL(4,2))";
    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Alumnos");
        db.execSQL(BD);

    }
}
