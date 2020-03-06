package shadow.android.data_entry_1.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static shadow.android.data_entry_1.client.ClientFragment.BREAKFAST;
import static shadow.android.data_entry_1.client.ClientFragment.DINNER;
import static shadow.android.data_entry_1.client.ClientFragment.LUNCH;

public class DBController {
    private SQLiteDatabase database;
    private static DBController dbcontroller;

    private DBController(Context context) {
        DBHelper helper=new DBHelper(context);
        database=helper.getWritableDatabase();
    }

    private static DBController getDBController(Context context) {
        if(dbcontroller==null)
        dbcontroller=new DBController(context);
        return dbcontroller;
    }


    public static boolean addClient(Context context, String title,byte[] bytes){

        ContentValues contentValues=new ContentValues();
        contentValues.put(DBContract.ClientTable.COL_NAME,title);
        if(bytes!=null)
        contentValues.put(DBContract.ClientTable.COL_THUMP, bytes);
        long id=getDBController(context).database.insert(DBContract.ClientTable.TABLE_NAME,
                                                 null,
                contentValues
                );
        if(id!=0) Log.i("addClient",""+id);
        return id != 0;
    }

    public static long removeClient(Context context,long client){

        return getDBController(context).database.delete(DBContract.ClientTable.TABLE_NAME,
                DBContract.ClientTable._ID+"=?",new String[]{""+client}
        );
    }

