package com.game.draco.app.hero;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.Util;

import com.alibaba.fastjson.JSON;
import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.HeroEquip;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.component.ssdb.SSDB;
import com.game.draco.component.ssdb.SSDBID;
import com.game.draco.component.ssdb.SSDBUtil;

public class HeroSSDBStorage implements HeroStorage {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Getter @Setter private SSDBUtil ssdbUtil;
	
	
	private String makeRoleHerosName(){
		return SSDBID.ROLE_ARENA_HEROS + GameContext.getServerId();
	}
	
	private String makeHeroEquipName(){
		return SSDBID.HERO_EQUIP_RECORD + GameContext.getServerId();
	}
	
	@Override
	public HeroEquip getHeroEquip(String roleId) {
		SSDB ssdb = null;
		try {
			ssdb = this.ssdbUtil.getSSDB();
			String str = ssdb.hgetString(this.makeHeroEquipName(), roleId);
			if (Util.isEmpty(str)) {
				return null ;
			}
			return JSON.parseObject(str, HeroEquip.class);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".getHeroEquip error: ", e);
			return null;
		} finally {
			this.ssdbUtil.returnSSDB(ssdb);
		}
	}

	@Override
	public void saveHeroEquip(HeroEquip heroEquip) {
		SSDB ssdb = null;
		try {
			String roleId = heroEquip.getRoleId() ;
			ssdb = this.ssdbUtil.getSSDB();
			ssdb.hset(this.makeHeroEquipName(), roleId, JSON.toJSONString(heroEquip));
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".saveHeroEquip error: ", e);
		} finally {
			this.ssdbUtil.returnSSDB(ssdb);
		}
	}

	@Override
	public List<RoleHero> getRoleHeros(String roleId) {
		SSDB ssdb = null;
		try {
			ssdb = this.ssdbUtil.getSSDB();
			String str = ssdb.hgetString(this.makeRoleHerosName(), roleId);
			if (Util.isEmpty(str)) {
				return null ;
			}
			return JSON.parseArray(str, RoleHero.class);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".getRoleHeros error: ", e);
			return null;
		} finally {
			this.ssdbUtil.returnSSDB(ssdb);
		}
	}

	@Override
	public void saveRoleHeros(String roleId, List<RoleHero> heros) {
		if (Util.isEmpty(heros)) {
			return ;
		}
		SSDB ssdb = null;
		try {
			ssdb = this.ssdbUtil.getSSDB();
			ssdb.hset(this.makeRoleHerosName(), roleId, JSON.toJSONString(heros));
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".saveRoleHeros error: ", e);
		} finally {
			this.ssdbUtil.returnSSDB(ssdb);
		}
	}

	
}
