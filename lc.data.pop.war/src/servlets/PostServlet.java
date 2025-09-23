package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.lconn.automation.datapop.interpreter.DataPopInterpreter;
import com.ibm.lconn.automation.datapop.DataPopAdapterException;

/**
 * This servlet is designed to accept block information from the 
 * index page, initiate the interpretation of the information,
 * and respond with the submission confirmation page.  
 * 
 * @author Eric Peterson petersde@us.ibm.com
 * @version 1.6
 * @since 8/14/2012
 */
public class PostServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PostServlet() {
        super();
    }

	/**
	 * doPost
	 * 
	 * 
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		writer.print("loading confirmation page");

		String data = request.getParameter("connections-data"); //get the Connections lang xml
		String blocks = request.getParameter("blockly-blocks"); //get the Blockly xml
		
		try {
			DataPopInterpreter.interpret(DataPopInterpreter.parseString(data)); //interpret the blocks
		}
		catch (DataPopAdapterException be) { //user tried to target a blacklisted host
			request.setAttribute("report", "ERROR: " + be.getMessage());
		}
		catch (Exception e) { //catch any unhandled exceptions so the user understands
			request.setAttribute("report", e.toString());
		}
		
		//add these two parameters as attributes to the request to foward it to the submission page
		request.setAttribute("connections-data", data.toString());
		request.setAttribute("blockly-blocks", blocks.toString());
		RequestDispatcher reqdis = request.getRequestDispatcher("/submitted.jsp");
		reqdis.forward(request, response); //forward the req and res to the jsp
	}

}
