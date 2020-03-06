package shadow.android.data_entry_1.db;

import android.provider.BaseColumns;

public class DBContract {

    static abstract class DayTable implements BaseColumns {
        static final String TABLE_NAME = "day";
        static final String COL_DAY = "day";
        static final String COL_BREAKFAST = "breakfast";
        static final String COL_LUNCH = "lunch";
        static final String COL_DINNER = "dinner";
        static final String COL_INFO_BREAKFAST = "info_breakfast";
        static final String COL_INFO_LUNCH = "info_lunch";
        static final String COL_INFO_DINNER = "info_dinner";
        static final String COL_PERIOD="period";
    }
    static abstract class PeriodTable implements BaseColumns{
        static final String TABLE_NAME = "period";
        static final String COL_START = "start";
        static final String COL_END = "end";
        static final String COL_CLIENT="client";
    }
    public static class ClientTable implements BaseColumns{
        public static final String TABLE_NAME="client";
        public static final String COL_NAME ="title";
        public static final String COL_THUMP="thump";
    }
}
