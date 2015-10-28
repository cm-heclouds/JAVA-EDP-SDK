package onenet.edp;

import onenet.edp.Common.MsgType;

public class PingMsg extends EdpMsg
{
	private byte[] pingMsg;
	public PingMsg()
	{
		super(MsgType.PINGREQ);
		byte[] msg = {MsgType.PINGREQ, 0x00};
		pingMsg = msg;
	}
	
	public byte[] packMsg()
	{
		return this.pingMsg;
	}
}
