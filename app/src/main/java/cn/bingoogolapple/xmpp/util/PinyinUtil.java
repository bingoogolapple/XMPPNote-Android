package cn.bingoogolapple.xmpp.util;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/3 下午10:50
 * 描述:
 */
public class PinyinUtil {

    private PinyinUtil() {
    }

    public static String getPinyin(String str) {
        return PinyinHelper.convertToPinyinString(str, "", PinyinFormat.WITHOUT_TONE);
    }

}