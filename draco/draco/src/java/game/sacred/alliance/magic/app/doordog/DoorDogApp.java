package sacred.alliance.magic.app.doordog;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.vo.RoleInstance;

public interface DoorDogApp {

	public void roleLogin(RoleInstance role) ;
	
	public void roleLogout(RoleInstance role) ;
	
	/**
	 * 验证角色输入的验证码
	 * @param role
	 * @param anser
	 */
	public void verifyQuestion(RoleInstance role,String anser) ;
	
	/**
	 * 将角色标识为需要输入验证码(GM工具使用)
	 * @param role
	 * @param type 验证类型，-1,随机 0：文本，1：图片，2：声音
	 * @param subType  图片小类型：0：图片验证码,1：图片问题验证码,2:处理客服
	 * @type 验证码类型
	 */
	public boolean flagToVerify(RoleInstance role, byte type) ;
	
	
	public void heartbeat(RoleInstance role);
 	
	/**
	 * 判断玩家同一IP能否登陆
	 * @param role
	 * @return
	 */
	public Result canUserLogin(String ipInfo);
	
	public boolean isWhiteRole(RoleInstance role) ;
	
	public Result reloadBlackIp() ;
	
	public RoleDoorDogInfo getRoleDoorDogInfo(String roleId) ;
}
