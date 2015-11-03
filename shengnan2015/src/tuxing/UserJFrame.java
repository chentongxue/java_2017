package tuxing;

import java.awt.*;
import javax.swing.*;

import java.awt.event.*;
public class UserJFrame extends JFrame implements ActionListener
{

	private int number=1;
	private JTextField text_number,text_name;
	private JRadioButton radiob_male,radiob_female;
	private Object[][] cities;
	private JComboBox combox_province,combox_city;
	private JButton button_add;
	private JTextArea text_user;
	
	public UserJFrame(Object provinces[],Object cities[][])
	{
		super("input the USER INFORMATION please");
		this.setBounds(300,240,360,200);
		this.setDefaultCloseOperation(3);
		this.getContentPane().setLayout(new GridLayout(1,2));
		/**********************
		 * 新布局
		 *
		GridBagLayout gridbag = new GridBagLayout();//网格包布局
		JPanel panel = new JPanel(gridbag);         //面板网格包布局
		GridBagConstraints cons = new GridBagConstraints();//网格包布局的约束条件
		cons.fill = GridBagConstraints.BOTH;        //在水平和垂直方向上调整组件大小
		text_number = new JTextField("1");
		cons.gridwidth = 3;//指定组件宽度，水平方向上占用三格
		gridbag.setConstraints(text_number, cons);//为我能本行设置约束条件
		panel.add(text_number);
		
		text_name = new JTextField("姓名");
		cons.gridwidth = GridBagConstraints.REMAINDER;//指定组件在行上最后，款识、行尾
		 *******************/
		////////////////////////
		text_user=new JTextArea();
		
		this.getContentPane().add(text_user);
		this.getContentPane().add(new JScrollPane(text_user));//+++必须放在这
		
		JPanel panel = new JPanel(new GridLayout(6,1));           //right half
		this.getContentPane().add(panel);
		
		text_number = new JTextField("1");
		text_number.setEditable(false);
		panel.add(text_number);                                  //1
		
		text_name = new JTextField("姓名3");
		panel.add(text_name);                                    //2
		
		JPanel panel_rb=new JPanel(new GridLayout(1,2));           //
		panel.add(panel_rb);                                     //3rd Line
		
		ButtonGroup bgroup = new ButtonGroup();               
		radiob_male = new JRadioButton("男",true);	           
		bgroup.add(radiob_male);
		panel_rb.add(radiob_male);
		
		radiob_female = new JRadioButton("女");
		bgroup.add(radiob_female);                                  //4
		panel_rb.add(radiob_female);
		
		//this.cities=cities;
		combox_province = new JComboBox(provinces);              //province
		combox_province.setEditable(false);
		combox_province.addActionListener(this);
		panel.add(combox_province);
		
		combox_city = new JComboBox(cities[0]);
		panel.add(combox_city);                                  //city
		
		button_add = new JButton("添加");
		button_add.addActionListener(this);                     //listen
		panel.add(button_add);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==combox_province)
		{
			int i=combox_province.getSelectedIndex();
			combox_city.removeAllItems();
			for(int j=0;j<this.cities[i].length;j++)
			{
				combox_city.addItem(cities[i][j]);
			}
		}
		if(e.getSource()==button_add)
		{
			String aline = number+","+text_name.getText();
			if(radiob_male.isSelected())
			    aline+=","+radiob_male.getText();
			if(radiob_female.isSelected())
			    aline+=","+radiob_female.getText();
			aline+=","+combox_province.getSelectedItem();
			text_user.append(aline+"\n");
			this.number++;
			text_number.setText(""+this.number);
			text_name.setText("姓名");
		}
	}

	public static void main(String args[])
	   {
		   Object provinces[]={"山东省","江苏省","河北省","山西省","辽宁省","吉林省","黑龙江省","浙江省","安徽省","福建省","江西省","河南省","湖北省","湖南省","广东省","海南省","四川省","贵州省","云南省","陕西省","甘肃省","青海省"};
		   Object cities[][]={{"烟台","青岛","济南"},{"南京","苏州","无锡"}};
		   new UserJFrame(provinces,cities);

	   }   
}
