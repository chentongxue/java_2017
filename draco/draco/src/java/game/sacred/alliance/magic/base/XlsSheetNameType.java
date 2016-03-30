package sacred.alliance.magic.base;

import com.game.draco.app.quest.config.QuestXlsType;

public enum XlsSheetNameType {
	
	//角色出生
	role_born("role_born.xls", "born"),
	role_born_hero("role_born.xls", "hero"),
	//角色复活
	role_reborn_point("role_reborn.xls","reborn_point"),
	role_reborn_mode("role_reborn.xls","mode"),
	role_reborn_die_info("role_reborn.xls","die_info"),
	//等级配置
	role_levelup("role_levelup.xls", "levelup"),
	
	//NPC属性
	npc_template("npc_template.xls","template"),
	//NPC BUILD属性
	npc_buildtemplate("npc_buildtemplate.xls","template"),
	//势力关系匹配
	force_config("force_config.xls","config"),
	//势力关系匹配
	force_npc_config("force_npc_config.xls","config"),
	//NPC AI
	npc_ai("npc_ai.xls","ai"),
	npc_ai_auto_maxhp("npc_ai.xls","auto_max_hp"),
	boss_action("npc_ai.xls","boss_action"),
	npc_death_refresh_rule("npc_ai.xls","npc_refresh_rule"),
	npc_death_refresh_group("npc_ai.xls","death_refresh_group"),
	boss_refresh_group("npc_ai.xls","boss_refresh_rule"),
	
	//NPC商铺
	npc_store("npc_store.xls","shop"),
	//NPC商铺
	npc_storeName("npc_store.xls","shop_name"),
	//NPC教学
	npc_teach("npc_teach.xls","teach"),
	//NPC传送
	npc_transfer("npc_transfer.xls","transfer"),
	//刷怪
	npc_refresh_config("npc_refresh.xls","config"),
	npc_refresh_rule("npc_refresh.xls","rule"),
	npc_refresh_loot("npc_refresh.xls","boss_loot"),
	
	//技能
	skill("skill_list.xls","skill"),
	skill_Learn("skill_learn.xls","learn_list"),
	skill_role_learn("skill_learn.xls","role_skill"),
	//buff
	buff("skill_list.xls","buff"),
	//伤害减免系数
	hurtRemit("skill_list.xls","hurt_remit"),
	
	//采集点
	collect_point("collect_point.xls","collect_point"),
	
	//任务相关
	quest_award("quest_award.xls","award"),//任务奖励
	//任务接取限制
	quest_visible("quest_service.xls","visible"),
	//任务活动地图限制
	quest_active_limit("quest_service.xls","active_limit"),
	//主线任务链
	quest_main_line_chain("quest_service.xls","main_line_chain"),
	
	//公会
	faction_create("faction_config.xls","create"),
	faction_build("faction_build.xls","build"),
	faction_pvp_map("faction_config.xls","faction_pvp_map"),
	faction_upgrade("faction_config.xls","upgrade"),
	faction_power("faction_config.xls","power"),
	faction_describe("faction_config.xls","describe"),
	faction_donate("faction_config.xls","donate"),
	faction_skill("faction_build.xls","faction_skill"),
	faction_salary("faction_config.xls","salary"),
	faction_active("faction_config.xls","active"),
	
	//门派神兽
	god_beast("faction_god_beast.xls","god_beast"),
	god_beast_attribute("faction_god_beast.xls","attribute"),
	god_beast_feed("faction_god_beast.xls","feed"),
	god_beast_fly("faction_god_beast.xls","fly_config"),
	god_beast_inspire("faction_god_beast.xls","inspire"),
	god_beast_inspire_buff("faction_god_beast.xls","inspire_buff"),
	
	//合成配方
	mix_formula("mix_formula.xls","formula"),
	
	//装备衍生
	//强化
	equip_qianghua_list("equip_derive_qianghua.xls","list"),
	equip_qianghua_effect("equip_derive_qianghua.xls","effect"),
	//升级
	equip_shengji_list("equip_derive_shengji.xls","list"),
	//洗练
	equip_xilian_list("equip_derive_xilian.xls","list"),
	equip_xilian_lock_ratio("equip_derive_xilian.xls","lock_ratio"),
	equip_xilian_attr_weight("equip_derive_xilian.xls","attr_weight"),
	//宝石
	equip_baoshi_config("equip_derive_baoshi.xls","config"),
	//翅膀命格
	equip_wing_grid("equip_derive_chibang.xls","wing_grid"),
	
