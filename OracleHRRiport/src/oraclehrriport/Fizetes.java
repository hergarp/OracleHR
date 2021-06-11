package oraclehrriport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;

public class Fizetes {

    private Fizetes() {
        ;

    }
    /*Egy listában tároljuk az "Adatok" panel adatait, kivéve a létszámot, mert az a lista elemeinek a száma*/
    //hatékonyabb, mint minden adat kiszámításához egyenként megírni a  függvényt, mert csak egyszer kell lefutnia a ciklusnak
    public static List<Double> szamitAdatok(List<OracleHRData> lista) {
        List<Double> eredmenyLista = new ArrayList<>();
        int osszeg = 0;
        int minFizetes = lista.get(0).getSalary(); //a kezdőérték a paraméterlista első elemének fiztés része
        int maxFizetes = lista.get(0).getSalary(); //a kezdőérték a paraméterlista első elemének fiztés része
        for (OracleHRData elem : lista) {
            osszeg += elem.getSalary();
            if (elem.getSalary() < minFizetes) {
                minFizetes = elem.getSalary();
            }
            if (elem.getSalary() > maxFizetes) {
                maxFizetes = elem.getSalary();
            }

        }
        eredmenyLista.add(1.0 * minFizetes);            //0.elem: minimum fizetés
        eredmenyLista.add(1.0 * maxFizetes);            //1.elem: maximum fizetés
        eredmenyLista.add(1.0 * osszeg / lista.size());   //2.elem: átlagfizetés
        eredmenyLista.add(1.0 * osszeg);                //3.elem: összes fizetés 

        return eredmenyLista;
    }

    public static List<String> fizetesHanyFo(List<OracleHRData> lista) {
        List<String> kimenetiLista = new ArrayList<>();
        //másolat készítése az eredeti adatlistáról
        List<OracleHRData> masolatLista = new ArrayList<>(); //lefoglalok memóriát egy üres listának
        masolatLista.addAll(lista);

//        Set<Integer> rendezettHalmaz = new TreeSet<>();
//        for (int i = 0; i < masolatLista.size(); i++) {
//        rendezettHalmaz.add(masolatLista.get(i).getSalary());        
//        }
//        System.out.println(rendezettHalmaz);
        //fizetés szerint növekvő sorrendbe rendezzük az objektumokat a másolatlistában, hogy tudjunk csoportváltást alkalmazni
        Collections.sort(masolatLista, (OracleHRData d1, OracleHRData d2) -> {
            return Integer.compare(d1.getSalary(), d2.getSalary());  //return d1.getSalary()-d2.getSalary()
        });
        //csoportváltás 
        int i = 0;
        while (i < masolatLista.size()) {
            int aktFizetes = masolatLista.get(i).getSalary();
            String rekord = "  " + aktFizetes + "  (";
            int dB = 0;
            while (i < masolatLista.size() && masolatLista.get(i).getSalary() == aktFizetes) {
                dB++;
                i++;
            }
            rekord += dB + " fő)";
            kimenetiLista.add(rekord);
        }
        return kimenetiLista;
    }

