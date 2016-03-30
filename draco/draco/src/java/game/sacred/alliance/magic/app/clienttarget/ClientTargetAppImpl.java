package sacred.alliance.magic.app.clienttarget;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.message.push.C3001_ClientTargetNotifyMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.vo.RoleInstance;

public class ClientTargetAppImpl implements ClientTargetApp {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void autoCollect() {
		try{
			Collection<RoleInstance> collection = GameContext.getOnlineCenter().getAllOnlineRole();
			if(collection.size() <= 0){
				return;
			}
			C3001_ClientTargetNotifyMessage message = new C3001_ClientTargetNotifyMessage();
			for(RoleInstance role : collection){
				if(null == role){
					continue;
				}
				/** 
				 * 网络延时计算
				 * 1.服务器主推-3001进行取样，此时在角色身上保存当前时间T1（毫秒）
				 * 2.客户端收到-3001时需要发3000，服务器接到命令时，当前时间为T2。计算一次消息交互时间：time=T2-T1，将time封装到-3000中返回给客户端。
				 * 3.客户端收到-3000时，获取time的值，构建3001消息发送给服务器。
				 */
				role.setNetDelayTime(System.currentTimeMillis());
				GameContext.getMessageCenter().sendByRoleId(null, role.getRoleId(), message);
			}
		}catch(Exception e){
			this.logger.error(this.getClass().getName() + ".autoCollect error: ", e);
		}
	}

	@Override
	public Result gmCollect(int roleNum) {
		Result result = new Result();
		try{
			if(roleNum <= 0){
				return result.setInfo(GameContext.getI18n().getText(TextId.CLIENT_TARGET_NUM));
			}
			Collection<RoleInstance> collection = GameContext.getOnlineCenter().getAllOnlineRole();
			int onlineSize = collection.size();
			if(onlineSize <= 0){
				return result.setInfo(GameContext.getI18n().getText(TextId.CLIENT_TARGET_ON_ONLINE));
			}
			if(roleNum > onlineSize){
				return result.setInfo(GameContext.getI18n().getText(TextId.CLIENT_TARGET_NUM_MAX));
			}
			C3001_ClientTargetNotifyMessage message = new C3001_ClientTargetNotifyMessage();
			float odds = roleNum/(float)onlineSize;
			int count = 0;
			int index = 0;
			for(RoleInstance role : collection){
				index ++;
				if(count >= roleNum){
					break;
				}
				if(onlineSize-index <= roleNum-count || RandomUtil.randomBoolean(odds)){
					count++;
					/** 网络延时计算（第一次记录时间） */
					role.setNetDelayTime(System.currentTimeMillis());
					GameContext.getMessageCenter().sendByRoleId(null,role.getRoleId(), message);
				}
			}
			return result.success();
		}catch(Exception e){
			this.logger.error(this.getClass().getName() + ".gmCollect error: ", e);
			return result.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
		}
	}

	@Override
	public Result collectRole(RoleInstance role) {
		Result result = new Result();
		try{
			if(null == role){
				return result.setInfo(GameContext.getI18n().getText(TextId.Role_No_Online));
			}
			C3001_ClientTargetNotifyMessage message = new C3001_ClientTargetNotifyMessage();
			role.setNetDelayTime(System.currentTimeMillis());
			GameContext.getMessageCenter().sendByRoleId(null, role.getRoleId(), message);
			return result.success();
		}catch(Exception e){
			this.logger.error(this.getClass().getName() + ".collectRole error: ", e);
			return result.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
		}
	}
	
}
