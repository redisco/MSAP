package ua.com.msap.gui;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import ua.com.msap.ProcessSimulator;
import ua.com.msap.gui.components.ISchemeUIComponent;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class ControlWindow extends JFrame
{
   public ControlWindow(String filename)
   {
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
      setTitle("Модель обьекта");

      gridbag = new GridBagPane(filename);
      add(gridbag);
      
      /*
      //Example of access by bean id 
      JComboBox size = (JComboBox) gridbag.get("size");
      JCheckBox bold = (JCheckBox) gridbag.get("bold");
      JCheckBox italic = (JCheckBox) gridbag.get("italic");
      */
   }
public static List<ISchemeUIComponent> getSchemeComponents(final Container c) {
    Component[] comps = c.getComponents();
    List<ISchemeUIComponent> compList = new ArrayList<ISchemeUIComponent>();
    for (Component comp : comps) {
        if(comp instanceof ISchemeUIComponent){
            compList.add((ISchemeUIComponent)comp);
        }
        if (comp instanceof Container)
            compList.addAll(getSchemeComponents((Container) comp));
    }
    return compList;
}  

   public void connectToSimulator(ProcessSimulator simulator){
       List<ISchemeUIComponent> components = this.getSchemeComponents(this);
       for(ISchemeUIComponent component: components){
           simulator.addSchemeUIComponent(component);
       }
   }

   private GridBagPane gridbag;
   
   
   private static final int DEFAULT_WIDTH = 300;
   private static final int DEFAULT_HEIGHT = 400;

}
