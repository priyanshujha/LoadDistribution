/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GAFiles;

import org.jgap.Configuration;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.MutationOperator;

/**
 *
 * @author Pk
 */
public class SwapMutate extends MutationOperator{
    public SwapMutate(Configuration config) throws InvalidConfigurationException {
        super(config);
    }
}
