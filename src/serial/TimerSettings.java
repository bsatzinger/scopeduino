/*Copyright (C) 2009,2010 Brian Satzinger

This file is part of Scopeduino.

    Scopeduino is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Scopeduino is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Scopeduino.  If not, see <http://www.gnu.org/licenses/>.
*/

package serial;

/**
 *
 * @author brian
 */
public class TimerSettings {
    public int prescale;
    public int startvalue;
    public static int clock = 16000000;
    public static int[] prescalevalues = {1, 8, 32, 64, 128, 256, 1024};

    public TimerSettings()
    {
        //Default sample rate is 10k (10009.77 Hz)
        //Prescale is 256
        //startvalue is 218

        //Timer counts from starvalue to 255 at 16MHz/prescale
        //Interrupt is caused with timer overflows


        prescale = 256;
        startvalue = 218;
    }

    public TimerSettings(int pre, int start)
    {
        prescale = pre;
        startvalue = start;
    }

    //Calculates the interrupt frequency from a prescale and timer start value
    public static double freq(int pre, int start)
    {
        double f = (clock / pre) * (256 - start)/256;

        return f;
    }

    //Finds the closest timer setting to the given frequency f
    public static TimerSettings findClosestTimerSetting(double targetf)
    {
        int p;
        int s;

        double closestF;
        int closestP;
        int closestS;

        //Give an initial value
        closestP = prescalevalues[0];
        closestS = 0;
        closestF = freq(closestP, closestS);

        //Search for the closest matching frequency
        for (p = 0; p < prescalevalues.length; p++)
        {
            for (s = 0; s < 256; s++)
            {
                double f = freq(prescalevalues[p], s);

                //Check if this is a better match
                if (Math.abs(targetf - f) < Math.abs(targetf - closestF))
                {
                    //It is.  Update the best match up to now
                    closestP = prescalevalues[p];
                    closestS = s;
                    closestF = f;
                }
            }
        }

        //closestP and closestS have the closest timer values to the desired
        //frequency.  Return a TimerSettings object with their values
        return new TimerSettings(closestP, closestS);
    }
}
