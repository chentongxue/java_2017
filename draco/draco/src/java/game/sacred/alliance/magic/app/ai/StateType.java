package sacred.alliance.magic.app.ai;

public enum StateType {
	
	
	State_Idle("Idle"),
	State_Random_Move("Random_Move"),
	State_WayPoint_Move("WayPoint_Move"),
	State_Targeted_Move("Targeted_Move"),
	State_Point_Move("Point_Move"),
	State_Home_Move("Home_Move"),
	State_Battle("Battle"),
    State_Escape("Escape"),
    State_Global("Global"),

	;
	
	String name ;
	StateType(String name){
		this.name = name ;
	}
	
	public String getName(){
		return this.name;
	}
}
