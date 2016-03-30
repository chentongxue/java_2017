package sacred.alliance.magic.app.ai;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.CondCompareType;
import sacred.alliance.magic.base.SkillApplyResult;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.push.C0110_AiScriptNotifyMessage;
import com.game.draco.message.push.C0111_AiProgressBarNotifyMessage;
import com.game.draco.message.push.C0216_WalkTeleportNotifyMessage;
import com.game.draco.message.push.C1190_NpcMusicNotifyMessage;
import com.game.draco.message.response.C0200_WalkSynchMoveRespMessage;

public @Data class BossAction {
	private final static Logger logger = LoggerFactory.getLogger(BossAction.class);
	private final static int COND_TYPE_HP = 1 ;
	private final static int EXEC_NULL = 0 ; //空实现
	private final static int EXEC_TYPE_SKILL = 1 ; //使用技能
	private final static int EXEC_TYPE_SPEAK = 2 ; //说话
	private final static int EXEC_TYPE_SUMMON = 3 ; //召唤怪物
	private final static int EXEC_TYPE_MUSIC = 4 ; //怪物广播音乐
	private final static int EXEC_TYPE_SHAPESHIFT = 5;//变身
	private final static int EXEC_TYPE_SPLIT = 6;//分裂
	private final static int EXEC_TYPE_SPLIT_DEFINITION = 7;//分裂删除指定精灵
	private final static int EXEC_TYPE_SCRIPT = 8;//精灵播放特效
	private final static int EXEC_CHANGE_DIR = 9 ; //切换朝向
	private final static int EXEC_PROGRESS_BAR = 10 ; //进度条
	private final static int EXEC_TYPE_PULL = 11 ;//拉回
	private final static int EXEC_TYPE_SPLIT_BLOCK = 12;//分裂删除指定障碍物
	
	private int linkId ;
	private int id ;
	private int interval ;
	private int condType ;
	private int compareType ;
	private int condValue ;
	private int failureId ;
	private int execType ;
	private String execData1 ;
	private String execData2 ;
	private String execData3 ;
	private String execData4 ;
	private int successId ;
	
	private CondCompareType condCompareType ;
	private Map<Integer,Integer> skillMap = new HashMap<Integer,Integer>();
	
	public void init(){
		if(this.condType > 0){
			this.condCompareType = CondCompareType.get(this.compareType);
			if(null == condCompareType){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("BossAction config error: this condType not exist,linkId=" + linkId + " id=" + id + " condType=" + condType);
			}
		}
		if(execType == EXEC_TYPE_SKILL){
			int skillLv = 1 ;
			if(!Util.isEmpty(this.execData2)){
				skillLv = Integer.parseInt(execData2);
			}
			skillMap.put(Integer.parseInt(execData1), skillLv);
		}
	}
	
	public boolean execute(NpcInstance npc){
		if(!this.can(npc)){
			return false ;
		}
		this.done(npc);
		return true ;
	}
	
	private void done(NpcInstance npc){
		if(this.execType <= EXEC_NULL){
			//空行为
			return ;
		}
		try {
			switch (this.execType) {
				case EXEC_TYPE_SKILL:{
					// 使用技能
					SkillApplyResult result = GameContext.getUserSkillApp().useSkill(npc, Short.parseShort(execData1));
					if (null == execData3 || execData3.length() == 0) {
						break;
					}
					if (result != SkillApplyResult.SUCCESS) {
						break;
					}
					MapInstance mapInstance = npc.getMapInstance();
					if(null == mapInstance) {
						break;
					}
					C1190_NpcMusicNotifyMessage message = new C1190_NpcMusicNotifyMessage();
					short musicId = Short.parseShort(execData3);
					message.setMusicId((byte)musicId);
					mapInstance.broadcastMap(null, message);
					break;
				}
				case EXEC_TYPE_SPEAK:{
					ChannelType channelType = ChannelType.getChannelType(Byte.parseByte(this.execData1));
					Serializable target = null;
					if (channelType == ChannelType.Map || channelType == ChannelType.Speak) {
						target = npc.getMapInstance();
					}
					GameContext.getChatApp().sendSysMessage(npc,channelType, this.execData2, null, target);
					// 说话
					break;
				}
				case EXEC_TYPE_SUMMON:{
					// 召唤怪物
					GameContext.getAiApp().bossRefresh(Integer.parseInt(execData1), npc);
					break;
				}
				case EXEC_TYPE_MUSIC:{
					MapInstance mapInstance = npc.getMapInstance();
					if(null == mapInstance) {
						break;
					}
					C1190_NpcMusicNotifyMessage message = new C1190_NpcMusicNotifyMessage();
					short musicId = Short.parseShort(execData1);
					message.setMusicId((byte)musicId);
					mapInstance.broadcastMap(null, message);
					break;
				}
				case EXEC_TYPE_SHAPESHIFT: 
				{
					MapInstance mapInstance = npc.getMapInstance();
					if (null == mapInstance) {
						break;
					}
					String resCol = getExecData1();
					if(resCol != null && !"".equals(resCol) && getExecData2() != null && getExecData3() != null){
						String [] resColArr = resCol.split(",");
						int color = (int) Long.parseLong(resColArr[1], 16);
						GameContext.getSkillApp().roleChangeColor(npc, color,Integer.parseInt(getExecData3()));
						GameContext.getSkillApp().roleChangeShape(npc, Integer.parseInt(resColArr[0]), Integer.parseInt(getExecData3()));
						GameContext.getSkillApp().roleChangeZoom(npc, Integer.parseInt(getExecData2()), Integer.parseInt(getExecData3()));
					}
					break;
				}
				case EXEC_TYPE_SPLIT:
				{
					if(!Util.isEmpty(getExecData1()) && !Util.isEmpty(getExecData2()) && !Util.isEmpty(getExecData3())) {
						// 召唤分裂
						GameContext.getAiApp().bossSpilt(npc,this);
					}
					break;
				}
				case EXEC_TYPE_SPLIT_DEFINITION:
				{
					if(!Util.isEmpty(getExecData1()) && !Util.isEmpty(getExecData2()) && !Util.isEmpty(getExecData3())) {
						// 分裂并删除指定NPC
						GameContext.getAiApp().bossSpiltDefinition(npc,this);
					}
					break;
				}
				case EXEC_TYPE_SPLIT_BLOCK:
				{
					if(!Util.isEmpty(getExecData1()) && !Util.isEmpty(getExecData2()) && !Util.isEmpty(getExecData3())) {
						// 分裂并删除指定NPC
						GameContext.getAiApp().bossSpiltBlock(npc,this);
					}
					break;
				}
				case EXEC_TYPE_SCRIPT:
				{
					MapInstance mapInstance = npc.getMapInstance();
					if(null == mapInstance) {
						break;
					}
					C0110_AiScriptNotifyMessage message = new C0110_AiScriptNotifyMessage();
					message.setScript(Short.parseShort(getExecData1()));
					message.setSpriteId(npc.getIntRoleId());
					if(npc.getTarget() != null){
						message.setTargetId(npc.getTarget().getIntRoleId());
					}
					mapInstance.broadcastMap(npc, message);
					break;
				}
				case EXEC_CHANGE_DIR :{
					MapInstance mapInstance = npc.getMapInstance();
					if(null == mapInstance) {
						break;
					}
					//获得仇恨列表中随机一目标
					Object oid = npc.getHatredTarget().getHatredMap().getRandomKey() ;
					if(null == oid){
						break ;
					}
					RoleInstance role = mapInstance.getRoleInstance(oid.toString());
					if(null == role){
						break ;
					}
					//修改朝向
					int dir = Util.getAngle(role.getMapX()-npc.getMapX(), 
							role.getMapY()-npc.getMapY()) ;
					npc.setDir((byte)Util.getDir((byte)dir));
					break ;
				}
				case EXEC_PROGRESS_BAR:{
					MapInstance mapInstance = npc.getMapInstance();
					if(null == mapInstance) {
						break;
					}
					C0111_AiProgressBarNotifyMessage notifyMessage = new C0111_AiProgressBarNotifyMessage();
					notifyMessage.setEntityId(npc.getIntRoleId());
					int time = Integer.parseInt(execData1) ;
					notifyMessage.setTime(time);
					notifyMessage.setTips(execData2);
					byte dir = 0 ;
					if(Util.isNumber(execData3)){
						dir = Byte.parseByte(execData3) ;
					}
					notifyMessage.setDirection(dir);
					if(Util.isNumber(execData4)){
						//添加buff
						short buffId = Short.parseShort(execData4);
						if(0 != buffId){
							GameContext.getUserBuffApp().addBuffStat(npc, npc, buffId, time, 1);
						}
					}
					mapInstance.broadcastMap(null,notifyMessage);
					break;
				}
				case EXEC_TYPE_PULL:{
					MapInstance mapInstance = npc.getMapInstance();
					if(null == mapInstance) {
						break;
					}
					if(npc.isDeath()){
						break;
					}
					if(Util.isEmpty(npc.getSummonRoleId())){
						break;
					}
//					AbstractRole role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(npc.getSummonRoleId());
//					if(npc.getMapInstance().getInstanceId().equals(role.getMapInstance().getInstanceId())){
//						if(role.getMapX() != npc.getMapX() || role.getMapY() != npc.getMapY()){
//							C0216_WalkTeleportNotifyMessage respMsg = new C0216_WalkTeleportNotifyMessage();
//							Point point = new Point(npc.getMapId(), role.getMapX(), role.getMapY());
//							respMsg.setRoleId(npc.getIntRoleId());
//							respMsg.setX((short)point.getX());
//							respMsg.setY((short)(point.getY()-10));
//							respMsg.setEventType(point.getEventType());
//							role.getBehavior().notifyPosition(respMsg);
//							System.out.println(respMsg);
//						}
//					}
					break;
				}
			}
		}catch(Exception ex){
			logger.error("",ex);
		}
	}
	
	private boolean can(NpcInstance npc){
		if(condType <=0){
			return true ;
		}
		int attriValue = 0 ;
		switch(condType){
			case COND_TYPE_HP :
				attriValue = (int)(npc.getCurHP()/(float)npc.getMaxHP()*100) ;
				break ;
			default:
				return false ;
		}
	   return CondCompareType.isMeet(condCompareType, 
			   attriValue, condValue, condValue, null);
	}
}
