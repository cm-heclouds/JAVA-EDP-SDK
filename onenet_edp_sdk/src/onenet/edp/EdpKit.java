package onenet.edp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import onenet.edp.Common.MsgType;

/*
 * function:	edp常用操作方法,处理报文的封装和解析，不参与报文的发送和接收
 * author:		yonghua
 * date:		2015/01/05
 * version:		0.0.1
 */

public class EdpKit {
	//默认参数
	private final int BUFFER_LEN = 1024;
	public static final String EDP_PROTOCOL = "EDP";
	
	private ByteBuffer cache;
	
	public EdpKit()
	{
		cache = ByteBuffer.allocate(BUFFER_LEN).order(ByteOrder.BIG_ENDIAN);
	}
	
	/**
	 * 检查缓存池容量是否足够使用
	 * @param packetSize packet size
	 * @return if cache capacity is enough ,return true; else increase cache capacity
	 * and return false.
	 */
	private boolean checkCacheCapacity(int packetSize){
		if (packetSize > cache.remaining()){
			int srcDataLen = cache.position();
			int newCacheLen = srcDataLen + packetSize;
			ByteBuffer newBuffer = ByteBuffer.allocate(newCacheLen);
			byte[] srcData = new byte[srcDataLen];
			cache.flip();
			cache.get(srcData);
			newBuffer.put(srcData);
			cache = newBuffer;
			return false;
		} else{
			return true;
		}
	}
	
	/**
	 * 从缓存池中提取完整edp包,若无，则返回null，缓存池做compact操作
	 * @return if cache has edp packet,return one packet;else return null.
	 */
	private EdpPacket popPacket(){
		if (cache.remaining() <= 1){
			cache.compact();
			return null;
		}
		
		EdpPacket packet = new EdpPacket();
		int startPos = cache.position();
		packet.type = cache.get();
		
		//获取包 消息剩余长度,字节数不确定，最多占四个字节
		int packetValidLen = cache.limit() - startPos;
		int packetRemainLen = 0;
		int remainLen = 0;
		byte firstLen = cache.get();
		if (firstLen < 0)
		{
			//当消息长度字节大于 1 个字节，消息的长度是可预期的，至少可往后预读取 3 个字节
			if (packetValidLen <= (startPos + 5))
			{
				return null;
			}
			
			byte secondLen = cache.get();
			if (secondLen >= 0)
			{
				packetRemainLen = packetValidLen - 3;
				remainLen = (secondLen << 7) + (firstLen & 0x7F);		
			}
			else
			{
				byte thirdLen = cache.get();
				if (thirdLen >= 0)
				{
					packetRemainLen = packetValidLen - 4;
					remainLen = (thirdLen << 14) + ((secondLen & 0x7F) << 7) + (firstLen & 0x7F);
				}
				else
				{
					byte fourthLen = cache.get();
					packetRemainLen = packetValidLen - 5;
					remainLen = ((fourthLen & 0x7F) << 21) + ((thirdLen & 0x7F) << 14) 
							+ ((secondLen & 0x7F) << 7) + (firstLen & 0x7F);
				}
			}
		}
		else
		{
			remainLen  = firstLen;
			packetRemainLen = packetValidLen - 2;
		}
		
		//收到的包长度，小于报头定义的长度
		if (packetRemainLen < remainLen)
		{
			cache.position(startPos);
			cache.compact();
			return null;
		}
		else
		{
			packet.dataLength = remainLen;
			packet.data = new byte[remainLen];
			cache.get(packet.data);
		}
		
		return packet;
	}

	/**
	 * 解析响应数据包，根据消息类型不同，解析成对应的消息
	 * @param packet edp packet
	 * @return edp message
	 * @throws IOException if resolve fail
	 */
	private EdpMsg resolvePacket(EdpPacket packet) throws IOException{
		if (packet == null){
			throw new IOException("packet is null");
		}
		
		//解析报文
		switch (packet.type)
		{
		case MsgType.CONNRESP:
			EdpMsg connRespMsg = new ConnectRespMsg();
			connRespMsg.unpackMsg(packet.data);
			return connRespMsg;
		case MsgType.PUSHDATA:
			EdpMsg pushDataMsg = new PushDataMsg();
			pushDataMsg.unpackMsg(packet.data);
			return pushDataMsg;
		case MsgType.SAVEDATA:
			EdpMsg saveDataMsg = new SaveDataMsg();
			saveDataMsg.unpackMsg(packet.data);
			return saveDataMsg;
		case MsgType.SAVERESP:
			EdpMsg saveRespMsg = new SaveRespMsg();
			saveRespMsg.unpackMsg(packet.data);
			return saveRespMsg;
		case MsgType.PINGRESP:
			EdpMsg pingRespMsg = new PingRespMsg();
			pingRespMsg.unpackMsg(packet.data);
			return pingRespMsg;
		case MsgType.CMDREQ:
			EdpMsg cmdRequestMsg = new CmdRequestMsg();
			cmdRequestMsg.unpackMsg(packet.data);
			return cmdRequestMsg;
		case MsgType.CONNCLOSE:
			EdpMsg connCloseMsg = new ConnectCloseMsg();
			connCloseMsg.unpackMsg(packet.data);
			return connCloseMsg;
		default:
			throw new IOException("resolve packet exception. not supported msg_type:" 
					+ packet.type);
		}	
	}
	
	/**
	 * 解析数据，对完整的报文进行内容解析
	 * @param packet edp responce packet ,maybe has more than one edp message.
	 * @return edp message list ,if don't has any complete message, return null.
	 */
	public List<EdpMsg> unpack(byte[] packet){
		List<EdpMsg> msgList = new ArrayList<EdpMsg>();
		if (packet == null || packet.length <= 0)
		{
			return null;
		}
		
		//若缓存池容量不够，扩容
		int packetLen = packet.length;
		checkCacheCapacity(packetLen);
		
		//添加新读取的数据到缓冲池
		cache.put(packet);
		
		//检验包完整性
		//查看包的长度是否偏少(包至少两个字节)		
		EdpPacket recvPacket = null;
		cache.flip();	//回调指针，准备数据读取
		while ((recvPacket = popPacket()) != null)
		{
			try {
				EdpMsg msg = resolvePacket(recvPacket);
				if (msg != null)
				{
					msgList.add(msg);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		if (msgList.size() == 0){
			return null;
		}
		else
		{
			return msgList;
		}
	}
	
}
