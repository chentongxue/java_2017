package sacred.alliance.magic.action;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.mina.core.session.IoSession;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.ChangeNameFlag;
import sacred.alliance.magic.channel.mina.MinaChannelSession;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.ScreenConstant;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.domain.RoleLevelup;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.SessionUtil;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.RoleShape;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.horse.config.HorseBase;
import com.game.draco.app.login.UserInfo;
import com.game.draco.app.union.domain.Union;
import com.game.draco.app.union.domain.UnionMember;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.RoleSkillItem;
import com.game.draco.message.request.C0104_RoleEnterReqMessage;
import com.game.draco.message.response.C0104_RoleEnterRespMessage;

public class RoleEnterAction extends BaseAction<C0104_RoleEnterReqMessage> {
	private static List<AttributeType> attriTypeList = new ArrayList<AttributeType>();
	static {
		attriTypeList.add(AttributeType.speed);
		attriTypeList.add(AttributeType.goldMoney);
		attriTypeList.add(AttributeType.bindingGoldMoney);
		attriTypeList.add(AttributeType.silverMoney);
		attriTypeList.add(AttributeType.potential);
		attriTypeList.add(AttributeType.curHP);
		attriTypeList.add(AttributeType.maxHP);
		attriTypeList.add(AttributeType.curMP);
		attriTypeList.add(AttributeType.maxMP);
		attriTypeList.add(AttributeType.level);
		attriTypeList.add(AttributeType.exp);
		attriTypeList.add(AttributeType.maxExp);
		attriTypeList.add(AttributeType.battleScore);
		attriTypeList.add(AttributeType.lq);
		attriTypeList.add(AttributeType.dkp);
	}
	
