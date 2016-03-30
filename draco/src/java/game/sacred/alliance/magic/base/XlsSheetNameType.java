package sacred.alliance.magic.base;

import com.game.draco.app.quest.config.QuestXlsType;

public enum XlsSheetNameType {
	
	//角色出生
	role_born("role_born.xls", "born"),
	role_born_hero("role_born.xls", "hero"),
	role_born_guide("role_born.xls", "guide"),
	//角色复活
	role_reborn_point("role_reborn.xls","reborn_point"),
	role_reborn_mode("role_reborn.xls","mode"),
	role_reborn_die_info("role_reborn.xls","die_info"),
	//等级配置
	role_levelup("role_levelup.xls", "levelup"),
	
	//NPC属性
	npc_template("npc_template.xls","template"),
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
	npc_store_anytime("npc_store.xls","anytime_shop"),
	//NPC教学
	npc_teach("npc_teach.xls","teach"),
	//NPC传送
	npc_transfer("npc_transfer.xls","transfer"),
	//刷怪
	npc_refresh_config("npc_refresh.xls","config"),
	npc_refresh_rule("npc_refresh.xls","rule"),
	npc_refresh_loot("npc_refresh.xls","boss_loot"),
	
	//技能
	//skill("skill_list.xls","skill"),
	
	///////////////////////////////////////////////////////////////////////////////////////////	
	//新技能
	skill_reform_base("skill_list_reform.xls","skill_base"),
	skill_reform_attr("skill_list_reform.xls","skill_attr"),
	skill_reform_attr_c("skill_list_reform.xls","skill_attr_c"),
	skill_reform_mp("skill_list_reform.xls","skill_mp"),
	skill_reform_mp_c("skill_list_reform.xls","skill_mp_c"),
	skill_reform_hurt("skill_list_reform.xls","skill_hurt"),
	skill_reform_hurt_c("skill_list_reform.xls","skill_hurt_c"),
	skill_reform_battlescore("skill_list_reform.xls","skill_battlescore"),
	skill_reform_battlescore_c("skill_list_reform.xls","skill_battlescore_c"),
	skill_reform_buff("skill_list_reform.xls","skill_buff"),
	skill_reform_buff_c("skill_list_reform.xls","skill_buff_c"),
	skill_reform_learn_base_money("skill_list_reform.xls","skill_learn_base_money"),
	skill_reform_learn_base_potential("skill_list_reform.xls","skill_learn_base_potential"),
	skill_reform_learn_consume("skill_list_reform.xls","skill_learn_consume"),
	
	//新buff
	buff_reform_base("buff_list_reform.xls","buff_base"),
	buff_reform_hurt("buff_list_reform.xls","buff_hurt"),
	buff_reform_hurt_c("buff_list_reform.xls","buff_hurt_c"),
	buff_reform_shout("buff_list_reform.xls","buff_shout"),
	
	//新区域
	skill_reform_scope("skill_scope_reform.xls","skill_scope_base"),
	///////////////////////////////////////////////////////////////////////////////////////////	
	
	//skill_Learn("skill_learn.xls","learn_list"),
	//skill_role_learn("skill_learn.xls","role_skill"),
	//skill_music("skill_music.xls","music_config"),
	//buff
	//buff("skill_list.xls","buff"),
	//伤害减免系数
	//hurtRemit("skill_list.xls","hurt_remit"),
	
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
	
	//装备衍生
	//强化
	equip_qianghua_list("equip_derive_qianghua.xls","list"),
	equip_streng_level_hole("equip_derive_qianghua.xls","streng_Level_hole"),
	equip_streng_vip("equip_derive_qianghua.xls","level_changed_weights"),
	
	//洗练
	equip_xilian_list("equip_derive_xilian.xls","list"),
	equip_xilian_lock_ratio("equip_derive_xilian.xls","lock_ratio"),
	equip_xilian_attr_weight("equip_derive_xilian.xls","attr_weight"),

