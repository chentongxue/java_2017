/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sacred.alliance.magic.app.ai.event;

/**
 *
 * @author tiefengKuang
 * @time 2010-3-17
 */
public class MessageDispatcherFactory {

    public static MessageDispatcher createDispatcher() {
        return new DefaultMessageDispatcher();
    }
}
