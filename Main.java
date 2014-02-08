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
 
import java.io.*;
import javax.swing.*;

public class Main
{
    public static void main(String[] args) throws WavFileException, IOException 
    {
        Gui mygui = new Gui();
        mygui.setTitle("Morse Audio Decoder");
        mygui.setSize(300,200);
        mygui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mygui.setVisible(true);
        
        for (String cwfile: args) {
            System.out.println(cwfile);
            MorseProcessor ap = new MorseProcessor(cwfile);
            ap.process();
        }
                
    }
}
