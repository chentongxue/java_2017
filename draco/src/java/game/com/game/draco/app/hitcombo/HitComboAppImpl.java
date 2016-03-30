package com.game.draco.app.hitcombo;

import com.game.draco.GameContext;
import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.message.item.HitComboConfigItem;
import com.game.draco.message.response.C0324_HitComboConfigRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HitComboAppImpl implements HitComboApp {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private HitComboConfig hitComboConfig ;
	private Map<Integer, HitComboBuffConfig> hitComboBuffMap = new HashMap<Integer, HitComboBuffConfig>();
	private Set<Short> buffSet = Sets.newHashSet() ;

	@Override
	public void start() {
		initConfig();
	}
	
	private void initConfig(){
		String fileName = XlsSheetNameType.hit_combo.getXlsName();
		String sheetName = XlsSheetNameType.hit_combo.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			hitComboConfig = XlsPojoUtil.getEntity(sourceFile, sheetName, HitComboConfig.class);
			if(null == hitComboConfig) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
			}
			fileName = XlsSheetNameType.hit_combo_buff.getXlsName();
			sheetName = XlsSheetNameType.hit_combo_buff.getSheetName();
			hitComboBuffMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, HitComboBuffConfig.class);
			buffSet.clear();
			if(Util.isEmpty(hitComboBuffMap)){
				return ;
			}
			for(HitComboBuffConfig config : hitComboBuffMap.values()){
				buffSet.add(config.getBuffId());
			}
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
	}

	@Override
	public void stop() {

	}

	@Override
	public void setArgs(Object arg0) {

	}
	
	@Override
	public void pushHitComboConfig(RoleInstance role){
		C0324_HitComboConfigRespMessage respMsg = new C0324_HitComboConfigRespMessage() ;
		respMsg.setComboCd((short)this.hitComboConfig.getClearTime());
		if(!Util.isEmpty(hitComboBuffMap)){
			List<HitComboConfigItem> musicList = Lists.newArrayList() ;
			for(HitComboBuffConfig config : this.hitComboBuffMap.values()){
				if(0 == config.getMusicId() ){
					continue ;
				}
				HitComboConfigItem item = new HitComboConfigItem();
				item.setHitCombo((short)config.getHitCombo());
				item.setMusicId(config.getMusicId());
				musicList.add(item);
			}
			respMsg.setMusicList(musicList);
		}
		role.getBehavior().sendMessage(respMsg);
	}

	@Override
	public void clearHitCombo(RoleInstance role) {
		if(null == role){
			return ;
		}
		try{
			long lastUseSkillTime = role.getLastUseSkillTime() ;
			if(System.currentTimeMillis() - lastUseSkillTime > this.hitComboConfig.getClearTime()){
				//清除buff
				role.getHitCombo().set(0);
				for(short buffId : buffSet){
					GameContext.getUserBuffApp().delBuffStat(role, buffId, false);
				}
			}
		}catch(Exception e){
			logger.error("HitComboApp.clearHitCombo error",e);
		}
	}
	
	@Override
	public void addHitCombo(RoleInstance role){
		if(null == role){
			return ;
		}
		try{
			int max = this.hitComboConfig.getMaxCombo() ;
			int hit = role.getHitCombo().incrementAndGet();
			boolean gt = hit > max ;
			if(gt){
				role.getHitCombo().set(max);
			}
			
			hit = role.getHitCombo().get();
			HitComboBuffConfig config = this.hitComboBuffMap.get(hit);
			if(null == config){
				return;
			}
			if(gt){
				//已经存在此buff,并且持续时间>30s 不再添加
				BuffStat stat = role.getBuffStat(config.getBuffId());
				if(null != stat && stat.getRemainTime() <= 30000){
					return ;
				}
			}
			GameContext.getUserBuffApp().addBuffStat(role, role, config.getBuffId(), config.getBuffLevel());
		}catch(Exception e){
			logger.error("HitComboApp.clearHitCombo error",e);
		}
	}
}
