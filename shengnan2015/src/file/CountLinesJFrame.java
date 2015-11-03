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
		private JFileChooser fchooser; //ѡ���ļ��Ի���
	    private JButton cho_button;  
	    public String filepath = null;
	    public String filename = null;
		public CountLinesJFrame() throws  InterruptedException, IOException 
	    {    
	    	super("��java��������");
	        this.setBounds(320,240,435,300);
	        this.setDefaultCloseOperation(3);
	        this.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT,10,5));
	        
	        this.fchooser = new JFileChooser(new File("",""));//�ļ��Ի���ĳ�ʼ·��Ϊ��ǰĿ¼
	        // this.fchooser.setFileFilter(new FileNameExtensionFilter(".","java"));
   
	        text_receiver = new JTextArea(10,36);
	        this.getContentPane().add(new JScrollPane(text_receiver));

	        cho_button = new JButton("ѡ���ļ�");
	        cho_button.addActionListener(this);	
	        this.getContentPane().add(cho_button);
	        
	        count_button = new JButton("��һ��");
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
		    {       //��ʾ�򿪶Ի���
		           fchooser.showOpenDialog(this);
		           File file = fchooser.getSelectedFile();
		           filepath =file.getAbsolutePath();  //����ļ��Ի���ĵ�ǡѡ���ļ�
		           filename = file.getName();
		           text_receiver.setText(filepath);
		     }
			 if(e.getSource()==count_button)
		     {
				System.out.println("�ļ���Ϊ"+this.filename);
				System.out.println("�ļ�·��Ϊ"+this.filepath);
				
				CountFile.initLinesCount();
				
				File dir = new File(text_receiver.getText());//�ļ�
				if(dir.isDirectory())//���ļ���
				{
					CountFile.countDirLines(text_receiver.getText());
					text_receiver.append("\nwww");
				}
				else {
					text_receiver.append("\ndd");
					//CountFile.countFileLines(text_receiver.getText());
				}
				text_receiver.append("\n");
				text_receiver.append(CountFile.maxlines+"������������\n");
				text_receiver.append(CountFile.minlines+"�������ع�����\n");
				text_receiver.append(CountFile.filecounts+"����java�ļ���Ŀ");
		     }
		}
}
