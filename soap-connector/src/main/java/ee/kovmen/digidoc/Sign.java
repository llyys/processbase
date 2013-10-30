package ee.kovmen.digidoc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.exception.DocumentNotFoundException;
import org.ow2.bonita.facade.runtime.Document;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;

import ee.sk.digidoc.DataFile;
import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.Signature;
import ee.sk.digidoc.SignedDoc;
import ee.sk.digidoc.factory.DigiDocFactory;
import ee.sk.utils.ConfigManager;


public class Sign {
	
	public Sign(String processInscanceId){
		pid=processInscanceId;
	}
	private SignedDoc sdoc=null;
	private Signature signature=null;
	private QueryRuntimeAPI queryRuntimeAPI;
	private String pid=null;
	public static final String SIGNED_DOC = "SignedDoc";
	public static final String MIME_TYPE = "application/octet-stream";
	
	public void initSettings(String bonitaHomeDir) throws IOException{
		FileInputStream fis=null;
		try {
			File file = new File(bonitaHomeDir + "/esteid.properties");//global configuration can be accessed %BONITA_HOME%\processbase3.properties
			fis = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fis);
			properties.put("DIGIDOC_DF_CACHE_DIR", getProcessTempDirectory(pid));
			ConfigManager.init("jar://jdigidoc.cfg");
			ConfigManager.init(properties);
			
		} finally  {
			if(fis!=null)
				fis.close();
		}		
	}
	
	public static byte[] toByteArray(SignedDoc doc) throws DigiDocException, IOException{
		//this dirty smelling file write and read is created beacause EST EID library is written this way :'-(
		File outputFile = File.createTempFile("sdoc", null);
		doc.writeToFile(outputFile);
		FileInputStream fis=new FileInputStream(outputFile);
		  byte fileContent[] = new byte[(int)outputFile.length()];
		  fis.read(fileContent);
		  return fileContent;
		/*ByteArrayOutputStream baos=new ByteArrayOutputStream();
		doc.writeToStream(baos);
		return baos.toByteArray();*/
	}
	
	
	
	public static String getBonitaHomeDir(){
		return System.getProperty("BONITA_HOME");
	}
	
	public String appendFilesAndGenerateHash(String cert, List<Document> documents) throws DigiDocException, CertificateException, DocumentNotFoundException, IOException{
		 sdoc=new SignedDoc(SignedDoc.FORMAT_DIGIDOC_XML, SignedDoc.VERSION_1_3);
		 for (Document document : documents) {
			 
			 //DataFile df=new DataFile(getSdoc().getNewDataFileId(), DataFile.CONTENT_EMBEDDED_BASE64, document.getContentFileName(), document.getContentMimeType(), getSdoc());
			 byte[] content = readDocumentContent(document);
			 //df.setBody(content, "UTF-8");
			 //getSdoc().addDataFile(df);
			 String tempDir = getProcessTempDirectory(pid.toString());			 
			 //File tempFile=File.createTempFile("sign", null, null);
			 File tempFile=new File(tempDir, document.getContentFileName());
			 
			 FileOutputStream fos = new FileOutputStream(tempFile);				
			 fos.write(content);
			 fos.close();
			 
			 sdoc.addDataFile(tempFile, document.getContentMimeType(), DataFile.CONTENT_EMBEDDED_BASE64);
			 /*
			 ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
			 
				try {
					Base64OutputStream b = new Base64OutputStream(out);
					//GZIPOutputStream g = new GZIPOutputStream(b);
					//IOUtils.write(bytes, g);
					IOUtils.write(content, b);
					b.close();
					//g.close();
					byte[] outarr = out.toByteArray();
					
					DataFile df=new DataFile(sdoc.getNewDataFileId(), DataFile.CONTENT_EMBEDDED_BASE64, document.getContentFileName(), document.getContentMimeType(), sdoc);
					df.setBody(outarr);
					ByteArrayOutputStream baos2=new ByteArrayOutputStream();
					df.calculateFileSizeAndDigest(baos2);
					//df.setDigest(digest);
					sdoc.addDataFile(df);
					
					out.close();
					
				} catch (IOException e) {
					throw new DigiDocException(21, "Viga", e);
				}
				*/
		}
		 
		CertificateFactory cf = CertificateFactory.getInstance("X.509", new BouncyCastleProvider());
		X509Certificate certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cert.getBytes()));
		setSignature(getSdoc().prepareSignature((X509Certificate) certificate, null, null));
		return SignedDoc.bin2hex(getSignature().calculateSignedInfoDigest());
	}

	public static String getProcessTempDirectory(String pid) {
		String tempDir=System.getProperty("java.io.tmpdir");
		 if(pid!=null)
		 {
			 if(tempDir.endsWith(File.separator))
				 tempDir+=pid;
			 else
				 tempDir+=File.separator+pid;
		 }
		 if(new File(tempDir).exists()==false)
			 new File(tempDir).mkdir();
		return tempDir;
	}

	/*
	 * Method created for testability reasons, so we can fake the document reading
	 * */
	protected byte[] readDocumentContent(Document document)
			throws DocumentNotFoundException {
		
		byte[] content=queryRuntimeAPI.getDocumentContent(document.getUUID());
		return content;
	}
	
	public void appendSignatureToDocument(String signVal) throws DigiDocException{
		 Signature sig=sdoc.getLastSignature();
		 sig.setSignatureValue(SignedDoc.hex2bin(signVal));
		 sig.getConfirmation();
		 sdoc=sig.getSignedDoc();
	}

	
	public SignedDoc getSdoc() {
		return sdoc;
	}

	public void setQueryRuntimeAPI(QueryRuntimeAPI queryRuntimeAPI) {
		this.queryRuntimeAPI = queryRuntimeAPI;
	}

	public void setSignature(Signature signature) {
		this.signature = signature;
	}

	public Signature getSignature() {
		return signature;
	}



	public void writeToFile(String tempFileName) throws Exception {
		// TODO Auto-generated method stub
		String tempDir = Sign.getProcessTempDirectory(pid);
		File dir = new File(tempDir);
		sdoc.writeToFile(new File(dir, tempFileName));
	}
	public void SerializeDoc(String tempFileName) throws Exception {
		// TODO Auto-generated method stub
		String tempDir = Sign.getProcessTempDirectory(pid);
		File dir = new File(tempDir);
		ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(dir, tempFileName)));
		try {
			os.writeObject(sdoc);
		}
		finally {
			os.close();
		}
		// sdoc.writeToFile(new File(tempDir+File.separator+tempFileName));
	}

	public void DeSerializeDoc(String tempFileName) throws Exception {
		String tempDir = Sign.getProcessTempDirectory(pid);
		File dir = new File(tempDir);
		ObjectInputStream is = new ObjectInputStream(new FileInputStream(new File(dir, tempFileName)));
		
		try {
			sdoc = (SignedDoc) is.readObject();
		}
		finally {
			is.close();
		}

		
	}

	public static byte[] readFileBytes(String processId, String tempFileName) throws Exception {
		String tempDir = Sign.getProcessTempDirectory(processId);
		File f = new File(tempDir+File.separator+tempFileName);
		FileInputStream fis=new FileInputStream(f);
		  byte fileContent[] = new byte[(int)f.length()];
		  fis.read(fileContent);
		  return fileContent;
	}

	
	
	
}
