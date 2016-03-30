package baoutil;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.app.count.vo.CountRecord;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.RoleBackpack;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTreasure;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.rank.RankAppImpl;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1401_ExchangeListReqMessage;

public class LogBatJFrameRole extends JFrame implements ActionListener {
	public static ConcurrentHashMap<String, LogBatJFrameRole> allFramesMap = new ConcurrentHashMap<String, LogBatJFrameRole>();
	private JTextPane textPane;
	private JTextPane textPane1;
	private JPanel panel;

	private JPanel panel_control;
	private JButton[] buttons_control = new JButton[4];
	private JTextField[] textFields_control = new JTextField[4];
	private String[] s_control = { "打开一键追回", "一键追回详情", "一键追回", "金手指" };
	private JLabel msgLabel;
	private RoleInstance role;
	private static int frameWidth = 900;
	private static int frameHeight = 80;
	private static int southPanelHeight = 50;
	private static int textPaneWidth = 860;
	private static int textPaneHeight = 50;
	public String title;
	public int zuanshi;
	public int jinbi ;
	public int dkp ;
	//temp
	private final static String path = "D://workspace//draco//resources//txt//test-dirty-word.txt";
	List<String>  list = new ArrayList<String>();
	public LogBatJFrameRole() {
		super();
		this.setBounds(200, 100, frameWidth, frameHeight);
		this.setDefaultCloseOperation(2);

		Container ct = this.getContentPane();
		ct.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));
		panel_control = getPanelSouth();
		ct.add(panel_control);

		// 隐藏的
		textPane = new JTextPane();
		textPane.setPreferredSize(new Dimension(textPaneWidth, textPaneHeight));

		textPane = getSimpleTextPanle(1);
		textPane1 = getSimpleTextPanle(17);

		panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
		panel.setPreferredSize(new Dimension(textPaneWidth,
						textPaneHeight * 18));

		panel.add(new JScrollPane(textPane));
		panel.add(new JScrollPane(textPane1));
		ct.add(panel, BorderLayout.CENTER);

		this.setVisible(true);
	}

	private JTextPane getSimpleTextPanle(int size) {
		JTextPane textPane = new JTextPane();
		textPane.setPreferredSize(new Dimension(textPaneWidth, textPaneHeight*size));
		return textPane;
	}

	public LogBatJFrameRole(RoleInstance role) {
		this();
		if (role != null) {
			textFields_control[0].setText(role.getRoleId());
			title = "<" + role.getRoleName() + ">" + role.getRoleId();
			textFields_control[1].setText(role.getRoleName());
			allFramesMap.put(role.getRoleId(), this);
			this.setTitle(title);
		}
		this.role = role;
		
	}

	private JTextField getSimpleJtext() {
		JTextField tField = new JTextField();
		tField.setPreferredSize(new Dimension(100, 25));
		return tField;
	}

	private JLabel getSimpleJLabel() {
		JLabel jl = new JLabel();
		jl.setPreferredSize(new Dimension(100, 25));
		return jl;
	}

	private JButton getSimpleButton(String bt_name) {
		JButton button = new JButton(bt_name);
		button.addActionListener(this);
		button.setPreferredSize(new Dimension(100, 25));
		button.setContentAreaFilled(false);
		return button;
	}

	public JPanel getPanelSouth() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.setPreferredSize(new Dimension(frameWidth, southPanelHeight));
		for (int i = 0; i < buttons_control.length; i++) {
			buttons_control[i] = getSimpleButton(s_control[i]);
			textFields_control[i] = getSimpleJtext();
			panel.add(textFields_control[i]);
			panel.add(buttons_control[i]);
		}
		msgLabel = getSimpleJLabel();
		panel.add(msgLabel);
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		updateTitle();
//		addRoleGoods(role);
		setTextPane();
		GameContext.getRankApp().getRankedUnionIds(18);
		if (e.getSource() == buttons_control[0]) {
			String roleId = textFields_control[0].getText();
			int heroId = 1002019;
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
			if (role == null) {
				msgLabel.setText("玩家不在线");
//				return;
			}
			try {
				role = GameContext.getUserRoleApp().getRoleByRoleId(roleId);
			} catch (ServiceException e1) {
				e1.printStackTrace();
			}
//			GameContext.getRankApp().getRankedUnionIds(10);
/*			RoleCount rc = role.getRoleCount();
			int onlieSeconds = rc.getRoleTimesToInt(CountType.YesterDayOnlineTimeSeconds);
			System.err.println(onlieSeconds);
			int b = rc.getRoleTimesToInt(CountType.ToDayOnlineTimeSeconds);
			System.err.println("a="+onlieSeconds);
			System.err.println("b="+b);*/
//			GameContext.getUnionBattleApp().testActiveEnd();
			//18个最强公会
//			List<String> list = GameContext.getRankApp().getRankedUnionIds(18);
//			System.out.println(list);
//			C1925_RecoveryPanelReqMessage m2 =new C1925_RecoveryPanelReqMessage();
//			role.getBehavior().addCumulateEvent(m2);
			if(true)return;
			//d兑换
			C1401_ExchangeListReqMessage m =new C1401_ExchangeListReqMessage();
			m.setParam("3");
			role.getBehavior().addCumulateEvent(m);
			if(true)return;
			
			
			//藏宝图
//			C0515_GoodsTreasureTransReqMessage m = new C0515_GoodsTreasureTransReqMessage();
//			m.setParam("tcdprn");
//			role.getBehavior().addCumulateEvent(m);
//			给角色加物品1万个
//			addRoleGoods(role);
			
			//商店刷新二次确认BUG
//			Message notifyMsg = QuickCostHelper.getMessage(role,
//					(short)1619, "19001" + "," + 1,
//					(short) 0, "", 2, 0, "haha");
//			role.getBehavior().sendMessage(notifyMsg);
//			if(true)return;
			
			//打开“公会战”面板
//			C2530_UnionBattlePanelReqMessage m = new C2530_UnionBattlePanelReqMessage();
//			role.getBehavior().addCumulateEvent(m);
			
			//进入公会战
//			C2531_UnionBattleJoinReqMessage m = new C2531_UnionBattleJoinReqMessage();
//			m.setBattleId(1);
//			role.getBehavior().addCumulateEvent(m);
			
			
//			if(true)return;
			//无用
//			HeroEquipBackpack h = GameContext.getUserHeroApp().getEquipBackpack(roleId, 1002019 );
//			Collection<HeroEquipBackpack> packList = GameContext.getUserHeroApp().getEquipBackpack(roleId);
//			if(h!=null)
//			textPane.setText(h.getAllGoods()+"");
//			textPane.setText("---"+packList);//null
			
			//获得英雄
//			Collection<RoleHero> heros = GameContext.getUserHeroApp().getAllRoleHero(roleId);
//			if(Util.isEmpty(heros)){
//				heros = GameContext.getBaseDAO().selectList(RoleHero.class, 
//						RoleHero.ROLE_ID, roleId);
//			}
//			
//			//获得英雄的装备
//			Map<String,List<RoleGoods>> equipMap = GameContext.getHeroApp().buildHeroEquipMap(role.getRoleId());
//			List<RoleGoods>  list = equipMap.get(String.valueOf(1002019));
//			HeroEquipBackpack equippack = new HeroEquipBackpack(role,
//					ParasConstant.HERO_EQUIP_MAX_NUM,heroId);
//			equippack.initGoods(list);
//			textPane1.setText(list+"\n"+equippack.getAllGoods());
			

			//BOSS战
//			C0611_BossListReqMessage m = new C0611_BossListReqMessage();
//			role.getBehavior().addCumulateEvent(m);
//			
//			RoleRecovery rc = GameContext.getBaseDAO().selectEntity(RoleRecovery.class, RoleRecovery.ROLE_ID, role.getRoleId(), RoleRecovery.RECOVER_ID, "2");
			
			
//			C0612_BossEnterMapReqMessage m2 = new C0612_BossEnterMapReqMessage();
//			m2.setId((short)1001 );
//			role.getBehavior().addCumulateEvent(m2);
			
			//统一的打开神秘商店和兑换
//			C1408_TradeFunctionReqMessage m = new C1408_TradeFunctionReqMessage();
//			m.setInterId((byte)1);
//			role.getBehavior().addCumulateEvent(m);
			//☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆
//			addPaneRoleGoods(role);
//			
//			GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.braveSoul, OperatorType.Add, 2, OutputConsumeType.goods_decompose);
//			role.getBehavior().notifyAttribute();
			
			//测试分解
			
//			C0560_GoodsDecomposeReqMessage m = new C0560_GoodsDecomposeReqMessage();
//			m.setNum((short)1);
//			m.setGoodsId("13000005");
//			role.getBehavior().addCumulateEvent(m);
			
			
			//VIP商城打开礼包面板
//			C2106_VipShopGiftReqMessage m = new C2106_VipShopGiftReqMessage();
//			role.getBehavior().addCumulateEvent(m);
			
//			每日任务（翻牌）
//			C0722_QuestPokerBuyCountTimeReqMessage m = new C0722_QuestPokerBuyCountTimeReqMessage();
//			m.setParam("1");
//			role.getBehavior().addCumulateEvent(m);
			//判断一件追回是否有红点儿
//			String s =  GameContext.getRecoveryApp().hasFreeRecovery(role)+"";
//			
//			
//			textPane.setText(s);
			
			//给角色删除物品还没有
			
			
			//测试一键追回的根据类型和参数得到追回ID的方法
//			String s = GameContext.getRecoveryApp().getRecoveryIdByTypeAndParam(RecoveryType.HUNG_UP_EXP.getType(),"");
//			s = s + "\n" + GameContext.getRecoveryApp().getRecoveryIdByTypeAndParam(RecoveryType.COPY.getType(), "39");
//			s = s + "\n" + GameContext.getRecoveryApp().getRecoveryIdByTypeAndParam(RecoveryType.COPY.getType(), "42");
//			s = s + "\n" + GameContext.getRecoveryApp().getRecoveryIdByTypeAndParam(RecoveryType.COPY.getType(), "40");
//
//			s = s + "\n" + GameContext.getRecoveryApp().getRecoveryIdByTypeAndParam(RecoveryType.ARENA_RECOVERY.getType(), "");
//			s = s + "\n" + GameContext.getRecoveryApp().getRecoveryIdByTypeAndParam(RecoveryType.BOSS_KILL.getType(), "");
//			s = s + "\n" + GameContext.getRecoveryApp().getRecoveryIdByTypeAndParam(RecoveryType.CAMP_BATTLE.getType(), "");
//			s = s + "\n" + GameContext.getRecoveryApp().getRecoveryIdByTypeAndParam(RecoveryType.DAILY_QUEST.getType(), "");
//			s = s + "\n" + GameContext.getRecoveryApp().getRecoveryIdByTypeAndParam(RecoveryType.ANGEL_CHEST.getType(), "");
//			
//			
//			
//			textPane.setText(s);
			
			//测试打开一键追回
//			C1925_RecoveryPanelReqMessage msg = new C1925_RecoveryPanelReqMessage();
//			role.getBehavior().addCumulateEvent(msg);
			
//			//海盗宝箱改幸运转盘
//			C1915_LuckyBoxDisplayReqMessage msg = new C1915_LuckyBoxDisplayReqMessage();
//			msg.setRefreshFlag((byte)0);
//			role.getBehavior().addCumulateEvent(msg);
			
			//传送地图
//			GameContext.getChatApp().sendSysMessage(ChatSysName.System,
//					ChannelType.World, textFields_control[1].getText(), null,
//					role);
//			Point targetPoint = new Point("01_MFSM_mofangshenmiao",800,800);
//			try {
//				GameContext.getUserMapApp().changeMap(role, targetPoint);
//			} catch (ServiceException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			//复活
//			C2001_RoleRebornReqMessage msg = new C2001_RoleRebornReqMessage();
//			role.getBehavior().addCumulateEvent(msg);
			
//			C0620_LevelUpEnhanceOptionReqMessage msg = new C0620_LevelUpEnhanceOptionReqMessage();
//			role.getBehavior().addCumulateEvent(msg);
			
//			C2510_VipDisplayReqMessage msg = new C2510_VipDisplayReqMessage();
//			role.getBehavior().addCumulateEvent(---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------sg);
//			RoleCount rc = role.getRoleCount();
//			GameContext.getCountApp().setAccumulateLoginCount(role,
//					20, 180);
			C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage(
					"你好，提示信息");
			role.getBehavior().sendMessage(msg);
			GameContext.getChatApp().sendSysMessage(ChatSysName.Active_Compass,
					    ChannelType.Publicize_Personal, "你好广播信息", null, null);
//			
//			C1619_ShopSecretRefreshReqMessage m = new C1619_ShopSecretRefreshReqMessage();
//			m.setParam("2");
//			role.getBehavior().addCumulateEvent(m);
//			for(int i = 0;i<30;i++){
//				String s = GameContext.getVipApp().getNextVipLevelPrivilegeInfo((byte)i, 0, "");
//				String ss = textPane.getText();
//				
//				textPane.setText(ss+"\n"+"<"+i+">"+s);
//			}
		}
		if (e.getSource() == buttons_control[1]) {
			if (role == null) {
				textFields_control[2].setText("玩家不在线");
				return;
			}
			//显示角色所在地图 以及工会
			String s =  role.getMapInstance().getInstanceId();
			String su = role.getUnionId();
			this.setTitle(this.getTitle() + "--MAP--【" + s + "】<"+su + ">");
			
			//显示RoleCount信息
//			RoleCount rc = role.getRoleCount();
			RoleCount rc = null;
			if(rc == null){
				rc = GameContext.getBaseDAO().selectEntity(RoleCount.class, RoleCount.ROLE_ID, role.getRoleId());
				rc.parseDataBase();
			}
			Map<Integer, CountRecord> timesMap = rc.getTimesMap();
			for (CountRecord cr : timesMap.values()) {
				System.err.println(cr.getId()+"<-->"+cr.getV());
				System.out.println(CountType.get(cr.getId()).toString());
			}
			
			
//			C1928_RecoveryInfoReqMessage msg = new C1928_RecoveryInfoReqMessage();
//			msg.setId("1");
//			role.getBehavior().addCumulateEvent(msg);
			
			//添加伪的一键追回数据
//			GameContext.getRecoveryApp().saveRoleRecovery(role, "1", (short)1, 1, "500,1000");
//			int i = 1;
//			while(i++<7){
//				GameContext.getRecoveryApp().saveRoleRecovery(role, i+"", (short)1, i, "");
//
//			}
//			GameContext.getRecoveryApp().saveCopyRecovery(role,  3, (short)39);
//			GameContext.getRecoveryApp().saveCopyRecovery(role,  3, (short)42);
//			GameContext.getRecoveryApp().saveCopyRecovery(role,  3, (short)40);
			//测试幸运转盘转动
//			C1916_LuckyBoxPlayReqMessage msg = new C1916_LuckyBoxPlayReqMessage();
//			msg.setCoordinate((byte)0);
//			role.getBehavior().addCumulateEvent(msg);
			//获得角色血量
			
//			int xue = role.get(AttributeType.curHP);
//			int xue_delete = xue - 1;
//			GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.curHP, 
//					OperatorType.Decrease, 
//					xue_delete,OutputConsumeType.gm_consume);
//			role.getBehavior().notifyAttribute();
//			
//			C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage(
//					"目前血量调整到"+role.get(AttributeType.curHP));
//			role.getBehavior().sendMessage(msg);
			
			
			//
//			C2512_VipCallFunctionReqMessage msg = new C2512_VipCallFunctionReqMessage();
//			msg.setFunctionId("1");
//			role.getBehavior().addCumulateEvent(msg);
//			int itemId = Integer.parseInt(textFields_control[3].getText());
//			textFields_control[2].setText("玩家在线!!测试购买神秘商店！");
//			C2522_AccumulateLoginAwardDetailReqMessage msg = new C2522_AccumulateLoginAwardDetailReqMessage();
//			byte day = 2;
//			msg.setDay(day);
//			role.getBehavior().addCumulateEvent(msg);
//			C1620_ShopSecretBuyReqMessage msg = new C1620_ShopSecretBuyReqMessage();
//			msg.setShopId("2");
//			msg.setId(itemId);
//			role.getBehavior().addCumulateEvent(msg);
//			try {
//				Class.forName("ac.ahocorasick.WordsFilter");
//			} catch (ClassNotFoundException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			initList();
			
//			initList();
//			initList();
//			initList();
//			initList();
		}
		if (e.getSource() == buttons_control[2]) {
			
			RoleCount rc = GameContext.getBaseDAO().selectEntity(RoleCount.class, "roleId", role.getRoleId());
			Date d = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			c.add(c.DATE,-1);
			d=c.getTime(); //这个时间就是日期往后推一天的结果 
			rc.setDayTime(d);
			role.setRoleCount(rc);
			
			GameContext.getBaseDAO().update(rc);
			//一键追回
//			C1926_RecoveryReqMessage msg = new C1926_RecoveryReqMessage();
//			msg.setConsumeType((byte)1);
//			msg.setId("1");
//			role.getBehavior().addCumulateEvent(msg);
			//
//			if (role == null) {
//				textFields_control[2].setText("家不在线");
//				return;
//			}
//			RoleSecretShop s = GameContext.getShopSecretApp().selectRoleSecretShopFromDB(role.getRoleId(), "2");
//			if(s!=null){
//				s.initDB();
//				textPane.setText(s.toString());
//			}else{
//				textPane.setText("null");
//			}
			
//			C1618_ShopSecretOpenPanelReqMessage msg = new C1618_ShopSecretOpenPanelReqMessage();
//			msg.setShopId((byte)1);
//			role.getBehavior().addCumulateEvent(msg);

			
//			Message msg = GameContext.getShopSecretApp().openShopSecretEnterRespMessage(role, "2");
//			if(msg == null){
//				textPane1.setText(null);
//				return;
//			}
			
			//--- 测试 过滤敏感词
//			try {
//				System.out.println("start");
//				Thread.sleep(1000);
//				test1(list);
//				Thread.sleep(1000);
//				test2(list);
//				textFields_control[2].setText("家在线!!");
//			
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
			//测试 运营的商城
//			C2105_ShopEnterReqMessage msg = new C2105_ShopEnterReqMessage();
			
			//    限时抢购
//			C2101_ShopGoodsListReqMessage msg = new C2101_ShopGoodsListReqMessage();
			//    每日限购 
//			C2101_ShopGoodsListReqMessage msg = new C2101_ShopGoodsListReqMessage();
//			msg.setType((byte)1);
			
			//VIP奖励
//			C2106_VipShopGiftReqMessage msg = new C2106_VipShopGiftReqMessage();
			
//			C2107_VipShopGiftReceiveReqMessage msg = new C2107_VipShopGiftReceiveReqMessage();
//			msg.setVipLevel((byte)1);
//			role.getBehavior().addCumulateEvent(msg);
//			
//			C2101_ShopGoodsListReqMessage m = new C2101_ShopGoodsListReqMessage();
//			m.setType((byte)0);
//			role.getBehavior().addCumulateEvent(m);
//			
//			C1403_ExchangeDetailReqMessage mm = new C1403_ExchangeDetailReqMessage();
//			mm.setParam("1");
//			role.getBehavior().addCumulateEvent(mm);
		}

		// 金手指
		if (e.getSource() == buttons_control[3]) {
			String roleId = textFields_control[0].getText();
			RoleInstance role = GameContext.getOnlineCenter()
					.getRoleInstanceByRoleId(roleId);
			if (role == null) {
				msgLabel.setText("玩家不在线");
				return;
			}
//			GameContext.getUserAttributeApp().changeRoleMoney(role,
//					AttributeType.goldMoney, OperatorType.Add, 1000,
//					OutputConsumeType.gm_consume);
//			GameContext.getUserAttributeApp().changeRoleMoney(role,
//					AttributeType.silverMoney, OperatorType.Add, 1000,
//					OutputConsumeType.gm_consume);
//			GameContext.getUserAttributeApp().changeRoleDkp(role, AttributeType.dkp,
//					OperatorType.Add, 500,
//					OutputConsumeType.accumulate_login_reward);
//			role.getBehavior().notifyAttribute();
			
			GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.atk,  OperatorType.Add, 9999000, OutputConsumeType.gm_output);
			GameContext.getUserAttributeApp().changeRoleDkp(role, AttributeType.dkp,  OperatorType.Add, 1000, OutputConsumeType.gm_output);
			GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.goldMoney,  OperatorType.Add, 1000, OutputConsumeType.gm_output);
