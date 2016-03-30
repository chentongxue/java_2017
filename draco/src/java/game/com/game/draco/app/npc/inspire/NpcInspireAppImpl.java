package com.game.draco.app.npc.inspire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.inspire.vo.NpcInspireFunc;
import com.game.draco.app.npc.inspire.vo.NpcInspireRatio;
import com.game.draco.app.npc.type.NpcFuncShowType;
import com.game.draco.message.item.NpcFunctionItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0613_NpcInspireReqMessage;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

public class NpcInspireAppImpl implements NpcInspireApp {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	/** Map<npcId,List<鼓舞的NPC功能项>> */
	private Map<String,List<NpcInspireFunc>> functionMap = new HashMap<String,List<NpcInspireFunc>>();
	/** Map<buffId,Map<鼓舞方式,Map<buff等级,配置信息>>> */
	private Map<Short,Map<NpcInspireType,Map<Integer,NpcInspireRatio>>> ratioMap = new HashMap<Short,Map<NpcInspireType,Map<Integer,NpcInspireRatio>>>();
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadNpcInspireConfig();
	}

	@Override
	public void stop() {
		
	}
	
	/**
	 * 加载NPC鼓舞的配置
	 */
	private void loadNpcInspireConfig(){
		String fileName= "";
		String sheetName = "";
		String info = "";
		try {
			String xlsPath = GameContext.getPathConfig().getXlsPath();
			//①加载NPC鼓舞的功能选项
			fileName = XlsSheetNameType.npc_inspire_func.getXlsName();
			sheetName = XlsSheetNameType.npc_inspire_func.getSheetName();
			info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
			List<NpcInspireFunc> funcList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, NpcInspireFunc.class);
			//功能项中的buffId，为了验证使用
			Set<Short> funcBuffIdSet = new HashSet<Short>();
			for(NpcInspireFunc func : funcList){
				if(null == func){
					continue;
				}
				//初始化验证配置信息
				func.checkAndInit(info);
				String npcId = func.getNpcId();
				if(!this.functionMap.containsKey(npcId)){
					this.functionMap.put(npcId, new ArrayList<NpcInspireFunc>());
				}
				this.functionMap.get(npcId).add(func);
				funcBuffIdSet.add(func.getBuffId());
			}
			//②加载鼓舞的buff概率配置
			fileName = XlsSheetNameType.npc_inspire_ratio.getXlsName();
			sheetName = XlsSheetNameType.npc_inspire_ratio.getSheetName();
			info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
			List<NpcInspireRatio> ratioList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, NpcInspireRatio.class);
			for(NpcInspireRatio config : ratioList){
				if(null == config){
					continue;
				}
				short buffId = config.getBuffId();
				if(!funcBuffIdSet.contains(buffId)){
					Log4jManager.CHECK.error(info + "buffId=" + buffId + ",npc_function not contains this buffId.");
					Log4jManager.checkFail();
				}
				//初始化验证配置信息
				config.checkAndInit(info);
				Map<NpcInspireType,Map<Integer,NpcInspireRatio>> map = this.ratioMap.get(buffId);
				if(null == map){
					map = new HashMap<NpcInspireType,Map<Integer,NpcInspireRatio>>();
					this.ratioMap.put(buffId, map);
				}
				NpcInspireType npcInspireType = config.getNpcInspireType();
				if(!map.containsKey(npcInspireType)){
					map.put(npcInspireType, new HashMap<Integer,NpcInspireRatio>());
				}
				map.get(npcInspireType).put(config.getBuffLevel(), config);
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error(info, e);
			Log4jManager.checkFail();
		}
	}

	@Override
	public List<NpcFunctionItem> getNpcFunction(RoleInstance role, NpcInstance npc) {
		String npcId = npc.getNpc().getNpcid();
		short cmdId = new C0613_NpcInspireReqMessage().getCommandId();
		List<NpcFunctionItem> list = new ArrayList<NpcFunctionItem>();
		List<NpcInspireFunc> inspireList = this.functionMap.get(npcId);
		if(Util.isEmpty(inspireList)){
			return list;
		}
		for(NpcInspireFunc func : inspireList){
			if(null == func){
				continue;
			}
			NpcFunctionItem item = new NpcFunctionItem();
			//继续保持父窗口
			item.setType(NpcFuncShowType.KeepParents.getType());
			item.setTitle(func.getFunctionName());
			item.setCommandId(cmdId);
			item.setParam(this.buildParam(func));
			list.add(item);
		}
		return list;
	}
	
	/**
	 * 构建NPC功能的参数
	 * @param func
	 * @return
	 */
	private String buildParam(NpcInspireFunc func){
		return "" + func.getType() + Cat.underline + func.getBuffId();
	}

	@Override
	public Result inspire(RoleInstance role, String param) {
		Result result = new Result();
		try {
			//验证参数，找到buff的信息
			if(Util.isEmpty(param)){
				return result.setInfo(Status.Sys_Param_Error.getTips());
			}
			String[] paramInfo = param.split(Cat.underline);
			if(null == paramInfo || 2 != paramInfo.length){
				return result.setInfo(Status.Sys_Param_Error.getTips());
			}
			byte type = Byte.valueOf(paramInfo[0]);
			short buffId = Short.valueOf(paramInfo[1]);
			NpcInspireType npcInspireType = NpcInspireType.get(type);
			if(buffId <=0 || null == npcInspireType){
				return result.setInfo(Status.Sys_Param_Error.getTips());
			}
			Map<Integer,NpcInspireRatio> map = this.ratioMap.get(buffId).get(npcInspireType);
			if(Util.isEmpty(map)){
				return result.setInfo(Status.Sys_Param_Error.getTips());
			}
			//鼓舞
			//需要增加的buff等级
			int buffLevel = role.getBuffLevel(buffId) + 1;
			NpcInspireRatio inspireRatio = map.get(buffLevel);
			//当前等级是最高，不能再鼓舞了，提示失败
			if(null == inspireRatio){
				//提示已经达到上限
				return result.setInfo(Status.Npc_Inspire_Max.getTips());
			}
			int costValue = inspireRatio.getCostValue();
			AttributeType attrType = npcInspireType.getAttrType();
			//【游戏币/潜能/钻石不足弹板】 判断
			Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, attrType, costValue);
			if(ar.isIgnore()){
				return ar;
			}
			if(!ar.isSuccess()){
				return result.setInfo(Status.Npc_Inspire_Cost_Not_Enough.getTips().replace(Wildcard.AttrType, attrType.getName()));
			}

//			if(role.get(attrType) < costValue){
//				return result.setInfo(Status.Npc_Inspire_Cost_Not_Enough.getTips().replace(Wildcard.AttrType, attrType.getName()));
//			}
			//扣除消耗
			if(attrType.isMoney()){
				GameContext.getUserAttributeApp().changeRoleMoney(role, attrType,
						OperatorType.Decrease, costValue, OutputConsumeType.npc_inspire_consume);
				role.getBehavior().notifyAttribute();
			}else{
				GameContext.getUserAttributeApp().changeAttribute(role, attrType, OperatorType.Decrease, 
						costValue, OutputConsumeType.npc_inspire_consume);
				role.getBehavior().notifyAttribute();
			}
			//失败提示信息
			String failInfo = inspireRatio.getFailInfo();
			//随机
			int randNum = RandomUtil.randomInt(1,(int)ParasConstant.PERCENT_BASE_VALUE);
			if(randNum > inspireRatio.getRatio()){
				return result.setInfo(failInfo);
			}
			//增加buff
			GameContext.getUserBuffApp().addBuffStat(role, role, buffId, buffLevel);
			//成功提示信息
			String successInfo = inspireRatio.getSuccessInfo();
			if(!Util.isEmpty(successInfo)){
				role.getBehavior().sendMessage(new C0003_TipNotifyMessage(successInfo));
			}
			return result.success();
		} catch (Exception e) {
			this.logger.error("NpcInspireApp.inspire error: ", e);
			return result.setInfo(Status.Sys_Error.getTips());
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
