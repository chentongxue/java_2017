package com.game.draco.app.talent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.AttributeOperateLevelBean;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.talent.config.TalentBase;
import com.game.draco.app.talent.config.TalentCondition;
import com.game.draco.app.talent.config.TalentConsumeInfo;
import com.game.draco.app.talent.config.TalentDes;
import com.game.draco.app.talent.config.TalentGroup;
import com.game.draco.app.talent.config.TalentInfo;
import com.game.draco.app.talent.config.TalentLevelUp;
import com.game.draco.app.talent.config.TalentShop;
import com.game.draco.app.talent.domain.RoleTalent;
import com.game.draco.app.talent.domain.RoleTalentTemp;
import com.game.draco.app.talent.fun.TalentType;
import com.game.draco.app.talent.vo.RoleTrainTalentResult;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.RoleTalentConsumeItem;
import com.game.draco.message.item.RoleTalentInfoItem;
import com.game.draco.message.item.RoleTalentRefItem;
import com.game.draco.message.item.RoleTalentTempItem;
import com.game.draco.message.response.C2816_RoleTalentListRespMessage;
import com.game.draco.message.response.C2817_RoleTalentRefRespMessage;
import com.game.draco.message.response.C2819_RoleTrainTalentInfoRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class RoleTalentAppImpl implements RoleTalentApp {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	//角色天赋数据
	@Getter
	private Map<Integer,RoleTalent> roleTalentMap = Maps.newConcurrentMap();
	
	//角色临时天赋数据
	@Getter
	private Map<Integer,RoleTalent> roleTempTalentMap = Maps.newConcurrentMap();
	
	//角色临时操作数据
	@Getter
	private Map<Integer,Byte> roleTempOperationMap = Maps.newConcurrentMap();
	
	@Override
	public int onLogin(RoleInstance role, Object context) {
		try{
			// 初始化角色天赋数据
			RoleTalent talent = GameContext.getBaseDAO().selectEntity(RoleTalent.class, RoleTalent.ROLE_ID,role.getIntRoleId());
			if(talent == null){
				talent = initRoleTalent(role);
			}
			roleTalentMap.put(role.getIntRoleId(), talent);
		}catch(Exception e){
			logger.error("login roleTalent ERR",e);
			return 0;
		}
		return 1;
	}
	
	private void notifyAttr(RoleInstance role,AttriBuffer additionBuffer){
		GameContext.getUserAttributeApp().changeAttribute(role, additionBuffer);
		role.getBehavior().notifyAttribute();
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		delTempTalent(role.getIntRoleId());
		roleTalentMap.remove(role.getIntRoleId());
		return 1;
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		int intRoleId = Integer.parseInt(roleId);
		delTempTalent(intRoleId);
		roleTalentMap.remove(intRoleId);
		return 1 ;
	}
	
	private RoleTalent initRoleTalent(RoleInstance role){
		try{
			Map<Integer, TalentInfo> map = GameContext.getTalentApp().getTalentInfoMap();
			RoleTalent talent = new RoleTalent();
			talent.setRoleId(role.getIntRoleId());
			for(Entry<Integer, TalentInfo> talentInfo : map.entrySet()){
				talent.updateTalent(talentInfo.getKey(), talentInfo.getValue().getAttrValue());
			}
			TalentLevelUp levelInfo = GameContext.getTalentApp().getTalentLevelUp(role.getLevel());
			talent.setTalent(levelInfo.getSumTalent());
			roleTalentMap.put(talent.getRoleId(), talent);
			return talent;
		}catch(Exception e){
			logger.error("init roleTalent ERR",e);
		}
		return null;
	}
	
	private void saveOrUpdateRoleTalent(RoleTalent talent){
		try{
			GameContext.getBaseDAO().saveOrUpdate(talent);
		}catch(Exception e){
			logger.error("saveOrUpd roleTalent ERR",e);
		}
	}
	
	@Override
	public RoleTrainTalentResult trainTalent(RoleInstance role,byte type){
		RoleTrainTalentResult result = new RoleTrainTalentResult();
		try{
			//检查是否满足条件
			result = isCondition(role,type);
			if(!result.isSuccess()){
				return result;
			}
			
			result = consume(role,type);
			if(!result.isSuccess()){
				return result;
			}
			//删除历史数据
			delTempTalent(role.getIntRoleId());
			RoleTalent roleTalent = getRoleTalent(role.getIntRoleId());
			
			TalentGroup talentGroup = null;
			List<TalentGroup> groupList = GameContext.getTalentApp().getTalentGroupList(type);
			Map<Integer,TalentInfo> talentInfoMap = GameContext.getTalentApp().getTalentInfoMap();
			for(TalentGroup group : groupList){
				if(talentGroup != null){
					break;
				}
				for(Entry<Integer,TalentInfo> info : talentInfoMap.entrySet()){
					if(roleTalent.talentValue(info.getKey()) >= group.getGe()){
						talentGroup = group;
						break;
					}
					if(roleTalent.talentValue(info.getKey()) < group.getLt()){
						talentGroup = group;
						break;
					}
				}
			}
			
			TalentCondition condition = GameContext.getTalentApp().getTalentCondition(talentGroup.getConditionId());
			
			//保存角色操作数据
			roleTempOperationMap.put(role.getIntRoleId(), condition.getId());
			
			TalentLevelUp levelUp = GameContext.getTalentApp().getTalentLevelUp(role.getLevel());
			
			//培养最大值
			int maxTalent = levelUp.getMaxTalent();
			
			RoleTalent temp = new RoleTalent() ;
			org.springframework.beans.BeanUtils.copyProperties(roleTalent, temp);
			
			roleTempTalentMap.put(temp.getRoleId(), temp);
			
			List<RoleTalentTemp> list = roleTalent.getRoleTalentTempList();
			
			Map<Integer,Integer> rankMap = GameContext.getTalentApp().getTalentRankMap();
			Map<Integer,List<RoleTalentTemp>> talentRankMap = Maps.newConcurrentMap();
			//取第几个
			int i = 0;
			int talentValue = 0;
			for(RoleTalentTemp talent : list){
				if(talentValue == talent.getTalent()){
					talentRankMap.get(i).add(talent);
				}else{
					i++;
					List<RoleTalentTemp> rList = Lists.newArrayList();
					rList.add(talent);
					talentRankMap.put(i,rList);
				}
			}
			Map<Integer,Integer> factorMap = Maps.newHashMap();
			
			for(Entry<Integer,List<RoleTalentTemp>> rank : talentRankMap.entrySet()){
				factorMap.put(rank.getKey(), rankMap.get(rank.getKey()));
			}
			
			for(int k = 0;k < factorMap.size(); k++){
				//A
				int index = Util.getWeightCalct(factorMap);
				
				List<RoleTalentTemp> talentList = talentRankMap.get(index);
				
				Random rand = new Random();
				int listIndex = rand.nextInt(talentList.size());
				RoleTalentTemp roleTalentTemp = talentList.get(listIndex);
				int tempARange = condition.getARangeMax() - condition.getARangeMin() + 1;
				int talent = rand.nextInt(tempARange) + condition.getARangeMin();
				if(roleTalentTemp.getTalent() < talent){
					continue;
				}
				temp.updateTalent(roleTalentTemp.getTalentId(),roleTalent.talentValue(roleTalentTemp.getTalentId()) - talent);
				factorMap.remove(index);
				
				//B
				
				for(int b=0;b<list.size();b++){
					int bTalentIndex = rand.nextInt(list.size());
					RoleTalentTemp bRTalent = list.get(bTalentIndex);
					if(roleTalentTemp.getTalentId() == bRTalent.getTalentId() 
							|| bRTalent.getTalent() > maxTalent){
						continue;
					}
					temp.updateTalent(bRTalent.getTalentId(), bRTalent.getTalent() + talent + condition.getAddB());
					break;
				}
				break;
			}
			result.setTemp(temp);
		}catch(Exception e){
			logger.error("TrainTalent ERR type=" + type ,e);
		}
		return result;
	}
	
	@Override
	public C2817_RoleTalentRefRespMessage sendC2817_RoleTalentRefRespMessage(RoleInstance role,RoleTalent temp){
		//临时数据
		C2817_RoleTalentRefRespMessage respMsg = new C2817_RoleTalentRefRespMessage();
		List<RoleTalentTemp> list = temp.getRoleTalentRankList();
		List<RoleTalentTempItem> tempTalentItem = Lists.newArrayList();
		for(RoleTalentTemp talentTemp : list){
			RoleTalentTempItem refItem = new RoleTalentTempItem();
			refItem.setTalentId(talentTemp.getTalentId());
			refItem.setTalent(talentTemp.getTalent());
			tempTalentItem.add(refItem);
		}
		
		respMsg.setTalentItem(tempTalentItem);
		return respMsg;
	}

	@Override
	public C2816_RoleTalentListRespMessage sendC2816_RoleTalentListRespMessage(
			RoleInstance role) {
		C2816_RoleTalentListRespMessage respMsg = new C2816_RoleTalentListRespMessage();
		List<RoleTalentInfoItem> talentItem = new ArrayList<RoleTalentInfoItem>();
		RoleTalent talent = getRoleTalent(role.getIntRoleId());
		Map<Integer, List<TalentBase>> baseMap = GameContext.getTalentApp().getTalentBaseMap();
		TalentLevelUp levelUp = GameContext.getTalentApp().getTalentLevelUp(role.getLevel());
		
		respMsg.setMaxTalent(levelUp.getMaxTalent());
		
		Map<Integer,TalentInfo> talentInfoMap = GameContext.getTalentApp().getTalentInfoMap();
		for(Entry<Integer,TalentInfo> map : talentInfoMap.entrySet()){
			RoleTalentInfoItem item = new RoleTalentInfoItem();
			item.setTalentId(map.getKey());
			item.setTalent(talent.talentValue(map.getKey()));
			item.setIconId(map.getValue().getIconId());
			item.setTalentName(map.getValue().getName());
			List<TalentBase> talentList = baseMap.get(map.getKey());
			TalentDes talentDes = GameContext.getTalentApp().getTalentDes(map.getKey());
			String des = MessageFormat.format(talentDes.getDes(),talent.talentValue(map.getKey()));
			for(TalentBase base : talentList){
				des += MessageFormat.format(base.getAttrDes(), (float)(talent.talentValue(map.getKey()) / 10));
			}
			item.setDes(des);
			talentItem.add(item);
		}
		Collections.sort(talentItem,roleTalentItemComparator);
		respMsg.setTalentItem(talentItem);
		return respMsg;
	}
	
	/**
	 * 检查是否满足条件
	 * @param role
	 * @param type
	 * @return
	 */
	private RoleTrainTalentResult isCondition(RoleInstance role,byte type){
		RoleTrainTalentResult result = new RoleTrainTalentResult();
		TalentConsumeInfo consume = GameContext.getTalentApp().getTalentConsumeInfo(type);
		
		//判断背包里是否有该物品
		for(GoodsLiteNamedItem goodsItem : consume.getGoodsGroup()){
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsItem.getGoodsId());
			boolean flag = GameContext.getUserGoodsApp().isExistGoodsForBag(role, goodsItem.getGoodsId());
			if(!flag){
				String goodsName = Wildcard.getQualityGoodsName(goodsBase) ;
				result.setInfo(GameContext.getI18n().messageFormat(TextId.TALENT_NO_GOODS,goodsName));
				return result;
			}
		}
		
		AttributeOperateLevelBean attrBean = getTalentConsumeInfo(role,consume.getAttrList());
		if(attrBean != null){
			Result ar = GameContext.getUserAttributeApp().getEnoughResult(
					role, attrBean.getAttrType(), attrBean.getValue());
			if (ar.isIgnore()) {
				return null;
			}
			if (!ar.isSuccess()) {
				result.setInfo(GameContext.getI18n().messageFormat(TextId.TALENT_NO_ATTR,attrBean.getAttrType().getName()));
				return result;
			}
		}
		result.success();
		return result;
	}
	
	/**
	 * 消耗物品属性
	 * @param role
	 * @param type
	 * @return
	 */
	private RoleTrainTalentResult consume(RoleInstance role,byte type){
		RoleTrainTalentResult result = new RoleTrainTalentResult();
		TalentConsumeInfo consume = GameContext.getTalentApp().getTalentConsumeInfo(type);
		//判断背包里是否有该物品
		for(GoodsLiteNamedItem goodsItem : consume.getGoodsGroup()){
			 //扣除物品
			GoodsResult gr = GameContext.getUserGoodsApp().deleteForBag(role, goodsItem.getGoodsId(),goodsItem.getNum(),
					OutputConsumeType.talent_item_consume);
			if(!gr.isSuccess()){
				result.failure();
				result.setInfo(gr.getInfo());
				return result;
			}
		}
		
		AttributeOperateLevelBean attrBean = getTalentConsumeInfo(role,consume.getAttrList());
		if(attrBean != null){
			Result ar = GameContext.getUserAttributeApp().getEnoughResult(
					role, attrBean.getAttrType(), attrBean.getValue());
			if (ar.isIgnore()) {
				return null;
			}
			if (!ar.isSuccess()) {
				result.setInfo(GameContext.getI18n().messageFormat(TextId.TALENT_NO_ATTR,attrBean.getAttrType().getName()));
				return result;
			}
			GameContext.getUserAttributeApp().changeAttribute(role,
					attrBean.getAttrType(), OperatorType.Decrease,
					attrBean.getValue(), OutputConsumeType.talent_attr_consume);
			// 通知用户属性变化 消耗提示
			role.getBehavior().notifyAttribute();
		}
		result.success();
		return result;
	}
	
	Comparator<RoleTalentInfoItem> roleTalentItemComparator = new Comparator<RoleTalentInfoItem>(){
		@Override
		public int compare(RoleTalentInfoItem t1, RoleTalentInfoItem t2) {
			if(t1.getTalentId() < t2.getTalentId()){
				return -1;
			}
			if(t1.getTalentId() > t2.getTalentId()){
				return 1;
			}
			return 0;
		}
	} ;
	
	Comparator<RoleTalentRefItem> roleTalentRefItemComparator = new Comparator<RoleTalentRefItem>(){
		@Override
		public int compare(RoleTalentRefItem t1, RoleTalentRefItem t2) {
			if(t1.getTalentId() < t2.getTalentId()){
				return -1;
			}
			if(t1.getTalentId() > t2.getTalentId()){
				return 1;
			}
			return 0;
		}
	} ;
	
	
	public void delTempTalent(int roleId) {
		roleTempTalentMap.remove(roleId);
		roleTempOperationMap.remove(roleId);
	}

	@Override
	public Result saveTempTalent(RoleInstance role) {
		Result result = new Result();
		try{
			 Byte conditionId = roleTempOperationMap.get(role.getIntRoleId());
			 RoleTalent roleTalentTemp = roleTempTalentMap.get(role.getIntRoleId());
			 if(null == conditionId || null == roleTalentTemp){
				 result.setInfo(GameContext.getI18n().getText(TextId.TALENT_SAVE_OPERATION_FAIL));
					return result;
			 }
			TalentCondition condition = GameContext.getTalentApp().getTalentCondition(conditionId.byteValue());
			RoleTalent roleTalent = roleTalentMap.get(role.getIntRoleId());
			if(roleTalent.getTalent() < condition.getTempTalentNum()){
				result.setInfo(GameContext.getI18n().getText(TextId.TALENT_SAVE_FAIL));
				return result;
			}
			
			AttriBuffer additionBuffer = AttriBuffer.createAttriBuffer();
			//减老属性
			changeAttr(role, additionBuffer,true);
			
			roleTalentTemp.setTalent(roleTalent.getTalent() - condition.getTempTalentNum());
			roleTalentMap.put(role.getIntRoleId(),roleTalentTemp);
			//删除临时数据
			this.delTempTalent(role.getIntRoleId());
			//实时入库
			this.saveOrUpdateRoleTalent(roleTalentTemp);
			//加新属性
			changeAttr(role, additionBuffer,false);
			notifyAttr(role, additionBuffer);
			result.success();
			result.setInfo(GameContext.getI18n().getText(TextId.TALENT_SAVE_SUCCESS));
		}catch(Exception e){
			logger.error("Save TrainTalent ERR",e);
		}
		return result;
	}
	
	/**
	 * 属性变化
	 * @param role
	 */
	private void changeAttr(RoleInstance role,AttriBuffer additionBuffer ,boolean flag){
		RoleTalent talent = getRoleTalent(role.getIntRoleId());
		Map<Integer, List<TalentBase>> map = GameContext.getTalentApp().getTalentBaseMap();
		Map<Integer,TalentInfo> talentInfoMap = GameContext.getTalentApp().getTalentInfoMap();
		for(Entry<Integer,TalentInfo> talentInfo : talentInfoMap.entrySet()){
			List<TalentBase> list = map.get(talentInfo.getKey());
			for(TalentBase base : list){
				additionBuffer.append(base.getAttrType(), talent.talentValue(talentInfo.getKey()),false);
			}
		}
		if(flag){
			additionBuffer.reverse();
		}
	}

	@Override
	public C2819_RoleTrainTalentInfoRespMessage sendC2819_RoleTrainTalentInfoRespMessage(
			RoleInstance role) {
		
		C2819_RoleTrainTalentInfoRespMessage respMsg = new C2819_RoleTrainTalentInfoRespMessage();
		TalentShop shop = GameContext.getTalentApp().getTalentShop();
		respMsg.setShopId(shop.getShopId());
		
		List<RoleTalentRefItem> talentItem = Lists.newArrayList();
		List<RoleTalentTempItem> talentTempItem = Lists.newArrayList();
		List<AttriTypeValueItem> attrList = null;
		List<RoleTalentConsumeItem> consumeItemList = Lists.newArrayList();
		
		RoleTalent roleTalent = getRoleTalent(role.getIntRoleId());
		Map<Integer,TalentInfo> talentInfoMap = GameContext.getTalentApp().getTalentInfoMap();
		for(Entry<Integer,TalentInfo> talentInfo : talentInfoMap.entrySet()){
			RoleTalentRefItem item = new RoleTalentRefItem();
			item.setTalentId(talentInfo.getKey());
			item.setTalent(roleTalent.talentValue(talentInfo.getKey()));
			item.setTalentName(talentInfo.getValue().getName());
			talentItem.add(item);
		}
		Collections.sort(talentItem,roleTalentRefItemComparator); 
		respMsg.setTalentItemList(talentItem);
		
		//临时
		RoleTalent roleTalentTemp = roleTempTalentMap.get(role.getIntRoleId());
		if(null != roleTalentTemp){
			List<RoleTalentTemp> tempList = roleTalentTemp.getRoleTalentRankList();
			for(RoleTalentTemp temp : tempList){
				RoleTalentTempItem item = new RoleTalentTempItem();
				item.setTalentId(temp.getTalentId());
				item.setTalent(temp.getTalent());
				talentTempItem.add(item);
			}
			respMsg.setTalentTempItemList(talentTempItem);
		}
		
		for(TalentType type : TalentType.values()){
			TalentConsumeInfo consume = GameContext.getTalentApp().getTalentConsumeInfo(type.getType());
			RoleTalentConsumeItem consumeItem = new RoleTalentConsumeItem();
			attrList = Lists.newArrayList();
			AttributeOperateLevelBean attrBean = getTalentConsumeInfo(role,consume.getAttrList());
			if(attrBean != null){
				AttriTypeValueItem attrItem = new AttriTypeValueItem();
				attrItem.setAttriType(attrBean.getAttrType().getType());
				attrItem.setAttriValue(attrBean.getValue());
				attrList.add(attrItem);
			}
			consumeItem.setAttrList(attrList);
			consumeItem.setGoodsList(consume.getGoodsGroup());
			consumeItem.setType(type.getType());
			consumeItemList.add(consumeItem);
		}
		respMsg.setConsumeItemList(consumeItemList);
		return respMsg;
	}

	private AttributeOperateLevelBean getTalentConsumeInfo(RoleInstance role,List<AttributeOperateLevelBean> list){
		for(AttributeOperateLevelBean attrBean : list){
			if(role.getLevel() < attrBean.getMinLevel() || role.getLevel() > attrBean.getMaxLevel()){
				continue;
			}
			return attrBean;
		}
		return null;
	}
	
	@Override
	public RoleTalent getRoleTalent(int roleId) {
		return roleTalentMap.get(roleId);
	}

	
	@Override
	public AttriBuffer getAttriBuffer(RoleInstance role) {
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		Map<Integer, List<TalentBase>> map = GameContext.getTalentApp().getTalentBaseMap();
		RoleTalent roleTalent = getRoleTalent(role.getIntRoleId());
		for(Entry<Integer, List<TalentBase>> talentBase : map.entrySet()){
			for(TalentBase base : talentBase.getValue()){
				int attrValue = roleTalent.talentValue(base.getTalentId());
				buffer.append(base.getAttrType(),attrValue,false);
			}
		}
		return buffer;
	}

	@Override
	public void onRoleLevelUp(RoleInstance role) {
		RoleTalent roleTalent = getRoleTalent(role.getIntRoleId());
		TalentLevelUp preLevelUp = GameContext.getTalentApp().getTalentLevelUp(role.getLevel() -1);
		TalentLevelUp levelUp = GameContext.getTalentApp().getTalentLevelUp(role.getLevel());
		int talent = roleTalent.getTalent() + levelUp.getSumTalent() - preLevelUp.getSumTalent();
		if(talent <= 0){
			return;
		}
		roleTalent.setTalent(talent);
		saveOrUpdateRoleTalent(roleTalent);
		AttriBuffer additionBuffer = AttriBuffer.createAttriBuffer();
		additionBuffer.append(AttributeType.talent,roleTalent.getTalent());
		notifyAttr(role, additionBuffer);
	}
	
}