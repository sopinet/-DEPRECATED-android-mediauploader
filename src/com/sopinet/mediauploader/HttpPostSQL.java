package com.sopinet.mediauploader;
 
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class HttpPostSQL extends SQLiteOpenHelper {
	private static HttpPostSQL mInstance = null;
	private SQLiteDatabase iReadable = null;
	private SQLiteDatabase iWritable = null;
	private Context mCxt;
	private static final String DATABASE_NAME = "DBHttpPost";
	private static final int DATABASE_VERSION = 6;
	
    //Sentencia SQL para crear la tabla de Usuarios
    String sqlCreate = "CREATE TABLE http_index (indice INTEGER PRIMARY KEY AUTOINCREMENT, status TEXT, porcentage INTEGER, item TEXT)";
    String sqlCreate2 = "CREATE TABLE http_string (indice INTEGER, key TEXT, value TEXT)";
 
    public static HttpPostSQL getInstance(Context ctx) {
        if (mInstance == null) {
        	Log.d("TEMA", "CREADA: "+ctx.toString());
            mInstance = new HttpPostSQL(ctx.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
        }
        return mInstance;
    }
    
    public SQLiteDatabase getReadable() {
    	if (mInstance.iReadable == null) {
    		mInstance.iReadable = mInstance.getReadableDatabase();
    	}
    	return mInstance.iReadable;
    }
    
    public SQLiteDatabase getWritable() {
    	if (mInstance.iWritable == null) {
    		mInstance.iWritable = mInstance.getWritableDatabase();
    	}
    	return mInstance.iWritable;
    }
    
    private HttpPostSQL(Context contexto, String nombre,
                               CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
        this.mCxt = contexto;
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creación de la tabla
        db.execSQL(sqlCreate);
        db.execSQL(sqlCreate2);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        //NOTA: Por simplicidad del ejemplo aquí utilizamos directamente la opción de
        //      eliminar la tabla anterior y crearla de nuevo vacía con el nuevo formato.
        //      Sin embargo lo normal será que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este método debería ser más elaborado.
 
        //Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS http_index");
        db.execSQL("DROP TABLE IF EXISTS http_string");
 
        //Se crea la nueva versión de la tabla
        db.execSQL(sqlCreate);
        db.execSQL(sqlCreate2);
    } 
}