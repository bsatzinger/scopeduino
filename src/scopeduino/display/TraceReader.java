/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scopeduino.display;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import serial.ScopeDAQ;
/**
 *
 * @author brian
 */
public class TraceReader extends Thread {

    Vector<Trace> traces;
    ScopeDAQ arduino;

    ConcurrentLinkedQueue<byte[]> commandQueue;

    public TraceReader(Vector<Trace> t, ScopeDAQ a)
    {
        traces = t;
        arduino = a;

        commandQueue = new ConcurrentLinkedQueue<byte[]>();
    }

    public void run()
    {
        //Used to calculate the trace rate
        int ntraces = 0;
        long time = System.currentTimeMillis();

        while (arduino.connected)
        {
            double[] data = arduino.readTrace();

            Trace t = new Trace(data,0.2f, 0.2f, 1.0f, 0.25f,1);

            //Calculate the current trace rate
            ntraces++;
            if (ntraces > 30)
            {
                double tracerate;
                double dT;

                dT = (System.currentTimeMillis() - time) / 1000.0;

                tracerate = 30 / dT;

                System.out.println("Trace Rate: " + tracerate + "traces/second. " + dT);

                ntraces = 0;
                time = System.currentTimeMillis();
            }

            synchronized(traces)
            {
                traces.add(t);
            }


            //Check for other queued commands
            while (!commandQueue.isEmpty())
            {
                byte[] command = commandQueue.poll();

                if (command != null)
                {
                    //send the command
                    for (int i = 0; i < command.length; i++)
                    {
                        arduino.out.write((byte) command[i]);
                        System.out.println("Sending " + (char) command[i] + " to arduino");
                    }
                }
            }


            try
            {
                this.sleep(1);
            }
            catch (Exception e)
            {
                System.err.println(e);
            }

        }
        System.err.println("TraceReader terminated because arduino disconnected");
    }

}
