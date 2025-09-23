package com.ibm.wplc.tools.ossc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

import org.apache.commons.cli.CommandLine;

public class VerifyModulesUtil {

    public static File getFile( String pathname ){
        File rtnVal = new File(pathname);
        return rtnVal;
    }

	public static byte[] readFile(InputStream is) throws IOException {
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len;
		while ((len = is.read(buf)) > 0) {
			bos.write(buf, 0, len);
		}
		byte[] rtnVal = bos.toByteArray();
        return rtnVal;
	}

    /**
     * Calculate the hash for the byte array that will be read from the passed input stream
     * 
     * @param in
     * @return
     * @throws IOException
     */
    public static String calcHash(InputStream in,MessageDigest algorithm) throws IOException {
        StringBuffer hexString = new StringBuffer();

        algorithm.reset();
        long buf_size = 65536;
        byte[] buf = new byte[(int) buf_size];
        int len = in.read(buf);
        while (len != -1) {
            algorithm.update(buf, 0, len);
            len = in.read(buf);
        }

        byte messageDigest[] = algorithm.digest();
        for (int i = 0; i < messageDigest.length; i++) {
            String x = Integer.toHexString(0xFF & messageDigest[i]).toUpperCase();
            if (x.length() == 1) {
                x = "0" + x;
            }
            hexString.append(x);
        }
        return hexString.toString();
    }

    /**
     * Calculate the Hash for the specified file
     * 
     * @param f
     * @return
     * @throws IOException
     */
   public static String calcHash(File f,MessageDigest algorithm) throws IOException {
        if (!f.exists())
            throw new FileNotFoundException(f.toString());
        FileInputStream in = new FileInputStream(f);
        String hash = calcHash(in,algorithm);
        in.close();
        return hash;
    }

//   /**
//    * Calculate the Hash for the given string
//    * 
//    * @param source
//    * @return
//    * @throws IOException
//    */
//   @SuppressWarnings("unused")
//   private String calcHash(String source,MessageDigest algorithm) throws IOException {
//       InputStream in = null;
//       in = new ByteArrayInputStream(source.getBytes("UTF-8"));
//       String hash = calcHash(in,algorithm);
//       in.close();
//       return hash;
//   }

   public static void writeFile( String filename, InputStream in ) throws FileNotFoundException, IOException {
       // the caller is responsible for closing the inputstream
	   OutputStream out = null;
	   try{
		   File file = new File(filename);
		   boolean success = file.createNewFile();
		   
		   out = new FileOutputStream(filename);
		   int c;
		   while ((c = in.read()) != -1){
			   out.write(c);
		   }
	   }finally{
		   if (out != null) out.close();
	   }
   }

    public static void trace( CommandLine line, String msg ){
        if (line.hasOption("verbose")) {
            System.out.println(msg);
        }
    }
}
