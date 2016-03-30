package sacred.alliance.magic.constant;

import com.game.draco.GameContext;


public enum Status {
	FAILURE(0,(byte)0,false,TextId.FAILURE),//默认失败
	SUCCESS(1,(byte)1,true,TextId.SUCCESS),//默认成功
	
	//系统提示
	Sys_Error(0,(byte)1,false,TextId.Sys_Error),
	Sys_Param_Error(0,(byte)2,false,TextId.Sys_Param_Error),
	Sys_Operate_Frequently(0,(byte)6,false,TextId.Sys_Operate_Frequently),
	Sys_Input_Act_Code(0,(byte)7,false,TextId.Sys_Input_Act_Code),
	Sys_Sorry_Active_End(0,(byte)8,false,TextId.Sys_Sorry_Active_End),
	Sys_Act_Code_Center(0,(byte)9,false,TextId.Sys_Act_Code_Center),
	Sys_Act_Code_Success_Mail_Reward(0,(byte)10,false,TextId.Sys_Act_Code_Success_Mail_Reward),
	Sys_Charge_Not_Open(0,(byte)16,false,TextId.Sys_Charge_Not_Open),
	Sys_KeFu_Help_Re_Submit(0,(byte)18,false,TextId.Sys_KeFu_Help_Re_Submit),
	Sys_KeFu_Submit_Success(0,(byte)19,false,TextId.Sys_KeFu_Submit_Success),
	Sys_Pay_Not_Open(0,(byte)20,false,TextId.Sys_Pay_Not_Open),
	Sys_Pay_No_Help(0,(byte)21,false,TextId.Sys_Pay_No_Help),
	SYS_NO_LOGIN(0,(byte)22,false,TextId.SYS_NO_LOGIN),
	SYS_Game_Money_Not_Enough(0,(byte)23,false,TextId.SYS_Game_Money_Not_Enough),
	SYS_Login_Speed_Tip(0,(byte)24,false,TextId.SYS_Login_Speed_Tip),
	SYS_SystemSet_Fail(0,(byte)25,false,TextId.SYS_SystemSet_Fail),
	SYS_Money_Input_Error(0,(byte)26,false,TextId.SYS_Money_Input_Error),
	SYS_Charge_Args_Null(0,(byte)27,false,TextId.SYS_Charge_Args_Null),

	//技能学习、升级
	Skill_FAILURE(2,(byte)0,false,TextId.Skill_FAILURE),
	Skill_Not_Exist(2,(byte)1,false,TextId.Skill_Not_Exist),
	Skill_CanLearn_Fail(2,(byte)2,false,TextId.Skill_CanLearn_Fail),
	Skill_Own_Learn(2,(byte)3,false,TextId.Skill_Own_Learn),
	Skill_NotOwn_Upgrade(2,(byte)4,false,TextId.Skill_NotOwn_Upgrade),
	Skill_MaxLevel_Fail(2,(byte)5,false,TextId.Skill_MaxLevel_Fail),
	Skill_Career_Fail(2,(byte)6,false,TextId.Skill_Career_Fail),
	Skill_RoleLevel_Fail(2,(byte)7,false,TextId.Skill_RoleLevel_Fail),
	Skill_Money_Fail(2,(byte)9,false,TextId.Skill_Money_Fail),
	Skill_RelySkill_NotOwn(2,(byte)10,false,TextId.Skill_RelySkill_NotOwn),
	Skill_RelySkill_Level(2,(byte)11,false,TextId.Skill_RelySkill_Level),

	//角色相关
	Role_FAILURE(3,(byte)0,false,TextId.Role_FAILURE),
	Role_SUCCESS(3,(byte)1,true,TextId.Role_SUCCESS),
	Role_Career_Error(3,(byte)2,false,TextId.Role_Career_Error),
	Role_Camp_Error(3,(byte)3,false,TextId.Role_Camp_Error),
	Role_Sex_Error(3,(byte)4,false,TextId.Role_Sex_Error),
	Role_Name_Null(3,(byte)5,false,TextId.Role_Name_Null),
	Role_Exist(3,(byte)6,false,TextId.Role_Exist),
	Role_Not_Exist(3,(byte)7,false,TextId.Role_Not_Exist),
	Role_Illegal(3,(byte)8,false,TextId.Role_Illegal),
	Role_Operate_Illegal(3,(byte)9,false,TextId.Role_Operate_Illegal),
	Role_Login_Token_Error(3,(byte)10,false,TextId.Role_Login_Token_Error),
	Role_Login_Proto_Low(3,(byte)11,false,TextId.Role_Login_Proto_Low),
	Role_Login_OffLine(3,(byte)12,false,TextId.Role_Login_OffLine),
	Role_Login_Not_InnerId(3,(byte)13,false,TextId.Role_Login_Not_InnerId),
	Role_Head_Not_Change(3,(byte)14,false,TextId.Role_Head_Not_Change),
	Role_No_Online(3,(byte)15,false,TextId.Role_No_Online),
	Role_Frozen_End(3,(byte)16,false,TextId.Role_Frozen_End),
	Role_Login_Token_Expired(3,(byte)19,false,TextId.Role_Login_Token_Expired),
	Role_Create_Name_In_Char(3,(byte)20,false,TextId.Role_Create_Name_In_Char),
	Role_Create_Name_Char_Info(3,(byte)21,false,TextId.Role_Create_Name_Char_Info),
	Role_Create_Name_Illegal_Char(3,(byte)22,false,TextId.Role_Create_Name_Illegal_Char),
	Role_Create_Name_Forbid_Char(3,(byte)23,false,TextId.Role_Create_Name_Forbid_Char),
	Role_Create_Name_Has_Illegal_Char(3,(byte)24,false,TextId.Role_Create_Name_Has_Illegal_Char),
	Role_Is_Offline(3,(byte)27,false,TextId.Role_Is_Offline),
	Role_Upgrace_Fail(3,(byte)28,false,TextId.Role_Upgrace_Fail),
	Role_Modify_Success(3,(byte)29,false,TextId.Role_Modify_Success),
	Role_Modify_Sys_Error(3,(byte)30,false,TextId.Role_Modify_Sys_Error),
	Role_Targ_Is_Self(3,(byte)32,false,TextId.Role_Targ_Is_Self),
	Role_Targ_Offline(3,(byte)33,false,TextId.Role_Targ_Offline),
	Role_Created_Num_Full(3,(byte)34,false,TextId.Role_Created_Num_Full),
	Role_Login_Full_Size(3,(byte)35,false,TextId.Role_Login_Full_Size),

