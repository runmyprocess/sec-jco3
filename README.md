RunMyProcess SEC JCO3 Adapter
=============================

The "JCO3 Adapter" allows you to access SAP instances from the [SEC Manager](https://github.com/runmyprocess/sec-manager). When the Secure Enterprise Connector is used in conjunction with the JCO3 Adapter, you can securely access a SAP BAPIs and RFCs supported in SAP's JCO3 library from outside a firewall.


##Install and Configure the Adapter
1. Make sure you have [Java 7](http://www.oracle.com/technetwork/java/index.html) and [Maven](http://maven.apache.org/) installed on your machine. You must also have the RMP's [JSON](https://github.com/runmyprocess/json/) and the [sec-sdk](https://github.com/runmyprocess/sec-sdk) libraries installed on your local mvn repo; as well as the [jco-3.0.9.jar](http://service.sap.com/connectors).
2. Download this project and  run "mvn clean install" on the project's folder where the pom.xml is situated.

Run mvn clean install :

	mvn clean install

3. Copy the generated jar file (usually created in a generated "target" folder in the JDBC project's folder) to a folder of your choice. There will be two jar files generated: One will be the compiled code (JCO3Adapter-version.jar) and the compiled code with dependencies (JCO3Adapter-1.0-jar-with-dependencies.jar). If all the required libraries are on the maven repo then you can use the "JCO3Adapter-1.0-jar-with-dependencies.jar" for simple execution.
4. Create a "configFiles" directory in the jar file's path.
5. Inside the "configFiles" directory you must create 2 configuration files : handler.config and the JCO3.config

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
	JCO_POOL_CAPACITY  = 3
	JCO_PEAK_LIMIT = 10

This file contains the basic SAP connection information.

##Running and Testing the Adapter
You can now run the Adapter by executing the rmp-sec-JCO3Conector.jar in the installed path :

    java -jar rmp-sec-JCO3Conector.jar
    
If everything is configured correctly you can now place a request from RMP to retrieve information from SAP.
The POST body should look something like this :
    
	Example 1:
	{
	  "protocol": "JCO3",
	  "data": {
	    "SAPUser": "username",
	    "SAPPassword": "mypass123",
	    "serviceName": "ABAP_AS_WITH_POOL",
	    "functionName": "BAPI_USER_GET_DETAIL",
	    "importParameters": {
	      "USERNAME": "UserName"
	    }
	  }
	}
	Example 2:
	{
	"protocol":"JCO3",
	"data":{
	"SAPUser":"username",
	"SAPPassword":"mypass123",
	"serviceName":"ABAP_AS_WITH_POOL",
	"functionName":"BAPI_USER_GET_DETAIL",
	"getMetaData":"true"
	}
	}
	Example 3:
	{
	  "protocol": "JCO3",
	  "data": {
	    "SAPUser": "username",
	    "SAPPassword": "mypass123",
	    "serviceName": "ABAP_AS_WITH_POOL",
	    "functionName": "BAPI_REQUISITION_CREATE",
	    "importParameters": {
	      "AUTOMATIC_SOURCE": "X"
	    },
	    "tableParameters": {
	      "REQUISITION_ITEMS": [
	        {
	          "DOC_TYPE": "MYDOC_TYPE",
	          "DEL_DATCAT": "MYDEL_DATCAT",
	          "DELIV_DATE": "MYDELIV_DATE",
	          "PLANT": "MYPLANT",
	          "STORE_LOC": "MYSTORE_LOC",
	          "PUR_GROUP": "MYPUR_GROUP",
	          "MAT_GRP": "MYMAT_GRP",
	          "PREQ_ITEM": "MYPREQ_ITEM",
	          "MATERIAL": "MYMATERIAL",
	          "QUANTITY": "MYQUANTITY",
	          "PREQ_NAME": "MYPREQ_NAME",
	          "PURCH_ORG": "MYPURCH_ORG",
	          "ACCTASSCAT": "MYACCTASSCAT",
	          "VEND_MAT": "MYVEND_MAT"
	        }
	      ]
	    },
	    "responseType":"XML"
	  }
	}

If parameter contains table inside it then define table as shown in Example 3,
"tableParameters" contains table "REQUISITION_ITEMS" so that value for key 
"REQUISITION_ITEMS" is array of JSON where each array index will be consider as 
single row of corresponding table.
	
Two response type:
		 **JSON**- default.
		 **XML**- in case of filed “responseType” is present with value “XML”.


The expected return is a JSON object that should look like this :

	{
	"SECStatus":200,
	"Response":SAPResponse encoded in Base64
	}
