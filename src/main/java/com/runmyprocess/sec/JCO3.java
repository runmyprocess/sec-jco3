package com.runmyprocess.sec;


import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.util.Codecs.Base64;

import org.runmyprocess.json.JSONException;
import org.runmyprocess.json.JSONObject;
import org.runmyprocess.sec.SECLogManager;
import org.runmyprocess.sec.Config;
import org.runmyprocess.sec.ProtocolInterface;
import org.runmyprocess.sec.Response;
/** <li>Class <tt>JCO3</tt> implements <tt>ProtocolInterface</tt> and
 * overrides it's methods <code>accept</code>, <code>getResponse</code> 
 * with other methods in this class.<li>It is possible to create instance of <tt>JCO3</tt>.</p>
 *
 * @author Sanket Joshi <sanket.joshi@flowian.com>
 *
 */
public class JCO3 implements ProtocolInterface {

	/** Logging instance */
	private static final SECLogManager LOG = new SECLogManager(JCO3.class.getName());

	/**
	 * This is constant variable with value of service name <i>ABAP_AS_WITHOUT_POOL</i> 
	 */
	private static final String ABAP_AS = "ABAP_AS_WITHOUT_POOL";
	/**
	 * This is constant variable with value of service name <i>ABAP_AS_WITH_POOL</i> 
	 */
	private static final String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL";
	/**
	 * This is constant variable with value of service name <i>ABAP_MS_WITHOUT_POOL</i> 
	 */
	@SuppressWarnings("unused")
	private static final String ABAP_MS = "ABAP_MS_WITHOUT_POOL";
	/**
	 * Array of service name constants.
	 */
	private static final String[] services = {ABAP_AS,ABAP_AS_POOLED};
	/** Response instance */
	private Response response = new Response();

	/**
	 * <ul><li>Enforces use of <tt>Exception</tt> to accept generated exception at runtime.
	 * <li>The value returned is the instance of <tt>JSONObject</tt> 
	 * containing exception in <tt>String</tt> type.
	 * 
	 * @param exception any possible exception at runtime
	 * @return errorObject the instance of <tt>JSONObject</tt>
	 */
	private JSONObject JCO3Error(Exception exception){

		response.setStatus(400);//sets the return status to internal server error
		JSONObject errorObject = new JSONObject();
		errorObject.put("error", exception.toString());
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		errorObject.put("stack", sw.toString());
		LOG.log(exception.toString(),Level.WARNING);
		response.setData(errorObject);
		return errorObject;
	}


