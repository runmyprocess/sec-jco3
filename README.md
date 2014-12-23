RunMyProcess SEC JCO3 Adapter
=============================

The "JCO3 Adapter" allows you to access SAP instances from the [SEC Manager](https://github.com/runmyprocess/sec-manager). When the Secure Enterprise Connector is used in conjunction with the JCO3 Adapter, you can securely access a SAP BAPIs and RFCs supported in SAP's JCO3 library from outside a firewall.


##Install and Configure the Adapter
1. Make sure you have [Java](http://www.oracle.com/technetwork/java/index.html) and [Maven](http://maven.apache.org/) installed on your machine. You must also have the RMP's [JSON](https://github.com/runmyprocess/json/) and the [sec-sdk](https://github.com/runmyprocess/sec-sdk) libraries installed on your local mvn repo; as well as the [jco-3.01.jar](http://service.sap.com/connectors).
2. Download the jdbc project and  run mvn clean install on the project's folder.

Run mvn clean install :

	mvn clean install

3. Copy the generated jar file (usually created in a generated "target" folder in the JDBC project's folder) to a folder of your choice. There will be two jar files generated: One will be the compiled code (JCO3Adapter-version.jar) and the compiled code with dependencies (JCO3Adapter-1.0-jar-with-dependencies.jar). If all the required libraries are on the maven repo then you can use the "JCO3Adapter-1.0-jar-with-dependencies.jar" for simple execution.
4. Create a "configFiles" folder in the jar file's path.
5. Inside the "configFiles" folder you must create 2 config files : handler.config and the JCO3.config

The handler.config file should look like this :
    
	#Generic Protocol Configuration
	protocol = JCO3
	protocolClass = com.runmyprocess.sec.JCO3
	handlerHost = 127.0.0.1
	connectionPort = 5832
	managerHost = 127.0.0.1
	managerPort = 4444
	pingFrequency = 300
	    
Where :

* **protocol** is the name to identify our Adapter.
* **protocolClass** is the class of the Adapter.
* **handlerHost** is where the Adapter is running.
* **connectionPort** is the port of the adapter where data will be received and returned.
* **managerHost** is where the SEC is running. 
* **managerPort** is the port where the SEC is listening for ping registrations.
* **pingFrequency** is the frequency in which the manager will be pinged (at least three times shorter than what's configured in the manager).
 

In the **JCO3.config** file, the JCo3 configuration must be set.
The **JCO3.config** file should look like this :


	#SAP Server Connection Configuration
	JCO_ASHOST = 10.10.0.10 
	JCO_SYSNR = DPR
	JCO_CLIENT = 001
	JCO_LANG = en

This file contains the basic SAP connection information.

##Running and Testing the Adapter
You can now run the Adapter by executing the rmp-sec-JCO3Conector.jar in the installed path :

    java -jar rmp-sec-JCO3Conector.jar
    
If everything is configured correctly you can now place a request from RMP to retrieve information from SAP.
The POST body should look something like this :
    
	:::JSONRMP
	{
	"protocol":"JCO3",
	"data":{
	"SAPUser":"username",
	"SAPPassword":"mypass123",
	"serviceName":"ABAP_AS_WITH_POOL",
	"function":"BAPI_USER_GET_DETAIL",
	"inputParameters":{
		                        "USERNAME":"UserName"
		                    }
	}
	}

The expected return is a JSON object that should look like this :

	:::JSONRMP
	{
	"SECStatus":200,
	"Response":SAPResponse
	}
