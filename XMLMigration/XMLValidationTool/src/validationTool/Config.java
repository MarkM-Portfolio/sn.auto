package validationTool;

import org.eclipse.swt.widgets.Composite;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import resources.Configuration;
/**
 * tools to create configuration files for migration
 * @author Mike Della Donna	(mpdella@us.ibm.com)
 *
 */
public class Config{

	//controls
	private Text txtFileNameConfig;
	private Text txtFileNameLog;
	private Combo cmbRefFile;
	private List listChkFile;
	private Button btnChkFile;
	private Button btnRefFile;
	private Label lblDirConfig;
	private Button btnDirLog;
	private Label lblDirLog;
	private Label lblChooseFilesTo;
	private Combo combo;
	private Button btnCreate;
	private Shell shell;
	private Text txtCexamplexsd;
	private Text txtCexamplexsl;
	private Button btnChooseASchema;
	private Button btnDoNotValidate;
	private Button btnChooseATransformation;
	private Button btnDoNotApply;


	//size constants
	private final int BUTTONX = 60;
	private final int BUTTONY = 25;
	private final int LABELY = 15;
	private final int TEXTY = 20;
	private final int LISTY = 200;

	private final int CONFIGRECX = 1000;
	private final int REFRECX = 1000;
	private final int OUTPUTRECX = 1000;
	private final int CHECKRECX = 500;
	private final int MULTIRECX = 500;

	private final int CONTROLSPACER = 5;


	//maps
	private Map<String, ArrayList<String>> refChkMap; //maps references to migration files
	private Map<String, String> refXsl; //maps references to xsl files
	private Map<String, String> refXsd; //maps refernces to xsd files
	private Map<String, Boolean> refSchema; //maps refernce to ability to validate
	private Map<String, Boolean> refStyle; // maps refernce to ability to transform

	//parent
	private Composite parent;

