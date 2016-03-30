package sacred.alliance.magic.base;

/**
 * @author Administrator
 * bigType<获得（1）,流通（2）,消耗（3）>
 */
public enum OutputConsumeType {
	/******获得***************/
	npc_store_buy_goods(1,1101,"NPC商店_购买物品"),
	npc_store_buy_money(1,1102,"NPC商店_贩卖得钱"),
	goods_exchange_output(1,1103,"物品兑换_获得"),
	currency_store_output(1,1104,"多货币商店_获得"),
	shop_buy_bind_money(1,1105,"商城_绑金购买"),
	shop_buy_gold_money(1,1106,"商城_金条购买"),
	shop_time_buy_output(1,1107,"限时商城_金条购买"),//ID=1107 不能再使用
	shop_time_output(1,1108,"限时商城_获得"),
	
	//hero_marking_off(1,1109,"英雄印记摘除"),
	//hero_luck_output(1,1110,"英雄抽卡获得"),
	sign_award_output(1,1111,"签到奖励"),
	hero_swallow_equip_mail(1,1112,"英雄吞噬后装备邮件"),
	praise_award_output(1,1113,"点赞奖励"),
	hero_equip_material_mix(1,1114,"合成"),
	
	monster_fall(1,1201,"怪物_掉落"),
	quest_award(1,1202,"任务_奖励"),
	quest_collection(1,1203,"任务_采集"),
	summon_monster_fall(1,1204,"召唤怪物_掉落"),
	goods_summon_output(1,1205,"召唤怪物_获得"),
	quest_poker_finish(1,1206,"诈金花_完成_获得"),
	npc_auto_max_hp(1,1207,"NPC直动调整maxhp"),
	
	compass_output(1,1300,"罗盘_获得"),
	compass_mail_output(1,1301,"罗盘_获得（邮件）"),
	daily_play_reward(1,1302,"日活跃奖励"),
	
	
	
	treasure_map_mail(1,1310,"藏宝图_获得(邮件)"),
	treasure_map(1,1311,"藏宝图_获得"),
	//online_reward(1,1312,"在线时长礼包"),
	// 活动相关
	//active_serial_login(1,1313,"连续登陆礼包"),
	arena_output(1,1314,"擂台赛_结束奖励"),
	rank_output_mail(1,1315,"排行榜_邮件发奖"),
	active_rank_award(1,1316,"排行榜活动_领奖"),
	offline_output(1,1319,"离线时间_获得"),
	siege_output(1,1320,"怪物攻城_获得（邮件）"),
	VIP_mail_output(1,1325,"VIP_获得（邮件）"),
	VIP_output(1,1326,"VIP_获得"),
	
	active_dps_award_mail(1,1330,"BossDps_奖励（邮件）"),
	arena1v1_award(1,1331,"封神榜奖励"),
	angel_chest(1,1333,"神仙宝箱获得"),
	angel_chest_mail(1,1334,"神仙宝箱邮件获得"),
	
	// 哥布林活动
	goblin_general_award(1,1336,"击杀哥布林获得"),
	goblin_secret_award(1,1337,"哥布林密境获得"),
	
	pet_pvp_award(1,1342,"宠物pvp奖励"),
	pet_pvp_award_mail(1,1343,"宠物pvp奖励(邮件)"),
	
    ladder_over_award(1,1350,"天梯单次奖励"),
    ladder_rank_award(1,1351,"天梯排名奖励"),
    
    faction_salary(1,1352,"公会工资"),//公会工资，待定
    
    camp_war_last_reward(1,1355,"阵营战结束奖励"),
	camp_war_reward(1,1356,"阵营战奖励"),
    carnival_reward(1,1357,"嘉年华奖励"),
    union_battle_role_reward(1,1358,"公会战_个人奖励"),
    faction_war_reward(1,1359,"门派战_门派奖励"),
    faction_war_gamble_reward(1,1360,"门派战_押注奖励"),
    
    union_integral_battle_role_reward(1,1361,"新公会战_个人奖励"),
    
    rune_discharge_pet(1,1402,"符文获得_宠物卸下"),
	rune_discharge_equ(1,1403,"符文获得_装备卸下"),	
	rune_reward_compose(1,1404,"符文获得_符文合成"),
	rune_reward_smelt(1,1405,"符文获得_符文熔炼"),
	
