package sacred.alliance.magic.app.hint;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.message.push.C1171_HintAddNotifyMessage;
import com.game.draco.message.push.C1172_HintRemoveNotifyMessage;
import com.game.draco.message.response.C1170_HintListRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class HintAppImpl implements HintApp {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<HintSupport> hintSupportList;
	
	@Override
	public void pushHintListMessage(RoleInstance role) {
		try {
			if(null == this.hintSupportList){
				return;
			}
			List<Byte> idList = new ArrayList<Byte>();
			for(HintSupport support : this.hintSupportList){
				if(null == support){
					continue;
				}
				Set<HintId> hintIdSet = support.getHintIdSet(role);
				if(Util.isEmpty(hintIdSet)){
					continue;
				}
				for(HintId hintId : hintIdSet){
					if(null == hintId){
						continue;
					}
					idList.add(hintId.getId());
				}
			}
			int size = idList.size();
			if(size <= 0){
				return;
			}
			byte[] ids = new byte[size];
			for(int i=0; i<size; i++){
				ids[i] = idList.get(i);
			}
			C1170_HintListRespMessage message = new C1170_HintListRespMessage();
			message.setIds(ids);
			this.sendMessage(role, message);
		} catch (Exception e) {
			this.logger.error("HintApp.pushHintListMessage error: ", e);
		}
	}
	
	@Override
	public void hintChange(RoleInstance role, HintId hintId, boolean hasHint) {
		try {
			if(hasHint){
				this.hintAddNotify(role, hintId);
			}else{
				this.hintRemoveNotify(role, hintId);
			}
		} catch (Exception e) {
			this.logger.error("HintApp.hintChange error: ", e);
		}
	}
	
	/**
	 * 通知新增特效
	 * @param role
	 * @param hintId
	 */
	private void hintAddNotify(RoleInstance role, HintId hintId) {
		try {
			C1171_HintAddNotifyMessage message = new C1171_HintAddNotifyMessage();
			message.setId(hintId.getId());
			this.sendMessage(role, message);
		} catch (Exception e) {
			this.logger.error("HintApp.addHint error: ", e);
		}
	}

	/**
	 * 通知特效消失
	 * @param role
	 * @param hintId
	 */
	private void hintRemoveNotify(RoleInstance role, HintId hintId) {
		try {
			C1172_HintRemoveNotifyMessage message = new C1172_HintRemoveNotifyMessage();
			message.setId(hintId.getId());
			this.sendMessage(role, message);
		} catch (Exception e) {
			this.logger.error("HintApp.removeHint error: ", e);
		}
	}
	
	/**
	 * 发消息
	 * @param role
	 * @param message
	 */
	protected void sendMessage(RoleInstance role, Message message){
		GameContext.getMessageCenter().sendSysMsg(role, message);
	}

	@Override
	public void sysPushHintMsg() {
		try {
			//没有用户在线
			if(GameContext.getOnlineCenter().onlineUserSize() <= 0){
				return;
			}
			for(RoleInstance role : GameContext.getOnlineCenter().getAllOnlineRole()){
				if(null == role){
					continue;
				}
				this.pushHintListMessage(role);
			}
		} catch (Exception e) {
			this.logger.error("HintApp.sysPushHintMsg error: ", e);
		}
	}
	
	public List<HintSupport> getHintSupportList() {
		return hintSupportList;
	}

	public void setHintSupportList(List<HintSupport> hintSupportList) {
		this.hintSupportList = hintSupportList;
	}

}
