package sacred.alliance.magic.util;

import java.util.List;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.QualityType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.domain.GoodsBase;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.message.request.C0508_GoodsInfoViewIdBindReqMessage;

public class Wildcard {
	
	public static final String Role_Name = "${roleName}";//角色名称
	public static final String Role_Level = "${roleLevel}" ;//角色等级
	public static final String GoodsName = "${goodsName}";//物品名称
	public static final String Sender = "${sender}";//发送者
	public static final String Receiver = "${receiver}";//接收者
	public static final String Hurt = "${hurt}";//伤害值
	public static final String Index = "${index}";//索引或名次
	public static final String Number = "${number}";//数量
	public static final String MinNum = "${minNum}";//最小数量
	public static final String MaxNum = "${maxNum}";//最大数量
	
	public static final String AttrType = "${attrType}";//属性类型
	public static final String AttrName = "${attrName}";//属性名称
	public static final String GoodsType = "${goodsType}";//最大数量
	public static final String CopyName = "${copyName}";//最大数量
	public static final String MoneyName = "${moneyName}";//最大数量
	public static final String DateStr = "${dateStr}";//最大数量
	public static final String Attacker_Name = "${attackerName}";//攻击者名称
	
	public static final String Strengthen_Level = "${level}";//强化等级
	public static final String Quality = "${quality}";// 品质
	public static final String Star = "${star}";// 星级
	public static final String Compass_Name = "${compassName}";//罗盘名称
	public static final String Box_Name = "${boxName}";//box名称
	public static final String MapName = "${mapName}";//地图名称
	public static final String MapX = "${mapX}";//坐标X
	public static final String MapY = "${mapY}";//坐标Y
	public static final String Map_LineId = "${lineId}";//地图分线ID
	public static final String EventName = "${eventName}";//事件名称
	
	//Npc刷新
	public static final String NpcName = "${npcName}"; //NPC名称
	public static final String Npc_Say_Count = "${npcSayCount}"; //npc喊话次数
	public static final String Current_Map_Name = "${currentMapName}"; //当前地图名称
	public static final String Current_Map_LineId = "${currentMapLineId}"; //当前地图分线号
	public static final String Random_Role_Name = "${randomRoleName}"; //随机获取附近玩家名称
	public static final String System_DateTime = "${systemDateTime}"; //当前系统时间
	
	//藏宝图(虚空漩涡) 
	public static final String Treasure_Name = "${treasureName}";
	public static final String Monster1_Name = "${monster1}";
	public static final String Monster1_Num = "${num1}";
	public static final String Monster2_Name = "${monster2}";
	public static final String Monster2_Num = "${num2}";
	public static final String Monster3_Name = "${monster3}";
	public static final String Monster3_Num = "${num3}";
	
	public static final String BindMoney = "${bindMoney}";
	public static final String GameMoney = "${gameMoney}";
	public static final String TrGoods1_Name = "${goods1}";
	public static final String TrGoods1_Num = "${num1}";
	public static final String TrGoods2_Name = "${goods2}";
	public static final String TrGoods2_Num = "${num2}";
	public static final String TrGoods3_Name = "${goods3}";
	public static final String TrGoods3_Num = "${num3}";
	public static final String TrGoods4_Name = "${goods4}";
	public static final String TrGoods4_Num = "${num4}";
	
	
	//称号
	public static final String Title_Attacker_Name="${attackerName}";//攻击者角色名称
	public static final String Title_Victim_Name="${victimName}";//死亡角色名称
	public static final String Title_Role_Name="${roleName}";//角色名称
	public static final String Title_Name="${titleName}";//称号名称
	
	//排行榜
	public static final String Rank_Name="${rankName}";//排行榜名称
	public static final String Rank_RewardTime="${rewardTime}";//排行榜发奖邮件获得时间
	public static final String Rank_Rank="${rank}";//排行榜名称
	public static final String Rank_StartTime = "${startTime}"; //活动排行榜开始时间
	public static final String Rank_EndTime = "${endTime}"; //活动排行榜开始时间
	public static final String Rank_StatStartTime = "${statStartTime}"; //活动排行榜开始时间
	public static final String Rank_StatEndTime = "${statEndTime}"; //活动排行榜开始时间
	
	
	//门派
	public static final String Faction_Integral = "${integral}";
	