	//门派
	Faction_FAILURE(5,(byte)0,false,TextId.Faction_FAILURE),
	Faction_Role_Own(5,(byte)1,false,TextId.Faction_Role_Own),
	Faction_RoleLevel_NotEnough(5,(byte)2,false,TextId.Faction_RoleLevel_NotEnough),
	Faction_Name_Null(5,(byte)3,false,TextId.Faction_Name_Null),
	Faction_Name_Illegal(5,(byte)4,false,TextId.Faction_Name_Illegal),
	Faction_Name_Forbidden(5,(byte)5,false,TextId.Faction_Name_Forbidden),
	Faction_Name_NotInRange(5,(byte)6,false,TextId.Faction_Name_NotInRange),
	Faction_Name_TooLonger(5,(byte)7,false,TextId.Faction_Name_TooLonger),
	Faction_Name_Exist(5,(byte)8,false,TextId.Faction_Name_Exist),
	Faction_Desc_Illegal(5,(byte)9,false,TextId.Faction_Desc_Illegal),
	Faction_Desc_TooLonger(5,(byte)10,false,TextId.Faction_Desc_TooLonger),
	Faction_Desc_Default(5,(byte)11,false,TextId.Faction_Desc_Default),
	Faction_Exist(5,(byte)12,false,TextId.Faction_Exist),
	Faction_Role_Exist(5,(byte)13,false,TextId.Faction_Role_Exist),
	Faction_Not_Exist(5,(byte)14,false,TextId.Faction_Not_Exist),
	Faction_Member_Full(5,(byte)15,false,TextId.Faction_Member_Full),
	Faction_ApplyRole_Null(5,(byte)16,false,TextId.Faction_ApplyRole_Null),
	Faction_ApplyRole_OwnFaction(5,(byte)17,false,TextId.Faction_ApplyRole_OwnFaction),
	Faction_Role_No_Position(5,(byte)18,false,TextId.Faction_Role_No_Position),
	Faction_Exit_HasMember(5,(byte)19,false,TextId.Faction_Exit_HasMember),
	Faction_Remove_Self(5,(byte)20,false,TextId.Faction_Remove_Self),
	Faction_Member_Not_Exist(5,(byte)21,false,TextId.Faction_Member_Not_Exist),
	Faction_Not_Own(5,(byte)22,false,TextId.Faction_Not_Own),
	Faction_Target_Own(5,(byte)23,false,TextId.Faction_Target_Own),
	Faction_Build_MaxLevel(5,(byte)24,false,TextId.Faction_Build_MaxLevel),
	Faction_Build_Not_Exist(5,(byte)25,false,TextId.Faction_Build_Not_Exist),
	Faction_Level_Not_Enough(5,(byte)26,false,TextId.Faction_Level_Not_Enough),
	Faction_Money_Not_Enough(5,(byte)27,false,TextId.Faction_Money_Not_Enough),
	Faction_RoleNum_Not_Enough(5,(byte)28,false,TextId.Faction_RoleNum_Not_Enough),
	Faction_RelyBuild_Not_Exist(5,(byte)29,false,TextId.Faction_RelyBuild_Not_Exist),
	Faction_RelyBuild_Level_Not_Enough(5,(byte)30,false,TextId.Faction_RelyBuild_Level_Not_Enough),
	Faction_Have_Mutex_Build(5,(byte)31,false,TextId.Faction_Have_Mutex_Build),
	Faction_Role_Not_Member(5,(byte)32,false,TextId.Faction_Role_Not_Member),
	Faction_Create_Goods_NotEnough(5,(byte)33,false,TextId.Faction_Create_Goods_NotEnough),
	Faction_Notice_TooLonger(5,(byte)34,false,TextId.Faction_Notice_TooLonger),
	Faction_Role_Signature_TooLonger(5,(byte)35,false,TextId.Faction_Role_Signature_TooLonger),
	Faction_Target_Level_NotEnough(5,(byte)36,false,TextId.Faction_Target_Level_NotEnough),
	Faction_Donate_Null(5,(byte)37,false,TextId.Faction_Donate_Null),
	Faction_Donate_Money_Not_Enough(5,(byte)39,false,TextId.Faction_Donate_Money_Not_Enough),
	Faction_Donate_GoldMoney_Not_Enough(5,(byte)40,false,TextId.Faction_Donate_GoldMoney_Not_Enough),
	Faction_Upgrade_No_Position(5,(byte)41,false,TextId.Faction_Upgrade_No_Position),
	Faction_Upgrade_No_Money(5,(byte)42,false,TextId.Faction_Upgrade_No_Money),
	Faction_Upgrade_Max(5,(byte)43,false,TextId.Faction_Upgrade_Max),
	Faction_Skill_Null(5,(byte)44,false,TextId.Faction_Skill_Null),
	Faction_Skill_Level_Max(5,(byte)45,false,TextId.Faction_Skill_Level_Max),
	Faction_Skill_Build_Null(5,(byte)46,false,TextId.Faction_Skill_Build_Null),
	Faction_Skill_Build_Level_NotEnough(5,(byte)47,false,TextId.Faction_Skill_Build_Level_NotEnough),
	Faction_Skill_Max_Level(5,(byte)48,false,TextId.Faction_Skill_Max_Level),
	Faction_Skill_Role_Level_Not_Enough(5,(byte)49,false,TextId.Faction_Skill_Role_Level_Not_Enough),
	Faction_Contribution_Not_Enough(5,(byte)50,false,TextId.Faction_Contribution_Not_Enough),
	Faction_Build_Upgrade_No_Position(5,(byte)51,false,TextId.Faction_Build_Upgrade_No_Position),
	Faction_Skill_Upgrade_No_Position(5,(byte)52,false,TextId.Faction_Skill_Upgrade_No_Position),
	//门派神兽
	Faction_Soul_Null(5,(byte)53,false,TextId.Faction_Soul_Null),
	Faction_Soul_Feed_Full(5,(byte)54,false,TextId.Faction_Soul_Feed_Full),
	Faction_Soul_No_Level(5,(byte)55,false,TextId.Faction_Soul_No_Level),
	Faction_Soul_No_Feed(5,(byte)56,false,TextId.Faction_Soul_No_Feed),
	Faction_Soul_Param_Err(5,(byte)57,false,TextId.Faction_Soul_Param_Err),
	Faction_Soul_Money_Err(5,(byte)58,false,TextId.Faction_Soul_Money_Err),
	Faction_Soul_No_Change_State(5,(byte)59,false,TextId.Faction_Soul_No_Change_State),
	Faction_Task_Goods(5,(byte)60,false,TextId.Faction_Task_Goods),
	Faction_Is_War(5,(byte)61,false,TextId.Faction_Is_War),
	Faction_war_begin_no_channelsoul(5,(byte)62,false,TextId.Faction_war_begin_no_channelsoul),
	Faction_Soul_Fly_Max(5,(byte)63,false,TextId.Faction_Soul_Fly_Max),
	Faction_Soul_Not_Enough(5,(byte)63,false,TextId.Faction_Soul_Not_Enough),
	Faction_Soul_Fly_No_Position(5,(byte)64,false,TextId.Faction_Soul_Fly_No_Position),
	Faction_Soul_Feed_Failure(5,(byte)65,false,TextId.Faction_Soul_Feed_Failure),
	Faction_Soul_Fly_Failure(5,(byte)66,false,TextId.Faction_Soul_Fly_Failure),
	Faction_Camp_Err(5,(byte)67,false,TextId.Faction_Camp_Err),
	Faction_Salary_One_Day(5,(byte)68,false,TextId.Faction_Salary_One_Day),
	Faction_Impeach_Err(5,(byte)69,false,TextId.Faction_Impeach_Err),
	Faction_Impeach_No_Position(5,(byte)70,false,TextId.Faction_Impeach_No_Position),
	Faction_Active_Err(6,(byte)71,false,TextId.Faction_Active_Err),
	Faction_Kick_Position_Low(5,(byte)72,false,TextId.Faction_Kick_Position_Low),
	Faction_Kick_Position_Same(5,(byte)73,false,TextId.Faction_Kick_Position_Same),
	Faction_Kick_Error(5,(byte)74,false,TextId.Faction_Kick_Error),
	Faction_Demise_Error(5,(byte)75,false,TextId.Faction_Demise_Error),
	Faction_Demise_Not_Leader(5,(byte)76,false,TextId.Faction_Demise_Not_Leader),