	invite_Vote(1,1501,"邀请码_激活其他人"),
	invite_Recv_Reward(1,1502,"邀请码"),
	invite_Recv_Reward_Mail(1,1503,"邀请码邮件"),
	
	arena_Top_Reward_Mail(1,1504,"大师赛邮件"),
	arena_Top_Reward(1,1505,"大师赛"),
	hero_arena_Reward(1,1506,"英雄试练_奖励"),
	
	treasure_box_output(1,1601,"宝箱_获得"),	
	role_born(1,1602,"角色_出生"),	
	camp_balance_select_camp(1,1604,"选阵营推荐奖励"),	
	
	recall_award_mail(1,1610,"回归奖励获得（邮件）"),
    tower_raids_award(1,1611,"爬塔扫荡奖励"),
    tower_star_award(1,1612,"爬塔星奖励"),
    tower_pass_award(1,1613,"爬塔通关地图奖励"),

	richman_event_output(1, 1620, "大富翁事件获得"),
	target_award_output(1, 1630, "目标系统_获得"),
	
	async_arena_role_add_money(1,1700,"异步竞技场胜利获得游戏币"),
	async_arena_role_add_exp(1,1701,"异步竞技场胜利获得经验"),
	async_arena_role_add_potential(1,1702,"异步竞技场胜利获得潜能"),
	async_arena_role_add_honor(1,1703,"异步竞技场胜利获得荣誉"),
	async_arena_role_rank_reward_mail(1,1704,"异步竞技场排行获得（邮件）"),
	async_arena_role_rank_reward(1,1705,"异步竞技场排行奖励获得"),
	
	luckybox_output(1,1710,"幸运转盘_获得奖励"),
	luckybox_mail_output(1,1720,"幸运转盘_获得奖励（邮件）"),
	
	user_prepaid(1,1901,"充值_获得"),
	gm_output(1,1902,"GM工具_获得"),	
	gm_output_mail(1,1903,"GM工具_获得（邮件）"),
	
	active_discount_award(1,1904,"充值活动_获得"),
	active_code_award(1,1905,"激活码兑换_获得"),
	level_gift_award(1,1907,"冲级奖励_获得"),
	recoup_mail(1,1908,"系统补偿_获得（邮件）"), 
	pay_first_award(1,1909,"首充奖励"),
	
//	faction_money_donate(1,1910,"门派捐献_获得"),
//	faction_money_war(1,1911,"门派战_获得"),
//	faction_money_quest(1,1912,"任务获得门派资金_获得"),
	
	active_3v3_chest(1,1913,"3V3宝箱_奖励"),
	
	// 首冲
	operate_first_pay(1,1914,"首冲奖励_获得"),
	//乐翻天
	donate_world_reward(1,1915,"乐翻天全民奖励_获得"),
	donate_rank_reward(1,1916,"乐翻天排名奖励_获得"),
	// 月卡
	month_card_reward(1,1917,"月卡每日福利_获得"),
	// 充值额外
	operate_pay_extra(1,1918,"首冲奖励_获得"),
	// 成长基金
	operate_grow_fund(1,1919,"首冲奖励_获得"),
	
	copyline_take(1,1920,"章节副本领奖_获得"),
	alchemy_award(1,1921,"点金获得"),
	hero_goods_to_shadow(1,1922,"英雄物品转换影子"),
	union_add_donate_dkp(1,1923,"公会捐献获得"),
	union_instance_dkp_reward(1,1924,"公会副本获得"),
    union_donate_dkp_reward(1,1925,"公会捐献获得"),
    union_auction_mail_reward(1,1926,"公会拍卖获得（邮件）"),
    union_auction_dkp_reward(1,1927,"公会拍卖失败返回DKP"),

    horse_goods_to_shadow(1,1928,"坐骑物品转换影子"),
    
    goods_decompose_reward(1,1929,"物品分解获得"),
    
    card_gold_output(1,1930,"金币抽卡_获得"),
    card_gem_output(1,1931,"钻石抽卡_获得"),
    card_activity_output(1,1932,"活动抽卡_获得"),
    
    //运营商城
    shop_carrier_output(1,1933,"运营商城获得"),
    
