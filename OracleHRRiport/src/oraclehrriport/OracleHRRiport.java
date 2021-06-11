package oraclehrriport;

import java.awt.image.BufferedImage;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class OracleHRRiport extends javax.swing.JFrame /*implements ActionListener*/ {

    private List<OracleHRData> adatLista = null;
    private List<Department> departmentList = null;//EZ!!
    private Set<Department> activeDeps = null; //EZ!!
    /*PÉLDÁNYVÁLTOZÓK--------------------------------------------------------------------------------------------------------------------------------*/

    //több fülöhöz is szükséges példányváltozók
    private final Dimension dAblakMeret = new Dimension(1038, 677);//a főablak mérete
    private final int padding = dAblakMeret.width / 80;
    int res = (dAblakMeret.height / 40);
    private final Color vilagosKek = new Color(186, 201, 227);

    /*FIZETÉS FÜL - LISTA*/
    private DefaultListModel fizEloszlasListaSzalag = new DefaultListModel();    //jList22 mögötti adatszerk. (Lista)

    /*FIZETÉS FÜL - MIN ÉS MAX FIZETÉSEK*/
    private DefaultListModel eletpalyaFizListaSzalag = new DefaultListModel();     //jList5 mögötti adatszerk. (Életpálya szerinti min. és max. fiz.)
    private DefaultListModel tenylegesFizListaSzalag = new DefaultListModel();     //jList3 mögötti adatszerk. (Tényleges min. és max. fiz.) 

    /*FIZETÉS FÜL - ÁTLAGFIZETÉS RÉSZLEGENKÉNT*/
    private DefaultListModel fizReszlegenkentListaSzalag = new DefaultListModel();

    /*FIZETÉS FÜL - JUTALÉK ELŐFORDULÁS*/
    private DefaultListModel jutalekElofordListaSzalag = new DefaultListModel();
    private DefaultListModel jutalekMkListaSzalag = new DefaultListModel();

    /*NÉVJEGY FÜL*/
    private JList lsNevlista = new JList();
    private DefaultListModel nevlistaSzalag = new DefaultListModel();   //lsNevlista mögötti adatszerk. 
    private Font betuVastag = new Font("Tahoma", Font.BOLD, 14);
    private Font betuVekony = new Font("Tahoma", 0, 14);
    private Dimension sorKoz = new Dimension(dAblakMeret.width / 40, dAblakMeret.height / 40); //a címkék közti sorköz 
    private JTextArea taNevjegyNevCimke = new JTextArea("Név:");
    private JTextArea taNevjegyDACimke = new JTextArea("Dolgozóazonosító:");
    private JTextArea taNevjegyEmailCimke = new JTextArea("E-mail cím:");
    private JTextArea taNevjegyMunkakorCimke = new JTextArea("Munkakör:");
    private JTextArea taNevjegyReszlegCimke = new JTextArea("Részleg:");
    private JTextArea taNevjegyMenedzserCimke = new JTextArea("Menedzser:");
    private JTextArea taNevjegyRMenedzserCimke = new JTextArea("Részlegmenedzser:");
    private JTextArea taNevjegyTelefonszámCimke = new JTextArea("Telefonszám:");
    private JTextArea taNevjegyNev = new JTextArea("Vezetéknév Keresztnév");
    private JTextArea taNevjegyDA = new JTextArea("100");
    private JTextArea taNevjegyEmail = new JTextArea("valaki@oracle.com:");
    private JTextArea taNevjegyMunkakor = new JTextArea("Munkakör");
    private JTextArea taNevjegyReszleg = new JTextArea("Részleg");
    private JTextArea taNevjegyMenedzser = new JTextArea("Vezetéknév Keresztnév");
    private JTextArea taNevjegyRMenedzser = new JTextArea("Vezetéknév Keresztnév");
    private JTextArea taNevjegyTelefonszám = new JTextArea("Telefonszám");
    private int cimkeSzelesseg = taNevjegyMunkakor.getWidth();

    /*NÉVSOR fül*/
    private DefaultListModel ltKiir1 = new DefaultListModel(); //ide rakjuk a neveket a részlegek szerint
    private DefaultListModel ltKiir16 = new DefaultListModel(); //alkalmazottak megjelenítéshez szükséges adatszerkezet.
    private DefaultListModel ltKiir17 = new DefaultListModel(); //ide rakjuk az összes vezetőt
    private DefaultListModel ltKiir18 = new DefaultListModel(); //ide rakjuk az összes részlegvezetőt
    private DefaultListModel ltKiir19 = new DefaultListModel(); //ide rakjuk a munkakör szerinti neveket
    private DefaultComboBoxModel cbKiir7 = new DefaultComboBoxModel(); //itt jelennek meg a részlegnevek
    private DefaultComboBoxModel cbKiir2 = new DefaultComboBoxModel(); //itt jelennek meg munkakörnevek
    private DefaultComboBoxModel cbExtra2Kiir = new DefaultComboBoxModel(); //itt jelennek meg a dolgozók adatai, név és EmployeeId alapján

    /*KIVÁLOGATÁS fül (Kotán Péter)*/
    private Kivalogatas kivalogatas = new Kivalogatas();

    /*--------------------------------------------------------------------------------------------------------------------------------------------------*/
    public OracleHRRiport(OracleHRXML xmlModel) {
        adatLista = xmlModel.getDataList(); //ezt a metódust többször nem szabad meghívni
//    for (OracleHRData adat : adatLista)
//      System.out.println(adat.getEmployeeName());
        initComponents();
        setLocationRelativeTo(this);
        /*ÉRTEKEZLET - (Hergár Péter MAA) ---------------------------------------------------------------------------------------*/
        departmentList = Ertekezlet.createSortedDepartmentList(adatLista);//EZ!!
        activeDeps = new TreeSet<>(); //EZ!!
        initOwnComponents();//EZ!!
        setLocationRelativeTo(this);
//        jTabbedPane1.setSelectedIndex(4);
        setVisible(true);
        /*KIVÁLOGATÁS - (Kotán Péter) ---------------------------------------------------------------------------------------*/
        kivalogatas.departmentManagers(adatLista, jComboBox3, lReszlegvezAlk);
        kivalogatas.employeeManagers(adatLista, cbVezAlk, lVezAlkLista);
        kivalogatas.departmentAndJobtitle(adatLista, cbReszlegNev, cbMunkakor, lReszlMkLista);
        kivalogatas.employeeSalary(adatLista, spnrFizAlsoHatar, spnrFizFelsoHatar, lSzuresFizLista, btKivalogatasSzur, btKivalogatasTorol);

        /*NÉVSOR - (Dombai Norbert, Tibai Roland, Szabó Ádám Erik) -------------------------------------------------------------------------------------------------------- */
        jList1.setModel(ltKiir1);
        lNevsorAlkLista.setModel(ltKiir16);
        lNevsorVezLista.setModel(ltKiir17);
        lNevsReszlVezLista.setModel(ltKiir18);
        lNevsorMkLista.setModel(ltKiir19);
        cbNevsorReszlegenkent.setModel(cbKiir7);
        cnNevsorMunkakor.setModel(cbKiir2);
        cbKeresAlkalmazott.setModel(cbExtra2Kiir);

        List<String> osszesNevLista = Nevsor.alkalmazottakNevsor(adatLista); // alkalmazottak névsora
        int osszNevDarab = 0;
        for (int i = 0; i < osszesNevLista.size(); i++) {
            ltKiir16.addElement(osszesNevLista.get(i));
//            System.out.println(osszNevLista.get(i));
            osszNevDarab++;
        }
        lbNevsorAlkLetszam.setText(osszNevDarab + " fő");

        List<String> osszesNevListaReszlegvezetok = Nevsor.reszlegvezetokNevsor(adatLista); // részlegvezetők névsora
        int ReszlegvezetokNevDarab = 0;
        for (int i = 0; i < osszesNevListaReszlegvezetok.size(); i++) {
            ltKiir18.addElement(osszesNevListaReszlegvezetok.get(i));
//            System.out.println(osszesNevListaReszlegvezetok.get(i));
            ReszlegvezetokNevDarab++;
        }
        lbNevsReszlVez.setText(ReszlegvezetokNevDarab + " fő");

        List<String> osszesNevListaVezetok = Nevsor.vezetokNevsor(adatLista); // részlegvezetők névsora
        int VezetokNevDarab = 0;
        for (int i = 0; i < osszesNevListaVezetok.size(); i++) {
            ltKiir17.addElement(osszesNevListaVezetok.get(i));
//            System.out.println(osszesNevListaVezetok.get(i));
            VezetokNevDarab++;
        }
        lbNevsorVezLetszam.setText(VezetokNevDarab + " fő");

        List<String> osszesReszlegnev = Nevsor.reszlegNevek(adatLista);
//        System.out.println(osszesReszlegnev.size());
//        int lepesszam=0;
        for (int i = 0; i < osszesReszlegnev.size(); i++) {
            cbKiir7.addElement(osszesReszlegnev.get(i));
        }

        List<String> osszesMunkakorNev = Nevsor.munkakorNevek(adatLista);
//        System.out.println(osszesReszlegnev.size());
//        int lepesszam=0;
        for (int i = 0; i < osszesMunkakorNev.size(); i++) {
            cbKiir2.addElement(osszesMunkakorNev.get(i));
        }


        /*    EXTRA2 */
        List<String> osszesAlkalmazottNev = Nevsor.alkalmazottakNev(adatLista);
//        System.out.println(osszesReszlegnev.size());
//        int lepesszam=0;
        for (int i = 0; i < osszesAlkalmazottNev.size(); i++) {
            cbExtra2Kiir.addElement(osszesAlkalmazottNev.get(i));
        }

        cnNevsorMunkakor.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox2ItemStateChanged(evt);
            }
        });

        cbNevsorReszlegenkent.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox7ItemStateChanged(evt);
            }
        });

        cbKeresAlkalmazott.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxExtra2ItemStateChanged(evt);
            }
        });

        /*NÉVJEGY FÜL - GUI ELEMEK (Antal Adrienn)----------------------------------------------------------------------------------------------------------------------*/
        pnNevjegyFopanel.setLayout(new BorderLayout(padding, padding));
        pnNevjegyFopanel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));

        //a bal oldali konténer
        JPanel pnBalKontener = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnBalKontener.setBorder(BorderFactory.createTitledBorder(null, "Névlista", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, betuVastag));
        //belső boxkonténer
        JPanel pnNevvalasztoPanel = new JPanel(new BorderLayout());
//        pnNevvalasztoPanel.setLayout(new BoxLayout(pnNevvalasztoPanel, BoxLayout.PAGE_AXIS));
        pnNevvalasztoPanel.setPreferredSize(new Dimension(2 * dAblakMeret.width / 7, 5 * dAblakMeret.height / 7));
        gyartFopanelMargo(pnNevvalasztoPanel);
        pnBalKontener.add(pnNevvalasztoPanel);

        //a betűválasztó gombpanel 
        int gombokKoztiRes = dAblakMeret.width / 130;
        Font gombBetu = new Font("Tahoma", Font.BOLD, 10);
        Dimension dGombMeret = new Dimension(dAblakMeret.width / 22, dAblakMeret.width / 40);
        JPanel pnBetuGombPanel = new JPanel(new GridLayout(5, 6, gombokKoztiRes, gombokKoztiRes));
        List<String> nevKezdoBetuLista = Nevjegy.gyartNevKezdobetuLista(adatLista); //listába gyűjtjük az összes létező vezetéknévkezdőbetűt
        String elsoKezdoBetu = Nevjegy.gyartNevKezdobetuLista(adatLista).get(0);    //az ABC szerint legelső vezetéknévkezdőbetű lekérése
        List<JToggleButton> tbtGombLista = new ArrayList<>();                      //JToggleButton-ok memóriacímei tárolására szolgáló tömb
        for (String betu : nevKezdoBetuLista) {                                     //végighaladunk a kezdőbedük listáján
            JToggleButton tbtBetu = new JToggleButton();                           //létrejön a gomb objektum
            tbtBetu.setPreferredSize(dGombMeret);
            tbtBetu.setText(betu);
            tbtBetu.setFont(gombBetu);
            tbtBetu.addActionListener((e) -> {                              //aktuális JToggleButton eseménykezelése     
                nevlistaSzalag.clear();                                     //--> lsNevlista.getSelectedIndex()=-1 !!!; újraépítjük a listát
                for (int i = 0; i < tbtGombLista.size(); i++) {             //megnézzük mely gombok vannak beragadva az aktuális gombra való kattintáskor; végighaladunk a gombok listáján
                    if (tbtGombLista.get(i).isSelected()) {                   //amelyik gomb be van ragadva, akkor az azon feliratként szereplő betűvel kezdődő vezetéknevű dolgozók névsorát kimentjük a vezeteknevPerKezdobetuLista-ba                     
                        char kezdoBetu = tbtGombLista.get(i).getText().charAt(0);
                        List<Nevjegy> vezeteknevPerKezdobetuLista = Nevjegy.vezeteknevPerKezdobetuLista(adatLista, kezdoBetu);
                        for (Nevjegy rekord : vezeteknevPerKezdobetuLista) {
                            nevlistaSzalag.addElement(rekord.getVezetekNev() + ", " + rekord.getKeresztNev() + " (" + rekord.getDolgozoAzonosito() + ")"); //a névsor elemeit a listaszalagra pakoljuk
                        }
                    }
                }
                if (!(nevlistaSzalag.isEmpty())) {
                    lsNevlista.setSelectedIndex(0);   //ha nem üres a listaszalag, az első listaelem legyen kijelölve                 
                } else {
                    elsoNevjegyBetolt();   //ha amúgy üres lenne, akkor töltsük be az ABC szerinti legelső létező kezdőbetűhöz tartozó névsor legelső névjegyét
                    tbtGombLista.get(0).setSelected(true); //és ragadjon be az ABC szerinti legelső létező kezdőbetű gombja
                }

            });
            tbtGombLista.add(tbtBetu);
            pnBetuGombPanel.add(tbtBetu);
            if (betu.equals(elsoKezdoBetu)) {
                tbtBetu.setSelected(true);
                List<Nevjegy> vezeteknevPerKezdobetuLista = Nevjegy.vezeteknevPerKezdobetuLista(adatLista, elsoKezdoBetu.charAt(0));

                for (Nevjegy rekord : vezeteknevPerKezdobetuLista) {
                    nevlistaSzalag.addElement(rekord.getVezetekNev() + ", " + rekord.getKeresztNev() + " (" + rekord.getDolgozoAzonosito() + ")");
                }
            }
        }

        pnBetuGombPanel.setBorder(BorderFactory.createEmptyBorder(res, 0, res, 0));
        pnNevvalasztoPanel.add(pnBetuGombPanel, BorderLayout.NORTH);

        //a névlista
        JPanel pnListaPanel = new JPanel(new BorderLayout());
        lsNevlista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lsNevlista.setFont(betuVekony);
        pnListaPanel.add(new JScrollPane(lsNevlista));
        pnListaPanel.setBorder(BorderFactory.createEmptyBorder(res, 0, 0, 0));
        pnNevvalasztoPanel.add(pnListaPanel, BorderLayout.CENTER);
        lsNevlista.setModel(nevlistaSzalag);
        elsoNevjegyBetolt();