	//升星
	equip_shengxing_formula("equip_derive_shengxing.xls","shengxing_formula"),
	equip_shengxing_material_formula("equip_derive_shengxing.xls","material_formula"),
	equip_shengxing_material_ways("equip_derive_shengxing.xls","material_ways"),
	equip_shengxing_ways_list("equip_derive_shengxing.xls","ways_list"),
	equip_shengxing_rate("equip_derive_shengxing.xls","rate"),

	//装备属性相关
	equip_attri_base_attri("equip_attri.xls","base_attri"),
	equip_attri_qianghua_rate("equip_attri.xls","qianghua_rate"),
	equip_attri_jinhua_rate("equip_attri.xls","jinhua_rate"),
	equip_attri_attri_rate("equip_attri.xls","attri_rate"),
	equip_attri_equip_rate("equip_attri.xls","equip_rate"),

	
	goods_buff_detail("goods_buff_detail.xls","buff"),
	goods_consume_buff("goods_food.xls","buff"),//消耗品buff
	goods_exp_buff("goods_exp.xls","buff"),
	
	
	//符文
	rules_desc("rune_config.xls","rules_desc"),
	rune_smelt_attributeValue("rune_config.xls","rune_smelt"),
	rune_smelt_cost("rune_config.xls","rune_cost"),
	rune_smelt_weight("rune_config.xls","rune_weight"),
	rune_compose_rule("rune_config.xls","compose_rule"),
	pet_mosaic_rules("rune_config.xls", "pet_mosaic_rules"),
	equipment_mosaic_rules("rune_config.xls", "equipment_mosaic_rules"),
	equip_baoshi_config("rune_config.xls","mosaic_pay_config"),
	
	//世界掉落列表
	loot_list_world("loot_world.xls","list"),
	//世界掉落组
	loot_group_world("loot_world.xls","group"),
	//NPC掉落列表
	loot_list_npc("loot_npc.xls","list"),
	//NPC掉落组
	loot_group_npc("loot_npc.xls","group"),
	
	//副本逻辑
	copy_base_config("copy_config.xls","base_config"),
	copy_config("copy_config.xls","copy_config"),
	copy_map_config("copy_config.xls","map_config"),
	copy_rule_config("copy_config.xls","rule_config"),
	copy_buy_config("copy_config.xls","buy_config"),
	copy_fall_config("copy_config.xls","copy_fall"),
	copy_attr_config("copy_config.xls","copy_attr"),
	copy_first_config("copy_config.xls", "first_fall"),
	
	// 组队面板
	team_full_config("team_config.xls", "team_full"),
	team_target_config("team_config.xls", "team_target"),
	
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
	
	//3v3
	arena_map_config("arena_list.xls","map_3v3_config"),
	arena_map_rule("arena_list.xls","map_3v3_rule"),
	arena_reward_3v3("arena_list.xls","reward_3v3"),
	arena_buff_config("arena_list.xls","buff_3v3_config"),
	
	//商城
	shop_list("shop_list.xls","shop_list"),
	//限时商城
	shop_limit_list("shop_list.xls","limit_list"),
	shop_limit_param("shop_list.xls","limit_param"),
	
	
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
	rank_layout("rank_list.xls", "rank_layout"),
	
	//活动排行榜
	active_rank("active_rank.xls", "list"),
	
	//怪物攻城活动
	siege_award_add_rate("active_monster_siege.xls","award_add_rate"),
	siege_win_award("active_monster_siege.xls","win_award"),
	siege_fail_award("active_monster_siege.xls","fail_award"),
	siege_mapid_active("active_monster_siege.xls","mapid_active"),
	
	//VIP <new>
	VipLevelUpConfig("vip.xls","vip_levelup"),
	VipPrivilegeConfig("vip.xls","vip_privilege"),
	VipConfig("vip.xls","vipconf"),
	VipLevelFunctionConfig("vip.xls","vip_function"),
	//vip【商城礼包】
	VipGiftConfig("vip.xls","vip_gift"),
	//VIP等级礼包
	vipLevelAwardConfig("vip.xls", "vip_level_award"),
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
	//随机任务-扑克-> 购买轮次
	poker_round_buy_consume("quest_poker.xls","round_buy_consume"),
	poker_accept_quest_consume("quest_poker.xls","accept_quest_consume"),
	
