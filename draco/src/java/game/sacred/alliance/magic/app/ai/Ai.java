package sacred.alliance.magic.app.ai;

import java.util.List;

import sacred.alliance.magic.base.SkillApplyResult;
import sacred.alliance.magic.constant.AIMoveConstant;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.message.item.NpcFunctionItem;
import com.google.common.collect.Lists;

public abstract class Ai implements java.lang.Cloneable {
	
    protected String aiId;
    /**NPC的状态机*/
    protected StateMachine<AbstractRole> stateMachine;
    protected AbstractRole role;
    protected WayPointInfo wayPointInfo ;
    protected GameContext context = GameContext.getGameContext() ;
    
    protected List<AiMessageListener> aiMessageListenerList = null ;

    /**
     * 判断是否在自己的视野范围内
     * @param target
     * @return
     */
    public abstract boolean isInView(AbstractRole target);
    
    /**
     * 判断是否在呼救范围内
     * @param target
     * @return
     */
    public abstract boolean isInSummonedView(AbstractRole target);

    //对话NPC
    public abstract List<NpcFunctionItem> talkTo(AbstractRole target);
    
    public abstract String afterContent(AbstractRole target);
    //选择
    public abstract List<NpcFunctionItem> choice(AbstractRole target, int index);
    
    
    public NpcFunctionItem getDefaultNpcChoiceFunctionItem(String title, int index){
    	return new NpcFunctionItem(title,AIMoveConstant.NPC_CHOICE_COMMANDID, "roleid="
				+ role.getRoleId() + "&index=" + index);
    }
    
    //进入视线范围
    public abstract void moveInLineOfSight(AbstractRole target);

    //当每次攻击触发
    public abstract void attackStart(AbstractRole attacker, int hatred);

    //脱离战斗模式
    public abstract void enterEvadeMode();

    //当对目标造成伤害
    //public abstract void damageDeal(AbstractRole target, int hurt);
    
    //当有攻击者造成伤害
    public abstract void damageTaken(AbstractRole attacker, int hurt);
    
    //更新AI
    public abstract void updateAI();

    //刚死亡
    public abstract void justDied();
    
    //Npc死亡奖励
    public abstract void npcDiedEncouragement();

    //看见一个角色刚被杀死
    public abstract void killedRole(AbstractRole target);

    //响应寻求回血
    public abstract void respondSeekRescue(AbstractRole seeker);
    
    //召唤同伴
    public abstract void summonedRole();

    //刚召唤同伴成功
    public abstract void justSummoned();

    //接收到召唤消息
    public abstract void receiveSummonMsg(AbstractRole summonRole);

    //被召唤成功
    public abstract void beSummoned(AbstractRole summonRole);

    //刚诞生
    public abstract void justRespawned();

    //到达目的地通知
    public abstract void movementInform();

    //当攻击者在不能到达的地方
    public abstract void canReachByRangeAttack(AbstractRole attack);

    //追逐上目标
    public abstract void chasedTarget(AbstractRole chaser);

    //选择技能
    public abstract SkillSelectResult selectSkill();
    
    //选择回血技能
    public abstract int selectRescueSkill();

    //进入逃跑
    public abstract void enterEscapeMode();

    //逃跑条件
    public abstract boolean escapeConditions() ;

    
    //召唤条件
    public abstract boolean summonedConditions();
    
    //逃跑召唤条件
    public abstract boolean escapeSummonedConditions();


    /**
     * 此接口不需要考虑用户状态,这里接收到的消息都是已经过滤的
     * 调用者为各State 中的 handleMessage
     * @param entity
     * @param telegram
     */
   // public abstract void handleMessage(NpcInstance entity, Telegram telegram);
    
    protected void postAiMessageEvent(MessageType messageType){
    	if(null == messageType){
    		return ;
    	}
    	if(Util.isEmpty(this.aiMessageListenerList)){
    		return ;
    	}
    	for(AiMessageListener listener : aiMessageListenerList){
    		listener.onEvent(messageType, role) ;
    	}
    }
    
