package sacred.alliance.magic.app.menu.func;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.menu.MenuIdType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;

public class MenuActiveDpsFunc extends MenuAbstractActiveFunc{

	public MenuActiveDpsFunc() {
		super(MenuIdType.Active_Dps);
	}

	
	@Override
	public Message createFuncReqMessage(RoleInstance role) {
		try {
			Active active = this.getActive();
			//不符合活动条件，提示活动未开启
			if(!active.isSuitLevel(role) || !active.isTimeOpen()){
				return new C0003_TipNotifyMessage(this.getText(TextId.Active_Not_Open));
			}
			Point point = GameContext.getActiveDpsApp().getEnterMapPoint(active.getId());
			if(null == point){
				return new C0003_TipNotifyMessage(this.getText(TextId.Active_Enter_Map_Point_Null));
			}
			//进入DPS地图
			GameContext.getUserMapApp().changeMap(role, point);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".createFuncReqMessage error : ", e);
		}
		return null;
	}

}
