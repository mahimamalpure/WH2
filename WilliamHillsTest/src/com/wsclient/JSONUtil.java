package com.wsclient;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JSONUtil 
{
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
}