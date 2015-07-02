package com.runmyprocess.sec;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;

import org.runmyprocess.json.JSON;
import org.runmyprocess.json.JSONArray;
import org.runmyprocess.json.JSONObject;
import org.runmyprocess.sec.SECLogManager;

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

/**
 * <p>The main data handling class <tt>JCO3DataHandler</tt> with methods in this class,
 * it is possible to create instance of <tt>JCO3DataHandler</tt>.
 * <tt>JCO3DataHandler</tt> capacitively handles data with type conversion ABAP to JAVA and vis versa.
 * also provides generic getter/setter methods to get JCoParameterList as LinkedHashMap or set JCoParameterList from JSON.</p>
 * 
 * @author Sanket Joshi <sanket.joshi@flowian.com>
 *
 */
public class JCO3DataHandler {

	/** Logging instance */
	private static final SECLogManager LOG = new SECLogManager(JCO3DataHandler.class.getName());

	/**<ul><li>Returns the description of all parameters of requested <TT>JCoFunction</TT> as meta-data.
	 * 
	 * @param function instance of <tt>JCoFunction</tt>
	 * @return paramertsList instance of <tt>LinkedHashMap</tt> values contains meta-data. 
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

	/**<ul><li>Returns the description of field of requested <TT>JCoParameterList</TT> as meta-data.
	 * 
	 * @param iterator instance of <tt>JCoFieldIterator</tt> to iterate over fields <tt>JCoParameterList</tt>.
	 * @param parameters instance of <tt>LinkedHashMap</tt> to put values of fields.
	 * @return parameters instance of <tt>LinkedHashMap</tt> updated values of fields.
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

	/**<ul><li>Returns the description of table of requested <TT>JCoField</TT> as meta-data.
	 * 
	 * @param field instance of <tt>JCoField</tt>.
	 * @return map instance of <tt>LinkedHashMap</tt> to put values of table.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LinkedHashMap getTableParameterMetadata(JCoField field)
	{
		// TODO Auto-generated method stub
		JCoTable t = field.getTable();
		JCoFieldIterator iter = t.getFieldIterator();
		LinkedHashMap map = new LinkedHashMap();
		while(iter.hasNextField())
		{
			JCoField f = iter.nextField();
			map.put(f.getName(), f.getDescription());
		}

		return map;
	}

	/**<ul><li>Returns the description of structure of requested <TT>JCoField</TT> as meta-data.
	 * 
	 * @param field instance of <tt>JCoField</tt>.
	 * @return map instance of <tt>LinkedHashMap</tt> to put values of structure.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LinkedHashMap getStructureParameterMetadata(JCoField field)
	{
		// TODO Auto-generated method stub
		JCoFieldIterator iter = field.getStructure().getFieldIterator();
		LinkedHashMap map = new LinkedHashMap();
		while(iter.hasNextField())
		{
			JCoField f = iter.nextField();
			map.put(f.getName(),f.getDescription());
		}
		return map;
	}

	/**<ul><li>Returns the values of all parameters(import, export, table, changing) of requested <TT>JCoFunction</TT>.
	 * 
	 * @param function instance of <tt>JCoFunction</tt>
	 * @return paramertsList instance of <tt>LinkedHashMap</tt> values contains parameters value in SAP
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public LinkedHashMap getParamerts(JCoFunction function) throws Exception
	{
		LOG.log("Getting import parameters...", Level.INFO);
		LinkedHashMap paramertsList=new LinkedHashMap();
		if(function.getImportParameterList() != null){
			JCoFieldIterator importIterator=function.getImportParameterList().getFieldIterator();
			LinkedHashMap importParameters=new LinkedHashMap();
			paramertsList.put("importParameters",getField(importIterator,importParameters));
		}
		LOG.log("Getting export parameters...", Level.INFO);
		if(function.getExportParameterList() != null){
			JCoFieldIterator exportIterator=function.getExportParameterList().getFieldIterator();
			LinkedHashMap exportParameters=new LinkedHashMap();
			paramertsList.put("exportParameters",getField(exportIterator,exportParameters));
		}
		LOG.log("Getting table parameters...", Level.INFO);
		if(function.getTableParameterList() != null){
			JCoFieldIterator tableIterator=function.getTableParameterList().getFieldIterator();
			LinkedHashMap tableParameters=new LinkedHashMap();	
			paramertsList.put("tableParameters",getField(tableIterator,tableParameters));
		}
		LOG.log("Getting changing parameters...", Level.INFO);
		if(function.getChangingParameterList() != null){
			JCoFieldIterator changingIterator=function.getChangingParameterList().getFieldIterator();
			LinkedHashMap changingParameters=new LinkedHashMap();	
			paramertsList.put("changingParameters",getField(changingIterator,changingParameters));
		}
		return paramertsList;
	}

	/**<ul><li>Returns the values of field of requested <TT>JCoParameterList</TT>.
	 * 
	 * @param iterator instance of <tt>JCoFieldIterator</tt> to iterate over fields <tt>JCoParameterList</tt>.
	 * @param parameters instance of <tt>LinkedHashMap</tt> to put values of fields.
	 * @return parameters instance of <tt>LinkedHashMap</tt> with values of fields.
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

	/**<ul><li>Returns the values of table of requested <TT>JCoField</TT>.
	 * 
	 * @param field instance of <tt>JCoField</tt>.
	 * @return list instance of <tt>LinkedList</tt> to put values of table.
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LinkedList getTableParameter(JCoField field) throws Exception
	{
		// TODO Auto-generated method stub
		LinkedList list = new LinkedList();
		JCoTable t = field.getTable();
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
			list.add(m);
		}
		return list;
	}

	/**<ul><li>Returns the values of structure of requested <TT>JCoField</TT>.
	 * 
	 * @param field instance of <tt>JCoField</tt>.
	 * @return map instance of <tt>LinkedHashMap</tt> to put values of table.
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LinkedHashMap getStructureParameter(JCoField field) throws Exception
	{	
		// TODO Auto-generated method stub
		JCoStructure s=field.getStructure();
		JCoFieldIterator iter = s.getFieldIterator();
		LinkedHashMap map = new LinkedHashMap();
		while(iter.hasNextField())
		{
			JCoField f = iter.nextField();
			map.put(f.getName(), getABAPStructureValueAsString(s,f));
		}
		return map;
	}

	/**<ul><li>Calls the <tt>JCoField</tt> appropriate getter method 
	 * depending on current case of switch and return the result as type <tt>String</tt>.</li>  
	 * 
	 * @param field instance of <tt>JCoField</tt>
	 * @param abap_jco_type Possible ABAP types </br>TYPE_CHAR</br>TYPE_NUM</br>TYPE_BYTE</br>TYPE_BCD</br>TYPE_INT
	 * 						</br>TYPE_INT1</br>TYPE_INT2</br>TYPE_FLOAT</br>TYPE_DATE</br>TYPE_TIME
	 * 						</br>TYPE_DECF16</br>TYPE_DECF34</br>TYPE_STRING</br>TYPE_XSTRING
	 * @return value value of <code>field</code> in <tt>String</tt> type
	 * @throws Exception
	 */
	private String getABAPFliedValueAsString(JCoField field,String abap_jco_type) throws Exception
	{

		String value=null;
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

		default: LOG.log("Error while getting value for Unknown ABAP type "+abap_jco_type, Level.WARNING);break;				
		}
		return value;
	}

	
	/**<ul><li>Calls the <tt>JCoTable</tt> appropriate getter method 
	 * depending on current case of switch and return the result as type <tt>String</tt>.
	 * <li>Possible ABAP types </br>TYPE_CHAR</br>TYPE_NUM</br>TYPE_BYTE</br>TYPE_BCD</br>TYPE_INT
	 *				 </br>TYPE_INT1</br>TYPE_INT2</br>TYPE_FLOAT</br>TYPE_DATE</br>TYPE_TIME
	 *				 </br>TYPE_DECF16</br>TYPE_DECF34</br>TYPE_STRING</br>TYPE_XSTRING
	 * 
	 * @param table instance of <tt>JCoStructure</tt> whose field vale to be retrieve. 
	 * @param field instance of <tt>JCoField</tt> whose vale to be retrieve.
	 * @return value value of <code>field</code> in <tt>String</tt> type.
	 * @throws Exception
	 */
	
	private String getABAPTableValueAsString(JCoTable table,JCoField field)  throws Exception
	{

		String abap_jco_type=field.getTypeAsString();
		String value=null;
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

		default: LOG.log("Error while getting value for Unknown ABAP type "+abap_jco_type, Level.WARNING);break;				
		}
		return value;
	}

	/**<ul><li>Calls the <tt>JCoStructure</tt> appropriate getter method 
	 * depending on current case of switch and return the result as type <tt>String</tt>. 
	 * <li>Possible ABAP types </br>TYPE_CHAR</br>TYPE_NUM</br>TYPE_BYTE</br>TYPE_BCD</br>TYPE_INT
	 * </br>TYPE_INT1</br>TYPE_INT2</br>TYPE_FLOAT</br>TYPE_DATE</br>TYPE_TIME
	 * </br>TYPE_DECF16</br>TYPE_DECF34</br>TYPE_STRING</br>TYPE_XSTRING
	 * 
	 * @param structure instance of <tt>JCoStructure</tt> whose field vale to be retrieve. 
	 * @param field instance of <tt>JCoField</tt> whose vale to be retrieve.
	 * @return value value of <code>field</code> in <tt>String</tt> type.
	 * @throws Exception
	 */
	private String getABAPStructureValueAsString(JCoStructure structure,JCoField field)  throws Exception
	{

		String abap_jco_type=field.getTypeAsString();
		String value=null;
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

		default: LOG.log("Error while getting value for Unknown ABAP type "+abap_jco_type, Level.WARNING);break;		
		}
		return value;
	}

	/**<ul><li>Call <tt>JCO3DataHandler</tt> generic method <code>setParametersList</code>to 
	 * set the values of list's of parameters(import, export, table, changing) of requested <TT>JCoFunction</TT>.
	 * 
	 * @param jsonObject hold JSON input
	 * @param function requested <tt>JCoFunction</tt> instance
	 * @throws Exception
	 */
	public void setParamerts(JSONObject jsonObject,JCoFunction function) throws Exception 
	{

		LOG.log("Setting import parameters...", Level.INFO);
		JSONObject inputParameters = jsonObject.getJSONObject("inputParameters"); 
		if(inputParameters != null)
		{
			if(inputParameters.keySet() == null)
				return;
			setParametersList(inputParameters, function.getImportParameterList());
		}
		LOG.log("Setting and deactivating unwanted export parameters...", Level.INFO);
		JSONObject exportParameters = jsonObject.getJSONObject("exportParameters"); 
		if(exportParameters != null)
		{
			if(exportParameters.keySet() == null)
				return;
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
		LOG.log("Setting table parameters...", Level.INFO);
		JSONObject tableParameters = jsonObject.getJSONObject("tableParameters");		
		if(tableParameters != null)
		{
			setParametersList(tableParameters, function.getTableParameterList());
		}        
		LOG.log("Setting changing parameters...", Level.INFO);
		JSONObject changingParameter = jsonObject.getJSONObject("changingParameter"); 
		if(changingParameter != null)
		{
			setParametersList(changingParameter, function.getChangingParameterList());
		}
	}


	/**<ul>This is a generic method to set any <tt>JCoParameterList</tt>.
	 * 
	 * @param parameters hold JSON input.
	 * @param parameterlist requested <tt>JCoParameterList</tt> instance (import, export, table, changing)
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
				{	table=defineABAPTable(parameterlist.getTable(key.toString()),child);
				parameterlist.setValue(key.toString(), table);
				}
				if(metaData.getTypeAsString(key.toString())=="STRUCTURE")
				{LOG.log("Bad input pattern.\nYou'r trying to create a table which is structure type in ABAP.", Level.INFO);
				throw new Exception("Bad input pattern.");}
				}
			}
			else
				if(JSON.mayBeJSON(parameters.get(key.toString()).toString()))
				{	JSONObject child= parameters.getJSONObject(key.toString());
				if(child != null)
				{	JCoStructure structure=null;
				if(metaData.getTypeAsString(key.toString())=="STRUCTURE")
				{	structure=defineABAPStructure(parameterlist.getStructure(key.toString()),child);
				parameterlist.setValue(key.toString(), structure);
				}
				if(metaData.getTypeAsString(key.toString())=="TABLE")
				{LOG.log("Bad input pattern.\nYou'r trying to create a structure which is table type in ABAP.", Level.INFO);
				throw new Exception("Bad input pattern.");}
				}
				}else
				{
					setABAPFliedValue(parameterlist, key.toString(), parameters.getString(key.toString()), metaData.getTypeAsString(key.toString())); 
				}            		 	
		}
	}

	
	/**<ul><li>Define values for requested instance of <tt>JCoTable</tt> from <tt>JSONArray</tt>.
	 * <li>The vale return is instance of <tt>JCoTable</tt> with defined row and filed in it.
	 *  
	 * @param table instance of <tt>JCoTable</tt> which is to be define. 
	 * @param child instance of <tt>JSONArray</tt> array of <tt>JSONObject</tt> each index will be one row for <code>table</code>.
	 * @return table  instance of <tt>JCoTable</tt> with value.
	 * @throws Exception
	 */
	private JCoTable defineABAPTable(JCoTable table, JSONArray child) throws Exception {
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


	/**<ul><li>Define values for requested instance of <tt>JCoStructure</tt> from <tt>JSONObject</tt>.
	 * <li>The vale return is instance of <tt>JCoStructure</tt> with defined filed in it.
	 * 
	 * @param structure instance of <tt>JCoStructure</tt> which is to be define. 
	 * @param child instance of <tt>JSONObject</tt>.
	 * @return structure  instance of <tt>JCoStructure</tt> with value.
	 * @throws Exception
	 */
	private JCoStructure defineABAPStructure(JCoStructure structure, JSONObject child) throws Exception {
		// TODO Auto-generated method stub
		Set<?> keys = child.keySet();
		for (Object key : keys) 
		{          	          
			setABAPStructureValue(structure,key.toString(), child.getString(key.toString()), structure.getMetaData().getTypeAsString(key.toString()));
		}  
		return structure;
	}



	/**<ul><li>Convert the accepted <code>parameterValue</code> to target JAVA 
	 * type depending on current case of switch.
	 * <li>Possible ABAP types </br>TYPE_CHAR</br>TYPE_NUM</br>TYPE_BYTE</br>TYPE_BCD</br>TYPE_INT
	 * </br>TYPE_INT1</br>TYPE_INT2</br>TYPE_FLOAT</br>TYPE_DATE</br>TYPE_TIME
	 * </br>TYPE_DECF16</br>TYPE_DECF34</br>TYPE_STRING</br>TYPE_XSTRING
	 * <li>Calls the <tt>JCoParameterList</tt> overloaded appropriate setter method to set value.
	 * 
	 * @param parameterlist instance of <tt>JCoParameterList</tt>
	  * @param parameterKey name of parameter
	 * @param parameterValue value of parameter
	 * @param abap_jco_type ABAP type
	 * @throws Exception
	 */
	private void setABAPFliedValue(JCoParameterList parameterlist,String parameterKey,String parameterValue,String abap_jco_type) throws Exception
	{

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

		default:LOG.log("Error while setting value for Unknown ABAP type "+abap_jco_type, Level.WARNING);
		break;		
		}
	}




	/**<ul><li>Convert the accepted <code>parameterValue</code> to target JAVA 
	 * type depending on current case of switch.
	 * <li>Possible ABAP types </br>TYPE_CHAR</br>TYPE_NUM</br>TYPE_BYTE</br>TYPE_BCD</br>TYPE_INT
	 * </br>TYPE_INT1</br>TYPE_INT2</br>TYPE_FLOAT</br>TYPE_DATE</br>TYPE_TIME
	 * </br>TYPE_DECF16</br>TYPE_DECF34</br>TYPE_STRING</br>TYPE_XSTRING
	 * <li>Calls the <tt>JCoTable</tt> overloaded appropriate setter method to set value.
	 * 
	 * @param table instance of <tt>JCoTable</tt>
	 * @param parameterKey name of parameter
	 * @param parameterValue value of parameter
	 * @param abap_jco_type ABAP type
	 * @throws Exception
	 */
	private void setABAPTableValue(JCoTable table,String parameterKey,String parameterValue,String abap_jco_type) throws Exception
	{

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

		default: LOG.log("Error while setting value for Unknown ABAP type "+abap_jco_type, Level.WARNING);
		break;		
		}
	}

	/**<ul><li>Convert the accepted <code>parameterValue</code> to target JAVA 
	 * type depending on current case of switch.
	 * <li>Possible ABAP types </br>TYPE_CHAR</br>TYPE_NUM</br>TYPE_BYTE</br>TYPE_BCD</br>TYPE_INT
	 * </br>TYPE_INT1</br>TYPE_INT2</br>TYPE_FLOAT</br>TYPE_DATE</br>TYPE_TIME
	 * </br>TYPE_DECF16</br>TYPE_DECF34</br>TYPE_STRING</br>TYPE_XSTRING
	 * <li>Calls the <tt>JCoStructure</tt> overloaded appropriate setter method to set value.
	 * 
	 * @param structure instance of <tt>JCoStructure</tt>
	 * @param parameterKey name of parameter
	 * @param parameterValue value of parameter
	 * @param abap_jco_type ABAP type
	 * @throws Exception
	 */

	private void setABAPStructureValue(JCoStructure structure,String parameterKey,String parameterValue,String abap_jco_type) throws Exception
	{
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

		default: LOG.log("Error while setting value for Unknown ABAP type "+abap_jco_type, Level.WARNING);
		break;		
		}
	}


}
