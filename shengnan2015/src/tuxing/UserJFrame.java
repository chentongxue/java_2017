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
		 * �²���
		 *
		GridBagLayout gridbag = new GridBagLayout();//���������
		JPanel panel = new JPanel(gridbag);         //������������
		GridBagConstraints cons = new GridBagConstraints();//��������ֵ�Լ������
		cons.fill = GridBagConstraints.BOTH;        //��ˮƽ�ʹ�ֱ�����ϵ��������С
		text_number = new JTextField("1");
		cons.gridwidth = 3;//ָ�������ȣ�ˮƽ������ռ������
		gridbag.setConstraints(text_number, cons);//Ϊ���ܱ�������Լ������
		panel.add(text_number);
		
		text_name = new JTextField("����");
		cons.gridwidth = GridBagConstraints.REMAINDER;//ָ�������������󣬿�ʶ����β
		 *******************/
		////////////////////////
		text_user=new JTextArea();
		
		this.getContentPane().add(text_user);
		this.getContentPane().add(new JScrollPane(text_user));//+++���������
		
		JPanel panel = new JPanel(new GridLayout(6,1));           //right half
		this.getContentPane().add(panel);
		
		text_number = new JTextField("1");
		text_number.setEditable(false);
		panel.add(text_number);                                  //1
		
		text_name = new JTextField("����3");
		panel.add(text_name);                                    //2
		
		JPanel panel_rb=new JPanel(new GridLayout(1,2));           //
		panel.add(panel_rb);                                     //3rd Line
		
		ButtonGroup bgroup = new ButtonGroup();               
		radiob_male = new JRadioButton("��",true);	           
		bgroup.add(radiob_male);
		panel_rb.add(radiob_male);
		
		radiob_female = new JRadioButton("Ů");
		bgroup.add(radiob_female);                                  //4
		panel_rb.add(radiob_female);
		
		//this.cities=cities;
		combox_province = new JComboBox(provinces);              //province
		combox_province.setEditable(false);
		combox_province.addActionListener(this);
		panel.add(combox_province);
		
		combox_city = new JComboBox(cities[0]);
		panel.add(combox_city);                                  //city
		
		button_add = new JButton("���");
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
			text_name.setText("����");
		}
	}

	public static void main(String args[])
	   {
		   Object provinces[]={"ɽ��ʡ","����ʡ","�ӱ�ʡ","ɽ��ʡ","����ʡ","����ʡ","������ʡ","�㽭ʡ","����ʡ","����ʡ","����ʡ","����ʡ","����ʡ","����ʡ","�㶫ʡ","����ʡ","�Ĵ�ʡ","����ʡ","����ʡ","����ʡ","����ʡ","�ຣʡ"};
		   Object cities[][]={{"��̨","�ൺ","����"},{"�Ͼ�","����","����"}};
		   new UserJFrame(provinces,cities);

	   }   
}