	//动态菜单
	menu_config("menu_config.xls","config"),
	menu_hit("menu_config.xls","hint"),
	menu_open_hint("menu_config.xls", "open_hint"),
	
	// 世界等级
	world_level_base("worldlevel_config.xls", "base_config"),
	world_level_stage("worldlevel_config.xls", "level_stage"),
	
	// 红点提示
	hint_rules("hint_config.xls","hint"),
	
	//淘宝――法阵抽奖
	taobao_compass("taobao_compass.xls","compass"),
	taobao_extra("taobao_compass.xls","taobao_extra"),
	
	//炼金系统配置
	AlchemyConfig("alchemy.xls","alchemy_config"),
	AlchemyOutBreakConfig("alchemy.xls","alchemy_outbreak_config"),
	Alchemy("alchemy.xls","alchemy"),
	AlchemyLeveReward("alchemy.xls","level_reward"),
	//幸运宝箱XLS配置
	LuckyBoxRewardPoolConfig("lucky_box.xls","award_pool"),
	LuckyBoxDiamandsConsumeConfig("lucky_box.xls","diamands_consume"),
	LuckyBoxAppConfig("lucky_box.xls","app_config"),
	LuckyBoxRefreshConfig("lucky_box.xls","refresh_config"),
	LuckyBoxConsumeConfig("lucky_box.xls","level_consume"),
	//社交
	social_intimate("social_config.xls","intimate"),
	social_flower("social_config.xls","flower"),
	social_desc("social_config.xls","desc"),
	social_batch_friend("social_config.xls","batch_friend"),
	social_praise("social_config.xls","praise"),
	social_praise_recv("social_config.xls","give_praise_recv"),
	social_praise_goods("social_config.xls","praise_goods"),
	social_transmission("social_config.xls","transmission"),
	social_transmission_level("social_config.xls","transmission_exp_level"),
	social_transmission_levelm("social_config.xls","transmission_exp_levelm"),
	
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
	
	//怪物随机
	map_npc_refresh_random("refresh_rule.xls","random"),
	
	//怪物刷新再刷组
	map_npc_refresh_group("refresh_rule.xls","group"),
	
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
	drama_exit_map("drama_config.xls","exit_map"),
	drama_fly("drama_config.xls","fly"),
	drama_effect("drama_config.xls","effect_item"),
	drama_name_item("drama_config.xls","name_item"),
	drama_music_item("drama_config.xls","music_item"),
	drama_dramacompose("drama_config.xls","dramacompose"),
	drama_drama("drama_config.xls","drama"),
	drama_trigger_point("drama_trigger.xls","point"),
	drama_trigger_enter_map("drama_trigger.xls","enterMap"),
	drama_trigger_quest("drama_trigger.xls","quest"),
	drama_trigger_npc("drama_trigger.xls","npc"),
	drama_trigger_role_die("drama_trigger.xls","roleDie"),

	
	//秘药
	goods_nostrum_show("goods_nostrum.xls","show_list"),	
	goods_nostrum_limit("goods_nostrum.xls","level_limit"),
	
	//英雄
	hero_base_config("hero_config.xls","base_config"),
	hero_quality_upgrade("hero_config.xls","hero_quality_upgrade"),
	//hero_skill("hero_skill.xls","skill_config"),
	attribute_hero_rate("hero_config.xls","attribute_hero_rate"),
	attribute_quality_rate("hero_config.xls","attribute_quality_rate"),
	attribute_type_rate("hero_config.xls","attribute_type_rate"),
	attribute_herolevel_rate("hero_config.xls","attribute_herolevel_rate"),
	attribute_born_rate("hero_config.xls","attribute_born_rate"),
	
