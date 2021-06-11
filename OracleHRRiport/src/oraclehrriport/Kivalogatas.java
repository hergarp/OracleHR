package oraclehrriport;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Kivalogatas {
    
    private Integer[] egyediFizetesTomb;
    private JSpinner jspinnerMin=new JSpinner();
    private JSpinner jspinnerMax=new JSpinner();
    private List<Integer> adatListaSalary = new ArrayList<>();
    private SpinnerListModel minSzamLista;
    private SpinnerListModel maxSzamLista;
    private JButton jbSzur=new JButton();
    private JList jlLista=new JList();
    private List<OracleHRData> adatLista=new ArrayList<>();
    
    public void departmentManagers(List<OracleHRData> adatLista, JComboBox jcombobox, JList jlist ){
        List<String> reszlegvezetokLista=new ArrayList<>();
        List<List<String>> reszlegalkalmazottakLista=new ArrayList<>();
        for (OracleHRData adat : adatLista) {
            if (adat.getDepartmentManagerID()==adat.getEmployeeID()) {
                int vezetoid=adat.getDepartmentManagerID();
                List<OracleHRData> alkalmazottakLista=adatLista.stream().filter(
                    x->x.getDepartmentManagerID()==vezetoid && vezetoid!=x.getEmployeeID()).collect(Collectors.toCollection(()->new ArrayList<>()));
                alkalmazottakLista.sort((OracleHRData a1, OracleHRData a2)->a1.getEmployeeName().compareTo(a2.getEmployeeName()));
                List<String> lista=new ArrayList<>();
                for (OracleHRData alkalmazott : alkalmazottakLista) {
                    lista.add(alkalmazott.getEmployeeName()+" ("+alkalmazott.getJobTitle()+")");
                }
                reszlegalkalmazottakLista.add(lista);
                reszlegvezetokLista.add(adat.getEmployeeName()+" - "+adat.getDepartmentName()+" ("+alkalmazottakLista.size()+" fő)");
                
            }
        }
        jcombobox.setModel(new DefaultComboBoxModel(reszlegvezetokLista.toArray()));
        jlistModel(jcombobox, jlist, reszlegalkalmazottakLista);
        jcombobox.addActionListener((ActionEvent e) -> {
            jlistModel(jcombobox, jlist, reszlegalkalmazottakLista);
            });
    }
    
    public void employeeManagers(List<OracleHRData> adatLista, JComboBox jcombobox, JList jlist){
        List<String> vezetokLista=new ArrayList<>();
        List<Integer>vezetokListaID=new ArrayList<>();
        List<List<String>> vezetoalkalmazottakLista=new ArrayList<>();
        adatLista.stream().collect(Collectors.groupingBy(e->e.getEmployeeManagerID())).forEach((k,v)->{vezetokListaID.add(k);});
        for (OracleHRData adat : adatLista) {
            for (Integer id : vezetokListaID ) {
                if (adat.getEmployeeID()==id) {
                    List<OracleHRData> alkalmazottakLista=adatLista.stream().filter(
                        x->x.getEmployeeManagerID()==id && x.getEmployeeID()!=id).collect(Collectors.toCollection(()->new ArrayList<>()));
                    alkalmazottakLista.sort((OracleHRData a1, OracleHRData a2)->a1.getEmployeeName().compareTo(a2.getEmployeeName()));
                    vezetokLista.add(adat.getEmployeeName()+" - "+adat.getJobTitle()+" ("+alkalmazottakLista.size()+" fő)");
                    List<String> ideiglenesLista=new ArrayList<>();
                    for (OracleHRData alkalmazott : alkalmazottakLista) {
                      ideiglenesLista.add(alkalmazott.getEmployeeName()+" ("+alkalmazott.getJobTitle()+")");
                    }
                    vezetoalkalmazottakLista.add(ideiglenesLista);
                }
            }        
        }  
        jcombobox.setModel(new DefaultComboBoxModel(vezetokLista.toArray()));
            jlistModel(jcombobox, jlist, vezetoalkalmazottakLista);
            jcombobox.addActionListener((ActionEvent e) -> {
               jlistModel(jcombobox, jlist, vezetoalkalmazottakLista);
          });
    }
    
    public void jlistModel(JComboBox jbox, JList jlist, List<List<String>> list){
        int index=jbox.getSelectedIndex();
        DefaultListModel listaModel=new DefaultListModel();
        jlist.setModel(listaModel);
        for (String string : list.get(index)) {
            listaModel.addElement(string);
        }
    }
    
    public void departmentAndJobtitle(List<OracleHRData> adatLista, JComboBox combobox1, JComboBox combobox2, JList jlist){
        List<String> reszlegNevekLista=new ArrayList<>();
        List<List<String>> munkakorokLista=new ArrayList<>();
        adatLista.stream().collect(Collectors.groupingBy(d->d.getDepartmentName())).forEach((k,v)->{reszlegNevekLista.add(k+" ("+v.size()+" fő)");});
        for (String reszleg : reszlegNevekLista) {
                List<String> jobtitles=adatLista.stream().filter(x->reszleg.contains(
                        x.getDepartmentName())).map(x->x.getJobTitle()).distinct().collect(Collectors.toCollection(()->new ArrayList<>()));
                for (String jobtitle : jobtitles) {
                long dolgozokszama=adatLista.stream().filter(x->reszleg.contains(x.getDepartmentName()) && jobtitle.equals(x.getJobTitle())).count();
                int ind=jobtitles.indexOf(jobtitle);
                jobtitles.set(ind, jobtitle+" ("+dolgozokszama+" fő)");
            }               
                munkakorokLista.add(jobtitles);
            }
        combobox1.setModel(new DefaultComboBoxModel(reszlegNevekLista.toArray()));
        combobox2.setModel(new DefaultComboBoxModel(munkakorokLista.get(0).toArray()));
        jListModel2(adatLista,0, 0, jlist, reszlegNevekLista, munkakorokLista);
        combobox1.addActionListener((ActionEvent e) -> {
            int index=combobox1.getSelectedIndex();           
            combobox2.setModel(new DefaultComboBoxModel(munkakorokLista.get(index).toArray()));
            jListModel2(adatLista, index, 0, jlist, reszlegNevekLista, munkakorokLista);
        });
        combobox2.addActionListener((ActionEvent e) -> {
          int index=combobox1.getSelectedIndex();
          int index2=combobox2.getSelectedIndex();
           jListModel2(adatLista,index, index2, jlist, reszlegNevekLista, munkakorokLista);
        });      
    }
    
    public void jListModel2(List<OracleHRData> adatLista,Integer index1, Integer index2, JList jlist, List<String> list1, List<List<String>> list2){
        List<String> dolgozok=adatLista.stream().filter(x->list1.get(index1).contains(
                x.getDepartmentName()) && x.getJobTitle().equals(
                    list2.get(index1).get(index2).split(" \\(")[0])).map(x->x.getEmployeeName()).sorted().collect(Collectors.toCollection(()->new ArrayList<>()));
        DefaultListModel listaModel=new DefaultListModel();
        jlist.setModel(listaModel);
        for (String dolgozo : dolgozok) {
            listaModel.addElement(dolgozo);
        }
    }
    
    
    public void employeeSalary(List<OracleHRData> adatLista, JSpinner jspinner1, JSpinner jspinner2, JList jlist, JButton jbutton1, JButton jbutton2){
        int minFizetes=adatLista.stream().mapToInt(x->x.getSalary()).min().orElse(-1);
        int maxFizetes=adatLista.stream().mapToInt(x->x.getSalary()).max().orElse(-1);
        jspinnerMin=jspinner1;
        jspinnerMax=jspinner2;
        jlLista=jlist;
        jbSzur=jbutton1;
        this.adatLista=adatLista;
        Set<Integer> rendezettHalmaz=new TreeSet<>();
        for (OracleHRData adat : adatLista) {
            rendezettHalmaz.add(adat.getSalary());
        }
        egyediFizetesTomb=new Integer[rendezettHalmaz.size()];
        int i=0;
        for (Integer fizetes : rendezettHalmaz) {
            egyediFizetesTomb[i++]=fizetes;
            adatListaSalary.add(fizetes);
        }
        minSzamLista=new SpinnerListModel(adatListaSalary);
        maxSzamLista=new SpinnerListModel(adatListaSalary);
        jspinner1.setModel(minSzamLista);
        jspinner1.setValue(egyediFizetesTomb[0]);
        jspinner1.addChangeListener(minValtozas);
        jspinner2.setModel(maxSzamLista);
        jspinner2.setValue(egyediFizetesTomb[egyediFizetesTomb.length-1]);
        jspinner2.addChangeListener(maxValtozas);
        List<OracleHRData> alkalmazottakLista=adatLista.stream().filter(
                x->x.getSalary()>=minFizetes || x.getSalary()<=maxFizetes).collect(Collectors.toCollection(()->new ArrayList<>()));
        alkalmazottakLista.sort((OracleHRData a1, OracleHRData a2)->Integer.compare(a1.getSalary(), a2.getSalary()));
        DefaultListModel listaModel=new DefaultListModel();
        jlist.setModel(listaModel);
        for (OracleHRData alkalmazott : alkalmazottakLista) {
          listaModel.addElement(alkalmazott.getEmployeeName()+" ("+alkalmazott.getSalary()+")");
        }
        jbutton1.setText("Szűr ("+alkalmazottakLista.size()+" fő)");

        jbutton1.addActionListener((ActionEvent e) -> {
            int value1=(int)jspinner1.getValue();
            int value2=(int)jspinner2.getValue();
            List<OracleHRData> szurtalkalmazottakLista=adatLista.stream().filter(
                    x->x.getSalary()>=value1 && x.getSalary()<=value2).collect(Collectors.toCollection(()->new ArrayList<>()));
            szurtalkalmazottakLista.sort((OracleHRData a1, OracleHRData a2)->Integer.compare(a1.getSalary(), a2.getSalary()));
            DefaultListModel szurtlistaModel=new DefaultListModel();
            jlist.setModel(szurtlistaModel);
            for (OracleHRData string : szurtalkalmazottakLista) {
              szurtlistaModel.addElement(string.getEmployeeName()+" ("+string.getSalary()+")");
            }
        });

        jbutton2.addActionListener((ActionEvent e) -> {
            DefaultListModel torlestlistaModel=new DefaultListModel();
            jlist.setModel(torlestlistaModel);
            torlestlistaModel.clear();
            jspinner1.setValue(minFizetes);
            jspinner2.setValue(maxFizetes);
        }); 
    }
  
    ChangeListener minValtozas=new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            int min=(int)jspinnerMin.getValue();
            int max=(int)jspinnerMax.getValue();
            jspinnerMax.removeChangeListener(maxValtozas);
            List<Integer>ujMaxFizetesLista=new ArrayList<>();
            for (Integer fizetes : egyediFizetesTomb) {
                if (fizetes>=min) {
                    ujMaxFizetesLista.add(fizetes);
                }
            }
            maxSzamLista.setList(ujMaxFizetesLista);
            jspinnerMax.addChangeListener(maxValtozas);
            jspinnerMax.setValue(max);
            List<OracleHRData> szurtalkalmazottak=adatLista.stream().filter(x->x.getSalary()>=min && x.getSalary()<=max).collect(Collectors.toCollection(()->new ArrayList<>()));
            jbSzur.setText("Szűr ("+szurtalkalmazottak.size()+" fő)");
            DefaultListModel torlestlistaModel=new DefaultListModel();
            jlLista.setModel(torlestlistaModel);
            torlestlistaModel.clear();
        }
    };
            
    ChangeListener maxValtozas=new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            int min=(int)jspinnerMin.getValue();
            int max=(int)jspinnerMax.getValue();
            jspinnerMin.removeChangeListener(minValtozas);
            List<Integer>ujMinFizetesLista=new ArrayList<>();
            for (Integer fizetes : egyediFizetesTomb) {
                if (fizetes<=max) {
                    ujMinFizetesLista.add(fizetes);
                }
            }
            minSzamLista.setList(ujMinFizetesLista);
            jspinnerMin.addChangeListener(minValtozas);
            jspinnerMin.setValue(min);
            List<OracleHRData> szurtalkalmazottak=adatLista.stream().filter(x->x.getSalary()>=min && x.getSalary()<=max).collect(Collectors.toCollection(()->new ArrayList<>()));
            jbSzur.setText("Szűr ("+szurtalkalmazottak.size()+" fő)");
            DefaultListModel torlestlistaModel=new DefaultListModel();
            jlLista.setModel(torlestlistaModel);
            torlestlistaModel.clear();               
        }
    };

            
      
   
}