    //一键追回获得
    recovery_output(1,1934,"一键追回获得"),
    recovery_add_dkp_reward(1,1935,"一键追回获得DKP"),
    
    //一键追回获得
    survival_battle_output(1,1936,"生存战场参与获得"),
    survival_battle_win_output(1,1937,"生存战场胜利获得"),
    
    union_add_union_reward(1,1938,"加入历史公会获得"),

	//累计登录 连续登录
	accumulate_login_reward(1,1939,"连续登录获得奖励"),
    
	/******流通*********/
	trade_given(2,2101,"单对单交易_给出"),
	trade_output(2,2102,"单对单交易_获得"),
	
	auction_shop_goods_up(2,2103,"拍卖行_物品上架",false),
	auction_shop_goods_down(2,2104,"拍卖行_物品下架",false),
	auction_shop_goods_due(2,2105,"拍卖行_物品到期",false),
	auction_shop_buy_goods(2,2106,"拍卖行_买到物品",false),
	auction_shop_sell_add_money(2,2107,"拍卖行_卖出得钱",false),
	auction_shop_buy_minus_money(2,2108,"拍卖行_购买花钱",false),
	
	trade_up(2,2109,"单对单交易_上架"),
	trade_down(2,2110,"单对单交易_下架"),
	goods_split(2,2201,"物品_物品拆分"),
	goods_backpack_tidy(2,2202,"物品_背包整理"),
	goods_warehouse_add(2,2203,"物品_存入仓库"),
	goods_warehouse_take(2,2204,"物品_仓库取出"),
	goods_warehouse_clear(2,2205,"物品_仓库清空"),
	goods_warehouse_tidy(2,2206,"物品_仓库整理"),

	
	/******消耗**********/
	npc_store_buy_consume(3,3101,"NPC商店_购买消耗"),
	npc_store_repair_equip(3,3102,"NPC商店_修理装备"),
	goods_exchange_consume(3,3103,"物品兑换_消耗"),
	currency_store_output_consume(3,3104,"多货币商店_消耗"),
	shop_buy_consume(3,3105,"商城_购买花费"),
	npc_store_sell_consume(3,3106,"NPC商店_出售消耗"),
	shop_time_buy_consume(3,3107,"限时商城_购买消耗"),
	
	
	richman_dice_consume(3,3110,"大富翁_掷骰子消耗"),
	richman_event_consume(3,3111,"大富翁_事件消耗"),
	richman_use_card_consume(3,3112,"大富翁_使用道具卡消耗"),
	role_fatigue_clean_consume(3,3113,"疲劳度清除"),
	role_fatigue_auto_clean_consume(3,3114,"疲劳度自动清除"),
	
	quest_submit_consume(3,3201,"任务_交任务消耗"),
	quest_accept_consume(3,3202,"任务_接任务消耗"),
	quest_giveup_consume(3,3203,"任务_放弃任务消耗"),
	quest_pay_submit_consume(3,3204,"任务_付费完成消耗"),
	
	compass_consume(3,3305,"罗盘_消耗"),
	hero_arena_reborn_consume(3,3306,"英雄试练_复活消耗"),
	
	treasure_map_identify(3,3309,"藏宝图_鉴定消耗"),
	treasure_map_transmit(3,3310,"藏宝图_传送"),
	treasure_map_use(3,3311,"藏宝图_使用"),
	enter_copy_consume(3,3312,"副本_进入消耗"),
	hero_equip_material_mix_consume(3,3313,"合成消耗"),
	equip_upgrade_star_consume(3,3314,"装备升星消耗"),
	offline_consume(3,3315,"离线时间_消耗"),
	
	goods_summon_consume(3,3322,"物品召唤_消耗"),
	npc_inspire_consume(3,3323,"鼓舞_消耗"),
	quest_poker_refresh_consume(3,3324,"诈金花_刷新_消耗"),
	quest_poker_accept_quest_consume(3,3325,"诈金花_接任务_消耗"),
	
	shop_secret_goods_output(3,3353,"神秘商店购买"),
	shop_secret_refresh_consume(3,3354,"神秘商店刷新消耗"),
	shop_secret_buy_consume(3,3355,"神秘商店购买消耗"),
	
