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

/**
 *
 * @author Pk
 */

public class WeightDistributionUniformity extends FitnessFunction{

    private PackageSpecifications[] packages=new PackageSpecifications[80];
    public WeightDistributionUniformity(PackageSpecifications[] packages) {
        this.packages=packages;
    }

    @Override
    protected double evaluate(IChromosome ic) {
        double score=0.0f;
        Gene[] genes=ic.getGenes();
        int[] packageId=new int[4];
        int count=0;
        double stackscore=0.0f;
        for(int i=0;i<64;i++)
        {
            while((i+1)%4!=0)
            {
                packageId[count]=(Integer)genes[i].getAllele();
                i++;
                count++;
            }
            stackscore=calculateStackScore(packageId);
        }
        return score;        
    }

    private double calculateStackScore(int[] packageId) {
        double stackScore=0.0f;
        return stackScore;
    }
    
    
}
