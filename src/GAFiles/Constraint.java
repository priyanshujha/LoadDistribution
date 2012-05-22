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
        
        if (a_alleleValue == null) {
            return true;
        }
        Gene[] geneSet=a_chromosome.getGenes();
        int boxNumber=(a_geneIndex)/4;
        int marker=boxNumber*4;
        int totalLength=0;
        int packageId=0;
        for(int i=marker;i<(marker+4);i++)
        {
            packageId=(Integer)geneSet[i].getAllele();
            totalLength+=Configurations.PACKAGES[packageId].getLength();
        }
        if(totalLength>Configurations.BOX_LENGTH)
        {
            
            System.out.println("Length Exceeding");
            return false;
        }
        return true;               
    }
    
}
