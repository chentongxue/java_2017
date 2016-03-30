package com.game.draco.app.choicecard;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.Setter;

import org.python.google.common.collect.Lists;
import org.python.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;

import com.game.draco.GameContext;
import com.game.draco.app.choicecard.activity.config.ActivityShow;
import com.game.draco.app.choicecard.base.BaseConsume;
import com.game.draco.app.choicecard.base.BaseInfo;
import com.game.draco.app.choicecard.base.BaseLeaf;
import com.game.draco.app.choicecard.base.BaseMain;
import com.game.draco.app.choicecard.base.BasePreview;
import com.game.draco.app.choicecard.base.BaseTree;

public class ChoiceCardAppImpl implements ChoiceCardApp {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Getter @Setter private Map<Byte,BaseMain> gameMoneyMainMap = null;
	
	@Getter @Setter private Map<String,BaseTree> gameMoneyTreeMap = null;
	
	@Getter @Setter private Map<Integer,List<BaseLeaf>> gameMoneyLeafMap = null;
	
	@Getter @Setter private Map<Byte,List<BaseConsume>> gameMoneyConsumeMap = null;
	
	@Getter @Setter private Map<Byte,BaseMain> gemMainMap = null;
	
	@Getter @Setter private Map<String,BaseTree> gemTreeMap = null;
	
	@Getter @Setter private Map<Integer,List<BaseLeaf>> gemLeafMap = null;
	
	@Getter @Setter private Map<Byte,Map<Integer,BaseConsume>> gemConsumeMap = null;
	
	@Getter @Setter private Map<Byte,List<BaseMain>> activityMainMap = null;
	
	@Getter @Setter private Map<String,BaseTree> activityTreeMap = null;
	
	@Getter @Setter private Map<Integer,List<BaseLeaf>> activityLeafMap = null;
	
	@Getter @Setter private Map<Byte,Map<Integer,BaseConsume>> activityConsumeMap = null;
	
	//金币物品权重
	@Getter @Setter private Map<Integer,Map<Integer, Integer>> gameMoneyLeafWeightMap = Maps.newHashMap();
	
	//钻石物品权重
	@Getter @Setter private Map<Integer,Map<Integer, Integer>> gemLeafWeightMap = Maps.newHashMap();
	
	//活动物品权重
	@Getter @Setter private Map<Integer,Map<Integer, Integer>> activityLeafWeightMap = Maps.newHashMap();
	
	//活动抽卡显示
	@Getter @Setter private List<ActivityShow> activityShowList = null;
	
	//金币抽卡描述
	@Getter @Setter private String gameMoneyInfo = null;
	
	//钻石抽卡描述
	@Getter @Setter private String gemInfo = null;
	
	//活动抽卡描述
	@Getter @Setter private String activityInfo = null;
	
	@Getter @Setter private String broadcastInfo = null;
	
	@Getter @Setter private List<BasePreview> gameMoneyPreviewList = null;
	
	@Getter @Setter private List<BasePreview> gemPreviewList = null;
	
	private boolean loadConfig = true;
	
	public void initGoldLeaf(int parentId,int indexId,int prob) {
		if(parentId <= 0 || indexId <= 0 || prob <= 0) {
			return ;
		}
		
		Map<Integer,Integer> map = null;
		if(gameMoneyLeafWeightMap.containsKey(parentId)){
			map = gameMoneyLeafWeightMap.get(parentId);
			map.put(indexId,prob);
		}else{
			map = Maps.newHashMap();
			map.put(indexId,prob);
			gameMoneyLeafWeightMap.put(parentId, map);
		}
	}
	
	public void initGemLeaf(int parentId,int indexId,int prob) {
		if(parentId <= 0 || indexId <= 0 || prob <= 0) {
			return ;
		}
		
		Map<Integer,Integer> map = null;
		if(gemLeafWeightMap.containsKey(parentId)){
			map = gemLeafWeightMap.get(parentId);
			map.put(indexId,prob);
		}else{
			map = Maps.newHashMap();
			map.put(indexId,prob);
			gemLeafWeightMap.put(parentId, map);
		}
	}
	
