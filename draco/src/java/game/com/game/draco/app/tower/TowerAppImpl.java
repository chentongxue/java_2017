package com.game.draco.app.tower;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.tower.config.*;
import com.game.draco.app.tower.domain.RoleTowerGate;
import com.game.draco.app.tower.domain.RoleTowerInfo;
import com.game.draco.app.tower.domain.TowerCurrLayer;
import com.game.draco.app.tower.type.TowerGateStatus;
import com.game.draco.app.tower.type.TowerLayerStatus;
import com.game.draco.app.tower.type.TowerStarConditionType;
import com.game.draco.app.vip.type.VipPrivilegeType;
import com.game.draco.message.item.*;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.base.*;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.util.*;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.TowerPoint;
import sacred.alliance.magic.vo.map.MapTowerContainer;
import sacred.alliance.magic.vo.map.MapTowerInstance;

import java.util.List;
import java.util.Map;

public
@Data
class TowerAppImpl implements TowerApp {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public final static int LAYER_COND_NUM = 3;
    private final static byte RESET_STATUS = 0;
    private final static byte NOT_RESET_STATUS = -1;
    private final static short MIN_GATE_ID = 1;
    private final static byte MIN_LAYER = 1;
    private final static byte MAX_LAYER = 4;
    private final static byte MAX_GATE_STAR = MAX_LAYER * 3;

    private MapTowerContainer mapTowerContainer = new MapTowerContainer();

    private Map<String, TowerCurrLayer> roleCurrLayerMap = Maps.newConcurrentMap();
    private Map<String, RoleTowerInfo> roleTowerGateMap = Maps.newConcurrentMap();

    private TowerAppConfig towerAppConfig;
    private Map<Integer, TowerConditionConfig> towerConditionConfigMap;
    private Map<String, TowerLayerConfig> towerLayerConfigMap;
    private Map<Short, TowerGateConfig> towerGateConfigMap;
    private Map<Short, List<TowerStarAwardConfig>> towerStarAwardConfigMap;

    @Override
    public void setArgs(Object arg0) {

    }

    public List<TowerConditionConfig> getLayerConditionList(short gateId, byte layerId) {
        List<TowerConditionConfig> ret = Lists.newArrayList();
        TowerLayerConfig config = this.getTowerLayerConfig(gateId, layerId);
        if (null == config) {
            return ret;
        }
        for (int condId : config.getCondList()) {
            TowerConditionConfig condConfig = this.getTowerConditionConfig(condId);
            if (null == condConfig) {
                continue;
            }
            ret.add(condConfig);
        }
        return ret;
    }

    @Override
    public Message getLayerCondMessage(short gateId, byte layerId) {
        List<TowerConditionConfig> condList = this.getLayerConditionList(gateId, layerId);
        if (Util.isEmpty(condList)) {
            return null;
        }
        C2556_TowerStarConditonRespMessage respMsg = new C2556_TowerStarConditonRespMessage();
        respMsg.setGateId(gateId);
        respMsg.setLayerId(layerId);
        String[] desc = new String[LAYER_COND_NUM];
        int index = 0;
        for (TowerConditionConfig config : condList) {
            desc[index++] = config.getDesc();
        }
        respMsg.setCondDesc(desc);
        return respMsg;
    }

