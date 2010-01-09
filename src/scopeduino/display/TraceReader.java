/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scopeduino.display;
import java.util.*;
import serial.ScopeDAQ;
/**
 *
 * @author brian
 */
public class TraceReader extends Thread {

    Vector<Trace> traces;
    ScopeDAQ arduino;

    public TraceReader(Vector<Trace> t, ScopeDAQ a)
    {
        traces = t;
        arduino = a;
    }

    public void run()
    {
        while (arduino.connected)
        {
            double[] data = arduino.readTrace();

            Trace t = new Trace(data,0.2f, 0.2f, 1.0f, 0.5f,1);

            synchronized(traces)
            {
                traces.add(t);
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
