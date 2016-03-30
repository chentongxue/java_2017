/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sacred.alliance.magic.app.ai.event;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.app.npc.domain.NpcInstance;

import sacred.alliance.magic.app.ai.Ai;
import sacred.alliance.magic.app.ai.Telegram;
import sacred.alliance.magic.vo.AbstractRole;

/**
 *
 * 考虑还是使用单线程,1个地图实例使用一个消息分发器
 */
public class DefaultMessageDispatcher implements MessageDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(DefaultMessageDispatcher.class);
    private LinkedList<Telegram> messages = new LinkedList<Telegram>();

    @Override
    public void dispatchMessages(long currentTime) {
        //注意此处的currentTime
         while (!messages.isEmpty() && messages.getFirst().getDispatchTime() < currentTime) {
            Telegram telegram = messages.getFirst();
            discharge(telegram);
            messages.remove(telegram);
        }
    }


    /**
      * Npc接收到消息的统一执行接口
      * @param receiver
      * @param telegram
      */
    private void discharge(Telegram telegram) {
        if(null == telegram || null == telegram.getSender()){
            return ;
        }
        AbstractRole receiver = telegram.getReceiver();
        if(null == receiver){
            //如果接受者为空,则为当前地图里面的所有npc
            for(NpcInstance npc : telegram.getSender().getMapInstance().getNpcList()){
            	if(npc.getRoleId().equals(telegram.getSender().getRoleId())){
            		//自己
            		continue ;
            	}
                 npcHandleMessage(npc,telegram);
            }
            return ;
        }
        npcHandleMessage(receiver,telegram);
       
    }

    private void npcHandleMessage(AbstractRole receiver,Telegram telegram){
    	if(null == receiver){
    		return ;
    	}
        Ai ai = receiver.getAi() ;
        if(null == ai){
            return ;
        }
        try{
             ai.getStateMachine().handleMessage(telegram);
        }catch(Exception ex){
            logger.error("",ex);
        }
    }
            
    @Override
    public void dispatch(Telegram telegram) {
        if(null == telegram || null == telegram.getSender()){
            return ;
        }
        if(telegram.getDispatchTime() <= 0.0f){
            //当时就执行
            this.discharge(telegram);
            return ;
        }
         telegram.setDispatchTime(System.currentTimeMillis() + telegram.getDispatchTime());
         messages.add(telegram);
         Collections.sort(messages, new TelegramComparator());
    }

      private static class TelegramComparator implements Comparator<Telegram> {
        @Override
        public int compare(Telegram t1, Telegram t2) {
            return Double.compare(t1.getDispatchTime(), t2.getDispatchTime());
        }
    }
    
}
