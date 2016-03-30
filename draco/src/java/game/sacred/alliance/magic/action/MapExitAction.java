package sacred.alliance.magic.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.base.ChangeMapResult;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.MapContainer;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapMultiCopyContainer;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.buff.BuffLostType;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.copy.config.CopyConfig;
import com.game.draco.app.copy.config.CopyMapConfig;
import com.game.draco.app.team.Team;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0212_MapExitReqMessage;
import com.game.draco.message.response.C0212_MapCopySpreadRespMessage;

public class MapExitAction extends BaseAction<C0212_MapExitReqMessage>{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	
	private boolean isSpecial(MapInstance mapInstance) {
		int logicType = mapInstance.getMap().getMapConfig().getLogictype();
		return logicType == MapLogicType.arenaLogic.getType()
				|| logicType == MapLogicType.arenaPK.getType()
				|| logicType == MapLogicType.pet.getType()
				|| logicType == MapLogicType.roleBornGuide.getType()
				|| logicType == MapLogicType.heroArena.getType()
				|| logicType == MapLogicType.qualify.getType()
				|| logicType == MapLogicType.asyncArena.getType()
				|| logicType == MapLogicType.goblin.getType();
	}
	
	private boolean canShowExit(MapInstance mapInstance){
		return 1 == mapInstance.getMap().getMapConfig().getShowExit();
	}
	
	@Override
	public Message execute(ActionContext context, C0212_MapExitReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		try {
			MapInstance mapInstance = role.getMapInstance();
			if(null == mapInstance){
				return null;
			}
			try {
				//个人副本加速销毁
				MapContainer mapContainer = mapInstance.getMapContainer();
				if (mapContainer instanceof MapMultiCopyContainer) {
					((MapMultiCopyContainer) mapContainer).flagApplyExist();
				}
			}catch(Exception ex){
				
			}
			if (!canShowExit(mapInstance)) {
				//特殊地图不需要判断是否配置了出口
				C0003_TipNotifyMessage tips = new C0003_TipNotifyMessage();
				tips.setMsgContext(Status.MAP_NOT_SUPPORT_EXIT.getTips());
				return tips ;
			}
			
			if(role.isDeath() && !this.isSpecial(mapInstance)){
				C0003_TipNotifyMessage tips = new C0003_TipNotifyMessage();
				tips.setMsgContext(GameContext.getI18n().getText(TextId.Copy_Reborn_Trans_Out));
				return tips ;
			}
			
			Point point = mapInstance.getBeforeEnterPoint(role);
			if (null == point) {
				C0003_TipNotifyMessage tips = new C0003_TipNotifyMessage();
				tips.setMsgContext(GameContext.getI18n().getText(TextId.Copy_Not_Can_Trans));
				return tips ;
			}
			
			int logicType = mapInstance.getMap().getMapConfig().getLogictype();
			//副本重设置
			if(logicType == MapLogicType.copyLogic.getType()
					|| logicType == MapLogicType.unionTerritoryLogic.getType()){
				//离开副本消息
				this.sendOutMessage(role);
			}
			
			ChangeMapResult type = GameContext.getUserMapApp().changeMap(
					role,point);
			if(!type.isSuccess()){
				C0003_TipNotifyMessage tips = new C0003_TipNotifyMessage();
				tips.setMsgContext(GameContext.getI18n().getText(TextId.Copy_Trans_Out_Error));
				return tips ;
			}
			
			MapConfig mapConfig = mapInstance.getMap().getMapConfig();
			if(null != mapConfig) {
				short copyId = mapConfig.getCopyId();
				if(copyId > -1) {
					GameContext.getExchangeApp().resetExchangeByCopyId(role, copyId);
					GameContext.getSummonApp().resetSummonByCopyId(role, copyId);
				}
			}
			role.delAllBuffStat(BuffLostType.exitInsLost);
		} catch (Exception ex) {
			logger.error("",ex);
		}
		return new C0212_MapCopySpreadRespMessage() ;
	}
	
	//离开副本消息
	private void sendOutMessage(RoleInstance role) {
		try {
			CopyMapConfig mapConfig = GameContext.getCopyLogicApp()
					.getMapConfig(role.getMapId());
			if (mapConfig == null) {
				return;
			}
			CopyConfig config = GameContext.getCopyLogicApp().getCopyConfig(
					mapConfig.getCopyId());
			String msg = GameContext.getI18n().messageFormat(TextId.Copy_Role_Leave_Copy, role.getRoleName(), config.getCopyName());
			/*if (config.getCopyType() == CopyType.faction) {
				Faction faction = role.getFaction();
				if (faction == null) {
					return;
				}
				GameContext.getChatApp().sendSysMessage(ChatSysName.System,
						ChannelType.Faction, msg, null, faction);
				return;
			}*/
			Team team = role.getTeam();
			if (team == null) {
				return;
			}
			GameContext.getChatApp().sendSysMessage(ChatSysName.System,
					ChannelType.Team, msg, null, role.getTeam());
		} catch (Exception e) {
			this.logger.error("", e);
		}
	}

}