	hero_levelup("hero_config.xls","hero_levelup"),
	//hero_equip_open("hero_config.xls","hero_equip_open"),
	//hero_luck("hero_config.xls","hero_luck"),
	//hero_luck_show("hero_config.xls","hero_luck_show"),
	///hero_luck_goods("hero_config.xls","hero_luck_goods"),
	//hero_luck_firstfee_goods("hero_config.xls","hero_luck_firstfee_goods"),
	hero_identify("hero_config.xls","hero_identify"),
	hero_love("hero_config.xls","hero_love"),
	hero_love_attribute("hero_config.xls","hero_love_attribute"),
	hero_rolelevel_hero_num("hero_config.xls","rolelevel_hero_num"),
	
	//英雄试练
	hero_arena_base("hero_arena_config.xls","base"),
	hero_arena_gate("hero_arena_config.xls","gate"),
	hero_arena_match_rule("hero_arena_config.xls","match_rule"),
	hero_arena_reward("hero_arena_config.xls","reward"),
	hero_arena_reward_group("hero_arena_config.xls","group"),
	hero_arena_reward_must("hero_arena_config.xls","must"),
	
	//宠物
	pet_list("pet_config.xls","pet_list"),
	pet_level_up("pet_config.xls","pet_level_up"),
	pet_star_up("pet_config.xls","pet_star_up"),
	pet_star_hole("pet_config.xls","pet_star_hole"),
	attribute_pet_rate("pet_config.xls","attribute_pet_rate"),
	attribute_pet_type("pet_config.xls","attribute_pet_type"),
	attribute_pet_born("pet_config.xls","attribute_pet_born"),
	attribute_pet_quality("pet_config.xls","attribute_pet_quality"),
	attribute_pet_level("pet_config.xls","attribute_pet_level"),
	pet_pvp_config("pet_config.xls","pet_pvp_config"),
	pet_pvp_refresh("pet_config.xls","pet_pvp_refresh"),
	
	//目标
	target_config("target_config.xls", "target"),
	target_condition("target_config.xls", "condition"),
	
	//战斗力
	attri_battle_score("attribute.xls","battle_score"),
	attri_hurt_series("attribute.xls","hurt_series"),
	attri_restrict_info("attribute.xls","restrict_info"),
	attri_restrict_gear("attribute.xls","restrict_gear"),
    attri_help("attribute.xls","help"),
	
	attri_ceiling_potential("attribute_ceiling.xls","potential"),
	attri_ceiling_exp_hook("attribute_ceiling.xls","exp_hook"),
	attri_ceiling_exp_hook_clean("attribute_ceiling.xls","exp_hook_clean"),
	
	//冲级奖励
	level_gift("level_gift.xls","leveling"),
	
	sign_config("sign_config.xls","config"),
	
	//坐骑
	horse_base_config("horse_config.xls","base"),
	horse_addition_prop_config("horse_config.xls","addition_prop"),
	horse_skill_config("horse_config.xls","skill"),
	horse_star_config("horse_config.xls","star"),
	horse_exchange_config("horse_config.xls","exchange"),
	horse_skill_limit_config("horse_config.xls","skilllimit"),
	horse_luckprob_config("horse_config.xls","luckprob"),
	
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
	async_group_reward_config("async_arena_config.xls","reward_group"),
	async_base_config("async_arena_config.xls","base"),
	
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
	union_gem_donate_config("union_config.xls","gem_donate"),
	union_boss_config("union_config.xls","boss"),
	union_activity_consume_config("union_config.xls","activityConsume"),
	union_activity_conf_config("union_config.xls","config"),
	ins_logic_config("ins_logic_config.xls","instance"),
	ins_logic_group_config("ins_logic_config.xls","group"),
	union_activity_vip_config("union_config.xls","vipreward"),
	union_activity_mail_config("union_config.xls","mail"),
	union_summon_config("union_config.xls","summon"),
	
	//连续登录
	accumulate_login_award("accumulate_login_award.xls","accumulate_login_award"),
	accumulate_login_config("accumulate_login_award.xls","accumulate_login_config"),
	
	//金币抽卡
	choice_gamemoney_main("card_gamemoney.xls","main"),
	choice_gamemoney_tree("card_gamemoney.xls","tree"),
	choice_gamemoney_leaf("card_gamemoney.xls","leaf"),
	choice_gamemoney_consume("card_gamemoney.xls","consume"),
	choice_gamemoney_info("card_gamemoney.xls","info"),
	choice_gamemoney_preview("card_gamemoney.xls","preview"),
	
