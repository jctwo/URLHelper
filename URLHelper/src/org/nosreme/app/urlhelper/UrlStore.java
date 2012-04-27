package org.nosreme.app.urlhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UrlStore {
	   private static final String URLSTORE_TABLE_NAME = "urls";
	     
	   static class DbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "urlstore.db";
        private static final int DATABASE_VERSION = 2;
        
        private static final String URLSTORE_TABLE_CREATE =
                    "CREATE TABLE " + URLSTORE_TABLE_NAME + " (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "URL TEXT," +
                    "BLAH TEXT);";

        DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        
         @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(URLSTORE_TABLE_CREATE);
            ContentValues values = new ContentValues();
            values.put("URL", "http://www.nosreme.org");
            values.put("_id", 1);            
			db.insert(URLSTORE_TABLE_NAME, "URL", values);
            values.put("URL", "blah blah");
            values.put("_id", 2);            
			db.insert(URLSTORE_TABLE_NAME, "URL", values);

        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            // Kills the table and existing data
            db.execSQL("DROP TABLE IF EXISTS " + URLSTORE_TABLE_NAME);

            // Recreates the database with a new version
            onCreate(db);
        }
    }
    
    private DbHelper dbhelper;
    
    public UrlStore(Context context)
    {
    	dbhelper = new DbHelper(context);
    }
    
    private String[] cols = new String[] { "_id", "URL" };
    
    public Cursor getUrlCursor()
    {
    	SQLiteDatabase db = dbhelper.getReadableDatabase();
    	
    	Cursor cursor = db.query(URLSTORE_TABLE_NAME, cols, null, null, null, null, null);
    	
    	return cursor;
    	
    }
    public void addUrl(String url)
    {
    	SQLiteDatabase db = dbhelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("URL", url);         
		db.insert(URLSTORE_TABLE_NAME, "URL", values);
    }


}
