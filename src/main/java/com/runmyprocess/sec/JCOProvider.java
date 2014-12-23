package com.runmyprocess.sec;

import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;

import java.util.Properties;

public class JCOProvider implements DestinationDataProvider {

    private String SAP_SERVER = "SAPSERVER";
    private DestinationDataEventListener eventListener;
    private Properties ABAP_AS_properties;

    public JCOProvider() {
    }

    @Override
    public Properties getDestinationProperties(String name) {

        if (name.equals(SAP_SERVER) && ABAP_AS_properties != null) {
            return ABAP_AS_properties;
        } else {
            return null;
        }
//        if(ABAP_AS_properties!=null) return ABAP_AS_properties;
//        else throw new RuntimeException("Destination " + name + " is not available");

    }

    @Override
    public boolean supportsEvents() {
        return true;
    }

    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void changePropertiesForABAP_AS(Properties properties) throws Exception {

        try {

            if (!Environment.isDestinationDataProviderRegistered()) {

                if (ABAP_AS_properties == null) {
                    ABAP_AS_properties = properties;
                }
                Environment.registerDestinationDataProvider(this);

            }

            if (properties == null) {

                if (eventListener != null) {
                    eventListener.deleted(SAP_SERVER);
                }
                ABAP_AS_properties = null;

            } else {

                ABAP_AS_properties = properties;
                if (eventListener != null) {
                    eventListener.updated(SAP_SERVER);
                }

            }

        } catch (Exception ex) {

            throw new Exception(ex.getMessage());

        }


    }
}