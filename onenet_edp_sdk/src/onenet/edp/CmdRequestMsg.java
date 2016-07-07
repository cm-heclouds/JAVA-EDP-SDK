package onenet.edp;

import onenet.edp.Common.MsgType;

import java.io.IOException;

/**
 * cmd request
 * Created by yonghua on 2016/7/4.
 */
public class CmdRequestMsg extends EdpMsg {
    private byte[] cmdid;
    private byte[] cmdBody;

    public CmdRequestMsg() {
        super(MsgType.CMDREQ);
    }

    @Override
    public void unpackMsg(byte[] msgData) throws IOException {
        int dataLen = msgData.length;
        if (dataLen <= 2) {
            throw new IOException("packet size too short. size:" + dataLen);
        }

        int cmdidLen = Common.twoByteToLen(msgData[0], msgData[1]);
        int dataRemain = dataLen - 2;
        if (dataRemain < cmdidLen + 4) {
            throw new IOException("packet size too short. cmdid size:" + cmdidLen);
        }
        cmdid = new byte[cmdidLen];
        System.arraycopy(msgData, 2, cmdid, 0, cmdidLen);
        int cmdBodyLen = Common.fourByteToLen(msgData[2 + cmdidLen],
                msgData[3 + cmdidLen],
                msgData[4 + cmdidLen],
                msgData[5 + cmdidLen]);
        dataRemain = dataRemain - cmdidLen - 4;
        if (dataRemain != cmdBodyLen) {
            throw  new IOException("packet size invalid.");
        } else {
            cmdBody = new byte[cmdBodyLen];
            System.arraycopy(msgData, 6 + cmdidLen, cmdBody, 0, cmdBodyLen);
        }
    }

    public byte[] getCmdid() {
        return this.cmdid;
    }

    public byte[] getCmdBody() {
        return this.cmdBody;
    }
}