    public static List<Client> getClients(Context context){
        String[]projection=new String[]{DBContract.ClientTable._ID, DBContract.ClientTable.COL_NAME, DBContract.ClientTable.COL_THUMP};
        Cursor cursor=getDBController(context).database.query(DBContract.ClientTable.TABLE_NAME,projection,null,null,null,null,null);
        List<Client> clients =new ArrayList<>();
        Client client;
        while (cursor.moveToNext()){
            client =new Client(0,"",new byte[0]);
            client.setId(cursor.getLong(cursor.getColumnIndex(DBContract.ClientTable._ID)));
            client.setName(cursor.getString(cursor.getColumnIndex(DBContract.ClientTable.COL_NAME)));
            client.setThump(cursor.getBlob(cursor.getColumnIndex(DBContract.ClientTable.COL_THUMP)));
            clients.add(client);
        }
        cursor.close();
        return clients;
    }
    public static Day addDay(Context context, long client, Date date, double breakfast, double lunch, double dinner, long period){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DBContract.DayTable.COL_DAY,date.getTime());
        contentValues.put(DBContract.DayTable.COL_BREAKFAST,breakfast);
        contentValues.put(DBContract.DayTable.COL_LUNCH,lunch);
        contentValues.put(DBContract.DayTable.COL_DINNER,dinner);
        contentValues.put(DBContract.DayTable.COL_PERIOD,period);
        long id=getDBController(context).database.insert(DBContract.DayTable.TABLE_NAME,
                null,
                contentValues
        );
        if(id!=0) Log.i("addDay",""+id+"\tfor client: "+client+"\tfor date"+date);
        return new Day(id,date,breakfast,lunch,dinner,period);
    }
    public static long updateDay(Context context, long day, String type, double value){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DBContract.DayTable._ID,day);
        switch (type){
            case BREAKFAST:
                contentValues.put(DBContract.DayTable.COL_BREAKFAST,value);
                break;
            case LUNCH:
                contentValues.put(DBContract.DayTable.COL_LUNCH,value);
                break;
            case DINNER:
                contentValues.put(DBContract.DayTable.COL_DINNER,value);
                break;
        }

        long id=getDBController(context).database.update(DBContract.DayTable.TABLE_NAME,
                contentValues
                ,DBContract.DayTable._ID+"=?",new String[]{""+day}
        );
        Log.i(DBController.class.getSimpleName(),"updateDay: "+id+" price= "+value);
        return id;
    }

    public static List<Day> getDays(Context context, long client, long period){
        //String[]projection=new String[]{DBContract.MealTable._ID, DBContract.MealTable.COL_NAME, DBContract.MealTable.COL_THUMP};
        Cursor cursor=getDBController(context).database.query(DBContract.DayTable.TABLE_NAME,null,
                DBContract.DayTable.COL_PERIOD+"=?",
                new String[]{""+period},null,null,null);
        List<Day> days =new ArrayList<>();
        Day day;
        while (cursor.moveToNext()){
            day =new Day();
            day.setId(cursor.getLong(cursor.getColumnIndex(DBContract.DayTable._ID)));
            day.setDay(cursor.getLong(cursor.getColumnIndex(DBContract.DayTable.COL_DAY)));
            day.setBreakfast(cursor.getDouble(cursor.getColumnIndex(DBContract.DayTable.COL_BREAKFAST)));
            day.setLunch(cursor.getDouble(cursor.getColumnIndex(DBContract.DayTable.COL_LUNCH)));
            day.setDinner(cursor.getDouble(cursor.getColumnIndex(DBContract.DayTable.COL_DINNER)));
            day.setPeriod(cursor.getLong(cursor.getColumnIndex(DBContract.DayTable.COL_PERIOD)));
            day.setBreakfastInfo(cursor.getString(cursor.getColumnIndex(DBContract.DayTable.COL_INFO_BREAKFAST)));
            day.setLunchInfo(cursor.getString(cursor.getColumnIndex(DBContract.DayTable.COL_INFO_LUNCH)));
            day.setDinnerInfo(cursor.getString(cursor.getColumnIndex(DBContract.DayTable.COL_INFO_DINNER)));
            days.add(day);
        }
        long x=(getPeriod(context,1).getEnd().getTime()-getPeriod(context,1).getStart().getTime())/86400000;
        x++;
        Log.i("period",""+x);
        // we add the last day
        cursor.close();
        return days;
    }
    public static long updateDayInfo(Context context,long day,String type,String value,Day d){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DBContract.DayTable._ID,day);
        switch (type){
            case BREAKFAST:
                contentValues.put(DBContract.DayTable.COL_INFO_BREAKFAST,value);
                d.setBreakfastInfo(value);
                break;
            case LUNCH:
                contentValues.put(DBContract.DayTable.COL_INFO_LUNCH,value);
                d.setLunchInfo(value);
                break;
            case DINNER:
                contentValues.put(DBContract.DayTable.COL_INFO_DINNER,value);
                d.setDinnerInfo(value);
                break;
        }
        long id=getDBController(context).database.update(DBContract.DayTable.TABLE_NAME,
                contentValues
                ,DBContract.DayTable._ID+"=?",new String[]{""+day}
        );
        Log.i(DBController.class.getSimpleName(),"updateDay: "+id+" price= "+value);
        return id;
    }


    public static long addPeriod(Context context,long from,long to,long client){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DBContract.PeriodTable.COL_START,from);
        contentValues.put(DBContract.PeriodTable.COL_END,to);
        contentValues.put(DBContract.PeriodTable.COL_CLIENT,client);
        long id=getDBController(context).database.insert(DBContract.PeriodTable.TABLE_NAME,
                null,
                contentValues
        );
        return id;
    }
    public static long removePeriod(Context context,long period){

        return getDBController(context).database.delete(DBContract.PeriodTable.TABLE_NAME,
                DBContract.PeriodTable._ID+"=?",new String[]{""+period}
        );
    }
    public static Period getPeriod(Context context,long client){
        Period period=new Period();
        Cursor cursor=getDBController(context).database.query(DBContract.PeriodTable.TABLE_NAME,null,
                DBContract.PeriodTable.COL_CLIENT+"=?",new String[]{""+client},null,null,null);
        while (cursor.moveToNext()) {
            period.setId(cursor.getLong(cursor.getColumnIndex(DBContract.PeriodTable._ID)));
            period.setStart(cursor.getLong(cursor.getColumnIndex(DBContract.PeriodTable.COL_START)));
            period.setEnd(cursor.getLong(cursor.getColumnIndex(DBContract.PeriodTable.COL_END)));
            period.setClient(cursor.getLong(cursor.getColumnIndex(DBContract.PeriodTable.COL_CLIENT)));
        }
        cursor.close();
        return period;
    }



}
