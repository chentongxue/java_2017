package baoutil;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.relation.Role;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import lombok.Data;

import org.aspectj.lang.annotation.DeclareAnnotation;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.accumulatelogin.AccumulateLoginAppImpl;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.shop.ShopTimeAppImpl;
import com.game.draco.app.vip.VipAppImpl;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2101_ShopGoodsListReqMessage;
import com.game.draco.message.request.C2520_AccumulateLoginReqMessage;
import com.game.draco.message.request.C2521_AccumulateLoginAwardReceiveReqMessage;
import com.game.draco.message.response.C2520_AccumulateLoginRespMessage;

public class LogBatJFrame extends JFrame implements ActionListener {
	private JPanel panel;
	
	private JTextPane textPane;
	private JButton clean_button;
	private JButton search_button;
	private JButton search_v_button;
	private JButton[] buttons = new JButton[5];
	private JTextField jTextField;
	private JTextField searchField;
	public VipAppImpl VipAppImpl;
	public ShopTimeAppImpl shopTimeAppImpl;
	public AccumulateLoginAppImpl accumulateLoginAppImpl;
	public  Service service = null;
	public RoleInstance role;
	public int roleId = 0;
	private String file = "C:\\Users\\mofun030601\\Desktop\\备份\\o_file";
	private ArrayList<LogBItem> wordsList = new ArrayList<LogBItem>();

	
	//south panel
	private JPanel panel_control;
	private JButton[] buttons_control = new JButton[4];
	private JTextField[] textFields_control = new JTextField[4];
	private String[] s_control = {"通知","钻石","金币","金手指"};
	public LogBatJFrame(String name) {
		super("(" + name + ")");
		this.setBounds(200, 100, 1100, 800);
		this.setDefaultCloseOperation(3);

		panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.LIGHT_GRAY);

		// font = new Font("Consolas", 0, 25);

		textPane = new JTextPane();

		// textPane.setFont(font);
		textPane.setBackground(Color.DARK_GRAY);

