package sacred.alliance.magic.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.copy.CopyConfig;
import com.game.draco.app.copy.CopyMapConfig;
import com.game.draco.app.copy.CopyType;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0212_MapCopySpreadReqMessage;
import com.game.draco.message.response.C0212_MapCopySpreadRespMessage;

import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.chat.ChatSysName;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.app.team.Team;
import sacred.alliance.magic.base.ChangeMapResult;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.MapContainer;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapMultiCopyContainer;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

public class MapCopySpreadAction extends BaseAction<C0212_MapCopySpreadReqMessage>{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	
	private boolean isSpecial(MapInstance mapInstance) {
		int logicType = mapInstance.getMap().getMapConfig().getLogictype();
		return logicType == MapLogicType.arenaLogic.getType()
				|| logicType == MapLogicType.arenaPK.getType()
				|| logicType == MapLogicType.goddess.getType() ;
	}
	
	private boolean canShowExit(MapInstance mapInstance){
		return 1 == mapInstance.getMap().getMapConfig().getShowExit();
	}
	
	@Override
	public Message execute(ActionContext context, C0212_MapCopySpreadReqMessage reqMsg) {
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
			boolean isSpecial = this.isSpecial(mapInstance);
			if (!isSpecial && !canShowExit(mapInstance)) {
				//特殊地图不需要判断是否配置了出口
				C0003_TipNotifyMessage tips = new C0003_TipNotifyMessage();
				tips.setMsgContext(Status.MAP_NOT_SUPPORT_EXIT.getTips());
				return tips ;
			}
			
			if(role.isDeath() && !isSpecial){
				C0003_TipNotifyMessage tips = new C0003_TipNotifyMessage();
				tips.setMsgContext(Status.Copy_Reborn_Trans_Out.getTips());
				return tips ;
			}
			
			Point point = role.getCopyBeforePoint();
			if (null == point) {
				C0003_TipNotifyMessage tips = new C0003_TipNotifyMessage();
				tips.setMsgContext(Status.Copy_Not_Can_Trans.getTips());
				return tips ;
			}
			
			int logicType = mapInstance.getMap().getMapConfig().getLogictype();
			//副本重设置
			if(logicType == MapLogicType.copyLogic.getType()
					|| logicType == MapLogicType.unionLogic.getType()){
				//离开副本消息
				this.sendOutMessage(role);
			}
			
			ChangeMapResult type = GameContext.getUserMapApp().changeMap(
					role,point);
			if(!type.isSuccess()){
				C0003_TipNotifyMessage tips = new C0003_TipNotifyMessage();
				tips.setMsgContext(Status.Copy_Trans_Out_Error.getTips());
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
			String msg = Status.Copy_Role_Leave_Copy.getTips().replace(
					Wildcard.Role_Name, role.getRoleName()).replace(
					Wildcard.CopyName, config.getCopyName());
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
