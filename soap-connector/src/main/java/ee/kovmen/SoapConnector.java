package ee.kovmen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.ow2.bonita.connector.core.ConnectorError;
import org.ow2.bonita.connector.core.ProcessConnector;
import org.ow2.bonita.facade.APIAccessor;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.util.BonitaConstants;

public class SoapConnector extends ProcessConnector {

	// DO NOT REMOVE NOR RENAME THIS FIELD
	private java.lang.String script;

	@Override
	protected void executeConnector() throws Exception {
		StringBuilder builder = new StringBuilder("");
		    if(script.startsWith("${")==false){
		       builder = new StringBuilder("${");
		       builder.append(script);
		       builder.append("}");
		    }
		    final APIAccessor accessor = getApiAccessor();
		    final RuntimeAPI runtimeAPI = accessor.getRuntimeAPI();
		    if (getActivityInstanceUUID() != null) {
		      result = runtimeAPI.evaluateGroovyExpression(builder.toString(), getActivityInstanceUUID(), false, true);
		    } else {
		      result = runtimeAPI.evaluateGroovyExpression(builder.toString(), getProcessInstanceUUID(), true);
		    }

	}

	@Override
	protected List<ConnectorError> validateValues() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Setter for input argument 'script'
	 * DO NOT REMOVE NOR RENAME THIS SETTER, unless you also change the related entry in the XML descriptor file
	 */
	public void setScript(java.lang.String script) {
		this.script = script;
	}

	/**
	 * Getter for output argument 'result'
	 * DO NOT REMOVE NOR RENAME THIS GETTER, unless you also change the related entry in the XML descriptor file
	 */
	 private Object result;

	public Object getResult() {
		// TODO Add return value for the output here
		 return result;
	}
	public static StringWriter postXML(String endpoint, String xml) throws Exception
    {
		StringWriter sw=new StringWriter();
        postData( new StringReader(xml), new URL(endpoint), sw);
        return sw;
    }
	
	  /**
	     * Reads data from the data reader and posts it to a server via POST request.
	     * data - The data you want to send
	     * endpoint - The server's address
	     * output - writes the server's response to output
	     * @throws Exception
	     */
	    public static void postData(Reader data, URL endpoint, Writer output) throws Exception {
	        //log.debug("postData:"+endpoint.toString());
	        HttpURLConnection urlc = null;
	        try {
	            urlc = (HttpURLConnection) endpoint.openConnection();
	            try {
	                urlc.setRequestMethod("POST");
	            } catch (ProtocolException e) {
	          //      log.error("Shouldn't happen: HttpURLConnection doesn't support POST??" + e);
	                throw new Exception("Shouldn't happen: HttpURLConnection doesn't support POST??", e);
	            }
	            urlc.setDoOutput(true);
	            urlc.setDoInput(true);
	            urlc.setUseCaches(false);
	            urlc.setAllowUserInteraction(false);
	            urlc.setRequestProperty("Content-type", "text/xml; charset=" + "UTF-8");
	            OutputStream out = urlc.getOutputStream();
	            try {
	                //Writer writer = new OutputStreamWriter(out, "UTF-8");
	                Writer writer = new OutputStreamWriter(out);
	                pipe(data, writer);
	                writer.close();
	            } catch (IOException e) {
	               // log.error("IOException while posting data " + e);
	                throw new Exception("IOException while posting data", e);
	            } finally {
	                if (out != null) {
	                    out.close();
	                }
	            }
	            InputStream in = urlc.getInputStream();
	            try {
	                Reader reader = new InputStreamReader(in);
	                pipe(reader, output);
	                reader.close();
	            } catch (IOException e) {
	                throw new Exception("IOException while reading response", e);
	            } finally {
	                if (in != null) {
	                    in.close();
	                }
	            }
	        } catch (IOException e) {
	            throw new Exception("Connection error (is server running at " + endpoint + " ?): " + e);

	        } finally {
	            if (urlc != null) {
	                urlc.disconnect();
	            }
	        }
	    }
	    
	    /**
	     * Pipes everything from the reader to the writer via a buffer
	     */
	    private static void pipe(Reader reader, Writer writer) throws IOException {
	        char[] buf = new char[1024];
	        int read = 0;
	        while ((read = reader.read(buf)) >= 0) {
	            writer.write(buf, 0, read);
	        }
	        writer.flush();
	    }

	    
	    private static Properties props = null;
	    public static Properties getConfig(){
	    	if (props == null) {

				FileInputStream fs = null;
				try {
					props = new Properties();
					String bonita_home = BonitaConstants.getBonitaHomeFolder();
					fs = new FileInputStream(new File(bonita_home
							+ "/xtee_connector.properties"));
					props.load(fs);

				} 
				
				catch (Exception e) {
					// TODO Auto-generated catch block
					//log.error("GetProperty", e);
				} finally {
					if (fs != null)
						try {
							fs.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}

			}
	    	return props;
	    }

}
