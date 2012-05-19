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

    
    @Override
    protected double evaluate(IChromosome ic) {
        double score=0.0f;
        Gene[] genes=ic.getGenes();
        int[] packageId=new int[4];
        int count=0;
        
        for(int i=0;i<64;i++)
        {
            while((i+1)%4!=0)
            {
                packageId[count]=(Integer)genes[i].getAllele();
                i++;
                count++;
            }
            score+=calculateStackScore(packageId);
        }
        return score;        
    }

    private double calculateStackScore(int[] packageId) {
        double stackScore=0.0f;
        int length=packageId.length;
        int stackLength=0;
        int stackBreadth=0;
        int stackHeight=0;        
        int stackWeight=0;
        for(int i=0;i<4;i++)
        {
            stackWeight+=Configurations.PACKAGES[packageId[i]].getWt();
            stackLength+=Configurations.PACKAGES[packageId[i]].getLength();
            stackBreadth+=Configurations.PACKAGES[packageId[i]].getBreadth();
            stackHeight+=Configurations.PACKAGES[packageId[i]].getHeight();
        }
        double wtScore=Math.pow((stackWeight-Configurations.AVERAGE_WEIGHT),2);
        double lengthScore=Math.pow((stackLength-Configurations.AVERAGE_LENGTH),2);
        double breadthScore=Math.pow((stackBreadth-Configurations.AVERAGE_BREADTH),2);
        double heightScore=Math.pow((stackWeight-Configurations.AVERAGE_HEIGHT),2);
        stackScore=1/(wtScore+lengthScore+breadthScore+heightScore);
        return stackScore;
    }
    
    
}
