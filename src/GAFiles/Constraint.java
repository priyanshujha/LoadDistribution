/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GAFiles;

import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.IGeneConstraintChecker;

/**
 *
 * @author Pk
 */
public class Constraint implements IGeneConstraintChecker{

    @Override
    public boolean verify(Gene a_gene, Object a_alleleValue, IChromosome a_chromosome, int a_geneIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
