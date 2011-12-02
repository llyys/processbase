package org.processbase.raports.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.eclipse.birt.report.engine.api.HTMLRenderContext;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IImage;



public class HTMLServerEmbeddedImageHandler extends HTMLServerImageHandler {
	private byte[] encoded;

	/**
	 * handles an image report item and returns an image URL
	 * 
	 * @param image
	 *            represents the image design information
	 * @param context
	 *            context information
	 * @param prefix
	 *            image prefix in URL
	 * @param needMap
	 *            whether image map is needed
	 * @return URL for the image
	 */
	protected String handleImage( IImage image, Object context, String prefix,
			boolean needMap )
	{
		return generateImageStream(image);
	}
	
	private String generateImageStream(IImage image){
		
	        InputStream is = image.getImageStream();
	        byte[] bytes;
			try {
				bytes = IOUtils.toByteArray(is);
				byte[] encoded = org.apache.commons.codec.binary.Base64.encodeBase64(bytes);
		        String encodedString=new String(encoded);
		        
	        	return "data:"+image.getMimeType()+";base64,"+encodedString;
			} catch (IOException e) {
				e.printStackTrace();
			}
	        return "error";
	}
	

}
