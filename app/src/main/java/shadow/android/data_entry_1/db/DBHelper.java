package shadow.android.data_entry_1.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private final static String DB_NAME="mies";
    private final static int DB_VERSION=1;
    //
    private final String TYPE_TEXT=" text ";
    private final String TYPE_INT=" integer ";
    private final String TYPE_DOUBLE=" double ";
    private final String TYPE_BOOLEAN=" numeric ";
    private final String TYPE_BLOB=" blob ";
    private final String CM=" , ";
    private final String LP=" ( ";
    private final String RP=" ) ";
    private final String NOT_NULL=" not null ";
    private final String CREATE_TABLE_CLIENT ="create table "+ DBContract.ClientTable.TABLE_NAME+LP+ DBContract.ClientTable._ID
            +TYPE_INT+NOT_NULL+"PRIMARY KEY AUTOINCREMENT"+CM+ DBContract.ClientTable.COL_NAME +TYPE_TEXT+CM
            + DBContract.ClientTable.COL_THUMP+TYPE_BLOB+RP+" ;";

    private final String CREATE_TABLE_DAY ="create table "+DBContract.DayTable.TABLE_NAME+LP+DBContract.DayTable._ID
            +TYPE_INT+NOT_NULL+"PRIMARY KEY AUTOINCREMENT"+CM+DBContract.DayTable.COL_DAY
            +TYPE_INT+CM+DBContract.DayTable.COL_BREAKFAST +TYPE_DOUBLE+CM+DBContract.DayTable.COL_LUNCH +TYPE_DOUBLE+CM
            +DBContract.DayTable.COL_DINNER +TYPE_DOUBLE+CM+DBContract.DayTable.COL_INFO_BREAKFAST+TYPE_TEXT+CM+
            DBContract.DayTable.COL_INFO_LUNCH+TYPE_TEXT+CM+DBContract.DayTable.COL_INFO_DINNER+CM
            +DBContract.DayTable.COL_PERIOD+TYPE_INT+CM+"FOREIGN KEY(period) references period(_id)"+RP+";";

    private final String CREATE_TABLE_PERIOD ="create table "+ DBContract.PeriodTable.TABLE_NAME+LP+ DBContract.PeriodTable._ID
            +TYPE_INT+NOT_NULL+"PRIMARY KEY AUTOINCREMENT"+CM+ DBContract.PeriodTable.COL_START +TYPE_INT+CM
            + DBContract.PeriodTable.COL_END+TYPE_INT+CM+DBContract.PeriodTable.COL_CLIENT+TYPE_INT+
            CM+"FOREIGN KEY(client) references client(_id)"+RP+";";
    private final String DROP_TABLE_CLIENT="drop table if exists "+ DBContract.ClientTable.TABLE_NAME;


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public DBHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CLIENT);
        db.execSQL(CREATE_TABLE_DAY);
        db.execSQL(CREATE_TABLE_PERIOD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //todo: set upgrade policy based on your app's logic
    }
}
