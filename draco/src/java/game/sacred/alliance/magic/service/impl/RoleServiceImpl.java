package sacred.alliance.magic.service.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.dao.BaseDAO;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.service.RoleService;
import sacred.alliance.magic.vo.RoleBorn;
import sacred.alliance.magic.vo.RoleBornGuide;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public class RoleServiceImpl implements RoleService {
	private BaseDAO baseDAO;
	private IdFactory<String> idFactory;

	@Override
	public List<RoleInstance> selectAllByUserId(String userId)
			throws ServiceException {
		try {
			return baseDAO.selectList(RoleInstance.class, "userId", userId);
		} catch (Exception e) {
			throw new ServiceException("selectAllByUserId exception", e);
		}
	}

	@Override
	public List<RoleInstance> selectAllByUserName(String userName)
			throws ServiceException {
		try {
			return baseDAO.selectList(RoleInstance.class, "userName", userName);
		} catch (Exception e) {
			throw new ServiceException("selectAllByUserId exception", e);
		}
	}

	@Override
	public RoleInstance selectByRoleId(String roleId) throws ServiceException {
		try {
			return (RoleInstance) baseDAO.selectEntity(RoleInstance.class,
					"roleId", roleId);
		} catch (Exception e) {
			throw new ServiceException("selectByRoleId exception", e);
		}
	}

	@Override
	public RoleInstance selectByRoleName(String roleName)
			throws ServiceException {
		try {
			return (RoleInstance) baseDAO.selectEntity(RoleInstance.class,
					"roleName", roleName);
		} catch (Exception e) {
			throw new ServiceException("selectByRoleName exception", e);
		}
	}

	private String createRoleId(RoleInstance role) throws ServiceException {
		String roleId = null;
		try {
			roleId = idFactory.nextId(IdType.ROLEID);
			role.setRoleId(roleId);
		} catch (Exception e) {
			throw new ServiceException("createRole exception ", e);
		}
		return roleId;
	}

	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}

	@Override
	public int sameName(String roleName) throws ServiceException {
		return baseDAO.count(RoleInstance.class, "roleName", roleName);
	}

	@Override
	public void initRole(RoleInstance role, int channelId,int heroId)
			throws ServiceException {
		try {
			//设置渠道号
			role.setChannelId(channelId);
			Date now = new Date();
			role.setCreateTime(now);
			role.setLastLoginTime(now);
			// TODO:因需要用最后登录时间来做为激活角色的条件，所以在此处去掉设置下线时间。
			// role.setLastOffTime(new Date());
			// 设置出生点
			RoleBorn roleBorn = GameContext.getRoleBornApp().getRoleBorn();
			//向导地图
			RoleBornGuide guide = GameContext.getRoleBornApp().getRoleBornGuide() ;
			if(null == guide){
				role.setMapX(roleBorn.getBornX());
				role.setMapY(roleBorn.getBornY());
				role.setMapId(roleBorn.getBornMapId());
			}else{
				role.setMapX(guide.getMapX());
				role.setMapY(guide.getMapY());
				role.setMapId(guide.getMapId());
			}

			role.setLevel(roleBorn.getLevel());
			this.createRoleId(role);
			// 添加默认物品
			GameContext.getUserGoodsApp().createRoleInitGoods(role);
			//添加英雄
			GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, heroId);
			GameContext.getHeroApp().insertHeroDb(role.getRoleId(), goodsHero);
			//GameContext.getCalctManager().getCalct(role).bornAtrri(role);
			//!!!!!!!!
			//出生的时候不进行属性计算
			//直接将curHP,curMp 设置为0，登录的时候判断2者都是0则将赋值为maxHP,maxMP
			role.setCurHP(0);
			// 角色入库
			baseDAO.insert(role);
		} catch (Exception e) {
			throw new ServiceException("init Role exception", e);
		}

	}

	public void setIdFactory(IdFactory<String> idFactory) {
		this.idFactory = idFactory;
	}
}