//			role.getBehavior().changeAttribute(AttributeType.exp, OperatorType.Add, 999999);

//			GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.speed,  OperatorType.Add, 10000, OutputConsumeType.gm_output);
			role.getBehavior().notifyAttribute();
			if(true)return;
			String exps = textFields_control[3].getText();
			if(exps!=null&&!exps.equals("")){
				int exp = Integer.parseInt(exps);
				GameContext.getVipApp().addVipLevelExp(role.getIntRoleId(), exp);
				return;
			}
//			GameContext.getUserGoodsApp().deleteForBag(role, delList, null);/
			int vipEx = GameContext.getVipApp().getRoleVipExp(role);
			GameContext.getVipApp().addVipLevelExp(role.getIntRoleId(), 200000);
		}

	}

	private void addRoleGoods(RoleInstance role) {
		List<GoodsOperateBean> addList = new ArrayList<GoodsOperateBean>();
		GoodsOperateBean bean = new GoodsOperateBean();
		bean.setGoodsId(7009003);
		bean.setGoodsNum(1);
		addList.add(bean);
		GameContext.getUserGoodsApp().addSomeGoodsBeanForBag(role, addList,OutputConsumeType.luckybox_output);
	}

	private void addPaneRoleGoods(RoleInstance role2) {
	
//		String s = textPane1.getText()+"\n";
		setTextPane();
		if(true)return;
		//添加物品
		List<GoodsOperateBean> addList = new ArrayList<GoodsOperateBean>();
		GoodsOperateBean bean = new GoodsOperateBean();
		bean.setGoodsId(7009001);
		bean.setGoodsNum(2);
		addList.add(bean);
		GameContext.getUserGoodsApp().addSomeGoodsBeanForBag(role, addList,OutputConsumeType.luckybox_output);
		
//		GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.atk,  OperatorType.Add, 1, OutputConsumeType.gm_output);
//		role.getBehavior().notifyAttribute();

		//删除物品
		return;
	}

	private void setTextPane() {
		try {
			String s = "\n******************************\n";
			String s1 = "<braveSoul=" +role.get(AttributeType.braveSoul)+">,\n<wildBlood="+role.get(AttributeType.wildBlood)+">" +
					"honor:"+role.get(AttributeType.honor);
			RoleBackpack pack = role.getRoleBackpack();
			List<RoleGoods> list = pack.getAllGoods();
			for (RoleGoods roleGoods : list) {
				GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(
						roleGoods.getGoodsId());
				s += "goodsName:"+goodsBase.getName() +"="+roleGoods.getId()+", goodsId = "+goodsBase.getId()+",num:"+roleGoods.getCurrOverlapCount()+"\n";
				s+="\n------decomposeDesc = " + goodsBase.getDecomposeDesc();
//				s+="\n------goodsInstanceId = " + roleGoods.getGoodsId(;
				s+="\ngetRecyclePrice = "+goodsBase.getRecyclePrice()+"\n";
				if(goodsBase instanceof GoodsTreasure){
					GoodsTreasure t = (GoodsTreasure)goodsBase;
					System.err.println("\n-------------藏宝图物品----------------");
					System.err.println("t.getMapProbs()----------" + t.getMapProbs());
					System.err.println("t.getClueDesc()----------"+t.getClueDesc());
					System.err.println("t.getMaps()--------------"+t.getMaps());
					System.err.println("roleGoods.getOtherParm()--------------"+roleGoods.getOtherParm());
					
				}
			}
			textPane1.setText(s1+s+"\n---------------------------------");
			System.err.println(s1+s+"\n---------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateTitle() {
		Map<Integer ,Integer>m = new HashMap<Integer, Integer>();
		m.put(8002, 100);
		m.put(8001, 100);
		List<Integer> oddsList = Util.getLuckyDrawUnique(1, m);
//		LogB.ic("内部测试结果");
//		GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.heroCoin,
//				OperatorType.Add, 999999,
//				OutputConsumeType.shop_secret_refresh_consume);
		LogB.ic(oddsList);
		int zuanshiT = role.get(AttributeType.goldMoney) - zuanshi;
		zuanshi = role.get(AttributeType.goldMoney);
		
		int jinbiT = role.get(AttributeType.gameMoney) - jinbi;
		jinbi = role.get(AttributeType.gameMoney);
		
		dkp = role.get(AttributeType.goldMoney);
		String newTitle =  "钻石:"+zuanshi+"("+ zuanshiT+"),游戏币："+jinbi +"("+jinbiT+")";
		String s1 = "<勇者之魂" +role.get(AttributeType.braveSoul)+",狂野之血"+role.get(AttributeType.wildBlood)+">" +
				"<荣誉"+role.get(AttributeType.honor)+">" +"<DKP"+role.get(AttributeType.dkp)+">";
		this.setTitle(title+newTitle +s1);
		LogB.ic("<DKP"+role.get(AttributeType.dkp)+">");
		LogB.ic("<heroCoin"+role.get(AttributeType.heroCoin)+">");
	}
	public void initList(){
		BufferedReader br;
		try {
			FileInputStream fs = new FileInputStream(path); 
		    InputStreamReader reader = new InputStreamReader(fs, "UTF-8"); 
			br = new BufferedReader(reader);
			String line = null;
			while ((line = br.readLine()) != null) {
				list.add(line);
		}
		br.close();
		
		String s = GameContext.getIllegalWordsService().findIllegalChar("李洪志");
		textPane.setText(s);
		
		textPane1.setText(s);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	public void test1(List <String> list){
		Runtime.getRuntime().gc();
		Thread.yield();
		Runtime rt = Runtime.getRuntime();
		long a = rt.freeMemory();
		//
		long time = System.currentTimeMillis();
		StringBuffer sb = new StringBuffer("\n");
		for (String line : list) {
//			line = WordsFilter.getInstance().findFilterWord(line);
			sb.append(line);
		}
		time = System.currentTimeMillis() -time;
		String s= time+"毫秒  me"+list.size();
		sb.append(s);
		long b = rt.freeMemory();
		sb.append("内存占用"+(a-b));
		textPane.setText(sb.toString());
		
	}
	public void test2(List <String> list){
		Runtime.getRuntime().gc();
		Thread.yield();
		Runtime rt = Runtime.getRuntime();
		long a = rt.freeMemory();
		
		long time = System.currentTimeMillis();
		StringBuffer sb = new StringBuffer("\n");
		for (String line : list) {
				line = GameContext.getIllegalWordsService().findIllegalChar(line);
				sb.append(line);
				
		}
		time = System.currentTimeMillis() -time;
		String s= time+"毫秒  server"+list.size();
		sb.append(s);
		long b = rt.freeMemory();
		sb.append("内存占用"+(a-b));
		textPane1.setText(sb.toString());
		
	}
	public void setRole(RoleInstance role) {
		this.role = role;
		this.textFields_control[0].setText(role.getRoleId());
		allFramesMap.put(role.getRoleId(), this);
	}

	public static void remove(RoleInstance role) {
		allFramesMap.get(role.getRoleId()).dispose();
		allFramesMap.remove(role.getRoleId());
	}

	public static void main(String[] args) {
		new LogBatJFrameRole();
	}
}