    //tényleges fiz. munkakörönként, életpálya szerinti fiz. munkakörönként
    public static List<String> kimutatasMunkakoronkenti(List<OracleHRData> lista, String valasztas) { //a választás szabja meg, melyik kimeneti listát adja vissza a metódus
        List<String> kimenetiListaTenyleges = new ArrayList<>();
        List<String> kimenetiListaEletpalya = new ArrayList<>();
        List<String> kimenetiListaAtlagFizetesPerMk = new ArrayList<>();
        List<String> kimenetiListaLetszam = new ArrayList<>();
        //másolat készítése az eredeti adatlistáról
        List<OracleHRData> masolatLista = new ArrayList<>(); //lefoglalok memóriát egy üres listának
        masolatLista.addAll(lista);

        //munkakör szerint növekvő sorrendbe rendezzük az objektumokat a másolatlistában, hogy tudjunk csoportváltást alkalmazni
        Collections.sort(masolatLista, (OracleHRData d1, OracleHRData d2) -> {
            return d1.getJobTitle().compareTo(d2.getJobTitle());
        });
        //csoportváltás 
        int i = 0;
        while (i < masolatLista.size()) {
            String aktMunkakor = (masolatLista.get(i)).getJobTitle();
            String rekord = "  " + aktMunkakor + " (";
            int minFizTenyleges = masolatLista.get(i).getSalary();
            int maxFizTenyleges = masolatLista.get(i).getSalary();
            int osszFiz = 0;
            int letszam = 0;
            if (valasztas.equals("eletpalya")) {
                rekord += masolatLista.get(i).getMinSalary() + ", " + masolatLista.get(i).getMaxSalary() + ")";
                kimenetiListaEletpalya.add(rekord);
            }
            while (i < masolatLista.size() && ((masolatLista.get(i)).getJobTitle()).equals(aktMunkakor)) {
                if (maxFizTenyleges < masolatLista.get(i).getSalary()) {
                    maxFizTenyleges = masolatLista.get(i).getSalary();
                }
                if (minFizTenyleges > masolatLista.get(i).getSalary()) {
                    minFizTenyleges = masolatLista.get(i).getSalary();
                }
                osszFiz += masolatLista.get(i).getSalary();
                i++;
                letszam++;
            }
            if (valasztas.equals("tenyleges")) {
                rekord += minFizTenyleges + ", " + maxFizTenyleges + ")";
                kimenetiListaTenyleges.add(rekord);
            }
            if (valasztas.equals("atlagfizpermk")) {
                rekord += extra.Format.left(1.0 * osszFiz / letszam, 0, 2) + ")";
                kimenetiListaAtlagFizetesPerMk.add(rekord);
            }
            if (valasztas.equals("letszam")) {
                rekord += letszam + ")";
                kimenetiListaLetszam.add(rekord);
            }
        }
        if (valasztas.equals("tenyleges")) {
            return kimenetiListaTenyleges;
        } else if (valasztas.equals("eletpalya")) {
            return kimenetiListaEletpalya;
        } else  if (valasztas.equals("atlagfizpermk")){
            return kimenetiListaAtlagFizetesPerMk;
        }else{
            return kimenetiListaLetszam;
        }
    }

    public static List<String> jutalekMunkakoronkent(List<OracleHRData> lista) {
        List<String> kimenetiLista = new ArrayList<>();
        //másolat készítése az eredeti adatlistáról
        List<OracleHRData> masolatLista = new ArrayList<>(); //lefoglalok memóriát egy üres listának
        masolatLista.addAll(lista);
        //munkakör, majd jutalék szerint növekvő sorrendbe rendezzük az objektumokat a másolatlistában
        Collections.sort(masolatLista, (OracleHRData d1, OracleHRData d2) -> {
            if (d1.getJobTitle().compareTo(d2.getJobTitle()) == 0) {
                return new Double(d1.getCommission()).compareTo((d2.getCommission()));
            } else {
                return d1.getJobTitle().compareTo(d2.getJobTitle());
            }
        });
        //csoportváltás
        int i = 0;
        String rekord;
        while (i < masolatLista.size()) {
            String aktMunkakor = (masolatLista.get(i)).getJobTitle();
            while (i < masolatLista.size() && ((masolatLista.get(i)).getJobTitle()).equals(aktMunkakor)) {
                double aktSzazalek = masolatLista.get(i).getCommission();
                if (aktSzazalek * 100 % 1 == 0) {
                    rekord = "  " + aktMunkakor + "  (" + extra.Format.left(aktSzazalek * 100, 0, 0) + "%)";
                } else {
                    rekord = "  " + aktMunkakor + "  (" + extra.Format.left(aktSzazalek * 100, 0, 2) + "%)";
                }
                kimenetiLista.add(rekord);
                while (i < masolatLista.size() && ((masolatLista.get(i)).getJobTitle()).equals(aktMunkakor) && masolatLista.get(i).getCommission() == aktSzazalek) {
                    i++;
                }
            }
        }
        return kimenetiLista;
    }