	public static final String Arena1V1_Rank="${rank}";
	
	//快捷购买
	public static final String QuickBuy_Goods_Name_Num="${goodsNameNum}";
	
	// 公会
	public static final String Union_Name = "${unionName}";
	
	
	public static final short chatGoodsCommandId = (new C0508_GoodsInfoViewIdBindReqMessage()).getCommandId();
	
	/**
	 * 给物品名称加上模版品质对应的颜色
	 * @param goodsId
	 * @param channelType
	 * @return
	 */
	public static String getChatGoodsName(int goodsId, ChannelType channelType){
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null == gb){
			return "" ;
		}
		String goodsName = gb.getName();
		if(null == channelType){
			return goodsName;
		}
		QualityType qualityType = QualityType.get(gb.getQualityType());
		if(null == qualityType){
			return goodsName;
		}
		return Util.getColor(qualityType.getColor()) + " [" + goodsName + "] " + Util.getColor(channelType.getColor());
	}
	
	/**
	 * 给物品名称加上品质对应颜色
	 * @param goodsId
	 * @param quality
	 * @return
	 */
	public static String getChatGoodsName(int goodsId, byte quality, ChannelType channelType) {
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if (null == goodsBase) {
			return "";
		}
		if (null == channelType) {
			return goodsBase.getName();
		}
		QualityType qualityType = QualityType.get(quality);
		if (null == qualityType) {
			return goodsBase.getName();
		}
		return Util.getColor(qualityType.getColor()) + " [" + goodsBase.getName() + "] " + Util.getColor(channelType.getColor());
	}
	
	public static String getQualityGoodsName(GoodsBase gb){
		String goodsName = gb.getName();
		QualityType qualityType = QualityType.get(gb.getQualityType());
		if(null == qualityType){
			return goodsName;
		}
		return Util.getColor(qualityType.getColor()) + " [" + goodsName + "] ";
	}
	
	public static String getNpcName(String npcTemplateId){
		NpcTemplate npcTemplate = GameContext.getNpcApp().getNpcTemplate(npcTemplateId);
		if(null == npcTemplate){
			return "";
		}
		return npcTemplate.getNpcname();
	}
	
	public static String getChatGoodsName(List<GoodsOperateBean> goodsList, ChannelType channelType){
		if(Util.isEmpty(goodsList)) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for(GoodsOperateBean goodsOperateBean : goodsList) {
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsOperateBean.getGoodsId());
			if(null == gb){
				continue;
			}
			String goodsName = gb.getName();
			if(null == channelType){
				return goodsName;
			}
			QualityType qualityType = QualityType.get(gb.getQualityType());
			if(null == qualityType){
				return goodsName;
			}
			sb.append(Util.getColor(qualityType.getColor()));
			sb.append("[").append(goodsName).append("]");
			if(goodsOperateBean.getGoodsNum() > 1){
				sb.append("*").append(goodsOperateBean.getGoodsNum());
			}
		}
		if(sb.length() > 0) {
			sb.append(Util.getColor(channelType.getColor()));
		}
		return sb.toString();
	}
	
	
	public static String getChatGoodsContent(GoodsBase gb, ChannelType channelType,int goodsNum){
		if(null == gb){
			return "" ;
		}
		StringBuffer buffer = new StringBuffer("");
		buffer.append("[\\P]") .append(chatGoodsCommandId).append(Cat.colon).append(gb.getId())
			  .append("[\\U\\]").append(getQualityGoodsName(gb)).append("[P]");
		if(goodsNum > 1){
			buffer.append("*").append(goodsNum);
		}
		if(null != channelType){
			buffer.append(Util.getColor(channelType.getColor()));
		}
		return buffer.toString();
	}
	
	public static String getChatGoodsContent(int goodsId, ChannelType channelType) {
		return getChatGoodsContent(goodsId,channelType,1);
	}
	
	public static String getChatGoodsContent(int goodsId, ChannelType channelType,int goodsNum) {
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null == gb){
			return "" ;
		}
		return getChatGoodsContent(gb,channelType,goodsNum);
	}
	
	
}
