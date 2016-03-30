package com.game.draco.app.team.vo;

import java.util.List;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.team.Team;
import com.game.draco.message.response.C1314_TeamPanelMatchingCancelResqMessage;
import com.google.common.collect.Lists;

public @Data class MatchResult {
	private final static Logger logger = LoggerFactory.getLogger(MatchResult.class);
	private static int MAX_SIZE = 4;
	
	private String target;
	private int index41 = 0;
	private int index42 = 0;
	private int index21 = 0;
	private int index31 = 0;
	private List<ApplyInfo> apply21 = Lists.newArrayList();
	private List<ApplyInfo> apply31 = Lists.newArrayList();
	private List<ApplyInfo> apply32 = Lists.newArrayList();
	private List<ApplyInfo> apply41 = Lists.newArrayList();
	private List<ApplyInfo> apply42 = Lists.newArrayList();
	private List<ApplyInfo> apply43 = Lists.newArrayList();

	public MatchResult(String target) {
		this.target = target;
	}

	/**
	 * 保证先清除进度,后移除报名信息
	 * @param master
	 * @param slaves
	 */
	private void memberChange(ApplyInfo master, ApplyInfo... slaves) {
		Team masterTeam = master.getTeam();
		for (ApplyInfo slave : slaves) {
			for (AbstractRole role : slave.getApplyRoles()) {
				if (masterTeam.isFull()) {
					this.teamFull(master, masterTeam);
					return ;
				}
				masterTeam.memberJoin(role);
				master.getApplyRoles().add(role);
			}
			GameContext.getTeamApp().removeApplyInfo(slave.getTeamId());
		}
		// 队满处理
		if (masterTeam.isFull()) {
			this.teamFull(master, masterTeam);
		}
	}
	
	/**
	 * 队满处理
	 * @param master
	 * @param masterTeam
	 */
	private void teamFull(ApplyInfo master, Team masterTeam) {
		// 移除报名信息
		GameContext.getTeamApp().removeApplyInfo(master.getTeamId());
		// 通知队长离开队列
		C1314_TeamPanelMatchingCancelResqMessage message = new C1314_TeamPanelMatchingCancelResqMessage();
		message.setStatus((byte) 1);
		RoleInstance role = (RoleInstance) masterTeam.getLeader();
		role.getBehavior().sendMessage(message);
	}

	public void match() {
		// 1+1
		int total21 = this.apply21.size();
		for (;;) {
			if (total21 - this.index21 < 2) {
				break ;
			}
			ApplyInfo masterInfo = this.apply21.get(this.index21);
			ApplyInfo slaveInfo = this.apply21.get(this.index21 + 1);
			this.index21 += 2;
			this.memberChange(masterInfo, slaveInfo);
		}
		
		// 2+1
		for (ApplyInfo masterInfo : apply32) {
			try {
				if (this.index31 >= this.apply31.size()) {
					break ;
				}
				ApplyInfo slaveInfo = this.apply31.get(this.index31);
				this.index31 ++;
				this.memberChange(masterInfo, slaveInfo);
			} catch (Exception ex) {
				logger.error("", ex);
			}
		}
		
		// 1+1+1
		int total31 = this.apply31.size();
		for (;;) {
			if (total31 - this.index31 < 3) {
				if (this.apply31.size() <= this.index31) {
					break;
				}
				ApplyInfo masterInfo = this.apply31.get(this.index31);
				this.index31 ++;
				for (int n = this.index31; n < this.apply31.size(); n ++) {
					this.memberChange(masterInfo, this.apply31.get(n));
					this.index31 ++;
				}
				break ;
			}
			ApplyInfo masterInfo = this.apply31.get(this.index31);
			ApplyInfo slave1 = this.apply31.get(this.index31 + 1);
			ApplyInfo slave2 = this.apply31.get(this.index31 + 2);
			this.index31 += 3;
			this.memberChange(masterInfo, slave1, slave2);
		}
		
		// 3+1
		for (ApplyInfo masterInfo : apply43) {
			try {
				if (this.index41 >= this.apply41.size()) {
					break;
				}
				ApplyInfo slaveInfo = this.apply41.get(this.index41);
				this.index41++;
				this.memberChange(masterInfo, slaveInfo);
			} catch (Exception ex) {
				logger.error("", ex);
			}
		}

		// 2+2
		int total42 = this.apply42.size();
		for (;;) {
			if (total42 - this.index42 < 2) {
				break;
			}
			ApplyInfo masterInfo = this.apply42.get(this.index42);
			ApplyInfo slaveInfo = this.apply42.get(this.index42 + 1);
			this.index42 += 2;
			this.memberChange(masterInfo, slaveInfo);
		}

		// 2+1+1 ;
		int total41 = this.apply41.size();
		for (int i = this.index42; i < this.apply42.size(); i++) {
			try {
				if (total41 - this.index41 < 2) {
					if (this.apply41.size() <= this.index41) {
						break;
					}
					ApplyInfo masterInfo = this.apply42.get(i);
					for (int n = this.index41; n < this.apply41.size(); n ++) {
						this.memberChange(masterInfo, this.apply41.get(n));
						this.index41 ++;
					}
					break;
				}
				ApplyInfo masterInfo = this.apply42.get(i);
				this.index42++;

				ApplyInfo slave1 = this.apply41.get(this.index41);
				ApplyInfo slave2 = this.apply41.get(this.index41 + 1);
				this.index41 += 2;
				this.memberChange(masterInfo, slave1, slave2);
			} catch (Exception ex) {
				logger.error("", ex);
			}
		}

		// 1+1+1+1
		for (;;) {
			if (total41 - this.index41 < 4) {
				if (this.apply41.size() <= this.index41) {
					break;
				}
				ApplyInfo masterInfo = this.apply41.get(this.index41);
				this.index41 ++;
				for (int n = this.index41; n < this.apply41.size(); n ++) {
					this.memberChange(masterInfo, this.apply41.get(n));
					this.index41 ++;
				}
				break;
			}
			ApplyInfo masterInfo = this.apply41.get(this.index41);
			ApplyInfo slave1 = this.apply41.get(this.index41 + 1);
			ApplyInfo slave2 = this.apply41.get(this.index41 + 2);
			ApplyInfo slave3 = this.apply41.get(this.index41 + 3);
			this.index41 += 4;
			this.memberChange(masterInfo, slave1, slave2, slave3);
		}
		
	}

	public boolean addApplyInfo(ApplyInfo applyInfo) {
		if (null == applyInfo) {
			return false;
		}
		int size = applyInfo.getApplyRoles().size();
		int maxSize = applyInfo.getNumber();// 最大人数
		if (maxSize > MAX_SIZE || size >= maxSize) {
			return false;
		}
		if (1 == size) {
			if (2 == maxSize) {
				this.apply21.add(applyInfo);
				return true;
			}
			if (3 == maxSize) {
				this.apply31.add(applyInfo);
				return true;
			}
			if (4 == maxSize) {
				this.apply41.add(applyInfo);
				return true;
			}
		}
		if (2 == size) {
			if (3 == maxSize) {
				this.apply32.add(applyInfo);
				return true;
			}
			if (4 == maxSize) {
				this.apply42.add(applyInfo);
				return true;
			}
		}
		if (3 == size) {
			apply43.add(applyInfo);
			return true;
		}
		return false;
	}

}
