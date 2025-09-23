package servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet is designed to accept block information from the 
 * index page and send a download back to the client. This way, 
 * the client can keep the xml for their block arrangement on
 * their computer to import later. 
 * 
 * @author Eric Peterson petersde@us.ibm.com
 * @version 1.6
 * @since 8/14/2012
 */

/**
 * Servlet implementation class Download
 */
public class Download extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Download() {
        super();
    }

	/**
	 * doPost
	 * 
	 * Accepts the information posted by the saveform-id form 
	 * in index.jsp
	 * 
	 * It takes in the xml (as a string) of blockly's representation
	 * of the blocks, and sets the response object to a download so that
	 * it gets downloaded to the client's computer. 
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String blocks = request.getParameter("blockly-xml"); //get the Blockly xml 
		String filename = request.getParameter("filename"); //get the file name for the download
		
		//set the response to be a downloaded text file, without caching the info
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("text/xml");
		setFileDownloadHeader(filename, request, response); //change the response header

		response.getWriter().write(blocks); //write the xml to the response body
		response.getWriter().close(); 
		response.getWriter().flush(); 
		
	}
	
	/*
	 * Method provided by Jeremy T. Kalinowski
	 */
	protected void setFileDownloadHeader(String filename, HttpServletRequest request, HttpServletResponse response ) {
        String defaultFilename = "DataPopBlocks";
        if(filename == null || filename.contains("Name your download here"))  {
        	filename = defaultFilename;
        }
        if (filename.endsWith(".xml")) {
        	filename = filename.substring(0, filename.length()-4);
        }
        // New header to fix file names being displayed improperly in download dialog windows.
        if(getIEVersion(request)==null){
            //standards browser version
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8'en'"+encodeUTF8(filename)+";");
        }else{
            String fileEnc=filename;
            try{
                fileEnc= URLEncoder.encode(filename,"UTF-8");
                //no modern browser understands + for space instead of  , why do you persist on doing this Java?
                fileEnc = fileEnc.replace("+", " ");
            }catch(UnsupportedEncodingException e){
            	//nothing right now
            }
            // Fix for IE dealing with doublebyte characters in file names.
            response.setHeader("Content-Disposition", "attachment; filename=\""+fileEnc.replaceAll(" ", "_")+"\";");
        }
    }
	
	/*
	 * Method provided by Jeremy T. Kalinowski
	 */
	private static StringBuffer encodeUTF8(String input) {
        StringBuffer buf = new StringBuffer();
        try {
            byte[] bytes = input.getBytes("UTF-8");
            int len = bytes.length;
            int i = 0;
            while (i < len) {
                int nByte = bytes[i];
                if (nByte < 0){
                    nByte += 256;
                }
                buf.append('%');
                buf.append(Integer.toHexString(nByte));
                i++;
           }
         } catch (UnsupportedEncodingException e){
             buf.append(input);
         }
         return buf;
   }
	
	//Get the browser information
	private static String getIEVersion(HttpServletRequest req) {
		String header = req.getHeader("User-Agent");
		return header;
	}

}
