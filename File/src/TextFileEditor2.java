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
	private File file;                           //当前文件
	private JTextArea text;                      //文本区
	private JFileChooser fchooser;               //选择文件对话框
	private JComboBox combox_name,combox_size;   //@字体字号组合框
	private JCheckBox checkb_bold,checkb_italic; //粗体 斜体
	private JButton bopen,bsave; //粗体 斜体
	public TextFileEditor2()                      //空文件的构造方法
	{
		super("文本编辑器");
		this.setBounds(400, 300, 880, 320);
		this.setDefaultCloseOperation(3);
		this.text = new JTextArea();
		JScrollPane textpane = new JScrollPane(this.text);
		ImageIcon texticon = new ImageIcon("博士帽.jpg");
		Image textimage = texticon.getImage();
		
		//text.setFont(new Font("Consolas", 0, 25));
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
		menuitem[0].setIcon(new ImageIcon("新建文件夹\\resources\\new.gif"));
		menuitem[1].setIcon(new ImageIcon("新建文件夹\\resources\\open.gif"));
		menuitem[2].setIcon(new ImageIcon("新建文件夹\\resources\\save.gif"));			
		
		JToolBar toolbar = new JToolBar();                             //工具栏		
		toolbar.setPreferredSize(new Dimension(250,32));
		toolbar.setBackground(new Color(100, 100, 210));
		toolbar.setLayout(new FlowLayout(FlowLayout.LEFT,2,0));
		this.getContentPane().add(toolbar, "North");
		
		bopen = new JButton(new ImageIcon("新建文件夹\\resources\\open.gif"));//打开
		bopen.addActionListener(this);	
		bopen.setPreferredSize(new Dimension(40,30));
		toolbar.add(bopen);
		
		bsave = new JButton(new ImageIcon("新建文件夹\\resources\\save.gif"));//保存
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
        jpanel.add(new JLabel(new ImageIcon("新建文件夹\\resources\\bold.gif")));
        checkb_bold.addActionListener(this);
        
        //italic
        JPanel jpanel2 = new JPanel();
		jpanel2.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		jpanel2.setPreferredSize(new Dimension(41,30));
		toolbar.add(jpanel2);  
		
        checkb_italic = new JCheckBox();
        checkb_italic.setPreferredSize(new Dimension(17,30));
        jpanel2.add(checkb_italic);
        jpanel2.add(new JLabel(new ImageIcon("新建文件夹\\resources\\italic.gif")));
        checkb_italic.addActionListener(this);
        
        //
		this.setVisible(true);
		/**
		 * 布局完成
		 * **/
		this.file = null;                              //文件对象为空
		this.fchooser = new JFileChooser(new File(".",""));//文件对话框的初始路径为当前目录
		this.fchooser.setFileFilter(new FileNameExtensionFilter("文本文件(*.txt)","txt"));
		              //设置文件过滤器
	}
	public TextFileEditor2(File file)                   //指定文件对象的构造方法
	{
		this();
		if(file != null)
		{
			this.file = file;
			this.text.setText( this.readFromFile() );  //读取文件的字符串并显示
			this.setTitle(this.file.getName());        //将文件名添加在文件标题上
		}
	}
	public TextFileEditor2(String filename)             //指定文件名的构造方法
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
			//FileReader fin = new FileReader( this.file, "UTF-8" );//创建字符输入流对象
			InputStreamReader in = new InputStreamReader(new FileInputStream(this.file), "UTF-8");
			BufferedReader br = new BufferedReader(in);
			String line = null;
			
			//FileReader fin = new FileReader( this.file );//创建字符输入流对象
			//lines = new char[(int)this.file.length()];
			//fin.read(lines);                             //读取字符流到字符数组
			//fin.close();
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
	{
		System.out.println(this.file);
		
		if(e.getActionCommand() == "新建")
		{
			this.file = null;
			this.setTitle("未命名");
			this.text.setText("");
		}
		else if(e.getActionCommand() == "打开"||e.getSource()==bopen)
		{       //显示打开对话框并 单击打开按钮
			fchooser.showOpenDialog(this);
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
		new TextFileEditor2("凉州词.txt");
	}
}