    private byte[] getTowerStarResult(RoleInstance role) {
        byte[] result = new byte[LAYER_COND_NUM];
        MapInstance mapInstance = role.getMapInstance();
        if (null == mapInstance || !(mapInstance instanceof MapTowerInstance)) {
            return result;
        }
        MapTowerInstance towerInstance = (MapTowerInstance) mapInstance;
        //获得条你列表
        List<TowerConditionConfig> condList = this.getLayerConditionList(towerInstance.getGateId(),
                towerInstance.getLayerId());
        if (Util.isEmpty(condList)) {
            return result;
        }
        int index = -1;
        for (TowerConditionConfig cond : condList) {
            index++;
            TowerStarConditionType condType = TowerStarConditionType.getType(cond.getStarConditonType());
            if(null == condType){
                continue ;
            }
            boolean ok = false;
            switch (condType) {
                case TIME_USED:
                    ok = cond.getData() >= towerInstance.getUseSecondTime();
                    break;
                case HEROS_HP:
                    List<RoleHero> heroList = GameContext.getHeroApp().getRoleSwitchableHeroList(role.getRoleId());
                    if (Util.isEmpty(heroList)) {
                        break;
                    }
                    ok = true ;
                    for (RoleHero hero : heroList) {
                        if (hero.getHpRate() <= cond.getData() * 100) {
                            ok = false ;
                            break;
                        }
                    }
                    break;
                case HERO_SERIES_LIMIT:
                    List<RoleHero> list = GameContext.getHeroApp().getRoleSwitchableHeroList(role.getRoleId());
                    if (Util.isEmpty(list)) {
                        break;
                    }
                    ok = true ;
                    for (RoleHero hero : list) {
                        GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, hero.getHeroId());
                        if (null != goodsHero && goodsHero.getSeriesId() == cond.getData()) {
                            ok = false ;
                            break;
                        }
                    }
                    break;
                case HERO_SWITCH_TIMES:
                    ok = cond.getData() > towerInstance.getHeroOnBattleTimes();
                    break;
                default:
                    break;

            }
            if (ok) {
                result[index] = 1;
            }
        }
        return result;
    }

    @Override
    public void start() {
        loadTowerAppConfig();
        loadTowerConditionConfigMap();
        loadTowerGateConfigMap();
        loadTowerLayerConfigMap();
        loadTowerStarAwardConfigMap();
    }

    private TowerConditionConfig getTowerConditionConfig(int condId) {
        return Util.fromMap(this.towerConditionConfigMap, condId);
    }

    private void loadTowerStarAwardConfigMap() {
        List<TowerStarAwardConfig> list = this.loadConfigList(XlsSheetNameType.tower_star_award_config, TowerStarAwardConfig.class);
        if (Util.isEmpty(list)) {
            return;
        }
        Map<Short, List<TowerStarAwardConfig>> map = Maps.newHashMap();
        for (TowerStarAwardConfig cf : list) {
            cf.init();
            short gateId = cf.getGate();
            List<TowerStarAwardConfig> awardList = map.get(gateId);
            if (null == awardList) {
                awardList = Lists.newArrayList();
                map.put(gateId, awardList);
            }
            awardList.add(cf);
        }
        this.towerStarAwardConfigMap = map;
    }

    private void loadTowerGateConfigMap() {
        towerGateConfigMap = loadConfigMap(
                XlsSheetNameType.tower_gate_config, TowerGateConfig.class,
                false);
        for (TowerGateConfig cf : towerGateConfigMap.values()) {
            cf.init();
        }
    }

    private void loadTowerLayerConfigMap() {
        towerLayerConfigMap = loadConfigMap(
                XlsSheetNameType.tower_layer_config, TowerLayerConfig.class,
                false);
        for (TowerLayerConfig cf : towerLayerConfigMap.values()) {
            cf.init();
            if (cf.getLayer() > MAX_LAYER || cf.getLayer() < MIN_LAYER) {
                this.checkFail("tower layer error, layerId value must [1-4],gate="
                        + cf.getGate() + "layerId=" + cf.getLayer());
            }
            for (int condId : cf.getCondList()) {
                if (null != this.getTowerConditionConfig(condId)) {
                    continue;
                }
                this.checkFail("tower layer error, cond not exist ,gate="
                        + cf.getGate() + "layerId=" + cf.getLayer() + " condId=" + condId);
            }
        }
    }

    private void loadTowerConditionConfigMap() {
        towerConditionConfigMap = loadConfigMap(
                XlsSheetNameType.tower_condition_config, TowerConditionConfig.class,
                false);
        for (TowerConditionConfig cf : towerConditionConfigMap.values()) {
            cf.init();
        }
    }

    @Override
    public void stop() {

    }

    private void loadTowerAppConfig() {
        String fileName = XlsSheetNameType.tower_app_config.getXlsName();
        String sheetName = XlsSheetNameType.tower_app_config
                .getSheetName();
        String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
        try {
            this.towerAppConfig = XlsPojoUtil.getEntity(sourceFile,
                    sheetName, TowerAppConfig.class);
            if (null == towerAppConfig) {
                this.checkFail("load Excel error: " + fileName + ",sheet="
                        + sheetName + " is not config!");
            }
        } catch (Exception e) {
            this.checkFail("load Excel error: " + fileName + ",sheet="
                    + sheetName + " is not config!");
        }
    }

    private <K, V extends KeySupport<K>> Map<K, V> loadConfigMap(
            XlsSheetNameType xls, Class<V> clazz, boolean linked) {
        String fileName = xls.getXlsName();
        String sheetName = xls.getSheetName();
        String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
        Map<K, V> map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName,
                clazz, linked);
        if (Util.isEmpty(map)) {
            checkFail("not config the " + clazz.getSimpleName() + " ,file="
                    + sourceFile + " sheet=" + sheetName);
        }
        return map;
    }

    private <T> List<T> loadConfigList(XlsSheetNameType xls, Class<T> t) {
        List<T> list = null;
        String fileName = xls.getXlsName();
        String sheetName = xls.getSheetName();
        String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
        try {
            list = XlsPojoUtil.sheetToList(sourceFile, sheetName,
                    t);
        } catch (Exception e) {
            Log4jManager.CHECK.error("load " + t.getSimpleName() + " error:fileName=" + fileName + ",sheetName=" + sheetName);
            Log4jManager.checkFail();

        }
        if (Util.isEmpty(list)) {
            Log4jManager.CHECK.error("load " + t.getSimpleName() + " error: result is null fileName=" + fileName + ",sheetName=" + sheetName);
            Log4jManager.checkFail();
        }
        return list;
    }

    private void checkFail(String info) {
        Log4jManager.CHECK.error(info);
        Log4jManager.checkFail();
    }

    @Override
    public int onCleanup(String roleId, Object context) {
        this.roleCurrLayerMap.remove(roleId);
        this.roleTowerGateMap.remove(roleId);
        return 1;
    }

    @Override
    public int onLogin(RoleInstance role, Object context) {
        List<RoleTowerGate> gateList = GameContext.getBaseDAO().selectList(RoleTowerGate.class, RoleTowerGate.ROLE_ID, role.getRoleId());
        RoleTowerInfo towerInfo = new RoleTowerInfo();
        this.roleTowerGateMap.put(role.getRoleId(), towerInfo);
        if (Util.isEmpty(gateList)) {
            return 1;
        }
        for (RoleTowerGate gate : gateList) {
            towerInfo.putRoleTowerGate(gate);
        }
        return 1;
    }


    @Override
    public int onLogout(RoleInstance role, Object context) {
        this.roleCurrLayerMap.remove(role.getRoleId());
        this.roleTowerGateMap.remove(role.getRoleId());
        return 1;
    }


    public TowerCurrLayer getTowerCurrLayer(String roleId) {
        return this.roleCurrLayerMap.get(roleId);
    }

    public TowerLayerConfig getTowerLayerConfig(short gateId, byte layerId) {
        return Util.fromMap(towerLayerConfigMap, gateId + "_" + layerId);
    }

    public short getMaxGate() {
        return this.towerAppConfig.getMaxOpenGate();
    }


    public byte getMaxLayer() {
        return MAX_LAYER;
    }

    private byte getTodayRemainResetNum(RoleInstance role){
    	int vipTimes = GameContext.getVipApp().getVipPrivilegeTimes(role.getRoleId(), VipPrivilegeType.TOWER_RESET_TIMES.getType(), "");
        int maxTimes = vipTimes + this.getTowerAppConfig().getDefaultResetClearNum();
        int useTimes = role.getRoleCount().getRoleTimesToByte(CountType.ToDayTowerResetNum);
        return  (byte)Math.max(maxTimes-useTimes,0);
    }

    public Message getTowerInfoMessage(RoleInstance role){
        return this.getTowerInfoMessage(role,(short)0);
    }

    private short getMaxShowGate(RoleInstance role,RoleTowerGate maxGate){
        if(null == maxGate){
            return 1 ;
        }
        short maxGateId = this.getMaxGate() ;
        if(maxGate.getGateId() >= maxGateId){
            return maxGateId ;
        }
        return maxGate.isPassed()?(short)(maxGate.getGateId() + 1) : maxGate.getGateId() ;
    }

    public Message getTowerInfoMessage(RoleInstance role,short selectGateId) {
        boolean isAutoGate = (selectGateId <=0) ;
        C2551_TowerInfoRespMessage respMsg = new C2551_TowerInfoRespMessage();
        respMsg.setSelectedGate(isAutoGate?(short)1:selectGateId);
        respMsg.setRankId(this.getTowerAppConfig().getRankId());
        List<TowerGateItem> towerGateItemList = Lists.newArrayList();
        RoleTowerInfo roleTowerInfo = this.getRoleTowerInfo(role.getRoleId());
        RoleTowerGate maxGate = roleTowerInfo.getMaxGate();
        if (null != maxGate) {
            respMsg.setMaxGate(maxGate.getGateId());
            respMsg.setMaxLayer(maxGate.getMaxLayer());
            if(isAutoGate){
                respMsg.setSelectedGate(maxGate.getGateId());
                short maxGateId = (short)(maxGate.getGateId() + 1) ;
                if(maxGate.isPassed() && this.getMaxGate() > maxGateId){
                    respMsg.setSelectedGate(maxGateId);
                }
            }
        }
        respMsg.setTodayRemainResetNum(this.getTodayRemainResetNum(role));
        short maxShowGateId = this.getMaxShowGate(role,maxGate);
        for (short gateId = 1; gateId <= maxShowGateId; gateId++) {
            TowerGateConfig gateConfig = this.getTowerGateConfig(gateId);
            if (null == gateConfig) {
                break;
            }
            TowerGateItem gateItem = new TowerGateItem();
            gateItem.setGate(gateId);
            gateItem.setGateName(gateConfig.getGateName());
            gateItem.setStarNumMax(MAX_GATE_STAR);
            //关状态
            RoleTowerGate gate = roleTowerInfo.getRoleTowerGate(gateId);
            gateItem.setGateStatus(this.getGateStatus(maxGate, gate, gateId).getType());
            //设置为不能领取
            gateItem.setAwardState((byte)0);
            if (null != gate) {
                gateItem.setStarNum(gate.totalStar());
                gateItem.setAwardState(this.haveCanRecvStarAward(gate)?(byte)1:(byte)0);
            }
            towerGateItemList.add(gateItem);
        }
        respMsg.setTowerGateItemList(towerGateItemList);
        //last raids cd
        respMsg.setRaidsRemainCd(this.getRaidsRemainCd(role));
        return respMsg;
    }

    private boolean haveCanRecvStarAward(RoleTowerGate gate){
        List<TowerStarAwardConfig> list = this.getTowerStarAwardConfig(gate.getGateId()) ;
        if(Util.isEmpty(list)){
            return false ;
        }
        int totalStar = gate.totalStar() ;
        for(TowerStarAwardConfig config : list){
            if(totalStar < config.getStar()){
                break ;
            }
            if(1 != gate.getStarAwardState(config.getStar())){
                return true ;
            }
        }
        return false ;
    }

    private short getRaidsRemainCd(RoleInstance role){
        RoleCount rc = role.getRoleCount();
        long lastTime = rc.getRoleTimesToLong(CountType.TowerRaidsLastTime);
        long now = SystemTimer.currentTimeMillis();
        if (lastTime > now) {
            lastTime = now;
        }
        long cd = this.getTowerAppConfig().getRaidsCd() - (now - lastTime) / 1000;
        return (short) Math.max(0, cd) ;
    }

    private TowerGateStatus getGateStatus(RoleTowerGate maxGate, RoleTowerGate gate,
                                          short gateId) {
        if (null == maxGate) {
            if (MIN_GATE_ID == gateId) {
                return TowerGateStatus.notPass;
            }
            return TowerGateStatus.closed;
        }
        if (gateId > maxGate.getGateId() + 1) {
            return TowerGateStatus.closed;
        }
        if (gateId == maxGate.getGateId() + 1) {
            if (maxGate.isPassed()) {
                return TowerGateStatus.notPass;
            }
            //没有通关
            return TowerGateStatus.closed;
        }
        if (null == gate) {
            return TowerGateStatus.notPass;
        }
        if (gate.isReseted()) {
            return gate.isResetAndAllPassed() ? TowerGateStatus.passed : TowerGateStatus.reseted;
        }
        if (gate.isPassed()) {
            return TowerGateStatus.passed;
        }
        return TowerGateStatus.notPass;
    }


    private TowerLayerStatus getLayerStatus(RoleTowerGate maxGate, RoleTowerGate gate,
                                            short gateId, byte layerId) {
        if (null == maxGate) {
            if (MIN_GATE_ID == gateId && MIN_LAYER == layerId) {
                //首层能进入
                return TowerLayerStatus.canEnter;
            }
            return TowerLayerStatus.closed;
        }
        if (null == gate) {
            if (gateId == maxGate.getGateId() + 1
                    && maxGate.isPassed()
                    && MIN_LAYER == layerId) {
                return TowerLayerStatus.canEnter;
            }
            return TowerLayerStatus.closed;
        }
        if (gate.isReseted()) {
            if (gate.isResetAndPassed(layerId)) {
                return TowerLayerStatus.passed;
            }
            return TowerLayerStatus.canEnter;
        }
        byte maxLayer = gate.getMaxLayer();
        if (layerId <= maxLayer) {
            return TowerLayerStatus.passed;
        }
        return (layerId == maxLayer + 1)?TowerLayerStatus.canEnter:TowerLayerStatus.closed;
    }

    @Override
    public Message getTowerGateInfoMessage(RoleInstance role, short gateId) {
        TowerGateConfig gateConfig = this.getTowerGateConfig(gateId);
        if (null == gateConfig) {
            return this.buildTipsMessage(TextId.ERROR_INPUT);
        }
        //每层的奖励
        TowerAwardItem layerAwardItem = new TowerAwardItem();
        layerAwardItem.setAttriList(gateConfig.getAttriAwards());
        layerAwardItem.setGoodsList(gateConfig.getGoodsAwards());

        //每层信息
        List<TowerLayerItem> layerItemList = Lists.newArrayList();
        RoleTowerInfo roleTowerInfo = this.getRoleTowerInfo(role.getRoleId());
        RoleTowerGate roleGate = roleTowerInfo.getRoleTowerGate(gateId);
        RoleTowerGate maxGate = roleTowerInfo.getMaxGate();
        for (byte layerId = 1; layerId <= this.getMaxLayer(); layerId++) {
            TowerLayerItem layerItem = new TowerLayerItem();
            layerItem.setLayer(layerId);
            //层状态： 0 未通关 ,1通过
            layerItem.setLayerStatus(this.getLayerStatus(maxGate, roleGate, gateId, layerId).getType());
            if (null != roleGate) {
                layerItem.setStarNum((byte) Math.max(0,roleGate.getLayerStar(layerId)));
            }
            layerItemList.add(layerItem);
        }

        C2552_TowerGateInfoRespMessage respMsg = new C2552_TowerGateInfoRespMessage();
        respMsg.setGate(gateId);
        //关的星奖励
        List<TowerStarAwardConfig> awardList = this.getTowerStarAwardConfig(gateId);
        if (!Util.isEmpty(awardList)) {
            List<TowerStarAwardItem> starAwardList = Lists.newArrayList();
            for (TowerStarAwardConfig award : awardList) {
                TowerStarAwardItem starAwardItem = new TowerStarAwardItem();
                starAwardItem.setStar(award.getStar());
                //领奖状态
                if (null != roleGate) {
                    starAwardItem.setStatus(roleGate.getStarAwardState(award.getStar()));
                }
                TowerAwardItem awardItem = new TowerAwardItem();
                awardItem.setGoodsList(award.getGoodsAwards());
                awardItem.setAttriList(award.getAttriAwards());
                starAwardItem.setAwardItem(awardItem);
                starAwardList.add(starAwardItem);
            }
            respMsg.setStarAwardList(starAwardList);
        }

        respMsg.setLayerItemList(layerItemList);
        respMsg.setLayerAwardItem(layerAwardItem);
        return respMsg;
    }

    @Override
    public Message joinTower(RoleInstance role, short gateId, byte layerId) {
        TowerGateConfig gateConfig = this.getTowerGateConfig(gateId);
        if (null == gateConfig) {
            return this.buildTipsMessage(TextId.ERROR_INPUT);
        }
        TowerLayerConfig layerConfig = this.getTowerLayerConfig(gateId, layerId);
        if (null == layerConfig) {
            return this.buildTipsMessage(TextId.ERROR_INPUT);
        }
        //判断是否可进入
        RoleTowerInfo roleTowerInfo = this.roleTowerGateMap.get(role.getRoleId());
        TowerLayerStatus status = this.getLayerStatus(roleTowerInfo.getMaxGate(),
                roleTowerInfo.getRoleTowerGate(gateId), gateId, layerId);
        if (status != TowerLayerStatus.canEnter) {
            return this.buildTipsMessage(TextId.TOWER_CANOT_ENTER_MAP_BY_STATUS);
        }
        TowerCurrLayer currLayer = new TowerCurrLayer();
        currLayer.setGateId(gateId);
        currLayer.setLayerId(layerId);
        this.roleCurrLayerMap.put(role.getRoleId(), currLayer);
        //必须是TowerPoint,否则如果下一关是同一地图时不会跳转
        Point targetPoint = new TowerPoint(gateConfig.getMapId(), gateConfig.getEnterX(), gateConfig.getEnterY());
        try {
            ChangeMapResult mapResult = GameContext.getUserMapApp().changeMap(role, targetPoint);
            if (!mapResult.isSuccess()) {
                return this.buildTipsMessageByContext(mapResult.getDesc());
            }
        } catch (Exception ex) {
            return this.buildTipsMessage(TextId.Sys_Error);
        }
        return null;
    }

    @Override
    public void towerPassed(RoleInstance role) {
        MapInstance mapInstance = role.getMapInstance();
        if (null == mapInstance || !(mapInstance instanceof MapTowerInstance)) {
            return;
        }
        C2558_TowerMapPassedRespMessage respMsg = new C2558_TowerMapPassedRespMessage();

        //获得星条件
        byte[] result = this.getTowerStarResult(role);
        int newTotalStar = 0;
        for (byte r : result) {
            if (0 == r) {
                continue;
            }
            newTotalStar++;
        }
        respMsg.setCondRestlt(result);

        MapTowerInstance towerInstance = (MapTowerInstance) mapInstance;
        short gateId = towerInstance.getGateId();
        byte layerId = towerInstance.getLayerId();

        //设置下一层
        if(layerId != MAX_LAYER){
            //最后一层不用设置
            int nextLayerId = layerId + 1;
            short nextGateId = gateId;
            if (nextLayerId > MAX_LAYER) {
                nextLayerId = 1;
                nextGateId++;
            }
            if (nextGateId > this.getMaxGate()) {
                //已经是最后一层
                nextGateId = 0;
                nextLayerId = 0;
            }
            respMsg.setNextGateId(nextGateId);
            respMsg.setNextLayerId((byte) nextLayerId);
        }

        respMsg.setOpenNewGate(this.isOpenNewGate(gateId,layerId)?(byte)1:(byte)0);

        RoleTowerInfo roleTowerInfo = this.getRoleTowerInfo(role.getRoleId());
        RoleTowerGate roleGate = roleTowerInfo.getRoleTowerGate(gateId);
        if (null == roleGate) {
            roleGate = new RoleTowerGate();
            roleGate.setRoleId(role.getRoleId());
            roleGate.setGateId(gateId);
            roleGate.updateLayerStar(layerId, newTotalStar);
            this.saveRoleTowerGate(roleGate);
        } else {
            boolean save = false ;
            if(newTotalStar > roleGate.getLayerStar(layerId)){
                //更新最高星
                roleGate.updateLayerStar(layerId, newTotalStar);
                save = true ;
            }
            if(roleGate.isReseted()){
                roleGate.updatePassedWhenReseted(layerId);
                save = true ;
            }
            if(save){
                this.saveRoleTowerGate(roleGate);
            }
        }

        roleTowerInfo.putRoleTowerGate(roleGate);

        TowerGateConfig gateConfig = this.getTowerGateConfig(gateId);
        if (null == gateConfig) {
            GameContext.getMessageCenter().sendSysMsg(role, respMsg);
            return;
        }
        TowerAwardItem awardItem = new TowerAwardItem();
        awardItem.setAttriList(gateConfig.multAttriAwards(1));
        awardItem.setGoodsList(gateConfig.multGoodsAwards(1));
        //to add attr and goods
        AddGoodsBeanResult goodsResult = GameContext.getUserGoodsApp().addSomeGoodsBeanForBag(role,
                gateConfig.getGoodsOperateList(), OutputConsumeType.tower_pass_award);
        //背包满了发邮件
        List<GoodsOperateBean> putFailureList = goodsResult.getPutFailureList();
        try {
            if (!Util.isEmpty(putFailureList)) {
                GameContext.getMailApp().sendMail(role.getRoleId(),
                        MailSendRoleType.System.getName(), "",
                        MailSendRoleType.System.getName(),
                        OutputConsumeType.tower_pass_award.getType(),
                        putFailureList);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        for (AttriTypeValueItem attri : awardItem.getAttriList()) {
            GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.get(attri.getAttriType()),
                    OperatorType.Add, attri.getAttriValue(), OutputConsumeType.tower_pass_award);
        }
        role.getBehavior().notifyAttribute();

        respMsg.setAwardItem(awardItem);
        GameContext.getMessageCenter().sendSysMsg(role, respMsg);
    }

    private boolean isOpenNewGate(short gateId,byte layerId){
        if(MAX_LAYER != layerId || gateId >= this.getMaxGate()){
            return false ;
        }
        return true ;
    }


    @Override
    public Message raidsTower(RoleInstance role, short gateId) {
        //cd
        int cd = this.getRaidsRemainCd(role) ;
        if (cd > 0) {
            return this.buildTipsMessage(TextId.TOWER_RAIDS_IN_CD);
        }
        //this gate passed
        RoleTowerInfo roleTowerInfo = this.getRoleTowerInfo(role.getRoleId());
        RoleTowerGate roleGate = roleTowerInfo.getRoleTowerGate(gateId);
        if (null == roleGate || !roleGate.isPassed() || !roleGate.isReseted()) {
            return this.buildTipsMessage(TextId.TOWER_CANOT_RAIDS_BY_NOT_PASS_OR_RESET);
        }
        if (roleGate.isResetAndAllPassed()) {
            roleGate.setResetStatus(NOT_RESET_STATUS);
            this.saveRoleTowerGate(roleGate);
            return this.buildTipsMessage(TextId.TOWER_CANOT_RAIDS_BY_NOT_PASS_OR_RESET);
        }
        //go to raids
        TowerGateConfig gateConfig = this.getTowerGateConfig(gateId);
        if (null == gateConfig) {
            return this.buildTipsMessage(TextId.ERROR_DATA);
        }
        int mult = 0;
        for (byte layerId = MIN_LAYER; layerId <= MAX_LAYER; layerId++) {
            if (1 == RoleTowerGate.getIndexValue(roleGate.getResetStatus(), layerId)) {
                continue;
            }
            mult++;
        }
        List<GoodsOperateBean> goodsList = gateConfig.multGoodsOperateList(mult);
        if (!GameContext.getUserGoodsApp().canPutGoodsBean(role, goodsList)) {
            return this.buildTipsMessage(TextId.Bag_Is_Full);
        }

        TowerAwardItem awardItem = new TowerAwardItem();
        awardItem.setAttriList(gateConfig.multAttriAwards(mult));
        awardItem.setGoodsList(gateConfig.multGoodsAwards(mult));
        //set last raids
        role.getRoleCount().changeTimes(CountType.TowerRaidsLastTime, SystemTimer.currentTimeMillis());
        roleGate.setResetStatus(NOT_RESET_STATUS);
        this.saveRoleTowerGate(roleGate);
        //to add attr and goods
        GameContext.getUserGoodsApp().addGoodsBeanForBag(role, goodsList, OutputConsumeType.tower_raids_award);
        for (AttriTypeValueItem attri : awardItem.getAttriList()) {
            GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.get(attri.getAttriType()),
                    OperatorType.Add, attri.getAttriValue(), OutputConsumeType.tower_raids_award);
        }
        role.getBehavior().notifyAttribute();

        C2553_TowerRaidsRespMessage respMsg = new C2553_TowerRaidsRespMessage();
        respMsg.setType(RespTypeStatus.SUCCESS);
        respMsg.setGateId(gateId);
        respMsg.setAwardItem(awardItem);
        respMsg.setRaidsRemainCd(this.getRaidsRemainCd(role));
        return respMsg;
    }

    @Override
    public Message recvAward(RoleInstance role, short gateId, byte star) {
        RoleTowerInfo roleTowerInfo = this.getRoleTowerInfo(role.getRoleId());
        RoleTowerGate roleGate = roleTowerInfo.getRoleTowerGate(gateId);
        if (null == roleGate || roleGate.totalStar() < star) {
            return this.buildTipsMessage(TextId.TOWER_CANOT_RECV_BY_LITTLE_STAR);
        }
        //判断是否已经领取过奖励
        if (1 == roleGate.getStarAwardState(star)) {
            //已经领取
            return this.buildTipsMessage(TextId.TOWER_CANOT_RECV_BY_HAD_RECV);
        }
        TowerStarAwardConfig awardConfig = this.getTowerStarAwardConfig(gateId, star);
        if (null == awardConfig) {
            return this.buildTipsMessage(TextId.ERROR_INPUT);
        }
        List<GoodsOperateBean> goodsList = awardConfig.getGoodsOperateList();
        if (!GameContext.getUserGoodsApp().canPutGoodsBean(role, goodsList)) {
            return this.buildTipsMessage(TextId.Bag_Is_Full);
        }
        //设置为领取奖励
        roleGate.updateStarAwardState(star);
        this.saveRoleTowerGate(roleGate);
        //to add attr and goods
        GameContext.getUserGoodsApp().addGoodsBeanForBag(role, goodsList, OutputConsumeType.tower_star_award);
        for (AttriTypeValueItem attri : awardConfig.getAttriAwards()) {
            GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.get(attri.getAttriType()),
                    OperatorType.Add, attri.getAttriValue(), OutputConsumeType.tower_star_award);
        }
        role.getBehavior().notifyAttribute();
        C2557_TowerAwardGetRespMessage respMsg = new C2557_TowerAwardGetRespMessage();
        respMsg.setType(RespTypeStatus.SUCCESS);
        respMsg.setGateId(gateId);
        respMsg.setStar(star);
        return respMsg;
    }

    @Override
    public Message resetTower(RoleInstance role, short gateId) {
        int remainTime = this.getTodayRemainResetNum(role);
        if (remainTime <=0 ) {
            return this.buildTipsMessage(TextId.TOWER_CANOT_RESET_BY_NOT_TIMES_TODAY);
        }
        RoleTowerInfo roleTowerInfo = this.getRoleTowerInfo(role.getRoleId());
        RoleTowerGate roleGate = roleTowerInfo.getRoleTowerGate(gateId);
        if (null == roleGate || !roleGate.isPassed()) {
            return this.buildTipsMessage(TextId.TOWER_CANOT_RESET_BY_NOT_PASS);
        }
        if (roleGate.isReseted() && !roleGate.isResetAndAllPassed()) {
            return this.buildTipsMessage(TextId.TOWER_CANOT_RESET_BY_RESETED);
        }
        //reset time + 1
        role.getRoleCount().changeTimes(CountType.ToDayTowerResetNum);
        //set to reset status
        roleGate.setResetStatus(RESET_STATUS);
        this.saveRoleTowerGate(roleGate);
        C2554_TowerResetRespMessage respMsg = new C2554_TowerResetRespMessage();
        respMsg.setType(RespTypeStatus.SUCCESS);
        respMsg.setGateId(gateId);
        respMsg.setTodayRemainResetNum(this.getTodayRemainResetNum(role));
        return respMsg;
    }

    private void saveRoleTowerGate(RoleTowerGate gate) {
        if (null == gate) {
            return;
        }
        GameContext.getBaseDAO().saveOrUpdate(gate);
    }

    private Message buildTipsMessage(String textId) {
        C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage();
        msg.setMsgContext(GameContext.getI18n().getText(textId));
        return msg;
    }

    private Message buildTipsMessageByContext(String context) {
        C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage();
        msg.setMsgContext(context);
        return msg;
    }

    @Override
    public RoleTowerInfo getRoleTowerInfo(String roleId) {
        return Util.fromMap(this.roleTowerGateMap, roleId);
    }


    private TowerGateConfig getTowerGateConfig(short gateId) {
        return Util.fromMap(this.towerGateConfigMap, gateId);
    }

    private List<TowerStarAwardConfig> getTowerStarAwardConfig(short gateId) {
        return Util.fromMap(this.towerStarAwardConfigMap, gateId);
    }

    private TowerStarAwardConfig getTowerStarAwardConfig(short gateId, byte star) {
        List<TowerStarAwardConfig> awardList = this.getTowerStarAwardConfig(gateId);
        if (Util.isEmpty(awardList)) {
            return null;
        }
        for (TowerStarAwardConfig config : awardList) {
            if (config.getStar() == star) {
                return config;
            }
        }
        return null;
    }
}
