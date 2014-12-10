package ua.com.msap.gui.components;

import ua.com.msap.ProcessSimulator;
import ua.com.msap.core.Scheme;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public interface ISchemeUIComponent {
    void onAttachToSimulator(ProcessSimulator simulator);
    void beforeSimulationStart(ProcessSimulator simulator);
    void afterSimulationStop(ProcessSimulator simulator);
    void beforeSimulationStep(ProcessSimulator simulator);
    void afterSimulationStep(ProcessSimulator simulator);
}