	//背包
	Bag_Is_Full(7,(byte)1,false,TextId.Bag_Is_Full),

	//组队
	Team_Fail(8,(byte)0,false,TextId.Team_Fail),
	Team_Role_Not_Online(8,(byte)1,false,TextId.Team_Role_Not_Online),
	Team_Role_Self(8,(byte)2,false,TextId.Team_Role_Self),
	Team_Role_OwnTeam(8,(byte)3,false,TextId.Team_Role_OwnTeam),
	Team_Targ_OwnTeam(8,(byte)4,false,TextId.Team_Targ_OwnTeam),
	Team_In_SameTeam(8,(byte)5,false,TextId.Team_In_SameTeam),
	Team_Map_Not_Support(8,(byte)6,false,TextId.Team_Map_Not_Support),
	Team_TargetRole_Busy(8,(byte)7,false,TextId.Team_TargetRole_Busy),
	Team_Target_Role_Offline(8,(byte)8,false,TextId.Team_Target_Role_Offline),
	Team_Full(8,(byte)9,false,TextId.Team_Full),
	Team_Auto_Refuse_Targ_tip(8,(byte)10,false,TextId.Team_Auto_Refuse_Targ_tip),
	Team_Auto_Refuse_Apply_Tip(8,(byte)11,false,TextId.Team_Auto_Refuse_Apply_Tip),
	Team_In_differentTeam(8,(byte)12,false,TextId.Team_In_differentTeam),
	Team_Shield_By_Target(8,(byte)13,false,TextId.Team_Shield_By_Target),
	Team_Offline_Role_Is_Leader(8,(byte)14,false,TextId.Team_Offline_Role_Is_Leader),
	Team_Oprate_Not_Leader(8,(byte)15,false,TextId.Team_Oprate_Not_Leader),
	Team_Refuse(8,(byte)16,false,TextId.Team_Refuse),