	public void initActivityLeaf(int parentId,int indexId,int prob) {
		if(parentId <= 0 || indexId <= 0 || prob <= 0) {
			return ;
		}
		
		Map<Integer,Integer> map = null;
		if(activityLeafWeightMap.containsKey(parentId)){
			map = activityLeafWeightMap.get(parentId);
		}else{
			map = Maps.newHashMap();
			activityLeafWeightMap.put(parentId, map);
		}
		map.put(indexId,prob);
	}
	
	/**
	 * 金币抽卡数据
	 */
	private Map<Byte,BaseMain> loadGameMoneyMainConfig(){
		Map<Byte,BaseMain> map = null;
		try{
			String fileName = XlsSheetNameType.choice_gamemoney_main.getXlsName();
			String sheetName = XlsSheetNameType.choice_gamemoney_main.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<BaseMain> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, BaseMain.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("not config the gameMoneyMainMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}else{
				map = Maps.newHashMap();
				for(BaseMain main : list){
					main.addBaseMain();
					map.put(main.getType(),main);
				}
			}
		}catch(Exception e){
			logger.error("loadGameMoneyMainConfig is error",e);
		}
		return map;
	}
	
	/**
	 * 金币抽卡数据
	 */
	private Map<String,BaseTree> loadGameMoneyTreeConfig(){
		Map<String,BaseTree> map = null;
		try{
			String fileName = XlsSheetNameType.choice_gamemoney_tree.getXlsName();
			String sheetName = XlsSheetNameType.choice_gamemoney_tree.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, BaseTree.class);
			if(Util.isEmpty(map)){
				Log4jManager.CHECK.error("not config the gameMoneyTreeMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}else{
				for(Entry<String,BaseTree> tree : map.entrySet()){
					tree.getValue().init();
				}
			}
			
		}catch(Exception e){
			logger.error("loadGameMoneyTreeConfig is error",e);
		}
		return map;
	}
	
