package validationTool;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import resources.Migration;
import org.eclipse.swt.widgets.ProgressBar;
/**
 * tool to run validations specified in the config file
 * @author Mike Della Donna	(mpdella@us.ibm.com)
 *
 */
public class BatchMigrate{



	protected Text checkXML;
	protected ArrayList<Migration> validatorList = null;
	protected Label lblNoFileSelected;
	protected Shell shell;

	protected Composite parent;
	private Text outFile;

	public static void main(String args[])
	{
		Display display = Display.getDefault();
		final Shell shell = new Shell (display);
		shell.setSize(900, 7);
		final TabFolder tabFolder = new TabFolder (shell, SWT.BORDER);

		tabFolder.setLocation (10, 0);

		TabItem singleValidation = new TabItem(tabFolder, SWT.NONE);
		singleValidation.setText("BatchMigrate");
		BatchMigrate validate = new BatchMigrate(tabFolder, shell);
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
	 * Create the shell.
	 * @param display
	 */
	public BatchMigrate(TabFolder tabFolder, Shell shell) {
		parent = new Composite(tabFolder, SWT.NONE);
		this.shell = shell;
	}

	/**
	 * Create contents of the shell.
	 */
	public Composite createContents() {

		final ProgressBar progressBar = new ProgressBar(parent, SWT.NONE);
		progressBar.setBounds(10, 531, 1054, 17);

		Label lblXmlValidation = new Label(parent, SWT.NONE);
		lblXmlValidation.setBounds(10, 10, 264, 15);
		lblXmlValidation.setText("Please choose a configuration file");

		Label lblSummary = new Label(parent, SWT.NONE);
		lblSummary.setBounds(10, 69, 55, 15);
		lblSummary.setText("Summary");
		
		Label lblOutputFile = new Label(parent, SWT.NONE);
		lblOutputFile.setBounds(10, 557, 67, 15);
		lblOutputFile.setText("Output File:");
		
		outFile = new Text(parent, SWT.BORDER);
		outFile.setEditable(false);
		outFile.setBounds(83, 554, 981, 21);

		Button btnValidate = new Button(parent, SWT.NONE);
		btnValidate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell);
				String platform = SWT.getPlatform();
				dialog.setFilterPath (platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");

				String path = dialog.open();

				lblNoFileSelected.setText("");

				String string = "";

				System.out.println ("RESULT=" + path);
				if(path != null)
				{
					try {
						validatorList = Migration.createMigrations(path);
						checkXML.setText("");
						for(Migration x : validatorList)
						{
							string = string.concat(x.getXMLtoCheck()+"\n");
						}
						checkXML.setText(string);
						outFile.setText(validatorList.get(0).getOutputFile());
					} catch (SAXParseException error){
						lblNoFileSelected.setText(error.getMessage()+"");
					} catch (SAXException error2){
						lblNoFileSelected.setText(error2.getMessage()+"");
					} catch (Exception e1) {
						lblNoFileSelected.setText(e1.getMessage()+e1.toString()+e1.getLocalizedMessage()+"");
					}
				}
				else
				{
					lblNoFileSelected.setText("No File Selected");
				}
			}
		});
		btnValidate.setBounds(10, 31, 54, 25);
		btnValidate.setText("Choose");

		checkXML = new Text(parent, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		checkXML.setBounds(10, 90, 1054, 435);

		Button btnValidate_1 = new Button(parent, SWT.NONE);
		btnValidate_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(validatorList != null)
				{
					String s = "";
					int interval = progressBar.getMaximum() / validatorList.size();
					int progress = 0;
					progressBar.setSelection(interval/2);
					for(Migration mig : validatorList)
					{
						try {
							s = s.concat(mig.migrate());
							progress++;
							progressBar.setSelection(interval * progress);
						} catch (Exception e1) {
							e1.printStackTrace();
						}

					}
					progressBar.setSelection(progressBar.getMaximum());
					checkXML.setText(s);
				}
			}
		});
		btnValidate_1.setBounds(70, 31, 75, 25);
		btnValidate_1.setText("Migrate");

		lblNoFileSelected = new Label(parent, SWT.NONE);
		lblNoFileSelected.setBounds(153, 36, 757, 15);


		return parent;

	}
}