	@Override
	public Message execute(ActionContext context, C0104_RoleEnterReqMessage req) {
		C0104_RoleEnterRespMessage respMsg = new C0104_RoleEnterRespMessage();
		respMsg.setType(Status.Role_FAILURE.getInnerCode());
		try {
			// !!!!链接已经断开直接返回
			ChannelSession session = context.getSession();
			IoSession ioSession = ((MinaChannelSession) session).getIoSession();
			if (null == ioSession || !ioSession.isConnected()) {
				return respMsg;
			}
			UserInfo userInfo = SessionUtil.getUserInfo(session);
			RoleInstance onlineRole = GameContext.getOnlineCenter()
					.getRoleInstanceByUserId(userInfo.getUserId());
			if (null != onlineRole) {
				// 提示失败
				// 出现这种情况可能是切换角色的时候,前一角色没有入库完毕
				respMsg.setInfo(Status.Role_Is_Offline.getTips());
				return respMsg;
			}
			String roleId = String.valueOf(req.getRoleId());
			RoleInstance role = GameContext.getRoleService().selectByRoleId(
					roleId);
			if (null == role) {
				respMsg.setInfo(Status.Role_Not_Exist.getTips());
				return respMsg;
			}
			if (!role.getUserId().equalsIgnoreCase(userInfo.getUserId())) {
				respMsg.setInfo(Status.Role_Illegal.getTips());
				return respMsg;
			}
			Date nowTime = new Date();
			Date frozenEndTime = role.getFrozenEndTime();
			if (frozenEndTime != null && nowTime.before(frozenEndTime)) {
				String endTime = DateUtil.date2FormatDate(frozenEndTime,
						"yyyy-MM-dd HH:mm");
				respMsg.setInfo(Status.Role_Frozen_End.getTips() + endTime);
				return respMsg;
			}
			//将封停时间设置为空
			role.setFrozenBeginTime(null);
			role.setFrozenEndTime(null);
			role.setFrozenMemo("");
			
			//判断是否需要改名
			ChangeNameFlag flag = GameContext.getUserRoleApp().getChangeNameFlag(role);
			if(ChangeNameFlag.must == flag){
				//必须改名
				respMsg.setInfo(this.getText(TextId.ROLE_MUST_CHANGE_NAME_FOR_LOGIN));
				return respMsg;
			}
			Date lastLoginTime = role.getLastLoginTime();
			//登录逻辑
			GameContext.getUserRoleApp().roleLogin(role,context.getSession());
			//设置角色当前登录ip信息
			InetSocketAddress remoteAddr = (InetSocketAddress)(ioSession.getRemoteAddress());
			if(null != remoteAddr ){
				role.setLoginIp(remoteAddr.getAddress().getHostAddress() + ":" + remoteAddr.getPort());
			}
			
			List<RoleSkillItem> skillItemes = GameContext.getUserSkillApp().getRoleSkillItemList(role);
			respMsg.setSkillItems(skillItemes);

			RoleShape info = GameContext.getUserRoleApp().getRoleShape(roleId);
			respMsg.setEquipResId((short) info.getEquipResId());
			respMsg.setClothesResId((short) info.getClothesResId());
			respMsg.setWingResId((short) info.getWingResId());
			//顺便更新下角色对象上数据
			role.setEquipResId(info.getEquipResId());
			role.setClothesResId(info.getClothesResId());
			role.setWingResId(info.getWingResId());
			
			respMsg.setMapId(role.getMapId());
			respMsg.setMapx((short) role.getMapX());
			respMsg.setMapy((short) role.getMapY());
			respMsg.setMapDir((byte) role.getDir().getType());
			respMsg.setState(role.getState());
			respMsg.setRoleId(role.getIntRoleId());
			respMsg.setRoleName(role.getRoleName());
			respMsg.setSex(role.getSex());
			respMsg.setCamp(role.getCampId());
			
			//设置相关属性
			List<AttriTypeValueItem> attriValueList = new ArrayList<AttriTypeValueItem>();
			for(AttributeType at : attriTypeList){
				AttriTypeValueItem item = new AttriTypeValueItem();
				item.setAttriType(at.getType());
				if(at == AttributeType.maxExp){
					RoleLevelup roleLevelup = GameContext.getAttriApp().getLevelup(role.getLevel());
					item.setAttriValue(roleLevelup.getUpExp());
				}else{
					item.setAttriValue(role.get(at));
				}
				attriValueList.add(item);
			}
			respMsg.setAttriList(attriValueList);

			// 系统设置
			respMsg.setSysSetList(GameContext.getSystemSetApp().getSystemSetList(
					role));

			respMsg.setFactionName("");
			if (role.hasUnion()) {
				Union union = GameContext.getUnionApp().getUnion(role);
				UnionMember member = union.getUnionMember(role.getIntRoleId());
				String color = GameContext.getFactionConfig().getViewColor();
				if (union != null && member != null) {
					respMsg.setFactionName("<" + union.getUnionName() + ">"
							+ member.getPositionNick(member.getPosition()));
					respMsg.setFactionColors((int) Long.parseLong(
							color != null ? color : "ffffffff", 16));
				}
			}
			
			// 设置渠道ID
			role.setRegChannelId(userInfo.getRegChannelId());
			role.setChannelId(userInfo.getLoginChannelId());
			role.setUserRegTime(userInfo.getUserRegTime());
			
			//渠道用户ID，token，刷新token
			role.setChannelUserId(userInfo.getChannelUserId());
			role.setChannelAccessToken(userInfo.getChannelAccessToken());
			role.setChannelRefreshToken(userInfo.getChannelRefreshToken());
			role.setResType(userInfo.getResType());
			role.setOstype(userInfo.getLoginOsType());
			// 统计角色登陆次数
			// 设置同屏幕范围
			short screenWidth = ScreenConstant.SCREEN_WIDTH_MAX;
			short screenHeight = ScreenConstant.SCREEN_HEIGHT_MAX;

			if (ScreenConstant.SCREEN_WIDTH_MAX > req.getScreenWidth()
					&& req.getScreenWidth() > 0) {
				screenWidth = req.getScreenWidth();
			}
			if (ScreenConstant.SCREEN_HEIGHT_MAX > req.getScreenHeight()
					&& req.getScreenHeight() > 0) {
				screenHeight = req.getScreenHeight();
			}
			role.setScreenWidth(screenWidth);
			role.setScreenHeight(screenHeight);

			int time = GameContext.getHeartBeatConfig().getHeartBeat();
			respMsg.setHeartBeatTime((short) time);

			// 获得公共cd
			respMsg.setCommonCd((short) GameContext.getSkillConfig()
					.getRoleSkillCd());

			respMsg.setSpeedUpIntervalTime(GameContext.getHeartBeatConfig()
					.getSpeedUpMsgSendTime());

			// 称号
			respMsg.setTitleItems(Converter.getTitleItems(role));
			
			// 装备特效
			respMsg.setEquipEffects(GameContext.getMedalApp().getRoleMedalEffects(role));

			// vip等级
			respMsg.setVipLevel(GameContext.getVipApp().getVipLevel(role.getRoleId()));
			//女神
			respMsg.setBattleGoddess(GameContext.getGoddessApp().getOnBattleGoddessItem(role));
			//坐骑
			HorseBase horseBase = GameContext.getRoleHorseApp().getHorseResId(role.getIntRoleId());
			if(horseBase != null){
				respMsg.setHorseId(horseBase.getId());
				respMsg.setCurMountResId(horseBase.getImageId());
			}
			
			//PK规则
			respMsg.setColor(role.getColor());
			respMsg.setPkStatus(role.getPkStatus());
			//出战英雄
			RoleHero hero = GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId());
			if(null != hero){
				respMsg.setOnBattleHeroId(hero.getHeroId());
			}
			respMsg.setHeroHeadId(GameContext.getHeroApp().getRoleHeroHeadId(role.getRoleId()));
			// 将角色roleid放入session中
			userInfo.setCurrRoleId(role.getIntRoleId());
			
			// !!!!!!!!!!!!
			// 下面顺序不能随便变化
			// 最后放入在线中心
			// 一定不能忘记
			GameContext.getOnlineCenter().addOnlineUser(role);
			// 角色完全登录成功后,才能进行邮件补偿
			GameContext.getRecoupApp().receiveRecoup(role);
			//看门狗
			GameContext.getDoorDogApp().roleLogin(role);
			// 角色登录日志
			GameContext.getStatLogApp().roleLoginLog(role);
			//玩家回归处理
			try{
				GameContext.getRecallApp().sendRecallAwardMsg(role, lastLoginTime);
			}catch (Exception ex){
				logger.error("", ex);
			}
			// 角色登录日志
			GameContext.getLogApp().loginLog(role);
			respMsg.setType(RespTypeStatus.SUCCESS);
			return respMsg;
		} catch (Exception e) {
			logger.error("", e);
			respMsg.setType((byte) RespTypeStatus.FAILURE);
			respMsg.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return respMsg;
		}
	}

}