	/** <ul><li>Calls the <tt>Properties</tt> method <code>store</code> to store connection 
	 * Properties to the output stream in a format suitable for loading into a 
	 * <code>Properties</code> table</li>.
	 * 
	 * @param destinationName    name for destination,
	 * @param connectProperties  destination property set in <code>setConnection</code>.
	 * @Exception 		RuntimeException if Unable to create the destination files.
	 */
	static void createDestinationDataFile(String destinationName, Properties connectProperties)
	{
		LOG.log("Creating destination files...", Level.INFO);
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

	/**<ul><li>Calls the <tt>Object</tt> method <code>setProperty</code> 
	 * to set connection Properties of <tt>DestinationDataProvider</tt>.
	 * <li>Enforces use of <tt>JSONObject</tt> for accessing SAP server's authentication details,
	 * and <tt>JCO3.config</tt> file for accessing SAP server's other details.
	 * <li>Set connection directly with JCO repository and indirectly with SAP server.
	 * <li>The value returned is the result of the <tt>JCoDestinationManager</tt>
	 *  call to <code>getDestination</code>.
	 * 
	 * @param jsonObject JSON input from RMP application.
	 * @return			 the object of <tt>JCoDestination</tt>	
	 * @throws Exception 
	 */

	private JCoDestination setConnection(JSONObject jsonObject)throws Exception{
		LOG.log("Searching for JCO3 config file ...",Level.INFO);
		Config config = new Config("configFiles"+File.separator+"JCO3.config",true);//finds and reads the config file
		LOG.log("JCO3 config file found...", Level.INFO);
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

		if (!Arrays.asList(services).contains(jsonObject.getString("serviceName"))){
			throw new RuntimeException("The service is not supported. Please input one of the following services: "
					+Arrays.toString(services));
		}		
		LOG.log("Getting destination...", Level.INFO);
		JCoDestination destination= JCoDestinationManager.getDestination(jsonObject.getString("serviceName"));
		return destination;


	}
	/**<ul><li>calls <tt>JCoDestination</tt> method <code>getRepository</code> to access <tt>JCo Repository</tt>
	 * <li>calls <tt>JCoContext</tt> method <code>begin</code> to start State-full session
	 * <li>understands the type request from requested data (either meta-data or BAPI execution) and execute accordingly.()
	 * <li>calls <tt>JCoRepository</tt> method <code>getFunction</code> to access <tt>JCo Function</tt>.
	 * <li>calls <tt>JCO3DataHandler</tt> method <code>setParamerts</code>
	 * to set required parameters of BAPI execution.
	 * <li>The value returned is the instance of <tt>JSONObject</tt> 
	 * containing either results of current <tt>JCoFunction</tt> execution(value XML/JSON), or
	 * the meta-data of current <tt>JCoFunction</tt>.
	 *
	 * @param destination JCo Destination for current JCo request. 
	 * @param jsonObject JSON input from caller.
	 * @return response results for current JCo request after completion of <code>worker</code>
	 * @throws Exception
	 */
	
	private JSONObject worker(JCoDestination destination,JSONObject jsonObject)throws Exception
	{
		LOG.log("Getting repository...", Level.INFO);
		JCoRepository repository=destination.getRepository();
		LOG.log("Starting State-full session...", Level.INFO);
		JCoContext.begin(destination);
		JCO3DataHandler datahandler=new JCO3DataHandler();
		JSONObject response=new JSONObject();

		if(jsonObject.containsKey("functionName") && !jsonObject.containsKey("getMetaData"))
		{
			LOG.log("Retrieving function...", Level.INFO);
			JCoFunction function=repository.getFunction(jsonObject.getString("functionName"));
			if(function == null)
				throw new RuntimeException(jsonObject.getString("functionName")+" function not found in SAP.");
			if(function != null)
			{
				try{
					LOG.log("Setting Parameters...", Level.INFO);
					if(jsonObject.containsKey("importParameters") || jsonObject.containsKey("exportParameters") || jsonObject.containsKey("tableParameters") || jsonObject.containsKey("changingParameters"))
						datahandler.setParamerts(jsonObject,function); 

					LOG.log("Executing BAPI...", Level.INFO);
					function.execute(destination);  // call to SAP server

					LOG.log("BAPI execution completed...", Level.INFO);
					LOG.log("Getting results...", Level.INFO);
					if(jsonObject.containsKey("responseType") && jsonObject.getString("responseType").equalsIgnoreCase("xml"))
						response.put(function.getName(),function.toXML());	
					else
						response.put(function.getName(),datahandler.getParamerts(function));

				}
				catch (JSONException e){
					return JCO3Error(e);
				}
				catch (AbapException e){
					return JCO3Error(e);
				}
				catch (JCoException e) {
					return JCO3Error(e);
				}
				catch (Exception e) {
					return JCO3Error(e);
				}
				finally{
					LOG.log("Finishing State-full session...", Level.INFO);
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
				LOG.log("Retrieving function...", Level.INFO);
				JCoFunction function=repository.getFunction(jsonObject.getString("functionName"));
				if(function == null)
					throw new RuntimeException(jsonObject.getString("functionName")+" function not found in SAP.");
				if(function != null)
				{
					try	{	LOG.log("Retrieving metadata...", Level.INFO);
					response.put(function.getName(),datahandler.getParamertsMetadata(function));
					}catch (Exception e){
						return JCO3Error(e);
					}finally{
						LOG.log("Finishing State-full session...", Level.INFO);
						JCoContext.end(destination);
					}
				}
			}
			else
			{
				return JCO3Error(new Exception("Bad Request"));
			}
		LOG.log("Returning results...", Level.INFO);
		return response;
	}

	/** <ul><li>Calls the <tt>JCO3</tt> method <code>setConnection</code> to set the connection with SAP server
	 * 	and accept the object of <tt>JCoDestination.</tt>
	 * 	<li>Calls the <tt>JCO3</tt> method <code>worker</code> to start working on current JCO request
	 * 	and accept the result as <tt>JSONObject</tt>
	 * 	<li>Encode the request result and set in Response with status 200.
	 *	<li> Enforces use of <tt>JSONObject</tt> and <tt>String</tt> for receiving JSON of input data 
	 * and local configuration file path.
	 * @param jsonObject JSON input from RMP application.
	 * @param configPath configuration file path.
	 */
	@Override
	public void accept(JSONObject jsonObject,String configPath) {

		JSONObject reply = new JSONObject();
		try{
			LOG.log("\nStarting JCO request...", Level.INFO);
			if (jsonObject.getString("TEST") != null){
				reply.put("Response","Automatic TEST response from SAP Adapter :"+Base64.encode(new String("顎").getBytes("UTF-8")));
				response.setStatus(200);//sets SEC status to 200
				response.setData(reply);
			}else{
				LOG.log("Setting JCO Connection...", Level.INFO);
				JCoDestination destination=setConnection(jsonObject);
				LOG.log("Connection established successfully...", Level.INFO);
				String encodedData=Base64.encode(worker(destination,jsonObject).toString().getBytes("UTF-8"));
				reply.put("Response",encodedData);
				response.setStatus(200);
				response.setData(reply);
				LOG.log("JCO request completed successfully...", Level.INFO);
			}

		} catch (Exception e) {
			JCO3Error(e);
		}
	}

	
	/**
	 * <ul><li>The value returned is the instance of <tt>Response</tt> 
	 * containing results of current JCO request.</li> 
	 */
	@Override
	public Response getResponse() {
		LOG.log("returning response",Level.INFO);
		return response;
	}


}
