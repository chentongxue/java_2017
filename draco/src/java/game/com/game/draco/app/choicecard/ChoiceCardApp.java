package com.game.draco.app.choicecard;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;

import com.game.draco.app.choicecard.activity.config.ActivityShow;
import com.game.draco.app.choicecard.base.BaseConsume;
import com.game.draco.app.choicecard.base.BaseLeaf;
import com.game.draco.app.choicecard.base.BaseMain;
import com.game.draco.app.choicecard.base.BasePreview;
import com.game.draco.app.choicecard.base.BaseTree;

public interface ChoiceCardApp extends Service{
	
	BaseMain getGameMoneyMain(byte type);
	
	BaseTree getGameMoneyTree(String treeId);
	
	List<BaseLeaf> getGameMoneyLeafList(int parentId);
	
	BaseConsume getGameMoneyConsume(byte type,int num);
	
	BaseConsume getMaxGameMoneyConsume(byte type);
	
	BaseMain getGemMain(byte type);
	
	BaseTree getGemTree(String treeKey);
	
	List<BaseLeaf> getGemLeafList(int parentId);
	
	BaseConsume getGemConsume(byte type,int num);
	
	BaseConsume getMaxGemConsume(byte type);
	
	BaseMain getActivityMain(byte type);
	
	BaseTree getActivityTree(String key);
	
	List<BaseLeaf> getActivityLeafList(int parentId);
	
	BaseConsume getActivityConsume(byte type,int num);

	BaseConsume getMaxActivityConsume(byte type);
	
	Map<Integer,Integer> getGameMoneyLeafWeight(int parentId);
	
	BaseLeaf getGameMoneyLeaf(int parentId,int indexId);
	
	Map<Integer,Integer> getGemLeafWeight(int parentId);
	
	BaseLeaf getGemLeaf(int parentId,int indexId);
	
	Map<Integer,Integer> getActivityLeafWeight(int parentId);
	
	BaseLeaf getActivityLeaf(int parentId,int indexId);
	
	ActivityShow getActivityShow();
	
	Result reLoad();
	
	String getGameMoneyInfo();
	
	String getGemInfo();
	
	String getActivityInfo();
	
	String getBroadcastInfo();
	
	List<BasePreview> getGameMoneyPreview();
	
	List<BasePreview> getGemPreview();
	
}
