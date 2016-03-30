package sacred.alliance.magic.app.role;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleBorn;
import sacred.alliance.magic.vo.RoleBornHero;

import com.game.draco.GameContext;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.item.UserLoginHeroShowItem;
import com.game.draco.message.item.UserLoginHeroSkillItem;
import com.google.common.collect.Lists;

public class RoleBornAppImpl implements RoleBornApp{
	
	private RoleBorn roleBorn ;
	private Map<Integer,RoleBornHero> roleBornHeroMap ; 
	private final byte RUN_ACTION_ID = 1 ;
	private final byte WAIT_ACTION_ID = 0 ;
	private final int RUN_EFFECT_TIME = 2000 ;
	private final int WAIT_EFFECT_TIME = 300 ;
	
	
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
			heroItem.setHeadId(goodsHero.getHeadId());
			heroItem.setResId((short)goodsHero.getResId());
			heroItem.setDesc(rbh.getDesc()); 
			heroItem.setName(goodsHero.getName());
			List<UserLoginHeroSkillItem> skillList = Lists.newArrayList();
			List<Short> skillIdList = goodsHero.getSkillIdList();
			//行走
			UserLoginHeroSkillItem runItem = new UserLoginHeroSkillItem();
			runItem.setEffectId((byte)-1);
			runItem.setActionId(RUN_ACTION_ID);
			runItem.setEffectTime(RUN_EFFECT_TIME);
			skillList.add(runItem);
			
			for(short skillId : skillIdList){
				Skill skill = GameContext.getSkillApp().getSkill(skillId);
				if(null == skill){
					continue ;
				}
				SkillDetail detail = skill.getSkillDetail(1);
				if(null == detail){
					continue ;
				}
				//相关技能
				UserLoginHeroSkillItem skillItem = new UserLoginHeroSkillItem();
				skillItem.setActionId(detail.getActionId());
				skillItem.setEffectId(detail.getEffectId());
				skillItem.setEffectTime(detail.getEffectTime());
				skillList.add(skillItem);
				//待机
				UserLoginHeroSkillItem waitItem = new UserLoginHeroSkillItem();
				waitItem.setEffectId((byte)-1);
				waitItem.setActionId(WAIT_ACTION_ID);
				waitItem.setEffectTime(WAIT_EFFECT_TIME);
				skillList.add(waitItem);
			}
			heroItem.setSkillInfoItem(skillList);
			heroList.add(heroItem);
		}
		return heroList ;
	}
}
