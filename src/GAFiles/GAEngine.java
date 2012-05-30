/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GAFiles;

import GUI.UserInterface;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.jgap.*;
import org.jgap.impl.CrossoverOperator;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;
import org.jgap.impl.WeightedRouletteSelector;

/**
 *
 * @author Pk
 */
public class GAEngine {

    /**
     * @param args the command line arguments
     */
    private UserInterface parent;
    public Configuration conf = new DefaultConfiguration();
    private SwapMutate mutationOperator;
    private OrderCrossOver orderCrossOver;
    private int populationSize = 100;
    private Genotype genoType;
    Constraint constraint = new Constraint();
    private double populationDiversity = 0.0f;

    public void evolve() {
        try {
            mutationOperator = new SwapMutate(conf);
            orderCrossOver = new OrderCrossOver(conf);

            if (!Configurations.ADAPTIVE) {
                mutationOperator.setMutationRate(Configurations.MUTATION_RATE);
                orderCrossOver.setCrossoverRate(Configurations.CROSSOVER_RATE);
            } else {
                Configurations.ADAPTIVE = true;
            }
            conf.setPreservFittestIndividual(true);
            conf.setKeepPopulationSizeConstant(true);
            conf.removeNaturalSelectors(true);
            WeightedRouletteSelector selector = new WeightedRouletteSelector(conf);
            conf.addNaturalSelector(selector, false);
            conf.getGeneticOperators().clear();

            conf.addGeneticOperator(orderCrossOver);
            conf.addGeneticOperator(mutationOperator);
            conf.setPopulationSize(populationSize);


            WeightDistributionUniformity fitness = new WeightDistributionUniformity();
            conf.setFitnessFunction(fitness);
            IntegerGene[] sampleGene = new IntegerGene[64];
            for (int i = 0; i < 64; i++) {
                try {
                    sampleGene[i] = new IntegerGene(conf, i + 1, i + 1);
                    sampleGene[i].setAllele(i + 1);
                } catch (InvalidConfigurationException ex) {
                    Logger.getLogger(UserInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            IChromosome sampleChromosome = new Chromosome(conf, sampleGene);

            conf.setSampleChromosome(sampleChromosome);
            conf.setPopulationSize(Configurations.POPULATION_SIZE);

            Genotype population = Genotype.randomInitialGenotype(conf);
            List chromosomes = population.getPopulation().getChromosomes();
            for (int i = 0; i < chromosomes.size(); i++) {
                IChromosome chromosome = (IChromosome) chromosomes.get(i);
                RandomGenerator generator = conf.getRandomGenerator();
                diversifyPopulation(chromosome, generator);
            }
            for (int i = 0; i < Configurations.NO_OF_EVOLUTIONS; i++) {

                if (!uniqueChromosomes(population.getPopulation())) {
                    break;
                }
                List<IChromosome> populationChromosomes = population.getPopulation().getChromosomes();

                population.evolve();

                if (Configurations.ADAPTIVE) {
                    double bestFit = population.getFittestChromosome().getFitnessValue();
                    double avgFit = parent.findAverageFitness(population);
                    double generationDiversity = (double) ((bestFit - avgFit) / avgFit);                    

                    if (generationDiversity > populationDiversity) {
                        populationDiversity = generationDiversity;
                    } else {
                        if (generationDiversity < 0.07) {
                            double temp = Configurations.MUTATION_RATE;
                            temp = temp + temp * (2 * ((double) (populationDiversity - generationDiversity) / populationDiversity));
                            if (temp > 0.2) {
                                temp = conf.getRandomGenerator().nextDouble();
                            }
                            Configurations.MUTATION_RATE = temp;
                            mutationOperator.setMutationRate(Configurations.MUTATION_RATE);
                            
                        } else {
                            double temp = Configurations.CROSSOVER_RATE;
                            temp = temp + (2 - 2 * Math.pow(generationDiversity / populationDiversity, 2));
                            if (temp > 1) {
                                temp = conf.getRandomGenerator().nextDouble();
                            }
                            Configurations.CROSSOVER_RATE = temp;
                            orderCrossOver.setCrossoverRate(Configurations.CROSSOVER_RATE);                            
                        }
                    }
                }
                updateUI(population);
            }
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(GAEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static boolean uniqueChromosomes(Population a_pop) {
        // Check that all chromosomes are unique
        for (int i = 0; i < a_pop.size() - 1; i++) {
            IChromosome c = a_pop.getChromosome(i);
            for (int j = i + 1; j < a_pop.size(); j++) {
                IChromosome c2 = a_pop.getChromosome(j);
                if (c == c2) {
                    System.out.println("Unique");
                    return false;
                }
            }
        }
        return true;
    }

    private void diversifyPopulation(IChromosome chromosome, RandomGenerator generator) {

        if (Configurations.NO_LENGTH || Configurations.WEIGHT_UNIFORM) {
            createPopulation(chromosome, generator);
        } else {
            do {
                
                createPopulation(chromosome, generator);
            } while (!constraint.verify(null, null, chromosome, 0));
        }

    }

    public void createPopulation(IChromosome chromosome, RandomGenerator generator) {
        IntegerGene[] Gene = new IntegerGene[64];
        for (int i = 0; i < 64; i++) {
            try {
                int value = generator.nextInt(64);
                for (int k = 0; k < i; k++) {

                    if ((value + 1) == Gene[k].intValue()) {
                        value = generator.nextInt(64);
                        k = -1;

                    }
                }
                Gene[i] = new IntegerGene(conf, value + 1, value + 1);
                //Gene[i].setConstraintChecker(constraint);
                Gene[i].setAllele(value + 1);
                chromosome.setGenes(Gene);
            } catch (InvalidConfigurationException ex) {
                Logger.getLogger(UserInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public GAEngine(UserInterface parent) {
        this.parent = parent;
        conf.reset();
    }

    public static void main(String[] args) {
        // TODO code application logic here
    }

    private void updateUI(Genotype population) {
        final Genotype generationPopulation = population;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                parent.updateUI(generationPopulation);
            }
        });
    }
}
