package com.game.draco.app.recovery.type;

import com.game.draco.app.recovery.logic.IRecoveryLogic;
import com.game.draco.app.recovery.logic.RecoveryArenaLogic;
import com.game.draco.app.recovery.logic.RecoveryBossKillLogic;
import com.game.draco.app.recovery.logic.RecoveryCampBattleLogic;
import com.game.draco.app.recovery.logic.RecoveryCopyLogic;
import com.game.draco.app.recovery.logic.RecoveryDailyQuestLogic;
import com.game.draco.app.recovery.logic.RecoveryHangUpLogic;

public enum RecoveryType {
	HUNG_UP_EXP((byte)1, "挂机经验追回"),  //经验（只能追回一次）      ；消耗的金币量=X*（未获取经验占经验上限的壁纸），X与等级相关；产出根据昨天取得
	COPY((byte)2, "副本"),				  //经验，金币；消耗钻石为固定值
	BOSS_KILL((byte)3, "肉山必须死"),      //金币      ；消耗的钻石为固定值,只能追回一次
	CAMP_BATTLE((byte)4, "阵营战"),        //金币，潜能，物品
	ARENA_RECOVERY((byte)5, "谁与争锋（同步竞技场）"),//金币，荣誉
	DAILY_QUEST((byte)6,"每日任务"),       //经验，金币
	ANGEL_CHEST((byte)7,"赏金宝库")     //（暂无）即神仙福地
	;
	private final byte type;
	private final String name;
	
	private RecoveryType(byte type, String name){
		this.type = type;
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	public static RecoveryType getType(byte type) {
		for(RecoveryType tp : RecoveryType.values()){
			if(tp.getType() == type){
				return tp;
			}
		}
		return null ;
	}
	public IRecoveryLogic createRecoveryLogic(){
		switch(this){
			case HUNG_UP_EXP:
				return RecoveryHangUpLogic.getInstance();
			case COPY:
				return RecoveryCopyLogic.getInstance();
			case BOSS_KILL:
				return RecoveryBossKillLogic.getInstance();
			case CAMP_BATTLE:
				return RecoveryCampBattleLogic.getInstance();
			case ARENA_RECOVERY:
				return RecoveryArenaLogic.getInstance();
			case DAILY_QUEST:
				return RecoveryDailyQuestLogic.getInstance();
			case ANGEL_CHEST:
				return RecoveryCopyLogic.getInstance();
			default :
				return null ;
		}
	}
}
