package sacred.alliance.magic.vo;

import sacred.alliance.magic.base.RoleType;

public abstract class RoleEntity implements java.io.Serializable {
	
	protected String roleId ;
	protected RoleType roleType;
	private int intRoleId = 0 ;
	
	
	public RoleType getRoleType() {
		return roleType;
	}
	
	public int getIntRoleId() {
		if(0 == intRoleId){
			intRoleId = Integer.parseInt(this.roleId);
		}
		return intRoleId;
	}
	
	public void setRoleId(String roleId) {
		this.roleId = roleId;
		this.intRoleId = 0 ;
	}
	
	public String getRoleId() {
		return roleId;
	}
	
	
	
	
}
