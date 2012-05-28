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
            System.out.println(geneSet.length);
            int totalLength = 0;
            int packageId = 0;
            for (int i = 0; i < geneSet.length; i++) {
                while ((i + 1) % 4 != 0) {
                    packageId = (Integer) geneSet[i].getAllele();
                    totalLength += Configurations.PACKAGES[packageId].getLength();
                    i++;
                    
                }
                if (totalLength > Configurations.BOX_LENGTH) {

                    System.out.println("Length Exceeding");
                    return false;
                }
                totalLength = 0;
            }
            /*
             * int boxNumber = (a_geneIndex) / 4; int marker = boxNumber * 4;
             * int totalLength = 0; int packageId = 0; for (int i = marker; i <
             * (marker + 4); i++) { packageId = (Integer)
             * geneSet[i].getAllele(); totalLength +=
             * Configurations.PACKAGES[packageId].getLength(); } if (totalLength
             * > Configurations.BOX_LENGTH) {
             *
             * System.out.println("Length Exceeding"); return false;
            }
             */
            return true;
        }
        return true;
        //Configurations.GenePrinter(a_chromosome.getGenes());
        //System.out.println(a_alleleValue+"\t"+a_geneIndex);                       

    }
}
