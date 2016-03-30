package sacred.alliance.magic.app.attri.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1107_RoleAttriHelpReqMessage;
import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;


public class RoleAttriHelpAction extends BaseAction<C1107_RoleAttriHelpReqMessage> {
    @Override
    public Message execute(ActionContext context, C1107_RoleAttriHelpReqMessage reqMsg) {

        RoleInstance role = this.getCurrentRole(context);
        if(null == role){
            return null ;
        }
        return GameContext.getAttriApp().getAttriHelpMessage(role);
    }
}
