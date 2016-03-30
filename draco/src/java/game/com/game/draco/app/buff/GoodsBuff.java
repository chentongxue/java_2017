package com.game.draco.app.buff;

import java.util.List;

import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.Util;

/**
 * 消耗品Buff处理
 * @author Ljm
 *
 */
public class GoodsBuff extends RoleBuff{

	public GoodsBuff(short buffId) {
		super(buffId);
	}
	
	@Override
	public String getNotReplaceDesc(){
		return this.notReplaceDesc ;
	}

	/**
	 * 消耗品的治疗系数
	 */
	@Override
	protected boolean doHealRate(){
		return false ;
	}
	
	
	/**buff添加时刻效果*/
	public void beginEffect(BuffContext context){
		int buffLevel = context.getBuffLevel();
		BuffDetail detail = this.getBuffDetail(buffLevel);
		
		GoodsBuffDetail consume = (GoodsBuffDetail)detail;
		
		List<AttriItem> beginList = consume.getBeginAttriList(context.getOwner());
		this.buildBuffAttri(beginList, context);
		
		//可以清除的buff组
		List<Short> removeList = consume.getClearBuffGroup();
		int clearNum = consume.getClearNum();
		if(!Util.isEmpty(removeList)){
			for(int i=0;i<clearNum;i++){
				short buffId = removeList.get(i);
				context.appendBuff(buffId, 0, 10000, 0);
			}
		}
		
	}

	/**buff执行效果*/
	public void processEffect(BuffContext context) {
		int buffLevel = context.getBuffLevel();
		BuffDetail detail = this.getBuffDetail(buffLevel);
		
		GoodsBuffDetail consume = (GoodsBuffDetail)detail;
		//每跳影响的属性
		List<AttriItem> everyList = consume.getProcessAttriList(context.getOwner());
		this.buildBuffAttri(everyList, context);
	}

	/**buff中断效果*/
	public void removeEffect(BuffContext context){
	}

	/**buff超时效果*/
	public void timeOverEffect(BuffContext context){
	}
	
	
	private void buildBuffAttri(List<AttriItem> attriList, BuffContext context){
		if(Util.isEmpty(attriList)){
			return;
		}
		for(AttriItem item : attriList){
			context.appendAttri(AttributeType.get(item.getAttriTypeValue()),
						(int)item.getValue(),(int)item.getPrecValue());
			//气力值飘字
			/*if(AttributeType.angerValue.getType() == item.getAttriTypeValue()
					&& item.getValue()>0){
				context.getOwner().getBehavior().addSelfFont(AttrFontSizeType.Common, 
						AttrFontColorType.Anger_Revert,(int)item.getValue());
				context.getOwner().getBehavior().notifyAttrFont();
			}*/
		}
	}
	
}