	//传说套装
	story_suit("equip_story_suit.xls","story_suit"),
	story_suit_equip_list("equip_story_suit.xls","equip_list"),
	story_suit_default_image("equip_story_suit.xls","default_image"),
	
	//套装
	suit_list("suit_config.xls","list"),
	suit_attributes("suit_config.xls","attributes"),
	
	goods_buff_detail("goods_buff_detail.xls","buff"),
	goods_consume_buff("goods_food.xls","buff"),//消耗品buff
	goods_exp_buff("goods_exp.xls","buff"),
	
	//世界掉落列表
	loot_list_world("loot_world.xls","list"),
	//世界掉落组
	loot_group_world("loot_world.xls","group"),
	//NPC掉落列表
	loot_list_npc("loot_npc.xls","list"),
	//NPC掉落组
	loot_group_npc("loot_npc.xls","group"),
	
	//副本逻辑
	copy_config("copy_config.xls","copy_config"),
	copy_npc_rule("copy_config.xls","npc_rule"),
	copy_map_config("copy_config.xls","map_config"),
	copy_map_role_rule("copy_config.xls","map_role_rule"),
	copy_fall_config("copy_config.xls","fall"),
	copy_point_config("copy_config.xls","point"),
	//章节副本
	copy_line_config("copy_line.xls","copy_list"),
	copy_line_map_rule("copy_line.xls","map_rule"),
	copy_line_reward_list("copy_line.xls","reward_list"),
	
	//小地图
	map_basic_config("map_basic_config.xls","config"),
	
	//拍卖行菜单
	auction_menu("auction_menu.xls","menu"),
	auction_adapter("auction_menu.xls","convert"),
	auction_fee_info("auction_menu.xls","fee_info"),
	//npc功能
	npc_function("npc_function.xls","npc_function"),
	
	
	//活动
	active_total("active_total.xls","active_total"),

	//活动-BossDPS
	active_dps_index("active_dps.xls","dps_index"),
	active_dps_reward_detail("active_dps.xls","reward_detail"),
	active_dps_map_config("active_dps.xls","map_config"),
	active_dps_hurt_Point("active_dps.xls","hurt_Point"),
	
	
	//大地图
	map_world("map_world.xls","map_world"),

	//擂台赛
	arena_list("arena_list.xls","config"),
	arena_reward_1v1_bout("arena_list.xls","reward_1v1_bout"),
	//1v1排名奖励
	arena_reward_1v1_finish("arena_list.xls","reward_1v1_finish"),
	
	//商城
	shop_list("shop_list.xls","shop_list"),
	//限时商城
	shop_limit_list("shop_list.xls","limit_list"),
	shop_limit_param("shop_list.xls","limit_param"),
	//神秘商店
	shop_secret_role_rule("shop_secret.xls","role_rule"),
	shop_secret_rule("shop_secret.xls","rule"),
	shop_secret_refresh("shop_secret.xls","refresh"),
	
	//在线时长奖励
	online_reward("role_online_reward.xls","online_reward"),
	
	//物品兑换
	exchange_menu("npc_exchange.xls", "menu"),
	exchange_item("npc_exchange.xls", "item"),
	exchange_condition("npc_exchange.xls", "condition"),
	//宝藏
	treasure_item("goods_treasure.xls", "item"),
	treasure_maps("goods_treasure.xls", "maps"),
	treasure_monsters("goods_treasure.xls", "monsters"),
	treasure_goods("goods_treasure.xls", "goods"),
	
	//分线地图
	map_line("map_lineconfig.xls","lineMapConfig"),
	
	//激活码
	gift_code("gift_code.xls","list"),
	
	//折扣活动
	discount_list("active_discount.xls", "list"),
	discount_cond("active_discount.xls", "cond"),
	discount_reward("active_discount.xls", "reward"),
	discount_charge_desc("active_discount.xls", "chargeDesc"),
	
	//勋章
	medal_qianghua("medal_config.xls","qianghua"),
	medal_xiangqian("medal_config.xls","xiangqian"),
	medal_xilian("medal_config.xls","xilian"),
	medal_attribute("medal_config.xls","attribute"),
	medal_default_icon("medal_config.xls","default_icon"),
	
