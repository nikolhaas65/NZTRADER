/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Configurer;

import javax.xml.transform.*;
import java.io.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

/**
 *
 * @author nik
 */
public class AtXMLWriter {

  Document mDoc = null;

  public AtXMLWriter() {
    try{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        mDoc = db.newDocument();
    } catch(Exception ex) {};
  }

// This method writes a DOM document to a file
  public static void writeXmlFile(Document doc, String filename) {
      try {
          // Prepare the DOM document for writing
          Source source = new DOMSource(doc);

          // Prepare the output file
          File file = new File(filename);
          Result result = new StreamResult(file);

          // Write the DOM document to the file
          Transformer xformer = TransformerFactory.newInstance().newTransformer();
          xformer.transform(source, result);
      } catch (TransformerConfigurationException e) {
      } catch (TransformerException e) {
      }
  }
}
