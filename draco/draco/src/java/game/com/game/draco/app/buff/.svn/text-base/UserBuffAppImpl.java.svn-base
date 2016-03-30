package com.game.draco.app.buff;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.message.push.C0302_BuffAddNotifyMessage;
import com.game.draco.message.push.C0304_BuffDeleteNotifyMessage;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.team.TeamApp;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.dao.BaseDAO;
import sacred.alliance.magic.domain.RoleBuff;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;


public class UserBuffAppImpl implements UserBuffApp {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private BuffApplication buffApp;
	private BaseDAO baseDAO;
	private TeamApp teamApplication;

	public void setBuffApp(BuffApplication buffApp) {
		this.buffApp = buffApp;
	}

	public void setTeamApplication(TeamApp teamApplication) {
		this.teamApplication = teamApplication;
	}
	
	@Override
	public BuffAddResult addBuffStat(AbstractRole player, AbstractRole caster,
			short buffId, int buffLevel) {
		return this.addBuffStat(player, caster, buffId, 0, buffLevel);
	}
	
	
	
	private BuffAddResult doAddBuffStat(AbstractRole player, AbstractRole caster,
			short buffId,int effectTime,int buffLevel,boolean must){
		if(player.isDeath()){
			BuffAddResult result = new BuffAddResult();
			result.setInfo(GameContext.getI18n().getText(TextId.BUFF_TARGET_ERROR));
			return result ;
		}
		Buff buff = buffApp.getBuff(buffId);
		if (null == buff) {
			logger.warn("buff(" + buffId + ") is null,please seeing template and db configure data");
			BuffAddResult result = new BuffAddResult();
			result.setInfo(GameContext.getI18n().getText(TextId.BUFF_ERROR));
			return result ;
		}
		return this.doAddBuffStat(player, caster, buff, effectTime,null,buffLevel,must);
	}
	
	
	/**
	 * 强制添加buff
	 */
	public BuffAddResult addForceBuffStat(AbstractRole player, AbstractRole caster,
			short buffId,int effectTime,int buffLevel) {
		return this.doAddBuffStat(player, caster, buffId, effectTime, buffLevel, true);
	}
	
	
	/**
	 * 添加buff
	 */
	public BuffAddResult addBuffStat(AbstractRole player, AbstractRole caster,
			short buffId,int effectTime,int buffLevel) {
		return this.doAddBuffStat(player, caster, buffId, effectTime, buffLevel,false);
	}
	
	
	private BuffContext getBuffContext(AbstractRole role,BuffStat buffStat){
		return getBuffContext(role,buffStat,true);
	}
	
	private BuffContext getBuffContext(AbstractRole role,BuffStat buffStat,
			boolean realTimeNotifyAttri){
		BuffContext context = new BuffContext();
		context.setBuffStat(buffStat);
		context.setRealTimeNotifyAttri(realTimeNotifyAttri);
		return context ;
	}


