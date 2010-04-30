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

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.*;
import java.util.*;






/**
 *
 * @author brian
 */
public class ScopeDAQ {
    public BufferedReader in;
    public PrintStream out;

    public boolean connected;

    public Vector<String> getPorts()
    {
        Vector<String> ports = new Vector<String>();

        Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();

        while (portIdentifiers.hasMoreElements())
        {
            CommPortIdentifier pid = (CommPortIdentifier) portIdentifiers.nextElement();
            ports.add(pid.getName());
        }

        return ports;
    }

    public void connect(String portName) throws Exception
    {
        
        System.out.println("USING PORT: " + portName);

        CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);


        // Open the com port
        SerialPort serialPort = (SerialPort) portId.open("Test", 5000);

        // Set the parameters. Ensure that the baud is the same as specified in the Arduino code. (set to 9600 in this example)

        serialPort.setSerialPortParams(115200,
        SerialPort.DATABITS_8,
        SerialPort.STOPBITS_1,
        SerialPort.PARITY_NONE);

        System.out.println("A");

        // Disable flow control, it's not needed
        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

        System.out.println("B");

        // A stream for incoming data, & a stream for incoming
        OutputStream outStream = serialPort.getOutputStream();

        System.out.println("C");

        InputStream inStream = serialPort.getInputStream();

        System.out.println("D");

        in = new BufferedReader(new InputStreamReader(inStream));

        System.out.println("E");

        out = new PrintStream(outStream);

        System.out.println("F");

        //Do handshake

        //s for scope
        out.print("s");

        System.out.println("G");

        String s = in.readLine();


        System.out.println("H");

        if (s.contains("scopeduino version"))
        {
            //Clear the buffer
            System.out.println("FOUND: " + s);
        }
        else
        {
            System.err.println("Problem with Arduino Handshake");
        }

        connected = true;

        System.out.println("I");
    }

    public Vector<double[]> readTrace()
    {
        if (!connected)
        {
            return null;
        }

        //clear any buffered data from before
        try
        {
            while (in.ready())
            {
                System.out.println("DISCARDING: " + in.read());
            }
        }
        catch (Exception e)
        {
            System.err.println("While clearing buffered reader: " + e);
        }

        out.print("t");

        //Read the number of samples in the trace
        int size;


        //channel 1
        try
        {
            size = Integer.valueOf(in.readLine());
        }
        catch (Exception e)
        {
            System.err.println(e);
            connected = false;
            return null;
        }

        double[] trace1 = new double[size];

        try
        {
            for (int i = 0; i < size; i++)
            {
                trace1[i] = (double) Integer.valueOf(in.readLine()) / 512.0 - 1.0;
            }
        }
        catch (Exception e)
        {
            System.err.println(e);
            connected = false;
        }

        //channel 2
               try
        {
            size = Integer.valueOf(in.readLine());
        }
        catch (Exception e)
        {
            System.err.println(e);
            connected = false;
            return null;
        }

        double[] trace2 = new double[size];

        try
        {
            for (int i = 0; i < size; i++)
            {
                trace2[i] = (double) Integer.valueOf(in.readLine()) / 512.0 - 1.0;
            }
        }
        catch (Exception e)
        {
            System.err.println(e);
            connected = false;
        }

        Vector<double[]> traces = new Vector<double[]>(2);

        traces.add(trace1);
        traces.add(trace2);

        return traces;
    }
}
