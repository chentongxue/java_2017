package sacred.alliance.magic.app.menu;

import lombok.Data;
import sacred.alliance.magic.app.menu.before.MenuActiveAngelchestBefore;
import sacred.alliance.magic.app.menu.before.MenuActiveCampWarBefore;
import sacred.alliance.magic.app.menu.before.MenuActiveDpsBefore;
import sacred.alliance.magic.app.menu.before.MenuActiveGoblinBefore;
import sacred.alliance.magic.app.menu.before.MenuActiveSurvivalBattleBefore;
import sacred.alliance.magic.app.menu.before.MenuActiveUnionIntegralBattleBefore;
import sacred.alliance.magic.app.menu.before.MenuArena1v1Before;
import sacred.alliance.magic.app.menu.before.MenuArena3v3Before;
import sacred.alliance.magic.app.menu.before.MenuArena3v3DarkDoorBefore;
import sacred.alliance.magic.app.menu.before.MenuUnionBattleBefore;
import sacred.alliance.magic.app.menu.func.MenuActiveAccumulateLoginFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveAngelchestFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveCampWarFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveChoiceCardFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveDpsFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveFactionSuperFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveFirstPayFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveGoblinFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveSiegeFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveSurvivalBattleFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveUnionBattleFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveUnionIntegralBattleFunc;
import sacred.alliance.magic.app.menu.func.MenuAlchemyFunc;
import sacred.alliance.magic.app.menu.func.MenuArena1v1Func;
import sacred.alliance.magic.app.menu.func.MenuArena3v3DarkDoorFunc;
import sacred.alliance.magic.app.menu.func.MenuArena3v3Func;
import sacred.alliance.magic.app.menu.func.MenuAsyncArenaFunc;
import sacred.alliance.magic.app.menu.func.MenuCopyFunc;
import sacred.alliance.magic.app.menu.func.MenuHeroArenaFunc;
import sacred.alliance.magic.app.menu.func.MenuLuckBoxFunc;
import sacred.alliance.magic.app.menu.func.MenuOperateActiveFunc;
import sacred.alliance.magic.app.menu.func.MenuQuestPokerFunc;
import sacred.alliance.magic.app.menu.func.MenuRankFunc;
import sacred.alliance.magic.app.menu.func.MenuRichManFunc;
import sacred.alliance.magic.app.menu.func.MenuShopFunc;
import sacred.alliance.magic.app.menu.func.MenuSignRewardFunc;
import sacred.alliance.magic.app.menu.func.MenuTaobaoFunc;

public @Data class MenuConfig {

	//菜单对应功能ID
	private short menuId;
	//菜单类型
	private byte menuType ;
	//图片ID
	private short iconId ;
	//特效ID
	private short effectId ;
	//优先级
	private byte priority ;
	//角色等级
	private int roleLevel ;
	//关联活动ID
	private int activeId ;
	//活动开启提前提示时间 （秒）
	private short activeBeforeTimes ;
	//几级菜单
	private byte menuLevel;
	//上级菜单ID
	private short superiorMenuId;
	
	private MenuFunc menuFunc ;
	private MenuBefore menuBefore ;
	
	public void init(){
		MenuIdType mt = MenuIdType.get(this.menuId);
		switch(mt){
		case Rank :
			this.menuFunc = new MenuRankFunc() ;
			return ;
		case Shop :
			this.menuFunc = new MenuShopFunc() ;
			return ;
		case SignReward :
			this.menuFunc = new MenuSignRewardFunc() ;
			return ;
		case Operate_Active :
			this.menuFunc = new MenuOperateActiveFunc() ;
			return ;
		case Active :
			this.menuFunc = new MenuActiveFunc() ;
			return ;
		case Active_Dps :
			this.menuFunc = new MenuActiveDpsFunc() ;
			if(this.activeBeforeTimes > 0){
				this.menuBefore = new MenuActiveDpsBefore();
			}
			return ;
		case Active_CampWar :
			this.menuFunc = new MenuActiveCampWarFunc();
			if(this.activeBeforeTimes > 0){
				this.menuBefore = new MenuActiveCampWarBefore();
			}
			return ;
		case Active_Siege :
			this.menuFunc = new MenuActiveSiegeFunc() ;
			return ;
		case Active_FactionSuper :
			this.menuFunc = new MenuActiveFactionSuperFunc() ;
			return ;
		case Taobao :
			this.menuFunc = new MenuTaobaoFunc() ;
			return ;
		case LuckBox :
			this.menuFunc = new MenuLuckBoxFunc() ;
			return ;
		case Alchemy :
			this.menuFunc = new MenuAlchemyFunc();
			return ;
		case Copy :
			this.menuFunc = new MenuCopyFunc();
			return ;
		case QuestPoker :
			this.menuFunc = new MenuQuestPokerFunc();
			return ;
		case RichMan :
			this.menuFunc = new MenuRichManFunc() ;
			return ;
		case Hero_Arena :
			this.menuFunc = new MenuHeroArenaFunc() ;
			return ;
		case Arena_1v1 :
			this.menuFunc = new MenuArena1v1Func() ;
			if(this.activeBeforeTimes > 0){
				this.menuBefore = new MenuArena1v1Before();
			}
			return;
		case AsyncArena:
			this.menuFunc = new MenuAsyncArenaFunc() ;
			return ;
		case AccumulateLogin:
			this.menuFunc = new MenuActiveAccumulateLoginFunc() ;
			return ;
		case ChoiceCard:
			this.menuFunc = new MenuActiveChoiceCardFunc() ;
			return ;
		case FirstPay:
			this.menuFunc = new MenuActiveFirstPayFunc() ;
			return ;
		case Goblin:
			this.menuFunc = new MenuActiveGoblinFunc() ;
			if(this.activeBeforeTimes > 0){
				this.menuBefore = new MenuActiveGoblinBefore();
			}
			return ;
		case Active_AngelChest :
			this.menuFunc = new MenuActiveAngelchestFunc() ;
			if(this.activeBeforeTimes > 0){
				this.menuBefore = new MenuActiveAngelchestBefore();
			}
			return ;
		case SurvivalBattle :
			this.menuFunc = new MenuActiveSurvivalBattleFunc() ;
			if(this.activeBeforeTimes > 0){
				this.menuBefore = new MenuActiveSurvivalBattleBefore();
			}
			return ;
		case UnionBattle :
			this.menuFunc = new MenuActiveUnionBattleFunc();
			if(this.activeBeforeTimes > 0){
				this.menuBefore = new MenuUnionBattleBefore();
			}
			return ;
		case arena_3v3 :
			this.menuFunc = new MenuArena3v3Func();
			if(this.activeBeforeTimes > 0){
				this.menuBefore = new MenuArena3v3Before();
			}
			return ;
		case arena_3v3_dark_door:
			this.menuFunc = new MenuArena3v3DarkDoorFunc();
			if(this.activeBeforeTimes > 0){
				this.menuBefore = new MenuArena3v3DarkDoorBefore();
			}
			return ;
		case UnionIntegralBattle :
			this.menuFunc = new MenuActiveUnionIntegralBattleFunc() ;
			if(this.activeBeforeTimes > 0){
				this.menuBefore = new MenuActiveUnionIntegralBattleBefore();
			}
			return ;
		}
		
	}
}
