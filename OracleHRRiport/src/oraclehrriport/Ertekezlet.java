package oraclehrriport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultListModel;

public class Ertekezlet {
    public static List<Department> createSortedDepartmentList(List<OracleHRData> adatLista) {
        List<Department> departmentList = new ArrayList<>(createDepartmentMap(adatLista).values());
//        departmentList.sort();
        Collections.sort(departmentList);
        return departmentList;
    }
    
    
    public static Map<Integer, Department> createDepartmentMap(List<OracleHRData> adatLista) {
        Map<Integer, Department> departments = new HashMap<>();
        for (OracleHRData adat : adatLista) {
            int departmentID = adat.getDepartmentID();
            
            if (!departments.containsKey(departmentID)) {
                String departmentName = adat.getDepartmentName();
                Department department = new Department(departmentID, departmentName);
                departments.put(departmentID, department);
            }
            
            String employeeName = adat.getEmployeeName();
            String email = adat.getEmail();
            Employee employee = new Employee(employeeName, email);
            departments.get(departmentID).addEmployee(employee);
        }
        
        return departments;
    }
    
    public static int getSummarizedHeadcount(Set<Department> activeDeps) {
        return activeDeps.stream().mapToInt(Department::getSize).sum();
    }
    
    public static String getSelectedDeps(Set<Department> activeDeps) {
//        System.out.println(activeDeps);
//        System.out.println(activeDeps.size());
//        System.out.println(activeDeps.stream().mapToInt(Department::getVisibleTitle).toArray());
        List<String> depsList = new ArrayList<>();
        for(Department dep : activeDeps) {
            depsList.add(dep.getVisibleTitle());
        }
//        return depsList.toString();
        return String.join(", ", depsList);
    }
    
//    public static Set<Department> getSelectedEmployees(Set<Department> activeDeps) {
//        return null;
//    }
    public static DefaultListModel getEmployeesNameList(Set<Department> activeDeps){
         List<String> empNameList = new ArrayList<>();
        for(Department dep : activeDeps) {
            for (Employee emp :dep.getEmployees()) {
                empNameList.add(emp.getEmployeeName());
            }
//            empNameList.add(((Employee)dep.getEmployees().get(0)).getEmployeeName());
            
    }
        //sorbarendezés
        Collections.sort(empNameList);
        DefaultListModel dlm=new DefaultListModel();
        for (String name: empNameList ) {
            dlm.addElement(name);
        }
        return dlm;
    }
    public static DefaultListModel getEmployeesEmailList(Set<Department> activeDeps){
         List<String> empNameList = new ArrayList<>();
        for(Department dep : activeDeps) {
            for (Employee emp :dep.getEmployees()) {
                empNameList.add(emp.getEmail());
            }
//            empNameList.add(((Employee)dep.getEmployees().get(0)).getEmployeeName());
            
    }
        //sorbarendezés
        Collections.sort(empNameList);
        DefaultListModel dlm=new DefaultListModel();
        for (String name: empNameList ) {
            dlm.addElement(name);
        }
        return dlm;
    }
}


class Department implements Comparable<Department>{
    private int departmentID;
    private String departmentName;
    private List<Employee> employees;
    
    
    
    public Department(int departmentID, String departmentName) {
        this.departmentID = departmentID;
        this.departmentName = departmentName;
        this.employees = new ArrayList<>();
    }
    
    public void addEmployee(Employee employee) {
        this.employees.add(employee);
    }
    
    public int getSize() {
        return employees.size();
    }
    
    public int getDepartmentID() {
        return departmentID;
    }
    
    public String getDepartmentName() {
        return departmentName;
    }
    
    public List<Employee> getEmployees() {
        return employees;
    }
    
    public String getVisibleTitle() {
        return String.format("%s (%d fő)", departmentName, getSize());
    }

    @Override
    public int compareTo(Department department) {
//        return (getSize()-department.getSize())*-1;
//        return this.departmentName.compareTo(department.getDepartmentName());
        if(getSize() == department.getSize()) {
            return departmentName.compareTo(department.getDepartmentName()); //azonos létszámúak növekvő ábécében
        }
        return -(getSize()-department.getSize()); //csökkenő sorrendben kiírás a fők száma szerint
    }
    
    @Override
    public String toString() {
        return departmentName;
    }
    
}

class Employee {
    private String employeeName;
    private String email;
    
    public Employee(String employeeName, String email) {
        this.employeeName = employeeName;
        this.email = email.toLowerCase() + "@oracle.hu";
    }
    
//    public String getEmployeeEmail() {
//        return email + "@oracle.hu";
//    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getEmail() {
        return email;
    }
    
}


