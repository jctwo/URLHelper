package org.nosreme.app.urlhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RuleStore {
	private static final String RULESTORE_TABLE_NAME = "rules";

	static class RuleDbHelper extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "rulestore.db";
		private static final int DATABASE_VERSION = 1;

		private static final String RULES_TABLE_CREATE =
				/* rulestore table stores a list of rules to decide what to
				 * do with a URL.
				 */
				"CREATE TABLE " + RULESTORE_TABLE_NAME + " (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"order INTEGER," +
				"regex TEXT," +
				"action TEXT," +  /* Encoded list of handlers */
				");";

		RuleDbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(RULES_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			assert false;
		}
	}

	private RuleDbHelper dbhelper;

	public RuleStore(Context context)
	{
		dbhelper = new RuleDbHelper(context);
	}
}
