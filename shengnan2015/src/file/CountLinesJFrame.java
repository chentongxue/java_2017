package file;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

public class CountLinesJFrame  extends JFrame implements ActionListener
{

	    private JButton count_button;              
	    private JTextArea text_receiver;
		private JFileChooser fchooser; //选择文件对话框
	    private JButton cho_button;  
	    public String filepath = null;
	    public String filename = null;
		public CountLinesJFrame() throws  InterruptedException, IOException 
	    {    
	    	super("数java代码行数");
	        this.setBounds(320,240,435,300);
	        this.setDefaultCloseOperation(3);
	        this.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT,10,5));
	        
	        this.fchooser = new JFileChooser(new File("",""));//文件对话框的初始路径为当前目录
	        // this.fchooser.setFileFilter(new FileNameExtensionFilter(".","java"));
   
	        text_receiver = new JTextArea(10,36);
	        this.getContentPane().add(new JScrollPane(text_receiver));

	        cho_button = new JButton("选择文件");
	        cho_button.addActionListener(this);	
	        this.getContentPane().add(cho_button);
	        
	        count_button = new JButton("数一数");
	        count_button.addActionListener(this);
	        this.getContentPane().add(count_button);
	        this.setVisible(true); 
	        
	    }
	    public static void main(String args[]) throws IOException, InterruptedException
		{
	    	new CountLinesJFrame();
		}
		public void actionPerformed(ActionEvent e)
		{
			if(e.getSource()==cho_button)
		    {       //显示打开对话框并
		           fchooser.showOpenDialog(this);
		           File file = fchooser.getSelectedFile();
		           filepath =file.getAbsolutePath();  //获得文件对话框的当恰选中文件
		           filename = file.getName();
		           text_receiver.setText(filepath);
		     }
			 if(e.getSource()==count_button)
		     {
				System.out.println("文件名为"+this.filename);
				System.out.println("文件路径为"+this.filepath);
				
				CountFile.initLinesCount();
				
				File dir = new File(text_receiver.getText());//文件
				if(dir.isDirectory())//是文件夹
				{
					CountFile.countDirLines(text_receiver.getText());
					text_receiver.append("\nwww");
				}
				else {
					text_receiver.append("\ndd");
					//CountFile.countFileLines(text_receiver.getText());
				}
				text_receiver.append("\n");
				text_receiver.append(CountFile.maxlines+"——最大估计行\n");
				text_receiver.append(CountFile.minlines+"——保守估计行\n");
				text_receiver.append(CountFile.filecounts+"——java文件数目");
		     }
		}
}
