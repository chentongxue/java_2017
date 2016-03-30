package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.buff.Buff;
import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.message.item.RoleAttrSyncItem;
import com.game.draco.message.item.RoleBuffItem;
import com.game.draco.message.request.C0500_RoleAttrSyncReqMessage;
import com.game.draco.message.response.C0500_RoleAttrSyncRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

public class RoleAttrSyncAction extends BaseAction<C0500_RoleAttrSyncReqMessage> {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public Message execute(ActionContext context, C0500_RoleAttrSyncReqMessage req) {
		C0500_RoleAttrSyncRespMessage resp = new C0500_RoleAttrSyncRespMessage();
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		try {
			int[] roleIds = req.getRoleIds();
			if (null == roleIds || roleIds.length == 0){
				return null;
			}
			MapInstance mapInstance = role.getMapInstance();
			if(null == mapInstance){
				return null;
			}
			List<RoleAttrSyncItem> synItemes = new ArrayList<RoleAttrSyncItem>();
			for (int roleId : roleIds) {
				AbstractRole roleIn = null ;
				if(roleId < 0){
					roleIn = mapInstance.getNpcInstance(String.valueOf(roleId));
				}else{
					roleIn = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
				}
				if (null == roleIn) {
					continue;
				}
				RoleAttrSyncItem synItem = new RoleAttrSyncItem();

				synItem.setRoleId(roleId);
				synItem.setCurrHP(roleIn.getCurHP());
				//synItem.setExp(roleIn.getExp());
				synItem.setLevel((byte) roleIn.getLevel());
				synItem.setMaxHp(roleIn.getMaxHP());
				//synItem.setX((short)roleIn.getMapX());
				//synItem.setY((short)roleIn.getMapY());
				List<RoleBuffItem> buffItemes = new ArrayList<RoleBuffItem>();
				Collection<BuffStat> buffList = roleIn.getReceiveBuffCopy();
				for (BuffStat buffStat : buffList) {
					try {
						RoleBuffItem buffItem = new RoleBuffItem();
						Buff buff = GameContext.getBuffApp().getBuff(buffStat
								.getBuffId());
						buffItem.setCategoryType((byte) buff.getCategoryType().getType());
						buffItem.setBuffId(buff.getBuffId());
//						buffItem.setBuffPersistTime(buff
//								.getPersistTime(buffStat.getBuffLevel()));
						buffItem.setLayer(buffStat.getLayer());
						buffItem.setIconId(buff.getIconId());
						buffItem.setBuffRemainTime(buffStat.getRemainTime());
						buffItem.setEffectId(buff.getEffectId());
						buffItemes.add(buffItem);
					} catch (Exception e) {
						logger.error("", e);
						continue;
					}
				}
				synItem.setBuffItemes(buffItemes);

				synItemes.add(synItem);
			}

			resp.setItemes(synItemes.toArray(new RoleAttrSyncItem[0]));
			return resp;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}

}
