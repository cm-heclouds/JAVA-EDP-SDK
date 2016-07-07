package onenet.edp;

import onenet.edp.Common.MsgType;

import java.io.IOException;

/**
 * connect close message
 * Created by yonghua on 2016/7/6.
 */
public class ConnectCloseMsg extends EdpMsg {
    private byte errorCode;

    public ConnectCloseMsg() {
        super(MsgType.CONNCLOSE);
    }

    @Override
    public void unpackMsg(byte[] msgData) throws IOException{
        int dataLen = msgData.length;
        if (dataLen != 1) {
            throw new IOException("packet size invalid. size:" + dataLen);
        }
        errorCode = msgData[0];
    }

    public byte getErrorCode() {
        return this.errorCode;
    }
}
