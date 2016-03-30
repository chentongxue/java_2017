package sacred.alliance.magic.app.social.config;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.base.SkyEffectType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;

public @Data class SocialFlowerConfig {
	
	private short flowerId;//鲜花ID
	private String flowerName;//名称
	private int flowerNum;//花数量
	private short iconId;//图片ID
	private int gold;//消耗金条
	private int goodsId;//物品ID
	private int intimate;//亲密度
	private byte effect;//效果ID
	private short time;//特效时间
	private byte effectType;//特效广播方式
	private String broadcastInfo;//广播内容
	private String info;//鲜花效果说明
	
	private SkyEffectType skyEffectType;
	
	public void init(){
		this.skyEffectType = SkyEffectType.get(this.effectType);
		//验证数据配置是否合法
		String info = "load SocialFlowerConfig error: flowerId=" + this.flowerId + ".";
		if(this.flowerId <= 0){
			this.checkFail(info + "flowerId is config error!");
		}
		if(this.flowerNum <= 0){
			this.checkFail(info + "flowerNum is config error!");
		}
		if(null == this.skyEffectType){
			this.checkFail(info + "effectType is not exist!");
		}
		if(this.intimate < 0){
			this.checkFail(info + "intimate is config error!");
		}
		if(this.effect <= 0){
			this.checkFail(info + "effect is config error!");
		}
		if(this.time <= 0){
			this.checkFail(info + "time is config error!");
		}
		if(this.iconId <= 0){
			this.checkFail(info + "iconId is config error!");
		}
		if(this.gold <= 0){
			this.checkFail(info + "gold is config error!");
		}
		if(this.goodsId > 0){
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(this.goodsId);
			if(null == goodsBase){
				this.checkFail(info + "goodsId is config error!");
			}
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
