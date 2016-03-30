package com.game.draco.app.camp.war.vo;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import sacred.alliance.magic.util.Util;

import com.google.common.collect.Lists;

public class ApplyGroup {

	private LinkedList<ApplyInfo> applyList = Lists.newLinkedList();
	private AtomicInteger size = new AtomicInteger(0);
	
	public void addApplyInfo(ApplyInfo applyInfo){
		this.applyList.add(applyInfo);
		size.incrementAndGet();
	}
	
	public void decrementSize(){
		size.decrementAndGet() ;
	}
	
	public void clear(){
		this.applyList.clear();
		size.set(0);
	}
	
	public ApplyInfo popEffect(){
		while(true){
			ApplyInfo info = this.pop();
			if(null == info || !info.isCancel()){
				return info ;
			}
		}
	}
	
	public ApplyInfo pop(){
		if(Util.isEmpty(this.applyList)){
			return null ;
		}
		ApplyInfo info = this.applyList.pop();
		if(null != info && !info.isCancel()){
			//取消的报名，已经在取消操作的时候，调用decrementSize()
			this.decrementSize();
		}
		return info ;
	}
	
	
	public ApplyInfo peek(){
		return this.applyList.peek();
	}
	
	public int size(){
		return this.size.get();
	}
	
	public List<ApplyInfo> getApplyList(){
		return this.applyList ;
	}
}
