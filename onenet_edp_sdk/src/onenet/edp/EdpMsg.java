package onenet.edp;

import java.io.IOException;
import java.nio.ByteBuffer;

/*
 * function:	EDP 消息基类
 * author:		yonghua
 * date:		2015/01/18
 * version:		0.0.1
 */

public class EdpMsg
{
	private byte type;
	
	public byte getMsgType()
	{
		return type;
	}
	
	public EdpMsg(byte msgType)
	{
		type = msgType;
	}
	
	public void unpackMsg(byte[] msgData)
	throws IOException{
	}
	
	public byte[] packPkg(byte[] _msgData)
	{
		int len  = _msgData.length;
		byte[] pkgDataLength = packLength(len);
		if (pkgDataLength == null)
		{
			System.err.println("[packPkg] packet data length exception. "
					+ "data_len=" + len);
			return null;
		}
		int pkgDataLengthSize = pkgDataLength.length;
		int pkgLength = 1 + pkgDataLengthSize + len;
		ByteBuffer packet = ByteBuffer.allocate(pkgLength);
		
		packet.put(this.type);
		packet.put(pkgDataLength);
		packet.put(_msgData);
		
		return packet.array();
	}
	
	//消息长度转换为edp长度格式
	public byte[] packLength(int size)
	{
		int twoByteMin = 128;
		int threeByteMin = 16384;
		int fourByteMin = 2097152;
		int maxSize = 268435455;
		ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
		int cnt = 0;
		if (size > maxSize)
		{
			return null;
		}
		else if (size >= fourByteMin)
		{
			cnt = 4;
		}
		else if (size >= threeByteMin)
		{
			cnt = 3;
		}
		else if (size >= twoByteMin)
		{
			cnt = 2;
		}
		else
		{
			cnt = 1;
		}
		
		byte bSize = 0;
		for (int i = 0; i < cnt; i++)
		{
			if (i == (cnt - 1))
			{
				bSize = (byte) size;
			}
			else
			{
				bSize = (byte) ((size & 0x7F) | 0x80);
				size = size >> 7;
			}
			sizeBuffer.put(bSize);
		}
		
		int bufferSize = sizeBuffer.position();
		byte[] sizeArray = new byte[bufferSize];
		sizeBuffer.flip();
		sizeBuffer.get(sizeArray);
		return sizeArray;
	}
	
	//检测设备地址长度是否合法，大于等于5，小于10
	public boolean checkAddressLen(int _len)
	{
		if (_len <5 || _len > 10)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
}

class EdpPacket
{
	public byte type;
	public int dataLength;
	public byte[] data;
	
	public EdpPacket()
	{
		type = 0;
		dataLength = 0;
	}
}