	//排行榜
	rank_list("rank_list.xls", "list"),
	rank_world("rank_list.xls", "world_ranks"),
	rank_rewardRole("rank_list.xls", "rewardRole"),
	rank_rewardRank("rank_list.xls", "rewardRank"),
	rank_reward("rank_list.xls", "reward"),
	//rank_BCEx("rank_list.xls", "broadcastEx"),
	rank_layout("rank_list.xls", "rank_layout"),
	
	//活动排行榜
	active_rank("active_rank.xls", "list"),
	
	
	//福利-离线经验
	benefit_offline_exp("benefit_offline_exp.xls","level_exp"),
	benefit_offline_award("benefit_offline_exp.xls","award"),
	benefit_popup_param("benefit_offline_exp.xls","popup_param"),
	//福利-每日登录
	benefit_login_count_reward("benefit_daily_login.xls","reward"),
	benefit_login_count_special_reward("benefit_daily_login.xls","special_reward"),
	
	//怪物攻城活动
	siege_award_add_rate("active_monster_siege.xls","award_add_rate"),
	siege_win_award("active_monster_siege.xls","win_award"),
	siege_fail_award("active_monster_siege.xls","fail_award"),
	siege_mapid_active("active_monster_siege.xls","mapid_active"),
	
	//VIP <new>
	VipLevelUpConfig("vip.xls","vip_levelup"),
	VipPrivilegeConfig("vip.xls","vip_privilege"),
	VipConfig("vip.xls","vipconf"),
	//系统广播
	sys_broadcast("system_broadcast.xls","broadcast"),
	
	//任务
	quest_config_list("quest_config_list.xls",QuestXlsType.getSheetNames()),
	
	//随机任务-扑克
	poker_quest_weight("quest_poker.xls","quest_weight"),
	poker_quest_list("quest_poker.xls","quest_list"),
	poker_award_list("quest_poker.xls","award_list"),
	poker_award_weight("quest_poker.xls","award_weight"),
	poker_base_config("quest_poker.xls","base_config"),
	poker_second_weight("quest_poker.xls","poker_second_weight"),
	poker_third_weight("quest_poker.xls","poker_third_weight"),
	poker_award("quest_poker.xls","poker_award"),
	poker_award_ratio("quest_poker.xls","poker_award_ratio"),
	
	//动态菜单
	menu_config("menu_config.xls","config"),
	menu_hit("menu_config.xls","hint"),
	
	//罗盘――抽奖
	compass("compass.xls","compass"),
	//罗盘配置
	compassConfig("compass.xls","compass_config"),
	
	//炼金系统配置
	AlchemyConfig("alchemy.xls","alchemy_config"),
	AlchemyVipDailyConfig("alchemy.xls","alchemy_vip_daily_config"),
	AlchemyOutBreakConfig("alchemy.xls","alchemy_outbreak_config"),
	Alchemy("alchemy.xls","alchemy"),
	AlchemyLeveReward("alchemy.xls","level_reward"),
	//幸运宝箱XLS配置
	LuckyBoxVipTimesConfig("luckybox.xls","vip_times"),
	LuckyBoxRewardPoolConfig("luckybox.xls","award_pool"),
	LuckyBoxDiamandsConsumeConfig("luckybox.xls","diamands_consume"),
	LuckyOddsConfig("luckybox.xls","lucky_odds"),
	LuckyBoxVipRewardPoolSizeConfig("luckybox.xls","vip_award_pool_size"),
	LuckyBoxAppConfig("luckybox.xls","app_config"),
	//社交
	social_intimate("social_config.xls","intimate"),
	social_flower("social_config.xls","flower"),
	social_desc("social_config.xls","desc"),
	social_batch_friend("social_config.xls","batch_friend"),
	
	//召唤
	summon("npc_summon.xls", "summon"),
	summon_condition("npc_summon.xls", "condition"),
	summon_rule("npc_summon.xls","summon_rule"),
	summon_group("npc_summon.xls","summon_group"),
	
	
	//嘉年华活动
	carnival_active("carnival.xls","active"),
	carnival_item("carnival.xls","item"),
	carnival_rule("carnival.xls","rule"),
	carnival_rankReward("carnival.xls","rankReward"),
	carnival_reward("carnival.xls","reward"),
	
