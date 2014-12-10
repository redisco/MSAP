package ua.com.msap.gui.components;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.InternationalFormatter;
import ua.com.msap.ProcessSimulator;
import ua.com.msap.core.Variable;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class WriteVariableComponent extends JPanel implements ISchemeUIComponent{
    private String caption;
    private String variableName;
    private double value = 0;
    private ProcessSimulator simulator = null;
        
    JLabel label;
    JFormattedTextField edit;
    JButton button;
    
    public WriteVariableComponent(){
        this.setLayout(new GridLayout(1,3));
        label = new JLabel(this.getCaption());
        
        edit = new JFormattedTextField(new InternationalFormatter(
            this.getEditFieldFormat())
         {
            protected DocumentFilter getDocumentFilter()
            {
               return filter;
            }

            private DocumentFilter filter = new DoubleFilter();
         });
        edit.setValue(value);
        
        ActionListener buttonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double newValue = ((Number) edit.getValue()).doubleValue();
                boolean isDouble = false;
                try{
                    String text = edit.getText();
                    Double.parseDouble(text);
                    isDouble = true;
                }catch(Exception ex)  {
                    isDouble = false;
                }
                if(isDouble){                    
                    value = newValue;                    
                    edit.setBackground(Color.WHITE);
                    writeValueToVariable(simulator);

                } else {
                    edit.setValue(new Double(value));
                }
            }
        };
        button = new JButton("ok");
        button.addActionListener(buttonListener);
        
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
    private void writeValueToVariable(ProcessSimulator simulator){
        if(simulator == null){
            return;
        }
        Variable variable = 
                simulator.getScheme().getVariableByName(this.variableName);
        if(variable == null){
            String message = "Графический компонент чтения не смог связаться с "
                    + "переменной "+this.variableName;
            JOptionPane.showMessageDialog(null, message);
            return;
        }
        variable.setValue(value);
    }
    private void readValueFromVariable(ProcessSimulator simulator){
        if(simulator == null){
            return;
        }
        Variable variable = 
                simulator.getScheme().getVariableByName(this.variableName);
        if(variable == null){
            String message = "Графический компонент чтения не смог связаться с "
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
        this.simulator = simulator;
        if(simulator == null){
            String message = "Попытка подключения компонента записи к не "
                    + "существующему симулятору в функции "
                    + "onAttachToSimulator()...";
            JOptionPane.showMessageDialog(null, message);
        }
        this.readValueFromVariable(simulator);
    }
    @Override
    public void beforeSimulationStart(ProcessSimulator simulator) {
        this.writeValueToVariable(simulator);
    }

    @Override
    public void afterSimulationStop(ProcessSimulator simulator) {

    }

    @Override
    public void beforeSimulationStep(ProcessSimulator simulator) {
        this.writeValueToVariable(simulator);
    }
    
    @Override
    public void afterSimulationStep(ProcessSimulator simulator) {
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
    
 /**
 * A filter that restricts input to digits and a '.' sign.
 */
class DoubleFilter extends DocumentFilter
{
  @Override
   public void insertString(FilterBypass fb, int offset, String string, 
           AttributeSet attr) throws BadLocationException
   {
      StringBuilder builder = new StringBuilder(string);
      for (int i = builder.length() - 1; i >= 0; i--)
      {
         int cp = builder.codePointAt(i);
         if (!Character.isDigit(cp) && cp != '.' && cp != '-')
         {
            builder.deleteCharAt(i);
            if (Character.isSupplementaryCodePoint(cp))
            {
               i--;
               builder.deleteCharAt(i);
            }
         } else {
                edit.setBackground(Color.YELLOW);
            } 
      }
      super.insertString(fb, offset, builder.toString(), attr);
   }

        @Override
   public void replace(FilterBypass fb, int offset, int length, String string, 
           AttributeSet attr) throws BadLocationException
   {
      
      if (string != null)
      {
         StringBuilder builder = new StringBuilder(string);
         for (int i = builder.length() - 1; i >= 0; i--)
         {
            int cp = builder.codePointAt(i);
            if (!Character.isDigit(cp) && cp != '.' && cp != '-')
            {
               builder.deleteCharAt(i);
               if (Character.isSupplementaryCodePoint(cp))
               {
                  i--;
                  builder.deleteCharAt(i);
               }
            } else {
                edit.setBackground(Color.YELLOW);
            }
         }
         string = builder.toString();
      }
      super.replace(fb, offset, length, string, attr);
   }
}

}
