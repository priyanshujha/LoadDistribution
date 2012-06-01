/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GAFiles;

import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.IGeneConstraintChecker;
import org.jgap.impl.IntegerGene;

/**
 *
 * @author Pk
 */
public class Constraint implements IGeneConstraintChecker {

    @Override
    public boolean verify(Gene a_gene, Object a_alleleValue, IChromosome a_chromosome, int a_geneIndex) {

        if (a_alleleValue == null) {
            Gene[] geneSet = a_chromosome.getGenes();            
            int totalLength = 0;
            int containerId = 0;
            for (int i = 0; i < geneSet.length; i++) {
                do{
                    containerId = (Integer) geneSet[i].getAllele();
                    totalLength += Configurations.PACKAGES[containerId-1].getLength();
                    i++;                    
                }while((i%4)!=0);
                
                if (totalLength > Configurations.RACK_LENGTH) {                    
                    return false;
                }
                totalLength = 0;
                i--;
            }
            
            return true;
        }
        return true;        

    }
}
