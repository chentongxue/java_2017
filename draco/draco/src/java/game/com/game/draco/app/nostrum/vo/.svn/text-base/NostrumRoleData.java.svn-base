package com.game.draco.app.nostrum.vo;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

import com.game.draco.GameContext;
import com.game.draco.app.nostrum.domain.RoleNostrum;

@Data
public class NostrumRoleData {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String roleId;//角色ID
	private Map<Integer,RoleNostrum> nostrumMap = new HashMap<Integer,RoleNostrum>();
	
	public void addRoleNostrum(RoleNostrum roleNostrum){
		this.nostrumMap.put(roleNostrum.getGoodsId(), roleNostrum);
	}
	
	public RoleNostrum getRoleNostrum(int goodsId){
		return this.nostrumMap.get(goodsId);
	}
	
	public void updateNostrumNum(int goodsId, int number){
		try {
			RoleNostrum rn = this.getRoleNostrum(goodsId);
			if(null == rn){
				rn = new RoleNostrum();
				rn.setRoleId(this.roleId);
				rn.setGoodsId(goodsId);
				rn.setGoodsNum(number);
				this.nostrumMap.put(goodsId, rn);
				GameContext.getBaseDAO().insert(rn);
			}else{
				int goodsNum = rn.getGoodsNum() + number;
				rn.setGoodsNum(goodsNum);
				GameContext.getBaseDAO().update(rn);
			}
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".updateNostrumNum error: ", e);
		}
	}
	
}
