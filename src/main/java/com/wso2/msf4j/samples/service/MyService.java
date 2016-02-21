/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wso2.msf4j.samples.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.transform.stream.StreamSource;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.event.report.HtmlReportGenerator;
import org.milyn.io.StreamUtils;
import org.milyn.payload.StringResult;
import org.wso2.msf4j.template.MustacheTemplateEngine;
import org.xml.sax.SAXException;

/**
 * This is the Microservice resource class.
 * See <a href="https://github.com/wso2/msf4j#getting-started">https://github.com/wso2/msf4j#getting-started</a>
 * for the usage of annotations.
 *
 * @since 1.0.0-SNAPSHOT
 */
@Path("/service")
public class MyService {
	
	private static byte[] messageIn = readInputMessage();

    protected static String runSmooksTransform() throws IOException, SAXException, SmooksException {
    	
    	Locale defaultLocale = Locale.getDefault();
    	Locale.setDefault(new Locale("en", "IE"));
    	
        // Instantiate Smooks with the config...
        Smooks smooks = new Smooks("smooks-config.xml");
        try {
             // Create an exec context - no profiles....
            ExecutionContext executionContext = smooks.createExecutionContext();

            StringResult result = new StringResult();

            // Configure the execution context to generate a report...
            executionContext.setEventListener(new HtmlReportGenerator("target/report/report.html"));

            // Filter the input message to the outputWriter, using the execution context...
            smooks.filterSource(executionContext, new StreamSource(new ByteArrayInputStream(messageIn)), result);

            Locale.setDefault(defaultLocale);

            return result.getResult();
        } finally {
            smooks.close();
        }
    }
    
    private static byte[] readInputMessage() {
        try {
        	return StreamUtils.readStream( MyService.class.getClassLoader().getResourceAsStream("input-message.edi") );
        } catch (IOException e) {
            e.printStackTrace();
            return "<no-message/>".getBytes();
        }
    }

    private static void pause(String message) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("> " + message);
            in.readLine();
        } catch (IOException e) {
        }
        System.out.println("\n");
    }
    
    @GET
    @Path("/index")
    public Response getBootStrapHome() {
    	
    	String html = MustacheTemplateEngine.instance().render("edi.mustache",null);
        return Response.ok()
                .type(MediaType.TEXT_HTML)
                .entity(html)
                .build();
    }
    
    @GET
    @Path("/smooks/2")
    public String getSmooks() throws SmooksException, IOException, SAXException {
    	 System.out.println("\n\n==============Message In==============");
         System.out.println(new String(messageIn));
         System.out.println("======================================\n");

         pause("The EDI input stream can be seen above.  Press 'enter' to see this stream transformed into XML...");

         String messageOut = runSmooksTransform();

         System.out.println("==============Message Out=============");
         System.out.println(messageOut);
         System.out.println("======================================\n\n");

         pause("And that's it!  Press 'enter' to finish...");
        return "Smooks went well";
    }
    
    @GET
    @Path("/smooks") 
    public String postConvertionFromSmooks( @QueryParam("edi") String edi)  {
    	
    	return new EDIFactParser().parse(edi);
    	
    }
    
    @POST
    @Path("/smooks-transform/{edi}")
    public String getConvertionFromSmooks(@PathParam("edi") String edi)  {
    	
    	return new EDIFactParser().parse(edi);
    	
    }
    
    

    @GET
    @Path("/")
    public String get() {
        // TODO: Implementation for HTTP GET request
        System.out.println("GET invoked");
        return "Hello from WSO2 MSF4J";
    }

    @POST
    @Path("/")
    public void post() {
        // TODO: Implementation for HTTP POST request
        System.out.println("POST invoked");
    }

    @PUT
    @Path("/")
    public void put() {
        // TODO: Implementation for HTTP PUT request
        System.out.println("PUT invoked");
    }

    @DELETE
    @Path("/")
    public void delete() {
        // TODO: Implementation for HTTP DELETE request
        System.out.println("DELETE invoked");
    }
}
