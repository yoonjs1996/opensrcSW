import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
public class Main2{
	private static class DocValuePair implements Comparable<DocValuePair>{
		public String docId;
		public float value;
		public DocValuePair(String paramDocId,float paramValue){
			docId=paramDocId;
			value=paramValue;
		}
		@Override
		public int compareTo(DocValuePair pair){
			if(value<pair.value){
				return-1;
			}else if(value>pair.value){
				return+1;
			}else{
				return 0;
			}
		}
	}
	private static Element getElementByTag(Element parent,String tag){
		for(Element child=(Element)parent.getFirstChild();child!=null;child=(Element)child.getNextSibling()){
			if(child.getNodeName().equals(tag)){
				return child;
			}
		}
		return null;
	}
	private static float CalcSim(HashMap<String,Float>queryWeightMap,HashMap<String,Float>docWeightMap){
		float innerProduct=0;
		Iterator<String>entries=queryWeightMap.keySet().iterator();
		while(entries.hasNext()){
			String keyword=entries.next();
			float queryWeight=queryWeightMap.get(keyword);
			float docWeight=docWeightMap.get(keyword);
			innerProduct+=queryWeight*docWeight;
		}
		return innerProduct;
	}
	public static void main(String[]args){
		try{
			InputStream inputStream=new FileInputStream("./collection.xml");
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
			HashMap<String,String>docTitleMap=new HashMap<String,String>();
			for(elementIn=(Element)elementDocsIn.getFirstChild();elementIn!=null;elementIn=(Element)elementIn.getNextSibling()){
				String id=elementIn.getAttribute("id");
				Element titleElementIn=getElementByTag(elementIn,"title");
				String title=titleElementIn.getTextContent();
				docTitleMap.put(id,title);
			}
			Scanner scanner=new Scanner(System.in,"EUC-KR");
			KeywordExtractor keywordExtractor=new KeywordExtractor();
			String scanned=scanner.nextLine();
			KeywordList keywordList=keywordExtractor.extractKeyword(scanned,true);
			FileInputStream fileInputStream=new FileInputStream("index.post");
			ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
			HashMap<String,String>docWeightPremap=(HashMap<String,String>)objectInputStream.readObject();
			objectInputStream.close();
			Iterator<String>entries=docWeightPremap.keySet().iterator();
			HashMap<String,HashMap<String,Float>>docWeightTable=new HashMap<String,HashMap<String,Float>>();
			int docCount=0;
			while(entries.hasNext()){
				String keyword=entries.next();
				String stringifiedKvList=docWeightPremap.get(keyword);
				String[]splittedKvList=stringifiedKvList.split(" ");
				for(int i=0;i<splittedKvList.length/2;++i){
					String docId=splittedKvList[2*i+0];
					float weight=Float.parseFloat(splittedKvList[2*i+1]);
					HashMap<String,Float>docWeightMap=docWeightTable.get(docId);
					if(docWeightMap==null){
						docWeightMap=new HashMap<String,Float>();
						docWeightTable.put(docId,docWeightMap);
						docCount+=1;
					}
					docWeightMap.put(keyword,weight);
				}
			}
			HashMap<String,Float>queryWeightMap=new HashMap<String,Float>();
			for(int i=0;i<keywordList.size();++i){
				Keyword keyword=keywordList.get(i);
				String keywordString=keyword.getString();
				queryWeightMap.put(keywordString,(float)keyword.getCnt());
			}
			ArrayList<DocValuePair>docValueList=new ArrayList<DocValuePair>();
			Iterator<String>docWeightTableIterator=docWeightTable.keySet().iterator();
			while(docWeightTableIterator.hasNext()){
				String key=docWeightTableIterator.next();
				HashMap<String,Float>docWeightMap=docWeightTable.get(key);
				float innerProduct=CalcSim(queryWeightMap,docWeightMap);
				DocValuePair docValuePair=new DocValuePair(key,innerProduct);
				docValueList.add(docValuePair);
			}
			Collections.sort(docValueList,Collections.reverseOrder());
			int printCount=docCount>3?3:docCount;
			if(printCount==0){
				System.out.println("검색된 문서가 없습니다");
			}
			for(int i=0;i<printCount;++i){
				DocValuePair docValuePair=docValueList.get(i);
				String docId=docValuePair.docId;
				String title=docTitleMap.get(docId);
				System.out.println(title);
			}
		}catch(Exception e){
			StackTraceElement[]strackTraceElements=e.getStackTrace();
			System.out.println(e.toString());
		}
	}
}