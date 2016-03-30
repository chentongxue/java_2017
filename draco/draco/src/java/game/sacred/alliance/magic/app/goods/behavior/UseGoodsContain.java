package sacred.alliance.magic.app.goods.behavior;

import java.text.MessageFormat;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0514_GoodsConfirmApplyReqMessage;

import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.app.goods.behavior.result.UseResult;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.domain.GoodsContain;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class UseGoodsContain extends AbstractGoodsBehavior{
	private final short CONFIRM_CMDID = new C0514_GoodsConfirmApplyReqMessage().getCommandId();
	
	public UseGoodsContain(){
		this.behaviorType = GoodsBehaviorType.Use;
	}
	
	@Override
	public Result operate(AbstractParam param) {
		
		UseGoodsParam useParam = (UseGoodsParam)param;
		RoleInstance role = useParam.getRole();
		RoleGoods roleGoods = useParam.getRoleGoods();
		//是否确定
		boolean isConfirm = useParam.isConfirm() ;
		int goodsId = roleGoods.getGoodsId();
		GoodsContain goodsContain = GameContext.getGoodsApp().getGoodsTemplate(GoodsContain.class, goodsId);
		if(goodsContain == null){
			return new Result().setInfo(Status.GOODS_NO_FOUND.getTips());
		}
		
		if(goodsContain.getLvLimit() > role.getLevel()){
			return new Result().setInfo(Status.Skill_RoleLevel_Fail.getTips());
		}
		
		byte containerType = (byte)goodsContain.getSecondType();
		int addContainNum = goodsContain.getGrid();
		if(addContainNum <= 0){
			return new Result().setInfo(Status.Sys_Param_Error.getTips());
		}
		
		StorageType storageType = StorageType.get(containerType);
		if(storageType == StorageType.bag){
			int rolePackCount = role.getBackpackCapacity();
			if(rolePackCount  >= ParasConstant.ROLE_BACKPACK_MAX_NUM){
				return new Result().setInfo(Status.GOODS_BAG_FULL.getTips());
			}
			addContainNum = Math.min(addContainNum, ParasConstant.ROLE_BACKPACK_MAX_NUM-rolePackCount);
			
			if(!isConfirm && addContainNum < goodsContain.getGrid()){
				//需要二次确认
				UseResult result = new UseResult();
				result.setConfirmInfo(roleGoods.getId());
				result.setMustConfirm(true);
				result.setConfirmCmdId(CONFIRM_CMDID);
				result.success();
				//info放到success后面,success会清除info信息
				//您使用的物品能扩展背包{0}个格子,但您格子数已达上限,只能再扩充{1}格,是否确定使用?
				String txt = GameContext.getI18n().messageFormat(TextId.CONTAIN_TOOLS_BAG_WILL_FULL_TIPS,
						String.valueOf(goodsContain.getGrid()),
						String.valueOf(addContainNum));
				result.setInfo(txt);
				return result ;
			}
			role.getRoleBackpack().expansionStorage(addContainNum);
			GameContext.getUserGoodsApp().notifyBackpackExpansionMessage(role, addContainNum);
		}
		boolean consume = goodsContain.hasApplyDisappear();
		if(consume){
			GameContext.getUserGoodsApp()
				.deleteForBagByInstanceId(role, roleGoods.getId(), 1, OutputConsumeType.expansion_pack);
		}
		return new Result().success();
	}

	
}
