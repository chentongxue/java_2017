package com.game.draco.app.quest.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.LongTool;

/**
 * 任务完成记录 使用N个long型数据来按位保存任务是否完成 每个data能保存64个任务，每个位置的0和1分别表示此任务是否完成过
 * data0的64个位置，保存的任务ID范围是1~64 data1的64个位置，保存的任务ID范围是65~128
 * 主线支线任务使用50个long字段，任务ID的范围是1~3199
 * 日常任务使用40个long字段，任务ID的范围是10001~12559，保存时减去10000即1~2559
 * public的方法必须调用reset重置逻辑（为了日常任务）
 */
public abstract class AbstractQuestFinish {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected static final int Six = 6;//求索引时需要右移的位数 6
	protected static final int Sixty_Three = 63;//求位置时需要的最大余数63（共64个位置）
	protected DbState dbState = DbState.Default;//如果是create状态，说明需要insert
	
	protected enum DbState{
		Default,
		Creat,
	}
	
	/**
	 * 登录的时候从库中读取已完成的任务信息
	 * 如果不存在，则创建一个对象并标识为创建状态
	 */
	public void setDbStateCreate(){
		this.dbState = DbState.Creat;
	}
	
	/**
	 * 是否完成过某个任务
	 * @param questId
	 * @return
	 */
	public boolean hasFinishedQuest(int questId) {
		//必须检测重置
		this.reset();
		//必须处理任务ID
		questId = this.verifyQuestId(questId);
		long data = this.getValue(this.getIndex(questId));
		long value = LongTool.getIndexValue(data, this.getPlace(questId));
		return 1 == value;
	}
	
	/**
	 * 完成某个任务
	 * @param questId
	 */
	public void completeQuest(int questId){
		try {
			//必须检测重置
			this.reset();
			//必须处理任务ID
			questId = this.verifyQuestId(questId);
			int index = this.getIndex(questId);
			long data = this.getValue(index);
			data = LongTool.setIndexValueOne(data, this.getPlace(questId));
			//修改数值
			this.setValue(data, index);
			//this.update();//性能低，所以更新指定数据
			this.update(index, data);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".completeQuest error: ", e);
		}
	}
	
	/**
	 * 清除某个任务的记录
	 * @param questId
	 */
	public void clearQuest(int questId){
		try {
			//必须检测重置
			this.reset();
			//必须处理任务ID
			questId = this.verifyQuestId(questId);
			int index = this.getIndex(questId);
			long data = this.getValue(index);
			data = LongTool.setIndexValueZero(data, this.getPlace(questId));
			//修改数值
			this.setValue(data, index);
			//this.update();//性能低，所以更新指定数据
			this.update(index, data);
		} catch (RuntimeException e) {
			this.logger.error(this.getClass().getName() + ".clearQuest error: ", e);
		}
	}
	
	/**
	 * 获取索引
	 * 该任务ID保存在哪个data字段中
	 * @param questId
	 * @return
	 */
	private int getIndex(int questId){
		/* 任务ID对64整除运算（即右移6位）*/
		return questId >> Six;
	}
	
	/**
	 * 获取位置
	 * 该任务ID保存在data数据的第几个位置
	 * @param questId
	 * @return
	 */
	private int getPlace(int questId){
		/* 
		 * 任务ID对64取余运算，有两种算法
		 * ① questId & Sixty_Three 
		 * ② questId - ((questId >> Six) << Six 
		 */
		return questId & Sixty_Three;
	}
	
	/**
	 * 处理任务ID
	 * 主线、支线的直接返回该任务ID
	 * 日常任务ID需要减去10000
	 * @param questId
	 * @return
	 */
	protected abstract int verifyQuestId(int questId);
	
	/**
	 * 重置任务完成日志
	 * 日常任务需要用到
	 */
	protected abstract void reset();
	
	/**
	 * 根据索引获取数据
	 * @param index
	 * @return
	 */
	protected abstract long getValue(int index);
	
	/**
	 * 根据索引给数据赋值
	 * @param data
	 * @param index
	 */
	protected abstract void setValue(long data, int index);
	
	/**
	 * 更新数据库
	 * （更新所有字段，性能低）
	 */
	protected abstract void update();
	
	/**
	 * 根据索引更新值
	 * （更新某一个字段，性能较高）
	 * @param index 索引
	 * @param data 值
	 */
	protected abstract void update(int index, long data);
	
}
