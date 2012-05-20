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
            score+=calculateStackScore(packageId,(i+1)%4);
        }
        return score;        
    }

    private double calculateStackScore(int[] packageId,int level) {
        double stackScore=0.0f;
        int length=packageId.length;
        int stackLength=0;        
        int stackWeight=0;
        int penalty=0;
        int safetyFactor=0;
        for(int i=0;i<4;i++)
        {
            stackWeight+=Configurations.PACKAGES[packageId[i]].getWt();
            stackLength+=Configurations.PACKAGES[packageId[i]].getLength();            
            safetyFactor=Configurations.PACKAGES[packageId[i]].getSafetyFactor(); 
            if(safetyFactor!=level)
            {
                penalty+=Math.abs(safetyFactor-level)*10;
            }            
        }
        double wtScore=Math.pow((stackWeight-Configurations.AVERAGE_WEIGHT),2);
        double lengthScore=Math.pow((stackLength-Configurations.AVERAGE_LENGTH),2);        
        stackScore=1000/(wtScore+lengthScore+penalty);
        return stackScore;
    }
    
    
}
