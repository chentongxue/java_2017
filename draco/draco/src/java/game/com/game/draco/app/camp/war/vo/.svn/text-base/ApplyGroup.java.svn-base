package com.game.draco.app.camp.war.vo;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

public class ApplyGroup {

	private LinkedList<ApplyInfo> applyList = Lists.newLinkedList();
	private Set<Integer> idSet = new HashSet<Integer>();
	
	
	public void addApplyInfo(ApplyInfo applyInfo){
		this.applyList.add(applyInfo);
		this.idSet.add(applyInfo.getId());
	}
	
	public void clear(){
		this.applyList.clear();
	}
	
	public ApplyInfo pop(){
		ApplyInfo info = this.applyList.pop();
		return info ;
	}
	
	public ApplyInfo peek(){
		return this.applyList.peek();
	}
	
	public int size(){
		return applyList.size();
	}
	
	public List<ApplyInfo> getApplyList(){
		return this.applyList ;
	}
}
