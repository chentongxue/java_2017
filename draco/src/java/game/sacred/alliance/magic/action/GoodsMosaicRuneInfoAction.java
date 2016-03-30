package sacred.alliance.magic.action;

import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.rune.domain.MosaicRune;
import com.game.draco.message.request.C0559_GoodsMosaicGemInfoReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C0504_GoodsInfoViewRespMessage;

public class GoodsMosaicRuneInfoAction extends BaseAction<C0559_GoodsMosaicGemInfoReqMessage> {

	@Override
	public Message execute(ActionContext context, C0559_GoodsMosaicGemInfoReqMessage reqMsg) {
		try {
			// 判断装备或宠物
			// 获得镶嵌的符文MosaicRune
			// 获得C0504所需的信息
			// 返回C0504
			RoleInstance role = this.getCurrentRole(context);
			if (null == role) {
				return null;
			}
			int targetId = reqMsg.getTargetId();
			byte bagType = reqMsg.getBagType();
			// 如果是宠物符文
			if (0 == bagType) {
				RolePet rolePet = GameContext.getUserPetApp().getRolePet(role.getRoleId(), targetId);
				if (null == rolePet) {
					return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.ERROR_INPUT));
				}
				MosaicRune mosaicRune = rolePet.getMosaicRune(reqMsg.getHoleId());
				if (null == mosaicRune) {
					return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.ERROR_INPUT));
				}
				C0504_GoodsInfoViewRespMessage resp = new C0504_GoodsInfoViewRespMessage();
				resp.setId(String.valueOf(mosaicRune.getGoodsId()));
				resp.setBaseItem(mosaicRune.getGoodsBaseItem(role.getRoleId()));
				return resp;
			}
			// 如果是装备
			// 获得装备物品
			RoleGoods roleGoods = GameContext.getUserGoodsApp().getRoleGoods(role, StorageType.get(reqMsg.getBagType()), reqMsg.getGoodsId(), targetId);
			if (null == roleGoods) {
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.ERROR_INPUT));
			}
			MosaicRune[] runes = roleGoods.getMosaicRune();
			int holeId = reqMsg.getHoleId();
			// 容错判断
			if (null == runes || 0 == runes.length || 0 > holeId || holeId >= runes.length) {
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.ERROR_INPUT));
			}
			MosaicRune mosaicRune = runes[holeId];
			if (null == mosaicRune) {
				new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.ERROR_INPUT));
			}
			C0504_GoodsInfoViewRespMessage resp = new C0504_GoodsInfoViewRespMessage();
			resp.setId(String.valueOf(mosaicRune.getGoodsId()));
			resp.setBaseItem(mosaicRune.getGoodsBaseItem(role.getRoleId()));
			return resp;
		} catch (Exception e) {
			logger.error("", e);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.SYSTEM_ERROR));
		}
	}

}
