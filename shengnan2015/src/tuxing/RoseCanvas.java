package tuxing;

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.*;
public class RoseCanvas extends Canvas implements ComponentListener
{
	private Color color;
	private int Para;
     public RoseCanvas(Color color,int Para)
     {
    	 this.Para = Para;
    	 this.color = color;
    	 this.addComponentListener(this);
     }
     public RoseCanvas()
     {
    	 this.Para = 6;
    	 this.color = Color.black;
    	 this.addComponentListener(this);
     }
     public void setColor(Color color)
     {
    	 this.color=color;
     }
     public void setPara(int Pa)
     {
    	 this.Para=Pa;
     }
     public void paint(Graphics g)
     {
    	 int x0 = this.getWidth()/2;
    	 int y0 = this.getHeight()/2;
    	 
    	 g.setColor(color);
    	 g.drawLine(x0,0,x0,y0*2);
    	 g.drawLine(0, y0, x0*2, y0);
    	 int j=40;
    	 
    	 while(j<200)
    	 {
    		 for(int i=0;i<1000;i++)
    		 {	 
    			 double angel = i*Math.PI/512;
    			 int x = (int)Math.round( j*Math.sin(Para*angel) * Math.cos(angel) );
    			 int y = (int)Math.round( j*Math.sin(Para*angel) * Math.sin(angel) );
    			 g.fillOval(x+x0, y+y0, 2, 2);
    		 }
    		 j+=20;	 	 
    	 }
    	 
    		
     }
     public void componentResized(ComponentEvent e)
     {
    	 this.repaint();
     }
     public void componentMoved(ComponentEvent e){}
	 public void componentHidden(ComponentEvent e) {}
	 public void componentShown(ComponentEvent e) {}
     
}
