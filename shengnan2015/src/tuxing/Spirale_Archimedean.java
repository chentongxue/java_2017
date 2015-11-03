package tuxing;

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.*;
public class Spirale_Archimedean  extends Canvas implements ComponentListener
{
     /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color color;
	private int a;
	private int b;
     public Spirale_Archimedean(Color color,int a,int b)
     {
    	 this.a = a;
    	 this.b = b;
    	 this.color = color;
    	 this.addComponentListener(this);
     }
     public Spirale_Archimedean()
     {
    	 this.a = 0;
    	 this.b = 10;
    	 this.color = Color.black;
    	 this.addComponentListener(this);
     }
     public void setColor(Color color)
     {
    	 this.color=color;
     }
     public void setA(int a)
     {
    	 this.a=a;
     }
     public void setB(int b)
     {
    	 this.b=b;
     }
     public void paint(Graphics g)
     {
    	 int x0 = this.getWidth()/2;
    	 int y0 = this.getHeight()/2;
    	 
    	 g.setColor(color);
    	 g.drawLine(x0,0,x0,y0*2);
    	 g.drawLine(0, y0, x0*2, y0);
    	 
    		 for(int i=0;i<6*1024;i++)
    		 {	 
    			 double angel = i*Math.PI/1024;
    			 int x = (int)Math.round( ( a+b*(angel) ) * Math.cos(angel) );
    			 int y = (int)Math.round( ( a+b*(angel) ) * Math.sin(angel) );
    			 g.fillOval(x+x0,y+y0, 3, 3);
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
