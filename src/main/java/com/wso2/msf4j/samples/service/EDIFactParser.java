package com.wso2.msf4j.samples.service;

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.tools.ant.filters.StringInputStream;
import org.milyn.Smooks;
import org.milyn.smooks.edi.unedifact.UNEdifactReaderConfigurator;

/**
 * @author Edgar Silva - edgar.silva@gmail.com 
 * */

public class EDIFactParser {
	
	public String parse(String in) {
		

        Smooks smooks = new Smooks();
       
        smooks.setReaderConfig(new UNEdifactReaderConfigurator("urn:org.milyn.edi.unedifact:d03b-mapping:*"));
       // smooks.setReaderConfig(new UNEdifactReaderConfigurator("urn:org.milyn.edi.unedifact:d96a-mapping:*"));

        try {
        	
             StringWriter writer = new StringWriter();
             
              InputStream is = new StringInputStream(in);
              
             smooks.filterSource(new StreamSource(is), new StreamResult(writer));
   
            return writer.toString();
        }
          catch (Exception e) {
			e.printStackTrace();
			return "ERRO NO SMOOKS : /n "  + e.getMessage();
		 
            
        } finally {
            smooks.close();
        }
	}
	
	public String parseFromMapping(String in, String mapping) {
		

        Smooks smooks = new Smooks();
       
       // smooks.setReaderConfig(new UNEdifactReaderConfigurator("urn:org.milyn.edi.unedifact:d03b-mapping:*"));
        smooks.setReaderConfig(new UNEdifactReaderConfigurator(mapping));

        try {
        	
             StringWriter writer = new StringWriter();
             
              InputStream is = new StringInputStream(in);
              
             smooks.filterSource(new StreamSource(is), new StreamResult(writer));
   
            return writer.toString();
        }
          catch (Exception e) {
			e.printStackTrace();
			return "ERRO NO SMOOKS : /n "  + e.getMessage();
		 
            
        } finally {
            smooks.close();
        }
	}
	
	


}
