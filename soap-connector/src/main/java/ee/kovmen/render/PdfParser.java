package ee.kovmen.render;

import java.util.Map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.map.CaseInsensitiveMap;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
public class PdfParser {
	/**
     * Takes a text file as input, replaces any special tokens with values pulled from the values map, then writes
     * the result to a new file.
     * <p/>
     * Tokens have the syntax $$TOKEN_NAME$$, i.e. the unique token name wrapped with double '$'.
     *
     * @param map A map of [token name] --> [replacement value]
     * @param templateFileName The file name of the input text file.
     * @param newFileName The file name for the new 'merged' output file.
     */
    public static byte[] replaceTokensInPdfFile(Map<String, Object> data, byte[] source) {
        try {        	
        	PdfReader reader = new PdfReader(source);
        	//insert the data
        	ByteArrayOutputStream BOS = new ByteArrayOutputStream();
        	PdfStamper stamp = new PdfStamper(reader, BOS);
        	AcroFields form = stamp.getAcroFields();
        	for (Entry<String, Object> field : data.entrySet()) {
				Object value = field.getValue();				
				form.setField(field.getKey(), value==null?"":value.toString());
			}
            
        	stamp.close();
            return BOS.toByteArray();
        	
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
}
