package onenet.edp;

import onenet.edp.Common.MsgType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 上报固件信息消息封装
 * Created by yonghua on 2017/6/23.
 */
public class UpdateMsg extends EdpMsg {

    public UpdateMsg(byte msgType) {
        super(MsgType.UPDATEREQ);
    }

    /**
     * 封装上报固件消息
     * @param softInfos 软件信息列表
     * @return  update packet
     * @throws IllegalArgumentException if softInfos invalid
     */
    public byte[] packMsg(Collection<SoftInfo> softInfos) throws IllegalArgumentException {
        if (softInfos == null || softInfos.size() == 0) {
            throw new IllegalArgumentException("softInfos empty");
        }

        List<ByteBuffer>  buffers = new ArrayList<>(softInfos.size());
        int byteAllSize = 0;
        for (SoftInfo softInfo : softInfos) {
            int bufferSize = 4 + softInfo.getNameVersionlength();
            byteAllSize += bufferSize;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize).order(ByteOrder.BIG_ENDIAN);
            buffer.putShort((short)softInfo.getName().length())
                    .put(softInfo.getName().getBytes())
                    .putShort((short)softInfo.getVersion().length())
                    .put(softInfo.getVersion().getBytes());
            buffers.add(buffer);
        }

        ByteBuffer infoBuffer = ByteBuffer.allocate(byteAllSize);
        for (ByteBuffer buffer : buffers) {
            infoBuffer.put(buffer.array());
        }
        return packPkg(infoBuffer.array());
    }
}
