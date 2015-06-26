package com.runmyprocess.sec;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

import org.runmyprocess.json.JSON;
import org.runmyprocess.json.JSONArray;
import org.runmyprocess.json.JSONObject;

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
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


	/****************************************************************************get meta data*******************************************************************/
	/**
	 * 
	 * @param function
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public LinkedHashMap getParamertsMetadata(JCoFunction function)
	{
		LinkedHashMap paramertsList=new LinkedHashMap();
		if(function.getImportParameterList() != null){
			JCoFieldIterator importIterator=function.getImportParameterList().getFieldIterator();
			LinkedHashMap importParameters=new LinkedHashMap();
			paramertsList.put("importParameters",getFieldMetadata(importIterator,importParameters));
		}
		if(function.getExportParameterList() != null){
			JCoFieldIterator exportIterator=function.getExportParameterList().getFieldIterator();
			LinkedHashMap exportParameters=new LinkedHashMap();
			paramertsList.put("exportParameters",getFieldMetadata(exportIterator,exportParameters));
		}
		if(function.getTableParameterList() != null){
			JCoFieldIterator tableIterator=function.getTableParameterList().getFieldIterator();
			LinkedHashMap tableParameters=new LinkedHashMap();	
			paramertsList.put("tableParameters",getFieldMetadata(tableIterator,tableParameters));
		}
		if(function.getChangingParameterList() != null){
			JCoFieldIterator changingIterator=function.getChangingParameterList().getFieldIterator();
			LinkedHashMap changingParameters=new LinkedHashMap();	
			paramertsList.put("changingParameters",getFieldMetadata(changingIterator,changingParameters));
		}
		return paramertsList;
	}

	/**
	 * 
	 * @param iterator
	 * @param parameters
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LinkedHashMap getFieldMetadata(JCoFieldIterator iterator,LinkedHashMap parameters)
	{
		// TODO Auto-generated method stub
		while(iterator.hasNextField())
		{
			JCoField jf=iterator.nextField();
			if(jf.getTypeAsString()=="TABLE")
			{
				parameters.put(jf.getName(),getTableParameterMetadata(jf));
			}
			else
				if(jf.getTypeAsString()=="STRUCTURE")
				{
					parameters.put(jf.getName(),getStructureParameterMetadata(jf));
				}
				else{
					parameters.put(jf.getName(),jf.getDescription());
				}
		}
		return parameters;
	}

	/**
	 * 
	 * @param table
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LinkedHashMap getTableParameterMetadata(JCoField table)
	{
		// TODO Auto-generated method stub
		JCoTable t = table.getTable();
		JCoFieldIterator iter = t.getFieldIterator();
		LinkedHashMap m = new LinkedHashMap();
		while(iter.hasNextField())
		{
			JCoField f = iter.nextField();
			m.put(f.getName(), f.getDescription());
		}

		return m;
	}

	/**
	 * 
	 * @param structure
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LinkedHashMap getStructureParameterMetadata(JCoField structure)
	{
		// TODO Auto-generated method stub
		JCoFieldIterator iter = structure.getStructure().getFieldIterator();
		LinkedHashMap m = new LinkedHashMap();
		while(iter.hasNextField())
		{
			JCoField f = iter.nextField();
			m.put(f.getName(),f.getDescription());
		}
		return m;
	}

	/*******************************************************************Getters***************************************************************************/



	/**
	 * 
	 * @param function
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public LinkedHashMap getParamerts(JCoFunction function) throws Exception
	{
		LinkedHashMap paramertsList=new LinkedHashMap();
		if(function.getImportParameterList() != null){
			JCoFieldIterator importIterator=function.getImportParameterList().getFieldIterator();
			LinkedHashMap importParameters=new LinkedHashMap();
			paramertsList.put("importParameters",getField(importIterator,importParameters));
		}
		if(function.getExportParameterList() != null){
			JCoFieldIterator exportIterator=function.getExportParameterList().getFieldIterator();
			LinkedHashMap exportParameters=new LinkedHashMap();
			paramertsList.put("exportParameters",getField(exportIterator,exportParameters));
		}
		if(function.getTableParameterList() != null){
			JCoFieldIterator tableIterator=function.getTableParameterList().getFieldIterator();
			LinkedHashMap tableParameters=new LinkedHashMap();	
			paramertsList.put("tableParameters",getField(tableIterator,tableParameters));
		}
		if(function.getChangingParameterList() != null){
			JCoFieldIterator changingIterator=function.getChangingParameterList().getFieldIterator();
			LinkedHashMap changingParameters=new LinkedHashMap();	
			paramertsList.put("changingParameters",getField(changingIterator,changingParameters));
		}
		return paramertsList;
	}

	/**
	 * 
	 * @param iterator
	 * @param parameters
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LinkedHashMap getField(JCoFieldIterator iterator,LinkedHashMap parameters) throws Exception
	{
		// TODO Auto-generated method stub
		while(iterator.hasNextField())
		{
			JCoField jf=iterator.nextField();
			if(jf.getTypeAsString()=="TABLE")
			{
				parameters.put(jf.getName(),getTableParameter(jf));
			}
			else
				if(jf.getTypeAsString()=="STRUCTURE")
				{
					parameters.put(jf.getName(),getStructureParameter(jf));
				}
				else{
					parameters.put(jf.getName(),getABAPFliedValueAsString(jf,jf.getTypeAsString()));
				}
		}
		return parameters;
	}

	/**
	 * 
	 * @param table
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LinkedList getTableParameter(JCoField table) throws Exception
	{
		// TODO Auto-generated method stub
		LinkedList l = new LinkedList();
		JCoTable t = table.getTable();
		for (int i = 0; i < t.getNumRows(); i++)
		{
			t.setRow(i);
			JCoFieldIterator iter = t.getFieldIterator();
			LinkedHashMap m = new LinkedHashMap();
			while(iter.hasNextField())
			{
				JCoField f = iter.nextField();
				m.put(f.getName(),getABAPTableValueAsString(t,f));
			}
			l.add(m);
		}
		return l;
	}

	/**
	 * 
	 * @param structure
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LinkedHashMap getStructureParameter(JCoField structure) throws Exception
	{	
		// TODO Auto-generated method stub
		JCoStructure s=structure.getStructure();
		JCoFieldIterator iter = s.getFieldIterator();
		LinkedHashMap m = new LinkedHashMap();
		while(iter.hasNextField())
		{
			JCoField f = iter.nextField();
			m.put(f.getName(), getABAPStructureValueAsString(s,f));
		}
		return m;
	}

	/*****************************************************************************************************************************/

	private String getABAPFliedValueAsString(JCoField field,String abap_jco_type) throws Exception
	{

		String value=null;
		/**
		 * Possible ABAP types
		 * TYPE_CHAR, TYPE_NUM, TYPE_BYTE, TYPE_BCD, TYPE_INT, TYPE_INT1, TYPE_INT2, TYPE_FLOAT, 
		 * TYPE_DATE, TYPE_TIME, TYPE_DECF16, TYPE_DECF34, TYPE_STRING, TYPE_XSTRING
		 */
		switch(abap_jco_type)
		{
		case "CHAR":value=field.getString();break;//String

		case "NUM":value=field.getString();break;//String

		case "BYTE":value=field.getByteArray().toString();
				;break;//byte()

		case "BCD":value=field.getBigDecimal().toString();
				;break;//BigDecimal

		case "INT":value=String.valueOf(field.getInt());
				;break;//int

		case "INT1":value=String.valueOf(field.getInt());
				;break;//int

		case "INT2":value=String.valueOf(field.getInt()) ;
				;break;//int

		case "FLOAT":value=String.valueOf(field.getFloat());
				;break;//double

		case "DATE":value=field.getString();
				;break;//Date("YYYYMMDD")

		case "TIME":value=field.getString();
				;break;//Date("HHMMSS")

		case "DECF16":value=field.getBigDecimal().toString();
		;break;//BigDecimal

		case "DECF34":value=field.getBigDecimal().toString();
				;break;//BigDecimal

		case "STRING":value=field.getString();
				;break;//string

		case "XSTRING":value=field.getByteArray().toString() ;
				;break;//byte()

		default: System.out.println("Error while setting value for Unknown ABAP type "+abap_jco_type);break;		
		}
		return value;
	}

	private String getABAPTableValueAsString(JCoTable table,JCoField field)  throws Exception
	{

		String abap_jco_type=field.getTypeAsString();
		String value=null;
		/**
		 * Possible ABAP types
		 * TYPE_CHAR, TYPE_NUM, TYPE_BYTE, TYPE_BCD, TYPE_INT, TYPE_INT1, TYPE_INT2, TYPE_FLOAT, 
		 * TYPE_DATE, TYPE_TIME, TYPE_DECF16, TYPE_DECF34, TYPE_STRING, TYPE_XSTRING
		 */
		switch(abap_jco_type)
		{
		case "CHAR":value=table.getString(field.getName()); 
				;break;//String

		case "NUM":value=table.getString(field.getName());
				;break;//String

		case "BYTE":value=table.getByteArray(field.getName()).toString();
				;break;//byte()

		case "BCD":value=table.getBigDecimal(field.getName()).toString() ;
				;break;//BigDecimal

		case "INT":value=String.valueOf(table.getInt(field.getName())) ;
				;break;//int

		case "INT1":value=String.valueOf(table.getInt(field.getName())) ;
				;break;//int

		case "INT2":value=String.valueOf(table.getInt(field.getName()));
				;break;//int

		case "FLOAT":value=String.valueOf(table.getFloat(field.getName())) ;
			;break;//double

		case "DATE":value=table.getString(field.getName());
				;break;//Date("YYYYMMDD")

		case "TIME":value=table.getString(field.getName());
				;break;//Date("HHMMSS")

		case "DECF16":value=table.getBigDecimal(field.getName()).toString() ;
				;break;//BigDecimal

		case "DECF34":value=table.getBigDecimal(field.getName()).toString() ;
				;break;//BigDecimal

		case "STRING":value=table.getString(field.getName());
				;break;//string

		case "XSTRING":value=table.getByteArray(field.getName()).toString();
				;break;//byte()

		default: System.out.println("Error while setting value for Unknown ABAP type "+abap_jco_type);break;		
		}
		return value;
	}

	private String getABAPStructureValueAsString(JCoStructure structure,JCoField field)  throws Exception
	{

		String abap_jco_type=field.getTypeAsString();
		String value=null;
		/**
		 * Possible ABAP types
		 * TYPE_CHAR, TYPE_NUM, TYPE_BYTE, TYPE_BCD, TYPE_INT, TYPE_INT1, TYPE_INT2, TYPE_FLOAT, 
		 * TYPE_DATE, TYPE_TIME, TYPE_DECF16, TYPE_DECF34, TYPE_STRING, TYPE_XSTRING
		 */
		switch(abap_jco_type)
		{
		case "CHAR":value=structure.getString(field.getName());break;//String

		case "NUM":value=structure.getString(field.getName());break;//String

		case "BYTE":value=structure.getByteArray(field.getName()).toString() ;break;//byte()

		case "BCD":value=structure.getBigDecimal(field.getName()).toString();break;//BigDecimal

		case "INT":value=String.valueOf(structure.getInt(field.getName())) ;break;//int

		case "INT1":value=String.valueOf(structure.getInt(field.getName()));break;//int

		case "INT2":value=String.valueOf(structure.getInt(field.getName()));
				;break;//int

		case "FLOAT":value=String.valueOf(structure.getFloat(field.getName())) ;
				;break;//double

		case "DATE":value=structure.getString(field.getName()) ;
				;break;//Date("YYYYMMDD")

		case "TIME":value=structure.getString(field.getName()) ;
				;break;//Date("HHMMSS")

		case "DECF16":value=structure.getBigDecimal(field.getName()).toString() ;
				;break;//BigDecimal

		case "DECF34":value=structure.getBigDecimal(field.getName()).toString();
				;break;//BigDecimal

		case "STRING":value=structure.getString(field.getName());
				;break;//string

		case "XSTRING":value=structure.getByteArray(field.getName()).toString(); 
				;break;//byte()

		default: System.out.println("Error while setting value for Unknown ABAP type "+abap_jco_type);break;		
		}
		return value;
	}



	
	/************************************************************************Setters***********************************************************************/
	/**
	 * 
	 * @param jsonObject
	 * @param function
	 * @throws Exception 
	 */
	public void setParamerts(JSONObject jsonObject,JCoFunction function) throws Exception 
	{

		//LOG.log("Setting import parameters", Level.INFO);
		JSONObject inputParameters = jsonObject.getJSONObject("inputParameters"); 
		if(inputParameters != null)
		{
			setParametersList(inputParameters, function.getImportParameterList());
		}
		//LOG.log("Deactivating unwanted export parameters"", Level.INFO);
		JSONObject exportParameters = jsonObject.getJSONObject("exportParameters"); 
		if(exportParameters != null)
		{
			setParametersList(exportParameters, function.getExportParameterList());
			Set<?> keys = exportParameters.keySet();            
			JCoParameterList parameterlist=function.getExportParameterList();
			Iterator<JCoField> fieldIterator = parameterlist.iterator();
			while (fieldIterator.hasNext()){
				String fieldName=fieldIterator.next().getName();
				if(keys.contains(fieldName))
					parameterlist.setActive(fieldName, true);
				else
					parameterlist.setActive(fieldName, false);
			}
		}
		//LOG.log("Setting table parameters", Level.INFO);
		JSONObject tableParameters = jsonObject.getJSONObject("tableParameters");		
		if(tableParameters != null)
		{
			setParametersList(tableParameters, function.getTableParameterList());
		}        
		//LOG.log("Setting changing parameters", Level.INFO);
		JSONObject changingParameter = jsonObject.getJSONObject("changingParameter"); 
		if(changingParameter != null)
		{
			setParametersList(changingParameter, function.getChangingParameterList());
		}
	}


	/**
	 * 
	 * @param parameters
	 * @param parameterlist
	 * @throws Exception
	 */
	private void setParametersList(JSONObject parameters,JCoParameterList parameterlist) throws Exception
	{
		// TODO Auto-generated method stub
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
				{	table=setABAPTable(parameterlist.getTable(key.toString()),child);
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
				{	structure=setABAPStructure(parameterlist.getStructure(key.toString()),child);
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
	private JCoTable setABAPTable(JCoTable table, JSONArray child) throws Exception {
		// TODO Auto-generated method stub
		for(int i=0;i<child.size();i++)
		{
			table.appendRow();
			Set<?> keys = child.getJSONObject(i).keySet();
			for (Object key : keys) 
			{          	    
				try{
					setABAPTableValue(table,key.toString(), child.getJSONObject(i).getString(key.toString()), table.getMetaData().getTypeAsString(key.toString()));
				}
				catch(Exception ez){
					ez.printStackTrace();
				}
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
	private JCoStructure setABAPStructure(JCoStructure structure, JSONObject child) throws Exception {
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
	private void setABAPFliedValue(JCoParameterList parameterlist,String parameterKey,String parameterValue,String abap_jco_type) throws Exception
	{

		/**
		 * Possible ABAP types
		 * TYPE_CHAR, TYPE_NUM, TYPE_BYTE, TYPE_BCD, TYPE_INT, TYPE_INT1, TYPE_INT2, TYPE_FLOAT, 
		 * TYPE_DATE, TYPE_TIME, TYPE_DECF16, TYPE_DECF34, TYPE_STRING, TYPE_XSTRING
		 */
		switch(abap_jco_type)
		{


		case "CHAR"://String
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, parameterValue);
			}
			break;


		case "NUM"://String
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, parameterValue);
			}
			break;


		case "BYTE"://byte()
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


		case "BCD"://BigDecimal
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, BigDecimal.valueOf(Double.parseDouble(parameterValue)));
			}

			break;


		case "INT"://int
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, Integer.parseInt(parameterValue));
			}
			break;


		case "INT1"://int
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, Integer.parseInt(parameterValue));
			}
			break;


		case "INT2"://int
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, Integer.parseInt(parameterValue));
			}
			break;


		case "FLOAT"://double
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, Double.parseDouble(parameterValue));
			}
			break;


		case "DATE"://Date("YYYYMMDD")
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, parameterValue);
			}
			break;


		case "TIME"://Date("HHMMSS")
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, parameterValue);
			}
			break;


		case "DECF16"://BigDecimal
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, BigDecimal.valueOf(Double.parseDouble(parameterValue)));
			}
			break;


		case "DECF34"://BigDecimal
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, BigDecimal.valueOf(Double.parseDouble(parameterValue)));
			}
			break;


		case "STRING"://string
			if(parameterlist != null)
			{
				parameterlist.setValue(parameterKey, parameterValue);
			}
			break;


		case "XSTRING"://byte()
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


	private void setABAPTableValue(JCoTable table,String parameterKey,String parameterValue,String abap_jco_type) throws Exception
	{

		/**
		 * Possible ABAP types
		 * TYPE_CHAR, TYPE_NUM, TYPE_BYTE, TYPE_BCD, TYPE_INT, TYPE_INT1, TYPE_INT2, TYPE_FLOAT, 
		 * TYPE_DATE, TYPE_TIME, TYPE_DECF16, TYPE_DECF34, TYPE_STRING, TYPE_XSTRING
		 */
		switch(abap_jco_type)
		{

		case "CHAR"://String
			if(table != null)
			{
				table.setValue(parameterKey, parameterValue);
			}
			break;


		case "NUM"://String
			if(table != null)
			{
				table.setValue(parameterKey, parameterValue);
			}
			break;


		case "BYTE"://byte()
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


		case "BCD"://BigDecimal
			if(table != null)
			{
				table.setValue(parameterKey, BigDecimal.valueOf(Double.parseDouble(parameterValue)));
			}		
			break;


		case "INT"://int
			if(table != null)
			{
				table.setValue(parameterKey, Integer.parseInt(parameterValue));
			}
			break;


		case "INT1"://int
			if(table != null)
			{
				table.setValue(parameterKey, Integer.parseInt(parameterValue));
			}
			break;


		case "INT2"://int
			if(table != null)
			{
				table.setValue(parameterKey,Integer.parseInt(parameterValue));
			}
			break;


		case "FLOAT"://double
			if(table != null)
			{
				table.setValue(parameterKey, Double.parseDouble(parameterValue));
			}
			break;


		case "DATE"://Date("YYYYMMDD")
			if(table != null)
			{
				table.setValue(parameterKey, parameterValue);
			}
			break;


		case "TIME"://Date("HHMMSS")
			if(table != null)
			{
				table.setValue(parameterKey, parameterValue);
			}
			break;


		case "DECF16"://BigDecimal
			if(table != null)
			{
				table.setValue(parameterKey, BigDecimal.valueOf(Double.parseDouble(parameterValue)));
			}
			break;


		case "DECF34"://BigDecimal
			if(table != null)
			{
				table.setValue(parameterKey, BigDecimal.valueOf(Double.parseDouble(parameterValue)));
			}
			break;


		case "STRING"://string
			if(table != null)
			{
				table.setValue(parameterKey, parameterValue);
			}
			break;


		case "XSTRING"://byte()
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

	private void setABAPStructureValue(JCoStructure structure,String parameterKey,String parameterValue,String abap_jco_type) throws Exception
	{

		/**
		 * Possible ABAP types
		 * TYPE_CHAR, TYPE_NUM, TYPE_BYTE, TYPE_BCD, TYPE_INT, TYPE_INT1, TYPE_INT2, TYPE_FLOAT, 
		 * TYPE_DATE, TYPE_TIME, TYPE_DECF16, TYPE_DECF34, TYPE_STRING, TYPE_XSTRING
		 */

		switch(abap_jco_type)
		{

		case "CHAR"://String
			if(structure != null)
			{
				structure.setValue(parameterKey, parameterValue);
			}
			break;


		case "NUM":	//String			
			if(structure != null)
			{
				structure.setValue(parameterKey, parameterValue);
			}			
			break;


		case "BYTE"://byte()
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


		case "BCD"://BigDecimal
			if(structure != null)
			{
				structure.setValue(parameterKey, BigDecimal.valueOf(Double.parseDouble(parameterValue)));
			}
			break;


		case "INT":	//int
			if(structure != null)
			{
				structure.setValue(parameterKey, Integer.parseInt(parameterValue));
			}
			break;


		case "INT1"://int
			if(structure != null)
			{
				structure.setValue(parameterKey, Integer.parseInt(parameterValue));
			}
			break;


		case "INT2"://int
			if(structure != null)
			{
				structure.setValue(parameterKey,Integer.parseInt(parameterValue));
			}
			break;


		case "FLOAT"://double			
			if(structure != null)
			{
				structure.setValue(parameterKey, Double.parseDouble(parameterValue));
			}
			break;


		case "DATE"://Date("YYYYMMDD")
			if(structure != null)
			{
				structure.setValue(parameterKey, parameterValue);
			}
			break;


		case "TIME"://Date("HHMMSS")
			if(structure != null)
			{
				structure.setValue(parameterKey, parameterValue);
			}
			break;


		case "DECF16"://BigDecimal
			if(structure != null)
			{
				structure.setValue(parameterKey, BigDecimal.valueOf(Double.parseDouble(parameterValue)));
			}
			break;


		case "DECF34"://BigDecimal
			if(structure != null)
			{
				structure.setValue(parameterKey, BigDecimal.valueOf(Double.parseDouble(parameterValue)));
			}
			break;


		case "STRING"://string
			if(structure != null)
			{
				structure.setValue(parameterKey, parameterValue);
			}
			break;


		case "XSTRING"://byte()
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
