package sacred.alliance.magic.app.role;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleBorn;
import sacred.alliance.magic.vo.RoleBornGuide;
import sacred.alliance.magic.vo.RoleBornHero;

import com.game.draco.GameContext;
import com.game.draco.message.item.UserLoginHeroShowItem;
import com.google.common.collect.Lists;

public class RoleBornAppImpl implements RoleBornApp{
	
	private RoleBorn roleBorn ;
	private RoleBornGuide roleBornGuide ;
	private Map<Integer,RoleBornHero> roleBornHeroMap ; 
	
	
	@Override
	public Map<Integer,RoleBornHero> getRoleBornHeroMap() {
		return this.roleBornHeroMap ;
	}

	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadRoleBornData();
		this.loadRoleBornHero();
		this.loadRoleBornGuide();
	}

	@Override
	public void stop() {
		
	}
	
	private void loadRoleBornHero(){
		String fileName = XlsSheetNameType.role_born_hero.getXlsName();
		String sheetName = XlsSheetNameType.role_born_hero.getSheetName();
		String errorMsg = "loadRoleBornHero error : sourceFile = "+fileName +" sheetName =" + sheetName;
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath()
					+ fileName;
			Map<Integer,RoleBornHero> heroMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName,
					RoleBornHero.class);
			if (Util.isEmpty(heroMap)) {
				Log4jManager.CHECK
						.error(errorMsg + " not config roleBornHero data");
				Log4jManager.checkFail();
				return;
			}
			for(RoleBornHero hero : heroMap.values()){
				if (null == GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class,
						hero.getHeroId())) {
					Log4jManager.CHECK.error("RoleBorn config error,heroId=" + hero.getHeroId() + " not exist!");
					Log4jManager.checkFail();
				}
			}
			this.roleBornHeroMap = heroMap ;
		} catch (Exception e) {
			Log4jManager.CHECK.error(errorMsg , e);
			Log4jManager.checkFail();
		}
	}
	
	private void loadRoleBornGuide(){
		String fileName = XlsSheetNameType.role_born_guide.getXlsName();
		String sheetName = XlsSheetNameType.role_born_guide.getSheetName();
		String errorMsg = "loadRoleBornData error : sourceFile = "+fileName +" sheetName =" + sheetName;
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath()
					+ fileName;
			this.roleBornGuide = XlsPojoUtil.getEntity(sourceFile, sheetName,
					RoleBornGuide.class);
			if (null == roleBornGuide) {
				//允许不配置出生向导地图
				return;
			}
			// 判断设置地图
			MapConfig mapConfig = GameContext.getMapApp().getMapConfig(
					this.roleBornGuide.getMapId());
			if (null == mapConfig) {
				Log4jManager.CHECK.error(errorMsg + " map not exist,mapId="
						+ this.roleBornGuide.getMapId());
				Log4jManager.checkFail();
				return;
			}
			if(!mapConfig.changeLogicType(MapLogicType.roleBornGuide)){
				Log4jManager.CHECK.error(errorMsg + " map logicType error,mapId="
						+ this.roleBornGuide.getMapId());
				Log4jManager.checkFail();
				return;
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error(errorMsg , e);
			Log4jManager.checkFail();
		}
	
	}
	
	private void loadRoleBornData(){
		String fileName = XlsSheetNameType.role_born.getXlsName();
		String sheetName = XlsSheetNameType.role_born.getSheetName();
		String errorMsg = "loadRoleBornData error : sourceFile = "+fileName +" sheetName =" + sheetName;
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath()
					+ fileName;
			this.roleBorn = XlsPojoUtil.getEntity(sourceFile, sheetName,
					RoleBorn.class);
			if (null == roleBorn) {
				Log4jManager.CHECK
						.error(errorMsg + " not config roleBorn data");
				Log4jManager.checkFail();
				return;
			}
			this.roleBorn.init();
		} catch (Exception e) {
			Log4jManager.CHECK.error(errorMsg , e);
			Log4jManager.checkFail();
		}
	}

	@Override
	public RoleBorn getRoleBorn() {
		return this.roleBorn ;
	}
	
	@Override
	public RoleBornGuide getRoleBornGuide(){
		return this.roleBornGuide ;
	}
	
	@Override
	public boolean isBornHero(int heroId){
		return (null != this.roleBornHeroMap) && roleBornHeroMap.containsKey(heroId);
	}
	
	@Override
	public List<UserLoginHeroShowItem> getBornHeroInfoList(){
		//获得初始英雄的相关信息
		List<UserLoginHeroShowItem> heroList = Lists.newArrayList();
		for(RoleBornHero rbh : this.roleBornHeroMap.values()){
			int heroId = rbh.getHeroId(); 
			GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, heroId);
			if(null == goodsHero){
				continue ;
			}
			UserLoginHeroShowItem heroItem = new UserLoginHeroShowItem();
			heroItem.setHeroId(heroId);
			heroItem.setResId(rbh.getImageId());
			heroItem.setName(goodsHero.getName());
			heroItem.setMusicId(goodsHero.getMusicId());
			heroList.add(heroItem);
		}
		return heroList ;
	}
}
