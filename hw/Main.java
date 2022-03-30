import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import org.xml.sax.InputSource;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Iterator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
public class Main{
	private static Element getElementByTag(Element parent,String tag){
		for(Element child=(Element)parent.getFirstChild();child!=null;child=(Element)child.getNextSibling()){
			if(child.getNodeName().equals(tag)){
				return child;
			}
		}
		return null;
	}
	public static void main(String[]args){
		try{
			File xmlFile=new File("./index.xml");
			InputStream inputStream=new FileInputStream(xmlFile);
			Reader reader=new InputStreamReader(inputStream,"UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			DocumentBuilder builder=factory.newDocumentBuilder();
			Document documentIn;
			Element elementDocsIn;
			Element elementIn;
			documentIn=builder.parse(is);
			elementDocsIn=(Element)documentIn.getFirstChild();
			int documentCount=elementDocsIn.getChildNodes().getLength();
			List<String>docIdList=new ArrayList<String>();
			HashMap<String,Integer> keywordDocCountMap=new HashMap<String,Integer>();
			List<HashMap<String,Integer>>keywordCountGroup=new ArrayList<HashMap<String,Integer>>();
			for(elementIn=(Element)elementDocsIn.getFirstChild();elementIn!=null;elementIn=(Element)elementIn.getNextSibling()){
				HashMap<String,Integer>keywordCountMap=new HashMap<String,Integer>();
				keywordCountGroup.add(keywordCountMap);
				String id=elementIn.getAttribute("id");
				docIdList.add(id);
				Element bodyElementIn;
				bodyElementIn=getElementByTag(elementIn,"body");
				if(bodyElementIn==null){
					return;
				}
				String body=bodyElementIn.getTextContent();
				String[]keywordList=body.split("#");
				for(int i=0;i<keywordList.length;++i){
					String keyword=keywordList[i];
					String[]kv=keyword.split(":");
					String key=kv[0];
					String value=kv[1];
					Integer prevCount=keywordDocCountMap.get(key);
					int count=Integer.parseInt(value);
					keywordCountMap.put(key,count);
					if(prevCount==null){
						keywordDocCountMap.put(key,1);
					}else{
						keywordDocCountMap.put(key,prevCount+1);
					}
				}
			}
			Iterator<Entry<String,Integer>>entries=keywordDocCountMap.entrySet().iterator();
			HashMap<String,String>result=new HashMap<String,String>();
			while(entries.hasNext()){
				String value="";
				Entry<String,Integer>entry=entries.next();
				String key=entry.getKey();
				int globalCount=entry.getValue();
				for(int i=0;i<keywordCountGroup.size();++i){
					double weight;
					String docId=docIdList.get(i);
					HashMap<String,Integer>keywordCountMap=keywordCountGroup.get(i);
					Integer count=keywordCountMap.get(key);
					if(count==null){
						weight=0;
					}else{
						weight=count*Math.log((double)documentCount/(double)globalCount);
					}
					value+=docId+" "+String.format("%.02f",weight)+" ";
				}
				result.put(key,value);
			}
			FileOutputStream fileOutputStream=new FileOutputStream("index.post");
			ObjectOutputStream objectOutputStream=new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(result);
			objectOutputStream.close();
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}
}