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

public class MorseSignal
{
    public int length;
    public long frame_start, frame_end;
    public boolean isSignal, isSilence;

    public MorseSignal()
    {
        this.frame_start    = 0;
        this.frame_end      = 0;
        this.length         = 0;
        
        this.isSignal   = false;
        this.isSilence  = false;
    }

    public int length_ms()
    {
       return this.length * 10; 
    }
    
    public boolean isValidSignal()
    {
        if ( this.isSignal != false ) {
          return true;  
        }
        
        return false;
    }
}