	//NPC鼓舞
	npc_inspire_func("npc_inspire.xls","npc_function"),
	npc_inspire_ratio("npc_inspire.xls","buff_ratio"),
	
	//聊天
	chat_config("chat_config.xls","chat_limit"),
	
	//背包扩充
	container_config("container_config.xls","config"),
	
	//怪物刷新
	map_npc_refresh_rule("refresh_rule.xls","rule"),
	
	//神仙宝箱
	angel_chest_hours("active_angelchest.xls","refreshHours"),
	angel_chest_map("active_angelchest.xls","mapId"),
	
	//相关宝箱配置
	chest_type("chest_config.xls","chestType"),
	chest_refresh("chest_config.xls","chestRefresh"),
	chest_range("chest_config.xls","refreshRange"),
	
	
	//物品广播
	broadcast_goods("broadcast_goods.xls","loot"),
	broadcast_box("broadcast_goods.xls","box"),
	
	//邀请
	invite_activated_reward("invite_config.xls","activated_reward") ,
	invite_config("invite_config.xls","config") ,
	invite_download("invite_config.xls","download") ,
	
	//大师赛
	arean_top_enter_points("arena_top.xls","enterPoints"),
	arean_top_reward("arena_top.xls","reward"),
	arena_top_config("arena_top.xls","config"),
	
	//门派战
	faction_war_config("faction_war.xls","config"),
	faction_war_award_config("faction_war.xls","award_config"),
	faction_war_award_rule("faction_war.xls","award_rule"),
	faction_war_role_award_config("faction_war.xls","role_award_config"),
	faction_war_gamble("faction_war.xls","gamble_config"),
	faction_war_faction_award("faction_war.xls","faction_awrad_config"),
	faction_war_broadcast("faction_war.xls","broadcast"),
	
	//回归奖励
	recall_award_config("recall_reward.xls", "award_config"),
	
	//pk规则
	pk_config("pk.xls", "config"),
	pk_kill_config("pk.xls", "kill_Config"),
	
	//阵营平衡
	camp_balance_recommend("camp_balance.xls","recommend_config"),
	camp_balance_change("camp_balance.xls","change_config"),
	
	//公共设置
	public_set("public_set.xls", "public_set"),
	
	//剧情配置
	drama_npcs("drama_config.xls","npcs"),
	drama_camera("drama_config.xls","camera"),
	drama_npc_appear("drama_config.xls","npc_appear"),
	drama_npc_disppear("drama_config.xls","npc_disppear"),
	drama_npc_move("drama_config.xls","npc_move"),
	drama_talk("drama_config.xls","talk"),
	drama_attack("drama_config.xls","attack"),
	drama_bubble("drama_config.xls","chat_bubble"),
	drama_screen_shock("drama_config.xls","screen_shock"),
	drama_npc_set("drama_config.xls","npc_set"),
	drama_tips("drama_config.xls","tips"),
	drama_dramacompose("drama_config.xls","dramacompose"),
	drama_drama("drama_config.xls","drama"),
	drama_trigger_point("drama_trigger.xls","point"),
	drama_trigger_enter_map("drama_trigger.xls","enterMap"),
	drama_trigger_quest("drama_trigger.xls","quest"),
	drama_trigger_npc("drama_trigger.xls","npc"),

	
	//秘药
	goods_nostrum_show("goods_nostrum.xls","show_list"),	
	goods_nostrum_limit("goods_nostrum.xls","level_limit"),
	
	//英雄
	hero_base_config("hero_config.xls","base_config"),
	hero_quality_upgrade("hero_config.xls","hero_quality_upgrade"),
	attribute_hero_rate("hero_config.xls","attribute_hero_rate"),
	attribute_quality_rate("hero_config.xls","attribute_quality_rate"),
	attribute_type_rate("hero_config.xls","attribute_type_rate"),
	attribute_herolevel_rate("hero_config.xls","attribute_herolevel_rate"),
	
	hero_levelup("hero_config.xls","hero_levelup"),
	hero_equip_open("hero_config.xls","hero_equip_open"),
	hero_luck("hero_config.xls","hero_luck"),
	hero_luck_show("hero_config.xls","hero_luck_show"),
	hero_luck_goods("hero_config.xls","hero_luck_goods"),
	hero_luck_firstfee_goods("hero_config.xls","hero_luck_firstfee_goods"),
	hero_exchange("hero_config.xls","hero_exchange"),
	hero_love("hero_config.xls","hero_love"),
	
