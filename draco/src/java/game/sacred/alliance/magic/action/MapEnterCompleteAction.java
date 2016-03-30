package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0241_MapEnterCompleteReqMessage;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;


public class MapEnterCompleteAction extends BaseAction<C0241_MapEnterCompleteReqMessage> {

    @Override
    public Message execute(ActionContext context, C0241_MapEnterCompleteReqMessage reqMsg) {
        RoleInstance role = this.getCurrentRole(context) ;
        if(null == role){
            return null ;
        }
        GameContext.getDramaApp().enterMap(role, role.getMapId());
        return null;
    }
}
