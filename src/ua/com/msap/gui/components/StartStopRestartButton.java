package ua.com.msap.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import ua.com.msap.ProcessSimulator;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class StartStopRestartButton extends JButton 
implements ISchemeUIComponent{
    private String startCaption = "Старт";
    private String stopCaption = "Стоп";
    private String restartCaption = "Рестарт";
    private ProcessSimulator simulator = null;
    /*workMode: 0-stopped 1-started*/
    private int workMode = 0;
    
    public StartStopRestartButton(){
        this.setText(startCaption);
        ActionListener listener = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setNextWorkMode();
            }
        };
        this.addActionListener(listener);
    }
    private void setNextWorkMode(){
        if(simulator == null){
            return;
        }
        switch(this.workMode){
            case 0:                
                this.setText(stopCaption);
                this.workMode = 1;
                this.simulator.run();
                break;
            case 1:
                this.setText(restartCaption);
                this.workMode = 0;
                this.simulator.stop();
                break;
        }
    }

    @Override
    public void onAttachToSimulator(ProcessSimulator simulator) {
        this.simulator = simulator;
        if(simulator == null){
            String message = "Попытка подключения кнопки старта к не "
                    + "существующему симулятору в функции "
                    + "onAttachToSimulator()...";
            JOptionPane.showMessageDialog(null, message);
        }
    }
    @Override
    public void beforeSimulationStart(ProcessSimulator simulator) {

    }

    @Override
    public void afterSimulationStop(ProcessSimulator simulator) {
 
    }
    
    @Override
    public void beforeSimulationStep(ProcessSimulator simulator) {
    }
    
    @Override
    public void afterSimulationStep(ProcessSimulator simulator) {
    }

    /**
     * @return the startCaption
     */
    public String getStartCaption() {
        return startCaption;
    }

    /**
     * @param startCaption the startCaption to set
     */
    public void setStartCaption(String startCaption) {
        this.startCaption = startCaption;
    }

    /**
     * @return the stopCaption
     */
    public String getStopCaption() {
        return stopCaption;
    }

    /**
     * @param stopCaption the stopCaption to set
     */
    public void setStopCaption(String stopCaption) {
        this.stopCaption = stopCaption;
    }

    /**
     * @return the restartCaption
     */
    public String getRestartCaption() {
        return restartCaption;
    }

    /**
     * @param restartCaption the restartCaption to set
     */
    public void setRestartCaption(String restartCaption) {
        this.restartCaption = restartCaption;
    }
}
