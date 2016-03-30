package com.game.draco.app.quest.domain;

import com.game.draco.GameContext;

import lombok.Data;

public @Data class RoleQuestFinished extends AbstractQuestFinish {

	/**
	 * 50个字段能表示的最大的任务ID值是3199 主线、支线的任务ID范围是1~3199
	 */
	public static final long Max_QuestId = 3199;
	protected static final String Field_Prefix = "data";//列名前缀
	//private static final int Max_Index = 50;
	
	public static final String ROLEID = "roleId";
	private String roleId;
	private long data0;
	private long data1;
	private long data2;
	private long data3;
	private long data4;
	private long data5;
	private long data6;
	private long data7;
	private long data8;
	private long data9;
	private long data10;
	private long data11;
	private long data12;
	private long data13;
	private long data14;
	private long data15;
	private long data16;
	private long data17;
	private long data18;
	private long data19;
	private long data20;
	private long data21;
	private long data22;
	private long data23;
	private long data24;
	private long data25;
	private long data26;
	private long data27;
	private long data28;
	private long data29;
	private long data30;
	private long data31;
	private long data32;
	private long data33;
	private long data34;
	private long data35;
	private long data36;
	private long data37;
	private long data38;
	private long data39;
	private long data40;
	private long data41;
	private long data42;
	private long data43;
	private long data44;
	private long data45;
	private long data46;
	private long data47;
	private long data48;
	private long data49;
	
	@Override
	protected void update() {
		try {
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
			if(DbState.Creat == this.dbState){
				GameContext.getQuestDAO().insert(this);
				this.dbState = DbState.Default;
			}else{
				GameContext.getQuestDAO().updateOneField(this.roleId, this.getFieldName(index), data);
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
	protected String getFieldName(int index){
		return Field_Prefix + index;
	}

	@Override
	protected int verifyQuestId(int questId) {
		return questId;
	}
	
	@Override
	protected void reset() {
		//主线支线任务不需要重置
	}
	
	@Override
	protected long getValue(int index) {
		switch (index) {
		case 0:
			return this.data0;
		case 1:
			return this.data1;
		case 2:
			return this.data2;
		case 3:
			return this.data3;
		case 4:
			return this.data4;
		case 5:
			return this.data5;
		case 6:
			return this.data6;
		case 7:
			return this.data7;
		case 8:
			return this.data8;
		case 9:
			return this.data9;
		case 10:
			return this.data10;
		case 11:
			return this.data11;
		case 12:
			return this.data12;
		case 13:
			return this.data13;
		case 14:
			return this.data14;
		case 15:
			return this.data15;
		case 16:
			return this.data16;
		case 17:
			return this.data17;
		case 18:
			return this.data18;
		case 19:
			return this.data19;
		case 20:
			return this.data20;
		case 21:
			return this.data21;
		case 22:
			return this.data22;
		case 23:
			return this.data23;
		case 24:
			return this.data24;
		case 25:
			return this.data25;
		case 26:
			return this.data26;
		case 27:
			return this.data27;
		case 28:
			return this.data28;
		case 29:
			return this.data29;
		case 30:
			return this.data30;
		case 31:
			return this.data31;
		case 32:
			return this.data32;
		case 33:
			return this.data33;
		case 34:
			return this.data34;
		case 35:
			return this.data35;
		case 36:
			return this.data36;
		case 37:
			return this.data37;
		case 38:
			return this.data38;
		case 39:
			return this.data39;
		case 40:
			return this.data40;
		case 41:
			return this.data41;
		case 42:
			return this.data42;
		case 43:
			return this.data43;
		case 44:
			return this.data44;
		case 45:
			return this.data45;
		case 46:
			return this.data46;
		case 47:
			return this.data47;
		case 48:
			return this.data48;
		case 49:
			return this.data49;
		}
		return 0;
	}
	
	@Override
	protected void setValue(long data, int index) {
		switch (index) {
		case 0:
			this.data0 = data;
			break;
		case 1:
			this.data1 = data;
			break;
		case 2:
			this.data2 = data;
			break;
		case 3:
			this.data3 = data;
			break;
		case 4:
			this.data4 = data;
			break;
		case 5:
			this.data5 = data;
			break;
		case 6:
			this.data6 = data;
			break;
		case 7:
			this.data7 = data;
			break;
		case 8:
			this.data8 = data;
			break;
		case 9:
			this.data9 = data;
			break;
		case 10:
			this.data10 = data;
			break;
		case 11:
			this.data11 = data;
			break;
		case 12:
			this.data12 = data;
			break;
		case 13:
			this.data13 = data;
			break;
		case 14:
			this.data14 = data;
			break;
		case 15:
			this.data15 = data;
			break;
		case 16:
			this.data16 = data;
			break;
		case 17:
			this.data17 = data;
			break;
		case 18:
			this.data18 = data;
			break;
		case 19:
			this.data19 = data;
			break;
		case 20:
			this.data20 = data;
			break;
		case 21:
			this.data21 = data;
			break;
		case 22:
			this.data22 = data;
			break;
		case 23:
			this.data23 = data;
			break;
		case 24:
			this.data24 = data;
			break;
		case 25:
			this.data25 = data;
			break;
		case 26:
			this.data26 = data;
			break;
		case 27:
			this.data27 = data;
			break;
		case 28:
			this.data28 = data;
			break;
		case 29:
			this.data29 = data;
			break;
		case 30:
			this.data30 = data;
			break;
		case 31:
			this.data31 = data;
			break;
		case 32:
			this.data32 = data;
			break;
		case 33:
			this.data33 = data;
			break;
		case 34:
			this.data34 = data;
			break;
		case 35:
			this.data35 = data;
			break;
		case 36:
			this.data36 = data;
			break;
		case 37:
			this.data37 = data;
			break;
		case 38:
			this.data38 = data;
			break;
		case 39:
			this.data39 = data;
			break;
		case 40:
			this.data40 = data;
			break;
		case 41:
			this.data41 = data;
			break;
		case 42:
			this.data42 = data;
			break;
		case 43:
			this.data43 = data;
			break;
		case 44:
			this.data44 = data;
			break;
		case 45:
			this.data45 = data;
			break;
		case 46:
			this.data46 = data;
			break;
		case 47:
			this.data47 = data;
			break;
		case 48:
			this.data48 = data;
			break;
		case 49:
			this.data49 = data;
			break;
		}
	}

}
