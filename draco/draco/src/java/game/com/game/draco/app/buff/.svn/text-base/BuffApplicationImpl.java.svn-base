package com.game.draco.app.buff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.config.PathConfig;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.component.script.ScriptSupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;

import com.game.draco.GameContext;
import com.google.common.collect.Maps;

public class BuffApplicationImpl extends BuffApplication {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private PathConfig pathConfig;
	private ScriptSupport scriptSupport;
	private Map<Short,Map<Integer,BuffDetail>>  buffDetailMap = Maps.newHashMap();

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
		this.initBuffDetail();
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
			Map<Integer,BuffDetail> details = this.buffDetailMap.remove(buffId);
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
		if(!this.buffDetailMap.isEmpty()){
			for(short buffId : this.buffDetailMap.keySet()){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("initSkill error : ----buffId:" + buffId + "----The buff does not exist!");
			}
		}
		this.buffDetailMap.clear();
	}
	
	
	/**
	 * 载入buff配置表
	 */
	private void initBuffDetail() {
		//技能buff加载
		String fileName = XlsSheetNameType.buff.getXlsName();
		String sheetName = XlsSheetNameType.buff.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		List<BuffDetail> buffDetailList = XlsPojoUtil.sheetToList(sourceFile, sheetName, BuffDetail.class);
		for(BuffDetail detail : buffDetailList){
			this.init(detail);
		}
		
		//消耗品buff加载
		String consumeFileName = XlsSheetNameType.goods_consume_buff.getXlsName();
		String consumeSheetName = XlsSheetNameType.goods_consume_buff.getSheetName();
		sourceFile = GameContext.getPathConfig().getXlsPath() + consumeFileName;
		List<GoodsBuffDetail> consumeBuffList = XlsPojoUtil.sheetToList(sourceFile, consumeSheetName, GoodsBuffDetail.class);
		for(GoodsBuffDetail consumeDetail : consumeBuffList){
			this.init(consumeDetail);
		}
		
		//加载经验转轴
		String expFileName = XlsSheetNameType.goods_exp_buff.getXlsName();
		String expSheetName = XlsSheetNameType.goods_exp_buff.getSheetName();
		sourceFile = GameContext.getPathConfig().getXlsPath() + expFileName;
		List<RoleExpBuffDetail> expBuffList = XlsPojoUtil.sheetToList(sourceFile, expSheetName, RoleExpBuffDetail.class);
		for(RoleExpBuffDetail detail : expBuffList){
			this.init(detail);
		}
		
		//在线挂机buff加载
		/*String onlineHangupFileName = XlsSheetNameType.onlineHangup_buff.getXlsName();
		String onlineHangupSheetName = XlsSheetNameType.onlineHangup_buff.getSheetName();
		sourceFile = GameContext.getPathConfig().getXlsPath() + onlineHangupFileName;
		List<RoleSpaBuffDetail> onlineHangupBuffList = XlsPojoUtil.sheetToList(sourceFile, onlineHangupSheetName, RoleSpaBuffDetail.class);
		for(RoleSpaBuffDetail detail : onlineHangupBuffList){
			this.init(detail);
		}*/
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

}
