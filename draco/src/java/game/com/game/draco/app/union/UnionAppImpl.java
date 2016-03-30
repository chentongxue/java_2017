package com.game.draco.app.union;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.Setter;

import org.python.google.common.collect.Lists;
import org.python.google.common.collect.Maps;
import org.python.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.map.MapUtil;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.channel.EmptyChannelSession;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.CheckNameUtil;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.ListPage;
import sacred.alliance.magic.util.ListPageDisplay;
import sacred.alliance.magic.util.StringUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.map.MapUnionTerritoryInstance;

import com.game.draco.GameContext;
import com.game.draco.app.buff.Buff;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.dailyplay.DailyPlayType;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.domain.MailAttriBean;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.union.config.UnionActivityInfo;
import com.game.draco.app.union.config.UnionBase;
import com.game.draco.app.union.config.UnionDonate;
import com.game.draco.app.union.config.UnionDpsResult;
import com.game.draco.app.union.config.UnionDropGroup;
import com.game.draco.app.union.config.UnionGemDonate;
import com.game.draco.app.union.config.UnionMail;
import com.game.draco.app.union.config.UnionSummon;
import com.game.draco.app.union.config.UnionUpgrade;
import com.game.draco.app.union.config.instance.UnionActivityConsume;
import com.game.draco.app.union.config.instance.UnionInsBoss;
import com.game.draco.app.union.domain.Union;
import com.game.draco.app.union.domain.UnionActivity;
import com.game.draco.app.union.domain.UnionDkp;
import com.game.draco.app.union.domain.UnionMember;
import com.game.draco.app.union.domain.UnionRecord;
import com.game.draco.app.union.domain.instance.RoleDps;
import com.game.draco.app.union.type.UnionPositionType;
import com.game.draco.app.union.type.UnionPowerType;
import com.game.draco.app.union.type.UnionRecordType;
import com.game.draco.app.union.vo.ChangeActivityResult;
import com.game.draco.message.internal.C0086_UnionAddMemberMessage;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.UnionActivityBossItem;
import com.game.draco.message.item.UnionActivityItem;
import com.game.draco.message.item.UnionDonateItem;
import com.game.draco.message.item.UnionItem;
import com.game.draco.message.item.UnionMemberItem;
import com.game.draco.message.item.UnionRoleDpsItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C1719_UnionRoleHeadShowNotifyMessage;
import com.game.draco.message.push.C1739_UnionApplyJoinNotifyMessage;
import com.game.draco.message.request.C1618_ShopSecretOpenPanelReqMessage;
import com.game.draco.message.request.C2766_UnionSummonReqMessage;
import com.game.draco.message.response.C0204_MapUserEntryNoticeRespMessage;
import com.game.draco.message.response.C1721_UnionSelfPowerListRespMessage;
import com.game.draco.message.response.C1727_UnionDonateInfoRespMessage;
import com.game.draco.message.response.C1728_UnionDonateRespMessage;
import com.game.draco.message.response.C2753_UnionInfoRespMessage;
import com.game.draco.message.response.C2754_UnionActivityListRespMessage;
import com.game.draco.message.response.C2755_UnionBossListRespMessage;
import com.game.draco.message.response.C2757_UnionRoleDpsListRespMessage;

public class UnionAppImpl implements UnionApp{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public final static short HP_RATE_FULL = 10000 ;

	private static final ChannelSession emptyChannelSession = new EmptyChannelSession();
	
	//公会数据<公会ID，公会数据>
	@Getter @Setter
	private Map<String,Union> unionMap = Maps.newConcurrentMap();
	
	//个人dkp数据 <角色ID，dkp>
	@Getter @Setter
	private Map<Integer,UnionDkp> uniondkpMap = Maps.newConcurrentMap();
	
	//公会数据<公会ID，公会数据>
	@Getter @Setter
	private Map<String,Map<Byte,UnionActivity>> activityMap = Maps.newConcurrentMap();
	
	@Override
	public void initUnion() {
		try{
            //加载公会
			this.initUnionEntry();
            //DKP
			List<UnionDkp> unionDkpList = GameContext.getBaseDAO().selectAll(UnionDkp.class);
			if(Util.isEmpty(unionDkpList)){
				for(UnionDkp dkp : unionDkpList){
					if(dkp != null){
						uniondkpMap.put(dkp.getRoleId(), dkp);
					}
				}
			}
			//初始化活动状态
			initActivity();
		}catch(Exception e){
			logger.error("initUnion", e);
		}
	}


    private void initUnionEntry() {
        List<Union> unionList = GameContext.getBaseDAO().selectAll(Union.class);
        if (Util.isEmpty(unionList)) {
            return;
        }

        for (Union union : unionList) {
            if (union == null) {
                continue;
            }
            List<UnionMember> unionMemberList = GameContext.getBaseDAO().selectList(UnionMember.class, "unionId", union.getUnionId());
            if(Util.isEmpty(unionMemberList)){
                //说明是错误数据
                this.delUnionData(union.getUnionId());
                continue;
            }
            unionMap.put(union.getUnionId(), union);
            for (UnionMember member : unionMemberList) {
                if (member != null) {
                    union.getUnionMemberMap().put(member.getRoleId(), member);
                }
            }
            List<UnionRecord> unionRecordList = GameContext.getBaseDAO().selectList(UnionRecord.class, "unionId", union.getUnionId());
            if (Util.isEmpty(unionRecordList)) {
                continue;
            }
            for (UnionRecord record : unionRecordList) {
                if (record != null) {
                    union.getUnionRecordList().add(record);
                }
            }
        }
    }

	
	/**
	 * 初始化活动
	 */
	private void initActivity() {
        try {
            List<UnionActivity> list = GameContext.getBaseDAO().selectAll(UnionActivity.class);
            if (Util.isEmpty(list)) {
                return;
            }
            for (UnionActivity activity : list) {
                Map<Byte,UnionActivity> map = activityMap.get(activity.getUnionId());
                if(null == map){
                    map = Maps.newConcurrentMap();
                    activityMap.put(activity.getUnionId(), map);
                }
                map.put(activity.getActivityId(), activity);
            }
        } catch (Exception e) {
            logger.error("initActivity", e);
        }
    }

    @Override
	public Union getUnion(String unionId) {
		if(unionId == null){
			return null;
		}
		return unionMap.get(unionId);
	}

	@Override
	public Union getUnion(RoleInstance role) {
        if(null == role){
            return null ;
        }
        return Util.fromMap(this.unionMap,role.getUnionId()) ;
	}

	@Override
	public Result checkCondition(RoleInstance role) {
		
		Result result = new Result();
		try{
			UnionBase unionBase = getUnionData().getUnionBase();
			
			if(role.getLevel() < unionBase.getLevel()){
				String str = GameContext.getI18n().messageFormat(TextId.UNION_CREATE_LEVEL_ERR,unionBase.getLevel());
				result.failure();
				return result.setInfo(str);
			}
			
			if(role.hasUnion()){
				result.setInfo(GameContext.getI18n().getText(TextId.UNION_CREATE_FAIL));
				return result;
			}
			
			if(unionBase.getGoodsId() > 0 && unionBase.getGoodsNum() > 0){
				GoodsResult goodsResult = GameContext.getUserGoodsApp().deleteForBag(role, unionBase.getGoodsId(), unionBase.getGoodsNum(), OutputConsumeType.union_create_consume);
				if(!goodsResult.isSuccess()) {
					GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(unionBase.getGoodsId());
					String str = GameContext.getI18n().messageFormat(TextId.UNION_CREATE_GOODS_NOT, unionBase.getGoodsNum(),goodsBase.getName());
					result.failure();
					return result.setInfo(str);
				}
			}
			result.success();
		}catch(Exception e){
			logger.error("checkCreateCondition",e);
		}
		return result;
	}
	
	@Override
	public Result checkCreateUnionCondition(RoleInstance role) {
		
		Result result = new Result();
		try{
			UnionBase unionBase = getUnionData().getUnionBase();
			
			if(role.getLevel() < unionBase.getLevel()){
				String str = GameContext.getI18n().messageFormat(TextId.UNION_CREATE_LEVEL_ERR,unionBase.getLevel());
				result.failure();
				return result.setInfo(str);
			}
			
			UnionDkp uniondkp = uniondkpMap.get(role.getIntRoleId());
			if(uniondkp != null){
				Date roleLeaveTime = new Date(uniondkp.getExitTime());
				UnionBase base = getUnionData().getUnionBase();
				if(null != roleLeaveTime){
					int timeDiff = (int)DateUtil.dateDiffMinute(new Date(), roleLeaveTime);
					int cd = base.getIntervalCd();
					if(timeDiff < cd){
						return result.setInfo(getCdMsg(cd, timeDiff,true));
					}
				}
			}
			
			if(unionBase.getGoodsId() > 0 && unionBase.getGoodsNum() > 0){
                int goodsCount = role.getRoleBackpack().countByGoodsId(unionBase.getGoodsId());
				if(goodsCount < unionBase.getGoodsNum()) {
					GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(unionBase.getGoodsId());
					String str = GameContext.getI18n().messageFormat(TextId.UNION_CREATE_GOODS_NOT, unionBase.getGoodsNum(),goodsBase.getName());
					result.failure();
					return result.setInfo(str);
				}
			}
			result.success();
			result.setInfo(GameContext.getI18n().getText(TextId.UNION_CREATE_INFO));
		}catch(Exception e){
			logger.error("checkCreateUnionCondition",e);
		}
		return result;
	}

