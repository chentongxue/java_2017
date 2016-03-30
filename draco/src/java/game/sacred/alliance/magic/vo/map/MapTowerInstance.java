package sacred.alliance.magic.vo.map;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.tower.config.TowerGateConfig;
import com.game.draco.app.tower.config.TowerLayerConfig;
import com.game.draco.app.tower.domain.TowerCurrLayer;
import com.game.draco.message.internal.C0058_TowerPassedInternalMessage;
import com.game.draco.message.response.C2558_TowerMapPassedRespMessage;
import lombok.Getter;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapInstanceEvent;
import sacred.alliance.magic.vo.RoleInstance;

import java.util.Collection;
import java.util.Date;

public class MapTowerInstance extends MapInstance {

    protected final LoopCount refreshNpcLoop = new LoopCount(1000);// 1秒
    public static enum TowerState {
        _default,
        _complete,
        _fin,
        _failure,
        ;
    }
    @Getter
    private int heroOnBattleTimes = 0 ;

    @Getter private short gateId ;
    @Getter private byte layerId ;

    private int refreshRuleId;//刷怪ID
    private int refreshRuleMax;
    protected Date startTime;// 开始时间（倒计时结束自动传出）
    private Date endTime;// 倒计时结束时间
    private int ruleIndex = 0;// 已刷怪序列 npcRuleList的索引
    private TowerState towerState = TowerState._default ;
    private RoleInstance role ;

    public int getUseSecondTime(){
        return (int)((this.endTime.getTime()-this.startTime.getTime())/1000) ;
    }

    public MapTowerInstance(Map map){
        super(map);
    }

    @Override
    protected void enter(AbstractRole role){
        //第一人进入初始化时间，赋值波次List
        if(1 != this.getRoleCount()){
            return ;
        }
        this.role = (RoleInstance)role ;
        TowerCurrLayer currLayer = GameContext.getTowerApp().getTowerCurrLayer(role.getRoleId());
        this.gateId = currLayer.getGateId() ;
        this.layerId = currLayer.getLayerId();
        this.startTime = new Date();
        //根据gateId,layerId 获得配置
        TowerLayerConfig layerConfig = GameContext.getTowerApp().getTowerLayerConfig(this.gateId,this.layerId);
        this.refreshRuleId = layerConfig.getRuleId() ;
        this.refreshRuleMax = GameContext.getRefreshRuleApp().getRefreshMax(refreshRuleId);
        this.ruleIndex = 0;
        //满血满蓝
        this.perfectBody(role);
        //发送条件
        Message condMsg = GameContext.getTowerApp().getLayerCondMessage(this.gateId,this.layerId);
        if(null != condMsg){
            GameContext.getMessageCenter().sendSysMsg(role,condMsg);
        }

    }

    @Override
    public void npcDeath(NpcInstance npc) {
        super.npcDeath(npc);

        if(!this.isComplete()){
            return ;
        }
        if(this.towerState == TowerState._default){
            //设置为完成状态
            this.towerState = TowerState._complete ;
            // 结束时间
            this.endTime = new Date();
        }
    }

    //是否杀死所以敌对怪
    private boolean isComplete(){
        //波次列表是否完成
        if(this.ruleIndex < this.refreshRuleMax){
            return false;
        }
        //地图当前是否完成
        if(null == this.npcList || 0 == this.npcList.size()){
            return true;
        }
        //是否还存在敌对NPC
        for(NpcInstance npc : this.npcList){
            for(RoleInstance role : this.getRoleList()){
                if (role.getForceRelation(npc) == ForceRelation.enemy) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    protected String createInstanceId() {
        instanceId = "tower_" + instanceIdGenerator.incrementAndGet();
        return instanceId;
    }


    public void completeLogic(){
       //发送异步消息到单用户单线程中
        C0058_TowerPassedInternalMessage reqMsg = new C0058_TowerPassedInternalMessage();
        reqMsg.setRole(role);
        role.getBehavior().addCumulateEvent(reqMsg);
    }

    @Override
    public void updateSub(){
        try {
            super.updateSub();
            if(this.towerState == TowerState._fin ||
                    this.towerState == TowerState._failure){
                //翻牌状态不再需要做任何操作
                return ;
            }
            if(this.towerState == TowerState._complete){
                //已经通关
                this.completeLogic() ;
                //翻牌状态
                this.towerState = TowerState._fin;
                return ;
            }
            //刷怪
            this.refreshAction() ;
        } catch (Exception e) {
            logger.error("",e);
        }
    }

    @Override
    public boolean canDestroy() {
        if(this.getRoleCount() == 0){
            return true;
        }
        return false;
    }

    @Override
    public boolean canEnter(AbstractRole role) {
        return (null != GameContext.getTowerApp().getTowerCurrLayer(role.getRoleId())) ;
    }

    @Override
    protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {

    }

    @Override
    protected void deathLog(AbstractRole victim) {

    }

    @Override
    public void useGoods(int goodsId) {

    }

    @Override
    public void broadcastScreenMap(AbstractRole role, Message message) {
        super.broadcastMap(role, message, 0);
    }

    @Override
    public void broadcastScreenMap(AbstractRole role, Message message, int expireTime) {
        super.broadcastMap(role, message, expireTime);
    }

    /**
     * 主循环刷怪
     */
    private void refreshAction(){
        if(!refreshNpcLoop.isReachCycle()){
            return ;
        }
        if(this.ruleIndex >= refreshRuleMax){
            return ;
        }
        this.ruleIndex = GameContext.getRefreshRuleApp().refresh(this.refreshRuleId, this.ruleIndex, startTime, this, false);
    }

    @Override
    public void exitMap(AbstractRole role) {
        try {
            super.exitMap(role);
            String targetMapId = role.getMapId();
            if(Util.isEmpty(targetMapId)){
               return ;
            }
            MapConfig targetMapConfig = GameContext.getMapApp().getMapConfig(targetMapId);
            if(null == targetMapConfig
                    || targetMapConfig.getMapLogicType() == MapLogicType.tower){
                return ;
            }
            //重新pushui
            GameContext.getMessageCenter().sendSysMsg(role,
                    GameContext.getTowerApp().getTowerInfoMessage((RoleInstance)role,this.gateId));
        }finally{
            this.destroy();
            this.role = null ;
        }
    }

    public void doEvent(RoleInstance role,MapInstanceEvent event){
        if(event.getEventType() == MapInstanceEvent.EventType.heroOnBattle){
            heroOnBattleTimes++ ;
        }
    }

    @Override
    public void roleDeath(AbstractRole attacker, RoleInstance victim){
        super.roleDeath(attacker,victim);
        //其他逻辑
        if(this.towerState == TowerState._default){
            this.towerState = TowerState._failure ;
        }
    }

}