	//邮件
	Mail_FAILURE(10,(byte)0,false,TextId.Mail_FAILURE),
	Mail_Is_Empty(10,(byte)1,false,TextId.Mail_Is_Empty),
	Mail_Page_Out(10,(byte)2,false,TextId.Mail_Page_Out),
	Mail_Not_Exist(10,(byte)3,false,TextId.Mail_Not_Exist),
	Mail_Goods_Success(10,(byte)4,true,TextId.Mail_Goods_Success),
	Mail_Del_Success(10,(byte)5,true,TextId.Mail_Del_Success),
	Mail_Not_Mail_Id(10,(byte)6,false,TextId.Mail_Not_Mail_Id),
	Mail_Goods_Failure(10,(byte)7,false,TextId.Mail_Goods_Failure),
	Mail_Del_FAILURE(10,(byte)8,false,TextId.Mail_Del_FAILURE),
	Mail_Goods_Taken(10,(byte)9,false,TextId.Mail_Goods_Taken),
	Mail_Send_Success(10,(byte)10,true,TextId.Mail_Send_Success),
	Mail_Send_Failure(10,(byte)11,false,TextId.Mail_Send_Failure),
	Mail_Goods_Null(10,(byte)12,false,TextId.Mail_Goods_Null),
	Mail_Goods_Not_Exist(10,(byte)13,false,TextId.Mail_Goods_Not_Exist),
	Mail_Pack_Full(10,(byte)14,false,TextId.Mail_Pack_Full),
	Mail_Add_Goods_Err(10,(byte)15,false,TextId.Mail_Add_Goods_Err),
	Mail_Consume_Gold(10,(byte)16,false,TextId.Mail_Consume_Gold),
	Mail_Less_Gold(10,(byte)17,false,TextId.Mail_Less_Gold),
	Mail_Params_Err(10,(byte)18,false,TextId.Mail_Params_Err),
	Mail_Pick_Pay_Money(10,(byte)19,false,TextId.Mail_Pick_Pay_Money),
	Mail_Delete_Not_Empty_Or_Freeze(10,(byte)20,false,TextId.Mail_Delete_Not_Empty_Or_Freeze),

	//任务
	Quest_Not_Exist(11,(byte)1,false,TextId.Quest_Not_Exist),
	Quest_Not_Own(11,(byte)2,false,TextId.Quest_Not_Own),
	Quest_Phase_Invalid(11,(byte)3,false,TextId.Quest_Phase_Invalid),
	Quest_Backpack_Full(11,(byte)4,false,TextId.Quest_Backpack_Full),
	Quest_illegality_Id(11,(byte)5,false,TextId.Quest_illegality_Id),
	Quest_Own_Goods(11,(byte)6,false,TextId.Quest_Own_Goods),
	Quest_Param_Error_Oprate_Fail(11,(byte)7,false,TextId.Quest_Param_Error_Oprate_Fail),
	Quest_Search_Status_Fail(12,(byte)8,false,TextId.Quest_Search_Status_Fail),
	Quest_Search_Active_Not_Open(12,(byte)9,false,TextId.Quest_Search_Active_Not_Open),

	//聊天系统
	Chat_FAILURE(12,(byte)0,false,TextId.Chat_FAILURE),
	Chat_Message_Null(12,(byte)1,false,TextId.Chat_Message_Null),
	Chat_MaxLength_Fail(12,(byte)2,false,TextId.Chat_MaxLength_Fail),
	Chat_Team_Null(12,(byte)3,false,TextId.Chat_Team_Null),
	Chat_Faction_Null(12,(byte)4,false,TextId.Chat_Faction_Null),
	Chat_TimeOften_Fail(12,(byte)5,false,TextId.Chat_TimeOften_Fail),
	Chat_Map_Fail(12,(byte)6,false,TextId.Chat_Map_Fail),
	Chat_Role_Send_SysMsg(12,(byte)7,false,TextId.Chat_Role_Send_SysMsg),
	Chat_Speak_Time_Limit(12,(byte)8,false,TextId.Chat_Speak_Time_Limit),
	Chat_Role_Level_Limit(12,(byte)9,false,TextId.Chat_Role_Level_Limit),
	Chat_Forbid_All(12,(byte)10,false,TextId.Chat_Forbid_All),
	Chat_Forbid_Part(12,(byte)11,false,TextId.Chat_Forbid_Part),
	Chat_Private_Self(12,(byte)12,false,TextId.Chat_Private_Self),
	Chat_Sheild_By_Target(12,(byte)14,false,TextId.Chat_Sheild_By_Target),
	Chat_Role_Offilne(12,(byte)15,false,TextId.Chat_Role_Offilne),
	Chat_Gm_Channel(12,(byte)16,false,TextId.Chat_Gm_Channel),
	Chat_Goods_Limit(12,(byte)16,false,TextId.Chat_Goods_Limit),


	//npc买卖
	NpcStore_Not_Npc(13,(byte)1,false,TextId.NpcStore_Not_Npc),
	NpcStore_Type_Error(13,(byte)2,false,TextId.NpcStore_Type_Error),
	NpcStore_Not_Goods(13,(byte)3,false,TextId.NpcStore_Not_Goods),
	NpcStore_Goods_Null(13,(byte)4,false,TextId.NpcStore_Goods_Null),
	NpcStore_Goods_Not_Sell(13,(byte)5,false,TextId.NpcStore_Goods_Not_Sell),
	NpcStore_Price_Error(13,(byte)6,false,TextId.NpcStore_Price_Error),
	NpcStore_Task_Goods(13,(byte)7,false,TextId.NpcStore_Task_Goods),
	NpcStore_Is_Negative(13,(byte)8,false,TextId.NpcStore_Is_Negative),
	NpcStore_Sell_Num_Error(13,(byte)9,false,TextId.NpcStore_Sell_Num_Error),
	NpcStore_No_Have_Goods_Sell(13,(byte)10,false,TextId.NpcStore_No_Have_Goods_Sell),