	goods_streng(3,3401,"装备衍生_强化"),
	goods_recast(3,3402,"装备衍生_重铸"),
	goods_nostrum_use(3,3403,"秘药物品_使用"),
	goods_decompose(3,3404,"物品分解_消耗"),
	
	rune_mosaic_pet(3,3411,"符文消耗_宠物镶嵌"),
	rune_mosaic_equ(3,3412,"符文消耗_装备镶嵌"),
	rune_discharge_pet_consume(3,3413,"宠物符文_卸下消耗"),
	rune_discharge_equ_consume(3,3414,"装备符文_卸下消耗"),
	rune_consume_compose(3,3415,"符文合成_合成消耗"),
	rune_consume_smelt(3,3416,"符文熔炼_熔炼消耗"),

	treasure_box_use(3,3601,"宝箱_使用"),
	treasure_box_key(3,3602,"宝箱_钥匙消耗"),
	rebron_scroll(3,3603,"复活_卷轴消耗"),
	rebron_scroll_fast_buy(3,3604,"复活_卷轴消耗（快捷购买）"),
	chat_world_speak(3,3605,"聊天_世界发言消耗"),
	chat_world_speak_fast_buy(3,3606,"聊天_世界发言消耗（快捷购买）"),
	npc_transmit(3,3607,"传送_NPC传送"),
	world_transmit(3,3608,"传送_世界传送"),
	role_discard_goods(3,3609,"物品_主动丢弃"),
	prop_renewal(3,3610,"限时物品_道具续期"),
	due_delete(3,3611,"限时物品_到期删除"),
	auction_shop_manage_fee(3,3612,"拍卖行_管理费",false),
	faction_create(3,3613,"门派_创建花费"),
	goods_use(3,3614,"物品_使用"),
	expansion_pack(3,3615,"扩充包_使用"),
	double_exp_scroll_use(3,3616,"双倍经验卷轴_使用"),
	role_level_up(3,3617,"角色_升级"),
	gm_consume(3,3619,"GM工具_扣除"),	
	title_renewal(3,3621,"称号_续费消耗"),
	
	goods_faction_use(3,3626,"门派物品_使用"),  
	union_impeach_consume(3,3627,"门派弹劾_使用"),
	
    player_skill_upgrade(3,3633,"角色_技能升级"),
    mail_pick_consume(3,3634,"付费邮件_付费"),
    social_flower_consume(3,3635,"好友送花_消耗"),
    social_praise_consume(3,3636,"点赞奖励_获得"),
    
    //宠物
    pet_goods_to_shadow(1,3663,"宠物物品转换影子"),
    pet_exchange_consume(3,3661,"宠物兑换消耗"),
    pet_starup_consume(3,3662,"宠物升星消耗"),
    pet_swallow_consume(3,3664,"宠物吞噬"),
    pet_goods_use(3,3665,"宠物物品使用"),
    pet_pvp_refresh_consume(3,3666,"刷新宠物列表消耗"),
    pet_swallow_renu_mail(1,3667,"宠物吞噬后符文邮件"),
   
    
    union_buff_dkp_consume(3,3703,"公会BUFF购买消耗"),//公会BUFF购买消耗
    union_shop_dkp_consume(3,3704,"商店购买消耗"),//商店购买
    union_exchange_dkp_consume(3,3705,"商店兑换消耗"),//商店兑换
//    faction_soul_feed(3,3703,"公会喂养"),//公会喂养(RMB)，待定
//    faction_soul_fly(3,3704,"公会神兽飞升"),//公会神兽飞升(RMB)，待定
    union_create_consume(3,3706,"公会创建_消耗"),//公会创建物品，待定
    union_activity_gem_consume(3,3707,"开启活动消耗"),//商店兑换
    union_auction_did_consume(3,3708,"押注消耗"),//押注
    union_exit_union_consume(1,3709,"退公会消耗"),
    
    hero_swallow_equip_del(3,3710,"英雄吞噬删除装备"),
    hero_swallow_consume(3,3711,"英雄吞噬"),
   // hero_marking_on_consume(3,3712,"英雄印记镶嵌"),
    hero_exchange_consume(3,3713,"英雄兑换消耗"),
   // hero_luck_consume(3,3714,"英雄抽卡消耗"),
    hero_quality_upgrade_consume(3,3715,"英雄升品消耗"),
   
    
    reborn(3,3802,"原地复活"),

