package ee.kovmen.digidoc;

import com.codeborne.security.signature.SignatureSession;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.exception.DocumentNotFoundException;
import org.ow2.bonita.facade.runtime.Document;

import java.io.*;
import java.util.Properties;

public class SignatureHelper {

    public static void initializeKeystore(){
        if(System.getProperty("javax.net.ssl.trustStore")!=null)
            return;

        File keystore = new File(SignatureHelper.getBonitaHomeDir(), "keystore.jks");
        if (!keystore.exists())
            throw new RuntimeException("File not found: " + keystore.getAbsolutePath());

        System.setProperty("javax.net.ssl.trustStore", keystore.getAbsolutePath());
    }

    public static File getFileFromProcess(QueryRuntimeAPI queryRuntimeAPI, Document document, String path) throws DocumentNotFoundException, IOException {
        byte[] content = queryRuntimeAPI.getDocumentContent(document.getUUID());

        File tempFile=new File(path, document.getContentFileName());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tempFile);
            fos.write(content);
        }
        finally {
            fos.close();
        }
        return tempFile;
    }

    public static String getTempDirectoryForProcess(String pid){
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

    public static void serializeToDisc(String path, String filename, Serializable object) throws IOException {
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(path, filename)));
        try {
            os.writeObject(object);
        }
        finally {
            os.close();
        }
    }

    public static Object deSerializeFromFile(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
        try {
            return is.readObject();
        }
        finally {
            is.close();
        }
    }

    public static Properties getProperties(File file) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(fis);
            return properties;
        }finally {
            fis.close();
        }
    }

    public static String getBonitaHomeDir(){
        return System.getProperty("BONITA_HOME");
    }
}
