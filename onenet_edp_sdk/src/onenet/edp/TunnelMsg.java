package onenet.edp;

import onenet.edp.Common.MsgType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 透传第三方平台数据消息封装
 * Created by yonghua on 2017/6/23.
 */
public class TunnelMsg extends EdpMsg {
    private String server;
    private byte[] data;
    public static final int DATA_MAX_SIZE = 16 * 1024;

    public TunnelMsg() {
        super(MsgType.TUNNEL);
    }

    /**
     * 封装透传第三方平台数据消息
     * @param server    第三方服务名称
     * @param data  透传的数据
     * @return  tunnel packet
     * @throws IllegalArgumentException if server or data invalid
     */
    public byte[] packMsg(String server, byte[] data) throws IllegalArgumentException {
        if (server == null || server.length() == 0 || server.length() > Short.MAX_VALUE || data == null
                || data.length > DATA_MAX_SIZE) {
            throw new IllegalArgumentException("server or data invalid");
        }

        ByteBuffer buffer = ByteBuffer.allocate(2 + server.length() + data.length).order(ByteOrder.BIG_ENDIAN);
        buffer.putShort((short)server.length())
                .put(server.getBytes())
                .put(data);
        return packPkg(buffer.array());
    }

    /**
     * 解析透传数据包
     * @param msgData completely tunnel packet
     * @throws IOException if unpack fail
     */
    @Override
    public void unpackMsg(byte[] msgData) throws IOException {
        int dataLen = msgData.length;
        int position = 0;
        int remain = dataLen;
        if (dataLen < 2) {
            throw new IOException("packet is not completely");
        }
        int serverSize = Common.twoByteToLen(msgData[0], msgData[1]);
        position += 2;
        remain -= 2;
        if (remain < serverSize) {
            throw new IOException("packet is not completely");
        }
        this.server = new String(msgData, 2, serverSize);
        position += serverSize;
        remain -= serverSize;
        if (remain < 1) {
            throw new IOException("packet is not completely");
        }
        this.data = new byte[remain];
        System.arraycopy(msgData, position, this.data, 0, remain);
    }

    public String getServer() {
        return server;
    }

    public byte[] getData() {
        return data;
    }
}
