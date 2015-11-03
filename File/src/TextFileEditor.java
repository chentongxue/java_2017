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

public class TextFileEditor extends JFrame implements ActionListener
{
	private File file;                           //��ǰ�ļ�
	private JTextArea text;                      //�ı���
	private JFileChooser fchooser;               //ѡ���ļ��Ի���
	public TextFileEditor()                      //���ļ��Ĺ��췽��
	{
		super("�ı��༭��");
		this.setBounds(400, 300, 480, 320);
		this.setDefaultCloseOperation(3);
		this.text = new JTextArea();
		JScrollPane textpane = new JScrollPane(this.text);
		ImageIcon texticon = new ImageIcon("��ʿñ.jpg");
		Image textimage = texticon.getImage();
		
		//text.setFont(new Font("Consolas", 0, 25));//������
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
		menuitem[0].setIcon(new ImageIcon("��ɽ.jpg"));
		menuitem[1].setIcon(new ImageIcon("����.jpg"));
		menuitem[2].setIcon(new ImageIcon("�½��ļ���\\cloud_rain.gif"));			
		
		JToolBar toolbar = new JToolBar();                             //������		
		toolbar.setPreferredSize(new Dimension(150,50));               //��������С
		toolbar.setBackground(new Color(100, 100, 210));
		toolbar.setLayout(new FlowLayout(FlowLayout.LEFT,2,0));
		this.getContentPane().add(toolbar, "North");
		
		JButton bopen = new JButton("��",new ImageIcon("��ɽ.jpg"));//��
		bopen.addActionListener(this);	
		bopen.setPreferredSize(new Dimension(90,48));
		toolbar.add(bopen);
		
		JButton bsave = new JButton("����",new ImageIcon("����.jpg"));//����
		bsave.addActionListener(this);
		bsave.setPreferredSize(new Dimension(90,48));
		toolbar.add(bsave);
	
		this.setVisible(true);
		/**
		 * �������
		 * **/
		this.file = null;                              //�ļ�����Ϊ��
		this.fchooser = new JFileChooser(new File(".",""));//�ļ��Ի���ĳ�ʼ·��Ϊ��ǰĿ¼
		this.fchooser.setFileFilter(new FileNameExtensionFilter("�ı��ļ�(*.txt)","txt"));
		              //�����ļ�������
	}
	public TextFileEditor(File file)                   //ָ���ļ�����Ĺ��췽��
	{
		this();
		if(file != null)
		{
			this.file = file;
			this.text.setText( this.readFromFile() );  //��ȡ�ļ����ַ�������ʾ
			this.setTitle(this.file.getName());        //���ļ���������ļ�������
		}
	}
	public TextFileEditor(String filename)             //ָ���ļ����Ĺ��췽��
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
			FileReader fin = new FileReader( this.file );//�����ַ�����������
			lines = new char[(int)this.file.length()];
			fin.read(lines);                             //��ȡ�ַ������ַ�����
			fin.close();
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
	{System.out.println(this.file);
		if(e.getActionCommand() == "�½�")
		{
			this.file = null;
			this.setTitle("δ����");
			this.text.setText("");
		}
		else if(e.getActionCommand() == "��")
		{       //��ʾ�򿪶Ի��� �����򿪰�ť
			fchooser.showOpenDialog(this);           //this
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
	}
	public static void main(String args[])
	{
		new TextFileEditor("���ݴ�.txt");
	}
}