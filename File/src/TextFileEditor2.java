import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;

public class TextFileEditor2 extends JFrame implements ActionListener
{
	private File file;                           //��ǰ�ļ�
	private JTextArea text;                      //�ı���
	private JFileChooser fchooser;               //ѡ���ļ��Ի���
	private JComboBox combox_name,combox_size;   //@�����ֺ���Ͽ�
	private JCheckBox checkb_bold,checkb_italic; //���� б��
	private JButton bopen,bsave; //���� б��
	public TextFileEditor2()                      //���ļ��Ĺ��췽��
	{
		super("�ı��༭��");
		this.setBounds(400, 300, 880, 320);
		this.setDefaultCloseOperation(3);
		this.text = new JTextArea();
		JScrollPane textpane = new JScrollPane(this.text);
		ImageIcon texticon = new ImageIcon("��ʿñ.jpg");
		Image textimage = texticon.getImage();
		
		//text.setFont(new Font("Consolas", 0, 25));
		this.getContentPane().add(textpane);
		
		
		JMenuBar menubar = new JMenuBar();
		this.setJMenuBar(menubar);
		
		String menustr[] = {"�ļ�", "�༭", "����", "��ʽ", "����", "����"};
		JMenu menu[] = new JMenu[menustr.length];	
		for(int i = 0; i < menu.length; i++)       //�˵�����Ӳ˵���
		{
			menu[i] = new JMenu(menustr[i]);
			menubar.add(menu[i]);
		}
		
		String menuitemstr[] = {"�½�", "��", "����", "���Ϊ"};
		JMenuItem menuitem[] = new JMenuItem[menuitemstr.length];
		for(int i = 0; i < menuitem.length; i++)
		{
			menuitem[i] = new JMenuItem(menuitemstr[i]);
			menuitem[i].addActionListener(this);
			menu[0].add(menuitem[i]);
		}
		//�˵���ͼ��
		menuitem[0].setIcon(new ImageIcon("�½��ļ���\\resources\\new.gif"));
		menuitem[1].setIcon(new ImageIcon("�½��ļ���\\resources\\open.gif"));
		menuitem[2].setIcon(new ImageIcon("�½��ļ���\\resources\\save.gif"));			
		
		JToolBar toolbar = new JToolBar();                             //������		
		toolbar.setPreferredSize(new Dimension(250,32));
		toolbar.setBackground(new Color(100, 100, 210));
		toolbar.setLayout(new FlowLayout(FlowLayout.LEFT,2,0));
		this.getContentPane().add(toolbar, "North");
		
		bopen = new JButton(new ImageIcon("�½��ļ���\\resources\\open.gif"));//��
		bopen.addActionListener(this);	
		bopen.setPreferredSize(new Dimension(40,30));
		toolbar.add(bopen);
		
		bsave = new JButton(new ImageIcon("�½��ļ���\\resources\\save.gif"));//����
		bsave.addActionListener(this);
		bsave.setPreferredSize(new Dimension(40,30));
		toolbar.add(bsave);
		//font         
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[]fontsName=ge.getAvailableFontFamilyNames();
		combox_name = new JComboBox(fontsName);
		combox_name.addActionListener(this);
		combox_name.setPreferredSize(new Dimension(200,30));
		toolbar.add(combox_name);
		
        String sizestr[]={"20","30","40","50","60"};
        combox_size = new JComboBox(sizestr);
        combox_size.setEditable(true);
        combox_size.addActionListener(this);
        combox_size.setPreferredSize(new Dimension(50,30));
        toolbar.add(combox_size);
		//bold 

        JPanel jpanel = new JPanel();
		jpanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		jpanel.setPreferredSize(new Dimension(41,30));
		toolbar.add(jpanel);  
		
        checkb_bold = new JCheckBox();
        checkb_bold.setPreferredSize(new Dimension(17,30));
        jpanel.add(checkb_bold);
        jpanel.add(new JLabel(new ImageIcon("�½��ļ���\\resources\\bold.gif")));
        checkb_bold.addActionListener(this);
        
        //italic
        JPanel jpanel2 = new JPanel();
		jpanel2.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		jpanel2.setPreferredSize(new Dimension(41,30));
		toolbar.add(jpanel2);  
		
        checkb_italic = new JCheckBox();
        checkb_italic.setPreferredSize(new Dimension(17,30));
        jpanel2.add(checkb_italic);
        jpanel2.add(new JLabel(new ImageIcon("�½��ļ���\\resources\\italic.gif")));
        checkb_italic.addActionListener(this);
        
        //
		this.setVisible(true);
		/**
		 * �������
		 * **/
		this.file = null;                              //�ļ�����Ϊ��
		this.fchooser = new JFileChooser(new File(".",""));//�ļ��Ի���ĳ�ʼ·��Ϊ��ǰĿ¼
		this.fchooser.setFileFilter(new FileNameExtensionFilter("�ı��ļ�(*.txt)","txt"));
		              //�����ļ�������
	}
	public TextFileEditor2(File file)                   //ָ���ļ�����Ĺ��췽��
	{
		this();
		if(file != null)
		{
			this.file = file;
			this.text.setText( this.readFromFile() );  //��ȡ�ļ����ַ�������ʾ
			this.setTitle(this.file.getName());        //���ļ���������ļ�������
		}
	}
	public TextFileEditor2(String filename)             //ָ���ļ����Ĺ��췽��
	{
		this(new File(filename));	                   //��filename==null�׳��ն����쳣
	}	
	public void writeToFile( String lines )            //���ַ���linesд�뵱ǰ�ı��ļ���
	{
		try
		{
			FileWriter fout = new FileWriter(this.file);//�ļ��ַ�������
			fout.write(lines + "\r\n");                 //���ļ��ַ���д��һ���ַ���
			fout.close();
		}
		catch(IOException ioex)
		{
			JOptionPane.showMessageDialog(this, "IO��д��" + file.getName() + "�ļ����ɹ�");
		}
	}
	public String readFromFile()            //??????             //ʹ�����ӵ�ǰ�ı��ļ��ж�ȡ�ַ�
	{
		System.out.println("read");
		char lines[] = null;
		try
		{
			//FileReader fin = new FileReader( this.file, "UTF-8" );//�����ַ�����������
			InputStreamReader in = new InputStreamReader(new FileInputStream(this.file), "UTF-8");
			BufferedReader br = new BufferedReader(in);
			String line = null;
			
			//FileReader fin = new FileReader( this.file );//�����ַ�����������
			//lines = new char[(int)this.file.length()];
			//fin.read(lines);                             //��ȡ�ַ������ַ�����
			//fin.close();
		}
		catch(FileNotFoundException fe)
		{
			JOptionPane.showMessageDialog(this, "\"" + this.file.getName() + "\"�ļ�������");
		}
		catch(IOException ioex)
		{
			JOptionPane.showMessageDialog(this, "IO����ȡ" + file.getName() + "�ļ����ɹ�");
		}
		finally
		{
			return new String(lines);
		}
	}
	@Override
	public void actionPerformed(ActionEvent e)
	{
		System.out.println(this.file);
		
		if(e.getActionCommand() == "�½�")
		{
			this.file = null;
			this.setTitle("δ����");
			this.text.setText("");
		}
		else if(e.getActionCommand() == "��"||e.getSource()==bopen)
		{       //��ʾ�򿪶Ի��� �����򿪰�ť
			fchooser.showOpenDialog(this);
			this.file = fchooser.getSelectedFile();  //����ļ��Ի���ĵ�ǡѡ���ļ�
			this.setTitle(this.file.getName());
			this.text.setText(this.readFromFile());
		}
		else if(e.getActionCommand() == "����"&&this.file!=null)
			this.writeToFile(this.text.getText());
		else if( (e.getActionCommand()=="����"&&file==null||e.getActionCommand()=="���Ϊ")
			                       )
		{   
			fchooser.showSaveDialog(this);                       //������ļ���ִ�����Ϊ�˵�ʱ ��ʾ�����ļ��Ի����ҵ������水ť	
			this.file=fchooser.getSelectedFile();                //����ļ��Ի���ĵ�ǡѡ���ļ�
			if(!this.file.getName().endsWith(".txt"))
				this.file = new File(file.getAbsolutePath()+".txt"); //����ļ���չ��

			writeToFile(this.text.getText());
			setTitle(this.file.getName());
		}	
		///
		//
		
		if(e.getSource()instanceof JComboBox || e.getSource()instanceof JCheckBox )
		{
			int size=0;
			try
			{
				System.out.println("!");
				String fontname = (String)combox_name.getSelectedItem();
				size=Integer.parseInt((String)combox_size.getSelectedItem() );
				if(size<4||size>120)
					throw new Exception("SizeException");
				java.awt.Font font=text.getFont();
				int style=font.getStyle();
				if(e.getSource()==checkb_bold)
					style=style^1;                           //XOR
				if(e.getSource()==checkb_italic)
					style=style^2;
				text.setFont(new Font(fontname,style,size) );
				
			}
			catch(Exception e2)
			{
				if(e2.getMessage()=="SizeException")
					JOptionPane.showMessageDialog(this,size+"Wrong Font Size");
			}
			finally{}
		}
		if(e.getSource()==combox_size)
		{
			String size = (String)combox_size.getSelectedItem();
			int i=0,n=combox_size.getItemCount();
			while(i<n&&size.compareTo((String)combox_size.getItemAt(i))>=0)
			{
				if( size.compareTo((String)combox_size.getItemAt(i))==0 )
					return;
				i++;
				System.out.println("?"+i);
			}
			combox_size.insertItemAt(size,i);
		}
	}
	public static void main(String args[])
	{
		new TextFileEditor2("���ݴ�.txt");
	}
}