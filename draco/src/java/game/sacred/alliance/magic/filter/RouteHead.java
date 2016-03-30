package sacred.alliance.magic.filter;

import org.apache.mina.core.buffer.IoBuffer;

public class RouteHead {
	private static final String CAT = ":" ;
	private static final String UTF8 = "utf8" ;
	private static final int PARTNER_HEAD_LEN = 2 ;//合作方头
	private static final int SHORT_SIZE = 2 ;
	private static final int HEAD_MIN_LEN = PARTNER_HEAD_LEN + SHORT_SIZE ;//合作方路由头 + String[appId:serverId::帐号]长度(short)
	
	private byte[] partnerInfo = new byte[2] ;
	private int appId ;
	private String serverId = "" ;
	private String userId  = "";
	
	
	public RouteHead(){
		
	}
	
	public RouteHead(IoBuffer buffer){
		buffer.get(this.partnerInfo);
		int len = buffer.getShort();
		byte[] data = new byte[len];
		buffer.get(data);
		try {
			String info = new String(data,UTF8);
			String[] arr = info.split(CAT);
			if(arr.length < 1){
				return ;
			}
			this.appId = Integer.parseInt(arr[0]);
			if(arr.length < 2){
				return ;
			}
			this.serverId = arr[1];
			if(arr.length < 3){
				return ;
			}
			this.userId = arr[2] ;
		} catch (Exception e) {
		}
	}
	
	public byte[] toBytes(){
		return bytes(toBuffer());
	}
	
	public static boolean isCompleteRouteHead(IoBuffer buffer){
		int remain = buffer.remaining();
		//return remain >=2 ;
		if(HEAD_MIN_LEN > remain){
			return false ;
		}
		int infoLen = buffer.getShort(PARTNER_HEAD_LEN) ;
		if(remain < PARTNER_HEAD_LEN + SHORT_SIZE + infoLen){
			return false ;
		}
		return true ;
	}
	
	/*private static void bytes(byte[] destData,int index,IoBuffer buffer){
		for(int i = 0 ;i<destData.length;i++){
			destData[i] = buffer.get(index + i);
		}
	}*/
	
	
	private static byte[] bytes(IoBuffer buffer){
		byte[] b = new byte[buffer.remaining()];
		/*for(int i = 0;i<b.length;i++){
			b[i] = buffer.get(i);
		}*/
		buffer.get(b);
		return b ;
	}
	
	public IoBuffer toBuffer(){
		IoBuffer buffer = IoBuffer.allocate(0);
		buffer.setAutoExpand(true);
		buffer.put(this.partnerInfo);
		String info = appId + CAT + serverId + CAT + userId ;
		try {
			byte[] data = info.getBytes(UTF8);
			buffer.putShort((short) data.length);
			buffer.put(data);
		} catch (Exception e) {
		}
		buffer.flip();
		return buffer ;
	}
	
	public byte[] getPartnerInfo() {
		return partnerInfo;
	}

	public void setPartnerInfo(byte[] partnerInfo) {
		this.partnerInfo = partnerInfo;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