	public static void main(String args[])
	{
		Display display = Display.getDefault();
		final Shell shell = new Shell (display);
		shell.setSize(900, 7);
		final TabFolder tabFolder = new TabFolder (shell, SWT.BORDER);

		tabFolder.setLocation (10, 0);

		TabItem singleValidation = new TabItem(tabFolder, SWT.NONE);
		singleValidation.setText("Migrate");
		Config validate = new Config(tabFolder, shell);
		singleValidation.setControl(validate.createContents());		

		tabFolder.pack ();
		shell.pack ();
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Config(TabFolder tabFolder, Shell shell) {
		this.shell = shell;
		parent = new Composite(tabFolder, SWT.NONE);
		refChkMap = new HashMap<String, ArrayList<String>>();
		refXsl		= new   HashMap<String, String> ();
		refXsd      = new   HashMap<String, String> ();
		refSchema   = new   HashMap<String, Boolean>();
		refStyle    = new   HashMap<String, Boolean>();
	}

	public Composite createContents()
	{

		Rectangle clientArea = parent.getClientArea();
		int LOCX = clientArea.x;
		int LOCY = clientArea.y;







		//Config file location selection group
		Rectangle configFileRectangle = new Rectangle(LOCX, LOCY, CONFIGRECX, 0);

		//Label that says choose location at the top
		Label lblLoc = new Label(parent, SWT.NONE);
		lblLoc.setText("Choose a location to save this config file");
		lblLoc.setToolTipText("This button and text box allow you to specify \nthe location you want the configuration file to appear in.");
		lblLoc.setBounds(
				configFileRectangle.x+CONTROLSPACER, 
				configFileRectangle.y+CONTROLSPACER, 
				configFileRectangle.width, 	
				LABELY);

		//button to choose a directory for storing the config file
		Button btnDirConfig = new Button(parent, SWT.NONE);
		btnDirConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shell);
				String platform = SWT.getPlatform();
				dialog.setFilterPath (platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");

				String path = dialog.open();

				System.out.println ("CONFIG_FILE_PATH=" + path);
				if(path != null)
				{
					lblDirConfig.setText(path);
				}
				else
				{
					lblDirConfig.setText("No Directory Selected");
				}
			}
		});
		btnDirConfig.setText("Directory");
		btnDirConfig.setToolTipText("This button launches a window where you can select the directory\nthat you wish to store the configuration file in.");
		btnDirConfig.setBounds(
				configFileRectangle.x,
				configFileRectangle.y+CONTROLSPACER+lblLoc.getBounds().height + 2,
				BUTTONX,
				BUTTONY);

		//label that says file name
		Label lblFileNameConfig = new Label(parent, SWT.NONE);
		lblFileNameConfig.setText("File Name:");
		lblFileNameConfig.setBounds(
				configFileRectangle.x+3,
				configFileRectangle.y + btnDirConfig.getBounds().height + lblLoc.getBounds().height + (2*CONTROLSPACER)+1,
				btnDirConfig.getBounds().width,
				LABELY);

		//label that displays the directory path
		lblDirConfig = new Label(parent, SWT.NONE);
		lblDirConfig.setText("C:\\");
		lblDirConfig.setBounds(
				configFileRectangle.x+btnDirConfig.getBounds().width+CONTROLSPACER,
				btnDirConfig.getBounds().y+4,
				configFileRectangle.width - (btnDirConfig.getBounds().width+CONTROLSPACER),
				LABELY);

		//text box for users to type the filename of the config file
		txtFileNameConfig = new Text(parent, SWT.BORDER);
		txtFileNameConfig.setText("exampleConfig.xml");
		txtFileNameConfig.setToolTipText("Enter the name you would like for\nfor the gerneated configuration file.");
		txtFileNameConfig.setBounds(
				configFileRectangle.x + CONTROLSPACER + lblFileNameConfig.getBounds().width, 
				lblFileNameConfig.getBounds().y -2,
				lblDirConfig.getBounds().width,
				TEXTY);








		//logfile control group
		Rectangle logFileRectangle = new Rectangle(LOCX, LOCY+75, OUTPUTRECX, 0);

		//info label choose a location log file
		Label lblOutputFile = new Label(parent, SWT.NONE);
		lblOutputFile.setText("Choose a location to store the log file:");
		lblOutputFile.setBounds(
				logFileRectangle.x,
				logFileRectangle.y,
				300,
				LABELY);

		//directory button for log file
		btnDirLog = new Button(parent, SWT.NONE);
		btnDirLog.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shell);
				String platform = SWT.getPlatform();
				dialog.setFilterPath (platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");

				String path = dialog.open();

				System.out.println ("LOGFILE_PATH=" + path);
				if(path != null)
				{
					lblDirLog.setText(path);
				}
				else
				{
					lblDirLog.setText("No Directory Selected");
				}
			}
		});
		btnDirLog.setText("Directory");
		btnDirLog.setToolTipText("This button allows you to set a directory for the log file");
		btnDirLog.setBounds(
				logFileRectangle.x,
				logFileRectangle.y + lblOutputFile.getBounds().height + CONTROLSPACER,
				BUTTONX,
				BUTTONY);


		//label that displays the directory
		lblDirLog = new Label(parent, SWT.NONE);
		lblDirLog.setText("C:\\");
		lblDirLog.setBounds(
				logFileRectangle.x + CONTROLSPACER + btnDirLog.getBounds().width,
				btnDirLog.getBounds().y + 5,
				logFileRectangle.width - (btnDirLog.getBounds().width+CONTROLSPACER),
				LABELY);

		//label that says file name
		Label lblFileNameLog = new Label(parent, SWT.NONE);
		lblFileNameLog.setText("File Name:");
		lblFileNameLog.setBounds(
				logFileRectangle.x+3,
				logFileRectangle.y + btnDirLog.getBounds().height + lblOutputFile.getBounds().height + (2*CONTROLSPACER)+1,
				btnDirLog.getBounds().width,
				LABELY);

		//text box to enter the file name for the log
		txtFileNameLog = new Text(parent, SWT.BORDER);
		txtFileNameLog.setText("example.log");
		txtFileNameLog.setToolTipText("Enter the name of the log file.  If the file exists, the log will\nbe appended to the end, other wise the file will be created.");
		txtFileNameLog.setBounds(
				logFileRectangle.x + CONTROLSPACER + lblFileNameLog.getBounds().width, 
				lblFileNameLog.getBounds().y -1,
				lblDirLog.getBounds().width,
				TEXTY);

		//label for the mode combo box
		Label lblMode = new Label(parent, SWT.NONE);
		lblMode.setText("Mode:");
		lblMode.setBounds(
				logFileRectangle.x+3,
				lblFileNameLog.getBounds().y +lblFileNameLog.getBounds().height +CONTROLSPACER+ CONTROLSPACER,
				lblFileNameLog.getBounds().width,
				LABELY);

		//mode combo box
		combo = new Combo(parent, SWT.READ_ONLY);
		combo.setItems(new String[] {"short", "verbose"});
		combo.select(0);
		combo.setToolTipText("This selector switches between short output and\nverbose output.  This affects the output that will be written to the log file.\nShort output is the default, and indicates that only errors will be printed to the logfile.\nVerbose output will print every node comparison made.");
		combo.setBounds(
				logFileRectangle.x + CONTROLSPACER + lblMode.getBounds().width, 
				lblMode.getBounds().y -1,
				BUTTONX+5,
				BUTTONY);






		//ref file control group
		Rectangle refFileRectangle = new Rectangle(LOCX, LOCY+175, REFRECX, 0);

		//button choose reference file
		btnRefFile = new Button(parent, SWT.NONE);
		btnRefFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell);
				String platform = SWT.getPlatform();
				dialog.setFilterPath (platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");
				dialog.setFilterExtensions(new String[]{"*.xml"});

				String path = dialog.open();

				if(path != null)
				{
					if(cmbRefFile.indexOf(path) == -1)
					{
						cmbRefFile.add(path);
						cmbRefFile.select(cmbRefFile.indexOf(path));
						refChkMap.put(path, new ArrayList<String>());
						listChkFile.removeAll();
						txtCexamplexsd.setText("");
						txtCexamplexsl.setText("");
						btnDoNotApply.setSelection(false);
						btnDoNotValidate.setSelection(false);

						System.out.println("REF_SELECTION "+cmbRefFile.getSelectionIndex());
					}
				}

				if(!cmbRefFile.isEnabled())
				{
					cmbRefFile.setEnabled(true);
					btnChkFile.setEnabled(true);
					btnDoNotApply.setEnabled(true);
					btnDoNotValidate.setEnabled(true);
					btnChooseATransformation.setEnabled(true);
					btnChooseASchema.setEnabled(true);
				}
			}
		});
		btnRefFile.setText("Browse");
		btnRefFile.setToolTipText("This button allows you to select a reference file.\nThe reference file will be used to determine if there are any errors in the files to be checked.");
		btnRefFile.setBounds(
				refFileRectangle.x,
				refFileRectangle.y,
				BUTTONX,
				BUTTONY);

		// choose reference file directions
		Label lblChooseAReference = new Label(parent, SWT.NONE);
		lblChooseAReference.setText("Choose a reference file:");
		lblChooseAReference.setBounds(
				refFileRectangle.x + CONTROLSPACER +btnRefFile.getBounds().width,
				refFileRectangle.y + 5,
				200,
				LABELY);

		cmbRefFile = new Combo(parent, SWT.READ_ONLY);
		cmbRefFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				//this is the currently selected reference file
				String refFile = cmbRefFile.getItem((cmbRefFile.getSelectionIndex()));

				String[] anArray = new String[0];//ArrayList will resize this anyway so.....
				anArray = refChkMap.get(refFile).toArray(anArray);

				if(anArray.length == 0)
				{
					listChkFile.removeAll();
				}
				else
				{
					listChkFile.setItems(anArray);
				}

				if(refStyle.containsKey(refFile))
				{
					btnDoNotApply.setSelection(refStyle.get(refFile));
				}
				else
				{
					btnDoNotApply.setSelection(false);
				}

				if(refSchema.containsKey(refFile))
				{
					btnDoNotValidate.setSelection(refSchema.get(refFile));
				}
				else
				{
					btnDoNotValidate.setSelection(false);
				}

				if(refXsd.containsKey(refFile))
				{
					txtCexamplexsd.setText(refXsd.get(refFile));
				}
				else
				{
					txtCexamplexsd.setText("");
				}

				if(refXsl.containsKey(refFile))
				{
					txtCexamplexsl.setText(refXsl.get(refFile));
				}
				else
				{
					txtCexamplexsl.setText("");
				}
			}
		});
		cmbRefFile.setEnabled(false);
		cmbRefFile.setToolTipText("This box displays the currently selected reference document.\nIf there are multiple documents, it allows you to switch between them.");
		cmbRefFile.setBounds(
				refFileRectangle.x,
				btnRefFile.getBounds().y + btnRefFile.getBounds().height + CONTROLSPACER,
				logFileRectangle.width,
				BUTTONY);




		//check file control group
		Rectangle chkFileRectangle =  new Rectangle(LOCX, LOCY+235, CHECKRECX, 0);

		//file dialog to select the check files
		btnChkFile = new Button(parent, SWT.NONE);
		btnChkFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell);
				String platform = SWT.getPlatform();
				dialog.setFilterPath (platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");
				dialog.setFilterExtensions(new String[]{"*.xml"});
				String path = dialog.open();

				if(path != null)
				{
					listChkFile.add(path);
					refChkMap.get(cmbRefFile.getItem((cmbRefFile.getSelectionIndex()))).add(path);
					System.out.println("CHK_SELECTION "+cmbRefFile.getSelectionIndex());
					String[] anArray = new String[1];//ArrayList will resize this anyway so.....
					listChkFile.setItems(refChkMap.get(cmbRefFile.getItem((cmbRefFile.getSelectionIndex()))).toArray(anArray));
				}

			}
		});
		btnChkFile.setEnabled(false);
		btnChkFile.setText("Browse");
		btnChkFile.setToolTipText("This button allows you add a file to be checked against the reference file.\nWhen you add a file, it will be displayed in the list below");
		btnChkFile.setBounds(
				chkFileRectangle.x,
				chkFileRectangle.y,
				BUTTONX,
				BUTTONY);

		//info label check file picker
		lblChooseFilesTo = new Label(parent, SWT.NONE);
		lblChooseFilesTo.setText("Choose files to migrate:");
		lblChooseFilesTo.setBounds(
				chkFileRectangle.x + CONTROLSPACER +btnChkFile.getBounds().width,
				chkFileRectangle.y + 5,
				300,
				LABELY);

		//list of check files
		listChkFile = new List(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		listChkFile.setToolTipText("This list displays all the files that will be checked against the current reference file.");
		listChkFile.setBounds(
				chkFileRectangle.x,
				btnChkFile.getBounds().y + btnChkFile.getBounds().height + CONTROLSPACER,
				chkFileRectangle.width,
				LISTY);



		new Rectangle(LOCX+CHECKRECX+CONTROLSPACER, LOCY+235, MULTIRECX, 0);



		//button for help pop up
		Button btnHelp = new Button(parent, SWT.NONE);
		btnHelp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox m = new MessageBox(shell);
				m.setMessage("This tab allows you to create a configuration document for use" +
						" with the batch validate tab.  Begin by selelcting a location to store this" +
						" configuration file.  Next, select the location of the log file." +
						" The mode selector allows you to specify between short and verbose modes.  In short mode, only errors wil be reported. In verbose mode, printout will include a record of every node inspected." +
						" Next, select the first document to act as a reference.  This should be a" +
						" document that represents the result of the transformation" +
						" Next, select a transformation style sheet, or check the box. " +
						" Then select a schema file, or check the box.  Next, select any number of files to" +
						" be transformed, and validated according to the schema, then checked against the reference " +
						"document.  Next, you can either click create, or start the cycle over again by adding another " +
						"reference document" 
						);
				m.open();
				/*
				 * old help text
				 * 
				 * "This section allows you to specify which tags can occur multiple times\n"
		    			+"In order for the program to operate correctly, it needs to know which tags can be repeated "
		    			+"and how to tell them apart.  In order to do this, they must be specified ahead of time. "
		    			+"For example, a file could have multiple \"people\" tags that are differentiated because "
		    			+"they all have a different \"username\" attribute.  In order to function properly, the "
		    			+"system needs to know about this.  If there is no way to differentiate a set of multiple"
		    			+" elements from each other, enter null in the Unique attribute box. You can also check the" +
		    			"box to attempt to generate this section automatically."
				 */
			}
		});
		btnHelp.setText("?");
		btnHelp.setToolTipText("Help");
		btnHelp.setBounds(
				906,
				235,
				94,
				25);

		txtCexamplexsd = new Text(parent, SWT.BORDER);
		txtCexamplexsd.setEditable(false);
		txtCexamplexsd.setText("C:\\example.xsd");
		txtCexamplexsd.setBounds(506, 300, 500, 21);

		btnChooseASchema = new Button(parent, SWT.NONE);
		btnChooseASchema.setEnabled(false);
		btnChooseASchema.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell);
				String platform = SWT.getPlatform();
				dialog.setFilterPath (platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");
				dialog.setFilterExtensions(new String[]{"*.xsd"});
				String path = dialog.open();

				if( path != null)
				{
					refXsd.put(cmbRefFile.getItem((cmbRefFile.getSelectionIndex())), path);
					txtCexamplexsd.setText(path);
				}
			}

		});
		btnChooseASchema.setBounds(506, 269, 141, 25);
		btnChooseASchema.setText("Choose a schema file");



		btnDoNotValidate = new Button(parent, SWT.CHECK);
		btnDoNotValidate.setEnabled(false);
		btnDoNotValidate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refSchema.put(cmbRefFile.getItem((cmbRefFile.getSelectionIndex())), btnDoNotValidate.getSelection());
			}

		});
		btnDoNotValidate.setBounds(743, 273, 188, 16);
		btnDoNotValidate.setText("Do not validate schema");



		btnChooseATransformation = new Button(parent, SWT.NONE);
		btnChooseATransformation.setEnabled(false);
		btnChooseATransformation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell);
				String platform = SWT.getPlatform();
				dialog.setFilterPath (platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");
				dialog.setFilterExtensions(new String[]{"*.xsl", "*.xslt", "*.xlt"});
				String path = dialog.open();

				if(path != null)
				{
					refXsl.put(cmbRefFile.getItem((cmbRefFile.getSelectionIndex())), path);
					txtCexamplexsl.setText(path);
				}
			}

		});
		btnChooseATransformation.setBounds(506, 327, 210, 25);
		btnChooseATransformation.setText("Choose a transformation stylesheet");



		btnDoNotApply = new Button(parent, SWT.CHECK);
		btnDoNotApply.setEnabled(false);
		btnDoNotApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refStyle.put(cmbRefFile.getItem((cmbRefFile.getSelectionIndex())), btnDoNotApply.getSelection());
			}

		});
		btnDoNotApply.setBounds(743, 331, 144, 16);
		btnDoNotApply.setText("Do not apply stylesheet");



		txtCexamplexsl = new Text(parent, SWT.BORDER);
		txtCexamplexsl.setEditable(false);
		txtCexamplexsl.setText("C:\\example.xsl");
		txtCexamplexsl.setBounds(506, 358, 499, 21);



		btnCreate = new Button(parent, SWT.NONE);
		btnCreate.setToolTipText("This button creates a configuration file based on the information entered into this form.");
		btnCreate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createConfig();
			}

		});
		btnCreate.setText("Create");
		btnCreate.setBounds(
				687,
				400,
				110,
				42);

		return parent;

	}

	/**
	 * creates a new configuration file and saves it at the location specified
	 */
	private void createConfig()
	{
		try {

			//build a new document
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			//create root
			Element config = doc.createElement("config");
			doc.appendChild(config);

			//create output element
			Element output = doc.createElement("outputType");
			output.setAttribute("length", combo.getText());
			output.setAttribute("target", "file");
			config.appendChild(output);

			//create the log file element 
			Element logFile = doc.createElement("file");
			logFile.setAttribute("target", lblDirLog.getText() + System.getProperty("file.separator") + txtFileNameLog.getText());
			config.appendChild(logFile);

			Element refEl;
			Element chkEl;
			Element multiEl;
			Element xslEl;
			Element xsdEl;
			Map<String ,String> tempmap;
			for(String ref : Arrays.asList(cmbRefFile.getItems()))
			{
				refEl = doc.createElement("reference");
				refEl.setAttribute("file", ref);
				for(String chk : refChkMap.get(ref))
				{
					chkEl = doc.createElement("checkFile");
					chkEl.setAttribute("file", chk);
					refEl.appendChild(chkEl);
				}

				Configuration con = new Configuration("", ref);
				tempmap = con.refMap;


				if(refSchema.containsKey(ref) && refSchema.get(ref))
				{/*do nothing*/}
				else
				{
					xsdEl = doc.createElement("xsd");
					xsdEl.setAttribute("file", refXsd.get(ref));
					refEl.appendChild(xsdEl);
				}



				if(refStyle.containsKey(ref) && refStyle.get(ref))
				{/*do nothing*/}
				else
				{
					xslEl = doc.createElement("xsl");
					xslEl.setAttribute("file", refXsl.get(ref));
					refEl.appendChild(xslEl);
				}

				for(String mul : tempmap.keySet())
				{
					multiEl = doc.createElement("multiple");
					multiEl.setAttribute("element", mul);
					multiEl.setAttribute("attr", tempmap.get(mul));
					refEl.appendChild(multiEl);
				}

				config.appendChild(refEl);
			}


			//set up a transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");

			//create string from xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
			String xmlString = sw.toString();

			String message ="Config file construction complete.";
			//put the config file somewhere
			String filePath = lblDirConfig.getText() + System.getProperty("file.separator") + txtFileNameConfig.getText();
			try{
				BufferedWriter file = new BufferedWriter(new FileWriter(filePath, false));
				file.write(xmlString);
				file.close();
			}catch (Exception e)
			{
				message = e.getLocalizedMessage();
			}
			MessageBox m = new MessageBox(shell);
			m.setMessage(message);
			m.open();

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