//        lsNevlista.addMouseListener(nevjegyListaElemKatt);
        lsNevlista.addListSelectionListener((e) -> {
            if (!(nevlistaSzalag.isEmpty())) {
                aktualisNevjegyBetolt();
            }
        });
        pnNevjegyFopanel.add(pnBalKontener, BorderLayout.LINE_START);

        //a jobb térfél konténere, amelybe a Névjegy majd belekerül       
        JPanel pnJobbKontener = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); //balra igazítom a főkonténert, fix pozícióba      
        pnNevjegyFopanel.add(pnJobbKontener, BorderLayout.CENTER);

        //Névjegy - belső keret     
        JPanel pnNevjegyKontener = new JPanel(new BorderLayout());
        pnNevjegyKontener.setPreferredSize(new Dimension(2 * dAblakMeret.width / 3, 3 * dAblakMeret.height / 5));
        pnNevjegyKontener.setBorder(BorderFactory.createTitledBorder(null, "Névjegy", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, betuVastag));
        pnJobbKontener.add(pnNevjegyKontener);

        //az Adatlap --> fejlesztés: át lehetne alakítani 3 oszlopos GridbagLayout-tá,és az első 2 oszlopot összevonni...
        JPanel pnNevjegyAdatlap = new JPanel(new BorderLayout());
        pnNevjegyAdatlap.setBorder(BorderFactory.createEmptyBorder(padding, padding, 0, 0));
        pnNevjegyKontener.add(pnNevjegyAdatlap);

        //a fotókeret
        JPanel pnNevjegyFotoKeret = new JPanel(new FlowLayout());
        pnNevjegyFotoKeret.setBorder(BorderFactory.createEmptyBorder(dAblakMeret.width / 45, dAblakMeret.width / 45, dAblakMeret.width / 45, dAblakMeret.width / 45));
        JLabel lbFelirat = new JLabel("Fotó helye", SwingConstants.CENTER);
        pnNevjegyFotoKeret.add(lbFelirat);
        lbFelirat.setFont(betuVastag);
        lbFelirat.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.lightGray));
        lbFelirat.setPreferredSize(new Dimension(dAblakMeret.width / 8, dAblakMeret.width / 8));
        JLabel lbUres = new JLabel("", SwingConstants.CENTER);
        lbUres.add(pnNevjegyFotoKeret);
        pnNevjegyAdatlap.add(pnNevjegyFotoKeret, BorderLayout.EAST);

        //a névjegycímkék konténere
        JPanel pnNevjegyCimkek = new JPanel();
        pnNevjegyCimkek.setLayout(new GridLayout(8, 2));

        //címkék  (jobb és bal o. oszlop)     
        Nevjegy.jTextAreaNevjegyFormazBal(taNevjegyNevCimke, pnNevjegyCimkek, betuVastag, sorKoz);
        Nevjegy.jTextAreaNevjegyFormazJobb(taNevjegyNev, pnNevjegyCimkek, betuVekony, sorKoz);
        Nevjegy.jTextAreaNevjegyFormazBal(taNevjegyDACimke, pnNevjegyCimkek, betuVastag, sorKoz);
        Nevjegy.jTextAreaNevjegyFormazJobb(taNevjegyDA, pnNevjegyCimkek, betuVekony, sorKoz);
        Nevjegy.jTextAreaNevjegyFormazBal(taNevjegyEmailCimke, pnNevjegyCimkek, betuVastag, sorKoz);
        Nevjegy.jTextAreaNevjegyFormazJobb(taNevjegyEmail, pnNevjegyCimkek, betuVekony, sorKoz);
        Nevjegy.jTextAreaNevjegyFormazBal(taNevjegyMunkakorCimke, pnNevjegyCimkek, betuVastag, sorKoz);
        Nevjegy.jTextAreaNevjegyFormazJobb(taNevjegyMunkakor, pnNevjegyCimkek, betuVekony, sorKoz);
        Nevjegy.jTextAreaNevjegyFormazBal(taNevjegyReszlegCimke, pnNevjegyCimkek, betuVastag, sorKoz);
        Nevjegy.jTextAreaNevjegyFormazJobb(taNevjegyReszleg, pnNevjegyCimkek, betuVekony, sorKoz);
        Nevjegy.jTextAreaNevjegyFormazBal(taNevjegyMenedzserCimke, pnNevjegyCimkek, betuVastag, sorKoz);
        Nevjegy.jTextAreaNevjegyFormazJobb(taNevjegyMenedzser, pnNevjegyCimkek, betuVekony, sorKoz);
        Nevjegy.jTextAreaNevjegyFormazBal(taNevjegyRMenedzserCimke, pnNevjegyCimkek, betuVastag, sorKoz);
        Nevjegy.jTextAreaNevjegyFormazJobb(taNevjegyRMenedzser, pnNevjegyCimkek, betuVekony, sorKoz);
        Nevjegy.jTextAreaNevjegyFormazBal(taNevjegyTelefonszámCimke, pnNevjegyCimkek, betuVastag, sorKoz);
        Nevjegy.jTextAreaNevjegyFormazJobb(taNevjegyTelefonszám, pnNevjegyCimkek, betuVekony, sorKoz);

        pnNevjegyCimkek.setBorder(BorderFactory.createEmptyBorder(dAblakMeret.width / 45, 0, dAblakMeret.width / 50, dAblakMeret.width / 50));