	//钻石抽卡
	choice_gem_main("card_gem.xls","main"),
	choice_gem_tree("card_gem.xls","tree"),
	choice_gem_leaf("card_gem.xls","leaf"),
	choice_gem_consume("card_gem.xls","consume"),
	choice_gem_info("card_gem.xls","info"),
	choice_gem_preview("card_gem.xls","preview"),
	
	//活动抽卡
	choice_activity_main("card_active.xls","main"),
	choice_activity_tree("card_active.xls","tree"),
	choice_activity_leaf("card_active.xls","leaf"),
	choice_activity_consume("card_active.xls","consume"),
	choice_activity_show("card_active.xls","show"),
	choice_activity_info("card_active.xls","info"),
	
	//神秘商店
//	shop_secret_random_goods_config("shop_secret.xls","random_goods_config"),
//	shop_secret_must_goods_config("shop_secret.xls","must_goods_config"),
	shop_secret_shop_config("shop_secret.xls","shop_config"),
	shop_secret_manual_refresh_rule_config("shop_secret.xls","manual_refresh_rule_config"),
	shop_secret_sys_refresh_rule_config("shop_secret.xls","sys_refresh_rule_config"),
	shop_secret_pool_config("shop_secret.xls","pool_config"),
	shop_secret_goods_config("shop_secret.xls","goods_config"),
	
	//活跃度
	daily_play_rules("daily_play.xls","play_rules"),
	daily_play_rewards("daily_play.xls","play_rewards"),
	
	//前往
	forward_config("forward_config.xls","forward_list"),
	
	// 排位赛
	qualify_base_config("qualify_config.xls","base_config"),
	qualify_rank_gift("qualify_config.xls","rank_gift"),
	qualify_win_gift("qualify_config.xls","win_gift"),
	qualify_lose_gift("qualify_config.xls","lose_gift"),
	qualify_buy_cost("qualify_config.xls","buy_cost"),
	qualify_robot_rules("qualify_config.xls","robot_rules"),
	qualify_robot_hero("qualify_config.xls","robot_hero"),
	qualify_robot_name("qualify_config.xls","robot_name"),
	
	//死亡弹板和升级弹板等
	enhance_option_levelup_npc_config("enhance_options.xls","levelup_enhance_config"),
	enhance_option_levelup_config("enhance_options.xls","levelup_enhance_option"),
	enhance_option_death_config("enhance_options.xls","death_enhance_option"),
	enhance_option_base_config("enhance_options.xls","options"),
	
	//游戏币/潜能/钻石不足弹板
	attribute_enough_config("attribute_no_enough_guide.xls","attribute_pop_config"),
	
	// 哥布林世界BOSS
	goblin_base_config("goblin_config.xls", "goblin_base"),
	goblin_panel_goods("goblin_config.xls", "panel_goods"),
	goblin_refresh_maps("goblin_config.xls", "refresh_maps"),
	goblin_refresh_boss("goblin_config.xls", "refresh_boss"),
	goblin_refresh_real("goblin_config.xls", "refresh_real"),
	goblin_refresh_secret("goblin_config.xls", "refresh_secret"),
	goblin_refresh_location("goblin_config.xls", "refresh_location"),
	goblin_general_reward("goblin_config.xls", "general_reward"),
	goblin_secret_reward("goblin_config.xls", "secret_reward"),
	
	// 首冲
	operate_firstpay_base("operate_firstpay.xls", "firstpay_base"),
	operate_firstpay_goods("operate_firstpay.xls", "firstpay_goods"),
	operate_firstpay_attribute("operate_firstpay.xls", "firstpay_attribute"),
	
	// 月卡
	operate_monthcard_base("operate_monthcard.xls", "monthcard_base"),
	
