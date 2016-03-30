package sacred.alliance.magic.app.log;

import java.util.Date;
import java.util.List;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.param.StrengthenParam;
import sacred.alliance.magic.app.goods.behavior.result.StrengthenResult;
import sacred.alliance.magic.app.log.vo.StatRoleGoodsRecord;
import sacred.alliance.magic.app.quickbuy.QuickBuyGoods;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.domain.MailAccessory;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.QuestAward;

public interface StatLogApp extends Service{
	/***
	 * @param appId		产品id
	 * @param serverId   服务器id
	 * @param log4jPath  生成log4j-stat.properties文件的路径
	 * @param logDataPath  生成日志文件的路径
	 */
	public void initLog4j(String appId,String serverId,String log4jPath,String logDataPath);
	
	/***
	 * 给所有日志文件追加锚
	 */
	public void initAllLogCat();
	
	/**在线用户统计日志 */
	public void roleOnlineLog();
	
	/**
	 * 角色注册日志
	 * @param role
	 */
	public void roleRegisterLog(RoleInstance role, int roleCount);
	/**
	 * 角色下线日志
	 * @param role
	 */
	public void roleLogoutLog(RoleInstance role);
	/**
	 * 角色升级日志
	 * @param role
	 * @param changeLevel(变化值)
	 */
	public void roleLevelUpLog(RoleInstance role,int changeLevel);
	/**
	 * 角色死亡日志
	 * @param role
	 * @param acker
	 */
	public void roleDeathLog(RoleInstance role,AbstractRole acker);
	
	
	/**
	 * 物品强化日志
	 * @param role
	 * @param param
	 * @param result
	 */
	public void equipStrengLog(RoleInstance role, StrengthenParam param, StrengthenResult stResult);
	/**
	 * 定时任务
	 */
	public void printCat();
	
	/**
	 * 玩家杀怪日志
	 * @param role
	 * @param npc
	 */
	public void roleKillMonsterLog(RoleInstance role, NpcInstance npc, int silverMoney);
	/**
	 * 任务日志（0领取 1完成 2放弃 ）
	 * @param role
	 * @param questType
	 * @param questId
	 * @param award
	 */
	public void roleTaskLog(RoleInstance role, int questType, int questId,
			QuestAward award);
	/**
	 * 角色钱变化的日志
	 * @param role
	 * @param attriType
	 * @param ocType
	 * @param changeNum
	 * @param remark
	 */
	public void roleMoneyLog(RoleInstance role, AttributeType attriType,
			OutputConsumeType ocType, int changeNum, String remark);
	/**
	 * 角色物品日志
	 * @param role
	 * @param ocType
	 * @param remark
	 * @param upList
	 * @param addList
	 */
//	public void roleGoodsLog(RoleInstance role, OutputConsumeType ocType, String remark,List<RoleGoods> upList,List<RoleGoods> addList);
	/**
	 * 角色经验的日志
	 * @param role
	 * @param changeNum
	 * @param remark 备注信息
	 * @param ocType
	 */
	public void roleExpLog(RoleInstance role, int changeNum, String remark,
			OutputConsumeType ocType);
	
	/**
	 * 创建日志对像
	 * @param role
	 * @param roleGoods
	 * @param overlapChangeCount
	 * @param remark
	 * @param ocType
	 * @return
	 */
	public StatRoleGoodsRecord createRoleGoodsRecord(RoleInstance role,
			RoleGoods roleGoods, int overlapChangeCount, String remark,
			OutputConsumeType ocType);
	/**
	 * 角色物品日志
	 * @param role
	 * @param ocType
	 * @param remark
	 * @param list
	 */
	public void roleGoodsLog(RoleInstance role, OutputConsumeType ocType, String remark, List<StatRoleGoodsRecord> list);
	
	/**
	 * 角色物品日志
	 * @param role
	 * @param roleGoods
	 * @param overlapChangeCount
	 * @param remark
	 * @param ocType
	 */
	public void roleGoodsLog(RoleInstance role, RoleGoods roleGoods, int overlapChangeCount, String remark, OutputConsumeType ocType);
	
	/**
	 * 角色登录日志
	 * @param role
	 */
	public void roleLoginLog(RoleInstance role);
	/**
	 * 有效用户日志
	 * @param role
	 */
	public void userValidLog(RoleInstance role);

	/**
	 * 罗盘抽奖
	 * @param role
	 * @param id 罗盘ID
	 * @param count 抽奖次数
	 */
	public void compassLog(RoleInstance role, short id, int count);


	/**
	 *  每日活动参与度
	 * @param role
	 * @param activeId 活动ID
	 * @param activeName 活动名称
	 * @param activeType 活动类型
	 */
	public void activePartLog(RoleInstance role, short activeId, String activeName, int activeType);


	
	/**
	 * 客户端参数采集
	 * @param role
	 * @param targetInfo
	 */
	public void clientTargetCollect(RoleInstance role, String[] targetInfo);

