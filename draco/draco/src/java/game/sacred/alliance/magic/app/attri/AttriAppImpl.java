package sacred.alliance.magic.app.attri;

import java.util.Map;
import java.util.Map.Entry;

import sacred.alliance.magic.app.attri.config.AttriBattleScore;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.data.RoleLevelupLoader;
import sacred.alliance.magic.domain.RoleLevelup;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
public class AttriAppImpl implements AttriApp{
	private RoleLevelupLoader roleLevelupLoader;
	private Map<Byte, AttriBattleScore> battleScoreMap = null;

	@Override
	public void setArgs(Object args) {
		
	}
	
	@Override
	public int getEffectBattleScore(RoleInstance role) {
		int score = role.getBattleScore();
		if(!GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId())){
			return score ;
		}
		
		role.syncBattleScore();
		return role.getBattleScore() ;
	}
	

	@Override
	public void start() {
		roleLevelupLoader.load();
		Map<String,RoleLevelup> values = roleLevelupLoader.getDataMap();
		for(int lv=2;lv<=values.size();lv++ ){
			RoleLevelup current = values.get(String.valueOf(lv));
			if(null == current){
				continue ;
			}
			RoleLevelup pre = values.get(String.valueOf(lv-1));
			if(null == pre){
				continue ;
			}
			//与前一级有变动
			boolean changed = (current.getBaseMaxHp() != pre.getBaseMaxHp() || current.getBaseMaxMp() != pre.getBaseMaxMp());
			current.setBaseMaxChanged(changed);
		}
		
		this.loadBattleScore();
	}
	

	@Override
	public void stop() {
		
	}

	

	@Override
	public RoleLevelup getLevelup(int level) {
		return this.roleLevelupLoader.getDataMap().get(String.valueOf(level));
	}
	
	

	public void setRoleLevelupLoader(RoleLevelupLoader roleLevelupLoader) {
		this.roleLevelupLoader = roleLevelupLoader;
	}
	
	/**
	 * 加载属性战斗力配置
	 */
	private void loadBattleScore() {
		String fileName = "";
		String sheetName = "";
		try {			
			fileName = XlsSheetNameType.attri_battle_score.getXlsName();
			sheetName = XlsSheetNameType.attri_battle_score.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			battleScoreMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, AttriBattleScore.class);
			if(Util.isEmpty(battleScoreMap)) {
				Log4jManager.CHECK.error("AttriApp loadExel sourceFile = " + fileName + " sheetName =" + sheetName + ", not config");
				Log4jManager.checkFail();
				return ;
			}
			for(Entry<Byte, AttriBattleScore> entry : battleScoreMap.entrySet()) {
				AttriBattleScore abs = entry.getValue();
				if(null == abs) {
					continue;
				}
				abs.init();
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName, e);
			Log4jManager.checkFail();
		}
	}

	@Override
	public int getAttriBattleScore(AttriBuffer attriBuffer) {
		if(null == attriBuffer) {
			return 0;
		}
		Map<Byte, AttriItem> map = attriBuffer.getMap();
		if(Util.isEmpty(map)) {
			return 0;
		}
		float totalScore = 0;
		for(Entry<Byte, AttriItem> entry : map.entrySet()) {
			AttriItem item = entry.getValue();
			if(null == item) {
				continue;
			}
			totalScore += this.getBattleScoreFactor(entry.getKey()) * item.getValue();
		}
		return (int)(totalScore);
	}

	private float getBattleScoreFactor(byte attriType) {
		AttriBattleScore battleScore = battleScoreMap.get(attriType);
		if(null == battleScore) {
			return 0;
		}
		return battleScore.getFactor();
	}

	@Override
	public Map<Byte, AttriBattleScore> getBattleScoreMap() {
		return battleScoreMap;
	}
}
