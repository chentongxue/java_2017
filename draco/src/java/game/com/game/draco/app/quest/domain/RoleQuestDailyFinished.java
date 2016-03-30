package com.game.draco.app.quest.domain;

import java.util.Date;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.util.DateUtil;

public @Data class RoleQuestDailyFinished extends AbstractQuestFinish{
	
	/**
	 * 40个字段能表示的最大的任务ID值是2559 日常任务的ID范围是10001~12559
	 */
	public static final int Max_QuestId = 12559;
	public static final int Min_QuestId = 10000;
	private static final int Max_Index = 40;
	protected static final String Daily_Field_Prefix = "edata";//列名前缀
	
	public static final String ROLEID = "roleId";
	private String roleId;
	private Date updateTime;
	private long edata0;
	private long edata1;
	private long edata2;
	private long edata3;
	private long edata4;
	private long edata5;
	private long edata6;
	private long edata7;
	private long edata8;
	private long edata9;
	private long edata10;
	private long edata11;
	private long edata12;
	private long edata13;
	private long edata14;
	private long edata15;
	private long edata16;
	private long edata17;
	private long edata18;
	private long edata19;
	private long edata20;
	private long edata21;
	private long edata22;
	private long edata23;
	private long edata24;
	private long edata25;
	private long edata26;
	private long edata27;
	private long edata28;
	private long edata29;
	private long edata30;
	private long edata31;
	private long edata32;
	private long edata33;
	private long edata34;
	private long edata35;
	private long edata36;
	private long edata37;
	private long edata38;
	private long edata39;
	
	@Override
	protected void update() {
		try {
			this.updateTime = new Date();
			if(DbState.Creat == this.dbState){
				GameContext.getBaseDAO().insert(this);
				this.dbState = DbState.Default;
			}else{
				GameContext.getBaseDAO().update(this);
			}
		} catch (RuntimeException e) {
			this.logger.error(this.getClass().getName() + ".update error: ", e);
		}
	}
	
	@Override
	protected void update(int index, long data) {
		try {
			this.updateTime = new Date();
			if(DbState.Creat == this.dbState){
				GameContext.getQuestDAO().insert(this);
				this.dbState = DbState.Default;
			}else{
				GameContext.getQuestDAO().updateDailyOneField(this.roleId, this.updateTime, this.getFieldName(index), data);
			}
		} catch (RuntimeException e) {
			this.logger.error(this.getClass().getName() + ".update by index error: ", e);
		}
	}
	
	/**
	 * 根据索引获取字段名
	 * @param index
	 * @return
	 */
	private String getFieldName(int index){
		return Daily_Field_Prefix + index;
	}

	@Override
	protected int verifyQuestId(int questId) {
		return questId - Min_QuestId;
	}
	
	@Override
	public boolean hasFinishedQuest(int questId) {
		this.reset();
		return super.hasFinishedQuest(questId);
	}
	
	@Override
	protected void reset() {
		Date now = new Date();
		if(DateUtil.sameDay(this.updateTime, now)){
			return;
		}
		//不是同一天，将所有数据清零
		this.clearAll();
		this.updateTime = now;
		//重置之后，库中所有字段更新
		GameContext.getQuestDAO().update(this);
	}
	
	/**
	 * 清除所有任务数据
	 */
	private void clearAll(){
		for(int index=0; index<Max_Index; index++){
			this.setValue(0, index);
		}
	}
	
	@Override
	protected long getValue(int index) {
		switch (index) {
		case 0:
			return this.edata0;
		case 1:
			return this.edata1;
		case 2:
			return this.edata2;
		case 3:
			return this.edata3;
		case 4:
			return this.edata4;
		case 5:
			return this.edata5;
		case 6:
			return this.edata6;
		case 7:
			return this.edata7;
		case 8:
			return this.edata8;
		case 9:
			return this.edata9;
		case 10:
			return this.edata10;
		case 11:
			return this.edata11;
		case 12:
			return this.edata12;
		case 13:
			return this.edata13;
		case 14:
			return this.edata14;
		case 15:
			return this.edata15;
		case 16:
			return this.edata16;
		case 17:
			return this.edata17;
		case 18:
			return this.edata18;
		case 19:
			return this.edata19;
		case 20:
			return this.edata20;
		case 21:
			return this.edata21;
		case 22:
			return this.edata22;
		case 23:
			return this.edata23;
		case 24:
			return this.edata24;
		case 25:
			return this.edata25;
		case 26:
			return this.edata26;
		case 27:
			return this.edata27;
		case 28:
			return this.edata28;
		case 29:
			return this.edata29;
		case 30:
			return this.edata30;
		case 31:
			return this.edata31;
		case 32:
			return this.edata32;
		case 33:
			return this.edata33;
		case 34:
			return this.edata34;
		case 35:
			return this.edata35;
		case 36:
			return this.edata36;
		case 37:
			return this.edata37;
		case 38:
			return this.edata38;
		case 39:
			return this.edata39;
		}
		return 0;
	}
	
	@Override
	protected void setValue(long data, int index) {
		switch (index) {
		case 0:
			this.edata0 = data;
			break;
		case 1:
			this.edata1 = data;
			break;
		case 2:
			this.edata2 = data;
			break;
		case 3:
			this.edata3 = data;
			break;
		case 4:
			this.edata4 = data;
			break;
		case 5:
			this.edata5 = data;
			break;
		case 6:
			this.edata6 = data;
			break;
		case 7:
			this.edata7 = data;
			break;
		case 8:
			this.edata8 = data;
			break;
		case 9:
			this.edata9 = data;
			break;
		case 10:
			this.edata10 = data;
			break;
		case 11:
			this.edata11 = data;
			break;
		case 12:
			this.edata12 = data;
			break;
		case 13:
			this.edata13 = data;
			break;
		case 14:
			this.edata14 = data;
			break;
		case 15:
			this.edata15 = data;
			break;
		case 16:
			this.edata16 = data;
			break;
		case 17:
			this.edata17 = data;
			break;
		case 18:
			this.edata18 = data;
			break;
		case 19:
			this.edata19 = data;
			break;
		case 20:
			this.edata20 = data;
			break;
		case 21:
			this.edata21 = data;
			break;
		case 22:
			this.edata22 = data;
			break;
		case 23:
			this.edata23 = data;
			break;
		case 24:
			this.edata24 = data;
			break;
		case 25:
			this.edata25 = data;
			break;
		case 26:
			this.edata26 = data;
			break;
		case 27:
			this.edata27 = data;
			break;
		case 28:
			this.edata28 = data;
			break;
		case 29:
			this.edata29 = data;
			break;
		case 30:
			this.edata30 = data;
			break;
		case 31:
			this.edata31 = data;
			break;
		case 32:
			this.edata32 = data;
			break;
		case 33:
			this.edata33 = data;
			break;
		case 34:
			this.edata34 = data;
			break;
		case 35:
			this.edata35 = data;
			break;
		case 36:
			this.edata36 = data;
			break;
		case 37:
			this.edata37 = data;
			break;
		case 38:
			this.edata38 = data;
			break;
		case 39:
			this.edata39 = data;
			break;
		}
	}

}
