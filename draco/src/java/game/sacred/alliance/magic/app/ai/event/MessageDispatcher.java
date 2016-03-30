/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sacred.alliance.magic.app.ai.event;

import sacred.alliance.magic.app.ai.Telegram;

/**
 *
 * @author tiefengKuang
 * @time 2010-3-16
 */
public interface MessageDispatcher {

    /**
     * 主循环调用
     */
     public void dispatchMessages(long currentTime) ;

     /**
      * 消息分发接口,一般在NPC行为中调用
      * @param telegram
      */
     public void dispatch(Telegram telegram);
}
