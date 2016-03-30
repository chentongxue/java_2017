package com.game.draco.app.buff.effect;

import com.game.draco.GameContext;
import com.game.draco.app.buff.BuffContext;
import com.game.draco.app.buff.stat.BuffStat;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.AttributeType;

public class AttributeEffect implements Effect{

	static class SingletonHolder {
		static AttributeEffect singleton = new AttributeEffect();
	}
	
	public static AttributeEffect getInstance(){
		return SingletonHolder.singleton ;
	}
	
	@Override
	public void resume(BuffContext context) {
		//恢复begin时修改的属性
		AttriBuffer buffer = context.getBuffStat().getAttriBuffer();
		if(null == buffer||buffer.isEmpty()){
			return ;
		}
		GameContext.getUserAttributeApp().changeAttribute(context.getOwner(),
				buffer.reverse(), false);
		//通知属性变化
		context.getOwner().getBehavior().notifyAttribute();
	}

	@Override
	public void store(BuffContext context) {
		//将begin时修改的需要重算的属性存入BuffStat
		AttriBuffer buffer = context.getOwnerAttriBuffer();
		if(null == buffer || buffer.isEmpty()){
			return ;
		}
		AttriBuffer filterBuffer = buffer.filter(new AttriBuffer.Filter(){
			@Override
			public boolean filter(byte attriType) {
				return AttributeType.isBuffResumeAttribute(attriType);
			}
		});
		context.getBuffStat().setBuffInfo(filterBuffer.toStoreString());
	}

	@Override
	public AttriBuffer getAttriBuffer(BuffStat stat) {
		return AttriBuffer.build(stat.getBuffInfo());
	}

	@Override
	public boolean canRemoveNow(BuffContext context) {
		return false;
	}

}
