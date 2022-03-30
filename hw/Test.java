import java.util.HashMap;
import java.util.Iterator;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
public class Test{
	public static void main(String[]args){
		try{
		FileInputStream fileInputStream=new FileInputStream("index.post");
		ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
		Object object=objectInputStream.readObject();
		objectInputStream.close();
		HashMap hashMap=(HashMap)object;
		Iterator<String>iterator=hashMap.keySet().iterator();
		while(iterator.hasNext()){
			String key=iterator.next();
			String value=(String)hashMap.get(key);
			System.out.println(key+"=>"+value);
		}	
		}catch(Exception e){
			
		}
	}
}