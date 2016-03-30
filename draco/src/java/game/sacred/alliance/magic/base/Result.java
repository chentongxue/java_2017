package sacred.alliance.magic.base;


public class Result {
	
	public static final byte SUCCESS = 1;
	public static final byte FAIL =0;

	protected boolean ignore = false;//忽略,不给客户端返回任何信息
	protected byte result = 0; 
	protected String info = null;
	
	
	public byte getResult() {
		return result;
	}
	public String getInfo() {
		return info;
	}
	public Result setResult(byte ret) {
		this.result = ret;
		return this;
	}
	public Result setResult(Result r) {
		this.result = r.result;
		this.ignore = r.ignore;
		this.info = r.info;
		return this;
	}
	public Result setInfo(String info) {
		this.info = info;
		return this;
	}
	
	public boolean isSuccess(){
		return this.result == Result.SUCCESS;
	}
	
	public Result success(){
		this.result = Result.SUCCESS;
		return this;
	}
	
	public Result failure(){
		this.result = Result.FAIL;
		return this;
	}
	
	/**
	 * 忽略，流程终止
	 * 不给客户端返回任何信息
	 * 即 在action中return null
	 * @return
	 */
	public boolean isIgnore() {
		return ignore;
	}
	
	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}
	
}

