package validationTool;

import java.io.File;
import java.util.Observable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import resources.Configuration;
import resources.XMLUtilities;
import resources.XMLValidator;

/**
 * A Composite that contains controls to load and launch a migration sequence
 *  
 * @author Mike Della Donna (mpdella@us.ibm.com)
 *
 */
public class Migrate extends Observable {
	
	private TableItem[] items;
	private Table table;
	private Label chkLabel;
	private Label xsdLabel;
	private Label xslLabel;
	private Label refLabel;
	private ProgressBar progressBar;
	private Text text;
	private Composite composite;
	private Shell shell;
	private Label lblMigrationXml;
	private Label label;
	private Label label_1;
	private Label label_2;
	private Text transformedFile;
	
	public Migrate(TabFolder tabFolder, Shell shell) {
		composite = new Composite(tabFolder, SWT.NONE);
		this.shell = shell;
	}
	
	public static void main(String args[])
	{
		Display display = Display.getDefault();
		final Shell shell = new Shell (display);
		shell.setSize(900, 7);
		final TabFolder tabFolder = new TabFolder (shell, SWT.BORDER);
		
		tabFolder.setLocation (10, 0);
		
		TabItem singleValidation = new TabItem(tabFolder, SWT.NONE);
		singleValidation.setText("Migrate");
		Migrate migrate = new Migrate(tabFolder, shell);
		singleValidation.setControl(migrate.createContents());		
		
		tabFolder.pack ();
		shell.pack ();
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
	
	/**
	 * creates the controls for the composite
	 * @return a composite consisting of controls for configuring and launching the process
	 */
	public Composite createContents()
	{
		Label lblFileLocation = new Label(composite, SWT.NONE);
		lblFileLocation.setBounds(30, 160, 75, 15);
		lblFileLocation.setText("File Location:");
		
		lblMigrationXml = new Label(composite, SWT.NONE);
		lblMigrationXml.setBounds(30, 39, 86, 15);
		lblMigrationXml.setText("Migration XML");
		
		refLabel = new Label(composite, SWT.NONE);
		refLabel.setBounds(203, 70, 817, 15);
		refLabel.setText("C:\\XML_FILE_TO_ACT_AS_A_REFERENCE");
		
		Button refFileBtn = new Button(composite, SWT.NONE);
		refFileBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell);
				String platform = SWT.getPlatform();
				dialog.setFilterPath (platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");
				dialog.setFilterExtensions(new String[]{"*.xml"});
				String path = dialog.open();
					
				if(path != null)
				{
					refLabel.setText(path);
					setChanged();
					notifyObservers("ref");
				}
			}
		});
		refFileBtn.setText("Browse");
		refFileBtn.setBounds(122, 65, 75, 25);
		refFileBtn.setToolTipText("Select the XML file that the migrating XML file will be validated against.");
		
		label = new Label(composite, SWT.NONE);
		label.setText("Reference XML");
		label.setBounds(30, 70, 86, 15);
		
		chkLabel = new Label(composite, SWT.NONE);
		chkLabel.setBounds(203, 39, 817, 15);
		chkLabel.setText("C:\\XML_FILE_TO_MIGRATE");

		Button chkFileBtn = new Button(composite, SWT.NONE);
		chkFileBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell);
				String platform = SWT.getPlatform();
				dialog.setFilterPath (platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");
				dialog.setFilterExtensions(new String[]{"*.xml"});
				String path = dialog.open();
					
				if(path != null)
				{
					chkLabel.setText(path);
					setChanged();
					notifyObservers("chk");
				}
			}
		});
		chkFileBtn.setBounds(122, 34, 75, 25);
		chkFileBtn.setText("Browse");
		chkFileBtn.setToolTipText("Select the migrating XML file to be transformed.");
		
		label_1 = new Label(composite, SWT.NONE);
		label_1.setText("Stylesheet");
		label_1.setBounds(30, 101, 86, 15);
		
		xsdLabel = new Label(composite, SWT.NONE);
		xsdLabel.setBounds(203, 132, 817, 15);
		xsdLabel.setText("C:\\XSD_FILE");

		Button xsdBtn = new Button(composite, SWT.NONE);
		xsdBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell);
				String platform = SWT.getPlatform();
				dialog.setFilterPath (platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");
				dialog.setFilterExtensions(new String[]{"*.xsd"});
				String path = dialog.open();
					
				if(path != null)
				{
					xsdLabel.setText(path);
				}
			}
		});
		xsdBtn.setBounds(122, 127, 75, 25);
		xsdBtn.setText("Browse");
		xsdBtn.setToolTipText("Select the XSD file you want to validate the migration file with.");
		
		label_2 = new Label(composite, SWT.NONE);
		label_2.setText("Schema");
		label_2.setBounds(30, 132, 86, 15);
		
		xslLabel = new Label(composite, SWT.NONE);
		xslLabel.setBounds(203, 101, 817, 15);
		xslLabel.setText("C:\\XSL_FILE");
		
		Button xslBtn = new Button(composite, SWT.NONE);
		xslBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell);
				String platform = SWT.getPlatform();
				dialog.setFilterPath (platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");
				dialog.setFilterExtensions(new String[]{"*.xsl", "*.xslt", "*.xlt"});
				String path = dialog.open();
					
				if(path != null)
				{
					xslLabel.setText(path);
				}
			}
		});
		xslBtn.setBounds(122, 96, 75, 25);
		xslBtn.setText("Browse");
		xslBtn.setToolTipText("Select the XSL file you want to apply to the migrating XML file.");
		
		table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		table.setLinesVisible(true);
		table.setBounds(30, 256, 422, 137);
		table.setHeaderVisible(false);
		

		TableColumn col1 = new TableColumn(table, SWT.NONE);
		col1.setWidth(250);
	
		TableColumn col2 = new TableColumn(table, SWT.NONE);
		col2.setWidth(167);
		
		
		
		items = new TableItem[7];
		
		items[0] = new TableItem(table, SWT.NONE);
		items[0].setText(0, "XML Check File Loaded");
		
		items[1] = new TableItem(table, SWT.NONE);
		items[1].setText(0, "XML Reference File Loaded");
		
		items[2] = new TableItem(table, SWT.NONE);
		items[2].setText(0, "XSL File Loaded");
		
		items[3] = new TableItem(table, SWT.NONE);
		items[3].setText(0, "XSD File Loaded");
		
		items[4] = new TableItem(table, SWT.NONE);
		items[4].setText(0, "XSL Applied");
		
		items[5] = new TableItem(table, SWT.NONE);
		items[5].setText(0, "XSD Passed");
		
		items[6] = new TableItem(table, SWT.NONE);
		items[6].setText(0, "XML Validation Passed");

		progressBar = new ProgressBar(composite, SWT.BORDER | SWT.SMOOTH);
		progressBar.setBounds(103, 225, 349, 25);
		
		Button btnValidate = new Button(composite, SWT.NONE);
		btnValidate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
			}
		});
		btnValidate.setBounds(30, 225, 67, 25);
		btnValidate.setText("Migrate");
		
		text = new Text(composite, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		text.setBounds(458, 225, 562, 290);
		
		transformedFile = new Text(composite, SWT.BORDER);
		transformedFile.setEditable(false);
		transformedFile.setBounds(122, 157, 898, 21);
		
		return composite;
	}
	
	/**
	 * changes one of the table rows to red with the word fail
	 * @param index - the index of the table row
	 */
	private void setItemRed(int index)
	{
		items[index].setText(1, "FAIL");
		items[index].setBackground(1, Display.getDefault().getSystemColor(SWT.COLOR_RED));
	}
	
	/**
	 * changes one of the table rows to green with the word pass
	 * @param index - the index of the table row
	 */
	private void setItemGreen(int index)
	{
		items[index].setText(1, "PASS");
		items[index].setBackground(1, Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
	}
	
	/**
	 * changes one of the table rows to yellow with the word warning
	 * @param index - the index of the table row
	 */
	private void setItemYellow(int index)
	{
		items[index].setText(1, "WARNING");
		items[index].setBackground(1, Display.getDefault().getSystemColor(SWT.COLOR_YELLOW));
	}
	
	
	public Label getChkLabel() {
		return chkLabel;
	}

	public Label getRefLabel() {
		return refLabel;
	}
	
	public Text getTransformedFile() {
		return transformedFile;
	}

	/**
	 * 
	 * runs through a validation sequence
	 * 
	 */
	private void validate()
	{
		// 0 is check loaded
		// 1 is ref loaded
		// 2 is xsl loaded
		// 3 is xsd loaded
		// 4 is xsl applied
		// 5 is xsd passed
		// 6 is our validation passed
		
		String chkXml = chkLabel.getText();
		String refXml = refLabel.getText();
		String xsd = xsdLabel.getText();
		String xsl = xslLabel.getText();
		String xslPath = null;
		
		int interval = progressBar.getMaximum() / 10;
		
		//this section checks to make sure all 4 files are valid
		
		progressBar.setSelection(interval * 1);
		if(new File(chkXml).isFile())
		{setItemGreen(0);}
		else
		{setItemRed(0);}
		
		progressBar.setSelection(interval * 2);
		if(new File(refXml).isFile())
		{setItemGreen(1);}
		else
		{setItemRed(1);}
		
		progressBar.setSelection(interval * 3);
		if(new File(xsl).isFile())
		{setItemGreen(2);}
		else
		{setItemRed(2);}
		
		progressBar.setSelection(interval * 4);
		if(new File(xsd).isFile())
		{setItemGreen(3);}
		else
		{setItemRed(3);}
		
		//this applies the xsl transformation
		try {
			//if one of these files can't be loaded, the stylesheet is not applied
			if((new File(chkXml).isFile()) && (new File(xsl).isFile()))
			{
				progressBar.setSelection(interval * 5);
				xslPath = XMLUtilities.applyStyleSheet(chkXml,xsl);
				progressBar.setSelection(interval * 6);
				if(new File(xslPath).isFile())
				{
					setItemGreen(4);
					transformedFile.setText(xslPath);
					setChanged();
					notifyObservers("mig");
				}
				else
				{setItemRed(4);}
			}
			else
			{
				transformedFile.setText("Stylesheet not applied");
				setItemYellow(4);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			setItemRed(4);
		}
		
		
		text.setText("");

		try {
			progressBar.setSelection(interval * 7);
			//if the XSD is specified, apply it
			if(new File(xsd).isFile())
			{
				try{
					if(new File(xslPath).isFile())
						text.setText(XMLUtilities.validateXSD(xslPath, xsd));
				}
				catch (Exception e)
				{
					//if the transformation didn't happen, run the schema validation on the 
					//specified check file
					//if the schema validation doesn't happen, xslpath will be null
					//so the error will get caught and the program will flow here 
					text.setText(XMLUtilities.validateXSD(chkXml, xsd));
				}
				
				//if there is no output from the test, it passed
				if(text.getText().equalsIgnoreCase(""))
				{
					setItemGreen(5);
					progressBar.setSelection(interval * 8);
				}
				else
				{
					setItemRed(5);
				}
			}
			else
			{
				setItemYellow(5);
			}
		} catch (Exception e) {
			text.setText("XSD: "+e.getLocalizedMessage());
			setItemRed(5);
		}
			
		
		//run the XML difference validation
		try {
			if((new File(chkXml).isFile()) && (new File(refXml).isFile()))
			{
				Configuration config;
				try
				{
					if(new File(xslPath).isFile())
					{
						config = new Configuration(xslPath, refXml, "string", "short", "");
					}
					else
					{
						config = new Configuration(chkXml, refXml, "string", "short", "");
					}
				}
				catch (Exception e)
				{
					config = new Configuration(chkXml, refXml, "string", "short", "");
				}
				
				
				progressBar.setSelection(interval * 9);
				
				XMLValidator validate = XMLValidator.configure(config);
				
				String state = validate.validateXML();
				
				progressBar.setSelection(interval * 10);
				
				text.setText(text.getText()+"\n"+state);
				
				//check to see if there was any output, ie, if anything went wrong
				//the replaceAll reomves spaces, newlines, anything specified by the regex \s
				if(state.replaceAll("\\s", "").equalsIgnoreCase(""))
				{setItemGreen(6);}
				else
				{setItemYellow(6);}
			}
			else
			{   progressBar.setSelection(interval * 10);
				setItemYellow(6);}
			
		} catch (Exception e) {
			e.printStackTrace();
			progressBar.setSelection(interval * 10);
			setItemRed(6);
		}
		
		
		
	}
}