	//物品
	Goods_System_Busy(15,(byte)32,false,TextId.Goods_System_Busy),
	GOODS_BACKPACK_FULL(15,(byte)2,false,TextId.GOODS_BACKPACK_FULL),
	GOODS_NO_FOUND(15,(byte)3,false,TextId.GOODS_NO_FOUND),
	GOODS_NO_ENOUGH(15,(byte)4,false,TextId.GOODS_NO_ENOUGH),
	GOODS_NO_SPLIT(15,(byte)5,false,TextId.GOODS_NO_SPLIT),
	GOODS_NO_USE(15,(byte)6,false,TextId.GOODS_NO_USE),
	GOODS_NO_DISCARD(15,(byte)7,false,TextId.GOODS_NO_DISCARD),
	GOODS_NO_SUPPORT(15,(byte)8,false,TextId.GOODS_NO_SUPPORT),
	GOODS_BAG_FULL(15,(byte)9,false,TextId.GOODS_BAG_FULL),
	GOODS_BACKPACK_FULL_TIPS(15,(byte)10,false,TextId.GOODS_BACKPACK_FULL_TIPS),
	GOODS_IS_EXPIRED(15,(byte)22,false,TextId.GOODS_IS_EXPIRED),
	GOODS_OFFLINEDIE_NOPUT_WAREHOUSE(15,(byte)25,false,TextId.GOODS_OFFLINEDIE_NOPUT_WAREHOUSE),
	GOODS_WAREHOUSE_FULL(15,(byte)26,false,TextId.GOODS_WAREHOUSE_FULL),
	GOODS_DERIVE_MOSAIC_MONEY_LESS(15,(byte)27,false,TextId.GOODS_DERIVE_MOSAIC_MONEY_LESS),
	GOODS_DERIVE_REMOVE_GEM_MONEY_LESS(15,(byte)28,false,TextId.GOODS_DERIVE_REMOVE_GEM_MONEY_LESS),
	Goods_Not_Can_Used_In_Map(15,(byte)29,false,TextId.Goods_Not_Can_Used_In_Map),
	Goods_Mosaic_Success(15,(byte)30,false,TextId.Goods_Mosaic_Success),
	Goods_Recasting_Success(15,(byte)31,false,TextId.Goods_Recasting_Success),
	Goods_Gem_Remove_Success(15,(byte)33,false,TextId.Goods_Gem_Remove_Success),
	Goods_Targ_Role_Not_Online(15,(byte)34,false,TextId.Goods_Targ_Role_Not_Online),
	Goods_Split_Backpack_Full(15,(byte)35,false,TextId.Goods_Split_Backpack_Full),
	Goods_Identify_Success(15,(byte)36,false,TextId.Goods_Identify_Success),
	
	Map_Change_Line_Fail(16,(byte)27,false,TextId.Map_Change_Line_Fail),
	Map_Reborn_Oprate(16,(byte)28,false,TextId.Map_Reborn_Oprate),
	Map_Line_Role_Full(16,(byte)31,false,TextId.Map_Line_Role_Full),
	MAP_NOT_SUPPORT_EXIT(24,(byte)25,false,TextId.MAP_NOT_SUPPORT_EXIT),

	//活动
	Active_FAILURE(17,(byte)0,false,TextId.Active_FAILURE),
	Active_Param_Error(17,(byte)1,false,TextId.Active_Param_Error),
	Active_GoldMoney_Not_Enough(17,(byte)2,false,TextId.Active_GoldMoney_Not_Enough),
	Active_BindMoney_Not_Enough(17,(byte)3,false,TextId.Active_BindMoney_Not_Enough),
	Active_Not_Open(17,(byte)4,false,TextId.Active_Not_Open),


	//活动-怪物攻城
	Active_Err_Time(17,(byte)16,false,TextId.Active_Err_Time),
	Active_Mail_Title(17,(byte)17,false,TextId.Active_Mail_Title),


	//物品兑换
	Exchange_Not_InDate(18, (byte)0, false, TextId.Exchange_Not_InDate),
	Exchange_Frequency_Not_Enough(18, (byte)1, false, TextId.Exchange_Frequency_Not_Enough),
	Exchange_Condition_Not_Meet(18, (byte)2, false, TextId.Exchange_Condition_Not_Meet),
	Exchange_ConsumeGood_Not_Enough(18, (byte)3, false, TextId.Exchange_ConsumeGood_Not_Enough),
	Exchange_Backpack_Not_Enough(18, (byte)4, false, TextId.Exchange_Backpack_Not_Enough),
	Exchange_Can_Not_EXchange(18, (byte)5, false, TextId.Exchange_Can_Not_EXchange),
	Exchange_Can_Exchange(18, (byte)6, true, TextId.Exchange_Can_Exchange),
	Exchange_Success(18, (byte)7, true, TextId.Exchange_Success),
	Exchange_Attribute_Not_Enough(18, (byte)8, false, TextId.Exchange_Attribute_Not_Enough),
	Exchange_Param_ERR(18, (byte)15, false, TextId.Exchange_Param_ERR),

