//Refresher.java
//Used to periodically refresh the OpenGL Display

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


import javax.media.opengl.GLCanvas;
import serial.ScopeDAQ;

public class Refresher extends Thread  {

    private GLCanvas canvas;

    public Refresher(GLCanvas c)
    {
        canvas = c;

    }

    public void run()
    {
        for (;;)
        {
            

            canvas.repaint();

            try
            {
                this.sleep(25);
            }
            catch (Exception e)
            {
                System.err.println(e);
            }
        }

    }
}