	/**
	 * 
	 * @param player 被施法者
	 * @param caster 施法者
	 * @param buff
	 * @param effectTime 如果effectTime<=0 取buff的默认时间
	 * @param extroInfo
	 * @return true可以添加 false不可以添加
	 */
	@SuppressWarnings("unchecked")
	private BuffAddResult doAddBuffStat(AbstractRole player, AbstractRole caster,
			Buff buff,int effectTime,String extroInfo,int currAddBuffLevel,boolean must){
		BuffAddResult result = buff.getReplaceResult(player, caster, currAddBuffLevel,must);
		if(effectTime <= 0 ){
			effectTime = buff.getPersistTime(currAddBuffLevel);
		}
		int replaceType = result.getReplaceType();
		if(BuffReplaceType.failure.getType() == replaceType){
			String desc = buff.getNotReplaceDesc();
			if(null != desc && desc.trim().length()>0){
				//替换说明不为空，弹板提示
				result.setInfo(desc);
				return result ;
			}
			//标识抵抗
			buff.buffResist(player, caster);
			return result ;
		}else if(BuffReplaceType.confirm.getType() == replaceType){
			//二次确认
			return result ;
		}
		result.success();
		BuffStat replaceBuffStat = result.getReplaceBuffStat();
		if(BuffReplaceType.delay.getType() ==replaceType){
			//延时
			replaceBuffStat.setRemainTime(replaceBuffStat.getRemainTime()
					+ effectTime);
			return result;
		}
		short replaceId = 0 ;
		if(null != replaceBuffStat){
			replaceId = replaceBuffStat.getBuffId();
		}
		if(BuffReplaceType.reset.getType() == replaceType){
			//重置时间
			Date now = new Date();
			replaceBuffStat.setCreateTime(now);
			replaceBuffStat.setLastExecuteTime(now.getTime());
			replaceBuffStat.setRemainTime(effectTime);
			//直接替换类型
			//currAddBuffStat(player, caster, buff, effectTime, extroInfo, currAddBuffLevel,replaceId);
			//delBuffStat(player, replaceBuffStat, false,false);
			//return result ;
		}
		//替换不能单独在加或者减buff操作中同步属性,应该统一最后同步属性
		//直接替换类型
		currAddBuffStat(player,caster, buff, effectTime, extroInfo, currAddBuffLevel,replaceId);
		delBuffStat(player,replaceBuffStat, false,false,replaceId == 0);
		if(replaceId != 0){
			//替换最后才同步属性
			player.getBehavior().notifyAttribute();
			if(null != caster && caster.getIntRoleId() != player.getIntRoleId()){
				caster.getBehavior().notifyAttribute();
			}
		}
		return result;
	}

	
	private void currAddBuffStat(AbstractRole player, AbstractRole caster,
			Buff buff,int effectTime,String extroInfo,int buffLevel,short replacedBuffId) {
		try{
			BuffStat stat = new BuffStat(buff,buffLevel,buff.getIntervalTime(buffLevel));
			stat.setBuffId(buff.getBuffId());
			stat.setOwner(player);
			stat.setCaster(caster);
			if(effectTime >0){
				stat.setRemainTime(effectTime);
			}else{
				stat.setRemainTime(buff.getPersistTime(buffLevel));
			}
			//stat.setBuffSeries(buff.getGroupId());
			Date now = new Date();
			stat.setLastExecuteTime(now.getTime());
			stat.setCreateTime(now);
			stat.setBuffInfo(extroInfo);
			player.addBuffStat(stat);
			afterCurrAddBuffStat(player,caster,buff,stat.getRemainTime(),replacedBuffId,stat);
		}catch(Exception e){
			logger.error("--- used buff error: roleId=" + player.getRoleId() + ",buffId=" + buff.getBuffId(),e);
		}
	}
	
	private void afterCurrAddBuffStat(AbstractRole player, AbstractRole caster,
			Buff buff,int effectTime,short replacedBuffId,BuffStat stat) {
		if(buff.getTimeType() == BuffTimeType.continued){
			//持续buff才需要通知
			C0302_BuffAddNotifyMessage resp = new C0302_BuffAddNotifyMessage();
			resp.setCategoryType((byte) buff.getCategoryType().getType());
			resp.setBuffId(buff.getBuffId());
			resp.setBuffPersistTime(effectTime);
			resp.setRoleId(player.getIntRoleId());//命中者
			resp.setIconId(buff.getIconId());
			resp.setBuffRemainTime(effectTime);
			resp.setEffectId(buff.getEffectId());
			if(replacedBuffId > 0){
				resp.setReplaceId(replacedBuffId);
			}
			sendFlashMessage(player, caster, resp);
		}
		//begin方法一定要放在addBuffStat后
		//没有替换buff时实时通知属性
		buff.begin(this.getBuffContext(player, stat,replacedBuffId==0));
	}
	
	
	/**
	 * 给自己、ai.仇恨值列表、小队成员
	 * @param role
	 * @param resp
	 */
	private void sendFlashMessage(AbstractRole player ,AbstractRole caster, Message resp){
		/*if(player!=null && player instanceof RoleInstance){
			if(player.hasTeam()){
				player.getTeam().broadcast(player.getRoleId(), resp, true);
			}else{
				GameContext.getMessageCenter().sendByRoleId("", player.getRoleId(), resp);
			}
		}
		if(player!=null){
			player.getHatredTarget().broadcast(resp);
		}*/
		
		try {
			player.getBehavior().sendMessage(resp);
			player.getBehavior().notifySkillBuff(resp,null,null, false);
		} catch (ServiceException e) {
			logger.error("",e);
		}
	}
	
	/**
	 * 
	 * @param player
	 * @param buffStat
	 * @param timeOver
	 * @return
	 */
	
