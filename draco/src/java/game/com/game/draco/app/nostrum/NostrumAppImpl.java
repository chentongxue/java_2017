package com.game.draco.app.nostrum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsNostrum;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.nostrum.config.NostrumLimitConfig;
import com.game.draco.app.nostrum.domain.RoleNostrum;
import com.game.draco.app.nostrum.vo.NostrumRoleData;
import com.game.draco.message.response.C1912_NostrumUseRespMessage;
import com.google.common.collect.Maps;

public class NostrumAppImpl implements NostrumApp {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<Integer> goodsIdList = new ArrayList<Integer>();
	private Map<Integer,List<NostrumLimitConfig>> limitConfigMap = Maps.newHashMap();
	private Map<String,NostrumRoleData> roleDataMap = Maps.newConcurrentMap() ;
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadXlsConfig();
	}

	@Override
	public void stop() {
		
	}
	
	private void loadXlsConfig(){
		String fileName = XlsSheetNameType.goods_nostrum_show.getXlsName();
		String sheetName = XlsSheetNameType.goods_nostrum_show.getSheetName();
		String info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName + ".";
		try {
			String xlsPath = GameContext.getPathConfig().getXlsPath();
			//①加载秘药列表
			List<String> idList = XlsPojoUtil.sheetToStringList(xlsPath + fileName, sheetName);
			for(String id : idList){
				if(Util.isEmpty(id)){
					continue;
				}
				try {
					int goodsId = Integer.valueOf(id);
					if(this.goodsIdList.contains(goodsId)){
						this.checkFail(info + "goodsId = " + id + ", it's repeated.");
						continue;
					}
					GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
					if(null == gb){
						this.checkFail(info + "goodsId = " + id + ", goods is not exist.");
						continue;
					}
					if(GoodsType.GoodsNostrum.getType() != gb.getGoodsType()){
						this.checkFail(info + "goodsId = " + id + ", goodsType must be [GoodsNostrum].");
						continue;
					}
					this.goodsIdList.add(goodsId);
				} catch (Exception e) {
					this.checkFail(info + "goodsId = " + id + ", GoodsId must be a positive integer.");
				}
			}
			fileName = XlsSheetNameType.goods_nostrum_limit.getXlsName();
			sheetName = XlsSheetNameType.goods_nostrum_limit.getSheetName();
			info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName + ".";
			//②加载秘药等级限制
			List<NostrumLimitConfig> limitList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, NostrumLimitConfig.class);
			for(NostrumLimitConfig config : limitList){
				if(null == config){
					continue;
				}
				int goodsId = config.getGoodsId();
				if(!this.goodsIdList.contains(goodsId)){
					this.checkFail(info + ", goodsId = " + goodsId + ", it's not in the [show_list].");
					continue;
				}
				config.checkInit(info);
				if(!this.limitConfigMap.containsKey(goodsId)){
					this.limitConfigMap.put(goodsId, new ArrayList<NostrumLimitConfig>());
				}
				this.limitConfigMap.get(goodsId).add(config);
			}
		} catch (Exception e) {
			this.checkFail(info);
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

	@Override
	public List<Integer> getGoodsIdList() {
		return this.goodsIdList;
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		try {
			String roleId = role.getRoleId();
			NostrumRoleData roleData = new NostrumRoleData();
			roleData.setRoleId(roleId);
			List<RoleNostrum> list = GameContext.getBaseDAO().selectList(RoleNostrum.class, RoleNostrum.ROLEID, roleId);
			if(!Util.isEmpty(list)){
				for(RoleNostrum rn : list){
					if(null == rn){
						continue;
					}
					roleData.addRoleNostrum(rn);
				}
			}
			this.roleDataMap.put(roleId, roleData);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".login error: ", e);
			return 0;
		}
		
		return 1;
	}
	
	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			this.roleDataMap.remove(role.getRoleId());
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".logout error: ", e);
			return 0;
		}
		
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		this.roleDataMap.remove(roleId);
		return 0;
	}

	@Override
	public Map<Integer, List<NostrumLimitConfig>> getLimitConfigMap() {
		return this.limitConfigMap;
	}
	
	@Override
	public NostrumRoleData getNostrumRoleData(String roleId) {
		return this.roleDataMap.get(roleId);
	}
	
	@Override
	public short getMaxNumber(RoleInstance role, int goodsId) {
		NostrumLimitConfig config = this.getNostrumLimitConfig(role, goodsId);
		if(null != config){
			return config.getLimitNum();
		}
		return 0;
	}
	
	private NostrumLimitConfig getNostrumLimitConfig(RoleInstance role, int goodsId){
		List<NostrumLimitConfig> list = GameContext.getNostrumApp().getLimitConfigMap().get(goodsId);
		if(Util.isEmpty(list)){
			return null;
		}
		for(NostrumLimitConfig config : list){
			if(null == config){
				continue;
			}
			if(config.isSuitLevel(role)){
				return config;
			}
		}
		return null;
	}

	@Override
	public int getCurrNumber(RoleInstance role, int goodsId) {
		RoleNostrum rn = this.getRoleNostrum(role.getRoleId(), goodsId);
		if(null != rn){
			return rn.getGoodsNum();
		}
		return 0;
	}
	
	private RoleNostrum getRoleNostrum(String roleId, int goodsId){
		NostrumRoleData roleData = this.getNostrumRoleData(roleId);
		if(null == roleData){
			return null;
		}
		return roleData.getRoleNostrum(goodsId);
	}

	@Override
	public List<GoodsNostrum> getGoodsNostrumList() {
		List<GoodsNostrum> list = new ArrayList<GoodsNostrum>();
		for(int goodsId : GameContext.getNostrumApp().getGoodsIdList()){
			GoodsNostrum gn = this.getGoodsNostrum(goodsId);
			if(null == gn){
				continue;
			}
			list.add(gn);
		}
		return list;
	}
	
	private GoodsNostrum getGoodsNostrum(int goodsId){
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null == gb){
			return null;
		}
		if(gb instanceof GoodsNostrum){
			return (GoodsNostrum) gb;
		}
		return null;
	}

	@Override
	public int getAttrValue(RoleInstance role, int goodsId) {
		RoleNostrum rn = this.getRoleNostrum(role.getRoleId(), goodsId);
		GoodsNostrum gn = this.getGoodsNostrum(goodsId);
		if(null == rn || null == gn){
			return 0;
		}
		return gn.getAttrValue() * rn.getGoodsNum();
	}

	@Override
	public void useNostrum(RoleInstance role, int goodsId) {
		try {
			GoodsNostrum goodsNostrum = this.getGoodsNostrum(goodsId);
			if(null == goodsNostrum){
				this.responseUseNostrumFailed(role, this.getText(TextId.Nostrum_Goods_Not_Exist));
				return ;
			}
			//判断能否使用
			Result result = this.canUseNostrum(role, goodsId);
			if(!result.isSuccess()){
				this.responseUseNostrumFailed(role, result.getInfo());
				return ;
			}
			//扣除物品
			result = GameContext.getUserGoodsApp().deleteForBagByGoodsId(role, goodsId, OutputConsumeType.goods_nostrum_use);
			if(!result.isSuccess()){
				this.responseUseNostrumFailed(role, result.getInfo());
				return ;
			}
			//增加属性
			this.doUseNostrum(role, goodsNostrum);
			//更新面板
			C1912_NostrumUseRespMessage message = new C1912_NostrumUseRespMessage();
			message.setType((byte) 1);
			message.setGoodsId(goodsId);
			message.setCurrNum((short) this.getCurrNumber(role, goodsId));
			message.setMaxNum(this.getMaxNumber(role, goodsId));
			message.setAttrValue(this.getAttrValue(role, goodsId));
			this.sendMessage(role, message);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".useNostrum error: ", e);
		}
	}
	
	private String getText(String i18nKey){
		return GameContext.getI18n().getText(i18nKey);
	}
	
	private void responseUseNostrumFailed(RoleInstance role, String info){
		C1912_NostrumUseRespMessage message = new C1912_NostrumUseRespMessage();
		message.setType((byte) 0);
		message.setInfo(info);
		this.sendMessage(role, message);
	}
	
	private void sendMessage(RoleInstance role, Message message){
		GameContext.getMessageCenter().sendSysMsg(role, message);
	}

	@Override
	public Result useNostrum(RoleInstance role, RoleGoods roleGoods) {
		Result result = new Result();
		try {
			if(null == role || null == roleGoods || !role.getRoleId().equals(roleGoods.getRoleId())){
				return result.setInfo(this.getText(TextId.GOODS_NO_FOUND));
			}
			GoodsNostrum goodsNostrum = this.getGoodsNostrum(roleGoods.getGoodsId());
			if(null == goodsNostrum){
				return result.setInfo(this.getText(TextId.Nostrum_Goods_Not_Exist));
			}
			//判断能否使用
			result = this.canUseNostrum(role, roleGoods.getGoodsId());
			if(!result.isSuccess()){
				return result;
			}
			//扣除物品
			result = GameContext.getUserGoodsApp().deleteForBagByInstanceId(role, roleGoods.getId(), 1, OutputConsumeType.goods_nostrum_use);
			//增加属性
			this.doUseNostrum(role, goodsNostrum);
			return result.success();
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".useNostrum error: ", e);
			return result.setInfo(this.getText(TextId.GOODS_NO_FOUND));
		}
	}
	
	private Result canUseNostrum(RoleInstance role, int goodsId){
		Result result = new Result();
		int currNum = this.getCurrNumber(role, goodsId);
		int maxNum = this.getMaxNumber(role, goodsId);
		if(currNum >= maxNum){
			return result.setInfo(this.getText(TextId.Nostrum_Use_Fail_MaxNumber));
		}
		return result.success();
	}
	
	private void doUseNostrum(RoleInstance role, GoodsNostrum goodsNostrum){
		try {
			String roleId = role.getRoleId();
			NostrumRoleData roleData = this.getNostrumRoleData(roleId);
			roleData.updateNostrumNum(goodsNostrum.getId(), 1);
			//修改属性
			AttributeType attrType = AttributeType.get(goodsNostrum.getAttrType());
			role.getBehavior().changeAttribute(attrType, OperatorType.Add, goodsNostrum.getAttrValue());
			role.getBehavior().notifyAttribute();
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".doUseNostrum error: ", e);
		}
	}
	
	@Override
	public AttriBuffer getAttriBuffer(RoleInstance role) {
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		try {
			NostrumRoleData roleData = this.getNostrumRoleData(role.getRoleId());
			for(RoleNostrum rn : roleData.getNostrumMap().values()){
				if(null == rn){
					continue;
				}
				GoodsNostrum gn = this.getGoodsNostrum(rn.getGoodsId());
				if(null == gn){
					continue;
				}
				float value = gn.getAttrValue() * rn.getGoodsNum();
				buffer.append(gn.getActivateType(), value);
			}
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".getAttriBuffer error: ", e);
		}
		return buffer;
	}
	
}
