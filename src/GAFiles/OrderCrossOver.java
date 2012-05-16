/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GAFiles;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgap.*;
import org.jgap.impl.IntegerGene;
import org.jgap.util.ICloneable;

/**
 *
 * @author Pk
 */
public class OrderCrossOver extends BaseGeneticOperator {

    private double crossoverRate;
    private boolean adaptive = false;
    private int crossoverPosition = 5;

    public OrderCrossOver(Configuration config) throws InvalidConfigurationException {
        super(config);
    }

    @Override
    public void operate(Population a_population, List a_candidateChromosomes) {
        int size = Math.min(getConfiguration().getPopulationSize(),
                a_population.size());
        // if 0.6, then 0.3*size times cross over,each take two
        int numCrossovers = (int) (size * crossoverRate);
        RandomGenerator generator = getConfiguration().getRandomGenerator();
        for (int i = 0; i < numCrossovers; i++) {
            IChromosome firstMate = (IChromosome) ((ICloneable) a_population.getChromosome(
                    generator.nextInt(size))).clone();
            IChromosome secondMate = (IChromosome) ((ICloneable) a_population.getChromosome(
                    generator.nextInt(size))).clone();
            operate(firstMate, secondMate);
            // Add the modified chromosomes to the candidate pool so that
            // they'll be considered for natural selection during the next
            // phase of evolution.
            // -----------------------------------------------------------
            a_candidateChromosomes.add(firstMate);
            a_candidateChromosomes.add(secondMate);
        }
    }

    @Override
    public int compareTo(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void operate(IChromosome firstMate, IChromosome secondMate) {
        Gene[] parent1 = firstMate.getGenes();
        Gene[] parent2 = secondMate.getGenes();

        Gene[] child1 = new Gene[64];
        Gene[] child2 = new Gene[64];
        

        try {
            child1=operateChromosome(parent1,parent2,child1);
            child2=operateChromosome(parent2,parent1,child2);
            firstMate.setGenes(child1);
            secondMate.setGenes(child2);
        } catch (InvalidConfigurationException cex) {
            throw new Error("Error occured while operating on:" + firstMate
                    + " and " + secondMate + ". First from crossover. Error message: " + cex.getMessage());
        }
    }

    private Gene[] operateChromosome(Gene[] parent1,Gene[] parent2,Gene[] child) {
        
        int i = 0;
        RandomGenerator generator = getConfiguration().getRandomGenerator();
        int pos1 = generator.nextInt(64 - crossoverPosition);
        int pos2 = pos1 + crossoverPosition;
        boolean typeCheck = false;
        HashMap<Integer, Integer> values = new HashMap<Integer, Integer>();
        for (i = pos1; i <= pos2; i++) {
            try {
                child[i] = new IntegerGene();
                child[i] = parent1[i];
                Integer value = (Integer) parent1[i].getAllele();
                values.put(value.intValue(), value);
            } catch (InvalidConfigurationException ex) {
                Logger.getLogger(OrderCrossOver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        int position = pos2 + 1;
        for (i = pos2 + 1; i < 64; i++) {
            int value = (Integer) parent2[i].getAllele();
            Object val = values.get(value);
            if (val == null) {
                try {
                    child[position] = new IntegerGene();
                    child[position] = parent2[i];
                    position++;
                    if (position == 64) {
                        i++;
                        break;
                    }
                } catch (InvalidConfigurationException ex) {
                    Logger.getLogger(OrderCrossOver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (i >= 63) {
                i = 0;
            }
        }
        for (int j = 0; j < pos1; j++) {
            if (i >= 63) {
                i = 0;
            }
            int value = (Integer) parent2[i].getAllele();
            Object val = values.get(value);
            if (val == null) {
                try {
                    child[j] = new IntegerGene();
                    child[j] = parent2[i];
                    i++;
                } catch (InvalidConfigurationException ex) {
                    Logger.getLogger(OrderCrossOver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return child;
    }
}
