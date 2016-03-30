package com.game.draco.app.horse.config;

import java.util.List;

import lombok.Data;

import org.python.google.common.collect.Lists;

import com.game.draco.app.chat.ChannelType;
import com.game.draco.base.CampType;

import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.QualityType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 坐骑星级加成
 * @author zhouhaobing
 *
 */
public @Data class HorseProp implements KeySupport<String>{

	//坐骑Id
	private int horseId;
	//品质
	private byte quality;
	//坐骑星级
	private byte star;
	//攻击加成
	private int additionAttack;
	//防御加成
	private int additionRit;
	//生命加成
	private int additionHp;
	//破防加成
	private int additionBreakDefense;
	//暴击加成
	private int additionCritAtk;
	//韧性加成
	private int additionCritRit;
	//闪避加成
	private int additionDodge;
	//命中加成
	private int additionHit;
	//移动速度
	private int moveSpeed;
	//资源ID
	private short resId;
	//ICONID
	private short iconId;
	//物品ID
	private int goodsId;
	//物品类型
	private byte goodsType;
	//物品数量
	private short goodsNum;
	//是否绑定
	private byte binded;
	//坐骑名称
	private String horseName;
	//升级成功描述
	private String des;
	// 升级世界广播
	private String broadcastInfo;
	
		
	@Override
	public String getKey(){
		return getHorseId() + Cat.underline + getQuality() + Cat.underline + getStar();
	}
	
	public List<AttriItem> getAttriItemList() {
		List<AttriItem> list = Lists.newArrayList();
		this.append(list, AttributeType.atk, additionAttack);
		this.append(list, AttributeType.rit, additionRit);
		this.append(list, AttributeType.maxHP, additionHp);
		this.append(list, AttributeType.breakDefense, additionBreakDefense);
		this.append(list, AttributeType.critAtk, additionCritAtk);
		this.append(list, AttributeType.critRit, additionCritRit);
		this.append(list, AttributeType.dodge, additionDodge);
		this.append(list, AttributeType.hit, additionHit);
		return list ;
	}
	
	private void append(List<AttriItem> list, AttributeType at, int value) {
		if(null == at || value <=0) {
			return ;
		}
		list.add(new AttriItem(at.getType(), value, false));
	}
	
	public int getAttriValue(AttributeType at){
		if(null == at){
			return 0 ;
		}
		switch(at){
			case atk : return additionAttack;
			case rit : return additionRit;
			case maxHP : return additionHp;
			case breakDefense : return additionBreakDefense;
			case critAtk : return additionCritAtk;
			case critRit : return additionCritRit;
			case dodge : return additionDodge;
			case hit : return additionHit;
			default : return 0 ;
	    }
	}
	
	/**
	 * 世界广播
	 * @param role
	 * @return
	 */
	public String getBroadcastTips(RoleInstance role){
		if(Util.isEmpty(this.broadcastInfo)){
			return "" ;
		}
		String defColor = ChannelType.Publicize_Personal.getColor();
		String roleName = Util.getColorRoleName(role, ChannelType.Publicize_Personal);
		// 根据物品品质赋予物品名称不同颜色
		String goodsName = Util.getColorString(horseName, QualityType.get(quality).getColor(), defColor);
		String qualityInfo = Util.getColorString(QualityType.get(quality).getName(), QualityType.get(quality).getColor(), defColor);
		String starInfo = String.valueOf(star) + Util.getColor(defColor);
		return this.broadcastInfo.replace(Wildcard.Role_Name, roleName).replace(Wildcard.GoodsName, goodsName).replace(Wildcard.Quality, qualityInfo).replace(Wildcard.Star, starInfo);
	}
	
}
