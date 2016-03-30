package sacred.alliance.magic.filter;

import java.io.Serializable;

public class Tag {
	private long createTime;
	private long threadId;
	private Serializable message;
	public Tag() {
		this.createTime = System.currentTimeMillis();
	}
	
	public Serializable getMessage() {
		return message;
	}
	public void setMessage(Serializable message) {
		this.message = message;
	}
	public long getThreadId() {
		return threadId;
	}
	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	
	
}
