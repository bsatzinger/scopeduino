//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License as
//published by the Free Software Foundation; either version 3 of the
//License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful, but
//WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//General Public License for more details:
//http://www.gnu.org/licenses/gpl.txt

//Copyright 2009 Brian Satzinger

package scopeduino.display;


public class Trace implements Comparable{
    public static int TTL = 150;

    public int age;
    public double[] data;
    public float r,g,b;
    public float w;
    public int input;

    public int xrotate;

    public Trace(double[] newdata, float newr, float newg, float newb, float neww, int newinput)
    {
        data = newdata;
        age = 1;
        r = newr;
        g = newg;
        b = newb;
        w = neww;
        input = newinput;
        xrotate = 0;
    }

    //Return an array containing the color for this graph
    //The color is a function of age, and gradually fades to black
    public float[] agedColor()
    {
        float[] rtn = new float[3];

        //Give first 2 traces a white color
        if (age <= 1 && ScopeSettings.hardFirstTrace)
        {
            rtn[0] = 1.0f - ScopeSettings.backr;
            rtn[1] = 1.0f - ScopeSettings.backg;
            rtn[2] = 1.0f - ScopeSettings.backb;

            return rtn;
        }

        //Subsequent traces assume the trace's specified color
        //Which fades to black over time

        rtn[0] = r;//; * ((float) (TTL - age)) / ((float) TTL);// * (float) Math.exp(0 - age);
        rtn[1] = g;// * ((float) (TTL - age)) / ((float) TTL);// * (float) Math.exp(0 - age);
        rtn[2] = b;// * ((float) (TTL - age)) / ((float) TTL);// * (float) Math.exp(0 - age);




        return rtn;
    }

    public float agedWidth()
    {
        if (age <= 1)
        {
            return 2;
        }

        return w * age ;
    }

    public float agedAlpha()
    {
        return (0.25f) * (1 - (((float) age) / ((float) TTL)));
    }

    public void addAge()
    {
        age++;
    }

    //Used to sort traces by which input they correspond to and by age
    public int compareTo(Object o)
    {
        if (o instanceof Trace)
        {
            Trace t = (Trace) o;

            if (t.input < this.input)
            {
                return -1;
            }
            else if (t.input > this.input)
            {
                return 1;
            }
            else
            {
                if (t.age < this.age)
                {
                    return -1;
                }
                else if (t.age > this.age)
                {
                    return 1;
                }
                else
                {
                    return 0;
                }
            }
        }
        else
        {
            return -1;
        }
    }
}