	//称号
	Title_Goods_Null(19, (byte)0, false, TextId.Title_Goods_Null),
	Title_Is_Activated(19, (byte)1, false, TextId.Title_Is_Activated),
	Title_Is_Timeout(19, (byte)2, false, TextId.Title_Is_Timeout),
	Title_No_Activated(19, (byte)3, false, TextId.Title_No_Activated),
	Title_Not_Activated(19, (byte)4, false, TextId.Title_Not_Activated),
	Title_No_Pay(19, (byte)5, false, TextId.Title_No_Pay),
	Title_Gender_Err(19, (byte)6, false, TextId.Title_Gender_Err),
	Title_Career_Err(19, (byte)7, false, TextId.Title_Career_Err),
	Title_Level_Err(19, (byte)8, false, TextId.Title_Level_Err),
	Title_Exist(19, (byte)9, false, TextId.Title_Exist),
	Title_Permanent_No_Pay(19, (byte)10, false, TextId.Title_Permanent_No_Pay),
	Title_Menoy_Not_Enough(19, (byte)11, false, TextId.Title_Menoy_Not_Enough),
	Title_Current_IsFull(19, (byte)12, false, TextId.Title_Current_IsFull),
	Title_Map_Not_Can_Used(19, (byte)13, false, TextId.Title_Map_Not_Can_Used),

	//排行榜
	Rank_List_Null(20,(byte)1,false,TextId.Rank_List_Null),
	Rank_REWARD_REWARDED(20,(byte)2,false,TextId.Rank_REWARD_REWARDED),
	Rank_REWARD_NO(20, (byte)3,false,TextId.Rank_REWARD_NO),
	Rank_REWARD_DISABLE(20,(byte)4,false,TextId.Rank_REWARD_DISABLE),
	Rank_REWARD_NoExist(20,(byte)5,false,TextId.Rank_REWARD_NoExist),
	Rank_Role_Null(20,(byte)6,false,TextId.Rank_Role_Null),

	//擂台赛相关
	ArenaLearn_Role_HasArena(22,(byte)1,false,TextId.ArenaLearn_Role_HasArena),
	ArenaLearn_Beyond_Distance(22,(byte)2,false,TextId.ArenaLearn_Beyond_Distance),
	ArenaLearn_TheMap_NoAllow(22,(byte)3,false,TextId.ArenaLearn_TheMap_NoAllow),
	ArenaLearn_Prompt(22,(byte)4,false,TextId.ArenaLearn_Prompt),
	ArenaLearn_Refuse(22,(byte)5,false,TextId.ArenaLearn_Refuse),
	ArenaLearn_Role_Busy(22,(byte)6,false,TextId.ArenaLearn_Role_Busy),
	ArenaLearn_Self_HasArena(22,(byte)7,false,TextId.ArenaLearn_Self_HasArena),
	ArenaLearn_ShieldByTarget(22,(byte)8,false,TextId.ArenaLearn_ShieldByTarget),
	ArenaLearn_Role_COPY_TEAM(22,(byte)9,false,TextId.ArenaLearn_Role_COPY_TEAM),
	ArenaLearn_Self_COPY_TEAM(22,(byte)10,false,TextId.ArenaLearn_Self_COPY_TEAM),
	ArenaLearn_Targ_Not_Online(22,(byte)11,false,TextId.ArenaLearn_Targ_Not_Online),

	//社交
	Social_Error(26,(byte)0,false,TextId.Social_Error),
	Social_TargRole_Offline(26,(byte)1,false,TextId.Social_TargRole_Offline),
	Social_Black_IsFull(26,(byte)2,false,TextId.Social_Black_IsFull),
	Social_Friend_IsFull(26,(byte)3,false,TextId.Social_Friend_IsFull),
	Social_Friend_Add_Self(26,(byte)4,false,TextId.Social_Friend_Add_Self),
	Social_TargRole_Busy(26,(byte)5,false,TextId.Social_TargRole_Busy),
	Social_Black_Remove_Tip(26,(byte)6,false,TextId.Social_Black_Remove_Tip),
	Social_Already_Friend(26,(byte)7,false,TextId.Social_Already_Friend),
	Social_Already_In_Black(26,(byte)8,false,TextId.Social_Already_In_Black),
	Social_Break_Friend_Title(26,(byte)9,false,TextId.Social_Break_Friend_Title),
	Social_Break_Friend_content(26,(byte)10,false,TextId.Social_Break_Friend_content),
	Social_Friend_Add_Tip(26,(byte)11,false,TextId.Social_Friend_Add_Tip),
	Social_Friend_Refuse_Tip(26,(byte)12,false,TextId.Social_Friend_Refuse_Tip),
	Social_Targ_Friend_IsFull(26,(byte)13,false,TextId.Social_Targ_Friend_IsFull),
	Social_Flower_Not_Friend(26,(byte)14,false,TextId.Social_Flower_Not_Friend),
	Social_Gold_Not_Enough(26,(byte)15,false,TextId.Social_Gold_Not_Enough),
	Social_Batch_Friend_Num_Full(26,(byte)16,false,TextId.Social_Batch_Friend_Num_Full),
	Social_No_Have_Role(26,(byte)17,false,TextId.Social_No_Have_Role),
	Social_Black_Not_Self(26,(byte)18,false,TextId.Social_Black_Not_Self),
	Social_Friend_Apply_Wait(26,(byte)19,false,TextId.Social_Friend_Apply_Wait),
	Social_Friend_Intimate_Change_Tip(26,(byte)20,false,TextId.Social_Friend_Intimate_Change_Tip),
	Social_Batch_Friend_Remain_Num(26,(byte)21,false,TextId.Social_Batch_Friend_Remain_Num),
	Social_Praise_Already(26,(byte)22,false,TextId.Social_Praise_Already),
	Social_Praise_Not_Friend(26,(byte)23,false,TextId.Social_Praise_Not_Friend),
	Social_Praise_Tip(26,(byte)24,false,TextId.Social_Praise_Tip),
	Social_Transmission_Self(26,(byte)25,false,TextId.Social_Transmission_Self),
	Social_Transmission_No_Times(26,(byte)26,false,TextId.Social_Transmission_No_Times),
	Social_Transmission_Level_low(26,(byte)27,false,TextId.Social_Transmission_Level_low),
	Social_Transmission_No_RecvTimes(26,(byte)28,false,TextId.Social_Transmission_No_RecvTimes),
	Social_Transmission_Success_Tip(26,(byte)29,false,TextId.Social_Transmission_Success_Tip),
	Social_Transmission_Recv_Success(26,(byte)30,false,TextId.Social_Transmission_Recv_Success),
	Social_Transmission_Refuse_Tip(26,(byte)31,false,TextId.Social_Transmission_Refuse_Tip),
	Social_Transmission_No_SRTimes(26,(byte)32,false,TextId.Social_Transmission_No_SRTimes),
	Social_Praise_Not_Enough(26,(byte)33,false,TextId.Social_Praise_Not_Enough),
	Social_Praise_Have_Got(26,(byte)34,false,TextId.Social_Praise_Have_Got),
	Social_Friend_Online(26,(byte)35,false,TextId.Social_Friend_Online),
	Social_Black_Already(26,(byte)36,false,TextId.Social_Black_Already),
	//商城
	Shop_Failure(27,(byte)0,false,TextId.Shop_Failure),
	Shop_Load_Failure(27,(byte)1,false,TextId.Shop_Load_Failure),
	Shop_Req_Param_Error(27,(byte)2,false,TextId.Shop_Req_Param_Error),
	Shop_Goods_Not_Exist(27,(byte)3,false,TextId.Shop_Goods_Not_Exist),
	Shop_Time_Not_Open(27,(byte)4,false,TextId.Shop_Time_Not_Open),
	Shop_GoldMoney_Not_Enough(27,(byte)5,false,TextId.Shop_GoldMoney_Not_Enough),
	Shop_BindMoney_Not_Enough(27,(byte)6,false,TextId.Shop_BindMoney_Not_Enough),
	Shop_Remain_Num_Not_Enough(27,(byte)7,false,TextId.Shop_Remain_Num_Not_Enough),
	Shop_Level_Not_Open(27,(byte)8,false,TextId.Shop_Level_Not_Open),

