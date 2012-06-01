/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import GAFiles.*;
import Package.PackageSpecifications;
import java.awt.BasicStroke;
import java.awt.Stroke;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import jxl.*;
import jxl.read.biff.BiffException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
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
public class UserInterface extends javax.swing.JFrame {

    /**
     * Creates new form GA_Optimization
     */
    private String[] columns = {"Container ID", "Weight", "Length", "Safety"};
    private DefaultTableModel dt = new DefaultTableModel(columns, 0);
    private static UserInterface parent;
    private Thread executionThread;
    private TimeSeries series;
    private DefaultXYDataset dataset = null;
    private double[][] bestFitness = null;
    private double[][] averageFitness = null;

    public UserInterface() {

        initComponents();

        /*
         * series=new TimeSeries("Fitness Data",Millisecond.class);
         * TimeSeriesCollection dataSet=new TimeSeriesCollection(series);
         *
         */
        dataset = new DefaultXYDataset();
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);

        chartPanel.setPreferredSize(new java.awt.Dimension(1300, 300));
        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGap(0, 0, 0).addComponent(chartPanel).addContainerGap(0, Short.MAX_VALUE)));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGap(0, 0, 0).addComponent(chartPanel).addContainerGap(0, Short.MAX_VALUE)));
        jPanel1.setLayout(jPanel1Layout);
        jPanel1.validate();
        jLabel6.setText("");
        jLabel15.setText(Configurations.MUTATION_RATE + "");       
        jLabel16.setText(Configurations.CROSSOVER_RATE + "");       
        jLabel17.setText("");
        jLabel23.setText("");

        populateData();
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
                String[] row = new String[4];
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

        Configurations.AVERAGE_WEIGHT = totalWeight / 64;
        Configurations.AVERAGE_LENGTH = totalLength / 64;
        Configurations.RACK_LENGTH = (int) (Configurations.AVERAGE_LENGTH * 4 + 10);
        jLabel9.setText(Configurations.RACK_LENGTH + "");
        jLabel10.setText(Configurations.AVERAGE_WEIGHT + "");
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jSlider1 = new javax.swing.JSlider();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSlider2 = new javax.swing.JSlider();
        jLabel4 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                UserInterface.this.windowOpened(evt);
            }
        });

        jTable1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        jTable1.setModel(dt);
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText("Optimize");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Population Size ");

        jSlider1.setMinorTickSpacing(1);
        jSlider1.setPaintLabels(true);
        jSlider1.setPaintTicks(true);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                UserInterface.this.stateChanged(evt);
            }
        });

        jLabel2.setText("Crossover Rate ");

        jLabel3.setText("Mutation Rate");

        jSlider2.setMinorTickSpacing(1);
        jSlider2.setPaintLabels(true);
        jSlider2.setPaintTicks(true);
        jSlider2.setValue(2);
        jSlider2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                stateChangedSlider2(evt);
            }
        });

        jLabel4.setText("Adaptive Parameters :");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Yes");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setSelected(true);
        jRadioButton2.setText("No");

        jLabel5.setText("Solution Fitness :");

        jLabel6.setText("---------");

        jLabel7.setText("Average Rack Weight :");

        jLabel8.setText("Rack Length :");

        jLabel9.setText("jLabel9");

        jLabel10.setText("jLabel10");

        jLabel11.setText("No. Of Evolutions ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1298, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 373, Short.MAX_VALUE)
        );

        jButton2.setText("Stop");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel12.setText("Mutation Rate :");

        jLabel13.setText("Crossover Rate :");

        jLabel14.setText("Generation :");

        jLabel15.setText("jLabel15");

        jLabel16.setText("jLabel16");

        jLabel17.setText("jLabel17");

        jLabel18.setText("0");

        jLabel19.setText("0.1");

        jLabel20.setText("0");

        jLabel21.setText("1.0");

        jLabel22.setText("Average Fitness :");

        jLabel23.setText("jLabel23");

        buttonGroup2.add(jRadioButton3);
        jRadioButton3.setText("Just Weight Uniformity");

        buttonGroup2.add(jRadioButton4);
        jRadioButton4.setText("No Length Constraint");

        buttonGroup2.add(jRadioButton5);
        jRadioButton5.setText("No Safety Constraint");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 694, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jRadioButton1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButton2))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(14, 14, 14)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel13)
                                            .addComponent(jLabel7)
                                            .addComponent(jLabel14)
                                            .addComponent(jLabel12)
                                            .addComponent(jLabel8))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel17)
                                            .addComponent(jLabel10)
                                            .addComponent(jLabel9)
                                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addGap(23, 23, 23)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5)
                                            .addComponent(jLabel22))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel23)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jRadioButton4)
                                            .addComponent(jRadioButton3)
                                            .addComponent(jRadioButton5))))
                                .addGap(143, 143, 143))
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel20)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel11)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE))
                                .addComponent(jLabel21)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel18)
                                    .addGap(560, 560, 560)
                                    .addComponent(jLabel19))
                                .addComponent(jSlider1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(jSlider2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20)
                            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(jLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(jRadioButton1)
                                    .addComponent(jRadioButton2))
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel15)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton2)
                                    .addComponent(jRadioButton3))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(20, 20, 20)
                                        .addComponent(jButton1))
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButton4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jRadioButton5)))))
                        .addGap(11, 11, 11)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel16))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel17)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel22)
                                    .addComponent(jLabel23))))))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void updateUI(Genotype population) {

        IChromosome fittest = population.getFittestChromosome();
        double fitnessValue = fittest.getFitnessValue();
        int popNUm = population.getPopulation().size();
        int generation = population.getConfiguration().getGenerationNr();
        Gene[] solution = fittest.getGenes();
        String[] columns = {"Level", "Rack ID", "Container ID", "Rack Weight", "Rack Length", "Safety"};
        dt = new DefaultTableModel(columns, 0);
        double[] boxWeight = new double[16];
        int[] boxLength = new int[16];
        for (int j = 0; j < 64; j++) {
            int mark = (j / 4);
            boxWeight[mark] = 0;
            boxLength[mark] = 0;
            do {
                int temp = (Integer) solution[j].getAllele() - 1;
                if (temp == 64) {
                    System.out.println(temp);
                }
                boxWeight[mark] += Configurations.PACKAGES[temp].getWt();
                boxLength[mark] += Configurations.PACKAGES[temp].getLength();
                j++;
            } while ((j % 4) != 0);
            boxWeight[mark] = (double) boxWeight[mark] / 4;
            j--;
        }
        for (int k = 0; k < 64; k++) {
            String[] row = new String[6];
            int level = ((k / 4) % 4) + 1;
            int boxID = (k / 4);
            row[0] = level + "";
            row[1] = boxID + "";
            row[2] = Configurations.PACKAGES[(Integer) solution[k].getAllele() - 1].getId() + "";
            row[3] = boxWeight[boxID] + "";
            row[4] = boxLength[boxID] + "";

            row[5] = Configurations.PACKAGES[(Integer) solution[k].getAllele() - 1].getSafetyFactor() + "";
            dt.addRow(row);
        }
        DecimalFormat df = new DecimalFormat("#.####");
        jLabel15.setText(df.format(Configurations.MUTATION_RATE) + "");
        jLabel16.setText(df.format(Configurations.CROSSOVER_RATE) + "");
        jLabel6.setText(fittest.getFitnessValue() + "");
        jLabel23.setText(findAverageFitness(population) + "");
        jTable1.setModel(dt);
        jLabel17.setText(population.getConfiguration().getGenerationNr() + "");
        addData(population);

        this.validate();
    }

    private JFreeChart createChart(XYDataset dataset) {

        JFreeChart result = ChartFactory.createXYLineChart("Fitness Values every Generation", "Generation", "Fitness Value", dataset, PlotOrientation.VERTICAL, true, true, false);
        XYPlot plot = result.getXYPlot();
        TickUnitSource ticks = NumberAxis.createIntegerTickUnits();
        ValueAxis axis = plot.getRangeAxis();
        axis.setAutoRange(true);
        axis.centerRange(1.0);

        NumberAxis domain = (NumberAxis) plot.getDomainAxis();
        domain.setStandardTickUnits(ticks);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        plot.setRenderer(renderer);
        renderer.setBaseShapesVisible(false);
        //renderer.setBaseShapesFilled(false);
        // set the renderer's stroke
        Stroke stroke = new BasicStroke(0.00001f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
        renderer.setBaseOutlineStroke(stroke);
        // label the points
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(3);
        //XYItemLabelGenerator generator = new StandardXYItemLabelGenerator(StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT, format, format);
        //renderer.setBaseItemLabelGenerator(generator);
        //renderer.setBaseItemLabelsVisible(true);

        return result;

    }
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            jButton2.setEnabled(true);
            jButton1.setEnabled(false);
            if (!jTextField1.getText().equals("")) {
                Configurations.POPULATION_SIZE = Integer.parseInt(jTextField1.getText());
            }
            if (!jTextField2.getText().equals("")) {
                Configurations.NO_OF_EVOLUTIONS = Integer.parseInt(jTextField2.getText());
            }
            if (jRadioButton2.isSelected()) {

                Configurations.MUTATION_RATE = (double) jSlider2.getValue() / 1000;
                Configurations.CROSSOVER_RATE = (double) jSlider1.getValue() / 100;
            } else {
                Configurations.ADAPTIVE = true;
            }

            if (jRadioButton3.isSelected()) {
                Configurations.WEIGHT_UNIFORM = true;
            }
            if (jRadioButton4.isSelected()) {
                Configurations.NO_LENGTH = true;
            }
            if (jRadioButton5.isSelected()) {
                Configurations.NO_SAFETY = true;
            }
            final GAEngine gaEngine = new GAEngine(parent);
            DecimalFormat df = new DecimalFormat("#.####");
            jLabel15.setText(df.format(Configurations.MUTATION_RATE) + "");
            jLabel16.setText(df.format(Configurations.CROSSOVER_RATE) + "");
            Runnable gaThread = new Runnable() {

                @Override
                public void run() {
                    gaEngine.evolve();
                }
            };
            executionThread = new Thread(gaThread);
            executionThread.start();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void windowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowOpened
        setExtendedState(MAXIMIZED_BOTH);
    }//GEN-LAST:event_windowOpened

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        executionThread.stop();
        jButton1.setEnabled(true);
        jButton2.setEnabled(false);


    }//GEN-LAST:event_jButton2ActionPerformed

    private void stateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_stateChanged


        Configurations.CROSSOVER_RATE = (double) jSlider1.getValue() / 100;
        jLabel16.setText(Configurations.CROSSOVER_RATE + "");       

    }//GEN-LAST:event_stateChanged

    private void stateChangedSlider2(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_stateChangedSlider2
        Configurations.MUTATION_RATE = (double) jSlider2.getValue() / 1000;
        jLabel15.setText(Configurations.MUTATION_RATE + "");
    }//GEN-LAST:event_stateChangedSlider2

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
            java.util.logging.Logger.getLogger(UserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        parent = new UserInterface();
        parent.setVisible(true);

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables

    private void addData(Genotype population) {
        double[][] tmpBestValues = null;
        double[][] tmpAvgValues = null;
        if (bestFitness == null) {
            tmpBestValues = new double[2][1];
            tmpAvgValues = new double[2][1];
            tmpBestValues[0][0] = population.getConfiguration().getGenerationNr();
            tmpBestValues[1][0] = (double) population.getFittestChromosome().getFitnessValue();
            tmpAvgValues[0][0] = population.getConfiguration().getGenerationNr();
            tmpAvgValues[1][0] = findAverageFitness(population);
        } else {
            int length = bestFitness[0].length;

            if (length >= 50) {
                length = 49;
            }
            tmpBestValues = new double[2][length + 1];
            tmpAvgValues = new double[2][length + 1];
            /*
             * if (bestFitness[0].length == 50) { for (int i = 0; i < 49 ; i++)
             * { tmpBestValues[0][i] = bestFitness[0][i + 1]; tmpAvgValues[0][i]
             * = averageFitness[0][i + 1]; tmpBestValues[1][i] =
             * bestFitness[1][i + 1]; tmpAvgValues[1][i] = averageFitness[1][i +
             * 1]; } } else {
             */
            for (int i = 0; i < length; i++) {
                tmpBestValues[0][i] = bestFitness[0][i];
                tmpAvgValues[0][i] = averageFitness[0][i];
                tmpBestValues[1][i] = bestFitness[1][i];
                tmpAvgValues[1][i] = averageFitness[1][i];
            }
            //}
            tmpBestValues[0][length] = population.getConfiguration().getGenerationNr();
            tmpBestValues[1][length] = population.getFittestChromosome().getFitnessValue();
            tmpAvgValues[0][length] = population.getConfiguration().getGenerationNr();
            tmpAvgValues[1][length] = findAverageFitness(population);
        }
        bestFitness = tmpBestValues.clone();
        averageFitness = tmpAvgValues.clone();
        dataset.removeSeries("Best");
        dataset.removeSeries("Average");
        dataset.addSeries("Best", bestFitness);
        dataset.addSeries("Average", averageFitness);
    }

    public double findAverageFitness(Genotype population) {

        List<IChromosome> chromosomes = population.getPopulation().getChromosomes();
        Iterator i = chromosomes.iterator();
        double avg = 0;
        while (i.hasNext()) {
            IChromosome temp = (IChromosome) i.next();
            avg += temp.getFitnessValue();
        }
        avg = (double) avg / chromosomes.size();
        return avg;
    }
}
