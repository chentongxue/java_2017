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
	private File file;                           //当前文件
	private JTextArea text;                      //文本区
	private JFileChooser fchooser;               //选择文件对话框
	public TextFileEditor()                      //空文件的构造方法
	{
		super("文本编辑器");
		this.setBounds(400, 300, 480, 320);
		this.setDefaultCloseOperation(3);
		this.text = new JTextArea();
		JScrollPane textpane = new JScrollPane(this.text);
		ImageIcon texticon = new ImageIcon("博士帽.jpg");
		Image textimage = texticon.getImage();
		
		//text.setFont(new Font("Consolas", 0, 25));//会乱码
		this.getContentPane().add(textpane);
		
		
		JMenuBar menubar = new JMenuBar();
		this.setJMenuBar(menubar);
		
		String menustr[] = {"文件", "编辑", "插入", "格式", "工具", "帮助"};
		JMenu menu[] = new JMenu[menustr.length];	
		for(int i = 0; i < menu.length; i++)       //菜单栏添加菜单项
		{
			menu[i] = new JMenu(menustr[i]);
			menubar.add(menu[i]);
		}
		
		String menuitemstr[] = {"新建", "打开", "保存", "另存为"};
		JMenuItem menuitem[] = new JMenuItem[menuitemstr.length];
		for(int i = 0; i < menuitem.length; i++)
		{
			menuitem[i] = new JMenuItem(menuitemstr[i]);
			menuitem[i].addActionListener(this);
			menu[0].add(menuitem[i]);
		}
		//菜单栏图标
		menuitem[0].setIcon(new ImageIcon("冰山.jpg"));
		menuitem[1].setIcon(new ImageIcon("蓝宫.jpg"));
		menuitem[2].setIcon(new ImageIcon("新建文件夹\\cloud_rain.gif"));			
		
		JToolBar toolbar = new JToolBar();                             //工具栏		
		toolbar.setPreferredSize(new Dimension(150,50));               //工具栏大小
		toolbar.setBackground(new Color(100, 100, 210));
		toolbar.setLayout(new FlowLayout(FlowLayout.LEFT,2,0));
		this.getContentPane().add(toolbar, "North");
		
		JButton bopen = new JButton("打开",new ImageIcon("冰山.jpg"));//打开
		bopen.addActionListener(this);	
		bopen.setPreferredSize(new Dimension(90,48));
		toolbar.add(bopen);
		
		JButton bsave = new JButton("保存",new ImageIcon("蓝宫.jpg"));//保存
		bsave.addActionListener(this);
		bsave.setPreferredSize(new Dimension(90,48));
		toolbar.add(bsave);
	
		this.setVisible(true);
		/**
		 * 布局完成
		 * **/
		this.file = null;                              //文件对象为空
		this.fchooser = new JFileChooser(new File(".",""));//文件对话框的初始路径为当前目录
		this.fchooser.setFileFilter(new FileNameExtensionFilter("文本文件(*.txt)","txt"));
		              //设置文件过滤器
	}
	public TextFileEditor(File file)                   //指定文件对象的构造方法
	{
		this();
		if(file != null)
		{
			this.file = file;
			this.text.setText( this.readFromFile() );  //读取文件的字符串并显示
			this.setTitle(this.file.getName());        //将文件名添加在文件标题上
		}
	}
	public TextFileEditor(String filename)             //指定文件名的构造方法
	{
		this(new File(filename));	                   //若filename==null抛出空对象异常
	}	
	public void writeToFile( String lines )            //将字符串lines写入当前文本文件中
	{
		try
		{
			FileWriter fout = new FileWriter(this.file);//文件字符流对象
			fout.write(lines + "\r\n");                 //向文件字符流写入一个字符串
			fout.close();
		}
		catch(IOException ioex)
		{
			JOptionPane.showMessageDialog(this, "IO错，写入" + file.getName() + "文件不成功");
		}
	}
	public String readFromFile()            //??????             //使用流从当前文本文件中读取字符
	{
		System.out.println("read");
		char lines[] = null;
		try
		{
			FileReader fin = new FileReader( this.file );//创建字符输入流对象
			lines = new char[(int)this.file.length()];
			fin.read(lines);                             //读取字符流到字符数组
			fin.close();
		}
		catch(FileNotFoundException fe)
		{
			JOptionPane.showMessageDialog(this, "\"" + this.file.getName() + "\"文件不存在");
		}
		catch(IOException ioex)
		{
			JOptionPane.showMessageDialog(this, "IO错，读取" + file.getName() + "文件不成功");
		}
		finally
		{
			return new String(lines);
		}
	}
	@Override
	public void actionPerformed(ActionEvent e)
	{System.out.println(this.file);
		if(e.getActionCommand() == "新建")
		{
			this.file = null;
			this.setTitle("未命名");
			this.text.setText("");
		}
		else if(e.getActionCommand() == "打开")
		{       //显示打开对话框并 单击打开按钮
			fchooser.showOpenDialog(this);           //this
			this.file = fchooser.getSelectedFile();  //获得文件对话框的当恰选中文件
			this.setTitle(this.file.getName());
			this.text.setText(this.readFromFile());
		}
		else if(e.getActionCommand() == "保存"&&this.file!=null)
			this.writeToFile(this.text.getText());
		else if( (e.getActionCommand()=="保存"&&file==null||e.getActionCommand()=="另存为")
			                       )
		{   
			fchooser.showSaveDialog(this);                       //保存空文件或执行另存为菜单时 显示保存文件对话框，且单击保存按钮	
			this.file=fchooser.getSelectedFile();                //获得文件对话框的当恰选中文件
			if(!this.file.getName().endsWith(".txt"))
				this.file = new File(file.getAbsolutePath()+".txt"); //添加文件扩展名

			writeToFile(this.text.getText());
			setTitle(this.file.getName());
		}		
	}
	public static void main(String args[])
	{
		new TextFileEditor("凉州词.txt");
	}
}