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
import java.util.*;

public class MorseProcessor
{
    private WavFile wavFile;
    
    private int numChannels, //wav file audio channel number
                sampleRate, // rate of sampling
                framesRead,
                signal_medium_value,
                silence_medium_value;
    
    private double [] buffer;
    
    private String decodedString;
    
    public MorseProcessor(String filename) throws WavFileException, IOException
    {
        this.wavFile        = WavFile.openWavFile(new File(filename));
        this.numChannels    = this.wavFile.getNumChannels();
        this.sampleRate     = ((int) 44100 / 1000) * 10; // 44 frames per ms * 10 = 10ms sample length
        this.buffer         = new double[this.sampleRate * this.numChannels];
        
    }
    
    /**
    * Close the wav resource
    * 
    */ 
    public void close() throws WavFileException, IOException
    {
        this.wavFile.close();
    }

    /**
    * main method
    * 
    */     
    public void process() throws WavFileException, IOException
    {
    	
        // this is the dictionary needed to translate the dot/dash string into
        // a human readable string
        MorseDictionary md              = new MorseDictionary();
        
        // get the sequence of signals (dot, dash) from the wav file
        List<MorseSignal> signals_list  = this.getSignals();
        
        // translate the signals sequence into a string of dot and dash grouped by words
        List<String> cwstring               = this.finalize(signals_list);
        
        Iterator<String> cwstring_iterator  = cwstring.iterator();
        String cw_original      = "";
        String cw_translated    = "";
        
        while ( cwstring_iterator.hasNext() ) 
        {
            
            String cw_word = cwstring_iterator.next();
            
            if ( cw_word == "CSPACE" ) {
                
                cw_original     += " ";
                cw_translated   += "";
                
            } else if ( cw_word == "WSPACE" ) { // if the space is a word space is longer than normal
                
                cw_original     += "   ";
                cw_translated   += " ";
                
            } else {
                cw_original += cw_word; // original message
                cw_translated += md.translate(cw_word); // translated message
            }
             
        }
        
        System.out.println("CW MESSAGE: "    + cw_original);
        System.out.println("MESSAGE: "       + cw_translated);
        
        this.decodedString = cw_translated;
        
        this.close();
    }
    
    /**
    * manage the seeking of signal into the wav
    * 
    */ 
    public List<MorseSignal> getSignals() throws WavFileException, IOException
    {
        List<MorseSignal> signals = new ArrayList<MorseSignal>();
        
        // next couple of var are needed in order
        // to normalize the duration of signals/silence into
        // the stream
        boolean first_silence_flag  = false;
        boolean last_silence_flag   = false;
        
        while ( this.wavFile.getFramesRemaining() > 0 ) // test if we have reached the end of the file
        {
            MorseSignal next_signal = this.readNextSignal(); // read new signal
            
            if ( ! first_silence_flag && next_signal.isSilence ) {
                first_silence_flag = true;
                next_signal.length = 1;
                continue;
                
            }
            
            if ( ! last_silence_flag && this.wavFile.getFramesRemaining() <= 0 ) {
               last_silence_flag = true;
               
               next_signal.length = 50;
               //continue;
               
            }
            
            signals.add(next_signal);
        }
        
        return signals;
    }
    
    /**
    * read a signal from the wav stream
    * a signal is composed by a bounch of sample, each one's length is about 10ms
    * 
    */     
    public MorseSignal readNextSignal() throws WavFileException, IOException
    {
        MorseSignal signal  = new MorseSignal();
        signal.frame_start  = this.wavFile.getFrameAlreadyRead();
        
        float sample        = this.readSample(); // a positive value between 0 and 255
        
        signal.length++; //minimum signal length = 1 sample = 10ms
        
        if (sample > 0.01) { // if the sample value is greater than 0.01 it is a valid signal
            signal.isSignal = true; 
        } else {
            signal.isSilence = true; 
        }
        
        while ( this.wavFile.getFramesRemaining() > 0 ) {
            
            float tmp_sample = readSample(); // read the next one signal
            
            signal.length++; // total length increases thanks to the tmp_sample read
            
            // now check current type of signal and the next type of signal and
            // determine what to do
            if (    (signal.isSilence && tmp_sample > 0.01) // if we got a silence and now a signal
                ||  (signal.isSignal && tmp_sample <= 0.01) ) // if we got a signal and now a silence
            { 
               // signal changes read, delete last sample length from total length
               
               signal.length--;
               signal.frame_end = this.wavFile.getFrameAlreadyRead() - this.sampleRate;
               
               return signal;
            }
        }
        
        if ( this.wavFile.getFramesRemaining() <= 0 ) {
            signal.frame_end = this.wavFile.getFrameAlreadyRead();
        }
        
        return signal;
    }

