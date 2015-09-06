package cn.bingoogolapple.xmpp.util;

import cn.bingoogolapple.xmpp.service.IMService;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/6 下午4:24
 * 描述:
 */
public class BusinessUtil {
    private BusinessUtil() {
    }

    public static String getParticipant(String participant) {
        return participant.substring(0, participant.indexOf("@")) + "@" + IMService.SERVICE_NAME;
    }

    public static String getNickname(String account) {
        return account.substring(0, account.indexOf("@"));
    }
}