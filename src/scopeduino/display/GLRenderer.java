/**
 * GLRenderer.java
 *
 * Based on (GPL) example code provided with NetBeans OpenGL plugins
 * Credits: Brian Paul (converted to Java by Ron Cemer and Sven Goethel)
 * http://plugins.netbeans.org/PluginPortal/faces/PluginDetailPage.jsp?pluginid=3260
 *
 * Modifications: Brian Satzinger 2009-2010
 */

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

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import java.util.*;
import serial.ScopeDAQ;

public class GLRenderer implements GLEventListener {

    Vector<Trace> traces = new Vector<Trace>();
    
    float xangle;
    float yangle;
    float zangle;
    public boolean rotate = false;

    float tranx = 0.0f;
    float trany = 0.0f;
    float tranz = 0.0f;

    ScopeDAQ arduino;
    TraceReader reader;

    public void translate(int x, int y, int z)
    {
        //Scale the x and y translation roughly based on z
        float scale;
        
        if (tranz > 2.0f)
        {
            scale = 800.0f;
        }
        else if (tranz > 1.0f)
        {
            scale = 1000.0f;
        }
        else
        {
            scale = 1200.0f;
        }

        tranx += (float) x / scale;
        trany += (float) y / scale;
        tranz += (float) z / 10.0f;


        //Bound the translation
        if (tranx > 2.0f)
        {
            tranx = 2.0f;
        }
        if (tranx < -2.0f)
        {
            tranx = -2.0f;
        }

        if (trany > 2.0f)
        {
            trany = 2.0f;
        }
        if (trany < -2.0f)
        {
            trany = -2.0f;
        }

        if (tranz > 2.4f)
        {
            tranz = 2.4f;
        }
        if (tranz < 0.0f)
        {
            tranz = 0.0f;
        }
    }


    public void startTraceReader()
    {
        reader = new TraceReader(traces, arduino);
        reader.start();
    }

