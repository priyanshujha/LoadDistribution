/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GAFiles;

import Package.PackageSpecifications;
import javax.security.auth.login.Configuration;
import org.jgap.Chromosome;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.impl.IntegerGene;

/**
 *
 * @author Pk
 */
public class WeightDistributionUniformity extends FitnessFunction {

    @Override
    protected double evaluate(IChromosome ic) {
        double score = 0.0f;
        Gene[] genes = ic.getGenes();
        int[] packageId = new int[4];
        int count = 0;
        int i = 0;
        while (i < 64) {
            IntegerGene gene = (IntegerGene) genes[i];
            packageId[count] = gene.intValue();
            i++;
            count++;
            if ((i % 4) == 0) {
                count = 0;
                int level = ((i / 4) % 4);
                if(level==0)
                    level=4;
                score += calculateStackScore(packageId, level);
            }
        }
        return score;
    }

    private double calculateStackScore(int[] packageId, int level) {
        double stackScore = 0.0f;
        int length = packageId.length;
        int stackLength = 0;
        int stackWeight = 0;
        int penalty = 0;
        int safetyFactor = 0;
        for (int i = 0; i < 4; i++) {
            stackWeight += Configurations.PACKAGES[packageId[i]-1].getWt();
            stackLength += Configurations.PACKAGES[packageId[i]-1].getLength();
            safetyFactor = Configurations.PACKAGES[packageId[i]-1].getSafetyFactor();
            
            if (safetyFactor != level) {
                penalty += Math.abs(safetyFactor - level) * 100;
                //System.out.println(penalty);
            }
        }
        double wtScore = Math.pow((stackWeight - Configurations.AVERAGE_WEIGHT), 2);
        double lengthScore = Math.pow((stackLength - Configurations.AVERAGE_LENGTH), 2);
        stackScore = 10000 / (wtScore + lengthScore + penalty);
        return stackScore;
    }
}
