package com.runmyprocess.sec;


import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Properties;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.ext.DestinationDataProvider;

import org.runmyprocess.json.JSONException;
import org.runmyprocess.json.JSONObject;
//import org.runmyprocess.sec.SECLogManager;
import org.runmyprocess.sec.Config;
import org.runmyprocess.sec.ProtocolInterface;
import org.runmyprocess.sec.Response;

/**
 *
 * @author Malcolm Haslam <mhaslam@runmyprocess.com> 
 * @author Sanket Joshi <sanket.joshi@flowian.com>
 *
 * Copyright (C) 2013 Fujitsu RunMyProcess
 *
 * This file is part of RunMyProcess SEC.
 *
 * RunMyProcess SEC is free software: you can redistribute it and/or modify
 * it under the terms of the Apache License Version 2.0 (the "License");
 *
 *   You may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

public class JCO3 implements ProtocolInterface {

	// Logging instance
	//   private static final SECLogManager LOG = new SECLogManager(JCO3.class.getName());

	static String ABAP_AS = "ABAP_AS_WITHOUT_POOL";
	static String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL";
	static String ABAP_MS = "ABAP_MS_WITHOUT_POOL";

	static String[] services = {ABAP_AS,ABAP_AS_POOLED};
	private Response response = new Response();


	public JCO3() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Generates the error that will be sent back
	 * @param e error
	 * @return jsonObject error
	 */
	private JSONObject JCO3Error(Exception e){

		response.setStatus(400);//sets the return status to internal server error
		JSONObject errorObject = new JSONObject();
		errorObject.put("error", e.toString());
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		errorObject.put("stack", sw.toString());
		//        LOG.log(e.toString(),Level.WARNING);
		response.setData(errorObject);
		return errorObject;
	}


	/** creating destination files
	 * @param destinationName
	 * @param connectProperties
	 */
	static void createDestinationDataFile(String destinationName, Properties connectProperties)
	{
		File destCfg = new File(destinationName+".jcoDestination");
		try
		{
			FileOutputStream fos = new FileOutputStream(destCfg, false);
			connectProperties.store(fos, "for tests only !");
			fos.close();
		}
		catch (Exception e)
		{
			throw new RuntimeException("Unable to create the destination files", e);
		}
	}

	private void setConnection(JSONObject jsonObject)throws Exception{
		//        LOG.log("Searching for JCO3 config file ...",Level.INFO);
		Config config = new Config("configFiles"+File.separator+"JCO3.config",true);//finds and reads the config file
		//        LOG.log("JCO3 config file found", Level.INFO);
		Properties connectProperties = new Properties();
		connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST,config.getProperty("JCO_ASHOST"));
		connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, config.getProperty("JCO_SYSNR"));
		connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, config.getProperty("JCO_CLIENT"));
		connectProperties.setProperty(DestinationDataProvider.JCO_USER, jsonObject.getString("SAPUser"));
		connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD,jsonObject.getString("SAPPassword"));
		connectProperties.setProperty(DestinationDataProvider.JCO_LANG, config.getProperty("JCO_LANG"));
		createDestinationDataFile(ABAP_AS, connectProperties);

		connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, config.getProperty("JCO_POOL_CAPACITY"));
		connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT,   config.getProperty("JCO_PEAK_LIMIT"));
		createDestinationDataFile(ABAP_AS_POOLED, connectProperties);

	}

	/**
	 * @param jsonObject
	 * @return
	 * @throws Exception
	 */

	private JSONObject executeSAPBapi(JSONObject jsonObject)throws Exception
	{

		if (!Arrays.asList(services).contains(jsonObject.getString("serviceName"))){
			throw new RuntimeException("The service is not supported. Please input one of the following services: "
					+Arrays.toString(services));
		}
		//LOG.log("Getting destination...", Level.INFO);
		JCoDestination destination = JCoDestinationManager.getDestination(jsonObject.getString("serviceName"));
		//LOG.log("Getting repository...", Level.INFO);
		JCoRepository repository=destination.getRepository();
		//LOG.log("Starting State-full session...", Level.INFO);
		JCoContext.begin(destination);
		JCO3DataHandler datahandler=new JCO3DataHandler();
		JSONObject response=new JSONObject();

		if(jsonObject.containsKey("functionName") && !jsonObject.containsKey("getMetaData"))
		{
			//LOG.log("Retrieving function", Level.INFO);
			JCoFunction function=repository.getFunction(jsonObject.getString("functionName"));
			if(function == null)
				throw new RuntimeException(jsonObject.getString("functionName")+" function not found in SAP.");
			if(function != null)
			{
				try{
					if(jsonObject.containsKey("importParameters") || jsonObject.containsKey("exportParameters") || jsonObject.containsKey("tableParameters") || jsonObject.containsKey("changingParameters"))
						datahandler.setParamerts(jsonObject,function); 

					function.execute(destination);  // call to SAP server

					if(jsonObject.containsKey("responseType") && jsonObject.getString("responseType").equalsIgnoreCase("xml"))
						response.put(function.getName(),function.toXML());	
					else
						response.put(function.getName(),datahandler.getParamerts(function));
				}
				catch (JSONException e){
					e.printStackTrace();return JCO3Error(e);
				}
				catch (AbapException e){
					e.printStackTrace();return JCO3Error(e);
				}
				catch (JCoException e) {
					e.printStackTrace();return JCO3Error(e);
				}
				catch (Exception e) {
					e.printStackTrace();return JCO3Error(e);
				}
				finally{
					JCoContext.end(destination);
				}
			}
		}
		else
			if(jsonObject.containsKey("functionName") && 
					jsonObject.containsKey("getMetaData")  && 
					jsonObject.getString("getMetaData").equalsIgnoreCase("true") && 
					!jsonObject.containsKey("importParameters") &&
					!jsonObject.containsKey("exportParameters") &&
					!jsonObject.containsKey("tableParameters") &&
					!jsonObject.containsKey("changingParameters"))
			{
				JCoFunction function=repository.getFunction(jsonObject.getString("functionName"));
				if(function == null)
					throw new RuntimeException(jsonObject.getString("functionName")+" function not found in SAP.");
				if(function != null)
				{
					try	{	
						response.put(function.getName(),datahandler.getParamertsMetadata(function));
					}catch (Exception e){
						return JCO3Error(e);
					}finally{
						JCoContext.end(destination);
					}
				}
			}
			else
			{
				System.out.println("Bad Request");
				return JCO3Error(new Exception("Bad Request"));
			}

		return response;
	}

	/**
	 * Receives the information, reads the configuration information and calls the appropriate functions to set the
	 * response value
	 * @param jsonObject
	 * @param configPath
	 */
	@Override
	public void accept(JSONObject jsonObject,String configPath) {
		try{
			//            LOG.log("Starting JCO request", Level.INFO);
			if (jsonObject.getString("TEST") != null){
				JSONObject reply = new JSONObject();
				reply.put("Response","Automatic TEST response from SAP Adapter");
				response.setStatus(200);//sets SEC status to 200
				response.setData(reply);
			}else{
				setConnection(jsonObject);
				JSONObject reply = new JSONObject();
				reply.put("Response",executeSAPBapi(jsonObject));
				response.setData(reply);
			}

		} catch (Exception e) {
			JCO3Error(e);
		}
	}

	@Override
	public Response getResponse() {
		//        LOG.log("returning response",Level.INFO);
		return response;
	}


}