	//快捷购买
	QuickBuy_Param_Error(27, (byte)50, false, TextId.QuickBuy_Param_Error),
	QuickBuy_Gold_Not_Enough(27, (byte)51, false, TextId.QuickBuy_Gold_Not_Enough),
	QuickBuy_Confirm_Info(27, (byte)52, false, TextId.QuickBuy_Confirm_Info),
	QuickBuy_Goods_Info(27, (byte)53, false, TextId.QuickBuy_Goods_Info),

	//罗盘
	Compass_Failure(28,(byte)0,false,TextId.Compass_Failure),
	Compass_Not_Exist(28,(byte)1,false,TextId.Compass_Not_Exist),
	Compass_Mail_Context(28,(byte)2,false,TextId.Compass_Mail_Context),
	Compass_Count_Error(28,(byte)3,false,TextId.Compass_Count_Error),
	Compass_Req_Param_Error(28,(byte)4,false,TextId.Compass_Req_Param_Error),
	Compass_Not_Role_Level(28,(byte)5,false,TextId.Compass_Not_Role_Level),
	Compass_Not_Time(28,(byte)6,false,TextId.Compass_Not_Time),
	Compass_Money_Not_Enough(28,(byte)7,false,TextId.Compass_Money_Not_Enough),


	//拍卖行
	Auction_Input_Error(30,(byte)1,false,TextId.Auction_Input_Error),
	Auction_Input_Keyword(30,(byte)2,false,TextId.Auction_Input_Keyword),
	Auction_Input_Level_Error(30,(byte)3,false,TextId.Auction_Input_Level_Error),
	Auction_Error_Goods_Not_Exist(30,(byte)4,false,TextId.Auction_Error_Goods_Not_Exist),
	Auction_Search_Frequently(30,(byte)5,false,TextId.Auction_Search_Frequently),
	Auction_Sys_Frequently(30,(byte)6,false,TextId.Auction_Sys_Frequently),
	Auction_Goods_Not_Exist(30,(byte)7,false,TextId.Auction_Goods_Not_Exist),
	Auction_Goods_Illegality(30,(byte)8,false,TextId.Auction_Goods_Illegality),
	Auction_Goods_Down(30,(byte)9,false,TextId.Auction_Goods_Down),
	Auction_Bind_Goods_Not_Up(30,(byte)10,false,TextId.Auction_Bind_Goods_Not_Up),
	Auction_Time_Goods_Not_Up(30,(byte)11,false,TextId.Auction_Time_Goods_Not_Up),
	Auction_Up_Is_Check(30,(byte)12,false,TextId.Auction_Up_Is_Check),
	Auction_Money_Not_Enough(30,(byte)13,false,TextId.Auction_Money_Not_Enough),
	Auction_Money_Name_Not_Enough(30,(byte)14,false,TextId.Auction_Money_Name_Not_Enough),
	Auction_Money_Name_Not_Enough_Buy(30,(byte)15,false,TextId.Auction_Money_Name_Not_Enough_Buy),
	Auction_Record_Not_Exist(30,(byte)16,false,TextId.Auction_Record_Not_Exist),
	Auction_Record_Error(30,(byte)17,false,TextId.Auction_Record_Error),
	Auction_No_Power_Goods(30,(byte)18,false,TextId.Auction_No_Power_Goods),
	Auction_Not_Buy_Self_Goods(30,(byte)19,false,TextId.Auction_Not_Buy_Self_Goods),
	Auction_Buy_Success(30,(byte)20,false,TextId.Auction_Buy_Success),
	Auction_Down_Success(30,(byte)22,false,TextId.Auction_Down_Success),
	Auction_Sell_Down(30,(byte)23,false,TextId.Auction_Sell_Down),
	Auction_Sell_Money(30,(byte)24,false,TextId.Auction_Sell_Money),


