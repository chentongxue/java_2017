package baoutil;

import javax.swing.text.SimpleAttributeSet;

public class LogBItem implements Cloneable{
	private String msg;
	private SimpleAttributeSet st;
	
	public LogBItem(String str, SimpleAttributeSet attrSet) {
		super();
		this.msg = str;
		this.st = attrSet;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public SimpleAttributeSet getSt() {
		return st;
	}
	public void setSt(SimpleAttributeSet st) {
		this.st = st;
	}
	@Override
	protected Object clone(){
		LogBItem it = null;
		try {
			it = (LogBItem) super.clone();
			it.st = (SimpleAttributeSet) st.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return it;
	}

}
