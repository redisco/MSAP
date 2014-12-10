package ua.com.msap;

import java.awt.EventQueue;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import ua.com.msap.gui.*;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class ProcessViewer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.OFF);
        /*
        try {
            System.setOut(new PrintStream(System.out, true, "Cp866"));
        } catch (UnsupportedEncodingException ex) {
            System.out.println("Not supported encoding 'Cp866'...");
        }
*/


        int msSleepPeriod = 0;
        if (args.length > 0) {
            msSleepPeriod = Integer.parseInt(args[0]);
        }
        String schemaName = "Schema.xml";
        if (args.length > 1) {
            schemaName = args[1];
        }
        String configName = "Config.properties";
        if (args.length > 2) {
            configName = args[2];
        }
        String readTagsName = "OpcReadTags.properties";
        if (args.length > 3) {
            readTagsName = args[3];
        }
        String writeTagsName = "OpcWriteTags.properties";
        if (args.length > 4) {
            writeTagsName = args[4];
        }
        final String uiConfigFileName = 
                args.length > 5 ? args[0] : "UIConfig.xml";
        

        try {
            final ProcessSimulator simulator = new ProcessSimulator(schemaName,
                    configName, readTagsName, writeTagsName, msSleepPeriod);
           // simulator.run();
        
        
         EventQueue.invokeLater(new Runnable()
         {
            @Override
            public void run()
            {
               ControlWindow frame = new ControlWindow(uiConfigFileName);
               frame.connectToSimulator(simulator);
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            }
         });
         
         } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
