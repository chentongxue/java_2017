package sacred.alliance.magic.app.goods.behavior.param;

import sacred.alliance.magic.vo.RoleInstance;

public class StringParam extends AbstractParam{
	
	private String info;
	
	public StringParam(RoleInstance role) {
		super(role);
	}

	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
}