//        pnNevjegyCimkek.setBorder(BorderFactory.createEtchedBorder());
        pnNevjegyAdatlap.add(pnNevjegyCimkek, BorderLayout.WEST);

        //a névjegy gombpanele
        JPanel pnGombPanel = new JPanel();
        pnGombPanel.setLayout(new BoxLayout(pnGombPanel, BoxLayout.LINE_AXIS));
        pnGombPanel.setBorder(BorderFactory.createEmptyBorder(res, res, res, res));
        pnGombPanel.setBackground(vilagosKek);
        pnGombPanel.add(Box.createHorizontalGlue());
        JButton btMent = new JButton("Névjegy mentése");
        pnGombPanel.add(btMent);
        pnGombPanel.add(Box.createRigidArea(sorKoz));
        JButton btNyomtat = new JButton("Nyomtat");
        pnGombPanel.add(btNyomtat);
        pnGombPanel.add(Box.createRigidArea(sorKoz));
        JButton btSugo = new JButton("?");
        pnGombPanel.add(btSugo);
        btSugo.addActionListener((e) -> {
            String eleresiUtvonal = "src/Sugo/sugo-ful-nevjegy.html";
            sugotMegnyit(eleresiUtvonal);
        });

        pnNevjegyFopanel.add(pnGombPanel, BorderLayout.SOUTH);
        btMent.addActionListener((ActionEvent ev) -> {
            try {
                BufferedImage nevjegyFoto = new BufferedImage(pnNevjegyKontener.getWidth(), pnNevjegyKontener.getHeight(), BufferedImage.TYPE_INT_ARGB);
                pnNevjegyKontener.paint(nevjegyFoto.getGraphics()); //--> Graphics g = nevjegyFoto.getGraphics()
                File nevjegyFile = new File(taNevjegyNev.getText() + " névjegy .png");
                ImageIO.write(nevjegyFoto, "PNG", nevjegyFile);
                String eleresiUt = nevjegyFile.getAbsolutePath();
                JFrame fVisszajelzes = new JFrame();//            
                JOptionPane.showMessageDialog(fVisszajelzes, "Sikeres névjegygenerálás. Elérési útvonal: " + eleresiUt);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        btNyomtat.addActionListener((e) -> {
            Nevjegy.nyomtatPanelt(pnNevjegyAdatlap);
        });

        /*--------------------------------------------------------------------------------------------------------------------------------------------------*/
        setVisible(true);

        /*FIZETÉS FÜL - VEZÉRLŐ UTASÍTÁSOK (Antal Adrienn)--------------------------------------------------------------------------------------------------------------------------------*/
 /*ADATOK*/
        int maxSzamjegy = String.valueOf((Fizetes.szamitAdatok(adatLista)).get(3)).length();                //mindig az össz.fizetés lesz a leghosszabb szám
        List<Double> AlapAdatLista = Fizetes.szamitAdatok(adatLista);
        //adatok betöltése
        lbMinFiz.setText(extra.Format.right(AlapAdatLista.get(0), maxSzamjegy, 0) + "");     //0.elem: minimum fizetés
        lbMaxFiz.setText(extra.Format.right(AlapAdatLista.get(1), maxSzamjegy, 0) + "");     //1.elem: maximum fizetés
        lbAtlFiz.setText(extra.Format.right(AlapAdatLista.get(2), maxSzamjegy, 2) + "");    //2.elem: átlagfizetés
        lbOsszFiz.setText(extra.Format.right(AlapAdatLista.get(3), maxSzamjegy, 0) + "");     //3.elem: összes fizetés 

        //formázás
        lbLetszam.setText(extra.Format.right(adatLista.size(), maxSzamjegy) + " fő");
        lbMinFiz.setHorizontalAlignment(SwingConstants.RIGHT);
        lbMaxFiz.setHorizontalAlignment(SwingConstants.RIGHT);
        lbOsszFiz.setHorizontalAlignment(SwingConstants.RIGHT);
        lbAtlFiz.setHorizontalAlignment(SwingConstants.RIGHT);
        lbLetszam.setHorizontalAlignment(SwingConstants.RIGHT);

        /*LISTA*/
        List<String> fizHanyFoLista = Fizetes.fizetesHanyFo(adatLista);
        Fizetes.jListFeltolt(lFizeteseloszlasLista, fizEloszlasListaSzalag, fizHanyFoLista);

        /*TÉNYLEGES MIN ÉS MAX FIZETÉS MUNKAKÖRÖNKÉNT*/
        List<String> tenylegesFizIntervallumLista = Fizetes.kimutatasMunkakoronkenti(adatLista, "tenyleges"); //tényleges fiz. munkakörönként
        Fizetes.jListFeltolt(lTenylegesFizetesLista, tenylegesFizListaSzalag, tenylegesFizIntervallumLista);
        List<String> eletpalyaFizIntervallumLista = Fizetes.kimutatasMunkakoronkenti(adatLista, "eletpalya"); //életpálya szerinti fiz. munkakörönként
        Fizetes.jListFeltolt(lEletpalyaFizetesLista, eletpalyaFizListaSzalag, eletpalyaFizIntervallumLista);

        /*ÁTLAGFIZETÉS RÉSZLEGENKÉNT*/
        List<String> atlagFizetesReszlegenkentLista = Fizetes.kimutatasReszlegenkent(adatLista, "atlagfizetes");
        Fizetes.jListFeltolt(lAtlFizReszlegenkentLista, fizReszlegenkentListaSzalag, atlagFizetesReszlegenkentLista);

        /*JUTALÉKOK*/
        List<String> JutalekElofordulasLista = Fizetes.jutalekEloford(adatLista);
        Fizetes.jListFeltolt(lJutalekElofordulasLista, jutalekElofordListaSzalag, JutalekElofordulasLista);
        List<String> JutalekMunkakoronkentLista = Fizetes.jutalekMunkakoronkent(adatLista);
        Fizetes.jListFeltolt(lJutalekMunkakoronkentLista, jutalekMkListaSzalag, JutalekMunkakoronkentLista);

//        saját lemodellezés        
//        List<String> JutalekElofordulasLista = Fizetes.JutalekElofordulas(Fizetes.JutalekLekerdezes(adatLista, "elofordulas"));
//        Fizetes.jListFeltolt(jList23, listaSzalag23, JutalekElofordulasLista);
//        List<String> JutalekMunkakoronkentLista = Fizetes.JutalekMunkakoronkent(Fizetes.JutalekLekerdezes(adatLista, "munkakor"));
//        Fizetes.jListFeltolt(jList4, listaSzalag4, JutalekMunkakoronkentLista);
        /*--------------------------------------------------------------------------------------------------------------------------------------*/
 /*GRAFIKONOK------------------------------------------------------------------------------------------------------------------------------------------------------*/

 /*GRAFIKON - Átlagfizetés részlegenként (Antal Adrienn)*/
        String[][] grAtlFizPerReszlegTomb = Fizetes.keszitGrafikonAdatTomb(atlagFizetesReszlegenkentLista);
        DefaultCategoryDataset dcdGrafikon1Adatok = new DefaultCategoryDataset();
        Grafikon.letrehozOszlopDiagramAdatszerk(dcdGrafikon1Adatok, grAtlFizPerReszlegTomb, "Átlagfizetés");
        JFreeChart jfcAtlFizPerReszleg = ChartFactory.createBarChart("Átlagfizetés részlegenként", "Részleg", "Átlagfizetés (USD)", dcdGrafikon1Adatok, PlotOrientation.HORIZONTAL, true, true, false);
        Grafikon.oszlopDiagramFormaz(jfcAtlFizPerReszleg, pnGrAtlFizReszl, pnGrKontener1);

        /*GRAFIKON - Átlagfizetés munkakörönként (Antal Adrienn)*/
        List<String> atlagFizPerMk = Fizetes.kimutatasMunkakoronkenti(adatLista, "atlagfizpermk");
        String[][] grAtlagFizPerMkTomb = Fizetes.keszitGrafikonAdatTomb(atlagFizPerMk);
        DefaultCategoryDataset dcdGrafikon2Adatok = new DefaultCategoryDataset();
        Grafikon.letrehozOszlopDiagramAdatszerk(dcdGrafikon2Adatok, grAtlagFizPerMkTomb, "Átlagfizetés");
        JFreeChart jfcAtlagFizPerMk = ChartFactory.createBarChart("Átlagfizetés munkakörönként", "Munkakör", "Átlagfizetés (USD)", dcdGrafikon2Adatok, PlotOrientation.HORIZONTAL, true, true, false);
        Grafikon.oszlopDiagramFormaz(jfcAtlagFizPerMk, pnGrAtlFizMk, pnGrKontener1);

        /*GRAFIKON - Létszám részlegenként (Antal Adrienn)*/
        List<String> letszamPerReszleg = Fizetes.kimutatasReszlegenkent(adatLista, "letszam");
        String[][] grletszamPerReszlegTomb = Fizetes.keszitGrafikonAdatTomb(letszamPerReszleg);
        DefaultPieDataset dcdGrafikon3Adatok = new DefaultPieDataset();
        Grafikon.letrehozKordiagramAdatszerk(dcdGrafikon3Adatok, grletszamPerReszlegTomb);
        JFreeChart jfcLetszamPerReszleg = ChartFactory.createPieChart("Létszám részlegenként", dcdGrafikon3Adatok, true, true, true);
        Grafikon.korDiagramFormaz(jfcLetszamPerReszleg, pnGrLetszamReszl, pnGrKontener2, res);

        /*GRAFIKON - Létszám munkakörönként (Antal Adrienn)*/
        List<String> letszamPerMunkakor = Fizetes.kimutatasMunkakoronkenti(adatLista, "letszam");
        String[][] grletszamPerMunkakorTomb = Fizetes.keszitGrafikonAdatTomb(letszamPerMunkakor);
        DefaultPieDataset dcdGrafikon4Adatok = new DefaultPieDataset();
        Grafikon.letrehozKordiagramAdatszerk(dcdGrafikon4Adatok, grletszamPerMunkakorTomb);
        JFreeChart jfcLetszamPerMunkakor = ChartFactory.createPieChart("Létszám munkakörönként", dcdGrafikon4Adatok, true, true, true);
        Grafikon.korDiagramFormaz(jfcLetszamPerMunkakor, pnGrLetszamMk, pnGrKontener2, res);

//     
//
        /*--------------------------------------------------------------------------------------------------------------------------------------------------*/

 /*TÁBLÁZAT - (Antal Adrienn)*/
        //tab
        JPanel pnTablazatTab = new JPanel(new BorderLayout());
        JPanel pnTablazatKontener = new JPanel(new BorderLayout());
        pnTablazatTab.add(pnTablazatKontener, BorderLayout.CENTER);
        JPanel pnTablazatPanel = new JPanel(new BorderLayout());
        gyartFopanelMargo(pnTablazatTab);
        tpnOHRTFoPanel.addTab("Táblázat", pnTablazatTab);
        pnTablazatKontener.add(pnTablazatPanel, BorderLayout.CENTER);
        pnTablazatKontener.setBorder(BorderFactory.createTitledBorder(null, "Táblázat", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, betuVastag));
        JTable tbAdatTablazat = new JTable();
        tablazatKeszit(tbAdatTablazat);
        pnTablazatPanel.add(tbAdatTablazat);
        pnTablazatPanel.setBorder(BorderFactory.createEmptyBorder(res, res, res, res));
        tbAdatTablazat.setPreferredScrollableViewportSize(tbAdatTablazat.getPreferredSize()); //töltse ki a teret        
        JScrollPane spCsuszka = new JScrollPane(tbAdatTablazat);
        pnTablazatPanel.add(spCsuszka, BorderLayout.CENTER);
        spCsuszka.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        spCsuszka.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        DefaultTableCellRenderer dtcrBal = new DefaultTableCellRenderer(); //5.oszlop balra rendezés
        dtcrBal.setHorizontalAlignment(JLabel.LEFT);
        tbAdatTablazat.getColumnModel().getColumn(4).setCellRenderer(dtcrBal);
        JTableHeader thFejlec = tbAdatTablazat.getTableHeader();
        thFejlec.setBackground(vilagosKek);
        thFejlec.setFont(betuVastag);

        //a Táblázat fül gombpanele
        JPanel pnTablazatGombPanel = new JPanel();
        pnTablazatGombPanel.setLayout(new BoxLayout(pnTablazatGombPanel, BoxLayout.LINE_AXIS));
        pnTablazatGombPanel.setBorder(BorderFactory.createEmptyBorder(res, res, res, res));
        pnTablazatGombPanel.setBackground(vilagosKek);
        pnTablazatGombPanel.add(Box.createHorizontalGlue());
        JButton btTablazatSugoGomb = new JButton("?");
        pnTablazatGombPanel.add(btTablazatSugoGomb);
        pnTablazatTab.add(pnTablazatGombPanel, BorderLayout.SOUTH);
        btTablazatSugoGomb.addActionListener((e) -> {
            String eleresiUtvonal = "src/Sugo/sugo-ful-tablazat.html";
            sugotMegnyit(eleresiUtvonal);
        });

        /*TESZT*/
//                
//        for (OracleHRData adat : adatLista) {
//            System.out.println(extra.Format.left(adat.getEmployeeName(), 20) + " " + extra.Format.left(adat.getEmployeeID(), 5) + " " + extra.Format.left(adat.getSalary(), 10, 2)
//                    + " " + extra.Format.left(adat.getMinSalary(), 10, 2) + " "
//                    + extra.Format.left(adat.getMaxSalary(), 10, 2) + " " + extra.Format.left(adat.getJobTitle(), 40) + " "
//                    + extra.Format.left(adat.getDepartmentName(), 15) + " " + extra.Format.left(adat.getDepartmentID(), 15) + " " + extra.Format.left(adat.getEmployeeManagerID(), 15)
//                    + " " + extra.Format.left(Double.toString(adat.getCommission()), 15));
//        }
//        
//            System.out.println(adatLista.get(i).getCommission());
//        System.out.println("---------");
//
//        System.out.println(adatLista.get(1).getEmployeeName() + " " + adatLista.get(1).getEmployeeLastName() + ", " + adatLista.get(1).getEmployeeFirstAndMiddleName());
//        System.out.println("m: " + Nevjegy.keresMenedzserNevDolgozoIDAlapjan(101, adatLista));
//
//         String [][] grafikonTomb = Fizetes.keszitGrafikonAdatTomb(atlagFizetesReszlegenkentLista);
//         for (int i = 0; i < grafikonTomb.length; i++) {
//             System.out.println(grafikonTomb[i][0]+" "+grafikonTomb[i][1]);
////        }
//        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy");
//        String tesztString = dateFormatter.format(adatLista.get(50).getHireDate().getTime());
//        System.out.println(tesztString);
    }

    /*LOKÁLIS METÓDUSOK*/
 /*NÉVSOR - (Dombai Norbert, Tibai Roland, Szabó Ádám Erik)* -------------------------------------------------------------------------*/
//    private void jComboBox7ItemStateChanged(java.awt.event.ItemEvent evt) {                                            
//        String reszleg=(String) cbNevsorReszlegenkent.getSelectedItem();
//        int szoreszlet= reszleg.lastIndexOf(" (");
//        reszleg=reszleg.substring(0,szoreszlet);
//        ltKiir1.clear();
//        for (OracleHRData elem : adatLista) {
//            if(elem.getDepartmentName().equals(reszleg))
//                ltKiir1.addElement(elem.getEmployeeName());
//            
//        }  
//    }                                           
//    
//    private void jComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {
//        String munkakor=(String) cnNevsorMunkakor.getSelectedItem();
//        int szoreszlet= munkakor.lastIndexOf(" (");
//        munkakor=munkakor.substring(0,szoreszlet);
//        ltKiir19.clear();
//        for (OracleHRData elem : adatLista) {
//            if(elem.getJobTitle().equals(munkakor))
//                ltKiir19.addElement(elem.getEmployeeName());
//            
//        }  
//    }    
    private void jComboBox7ItemStateChanged(java.awt.event.ItemEvent evt) {
        String reszleg = (String) cbNevsorReszlegenkent.getSelectedItem();
        int szoreszlet = reszleg.lastIndexOf(" (");
        reszleg = reszleg.substring(0, szoreszlet);
        List<String> nevKiirReszleg = new ArrayList<>();
        for (OracleHRData elem : adatLista) {
            if (elem.getDepartmentName().equals(reszleg)) {
                nevKiirReszleg.add(elem.getEmployeeName());
            }

        }
        Collections.sort(nevKiirReszleg);
        ltKiir1.clear();
        for (String nev : nevKiirReszleg) {
            ltKiir1.addElement(nev);
        }
    }

    private void jComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {
        String munkakor = (String) cnNevsorMunkakor.getSelectedItem();
        int szoreszlet = munkakor.lastIndexOf(" (");
        munkakor = munkakor.substring(0, szoreszlet);
        List<String> nevKiirMunkakor = new ArrayList<>();
        for (OracleHRData elem : adatLista) {
            if (elem.getJobTitle().equals(munkakor)) {
                nevKiirMunkakor.add(elem.getEmployeeName());
            }

        }
        Collections.sort(nevKiirMunkakor);
        ltKiir19.clear();
        for (String nev : nevKiirMunkakor) {
            ltKiir19.addElement(nev);
        }
    }

    private void jComboBoxExtra2ItemStateChanged(ItemEvent evt) {
        String id = (String) cbKeresAlkalmazott.getSelectedItem();
        int nevReszlet = id.lastIndexOf(" (");
        id = id.substring(0, nevReszlet);
        for (OracleHRData elem : adatLista) {
            if (elem.getEmployeeName().equals(id)) {
                pnKeresAlkNev.setText(elem.getEmployeeName());
            }

            if (elem.getEmployeeName().equals(id)) {
                pnKeresAlkEmail.setText(elem.getEmail().toLowerCase() + "@oracle.hu");
            }

            if (elem.getEmployeeName().equals(id)) {
                pnKeresAlkMunkakor.setText(elem.getJobTitle());
            }

            if (elem.getEmployeeName().equals(id)) {
                pnKeresAlkReszleg.setText(elem.getDepartmentName());
            }

        }
    }

    /* JELENTÉS - (Vitéz Dóra, Balogh József, Szerencsés Bálint) ------------------------------------------------------------------------*/
    public String Lekerdezes1() {

        StringBuilder L1 = new StringBuilder();
        int i = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        ArrayList hasznalt1 = new ArrayList<>();

        while (i < adatLista.size()) {
            String bev = formatter.format(adatLista.get(i).getHireDate().getTime());
            while (!hasznalt1.contains(bev)) {
                ArrayList bevNév = new ArrayList<>();
                while (i < adatLista.size() && formatter.format(adatLista.get(i).getHireDate().getTime()).equals(bev)) {
                    bevNév.add(adatLista.get(i).getEmployeeName() + " (" + adatLista.get(i).getJobTitle() + ")");
                    i++;
                }

                for (int j = i; j < adatLista.size(); j++) {
                    if (formatter.format(adatLista.get(j).getHireDate().getTime()).equals(bev)) {
                        bevNév.add(adatLista.get(j).getEmployeeName() + " (" + adatLista.get(j).getJobTitle() + ")");
                    }
                }

                String ev = "\n" + bev + " (" + bevNév.size() + " fő)\n";
                L1.append(ev + "  " + String.join("\n  ", bevNév));
                hasznalt1.add(bev);
            }
            i++;
        }
        return L1.toString();
    }

    public String Lekerdezes2() {
        StringBuilder L2 = new StringBuilder();
        int i = 0;
        int atlag;
        ArrayList hasznalt2 = new ArrayList<>();

        while (i < adatLista.size()) {
            atlag = 0;
            String amkör = adatLista.get(i).getJobTitle();
            while (!hasznalt2.contains(amkör)) {
                ArrayList amkörNév = new ArrayList<>();
                while (i < adatLista.size() && adatLista.get(i).getJobTitle().equals(amkör)) {
                    amkörNév.add(adatLista.get(i).getEmployeeName() + " (" + adatLista.get(i).getSalary() + ")");
                    atlag += (int) adatLista.get(i).getSalary();
                    i++;
                }

                for (int j = i; j < adatLista.size(); j++) {
                    if (adatLista.get(j).getJobTitle().equals(amkör)) {
                        amkörNév.add(adatLista.get(j).getEmployeeName() + " (" + adatLista.get(j).getSalary() + ")");
                        atlag += (int) adatLista.get(j).getSalary();
                    }
                }

                String mkör = "\n" + amkör + " (" + amkörNév.size() + " fő, " + (atlag / amkörNév.size()) + ")\n";
                L2.append(mkör + "  " + String.join("\n  ", amkörNév));
                hasznalt2.add(amkör);
            }
        }
        return L2.toString();
    }

    /*GUI - (Antal Adrienn)---------------------------------------------------------------------------------------------------*/
    private void gyartFopanelMargo(JPanel panelNev) {
        panelNev.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
    }

    /*NEVJEGY - (Antal Adrienn)*/
    private void aktualisNevjegyBetolt() {
        if (!(lsNevlista.getSelectedIndex() == -1)) { //mivel a clear() -1-re állítja a getSelectedIndex() visszatérési értékét
            //ha van kijelölt elem a listában
            String aktElem = (String) lsNevlista.getSelectedValue();
            Nevjegy dolgozoNevjegy = Nevjegy.listaElembolNevjegy(aktElem, adatLista);
            taNevjegyNev.setText(dolgozoNevjegy.getKeresztNev() + " " + dolgozoNevjegy.getVezetekNev());
            taNevjegyDA.setText(Integer.toString(dolgozoNevjegy.getDolgozoAzonosito()));
            taNevjegyEmail.setText(dolgozoNevjegy.getEmailCim());
            taNevjegyMunkakor.setText(dolgozoNevjegy.getMunkakor());
            taNevjegyReszleg.setText(dolgozoNevjegy.getReszleg());
            String menedzserNeve = dolgozoNevjegy.getMenedzserNeve();
            if (menedzserNeve.equals("")) {
                taNevjegyMenedzser.setText(" ");
            } else {
                taNevjegyMenedzser.setText(dolgozoNevjegy.getMenedzserNeve());
            }
            taNevjegyRMenedzser.setText(dolgozoNevjegy.getReszlegMenedzserNeve());
            taNevjegyTelefonszám.setText(dolgozoNevjegy.getTelefonszam());
        }
    }

    private void elsoNevjegyBetolt() {
        String elsoKezdoBetu = Nevjegy.gyartNevKezdobetuLista(adatLista).get(0); //az előforduló vez.nevek közül ABC szerint a legelső kezdőbetű
        List<Nevjegy> vezeteknevPerKezdobetuLista = Nevjegy.vezeteknevPerKezdobetuLista(adatLista, elsoKezdoBetu.charAt(0)); //az legelső kezdőbetűvel kezdődő vez.nevű dolgozók listája
        for (Nevjegy rekord : vezeteknevPerKezdobetuLista) {
            nevlistaSzalag.addElement(rekord.getVezetekNev() + ", " + rekord.getKeresztNev() + " (" + rekord.getDolgozoAzonosito() + ")"); //az előző lista alapján Névjegy objektumok legyártása, listaszalagra pakolása
            String aktElem = (String) nevlistaSzalag.get(0);                                    //a listaszalag legelső elemének kijelöltté tétele    
            Nevjegy dolgozoNevjegy = Nevjegy.listaElembolNevjegy(aktElem, adatLista);           //a listaszalag legelső eleméhez Névjegy objektumot gyártunk
            taNevjegyNev.setText(dolgozoNevjegy.getKeresztNev() + " " + dolgozoNevjegy.getVezetekNev());
            taNevjegyDA.setText(Integer.toString(dolgozoNevjegy.getDolgozoAzonosito()));
            taNevjegyEmail.setText(dolgozoNevjegy.getEmailCim());
            taNevjegyMunkakor.setText(dolgozoNevjegy.getMunkakor());
            taNevjegyReszleg.setText(dolgozoNevjegy.getReszleg());
            String menedzserNeve = dolgozoNevjegy.getMenedzserNeve();
            if (menedzserNeve.equals("")) {
                taNevjegyMenedzser.setText(" ");
            } else {
                taNevjegyMenedzser.setText(dolgozoNevjegy.getMenedzserNeve());
            }
            taNevjegyRMenedzser.setText(dolgozoNevjegy.getReszlegMenedzserNeve());
            taNevjegyTelefonszám.setText(dolgozoNevjegy.getTelefonszam());
        }
        lsNevlista.setSelectedIndex(0);                                     //a lista legelső eleme legyen kijelölt
    }

    /*SÚGÓ - (módosítás: Antal Adrienn, Hergár Péter)*/
    private void sugotMegnyit(final String HTML) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                File f = new File(HTML);
                if (!f.exists()) {
                    JFrame fVisszajelzes = new JFrame();//            
                    JOptionPane.showMessageDialog(fVisszajelzes, "A dokumentum nem található. Vegye fel a kapcsolatot a Helpdeskkel!");
                } else {
                    URI u = f.toURI();
                    Desktop.getDesktop().browse(u);
                }
            } catch (/*URISyntaxException | */IOException e) {
                e.printStackTrace(); //ez későbbi részprobléma, egyelőre jó lesz így
            }
        }
    }

    /*TÁBLÁZAT - (Antal Adrienn)*/
    private void tablazatKeszit(JTable tbAdatTablazat) {

        tbAdatTablazat.setModel(new DefaultTableModel());
        String[] oszlopTomb = {"Név", "Részleg", "Munkakör", "E-mail", "Fizetés"};
        String[] oszlopTipusNevTomb = {"java.lang.String", "java.lang.String",
            "java.lang.String", "java.lang.String", "java.lang.Integer"};
        Class[] oszlopTipusTomb = new Class[oszlopTipusNevTomb.length];
        try {
            for (int i = 0; i < oszlopTipusTomb.length; i++) {
                oszlopTipusTomb[i] = Class.forName(oszlopTipusNevTomb[i]);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); //ez későbbi részprobléma, egyelőre jó lesz így
        }
        ArrayList<Object[]> adatLista2 = new ArrayList<>();
        for (OracleHRData adat : adatLista) {
            Object[] rekord = {adat.getEmployeeName(), adat.getDepartmentName(),
                adat.getJobTitle(), adat.getEmail().toLowerCase() + "@oracle.hu",
                adat.getSalary()};
            adatLista2.add(rekord);
        }
        Object[][] adatTomb = new Object[adatLista2.size()][oszlopTomb.length];
        for (int i = 0; i < adatTomb.length; i++) {
            adatTomb[i] = adatLista2.get(i);
        }
        DefaultTableModel dtm = new DefaultTableModel(adatTomb, oszlopTomb) {
            Class[] types = oszlopTipusTomb;

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tbAdatTablazat.setModel(dtm);
        tbAdatTablazat.setAutoCreateRowSorter(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tpnOHRTFoPanel = new javax.swing.JTabbedPane();
        pnFizetesFopanel = new javax.swing.JPanel();
        pnAlapAdatokKontener = new javax.swing.JPanel();
        lbMinFizCimke = new javax.swing.JLabel();
        lbMaxFizCimke = new javax.swing.JLabel();
        lbMinFiz = new javax.swing.JLabel();
        lbMaxFiz = new javax.swing.JLabel();
        lbAtlFizCimke = new javax.swing.JLabel();
        lbAtlFiz = new javax.swing.JLabel();
        lbOsszFizCimke = new javax.swing.JLabel();
        lbOsszFiz = new javax.swing.JLabel();
        lbLetszamCimke = new javax.swing.JLabel();
        lbLetszam = new javax.swing.JLabel();
        pnListaKontener = new javax.swing.JPanel();
        spFizeteseloszlasLista = new javax.swing.JScrollPane();
        lFizeteseloszlasLista = new javax.swing.JList<>();
        pnMinMaxFizMkKontener = new javax.swing.JPanel();
        lbTenylegesFiz = new javax.swing.JLabel();
        spTenylegesFizetesLista = new javax.swing.JScrollPane();
        lTenylegesFizetesLista = new javax.swing.JList<>();
        lbEletpalyaFiz = new javax.swing.JLabel();
        spEletpalyaFizetesLista = new javax.swing.JScrollPane();
        lEletpalyaFizetesLista = new javax.swing.JList<>();
        pnJutalekokKontener = new javax.swing.JPanel();
        spJutalekElofordulasLista = new javax.swing.JScrollPane();
        lJutalekElofordulasLista = new javax.swing.JList<>();
        lbElofordCimke = new javax.swing.JLabel();
        lbMkCimke = new javax.swing.JLabel();
        spJutalekMunkakoronkentLista = new javax.swing.JScrollPane();
        lJutalekMunkakoronkentLista = new javax.swing.JList<>();
        pnAtlFizReszlKontener = new javax.swing.JPanel();
        spAtlFizReszlegenkentLista = new javax.swing.JScrollPane();
        lAtlFizReszlegenkentLista = new javax.swing.JList<>();
        btFizetesSugo = new javax.swing.JButton();
        pnKivalogatasFopanel = new javax.swing.JPanel();
        pnSzuresFizKontener = new javax.swing.JPanel();
        lbFizAlsoHatar = new javax.swing.JLabel();
        lbFizFelsoHatar = new javax.swing.JLabel();
        spnrFizAlsoHatar = new javax.swing.JSpinner();
        spnrFizFelsoHatar = new javax.swing.JSpinner();
        spSzuresFizLista = new javax.swing.JScrollPane();
        lSzuresFizLista = new javax.swing.JList<>();
        btKivalogatasTorol = new javax.swing.JButton();
        btKivalogatasSzur = new javax.swing.JButton();
        pnlReszlegvezAlkKontener = new javax.swing.JPanel();
        jComboBox3 = new javax.swing.JComboBox<>();
        spReszlegvezAlk = new javax.swing.JScrollPane();
        lReszlegvezAlk = new javax.swing.JList<>();
        pnVezAlkKontener = new javax.swing.JPanel();
        cbVezAlk = new javax.swing.JComboBox<>();
        spVezAlkLista = new javax.swing.JScrollPane();
        lVezAlkLista = new javax.swing.JList<>();
        pnReszlMkKontener = new javax.swing.JPanel();
        cbReszlegNev = new javax.swing.JComboBox<>();
        spReszlMkLista = new javax.swing.JScrollPane();
        lReszlMkLista = new javax.swing.JList<>();
        cbMunkakor = new javax.swing.JComboBox<>();
        btKivalogatasSugo = new javax.swing.JButton();
        pnErtekezletFopanel = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jScrollPane20 = new javax.swing.JScrollPane();
        jList20 = new javax.swing.JList<>();
        jPanel23 = new javax.swing.JPanel();
        jScrollPane21 = new javax.swing.JScrollPane();
        jList21 = new javax.swing.JList<>();
        jPanel24 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        btErtekezletSugo1 = new javax.swing.JButton();
        pnBelepesFopanel = new javax.swing.JPanel();
        jPanel38 = new javax.swing.JPanel();
        jScrollPane28 = new javax.swing.JScrollPane();
        jList28 = new javax.swing.JList<>();
        jComboBox6 = new javax.swing.JComboBox<>();
        jPanel39 = new javax.swing.JPanel();
        jScrollPane29 = new javax.swing.JScrollPane();
        jList29 = new javax.swing.JList<>();
        jPanel40 = new javax.swing.JPanel();
        jScrollPane30 = new javax.swing.JScrollPane();
        jList30 = new javax.swing.JList<>();
        jPanel41 = new javax.swing.JPanel();
        jScrollPane31 = new javax.swing.JScrollPane();
        jList31 = new javax.swing.JList<>();
        pnNevsorFopanel = new javax.swing.JPanel();
        pnNevsorReszlKontener = new javax.swing.JPanel();
        spNevsorReszlLista = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        cbNevsorReszlegenkent = new javax.swing.JComboBox<>();
        pnNevsorAlkKontener = new javax.swing.JPanel();
        spNevsorAlkLista = new javax.swing.JScrollPane();
        lNevsorAlkLista = new javax.swing.JList<>();
        lbNevsAlkLetszCimke = new javax.swing.JLabel();
        lbNevsorAlkLetszam = new javax.swing.JLabel();
        pnNevsorVezKontener = new javax.swing.JPanel();
        spNevsorVezLista = new javax.swing.JScrollPane();
        lNevsorVezLista = new javax.swing.JList<>();
        lbNevsVezLetszCimke = new javax.swing.JLabel();
        lbNevsorVezLetszam = new javax.swing.JLabel();
        pnNevsorReszlVKontener = new javax.swing.JPanel();
        spNevsReszlVezLista = new javax.swing.JScrollPane();
        lNevsReszlVezLista = new javax.swing.JList<>();
        lbNevsReszlVezCimke = new javax.swing.JLabel();
        lbNevsReszlVez = new javax.swing.JLabel();
        pnNevsorMkKontener = new javax.swing.JPanel();
        spNevsorMkLista = new javax.swing.JScrollPane();
        lNevsorMkLista = new javax.swing.JList<>();
        cnNevsorMunkakor = new javax.swing.JComboBox<>();
        btNevsorSugo = new javax.swing.JButton();
        pnJelentesFopanel = new javax.swing.JPanel();
        jPanel35 = new javax.swing.JPanel();
        jScrollPane25 = new javax.swing.JScrollPane();
        jList25 = new javax.swing.JList<>();
        jPanel36 = new javax.swing.JPanel();
        jScrollPane26 = new javax.swing.JScrollPane();
        jList26 = new javax.swing.JList<>();
        pnGrafikonFopanel = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        pnGrKontener1 = new javax.swing.JPanel();
        lbGrAtlFizReszl = new javax.swing.JLabel();
        pnGrAtlFizReszl = new javax.swing.JPanel();
        lbGrAtlFizMk = new javax.swing.JLabel();
        pnGrAtlFizMk = new javax.swing.JPanel();
        pnGrKontener2 = new javax.swing.JPanel();
        lbGrLetszamReszl = new javax.swing.JLabel();
        pnGrLetszamReszl = new javax.swing.JPanel();
        lbGrLetszamMk = new javax.swing.JLabel();
        pnGrLetszamMk = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jComboBox10 = new javax.swing.JComboBox<>();
        jPanel37 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane27 = new javax.swing.JScrollPane();
        jList27 = new javax.swing.JList<>();
        pnKeresesFopanel = new javax.swing.JPanel();
        pnKeresesKontener = new javax.swing.JPanel();
        cbKeresAlkalmazott = new javax.swing.JComboBox<>();
        pnKeresAlkNevCimke = new javax.swing.JLabel();
        pnKeresAlkEmailCimke = new javax.swing.JLabel();
        pnKeresAlkMkCimke = new javax.swing.JLabel();
        pnKeresAlkReszlCimke = new javax.swing.JLabel();
        pnKeresAlkNev = new javax.swing.JLabel();
        pnKeresAlkEmail = new javax.swing.JLabel();
        pnKeresAlkMunkakor = new javax.swing.JLabel();
        pnKeresAlkReszleg = new javax.swing.JLabel();
        jPanel43 = new javax.swing.JPanel();
        jPanel44 = new javax.swing.JPanel();
        jPanel45 = new javax.swing.JPanel();
        pnNevjegyFopanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Oracle HR Riport 1.0");
        setResizable(false);
        setSize(new java.awt.Dimension(1024, 0));

        pnAlapAdatokKontener.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Alapadatok", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        lbMinFizCimke.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbMinFizCimke.setText("Mini. fizetés:");
        lbMinFizCimke.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        lbMaxFizCimke.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbMaxFizCimke.setText("Max. fizetés:");
        lbMaxFizCimke.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        lbMinFiz.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbMinFiz.setText("2400");

        lbMaxFiz.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbMaxFiz.setText("24000");

        lbAtlFizCimke.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbAtlFizCimke.setText("Átlagfizetés:");
        lbAtlFizCimke.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        lbAtlFiz.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbAtlFiz.setText("6314.13");

        lbOsszFizCimke.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbOsszFizCimke.setText("Összes fiz.:");
        lbOsszFizCimke.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        lbOsszFiz.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbOsszFiz.setText("640000");

        lbLetszamCimke.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbLetszamCimke.setText("Létszám:");
        lbLetszamCimke.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        lbLetszam.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbLetszam.setText("106 fő");

        javax.swing.GroupLayout pnAlapAdatokKontenerLayout = new javax.swing.GroupLayout(pnAlapAdatokKontener);
        pnAlapAdatokKontener.setLayout(pnAlapAdatokKontenerLayout);
        pnAlapAdatokKontenerLayout.setHorizontalGroup(
            pnAlapAdatokKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnAlapAdatokKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnAlapAdatokKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnAlapAdatokKontenerLayout.createSequentialGroup()
                        .addGroup(pnAlapAdatokKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbMinFizCimke, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbAtlFizCimke)
                            .addComponent(lbMaxFizCimke, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(pnAlapAdatokKontenerLayout.createSequentialGroup()
                        .addComponent(lbOsszFizCimke, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(25, 25, 25))
                    .addGroup(pnAlapAdatokKontenerLayout.createSequentialGroup()
                        .addComponent(lbLetszamCimke, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(29, 29, 29)))
                .addGroup(pnAlapAdatokKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lbOsszFiz, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbAtlFiz, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                    .addComponent(lbMinFiz, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbMaxFiz, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbLetszam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        pnAlapAdatokKontenerLayout.setVerticalGroup(
            pnAlapAdatokKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnAlapAdatokKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnAlapAdatokKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbMinFizCimke)
                    .addComponent(lbMinFiz))
                .addGap(11, 11, 11)
                .addGroup(pnAlapAdatokKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbMaxFizCimke)
                    .addComponent(lbMaxFiz))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnAlapAdatokKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbAtlFizCimke)
                    .addComponent(lbAtlFiz))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnAlapAdatokKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbOsszFizCimke)
                    .addComponent(lbOsszFiz))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnAlapAdatokKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbLetszamCimke)
                    .addComponent(lbLetszam))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnListaKontener.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Lista", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        lFizeteseloszlasLista.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lFizeteseloszlasLista.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Fiz1 (2 fő)", "Fiz2 (5 fő)", "...", "Fiz32 (1 fő)" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lFizeteseloszlasLista.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spFizeteseloszlasLista.setViewportView(lFizeteseloszlasLista);

        javax.swing.GroupLayout pnListaKontenerLayout = new javax.swing.GroupLayout(pnListaKontener);
        pnListaKontener.setLayout(pnListaKontenerLayout);
        pnListaKontenerLayout.setHorizontalGroup(
            pnListaKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnListaKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spFizeteseloszlasLista)
                .addContainerGap())
        );
        pnListaKontenerLayout.setVerticalGroup(
            pnListaKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnListaKontenerLayout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addComponent(spFizeteseloszlasLista, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58))
        );

        pnMinMaxFizMkKontener.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Minimum és maximum fizetések munkakörönként", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        lbTenylegesFiz.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbTenylegesFiz.setText("Tényleges:");

        lTenylegesFizetesLista.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lTenylegesFizetesLista.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Mkor1 (2400, 3600)", "Mkor2 (6000, 6000)", "Mkor3 (..., ...)" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lTenylegesFizetesLista.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spTenylegesFizetesLista.setViewportView(lTenylegesFizetesLista);

        lbEletpalyaFiz.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbEletpalyaFiz.setText("Életpálya szerint:");

        lEletpalyaFizetesLista.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lEletpalyaFizetesLista.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Mkor1 (2400, 3600)", "Mkor2 (5000, 6000)", "Mkor3 (..., ...)" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lEletpalyaFizetesLista.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spEletpalyaFizetesLista.setViewportView(lEletpalyaFizetesLista);

        javax.swing.GroupLayout pnMinMaxFizMkKontenerLayout = new javax.swing.GroupLayout(pnMinMaxFizMkKontener);
        pnMinMaxFizMkKontener.setLayout(pnMinMaxFizMkKontenerLayout);
        pnMinMaxFizMkKontenerLayout.setHorizontalGroup(
            pnMinMaxFizMkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnMinMaxFizMkKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnMinMaxFizMkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spTenylegesFizetesLista)
                    .addGroup(pnMinMaxFizMkKontenerLayout.createSequentialGroup()
                        .addComponent(lbTenylegesFiz, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 124, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(pnMinMaxFizMkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbEletpalyaFiz, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spEletpalyaFizetesLista, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        pnMinMaxFizMkKontenerLayout.setVerticalGroup(
            pnMinMaxFizMkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnMinMaxFizMkKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnMinMaxFizMkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnMinMaxFizMkKontenerLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(spEletpalyaFizetesLista, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                    .addGroup(pnMinMaxFizMkKontenerLayout.createSequentialGroup()
                        .addGroup(pnMinMaxFizMkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbTenylegesFiz)
                            .addComponent(lbEletpalyaFiz))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(spTenylegesFizetesLista)))
                .addContainerGap())
        );

        pnJutalekokKontener.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Jutalékok", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        lJutalekElofordulasLista.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lJutalekElofordulasLista.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "5% (5 fő)", "10% (3 fő)", "..." };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lJutalekElofordulasLista.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spJutalekElofordulasLista.setViewportView(lJutalekElofordulasLista);

        lbElofordCimke.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbElofordCimke.setText("Előfordulás:");

        lbMkCimke.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbMkCimke.setText("Munkakörönként:");

        lJutalekMunkakoronkentLista.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lJutalekMunkakoronkentLista.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Mkor1 (5%)", "Mkor2 (10%)", "Mkor3 (...)" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lJutalekMunkakoronkentLista.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spJutalekMunkakoronkentLista.setViewportView(lJutalekMunkakoronkentLista);

        javax.swing.GroupLayout pnJutalekokKontenerLayout = new javax.swing.GroupLayout(pnJutalekokKontener);
        pnJutalekokKontener.setLayout(pnJutalekokKontenerLayout);
        pnJutalekokKontenerLayout.setHorizontalGroup(
            pnJutalekokKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnJutalekokKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnJutalekokKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spJutalekElofordulasLista, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(lbElofordCimke, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(pnJutalekokKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbMkCimke, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spJutalekMunkakoronkentLista, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnJutalekokKontenerLayout.setVerticalGroup(
            pnJutalekokKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnJutalekokKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnJutalekokKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbElofordCimke)
                    .addComponent(lbMkCimke))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnJutalekokKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spJutalekMunkakoronkentLista, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .addComponent(spJutalekElofordulasLista))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnAtlFizReszlKontener.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Átlagfizetés részlegenként", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        lAtlFizReszlegenkentLista.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lAtlFizReszlegenkentLista.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Rnev1 (5423.25)", "Rnev2 (3543.00)", "..." };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lAtlFizReszlegenkentLista.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spAtlFizReszlegenkentLista.setViewportView(lAtlFizReszlegenkentLista);

        btFizetesSugo.setText("?");
        btFizetesSugo.setPreferredSize(new java.awt.Dimension(41, 23));
        btFizetesSugo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btFizetesSugoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnAtlFizReszlKontenerLayout = new javax.swing.GroupLayout(pnAtlFizReszlKontener);
        pnAtlFizReszlKontener.setLayout(pnAtlFizReszlKontenerLayout);
        pnAtlFizReszlKontenerLayout.setHorizontalGroup(
            pnAtlFizReszlKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnAtlFizReszlKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnAtlFizReszlKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spAtlFizReszlegenkentLista, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnAtlFizReszlKontenerLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btFizetesSugo, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnAtlFizReszlKontenerLayout.setVerticalGroup(
            pnAtlFizReszlKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnAtlFizReszlKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spAtlFizReszlegenkentLista, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btFizetesSugo, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnFizetesFopanelLayout = new javax.swing.GroupLayout(pnFizetesFopanel);
        pnFizetesFopanel.setLayout(pnFizetesFopanelLayout);
        pnFizetesFopanelLayout.setHorizontalGroup(
            pnFizetesFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnFizetesFopanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnFizetesFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnAlapAdatokKontener, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnListaKontener, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(pnFizetesFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnMinMaxFizMkKontener, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnFizetesFopanelLayout.createSequentialGroup()
                        .addComponent(pnJutalekokKontener, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(pnAtlFizReszlKontener, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnFizetesFopanelLayout.setVerticalGroup(
            pnFizetesFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnFizetesFopanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnFizetesFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnFizetesFopanelLayout.createSequentialGroup()
                        .addComponent(pnMinMaxFizMkKontener, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnFizetesFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnJutalekokKontener, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnAtlFizReszlKontener, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(pnFizetesFopanelLayout.createSequentialGroup()
                        .addComponent(pnAlapAdatokKontener, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnListaKontener, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tpnOHRTFoPanel.addTab("Fizetés", pnFizetesFopanel);

        pnSzuresFizKontener.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Szűrés fizetés alapján", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        lbFizAlsoHatar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbFizAlsoHatar.setText("Minimum:");

        lbFizFelsoHatar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbFizFelsoHatar.setText("Maximum:");

        spnrFizAlsoHatar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        spnrFizAlsoHatar.setModel(new javax.swing.SpinnerNumberModel(5000, 2400, 24000, 1000));

        spnrFizFelsoHatar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        spnrFizFelsoHatar.setModel(new javax.swing.SpinnerNumberModel(7000, 2400, 24000, 1000));

        lSzuresFizLista.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lSzuresFizLista.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Knev1 Vnev1 (Fiz1)", "Knev1 Vnev2 (Fiz4)", "Knev2 Vnev3 (Fiz3)" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lSzuresFizLista.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spSzuresFizLista.setViewportView(lSzuresFizLista);

        btKivalogatasTorol.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btKivalogatasTorol.setText("Töröl");

        btKivalogatasSzur.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btKivalogatasSzur.setText("Szűr (3 fő)");

        javax.swing.GroupLayout pnSzuresFizKontenerLayout = new javax.swing.GroupLayout(pnSzuresFizKontener);
        pnSzuresFizKontener.setLayout(pnSzuresFizKontenerLayout);
        pnSzuresFizKontenerLayout.setHorizontalGroup(
            pnSzuresFizKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnSzuresFizKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnSzuresFizKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnSzuresFizKontenerLayout.createSequentialGroup()
                        .addGroup(pnSzuresFizKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbFizFelsoHatar)
                            .addComponent(lbFizAlsoHatar))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnSzuresFizKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(spnrFizAlsoHatar, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spnrFizFelsoHatar, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
                    .addComponent(btKivalogatasSzur, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btKivalogatasTorol, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(spSzuresFizLista, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnSzuresFizKontenerLayout.setVerticalGroup(
            pnSzuresFizKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnSzuresFizKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnSzuresFizKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnSzuresFizKontenerLayout.createSequentialGroup()
                        .addGroup(pnSzuresFizKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbFizAlsoHatar)
                            .addComponent(spnrFizAlsoHatar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnSzuresFizKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbFizFelsoHatar)
                            .addComponent(spnrFizFelsoHatar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(btKivalogatasSzur, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btKivalogatasTorol, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(spSzuresFizLista))
                .addContainerGap())
        );

        pnlReszlegvezAlkKontener.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Részlegvezető alkalmazottai", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jComboBox3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "RVezNev1 - Rnev1 (3 fő)", "RVezNev12 - Rnev2  (5 fő)", "...", "RVezNev11 - Rnev3  (4 fő)" }));

        lReszlegvezAlk.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lReszlegvezAlk.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Knev1 Vnev1 (Mkor1)", "Knev1 Vnev2 (Mkor4)", "Knev2 Vnev3 (Mkor1)" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lReszlegvezAlk.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spReszlegvezAlk.setViewportView(lReszlegvezAlk);

        javax.swing.GroupLayout pnlReszlegvezAlkKontenerLayout = new javax.swing.GroupLayout(pnlReszlegvezAlkKontener);
        pnlReszlegvezAlkKontener.setLayout(pnlReszlegvezAlkKontenerLayout);
        pnlReszlegvezAlkKontenerLayout.setHorizontalGroup(
            pnlReszlegvezAlkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlReszlegvezAlkKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlReszlegvezAlkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spReszlegvezAlk))
                .addContainerGap())
        );
        pnlReszlegvezAlkKontenerLayout.setVerticalGroup(
            pnlReszlegvezAlkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlReszlegvezAlkKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(spReszlegvezAlk, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnVezAlkKontener.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Vezető alkalmazottai", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        cbVezAlk.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cbVezAlk.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "VezetoNev1 - Mkor1 (3 fő)", "VezetoNev12 - MKor2 (5 fő)", "..." }));

        lVezAlkLista.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lVezAlkLista.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Knev1 Vnev1 (Mkor1)", "Knev1 Vnev2 (Mkor4)", "Knev2 Vnev3 (Mkor1)" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lVezAlkLista.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spVezAlkLista.setViewportView(lVezAlkLista);

        javax.swing.GroupLayout pnVezAlkKontenerLayout = new javax.swing.GroupLayout(pnVezAlkKontener);
        pnVezAlkKontener.setLayout(pnVezAlkKontenerLayout);
        pnVezAlkKontenerLayout.setHorizontalGroup(
            pnVezAlkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnVezAlkKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnVezAlkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbVezAlk, 0, 472, Short.MAX_VALUE)
                    .addComponent(spVezAlkLista))
                .addContainerGap())
        );
        pnVezAlkKontenerLayout.setVerticalGroup(
            pnVezAlkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnVezAlkKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbVezAlk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spVezAlkLista, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnReszlMkKontener.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Részlegenként és munkakörönként", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        cbReszlegNev.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cbReszlegNev.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Rnev1 (3 fő)", "Rnev2  (5 fő)", "...", "Rnev3  (4 fő)" }));

        lReszlMkLista.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lReszlMkLista.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Knev1 Vnev1 (Rnev2, Mkor1)", "Knev1 Vnev2 (Rnev2, Mkor1)", "Knev2 Vnev3 (Rnev2, Mkor1)" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lReszlMkLista.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spReszlMkLista.setViewportView(lReszlMkLista);

        cbMunkakor.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cbMunkakor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mkor1 (3 fő)", "Mkor2 (5 fő)", "...", "Mkor19 (4 fő)" }));

        btKivalogatasSugo.setText("?");
        btKivalogatasSugo.setPreferredSize(new java.awt.Dimension(41, 23));
        btKivalogatasSugo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btKivalogatasSugoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnReszlMkKontenerLayout = new javax.swing.GroupLayout(pnReszlMkKontener);
        pnReszlMkKontener.setLayout(pnReszlMkKontenerLayout);
        pnReszlMkKontenerLayout.setHorizontalGroup(
            pnReszlMkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnReszlMkKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnReszlMkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spReszlMkLista)
                    .addGroup(pnReszlMkKontenerLayout.createSequentialGroup()
                        .addComponent(cbReszlegNev, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                        .addComponent(cbMunkakor, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnReszlMkKontenerLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btKivalogatasSugo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnReszlMkKontenerLayout.setVerticalGroup(
            pnReszlMkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnReszlMkKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnReszlMkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbReszlegNev, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbMunkakor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(spReszlMkLista, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btKivalogatasSugo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnKivalogatasFopanelLayout = new javax.swing.GroupLayout(pnKivalogatasFopanel);
        pnKivalogatasFopanel.setLayout(pnKivalogatasFopanelLayout);
        pnKivalogatasFopanelLayout.setHorizontalGroup(
            pnKivalogatasFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnKivalogatasFopanelLayout.createSequentialGroup()
                .addGroup(pnKivalogatasFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnKivalogatasFopanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnSzuresFizKontener, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pnlReszlegvezAlkKontener, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(pnKivalogatasFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnVezAlkKontener, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnReszlMkKontener, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnKivalogatasFopanelLayout.setVerticalGroup(
            pnKivalogatasFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnKivalogatasFopanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnKivalogatasFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlReszlegvezAlkKontener, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnVezAlkKontener, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(pnKivalogatasFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnSzuresFizKontener, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnReszlMkKontener, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        tpnOHRTFoPanel.addTab("Kiválogatás", pnKivalogatasFopanel);

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Részlegek", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 249, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 451, Short.MAX_VALUE)
        );

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Kiválasztott részleg", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel26.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel22.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Kiválasztott alkalmazott", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jList20.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jScrollPane20.setViewportView(jList20);

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane20, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane20, javax.swing.GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE)
        );

        jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Kiválasztott e-mail-címe", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jList21.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jScrollPane21.setViewportView(jList21);

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane21)
                .addContainerGap())
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane21)
        );

        jPanel24.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Kiválasztott létszám", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel27.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel28.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel28)
                .addContainerGap(57, Short.MAX_VALUE))
        );

        jButton3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton3.setText("Meghívottakat értesít");

        jButton4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton4.setText("Meghívottakat értesít");

        btErtekezletSugo1.setText("?");
        btErtekezletSugo1.setPreferredSize(new java.awt.Dimension(41, 23));
        btErtekezletSugo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btErtekezletSugo1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnErtekezletFopanelLayout = new javax.swing.GroupLayout(pnErtekezletFopanel);
        pnErtekezletFopanel.setLayout(pnErtekezletFopanelLayout);
        pnErtekezletFopanelLayout.setHorizontalGroup(
            pnErtekezletFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnErtekezletFopanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnErtekezletFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(pnErtekezletFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnErtekezletFopanelLayout.createSequentialGroup()
                        .addGroup(pnErtekezletFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnErtekezletFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnErtekezletFopanelLayout.createSequentialGroup()
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addComponent(btErtekezletSugo1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51))
        );
        pnErtekezletFopanelLayout.setVerticalGroup(
            pnErtekezletFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnErtekezletFopanelLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(pnErtekezletFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnErtekezletFopanelLayout.createSequentialGroup()
                        .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnErtekezletFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(pnErtekezletFopanelLayout.createSequentialGroup()
                        .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnErtekezletFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(btErtekezletSugo1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 22, Short.MAX_VALUE))
        );

        tpnOHRTFoPanel.addTab("Értekezlet", pnErtekezletFopanel);

        jPanel38.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Névsor belépés éve alapján", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jList28.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jList28.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Knev1 Vnev1 (év)", "Knev1 Vnev2 (év)", "Knev2 Vnev3 (év)" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList28.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane28.setViewportView(jList28);

        jComboBox6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1995 (3 fő)", "2002 (4 fő)", "...", " " }));

        javax.swing.GroupLayout jPanel38Layout = new javax.swing.GroupLayout(jPanel38);
        jPanel38.setLayout(jPanel38Layout);
        jPanel38Layout.setHorizontalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox6, 0, 249, Short.MAX_VALUE)
                    .addComponent(jScrollPane28))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel38Layout.setVerticalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane28, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(84, 84, 84))
        );

        jPanel39.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Létszám belépés éve alapján", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jList29.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jList29.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "1995 (2 fő)", "1999 (5 fő)", "..." };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList29.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane29.setViewportView(jList29);

        javax.swing.GroupLayout jPanel39Layout = new javax.swing.GroupLayout(jPanel39);
        jPanel39.setLayout(jPanel39Layout);
        jPanel39Layout.setHorizontalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel39Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane29)
                .addContainerGap())
        );
        jPanel39Layout.setVerticalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel39Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane29, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(227, 227, 227))
        );

        jPanel40.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Munkakörök belépés éve alapján", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jList30.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jList30.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "1995 (3 fő)", "  Mkor1 (2 fő): Knev1 Vnev1, Knev2 Vnev2", "  Mkor2 (1 fő): Knev3 Vnev3", "1997 (5 fő)", "  Mkor1 (3 fő): Knev4 Vnev4, Knev5 Vnev5, Knev6 Vnev6", "  Mkor3 (1 fő): Knev7 Vnev7", "  Mkor4 (1 fő): Knev8 Vnev8", "..." };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList30.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane30.setViewportView(jList30);

        javax.swing.GroupLayout jPanel40Layout = new javax.swing.GroupLayout(jPanel40);
        jPanel40.setLayout(jPanel40Layout);
        jPanel40Layout.setHorizontalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel40Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane30, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel40Layout.setVerticalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel40Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane30)
                .addContainerGap())
        );

        jPanel41.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Összesített létszám évenként", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jList31.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jList31.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "1995 (2 fő)", "1999 (7 fő)", "...", "... (106 fő)" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList31.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane31.setViewportView(jList31);

        javax.swing.GroupLayout jPanel41Layout = new javax.swing.GroupLayout(jPanel41);
        jPanel41.setLayout(jPanel41Layout);
        jPanel41Layout.setHorizontalGroup(
            jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel41Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane31)
                .addContainerGap())
        );
        jPanel41Layout.setVerticalGroup(
            jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel41Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane31)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnBelepesFopanelLayout = new javax.swing.GroupLayout(pnBelepesFopanel);
        pnBelepesFopanel.setLayout(pnBelepesFopanelLayout);
        pnBelepesFopanelLayout.setHorizontalGroup(
            pnBelepesFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnBelepesFopanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnBelepesFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel38, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnBelepesFopanelLayout.setVerticalGroup(
            pnBelepesFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnBelepesFopanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnBelepesFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnBelepesFopanelLayout.createSequentialGroup()
                        .addComponent(jPanel38, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel39, javax.swing.GroupLayout.PREFERRED_SIZE, 306, Short.MAX_VALUE))
                    .addComponent(jPanel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        tpnOHRTFoPanel.addTab("Belépés", pnBelepesFopanel);

        pnNevsorReszlKontener.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Névsor (részlegenként)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jList1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Knev1 Vnev1", "Knev1 Vnev2", "Knev2 Vnev3" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spNevsorReszlLista.setViewportView(jList1);

        cbNevsorReszlegenkent.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cbNevsorReszlegenkent.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Rnev1 (3 fő)", "Rnev2 (5 fő)", "...", "Rnev11 (45 fő)" }));

        javax.swing.GroupLayout pnNevsorReszlKontenerLayout = new javax.swing.GroupLayout(pnNevsorReszlKontener);
        pnNevsorReszlKontener.setLayout(pnNevsorReszlKontenerLayout);
        pnNevsorReszlKontenerLayout.setHorizontalGroup(
            pnNevsorReszlKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnNevsorReszlKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnNevsorReszlKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnNevsorReszlKontenerLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(spNevsorReszlLista, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cbNevsorReszlegenkent, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnNevsorReszlKontenerLayout.setVerticalGroup(
            pnNevsorReszlKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnNevsorReszlKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbNevsorReszlegenkent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(spNevsorReszlLista, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnNevsorAlkKontener.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Névsor (alkalmazottak)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        lNevsorAlkLista.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lNevsorAlkLista.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Knev1 Vnev1", "Knev1 Vnev2", "Knev2 Vnev3" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lNevsorAlkLista.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spNevsorAlkLista.setViewportView(lNevsorAlkLista);

        lbNevsAlkLetszCimke.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbNevsAlkLetszCimke.setText("Létszám:");

        lbNevsorAlkLetszam.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbNevsorAlkLetszam.setText("X fő");

        javax.swing.GroupLayout pnNevsorAlkKontenerLayout = new javax.swing.GroupLayout(pnNevsorAlkKontener);
        pnNevsorAlkKontener.setLayout(pnNevsorAlkKontenerLayout);
        pnNevsorAlkKontenerLayout.setHorizontalGroup(
            pnNevsorAlkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnNevsorAlkKontenerLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spNevsorAlkLista, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(pnNevsorAlkKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbNevsAlkLetszCimke)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbNevsorAlkLetszam)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnNevsorAlkKontenerLayout.setVerticalGroup(
            pnNevsorAlkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnNevsorAlkKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spNevsorAlkLista)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnNevsorAlkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbNevsAlkLetszCimke)
                    .addComponent(lbNevsorAlkLetszam))
                .addContainerGap())
        );

        pnNevsorVezKontener.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Névsor (vezetők)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        lNevsorVezLista.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lNevsorVezLista.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Knev1 Vnev1", "Knev1 Vnev2", "Knev2 Vnev3" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lNevsorVezLista.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spNevsorVezLista.setViewportView(lNevsorVezLista);

        lbNevsVezLetszCimke.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbNevsVezLetszCimke.setText("Létszám:");

        lbNevsorVezLetszam.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbNevsorVezLetszam.setText("X fő");

        javax.swing.GroupLayout pnNevsorVezKontenerLayout = new javax.swing.GroupLayout(pnNevsorVezKontener);
        pnNevsorVezKontener.setLayout(pnNevsorVezKontenerLayout);
        pnNevsorVezKontenerLayout.setHorizontalGroup(
            pnNevsorVezKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnNevsorVezKontenerLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spNevsorVezLista, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(pnNevsorVezKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbNevsVezLetszCimke)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbNevsorVezLetszam)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnNevsorVezKontenerLayout.setVerticalGroup(
            pnNevsorVezKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnNevsorVezKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spNevsorVezLista)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnNevsorVezKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbNevsVezLetszCimke)
                    .addComponent(lbNevsorVezLetszam))
                .addContainerGap())
        );

        pnNevsorReszlVKontener.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Névsor (részlegvezetők)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        lNevsReszlVezLista.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lNevsReszlVezLista.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Knev1 Vnev1", "Knev1 Vnev2", "Knev2 Vnev3" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lNevsReszlVezLista.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spNevsReszlVezLista.setViewportView(lNevsReszlVezLista);

        lbNevsReszlVezCimke.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbNevsReszlVezCimke.setText("Létszám:");

        lbNevsReszlVez.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbNevsReszlVez.setText("X fő");

        javax.swing.GroupLayout pnNevsorReszlVKontenerLayout = new javax.swing.GroupLayout(pnNevsorReszlVKontener);
        pnNevsorReszlVKontener.setLayout(pnNevsorReszlVKontenerLayout);
        pnNevsorReszlVKontenerLayout.setHorizontalGroup(
            pnNevsorReszlVKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnNevsorReszlVKontenerLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spNevsReszlVezLista, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(pnNevsorReszlVKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbNevsReszlVezCimke)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbNevsReszlVez)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnNevsorReszlVKontenerLayout.setVerticalGroup(
            pnNevsorReszlVKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnNevsorReszlVKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spNevsReszlVezLista, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnNevsorReszlVKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbNevsReszlVezCimke)
                    .addComponent(lbNevsReszlVez))
                .addGap(0, 9, Short.MAX_VALUE))
        );

        pnNevsorMkKontener.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Névsor (munkakörönként)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        lNevsorMkLista.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lNevsorMkLista.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Knev1 Vnev1", "Knev1 Vnev2", "Knev2 Vnev3" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lNevsorMkLista.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spNevsorMkLista.setViewportView(lNevsorMkLista);

        cnNevsorMunkakor.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cnNevsorMunkakor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mkor1 (3 fő)", "Mkor2 (5 fő)", "...", "Mkor19 (4 fő)" }));

        javax.swing.GroupLayout pnNevsorMkKontenerLayout = new javax.swing.GroupLayout(pnNevsorMkKontener);
        pnNevsorMkKontener.setLayout(pnNevsorMkKontenerLayout);
        pnNevsorMkKontenerLayout.setHorizontalGroup(
            pnNevsorMkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnNevsorMkKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnNevsorMkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnNevsorMkKontenerLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(spNevsorMkLista, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cnNevsorMunkakor, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnNevsorMkKontenerLayout.setVerticalGroup(
            pnNevsorMkKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnNevsorMkKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cnNevsorMunkakor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(spNevsorMkLista, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addContainerGap())
        );

        btNevsorSugo.setText("?");
        btNevsorSugo.setPreferredSize(new java.awt.Dimension(41, 23));
        btNevsorSugo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btNevsorSugoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnNevsorFopanelLayout = new javax.swing.GroupLayout(pnNevsorFopanel);
        pnNevsorFopanel.setLayout(pnNevsorFopanelLayout);
        pnNevsorFopanelLayout.setHorizontalGroup(
            pnNevsorFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnNevsorFopanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnNevsorAlkKontener, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnNevsorFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnNevsorFopanelLayout.createSequentialGroup()
                        .addComponent(pnNevsorReszlVKontener, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnNevsorReszlKontener, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnNevsorFopanelLayout.createSequentialGroup()
                        .addComponent(pnNevsorVezKontener, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnNevsorMkKontener, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
                        .addComponent(btNevsorSugo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30))))
        );
        pnNevsorFopanelLayout.setVerticalGroup(
            pnNevsorFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnNevsorFopanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnNevsorFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnNevsorFopanelLayout.createSequentialGroup()
                        .addGroup(pnNevsorFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnNevsorReszlKontener, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnNevsorReszlVKontener, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnNevsorFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnNevsorVezKontener, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnNevsorMkKontener, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(pnNevsorAlkKontener, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnNevsorFopanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btNevsorSugo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        tpnOHRTFoPanel.addTab("Névsor", pnNevsorFopanel);

        jPanel35.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Alkalmazottak száma és átlagfizetése munkakörönként", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jList25.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jList25.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = Lekerdezes2().split("\n");
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList25.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane25.setViewportView(jList25);

        javax.swing.GroupLayout jPanel35Layout = new javax.swing.GroupLayout(jPanel35);
        jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane25, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel35Layout.setVerticalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane25)
                .addContainerGap())
        );

        jPanel36.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Belépő alkalmazottak száma évenként", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jList26.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jList26.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = Lekerdezes1().split("\n");
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList26.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane26.setViewportView(jList26);

        javax.swing.GroupLayout jPanel36Layout = new javax.swing.GroupLayout(jPanel36);
        jPanel36.setLayout(jPanel36Layout);
        jPanel36Layout.setHorizontalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane26, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel36Layout.setVerticalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane26, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnJelentesFopanelLayout = new javax.swing.GroupLayout(pnJelentesFopanel);
        pnJelentesFopanel.setLayout(pnJelentesFopanelLayout);
        pnJelentesFopanelLayout.setHorizontalGroup(
            pnJelentesFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnJelentesFopanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnJelentesFopanelLayout.setVerticalGroup(
            pnJelentesFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnJelentesFopanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnJelentesFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        tpnOHRTFoPanel.addTab("Jelentés", pnJelentesFopanel);

        lbGrAtlFizReszl.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbGrAtlFizReszl.setText("Átlagfizetés részlegenként, oszlopdiagram:");

        pnGrAtlFizReszl.setBackground(new java.awt.Color(255, 255, 102));

        javax.swing.GroupLayout pnGrAtlFizReszlLayout = new javax.swing.GroupLayout(pnGrAtlFizReszl);
        pnGrAtlFizReszl.setLayout(pnGrAtlFizReszlLayout);
        pnGrAtlFizReszlLayout.setHorizontalGroup(
            pnGrAtlFizReszlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 453, Short.MAX_VALUE)
        );
        pnGrAtlFizReszlLayout.setVerticalGroup(
            pnGrAtlFizReszlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        lbGrAtlFizMk.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbGrAtlFizMk.setText("Átlagfizetés munkakörönként, oszlopdiagram:");

        pnGrAtlFizMk.setBackground(new java.awt.Color(255, 255, 102));

        javax.swing.GroupLayout pnGrAtlFizMkLayout = new javax.swing.GroupLayout(pnGrAtlFizMk);
        pnGrAtlFizMk.setLayout(pnGrAtlFizMkLayout);
        pnGrAtlFizMkLayout.setHorizontalGroup(
            pnGrAtlFizMkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnGrAtlFizMkLayout.setVerticalGroup(
            pnGrAtlFizMkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 568, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnGrKontener1Layout = new javax.swing.GroupLayout(pnGrKontener1);
        pnGrKontener1.setLayout(pnGrKontener1Layout);
        pnGrKontener1Layout.setHorizontalGroup(
            pnGrKontener1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnGrKontener1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnGrKontener1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbGrAtlFizReszl)
                    .addComponent(pnGrAtlFizReszl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(pnGrKontener1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnGrAtlFizMk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnGrKontener1Layout.createSequentialGroup()
                        .addComponent(lbGrAtlFizMk, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 215, Short.MAX_VALUE))))
        );
        pnGrKontener1Layout.setVerticalGroup(
            pnGrKontener1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnGrKontener1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnGrKontener1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnGrKontener1Layout.createSequentialGroup()
                        .addComponent(lbGrAtlFizMk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnGrAtlFizMk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnGrKontener1Layout.createSequentialGroup()
                        .addComponent(lbGrAtlFizReszl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnGrAtlFizReszl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane2.addTab("Átlagfizetés", pnGrKontener1);

        lbGrLetszamReszl.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbGrLetszamReszl.setText("Létszám részlegenként, kördiagram:");

        pnGrLetszamReszl.setBackground(new java.awt.Color(255, 255, 102));

        javax.swing.GroupLayout pnGrLetszamReszlLayout = new javax.swing.GroupLayout(pnGrLetszamReszl);
        pnGrLetszamReszl.setLayout(pnGrLetszamReszlLayout);
        pnGrLetszamReszlLayout.setHorizontalGroup(
            pnGrLetszamReszlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 460, Short.MAX_VALUE)
        );
        pnGrLetszamReszlLayout.setVerticalGroup(
            pnGrLetszamReszlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 568, Short.MAX_VALUE)
        );

        lbGrLetszamMk.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbGrLetszamMk.setText("Létszám munkakörként, kördiagram:");

        pnGrLetszamMk.setBackground(new java.awt.Color(255, 255, 102));

        javax.swing.GroupLayout pnGrLetszamMkLayout = new javax.swing.GroupLayout(pnGrLetszamMk);
        pnGrLetszamMk.setLayout(pnGrLetszamMkLayout);
        pnGrLetszamMkLayout.setHorizontalGroup(
            pnGrLetszamMkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 478, Short.MAX_VALUE)
        );
        pnGrLetszamMkLayout.setVerticalGroup(
            pnGrLetszamMkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnGrKontener2Layout = new javax.swing.GroupLayout(pnGrKontener2);
        pnGrKontener2.setLayout(pnGrKontener2Layout);
        pnGrKontener2Layout.setHorizontalGroup(
            pnGrKontener2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnGrKontener2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnGrKontener2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbGrLetszamReszl)
                    .addComponent(pnGrLetszamReszl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                .addGroup(pnGrKontener2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbGrLetszamMk)
                    .addComponent(pnGrLetszamMk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pnGrKontener2Layout.setVerticalGroup(
            pnGrKontener2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnGrKontener2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnGrKontener2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnGrKontener2Layout.createSequentialGroup()
                        .addComponent(lbGrLetszamMk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnGrLetszamMk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnGrKontener2Layout.createSequentialGroup()
                        .addComponent(lbGrLetszamReszl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnGrLetszamReszl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane2.addTab("Létszám", pnGrKontener2);

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel23.setText("Részleg");

        jComboBox10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Rnev1 (3 fő)", "Rnev2 (5 fő)", "...", "Rnev11 (45 fő)" }));

        jPanel37.setBackground(new java.awt.Color(255, 255, 102));

        javax.swing.GroupLayout jPanel37Layout = new javax.swing.GroupLayout(jPanel37);
        jPanel37.setLayout(jPanel37Layout);
        jPanel37Layout.setHorizontalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 703, Short.MAX_VALUE)
        );
        jPanel37Layout.setVerticalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel24.setText("Részleg létszáma azokban az években, amikor belépett új alkalmazott (összesített oszlopdiagram)");

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel25.setText("Jelentés");

        jList27.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jList27.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "1997 (+1 fő)", "  Knev1 Vnev1 (Mkor1)", "2000 (+2 fő)", "  Knev2 Vnev2 (Mkor2)", "  Knev3 Vnev3 (Mkor1)", "2001..." };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList27.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane27.setViewportView(jList27);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox10, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23)
                            .addComponent(jLabel25))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane27, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addGap(111, 111, 111))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jPanel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane27, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE))
                    .addComponent(jPanel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane2.addTab("Belépés", jPanel7);

        javax.swing.GroupLayout pnGrafikonFopanelLayout = new javax.swing.GroupLayout(pnGrafikonFopanel);
        pnGrafikonFopanel.setLayout(pnGrafikonFopanelLayout);
        pnGrafikonFopanelLayout.setHorizontalGroup(
            pnGrafikonFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnGrafikonFopanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );
        pnGrafikonFopanelLayout.setVerticalGroup(
            pnGrafikonFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnGrafikonFopanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );

        tpnOHRTFoPanel.addTab("Grafikon", pnGrafikonFopanel);

        pnKeresesKontener.setBorder(javax.swing.BorderFactory.createTitledBorder("Alkalmazott keresése (Név és EmployeeID alapján)"));
        pnKeresesKontener.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        cbKeresAlkalmazott.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        pnKeresAlkNevCimke.setText("Név:");

        pnKeresAlkEmailCimke.setText("E-mail címe:");

        pnKeresAlkMkCimke.setText("Munkaköre:");

        pnKeresAlkReszlCimke.setText("Részlege:");

        javax.swing.GroupLayout pnKeresesKontenerLayout = new javax.swing.GroupLayout(pnKeresesKontener);
        pnKeresesKontener.setLayout(pnKeresesKontenerLayout);
        pnKeresesKontenerLayout.setHorizontalGroup(
            pnKeresesKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnKeresesKontenerLayout.createSequentialGroup()
                .addGroup(pnKeresesKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnKeresesKontenerLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(cbKeresAlkalmazott, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnKeresesKontenerLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(pnKeresesKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pnKeresAlkReszlCimke)
                            .addComponent(pnKeresAlkMkCimke)
                            .addComponent(pnKeresAlkEmailCimke)
                            .addComponent(pnKeresAlkNevCimke))
                        .addGap(18, 18, 18)
                        .addGroup(pnKeresesKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnKeresAlkReszleg, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                            .addComponent(pnKeresAlkMunkakor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnKeresAlkEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnKeresAlkNev, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        pnKeresesKontenerLayout.setVerticalGroup(
            pnKeresesKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnKeresesKontenerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbKeresAlkalmazott, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnKeresesKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pnKeresAlkNevCimke)
                    .addComponent(pnKeresAlkNev))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnKeresesKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pnKeresAlkEmailCimke)
                    .addComponent(pnKeresAlkEmail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnKeresesKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pnKeresAlkMkCimke)
                    .addComponent(pnKeresAlkMunkakor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnKeresesKontenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pnKeresAlkReszlCimke)
                    .addComponent(pnKeresAlkReszleg))
                .addContainerGap(45, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnKeresesFopanelLayout = new javax.swing.GroupLayout(pnKeresesFopanel);
        pnKeresesFopanel.setLayout(pnKeresesFopanelLayout);
        pnKeresesFopanelLayout.setHorizontalGroup(
            pnKeresesFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnKeresesFopanelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(pnKeresesKontener, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(687, Short.MAX_VALUE))
        );
        pnKeresesFopanelLayout.setVerticalGroup(
            pnKeresesFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnKeresesFopanelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(pnKeresesKontener, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(434, Short.MAX_VALUE))
        );

        tpnOHRTFoPanel.addTab("Keresés", pnKeresesFopanel);

        javax.swing.GroupLayout jPanel43Layout = new javax.swing.GroupLayout(jPanel43);
        jPanel43.setLayout(jPanel43Layout);
        jPanel43Layout.setHorizontalGroup(
            jPanel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1045, Short.MAX_VALUE)
        );
        jPanel43Layout.setVerticalGroup(
            jPanel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 663, Short.MAX_VALUE)
        );

        tpnOHRTFoPanel.addTab("Extra3", jPanel43);

        javax.swing.GroupLayout jPanel44Layout = new javax.swing.GroupLayout(jPanel44);
        jPanel44.setLayout(jPanel44Layout);
        jPanel44Layout.setHorizontalGroup(
            jPanel44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1045, Short.MAX_VALUE)
        );
        jPanel44Layout.setVerticalGroup(
            jPanel44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 663, Short.MAX_VALUE)
        );

        tpnOHRTFoPanel.addTab("Extra4", jPanel44);

        javax.swing.GroupLayout jPanel45Layout = new javax.swing.GroupLayout(jPanel45);
        jPanel45.setLayout(jPanel45Layout);
        jPanel45Layout.setHorizontalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1045, Short.MAX_VALUE)
        );
        jPanel45Layout.setVerticalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 663, Short.MAX_VALUE)
        );

        tpnOHRTFoPanel.addTab("Extra5", jPanel45);

        javax.swing.GroupLayout pnNevjegyFopanelLayout = new javax.swing.GroupLayout(pnNevjegyFopanel);
        pnNevjegyFopanel.setLayout(pnNevjegyFopanelLayout);
        pnNevjegyFopanelLayout.setHorizontalGroup(
            pnNevjegyFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1045, Short.MAX_VALUE)
        );
        pnNevjegyFopanelLayout.setVerticalGroup(
            pnNevjegyFopanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 663, Short.MAX_VALUE)
        );

        tpnOHRTFoPanel.addTab("Névjegy", pnNevjegyFopanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tpnOHRTFoPanel, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tpnOHRTFoPanel)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btNevsorSugoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btNevsorSugoActionPerformed
        String HTML = "";
        sugotMegnyit(HTML);
    }//GEN-LAST:event_btNevsorSugoActionPerformed

    private void btFizetesSugoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btFizetesSugoActionPerformed
        String HTML = "src/Sugo/sugo-ful-fizetes.html";
        sugotMegnyit(HTML);
    }//GEN-LAST:event_btFizetesSugoActionPerformed

    private void btKivalogatasSugoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btKivalogatasSugoActionPerformed
        String HTML = "";
        sugotMegnyit(HTML);
    }//GEN-LAST:event_btKivalogatasSugoActionPerformed

    private void btErtekezletSugo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btErtekezletSugo1ActionPerformed
        String HTML = "src/Sugo/sugo-ful-ertekezlet.html";
        sugotMegnyit(HTML);
    }//GEN-LAST:event_btErtekezletSugo1ActionPerformed