	@Override
	public Result createUnion(RoleInstance role, String unionName,	String unionDesc) throws ServiceException {
		Result result = new Result();
		String unionId = null ;
		try {
			if(StringUtil.nullOrEmpty(unionName)){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NAME_NULL));
			}
			unionName = unionName.trim();
			unionName = unionName.replace(" ","");
			unionName = StringUtil.replaceNewLine(unionName);

			
			//判断是否有@#号等特殊字符
			if(StringUtil.haveSpecialChar(unionName)){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NAME_ILLEGAL));
			}
			
			if(StringUtil.stringFilter(unionName)){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NAME_ILLEGAL));
			}
			
			// 判断是否有以s/S+数字以尾
			if(CheckNameUtil.isMatchChangeName(unionName)){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NAME_ILLEGAL));
			}
			
			//过滤敏感词
			String illegalChar = GameContext.getIllegalWordsService().findIllegalChar(unionName);
			if(illegalChar != null){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NAME_ILLEGAL));
			}
			
			//过滤禁用词
			String forbidChar = GameContext.getIllegalWordsService().findForbiddenChar(unionName);
			if(forbidChar != null){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NAME_FORBIDDEN));
			}
			
			boolean nameFlag = validateStrByLength(unionName,GameContext.getFactionConfig().getFactionNameLength());
			if(!nameFlag){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NAME_TOOLLONGER));
			}
			
			if(StringUtil.haveSpecialChar(unionDesc)){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_DES_FORBIDDEN));
			}
			
			//过滤、替换逗号
			unionDesc = GameContext.getIllegalWordsService().doFilter(unionDesc);
			unionDesc = Util.replaceComma(unionDesc);
			if(unionDesc.indexOf("*") != -1){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_DES_FORBIDDEN));
			}
			boolean descFlag = validateStrByLength(unionDesc,GameContext.getFactionConfig().getFactionDescLength());
			if(!descFlag){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_DES_TOO_LENGTH));
			}
			if(Util.isEmpty(unionDesc)) {
				unionDesc = GameContext.getI18n().getText(TextId.UNION_DES_DEFAULT);
			}

            if(searchUnionByName(unionName) != null){
                return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NAME_EXIST));
            }
			
			result = checkCondition(role);
			if(!result.isSuccess()){
				return result;
			}
			
			//创建门派对象
			unionId = IdFactory.getInstance().nextId(IdType.UNION);
			Union union = new Union();
			union.setUnionId(unionId);
			union.setUnionName(unionName);
			union.setUnionLevel((byte)1);
			union.setLeaderId(role.getIntRoleId());
			union.setLeaderName(role.getRoleName());
			union.setCreateTime(System.currentTimeMillis());
			union.setUnionDesc(unionDesc);
			//门派信息入库，更新门派缓存数据
            GameContext.getBaseDAO().insert(union);

			//创建人信息入库
			int roleId = role.getIntRoleId();
			UnionMember member = this.createMember(union.getUnionId(), role, UnionPositionType.Leader.getType());
			union.saveUnionMember(member);
            GameContext.getBaseDAO().insert(member) ;

            //最后更新内存
            //更新角色信息,（先不更新数据库）
            role.setUnionId(union.getUnionId());
            unionMap.put(union.getUnionId(), union);
			
			role.getBehavior().notifyAttribute();
			//通知角色头顶显示
			notifyUnionRoleHeadShowChange(roleId);
			
			//主推权限列表
			this.notifyUnionRolePosition(role);
			result.success();
		} catch (Exception e) {
            if(null != unionId){
                unionMap.remove(unionId) ;
            }
			this.logger.error("createUnion", e);
            throw new ServiceException("createUnion error",e);
		}
		return result;
	}
	
	public boolean validateStrByLength(String strParameter , int limitLength){
		if(length(strParameter) > limitLength){
			return false;
		}
		return true;
	}
	
	/**
	 * 判断字符长度
	 * @param value
	 * @return
	 */
	private int length(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }
	
	/** 通知角色头顶显示门派名称和职位 */
	private void notifyUnionRoleHeadShowChange(int roleId){
		try{
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
			if(null == role){
				return;
			}
			//获取不到地图实例
			MapInstance mapInstance = role.getMapInstance();
			if(null == mapInstance){
				return;
			}
			sendUnionNotify(role);
		}catch(Exception e){
			this.logger.error("notifyUnionRoleHeadShowChange", e);
		}
	}
	
	/**
	 * 发送广播通知头顶标识
	 * @param role
	 */
	private void sendUnionNotify(RoleInstance role){
		try{
			//获取不到地图实例
			MapInstance mapInstance = role.getMapInstance();
			if(null == mapInstance){
				return;
			}
			Union union = getUnion(role);
			if(union != null){
				UnionMember member = union.getUnionMember(role.getIntRoleId());
				C1719_UnionRoleHeadShowNotifyMessage message = new C1719_UnionRoleHeadShowNotifyMessage();
				message.setRoleId(role.getIntRoleId());
				message.setUnionName("<" + union.getUnionName()+ ">"+ member.getPositionNick(member.getPosition()));
				String color = GameContext.getFactionConfig().getViewColor();
				message.setUnionColor((int) Long.parseLong(color != null ? color : "ffffffff", 16));
				mapInstance.broadcastMap(null, message);
			}
		}catch(Exception e){
			logger.error("sendUnionNotify",e);
		}
	}
	
	
	/** 离开门派的时候，通知地图内角色头顶显示 */
	private void notifyUnionRoleHeadShowClear(RoleInstance role){
		try {
			MapInstance mapInstance = role.getMapInstance();
			if(null == mapInstance){
				return;
			}
			C1719_UnionRoleHeadShowNotifyMessage message = new C1719_UnionRoleHeadShowNotifyMessage();
			message.setRoleId(role.getIntRoleId());
			mapInstance.broadcastMap(null, message);
		} catch (Exception e) {
			this.logger.error("FactionApp.notifyFactionRoleHeadShowClear error: ", e);
		}
	}

	@Override
	public ListPageDisplay<Union> getUnionList(int currPage, int size) {
		List<Union> list = Lists.newArrayList();
		if(!Util.isEmpty(unionMap)){
			list.addAll(unionMap.values());
			sortUnion(list);
		}
		ListPage<Union> listPage = new ListPage<Union>(list,size);
		return listPage.getObjectsDsiplay(currPage);
	}
	
	@Override
	public List<String> getUnionIdList(int size){
		List<Union> list = Lists.newArrayList();
		if(!Util.isEmpty(unionMap)){
			list.addAll(unionMap.values());
			sortUnion(list);
		}
		List<String> unionIdList = Lists.newArrayList();
		for(Union union : list){
			unionIdList.add(union.getUnionId());
		}
		if(Util.isEmpty(unionIdList)){
			return unionIdList;
		}
		if(unionIdList.size() < size){
			size = unionIdList.size();
		}
		return unionIdList.subList(0, size);
	}
	
	@Override
	public void sortUnion(List<Union> list){
		Collections.sort(list, new Comparator<Union>() {
			public int compare(Union info1, Union info2) {
				if(info1.getUnionLevel() > info2.getUnionLevel()) {
					return -1;
				}
				if(info1.getUnionLevel() < info2.getUnionLevel()) {
					return 1;
				}
				if(info1.getPopularity() > info2.getPopularity()) {
					return -1;
				}
				if(info1.getPopularity() < info2.getPopularity()) {
					return 1;
				}
				return 0;
			}
		});
	}
	
	@Override
	public Result applyJoinUnion(RoleInstance role, String unionId) {
		Result result = new Result();
		try{
			//TODO: 15 不要写死在此处
			if(role.getLevel() < 15){
				return result.setInfo(GameContext.getI18n().messageFormat(TextId.UNION_APPLY_LEVEL_ERR));
			}
			if(role.hasUnion()){
				result.setInfo(GameContext.getI18n().getText(TextId.UNION_APPLY_IS_NOT_NULL));
				return result;
			}
			
			UnionDkp uniondkp = uniondkpMap.get(role.getIntRoleId());
			if(uniondkp != null){
				Date roleLeaveTime = new Date(uniondkp.getExitTime());
				UnionBase base = getUnionData().getUnionBase();
				if(null != roleLeaveTime){
					int timeDiff = (int)DateUtil.dateDiffMinute(new Date(), roleLeaveTime);
					int cd = base.getIntervalCd();
					if(timeDiff < cd){
						return result.setInfo(getCdMsg(cd, timeDiff,true));
					}
				}
			}
			
			Union union = getUnion(unionId);
			if(union == null ){
				return result.setInfo(GameContext.getI18n().messageFormat(TextId.UNION_NULL));
			}
			if(isFull(unionId)){
				return result.setInfo(GameContext.getI18n().messageFormat(TextId.UNION_MEMBER_MAX));
			}
			
			UnionMember member = this.createMember(union.getUnionId(), role, UnionPositionType.Member.getType());
			
			if(uniondkp != null){
				if(uniondkp.getUnionId().equals(unionId)){
					member.setDkp(uniondkp.getDkp());
					changeMemberDkp(role,uniondkp.getDkp(),OperatorType.Equal,FunType.recoUnion,false);
					member.setOfflineTime(0);
				}else{
					member.setOfflineTime(uniondkp.getExitTime());
				}
			}
			
			union.getApplyMap().put(role.getIntRoleId(), member);
			//给有权限的在线成员发送入会申请
			sendApplyMessage(unionId, role);
			result.success();
		}catch(Exception e){
			logger.error("applyJoinUnion",e);
		}
		return result;
	}
	
	/**
	 * 给有权限的在线成员发送入会申请
	 * @param unionId
	 * @return
	 */
	private List<UnionMember> sendApplyMessage(String unionId, RoleInstance applyRole){
		List<UnionMember> frList = Lists.newArrayList();
		try{
			Union union = getUnion(unionId);
			if(Util.isEmpty(union.getApplyMap())){
				return frList;
			}
			C1739_UnionApplyJoinNotifyMessage message = null;
			for(Entry<Integer,UnionMember> member: union.getUnionMemberMap().entrySet()) {
				String roleId = String.valueOf(member.getKey());
				RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
				if(null == role){
					continue;
				}
				
				if(!union.getPowerTypeSet(role).contains(UnionPowerType.Dispose_Apply_Join)){
					continue;
				}
				
				if(null == message) {
					message = new C1739_UnionApplyJoinNotifyMessage();
					message.setRoleId(applyRole.getIntRoleId());
					message.setRoleName(applyRole.getRoleName());
				}
				role.getBehavior().sendMessage(message);
			}
		}catch(Exception e){
			logger.error("sendApplyMessage",e);
		}
		return frList;
	}
	
	/**
	 * 判断公会人数是否已满
	 * @param unionId
	 * @return
	 */
	private boolean isFull(String unionId) {
		try {
			Union union = getUnion(unionId);
			if (null == union) {
				return true;
			}
			UnionUpgrade upgrade = getUnionData().getUnionUpgrade(
					union.getUnionLevel());
			if(null == upgrade){
				return true ;
			}
			return union.getUnionMemberMap().size() >= upgrade
					.getMaxMemberNum() ;
		} catch (Exception e) {
			logger.error("isFull", e);
		}
		return true;
	}

	private UnionDataApp getUnionData(){
		return GameContext.getUnionDataApp();
	}
	
	@Override
	public List<UnionMember> getApplyJoinList(String unionId) {
		List<UnionMember> list = Lists.newArrayList();
		try {
			Union union = getUnion(unionId);
			if (null == union) {
				return list;
			}
			Map<Integer, UnionMember> applyMap = union.getApplyMap();
			if (Util.isEmpty(applyMap)) {
				return list;
			}

			for (Entry<Integer, UnionMember> apply : applyMap.entrySet()) {
				if (null == apply.getValue()) {
					continue;
				}
				RoleInstance role = GameContext.getOnlineCenter()
						.getRoleInstanceByRoleId(
								String.valueOf(apply.getValue().getRoleId()));
				UnionMember m = apply.getValue();
				if (null != role) {
					m.setLevel(role.getLevel());
				}
				list.add(m);
			}
		} catch (Exception e) {
			logger.error("getApplyJoinList", e);
		}
		return list;
	}
	
	@Override
	public Result acceptApplyJoin(RoleInstance leader, int roleId) throws ServiceException{
		Result result = new Result();
		try {
			Union union = this.getUnion(leader);
			if(null == union){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NOT));
			}
			
			if(!union.getPowerTypeSet(leader).contains(UnionPowerType.Dispose_Apply_Join)){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NO_POSITION));
			}
			
			if(isFull(union.getUnionId())){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_MEMBER_MAX));
			}
			
			UnionMember applyMember = getApplyJoin(union.getUnionId(),roleId);
			if(applyMember == null){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_OPERATED));
			}
			
			C0086_UnionAddMemberMessage reqMsg = new C0086_UnionAddMemberMessage();
			reqMsg.setRoleId(roleId);
			reqMsg.setOperaRoleId(leader.getIntRoleId());
			GameContext.getUserSocketChannelEventPublisher().publish(applyMember.getUserId(), 
					reqMsg, emptyChannelSession, true);
			
			result.success();
		} catch (RuntimeException e) {
			this.logger.error("acceptApplyJoin", e);
		}
		return result;
	}
	
	/**
	 * 加入公会
	 * @param roleId
	 * @param operRole
	 * @return
	 */
	@Override
	public Result joinUnion(int roleId,RoleInstance operRole){
		Result result = new Result();
		try{
			RoleInstance role = getRoleInstance(roleId);
			if(null == role) {
				return null;
			}
			
			if(role.hasUnion()){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_TARGET_ERR));
			}
			
			Union union = getUnion(operRole);
			
			if(!union.getPowerTypeSet(operRole).contains(UnionPowerType.Dispose_Apply_Join)){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NO_POSITION));
			}
			
			UnionMember newMember = this.createMember(union.getUnionId(), role, UnionPositionType.Member.getType());
		
			result = addUnionRole(role, union, newMember);
			saveOrUpdDkp(role,true);
			if(!result.isSuccess()) {
				return result;
			}
			
			//加入公会记录
			UnionRecord record = new UnionRecord();
			record.setType(UnionRecordType.Union_Record_Role_Join.getType());
			record.setUnionId(union.getUnionId());
			record.setData2(role.getRoleName());
			saveOrUpdUnionRecord(union,record);
		
		}catch(Exception e){
			logger.error("joinUnion",e);
		}
		return result;
	}
	
	/**
	 * 邀请同意加入公会
	 * @param roleId
	 * @param operRole
	 * @return
	 */
	@Override
	public Result inviteJoinUnion(int roleId,RoleInstance operRole){
		Result result = new Result();
		RoleInstance role = getRoleInstance(roleId);
		
		if(operRole.hasUnion()){
			return result.setInfo(GameContext.getI18n().getText(TextId.UNION_APPLY_IS_NOT_NULL));
		}
		
		Union union = getUnion(role);
		
		UnionMember inviteMember = this.createMember(union.getUnionId(), operRole, UnionPositionType.Member.getType());
		
		try{
			result = addUnionRole(operRole, union, inviteMember);
			if(!result.isSuccess()) {
				return result;
			}
			saveOrUpdDkp(operRole,true);
			//加入公会记录
			UnionRecord record = new UnionRecord();
			record.setType(UnionRecordType.Union_Record_Role_Join.getType());
			record.setUnionId(union.getUnionId());
			record.setData2(operRole.getRoleName());
			saveOrUpdUnionRecord(union,record);
		}catch(Exception e){
			logger.error("inviteJoinUnion",e);
		}
		return result;
	}
	
	/**
	 * 添加公会成员
	 */
	public Result addUnionRole(RoleInstance role, Union union,UnionMember member) throws ServiceException{
		Result result = new Result();
		try {
			String unionId = union.getUnionId();
			
			UnionDkp uniondkp = uniondkpMap.get(role.getIntRoleId());
			if(uniondkp != null){
				Date roleLeaveTime = new Date(uniondkp.getExitTime());
				if(null != roleLeaveTime){
					int timeDiff = (int)DateUtil.dateDiffMinute(new Date(), roleLeaveTime);
					int cd = getUnionData().getUnionBase().getIntervalCd();
					if(timeDiff < cd){
						return result.setInfo(getCdMsg(cd, timeDiff,false));
					}
				}
			}
			if(isFull(unionId)){
				return result.setInfo(GameContext.getI18n().messageFormat(TextId.UNION_NULL));
			}
			
			member.setLevel(role.getLevel());
			member.setCreateTime(System.currentTimeMillis());
			member.setPosition(UnionPositionType.Member.getType());
			saveOrUpdUnionMember(member);
			//门派ID
			role.setUnionId(unionId);
			notifyUnionRoleHeadShowChange(role.getIntRoleId());
            //不要更新role
			//GameContext.getBaseDAO().update(role);
			//在系统频道给新成员发消息
			String selfMsg = GameContext.getI18n().messageFormat(TextId.UNION_ADD_ROLE_MSG, union.getUnionName());
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.System, selfMsg, null, role);
			//门派频道内广播有新成员加入
			String unionMsg = role.getRoleName() + GameContext.getI18n().getText(TextId.UNION_ADD_MSG);
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Union, unionMsg, null, union);
			
			result.success();
		} catch (RuntimeException e) {
			logger.error("addUnionRole error: ", e);
		}
		return result;
	}
	
	private String getCdMsg(int cd,int timeDiff,boolean flag){
		String cdDuring = DateUtil.formatDuring(cd - timeDiff);
		if(flag){
			return GameContext.getI18n().messageFormat(TextId.UNION_APPLY_CD_NOT, cdDuring);
		}else{
			return GameContext.getI18n().messageFormat(TextId.UNION_TARGET_APPLY_CD_NOT, cdDuring);
		}
	}
	
	/**
	 * 删除申请加入门派的角色信息
	 * */
	private UnionMember getApplyJoin(String unionId, int roleId){
		Union union = getUnion(unionId);
		Map<Integer, UnionMember> applyMap = union.getApplyMap();
		if(null == applyMap || applyMap.isEmpty()){
			return null;
		}
		if(!applyMap.containsKey(roleId)){
			return null;
		}
		return applyMap.remove(roleId);
	}
	
	@Override
	public Result refuseApplyJoin(RoleInstance leader, int roleId) {
		Result result = new Result();
		Union union = getUnion(leader);
		if(null == union){
			return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NULL));
		}
		if(!union.getPowerTypeSet(leader).contains(UnionPowerType.Dispose_Apply_Join)){
			return result.setInfo(GameContext.getI18n().getText(TextId.Faction_Role_No_Position));
		}
		UnionMember unionMember = getApplyJoin(union.getUnionId(), roleId);
		if(unionMember == null){
			return result.setInfo(GameContext.getI18n().getText(TextId.UNION_APPLY_OVER));
		}
		//给申请人发邮件提示
		String title = GameContext.getI18n().getText(TextId.UNION_REFUSE_APPLY);
		String context = GameContext.getI18n().messageFormat(TextId.UNION_REFUSE_APPLY_CONTENT, union.getUnionName());
		this.sendMail(roleId, title, context);
		//在线则发浮动提示
		GameContext.getMessageCenter().sendByRoleId(null, String.valueOf(roleId), new C0003_TipNotifyMessage(context));
		return result.success();
	}
	
	/** 
	 * 发邮件通知目标角色
	 * @param roleId 角色ID
	 * @param title 邮件标题
	 * @param context 邮件内容
	 */
	private void sendMail(int roleId, String title, String context){
		try{
			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
			mail.setTitle(title);
			mail.setSendRole(MailSendRoleType.Union.getName());
			mail.setContent(context);
			mail.setRoleId(String.valueOf(roleId));
			GameContext.getMailApp().sendMail(mail); 
		}catch(Exception e){
			this.logger.error("sendMail", e);
		}
	}

	@Override
	public Map<Integer, UnionMember> getUnionMemberMap(String unionId) {
		Union union = getUnion(unionId);
		return union.getUnionMemberMap();
	}

	@Override
	public List<UnionMember> getUnionMemberList(String unionId) {
		List<UnionMember> frList = Lists.newArrayList();
		Union union = getUnion(unionId);
		Map<Integer,UnionMember> frMap = union.getUnionMemberMap();
		if(Util.isEmpty(frMap)){
			return frList;
		}
		frList.addAll(frMap.values());
		sortUnionRoleByPosition(frList);
		return frList;
	}
	

	private void sortUnionRoleByPosition(List<UnionMember> frList){
		Collections.sort(frList, new Comparator<UnionMember>() {
			public int compare(UnionMember fr1, UnionMember fr2) {
				if(fr1.getPosition() < fr2.getPosition()) {
					return -1;
				}
				if(fr1.getPosition() > fr2.getPosition()) {
					return 1;
				}
				return 0;
			}
		});
	}

	@Override
	public Collection<RoleInstance> getAllOnlineUnionMember(Union union) {
		List<RoleInstance> onlineRoleList = Lists.newArrayList();
		if(null == union){
			return onlineRoleList;
		}
		Map<Integer,UnionMember> unionRoleMap = union.getUnionMemberMap();
		if(Util.isEmpty(unionRoleMap)){
			return onlineRoleList;
		}
		for(Entry<Integer,UnionMember> member : unionRoleMap.entrySet()){
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(member.getKey()));
			if(null == role){
				continue;
			}
			onlineRoleList.add(role);
		}
		return onlineRoleList;
	}

	@Override
	public Result exitUnion(RoleInstance role) throws ServiceException {
		Result result = new Result();
		try{
			Union union = getUnion(role);
			if(union == null){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NULL));
			}
			UnionMember member = union.getUnionMember(role.getIntRoleId());
			//如果是会长并且有其他门派成员时，不可退出门派
			UnionPositionType position = member.getPositionType();
			boolean isPresident = UnionPositionType.Leader == position;
			if(isPresident && union.getUnionMemberMap().size() > 1){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_EXIT_HASMEMBER));
			}
			
			boolean isDelUnion = false;
			//如果是门派最后一个人，同时删除门派信息
			if(1 == union.getUnionMemberMap().size()){
				isDelUnion = true;
			}
			//更新角色dkp
			saveOrUpdDkp(role,false);
			role.setUnionId(null);
			changeMemberDkp(role,0,OperatorType.Equal,FunType.exitUnion,false);
			//不要更新role
			//GameContext.getBaseDAO().update(role);
			union.deleteUnionMember(role.getIntRoleId());

			delUnionData(union.getUnionId(),role.getIntRoleId(),isDelUnion);
			
			//同步门派总贡献
			this.notifyUnionRoleHeadShowClear(role);

			//帮众叛离时，给会长发邮件提示
			String unionName = union.getUnionName();
			String roleId = role.getRoleId();
			String exitText = TextId.UNION_EXIT_ROLE_MSG;
			if(!isPresident){
				String title = GameContext.getI18n().getText(TextId.UNION_EXIT_MAIL_TITLE);
				String context = GameContext.getI18n().messageFormat(TextId.UNION_EXIT_MAIL_CONTENT, role.getRoleName(), unionName);
				this.sendMail(union.getLeaderId(), title, context);
				GameContext.getMessageCenter().sendByRoleId(null, roleId, new C0003_TipNotifyMessage(context));
			}else{
				exitText = TextId.UNION_DISS_MSG;
			}
			C0003_TipNotifyMessage message = new C0003_TipNotifyMessage();
			message.setMsgContext(GameContext.getI18n().messageFormat(exitText, unionName));
			GameContext.getMessageCenter().sendByRoleId(null, roleId, message);

            if(!isDelUnion){
                //离开公会记录
                UnionRecord record = new UnionRecord();
                record.setType(UnionRecordType.Union_Record_Role_Leave.getType());
                record.setUnionId(union.getUnionId());
                record.setData2(role.getRoleName());
                saveOrUpdUnionRecord(union,record);
            }

			String str = GameContext.getI18n().messageFormat(TextId.UNION_EXIT, role.getRoleName());
			this.sendUnionMessage(roleId, union, str);
			return result.success();
			
		} catch(Exception e){
			logger.error("exitUnion error:" + e);
			throw new ServiceException("exitUnion exception",e);
		}
	}
	
	/**
	 * 更新角色Dkp
	 * @param role
	 */
	public void saveOrUpdDkp(RoleInstance role,boolean flag){
		try{
			Union union = getUnion(role);
			UnionDkp dkp = new UnionDkp();
			//是否归0
			if(!flag){
				dkp.setDkp(union.getUnionMember(role.getIntRoleId()).getDkp());
			}
			dkp.setRoleId(role.getIntRoleId());
			dkp.setUnionId(union.getUnionId());
			dkp.setExitTime(System.currentTimeMillis());
			uniondkpMap.put(role.getIntRoleId(),dkp);
			GameContext.getBaseDAO().saveOrUpdate(dkp);
			
		}catch(Exception e){
			logger.error("saveOrUpdDkp",e);
		}
	}
	
	/**
	 * 广播
	 * @param roleId
	 * @param faction
	 * @param text
	 */
	private void sendUnionMessage(String roleId, Union union, String text){
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
		if(null != role){
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.System, text, null, role);
		}
		//门派频道内广播
		GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Union, text, null, union);
	}
	
	@Override
	public Result removeUnionMember(RoleInstance leader, int roleId)
			throws ServiceException {
		try{
			Union union = getUnion(leader);
			Result result = new Result();
			//不能踢出自己
			if(leader.getIntRoleId() == roleId){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_REMOVE_SELF));
			}
			if(!union.getPowerTypeSet(leader).contains(UnionPowerType.Remove_Member)){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_KICK_POSITION_NOT_HAS));
			}
			
			UnionMember leaderMember = union.getUnionMember(leader.getIntRoleId());
			UnionMember member = union.getUnionMember(roleId);
			Result operateResult = UnionPositionType.isGreaterThan(leaderMember.getPositionType(), member.getPositionType());
			if(!operateResult.isSuccess()){
				return operateResult;
			}
			
			//更新角色dkp
			RoleInstance targetRole = getRoleInstance(roleId);
			saveOrUpdDkp(targetRole,false);
			targetRole.setUnionId(null);
            //role上的unionId没有在role表中
			//GameContext.getBaseDAO().update(targetRole);
			
			//删成员
			delUnionData(union.getUnionId(),roleId,false);
			union.deleteUnionMember(roleId);
			
			//给被驱逐的帮众发邮件提示
			String title = GameContext.getI18n().getText(TextId.FACTION_KICK_MAIL_TITLE);
			String unionName = union.getUnionName();
			String context = GameContext.getI18n().messageFormat(TextId.FACTION_KICK_MAIL_CONTENT, unionName);
			this.sendMail(roleId, title, context);
			GameContext.getMessageCenter().sendByRoleId(null, String.valueOf(roleId), new C0003_TipNotifyMessage(context));
			
			//离开公会记录
			UnionRecord record = new UnionRecord();
			record.setType(UnionRecordType.Union_Record_Role_Kick.getType());
			record.setUnionId(union.getUnionId());
			record.setData2(targetRole.getRoleName());
			record.setData3(leader.getRoleName());
			saveOrUpdUnionRecord(union,record);
			
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
			if(null != role) {
				//主推权限列表
				this.notifyUnionRolePosition(role);
			}
			
			String str = GameContext.getI18n().messageFormat(TextId.FACTION_REMOVE, targetRole.getRoleName());
			this.sendUnionMessage(targetRole.getRoleId(), union, str);
			notifyUnionRoleHeadShowClear(targetRole);
			return result.success();
		} catch(Exception e){
			logger.error("removeUnionMember error:" + e);
			throw new ServiceException("removeUnionMember exception", e);
		}
	}
	
	/**
	 * 获取角色信息，若角色不在线则查库
	 * @param roleId
	 * @return
	 */
	private RoleInstance getRoleInstance(int roleId){
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
		if(null == role){
			role = GameContext.getBaseDAO().selectEntity(RoleInstance.class, "roleId", roleId);
		}
		UnionMember member = GameContext.getBaseDAO().selectEntity(UnionMember.class, "roleId", role.getIntRoleId());
		if(role != null && member != null){
			role.setUnionId(member.getUnionId());
		}
		return role;
	}

	@Override
	public UnionMember searchUnionMember(String unionId, int roleId) {
		if(unionId != null){
			Union union = getUnion(unionId);
			return union.getUnionMember(roleId);
		}
		return null;
	}

	@Override
	public UnionMember getUnionMember(String unionId,int roleId) {
		if(unionId != null){
			Union union = getUnion(unionId);
			if(union != null){
				return union.getUnionMember(roleId);
			}
		}
		return null;
	}

	@Override
	public Result demisePresident(RoleInstance leader, int roleId)
			throws ServiceException {
		try {
			Result result = new Result();
			
			if(!this.getPowerTypeSet(leader).contains(UnionPowerType.UnionDemise)){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_MODIFY_DEMISE_POSITION_NOT_HAS));
			}
			
			Union union = getUnion(leader);
			if(union == null){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NOT));
			}
			
			RoleInstance role = this.getRoleInstance(roleId);
			if(role == null){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_DEMISE_ERROR));
			}
			
			//验证角色是否是这个公会的会长
			int leaderId = leader.getIntRoleId();
			UnionMember oldLeader = union.getUnionMember(leaderId);
			
			if(union.getLeaderId() != leaderId ||
					UnionPositionType.Leader != UnionPositionType.getPosition(oldLeader.getPosition())){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_DEMISE_NOT_LEADER));
			}
			
			String roleUnionId = role.getUnionId();
			if(Util.isEmpty(roleUnionId)){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_MEMBER_NOT_EXIST));
			}
			
			if(!roleUnionId.equals(union.getUnionId())){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_MEMBER_NOT_EXIST));
			}
			
			UnionMember newLeader = union.getUnionMember(roleId);
			if(null == newLeader){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_FAILURE));
			}
			//修改新会长的职位
			newLeader.setPosition(UnionPositionType.Leader.getType());
			//修改老会长的职位
			oldLeader.setPosition(UnionPositionType.Member.getType());
			//设置新会长
			String roleName = role.getRoleName();
			
			union.setLeaderId(roleId);
			
			union.setLeaderName(roleName);
			
			changePosition(union.getUnionId(),UnionPositionType.Member,oldLeader.getRoleId(),false);

			changePosition(union.getUnionId(),UnionPositionType.Leader,roleId,true);
			
			//禅让成功，门派频道发消息
			String oldLeaderName = leader.getRoleName();
			String info = GameContext.getI18n().messageFormat(TextId.UNION_DEMISE_PRESIDENT_MSG, oldLeaderName, roleName);
			GameContext.getChatApp().sendSysMessage(ChatSysName.Union, ChannelType.Union, info, null, union);
			//给新会长浮动提示
			boolean isOnline = GameContext.getOnlineCenter().isOnlineByRoleId(String.valueOf(roleId));
			String tips = GameContext.getI18n().messageFormat(TextId.UNION_DEMISE_PRESIDENT_MAIL_MSG,oldLeaderName ,union.getUnionName());
			if(isOnline) {
				GameContext.getMessageCenter().sendByRoleId(null, String.valueOf(roleId), new C0003_TipNotifyMessage(tips));
				//主推权限列表
				this.notifyUnionRolePosition(role);
			}else{
				this.sendMail(roleId, GameContext.getI18n().getText(TextId.UNION_DEMISE_PRESIDENT_MAIL_TITLE), tips);
			}
			//主推原会长的权限
			this.notifyUnionRolePosition(leader);
			return result.success();
		} catch (RuntimeException e) {
			this.logger.error("FactionApp.demisePresident error: ", e);
			throw new ServiceException("FactionAppImpl.demisePresident exception", e);
		}
	}
	
	/**
	 * 主推权限变化
	 * @param role
	 */
	private void notifyUnionRolePosition(RoleInstance role){
		try{
			C1721_UnionSelfPowerListRespMessage message = new C1721_UnionSelfPowerListRespMessage();
			int power = this.getUnionPosition(role);
			message.setPower(power);
			role.getBehavior().sendMessage(message);
		}catch(Exception e){
			this.logger.error("FactionApp.notifyFactionRoleHeadShowChange error: ", e);
		}
	}
	
	@Override
	public Result inviteJoinUnionValid(RoleInstance leader, RoleInstance role) {
		Result result = new Result();
		
		UnionDkp uniondkp = uniondkpMap.get(role.getIntRoleId());
		if(uniondkp != null){
			Date roleLeaveTime = new Date(uniondkp.getExitTime());
			if(null != roleLeaveTime){
				int timeDiff = (int)DateUtil.dateDiffMinute(new Date(), roleLeaveTime);
				int cd = getUnionData().getUnionBase().getIntervalCd();
				if(timeDiff < cd){
					return result.setInfo(getCdMsg(cd, timeDiff,false));
				}
			}
		}
		
		if(!getPowerTypeSet(leader).contains(UnionPowerType.Invite_To_Join)){
			return result.setInfo(GameContext.getI18n().getText(TextId.UNION_POSITION_NOT_HAS));
		}
	
		Union union = getUnion(leader);
		if(isFull(union.getUnionId())){
			return result.setInfo(GameContext.getI18n().getText(TextId.UNION_MEMBER_MAX));
		}
		
		if(role.hasUnion()){
			if(role.getUnionId().equals(leader.getUnionId())){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_ROLE_EXIST));
			}
			return result.setInfo(GameContext.getI18n().getText(TextId.UNION_TARGET_OWN));
		}
		return result.success();
	}

	@Override
	public Result modifyUnionDesc(RoleInstance role, String desc) {
		Result result = new Result();
		Union union = getUnion(role);
		if(union == null){
			return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NOT));
		}
		if(!this.getPowerTypeSet(role).contains(UnionPowerType.Modify_Desc)){
			return result.setInfo(GameContext.getI18n().getText(TextId.UNION_MODIFY_DESC_POSITION_NOT_HAS));
		}
		String unionDesc = GameContext.getIllegalWordsService().doFilter(desc.trim());
		
		unionDesc = Util.replaceComma(unionDesc);
		
		if(StringUtil.haveSpecialChar(unionDesc)){
			return result.setInfo(GameContext.getI18n().getText(TextId.UNION_DES_FORBIDDEN));
		}
		
		if(unionDesc.indexOf("*") != -1){
			return result.setInfo(GameContext.getI18n().getText(TextId.UNION_DES_FORBIDDEN));
		}
		boolean descFlag = validateStrByLength(unionDesc,GameContext.getFactionConfig().getFactionDescLength());
		if(!descFlag){
			return result.setInfo(GameContext.getI18n().getText(TextId.UNION_DES_TOO_LENGTH));
		}
		
		union.setUnionDesc(Util.replaceComma(unionDesc));
		GameContext.getBaseDAO().update(union);
		return result.success();
	}

	@Override
	public boolean isUnionPvpMap(String mapId) {
		return false;
	}
	
	@Override
	public int getUnionUpgradePopularity(String unionId){
		Union union = getUnion(unionId);
		if(union == null){
			return Integer.MAX_VALUE;
		}
		UnionUpgrade upgrade = getUnionData().getUnionUpgrade(union.getUnionLevel());
		if(upgrade == null){
			return Integer.MAX_VALUE;
		}
		return upgrade.getPopularity();
	}
	
	@Override
	public Result isUpgrade(RoleInstance role,String unionId){
		Result result = new Result();
		
		Union union = getUnion(unionId);

		if(union == null){
			result.setInfo(GameContext.getI18n().getText(TextId.UNION_NOT));
			return result;
		}
		
		if(!union.getPowerTypeSet(role).contains(UnionPowerType.Dispose_Apply_Join)){
			return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NO_POSITION));
		}
		UnionUpgrade upgrade = getUnionData().getUnionUpgrade(union.getUnionLevel());
		if(upgrade != null){
			if(union.getPopularity() >= upgrade.getPopularity()){
				result.success();
			}else{
				result.setInfo(GameContext.getI18n().messageFormat(TextId.UNION_UPGRADE_POPULARITY_FAIL,upgrade.getPopularity()));
			}
		}else{
			result.setInfo(GameContext.getI18n().getText(TextId.UNION_UPGRADE_FAIL));
		}
		return result;
	}

	@Override
	public Result changeUnionPopularity(String unionId,OperatorType operatorType, int value,int memberId) {
		Result result = new Result();
		Union union = getUnion(unionId);
		if(OperatorType.Add == operatorType){
			result = addUnionPopularity(union,value,memberId);
		}else if(OperatorType.Decrease == operatorType){
			result = changeUnionLevel(union,value);
		}
		return result;
	}
	
	@Override
	public void changeActivityPopularity(String unionId,int value,String activityName) {
		Union union = getUnion(unionId);
		try {
			union.getUpgradeLock().lock();
			
			if(union.getPopularity() >= value){
				union.setPopularity(union.getPopularity() - value);
				GameContext.getBaseDAO().update(union);
			
				String message = GameContext.getI18n().messageFormat(TextId.UNION_ACTIVITY_OPEN_MSG,activityName);
				GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Union, message, null, union);
				//门派升级记录
				UnionRecord record = new UnionRecord();
				record.setType(UnionRecordType.Union_Record_Level.getType());
				record.setUnionId(union.getUnionId());
				record.setData1(union.getUnionLevel());
				saveOrUpdUnionRecord(union,record);
			}
		} catch (Exception e) {
			this.logger.error("changeUnionLevel error:", e);
		}finally{
			union.getUpgradeLock().unlock();
		}
	}
	
	/**
	 * 添加公会人气
	 * @param union
	 * @param chanageValue
	 * @param memberId
	 */
	private Result addUnionPopularity(Union union, int chanageValue,int memberId){
		Result result = new Result();
		try{
			union.setPopularity(union.getPopularity() + chanageValue);
			GameContext.getBaseDAO().update(union);
			UnionMember member = union.getUnionMember(memberId);
			if(member != null){
				String message = GameContext.getI18n().messageFormat(TextId.UNION_ADD_POPULARITY,chanageValue,member.getRoleName());
				GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Union, message, null, union);
			}
			result.setInfo(GameContext.getI18n().getText(TextId.UNION_ADD_POPULARITY_SUCCESS));
			result.success();
		}catch(Exception e){
			logger.error("addUnionPopularity",e);
			result.setInfo(GameContext.getI18n().getText(TextId.UNION_ADD_POPULARITY_ERROR));
		}
		return result;
	}
	
	/**
	 * 公会等级变化
	 * @param union
	 * @param chanageValue
	 */
	private Result changeUnionLevel(Union union, int chanageValue){
		Result result = new Result();
		try {
			union.getUpgradeLock().lock();
			
			if(union.getPopularity() >= chanageValue){
				union.setPopularity(union.getPopularity() - chanageValue);
				union.setUnionLevel((byte)(union.getUnionLevel() + 1));
				GameContext.getBaseDAO().update(union);
			
				String message = GameContext.getI18n().messageFormat(TextId.UNION_UPGRADE_MSG,union.getUnionLevel());
				GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Union, message, null, union);
				//门派升级记录
				UnionRecord record = new UnionRecord();
				record.setType(UnionRecordType.Union_Record_Level.getType());
				record.setUnionId(union.getUnionId());
				record.setData1(union.getUnionLevel());
				saveOrUpdUnionRecord(union,record);
				
				//升级添加活动
				levelUpAddUnionActivity(union.getUnionId(),union.getUnionLevel());	
				
				UnionUpgrade upgrade = getUnionData().getUnionUpgrade(union.getUnionLevel());
				result.success();
				result.setInfo(upgrade.getInfo());
				return result;
			}else{
				result.setInfo(GameContext.getI18n().getText(TextId.UNION_LEVELUP_ERROR));
			}
			return result;
		} catch (Exception e) {
			this.logger.error("changeUnionLevel error:", e);
			result.setInfo(GameContext.getI18n().getText(TextId.UNION_LEVELUP_ERROR));
			return result;
		}finally{
			union.getUpgradeLock().unlock();
		}
	}
	
	@Override
	public int onLogin(RoleInstance role, Object context) {
		try {
			UnionMember member = GameContext.getBaseDAO().selectEntity(UnionMember.class, "roleId", role.getIntRoleId());
			if(member != null){
				role.setUnionId(member.getUnionId());
				member.setLevel(role.getLevel());
                //不要更新入库
				//saveOrUpdUnionMember(member);
			}
			
		} catch (RuntimeException e) {
			this.logger.error("UnionApp.login() error: ", e);
			return 0;
		}
		
		return 1;
	}
	
	@Override
	public int onLogout(RoleInstance role, Object context) {
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		return 1;
	}

	/**
	 * 弹劾物品检查
	 */
	@Override
	public Result impeach(RoleInstance role) {
		Result result = new Result();
		Union union = getUnion(role);
		try{
			if(union == null){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NOT));
			}
			
			UnionBase base = getUnionData().getUnionBase();
			int goodsNum = base.getImpeachGoondsNum();
		
			int goodsId = base.getImpeachGoodsId();
			
			result = union.canImpeach(role,goodsId,goodsNum);
			if(!result.isSuccess()) {
				return result;
			}
			
			//快速购买
			result = GameContext.getQuickBuyApp().doQuickBuy(role, goodsId, goodsNum, OutputConsumeType.union_impeach_consume, null);
			if(result.isIgnore()){
				return result;
			}
			if(!result.isSuccess()){
				return result;
			}
			impeachUpdate(union.getUnionId(),role);
		}catch(Exception e){
			logger.error("impeach",e);
		}
		return result.success();
	}
	
	/**
	 * 更换会长
	 * @param unionId
	 * @param role
	 */
	private void impeachUpdate(String unionId, RoleInstance role) {
		try {
			Union union = getUnion(unionId);
			if(union == null){
				return;
			}
			int impeachRoleId = role.getIntRoleId();
			Map<Integer,UnionMember> memberMap = union.getUnionMemberMap();
			if(Util.isEmpty(memberMap)){
				return;
			}
			int oldLeaderId = union.getLeaderId();
			UnionMember member = memberMap.get(impeachRoleId);
			UnionMember oldLeader = memberMap.get(oldLeaderId);
			if(oldLeader == null || member == null){
				return;
			}
			//更换会长
			member.setPosition(UnionPositionType.Leader.getType());
			
			oldLeader.setPosition(UnionPositionType.Member.getType());
			
			String impeachRoleName = member.getRoleName();
			
			union.setLeaderId(impeachRoleId);
			
			union.setLeaderName(impeachRoleName);
			
			changePosition(unionId,UnionPositionType.Member,oldLeader.getRoleId(),false);

			changePosition(unionId,UnionPositionType.Leader,member.getRoleId(),true);
			
			//弹劾成功，门派频道发消息
			String message = impeachRoleName + GameContext.getI18n().getText(TextId.UNION_IMPEACH_SUCCESS);
			GameContext.getChatApp().sendSysMessage(ChatSysName.Union, ChannelType.Union, message, null, union);
			//给新会长发邮件，通知弹劾成功
			String context1 = GameContext.getI18n().messageFormat(TextId.UNION_IMPEACH_MAIL_CONTENT, oldLeader.getRoleName());
			this.sendMail(impeachRoleId, GameContext.getI18n().getText(TextId.UNION_IMPEACH_MAIL_TITLE), context1);
			//给原会长发邮件，通知被弹劾
			String context2 =  GameContext.getI18n().messageFormat(TextId.UNION_IMPEACH_OLD_LEADER_MAIL_CONTENT, impeachRoleName);
			this.sendMail(oldLeaderId, GameContext.getI18n().getText(TextId.UNION_IMPEACH_MAIL_TITLE), context2);
		} catch (Exception e) {
			this.logger.error("impeachUpdate error: ", e);
		}
	}

	@Override
	public Collection<Union> getUnionListByName(String unionName) {
		List<Union> list = GameContext.getUnionDAO().getUnionByName(unionName);
		if(!Util.isEmpty(list)){
			return list;
		}
		return getUnionMap().values();
	}

	@Override
	public List<UnionMember> getUnionMemberListByOnline(String unionId) {
		Union union = getUnion(unionId);
		Map<Integer,UnionMember> memberMap = union.getUnionMemberMap();
		List<UnionMember> onlineMemberList = Lists.newArrayList();
		for(Entry<Integer,UnionMember> member : memberMap.entrySet()){
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(member.getKey()));
			if(role != null){
				onlineMemberList.add(member.getValue());
			}
		}
		return onlineMemberList;
	}

	@Override
	public Set<UnionPowerType> getPowerTypeSet(RoleInstance role) {
		Set<UnionPowerType> powerSet = Sets.newHashSet();
		Union union = getUnion(role);
		if(union == null){
			return powerSet;
		}
		UnionMember unionMember = union.getUnionMember(role.getIntRoleId());
		if(unionMember == null){
			return powerSet;
		}
		UnionPositionType positionType = unionMember.getPositionType();
		if(null == positionType){
			return powerSet;
		}
		powerSet.addAll(getUnionData().getPowerTypeSet(positionType));
		return powerSet;
	}

	@Override
	public int getUnionPosition(RoleInstance role) {
		int power = 0;
		Set<UnionPowerType> powerSet = getPowerTypeSet(role);
		if(Util.isEmpty(powerSet)) {
			return power;
		}
		int index = 1;
		for(UnionPowerType unionPowerType : UnionPowerType.values()) {
			if(powerSet.contains(unionPowerType)) {
				power = power |(1<<index);
			}
			index++;
		}
		return power;
	}


	@Override
	public int getUnionMemberPosition(String roleId, String unionId) {
		int position = -1;
		Union union = getUnion(unionId);
		if(union == null){
			return position;
		}
		UnionMember member = union.getUnionMember(Integer.parseInt(roleId));
		if(member == null){
			return position;
		}
		return member.getPosition();
	}

	@Override
	public Union getFirstUnion() {
		try{
			List<Union> list = Lists.newArrayList();
			list.addAll(unionMap.values());
			if(Util.isEmpty(list)){
				return null;
			}
			sortUnion(list);
			return list.get(0);
		}catch(Exception e){
			logger.debug("getFirstUnion error",e);
		}
		return null;
	}

	@Override
	public Union getCampFirstUnion(byte campId) {
		try{
			List<Union> list = Lists.newArrayList();
			list.addAll(unionMap.values());
			if(Util.isEmpty(list)){
				return null;
			}
			sortUnion(list);
			for(Union union : list){
				if(null == union){
					continue;
				}
				if(union.getUnionCamp() == campId){
					return union;
				}
			}
		}catch(Exception e){
			logger.debug("getFirstFaction error",e);
		}
		return null;
	}

	//按公会名称查找
	private Union searchUnionByName(String unionName){
		for(Entry<String,Union> union : unionMap.entrySet()){
			if(null == union){
				continue;
			}
			if(union.getValue().getUnionName().equals(unionName)){
				return union.getValue();
			}
		}
		return null;
	}

	@Override
	public void changePosition(String unionId,
			UnionPositionType positionTpye, int roleId,boolean flag) {
		Union union = getUnion(unionId);
		UnionMember unionMember = union.getUnionMember(roleId);
		unionMember.setPosition(positionTpye.getType());
		
		if(flag){
			GameContext.getBaseDAO().update(union);
		}
		saveOrUpdUnionMember(unionMember);
		this.notifyUnionRoleHeadShowChange(roleId);
	}

	@Override
	public List<Union> getUnionRankList(int size) {
		List<Union> list = Lists.newArrayList();
		if(unionMap != null && !unionMap.isEmpty()){
			list.addAll(unionMap.values());
			sortUnion(list);
		}
		int rankSize = Util.getSubListSize(list.size(), size);
		List<Union> sortList = list.subList(0, rankSize);
		return sortList;
	}

	@Override
	public String getUnionId(int roleId) {
		for(Entry<String,Union> union : unionMap.entrySet()){
			UnionMember member = union.getValue().getUnionMember(roleId);
			if(member != null){
				return union.getKey();
			}
		}
		return "";
	}

	@Override
	public int getUnionDataAllNum(int unionLevel) {
		UnionUpgrade upgrade = getUnionData().getUnionUpgrade(unionLevel);
		if(upgrade != null){
			return upgrade.getMaxMemberNum();
		}
		return 0;
	}

	@Override
	public int getUnionPositionNum(byte positionType,int unionLevel) {
		int num = 0;
		if(positionType == UnionPositionType.Member.getType()){
			num = UnionPositionType.getPositionAllNum();
			UnionUpgrade upgrade = getUnionData().getUnionUpgrade(unionLevel);
			return upgrade.getMaxMemberNum() - num;
		}
		return UnionPositionType.getPositionNum(positionType);
	}
	
	@Override
	public ListPageDisplay<UnionRecord> getUnionRecordList(String unionId,int currPage, int size) {
		Union union = getUnion(unionId);
		if(union != null){
			union.sortUnionRecord();
			ListPage<UnionRecord> listPage = new ListPage<UnionRecord>(union.getUnionRecordList(),size);
			return listPage.getObjectsDsiplay(currPage);
		}
		return null;
	}

	@Override
	public int getUnionDataMaxPopualrity(int unionLevel) {
		UnionUpgrade upgrade = getUnionData().getUnionUpgrade(unionLevel);
		if(upgrade != null){
			return upgrade.getPopularity();
		}
		return 0;
	}
	
	/**
	 * 保存成员数据
	 * @param team
	 * @param member
	 */
	@Override
	public void saveOrUpdUnionMember(UnionMember member){
		if(member != null){
			Union union = getUnion(member.getUnionId());
			if(union != null){
                GameContext.getBaseDAO().saveOrUpdate(member);
				union.saveUnionMember(member);
			}
		}
	}
	
	/**
	 * 保存记录数据
	 * @param union
	 * @param record
	 */
	private void saveOrUpdUnionRecord(Union union ,UnionRecord record){
		if(union != null && record != null){
			union.saveUnionRecord(record);
			GameContext.getBaseDAO().insert(record);
		}
	}
	
	/**
	 * 保存公会记录数据
	 * @param union
	 * @param record
	 */
	private void saveOrUpdUnion(Union union){
		if(union != null){
			GameContext.getBaseDAO().saveOrUpdate(union);
		}
	}
	
	@Override
	public C1728_UnionDonateRespMessage donate(RoleInstance role){
		C1728_UnionDonateRespMessage resp = new C1728_UnionDonateRespMessage();
		Union union = getUnion(role);
		
		if(union == null){
			resp.setInfo(GameContext.getI18n().getText(TextId.UNION_NOT));
			return resp;
		}
		
		Result result = canDonate(role);
		if(result.isIgnore()){
			return null;
		}
		
		if(!result.isSuccess()) {
			resp.setInfo(result.getInfo());
			return resp;
		}
		
		UnionDonate donateData = getUnionDonate(role);
		if(donateData == null) {
			resp.setInfo(GameContext.getI18n().getText(TextId.UNION_DONATE_NULL));
			return resp;
		}
		
		byte moneyType = donateData.getMoneyType();
		int money = donateData.getMoney();
		int contribute = donateData.getContribute();
		int addDkp = donateData.getAddDkp();
		
		role.getRoleCount().changeTimes(CountType.UnionMemberDonate);
		
		//捐赠兑换Dkp
		changeMemberDkp(role,addDkp,OperatorType.Add,FunType.donate,false);
		
		GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.get(moneyType), OperatorType.Decrease, money, FunType.donate.getType());
		role.getBehavior().notifyAttribute();
		
		UnionMember member = union.getUnionMember(role.getIntRoleId());
		
		addDonateRecord(role,contribute,addDkp);
		
		resp.setAddPoplarity(contribute);
		resp.setCounts(role.getRoleCount().getRoleTimesToByte(CountType.UnionMemberDonate));
		resp.setDkp(member.getDkp());
		resp.setAddDkp(addDkp);
		resp.setType(Result.SUCCESS);
		resp.setMoneyType((byte)0);
		//下次
		donateData = getUnionDonate(role);
		resp.setRewDkp(donateData.getAddDkp());
		resp.setMoneyNum(donateData.getMoney());
		return resp;
	}
	
	@Override
	public C1728_UnionDonateRespMessage gemDonate(RoleInstance role){
		C1728_UnionDonateRespMessage resp = new C1728_UnionDonateRespMessage();
		Union union = getUnion(role);
		
		if(union == null){
			resp.setInfo(GameContext.getI18n().getText(TextId.UNION_NOT));
			return resp;
		}
		
		Result result = canGemDonate(role);
		if(result.isIgnore()){
			return null;
		}
		if(!result.isSuccess()) {
			resp.setInfo(result.getInfo());
			return resp;
		}
		
		UnionGemDonate gemDonateData = getUnionGemDonate(role);
		if(gemDonateData == null) {
			resp.setInfo(GameContext.getI18n().getText(TextId.UNION_DONATE_NULL));
			return resp;
		}
		
		int gem = gemDonateData.getGem();
		int contribute = gemDonateData.getContribute();
		int addDkp = gemDonateData.getAddDkp();
		
		role.getRoleCount().changeTimes(CountType.UnionMemberGemDonate);
		
		//捐赠兑换Dkp
		changeMemberDkp(role,addDkp,OperatorType.Add,FunType.donate,false);
		
		GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, OperatorType.Decrease, gem, FunType.donate.getType());
		role.getBehavior().notifyAttribute();
		
		UnionMember member = union.getUnionMember(role.getIntRoleId());
		
		addDonateRecord(role,contribute,addDkp);
		
		resp.setAddPoplarity(contribute);
		resp.setCounts(role.getRoleCount().getRoleTimesToByte(CountType.UnionMemberGemDonate));
		resp.setDkp(member.getDkp());
		resp.setAddDkp(addDkp);
		resp.setType(Result.SUCCESS);
		resp.setMoneyType((byte)1);
		//下次
		gemDonateData = getUnionGemDonate(role);
		if(gemDonateData != null){
			resp.setRewDkp(gemDonateData.getAddDkp());
			resp.setMoneyNum(gemDonateData.getGem());
		}
		return resp;
	}
	
	/**
	 * 增加公会人气
	 * 记录捐献
	 * 添加活跃度
	 */
	private void addDonateRecord(RoleInstance role,int contribute,int dkp){
		Union union = getUnion(role);
		if(union == null){
			return;
		}
		changeUnionPopularity(union.getUnionId(), OperatorType.Add, contribute, role.getIntRoleId());
		// 公会捐献记录
		UnionRecord record = new UnionRecord();
		record.setType(UnionRecordType.Union_Record_Donate.getType());
		record.setUnionId(union.getUnionId());
		record.setData1(contribute);
		record.setData2(String.valueOf(dkp));
		record.setData3(role.getRoleName());
		saveOrUpdUnionRecord(union,record);
		
		//活跃度
		GameContext.getDailyPlayApp().incrCompleteTimes(role, 1, DailyPlayType.union_donate, "");
	}
	
	/**
	 * 判断是否可以捐献
	 * @param role
	 * @return
	 */
	public Result canDonate(RoleInstance role){
		Result result = new Result();
		UnionDonate donate = getUnionDonate(role);
		if(donate == null){
			return result.failure();
		}
		if(donate.getMaxCount() <= 0) {
			return result.failure();
		}
		int roleCount = role.getRoleCount().getRoleTimesToByte(CountType.UnionMemberDonate);
		if(roleCount >= donate.getMaxCount()) {
			String str = GameContext.getI18n().messageFormat(TextId.UNION_DONATE_MAX_COUNT, donate.getMaxCount());
			return result.setInfo(str);
		}
		AttributeType attriType = AttributeType.get(donate.getMoneyType());
		//【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, attriType, donate.getMoney());
		if(ar.isIgnore()){   //弹板
			return ar;
		}
		if(!ar.isSuccess()){ //不足
			String str = GameContext.getI18n().messageFormat(TextId.UNION_MONEY_NOT_ENOUGH, attriType.getName(), donate.getMoney());
			return result.setInfo(str);
		}
		
		return result.success();
	}
	
	/**
	 * 判断是否可以钻石捐献
	 * @param role
	 * @return
	 */
	public Result canGemDonate(RoleInstance role){
		Result result = new Result();
		UnionGemDonate donate = getUnionGemDonate(role);
		if(donate == null){
			return result.failure();
		}
		
		if(donate.getMaxCount() <= 0) {
			return result.failure();
		}
		
		int roleCount = role.getRoleCount().getRoleTimesToByte(CountType.UnionMemberGemDonate);
		
		if(roleCount >= donate.getMaxCount()) {
			String str = GameContext.getI18n().messageFormat(TextId.UNION_DONATE_MAX_COUNT, donate.getMaxCount());
			return result.setInfo(str);
		}
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.goldMoney, donate.getGem());
		if(ar.isIgnore()){   //弹板
			return ar;
		}
		if(!ar.isSuccess()){ //不足
			String str = GameContext.getI18n().messageFormat(TextId.UNION_MONEY_NOT_ENOUGH, AttributeType.goldMoney.getName(), donate.getGem());
			return result.setInfo(str);
		}
		
		return result.success();
	}

	/**
	 * 
	 * @param role
	 * @param value
	 * @param type
	 * @param type
	 */
	@Override
	public void changeMemberDkp(RoleInstance role, int value, OperatorType operatorType,FunType type,boolean flag) {
		GameContext.getUserAttributeApp().changeRoleDkp(role, AttributeType.dkp, operatorType, value,type.getType());
		role.getBehavior().notifyAttribute();
		if(flag && OperatorType.Add.equals(operatorType)){
			changeUnionPopularity(role.getUnionId(), OperatorType.Add, value, role.getIntRoleId());
		}
	}

	@Override
	public int getUnionMemberDkp(String unionId,int roleId) {
		Union union = getUnion(unionId);
		if(union != null){
			UnionMember member = union.getUnionMember(roleId);
			if(member != null){
				return member.getDkp();
			}
		}
		return 0;
	}
	
	/**
	 * 删除公会数据
	 * @param unionId
	 * @param roleId
	 * @param isDelUnion
	 */
	private void delUnionData(String unionId,int roleId,boolean isDelUnion){
		//从数据库中删除，并更新角色信息
		if(isDelUnion){
            //排行榜下榜
            GameContext.getRankApp().unionOffRank(unionId);
            delUnionData(unionId) ;
		}
		GameContext.getBaseDAO().delete(UnionMember.class, "roleId", roleId);
	}

    private void delUnionData(String unionId){
        unionMap.remove(unionId);
        GameContext.getBaseDAO().delete(Union.class, "unionId", unionId);
        GameContext.getBaseDAO().delete(UnionRecord.class,"unionId",unionId);
        GameContext.getBaseDAO().delete(UnionActivity.class,"unionId",unionId);
        GameContext.getUnionIntegralBattleApp().delUnion(unionId);
    }


	@Override
	public C2754_UnionActivityListRespMessage sendActivityList(String unionId) {
		C2754_UnionActivityListRespMessage respMsg = new C2754_UnionActivityListRespMessage();
		
		Union union = getUnion(unionId);
		if(union == null){
			return respMsg;
		}
		Map<Byte,UnionActivity> map = activityMap.get(unionId);
		if(map == null){
			initUnionActivity(union.getUnionId(),union.getUnionLevel());
			map = activityMap.get(unionId);
		}
		//获得当前级别公会活动基础数据
		Set<Byte> activityIdSet = GameContext.getUnionDataApp().getActivityGroupMap(union.getUnionLevel());
		Map<Byte,UnionActivityInfo> activityDataMap = GameContext.getUnionDataApp().getUnionActivityMap();
		if(Util.isEmpty(activityDataMap)){
			return respMsg;
		}
		List<UnionActivityItem> activityItemList = Lists.newArrayList();
		for(Entry<Byte,UnionActivityInfo> activityInfo : activityDataMap.entrySet()){
			
			byte num = 0,state=0;
			UnionActivity activity = null;
			UnionActivityItem item = new UnionActivityItem();
			
			item.setImageId(activityInfo.getValue().getImageId());
			
			if(activityIdSet.contains(activityInfo.getKey())){
				item.setFlag(true);
			}
			
			item.setFunType(activityInfo.getValue().getFunType());
			if(!Util.isEmpty(map)){
				if(map.containsKey(activityInfo.getKey())){
					activity = map.get(activityInfo.getKey());
				}
				if(activity != null){
					num = activity.getNum();
					state = activity.getState();
				}
				int nextNum = num+1;
				UnionActivityConsume consume = getUnionData().getUnionActivityConsume((byte)nextNum);
				if(consume !=null){
					item.setGem(consume.getGem());
					item.setPopularity(consume.getPopularity());
				}
			}
			item.setNum(num);
			item.setActivityId(activityInfo.getKey());
			item.setName(activityInfo.getValue().getActivityName());
			item.setType(activityInfo.getValue().getType());
			item.setState(state);
			item.setDes(activityInfo.getValue().getActivityDes());
			if(!item.isFlag()){
				item.setDes(activityInfo.getValue().getOpenLevel());
			}
			activityItemList.add(item);
		}
		respMsg.setItem(activityItemList);
		//排序
		Collections.sort(activityItemList,this.activityItemComparator);
		return respMsg;
	}
	
	Comparator<UnionActivityItem> activityItemComparator = new Comparator<UnionActivityItem>(){
		@Override
		public int compare(UnionActivityItem a1, UnionActivityItem a2) {
			if(a1.getActivityId() < a2.getActivityId()){
				return -1;
			}
			if(a1.getActivityId() > a2.getActivityId()){
				return 1;
			}
			return 0;
		}
	} ;

	@Override
	public C2755_UnionBossListRespMessage sendBossDpsList(String unionId,byte activityId) {
		C2755_UnionBossListRespMessage respMsg = new C2755_UnionBossListRespMessage();
		
		List<UnionActivityBossItem> bossItemList = Lists.newArrayList();
		
		UnionInsBoss insBoss = GameContext.getUnionDataApp().getUnionInsBossMap(activityId);
		if(insBoss != null){
			String [] groupArr = insBoss.getGroupId().split(",");
			
			for(String groupId : groupArr){
			
				Set<UnionDpsResult> bossArr = GameContext.getUnionDataApp().getUnionDpsResult(Byte.parseByte(groupId));
				List<GoodsLiteNamedItem> goodsList = Lists.newArrayList();
				UnionActivityBossItem bossItem = new UnionActivityBossItem();
				byte state = GameContext.getUnionInstanceApp().getInsBossState(unionId, activityId, Byte.parseByte(groupId));
				bossItem.setState(state);
				
				String bossId = "";
				for(UnionDpsResult bid : bossArr){
					NpcTemplate npc = GameContext.getNpcApp().getNpcTemplate(bid.getBossId());
					if(npc == null){
						continue;
					}
					bossId = bid.getBossId();
					if(bossItem.getBossName() != null && !"".equals(bossItem.getBossName())){
						bossItem.setBossName(bossItem.getBossName() +  " 、 " + npc.getNpcname());
					}else{
						bossItem.setBossName(npc.getNpcname());
					}
				}
				if(!Util.isEmpty(bossId)){
					Set<Integer> groupSet = GameContext.getUnionDataApp().getDropMap(bossId);
					if(!Util.isEmpty(groupSet)){
						for(int gid : groupSet){
							List<UnionDropGroup> dropGroupList = GameContext.getUnionDataApp().getUnionDropGroup(gid);
							if(Util.isEmpty(dropGroupList)){
								continue;
							}
							for(UnionDropGroup drop : dropGroupList){
								GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(drop.getGoodsId());
								GoodsLiteNamedItem goodsItem = goodsBase.getGoodsLiteNamedItem();
								goodsItem.setGoodsId(drop.getGoodsId());
								goodsItem.setBindType(drop.getGoodsBind());
								goodsItem.setNum(drop.getGoodsNum());
								goodsList.add(goodsItem);
							}
						}
					}
					bossItem.setGoodsList(goodsList);
				}
				bossItem.setGroupId(Byte.parseByte(groupId));
				bossItemList.add(bossItem);
			}
		}
		respMsg.setActivityId(activityId);
		respMsg.setBossList(bossItemList);
		return respMsg;
	}
	
	private List<RoleDps> sortDps(Map<Integer,RoleDps> roleDpsMap){
		List<RoleDps> list = Lists.newArrayList();
		if(roleDpsMap != null && !roleDpsMap.isEmpty()){
			
			list.addAll(roleDpsMap.values());
			if(Util.isEmpty(list)){
				return list;
			}
			Collections.sort(list, new Comparator<RoleDps>() {
				public int compare(RoleDps info1, RoleDps info2) {
					if(info1.getDps() > info2.getDps()) {
						return -1;
					}
					return 0;
				}
			});
		}
		return list;
	}
	
	@Override
	public void validActivity(){
		Map<Byte, UnionActivityInfo> activeInfoMap = getUnionData().getUnionActivityMap();
		//获得当前系统时间是星期几
		for(Entry<Byte, UnionActivityInfo> activityInfo : activeInfoMap.entrySet()){
			//获得星期几
			String []week = activityInfo.getValue().getCd().split(",");
			for(String w : week){
				GameContext.getUnionInstanceApp().resetActivity(activityInfo.getKey(),Byte.parseByte(w));
			}
		}
		Date now = new Date();
		for(Entry<String,Union> union : unionMap.entrySet()){
			if (DateUtil.sameDay(new Date(union.getValue().getSummonTime()), now)) {
				continue;
			}
			union.getValue().setSummonTime(now.getTime());
			union.getValue().setSummonFlag(false);
		}
	}

	
	/**
	 * 修改活动状态
	 * state 0关闭 1开启
	 */
	@Override
	public ChangeActivityResult changeActivity(RoleInstance role, byte activityId, byte state,byte consumeType) {
		
		ChangeActivityResult result = new ChangeActivityResult();
		if(!role.hasUnion()){
			result.setInfo(GameContext.getI18n().getText(TextId.UNION_NOT));
			return result;
		}
		Union union = getUnion(role);
		
		if(getActivityState(role.getUnionId(), activityId) == state){
			result.setInfo(GameContext.getI18n().getText(TextId.UNION_ACTIVITY_SUCCESS));
			return result;
		}
		
		//判断活动
		UnionActivityInfo activeInfo = getUnionData().getUnionActivityMap().get(activityId);
		if(activeInfo != null){
			if(activeInfo.getType() == (byte)0){
				result.setInfo(GameContext.getI18n().getText(TextId.UNION_ACTIVITY_CLOSE_PERSONAL_ERR));
				return result;
			}
		}
		UnionUpgrade upgrade = getUnionData().getUnionUpgrade(union.getUnionLevel());
		if(upgrade == null){
			result.setInfo(activeInfo.getOpenLevel());
			return result;
		}
		
		if(!upgrade.getActivityId().contains(String.valueOf(activityId))){
			result.setInfo(activeInfo.getOpenLevel());
			return result;
		}
		
		if(!this.getPowerTypeSet(role).contains(UnionPowerType.Union_Activity)){
			result.setInfo(GameContext.getI18n().getText(TextId.UNION_MODIFY_ACTIVITY_POSITION_NOT_HAS));
			return result;
		}
		Map<Byte,UnionActivity> map = activityMap.get(role.getUnionId());
		if(map == null){
			initUnionActivity(role.getUnionId(),union.getUnionLevel());
			map = activityMap.get(role.getUnionId());
		}
		byte num = 0;
		UnionActivity activity = map.get(activityId);
		if(activity == null){
			activity = map.get(activityId);
		}
		UnionActivityConsume consume = null;
		
		UnionActivityInfo activityInfo = null;
		
		Map<Byte,UnionActivityInfo> activityDataMap = GameContext.getUnionDataApp().getUnionActivityMap();
		if(activityDataMap.containsKey(activityId)){
			activityInfo = activityDataMap.get(activityId);
		}
		if(activityInfo == null){
			result.setInfo(GameContext.getI18n().getText(TextId.UNION_ACTIVITY_ERR));	
			return result;
		}
		if(state != 0){
			
			if(activity != null){
				num = (byte)(activity.getNum()+1);
			}else{
				num++;
			}
			
			consume = getUnionData().getUnionActivityConsume(num);
			if(consumeType == (byte)0){
				//【游戏币/潜能/钻石不足弹板】 判断
				Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.goldMoney, consume.getGem());
				if(ar.isIgnore()){//弹板
					result.setIgnore(true);
					return result;
				}
				if(!ar.isSuccess()){//不足
					result.setInfo(GameContext.getI18n().messageFormat(TextId.UNION_ACTIVITY_CONSUME_GEM_ERR,activityInfo.getActivityName()));
					return result;
				}
				//如果消耗钻石
				GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, OperatorType.Decrease, consume.getGem(), FunType.gemActivity.getType());
				role.getBehavior().notifyAttribute();
			}else if(consumeType == (byte)1){
				if(union.getPopularity() >= consume.getPopularity()){
					GameContext.getUnionApp().changeActivityPopularity(role.getUnionId(), consume.getPopularity(), activityInfo.getActivityName());
				}else{
					result.setInfo(GameContext.getI18n().messageFormat(TextId.UNION_ACTIVITY_CONSUME_POPULARITY_ERR,activityInfo.getActivityName()));
					return result;
				}
			}
			
		}else{
			if(activity != null){
				num = activity.getNum();
			}
		}
		
		//下一次开启所需
		consume = getUnionData().getUnionActivityConsume((byte)(num+1));
		
		result.setGem(consume.getGem());
		result.setPopularity(consume.getPopularity());
		
		
		activity.setState(state);
		activity.setNum(num);
		
		saveOrUpdUnionActivity(activity);
		
		result.setInfo(GameContext.getI18n().getText(TextId.UNION_ACTIVITY_SUCCESS));
		result.setState(state);
		result.success();
		
		UnionRecord record = new UnionRecord();
		if(state == (byte)1){
			record.setType(UnionRecordType.Uniuon_Record_Activity_Open.getType());
		}else{
			record.setType(UnionRecordType.Uniuon_Record_Activity_Close.getType());
		}
		record.setUnionId(union.getUnionId());
		record.setData2(activityInfo.getActivityName());
		record.setData3(role.getRoleName());
		saveOrUpdUnionRecord(union,record);
		
		return result;
	}
	
	/**
	 * 初始化公会活动
	 * @param unionId
	 * @param activityId
	 */
	private void initUnionActivity(String unionId,int unionLevel){
		Set<Byte> activityIdSet = GameContext.getUnionDataApp().getActivityGroupMap(unionLevel);
		if(activityIdSet != null && !activityIdSet.isEmpty()){
			for(byte activityId : activityIdSet){
				addUnionActivity(unionId,activityId);
			}
		}
		
		Map<Byte,UnionActivity> map = activityMap.get(unionId);
		if(map != null && !map.isEmpty()){
			for(Entry<Byte,UnionActivity> activity : map.entrySet()){
				saveOrUpdUnionActivity(activity.getValue());
			}
		}
	}
	
	/**
	 * 初始化公会活动
	 * @param unionId
	 * @param activityId
	 */
	private void levelUpAddUnionActivity(String unionId,int unionLevel){
		Set<Byte> activityIdSet = GameContext.getUnionDataApp().getActivityGroupMap(unionLevel);
		if(!activityMap.containsKey(unionId)){
			return;
		}
		Map<Byte,UnionActivity> map = activityMap.get(unionId);
		if(Util.isEmpty(map)){
			return;
		}
		if(activityIdSet != null && !activityIdSet.isEmpty()){
			for(byte activityId : activityIdSet){
				if(!map.containsKey(activityId)){
					addUnionActivity(unionId, activityId);
				}
			}
		}
	}
	
	/**
	 * 初始化公会活动
	 * @param unionId
	 * @param activityId
	 */
	private void addUnionActivity(String unionId,byte activityId){
		
		UnionActivity activity = new UnionActivity();
		activity.setUnionId(unionId);
		activity.setActivityId(activityId);
		
		//判断活动
		UnionActivityInfo activeInfo = getUnionData().getUnionActivityMap().get(activityId);
		if(activeInfo != null){
			if(activeInfo.getType() == (byte)0){
				activity.setState((byte)1);
			}
		}
		
		Map<Byte,UnionActivity> map = Maps.newConcurrentMap();
		
		if(activityMap.containsKey(unionId)){
			map = activityMap.get(unionId);
		}
		map.put(activityId, activity);
		
		activityMap.put(unionId,map);
		saveOrUpdUnionActivity(activity);
	}
	
	/**
	 * 保存公会活动数据
	 * @param activity
	 */
	private void saveOrUpdUnionActivity(UnionActivity activity){
		Map<Byte,UnionActivity> map = null;
		if(activityMap.containsKey(activity.getUnionId())){
			map = activityMap.get(activity.getUnionId());
		}else{
			map = Maps.newConcurrentMap();
		}
		map.put(activity.getActivityId(), activity);
		activityMap.put(activity.getUnionId(), map);
		GameContext.getBaseDAO().saveOrUpdate(activity);
	}
	
	@Override
	public byte getActivityState(String unionId,byte activityId){
		if(activityMap.containsKey(unionId)){
			Map<Byte,UnionActivity> map = activityMap.get(unionId);
			if(map.containsKey(activityId)){
				return map.get(activityId).getState();
			}
		}
		return (byte)0;
	}

	@Override
	public C2757_UnionRoleDpsListRespMessage sendRoleDpsList(String unionId,
			byte activityId, byte groupId) {
		
		C2757_UnionRoleDpsListRespMessage respMsg = new C2757_UnionRoleDpsListRespMessage();
		
		List<UnionRoleDpsItem> dpsList = new ArrayList<UnionRoleDpsItem>();
		
		Map<Integer,RoleDps> roleDpsMap = GameContext.getUnionInstanceApp().getUnionRoleDpsMap(unionId,activityId,groupId);
		if(!Util.isEmpty(roleDpsMap)){
			List<RoleDps> roleDpsList = sortDps(roleDpsMap);
			for(RoleDps dps : roleDpsList){
				UnionRoleDpsItem dpsItem = new UnionRoleDpsItem();
				dpsItem.setDps(dps.getDps());
				dpsItem.setRoleId(dps.getRoleId());
				dpsItem.setRoleName(dps.getRoleName());
				dpsList.add(dpsItem);
			}
		}
		
		Set<UnionDpsResult> bossArr = GameContext.getUnionDataApp().getUnionDpsResult(groupId);
		if(!Util.isEmpty(bossArr)){
			for(UnionDpsResult result : bossArr){
				respMsg.setBossDes(result.getBossDes());
				break;
			}
		}
		
		respMsg.setGroupId(groupId);
		respMsg.setDpsList(dpsList);
		return respMsg;
	}

	@Override
	public C2753_UnionInfoRespMessage sendC2753_UnionInfoRespMessage(RoleInstance role) {
		C2753_UnionInfoRespMessage resp = new C2753_UnionInfoRespMessage();
		Union union = GameContext.getUnionApp().getUnion(role);
		if(union != null){
			UnionItem unionItem = new UnionItem();
			unionItem.setLeaderName(union.getLeaderName());
			unionItem.setMaxMemberNum((short)GameContext.getUnionApp().getUnionDataAllNum(union.getUnionLevel()));
			unionItem.setMaxPopularity(GameContext.getUnionApp().getUnionDataMaxPopualrity(union.getUnionLevel()));
			unionItem.setMemberNum((short)union.getUnionMemberMap().size());
			unionItem.setPopularity(union.getPopularity());
			unionItem.setSelfUnion((byte)1);
			unionItem.setUnionDesc(union.getUnionDesc());
			unionItem.setUnionId(union.getUnionId());
			unionItem.setUnionLevel(union.getUnionLevel());
			unionItem.setUnionName(union.getUnionName());
			int progress = 0;
			if(union.getProgress() == 0){
				Set<String> setBoss = GameContext.getUnionInstanceApp().getUnionKillBossRecord(role.getUnionId());
				if(setBoss != null && !setBoss.isEmpty()){
					progress = setBoss.size();
				}
			}
			unionItem.setMinProgress(progress);
			unionItem.setMaxProgress(getUnionData().getActivityMaxBossNum());
			resp.setItem(unionItem);
			
			List<UnionMemberItem> memberItemList = new ArrayList<UnionMemberItem>();
			List<UnionMember> memberList = union.getUnionMemberList();
			if(!Util.isEmpty(memberList)){
				for(UnionMember member : memberList){
					UnionMemberItem memberItem = new UnionMemberItem();
					memberItem.setDkp(member.getDkp());
					memberItem.setMemberId(member.getRoleId());
					memberItem.setMemberName(member.getRoleName());
					memberItem.setPosition(member.getPosition());
					memberItem.setLevel(member.getLevel());
					memberItem.setOnline((byte)0);
					RoleInstance mem = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(member.getRoleId()));
					if(null != mem){
						memberItem.setLevel(mem.getLevel());
						memberItem.setOnline((byte)1);
					}
					
					memberItemList.add(memberItem);
				}
			}
			resp.setMemberList(memberItemList);
		}
		return resp;
	}
	
	private UnionDonate getUnionDonate(RoleInstance role){
		List<UnionDonate> list = getUnionData().getUnionDonateList();
		if(Util.isEmpty(list)){
			return null;
		}
		for(UnionDonate donate : list){
			if(donate == null){
				continue;
			}
			if(role.getLevel() < donate.getMinLevel() || role.getLevel() > donate.getMaxLevel()){
				continue;
			}
			return donate;
		}
		return null;
	}
	
	private UnionGemDonate getUnionGemDonate(RoleInstance role){
		List<UnionGemDonate> list = getUnionData().getUnionGemDonateList();
		if(Util.isEmpty(list)){
			return null;
		}
		
		int roleCount = role.getRoleCount().getRoleTimesToByte(CountType.UnionMemberGemDonate);
		
		for(UnionGemDonate donate : list){
			if(donate == null){
				continue;
			}
			
			if(roleCount >= donate.getMaxCount()){
				return list.get(list.size()-1);
			}
			
			if(roleCount < donate.getMin() || roleCount >= donate.getMax()){
				continue;
			}
			
			return donate;
		}
		return null;
	}

	/**
	 * 升级公会成员
	 */
	@Override
	public Result levelUpUnionMember(RoleInstance leader, int roleId)
			throws ServiceException {
		Result result = new Result();
		Union union = getUnion(leader);
		UnionPositionType positionType = null;
		UnionMember member = union.getUnionMember(roleId);
		UnionPositionType[] positionList = UnionPositionType.values();
		for(int i=positionList.length-1;i>0;i--){
			if(member.getPositionType().getType() > positionList[i].getType()){
				if(isPositionMaxNum(union.getUnionId(),positionList[i])){
					positionType = positionList[i];
					break;
				}
			}
			
		}
		if(positionType == null){
			return result.setInfo(GameContext.getI18n().messageFormat(TextId.UNION_CHANGE_POSITION_LEVELUP_ERR));
		}
		changePosition(union.getUnionId(),positionType,roleId,false);
		
		String info = GameContext.getI18n().messageFormat(TextId.UNION_CHANGE_POSITION_LEVELUP_SUCCESS, member.getRoleName(),positionType.getName());
		GameContext.getChatApp().sendSysMessage(ChatSysName.Union, ChannelType.Union, info, null, union);
		//给新会长浮动提示
		boolean isOnline = GameContext.getOnlineCenter().isOnlineByRoleId(String.valueOf(roleId));
		String tips = GameContext.getI18n().messageFormat(TextId.UNION_CHANGE_POSITION_LEVELUP_MAIL,member.getPositionType().getName());
		if(isOnline) {
			GameContext.getMessageCenter().sendByRoleId(null, String.valueOf(roleId), new C0003_TipNotifyMessage(tips));
			//主推权限列表
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
			this.notifyUnionRolePosition(role);
		}else{
			this.sendMail(roleId, GameContext.getI18n().getText(TextId.UNION_CHANGE_POSITION_LEVELUP_MAIL_TITLE), tips);
		}
		result.setInfo(GameContext.getI18n().getText(TextId.UNION_CHANGE_POSITION_LEVELUP_SUCCESS_MSG));
		//职位晋升记录
		UnionRecord record = new UnionRecord();
		record.setType(UnionRecordType.Uniuon_Record_Position_LevelUp.getType());
		record.setUnionId(union.getUnionId());
		record.setData3(member.getRoleName());
		record.setData2(member.getPositionType().getName());
		saveOrUpdUnionRecord(union,record);
		return result.success();
	}
	
	private boolean isPositionMaxNum(String unionId,UnionPositionType positionType){
		int postNum = 0;
		Union union = getUnion(unionId);
		for(Entry<Integer,UnionMember> memberMap : union.getUnionMemberMap().entrySet()){
			if(memberMap.getValue().getPositionType() == positionType){
				postNum++;
			}
		}
		
		int num = UnionPositionType.getPositionNum(positionType.getType());
		if(num <= 0 || postNum == 0){
			return true;
		}
		
		if(postNum >= num){
			return false;
		}
		return true;
	}

	@Override
	public Result demotionUnionMember(RoleInstance leader, int roleId)
			throws ServiceException {
		Result result = new Result();
		Union union = getUnion(leader);
		UnionPositionType positionType = null;
		UnionMember member = union.getUnionMember(roleId);
		if(union.getUnionMember(leader.getIntRoleId()).getPositionType().getType() >= member.getPositionType().getType()){
			return result.setInfo(GameContext.getI18n().messageFormat(TextId.UNION_CHANGE_POSITION_LEVELDOWN_ERR));
		}
		for(UnionPositionType post : UnionPositionType.values()){
			if(member.getPositionType().getType() < post.getType()){
				if(isPositionMaxNum(union.getUnionId(),post)){
					positionType = post;
					break;
				}
			}
		}
		if(positionType == null){
			return result.setInfo(GameContext.getI18n().messageFormat(TextId.UNION_CHANGE_POSITION_LEVELUP_ERR));
		}
		changePosition(union.getUnionId(),positionType,roleId,false);
		
		String info = GameContext.getI18n().messageFormat(TextId.UNION_CHANGE_POSITION_DEMOTION_MAIL, positionType.getName());
		GameContext.getChatApp().sendSysMessage(ChatSysName.Union, ChannelType.Union, info, null, union);
		//给新会长浮动提示
		boolean isOnline = GameContext.getOnlineCenter().isOnlineByRoleId(String.valueOf(roleId));
		if(isOnline) {
			GameContext.getMessageCenter().sendByRoleId(null, String.valueOf(roleId), new C0003_TipNotifyMessage(info));
			//主推权限列表
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
			this.notifyUnionRolePosition(role);
		}else{
			this.sendMail(roleId, GameContext.getI18n().getText(TextId.UNION_CHANGE_POSITION_DEMOTION_MAIL_TITLE), info);
		}
		result.setInfo(GameContext.getI18n().getText(TextId.UNION_CHANGE_POSITION_DEMOTION_SUCCESS_MSG));
		
		//职位降级记录
		UnionRecord record = new UnionRecord();
		record.setType(UnionRecordType.Uniuon_Record_Position_Demotion.getType());
		record.setUnionId(union.getUnionId());
		record.setData3(member.getRoleName());
		record.setData2(member.getPositionType().getName());
		saveOrUpdUnionRecord(union,record);
		
		return result.success();
	}
	
	@Override
	public boolean isBuy(int roleId){
		if(uniondkpMap.containsKey(roleId)){
			UnionDkp dkp = uniondkpMap.get(roleId);
			if(System.currentTimeMillis() - dkp.getExitTime() >= DateUtil.ONE_DAY_MILLIS){
				return true;
			}
			return false;
		}else{
			return true;
		}
	}


	@Override
	public Result joinUnionTerritory(RoleInstance role) {
		Result result = new Result();
		try {
			UnionBase unionBase = getUnionData().getUnionBase();
			if (unionBase == null) {
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_TERRITORY_NOT_EXIST));
			}
			// 是否有公会
			if (!role.hasUnion()) {
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NOT));
			}
			
			if(role.getMapId().equals(unionBase.getMapId())){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_TERRITORY_IN_ERR));
			}
			
			Point targetPoint = MapUtil.randomCorrectRoadPoint(unionBase.getMapId());
			GameContext.getUserMapApp().changeMap(role, targetPoint);
		}catch(Exception e){
			logger.error("union joinUnionTerritory",e.getMessage());
		}
		return result.success();
	}


	@Override
	public void joinUnionBuff(RoleInstance role) {
		try {
			UnionBase unionBase = getUnionData().getUnionBase();
			if (unionBase == null) {
				return;
			}
			// 是否有公会
			if (!role.hasUnion()) {
				return;
			}
			
			Point transferPoint = new Point(unionBase.getMapId(),unionBase.getTransferX(),unionBase.getTransferY());
		
			GameContext.getUserMapApp().changeMap(role, transferPoint);
		} catch (ServiceException e) {
			logger.error("union joinUnionBuff",e);
		}
	}


	@Override
	public Result receiveBuff(RoleInstance role) {
		Result result = new Result();
		try{
			Union union = getUnion(role);
			
			UnionUpgrade upgrade = getUnionData().getUnionUpgrade(union.getUnionLevel());
			if(upgrade == null || upgrade.getBuffId() == 0){
				result.setInfo(GameContext.getI18n().getText(TextId.UNION_BUFF_LEVEL_ERR));
				return result;
			}
			
			int roleCount = role.getRoleCount().getRoleTimesToByte(CountType.UnionBuff);
			if(roleCount >= upgrade.getBuffNum()){
				result.setInfo(GameContext.getI18n().getText(TextId.UNION_BUFF_RECEIVE_ALREADY));
				return result;
			}
			
			UnionMember member = union.getUnionMember(role.getIntRoleId());
			if(member.getDkp() < upgrade.getBuffDkp()){
				result.setInfo(GameContext.getI18n().getText(TextId.UNION_BUFF_RECEIVE_DKP_ERR));
				return result;
			}
			
			GameContext.getUnionApp().changeMemberDkp(role, upgrade.getBuffDkp(), OperatorType.Decrease,FunType.buyUnionBuff,false);                                      
			
			result = GameContext.getUserBuffApp().addBuffStat(role,role,upgrade.getBuffId(), upgrade.getBuffLevel());
			if(result.isSuccess()){
				Buff buff = GameContext.getBuffApp().getBuff(upgrade.getBuffId());
				role.getRoleCount().changeTimes(CountType.UnionBuff);
				result.setInfo(GameContext.getI18n().messageFormat(TextId.UNION_BUFF_RECEIVE_SUCCESS,buff.getBuffName()));
			}
		}catch(Exception e){
			logger.error("union receiveBuff",e);
		}
		return result;
	}


	@Override
	public Result summonNpc(RoleInstance role,boolean isBroadCast,boolean isSystem) {
		Result result = new Result();
		try{
			
			Union union = getUnion(role);
			
			if(!union.getPowerTypeSet(role).contains(UnionPowerType.UnionSummon)){
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_SUMMON_POWER_ERR));
			}
			
			UnionUpgrade upgrade = getUnionData().getUnionUpgrade(union.getUnionLevel());
			if(upgrade == null || upgrade.getGroupId() == 0){
				result.setInfo(GameContext.getI18n().getText(TextId.UNION_SUMMON_LEVEL_ERR));
				return result;
			}
			
			UnionSummon summon = getUnionData().getUnionSummon(upgrade.getGroupId());
			if(summon == null){
				result.setInfo(GameContext.getI18n().getText(TextId.UNION_SUMMON_LEVEL_ERR));
				return result;
			}
			
			if(!isSystem){
				if(union.isSummonFlag()){
					result.setInfo(GameContext.getI18n().getText(TextId.UNION_SUMMON_ALREADY));
					return result;
				}
			}
			
			union.setBossState(true);
			
			if(!isSystem){
				union.setSummonFlag(true);
				union.setSummonTime(System.currentTimeMillis());
				saveOrUpdUnion(union);
			}
			NpcInstance summonNpc = role.getMapInstance().summonUnionCreateNpc(summon.getSummonId(), summon.getSummonX(), summon.getSummonY());
			role.getMapInstance().addAbstractRole(summonNpc);
			
			MapUnionTerritoryInstance territoryInst = (MapUnionTerritoryInstance)role.getMapInstance();
			territoryInst.setSummonNpc(summonNpc);
			territoryInst.setSummonGroupId(summon.getGroupId());
			
			for (RoleInstance ri : role.getMapInstance().getRoleList()) {
				C0204_MapUserEntryNoticeRespMessage msg = new C0204_MapUserEntryNoticeRespMessage();
				msg.setItem(Converter.getNpcBodyItem(summonNpc, ri));
				GameContext.getMessageCenter().send("", ri.getUserId(), msg);
			}
			
			result.success();
			
			result.setInfo(GameContext.getI18n().messageFormat(TextId.UNION_SUMMON_SUCCESS,summonNpc.getNpcname()));
			
			if(isBroadCast){
				summonBroadCast(union,TextId.UNION_SUMMON_NOTIFY,summonNpc.getNpcname());
				UnionRecord record = new UnionRecord();
				record.setType(UnionRecordType.Union_Record_Summon_BOSS.getType());
				record.setData2(summonNpc.getNpcname());
				record.setUnionId(union.getUnionId());
				saveOrUpdUnionRecord(union,record); 
			}
			
		}catch(Exception e){
			logger.error("union summonNpc",e);
		}
		return result;
	}

	private void summonBroadCast(Union union,String textId,String npcName){
		for(UnionMember member : union.getUnionMemberList()){
			RoleInstance r = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(member.getRoleId()));
			if(r != null){
				String message = GameContext.getI18n().messageFormat(textId, npcName);
				GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, message, null, null);
			}
		}
	}

	@Override
	public Result joinUnionSummon(RoleInstance role) {
		Result result = new Result();
		try {
			UnionBase unionBase = getUnionData().getUnionBase();
			if (unionBase == null) {
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_TERRITORY_NOT_EXIST));
			}
			// 是否有公会
			if (!role.hasUnion()) {
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NOT));
			}
			
			Union union = getUnion(role);
			
			UnionUpgrade upgrade = getUnionData().getUnionUpgrade(union.getUnionLevel());
			if(upgrade == null){
				result.setInfo(GameContext.getI18n().getText(TextId.UNION_SUMMON_LEVEL_ERR));
				return result;
			}
			
			UnionSummon summon = getUnionData().getUnionSummon(upgrade.getGroupId());
			if(summon == null){
				result.setInfo(GameContext.getI18n().getText(TextId.UNION_SUMMON_LEVEL_ERR));
				return result;
			}
			
			Point transferPoint = new Point(unionBase.getMapId(),summon.getTransferX(),summon.getTransferY());
			
			GameContext.getUserMapApp().changeMap(role, transferPoint);
			
		}catch(Exception e){
			logger.error("union joinUnionSummon",e.getMessage());
		}
		return result.success();
	}


	@Override
	public void killSummonNpc(String unionId,String npcName,int groupId,List<String> hatredList) {
		UnionSummon summon = getUnionData().getUnionSummon(groupId);
		if(summon == null){
			return;
		}
		
		//添加物品
		List<GoodsOperateBean> goodsList = Lists.newArrayList();
		goodsList.add(new GoodsOperateBean(summon.getGoodsId(), summon.getGoodsNum(), BindingType.already_binding));
		
		MailAttriBean attriBean = new MailAttriBean();
		attriBean.setDkp(summon.getDkp());
		
		UnionMail mail = GameContext.getUnionDataApp().getUnionMail((byte)2);

		GoodsBase goods = GameContext.getGoodsApp().getGoodsBase(summon.getGoodsId());
		
		String content = MessageFormat.format(mail.getContent(),goods.getName(),summon.getDkp());
		
		// 背包满了则发邮件
		if(!Util.isEmpty(goodsList)){
			for(String roleId : hatredList){
				//异步发邮件
				GameContext.getMailApp().sendMailAsync(roleId, mail.getTitle(), content, 
						MailSendRoleType.System.getName(), OutputConsumeType.hero_arena_Reward.getType(),goodsList,attriBean);
			}
		}
		
		Union union = getUnion(unionId);
		
		UnionRecord record = new UnionRecord();
		record.setType(UnionRecordType.Union_Record_KILL_BOSS.getType());
		record.setData2(npcName);
		record.setUnionId(unionId);
		saveOrUpdUnionRecord(union,record); 
		//boss被击杀状态
		union.setBossState(false);
		saveOrUpdUnion(union);
		
		summonBroadCast(union,TextId.UNION_SUMMON_KILL,npcName);
	}


	@Override
	public C1727_UnionDonateInfoRespMessage getUnionDonateInfo(RoleInstance role) {
		C1727_UnionDonateInfoRespMessage resp = new C1727_UnionDonateInfoRespMessage();
		
		//捐献
		UnionDonateItem item = new UnionDonateItem();
		//金币
		UnionDonate donate = getUnionDonate(role);
		if(donate != null){
			item.setMoneyType((byte)0);
			item.setDkp(donate.getAddDkp());
			item.setMaxCounts(donate.getMaxCount());
			item.setMoneyNum(donate.getMoney());
			byte count = role.getRoleCount().getRoleTimesToByte(CountType.UnionMemberDonate);
			item.setCounts(count);
			resp.getItemList().add(item);
		}
		
		//钻石
		UnionDonateItem gemItem = new UnionDonateItem();
		UnionGemDonate gemDonate = getUnionGemDonate(role);
		if(gemDonate != null){
			gemItem.setMoneyType((byte)1);
			gemItem.setDkp(gemDonate.getAddDkp());
			gemItem.setMaxCounts(gemDonate.getMaxCount());
			gemItem.setMoneyNum(gemDonate.getGem());
			byte count = role.getRoleCount().getRoleTimesToByte(CountType.UnionMemberGemDonate);
			gemItem.setCounts(count);
			resp.getItemList().add(gemItem);
		}
		return resp;
	}


	@Override
	public Result openUnionShop(RoleInstance role) {
		Result result = new Result();
		try {
			UnionBase unionBase = getUnionData().getUnionBase();
			if (unionBase == null) {
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_TERRITORY_NOT_EXIST));
			}
			// 是否有公会
			if (!role.hasUnion()) {
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_NOT));
			}
			C1618_ShopSecretOpenPanelReqMessage req = new C1618_ShopSecretOpenPanelReqMessage();
			req.setShopId(unionBase.getUnionShopId());
			role.getBehavior().addEvent(req);
		}catch(Exception e){
			logger.error("union joinUnionTerritory",e.getMessage());
		}
		return result.success();
	}


    /**
     * 添加成员
     * @param unionId
     * @param role
     * @return
     */
    private UnionMember createMember(String unionId,RoleInstance role,byte type){
        UnionMember addMember = new UnionMember();
        addMember.setCreateTime(System.currentTimeMillis());
        addMember.setLevel(role.getLevel());
        addMember.setOccupation(role.getCareer());
        addMember.setPosition(type);
        addMember.setRoleId(role.getIntRoleId());
        addMember.setRoleName(role.getRoleName());
        addMember.setUnionId(unionId);
        addMember.setUserId(role.getUserId());
        return addMember;
    }
}
