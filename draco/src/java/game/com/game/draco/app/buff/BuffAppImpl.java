package com.game.draco.app.buff;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.python.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.config.PathConfig;
import sacred.alliance.magic.base.StateType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.component.script.ScriptSupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;

import com.game.draco.GameContext;
import com.game.draco.app.buff.config.BuffBase;
import com.game.draco.app.buff.config.BuffHurt;
import com.game.draco.app.buff.config.BuffHurtC;
import com.game.draco.app.buff.config.BuffShout;
import com.game.draco.app.buff.domain.BHurtC;
import com.game.draco.app.skill.vo.SkillFormula;
import com.google.common.collect.Maps;

public class BuffAppImpl extends BuffApp {
	
	private static final float TEN_THOUSAND_F = SkillFormula.TEN_THOUSAND;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private PathConfig pathConfig;
	private ScriptSupport scriptSupport;
	private Map<Short,Map<Integer,BuffDetail>>  buffDetailMap = Maps.newHashMap();
	private Map<Short,BuffBase> buffBaseMap = Maps.newHashMap();
	private Map<Short,List<BuffHurt>> buffHurtMap = Maps.newHashMap();
	private Map<Integer,Map<Byte,BuffHurtC>> buffHurtCMap = Maps.newHashMap();
	private Map<Short,BuffShout> buffShoutMap = Maps.newHashMap();

	public Buff getBuff(short buff) {
		return buffMap.get(buff);
	}

	public void setScriptSupport(ScriptSupport scriptSupport) {
		this.scriptSupport = scriptSupport;
	}

	public void setPathConfig(PathConfig pathConfig) {
		this.pathConfig = pathConfig;
	}
	
	private void load(){
		//加载buffDetail
		initBuffDetail();
		scriptSupport.loadScript(pathConfig.getBuffPath());
		this.build();
	}
	
	@Override
	public void start() {
		this.load();
	}
	
	@Override
	public boolean reLoad() {
		if(GameContext.isOfficialServer()){
			//正式服务器不运行此操作,此操作只是便于调试加载
			return false;
		}
		try {
			logger.info("reload buff start");
			this.load();
			logger.info("reload buff end");
			return true ;
		}catch(Exception ex){
			logger.error("reload buff error",ex);
		}
		return false ;
	}
	