//  public static void main(String args[]) {
//    /* Set the Nimbus look and feel */
//    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//     */
//    try {
//      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//        if ("Nimbus".equals(info.getName())) {
//          javax.swing.UIManager.setLookAndFeel(info.getClassName());
//          break;
//        }
//      }
//    } catch (ClassNotFoundException ex) {
//      java.util.logging.Logger.getLogger(OracleHRRiport.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//    } catch (InstantiationException ex) {
//      java.util.logging.Logger.getLogger(OracleHRRiport.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//    } catch (IllegalAccessException ex) {
//      java.util.logging.Logger.getLogger(OracleHRRiport.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//      java.util.logging.Logger.getLogger(OracleHRRiport.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//    }
//    //</editor-fold>
//
//    /* Create and display the form */
//    java.awt.EventQueue.invokeLater(new Runnable() {
//      public void run() {
//        new OracleHRRiport().setVisible(true);
//      }
//    });
//  }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btErtekezletSugo1;
    private javax.swing.JButton btFizetesSugo;
    private javax.swing.JButton btKivalogatasSugo;
    private javax.swing.JButton btKivalogatasSzur;
    private javax.swing.JButton btKivalogatasTorol;
    private javax.swing.JButton btNevsorSugo;
    private javax.swing.JComboBox<String> cbKeresAlkalmazott;
    private javax.swing.JComboBox<String> cbMunkakor;
    private javax.swing.JComboBox<String> cbNevsorReszlegenkent;
    private javax.swing.JComboBox<String> cbReszlegNev;
    private javax.swing.JComboBox<String> cbVezAlk;
    private javax.swing.JComboBox<String> cnNevsorMunkakor;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox10;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList20;
    private javax.swing.JList<String> jList21;
    private javax.swing.JList<String> jList25;
    private javax.swing.JList<String> jList26;
    private javax.swing.JList<String> jList27;
    private javax.swing.JList<String> jList28;
    private javax.swing.JList<String> jList29;
    private javax.swing.JList<String> jList30;
    private javax.swing.JList<String> jList31;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel37;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel39;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel41;
    private javax.swing.JPanel jPanel43;
    private javax.swing.JPanel jPanel44;
    private javax.swing.JPanel jPanel45;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane20;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane25;
    private javax.swing.JScrollPane jScrollPane26;
    private javax.swing.JScrollPane jScrollPane27;
    private javax.swing.JScrollPane jScrollPane28;
    private javax.swing.JScrollPane jScrollPane29;
    private javax.swing.JScrollPane jScrollPane30;
    private javax.swing.JScrollPane jScrollPane31;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JList<String> lAtlFizReszlegenkentLista;
    private javax.swing.JList<String> lEletpalyaFizetesLista;
    private javax.swing.JList<String> lFizeteseloszlasLista;
    private javax.swing.JList<String> lJutalekElofordulasLista;
    private javax.swing.JList<String> lJutalekMunkakoronkentLista;
    private javax.swing.JList<String> lNevsReszlVezLista;
    private javax.swing.JList<String> lNevsorAlkLista;
    private javax.swing.JList<String> lNevsorMkLista;
    private javax.swing.JList<String> lNevsorVezLista;
    private javax.swing.JList<String> lReszlMkLista;
    private javax.swing.JList<String> lReszlegvezAlk;
    private javax.swing.JList<String> lSzuresFizLista;
    private javax.swing.JList<String> lTenylegesFizetesLista;
    private javax.swing.JList<String> lVezAlkLista;
    private javax.swing.JLabel lbAtlFiz;
    private javax.swing.JLabel lbAtlFizCimke;
    private javax.swing.JLabel lbEletpalyaFiz;
    private javax.swing.JLabel lbElofordCimke;
    private javax.swing.JLabel lbFizAlsoHatar;
    private javax.swing.JLabel lbFizFelsoHatar;
    private javax.swing.JLabel lbGrAtlFizMk;
    private javax.swing.JLabel lbGrAtlFizReszl;
    private javax.swing.JLabel lbGrLetszamMk;
    private javax.swing.JLabel lbGrLetszamReszl;
    private javax.swing.JLabel lbLetszam;
    private javax.swing.JLabel lbLetszamCimke;
    private javax.swing.JLabel lbMaxFiz;
    private javax.swing.JLabel lbMaxFizCimke;
    private javax.swing.JLabel lbMinFiz;
    private javax.swing.JLabel lbMinFizCimke;
    private javax.swing.JLabel lbMkCimke;
    private javax.swing.JLabel lbNevsAlkLetszCimke;
    private javax.swing.JLabel lbNevsReszlVez;
    private javax.swing.JLabel lbNevsReszlVezCimke;
    private javax.swing.JLabel lbNevsVezLetszCimke;
    private javax.swing.JLabel lbNevsorAlkLetszam;
    private javax.swing.JLabel lbNevsorVezLetszam;
    private javax.swing.JLabel lbOsszFiz;
    private javax.swing.JLabel lbOsszFizCimke;
    private javax.swing.JLabel lbTenylegesFiz;
    private javax.swing.JPanel pnAlapAdatokKontener;
    private javax.swing.JPanel pnAtlFizReszlKontener;
    private javax.swing.JPanel pnBelepesFopanel;
    private javax.swing.JPanel pnErtekezletFopanel;
    private javax.swing.JPanel pnFizetesFopanel;
    private javax.swing.JPanel pnGrAtlFizMk;
    private javax.swing.JPanel pnGrAtlFizReszl;
    private javax.swing.JPanel pnGrKontener1;
    private javax.swing.JPanel pnGrKontener2;
    private javax.swing.JPanel pnGrLetszamMk;
    private javax.swing.JPanel pnGrLetszamReszl;
    private javax.swing.JPanel pnGrafikonFopanel;
    private javax.swing.JPanel pnJelentesFopanel;
    private javax.swing.JPanel pnJutalekokKontener;
    private javax.swing.JLabel pnKeresAlkEmail;
    private javax.swing.JLabel pnKeresAlkEmailCimke;
    private javax.swing.JLabel pnKeresAlkMkCimke;
    private javax.swing.JLabel pnKeresAlkMunkakor;
    private javax.swing.JLabel pnKeresAlkNev;
    private javax.swing.JLabel pnKeresAlkNevCimke;
    private javax.swing.JLabel pnKeresAlkReszlCimke;
    private javax.swing.JLabel pnKeresAlkReszleg;
    private javax.swing.JPanel pnKeresesFopanel;
    private javax.swing.JPanel pnKeresesKontener;
    private javax.swing.JPanel pnKivalogatasFopanel;
    private javax.swing.JPanel pnListaKontener;
    private javax.swing.JPanel pnMinMaxFizMkKontener;
    private javax.swing.JPanel pnNevjegyFopanel;
    private javax.swing.JPanel pnNevsorAlkKontener;
    private javax.swing.JPanel pnNevsorFopanel;
    private javax.swing.JPanel pnNevsorMkKontener;
    private javax.swing.JPanel pnNevsorReszlKontener;
    private javax.swing.JPanel pnNevsorReszlVKontener;
    private javax.swing.JPanel pnNevsorVezKontener;
    private javax.swing.JPanel pnReszlMkKontener;
    private javax.swing.JPanel pnSzuresFizKontener;
    private javax.swing.JPanel pnVezAlkKontener;
    private javax.swing.JPanel pnlReszlegvezAlkKontener;
    private javax.swing.JScrollPane spAtlFizReszlegenkentLista;
    private javax.swing.JScrollPane spEletpalyaFizetesLista;
    private javax.swing.JScrollPane spFizeteseloszlasLista;
    private javax.swing.JScrollPane spJutalekElofordulasLista;
    private javax.swing.JScrollPane spJutalekMunkakoronkentLista;
    private javax.swing.JScrollPane spNevsReszlVezLista;
    private javax.swing.JScrollPane spNevsorAlkLista;
    private javax.swing.JScrollPane spNevsorMkLista;
    private javax.swing.JScrollPane spNevsorReszlLista;
    private javax.swing.JScrollPane spNevsorVezLista;
    private javax.swing.JScrollPane spReszlMkLista;
    private javax.swing.JScrollPane spReszlegvezAlk;
    private javax.swing.JScrollPane spSzuresFizLista;
    private javax.swing.JScrollPane spTenylegesFizetesLista;
    private javax.swing.JScrollPane spVezAlkLista;
    private javax.swing.JSpinner spnrFizAlsoHatar;
    private javax.swing.JSpinner spnrFizFelsoHatar;
    private javax.swing.JTabbedPane tpnOHRTFoPanel;
    // End of variables declaration//GEN-END:variables

    private List<javax.swing.JToggleButton> departmentToggleButtons;

    private void initOwnComponents() {
        departmentToggleButtons = new ArrayList<>();
        jPanel21.setLayout(new GridLayout(departmentList.size(), 1, 5, 5));
        for (Department dep : departmentList) {

            javax.swing.JToggleButton jToggleButton = new javax.swing.JToggleButton();
            departmentToggleButtons.add(jToggleButton);
            jPanel21.add(jToggleButton);
            jToggleButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
//        String buttonText = String.format("%s (%d fő)", dep.getDepartmentName(), dep.getSize());
            String buttonText = dep.getVisibleTitle();
            jToggleButton.setText(buttonText);

            jToggleButton.addChangeListener(new javax.swing.event.ChangeListener() {
                @Override
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    if (((javax.swing.JToggleButton) evt.getSource()).isSelected()) {
                        activeDeps.add(dep);
                    } else {
                        activeDeps.remove(dep);
                    }
//                System.out.println("Nyihaha!" + activeDeps);
                    updatePanels();
                }
            });
        }

