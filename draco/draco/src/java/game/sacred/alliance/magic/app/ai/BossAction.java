package sacred.alliance.magic.app.ai;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.push.C1190_NpcMusicNotifyMessage;

import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.chat.ChatSysName;
import sacred.alliance.magic.base.CondCompareType;
import sacred.alliance.magic.base.SkillApplyResult;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.MapInstance;

public @Data class BossAction {
	private final static Logger logger = LoggerFactory.getLogger(BossAction.class);
	private final static int COND_TYPE_HP = 1 ;
	private final static int EXEC_NULL = 0 ; //空实现
	private final static int EXEC_TYPE_SKILL = 1 ; //使用技能
	private final static int EXEC_TYPE_SPEAK = 2 ; //说话
	private final static int EXEC_TYPE_SUMMON = 3 ; //召唤怪物
	private final static int EXEC_TYPE_MUSIC = 4 ; //怪物广播音乐
	
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
				// System.out.println("=============== result:" + result.getType() + " " + execData1);
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
				GameContext.getChatApp().sendSysMessage(ChatSysName.BOSS_AI,channelType, this.execData2, null, target);
				// 说话
				break;
			}
			case EXEC_TYPE_SUMMON:{
				// 召唤怪物
				GameContext.getAiApplication().bossRefresh(Integer.parseInt(execData1), npc);
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
				attriValue = (int)((float)100*npc.getCurHP()/npc.getMaxHP()) ;
				break ;
			default:
				return false ;
		}
	   return CondCompareType.isMeet(condCompareType, 
			   attriValue, condValue, condValue, null);
	}
}
