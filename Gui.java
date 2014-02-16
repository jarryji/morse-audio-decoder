/**
 * MorseDecoder
 * 
 * This is a free software, you can do whatever you want
 * with this code. Credentials are appreciated.
 * 
 * Files whitout this header are under specified license
 * and/or limits.
 * 
 * This software can translate any morse code audio signal (.wav file)
 * into a human-readable text
 * 
 * Donation (Litecoin): LYmQC9AMcvZq8dxQNkUBxngF6B2S2gafyX
 * Donation (Bitcoin):  1GEf7b2FjfCVSyoUdovePhKXe5yncqNZs7
 * 
 * @author  Matteo Benetti, mathew.benetti@gmail.com
 * @version 2013-12-23
 */

import javax.swing.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class Gui extends JFrame
{
	private String filename;
	private static final long serialVersionUID = -1022040207410645701L;

    /**
     * Constructor for objects of class Gui
     */
	public Gui()
    {
    	final JPanel panel = new JPanel();
    	final JFileChooser fc = new JFileChooser();
    	final JButton btnDecode = new JButton("Decode");
    	
    	final JButton btnChooseFile = new JButton("Choose File");
    	final JLabel lblPath = new JLabel("path...");
    	final JEditorPane editorPane = new JEditorPane();
    	final JButton btnPlay = new JButton("Play");
    	final JButton btnSave = new JButton("Save");
    	
        getContentPane().setLayout(null);
        panel.setBounds(0, 0, 382, 278);
        getContentPane().add(panel);
        panel.setLayout(null);
            
        btnChooseFile.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		int returnVal = fc.showOpenDialog(Gui.this);
        		if (returnVal == JFileChooser.APPROVE_OPTION) {
        			filename = fc.getSelectedFile().getName();
        			lblPath.setText(filename);
        		}
        	}
        });
        
        btnChooseFile.setBounds(6, 28, 122, 29);
        panel.add(btnChooseFile);
        
        btnDecode.setBounds(241, 28, 122, 29);
        btnDecode.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mouseClicked(MouseEvent e) {
    			try {
					MorseProcessor m_proc = new MorseProcessor(filename);
					m_proc.process();
					editorPane.setText(m_proc.result());
				} catch (WavFileException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(panel, e1.getMessage(), "Decode Error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(panel, e1.getMessage(), "File", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
    		}
    	});
        panel.add(btnDecode);       
        
        lblPath.setBounds(140, 33, 137, 16);
        panel.add(lblPath);        
        
        editorPane.setBounds(16, 69, 347, 76);
        panel.add(editorPane);       
        
        btnPlay.setBounds(11, 157, 117, 29);
        //panel.add(btnPlay);       
        
        btnSave.setBounds(246, 157, 117, 29);
        //panel.add(btnSave);
    }
}
