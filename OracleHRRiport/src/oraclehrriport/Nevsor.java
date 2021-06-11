package oraclehrriport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Nevsor {

    private Nevsor() {
        ;
    }

    public static List<String> alkalmazottakNevsor(List<OracleHRData> lista) {
        List<String> nevsorLista = new ArrayList<>();
        for (OracleHRData elem : lista) {
            nevsorLista.add(elem.getEmployeeName());

        }
        Collections.sort(nevsorLista);
        return nevsorLista;
    }

    public static List<String> reszlegvezetokNevsor(List<OracleHRData> lista) {
        List<String> ReszlegvezetokLista = new ArrayList<String>();
        for (OracleHRData elem : lista) {

            if (elem.getEmployeeID() == elem.getDepartmentManagerID()) {
                ReszlegvezetokLista.add(elem.getEmployeeName() + "  " + "(" + elem.getDepartmentName() + ")");

            }

        }

        Collections.sort(ReszlegvezetokLista);
        return ReszlegvezetokLista;
    }

    public static List<String> vezetokNevsor(List<OracleHRData> lista) {
        List<Integer> OsszesManagerID = new ArrayList<Integer>();
        List<String> VezetokLista = new ArrayList<String>();
        for (OracleHRData elem : lista) {

 

            OsszesManagerID.add(elem.getEmployeeManagerID());
        }
        
        Collections.sort(OsszesManagerID);
        List<Integer> egyediOsszesManagerID = new ArrayList<>();
        
        int i = 0;
        while (i < OsszesManagerID.size()) {
            int aktManagerID = OsszesManagerID.get(i);
            int db = 0;
            while (i < OsszesManagerID.size() && OsszesManagerID.get(i).equals(aktManagerID)) {
                i++;

 

            }
            egyediOsszesManagerID.add(aktManagerID);
        }
        
        for (OracleHRData elem : lista) {

 

            for (Integer integer : egyediOsszesManagerID) {
                if (integer == elem.getEmployeeID()) {
                    
                        VezetokLista.add(elem.getEmployeeName() + "  " + "(" + elem.getJobTitle() + ")");
                    
                }

 

            }

 

        }

 

        Collections.sort(VezetokLista);
        return VezetokLista;
    }

    public static List<String> reszlegNevek(List<OracleHRData> lista) {
        List<String> reszlegnevekLista = new ArrayList<>();
        
        for (OracleHRData elem : lista) {
            reszlegnevekLista.add(elem.getDepartmentName());
        }
        Collections.sort(reszlegnevekLista);
        List<String> egyediReszlegnevLista = new ArrayList<>();
        
        int i = 0;
        while (i < reszlegnevekLista.size()) {
            String aktReszlegnev = reszlegnevekLista.get(i);
            int db = 0;
            while (i < reszlegnevekLista.size() && reszlegnevekLista.get(i).equals(aktReszlegnev)) {
                db++;
                i++;

            }
            String darabfo = " (" + db + " fő)";
            egyediReszlegnevLista.add(aktReszlegnev+darabfo);
        }

        return egyediReszlegnevLista;

    }
    public static List<String> munkakorNevek(List<OracleHRData> lista) {
        List<String> munkakorNevekLista = new ArrayList<>();
        
        for (OracleHRData elem : lista) {
            munkakorNevekLista.add(elem.getJobTitle());
        }
        Collections.sort(munkakorNevekLista);
        List<String> egyediMunkakorLista = new ArrayList<>();
        
        int i = 0;
        while (i < munkakorNevekLista.size()) {
            String aktMunkakornev = munkakorNevekLista.get(i);
            int db = 0;
            while (i < munkakorNevekLista.size() && munkakorNevekLista.get(i).equals(aktMunkakornev)) {
                db++;
                i++;

            }
            String dbfo = " (" + db + " fő)";
            egyediMunkakorLista.add(aktMunkakornev+dbfo);
        }

        return egyediMunkakorLista;

    }
    
    public static List<String> adatokID(List<OracleHRData> lista) {
        List<Integer> IDAdatNevekLista = new ArrayList<>();
        
        for (OracleHRData elem : lista) {
            IDAdatNevekLista .add(elem.getDepartmentID());
        }
        Collections.sort(IDAdatNevekLista );
        List<String> IDAdatokLista = new ArrayList<>();
        
        int i = 0;
        while (i < IDAdatNevekLista .size()) {
            Integer IDnev = IDAdatNevekLista.get(i);
            int db = 0;
            while (i < IDAdatNevekLista.size() && IDAdatNevekLista.get(i).equals(IDnev)) {
                db++;
                i++;

 

            }
            String dbfo = " (" + db + " fő)";
            IDAdatokLista.add(IDnev+dbfo);
        }

 

        return IDAdatokLista;
        
        
        

    }
    
//    EXTRA2
    
    public static List<String> alkalmazottakNev(List<OracleHRData> lista) {
        List<String> alkalmazottNevek = new ArrayList<>();
        for (OracleHRData elem : lista) {
            alkalmazottNevek.add(elem.getEmployeeName()+" ("+elem.getEmployeeID()+")");

        }
        Collections.sort(alkalmazottNevek);
        return alkalmazottNevek;
    }
}
    


    
    

