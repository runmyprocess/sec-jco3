 package com.runmyprocess.sec;

import java.math.BigDecimal;
import java.util.Set;

import org.runmyprocess.json.JSON;
import org.runmyprocess.json.JSONArray;
import org.runmyprocess.json.JSONObject;

import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

/**
 * 
 * @author Sanket Joshi <sanket.joshi@flowian.com>
 *
 */

public class JCO3DataHandler {


	public JCO3DataHandler() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param parameters
	 * @param parameterlist
	 * @throws Exception
	 */
	
	public void setParameters(JSONObject parameters,JCoParameterList parameterlist) throws Exception
	{
		//JCO3DataHandler datahandler=new JCO3DataHandler();
		Set<?> keys = parameters.keySet();            
		JCoMetaData metaData = parameterlist.getMetaData();
		for (Object key : keys) 
		{
			if(JSON.mayBeJsonArray(parameters.get(key.toString()).toString()))
			{
				JSONArray child= parameters.getJSONArray(key.toString());
				if(child != null)
				{	JCoTable table=null;
				if(metaData.getTypeAsString(key.toString())=="TABLE")
				{	table=defineABAPTable(parameterlist.getTable(key.toString()),child);
				//System.out.println(table);
				parameterlist.setValue(key.toString(), table);
				}
				if(metaData.getTypeAsString(key.toString())=="STRUCTURE")
				{//LOG.log("Bad input pattern.\nYou'r trying to create a table which is structure type in ABAP.", Level.INFO);
					return;
				}
				}
			}
			else
				if(JSON.mayBeJSON(parameters.get(key.toString()).toString()))
				{	JSONObject child= parameters.getJSONObject(key.toString());
				if(child != null)
				{	JCoStructure structure=null;
				if(metaData.getTypeAsString(key.toString())=="STRUCTURE")
				{	structure=defineABAPStructure(parameterlist.getStructure(key.toString()),child);
				//System.out.println(structure);
				parameterlist.setValue(key.toString(), structure);
				}
				if(metaData.getTypeAsString(key.toString())=="TABLE")
				{//LOG.log("Bad input pattern.\nYou'r trying to create a structure which is table type in ABAP.", Level.INFO);
				return;}
				}
				}else
				{
					setABAPFliedValue(parameterlist, key.toString(), parameters.getString(key.toString()), metaData.getTypeAsString(key.toString())); 
				}            		 	
		}
	}

	/**
	 * @param table
	 * @param child
	 * @return
	 * @throws Exception
	 */
	public JCoTable defineABAPTable(JCoTable table, JSONArray child) throws Exception {
		// TODO Auto-generated method stub
		
		for(int i=0;i<child.size();i++)
		{
			table.appendRow();
			Set<?> keys = child.getJSONObject(i).keySet();
			for (Object key : keys) 
			{          	          
				setABAPTableValue(table,key.toString(), child.getJSONObject(i).getString(key.toString()), table.getMetaData().getTypeAsString(key.toString()));          		          
			}
		}
		return table;  

	}


	/**
	 *  @param structure
	 * @param child
	 * @return
	 * @throws Exception
	 */
	public JCoStructure defineABAPStructure(JCoStructure structure, JSONObject child) throws Exception {
		// TODO Auto-generated method stub
		Set<?> keys = child.keySet();
		for (Object key : keys) 
		{          	          
			setABAPStructureValue(structure,key.toString(), child.getString(key.toString()), structure.getMetaData().getTypeAsString(key.toString()));
		}  
		return structure;
	}



	/**
	 * @param parameterlist
	 * @param parameterKey
	 * @param parameterValue
	 * @param abap_jco_type
	 * @throws Exception
	 */
	public void setABAPFliedValue(JCoParameterList parameterlist,String parameterKey,String parameterValue,String abap_jco_type) throws Exception
	{

		/**
		 * Possible ABAP types
		 * TYPE_CHAR, TYPE_NUM, TYPE_BYTE, TYPE_BCD, TYPE_INT, TYPE_INT1, TYPE_INT2, TYPE_FLOAT, 
		 * TYPE_DATE, TYPE_TIME, TYPE_DECF16, TYPE_DECF34, TYPE_STRING, TYPE_XSTRING
		 */
		switch(abap_jco_type)
		{


		case "CHAR"://System.out.println("Character");
			//String
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, parameterValue);
			}
			break;


