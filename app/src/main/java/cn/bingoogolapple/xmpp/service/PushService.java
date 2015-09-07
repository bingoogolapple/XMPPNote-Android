package cn.bingoogolapple.xmpp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import cn.bingoogolapple.xmpp.util.ToastUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/7 下午1:22
 * 描述:
 */
public class PushService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        IMService.sConn.addPacketListener(new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                Message message = (Message) packet;
                ToastUtil.showSafe(message.getBody());
            }
        }, new PacketFilter() {
            @Override
            public boolean accept(Packet packet) {
                return IMService.SERVICE_NAME.equals(packet.getFrom());
            }
        });
        super.onCreate();
    }

}