	//乐翻天活动
	operate_worlddonate_list("operate_worlddonate.xls", "list"),
	operate_worlddonate_rules("operate_worlddonate.xls", "rules"),
	operate_worlddonate_rewards("operate_worlddonate.xls", "worldreward"),
	operate_worlddonate_score("operate_worlddonate.xls", "score"),
	operate_worlddonate_rankkey("operate_worlddonate.xls", "rankkey"),
	operate_worlddonate_rankreward("operate_worlddonate.xls", "rankreward"),
	
	//折扣活动
	operate_discount_list("operate_discount.xls", "list"),
	operate_discount_cond("operate_discount.xls", "cond"),
	operate_discount_reward("operate_discount.xls", "reward"),
	
	// 成长基金
	operate_growfund_base("operate_growfund.xls", "growfund_base"),
	operate_growfund_reward("operate_growfund.xls", "growfund_reward"),
	
	// 首次充值赠送
	operate_payextra_base("operate_payextra.xls", "payextra_base"),
	operate_payextra_reward("operate_payextra.xls", "payextra_reward"),
	
	//  简单运营活动
	operate_simple_base("operate_simple.xls", "simple_active"),
	
	//【运营·商城】
	vip_gift_info("vip.xls","vip_gift_info"),
	
	//一键恢复
	recovery_config("recovery.xls","recovery_config"),
	recovery_consume_config("recovery.xls","recovery_consume"),
	recovery_hungup_consume_config("recovery.xls","recovery_hungup_consume"),
	recovery_output_config("recovery.xls","recovery_output"),
	recovery_exp_hang_up_config("recovery.xls","exp_hang_up"),
	
	//连击
	hit_combo("hit_combo.xls","config"),
	hit_combo_buff("hit_combo.xls","buff"),
	
	//物品分解
	decompose_config("decompose.xls","decompose_gooods"),
	//打开神秘商店或兑换界面
	trade_function("trade_function.xls","function"),
	//天赋
	talent_base_config("talent_config.xls","talent_base"),
	talent_levelup_config("talent_config.xls","level_up"),
	talent_rank_config("talent_config.xls","rank"),
	talent_consume_config("talent_config.xls","consume"),
	talent_attr_config("talent_config.xls","attr"),
	talent_goods_config("talent_config.xls","goods"),
	talent_des_config("talent_config.xls","des"),
	talent_condition_config("talent_config.xls","condition"),
	talent_group_config("talent_config.xls","group"),
	talent_info_config("talent_config.xls","info"),
	talent_shop_config("talent_config.xls","shop"),
	
	//公会战
	union_battle_app_config("union_battle.xls","config"),
	union_battle_config("union_battle.xls","battle"),
	union_battle_kill_msg_config("union_battle.xls","kill_msg"),
	union_battle_killed_msg_config("union_battle.xls","killed_msg"),
	union_battle_buff_config("union_battle.xls","buff"),
	
	survival_base_config("survival_battle_config.xls","base"),
	survival_mail_config("survival_battle_config.xls","mail"),
	survival_reward_config("survival_battle_config.xls","reward"),
	
	//特殊数据
	special_logic_config("special_logic.xls","config"),//技能特殊逻辑
	special_worldlevel_config("special_logic.xls","worldlevel_logic"),//世界等级特殊逻辑
	special_worldlevel_group_config("special_logic.xls","worldlevel_group_logic"),//世界等级组特殊逻辑
	
	//公会积分战
	union_integral_config("union_integral_battle.xls","config"),
	union_integral_npc_config("union_integral_battle.xls","battle_npc"),
	union_integral_mail_config("union_integral_battle.xls","mail"),
	union_integral_reborn_config("union_integral_battle.xls","reborn"),
	union_integral_rewgroup_config("union_integral_battle.xls","rew_group"),
	union_integral_reward_config("union_integral_battle.xls","reward"),
	union_integral_summon_config("union_integral_battle.xls","summon"),
	
	//爬塔
	tower_app_config("tower_config.xls", "appConfig"),
	tower_condition_config("tower_config.xls", "condition"),
	tower_gate_config("tower_config.xls", "gate"),
	tower_star_award_config("tower_config.xls", "starAward"),
	tower_layer_config("tower_config.xls", "layer"),
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