	@SuppressWarnings("unchecked")
	private void afterDelBuffStat(AbstractRole player,BuffStat buffStat,
			boolean timeOver,boolean sendDelMsg,boolean realTimeNotifyAttri){
		if(null == buffStat){
			return  ;
		}
		Buff buff = buffStat.getBuff();
		if(null == buff){
			return  ;
		}
		if(timeOver){
			buff.timeOver(this.getBuffContext(player, buffStat,realTimeNotifyAttri));
			return  ;
		}
		buff.remove(this.getBuffContext(player,buffStat,realTimeNotifyAttri));
		
		//转发给自己和ai仇恨列表 和同一地图小队里面的成员
		//因为时间到达而结束buff的客户端清楚,无需push通知
		if(!timeOver && sendDelMsg){
			//通知删除buff
			C0304_BuffDeleteNotifyMessage resp = new C0304_BuffDeleteNotifyMessage();
			resp.setBuffId(buff.getBuffId());
			resp.setRoleId(player.getIntRoleId());
			sendFlashMessage(player, buffStat.getCaster(), resp);
		}
	}
	
	@Override
	public void delBuffStat(AbstractRole player,BuffStat buffStat,boolean timeOver){
		//player.delBuffStat(buffStat);
		//this.afterDelBuffStat(player, buffStat, timeOver,true);
		this.delBuffStat(player,buffStat,timeOver,true,true);
	}
	
	private void delBuffStat(AbstractRole player,BuffStat buffStat,
			boolean timeOver,boolean sendDelMsg,boolean realTimeNotifyAttri){
		if(null == buffStat){
			return ;
		}
		player.delBuffStat(buffStat);
		this.afterDelBuffStat(player, buffStat, timeOver,sendDelMsg,realTimeNotifyAttri);
	}
	
	public void delBuffStat(AbstractRole player, short buffId, boolean timeOver){
		BuffStat buffStat = player.getBuffStat(buffId);
		if(buffStat==null)return;
		delBuffStat(player, buffStat, timeOver);
	}
	
	public void delBuffStat(AbstractRole player, short buffId, boolean timeOver,String casterId){
		BuffStat buffStat = player.getBuffStat(buffId);
		if(buffStat==null){
			return;
		}
		if(null != casterId && (null == buffStat.getCaster() 
				|| !buffStat.getCaster().getRoleId().equals(casterId))){
			return ;
		}
		delBuffStat(player, buffStat, timeOver);
	}
	
	
	public int cleanBuffBySeries(AbstractRole player, int buffSeries, int count){
		int i=0;
		Collection<BuffStat> list = player.getReceiveBuffCopy();
		for(BuffStat stat : list){
			if(count > 0 && i>=count){
				return i;
			}
			if(stat.getBuffSeries()!=buffSeries){
				continue;
			}
			delBuffStat(player, stat, false);
			i++;
		}
		return i;
	}
	
    public int cleanBuffById(AbstractRole player,Set<Short> buffs,int count){
    	if(Util.isEmpty(buffs)){
    		return 0 ;
    	}
    	int i=0;
		Collection<BuffStat> list = player.getReceiveBuffCopy();
		for(BuffStat stat : list){
			if(count > 0 && i>=count){
				return i;
			}
			if(!buffs.contains(stat.getBuffId())){
				continue;
			}
			delBuffStat(player, stat, false);
			i++;
		}
		return i;
    }
	
	public int cleanBuffById(AbstractRole player,short buffId,int count){
    	int i=0;
		Collection<BuffStat> list = player.getReceiveBuffCopy();
		for(BuffStat stat : list){
			if(count > 0 && i>=count){
				return i;
			}
			if(buffId != stat.getBuffId()){
				continue;
			}
			delBuffStat(player, stat, false);
			i++;
		}
		return i;
	}
	
	@Override
	public List<RoleBuff> getRoleBuffList(String roleId) {
		return (List<RoleBuff>)(baseDAO.selectList(RoleBuff.class, "roleId", roleId));
	}