    public static List<String> jutalekEloford(List<OracleHRData> lista) {
        List<String> kimenetiLista = new ArrayList<>();
        //másolat készítése az eredeti adatlistáról
        List<OracleHRData> masolatLista = new ArrayList<>(); //lefoglalok memóriát egy üres listának
        masolatLista.addAll(lista);
        //jutalék szerint növekvő sorrendbe rendezzük az objektumokat a másolatlistában
        Collections.sort(masolatLista, (OracleHRData d1, OracleHRData d2) -> {
            return Double.compare(d1.getCommission(), d2.getCommission());  //return d1.getSalary()-d2.getSalary()
        });
        //csoportváltás 
        int i = 0;
        String rekord;
        while (i < masolatLista.size()) {
            double aktJutalek = masolatLista.get(i).getCommission();
            if (aktJutalek * 100 % 1 == 0) {
                rekord = "  " + extra.Format.left(aktJutalek * 100, 0, 0) + "%  (";
            } else {
                rekord = "  " + extra.Format.left(aktJutalek * 100, 0, 2) + "%  (";
            }
            int dB = 0;
            while (i < masolatLista.size() && masolatLista.get(i).getCommission() == aktJutalek) {
                dB++;
                i++;
            }
            rekord += dB + " fő)";
            kimenetiLista.add(rekord);
        }
        return kimenetiLista;
    }

    public static List<String> kimutatasReszlegenkent(List<OracleHRData> lista, String valasztas) {
        List<String> listaAtlFiz = new ArrayList<>();
        List<String> listaLetszam = new ArrayList<>();
        List<OracleHRData> masolatLista = new ArrayList<>(); //lefoglalok memóriát egy üres listának
        masolatLista.addAll(lista);
        //részleg szerint növekvő sorrendbe rendezzük az objektumokat a másolatlistában, hogy tudjunk csoportváltást alkalmazni
        Collections.sort(masolatLista, (OracleHRData d1, OracleHRData d2) -> {
            return d1.getDepartmentName().compareTo(d2.getDepartmentName());
        });
        //csoportváltás 
        int i = 0;
        while (i < masolatLista.size()) {
            String aktReszleg = masolatLista.get(i).getDepartmentName();
            String rekord = "  " + aktReszleg + " (";
            int osszFizPerReszleg = 0;
            int letszam = 0;
            while (i < masolatLista.size() && (masolatLista.get(i).getDepartmentName()).equals(aktReszleg)) {
                osszFizPerReszleg += (masolatLista.get(i)).getSalary();
                letszam++;
                i++;
            }
//            rekord += String.format("%.2f",(1.0*osszFizPerReszleg)/letszam)+")";
            if (valasztas.equals("atlagfizetes")) {
                rekord += (extra.Format.left(1.0 * osszFizPerReszleg / letszam, 0, 2)).toString() + ")";
                listaAtlFiz.add(rekord);
            }
            if (valasztas.equals("letszam")) {
                rekord += letszam + ")";
                listaLetszam.add(rekord);
            }
        }
        if (valasztas.equals("atlagfizetes")) {
            return listaAtlFiz;
        } else {
            return listaLetszam;
        }
    }

    public static String[][] keszitGrafikonAdatTomb(List<String> inputLista) {
        String[][] grafikon1AdatTomb = new String[inputLista.size()][2];
        List<String> masolatLista = new ArrayList<>(); //lefoglalok memóriát egy üres listának
        masolatLista.addAll(inputLista);

        for (int i = 0; i < masolatLista.size(); i++) {
            String aktElem = masolatLista.get(i);
            int indexOfBracket = aktElem.indexOf("(");
//             System.out.println(aktElem);
            grafikon1AdatTomb[i][0] = aktElem.substring(0, indexOfBracket - 1);

//            System.out.println(aktElem);
            grafikon1AdatTomb[i][1] = aktElem.substring(indexOfBracket + 1, aktElem.length() - 1);
        }

        return grafikon1AdatTomb;              //elem[reszleg][atlagfizetes]
    }