//      javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
//      jPanel21.setLayout(jPanel21Layout);
//      jPanel21Layout.setHorizontalGroup(
//        jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//        .addGroup(jPanel21Layout.createSequentialGroup()
//          .addContainerGap()
//          .addGroup(createParallelGroup(jPanel21Layout))
//          .addContainerGap())
//      );
//      jPanel21Layout.setVerticalGroup(
//        jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//        .addGroup(createSequentialGroup(jPanel21Layout))
//      );
//      jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Kiválasztott részleg", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N
    }

    private javax.swing.GroupLayout.ParallelGroup createParallelGroup(javax.swing.GroupLayout layout) {
        javax.swing.GroupLayout.ParallelGroup group = layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
        for (javax.swing.JToggleButton button : departmentToggleButtons) {
            group.addComponent(button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        }
        return group;
    }

    private javax.swing.GroupLayout.SequentialGroup createSequentialGroup(javax.swing.GroupLayout layout) {
        javax.swing.GroupLayout.SequentialGroup group = layout.createSequentialGroup().addContainerGap();
        for (javax.swing.JToggleButton button : departmentToggleButtons) {
            group.addComponent(button)
                    .addGap(12, 15, Short.MAX_VALUE);
        }
        return group;
    }

    private void updatePanels() {
        setSelectedDepsHeadcountPanel();
        setSelectedDepsPanel();
        setSelectedEmployeesPanel();
        setSelectedEmployeesEmailPanel();
    }

    private void setSelectedDepsHeadcountPanel() {
        int sumHeadcount = Ertekezlet.getSummarizedHeadcount(activeDeps);
        jLabel27.setText(activeDeps.size() + " részleg");
        jLabel28.setText(sumHeadcount + " fő");
    }

    private void setSelectedDepsPanel() {
        String selectedDeps = Ertekezlet.getSelectedDeps(activeDeps);
        if (activeDeps.size() <= 1) {
//          jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Kiválasztott részleg", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N
        } else {
//          jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Kiválasztott részlegek", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N
        }

        jLabel26.setText(selectedDeps);
    }

    private void setSelectedEmployeesPanel() {
        jList20.setModel(Ertekezlet.getEmployeesNameList(activeDeps));

        //    System.out.println(selectedEmps);
//      jPanel22.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Kiválasztott alkalmazott", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N
    }

    private void setSelectedEmployeesEmailPanel() {
//      jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Kiválasztott e-mail-címe", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N
//        ls10.setModel(Ertekezlet.getEmployeesEmailList(activeDeps));
        jList21.setModel(Ertekezlet.getEmployeesEmailList(activeDeps));
    }
}