		case "NUM"://System.out.println("Numerical Character");
			//String
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, parameterValue);
			}
			break;


		case "BYTE"://System.out.println("Binary Data");
			//byte()
			char[] chars=parameterValue.toCharArray();
			byte[] bytes = new byte[chars.length]; 
			for(int i=0;i<chars.length;i++)
			{
				bytes[i]=(byte)chars[i];
			}

			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, bytes);
			}
			break;


		case "BCD"://System.out.println("Binary Coded Decimal");
			//BigDecimal
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, BigDecimal.valueOf(Double.parseDouble(parameterValue)));
			}

			break;


		case "INT"://System.out.println("4-byte Integer");
			//int
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, Integer.parseInt(parameterValue));
			}
			break;


		case "INT1"://System.out.println("1-byte Integer");
			//int
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, Integer.parseInt(parameterValue));
			}
			break;


		case "INT2"://System.out.println("2-byte Integer");
			//int
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, Integer.parseInt(parameterValue));
			}
			break;


		case "FLOAT"://System.out.println("Float");
			//double
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, Double.parseDouble(parameterValue));
			}
			break;


		case "DATE"://System.out.println("Date");
			//Date("YYYYMMDD")
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, parameterValue);
			}
			break;


		case "TIME"://System.out.println("Time");
			//Date("HHMMSS")
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, parameterValue);
			}
			break;


		case "DECF16"://System.out.println("Decimal floating point 8 bytes (IEEE 754r)");
			//BigDecimal
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, BigDecimal.valueOf(Double.parseDouble(parameterValue)));
			}
			break;


		case "DECF34"://System.out.println("Decimal floating point 16 bytes (IEEE 754r)");
			//BigDecimal
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, BigDecimal.valueOf(Double.parseDouble(parameterValue)));
			}
			break;


		case "STRING"://System.out.println("String (variable length)");
			//string
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, parameterValue);
			}
			break;


		case "XSTRING"://System.out.println("Raw String (variable length)");
			//byte()
			char[] chars1=parameterValue.toCharArray();
			byte[] bytes1 = new byte[chars1.length]; 
			for(int i=0;i<chars1.length;i++)
			{
				bytes1[i]=(byte)chars1[i];
			}
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, bytes1);
			}
			break;		

		default: System.out.println("Error while setting value for Unknown ABAP type "+abap_jco_type);
		break;		
		}
	}




	/**
	 * @param table
	 * @param parameterKey
	 * @param parameterValue
	 * @param abap_jco_type
	 * @throws Exception
	 */


	public void setABAPTableValue(JCoTable table,String parameterKey,String parameterValue,String abap_jco_type) throws Exception
	{

		/**
		 * Possible ABAP types
		 * TYPE_CHAR, TYPE_NUM, TYPE_BYTE, TYPE_BCD, TYPE_INT, TYPE_INT1, TYPE_INT2, TYPE_FLOAT, 
		 * TYPE_DATE, TYPE_TIME, TYPE_DECF16, TYPE_DECF34, TYPE_STRING, TYPE_XSTRING
		 */
		switch(abap_jco_type)
		{

		case "CHAR"://System.out.println("Character");
			//String
			if(table != null)
			{
				table.setValue(parameterKey, parameterValue);
			}
			break;


		case "NUM"://System.out.println("Numerical Character");
			//String
			if(table != null)
			{
				table.setValue(parameterKey, parameterValue);
			}
			break;


		case "BYTE"://System.out.println("Binary Data");
			//byte()
			char[] chars=parameterValue.toCharArray();
			byte[] bytes = new byte[chars.length]; 
			for(int i=0;i<chars.length;i++)
			{
				bytes[i]=(byte)chars[i];
			}

			if(table != null)
			{
				table.setValue(parameterKey, bytes);
			}
			break;


		case "BCD"://System.out.println("Binary Coded Decimal");
			//BigDecimal
			if(table != null)
			{
				table.setValue(parameterKey, BigDecimal.valueOf(Double.parseDouble(parameterValue)));
			}		
			break;


		case "INT"://System.out.println("4-byte Integer");
			//int
			if(table != null)
			{
				table.setValue(parameterKey, Integer.parseInt(parameterValue));
			}
			break;


		case "INT1"://System.out.println("1-byte Integer");
			//int
			if(table != null)
			{
				table.setValue(parameterKey, Integer.parseInt(parameterValue));
			}
			break;


		case "INT2"://System.out.println("2-byte Integer");
			//int
			if(table != null)
			{
				table.setValue(parameterKey,Integer.parseInt(parameterValue));
			}
			break;


		case "FLOAT"://System.out.println("Float");
			//double
			if(table != null)
			{
				table.setValue(parameterKey, Double.parseDouble(parameterValue));
			}
			break;


		case "DATE"://System.out.println("Date");
			//Date("YYYYMMDD")
			if(table != null)
			{
				table.setValue(parameterKey, parameterValue);
			}
			break;


		case "TIME"://System.out.println("Time");
			//Date("HHMMSS")
			if(table != null)
			{
				table.setValue(parameterKey, parameterValue);
			}
			break;


		case "DECF16"://System.out.println("Decimal floating point 8 bytes (IEEE 754r)");
			//BigDecimal
			if(table != null)
			{
				table.setValue(parameterKey, BigDecimal.valueOf(Double.parseDouble(parameterValue)));
			}
			break;


		case "DECF34"://System.out.println("Decimal floating point 16 bytes (IEEE 754r)");
			//BigDecimal
			if(table != null)
			{
				table.setValue(parameterKey, BigDecimal.valueOf(Double.parseDouble(parameterValue)));
			}
			break;


		case "STRING"://System.out.println("String (variable length)");
			//string
			if(table != null)
			{
				table.setValue(parameterKey, parameterValue);
			}
			break;


		case "XSTRING"://System.out.println("Raw String (variable length)");
			//byte()
			char[] chars1=parameterValue.toCharArray();
			byte[] bytes1 = new byte[chars1.length]; 
			for(int i=0;i<chars1.length;i++)
			{
				bytes1[i]=(byte)chars1[i];
			}
			if(table != null)
			{
				table.setValue(parameterKey, bytes1);
			}
			break;		

		default: System.out.println("Error while setting value for Unknown ABAP type "+abap_jco_type);
		break;		
		}
	}

	/**
	 * @param structure
	 * @param parameterKey
	 * @param parameterValue
	 * @param abap_jco_type
	 * @throws Exception
	 */

	public void setABAPStructureValue(JCoStructure structure,String parameterKey,String parameterValue,String abap_jco_type) throws Exception
	{

		/**
		 * Possible ABAP types
		 * TYPE_CHAR, TYPE_NUM, TYPE_BYTE, TYPE_BCD, TYPE_INT, TYPE_INT1, TYPE_INT2, TYPE_FLOAT, 
		 * TYPE_DATE, TYPE_TIME, TYPE_DECF16, TYPE_DECF34, TYPE_STRING, TYPE_XSTRING
		 */

		switch(abap_jco_type)
		{

		case "CHAR"://System.out.println("Character");
			//String
			if(structure != null)
			{
				structure.setValue(parameterKey, parameterValue);
			}
			break;


		case "NUM"://System.out.println("Numerical Character");
			//String			
			if(structure != null)
			{
				structure.setValue(parameterKey, parameterValue);
			}			
			break;


		case "BYTE":///System.out.println("Binary Data");
			//byte()
			char[] chars=parameterValue.toCharArray();
			byte[] bytes = new byte[chars.length]; 
			for(int i=0;i<chars.length;i++)
			{
				bytes[i]=(byte)chars[i];
			}
			if(structure != null)
			{
				structure.setValue(parameterKey, bytes);
			}
			break;


		case "BCD"://System.out.println("Binary Coded Decimal");
			//BigDecimal
			if(structure != null)
			{
				structure.setValue(parameterKey, BigDecimal.valueOf(Double.parseDouble(parameterValue)));
			}
			break;


		case "INT"://System.out.println("4-byte Integer");
			//int
			if(structure != null)
			{
				structure.setValue(parameterKey, Integer.parseInt(parameterValue));
			}
			break;


		case "INT1"://System.out.println("1-byte Integer");
			//int
			if(structure != null)
			{
				structure.setValue(parameterKey, Integer.parseInt(parameterValue));
			}
			break;


		case "INT2"://System.out.println("2-byte Integer");
			//int
			if(structure != null)
			{
				structure.setValue(parameterKey,Integer.parseInt(parameterValue));
			}
			break;


		case "FLOAT"://System.out.println("Float");
			//double			
			if(structure != null)
			{
				structure.setValue(parameterKey, Double.parseDouble(parameterValue));
			}
			break;


		case "DATE"://System.out.println("Date");
			//Date("YYYYMMDD")
			if(structure != null)
			{
				structure.setValue(parameterKey, parameterValue);
			}
			break;


		case "TIME"://System.out.println("Time");
			//Date("HHMMSS")
			if(structure != null)
			{
				structure.setValue(parameterKey, parameterValue);
			}
			break;


		case "DECF16"://System.out.println("Decimal floating point 8 bytes (IEEE 754r)");
			//BigDecimal
			if(structure != null)
			{
				structure.setValue(parameterKey, BigDecimal.valueOf(Double.parseDouble(parameterValue)));
			}
			break;


		case "DECF34"://System.out.println("Decimal floating point 16 bytes (IEEE 754r)");
			//BigDecimal
			if(structure != null)
			{
				structure.setValue(parameterKey, BigDecimal.valueOf(Double.parseDouble(parameterValue)));
			}
			break;


		case "STRING"://System.out.println("String (variable length)");
			//string
			if(structure != null)
			{
				structure.setValue(parameterKey, parameterValue);
			}
			break;


		case "XSTRING"://System.out.println("Raw String (variable length)");
			//byte()
			char[] chars1=parameterValue.toCharArray();
			byte[] bytes1 = new byte[chars1.length]; 
			for(int i=0;i<chars1.length;i++)
			{
				bytes1[i]=(byte)chars1[i];
			}
			if(structure != null)
			{
				structure.setValue(parameterKey, bytes1);
			}
			break;		

		default: System.out.println("Error while setting value for Unknown ABAP type "+abap_jco_type);
		break;		
		}
	}








}