    shop_secret_refresh(3,3805,"神秘商店刷新"),//神秘商店刷新
    
    ladder_challenge(3,3806,"天梯_付费挑战"),

    faction_war_gamble(3,3807,"门派战_押注"),
    faction_war_inprie(3,3808,"门派战_鼓舞"),    
	
	
    //PK
    pk_punish(3,3822,"PK规则惩罚消耗"),
    pk_remove_buff(3,3823,"清除PK规则消耗"),
    
    //阵营平衡
    camp_balance_change_camp(3,3824,"阵营转换"),
    
    //乐翻天
    donate_goods_donate(3,3825,"乐翻天捐献消耗"),
    
    
    faction_money_build(3,3830,"门派建筑升级消耗"),
    faction_money_war_inpire(3,3831,"门派战鼓舞消耗"),
    faction_money_summon(3,3832,"门派召唤消耗"),
    
    //坐骑
    horse_level_consume(3,3833,"坐骑升级消耗"),
    //坐骑骑术
    horse_manship_level_consume_goods(3,3834,"坐骑骑术升级消耗物品"),
    horse_manship_level_consume_money(3,3835,"坐骑骑术升级消耗金币"),
    horse_manship_level_consume_zp(3,3836,"坐骑骑术升级消耗潜能"),
    horse_upgrade_consume(3,3836,"坐骑升品消耗"),
    horse_goods_use(3,3837,"坐骑物品使用"),
    
	default_type(0,0,"默认"),
	role_login_check(-1,-1,"登录检测修改"),
	//炼金
	alchemy_consume(3,3838,"炼金钻石消耗"),
	//幸运宝箱
	luckybox_consume(3,3839,"幸运宝箱消耗"),
	
	async_arena_role_ref_consume_money(3,3842,"异步竞技场刷新对手消耗钻石"),
	async_arena_role_consume_money(3,3847,"异步竞技场购买次数消耗钻石"),
	  //坐骑兑换
    horse_exchange_consume(3,3848,"坐骑兑换消耗"),
    
    //抽卡
    choice_gold_money(3,3849,"抽卡消耗金币"),
    choice_gem_money(3,3850,"抽卡消耗钻石"),
    
    //购买副本次数
    copy_buy_money(3,3851,"购买副本次数"),
    
    //VIP激活功能消耗
    vip_consume(3,3852,"VIP消耗"),
    
    // 副本
    copy_hero_raids(1, 3853, "英雄副本扫荡获得"),
    copy_pass_reward(1, 3854, "副本通关奖励"),
    
    // 排位赛
    qualify_buy_cost(3,3901,"VIP购买挑战次数消耗"),
    qualify_challenge_output(1,3902,"排位赛挑战获得"),
    qualify_rank_output(1,3903,"排位赛排行榜获得"),
    
    // 运营商店购买
    shop_carrier_buy(3,3906,"运营商店购买"),
    
    //一键追回消耗
    recover_consume(3, 3907, "一键追回消耗"),
    //每日任务（翻牌）
    quest_poker_buy_count_consume(3,3908,"每日任务（翻牌）_购买轮数_消耗"),
    //天赋
    talent_item_consume(3,3910,"天赋培养物品消耗"),
  	talent_attr_consume(3,3911,"天赋培养属性消耗"),
	;
	private final int bigType;
	private final int type;
	private final String name;
	//是否对元宝进行充值活动统计
	private final boolean countGoldMoney ;
	
	OutputConsumeType(int bigType,int type, String name,boolean countGoldMoney){
		this.bigType = bigType;
		this.name=name;
		this.type=type;
		this.countGoldMoney = countGoldMoney ;
	}
	
	OutputConsumeType(int bigType,int type, String name){
		this(bigType,type,name,true);
	}
	

	public static OutputConsumeType getType(int type){
		for(OutputConsumeType mt : OutputConsumeType.values()){
			if(mt.getType() == type){
				return mt ;
			}
		}
		return null ;
	}
	
	public int getType(){
		return type;
	}
	
	public String getName() {
		return name;
	}


	public int getBigType() {
		return bigType;
	}

	public boolean isCountGoldMoney() {
		return countGoldMoney;
	}
	
}