	private void build(){
		for(Buff buff : buffMap.values()){
			if(null == buff){
				continue;
			}
			
			short buffId = buff.getBuffId();
			if(buffId <= 0){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("initBuff buff error : buffId=" + buffId + ",the buffId is error!");
				continue;
			}
			Map<Integer,BuffDetail> details = buffDetailMap.get(buffId);
			if(Util.isEmpty(details)){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("initBuff error : ----buffId:" + buffId + "----The buff is not configured parameters!");
				continue ;
			}
			//检测属性类型的buff是否都配置了间隔时间
			((BuffAdaptor)buff).putBuffDetail(details);
			/*if(EffectType.attribute == buff.getEffectType() 
					&& BuffTimeType.continued == buff.getTimeType()){
				for(BuffDetail detail : details.values()){
					if(detail.getIntervalTime() >0){
						continue ;
					}
					Log4jManager.checkFail();
					Log4jManager.CHECK.error("initBuff error : ----buffId:" + buff.getBuffId() + "----The buff is not configured the interval time!");
				}
			}*/
		}
	}
	
	
	/**
	 * 载入buff配置表
	 */
	private void initBuffDetail() {
		
		loadBuffBase();
		loadBuffHurt();
		loadBuffHurtC();
		loadBuffShout();
		
		Buff buff = null;
		for(Entry<Short,BuffBase> buffBase : buffBaseMap.entrySet()){
			buff = new RoleBuffReform(buffBase.getKey());
			buff.setBuffName(buffBase.getValue().getName());
			if(!Util.isEmpty(buffBase.getValue().getDiscolor())) {
				int discolor = (int)Long.parseLong(buffBase.getValue().getDiscolor(), 16);
				buff.setDiscolor(discolor);
			}
			buff.setStack(buffBase.getValue().isStack());
			buff.setMaxLayer(buffBase.getValue().getMaxLayer());
			buff.setTransLost(buffBase.getValue().isTransLost());
			buff.setExitInsLost(buffBase.getValue().isExitInsLost());
			buff.setSwitchOn(buffBase.getValue().isSwitchOn());
			buff.setBeingType(buffBase.getValue().getBeingType());
			buff.setCategoryType(BuffCategoryType.get(buffBase.getValue().getCategoryType()));
			buff.setDieLost(buffBase.getValue().isDieLost());
			buff.setRandom(buffBase.getValue().isRandom());
			buff.setBlowfly(buffBase.getValue().isBlowfly());
			buff.setEffectId(buffBase.getValue().getEffectId());
			buff.setGroupId(buffBase.getValue().getGroupId());
			buff.setHatredAdd(buffBase.getValue().getHatredAdd());
			buff.setHatredPercent(buffBase.getValue().getHatredPercent());
			buff.setHurtType(buffBase.getValue().getHurtType());
			buff.setIconId(buffBase.getValue().getIconId());
			buff.setIntervalTime(buffBase.getValue().getIntervalTime());
			buff.setNotReplaceDesc(buffBase.getValue().getNotReplaceDesc());
			buff.setOfflineLost(buffBase.getValue().isOfflineLost());
			buff.setOfflineTiming(buffBase.getValue().isOfflineTiming());
			buff.setPersistTime(buffBase.getValue().getPersistTime());
			buff.setReplaceType(buffBase.getValue().getReplaceType());
			buff.setSwitchOn(buffBase.getValue().isSwitchOn());
			buff.setTimeType(BuffTimeType.get(buffBase.getValue().getTimeType()));
			buff.setTransNoClean(buffBase.getValue().isTransNoClean());
			buff.setZoom(buffBase.getValue().getZoom());
			buff.setStateType(buffBase.getValue().getStateType());
			buff.setEffectType(EffectType.get(buffBase.getValue().getEffectType()));
			buff.setSkillContinue(buffBase.getValue().getSkillContinue());
			
			if(buffShoutMap.containsKey(buffBase.getKey())){
				String shout = buffShoutMap.get(buffBase.getKey()).getMsg();
				buff.setShout(shout);
			}
			buffMap.put(buffBase.getKey(), buff);
			
			for(int i = 1; i<=buffBase.getValue().getMaxLevel(); i++){
				BuffDefaultDetail buffDetail = new BuffDefaultDetail();
				buffDetail.setDesc(buffBase.getValue().getDes());
				buffDetail.setLevel(i);
				buffDetail.setBuffId(buffBase.getValue().getBuffId());
				if (buffHurtMap.containsKey(buffBase.getKey())) {
					List<BuffHurt> buffHurtList = buffHurtMap.get(buffBase.getKey());
					if (buffHurtCMap.containsKey(i)) {
						List<BHurtC> bHurtList = Lists.newArrayList();
						Map<Byte,BuffHurtC> hurtCMap = buffHurtCMap.get(i);
						String damage = "{damage";
						for(int bi=0;bi<buffHurtList.size();bi++){
							String des = buffDetail.getDesc();
							BuffHurt buffHurt = buffHurtList.get(bi);
							BuffHurtC hurtC = hurtCMap.get(buffHurt.getAttrType());
							
							BHurtC bHurtC = new BHurtC();
							bHurtC.setA(buffHurt.getA());
							bHurtC.setAttrType(buffHurt.getAttrType());
							bHurtC.setB(buffHurt.getB());
							bHurtC.setD(buffHurt.getD());
							bHurtC.setC(hurtC.getC());
							bHurtC.setHurtType(buffHurt.getHurtType());
							bHurtC.setModifyTargetAttr(buffHurt.getModifyTargetAttr());
							bHurtC.setModifyTargetType(buffHurt.getModifyTargetType());
							bHurtC.setReduce(buffHurt.getReduce());
							bHurtC.setTargetType(buffHurt.getTargetType());
							bHurtC.setDamage(buffHurt.getDamage());
							bHurtList.add(bHurtC);
							String hurtDes = buffHurt.getDes();
							hurtDes = Util.replaceDes(0, buffHurt.getA(),
									buffHurt.getB(), hurtC.getC(),
									buffHurt.getD(), hurtDes);
							buffDetail.setDesc(des.replace(damage + bi + "}", hurtDes));
						}
						buffDetail.setBHurtList(bHurtList);
					}
				}
				
				Map<Integer,BuffDetail> map = null;
				if(buffDetailMap.containsKey(buffBase.getKey())){
					map = buffDetailMap.get(buffBase.getKey());
					map.put(i, buffDetail);
				}else{
					map = Maps.newHashMap();
					map.put(i, buffDetail);
					buffDetailMap.put(buffBase.getKey(), map);
				}
			}
		}
		
		//消耗品buff加载
		String consumeFileName = XlsSheetNameType.goods_consume_buff.getXlsName();
		String consumeSheetName = XlsSheetNameType.goods_consume_buff.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + consumeFileName;
		List<GoodsBuffDetail> consumeBuffList = XlsPojoUtil.sheetToList(sourceFile, consumeSheetName, GoodsBuffDetail.class);
		for(GoodsBuffDetail consumeDetail : consumeBuffList){
            this.checkDuplicate(XlsSheetNameType.goods_consume_buff,consumeDetail.getBuffId(),GoodsBuff.class);
			this.init(consumeDetail);
		}
		
		//加载经验转轴
		String expFileName = XlsSheetNameType.goods_exp_buff.getXlsName();
		String expSheetName = XlsSheetNameType.goods_exp_buff.getSheetName();
		sourceFile = GameContext.getPathConfig().getXlsPath() + expFileName;
		List<RoleExpBuffDetail> expBuffList = XlsPojoUtil.sheetToList(sourceFile, expSheetName, RoleExpBuffDetail.class);
		for(RoleExpBuffDetail detail : expBuffList){
            this.checkDuplicate(XlsSheetNameType.goods_consume_buff,detail.getBuffId(),RoleExpBuff.class);
			this.init(detail);
		}
	}