    public void init(GLAutoDrawable drawable) {
        // Use debug pipeline
        // drawable.setGL(new DebugGL(drawable.getGL()));


        //connect with the arduino
        arduino = new ScopeDAQ();

       


        GL gl = drawable.getGL();
        System.err.println("INIT GL IS: " + gl.getClass().getName());

        // Enable VSync
        gl.setSwapInterval(1);

        // Setup the drawing area and shading mode
        gl.glClearColor(ScopeSettings.backr, ScopeSettings.backg, ScopeSettings.backb, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.
        gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glEnable(GL.GL_POLYGON_SMOOTH);
        gl.glBlendFunc(gl.GL_SRC_ALPHA,gl.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL.GL_BLEND);
        //gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
        //gl.glHint(GL.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);

        xangle = 0;
        yangle = 0;
        zangle = 0;

        ScopeSettings.z = -2.5f;
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        GLU glu = new GLU();

        if (height <= 0) { // avoid a divide by zero error!
        
            height = 1;
        }
        final float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, h, 0.01, 20.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();

        gl.glClearColor(ScopeSettings.backr, ScopeSettings.backg, ScopeSettings.backb, 0.0f);

        // Clear the drawing area
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        // Reset the current matrix to the "identity"
        gl.glLoadIdentity();

        // Move the "drawing cursor" around
        gl.glTranslatef(0.0f, 0.0f, ScopeSettings.z);
        
        gl.glRotatef(xangle,1.0f,0.0f,0.0f);
        gl.glRotatef(yangle,0.0f,1.0f,0.0f);
        gl.glRotatef(zangle,0.0f,0.0f,1.0f);

        //Update the angle
        if (rotate)
        {
           
            
            xangle += .6 ;

            if (xangle > 360)
            {
                xangle = 0;
            }

            

            yangle += .4 ;

            if (yangle > 360)
            {
                yangle = 0;
            }

             

            zangle += .2 ;

            if (zangle > 360)
            {
                zangle = 0;
            }
        }

        //Do mouse translation

        gl.glTranslatef(tranx, trany, tranz);


        

        //Display each trace
        synchronized(traces)
        {
            //Sort the traces so they are drawn in the proper order
            Collections.sort(traces);

            Vector<Trace> expired = new Vector<Trace>();
            for(Trace t : traces)
            {
                drawGraph(gl, t);
                t.addAge();

                if (t.age > t.TTL)
                {
                    expired.add(t);
                }
            }

            //Remove the expired elements
            traces.removeAll(expired);
        }

        




        // Flush all drawing operations to the graphics card

        drawGrid(gl);
        drawAxes(gl);

        //gl.glRotatef(-90.0f,1.0f,0.0f,0.0f);

        //drawGrid(gl);
        //drawAxes(gl);

        gl.glFlush();
    }

    public void drawAxes(GL gl)
    {
       gl.glLineWidth(1.0f);
        gl.glBegin(GL.GL_LINES);
            gl.glColor3f(0.0f,1.0f,0.0f);

            //Y axis
            gl.glVertex2f(-1.0f,-1.0f);
            gl.glVertex2f(-1.0f,1.0f);

            //X Axis
            gl.glVertex2f(-1.0f,0.0f);
            gl.glVertex2f(1.0f,0.0f);
        gl.glEnd();

    }

    public void drawGrid(GL gl)
    {
        gl.glLineWidth(1.0f);
        gl.glBegin(GL.GL_LINES);
            gl.glColor3f(0.0f,0.2f,0.0f);

            for (double x = -1.0; x < 1.05; x += .25)
            {
                gl.glVertex2f((float)x,-1.0f);
                gl.glVertex2f((float)x, 1.0f);

                gl.glVertex2f(-1.0f,(float)x);
                gl.glVertex2f(1.0f,(float)x);
            }
        gl.glEnd();
    }

    public void drawGraph(GL gl, Trace t)
    {
        float[] rgb = t.agedColor();

        gl.glPushMatrix();

        drawGraph(gl, t.data, rgb[0], rgb[1], rgb[2], t.agedAlpha(), t.agedWidth(), t.xrotate);

        gl.glPopMatrix();
    }

    public void drawGraph(GL gl, double[] data, float r, float g, float b, float a, float w, float xrotate)
    {
        //gl object, data, red, green, blue, alpha, width
        gl.glPushMatrix();
        
        gl.glTranslatef(0.0f,0.0f,0.01f);

        gl.glRotatef(xrotate,1.0f,0.0f,0.0f);

        gl.glLineWidth(w);
        gl.glBegin(GL.GL_LINE_STRIP);
            gl.glColor4f(r,g,b,a);
            //gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
            for(int i = 0; i < data.length - 1; i++)
            {
                float x1 = indexToCoord(i, data.length);
                //float x2 = indexToCoord(i+1, data.length);

                gl.glVertex2f(x1, (float) data[i]);
                //gl.glVertex2f(x2, (float) data[i+1]);
            }
        gl.glEnd();

        gl.glPopMatrix();

    }

    public double[] sin(float a, float o)
    {
        int size = 256;
        double[] rtn = new double[size];

        Random rng = new Random();
        double amp =  rng.nextGaussian() / 25;
        double offset =  rng.nextGaussian();

        //Randomly invert the sin wave some times
        double pinvert = rng.nextDouble();
        int invert = 1;;

        if (pinvert < .05)
        {
            invert = -1;
        }


        for (int i = 0; i < rtn.length; i++)
        {
            double jitter = (rng.nextGaussian())*.01;
            rtn[i] = jitter + ScopeSettings.amp * a * invert * (1 - amp) * Math.sin((double) (i - offset - o) / 25);


            /*double dCap = rng.nextGaussian() * .01;
            if (rtn[i] > 0.5 + dCap)
            {
                rtn[i] = 0.5 + dCap;
            }*/

            

        }

        return rtn;
    }

    public double[] signum(double[] input)
    {
        double[] rtn = new double[input.length];

        for (int i = 0; i < input.length; i++)
        {
            if (input[i] > 0.0)
            {
                rtn[i] = .8;
            }
            else if (input[i] == 0.0)
            {
                rtn[i] = 0.0;
            }
            else
            {
                rtn[i] = -.8;
            }
        }

        return rtn;
    }

    public float indexToCoord(int index, int max)
    {
        double d;

        d = ((double) index) / ((double) max);



        return (float) (2 * d - 1);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
}