	/**
	 * 玩家下线时打印物品日志
	 * @param role
	 * 打印的顺序
	 * userId     用户编号
	 * userName	    用户名称
	 * roleId     角色编号
	 * roleName   角色名称
	 * goodsInstanceId    物品实例编号
	 * storageType 背包类型
	 * goodsId    物品模版编号
	 * currDurable  当前耐久
	 * mosaic     镶嵌[孔的ID1:宝石ID1,孔的ID2: 宝石ID2]
	 * currOverlapCount  当前叠放数
	 * bind      绑定类型
	 * punched     id1,id2
	 * attrVar    随机装备属性类型
	 * starNum    强化等级
	 * otherParm   保留字段
	 * logoutTime  玩家退出时间
	 * deadline	   限时物品有效时间（分钟）
	 * expiredTime   限时物品过期时间
	 */
	public void logoutGoodsInfoLog(RoleInstance role);

	/**
	 * 邮件日志(type:1,发送成功时 2,玩家提取时)
	 * @param mail
	 * @param role
	 * @param type
	 */
	public void mailSendInfoLog(Mail mail, RoleInstance role);

	/**
	 * 怪物攻城boss死亡日志
	 * @param role
	 * @param npc
	 */
	public void bossDeathLog(NpcInstance npc);

	// /**
	// * 座骑使用或续费日志
	// * @param role
	// * @param goods
	// * @param type
	// */
	// public void mountsLog(RoleInstance role, GoodsMetamorphic goods, int
	// type);

	/**
	 * 离线经验日志
	 * @param role
	 * @param hookType
	 */
//	public void offhookLog(RoleInstance role, int hookType);

	
	
	
	/**
	 * 提取邮件时的日志
	 * @param mail
	 * @param role
	 * @param logContent
	 */
	public void mailSendInfoPickLog(RoleInstance role,Mail mail,List<MailAccessory> maList,int exp,int gold,int bindGold,int silverMoney,int contribute,int zp,int magicSoul,int dkp);
	
//	/**
//	 * 群雄逐鹿获奖门派日志
//	 * @param faction 门派对象
//	 * @param activeId 活动ID
//	 * @param index 获奖名次
//	 * @param endTime 结束发奖时间
//	 */
//	public void factionWarlordsLog(Faction faction, short activeId, int index, Date endTime);
	
	
	/**
	 * 删除邮件日志
	 * @param mailId
	 * @param role
	 */
	public void mailDelInfoDelLog(String mailId, RoleInstance role);
	
	/**
	 * 首次充值日志
	 * @param role
	 * @param payGold
	 * @param payChannelId 充值渠道
	 */
	public void rolePayLog(RoleInstance role, int payGold, int payChannelId);
	
	/**
	 * 每日在线时长日志
	 * @param role
	 * @param now
	 */
	public void roleOnlineTimeLog(RoleInstance role, Date now);
	
	/**
	 * 统计在线玩家每日在线时长日志
	 */
	public void allRoleOnlineTimeLog();
	
	/**
	 * 首次消费记录
	 * @param role
	 * @param consumeGold
	 * @param type
	 */
	public void roleConsumeLog(RoleInstance role, int consumeGold, OutputConsumeType type);
	
	/**
	 * 快速购买日志
	 * @param role
	 * @param consumeGold
	 * @param ocType
	 */
	public void roleQuickBuyLog(RoleInstance role, List<QuickBuyGoods> quickBuyGoodsList, OutputConsumeType ocType);
	
	/**
	 * 商城购买物品日志
	 * @param role
	 * @param goodsId
	 * @param goodsPrice
	 * @param goodsNum
	 * @param consumeGold
	 */
	public void roleShopBuy(RoleInstance role, int goodsId, int goodsPrice, int goodsNum, int consumeGold, AttributeType attriType, OutputConsumeType ocType);
	
	/**
	 * 所有角色的等级分布日志
	 */
	public void roleLevelDistributionLog();
	
	/**
	 * 点对点交易日志
	 * @param id
	 * @param roleId
	 * @param targetRoleId
	 * @param goods
	 * @param money
	 * @param ocType
	 */
	public void tradingLog(long id,String roleId, String targetRoleId, List<RoleGoods> goods, int money, OutputConsumeType ocType);
	
	/**
	 * 任务日志
	 * @param role
	 * @param quest
	 * @param exp
	 * @param money
	 */
	public void roleQuest(RoleInstance role, Quest quest, int exp, int silverMoney);
	
	/**
	 * 日剩余元宝数
	 */
	public void goldMoneyDayRemainLog();
	
	/**
	 * 物品掉落日志
	 * @param role
	 * @param npc
	 * @param goodsList
	 */
	public void goodsFallLog(RoleInstance role, NpcTemplate npc, List<GoodsOperateBean> goodsList);
}
