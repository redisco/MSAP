package ua.com.msap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Properties;
import ua.com.msap.core.Link;
import ua.com.msap.core.Scheme;
import ua.com.msap.core.XmlSchemeReader;
import ua.com.msap.core.opc.OpcBridge;
import ua.com.msap.gui.components.ISchemeUIComponent;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class ProcessSimulator {

    private boolean running = false;
    private int sleepPeriodMs = 0;
    private String schemaFileName = "";
    private String configFileName = "";
    private String readTagsFileName = "";
    private String writeTagsFileName = "";
    private Properties config = new Properties();
    private Properties readTags = new Properties();
    private Properties writeTags = new Properties();
    private Scheme scheme = null;
    private OpcBridge opcBridge = null;
    private ArrayList<ISchemeUIComponent> uiComponents = 
            new ArrayList<ISchemeUIComponent>();
    private Thread simulationThread = null;
    
    public ProcessSimulator(String schemaFileName, String configFileName,
            String readTagsFileName, String writeTagsFileName,
            int sleepPeriodMs) throws Exception {
        if (sleepPeriodMs >= 0) {
            this.sleepPeriodMs = sleepPeriodMs;
        }
        if (schemaFileName != null && schemaFileName.length() != 0) {
            this.schemaFileName = schemaFileName;
        }
        if (configFileName != null && configFileName.length() != 0) {
            this.configFileName = configFileName;
        }
        if (readTagsFileName != null && readTagsFileName.length() != 0) {
            this.readTagsFileName = readTagsFileName;
        }
        if (writeTagsFileName != null && writeTagsFileName.length() != 0) {
            this.writeTagsFileName = writeTagsFileName;
        }
        try {
            FileInputStream configStream = new FileInputStream(
                    this.configFileName);
            this.config.load(configStream);
        } catch (FileNotFoundException ex) {
            String message = "Файл конфигурации '"
                    + this.configFileName
                    + "' не найден...";
            throw new FileNotFoundException(message);
        }
        try {
            FileInputStream readTagsStream = new FileInputStream(
                    this.readTagsFileName);
            this.readTags.load(readTagsStream);
        } catch (FileNotFoundException ex) {
            String message = "Файл конфигурации '"
                    + this.readTagsFileName
                    + "' не найден...";
            throw new FileNotFoundException(message);
        }
        try {
            FileInputStream writeTagsStream = new FileInputStream(
                    this.writeTagsFileName);
            this.writeTags.load(writeTagsStream);
        } catch (FileNotFoundException ex) {
            String message = "Файл конфигурации '"
                    + this.writeTagsFileName
                    + "' не найден...";
            throw new FileNotFoundException(message);
        }
        String pause = config.getProperty("pause");
        if (pause != null && pause.length() > 0) {
            try {
                this.sleepPeriodMs = Integer.parseInt(pause);
            } catch (NumberFormatException ex) {
                String message = "В файле конфигурации '"
                        + this.configFileName
                        + "', прараметр 'pause' имеет не верный формат...";
                throw new NumberFormatException(message);
            }
        }
        this.scheme = XmlSchemeReader.parse(
                new File(this.schemaFileName), null);
        
        this.opcBridge = new OpcBridge(this.getScheme(), this.config,
                this.readTags, this.writeTags);
        
    }

    public void run() {
        try {          
            this.scheme.resetAllModels();
            this.simulationThread = new SimulationThread();
            this.running =true;
            this.simulationThread.start();
        } catch(Exception ex) {
            opcBridge.disconnect();
        }
    }
    public void stop() {
            this.running =false;
    }
    public void close() {
            opcBridge.disconnect();
    }
    public void addSchemeUIComponent(ISchemeUIComponent component){
        this.uiComponents.add(component);
        component.onAttachToSimulator(this);
    }

    private void beforeSimulationStart(){
        for(ISchemeUIComponent component:this.uiComponents){
            component.beforeSimulationStart(this);
        }
    }
    private void afterSimulationStop(){
        for(ISchemeUIComponent component:this.uiComponents){
            component.afterSimulationStop(this);
        }
    }

    private void beforeSimulationStep(){
        for(ISchemeUIComponent component:this.uiComponents){
            component.beforeSimulationStep(this);
        }
    }
    
    private void afterSimulationStep(){
        for(ISchemeUIComponent component:this.uiComponents){
            component.afterSimulationStep(this);
        }
    }

    /**
     * @return the scheme
     */
    public Scheme getScheme() {
        return scheme;
    }

    /**
     * @return the running
     */
    public boolean isRunning() {
        return running;
    }
    
    private class SimulationThread extends Thread{
        private int time = 0;
        @Override
        public void run(){
                    time = 0;
                    beforeSimulationStart();
                    try {
                        while (!Thread.currentThread().isInterrupted() && isRunning()) {
                            System.out.println("---");
                            time++;
                            try {
                                opcBridge.readTags();
                                beforeSimulationStep();
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage()
                                        + ex.getStackTrace());
                                opcBridge.disconnect();
                            }
                            
                            scheme.stepAllModels();
                            
                            System.out.println((time+1)+" (sec)");
                            try {
                                afterSimulationStep();
                                opcBridge.writeTags();
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage()
                                        + ex.getStackTrace());
                                opcBridge.disconnect();
                            }
                            Thread.sleep(ProcessSimulator.this.sleepPeriodMs);
                        } 
                    } catch (Exception ex) {
                        
                        System.out.println(ex.getMessage());
                        ex.printStackTrace();
                    } finally {
                        afterSimulationStop();
                    }

        }
    }
}
