package sacred.alliance.magic.domain;

import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.social.SocialType;
import sacred.alliance.magic.app.social.config.SocialIntimateConfig;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class RoleSocialRelation {
	
	public static final String ROLEID1 = "roleId1";
	public static final String ROLEID2 = "roleId2";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final byte Black_1Shield2 = 1;//角色1屏蔽角色2，2在1的黑名单中
	private static final byte Black_2Shield1 = 2;//角色2屏蔽角色1，1在2的黑名单中
	private static final byte Black_Both_Shield = 3;//双方互相屏蔽，互相在对方的黑名单中
	
	private String roleId1;//角色ID
	private String roleName1;//角色名称
	private byte career1;//职业
	private byte camp1;//阵营
	private byte sex1;//性别
	private String roleId2;
	private String roleName2;
	private byte career2;
	private byte camp2;
	private byte sex2;
	private byte socialType;//社交关系类型
	private byte socialSign;//关系标记
	private int intimate;//亲密度
	
	private boolean modify = false;//是否修改过
	private boolean haveRecords = true;//数据库中是否有记录
	private boolean removeRelation = false;//是否解除关系[既不是好友也不在彼此黑名单中]
	private AtomicBoolean persistentLock = new AtomicBoolean();
	
	/** 是否是好友关系 */
	public boolean isFriend(){
		if(this.removeRelation){
			return false;
		}
		return SocialType.Friend.getType() == this.socialType;
	}
	
	/**
	 * 是否是自己屏蔽了对方
	 * @param roleId 自己的角色ID
	 * @return
	 */
	public boolean isSelfShieldOther(String roleId){
		if(SocialType.Blacklist.getType() != this.socialType){
			return false;
		}
		if(Black_Both_Shield == this.socialSign){
			return true;
		}
		if(this.roleId1.equals(roleId)){
			return Black_1Shield2 == this.socialSign;
		}
		return Black_2Shield1 == this.socialSign;
	}
	
	/**
	 * 判断角色是否被对方屏蔽
	 * @param roleId 自己的角色ID
	 * @return
	 */
	public boolean beShield(String roleId){
		String otherRoleId = this.getOtherRoleId(roleId);
		return this.isSelfShieldOther(otherRoleId);
	}
	
	/** 获取对方的角色ID */
	public String getOtherRoleId(String roleId){
		if(this.roleId1.equals(roleId)){
			return this.roleId2;
		}
		return this.roleId1;
	}
	
	/** 获取角色的名称 */
	public String getRoleName(String roleId){
		if(this.roleId1.equals(roleId)){
			return this.roleName1;
		}
		return this.roleName2;
	}
	
	/** 获取职业 */
	public byte getCareer(String roleId){
		if(this.roleId1.equals(roleId)){
			return this.career1;
		}
		return this.career2;
	}
	
	/** 获取阵营 */
	public byte getCamp(String roleId){
		if(this.roleId1.equals(roleId)){
			return this.camp1;
		}
		return this.camp2;
	}
	
	/** 获取性别 */
	public byte getSex(String roleId){
		if(this.roleId1.equals(roleId)){
			return this.sex1;
		}
		return this.sex2;
	}
	
	/** 变成好友关系 */
	public void becomeFriend(){
		this.socialType = SocialType.Friend.getType();
		this.modify = true;
		this.removeRelation = false;
	}
	
	/**
	 * 创建好友关系
	 * @param role
	 * @param targRole
	 */
	public void createFriend(RoleInstance role, RoleInstance targRole){
		if(null == role || null == targRole){
			return;
		}
		this.createRelation(role, targRole, SocialType.Friend);
	}
	
	/**
	 * 创建黑名单关系
	 * 将对方加入到自己黑名单
	 * @param role
	 * @param targRole
	 */
	public void createBlackList(RoleInstance role, RoleInstance targRole){
		if(null == role || null == targRole){
			return;
		}
		this.createRelation(role, targRole, SocialType.Blacklist);
		this.socialSign = Black_1Shield2;
	}
	
	/**
	 * 创建关系
	 * @param role
	 * @param targRole
	 * @param socialType
	 */
	private void createRelation(RoleInstance role, RoleInstance targRole, SocialType socialType){
		this.socialType = socialType.getType();
		this.roleId1 = role.getRoleId();
		this.roleName1 = role.getRoleName();
		this.career1 = role.getCareer();
		this.camp1 = role.getCampId();
		this.sex1 = role.getSex();
		this.roleId2 = targRole.getRoleId();
		this.roleName2 = targRole.getRoleName();
		this.career2 = targRole.getCareer();
		this.camp2 = targRole.getCampId();
		this.sex2 = targRole.getSex();
		this.modify = true;
		this.haveRecords = false;
	}
	
	/** 解除关系 */
	public void removeRelation(){
		if(SocialType.Friend.getType() == this.socialType){
			//解除好友关系，删除亲密度的值
			this.intimate = 0;
		}else if(SocialType.Blacklist.getType() == this.socialType){
			//解除黑名单关系，删除屏蔽标记的值
			this.socialSign = 0;
		}
		this.socialType = 0;
		this.modify = true;
		this.removeRelation = true;
	}
	
	/**
	 * 屏蔽对方
	 * @param roleId 自己的角色ID
	 */
	public void shieldOther(String roleId){
		//已经是黑名单并且屏蔽对方的，不需要修改
		if(this.isSelfShieldOther(roleId)){
			return;
		}
		//如果对方已经屏蔽了自己，此时自己再屏蔽对方，则变成互相屏蔽
		if(this.beShield(roleId)){
			this.socialSign = Black_Both_Shield;
		}else{
			//非黑名单关系变成黑名单关系
			this.socialType = SocialType.Blacklist.getType();
			if(this.roleId1.equals(roleId)){
				this.socialSign = Black_1Shield2;
			}else{
				this.socialSign = Black_2Shield1;
			}
		}
		this.modify = true;
		this.removeRelation = false;
	}
	
	/**
	 * 修改亲密度
	 * @param intimate
	 */
	public void changeIntimate(int intimate){
		this.intimate = intimate;
		this.modify = true;
		this.removeRelation = false;
	}
	
	/**
	 * 取消屏蔽对方
	 * @param roleId 自己的角色ID
	 */
	public void cancelShield(String roleId){
		if(SocialType.Blacklist.getType() != this.socialType){
			return;
		}
		//对方不在自己的黑名单中
		if(!this.isSelfShieldOther(roleId)){
			return;
		}
		//互相屏蔽的，变成对方屏蔽自己
		if(Black_Both_Shield == this.socialSign){
			if(this.roleId1.equals(roleId)){
				this.socialSign = Black_2Shield1;
			}else{
				this.socialSign = Black_1Shield2;
			}
		}else{
			//自己未在对方的黑名单中，关系解除
			this.socialType = 0;
			this.socialSign = 0;
			this.removeRelation = true;
		}
		this.modify = true;
	}
	
	/** 持久化（操作数据库） */
	public void persistent(){
		if(!this.persistentLock.compareAndSet(false, true)){
			return;
		}
		try{
			//没有修改过
			if(!this.modify){
				return;
			}
			//数据库中没有记录，并且解除社交关系
			if(!this.haveRecords && this.removeRelation){
				return;
			}
			//没有记录，需要写入库
			if(!this.haveRecords){
				GameContext.getSocialDAO().insert(this);
				this.modify = false;
				this.haveRecords = true;
				return;
			}
			//解除关系的，需要从库中删除
			if(this.removeRelation){
				GameContext.getSocialDAO().delete(this.getClass(), ROLEID1, this.roleId1, ROLEID2, this.roleId2);
				this.removeRelation = true;
				return;
			}
			//修改数据库
			GameContext.getSocialDAO().update(this);
			this.modify = false;
		}catch(Exception e){
			this.logger.error("RoleSocialRelation.persistent error: ", e);
		}finally{
			this.persistentLock.set(false);
		}
	}
	
	/**
	 * 获取亲密度所在等级信息
	 * @return
	 */
	public SocialIntimateConfig getIntimateConfig(){
		return GameContext.getSocialApp().getSocialIntimateConfig(this.intimate);
	}
	
	/**
	 * 更新好友记录中，在线玩家的名称、性别、阵营、职业（update时不会入库）
	 * @param role
	 */
	public void updateOnlineRole(RoleInstance role){
		String roleId = role.getRoleId();
		if(this.roleId1.equals(roleId)){
			this.camp1 = role.getCampId();
			this.career1 = role.getCareer();
			this.sex1 = role.getSex();
		}else if(this.roleId2.equals(roleId)){
			this.camp2 = role.getCampId();
			this.career2 = role.getCareer();
			this.sex2 = role.getSex();
		}
	}
	
}
