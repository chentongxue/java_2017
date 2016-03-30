package com.game.draco.app.copy.team.vo;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.GameContext;
import com.game.draco.app.team.Team;

public class MatchResult {
	private final static Logger logger = LoggerFactory.getLogger(MatchResult.class);
    private static int MAX_SIZE = 3 ;
    private short copyId  ;
    private int index1 = 0 ;
    private int index2 = 0 ;
	private List<ApplyInfo> apply1 = new ArrayList<ApplyInfo>(); 
	private List<ApplyInfo> apply2 = new ArrayList<ApplyInfo>();
	private List<ApplyInfo> apply3 = new ArrayList<ApplyInfo>();
	
	public MatchResult(short copyId){
		this.copyId = copyId ;
	}
	
	/**
	 * 保证先清除进度,后移除报名信息
	 * @param master
	 * @param slaves
	 */
	private void memberChange(ApplyInfo master,ApplyInfo... slaves){
		Team masterTeam = master.getTeam();
		for(ApplyInfo slave : slaves){
			for (AbstractRole role : slave.getApplyRoles()) {
				masterTeam.memberJoin(role);
				//清除进度
				//TODO:2012.12.16
				//GameContext.getCopyLogicApp().clearCopyProg((RoleInstance)role, copyId);
			}
		}
		//清除master Team 进度
		//TODO:2012.12.16
		/*for(AbstractRole role : master.getApplyRoles()){
			//清除进度
			GameContext.getCopyLogicApp().clearCopyProg((RoleInstance)role, copyId);
		}*/
		//移除报名信息
		GameContext.getCopyTeamApp().removeApplyInfo(master.getTeamId());
		for(ApplyInfo slave : slaves){
			GameContext.getCopyTeamApp().removeApplyInfo(slave.getTeamId());
		}
	}
	
	
	/*private void memberChange(ApplyInfo master,ApplyInfo slave){
		Team masterTeam = master.getTeam();
		Team slaveTeam = slave.getTeam();
		for (AbstractRole role : slave.getApplyRoles()) {
			slaveTeam.memberLeave(role, LeaveTeam.system);
			masterTeam.memberJoin(role);
			//清除进度
			GameContext.getCopyLogicApp().clearCopyProg((RoleInstance)role, copyId);
		}
		GameContext.getCopyTeamApp().removeApplyInfo(master.getTeamId());
		GameContext.getCopyTeamApp().removeApplyInfo(slave.getTeamId());
	}*/
	
	public void match(){
		//3+1
		for(ApplyInfo masterInfo:apply3){
			try {
				if (this.index1 >= this.apply1.size()) {
					break;
				}
				ApplyInfo slaveInfo = this.apply1.get(this.index1);
				this.index1++ ;
				this.memberChange(masterInfo, slaveInfo);
				//发送匹配成功
				Team masterTeam = masterInfo.getTeam();
				GameContext.getCopyTeamApp().matchSuccess(masterTeam, copyId);
			}catch(Exception ex){
				logger.error("",ex);
			}
		}
		//2+2
		int total2 = this.apply2.size();
		for(;; /*int i=this.index2;i<total2;i=i+2*/){
			if(total2 -this.index2 < 2){
				break ;
			}
			ApplyInfo masterInfo = this.apply2.get(this.index2);
			ApplyInfo slaveInfo = this.apply2.get(this.index2+1);
			this.index2 += 2 ;
			this.memberChange(masterInfo, slaveInfo);
			//发送匹配成功
			Team masterTeam = masterInfo.getTeam();
			GameContext.getCopyTeamApp().matchSuccess(masterTeam, copyId);
		}
		int total1 = this.apply1.size();
		//2+1+1 ;
		for(int i=this.index2;i<this.apply2.size();i++){
			try {
				if(total1 -this.index1 < 2){
					break ;
				}
				ApplyInfo masterInfo = this.apply2.get(i);
				this.index2++;
				
				ApplyInfo slave1 = this.apply1.get(this.index1);
				ApplyInfo slave2 = this.apply1.get(this.index1 +1);
				this.index1 += 2 ;
				this.memberChange(masterInfo, slave1,slave2);
				//发送匹配成功
				Team masterTeam = masterInfo.getTeam();
				GameContext.getCopyTeamApp().matchSuccess(masterTeam, copyId);
			}catch(Exception ex){
				logger.error("",ex);
			}
		}
		//1+1+1+1
		for(;;/*int i=this.index1;i<total1;i=i+4*/){
			if(total1 -this.index1 < 4){
				break ;
			}
			ApplyInfo masterInfo = this.apply1.get(this.index1);
			ApplyInfo slave1 = this.apply1.get(this.index1+1);
			ApplyInfo slave2 = this.apply1.get(this.index1+2);
			ApplyInfo slave3 = this.apply1.get(this.index1+3);
			this.index1 += 4 ;
			this.memberChange(masterInfo, slave1,slave2,slave3);
			//发送匹配成功
			Team masterTeam = masterInfo.getTeam();
			GameContext.getCopyTeamApp().matchSuccess(masterTeam, copyId);
		}
	}
	
	
	public boolean addApplyInfo(ApplyInfo applyInfo){
		if(null == applyInfo){
			return false;
		}
		int size = applyInfo.getApplyRoles().size();
		if(size > MAX_SIZE){
			return false ;
		}
		if(1 == size){
			apply1.add(applyInfo);
			return true ;
		}
		if(2 == size){
			apply2.add(applyInfo);
			return true ;
		}
		if(3 == size){
			apply3.add(applyInfo);
			return true ;
		}
		return false ;
	}
	
}
