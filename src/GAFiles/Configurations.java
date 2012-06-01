/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GAFiles;

import Package.PackageSpecifications;
import org.jgap.Gene;

/**
 *
 * @author Pk
 */
public class Configurations {
    public static double AVERAGE_WEIGHT=0.0f;
    public static double AVERAGE_LENGTH=0.0f;
    public static int BOX_LENGTH=0;    
    public static PackageSpecifications[] PACKAGES=new PackageSpecifications[64];
    public static boolean ADAPTIVE=false;
    public static int NO_OF_EVOLUTIONS=10000;
    public static int POPULATION_SIZE=200;
    public static double CROSSOVER_RATE=0.5f;
    public static double MUTATION_RATE=0.01f;
    public static boolean WEIGHT_UNIFORM=false;
    public static boolean NO_LENGTH=false;
    public static boolean NO_SAFETY=false;
    
    
    public static void GenePrinter(Gene[]x)
    {
        for(int i=0;i<64;i++)
        {
            System.out.print(x[i].getAllele()+"\t");
        }
        System.out.println("");
    }
    public static void UniquenessCheckerGenePrinter(Gene[]x,String status)
    {
        
        for(int i=0;i<64;i++)
        {
            for(int k=i+1;k<64;k++)
            {
                int val=(Integer)x[i].getAllele();
                int val2=(Integer)x[k].getAllele();
                if(val==val2)
                {
                    System.out.println("Not unique "+status+val);
                    GenePrinter(x);
                }
            }
        }
        
    }
}
