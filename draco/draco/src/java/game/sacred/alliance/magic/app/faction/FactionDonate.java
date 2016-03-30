//package sacred.alliance.magic.app.faction;
//
//import java.text.MessageFormat;
//import java.util.Map;
//
//import com.game.draco.GameContext;
//
//import lombok.Data;
//import sacred.alliance.magic.base.AttributeType;
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.domain.FactionRole;
//import sacred.alliance.magic.util.KeySupport;
//import sacred.alliance.magic.vo.RoleInstance;
//
//
///**
// * 公会捐献
// */
//public @Data class FactionDonate implements KeySupport<Integer> {
//	
//	private int id;
//	
//	private byte moneyType;
//	
//	private int money;
//	
//	private int contribute;
//	
//	private byte maxCounts;
//	
//	private int factionMoney;
//
//	/**
//	 * 判断是否可以捐献
//	 * @param role
//	 * @return
//	 */
//	public Result canDonate(RoleInstance role, FactionRole factionRole){
//		Result result = new Result();
//		if(maxCounts <= 0) {
//			return result.failure();
//		}
//		int roleCount = 0;
//		Map<Integer, Integer> donateMap = role.getFactionDonateMap();
//		if(donateMap.containsKey(id)){
//			roleCount = donateMap.get(id);
//		} 
//		if(roleCount >= maxCounts) {
//			String str = GameContext.getI18n().messageFormat(TextId.Faction_Donate_Max_Counts, maxCounts);
//			return result.setInfo(str);
//		}
//		AttributeType attriType = AttributeType.get(moneyType);
//		int roleAttr = role.get(attriType);
//		if(roleAttr < money) {
//			String str = GameContext.getI18n().messageFormat(TextId.Faction_Donate_MONEY_NOT_ENOUGH, attriType.getName(), money);
//			return result.setInfo(str);
//		}
//		
//		return result.success();
//	}
//	
//	@Override
//	public Integer getKey() {
//		return this.id;
//	}
//}
