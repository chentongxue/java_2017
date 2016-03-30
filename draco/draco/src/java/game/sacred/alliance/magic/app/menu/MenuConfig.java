package sacred.alliance.magic.app.menu;

import lombok.Data;
import sacred.alliance.magic.app.menu.before.MenuActiveCampWarBefore;
import sacred.alliance.magic.app.menu.before.MenuActiveDpsBefore;
import sacred.alliance.magic.app.menu.before.MenuArena1v1Before;
import sacred.alliance.magic.app.menu.func.MenuActiveAthleticsFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveBoxFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveCampWarFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveDiscountFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveDpsFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveFactionSuperFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveFactionWarFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveKillWJFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveSiegeFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveSuperKingFunc;
import sacred.alliance.magic.app.menu.func.MenuActiveTimeLimitFunc;
import sacred.alliance.magic.app.menu.func.MenuAlchemyFunc;
import sacred.alliance.magic.app.menu.func.MenuArena1v1Func;
import sacred.alliance.magic.app.menu.func.MenuAsyncArenaFunc;
import sacred.alliance.magic.app.menu.func.MenuCopyFunc;
import sacred.alliance.magic.app.menu.func.MenuHangUpFunc;
import sacred.alliance.magic.app.menu.func.MenuHeroArenaFunc;
import sacred.alliance.magic.app.menu.func.MenuLuckBoxFunc;
import sacred.alliance.magic.app.menu.func.MenuLuckDialFunc;
import sacred.alliance.magic.app.menu.func.MenuQuestPokerFunc;
import sacred.alliance.magic.app.menu.func.MenuRankFunc;
import sacred.alliance.magic.app.menu.func.MenuRichManFunc;
import sacred.alliance.magic.app.menu.func.MenuShopFunc;
import sacred.alliance.magic.app.menu.func.MenuSignRewardFunc;
import sacred.alliance.magic.app.menu.func.MenuStorySuitFunc;

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
		case HangUp :
			this.menuFunc = new MenuHangUpFunc() ;
			return ;
		case Rank :
			this.menuFunc = new MenuRankFunc() ;
			return ;
		case Shop :
			this.menuFunc = new MenuShopFunc() ;
			return ;
		case SignReward :
			this.menuFunc = new MenuSignRewardFunc() ;
			return ;
		case Active_Discount :
			this.menuFunc = new MenuActiveDiscountFunc() ;
			return ;
		case Active :
			this.menuFunc = new MenuActiveFunc() ;
			return ;
		case StorySuit :
			this.menuFunc = new MenuStorySuitFunc() ;
			return ;
		case Active_Dps :
			this.menuFunc = new MenuActiveDpsFunc() ;
			if(this.activeBeforeTimes > 0){
				this.menuBefore = new MenuActiveDpsBefore();
			}
			return ;
		case Active_KillWJ :
			this.menuFunc = new MenuActiveKillWJFunc() ;
			return ;
		case Active_Athletics :
			this.menuFunc = new MenuActiveAthleticsFunc();
			return ;
		case Active_Box :
			this.menuFunc = new MenuActiveBoxFunc() ;
			return ;
		case Active_CampWar :
			this.menuFunc = new MenuActiveCampWarFunc();
			if(this.activeBeforeTimes > 0){
				this.menuBefore = new MenuActiveCampWarBefore();
			}
			return ;
		case Active_FactionWar :
			this.menuFunc = new MenuActiveFactionWarFunc() ;
			return ;
		case Active_Siege :
			this.menuFunc = new MenuActiveSiegeFunc() ;
			return ;
		case Active_SuperKing :
			this.menuFunc = new MenuActiveSuperKingFunc() ;
			return ;
		case Active_FactionSuper :
			this.menuFunc = new MenuActiveFactionSuperFunc() ;
			return ;
		case Active_TimeLimit :
			this.menuFunc = new MenuActiveTimeLimitFunc() ;
			return ;
		case LuckDial :
			this.menuFunc = new MenuLuckDialFunc() ;
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
		}
		
	}
}
