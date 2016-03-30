package sacred.alliance.magic.app.goods.behavior;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0514_GoodsConfirmApplyReqMessage;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.goods.behavior.result.UseResult;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsContain;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class UseGoodsContain extends AbstractGoodsBehavior {
    private final short CONFIRM_CMDID = new C0514_GoodsConfirmApplyReqMessage().getCommandId();

    public UseGoodsContain() {
        this.behaviorType = GoodsBehaviorType.Use;
    }

    @Override
    public Result operate(AbstractParam param) {

        UseGoodsParam useParam = (UseGoodsParam) param;
        RoleInstance role = useParam.getRole();
        RoleGoods roleGoods = useParam.getRoleGoods();
        //是否确定
        boolean isConfirm = useParam.isConfirm();
        int goodsId = roleGoods.getGoodsId();
        int useCount = useParam.getUseCount();
        GoodsContain goodsContain = GameContext.getGoodsApp().getGoodsTemplate(GoodsContain.class, goodsId);
        if (goodsContain == null) {
            return new Result().setInfo(Status.GOODS_NO_FOUND.getTips());
        }

        if (goodsContain.getLvLimit() > role.getLevel()) {
            return new Result().setInfo(Status.Skill_RoleLevel_Fail.getTips());
        }

        byte containerType = (byte) goodsContain.getSecondType();
        int grid = goodsContain.getGrid();
        if (grid <= 0) {
            return new Result().setInfo(Status.Sys_Param_Error.getTips());
        }

        int delCount = 1;
        StorageType storageType = StorageType.get(containerType);
        if (storageType != StorageType.bag) {
            return new Result().setInfo(Status.Sys_Param_Error.getTips());
        }
        int rolePackCount = role.getBackpackCapacity();
        if (rolePackCount >= ParasConstant.ROLE_BACKPACK_MAX_NUM) {
            return new Result().setInfo(Status.GOODS_BAG_FULL.getTips());
        }
        int addContainNum = Math.min(grid * useCount, ParasConstant.ROLE_BACKPACK_MAX_NUM - rolePackCount);
        delCount = addContainNum % grid == 0 ? addContainNum / grid : addContainNum / grid + 1;
        if (delCount > useCount) {
            return new Result().setInfo(getText(TextId.ERROR_DATA));
        }
        if (!isConfirm && delCount * grid != addContainNum) {
            //需要二次确认
            UseResult result = new UseResult();
            //id + 数量
            result.setConfirmInfo(roleGoods.getId() + "&" + useCount);
            result.setMustConfirm(true);
            result.setConfirmCmdId(CONFIRM_CMDID);
            result.success();
            //info放到success后面,success会清除info信息
            //您使用的物品能扩展背包{0}个格子,但您格子数已达上限,只能再扩充{1}格,是否确定使用?
            String txt = GameContext.getI18n().messageFormat(TextId.CONTAIN_TOOLS_BAG_WILL_FULL_TIPS,
                    String.valueOf(goodsContain.getGrid()),
                    String.valueOf(addContainNum),
                    String.valueOf(delCount));
            result.setInfo(txt);
            return result;
        }
        GoodsResult result = GameContext.getUserGoodsApp()
                .deleteForBagByInstanceId(role, roleGoods.getId(), delCount, OutputConsumeType.expansion_pack);
        if(!result.isSuccess()){
            return result ;
        }
        role.getRoleBackpack().expansionStorage(addContainNum);
        GameContext.getUserGoodsApp().notifyBackpackExpansionMessage(role, addContainNum);
        return new Result().success();
    }


}
