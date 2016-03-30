package com.game.draco.app.skill.config;

//被动技能触发方式
public enum SkillPassiveType {
	helper(0,"辅助主动技能",true,false),
	attribute(1,"持续属性",true,false),
	attackType(2,"攻击方式",true,false),
	beforeAttackTarget(3,"使用主动技能前,和目标相关(循环内)",true,false),
	beforeDefenderExertHurt(4,"防御方被施加伤害前",false,false),//eg: 致死一击
    //beforeAttackerExertHurt(5,"攻击方施加伤害前",true,false),//
	afterAttack(6,"主动攻击后(循环外)",true,true),
	beforeDefend(7,"防御前",false,false),//被攻击者被动技能
	//afterDefend(8,"防御后",true,true),
	;
	
	private final int type;
	private final String name;
	private final boolean attack ;
	private final boolean mustTargetScope ;
	
	SkillPassiveType(int type, String name,
			boolean attack,boolean mustTargetScope){
		this.name=name;
		this.type=type;
		this.attack = attack ;
		this.mustTargetScope = mustTargetScope ;
	}

	public int getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	
	public boolean isAttack() {
		return attack;
	}

	
	public boolean isMustTargetScope() {
		return mustTargetScope;
	}

	public static SkillPassiveType get(int type){
		for(SkillPassiveType passiveType : SkillPassiveType.values()){
			if(passiveType.getType() == type){
				return passiveType;
			}
		}
		return null;
	}
}
