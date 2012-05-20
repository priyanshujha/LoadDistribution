/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import GAFiles.*;
import Package.PackageSpecifications;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import jxl.*;
import jxl.read.biff.BiffException;
import org.jgap.*;
import org.jgap.audit.*;
import org.jgap.data.*;
import org.jgap.gp.function.Cosine;
import org.jgap.impl.*;
import org.jgap.xml.*;

/**
 *
 * @author Pk
 */
public class GA_Optimization extends javax.swing.JFrame {

    /**
     * Creates new form GA_Optimization
     */
    private String[] columns = {"Package ID", "Weight", "Length", "Breadth", "Height", "Safety"};
    public Configuration conf = new DefaultConfiguration();
    private DefaultTableModel dt = new DefaultTableModel(columns, 0);
    private SwapMutate mutationOperator;
    private OrderCrossOver orderCrossOver;
    private int populationSize = 200;
    private Genotype genoType;
    private int MAX_ALLOWED_EVOLUTIONS = 2000;
    

    public GA_Optimization() {
        try {
            initComponents();
            conf.setPreservFittestIndividual(true);
            conf.setKeepPopulationSizeConstant(false);
            conf.getGeneticOperators().clear();
            mutationOperator = new SwapMutate(conf);
            orderCrossOver = new OrderCrossOver(conf);
            conf.addGeneticOperator(mutationOperator);
            conf.addGeneticOperator(orderCrossOver);
            conf.setPopulationSize(populationSize);
            

            populateData();
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(GA_Optimization.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void populateData() {
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(new File("data.xls"));
            contentReading(fs);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void contentReading(InputStream fileInputStream) {
        WorkbookSettings ws = null;
        Workbook workbook = null;
        Sheet s = null;
        Cell rowData[] = null;
        int rowCount = '0';
        int columnCount = '0';
        DateCell dc = null;
        int totalSheet = 0;
        int totalWeight = 0;
        int totalLength = 0;
        
        try {
            ws = new WorkbookSettings();
            ws.setLocale(new Locale("en", "EN"));
            workbook = Workbook.getWorkbook(fileInputStream, ws);

            totalSheet = workbook.getNumberOfSheets();

            //Getting Default Sheet i.e. 0
            s = workbook.getSheet(0);

            //Reading Individual Cell
            getHeadingFromXlsFile(s);

            //Total Total No Of Rows in Sheet, will return you no of rows that are occupied with some data
            rowCount = s.getRows();

            //Total Total No Of Columns in Sheet
            columnCount = s.getColumns();

            //Reading Individual Row Content
            for (int i = 1; i < rowCount; i++) {
                //Get Individual Row
                rowData = s.getRow(i);
                String[] row = new String[6];
                row[0] = rowData[0].getContents();
                row[1] = rowData[1].getContents();
                row[2] = rowData[2].getContents();
                row[3] = rowData[3].getContents();

                Configurations.PACKAGES[i - 1] = new PackageSpecifications();

                Configurations.PACKAGES[i - 1].setId(i);
                Configurations.PACKAGES[i - 1].setWt(Integer.parseInt(rowData[1].getContents()));
                Configurations.PACKAGES[i - 1].setLength(Integer.parseInt(rowData[2].getContents()));
                Configurations.PACKAGES[i - 1].setSafetyFactor(Integer.parseInt(rowData[3].getContents()));
                totalLength += Integer.parseInt(rowData[2].getContents());
                totalWeight += Integer.parseInt(rowData[1].getContents());                                
                dt.addRow(row);
            }
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
        Configurations.AVERAGE_WEIGHT=totalWeight/64;
        Configurations.AVERAGE_LENGTH=totalLength/64;
        Configurations.BOX_LENGTH=(int) (Configurations.AVERAGE_LENGTH*4+4);
    }
    /*
     * Returns the Headings used inside the excel sheet
     *
     */

    public void getHeadingFromXlsFile(Sheet sheet) {
        int columnCount = sheet.getColumns();
        for (int i = 0; i < columnCount; i++) {
            System.out.println(sheet.getCell(i, 0).getContents());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(dt);
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setText("Method :");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Order", "Group" }));

        jButton1.setText("Optimize");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 747, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(51, 51, 51))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)
                        .addGap(0, 569, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            genoType = Genotype.randomInitialGenotype(conf);
            Gene[] sampleGene = new Gene[64];
            for (int i = 0; i < 64; i++) {
                try {
                    sampleGene[i] = new IntegerGene();
                    sampleGene[i].setAllele(new Integer(i));
                } catch (InvalidConfigurationException ex) {
                    Logger.getLogger(GA_Optimization.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            Constraint constraint = new Constraint();
            //what they have worked on is keeping size of chromosome to 64 shile supplying 1 gene what
            //i am doing is keeping 64 gene with no specific size

            IChromosome sampleChromosome = new Chromosome(conf, sampleGene, constraint);
            conf.setSampleChromosome(sampleChromosome);
            WeightDistributionUniformity fitness = new WeightDistributionUniformity();
            conf.setFitnessFunction(fitness);
            conf.setPopulationSize(100);
            Genotype population = Genotype.randomInitialGenotype(conf);
            for (int i = 0; i < MAX_ALLOWED_EVOLUTIONS; i++) {
                if (!uniqueChromosomes(population.getPopulation())) {
                    throw new RuntimeException("Invalid state in generation " + i);
                }
                population.evolve();
            }

        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(GA_Optimization.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jButton1ActionPerformed
    public static boolean uniqueChromosomes(Population a_pop) {
        // Check that all chromosomes are unique
        for (int i = 0; i < a_pop.size() - 1; i++) {
            IChromosome c = a_pop.getChromosome(i);
            for (int j = i + 1; j < a_pop.size(); j++) {
                IChromosome c2 = a_pop.getChromosome(j);
                if (c == c2) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GA_Optimization.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GA_Optimization.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GA_Optimization.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GA_Optimization.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new GA_Optimization().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