	@Override
	public Collection<BuffStat> initBuffStatList(List<RoleBuff> buffList,
			RoleInstance roleIn) {
		if (null == buffList || 0 == buffList.size()) {
			return new ArrayList<BuffStat>();
		}
		List<BuffStat> buffStatList = new ArrayList<BuffStat>();
		BuffStat stat = null;
		Date now = new Date();
		for (RoleBuff roleBuff : buffList) {
			if(roleBuff.getRemainTime()<=0){
				continue;
			}
			Buff buff = buffApp.getBuff(roleBuff.getBuffId());
			if(buff==null){
				continue;
			}
			int remainTime = roleBuff.getRemainTime() ;
			if(buff.isOfflineTiming()){
				//下线计时
				Date offlineTime = roleIn.getLastOffTime();
				if(null != offlineTime){
					long rt = now.getTime()-offlineTime.getTime();
					remainTime -= rt ;
					if(remainTime <=0){
						//已经失效
						continue ;
					}
				}
			}
			stat = new BuffStat(buff,roleBuff.getBuffLevel(),roleBuff.getIntervalTime());
			// 赋值操作
			stat.setBuffId(roleBuff.getBuffId());
			stat.setOwner(roleIn);
			stat.setBuffInfo(roleBuff.getExtroInfo());
			/**
			 * 
			 * 注意： 1，buff里面要记录caster是因为在计算仇恨值时需要知道是谁放的buff
			 * 2，buff的逻辑只允许对owner进行操作，不允许对caster进行操作
			 * 3，下线后再上来就不用再管是谁释放的buff了，仇恨值也不需要计算了
			 * 
			 */
	
			stat.setLastExecuteTime(now.getTime());
			stat.setRemainTime(remainTime);
			stat.setBuffSeries(roleBuff.getBuffSeries());
			stat.setCreateTime(now);
			stat.setCasterRoleId(roleBuff.getCasterId());
			buffStatList.add(stat);
		}
		return buffStatList;
	}
	
	@Override
	public void addAllRoleBuff(Collection<BuffStat> statList) {
		if(null == statList || 0 == statList.size()) return ;
		for(BuffStat stat : statList){
			RoleBuff roleBuff = new RoleBuff();
			roleBuff.setRemainTime((int)stat.getRemainTime());
			roleBuff.setRoleId(stat.getOwner().getRoleId());
			roleBuff.setBuffId(stat.getBuffId());
           /* if(null == stat.getCaster()){
                roleBuff.setCasterId("");
            }else{
                roleBuff.setCasterId(stat.getCaster().getRoleId());
            }*/
			roleBuff.setCasterId(stat.getCasterRoleId());
			roleBuff.setLastExecuteTime(new Date(stat.getLastExecuteTime()));
			roleBuff.setCreateTime(stat.getCreateTime());
			roleBuff.setExtroInfo(stat.getBuffInfo());
			roleBuff.setBuffLevel(stat.getBuffLevel());
			roleBuff.setIntervalTime(stat.getIntervalTime());
			roleBuff.setBuffSeries(stat.getBuffSeries());
			baseDAO.insert(roleBuff);
		}
	}