	//NPC
	Npc_Not_Exist(31,(byte)1,false,TextId.Npc_Not_Exist),
	Npc_Map_Not_Exist(31,(byte)2,false,TextId.Npc_Map_Not_Exist),
	Npc_Trans_Success(31,(byte)3,false,TextId.Npc_Trans_Success),
	Npc_Inspire_Cost_Not_Enough(31,(byte)4,false,TextId.Npc_Inspire_Cost_Not_Enough),
	Npc_Inspire_Max(31,(byte)5,false,TextId.Npc_Inspire_Max),

	//任务条件名称
	Quest_Term_Kill_Monster_Limit(33,(byte)9,false,TextId.Quest_Term_Kill_Monster_Limit),
	Quest_Term_Use_Goods_Type(33,(byte)13,false,TextId.Quest_Term_Use_Goods_Type),
	Quest_Term_Discovery_Type(33,(byte)14,false,TextId.Quest_Term_Discovery_Type),
	Quest_Term_Attribute_Type(33,(byte)15,false,TextId.Quest_Term_Attribute_Type),
	Quest_Term_GoodsCollect_Type(33,(byte)16,false,TextId.Quest_Term_GoodsCollect_Type),
	Quest_Term_ChooseMenu_Type(33,(byte)17,false,TextId.Quest_Term_ChooseMenu_Type),
	Quest_Term_KillCopyMonster_Type(33,(byte)19,false,TextId.Quest_Term_KillCopyMonster_Type),
	Quest_Term_KillMonsterCollect_Type(33,(byte)20,false,TextId.Quest_Term_KillMonsterCollect_Type),//击杀${npcName}
	Quest_Term_KillMonster_Type(33,(byte)21,false,TextId.Quest_Term_KillMonster_Type),
	Quest_Term_KillNpcFall_Type(33,(byte)22,false,TextId.Quest_Term_KillNpcFall_Type),//击杀${npcName}
	Quest_Term_KillRole_Type(33,(byte)23,false,TextId.Quest_Term_KillRole_Type),
	Quest_Term_MapPoint_Type(33,(byte)24,false,TextId.Quest_Term_MapPoint_Type),
	Quest_Term_Trans_Type(33,(byte)33,false,TextId.Quest_Term_Trans_Type),
	Quest_Term_UseGoods(33,(byte)34,false,TextId.Quest_Term_UseGoods),
	Quest_Term_TriggerEventCollect_Type(33,(byte)35,false,TextId.Quest_Term_TriggerEventCollect_Type),
	Quest_Term_TriggerEvent_Type(33,(byte)36,false,TextId.Quest_Term_TriggerEvent_Type),

	//召唤
	Summon_Not_InDate(34, (byte)0, false, TextId.Summon_Not_InDate),
	Summon_Frequency_Not_Enough(34, (byte)1, false, TextId.Summon_Frequency_Not_Enough),
	Summon_Condition_Not_Meet(34, (byte)2, false, TextId.Summon_Condition_Not_Meet),
	Summon_ConsumeGood_Not_Enough(34, (byte)3, false, TextId.Summon_ConsumeGood_Not_Enough),
	Summon_Backpack_Not_Enough(34, (byte)4, false, TextId.Summon_Backpack_Not_Enough),
	Summon_Can_Not_Summon(34, (byte)5, false, TextId.Summon_Can_Not_Summon),
	Summon_Can_Summon(34, (byte)6, true, TextId.Summon_Can_Summon),
	Summon_Menoy_Not_Enough(34, (byte)7, false, TextId.Summon_Menoy_Not_Enough),
	Summon_Faction_Not_Has(34, (byte)8, false, TextId.Summon_Faction_Not_Has),
	Summon_Faction_Integal_Not_Enough(34, (byte)9, false, TextId.Summon_Faction_Integal_Not_Enough),
	Summon_Faction_Contribute_Not_Enough(34, (byte)10, false, TextId.Summon_Faction_Contribute_Not_Enough),
	Summon_Faction_Money_Not_Enough(34, (byte)9, false, TextId.Summon_Faction_Money_Not_Enough),

	//死亡复活
	Reborn_Illegality(35, (byte)1, false, TextId.Reborn_Illegality),
	Reborn_Not_Have_Point(35, (byte)2, false, TextId.Reborn_Not_Have_Point),
	
	//炼金
	Alchemy_Mail_Context(36,(byte)1,false,TextId.Alchemy_Mail_Context),
	Alchemy_Count_Limit(36,(byte)2,false,TextId.Alchemy_Count_Limit),
	Alchemy_Req_Param_Error(36,(byte)3,false,TextId.Alchemy_Req_Param_Error),
	Alchemy_Not_Role_Level(36,(byte)4,false,TextId.Alchemy_Not_Role_Level),
	Alchemy_Not_Time(36,(byte)5,false,TextId.Alchemy_Not_Time),
	Alchemy_Money_Not_Enough(36,(byte)6,false,TextId.Alchemy_Money_Not_Enough),
	;
	
	
	//统一编码(系统内唯一)
	private final int unityCode;
	//内部编码(可重复),和客户端协议相关
	private final byte innerCode ;
	//是否代表成功
	private final boolean success;
	//提示信息
	private final String tips ;
	
	Status(int unityCode,byte innerCode,boolean success,String tips){
		this.unityCode = unityCode ;
		this.innerCode = innerCode ;
		this.success = success ;
		this.tips = tips ;
	}

	public int getUnityCode() {
		return unityCode;
	}

	public byte getInnerCode() {
		return innerCode;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getTips() {
		return GameContext.getI18n().getText(tips);
	}
}