    /**
    * read a sample of the wav file
    * 
    */ 
    private float readSample() throws WavFileException, IOException
    {
        if ( this.wavFile.getFramesRemaining() > 0 ) {
            
            List<Float> positiveSamples = new ArrayList<Float>(); // we want only positive value
            this.framesRead             = wavFile.readFrames(this.buffer, this.sampleRate);
            
            if ( this.framesRead != 0 ) {
                
                for ( int s = 0; s < this.framesRead * this.numChannels; s++)
                {
                    if ( this.buffer[s] > 0 ) {
                        positiveSamples.add((float) this.buffer[s]);
                    }
                }
                
                if ( positiveSamples.size() <= 0 ) {
                    //add at least one value
                    positiveSamples.add((float) 0);
                }
                
                return this.get_list_average(positiveSamples);
            }
        }
        
        return (float) 0;
    }
    
    /**
    * parse the signals sequence and build words
    * 
    */ 
    private List<String> finalize(List<MorseSignal> signals)
    {
        int signal_total_value = 0;                         
        int silence_total_value = 0;
        
        Iterator<MorseSignal> first_iterator    = signals.iterator(); 
        Iterator<MorseSignal> second_iterator   = signals.iterator();
        
        List<Integer> signals_length    = new ArrayList<Integer>();
        List<Integer> silences_length   = new ArrayList<Integer>();
        
        while ( first_iterator.hasNext() ) {
            
            MorseSignal signal = first_iterator.next();
            
            if ( ! signal.isSilence && ! this.seek(signals_length, (signal.length * 10)) ) {
                
                signals_length.add(signal.length*10);
                signal_total_value += signal.length*10;
                
            } else if ( signal.isSilence && ! this.seek(signals_length, (signal.length * 10)) ) {
                
                silences_length.add(signal.length*10);
                silence_total_value += signal.length*10;
            }
           
        }
        
        
        this.signal_medium_value    = (int) signal_total_value / signals_length.size();
        this.silence_medium_value   = (int) silence_total_value / silences_length.size();
        
        //System.out.println("signal med val: " + this.signal_medium_value);
        //System.out.println("silence med val: " + this.silence_medium_value);
       
        List<String> cwstring = new ArrayList<String>();
        String cw_word = "";
        
        while (second_iterator.hasNext()) {
           
            MorseSignal signal = second_iterator.next();
            
            if ( signal.isSignal ) {
                
                if ( signal.length_ms() < this.signal_medium_value ) { 
                    //System.out.println(".");
                    cw_word += "."; //dot
                    
                } else {   
                    //System.out.println("-");
                    cw_word += "-"; //dash
                }
            
            } else if ( signal.isSilence != false ) {
                //System.out.println("silence, length: " + signal.length_ms());
                // check if silence is a "long" silence beetween two words
                if ( signal.length_ms() >= (2 * this.silence_medium_value) ) {
                    
                    cwstring.add(cw_word);
                    cwstring.add("WSPACE");
                    cw_word = "";
                    continue;
                }
                
                // check if silence is a short silence between two signals
                if ( signal.length_ms() >= this.silence_medium_value ) {
                    
                    cwstring.add(cw_word);
                    cwstring.add("CSPACE");
                    cw_word = "";
                    continue;
                }
            }
            
        }
        
        return cwstring; // list of cw signals, '.' for dot and '-' for dash, grouped by words
    }
    
    /**
    * look fo
    * 
    */     
    public boolean seek(List<Integer> values, int value) 
    {
        Iterator<Integer> i = values.iterator();
        
        while ( i.hasNext() ) 
        {
            if ( i.next() == value ) {
                return true;
            }
        }
        
        return false;
        
    }
    
    
    private float get_list_average(List<Float> list)
    {
        float sum = 0;
        
        for ( float value:list ) {
            sum += value;
        }
        
        return ((float) (sum / list.size()));
    }
    
    public void displayInfo()
    {
        System.out.print(this.wavFile.getInfo());
    }
    
    public String result()
    {
    	return this.decodedString;
    }
    
    public String toString()
    {
        return this.wavFile.getInfo();
    }
}