    public static void jListFeltolt(JList jListaNev, DefaultListModel listaSzalagNev, List<String> adatLista) {
        jListaNev.setModel(listaSzalagNev);
        for (int i = 0; i < adatLista.size(); i++) {
            listaSzalagNev.addElement(adatLista.get(i));
        }

    }
    
    
    
    
    
/*Jutalék modellezése*/    

    //     public static String[][] JutalekLekerdezes(List<OracleHRData> adatLista, String valasztas) {  //a kimenet a választás szerint 2 lista lehet
//         String [][] kimenetiTombElofordulashoz = new String [adatLista.size()][3]; 
//        String [][] kimenetiTombMunkakoronkent = new String [adatLista.size()][3];
//        
//        List<OracleHRData> masolatLista = new ArrayList<>(); //lefoglalok memóriát egy üres listának
//        masolatLista.addAll(adatLista);
//
//        //részleg szerint növekvő sorrendbe rendezzük az objektumokat a másolatlistában, hogy tudjunk csoportváltást alkalmazni
//        Collections.sort(masolatLista, (OracleHRData d1, OracleHRData d2) -> {
//            return d1.getDepartmentName().compareTo(d2.getDepartmentName());
//        });
//        
//        int [] JutalekTomb = {0,5,15,15,5,0,10,15,0,5,10};  //tegyük fel, hogy a részlegek ABC sorrendjéhez igazítva a JutalekTomb tartalmazza az adott részleghez tart. commission-t
//        
//        //csoportváltás
//        int aktReszlegIndex = 0;
//        int aktMunkakorIndex=0;
//        int i = 0;       
//        while (i < masolatLista.size()) {
//            String aktReszleg = masolatLista.get(i).getDepartmentName();          
//            int jutalek = JutalekTomb[aktReszlegIndex];                                              //az adott részleghez lekéri a jutalékot a JutalékTomb-ből           
//            kimenetiTombElofordulashoz[aktReszlegIndex][0]= Integer.toString(jutalek);              //System.out.print(kimenetiTombElofordulashoz[i][1]+"%, ");  //0: JUTALÉK AZ ADOTT RÉSZLEGEN 
//            kimenetiTombElofordulashoz[aktReszlegIndex][1]=aktReszleg;                               //System.out.print(kimenetiTombElofordulashoz[i][0]+": ");   //1: RÉSZLEG NEVE      
//            
//             int dolgozokSzamaAktReszleg = 0; 
//             String aktMunkakor = "";              
//            while (i < masolatLista.size() && (masolatLista.get(i).getDepartmentName()).equals(aktReszleg)) {
//                if(!(masolatLista.get(i).getJobTitle().equals(aktMunkakor))){
//                    kimenetiTombMunkakoronkent[aktMunkakorIndex][0]=Integer.toString(jutalek);                    // System.out.print(kimenetiTombMunkakoronkent[i][1]+"%, ");   //0: JUTALÉK AZ ADOTT RÉSZLEGEN
//                    kimenetiTombMunkakoronkent[aktMunkakorIndex][1]=aktReszleg;                                    //System.out.print(kimenetiTombMunkakoronkent[i][0]+", ");      //1: RÉSZLEG NEVE                
//                    kimenetiTombMunkakoronkent[aktMunkakorIndex][2]=masolatLista.get(i).getJobTitle();             //System.out.print(kimenetiTombMunkakoronkent[i][2]+"\n");     //2:  MUNKAKÖR NEVE
//                    aktMunkakorIndex++;
//                }
//                aktMunkakor = masolatLista.get(i).getJobTitle(); 
//                dolgozokSzamaAktReszleg ++;               
//                i++;
//            }
//             kimenetiTombElofordulashoz[aktReszlegIndex][2]= Integer.toString(dolgozokSzamaAktReszleg); //System.out.print(kimenetiTombElofordulashoz[aktReszlegIndex][2]+" fő\n");  //2: AZ ADOTT RÉSZLEG DOLGOZÓINAK SZÁMA
//             aktReszlegIndex++;
//        }  
//             
//       if (valasztas == "elofordulas")       
//        return kimenetiTombElofordulashoz; //a tömb egy eleme: [JUTALÉK AZ ADOTT RÉSZLEGEN, RÉSZLEG NEVE, AZ ADOTT RÉSZLEG DOLGOZÓINAK SZÁMA] --> a tömb elejére kerülnek majd a "hasznos", azaz nem NULL elemek    
//       else                                                                                                                                 // --> az előfordulás meghatározásánál elég tehát a "hasznos" szakasz végéig futtatni a ciklust
//         return kimenetiTombMunkakoronkent; //a tömb egy eleme: [JUTALÉK AZ ADOTT RÉSZLEGEN, RÉSZLEG NEVE, MUNKAKÖR NEVE]                   -->  a tömb elejére kerülnek majd a "hasznos", azaz nem NULL elemek 
//                                                                                                                                           //--> az eloszlás meghatározásánál elég lesz a "hasznos" szakasz végéig futtatni a ciklust
//    }
//     
//     
//     
//    public static List<String> JutalekElofordulas(String[][] adatTomb){
//    List<String> kimenetiListaElofordulas = new ArrayList<>();
//     List<String> SzazalekLista = new ArrayList<>();
//    int i=0;       
//    while(i<adatTomb.length && !(adatTomb[i][0]==null || adatTomb[i][1]==null || adatTomb[i][2]==null)){        
//        String szoveg ="";
//        String aktSzazalek = adatTomb[i][0];      
//        int dB=0;   
//        if (!(SzazalekLista.contains(aktSzazalek))){
//            szoveg=aktSzazalek+"% (";
//            int j=0;             
//            while (j < adatTomb.length && !(adatTomb[i][0]==null || adatTomb[i][1]==null || adatTomb[i][2]==null)) {
//                if (adatTomb[j][0] != null && adatTomb[j][0].equals(aktSzazalek)){ 
//                    dB+=Integer.parseInt(adatTomb[j][2]);
//                }
//                j++;
//            }    
//            szoveg += dB +"fő)";
//        }         
//        SzazalekLista.add(aktSzazalek);       
//        if (szoveg!="")
//            kimenetiListaElofordulas.add(szoveg);        
//        i++; 
//    }     
//    Collections.sort(kimenetiListaElofordulas, (String s1, String s2) -> { //a kimeneti lista rendezése, hogy százalékok szerint növekvő sorrendet kapjunk
//            int index=s1.indexOf("%");
//            int szam1 = Integer.parseInt(s1.substring(0,index));
//            index=s2.indexOf("%");
//            int szam2 = Integer.parseInt(s2.substring(0,index));        
//            return szam1-szam2;
//        });
//    return kimenetiListaElofordulas;
//}
//  public static List<String> JutalekMunkakoronkent(String[][] adatTomb) {
//        List<String> kimenetiListaMunkakoronkent = new ArrayList<>();
//        int i= 0;
//         while(i<adatTomb.length && !(adatTomb[i][0]==null || adatTomb[i][1]==null || adatTomb[i][2]==null)){  
//             kimenetiListaMunkakoronkent.add(adatTomb[i][2]+" ("+adatTomb[i][0]+"%)");           
//             i++;
//         }
//         Collections.sort(kimenetiListaMunkakoronkent, (String s1, String s2) -> { //a kimeneti lista rendezése munkakörönként                    
//            return s1.compareTo(s2);
//        });
//        return kimenetiListaMunkakoronkent;
//    }
    
    
}
