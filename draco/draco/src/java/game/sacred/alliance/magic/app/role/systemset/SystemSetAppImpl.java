package sacred.alliance.magic.app.role.systemset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.message.item.SystemSetItem;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.dao.BaseDAO;
import sacred.alliance.magic.domain.RoleSystemSet;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.vo.RoleInstance;

public class SystemSetAppImpl implements SystemSetApp {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private BaseDAO baseDAO;
	
	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}

	@Override
	public boolean modifyRoleSysSet(RoleInstance role, List<SystemSetItem> sysSetList) {
		try{
			Set<SystemSetType> typeSet = new HashSet<SystemSetType>();
			RoleSystemSet sysSet = role.getSystemSet();
			for(SystemSetItem item : sysSetList){
				if(null == item){
					continue;
				}
				SystemSetType systemSetType = SystemSetType.get(item.getType());
				sysSet.setValue(systemSetType, item.getValue());
				typeSet.add(systemSetType);
			}
			if(SystemSetState.Insert != sysSet.getSysSetState()){
				sysSet.setSysSetState(SystemSetState.Update);
			}
			//时装显示设置，需要同步人物外形
			if(typeSet.contains(SystemSetType.Fashion)){
				GameContext.getUserRoleApp().pushRoleShapeNotifyMessage(role);
			}
			return true;
		}catch(Exception e){
			this.logger.error("",e);
			return false;
		}
	}
	
	@Override
	public void initRoleSysSet(RoleInstance role) {
		try{
			RoleSystemSet sysSet = this.selectSystemSet(role.getIntRoleId());
			SystemSetState state = SystemSetState.Initialize;
			if(null == sysSet){
				sysSet = RoleSystemSet.getDefaultInstance(role.getIntRoleId());
				state = SystemSetState.Insert;
			}
			sysSet.setSysSetState(state);
			role.setSystemSet(sysSet);
		}catch(Exception e){
			this.logger.error("initRoleSysSet error:",e);
		}
	}
	
	/**
	 * 从数据库中查询玩家的系统设置
	 * @param roleId
	 * @return
	 */
	private RoleSystemSet selectSystemSet(int roleId){
		return this.baseDAO.selectEntity(RoleSystemSet.class, RoleSystemSet.ROLEID, roleId);
	}
	
	@Override
	public void saveSysSet(RoleInstance role) {
		try{
			RoleSystemSet sysSet = role.getSystemSet();
			SystemSetState state = sysSet.getSysSetState();
			if(SystemSetState.Initialize == state || sysSet.isDefaultValue()){
				return;
			}
			if(SystemSetState.Insert == state){
				this.baseDAO.insert(sysSet);
			}else if(SystemSetState.Update == state){
				this.baseDAO.update(sysSet);
			}
		}catch(Exception e){
			this.logger.error("saveSysSet error:",e);
		}
	}
	
	@Override
	public void offlineLog(RoleInstance role){
		try{
			RoleSystemSet sysSet = role.getSystemSet();
			SystemSetState state = sysSet.getSysSetState();
			if(SystemSetState.Initialize == state || sysSet.isDefaultValue()){
				return;
			}
			StringBuffer buffer = new StringBuffer();
			buffer.append(sysSet.getRoleId())
				.append(Cat.pound).append(sysSet.getHelmet())
				.append(Cat.pound).append(sysSet.getChat())
				.append(Cat.pound).append(sysSet.getGuide())
				.append(Cat.pound).append(sysSet.getTeamInvite())
				.append(Cat.pound).append(sysSet.getTeamApply())
				.append(Cat.pound).append(sysSet.getTrade())
				.append(Cat.pound).append(sysSet.getGuide2())
				.append(Cat.pound).append(sysSet.getChatVoice())
				.append(Cat.pound).append(sysSet.getFashion());
			Log4jManager.OFFLINE_SYS_SET_DB_LOG.info(buffer.toString());
		}catch(Exception e){
			this.logger.error("offlineLog error:",e);
		}
	}

	@Override
	public List<SystemSetItem> getSystemSetList(RoleInstance role) {
		List<SystemSetItem> sysSetList = new  ArrayList<SystemSetItem>();
		for(SystemSetType sysSetType : SystemSetType.values()){
			SystemSetItem item = new SystemSetItem();
			item.setType(sysSetType.getType());
			item.setValue(role.getSystemSet().getValue(sysSetType));
			sysSetList.add(item);
		}
		return sysSetList;
	}
	
}