    private void checkDuplicate(XlsSheetNameType xls,short buffId,Class clazz){
        Buff buff = this.getBuff(buffId) ;
        if(null == buff || buff.getClass().getName().equals(clazz.getName())){
            return ;
        }
        Log4jManager.checkFail();
        Log4jManager.CHECK.error("load buff xls:{} sheet:{} error,buffId={},the buff duplicate",
                xls.getXlsName(),xls.getSheetName(),buffId);
    }

	private void init(BuffDetail detail){
		if(null == detail){
			return;
		}
		short buffId = detail.getBuffId();
		if(buffId <= 0){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("load buff xls error : buffId=" + buffId + ",the buffId is error!");
			return;
		}
		detail.check();
		//初始化buff属性
		detail.init();
		if (!buffDetailMap.containsKey(buffId)) {
			buffDetailMap.put(buffId, new HashMap<Integer, BuffDetail>());
			//registerBuff已经判断buff=null,buff已经存在的情况
			registerBuff(detail.newBuff());
		}
		buffDetailMap.get(buffId).put(detail.getLevel(), detail);
	}

	@Override
	public void stop() {

	}
	


	@Override
	public void setArgs(Object args) {
		
	}
	
	private void loadBuffBase(){
		//技能buff加载
		String fileName = XlsSheetNameType.buff_reform_base.getXlsName();
		String sheetName = XlsSheetNameType.buff_reform_base.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		buffBaseMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, BuffBase.class);
		if(Util.isEmpty(buffBaseMap)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("load buff xls error : fileName=" + fileName + ",sheetName=" + sheetName +",the buffBaseMap is null!");
		}
	}
	
	private void loadBuffShout(){
		//技能buff加载
		String fileName = XlsSheetNameType.buff_reform_shout.getXlsName();
		String sheetName = XlsSheetNameType.buff_reform_shout.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		buffShoutMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, BuffShout.class);
		if(Util.isEmpty(buffShoutMap)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("load buff xls error : fileName=" + fileName + ",sheetName=" + sheetName +",the buffShoutMap is null!");
		}
	}
	
	private void loadBuffHurt(){
		//技能buff加载
		String fileName = XlsSheetNameType.buff_reform_hurt.getXlsName();
		String sheetName = XlsSheetNameType.buff_reform_hurt.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		List<BuffHurt> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, BuffHurt.class);
		if(Util.isEmpty(list)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("load buff xls error : fileName=" + fileName + ",sheetName=" + sheetName +",the buffHurtMap is null!");
			return;
		}
		
		List<BuffHurt> buffHurtList = null;
		for(BuffHurt buffHurt : list){
			if(buffHurtMap.containsKey(buffHurt.getBuffId())){
				buffHurtList = buffHurtMap.get(buffHurt.getBuffId());
				buffHurtList.add(buffHurt);
			}else{
				buffHurtList = Lists.newArrayList();
				buffHurtList.add(buffHurt);
				buffHurtMap.put(buffHurt.getBuffId(), buffHurtList);
			}
		}
		
	}
	
	
	
	private void loadBuffHurtC(){
		//技能buff加载
		String fileName = XlsSheetNameType.buff_reform_hurt_c.getXlsName();
		String sheetName = XlsSheetNameType.buff_reform_hurt_c.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		List<BuffHurtC> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, BuffHurtC.class);
		if(Util.isEmpty(list)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("load buff xls error : fileName=" + fileName + ",sheetName=" + sheetName +",the buffHurtCMap is null!");
			return;
		}
		
		for(BuffHurtC buffHurt : list){
			if(buffHurtCMap.containsKey(buffHurt.getBuffLevel())){
				buffHurtCMap.get(buffHurt.getBuffLevel()).put(buffHurt.getAttrType(), buffHurt);
			}else{
				Map<Byte,BuffHurtC> hurtCMap = Maps.newHashMap();
				hurtCMap.put(buffHurt.getAttrType(), buffHurt);
				buffHurtCMap.put(buffHurt.getBuffLevel(),hurtCMap);
			}
		}
	}

	@Override
	public BuffDetail getBuffDetail(short buffId,int buffLevel) {
		if(buffDetailMap.containsKey(buffId)){
			return buffDetailMap.get(buffId).get(buffLevel);
		}
		return null;
	}

}
