package sacred.alliance.magic.app.active.arena;

public enum ActiveArenaStatus {
	
	CanApply((byte)0,"可报名"),
	CanCancel((byte)1,"可取消"),
	CanEnter((byte)2,"可进入"),
	;
	
	private final byte status;
	private final String name;
	
	private ActiveArenaStatus(byte status, String name) {
		this.status = status;
		this.name = name;
	}

	public byte getStatus() {
		return status;
	}

	public String getName() {
		return name;
	}
	
	
}
