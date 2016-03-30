package sacred.alliance.magic.app.active.tower;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2551_TowerInfoReqMessage;
import sacred.alliance.magic.app.active.ActiveSupport;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;


public class ActiveTowerSupport implements ActiveSupport {

    @Override
    public Message getActiveDetail(RoleInstance role, Active active) {
       return GameContext.getTowerApp().getTowerInfoMessage(role) ;
    }

    @Override
    public ActiveStatus getActiveStatus(RoleInstance role, Active active) {
        if(!active.isTimeOpen() || !active.isSuitLevel(role)){
            return ActiveStatus.NotOpen;
        }
        return ActiveStatus.CanAccept;
    }

    @Override
    public void checkReset(RoleInstance role, Active active) {

    }

    @Override
    public boolean isOutDate(Active active) {
        return false;
    }

    @Override
    public boolean getActiveHint(RoleInstance role, Active active) {
        return false;
    }
}
