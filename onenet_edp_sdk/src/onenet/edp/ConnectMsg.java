package onenet.edp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import onenet.edp.Common.MsgType;

/**
 * edp 连接请求消息操作类，支持该消息的封装
 * @author yonghua
 * date:2015/08/07
 */
public class ConnectMsg extends EdpMsg
{
	public ConnectMsg()
	{
		super(MsgType.CONNREQ);
	}
	
	/**
	 * 封装edp连接请求的消息报文
	 * @param deviceId device id
	 * @param userId user id
	 * @param authInfo authentication information (e.g. master-key)
	 * @param connectTimeout connect timeout
	 * @return packet
	 */
	public byte[] packMsg(int deviceId, int userId, String authInfo, short connectTimeout)
	{
		if (authInfo == null)
		{
			return null;
		}
		
		ByteBuffer data = ByteBuffer.allocate(256).order(ByteOrder.BIG_ENDIAN);
		
		//协议描述
		data.putShort((short)EdpKit.EDP_PROTOCOL.length());
		data.put(EdpKit.EDP_PROTOCOL.getBytes());
		
		//协议版本
		data.put((byte)0x01);
		
		//连接标志
		if (userId > 0)
		{
			data.put((byte)0xC0);
		}
		else
		{
			data.put((byte)0x40);
		}
		
		//保持连接时间，单位为秒
		data.putShort(connectTimeout);
		
		//设备ID
		if (deviceId > 0)
		{
			String deviceIdStr = "" + deviceId;
			short strLen = (short)deviceIdStr.length();
			data.putShort(strLen);
			data.put(deviceIdStr.getBytes());
		}
		else
		{
			data.putShort((short)0);
		}
		
		//用户ID
		if (userId > 0)
		{
			String userIdStr = "" + userId;
			short strLen = (short)userIdStr.length();
			data.putShort(strLen);
			data.put(userIdStr.getBytes());
		}
		
		//鉴权信息
		short infoLen = (byte)authInfo.length();
		data.putShort(infoLen);
		data.put(authInfo.getBytes());
		
		int packetSize = data.position();
		byte[] packet = new byte[packetSize];
		data.flip();
		data.get(packet);
		
		byte[] edpPkg = packPkg(packet);
		return edpPkg;
	}
	
	/**
	 * 封装edp连接请求的消息报文
	 * @param deviceId device id
	 * @param userId user id. if don't set userId, set 0.
	 * @param authInfo authentication information (e.g. master-key)
	 * @return packet
	 */
	public byte[] packMsg(int deviceId, int userId, String authInfo)
	{
		return packMsg(deviceId, userId, authInfo, (short)300);	//默认时间设置为5分钟
	}
	
	/**
	 * 封装edp连接请求的消息报文
	 * @param deviceId device id
	 * @param authInfo authentication information (e.g. master-key)
	 * @return packet
	 */
	public byte[] packMsg(int deviceId, String authInfo) {
		return packMsg(deviceId, 0, authInfo, (short)300);
	}
}
