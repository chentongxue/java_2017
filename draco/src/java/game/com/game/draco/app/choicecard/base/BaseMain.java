package com.game.draco.app.choicecard.base;

import java.util.List;

import lombok.Data;

import org.python.google.common.collect.Lists;

public @Data class BaseMain{

	//免费抽 单次抽 十连抽
	private byte type;
	//cd时间
	private int cdTime;
	//节点1
	private int treeId1;
	//节点数量
	private int root1num;
	//幸运值类型1
	private byte luckType1;
	//节点2
	private int treeId2;
	//节点2数量
	private int root2num;
	//幸运值类型2
	private byte luckType2;
	//免费次数
	private int freeNum;
	//开始时间
	private String startTime;
	//结束时间
	private String endTime;
	
	private List<BaseTreeItem> list = Lists.newArrayList();

	public void addBaseMain(){
		BaseTreeItem tree = new BaseTreeItem();
		tree.setId(treeId1);
		tree.setNum(root1num);
		tree.setLuckType(luckType1);
		list.add(tree);
		
		BaseTreeItem tree2 = new BaseTreeItem();
		tree2.setId(treeId2);
		tree2.setNum(root2num);
		tree2.setLuckType(luckType2);
		list.add(tree2);
		
	}
	
}
