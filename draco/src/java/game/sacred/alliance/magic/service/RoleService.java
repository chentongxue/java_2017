package sacred.alliance.magic.service;
import java.util.List;

import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.RoleInstance;

public interface RoleService {
	List<RoleInstance> selectAllByUserId(String userId) throws ServiceException;
	RoleInstance selectByRoleId(String roleId) throws ServiceException;
	int sameName(String roleName) throws ServiceException;
	void initRole(RoleInstance role,int channelId,int heroId) throws ServiceException; 
	RoleInstance selectByRoleName(String roleName) throws ServiceException ;
	List<RoleInstance> selectAllByUserName(String userName) throws ServiceException;
}