		Container ct = this.getContentPane();
		ct.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));

		panel.add(new JScrollPane(textPane));
		
		clean_button = getSimpleButton("clear");
		search_button = getSimpleButton("search");
		search_v_button = getSimpleButton("no_have");

		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = getSimpleButton("button" + i);
			ct.add(buttons[i], "North");
		}


		searchField = getSimpleJtext();
		jTextField = getSimpleJtext();

		ct.add(clean_button, "North");
		ct.add(jTextField, "North");
		ct.add(search_button, "North");
		ct.add(searchField, "North");
		ct.add(search_v_button, "North");

		panel.setPreferredSize(new Dimension(1016, 600));
		ct.add(panel, BorderLayout.CENTER);

		panel_control = getPanelSouth();
		
		ct.add(panel_control,BorderLayout.SOUTH);
		
		this.setVisible(true);

		File f = new File(file);
	}
	private JTextField getSimpleJtext() {
		JTextField tField = new JTextField();
		tField.setPreferredSize(new Dimension(100, 25));
		return tField;
	}
	private JButton getSimpleButton(String bt_name) {
		JButton  button = new JButton(bt_name);
		button.addActionListener(this);
		button.setPreferredSize(new Dimension(100, 25));
		button.setContentAreaFilled(false);
		return button;
	}
	public JPanel getPanelSouth(){
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//		panel.setBackground(Color.LIGHT_GRAY);
		panel.setPreferredSize(new Dimension(1016, 200));
		for (int i = 0; i < buttons_control.length; i++) {
			buttons_control[i] = getSimpleButton(s_control[i]);
			textFields_control[i] = getSimpleJtext();
			panel.add(textFields_control[i]);
			panel.add(buttons_control[i]);
			
		}
		return panel;
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == clean_button) {
			System.out.println("JJ");
			wordsList.clear();
			textPane.setText("");
		}
		if (e.getSource() == search_v_button) {
			System.out.println("search_v_button");
			textPane.setText("");
			String filterWord = searchField.getText().trim();
			for (LogBItem it : wordsList) {
				if (!it.getMsg().contains(filterWord)) {
					insert(it);
				}
			}
		}
		if (e.getSource() == search_button) {
			textPane.setText("");
			String filterWord = searchField.getText().trim();
			if(filterWord.equals("")){
				return;
			}
			for (LogBItem it : wordsList) {
				if (it.getMsg().contains(filterWord)) {
					String src = "^^##^^" + it.getMsg() + "^^##^^";
					String[] ss = src.split(filterWord);
					System.out.println(Arrays.toString(ss));
					SimpleAttributeSet newSt = (SimpleAttributeSet) it.getSt()
							.clone();
					appendLine(newSt);
					StyleConstants.setBackground(newSt, Color.PINK);
					append(ss[0].replace("^^##^^", ""), it.getSt());
					append(filterWord, newSt);
					for (int i = 1; i < ss.length - 1; i++) {
						append(ss[i], it.getSt());
						append(filterWord, newSt);
					}
					append(ss[ss.length - 1].replace("^^##^^", ""), it.getSt());
				} else {
					insert(it);
				}
			}
			System.out.println("JJ2--" + filterWord);
		}
		if (e.getSource() == buttons[0]) {
			service.start();
			System.out.println("yy");
//			int n = Integer.parseInt(jTextField.getText());
//			VipAppImpl.addDiamands(roleId, n);
		}
		if (e.getSource() == buttons[1]) {
			System.out.println("yy");
			// role.getBehavior().sendMessage(new
			// C2101_ShopGoodsListReqMessage());
//			role.getBehavior()
//					.addCumulateEvent(new C2105_EnterShopReqMessage());
//			C2101_ShopGoodsListReqMessage m = new C2101_ShopGoodsListReqMessage();
//			m.setType((byte) 1);
//			role.getBehavior().addCumulateEvent(m);
//
//			C2101_ShopGoodsListReqMessage m2 = new C2101_ShopGoodsListReqMessage();
//			m2.setType((byte) 0);
//			role.getBehavior().addCumulateEvent(m2);
		}
		if (e.getSource() == buttons[2]) {
//			System.out.println("22");
//			shopTimeAppImpl.start();
			
			///
			System.out.println("33");
			accumulateLoginAppImpl.start();
		}
		if (e.getSource() == buttons[3]) {
			if(role==null){
				textFields_control[2].setText("玩家不在线");
				return;
			}
			textFields_control[2].setText("玩家在线!!");
			C2520_AccumulateLoginReqMessage m = new C2520_AccumulateLoginReqMessage();
			role.getBehavior().addCumulateEvent(m);
		}
		if (e.getSource() == buttons[4]) {
			C2521_AccumulateLoginAwardReceiveReqMessage m2 = new C2521_AccumulateLoginAwardReceiveReqMessage();
			
//			m2.setDay((byte)0);
			role.getBehavior().addCumulateEvent(m2);
		}
		if (e.getSource() == buttons_control[0]) {
			String roleId = textFields_control[0].getText();
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
			if(role==null){
				textFields_control[2].setText("玩家不在线");
				return;
			}
			GameContext.getChatApp().sendSysMessage(
					ChatSysName.System, ChannelType.World, textFields_control[1].getText(), null, role);
					
			C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage(textFields_control[1].getText());
			role.getBehavior().sendMessage(msg);
		}
		//金手指
		if (e.getSource() == buttons_control[3]) {
			String roleId = textFields_control[0].getText();
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
			if(role==null){
				textFields_control[2].setText("玩家不在线");
				return;
			}
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					AttributeType.goldMoney, OperatorType.Add,
					1000, OutputConsumeType.gm_consume);
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					AttributeType.goldMoney, OperatorType.Add,
					1000, OutputConsumeType.gm_consume);
			role.getBehavior().notifyAttribute();
		}

	}

	public void setDocs(String str, Color col, Color b_col, Boolean bold,
			Integer fontSize) {

		SimpleAttributeSet attrSet = new SimpleAttributeSet();

		if (col == null)
			col = Color.green;
		StyleConstants.setForeground(attrSet, col);
		if (bold == null)
			bold = false;
		StyleConstants.setBold(attrSet, bold);

		if (b_col != null)
			StyleConstants.setBackground(attrSet, b_col);
		// StyleConstants.setItalic(attrSet, true);

		if (fontSize == null)
			fontSize = 15;
		StyleConstants.setFontSize(attrSet, fontSize);
		StyleConstants.setFontFamily(attrSet, "Consolas");
		insert(str, attrSet);
		wordsList.add(new LogBItem(str, attrSet));
	}

	public void appendLine(AttributeSet attrSet) {
		Document doc = textPane.getDocument();
		try {
			doc.insertString(doc.getLength(), "\n", attrSet);
		} catch (BadLocationException e) {
			System.out.println("BadLocationException:   " + e);
		}
	}

	public void append(String str, AttributeSet attrSet) {
		Document doc = textPane.getDocument();
		try {
			doc.insertString(doc.getLength(), str, attrSet);
		} catch (BadLocationException e) {
			System.out.println("BadLocationException:   " + e);
		}
	}

	public void insert(String str, AttributeSet attrSet) {
		Document doc = textPane.getDocument();
		str = "\n" + str;
		try {
			// doc.insertString(doc.getLength(), str, attrSet);
			doc.insertString(doc.getLength(), str, attrSet);
		} catch (BadLocationException e) {
			System.out.println("BadLocationException:   " + e);
		}
	}

	public void insert(LogBItem it) {
		Document doc = textPane.getDocument();
		String str = it.getMsg();

		str = "\n" + str;
		try {
			// doc.insertString(doc.getLength(), str, attrSet);
			doc.insertString(doc.getLength(), str, it.getSt());
		} catch (BadLocationException e) {
			System.out.println("BadLocationException:   " + e);
		}
	}

	public void append(LogBItem it) {
		Document doc = textPane.getDocument();
		String str = it.getMsg();
		try {
			doc.insertString(doc.getLength(), str, it.getSt());
		} catch (BadLocationException e) {
			System.out.println("BadLocationException:   " + e);
		}
	}

	/**
	 * 
	 */
	public void writeObjToFile(Object obj) {
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(this.file);
			ObjectOutputStream objout = new ObjectOutputStream(fout);
			objout.writeObject(obj);
			objout.close();
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Object addObjFromFile() {
		Object o = null;
		try {
			FileInputStream fin = new FileInputStream(this.file);
			ObjectInputStream objin = new ObjectInputStream(fin);
			objin.close();
			fin.close();
			o = objin.readObject().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return o;
	}
	public void setRole(RoleInstance role2) {
		this.role = role2;
		new LogBatJFrameRole(role2);
	}
	public void removeRole(RoleInstance role2) {
		LogBatJFrameRole.remove(role2);
	}
	
}