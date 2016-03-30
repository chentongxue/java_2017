package com.game.draco.app.tower;

import com.game.draco.app.tower.config.TowerLayerConfig;
import com.game.draco.app.tower.domain.RoleTowerInfo;
import com.game.draco.app.tower.domain.TowerCurrLayer;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import sacred.alliance.magic.vo.map.MapTowerContainer;

public interface TowerApp  extends Service, AppSupport {



    public TowerCurrLayer getTowerCurrLayer(String roleId) ;

    public MapTowerContainer getMapTowerContainer() ;

    public TowerLayerConfig getTowerLayerConfig(short gateId,byte layerId) ;

    public short getMaxGate() ;

    public byte getMaxLayer() ;

    public Message getTowerInfoMessage(RoleInstance role,short selectGateId) ;

    public Message getTowerInfoMessage(RoleInstance role) ;

    public Message getTowerGateInfoMessage(RoleInstance role,short gateId);

    public Message joinTower(RoleInstance role,short gateId,byte layerId) ;

	public RoleTowerInfo getRoleTowerInfo(String roleId);

    public Message resetTower(RoleInstance role,short gateId) ;

    public Message raidsTower(RoleInstance role,short gateId) ;

    public Message recvAward(RoleInstance role,short gateId,byte star) ;

    public Message getLayerCondMessage(short gateId,byte layerId);

    public void towerPassed(RoleInstance role) ;

}