	@Override
	public void delAllRoleBuff(String roleId) {
		baseDAO.delete(RoleBuff.class, "roleId", roleId);
		
	}

	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}


	@SuppressWarnings("unchecked")
	public void runBuff(AbstractRole owner, long timeDiff, long now) {
		if(null == owner){
			return ;
		}
		Collection<BuffStat> tempBuffList =  owner.getReceiveBuffCopy();
		if(Util.isEmpty(tempBuffList)){
			return ;
		}
		
		/**
		 * TODO
		 * 这里有优化的余地
		 */
		
		for(Iterator<BuffStat> it = tempBuffList.iterator();it.hasNext();){
			BuffStat buffStat = it.next();
			if (null == buffStat) {
				continue;
			}
			Buff buff = buffStat.getBuff();
			if (null == buff) {
				it.remove();
				continue;
			}
			/**
			 * 
			 * 1、如果间隔时间为0   更新间隔时间 直接判断 3、
			 * 
			 * 2、
			 * 如果now-开始时间<buff持续时间
			 * 
			 * 					t=now-上次执行时间 
			 * 
			 * 如果now-开始时间>buff持续时间
			 * 
			 * 					t=buff开始时间+持续时间-上次时间			
			 * 
			 * 			t/间隔时间=执行次数
			 * 			上次执行时间=now-（t%间隔时间)
			 * 3、
			 * 如果(上次执行时间-开始时间+间隔时间)>持续时间 buff结束
			 * 
			 */
			int intervalTime = buffStat.getIntervalTime();
			if(intervalTime <= 0){
				if(buffStat.isTimeOver(now)){
					delBuffStat(owner, buffStat, true);
				}else{
					buffStat.setRemainTime(buffStat.getRemainTime()-(int)(now-buffStat.getLastExecuteTime()));
					buffStat.setLastExecuteTime(now);
				}
				continue;
			}
			long caculateTime = Math.min(now-buffStat.getLastExecuteTime(), buffStat.getRemainTime());
			/*if ((now - buffStat.getCreateTime().getTime()) < buffStat.getBuff()
					.getPersistTime(buffStat.getBuffLevel())) {
				caculateTime = now - buffStat.getLastExecuteTime();
			}else{
				caculateTime = buffStat.getCreateTime().getTime()
						+ buffStat.getBuff().getPersistTime(buffStat.getBuffLevel())
						- buffStat.getLastExecuteTime();
			}*/
			int executeNum = (int)(caculateTime/(long)intervalTime);
			int yuliangTime = (int)(caculateTime%(long)intervalTime);
			boolean remove = false ;
			if(!owner.isDeath()){
				//已经死亡不起效果
				for(int i=0; i<executeNum; i++) {
					BuffContext context = this.getBuffContext(owner, buffStat);
					buff.process(context);
					remove = context.isRemove();
				}
			}
			if(executeNum>0){
				buffStat.setRemainTime(buffStat.getRemainTime()-intervalTime*executeNum);
				buffStat.setLastExecuteTime(now - yuliangTime);
			}
			if(buffStat.isTimeOver(now)){
				delBuffStat(owner, buffStat, true);
			}else if(remove){
				delBuffStat(owner, buffStat, false);
			}
		}
	}
	


	/*@Override
	public byte getGoodsAddBuffStat(AbstractRole player, int goodsId) {
		try{
			GoodsFood g = (GoodsFood)GameContext.getGoodsApp().getGoodsBase(goodsId);
			int buffId = g.getTriggerBuffId();
			Buff buff = buffApp.getBuff((short)buffId);
			if (null == buff) {
				return GoodsAddBuffStatus.NOBUFF ;//没有buff
			}
			String key = buffId + Cat.underline + g.getTriggerBuffLv();
			BuffDetail buffDetail = ((BuffAdaptor)buff).getBuffDetail(g.getTriggerBuffLv());
			//GoodsBuffDetail buffDetail = GameContext.getBuffDetailDataLoader().getDataMap().get(key);
			Collection<BuffStat> list = player.getReceiveBuffCopy();
			for(BuffStat stat : list) {
				if (stat.getBuffId() != buffId) {
					continue;
				}
				if (stat.getBuffLevel() < buffDetail.getLevel()) {
					return GoodsAddBuffStatus.HASREPLACEBUFF;//有同等级或者低等级buff，二次确认是否需要替换
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return GoodsAddBuffStatus.HASNOREPLACEBUFF;//直接加
	}*/

	/**获得角色身上所有的buff对属性的修改*/
	@Override
	public AttriBuffer getAttriBuffer(AbstractRole player) {
		if(null == player){
			return null ;
		}
		Collection<BuffStat> tempBuffList =  player.getReceiveBuffCopy();
		if(Util.isEmpty(tempBuffList)){
			return null;
		}
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		for(Iterator<BuffStat> it = tempBuffList.iterator();it.hasNext();){
			buffer.append(it.next().getAttriBuffer());
		}
		return buffer ;
	}

	@Override
	public int hurtAbsorb(AbstractRole role,int hurts) {
		if(null == role || hurts <=0){
			return 0 ;
		}
		Collection<BuffStat> tempBuffList = role.getReceiveBuffCopy();
		if(Util.isEmpty(tempBuffList)){
			return 0;
		}
		int remainHurts = hurts ;
		int absorbValue = 0 ;
		BuffContext context = new BuffContext();
		for(Iterator<BuffStat> it = tempBuffList.iterator();it.hasNext();){
			BuffStat buffStat = it.next();
			if (null == buffStat) {
				continue;
			}
			Buff buff = buffStat.getBuff();
			if (null == buff) {
				it.remove();
				continue;
			}
			context.release();
			
			context.setBuffStat(buffStat);
			context.setInputHurts(remainHurts);
			
			buff.attacked(context);
			absorbValue += context.getAbsorbed();
			remainHurts -= context.getAbsorbed();
			if(remainHurts <=0){
				break ; 
			}
		}
		context.release();
		context = null ;
		return absorbValue ;
	}

	
	
}
