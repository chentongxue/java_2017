package sacred.alliance.magic.app.msgcenter;

import java.io.Serializable;

import sacred.alliance.magic.core.Message;



public class MessageEntry implements Serializable{

	/**
	 * 目的用户ID
	 */
	private String destUserId;

	/**
	 * 消息体
	 */
	private Message message;

	/**
	 * 消息的创建时间
	 */
	private long createTime;

	/**
	 * 消息的过期时间
	 */
	private int expireTime=0;
	
	private boolean customerService = false;
	
	public MessageEntry() {
		super();
		this.createTime= System.currentTimeMillis();
	}
	
	public boolean isExpire(){
		if(this.expireTime<=0){
			return false;
		}
		
		if((System.currentTimeMillis()-this.createTime)>this.expireTime){
			return true;
		}
		return false;
	}

	public int getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}
	
	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}

    public String getDestUserId() {
        return destUserId;
    }

    public void setDestUserId(String destUserId) {
        this.destUserId = destUserId;
    }

	public boolean isCustomerService() {
		return customerService;
	}

	public void setCustomerService(boolean customerService) {
		this.customerService = customerService;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	
}
