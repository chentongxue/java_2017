package sacred.alliance.magic.app.arena.domain;

import lombok.Data;

public @Data  class Arena1V1RealTime {

	private String roleId ;
	private String roleName ;
	private int roleLevel ;
	private int battleScore ;
	private int score ;
	private byte campId ;
	private boolean autoApply ;
	
}
