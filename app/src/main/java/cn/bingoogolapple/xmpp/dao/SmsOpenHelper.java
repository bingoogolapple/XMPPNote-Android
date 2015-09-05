package cn.bingoogolapple.xmpp.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/3 下午5:17
 * 描述:
 */
public class SmsOpenHelper extends SQLiteOpenHelper {
    public static final String T_SMS = "t_sms";

    public SmsOpenHelper(Context context) {
        super(context, "sms.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE " + T_SMS + " ( ");
        sql.append("_id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sql.append(SmsTable.FROM_ACCOUNT + " TEXT, ");
        sql.append(SmsTable.TO_ACCOUNT + " TEXT, ");
        sql.append(SmsTable.BODY + " TEXT, ");
        sql.append(SmsTable.STATUS + " TEXT, ");
        sql.append(SmsTable.TYPE + " TEXT, ");
        sql.append(SmsTable.TIME + " TEXT, ");
        sql.append(SmsTable.SESSION_ACCOUNT + " TEXT ");
        sql.append(");");
        db.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public interface SmsTable extends BaseColumns {
        // BaseColumns接口默认添加了列_id
        String FROM_ACCOUNT = "from_account";
        String TO_ACCOUNT = "to_account";
        String BODY = "body";
        String STATUS = "status";
        String TYPE = "type";
        String TIME = "time";
        String SESSION_ACCOUNT = "session_account";
    }

}