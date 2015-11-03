package tuxing;

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
public class RoseJFrame   extends JFrame implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RoseCanvas rose;
	private Spirale_Archimedean spirale;
	private JButton button_color;
	private JComboBox com_size;
	private JRadioButton rad_rose,rad_Archimedes;
	public RoseJFrame()
	{
		super("ColorJava");
		Dimension dim=getToolkit().getScreenSize();       //Get screen resolution
		this.setBounds(dim.width/4,dim.height/4,dim.width/2,dim.height);
		this.setDefaultCloseOperation(3);
		this.setAlwaysOnTop(true);
		this.getContentPane().setLayout(new BorderLayout());
		JPanel jpanel = new JPanel();                         //Drawing canvas
		this.getContentPane().add(jpanel,"North");             //1st
		
		button_color = new JButton("Select Color");
		button_color.addActionListener(this);                 //////
		jpanel.add(button_color);                              //JPanel<-button
		
		
        ///////////////////  plus   //////////////////////////////
		Object sizestr[]={"2","3","4","5","6"};
		com_size = new JComboBox(sizestr);
		com_size.setEditable(true);
		com_size.addActionListener(this);
		//com_size.setBackground(Color.DARK_GRAY);
		//com_size.setBorder(BorderFactory.createTitledBorder("HEllo"));
		
		jpanel.add(com_size);
		//this.getContentPane().add(com_size);
		
		ButtonGroup Style = new ButtonGroup();
		rad_rose = new JRadioButton("PolarRose",true);	
		rad_rose.addActionListener(this);
		Style.add(rad_rose);
		jpanel.add(rad_rose);
		
		rad_Archimedes = new JRadioButton("Archimede");
		rad_Archimedes.addActionListener(this);
		Style.add(rad_Archimedes);                                  //4
		jpanel.add(rad_Archimedes);
		///////////////////  plus   //////////////////////////////
		rose = new RoseCanvas();
		spirale = new Spirale_Archimedean();
		this.getContentPane().add(rose,"Center");              //getContentPane()<-canvas
		
		this.setVisible(true);
	}
	public void addRose()
	{
		this.getContentPane().remove(spirale);
		this.getContentPane().add(rose,"Center");
		com_size.setEnabled(true);
		//com_size.addActionListener(this);
		
		rose.repaint();
	}
	public void addSpirale()
	{   
		this.getContentPane().remove(rose);
		com_size.setEnabled(false);
		//com_size.removeActionListener(this);
		this.getContentPane().add(spirale,"Center"); 
		spirale.repaint();
		this.setVisible(true);                       //!!!!!!!!!!!!
	}
	public void actionPerformed(ActionEvent e)
	{
		System.out.println("???");
		if(e.getSource()instanceof JRadioButton)
		{
			if(e.getSource()==rad_rose)
			{
				addRose();
			}
			if(e.getSource()==rad_Archimedes)
			{
				addSpirale();
			}
		}
		if(e.getSource()instanceof JComboBox)
		{
			int size=0;
			try
			{
				size=Integer.parseInt((String)com_size.getSelectedItem() );
				if( size<1 )                     //size-2)4==0
					throw new Exception("ParaWrong");
			}
			catch(NumberFormatException nfe)
			{
				JOptionPane.showMessageDialog(this,com_size.getSelectedItem()+"  is a not an 'Integer'");
			}
			catch(Exception e2)
			{
				if(e2.getMessage()=="ParaWrong")
					JOptionPane.showMessageDialog(this,size+"  is a Wrong Para");
			}
			
			finally{}
			//com_size.insertItemAt(size,1);
		    rose.setPara(size);
		    rose.repaint();
		}
		if(e.getSource()instanceof JButton)
		{
		Color c = JColorChooser.showDialog(this, "Select Color", Color.BLUE);
		    if(rad_rose.isSelected())
		    {
		    	rose.setColor(c);
		    	rose.repaint();
		    }
		    else
		    {
		    	spirale.setColor(c);
		    	spirale.repaint();
		    }    
		}
	}
	public static void main(String args[])
	{
		new RoseJFrame();
	}
}

