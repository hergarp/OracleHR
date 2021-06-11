package oraclehrriport;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class Grafikon {

    public Grafikon() {
        ;
    }

    public static void letrehozKordiagramAdatszerk(DefaultPieDataset adatSzerk, String[][] tomb) {
        for (int i = 0; i < tomb.length; i++) {
            adatSzerk.setValue(tomb[i][0], Double.parseDouble(tomb[i][1]));
        }
    }

    public static void letrehozOszlopDiagramAdatszerk(DefaultCategoryDataset adatSzerk, String[][] tomb, String cim) {
        for (int i = 0; i < tomb.length; i++) {
            adatSzerk.setValue(Double.parseDouble(tomb[i][1]), cim, tomb[i][0]);
        }
    }

    public static void oszlopDiagramFormaz(JFreeChart chartNev, JPanel kozvPanel, JPanel foPanel) {
        chartNev.setBorderVisible(true);
        chartNev.setBorderPaint(new Color(186, 201, 227));
        chartNev.setBackgroundPaint(new Color(202, 255, 255));
        CategoryPlot cpPlot = chartNev.getCategoryPlot();
        ChartPanel cpGrPanel = new ChartPanel(chartNev);
        cpPlot.setRangeGridlinePaint(Color.BLACK);
        cpPlot.setDomainAxisLocation(AxisLocation.TOP_OR_RIGHT);
        cpPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        // ChartFrame cfGrafikonKeret = new ChartFrame("Átlagfizetés részlegenként",chartNev,true); //--külön ablakban fog megjelenni
        // cfGrafikonKeret.setVisible(true);
        // cfGrafikonKeret.setSize(foPanel.width/2,foPanel.height/2); 
        kozvPanel.removeAll();
        kozvPanel.setLayout(new BorderLayout());
        kozvPanel.add(cpGrPanel);
        kozvPanel.setPreferredSize(new Dimension(foPanel.getWidth() / 2, foPanel.getHeight() / 2));
        kozvPanel.updateUI();
    }

    public static void korDiagramFormaz(JFreeChart chartNev, JPanel kozvPanel, JPanel foPanel, int res) {
        chartNev.setBorderVisible(true);
        chartNev.setBorderPaint(new Color(186, 201, 227));
        chartNev.setBackgroundPaint(new Color(202, 255, 255));
        kozvPanel.setPreferredSize(new Dimension(foPanel.getWidth() / 2 - 2 * res, foPanel.getHeight() / 2 - 2 * res));
        kozvPanel.updateUI();
        ChartPanel cpGrPanel = new ChartPanel(chartNev);
        kozvPanel.removeAll();
        kozvPanel.setLayout(new BorderLayout());
        kozvPanel.add(cpGrPanel, BorderLayout.CENTER);
//        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
//                "{0}: {1} ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));
 PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
                "{1} ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));
        ((PiePlot) chartNev.getPlot()).setLabelGenerator(gen);
        cpGrPanel.validate(); //megjelenik a label adott szelet fölött "mouseover"  esetén
    }

}
