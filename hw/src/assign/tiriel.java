package assign;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import java.io.FileOutputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import java.nio.file.Paths;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
public class tiriel{
   public static void main(String[]args){
      try{
         String[] fileNames={
            "./떡.html",
            "./라면.html",
            "./초밥.html",
            "./아이스크림.html",
            "./파스타.html"
         };
         DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
         DocumentBuilder builder=factory.newDocumentBuilder();
         org.w3c.dom.Document documentOut=builder.newDocument();
         org.w3c.dom.Element elementDocs=documentOut.createElement("docs");
         documentOut.appendChild(elementDocs);
         int id=0;
         for(String fileName : fileNames){
            String html="";
            Path path=Paths.get(fileName);
            List<String>lines=Files.readAllLines(path);
            for(String line : lines){
               html+=line;
            }
            Document documentIn=Jsoup.parse(html);
            Elements elements;
            elements=documentIn.select("head title");
            String title="",body="";
            for(Element element : elements){
               title=element.text();
               break;
            }
            elements=documentIn.select("body div p");
            for(Element element : elements){
               body+=element.text()+"\n";
            }
            org.w3c.dom.Element elementDoc=documentOut.createElement("doc");
            elementDoc.setAttribute("id",""+id);
            id++;
            elementDocs.appendChild(elementDoc);
            org.w3c.dom.Element elementTitle=documentOut.createElement("title");
            elementDoc.appendChild(elementTitle);
            elementTitle.appendChild(documentOut.createTextNode(title));
            org.w3c.dom.Element elementBody=documentOut.createElement("body");
            elementDoc.appendChild(elementBody);
            elementBody.appendChild(documentOut.createTextNode(body));
         }
         TransformerFactory transformerFactory=TransformerFactory.newInstance();
         Transformer transformer=transformerFactory.newTransformer();
         transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
         DOMSource source=new DOMSource(documentOut);
         StreamResult result=new StreamResult(new FileOutputStream(new File("./book.xml")));
         transformer.transform(source,result);
      }catch(Exception e){
         System.out.println(e.toString());
      }
   }
}