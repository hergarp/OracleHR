package oraclehrriport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class OracleHRXML {
  private List<OracleHRData> dataList=new ArrayList<>(); 

  public OracleHRXML() {
		try {
      File xmlFajl=new File("./files/OracleHRData.xml");
			Document d=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFajl);
      NodeList nodeList=d.getDocumentElement().getElementsByTagName("Data");
      for(int i=0; i<nodeList.getLength(); i++)
        dataList.add(OracleHRData.createOracleHRData((Element)nodeList.item(i)));
		}
    catch(ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace(); //élesben MVC szerint ezt másképp kell, most tfh. minden OK
		}
  }

  public List<OracleHRData> getDataList() {
    return dataList;
  }
}