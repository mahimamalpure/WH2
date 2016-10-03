package com.wsclient;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * This is a utility class having utility functions to handle JSON file format.
 * 
 * @author Amit.Malpure
 *
 * @created-date 10 July 2013
 * @JIRA SAF-1920
 */
public class JSONUtil 
{
	/**
	 * This function reads the JSON file.
	 * It then parse that file.
	 * Converts that file into JSON string format.
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readJSONFileAndConvertToStr(String fileName) 
	{
		JSONParser parser = new JSONParser();

		try 
		{
			Object obj = parser.parse(new FileReader(fileName));
			String jsnStr = obj.toString();
			return jsnStr;

		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();

		} catch (IOException e) 
		{
			e.printStackTrace();

		} catch (org.json.simple.parser.ParseException e) 
		{
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * This method retrieves all records from a JSON array which match on a single tag.
	 * e.g. [{"tag1":"value1", "tag2":"value2"},{"tag1":"value1", "tag2":"value3"},{"tag1":"value4","tag2":"value3"}]
	 * given a tag: "tag1" and a match "value1" this will return
	 * [{"tag1":"value1", "tag2":"value2"},{"tag1":"value1", "tag2":"value3"}]
	 * @param values the JSON array containing the input date
	 * @param tag the tag name to check for matching records
	 * @param match the value to match in the tag to identify a matching record
	 * @return a JSONArray of the matching records
	 */
	public static JSONArray matchArrayElement(JSONArray values, String tag, String match)
	{
		JSONArray matches = new JSONArray();
		try
		{
			Iterator<JSONObject> iter = values.iterator();
			while (iter.hasNext())
			{
				JSONObject curr = iter.next();
				if (!curr.containsKey(tag))
				{
					continue;
				}
				String result = curr.get(tag).toString();
				if (result.equals(match))
				{
					matches.add(curr);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();			
		}
		return matches;
	}
	/**
	 * This method is used to get the first match from a JSON array to the supplied tag.
	 * This will return the value associated with the tag name supplied. If there
	 * is more than one tag matching the supplied tag value then this will choose the first
	 * e.g. [{"tag1":"value1","tag2":"value2"}]. given tag1 this will return "value1"
	 * @param input the input array to parse
	 * @param tag the tag to match
	 * @return The string of the matching value
	 */
	public static String getFirstItem(JSONArray input, String tag)
	{
		Iterator<JSONObject> iter = input.iterator();
		while (iter.hasNext())
		{
			JSONObject curr = iter.next();
			if (curr.containsKey(tag))
			{
				String value = curr.get(tag).toString();
				return value;
			}
		}
		return "";
	}
	/**
	 * This is a helper function for converting a JSON format string into a JSON array.
	 * This can help with comparisons of that dataor with further processing
	 */
	public static JSONArray parseToArray(String input, String tagname, Instance instance)
	{
		try
		{
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(input);
			Object tagValue = null;
			if(obj instanceof JSONArray)
			{
				JSONArray jsonArray = (JSONArray) obj;
				if (tagname != "")
				{
					tagValue = traverseJSONArray(jsonArray, tagname, instance); 
				}
				else
				{
					tagValue = jsonArray;
				}
			}
			else if(obj instanceof JSONObject)
			{
				JSONObject jsonObject = (JSONObject) obj;

				if(jsonObject.get(tagname) != null)
				{
					tagValue = jsonObject.get(tagname);
				}
				else
				{
					tagValue = traverseJSONObject(jsonObject, tagname, instance);
				}
			}
			return (JSONArray)tagValue;
		}
		catch (org.json.simple.parser.ParseException e) 
		{
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * This function reads the JSON file or JSON String.
	 * It then parse that file/String.
	 * Then iterates into JSON string to find the 'tagName'
	 * 
	 * @param fileNameOrJSONStr
	 * @param tagName
	 * @param instance 
	 * @return tagValue
	 */
	public static String readJSONFile(String fileNameOrJSONStr, String tagName, Instance instance) 
	{
		Object tagValue = null;

		JSONParser parser = new JSONParser();

		try 
		{
			Object obj = null;

			if(fileNameOrJSONStr.toLowerCase().endsWith(".jsn") || fileNameOrJSONStr.toLowerCase().endsWith(".json"))
			{
				obj = parser.parse(new FileReader(fileNameOrJSONStr));
			}
			else
			{
				obj = parser.parse(fileNameOrJSONStr);
			}

			if(obj instanceof JSONArray)
			{
				JSONArray jsonArray = (JSONArray) obj;
				//Need to hold on to the result otherwise this will always fail
				tagValue = traverseJSONArray(jsonArray, tagName, instance);
			}
			else if(obj instanceof JSONObject)
			{
				JSONObject jsonObject = (JSONObject) obj;

				if(jsonObject.get(tagName) != null)
				{
					if(instance == null || (instance != null && instance.get() <= 1))
					{
						tagValue = jsonObject.get(tagName);
						return tagValue.toString();
					}
					else
					{
						//instance.dereament();
					}
				}
				
				tagValue = traverseJSONObject(jsonObject, tagName, instance);
			}
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();

		} catch (IOException e) 
		{
			e.printStackTrace();

		} catch (org.json.simple.parser.ParseException e) 
		{
			e.printStackTrace();
		}

		if(tagValue == null)
		{
			return null;
		}
		else
		{
			return tagValue.toString();
		}
	}

	/**
	 * This function iterates through a JSONArray & JSONObject
	 * and finds 'tagName'.
	 * 
	 * @param jsonObject
	 * @param tagName
	 * @param instance 
	 * @return tagValue
	 */
	
	private static Object traverseJSONArray(JSONArray jsonArray, String tagName, Instance instance)
	{
		Iterator iterator = jsonArray.iterator();

		while (iterator.hasNext()) 
		{
			Object obj = iterator.next();

			if(obj instanceof JSONArray)
			{
				JSONArray jsonArray1 = (JSONArray) obj;

				Object returnObj =  traverseJSONArray(jsonArray1, tagName, instance);
				
				if(returnObj != null)
				{
					System.out.println(returnObj + " " +  instance);

					if(instance == null || (instance != null && instance.get() <= 1))
					{
						return returnObj;
					}

					if(instance != null)
					{
						instance.inreament();
					}				}
			}
			else if(obj instanceof JSONObject)
			{
				JSONObject jsonObject = (JSONObject) obj;

				Object returnObj =  traverseJSONObject(jsonObject, tagName, instance);
				
				if(returnObj != null)
				{
					//System.out.println(returnObj + " " +  instance.get());

					if(instance == null || (instance != null && instance.get() <= 1))
					{
						return returnObj;
					}

					if(instance != null)
					{
						instance.inreament();
					}
				}
			}
		}

		return null;
	}

	/**
	 * This function iterates through a JSONObject
	 * and finds 'tagName'.
	 * 
	 * @param jsonObject
	 * @param tagName
	 * @param instance 
	 * @return tagValue
	 
	 */
	private static Object traverseJSONObject(JSONObject jsonObject, String tagName, Instance instance)
	{
		Set keyset = jsonObject.keySet();
		Iterator itr = keyset.iterator();

		while(itr.hasNext())
		{
			Object key = itr.next();
			Object value = jsonObject.get(key);

			if(key.toString().equalsIgnoreCase(tagName))
			{
				if(instance == null || (instance != null && instance.get() <= 1))
				{
					return value;
				}
				else
				{
					if(instance != null)
					{
						instance.dereament();
					}
				}
			}
			
			if(value instanceof JSONObject)
			{
				value = traverseJSONObject((JSONObject)value, tagName, instance);
				
				if(value != null)
				{
					return value;
				}
			}
			else if(value instanceof JSONArray)
			{
				value = traverseJSONArray((JSONArray)value, tagName, instance);
				
				if(value != null)
				{
					return value;
				}
			}
		}

		return null;
	}



	public static void main(String[] args) 
	{
		String reqXMLDoc = readJSONFileAndConvertToStr("C:\\D\\Data\\scripts\\Paresh\\op2.json");
		String tagValue_1 = JSONUtil.readJSONFile(reqXMLDoc, "status", new Instance(-1));
		String tagValue0 = JSONUtil.readJSONFile(reqXMLDoc, "status", new Instance(0));
		String tagValue1 = JSONUtil.readJSONFile(reqXMLDoc, "status", new Instance(1));
		String tagValue2 = JSONUtil.readJSONFile(reqXMLDoc, "status", new Instance(2));
		String tagValue3 = JSONUtil.readJSONFile(reqXMLDoc, "status", new Instance(3));
		System.out.println(tagValue_1 + " " + tagValue0 + " " + tagValue1 + " " + tagValue2 + " " + tagValue3);
		
		reqXMLDoc = readJSONFileAndConvertToStr("C:\\D\\Data\\scripts\\Paresh\\op.json");
		tagValue_1 = JSONUtil.readJSONFile(reqXMLDoc, "accountNumber", new Instance(-1));
		tagValue0 = JSONUtil.readJSONFile(reqXMLDoc, "accountNumber", new Instance(0));
		tagValue1 = JSONUtil.readJSONFile(reqXMLDoc, "accountNumber", new Instance(1));
		tagValue2 = JSONUtil.readJSONFile(reqXMLDoc, "accountNumber", new Instance(2));
		tagValue3 = JSONUtil.readJSONFile(reqXMLDoc, "status", new Instance(3));

		System.out.println(tagValue_1 + " " + tagValue0 + " " + tagValue1 + " " + tagValue2 + " " + tagValue3);

		
		//main_Yugs();
		//main_Celiwe();
	}

	private static void main_Yugs() 
	{
		readJSONFileAndConvertToStr("C:\\D\\Data\\scripts\\1920 json\\login.jsn");
		String str = "http://10.254.135.220:9080/enterprise/ui/j_security_check";
		try 
		{
			//System.setProperty("http" + "proxyHost", "we1proxy01");
			//System.setProperty("http" + "proxyPort", "8080");
			//System.setProperty("" + "proxyHost", "we1proxy01");
			//System.setProperty("" + "proxyPort", "8080");
			HttpURLConnection.setFollowRedirects(false);

			URL url = new URL(str);
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod("POST");
			httpCon.setDoInput(true);
			httpCon.setUseCaches(false);
			httpCon.setAllowUserInteraction(false);
			httpCon.setDoOutput(true);
			httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			//httpCon.setRequestProperty("Accept", "application/json; charset=utf-8");

			OutputStream outstream = httpCon.getOutputStream();
			OutputStreamWriter out = new OutputStreamWriter(outstream);
			//out.write(jsnStr);
			out.write("j_username=yugeshantest&j_password=Passw0rd");
			out.flush();
			out.close();

			int httpResponceCode = httpCon.getResponseCode();
			String httpResponceMessage = httpCon.getResponseMessage();

			String cookie = httpCon.getHeaderField("Set-Cookie");

			System.out.println(httpResponceCode + " " + httpResponceMessage);
			System.out.println(" cookie " + cookie);

			/*BufferedReader readSoapResponce = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
			String inputLine = "", tempResponce = "";

			while ((inputLine = readSoapResponce.readLine()) != null)
			{
				tempResponce = tempResponce + inputLine + "\r\n";
			}

			System.out.println(tempResponce);
			readSoapResponce.close();*/

			//String jsnStr1 = readJSONFileAndConvertToStr("C:\\D\\Data\\scripts\\1920 json\\cat.jsn");
			String str1 = "http://10.254.135.220:9080/enterprise/ui/rest/compositions/all?effectivedate=2013-07-24&limit=25&offset=0";
			URL url1 = new URL(str1);
			HttpURLConnection httpCon1 = (HttpURLConnection) url1.openConnection();
			httpCon1.setRequestMethod("GET");
			httpCon1.setDoInput(true);
			httpCon1.setUseCaches(false);
			httpCon1.setAllowUserInteraction(false);
			httpCon1.setDoOutput(true);
			httpCon1.setRequestProperty("Content-Type", "application/json");
			//httpCon1.setRequestProperty("Accept", "application/json;");
			httpCon1.setRequestProperty("Cookie", cookie);

			
			System.out.println("Method : " + httpCon1.getRequestMethod());
			System.out.println("URL : " + httpCon1.getURL());
			
			Map<String, List<String>> prop = httpCon1.getRequestProperties();
			
			Set<String> keyset = prop.keySet();
			Iterator<String> itr = keyset.iterator();
			
			while(itr.hasNext())
			{
				String p = itr.next();
				System.out.println(p + " : " + prop.get(p));
			}
			
			//OutputStream outstream1 = httpCon1.getOutputStream();
			//OutputStreamWriter out1 = new OutputStreamWriter(outstream1);
			//out1.write(jsnStr1);
			//out1.flush();
			//out1.close();

			int httpResponceCode1 = httpCon1.getResponseCode();
			String httpResponceMessage1 = httpCon1.getResponseMessage();

			String cookie1 = httpCon1.getHeaderField("Set-Cookie");

			System.out.println(httpResponceCode1 + " " + httpResponceMessage1);
			System.out.println(" cookie1 " + cookie1);

			BufferedReader readSoapResponce1 = new BufferedReader(new InputStreamReader(httpCon1.getInputStream()));
			String inputLine1 = "", tempResponce1 = "";

			while ((inputLine1 = readSoapResponce1.readLine()) != null)
			{
				tempResponce1 = tempResponce1 + inputLine1 + "\r\n";
			}

			System.out.println(tempResponce1);

			readSoapResponce1.close();

		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public static void main_Celiwe() 
	{
		String jsnStr = readJSONFileAndConvertToStr("C:\\D\\Data\\scripts\\1920 json\\from celiwe\\login.jsn");
		String str = "http://customer1.enterprise13.dev.sungardims.net:7014/InvestOne/rest/auth/login";
		try 
		{
			URL url = new URL(str);
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod("POST");
			httpCon.setDoInput(true);
			httpCon.setUseCaches(false);
			httpCon.setAllowUserInteraction(false);
			httpCon.setDoOutput(true);
			httpCon.setRequestProperty("Content-Type", "application/json");
			httpCon.setRequestProperty("Accept", "application/json; charset=utf-8");
			httpCon.setRequestProperty("Authorization", "Digest username=utotester1@customer1, realm=InvestOne, nonce=MTM4MjQ0Nzg5MTI1Mjo4ZTYwOTkyNDYxZGQ5ZDk0ZTRjYzUxYjQ2ZGE1NWJkNg==, qop=auth");
			//Digest username="autotester1@customer1", realm="InvestOne", nonce="MTM4MjQzMzM5OTM4NTpiODZmMDk5ZjMwNjkwODM4Nzg2YTAyZjI3ZDcyMjZlMQ==", uri="/InvestOne/rest/auth/login", qop=auth, nc=, cnonce="", response="8dd490d020751d9150c7edc254cb42f3", opaque=""
			
			
			
			//String dig=calculatePasswordDigest(nonceDigest, Calendar.getInstance().toString(), pswDigest, userNameDigest, realmDigest, qopDigest);
			//httpCon.setRequestProperty("Authorization", dig);

			//String authorization = new sun.misc.BASE64Encoder().encode(("autotester1" + ":" + "tester0").getBytes());
			//httpCon.setRequestProperty("Authorization", "digest " + authorization);
			
		//	String result = getStringFromInputStream(httpCon.getInputStream());
			//System.out.println(result);
			
			OutputStream outstream = httpCon.getOutputStream();
			OutputStreamWriter out = new OutputStreamWriter(outstream);
			out.write(jsnStr);

			out.flush();
			out.close();

			int httpResponceCode = httpCon.getResponseCode();
			String httpResponceMessage = httpCon.getResponseMessage();

			httpCon.getHeaderField("Set-Cookie");

			System.out.println(httpResponceCode + " " + httpResponceMessage);

			BufferedReader readSoapResponce = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
			String inputLine = "", tempResponce = "";

			while ((inputLine = readSoapResponce.readLine()) != null)
			{
				tempResponce = tempResponce + inputLine + "\r\n";
			}

			System.out.println(tempResponce);
			readSoapResponce.close();

			/*String jsnStr1 = readJSONFileAndConvertToStr("C:\\D\\Data\\scripts\\1920 json\\del.jsn");
			//String str1 = "http://customer1.enterprise13.dev.sungardims.net:7014/InvestOne/rest/ems/categories";
			String str1 = "http://customer1.enterprise13.dev.sungardims.net:7014/InvestOne/rest/user/favorites/3336";
			URL url1 = new URL(str1);
			HttpURLConnection httpCon1 = (HttpURLConnection) url1.openConnection();
			httpCon1.setRequestMethod("POST");
			httpCon1.setDoInput(true);
			httpCon1.setUseCaches(false);
			httpCon1.setAllowUserInteraction(false);
			httpCon1.setDoOutput(true);
			httpCon1.setRequestProperty("Content-Type", "application/json");
			httpCon1.setRequestProperty("Accept", "application/json;");
			httpCon1.setRequestProperty("Cookie", cookie);

			OutputStream outstream1 = httpCon1.getOutputStream();
			OutputStreamWriter out1 = new OutputStreamWriter(outstream1);
			out1.write(jsnStr1);
			out1.flush();
			out1.close();

			int httpResponceCode1 = httpCon1.getResponseCode();
			String httpResponceMessage1 = httpCon1.getResponseMessage();

			String cookie1 = httpCon1.getHeaderField("Set-Cookie");

			System.out.println(httpResponceCode1 + " " + httpResponceMessage1 + " cookie " + cookie);

			BufferedReader readSoapResponce1 = new BufferedReader(new InputStreamReader(httpCon1.getInputStream()));
			String inputLine1 = "", tempResponce1 = "";

			while ((inputLine1 = readSoapResponce1.readLine()) != null)
			{
				tempResponce1 = tempResponce1 + inputLine1 + "\r\n";
			}

			System.out.println(tempResponce1);

			readSoapResponce1.close();*/

		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}