package GAFiles;

import org.jgap.Configuration;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.MutationOperator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgap.*;
import org.jgap.impl.IntegerGene;
import org.jgap.util.ICloneable;

public class SwapMutate extends MutationOperator {

    private double mutationRate = 0.1f;
    private boolean adaptive = false;
    private Constraint constraint = new Constraint();

    public SwapMutate(Configuration config) throws InvalidConfigurationException {
        super(config);
    }

    @Override
    public void operate(Population a_population, List a_candidateChromosomes) {
        int size = Math.min(getConfiguration().getPopulationSize(),
                a_population.size());
        int numMutation = (int) (size * mutationRate);
        RandomGenerator generator = getConfiguration().getRandomGenerator();
        for (int i = 0; i < numMutation; i++) {
            IChromosome mate = (IChromosome) ((ICloneable) a_population.getChromosome(
                    generator.nextInt(size))).clone();

            mutate(mate);
            // Add the modified chromosomes to the candidate pool so that
            // they'll be considered for natural selection during the next
            // phase of evolution.
            // -----------------------------------------------------------
            
            if (constraint.verify(null,null,mate,0)) {
                a_candidateChromosomes.add(mate);
            }

        }


    }

    private void mutate(IChromosome mate) {
        Gene[] parent = mate.getGenes();
        Gene[] child = new Gene[64];
        try {
            child = operateChromosome(parent, child);
            Configurations.UniquenessCheckerGenePrinter(parent, "Mutated Parent");
            Configurations.UniquenessCheckerGenePrinter(child, "Mutated Child");
            mate.setGenes(child);

        } catch (InvalidConfigurationException cex) {
        }
    }

    private Gene[] operateChromosome(Gene[] parent, Gene[] child) {

        RandomGenerator generator = getConfiguration().getRandomGenerator();
        int pos1 = generator.nextInt(64);
        generator = getConfiguration().getRandomGenerator();
        int pos2 = generator.nextInt(64);

        child = parent;
        child[pos2].setAllele(parent[pos1].getAllele());
        child[pos1].setAllele(parent[pos1].getAllele());

        return child;


    }

    void setMutationRate(double MUTATION_RATE) {
        this.mutationRate=MUTATION_RATE;
    }
}
