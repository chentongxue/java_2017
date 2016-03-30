package com.game.draco.app.team;

public enum LeaveTeam {
	
	/** 掉线 */
	offline(2,"掉线"),
	
	/** 退出 */
	//exit(3,"退出游戏"),
	
	/** 主动申请 */
	apply(1,"主动退出"),
	
	/** 被队长开除 */
	kicked(0,"被队长开除"),
	
	/**系统踢*/
	system(4,"系统踢出"),
	
	/**加入新队伍*/
	join(5,"加入新队伍"),
	
	;
	
	private final int type;
	private final String name;
	
	LeaveTeam(int type, String name){
		this.type = type;
		this.name = name;
	}
	
	public int getType(){
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public static LeaveTeam getLeaveType(int type){
		for(LeaveTeam item : LeaveTeam.values()){
			if(null == item){
				continue;
			}
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
}
