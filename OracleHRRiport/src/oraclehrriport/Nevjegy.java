package oraclehrriport;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class Nevjegy {

    private String keresztNev;
    private String vezetekNev;
    private String teljesNev;
    private char vezetekNevKezdobetu;
    private int dolgozoAzonosito;
    private String emailCim;
    private String munkakor;
    private String reszleg;
    private String menedzserNeve;
    private String reszlegMenedzserNeve;
    private String telefonszam;

    private Nevjegy(List<OracleHRData> adatLista, int i) {

        this.keresztNev = adatLista.get(i).getEmployeeFirstAndMiddleName();
        this.vezetekNev = adatLista.get(i).getEmployeeLastName();
        this.teljesNev = adatLista.get(i).getEmployeeName();
        this.vezetekNevKezdobetu = vezetekNev.charAt(0);
        this.dolgozoAzonosito = adatLista.get(i).getEmployeeID();
        this.emailCim = adatLista.get(i).getEmail().toLowerCase() + "@oracle.com";
        this.munkakor = adatLista.get(i).getJobTitle();
        this.reszleg = adatLista.get(i).getDepartmentName();
        this.menedzserNeve = keresMenedzserNevDolgozoIDAlapjan(dolgozoAzonosito, adatLista);
        this.reszlegMenedzserNeve = keresDolgozoNevIDAlapjan(adatLista.get(i).getDepartmentManagerID(), adatLista);
        this.telefonszam = adatLista.get(i).getRandomPhoneNumber();

    }

    public String getKeresztNev() {
        return keresztNev;
    }

    public String getVezetekNev() {
        return vezetekNev;
    }

    public int getDolgozoAzonosito() {
        return dolgozoAzonosito;
    }

    public String getEmailCim() {
        return emailCim;
    }

    public String getMunkakor() {
        return munkakor;
    }

    public String getReszleg() {
        return reszleg;
    }

    public String getMenedzserNeve() {
        return menedzserNeve;
    }

    public String getReszlegMenedzserNeve() {
        return reszlegMenedzserNeve;
    }

    public String getTelefonszam() {
        return telefonszam;
    }

    @Override
    public String toString() {
        return "{" + vezetekNevKezdobetu + "; " + vezetekNev + "; " + keresztNev + "; " + dolgozoAzonosito + "; " + emailCim + "; " + munkakor + "; " + reszleg + "; " + menedzserNeve + "; " + reszlegMenedzserNeve + "; " + telefonszam + "}";
    }

    //employee ID alapján megkeresi a dolgozó nevét 
    public static String keresDolgozoNevIDAlapjan(int employeeID, List<OracleHRData> adatLista) {
        String nev = "";
        int i = 0;
        boolean talalt = false;
        while (i < adatLista.size() && !talalt) {
            talalt = (adatLista.get(i).getEmployeeID() == employeeID);
            if (talalt) {
                nev = adatLista.get(i).getEmployeeName();
            }
            i++;
        }
        return nev;
    }

    //employee ID alapján megkeresi a dolgozó menedzserének nevét
    public static String keresMenedzserNevDolgozoIDAlapjan(int employeeID, List<OracleHRData> adatLista) {
        String menedzserNev = "";
        int menedzserID = 0;
        //megszerezzük a menedzser ID-ját
        int i = 0;
        boolean talalt = false;
        while (i < adatLista.size() && !talalt) {
            talalt = (adatLista.get(i).getEmployeeID() == employeeID);
            if (talalt) {
                menedzserID = adatLista.get(i).getEmployeeManagerID();
                menedzserNev = keresDolgozoNevIDAlapjan(menedzserID, adatLista);
            }
            i++;
        }
        return menedzserNev;

    }
    //Employee ID alapján visszaadja az adott dolgozó a forráslistában elfoglalt indexét   

    public static int keresDolgozoIndexIDAlapjan(int employeeID, List<OracleHRData> adatLista) {
        int Index = -5;
        int i = 0;
        boolean talalt = false;
        while (i < adatLista.size() && !talalt) {
            talalt = (adatLista.get(i).getEmployeeID() == employeeID);
            if (talalt) {
                Index = i;
            }
            i++;
        }
        return Index;
    }

    //összegyűjti egy listába a forráslistában szereplő dolgozók keresztneveinek kezdőbetűit (duplikátumok nélkül)  
    public static List<String> gyartNevKezdobetuLista(List<OracleHRData> lista) {
        List<String> kimenetiLista = new ArrayList<>();
        List<OracleHRData> masolatLista = new ArrayList<>(); //lefoglalok memóriát egy üres listának
        masolatLista.addAll(lista);

        //vezetéknév szerint növekvő sorrendbe rendezzük az objektumokat a másolatlistában, hogy tudjunk csoportváltást alkalmazni
        Collections.sort(masolatLista, (OracleHRData d1, OracleHRData d2) -> {
            return d1.getEmployeeLastName().compareTo(d2.getEmployeeLastName());
        });
        //csoportváltás 
        int i = 0;
        while (i < masolatLista.size()) {
            char aktVezetekNevKezdoBetu = masolatLista.get(i).getEmployeeLastName().charAt(0);
            kimenetiLista.add(Character.toString(aktVezetekNevKezdoBetu));
            while (i < masolatLista.size() && masolatLista.get(i).getEmployeeLastName().charAt(0) == aktVezetekNevKezdoBetu) {
                i++;
            }
        }
        return kimenetiLista;
    }

    //adott betűvel kezdődő keresztnevű dolgozók adatait menti el az output listába, Névjegfy objektumként
    public static List<Nevjegy> vezeteknevPerKezdobetuLista(List<OracleHRData> lista, char kezdobetu) {
        List<Nevjegy> kimenetiLista = new ArrayList<>();
        List<OracleHRData> masolatLista = new ArrayList<>(); //lefoglalok memóriát egy üres listának
        masolatLista.addAll(lista);
        //vezetéknév szerint növekvő sorrendbe rendezzük az objektumokat a másolatlistában, hogy tudjunk csoportváltást alkalmazni
        Collections.sort(masolatLista, (OracleHRData d1, OracleHRData d2) -> {
            return d1.getEmployeeLastName().compareTo(d2.getEmployeeLastName());
        });
        //csoportváltás 
        int i = 0;
        while (i < masolatLista.size()) {
            if (masolatLista.get(i).getEmployeeLastName().charAt(0) == kezdobetu) {
                Nevjegy dolgozoNevjegy = new Nevjegy(masolatLista, i);
                kimenetiLista.add(dolgozoNevjegy);
            }
            i++;
        }

        return kimenetiLista;
    }

    public static Nevjegy listaElembolNevjegy(String listaElem, List<OracleHRData> adatLista) {
        String dolgozoAzon = "";
        int indexZarojel = 0;
        int i = listaElem.length() - 2;
        boolean talalt = false;
        while (!talalt && i > 0) {
            talalt = (listaElem.charAt(i) == '(');
            if (talalt) {
                indexZarojel = i;
            }
            i--;
        }
        dolgozoAzon = listaElem.substring(indexZarojel + 1, listaElem.length() - 1);
        int Index = keresDolgozoIndexIDAlapjan(Integer.parseInt(dolgozoAzon), adatLista);
        Nevjegy dolgozoNevjegy = new Nevjegy(adatLista, Index);
        return dolgozoNevjegy;

    }

    public static void nyomtatPanelt(JPanel panel) {
        // Create PrinterJob Here
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        // Set Printer Job Name
        printerJob.setJobName("Print Record");
//         Set Printable
        printerJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                // Check If No Printable Content
                if (pageIndex > 0) {
                    return Printable.NO_SUCH_PAGE;
                }
                // Make 2D Graphics to map content
                Graphics2D graphics2D = (Graphics2D) graphics;
                // Set Graphics Translations
                // A Little Correction here Multiplication was not working so I replaced with addition
                graphics2D.translate(pageFormat.getImageableX() + 10, pageFormat.getImageableY() + 10);
                // This is a page scale. Default should be 0.3 I am using 0.5
                graphics2D.scale(0.5, 0.5);
                // Now paint panel as graphics2D
                panel.paint(graphics2D);
                // return if page exists
                return Printable.PAGE_EXISTS;
            }
        });
        // Store printerDialog as boolean
        boolean returningResult = printerJob.printDialog();
        // check if dilog is showing
        if (returningResult) {
            // Use try catch exeption for failure
            try {
                // Now call print method inside printerJob to print
                printerJob.print();
            } catch (PrinterException printerException) {
//                JOptionPane.showMessageDialog(this, "Print Error: " + printerException.getMessage());
            }
        }
    }

    /*GUI*/
 /*NÉVJEGY - címkék formázása*/
    public static void jTextAreaNevjegyFormazBal(JTextArea cimkeNev, JPanel panelNev, Font betuNev, Dimension sorkoz) {
        cimkeNev.setFont(betuNev);
        cimkeNev.setOpaque(false);
//        cimkeNev.setLineWrap(true);
//        cimkeNev.setWrapStyleWord(true);//cimkeNev.setLineWrap(true);
        panelNev.add(cimkeNev);
        panelNev.add(Box.createRigidArea(sorkoz));
    }

    public static void jTextAreaNevjegyFormazJobb(JTextArea cimkeNev, JPanel panelNev, Font betuNev, Dimension sorkoz) {
        cimkeNev.setFont(betuNev);
        cimkeNev.setOpaque(false);
        cimkeNev.setLineWrap(true);
        cimkeNev.setWrapStyleWord(true);
        panelNev.add(cimkeNev);
        panelNev.add(Box.createRigidArea(sorkoz));
    }

    

    
}
