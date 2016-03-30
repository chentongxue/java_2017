package baoutil;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import lombok.Data;

import org.aspectj.lang.annotation.DeclareAnnotation;

import com.game.draco.app.vip.VipAppImpl;

public @Data
class LogBatJFrame extends JFrame implements ActionListener {
	private JPanel panel;
	private JTextPane textPane;
	private JButton open_button;
	private JButton[] buttons = new JButton[5];
	private JTextField jTextField;
	public VipAppImpl VipAppImpl;
	public int roleId = 0;
	private String file = "C:\\Users\\mofun030601\\Desktop\\备份\\o_file";

	public LogBatJFrame(String name) {
		super("(" + name + ")");
		this.setBounds(200, 100, 1100, 700);
		this.setDefaultCloseOperation(3);

		panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.LIGHT_GRAY);

		// font = new Font("Consolas", 0, 25);

		textPane = new JTextPane();

		// textPane.setFont(font);
		textPane.setBackground(Color.DARK_GRAY);

		Container ct = this.getContentPane();
		ct.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));

		panel.add(new JScrollPane(textPane));
		open_button = new JButton("clear");
		open_button.addActionListener(this);
		open_button.setPreferredSize(new Dimension(100, 25));
		open_button.setContentAreaFilled(false);

		for (int i = 0; i < 5; i++) {
			buttons[i] = new JButton("button" + i);
			buttons[i].addActionListener(this);
			buttons[i].setPreferredSize(new Dimension(100, 25));
			buttons[i].setContentAreaFilled(false);
			ct.add(buttons[i], "North");
		}

		jTextField = new JTextField();
		jTextField.setPreferredSize(new Dimension(100, 25));

		ct.add(open_button, "North");
		ct.add(jTextField, "North");

		panel.setPreferredSize(new Dimension(1016, 600));
		ct.add(panel, BorderLayout.CENTER);

		this.setVisible(true);
		
		File f = new File(file);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == open_button) {
			System.out.println("JJ");
			textPane.setText("");
		}
		if (e.getSource() == buttons[0]) {
			System.out.println("yy");
			int n = Integer.parseInt(jTextField.getText());
			VipAppImpl.addDiamands(roleId, n);
		}

	}

	public void setDocs(String str, Color col, Color b_col, Boolean bold,
			Integer fontSize) {
		SimpleAttributeSet attrSet = new SimpleAttributeSet();

		if (col == null)
			col = Color.green;
		StyleConstants.setForeground(attrSet, col);
		if (bold == null)
			bold = false;
		StyleConstants.setBold(attrSet, bold);

		if (b_col != null)
			StyleConstants.setBackground(attrSet, b_col);
		// StyleConstants.setItalic(attrSet, true);

		if (fontSize == null)
			fontSize = 15;
		StyleConstants.setFontSize(attrSet, fontSize);
		StyleConstants.setFontFamily(attrSet, "Consolas");
		insert(str, attrSet);
	}

	public void insert(String str, AttributeSet attrSet) {
		Document doc = textPane.getDocument();
		str = "\n" + str;
		try {
			// doc.insertString(doc.getLength(), str, attrSet);
			doc.insertString(doc.getLength(), str, attrSet);
		} catch (BadLocationException e) {
			System.out.println("BadLocationException:   " + e);
		}
	}

	/**
	 * 
	 */
	public void writeObjToFile(Object obj) {
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(this.file);
			ObjectOutputStream objout = new ObjectOutputStream(fout);
			objout.writeObject(obj);
			objout.close();
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Object addObjFromFile() {
		Object o = null;
		try {
		FileInputStream fin = new FileInputStream(this.file);
		ObjectInputStream objin = new ObjectInputStream(fin);
		objin.close();
		fin.close();
		o = objin.readObject().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return o;
	}
}