package onenet.edp;

import onenet.edp.Common.MsgType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 下发固件信息封装
 * Created by yonghua on 2017/6/23.
 */
public class UpdateRespMsg extends EdpMsg {
    private List<SoftInfo> softInfos;
    public UpdateRespMsg() {
        super(MsgType.UPDATERESP);
        softInfos = new ArrayList<>();
    }

    /**
     * 解析固件信息
     * @param msgData   received completely packet
     * @throws IOException  if unpack fail
     */
    @Override
    public void unpackMsg(byte[] msgData) throws IOException {
        int dataLen = msgData.length;

        int position = 0;
        int remain = dataLen;
        while (position < dataLen) {
            if (remain < 2) {
                throw new IOException("packet is not completely");
            }
            int nameSize = Common.twoByteToLen(msgData[position], msgData[position + 1]);
            position += 2;
            remain -= 2;
            if (remain < nameSize) {
                throw new IOException("packet is not completely");
            }
            String name = new String(msgData, position, nameSize);
            position += nameSize;
            remain -= nameSize;
            if (remain < 2) {
                throw new IOException("packet is not completely");
            }
            int versionSize = Common.twoByteToLen(msgData[position], msgData[position + 1]);
            position += 2;
            remain -= 2;
            if (remain < versionSize) {
                throw new IOException("packet is not completely");
            }
            String version = new String(msgData, position, versionSize);
            position += versionSize;
            remain -= versionSize;
            if (remain < 2) {
                throw new IOException("packet is not completely");
            }
            int urlSize = Common.twoByteToLen(msgData[position], msgData[position + 1]);
            position += 2;
            remain -= 2;
            if (remain < urlSize) {
                throw new IOException("packet is not completely");
            }
            String url = new String(msgData, position, urlSize);
            position += urlSize;
            remain -= urlSize;
            if (remain < SoftInfo.MD5_SIZE) {
                throw new IOException("packet is not completely");
            }
            String md5 = new String(msgData, position, SoftInfo.MD5_SIZE);
            position += SoftInfo.MD5_SIZE;
            remain -= SoftInfo.MD5_SIZE;
            SoftInfo softInfo = new SoftInfo(name, version, url, md5);
            this.softInfos.add(softInfo);
        }
    }

    public List<SoftInfo> getSoftInfos() {
        return softInfos;
    }
}
