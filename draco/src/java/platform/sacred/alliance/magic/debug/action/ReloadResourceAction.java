package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.base.ResourceReloadType;
import com.game.draco.debug.message.request.C10029_ReloadResourceReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

import sacred.alliance.magic.app.config.ConfigCollection;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

public class ReloadResourceAction extends ActionSupport<C10029_ReloadResourceReqMessage>{

	@Override
	public Message execute(ActionContext context, C10029_ReloadResourceReqMessage req) {
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		try{
			int type = req.getType();
			Result result = new Result();
			ResourceReloadType reloadType = ResourceReloadType.get(type);
			switch(reloadType){
			case Shop:
				result = GameContext.getShopApp().reLoad();
				break;
			case Rate:
				if(GameContext.getRateApp().reload()){
					result.success();
				}
				break;
			case Config:
				result = ConfigCollection.reload();
				break;
			case Announce:
				result = GameContext.getAnnounceApp().reload();
				break;
			case Skill:
				if(GameContext.getSkillApp().reLoad()){
					result.success();
				}
				break;
			case Buff:
				if(GameContext.getBuffApp().reLoad()){
					result.success();
				}
				break;
			case Discount:
				result = GameContext.getDiscountApp().reLoad();
				break;
//			case Carnival:
//				result = GameContext.getCarnivalApp().reload();
//				break;
			case DoorDogBlackIp :
				result = GameContext.getDoorDogApp().reloadBlackIp();
				break ;
			case CardChoice:
				result = GameContext.getChoiceCardApp().reLoad();
				break;
			case ShopSecret:
				result = GameContext.getShopSecretApp().reLoad();
				break;
			case SimpleActive:
				result = GameContext.getSimpleApp().reload();
				break;
			case PayExtra:
				result = GameContext.getPayExtraApp().reload();
				break;
			case FirstPay:
				result = GameContext.getFirstPayApp().reLoad();
				break;
			case MonthCard:
				result = GameContext.getMonthCardApp().reLoad();
				break;
			case GrowFund:
				result = GameContext.getGrowFundApp().reload();
				break;
			}
			if(!result.isSuccess()){
				resp.setType((byte)RespTypeStatus.FAILURE);
				resp.setInfo(result.getInfo());
			}else{
				resp.setType((byte)RespTypeStatus.SUCCESS);
			}
			return resp;
		}catch(Exception e){
			logger.error("ReloadResourceAction error: ",e);
			resp.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return resp;
		}
		
	}

}
