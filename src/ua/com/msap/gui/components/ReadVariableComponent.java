package ua.com.msap.gui.components;

import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ua.com.msap.ProcessSimulator;
import ua.com.msap.core.Variable;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class ReadVariableComponent extends JPanel implements ISchemeUIComponent{
    private String caption;
    private String variableName;
    private double value = 0;
    
    JLabel label;
    JFormattedTextField edit;
    JButton button;
    
    public ReadVariableComponent(){
        this.setLayout(new GridLayout(1,3));
        this.label = new JLabel(this.getCaption());        
        this.edit = new JFormattedTextField(this.getEditFieldFormat());
        this.edit.setEditable(false);
        this.button = new JButton("ok");
        this.button.setVisible(false);
        
        this.add(this.label);
        this.add(this.edit);
        this.add(this.button);
    }
    
    /**
     * @return the caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * @param caption the caption to set
     */
    public void setCaption(String caption) {
        this.caption = caption;
        this.label.setText(caption);
    }

    /**
     * @return the variableName
     */
    public String getVariableName() {
        return variableName;
    }

    /**
     * @param variableName the variableName to set
     */
    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }
    
    private NumberFormat getEditFieldFormat(){
        DecimalFormat format = new DecimalFormat();
        DecimalFormatSymbols custom=new DecimalFormatSymbols();
        custom.setDecimalSeparator('.');
        format.setDecimalFormatSymbols(custom);
        format.setGroupingUsed(false);
        return format;        
    }

    private void readValueFromVariable(ProcessSimulator simulator){
        if(simulator == null){
            return;
        }
        Variable variable = 
                simulator.getScheme().getVariableByName(this.variableName);
        if(variable == null){
            String message = "Графический компонент записи не смог связаться с "
                    + "переменной "+this.variableName;
            JOptionPane.showMessageDialog(null, message);
            return;
        }
        double value = variable.getValue();
        this.value = value;
        this.edit.setValue(new Double(value));
    }
    @Override
    public void onAttachToSimulator(ProcessSimulator simulator) {
        if(simulator == null){
            String message = "Попытка подключения компонента чтения к не "
                    + "существующему симулятору в функции "
                    + "onAttachToSimulator()...";
            JOptionPane.showMessageDialog(null, message);
        }
        this.readValueFromVariable(simulator); 
    }
    @Override
    public void beforeSimulationStart(ProcessSimulator simulator) {
        this.readValueFromVariable(simulator);
    }

    @Override
    public void afterSimulationStop(ProcessSimulator simulator) {
        this.readValueFromVariable(simulator);
    }

    @Override
    public void beforeSimulationStep(ProcessSimulator simulator) {
    }
        
    @Override
    public void afterSimulationStep(ProcessSimulator simulator) {
        this.readValueFromVariable(simulator);
    }

    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(double value) {
        this.value = value;
        this.edit.setValue(new Double(value));
    }

}
