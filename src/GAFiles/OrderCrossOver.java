/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GAFiles;

import java.util.List;
import org.jgap.BaseGeneticOperator;
import org.jgap.Configuration;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;

/**
 *
 * @author Pk
 */
public class OrderCrossOver extends BaseGeneticOperator{

    public OrderCrossOver(Configuration config) throws InvalidConfigurationException
    {
        super(config);
    }
    @Override
    public void operate(Population a_population, List a_candidateChromosomes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareTo(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
