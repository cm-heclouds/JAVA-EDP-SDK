package onenet.edp;

import onenet.edp.Common.MsgType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * cmd responce
 * Created by yonghua on 2016/7/4.
 */
public class CmdRespMsg extends EdpMsg {

    public CmdRespMsg() {
        super(MsgType.CMDRESP);
    }

    public byte[] packMsg(byte[] cmdid, byte[] cmdBody) {
        if (cmdid == null || cmdBody == null) {
            return null;
        }

        int twoLenMaxSize = 0xffff;
        int cmdidLen = cmdid.length;
        int cmdBodyLen = cmdBody.length;
        if (cmdidLen > twoLenMaxSize || cmdBodyLen > 64 * 1024) {
            return null;
        }

        int msgSize = 2 + cmdidLen + 4 + cmdBodyLen;
        ByteBuffer buf = ByteBuffer.allocate(msgSize).order(ByteOrder.BIG_ENDIAN);
        short cmdidSize = (short)(cmdidLen & 0xffff);
        buf.putShort(cmdidSize);
        buf.put(cmdid);
        buf.putInt(cmdBodyLen);
        buf.put(cmdBody);

        int packetSize = buf.position();
        byte[] packet = new byte[packetSize];
        buf.flip();
        buf.get(packet);
        return packPkg(packet);
    }
}