    public boolean register(AiMessageListener listener) {
    	if(null == listener){
    		return false ;
    	}
    	if(null == this.aiMessageListenerList){
    		this.aiMessageListenerList = Lists.newArrayList() ;
    	}
    	String listenerKey = listener.getClass().getName() ;
    	for(AiMessageListener exist : aiMessageListenerList){
    		if(exist.getClass().getName().equals(listenerKey)){
    			return false ;
    		}
    	}
    	this.aiMessageListenerList.add(listener);
    	return true ;
    }

    /**绑定角色属性 AI初始化的时候有可能会修改角色属性
    eg: 添加相关技能
     */
    public abstract void init();
    
    //是否在攻击范围
    public abstract boolean inAttackRange(AbstractRole target) ;

    //离开视野范围
    public abstract boolean isOutOfView(AbstractRole target) ;

    //自己离家太远
    public abstract boolean tooFarFromHome();
    
    //目标离自己家太远
    public abstract boolean tooFarFromHome(AbstractRole target);

    //在家附近
    public abstract boolean nearFromHome();
    
    /**
     * 重置状态参数
     */
    public abstract void resetStateParameter();

    //是否主动攻击
    public abstract boolean isActiveAttack();
    
    /**存储副本进度(NPC杀死的时候调用)*/
    //public abstract boolean isStoreCopyProcess();
    
    public abstract int getAlertArea() ;
    
    public abstract int getThinkArea();
    
    /**判断是否在仇恨列表中*/
   /* public boolean inHatredMap(String targetId){
    	return role.getHatredTarget().inHatredMap(targetId);
    }
    //仇恨列表是否为空
    public boolean isEmptyHatredMap(){
    	return role.getHatredTarget().isEmptyHatredMap();
    }
    
    //删除某仇恨用户
    public void removeHateTarget(String targetId){
    	role.getHatredTarget().removeHateTarget(targetId);
    }
    
    public void clearHatredMap(){
    	role.getHatredTarget().clearHatredMap();
    }

    //获得第一仇恨目标
    public String getFirstHateTarget(){
    	return role.getHatredTarget().getFirstHateTarget();
    }
    
    public void addHatred(String hateTarget,int hateValue){
    	role.getHatredTarget().addHatred(hateTarget, hateValue);
    }*/
    
    public  WayPointInfo getWapPointInfo() {
        if(null == this.wayPointInfo){
            wayPointInfo = new WayPointInfo();
        }
        return wayPointInfo ;
    }

    public Ai(String aiId) {
        this.aiId = aiId;
    }

    public String getAiId() {
        return aiId;
    }

    public void setAiId(String aiId) {
        this.aiId = aiId;
    }

    public AbstractRole getRole() {
        return role;
    }

    public void setRole(AbstractRole role) {
        this.role = role;
    }

    public Object clone() {
        Ai ai = null;
        try {
            ai = (Ai) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return ai;
    }

    public StateMachine<AbstractRole> getStateMachine() {
        return stateMachine;
    }

    public void setStateMachine(StateMachine<AbstractRole> stateMachine) {
        this.stateMachine = stateMachine;
    }
        

   public class WayPointInfo{
        private int currentNodeIndex ;
        boolean forward = true;
        public WayPointInfo(){

        }

        public void incrCurrentNodeIndex() {
            this.currentNodeIndex ++ ;
        }

        public void deIncrCurrentNodeIndex() {
            this.currentNodeIndex -- ;
        }
         
        public WayPointInfo(int currentNodeIndex,boolean forward){
            this.currentNodeIndex = currentNodeIndex ;
            this.forward = forward ;
        }

        public int getCurrentNodeIndex() {
            return currentNodeIndex;
        }

        public void setCurrentNodeIndex(int currentNodeIndex) {
            this.currentNodeIndex = currentNodeIndex;
        }

        public boolean isForward() {
            return forward;
        }

        public void setForward(boolean forward) {
            this.forward = forward;
        }
    }

   //使用技能
   public abstract SkillApplyResult useSkill(NpcInstance entity,int skillId);
   
   //重置技能
   public abstract RoleSkillStat getNormalSkill();
   
   public boolean isAutoMaxHp(){
	   return false ;
   }
   
}
