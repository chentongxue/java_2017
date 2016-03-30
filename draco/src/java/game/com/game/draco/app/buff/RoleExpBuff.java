package com.game.draco.app.buff;

import java.util.Collection;

import com.game.draco.GameContext;
import com.game.draco.app.buff.stat.BuffStat;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.vo.AbstractRole;


public class RoleExpBuff extends RoleBuff{

	public RoleExpBuff(short buffId) {
		super(buffId);
	}

	/**buff添加时刻效果*/
	@Override
	public void beginEffect(BuffContext context){
		int buffLevel = context.getBuffLevel();
		RoleExpBuffDetail detail = (RoleExpBuffDetail) this.buffDetails.get(buffLevel);
		if(0 != detail.getAdd()){
			context.appendAttri(AttributeType.expAddRate, detail.getAdd(), 0);
		}
		if(0 != detail.getMult()){
			context.appendAttri(AttributeType.expMultRate, detail.getMult(), 0);
		}
	}
	
	/**
	 * 此类buff只会修改经验系数
	 */
	@Override
	protected void execute(BuffContext context, BuffFuncPoint fp) {
		if(null == context || null == fp){
			return ;
		}
		GameContext.getUserAttributeApp().changeAttribute(context.getOwner(), 
				context.getOwnerAttriBuffer());
	}
	
	
	@Override
	public BuffAddResult getReplaceResult(AbstractRole player,
			AbstractRole caster,int currAddBuffLevel,boolean mustSuccess) {
		Collection<BuffStat> list = player.getReceiveBuffCopy();
		for (BuffStat buffStat : list) {
			if(groupId == buffStat.getBuff().getGroupId()){
				return this.buildReplaceResult(buffStat, currAddBuffLevel,mustSuccess);
			}
		}
		BuffAddResult result = new BuffAddResult();
		result.setReplaceType(BuffReplaceType.replace.getType());
		return result;
	}
	
	private BuffAddResult buildReplaceResult(BuffStat replaceBuffStat,
			int currAddBuffLevel,boolean mustSuccess){
		BuffAddResult result = new BuffAddResult();
		if(this.getReplaceType() == BuffReplaceType.replace.getType()){
			//直接替换(任务给的相关buff)
			result.setReplaceBuffStat(replaceBuffStat);
			result.setReplaceType(BuffReplaceType.replace.getType());
			return result ;
		}
		if(null != replaceBuffStat 
				&& replaceBuffStat.getBuffId() == this.getBuffId() 
				&& replaceBuffStat.getBuffLevel() == currAddBuffLevel){
			//buff相同,等级相同延长时间
			//延长时间
			result.setReplaceBuffStat(replaceBuffStat);
			result.setReplaceType(BuffReplaceType.delay.getType());
			return result ;
		}
		if(mustSuccess){
			//已经是二次确认
			//直接替换(任务给的相关buff)
			result.setReplaceBuffStat(replaceBuffStat);
			result.setReplaceType(BuffReplaceType.replace.getType());
			return result ;
		}
		try {
			// 需要二次确认
			RoleExpBuffDetail thisDetail = (RoleExpBuffDetail) this.buffDetails
					.get(currAddBuffLevel);
			RoleExpBuffDetail thatDetail = (RoleExpBuffDetail) ((RoleExpBuff) replaceBuffStat
					.getBuff()).getBuffDetail(replaceBuffStat.getBuffLevel());
			result.setReplaceBuffStat(replaceBuffStat);
			result.setReplaceType(BuffReplaceType.confirm.getType());
			int timeDiff = this.getPersistTime()-replaceBuffStat.getRemainTime();
			int effectDiff = 0 ;
			if(thisDetail.getAdd()>0){
				effectDiff = thisDetail.getAdd() - thatDetail.getAdd();
			}else{
				effectDiff = thisDetail.getMult() - thatDetail.getMult() ;
			}
			StringBuffer buffer = new StringBuffer("");
			buffer.append(this.getText(TextId.BUFF_NEW_EFFECT));
			if(effectDiff>=0){
				buffer.append("[\\C]FF00FF00[C]"+this.getText(TextId.BUFF_UP)+"[\\C]FFFFFFFF[C] ");
			}else{
				buffer.append("[\\C]FFFF0000[C]"+this.getText(TextId.BUFF_DOWN)+"[\\C]FFFFFFFF[C] ");
			}
			buffer.append(Math.abs(effectDiff/100));
			buffer.append("% ");
			buffer.append(this.getText(TextId.BUFF_TIME));
			if(timeDiff>=0){
				buffer.append("[\\C]FF00FF00[C]"+this.getText(TextId.BUFF_EXTEND)+"[\\C]FFFFFFFF[C] ");
			}else{
				buffer.append("[\\C]FFFF0000[C]"+this.getText(TextId.BUFF_SHORT)+"[\\C]FFFFFFFF[C] ");
			}
			buffer.append(Math.abs((int)Math.ceil(timeDiff/1000/60)));
			buffer.append(this.getText(TextId.BUFF_REPLACE));
			result.setInfo(buffer.toString());
			result.setMustConfirm(true);
			return result;
		}catch(Exception ex){
			logger.error("",ex);
			result.setReplaceType(BuffReplaceType.failure.getType());
			result.setInfo(this.getText(TextId.SYSTEM_ERROR));
			return result ;
		}
	}
	
	private String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}
	
	
	@Override
	public void init(BuffDetail detail){
		super.init(detail);
		RoleExpBuffDetail thisDetail = (RoleExpBuffDetail)detail ;
		if(thisDetail.isReplace()){
			this.replaceType = BuffReplaceType.replace.getType();
		}
		this.setTimeType(BuffTimeType.continued);
	}
	
}