	//英雄试练
	hero_arena_base("hero_arena_config.xls","base"),
	hero_arena_match_rule("hero_arena_config.xls","match_rule"),
	hero_arena_reward("hero_arena_config.xls","reward"),
	
	//女神
	goddess_config("goddess_config.xls", "config"),
	goddess_pvp_config("goddess_config.xls", "pvpconfig"),
	goddess_list("goddess_config.xls", "list"),
	goddess_levelup("goddess_config.xls", "levelup"),
	goddess_upgrade("goddess_config.xls", "upgrade"),
	goddess_linger("goddess_config.xls", "linger"),
	goddess_bless("goddess_config.xls", "bless"),
	goddess_refresh("goddess_config.xls", "refresh"),
	
	//战斗力
	attri_battle_score("attribute.xls","battle_score"),
	
	//冲级奖励
	level_gift("level_gift.xls","leveling"),
	
	sign_config("sign_config.xls","config"),
	
	//坐骑
	horse_base_config("horse_config.xls","base"),
	horse_exp_config("horse_config.xls","exp"),
	horse_addition_prop_config("horse_config.xls","addition_prop"),
	horse_manship_des_config("horse_config.xls","manship_des"),
	horse_manship_consume_config("horse_config.xls","manship_consume"),
	horse_manship_level_filter_config("horse_config.xls","manship_level_filter"),
	horse_manship_addition_prop_config("horse_config.xls","manship_addition_prop"),
	horse_race_type_config("horse_config.xls","raceType"),
	
	//大富翁
	richman_event("richman_config.xls", "events"),
	richman_map_event("richman_config.xls", "map_event"),
	richman_random_event_ids("richman_config.xls", "random_event_ids"),
	richman_config("richman_config.xls", "config"),
	richman_card_ids("richman_config.xls", "cards"),
	richman_dice_price("richman_config.xls", "dice_price"),
	richman_box_config("richman_config.xls", "box_config"),
	richman_stat_config("richman_config.xls", "stat"),
	richman_random_card("richman_config.xls", "random_card"),
	
	//异步竞技场
	async_group_config("async_arena_config.xls","group_config"),
	async_refresh_config("async_arena_config.xls","refresh_config"),
	async_reward_config("async_arena_config.xls","reward_config"),
	async_rank_config("async_arena_config.xls","rank_award"),
	async_sort_config("async_arena_config.xls","sort"),
	async_rankdes_config("async_arena_config.xls","rank_des"),
	async_clubdes_config("async_arena_config.xls","club_des"),
	async_map_config("async_arena_config.xls","map"),
	async_buy_config("async_arena_config.xls","buy"),
	
	camp_war_leader_config("camp_war.xls","leader_config"),
	camp_war_leader_battle("camp_war.xls","leader_battle"),
	camp_war_leader_battle_effect("camp_war.xls","leader_battle_effect"),
	camp_war_role_battle_config("camp_war.xls","role_battle_config"),
	camp_war_battle_reward_config("camp_war.xls","battle_reward_config"),
	camp_war_consequent_killed_reward("camp_war.xls","consequent_killed_reward"),
	
	//公会
	union_base_config("union_config.xls","base"),
	union_upgrade_config("union_config.xls","upgrade"),
	union_authority_config("union_config.xls","authority"),
	union_activity_config("union_config.xls","activity"),
	union_des_config("union_config.xls","des"),
	union_instance_config("union_config.xls","instance"),
	union_dpsgrouprank_config("union_config.xls","dpsgrouprank"),
	union_dpsresult_config("union_config.xls","dpsresult"),
	union_dropconf_config("union_config.xls","dropconf"),
	union_dropgroup_config("union_config.xls","dropgroup"),
	union_bosshp_config("union_config.xls","bosshp"),
	union_donate_config("union_config.xls","donate"),
	union_boss_config("union_config.xls","boss"),
	union_activity_consume_config("union_config.xls","activityConsume"),
	union_activity_conf_config("union_config.xls","config"),
	;
	
	
	private String xlsName;
	private String sheetName;

	public String getXlsName() {
		return this.xlsName;
	}

	public String getSheetName() {
		return this.sheetName;
	}

	XlsSheetNameType(String xlsName, String sheetName) {
		this.xlsName = xlsName;
		this.sheetName = sheetName;
	}
}