	/**
	 * 金币抽卡数据
	 */
	private List<BaseLeaf> loadGameMoneyLeafConfig(){
		List<BaseLeaf> list = null;
		try{
			String fileName = XlsSheetNameType.choice_gamemoney_leaf.getXlsName();
			String sheetName = XlsSheetNameType.choice_gamemoney_leaf.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			list = XlsPojoUtil.sheetToList(sourceFile, sheetName, BaseLeaf.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("not config the gameMoneyLeafList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}
		}catch(Exception e){
			logger.error("loadGameMoneyLeafConfig is error",e);
		}
		return list;
	}
	
	private Map<Integer,List<BaseLeaf>> loadGameMoneyLeafMap(List<BaseLeaf> gameMoneyLeafList){
		Map<Integer,List<BaseLeaf>> gameMoneyLeafMap = Maps.newHashMap();
		for(BaseLeaf leaf : gameMoneyLeafList){
			if(gameMoneyLeafMap.containsKey(leaf.getParentId())){
				gameMoneyLeafMap.get(leaf.getParentId()).add(leaf);
			}else{
				List<BaseLeaf> list = Lists.newArrayList();
				list.add(leaf);
				gameMoneyLeafMap.put(leaf.getParentId(), list);
			}
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(leaf.getGoodsId());
			if(goodsBase == null){
				String fileName = XlsSheetNameType.choice_gamemoney_leaf.getXlsName();
				String sheetName = XlsSheetNameType.choice_gamemoney_leaf.getSheetName();
				String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
				Log4jManager.CHECK.error("not config the gameMoneyLeafList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}
			initGoldLeaf(leaf.getParentId(), leaf.getIndexId(),leaf.getProb());
		}
		return gameMoneyLeafMap;
	}
	
	/**
	 * 金币抽卡数据
	 */
	private List<BaseConsume> loadGameMoneyConsumeConfig(){
		List<BaseConsume> list = null;
		try{
			String fileName = XlsSheetNameType.choice_gamemoney_consume.getXlsName();
			String sheetName = XlsSheetNameType.choice_gamemoney_consume.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			list = XlsPojoUtil.sheetToList(sourceFile, sheetName, BaseConsume.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("not config the gameMoneyConsumeList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}
		}catch(Exception e){
			logger.error("loadGameMoneyConsumeConfig is error",e);
		}
		return list;
	}
	
	private Map<Byte,List<BaseConsume>> loadGameMoneyConsumeMap(List<BaseConsume> list){
		Map<Byte,List<BaseConsume>> gameMoneyConsMap = Maps.newHashMap();
		List<BaseConsume> consumeList = null;
		for(BaseConsume consume : list){
			if(gameMoneyConsMap.containsKey(consume.getType())){
				consumeList = gameMoneyConsMap.get(consume.getType());
			}else{
				consumeList = Lists.newArrayList();
				gameMoneyConsMap.put(consume.getType(), consumeList);
			}
			consumeList.add(consume);
		}
		return gameMoneyConsMap;
	}
	
	/**
	 * 钻石抽卡数据
	 */
	private Map<Byte,BaseMain> loadGemMainConfig(){
		Map<Byte,BaseMain> map = null;
		try{
			String fileName = XlsSheetNameType.choice_gem_main.getXlsName();
			String sheetName = XlsSheetNameType.choice_gem_main.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<BaseMain> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, BaseMain.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("not config the gemMainMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}else{
				map = Maps.newHashMap();
				for(BaseMain main : list){
					main.addBaseMain();
					map.put(main.getType(),main);
				}
			}
		}catch(Exception e){
			logger.error("loadGemMainConfig is error",e);
		}
		return map;
	}
	
	/**
	 * 钻石抽卡数据
	 */
	private Map<String,BaseTree> loadGemTreeConfig(){
		Map<String,BaseTree> map = null;
		try{
			String fileName = XlsSheetNameType.choice_gem_tree.getXlsName();
			String sheetName = XlsSheetNameType.choice_gem_tree.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, BaseTree.class);
			if(Util.isEmpty(map)){
				Log4jManager.CHECK.error("not config the gemTreeMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}else{
				for(Entry<String,BaseTree> tree : map.entrySet()){
					tree.getValue().init();
				}
			}
		}catch(Exception e){
			logger.error("loadGemTreeConfig is error",e);
		}
		return map;
	}
	
	/**
	 * 钻石抽卡数据
	 */
	private List<BaseLeaf> loadGemLeafConfig(){
		List<BaseLeaf> gemLeafList = null;
		try{
			String fileName = XlsSheetNameType.choice_gem_leaf.getXlsName();
			String sheetName = XlsSheetNameType.choice_gem_leaf.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			gemLeafList = XlsPojoUtil.sheetToList(sourceFile, sheetName, BaseLeaf.class);
			if(Util.isEmpty(gemLeafList)){
				Log4jManager.CHECK.error("not config the gemLeafList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}
		}catch(Exception e){
			logger.error("loadGemLeafConfig is error",e);
		}
		return gemLeafList;
	}
	
	private Map<Integer,List<BaseLeaf>> loadGemLeafMap(List<BaseLeaf> gemLeafList){
		Map<Integer,List<BaseLeaf>> map = Maps.newHashMap();
		for(BaseLeaf leaf : gemLeafList){
			if(map.containsKey(leaf.getParentId())){
				map.get(leaf.getParentId()).add(leaf);
			}else{
				List<BaseLeaf> list = Lists.newArrayList();
				list.add(leaf);
				map.put(leaf.getParentId(), list);
			}
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(leaf.getGoodsId());
			if(goodsBase == null){
				String fileName = XlsSheetNameType.choice_gem_leaf.getXlsName();
				String sheetName = XlsSheetNameType.choice_gem_leaf.getSheetName();
				String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
				Log4jManager.CHECK.error("not config the gemLeafList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}
			initGemLeaf(leaf.getParentId(), leaf.getIndexId(),leaf.getProb());
		}
		return map;
	}
	
	/**
	 * 钻石抽卡数据
	 */
	private List<BaseConsume> loadGemConsumeConfig(){
		List<BaseConsume> gemConsumeList = null;
		try{
			String fileName = XlsSheetNameType.choice_gem_consume.getXlsName();
			String sheetName = XlsSheetNameType.choice_gem_consume.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			gemConsumeList = XlsPojoUtil.sheetToList(sourceFile, sheetName, BaseConsume.class);
			if(Util.isEmpty(gemConsumeList)){
				Log4jManager.CHECK.error("not config the gemConsumeList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}
		}catch(Exception e){
			logger.error("loadGemConsumeConfig is error",e);
		}
		return gemConsumeList;
	}
	
	private Map<Byte,Map<Integer,BaseConsume>> loadGemConsumeMap(List<BaseConsume> gemConsumeList){
		
		Map<Byte,Map<Integer,BaseConsume>> gcMap = Maps.newHashMap();
		Map<Integer,BaseConsume> map = null;
		for(BaseConsume consume : gemConsumeList){
			if(gcMap.containsKey(consume.getType())){
				if(!gcMap.get(consume.getType()).containsKey(consume.getNum())){
					map  = gcMap.get(consume.getType());
					map.put(consume.getNum(), consume);
				}
			}else{
				map = Maps.newHashMap();
				map.put(consume.getNum(), consume);
				gcMap.put(consume.getType(), map);
			}
		}
		return gcMap;
	}
	
	/**
	 * 活动抽卡数据
	 */
	private List<BaseMain> loadActivityMainConfig(){
		List<BaseMain> list = null;
		try{
			String fileName = XlsSheetNameType.choice_activity_main.getXlsName();
			String sheetName = XlsSheetNameType.choice_activity_main.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			list = XlsPojoUtil.sheetToList(sourceFile, sheetName, BaseMain.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("not config the loadActivityMainConfig list,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}
		}catch(Exception e){
			logger.error("loadActivityMainConfig is error",e);
		}
		return list;
	}
	
	private Map<Byte,List<BaseMain>> loadActivityMainMap(List<BaseMain> list){
		Map<Byte,List<BaseMain>> map = Maps.newHashMap();
		List<BaseMain> activityMainList = null;
		for(BaseMain activity : list){
			activity.addBaseMain();
			if(map.containsKey(activity.getType())){
				map.get(activity.getType()).add(activity);
			}else{
				activityMainList = Lists.newArrayList();
				activityMainList.add(activity);
				map.put(activity.getType(), activityMainList);
			}
		}
		return map;
	}
	
	/**
	 * 活动抽卡数据
	 */
	private Map<String,BaseTree> loadActivityTreeConfig(){
		Map<String,BaseTree> map = null;
		try{
			String fileName = XlsSheetNameType.choice_activity_tree.getXlsName();
			String sheetName = XlsSheetNameType.choice_activity_tree.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, BaseTree.class);
			if(Util.isEmpty(map)){
				Log4jManager.CHECK.error("not config the activityTreeMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}else{
				for(Entry<String,BaseTree> tree : map.entrySet()){
					tree.getValue().init();
				}
			}
		}catch(Exception e){
			logger.error("loadGemTreeConfig is error",e);
		}
		return map;
	}
	
	/**
	 * 活动抽卡数据
	 */
	private List<BaseLeaf> loadActivityLeafConfig(){
		List<BaseLeaf> activityLeafList = null;
		try{
			String fileName = XlsSheetNameType.choice_activity_leaf.getXlsName();
			String sheetName = XlsSheetNameType.choice_activity_leaf.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			activityLeafList = XlsPojoUtil.sheetToList(sourceFile, sheetName, BaseLeaf.class);
			if(Util.isEmpty(activityLeafList)){
				Log4jManager.CHECK.error("not config the activityLeafList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}
		}catch(Exception e){
			logger.error("loadActivityLeafConfig is error",e);
		}
		return activityLeafList;
	}
	
	private Map<Integer,List<BaseLeaf>> loadActivityLeafMap(List<BaseLeaf> activityLeafList){
		Map<Integer,List<BaseLeaf>> map = Maps.newHashMap();
		for(BaseLeaf leaf : activityLeafList){
			if(map.containsKey(leaf.getParentId())){
				map.get(leaf.getParentId()).add(leaf);
			}else{
				List<BaseLeaf> list = Lists.newArrayList();
				list.add(leaf);
				map.put(leaf.getParentId(), list);
			}
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(leaf.getGoodsId());
			if(goodsBase == null){
				String fileName = XlsSheetNameType.choice_activity_leaf.getXlsName();
				String sheetName = XlsSheetNameType.choice_activity_leaf.getSheetName();
				String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
				Log4jManager.CHECK.error("not config the activityLeafList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}
			initActivityLeaf(leaf.getParentId(), leaf.getIndexId(),leaf.getProb());
		}
		return map;
	}
	
	/**
	 * 活动抽卡显示数据
	 */
	private List<ActivityShow> loadActivityShowConfig(){
		List<ActivityShow> list = null;
		try{
			String fileName = XlsSheetNameType.choice_activity_show.getXlsName();
			String sheetName = XlsSheetNameType.choice_activity_show.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			list = XlsPojoUtil.sheetToList(sourceFile, sheetName, ActivityShow.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("not config the loadActivityShowConfig,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}
		}catch(Exception e){
			logger.error("loadActivityLeafConfig is error",e);
		}
		return list;
	}
	
	/**
	 * 活动抽卡数据
	 */
	private List<BaseConsume> loadActivityConsumeConfig(){
		List<BaseConsume> activityConsumeList = null;
		try{
			String fileName = XlsSheetNameType.choice_activity_consume.getXlsName();
			String sheetName = XlsSheetNameType.choice_activity_consume.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			activityConsumeList = XlsPojoUtil.sheetToList(sourceFile, sheetName, BaseConsume.class);
			if(Util.isEmpty(activityConsumeList)){
				Log4jManager.CHECK.error("not config the activityConsumeList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}
		}catch(Exception e){
			logger.error("loadActivityConsumeConfig is error",e);
		}
		return activityConsumeList;
	}
	
	private Map<Byte,Map<Integer,BaseConsume>> loadActivityConsumeMap(List<BaseConsume> activityConsumeList){
		Map<Byte,Map<Integer,BaseConsume>> acMap = Maps.newHashMap();
		Map<Integer,BaseConsume> map = null;
		for(BaseConsume consume : activityConsumeList){
			if(acMap.containsKey(consume.getType())){
				if(!acMap.get(consume.getType()).containsKey(consume.getNum())){
					map  = acMap.get(consume.getType());
					map.put(consume.getNum(), consume);
				}
			}else{
				map = Maps.newHashMap();
				map.put(consume.getNum(), consume);
				acMap.put(consume.getType(), map);
			}
		}
		return acMap;
	}
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		try{
			loadConfig();
		}catch(Exception e){
			logger.error("start is error",e);
		}
		
	}

	@Override
	public void stop() {
		
	}

	@Override
	public BaseMain getGameMoneyMain(byte type) {
		if(gameMoneyMainMap.containsKey(type)){
			return gameMoneyMainMap.get(type);
		}
		return null;
	}

	@Override
	public BaseTree getGameMoneyTree(String treeId) {
		if(gameMoneyTreeMap.containsKey(treeId)){
			return gameMoneyTreeMap.get(treeId);
		}
		return null;
	}

	@Override
	public List<BaseLeaf> getGameMoneyLeafList(int parentId) {
		if(gameMoneyLeafMap.containsKey(parentId)){
			return gameMoneyLeafMap.get(parentId);
		}
		return null;
	}

	@Override
	public BaseConsume getGameMoneyConsume(byte type, int level) {
		if(gameMoneyConsumeMap.containsKey(type)){
			List<BaseConsume> list = gameMoneyConsumeMap.get(type);
			if(Util.isEmpty(list)){
				return null;
			}
			for(BaseConsume consume : list){
				if(level >= consume.getMinLevel() && level <= consume.getMaxLevel()){
					return consume;
				}
			}
		}
		return null;
	}
	
	@Override
	public BaseConsume getMaxGameMoneyConsume(byte type) {
		if(gameMoneyConsumeMap.containsKey(type)){
			List<BaseConsume> list = gameMoneyConsumeMap.get(type);
			if(Util.isEmpty(list)){
				return null;
			}
			
			return list.get(list.size() -1);
		}
		return null;
	}

	@Override
	public BaseMain getGemMain(byte type) {
		if(gemMainMap.containsKey(type)){
			return gemMainMap.get(type);
		}
		return null;
	}

	@Override
	public BaseTree getGemTree(String treeKey) {
		if(gemTreeMap.containsKey(treeKey)){
			return gemTreeMap.get(treeKey);
		}
		return null;
	}

	@Override
	public List<BaseLeaf> getGemLeafList(int parentId) {
		if(gemLeafMap.containsKey(parentId)){
			return gemLeafMap.get(parentId);
		}
		return null;
	}

	@Override
	public BaseConsume getGemConsume(byte type, int num) {
		if(gemConsumeMap.containsKey(type)){
			Map<Integer,BaseConsume> map = gemConsumeMap.get(type);
			if(map.containsKey(num)){
				return map.get(num);
			}
		}
		return null;
	}
	
	@Override
	public BaseConsume getMaxGemConsume(byte type) {
		if(gemConsumeMap.containsKey(type)){
			Map<Integer,BaseConsume> map = gemConsumeMap.get(type);
			int maxNum = -1;
			for(Entry<Integer,BaseConsume> max : map.entrySet()){
				if(max.getKey() > maxNum){
					maxNum = max.getKey();
				}
			}
			if(map.containsKey(maxNum)){
				return map.get(maxNum);
			}
		}
		return null;
	}

	@Override
	public BaseMain getActivityMain(byte type) {
		try{
			if(activityMainMap.containsKey(type)){
				List<BaseMain> list = activityMainMap.get(type);
				
				if(Util.isEmpty(list)){
					return null;
				}
				
				long nowTime = DateUtil.getNowTime();
				Date nowDate = new Date(nowTime);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				
				for(BaseMain activity : list){
					Date date = sdf.parse(activity.getStartTime());
					Date dateZero = DateUtil.getDateZero(date);
					Date dateEnd = DateUtil.getDateEndTime(date);
					if(nowDate.getTime() >= dateZero.getTime() && nowDate.getTime() <= dateEnd.getTime()){
						return activity;
					}
				}
				return list.get(0);
			}
		}catch(Exception e){
			logger.error("getActivityMain",e);
		}
		return null;
	}

	@Override
	public BaseTree getActivityTree(String key) {
		if(activityTreeMap.containsKey(key)){
			return activityTreeMap.get(key);
		}
		return null;
	}

	@Override
	public List<BaseLeaf> getActivityLeafList(int parentId) {
		if(activityLeafMap.containsKey(parentId)){
			return activityLeafMap.get(parentId);
		}
		return null;
	}

	@Override
	public BaseConsume getActivityConsume(byte type, int num) {
		if(activityConsumeMap.containsKey(type)){
			Map<Integer,BaseConsume> map = activityConsumeMap.get(type);
			if(map.containsKey(num)){
				return map.get(num);
			}
		}
		return null;
	}
	
	@Override
	public BaseConsume getMaxActivityConsume(byte type) {
		if(activityConsumeMap.containsKey(type)){
			Map<Integer,BaseConsume> map = activityConsumeMap.get(type);
			int maxNum = -1;
			for(Entry<Integer,BaseConsume> max : map.entrySet()){
				if(max.getKey() > maxNum){
					maxNum = max.getKey();
				}
			}
			if(map.containsKey(maxNum)){
				return map.get(maxNum);
			}
		}
		return null;
	}

	@Override
	public Map<Integer, Integer> getGameMoneyLeafWeight(int parentId) {
		try{
			return gameMoneyLeafWeightMap.get(parentId);
		}catch(Exception e){
			logger.error("getGoldLeafWeight",e);
		}
		return null;
	}

	@Override
	public BaseLeaf getGameMoneyLeaf(int parentId,int indexId) {
		List<BaseLeaf> list = getGameMoneyLeafList(parentId);
		if(list != null){
			for(BaseLeaf leaf : list){
				if(leaf.getIndexId() == indexId){
					return leaf;
				}
			}
		}
		return null;
	}

	@Override
	public Map<Integer, Integer> getGemLeafWeight(int parentId) {
		try{
			return gemLeafWeightMap.get(parentId);
		}catch(Exception e){
			logger.error("getGemLeafWeight",e);
		}
		return null;
	}

	@Override
	public BaseLeaf getGemLeaf(int parentId, int indexId) {
		List<BaseLeaf> list = getGemLeafList(parentId);
		if(list != null){
			for(BaseLeaf leaf : list){
				if(leaf.getIndexId() == indexId){
					return leaf;
				}
			}
		}
		return null;
	}

	@Override
	public Map<Integer, Integer> getActivityLeafWeight(int parentId) {
		try{
			return activityLeafWeightMap.get(parentId);
		}catch(Exception e){
			logger.error("getActivityLeafWeight",e);
		}
		return null;
	}

	@Override
	public BaseLeaf getActivityLeaf(int parentId, int indexId) {
		List<BaseLeaf> list = getActivityLeafList(parentId);
		if(list != null){
			for(BaseLeaf leaf : list){
				if(leaf.getIndexId() == indexId){
					return leaf;
				}
			}
		}
		return null;
	}

	@Override
	public ActivityShow getActivityShow() {
		if(Util.isEmpty(activityShowList)){
			return null;
		}
		
		long nowTime = System.currentTimeMillis();
		for(ActivityShow show : activityShowList){
			if(DateUtil.dateInRegion(new Date(nowTime),show.getStartTime(),show.getEndTime())){
				return show;
			}
		}
		return activityShowList.get(0);
	}

	@Override
	public Result reLoad() {
		Result result = new Result();
		try {
			logger.info("reload ChoiceCard start");
			result = loadConfig();
			logger.info("reload ChoiceCard end");
			
		}catch(Exception ex){
			logger.error("reload buff error",ex);
		}
		return result;
	}
	
	private Result loadConfig(){
		Result result = new Result();
		try{
			//金币
			Map<Byte,BaseMain> loadGameMoneyMainMap = loadGameMoneyMainConfig();
			Map<String,BaseTree> loadGameMoneyTreeMap = loadGameMoneyTreeConfig();
			List<BaseLeaf> loadGameMoneyLeafList = loadGameMoneyLeafConfig();
			List<BaseConsume> loadGameMoneyConsumeList = loadGameMoneyConsumeConfig();
			String gmInfo = loadBaseGameMoneyInfo();
			List<BasePreview> loadGameMoneyPreviewList = loadBaseGameMoneyPreview();
			
			//钻石
			Map<Byte,BaseMain> loadGemMainMap = loadGemMainConfig();
			Map<String,BaseTree> loadGemTreeMap = loadGemTreeConfig();
			List<BaseLeaf> loadGemLeafList = loadGemLeafConfig();
			List<BaseConsume> loadGemConsumeList = loadGemConsumeConfig();	
			String geInfo = loadBaseGemInfo();
			List<BasePreview> loadGemPreviewList = loadBaseGemPreview();
			
			//活动
			List<BaseMain> loadActivityMainList = loadActivityMainConfig();
			Map<String,BaseTree> loadActivityTreeMap = loadActivityTreeConfig();
			List<BaseLeaf> lodActivityLeafList = loadActivityLeafConfig();
			List<BaseConsume> loadActivityConsumeList = loadActivityConsumeConfig();	
			List<ActivityShow> loadActivityShowList = loadActivityShowConfig();
			String acInfo = loadBaseActivityInfo();
			
			if(!loadConfig){
				result.setInfo("加载英雄抽卡数据失败");
				return result;
			}
			
			gameMoneyMainMap = loadGameMoneyMainMap;
			gameMoneyTreeMap = loadGameMoneyTreeMap;
			gameMoneyInfo = gmInfo;
			gameMoneyPreviewList = loadGameMoneyPreviewList;
			
			gemMainMap = loadGemMainMap;
			gemTreeMap = loadGemTreeMap;
			gemInfo = geInfo;
			gemPreviewList = loadGemPreviewList;
			
			activityTreeMap = loadActivityTreeMap;
			activityInfo = acInfo;
			
			gameMoneyLeafMap = loadGameMoneyLeafMap(loadGameMoneyLeafList);
			gameMoneyConsumeMap = loadGameMoneyConsumeMap(loadGameMoneyConsumeList);
			gemLeafMap = loadGemLeafMap(loadGemLeafList);
			gemConsumeMap = loadGemConsumeMap(loadGemConsumeList);
			activityMainMap = loadActivityMainMap(loadActivityMainList);
			activityLeafMap = loadActivityLeafMap(lodActivityLeafList);
			activityConsumeMap = loadActivityConsumeMap(loadActivityConsumeList);
			activityShowList = loadActivityShowList;
			
			result.success();
			
		}catch(Exception e){
			logger.error("loadConfig ERR",e);
		}
		return result;
	}

	private String loadBaseGameMoneyInfo() {
		try{
			String fileName = XlsSheetNameType.choice_gamemoney_info.getXlsName();
			String sheetName = XlsSheetNameType.choice_gamemoney_info.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<BaseInfo> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, BaseInfo.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("not config the loadBaseGameMoneyInfo,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}else{
				return list.get(0).getInfo();
			}
		}catch(Exception e){
			logger.error("loadBaseGameMoneyInfo is error",e);
		}
		return null;
	}
	
	private List<BasePreview> loadBaseGameMoneyPreview() {
		try{
			String fileName = XlsSheetNameType.choice_gamemoney_preview.getXlsName();
			String sheetName = XlsSheetNameType.choice_gamemoney_preview.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<BasePreview> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, BasePreview.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("not config the loadBaseGameMoneyPreview,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}
			return list;
		}catch(Exception e){
			logger.error("loadBaseGameMoneyPreview is error",e);
		}
		return null;
	}
	
	private String loadBaseGemInfo() {
		try{
			String fileName = XlsSheetNameType.choice_gem_info.getXlsName();
			String sheetName = XlsSheetNameType.choice_gem_info.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<BaseInfo> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, BaseInfo.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("not config the loadBaseGemInfo,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}else{
				return list.get(0).getInfo();
			}
		}catch(Exception e){
			logger.error("loadBaseGemInfo is error",e);
		}
		return null;
	}
	
	private List<BasePreview> loadBaseGemPreview() {
		try{
			String fileName = XlsSheetNameType.choice_gem_preview.getXlsName();
			String sheetName = XlsSheetNameType.choice_gem_preview.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<BasePreview> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, BasePreview.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("not config the loadBaseGemPreview,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}
			return list;
		}catch(Exception e){
			logger.error("loadBaseGemPreview is error",e);
		}
		return null;
	}
	
	private String loadBaseActivityInfo() {
		try{
			String fileName = XlsSheetNameType.choice_activity_info.getXlsName();
			String sheetName = XlsSheetNameType.choice_activity_info.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<BaseInfo> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, BaseInfo.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("not config the loadBaseActivityInfo,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				loadConfig = false;
			}else{
				return list.get(0).getInfo();
			}
		}catch(Exception e){
			logger.error("loadBaseActivityInfo is error",e);
		}
		return null;
	}

	@Override
	public List<BasePreview> getGameMoneyPreview() {
		return gameMoneyPreviewList;
	}

	@Override
	public List<BasePreview> getGemPreview() {
		return gemPreviewList;
	}
	
}
