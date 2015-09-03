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
public class ContactOpenHelper extends SQLiteOpenHelper {
    public static final String T_CONTACT = "t_contact";

    public ContactOpenHelper(Context context) {
        super(context, "contact.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE " + T_CONTACT + " ( ");
        sql.append("_id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sql.append(ContactTable.ACCOUNT + " TEXT, ");
        sql.append(ContactTable.NICKNAME + " TEXT, ");
        sql.append(ContactTable.AVATAR + " TEXT, ");
        sql.append(ContactTable.PINYIN + " TEXT ");
        sql.append(");");
        db.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public interface ContactTable extends BaseColumns {
        // BaseColumns接口默认添加了列_id
        String ACCOUNT = "account";
        String NICKNAME = "nickname";
        String AVATAR = "avatar";
        String PINYIN = "pinyin";
    }

}