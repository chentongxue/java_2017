package com.game.draco.app.title.action;

import com.game.draco.GameContext;
import com.game.draco.base.ExecutorBean;
import com.game.draco.message.request.C2348_TitleAutoUseReqMessage;
import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.executor.annotation.ExecutorMapping;
import sacred.alliance.magic.domain.GoodsTitle;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;


/**
 * 自动使用
 * 不返回错误信息
 */
@ExecutorMapping(name= ExecutorBean.userOrderedExecutor,cumulate = true)
public class TitleAutoUseAction extends BaseAction<C2348_TitleAutoUseReqMessage>{
	@Override
	public Message execute(ActionContext context, C2348_TitleAutoUseReqMessage req) {
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null ;
			}
			String goodsInstanceId = req.getGoodsInstanceId();
			//获得相关物品
			RoleGoods roleGoods = role.getRoleBackpack().getRoleGoodsByInstanceId(goodsInstanceId);
			if(null == roleGoods){
				return null ;
			}
			GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(roleGoods.getGoodsId());
            if(GoodsType.GoodsTitle != goodsType){
                return null ;
            }
			AbstractGoodsBehavior behavior = goodsType.getGoodsBehavior(GoodsBehaviorType.Use);
			if(null == behavior){
				return null ;
			}
            GoodsTitle title = GameContext.getGoodsApp().getGoodsTemplate(GoodsTitle.class,roleGoods.getGoodsId());
            if(null == title || !title.isPermanent()){
                //限时的不自动使用
                return null ;
            }
			UseGoodsParam param = new UseGoodsParam(role);
			param.setRoleGoods(roleGoods);
			param.setActivate(true);
			Result result = behavior.operate(param);
            if(!result.isSuccess()){
                return null ;
            }
			//调用使用物品任务接口
			GameContext.getUserQuestApp().useGoods(role, roleGoods.getGoodsId());
			return null ;
		}catch(Exception e){
			logger.error("",e);
		}
		return null;
	}
}
