package oraclehrriport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import org.w3c.dom.Element;

public class OracleHRData {
  private int employeeID, salary, employeeManagerID, departmentID,
    departmentManagerID, minSalary, maxSalary;
  private String employeeName, email, jobTitle, departmentName;
  private GregorianCalendar hireDate;
  private double commission;

  private OracleHRData(int employeeID, int salary, int employeeManagerID,
      int departmentID, int departmentManagerID, int minSalary, int maxSalary,
      String employeeName, String email, String jobTitle, String departmentName,
      GregorianCalendar hireDate, double commission) {
    this.employeeID=employeeID;
    this.salary=salary;
    this.employeeManagerID=employeeManagerID;
    this.departmentID=departmentID;
    this.departmentManagerID=departmentManagerID;
    this.minSalary=minSalary;
    this.maxSalary=maxSalary;
    this.employeeName=employeeName;
    this.email=email;
    this.jobTitle=jobTitle;
    this.departmentName=departmentName;
    this.hireDate=hireDate;
    this.commission=commission;
  }
  
  private static String getItem(Element element, String tagName) {
    return element.getElementsByTagName(tagName).item(0).getFirstChild().getNodeValue();
  }
  
  public static OracleHRData createOracleHRData(Element element) {
    int employeeID=Integer.parseInt(getItem(element, "EmployeeID"));
    int salary=Integer.parseInt(getItem(element, "Salary"));
    int employeeManagerID=0;
    try { 
      employeeManagerID=Integer.parseInt(getItem(element, "EmployeeManagerID"));
    }
    catch(NumberFormatException | NullPointerException e) {
      //e.printStackTrace(); //1 fő vezetőnek nincs vezetője
    }
    int departmentID=Integer.parseInt(getItem(element, "DepartmentID"));
    int departmentManagerID=Integer.parseInt(getItem(element, "DepartmentManagerID"));
    int minSalary=Integer.parseInt(getItem(element, "MinSalary"));
    int maxSalary=Integer.parseInt(getItem(element, "MaxSalary"));
    double commission=0;
    try { 
      commission=Double.parseDouble(getItem(element, "Commission").replace(",", ".")); //!!!!!!!!!!!!!!!
    }
    catch(NumberFormatException | NullPointerException e) {
      //e.printStackTrace(); //nem minden munkakörhöz tartozik jutalék
    }
    String employeeName=getItem(element, "EmployeeName");
    String email=getItem(element, "Email");
    String jobTitle=getItem(element, "JobTitle");
    String departmentName=getItem(element, "DepartmentName");
    GregorianCalendar hireDate=new GregorianCalendar();
    try {      
      hireDate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(getItem(element, "HireDate")));
    }
    catch(ParseException e) {
      //e.printStackTrace(); //ideális állapotot feltételezve, mivel az adatok SQL2XML-ből származnak
    }
    return new OracleHRData(employeeID, salary, employeeManagerID,
      departmentID, departmentManagerID, minSalary, maxSalary,
      employeeName, email, jobTitle, departmentName,
      hireDate, commission);
  }
/*<Data>
    <EmployeeID>100</EmployeeID>
    <EmployeeName>Steven King</EmployeeName>
    <Email>SKING</Email>
    <HireDate>1987-06-17</HireDate>
    <JobTitle>President</JobTitle>
    <Salary>24000</Salary>
    <Commission></Commission>
    <EmployeeManagerID /> //vezetőnek null
    <DepartmentID>90</DepartmentID>
    <DepartmentName>Executive</DepartmentName>
    <DepartmentManagerID>100</DepartmentManagerID>
    <MinSalary>20000</MinSalary>
    <MaxSalary>40000</MaxSalary>
  </Data>
*/  

  public int getEmployeeID() {
    return employeeID;
  }

  public int getSalary() {
    return salary;
  }

  public int getEmployeeManagerID() {
    return employeeManagerID;
  }

  public int getDepartmentID() {
    return departmentID;
  }

  public int getDepartmentManagerID() {
    return departmentManagerID;
  }

  public int getMinSalary() {
    return minSalary;
  }

  public int getMaxSalary() {
    return maxSalary;
  }

  public String getEmployeeName() {
    return employeeName;
  }

  public String getEmail() {
    return email;
  }

  public String getJobTitle() {
    return jobTitle;
  }

  public String getDepartmentName() {
    return departmentName;
  }

  public GregorianCalendar getHireDate() {
    return hireDate;
  }  

  public double getCommission() {
    return commission;
  }

/*NÉVJEGY - SAJÁT METÓDUSOK (Antal Adrienn)*/
    
    //a keresztnév vagy középső név és a vezetéknév közötti szóköz indexét adja vissza (feltéve, ha a dolgozónak 1 vezetékneve van)
    private int IndexOfSpaceBtwFirstLastName() {
        int indexOfLastSpace = 0;
        int i = employeeName.length() - 1; //a név legvégéről indulunk, visszafele lépegetve      
        boolean found = false;
        while (!found && i > 0) {
            found = (employeeName.charAt(i) == ' ');
            if (found) {
                indexOfLastSpace = i;
            }
            i--;
        }
        //a holland neveknél a középső név a vezetéknév része, tehát nem lehet 2. keresztnév --> erre mindenképp oda kell figyelni, ha vezetéknév szerint rendezünk!
        String[] DutchMiddleNames = {" De ", " Van ", " Van Der ", " Den ", " Van Den "}; //tipikus, nyelvre jellemző, holland középső nevek 
        int j = 0;
        boolean dutchName = false;
        while (j < DutchMiddleNames.length && !dutchName) {
            dutchName = (employeeName.contains(DutchMiddleNames[j]));  //megnézzük, az adott név tartalmazza-e valamelyik holland középső nevet        
            j++;
        }
          if (dutchName) {                                            //ha igen, akkor az indexOfLastSpace a középső név előtti space indexe legyen
                i = 0; //      
                found = false;
                while (!found && i<employeeName.length()) {
                    found = (employeeName.charAt(i) == ' ');
                    if (found) {
                        indexOfLastSpace = i;
                    }
                    i++;
                }
            }
        return indexOfLastSpace;

    }
    
    
    public String getEmployeeLastName() {  // egy dolgozó vezetéknevét adja vissza
        int indexOfLastSpace = IndexOfSpaceBtwFirstLastName();
        return employeeName.substring(indexOfLastSpace + 1);
    }

    public String getEmployeeFirstAndMiddleName() { // egy dolgozó keresztnevét adja vissza
        int indexOfLastSpace = IndexOfSpaceBtwFirstLastName();
        return employeeName.substring(0, indexOfLastSpace);
    }
    
      

    public String getRandomPhoneNumber(){
    String phoneNr = "+36";
    String [] provider = {"20","30","70"};
    phoneNr += provider[(int)(Math.random()*3)];
        for (int i = 0; i < 7; i++) {
            int szam = (int)(Math.random()*10); //0-9
            phoneNr += Integer.toString(szam);
        }
    return phoneNr;
    }

    
    

}
