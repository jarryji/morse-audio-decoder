
/**
 * Write a description of class Gui here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import javax.swing.*;

public class Gui extends JFrame
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1022040207410645701L;

	private JPanel panel;
    private JButton jbutton;
    private JLabel jlabel;

    /**
     * Constructor for objects of class Gui
     */
    public Gui()
    {
        this.panel = new JPanel();
        this.jbutton = new JButton("Analyze");
        this.jlabel = new JLabel();
        
        this.panel.add(jbutton);
        this.panel.add(jlabel);
        
        this.add(panel);
    }

}
