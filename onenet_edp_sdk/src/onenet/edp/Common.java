package onenet.edp;

/*
 * function:	onenet edp默认参数定义和返回码说明表
 * author:		yonghua
 * date:		2015/01/04
 * version:		0.0.1
 */

public class Common {
	//edp 消息类型
	public class MsgType
	{
		public static final byte CONNREQ = (byte)0x10;		//连接建立请求
		public static final byte CONNRESP = (byte)0x20;		//连接简历响应
		public static final byte PUSHDATA = (byte)0x30;		//发送数据
		public static final byte SAVEDATA = (byte)0x80;		//保存数据
		public static final byte SAVERESP = (byte)0x90;		//保存确认响应
		public static final byte PINGREQ = (byte)0xC0;		//心跳请求
		public static final byte PINGRESP = (byte)0xD0;		//心跳响应
		public static final byte CMDREQ = (byte)0xA0;		//命令请求
		public static final byte CMDRESP = (byte)0xB0;		//命令响应
		public static final byte CONNCLOSE = (byte)0x40;	//连接关闭
		public static final byte UPDATEREQ = (byte)0x50;	//上报固件信息
		public static final byte UPDATERESP = (byte)0x60;	//下发固件信息
		public static final byte TUNNEL = (byte)0x70;		//透传第三方平台数据
	}
	
	//edp 返回码
	public class ConnResp
	{
		public static final byte ACCEPTED = 0;						//连接成功
		public static final byte REFUSED_PROTOCOL_INVALID = 1;		//协议不匹配
		public static final byte REFUSED_BAD_DEVID = 2;				//设备ID鉴权失败
		public static final byte REFUSED_SERVER_UNAVAILABLE = 3;	//服务器失败
		public static final byte REFUSED_BAD_USERID_PASSWORD = 4;	//用户名密码鉴权失败
		public static final byte REFUSED_NOT_AUTHORIZED = 5;		//未授权
		public static final byte REFUSED_INVALID_AUTHOR_CODE = 6;	//无效的鉴权码
		public static final byte REFUSED_INVALID_ACTIVATE_CODE = 7;	//无效的激活码
		public static final byte REFUSED_HAS_ACTIVATED = 8;			//
		public static final byte REFUSED_DUP_AUTHEN = 9;			//
	}
	
	//函数的异常返回码
	public class ErrorCode
	{
		
	}
	
	public static int twoByteToLen(byte highByte, byte lowByte)
	{
		int len = (((int) (highByte & 0xFF)) << 8) + (int) (lowByte & 0xFF);
		return len;
	}
	
	public static int fourByteToLen(byte firstByte, byte secondByte, byte thirdByte, byte fourthByte)
	{
		int len = firstByte & 0x7F;	//过滤掉异常负数
		len = len << 8;
		len += secondByte & 0xFF;
		len = len << 8;
		len += thirdByte & 0xFF;
		len = len << 8;
		len += fourthByte & 0xFF;
		return len;
	}
}
