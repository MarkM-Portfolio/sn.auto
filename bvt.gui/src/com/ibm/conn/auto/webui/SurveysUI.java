package com.ibm.conn.auto.webui;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.appobjects.base.BaseSurvey;
import com.ibm.conn.auto.appobjects.base.BaseSurveyQuestion;

public class SurveysUI extends ICBaseUI {

	public SurveysUI(RCLocationExecutor driver) {
		super(driver);
	}

	private static Logger log = LoggerFactory.getLogger(SurveysUI.class);

	/** Start of selectors section */
	// Survey Creation
	public static String createSurveyButton = "css=a[title='Create Survey'][role='button']";
	public static String createSurveyNameField = "css=input[id$='createViewSection_surveyName']";
	public static String createSurveyContinueButton = "//input[@value='Continue']";
	public static String surveyCreatedText = "Add one or more questions to your survey, then click Start to start collecting responses";
	public static String addSurveyQuestionButton = "css=button[title='Add Question']";
	public static String saveSurveyButton = "css=button[id$='FULLPAGE_designerSection_action_save']";
	public static String startSurveyButton = "css=button[id$='FULLPAGE_designerSection_action_publish']";
	public static String startSurveyEndDateField = "css=input[id$=FULLPAGE_listSection_listWidget_rootDiv_dialog_start_dateTextBox]";
	public static String startSurveyEndDate= "css=div[id$='start_dateTextBox'] input[type='hidden']";
	public static String DatePicker_Surveys_InputField = "css=input[class='dijitReset dijitInputField dijitArrowButtonInner']";
	public static String startSurveyEndDateTextField = "css=input[id$=FULLPAGE_listSection_listWidget_rootDiv_dialog_start_dateTextBox]";
	public static String startSurveyConfirmButton = "css=input.lotusFormButton.submit[value='Start']";
	public static String addSurveyPopUp = "css=div[class^='lconn-survey_dialog']";
	public static String addSurveyPopUpHeader = "css=div[class^='lconn-survey_dialog'] h1 span[dojoattachpoint='titleNode']";
	public static String addSurveyPopUpText = "css=div[class^='lconn-survey_dialog'] div[class='lotusDialogContent'] table td[class='dialogField']";
	public static String applicationAddedText = "css=div[class='app_palette_modal_header'] span[data-dojo-attach-point='itemAddedNode'] p";
	public static String SurveysAdded = "css=span[title='Surveys added']";
	public static String FeatureSurveysAdded = "css=span[title='Featured Survey added']";
	public static String surveysPage = "css=div[id^='com_ibm_form_integrations_formiwidget_widget_SurveysFullpageMode']";
	public static String surveysPageHeader = "css=div[class='lotusHeader'] h1:contains('Surveys')";
	public static String createSurvey = "css=a:contains('Create Survey')";
	public static String importSurvey = "css=a:contains('Import Survey')";
	public static String surveyText1 = "//h1[text()='Surveys']//following-sibling::div";
	public static String surveyText2 = "css=div[id$='emptyListMsg']";
	public static String text1="Use surveys to share community opinions and feedback.";
	public static String text2="There are no surveys currently available for this community.";
	public static String createSurveysHeader = "css=form[name='surveysCreateForm'] td[class='lotusFormTitle'] h2";
	public static String createSurveysHeaderText="Create Survey";
	public static String createSurveyDescField = "css=textarea[id$='createViewSection_surveyDesc']";
	public static String createSurveyNameLabel = "css=label[for$='createViewSection_surveyName']";
	public static String createSurveyDescLabel = "css=label[for$='createViewSection_surveyDesc']";
	public static String moreAction = "css=button[id$='action_more']";
	public static String surveyWidgetActionMenu = "//span[text()='Surveys']//preceding-sibling::a";
	public static String featuredSurveyWidgetActionMenu = "//span[text()='Featured Survey']//preceding-sibling::a";
	public static String surveyDeleteOption = "css=div[id*='widgetActionsMenu'] table[id*='moreActions'] tr td:contains('Delete')";
	public static String featuredSurveyRemoveOption = "css=div[id*='widgetActionsMenu'] table[id*='moreActions'] tr td:contains('Remove')";
	public static String featuredSurveyRemoveDialogueBox = "css=div[id*='dijit_Dialog'] div[class='dijitDialogPaneContent']";
	public static String featuredSurveyRemoveDialogueBoxHeader = "css=div[id*='dijit_Dialog'] div[class='dijitDialogPaneContent'] h1 span";
	public static String featuredSurveyRemoveDialogueBoxOK = "css=div[id*='dijit_Dialog'] div[class='dijitDialogPaneContent'] input[value='OK']";
	public static String featuredSurveyRemoveDialogueBoxCancel = "css=div[id*='dijit_Dialog'] div[class='dijitDialogPaneContent'] input[value='Cancel']";
	public static String featuredSurveyRemoveDialogueText = "css=div[class='lotusDialogContent']";
	
	public static String deleteConfirmWidgetDialogueBox = "css=div[id*='deleteConfirmWidget_'][class*='lotusDialogWrapper']";
	public static String deleteConfirmWidgetOKButton = "css=button:contains('OK')";
	public static String deleteConfirmWidgetCancelButton = "css=button:contains('Cancel')";
	public static String confirmApplicationNameInputField = "css=#communityInputField";
	public static String signWithYourNameField = "css=#signature";
	public static String featuredSurveysInactiveWarningText = "css=div[dojoattachpoint='inactiveWarningNode']";
	public static String featuredSurveysInactiveDeleteText = "css=div[dojoattachpoint='inactiveDeleteNode']";

	// Adding Questions
	public static String addQuestionIFrame = "css=iframe[id$='FULLPAGE_designerSection_myFrame']";
	public static String addQuestionDialogueBox = "css=div[class='question-creator']";
	public static String addQuestionNameField = "css=input[id$='questionTextBox']";
	public static String addQuestionNameLabel = "css=label[for$='questionTextBox']";
	public static String addQuestionTypeDropdown = "css=select[class='question-creator-type-select']";
	public static String addQuestionTypeDropdownLabel = "css=label[id$='questionTypeSelect']";
	public static String addQuestionAddOption = "css=td.optionActionsCell.trailingCell > a.addIcon";
	public static String addQuestionOptionTextField = "css=input[id=dijit_form_TextBox_INDEX]";
	public static String addQuestionOKButton = "css=#dijit_form_Button_7";
	public static String addQuestionCancelButton = "css=#dijit_form_Button_8";
	public static String addQuestionDisplayItemTab = "css=span:contains('Display Item')";
	public static String addQuestionOptionSection = "css=div[dojoattachpoint='optionsDiv']";
	public static String addQuestionSecondOptionRow = "css=#freedom_widget_ide_form_properties_Option_1";
	public static String option1DisplayedBox = "css=#dijit_form_TextBox_0";
	public static String option1SavedBox= "css=#dijit_form_TextBox_1";
	public static String option2DisplayedBox = "css=#dijit_form_TextBox_2";
	public static String option2SavedBox = "css=#dijit_form_TextBox_3";
	public static String surveyWarningMessage = "css=div[dojoattachpoint='designerWarningMsg']";
	public static String addedSurveyQueView = "css=table[class$='top-level'] tr[class='grid-layout-row']  td[class*='selected']";
	public static String surveyQuestion= "css=fieldset[dojoattachpoint='topLabelContainer'] div[class='title']";
	public static String surveyQueOptions= "css=div[dojoattachpoint='widgetWrapperNodeTop'] div[id^='freedom_widget_ide_form_properties_Options'] table tr[id*='Option']";
	public static String startSurveyPopUp = "css=div[id='dijit_Dialog_2'] form";
	public static String startSurveyPopUpHeader = "css=div[id='dijit_Dialog_2'] form h1 span";
	public static String msgOnstartSurveyPopUp = "css=div[id='dijit_Dialog_2'] form div[class='lotusDialogContent'] table td[class='dialogField']";
	public static String startSurveyPopUpEndDateLabel = "css=div[id='dijit_Dialog_2'] form div.lotusDialogContent div[dojoattachpoint='containerNode'] label";
	public static String startSurveyPopUpEndDateField= "css=div[id='dijit_Dialog_2'] form div.lotusDialogContent div[dojoattachpoint='containerNode'] div[id$='start_dateTextBox']";
	public static String startSurveyPopUpSaveChekbox= "css=div[id='dijit_Dialog_2'] form div.lotusDialogContent div[dojoattachpoint='containerNode'] input[dojoattachpoint='saveAsAnonymousOptionNode']";
	public static String startSurveyPopUpCancelButton= "css=div[id='dijit_Dialog_2'] form div.lotusDialogFooter input[value='Cancel']";
	
	public static String quetionText(String queName) {
		return "//h1[text()='"+queName+"']//following-sibling::div//child::ul//li";
	}
	
	public static String getSurveyEndDate(String surveyName) {
		return "//a[text()='"+surveyName+"']//ancestor::h4//following-sibling::div//li[contains(text(),'Ends on')]";
	}
	
	
	// Survey Taking
	public static String surveyInListSpecific = "css=a[id*='FULLPAGE_listSection_listWidget_title']:contains(SURVEYNAME)";
	public static String surveyInList = "css=a[id*='FULLPAGE_listSection_listWidget_title']";
	public static String moreLink = "link=More";
	public static String hideLink = "link=Hide";
	public static String closeSurveyButton = "css=input[id*='FULLPAGE_singleViewSection_close']";
	public static String takeSurveyIFrame = "css=iframe[id$='FULLPAGE_singleViewSection_iFrame']";
	public static String surveySubmitButton = "//li[@id='freedom_widget_solution_environment_ActionButton_0']//span[text()='Submit']";
	public static String viewResultsiFrame = "css=iframe[id*='FULLPAGE_singleViewSection_iFrame']";
	public static String responsesTabLink = "css=a[id='responses_tab_F_Form1']";
	public static String checkBoxChecked = "[aria-checked='true']";

	// Featured Survey
	public static String selectSurveyForFeaturedLink = "css=a[dojoattachevent$='onSelectSurvey']";
	public static String displaySelectedSurveyAsFeatured = "//input[@value='Display Survey']";
	public static String featuredSurveyiFrame = "css=iframe[title='Featured Survey']";
	public static String featuredSurveyQuestionTitle = "css=h2[id='F_SelectOne_Title']";

	public static String stopSurveyButton = "css=button[id*=FULLPAGE_designerSection_action_stop]";
	public static String confirmStopSurveyButton = "css=INPUT.lotusFormButton.submit[value='Stop']";

	public static String cancelSurveyButton = "css=button[id*='FULLPAGE_designerSection_action_cancel']";

	// Misc

	public static String surveySuccessImg = "css=img.lotusIcon.lotusIconMsgSuccess";
	public static String confimCopySurveyButton = "//input[@value='Copy']";
	public static String copySurveyNameField = "css=input[id*='FULLPAGE_listSection_listWidget_rootDiv_duplicate_dialog_appNameField']";
	public static String copySurveyDialog = "css=label[for*='FULLPAGE_listSection_listWidget_rootDiv_duplicate_dialog_appNameField']";
	public static String confirmDeleteSurveyButton = "//input[@value='Delete']";
	public static String confirmExportSurveyButton = "//input[@value='Export']";
	public static String cancelSurveyDialogButton = "css=input[class='lotusFormButton cancel']";
	public static String cancelButtonSurveyPage = "css=input[class='lotusBtn'][value='Cancel']";

	// Take Survey

	public static String summaryTabLink = "css=a[id='summary_tab_F_Form1']";
	public static String thankyouforparticipatingMsg = "css=ul[class='lotusActions lotusInlinelist'] li[id*='message']";
	public static String surveyTitle = "css=a[class='entry-title']";
	public static String surveyTitleOnCommOverview = "css=a[href*='titleAction']";
	public static String thankyouMsgforMemberofComm = "xpath=//div[contains(text(),'Thank you for participating.')]";
	public static String surveysSummaryWidget = "//span[contains(text(),'Surveys')]//ancestor::h2//parent::div";
	public static String featuredSummarySurveyWidget = "//span[contains(text(),'Featured Survey')]//ancestor::h2//parent::div";
	public static String createSurveyFromWidget = "xpath=//a[@class='lotusAction'][contains(text(),'Create a Survey')]";

	// Copy Survey

	public static String viewAllLink = "xpath=//td[@class='lotusFirstCell lotusNowrap']//a[@class='lotusAction lconnFontNormalNarrow'][contains(text(),'View All')]";
	public static String overviewLink = "xpath=//a[contains(text(),'Overview')]";
	public static String cancelCopySurveyButton = "xpath=//input[@class='lotusFormButton cancel']";

	// Delete Survey
	public static String cancelButton = "xpath= //input[@class='lotusFormButton cancel']";
	public static String deleteSurveyButton = "xpath=//input[@class='lotusFormButton submit']";
	public static String noSurveyMsg = "xpath=//td[contains(text(),'There are no surveys currently available for this')]";
	public static String noSurveyAvlText = "There are no surveys currently available for this community.";
	public static String noSurveyMsgFeaturedSurvey = "xpath= //a[contains(text(),'Select a Survey to Display.')]";
	public static String noSurveyTxtFeaturedSurvey = "Select a Survey to Display.";

	// Survey responses
	
	public static String summaryTab = "xpath=//a[contains(@id,'summary_tab')]/..";
	public static String responsesTab = "xpath=//a[contains(@id,'responses_tab')]/..";
	public static String customizeTab = "//div[contains(@class,'ViewResponses')]//span[text()='Customize']";
	public static String searchTab = "//div[contains(@class,'ViewResponses')]//span[text()='Search']";		
	public static String searchButton = "//ol[@role='toolbar']//li//span[contains(@id,'form_Button')][text()='Search']";
	public static String refreshButton = "//ol[@role='toolbar']//li//span[contains(@id,'form_Button')][text()='Refresh']";
	public static String viewAs = "xpath=//ul[@class='chartHorizontalNav']//li[1]";
	public static String pieChartLink = "css=ul[class='chartHorizontalNav'] a:contains(Pie Chart)";
	public static String barChartLink = "css=ul[class='chartHorizontalNav'] a:contains(Bar Chart)";
	public static String dataTableLink = "css=ul[class='chartHorizontalNav'] a:contains(Data Table)";
	public static String pieChartView = "css=div[class*='pieChartNode'] g circle";
	public static String barChartView = "css=div[dojoattachpoint='barViewNode']";
	public static String dataTableView = "css=div[dojoattachpoint='tableViewNode']";

	public static String searchDialogue = "css=div[id$='AdvancedSearchDialog_0_dialog']";
	public static String selectItemDropdowns = "//div[contains(@id,'environment_search_AdvancedSearchConditions')]//span[text()='Select Item']";
	public static String chooseOperator = "//div[contains(@id,'environment_search_AdvancedSearchConditions')]//span[text()='Choose Operator']";
	public static String searchInputBox = "css=div[id*='environment_search_AdvancedSearchConditions'] input[type='text']";
	public static String addIcon = "css=div[id*='environment_search_AdvancedSearchConditions'] a[class^='addIcon']";
	public static String addedFilterRows = "css=div[id*='search_AdvancedSearchConditionsWrapperContainer'] div[id*='tr']";
	public static String removeIcon = "css=div[id*='environment_search_AdvancedSearchConditions'] a[class^='removeIcon']";
	public static String radioButtonAnd = "//input[@aria-disabled='true']/..[contains(@class,'dijitRadio')]//following-sibling::label[contains(@for,'and')]";
	public static String radioButtonOr = "//input[@aria-disabled='true']/..[contains(@class,'dijitRadio')]//following-sibling::label[contains(@for,'or')]";
	public static String advSearchButton = "css=span[title='Click to Search']";
	public static String advSearchCancelButton = "//div[contains(@id,'freedom_widget_solution_environment_search_AdvancedSearchDialog')]//span[@title='Cancel']";

	public static String filtersEnabled = "css=div[id$='summary-view'] li.lotusWarning strong:contains('Filters Enabled')";
	public static String clearFilters = "css=div[id$='summary-view'] li.lotusWarning a:contains('Clear Filters')";
	public static String questionText = "css=h2[id$='SelectOne_Title']";
	public static String chartInfo = "css=div[dojoattachpoint='pieViewNode'] span[class='chartInfo']";

	public static String selectItemDropDownOptions = "css=div[id^='popup_'] table tr[id^='dijit_MenuItem'] td[class$='dijitMenuItemLabel']";
	public static String customizeDataTable = "css=span[title='Customize data table']";
	public static String refreshData = "css=span[title='Refresh Data']";
	public static String exportData = "css=span[title='Export Data']";
	public static String resposesTabView = "css=div[dojoattachpoint='filterArea']";

	public static String responsesOverviewView = "css=div#F_Form1-overview-view";
	public static String author = "css=div[class='dojoxGridMasterHeader'] table tr th[class*='authorCell'] div span";
	public static String lastUpdated = "css=div[class='dojoxGridMasterHeader'] table tr th[class*='lastUpdatedCell'] div span";
	public static String id = "css=div[class='dojoxGridMasterHeader'] table tr th[class*='idCell firstMetaCell'] div span";
	public static String question = "css=div[class='dojoxGridMasterHeader'] table tr th[class*='lastFieldCell'] div span";

	public static String idValue = "css=div[class='dojoxGridMasterView'] table tr td[class*='idCell firstMetaCell'] span";
	public static String authorName = "css=div[class='dojoxGridMasterView'] table tr td[class*='authorCell'] span a";
	public static String lastUpdatedValue = "css=div[class='dojoxGridMasterView'] table tr td[class*='lastUpdatedCell'] span";
	public static String response = "css=div[class='dojoxGridMasterView'] table tr td[class*='lastFieldCell'] span";
	public static String viewPropertiesDialogueBox = "css=div[id$='ViewDataSessionPropertyDialog_0_dialog']";
	public static String SubmissionSpecific = "css=h4:contains('Submission-specific column options:')";
	public static String showAllButton = "css=span[class*='lotusFormButtonFocused'] span:contains('Show All')";
	public static String hidewAllButton = "//span[contains(@class,'lotusFormButton')]//span[text()='Hide All']";
	public static String formSpecificColumnOption = "css=h4:contains('Form-specific column options:')";
	public static String applicationSpecificOption = "css=h4:contains('Application viewing options:')";
	
	public static String showIDCheck = "//label[text()='Show ID']/..";
	public static String showIDCheckBox = "css=input[id$='showId']";
	public static String showStageCheck = "//label[text()='Show Stage']/..";
	public static String showAuthorCheck = "//label[text()='Show Author']/..";
	public static String showLastUpdatedCheck = "//label[text()='Show Last Updated']/..";
	
	public static String questionOnViewPropertiexBox = "css=div[class*='dijitDialogFixed'] label[title='Question A']";
	public static String viewFormInRightPane = "//label[text()='View form in the right pane']";
	public static String viewFormInNewDialog = "//label[text()='View form in a new dialog']";
	public static String viewPropertiesOKButton = "//div[contains(@class,'viewDataSessionDialog ')]//span[text()='OK']";
	public static String viewPropertiesApplyButton = "//div[contains(@class,'viewDataSessionDialog ')]//span[text()='Apply']";
	public static String viewPropertiesCancelButton = "//div[contains(@class,'viewDataSessionDialog ')]//span[text()='Cancel']";
	
	public static String exportDialogBox = "css=#export-dialog_dialog";
	public static String exportResponsesFormatText = "//div//p[text()='Export responses in one of the following formats:']";
	public static String exportDialogXMLRadioLabel = "css=label[for$='xml']";
	public static String exportDialogXMLRadio = "css=#export-dialog_exportType_xml";
	public static String exportDialogexcelRadioLabel = "css=label[for$='excel']";
	public static String exportDialogOpenDocRadioLabel = "css=label[for$='symphony']";
	public static String exportDialogBoxNote ="//div//p[text()='Note: If there are search filters active, then only matching responses will be exported.']";
	public static String exportDialogExportButton ="//div[contains(@class,'lotusDialogFooter ')]//span[text()='Export']";
	public static String exportDialogCancelButton ="//div[@id='export-dialog_dialog']//span[text()='Cancel']";

	// Stop Surveys
	public static String cancelStopSurveyButton = "xpath= //input[@class='lotusFormButton cancel']";
	public static String stopStopSurveyButton = "xpath= //div[@id='dijit_Dialog_2']//input[@class='lotusFormButton submit']";
	public static String recentUpdatesLink = "xpath= //a[contains(text(),'Recent Updates')]";
	public static String surveyNameEE = "xpath= //html[1]/body[1]/div[1]/div[1]/div[3]/div[2]/div[1]/div[1]/h1[1]/a[1]";
	public static String surveyName2EE = "xpath= //div[@class='eeHeaderDescription']//a[contains(text(),'stopSurvey')]";
	public static String surveysLink = "xpath= //li[@widgetdefid='Surveys']";
	public static String endSurveyTitle = "css=li[id$='FULLPAGE_listSection_listWidget_message#0']";
	public static String frameEEpopUP = "xpath= //div[@class='lotusFlyoutHeader']";
	public static String closeEEpopUP = "xpath= //span[@class='lotusBtnImg lotusClose']";

	// Featured Surveys
	public static String selectSurveyTypeDropdown = "xpath=//span[@class='asFilterMenu']//select";
	public static String selectSurveyTypeDropdownvalues = "css=span[class='asFilterMenu'] option";
	public static String activeSurveyradioBtn = "xpath=//label[@class='lotusCheckbox']";
	public static String displaySurveyBtn = "xpath=//input[@class='lotusFormButton submit']";
	public static String cancelSurveyBtn = "xpath=//input[@class='lotusFormButton cancel']";
	public static String creatingSurveyLink = "xpath=//a[contains(text(),'creating')]";
	public static String cancelSurveyfromSurveys = "xpath=//span[@id='widget-container-fullpage']//input[2]";
	public static String editingDraft = "xpath=//a[contains(text(),'editing')]";
	public static String noDraftsSurveyMsg = "xpath=//div[contains(text(),'There are no draft surveys for this community.')]";
	public static String surveyQuestions = "css=input[id$='SingleLine-widget']";
	public static String submitbtn = "css=span[class$='lfFormActionBtn lfFormActionSubmitBtn dijitButton lfFormBtn lotusFormButton']";
	public static String responsetable = "css=a[dojoattachevent='ondijitclick:_showTableView']";
	public static String featruredSurveyWidget = "css=iframe[id$='VIEW_iFrame']";

	public static String authorName(String username) {
		return "css=div[class='dojoxGridMasterView'] table tr td[class*='authorCell'] span a:contains('" + username
				+ "')";
	}

	public static String selectItemOption(String optionName) {
		return "css=div[id^='popup_'] table tr[id^='dijit_MenuItem'] td[class$='dijitMenuItemLabel']:contains('"
				+ optionName + "')";
	}

	public static String chooseOperatorOption(String operator) {
		return "css=div[id^='popup_'] table tr[id^='dijit_MenuItem'] td[class$='dijitMenuItemLabel']:contains('"+operator+"')";
		
	}

	/** Enum for More menu */
	public enum MoreLink {
		TAKE_SURVEY("Take Survey"), VIEW_RESULTS("View Results"), EDIT("Edit"), COPY(
				"Copy"), EXPORT_SURVEY("Export Survey"), DELETE("Delete");

		String option = null;

		MoreLink(String option) {
			this.option = option;
		}

		public String getOption() {
			return this.option.toString();
		}

	}

	/**
	 * 
	 * <B>Prerequisite:</B> Assume you have navigated to surveys page by clicking
     * "Surveys" link in left panel of Community
     *<br><B>Description:</B> Click on the Create survey link in the Survey widget and the page navigates to create survey
     * page. Fill in Survey Name and click continue button to create survey.
     * @param survey
	 */
	public void createSurvey(BaseSurvey survey) {
		log.info("INFO: Clicking on 'Create Survey' button.");
		fluentWaitElementVisible(createSurveyButton);
		clickLinkWait(createSurveyButton);
		fluentWaitElementVisible(createSurveyNameField);
		log.info("INFO: Typing survey name into 'Name' field.");
		typeText(createSurveyNameField, survey.getName());
		log.info("INFO: Clicking 'Continue' button");
		driver.getSingleElement(createSurveyContinueButton).click();
		fluentWaitTextPresent(surveyCreatedText);
		log.info("INFO: Survey " + survey.getName() + " created.");
	}

	/**
	 * 
	 * <B>Prerequisite:</B> Assume questionnaire included after survey creation.
     * <br><B>Description:</B> Click add question button, should open Add Question dialog box, user types
     * the question in Question text box and choose the question type from drop down box.
     * Clicks Ok button to successfully add question to survey.
     * @param question
     * @throws Exception
     * @return boolean - return true if the question were added to survey successfully
	 */
	public Boolean addQuestionsByQuestionType(BaseSurveyQuestion question)
			throws Exception {
		log.info("INFO: Clicking on the add Question button.");
		fluentWaitElementVisible(addSurveyQuestionButton);
		driver.getSingleElement(addSurveyQuestionButton).click();

		log.info("INFO: Switching to Add Question iFrame");
		switchToFrame(addQuestionIFrame, addQuestionNameField);

		log.info("INFO: Typing Question in 'Question Name' field.");
		fluentWaitPresent(addQuestionNameField);
		typeText(addQuestionNameField, question.getQuestion());

		log.info("INFO: Selecting question type from dropdown");
		driver.getSingleElement(addQuestionTypeDropdown)
				.useAsDropdown()
				.selectOptionByVisibleText(
						question.getQuestionType().getOption());
		List<BaseSurveyQuestion.Option> options = question.getOptions();
		int optIndex = 0;
		for (BaseSurveyQuestion.Option opt : options) {
			if (optIndex > 0) {
				log.info("INFO: Adding Option " + opt.display);
				getFirstVisibleElement(addQuestionAddOption).click();
			}
			String questionTextField = addQuestionOptionTextField.replace(
					"INDEX", Integer.toString(optIndex * 2));
			driver.getSingleElement(questionTextField).clear();
			typeText(questionTextField, opt.display);
			optIndex++;
		}

		log.info("INFO: Clicking on OK button");
		driver.getSingleElement(addQuestionOKButton).click();

		// not seeing this success message...leaving out for now
		// CoreAutomation.Assert.assertTrue(scso.getTF_QuestionTitleField().isTextPresent(sQuestion),"Qustion was added successfully");
		switchToTopFrame();
		return true;
	}
	 /**
	  * <B>Prerequisite:</B> Assume survey questionnaire included in survey.
      * <br><B>Description:</B> Click on the survey save button to save changes to survey.
	  * @throws Exception
	  */
	public void saveSurvey() throws Exception {
		driver.executeScript("scroll(0,0);");
		fluentWaitPresent(saveSurveyButton);
		driver.getSingleElement(saveSurveyButton).click();
	}
	 /**
	  * <B>Prerequisite:</B> Assume survey is created.
	  * <br><B>Description:</B> Once survey question are included, you are all set to roll the survey.
	  * You start a survey by clicking on the start button in the same page.A dialog box opens
	  * prompting for a survey end date. You type in End date in the drop down box
	  * and click start button in the dialog.
	  * @param survey
	  * @throws Exception
	  */
	public void startSurvey(BaseSurvey survey) throws Exception {
		fluentWaitElementVisible(startSurveyButton);
		driver.getSingleElement(startSurveyButton).click();
		pickRandomDojoDate(DatePicker_Surveys_InputField, true);
		fluentWaitElementVisible(startSurveyConfirmButton);
		driver.getSingleElement(startSurveyConfirmButton).click();
		// TODO: VERIFY target survey listed in survey list with proper end
		// date.

	}

	/**
	 * <B>Prerequisite:</B> Assume Survey and survey questionnaire is created.
	 * <br><B>Description:</B> 'take survey' page should display questions,
	 * user choose between the options and clicks submit button to complete a survey.
	 * @param survey
	 * @throws Exception
	 */
	public void takeSimpleSurvey(BaseSurvey survey) throws Exception {
		fluentWaitPresent(closeSurveyButton);
		log.info("INFO: Survey has loaded successfully");
		switchToFrame(takeSurveyIFrame, surveySubmitButton);
		for (int i = 0; i < survey.getQuestions().size(); i++) {
			BaseSurveyQuestion survQuestion = survey.getQuestions().get(i);
			log.info("Clicking on the first answer: '"
					+ survQuestion.getOptions().get(0).display + "'");
			fluentWaitPresent(surveySubmitButton);
			String answer = getRadioButtonByLabel(survQuestion.getOptions()
					.get(0).display);
			clickLinkWithJavascript(answer);		
		}
		log.info("INFO: Scroll to the top of the page");
		driver.executeScript("scroll(0, 0);");
		log.info("INFO: Clicking on Survey Submit button");
		fluentWaitPresent(surveySubmitButton);
		driver.getSingleElement(surveySubmitButton).click();
		// Platform.sleep(Platform.giPause2TO);
		switchToTopFrame();
		log.info("INFO: Scroll to the top of the page");
		driver.executeScript("scroll(0, 0);");
		log.info("INFO Clicking on 'More' button and ensuring 'View Results' button is present");
		clickMoreLink(survey);
		fluentWaitPresent(getSurveyMoreOption(survey, MoreLink.VIEW_RESULTS));
		clickLink(hideLink);

	}

	/** 
	 * <B>Prerequisite:</B> Assume you have answered the survey questionnaire.Click on more link in the right end of the survey.
	 * Then click on view results below the survey
	 * <br><B>Description:</B> .In the results page, click on Response tab to ensure answer is present.
	 * To close, click close button on the right corner of the page.
	 * @param survey
	 * @throws Exception
	 * @return String 
	 */
	public Boolean viewSurveyResults(BaseSurvey survey) throws Exception {
		// TODO Enhance this method, and by extension the baseQuestion class to
		// store/use a 'correct' answer, so
		// we don't have to assume the first answer has been created.
		log.info("INFO: Switch to results iFrame");
		switchToFrame(viewResultsiFrame, responsesTabLink);
		log.info("INFO: Click on Responses Tab");
		driver.getSingleElement(responsesTabLink).click();
		for (int i = 0; i < survey.getQuestions().size(); i++) {

			String ans = survey.getQuestions().get(i).getOptions().get(0).display;
			log.info("INFO: Ensuring that answer : '" + ans + "' is present");
			fluentWaitElementVisible(getAnswerInResponseList(ans));

		}
		switchToTopFrame();
		log.info("INFO: Click on close survey button");
		driver.executeScript("scroll(0,0);");
		driver.getSingleElement(closeSurveyButton).click();
		return fluentWaitPresent(getSurveyLinkInList(survey.getName()));
	}
	
	/**
	 *<B>Prerequisite:</B> Assume survey is created successfully.
	 *<br><B>Description:</B> Locate and Click on more link in the right end of the survey.
	 * @param survey
	 */
	
	public int clickMoreLink(BaseSurvey survey) {
		int surveyPosition = 0;
		log.info("INFO: Locate the More link associated with our Survey");
		List<Element> surveys = driver.getVisibleElements(surveyInList);

		if (surveys.size() > 1) {
			for (Element survElement : surveys) {
				if (survElement.getText().contains(survey.getName())) {
					log.info("INFO: Selecting 'More' link");
					String[] ret = survElement.getAttribute("id").split("#");
					surveyPosition = Integer.parseInt(ret[1]);
					clickLinkWait(surveySpecificMore(surveyPosition));
				}
			}
		} else {
			log.info("INFO: Select more link");
			clickLinkWait(moreLink);
			surveyPosition = 0;
		}
		return surveyPosition;
	}

	public String surveySpecificMore(int postion) {
		return "css=a[id$='FULLPAGE_listSection_listWidget_rootDiv_more#"
				+ postion + "']:contains(More)";
	}

	public String getRadioButtonByLabel(String sLabel) {
		return new String("css=input[value='" + sLabel + "']");
	}

	public String getAnswerInResponseList(String resp) {
		return new String("css=span[class='dojoxGridCellContent']:contains("
				+ resp + ")");
	}

	public String getSurveyLinkInList(String surveyName) {
		return new String(
				"//a[text()='" + surveyName + "']");
	}

	public static SurveysUI getGui(String product, RCLocationExecutor driver) {

		return new SurveysUI(driver);
		// TODO Implement this when SurveysUICloud, SurveysUIOnPrem etc are
		// created
		/**
		 * if(product.toLowerCase().equals("cloud")){ return new
		 * CommunitiesUICloud(driver); } else
		 * if(product.toLowerCase().equals("onprem")) { return new
		 * CommunitiesUIOnPrem(driver); } else
		 * if(product.toLowerCase().equals("production")) { return new
		 * CommunitiesUIProduction(driver); } else
		 * if(product.toLowerCase().equals("vmodel")) { return new
		 * CommunitiesUIVmodel(driver); } else
		 * if(product.toLowerCase().equals("multi")) { return new
		 * CommunitiesUIMulti(driver); }else { throw new
		 * RuntimeException("Unknown product name: " + product); }
		 */
	}
      /**
	   * <B>Prerequisite:</B> Assume you are in overview page of Community and 'Featured survey' displayed at the right side of the page.
	   * <br><B>Description:</B> Click on the select link to display "Select Featured Survey" dialog box.
       * Selecting survey from the drop down and Featured survey should load the chosen survey.
	   * @param survey
	   * @return String - CSS location value of computed web element
	   * @throws Exception
	   */
	public boolean selectSurveyForFeaturedSurvey(BaseSurvey survey)
			throws Exception {
		log.info("INFO: Selecting Survey to display in Featured Survey widget");
		clickLinkWait(selectSurveyForFeaturedLink);
		clickLinkWait(getRadioButtonBySurveyName(survey.getName()));
		clickLink(displaySelectedSurveyAsFeatured);
		log.info("INFO: Ensure the Featured Survey widget is loaded");
		return fluentWaitPresent(featuredSurveyiFrame);

	}

	public String getRadioButtonBySurveyName(String sSurveyName) {
		return new String("//label[contains(text(),'" + sSurveyName + "')]");
	}

	/**
	 * <B>Prerequisite:</B> Assume you are in the survey you created and you clicked more link to display
     * other options for you to play around with survey. You also clicked edit link.
     * <br><B>Description:</B>Stop dialog box with stop and cancel button displayed.
     * Click on the stop button. "Your survey is closed.Click restart to
     * reactivate it" message displayed when survey are stopped.
     * @param survey
     * @throws Exception
     */
	public void stopSurvey(BaseSurvey survey) throws Exception {

		fluentWaitElementVisible(stopSurveyButton);
		clickLinkWait(stopSurveyButton);
		clickLinkWait(confirmStopSurveyButton);
		fluentWaitElementVisible(surveySuccessImg);
		fluentWaitTextPresent("Your survey is closed. Click Restart to reactivate it.");
	}
   /**
	* <B>Prerequisite:</B> Assume you are in the survey you created and you clicked more link to display
	* other options for you to play around with survey.
	* <br><B>Description:</B> Click copy link, Copy Survey dialog box open with Name text box and copy, cancel button displayed.
	* Type in surveyName suffixed with copy text.Verify: if no survey name is filled "Survey name can not
	* empty" messgage is displayed. Click copy button to make a copy of survey.
	* @param survey
	* @param surveyCopy
	* @throws Exception
	 */
	public void copySurvey(BaseSurvey survey, BaseSurvey surveyCopy)
			throws Exception {
		clickMoreLink(survey);
		driver.getSingleElement(getSurveyMoreOption(survey, MoreLink.COPY))
				.click();
		fluentWaitPresent(copySurveyNameField);
		clearText(copySurveyNameField);
		typeText(copySurveyNameField, surveyCopy.getName());
		driver.getFirstElement(copySurveyDialog).click();
		if(driver.isTextPresent("Survey name can not be empty")){
			//Adding second click for this dialog to ensure warning goes away
			driver.getFirstElement(copySurveyDialog).click();
		}
		clickLink(confimCopySurveyButton);
		
	}
	/**
	 *
	 * <B>Prerequisite:</B> Assume you are in the survey you created and you clicked more link to display
	 * other options for you to play around with survey.
	 * <br><B>Description:</B> Click delete link,Delete dialog box opens with delete and cancel button.
	 * click delete button in the dialog to delete survey.
	 * @param survey
	 * @throws Exception
	 */
	public void deleteSurvey(BaseSurvey survey) throws Exception {
		clickSurveyMoreOption(survey, MoreLink.DELETE);
		fluentWaitElementVisible(confirmDeleteSurveyButton);
		driver.getSingleElement(confirmDeleteSurveyButton).click();

	}
	
	/**
	 *
	 * <B>Prerequisite:</B> Assume you are in the survey you created and you clicked more link to display
	 * other options for you to play around with survey
	 * <br><B>Description:</B>Click export survey option then you click on confirm button
	 * on the export dialog box to export a survey.
	 * @param survey
	 * @throws Exception
	 * @return boolean - if export is successful returns 'True'
	 */
	
	public boolean exportSurvey(BaseSurvey survey) throws Exception {
		log.info("INFO Ensuring that the export survey dialog appears");
		clickSurveyMoreOption(survey, MoreLink.EXPORT_SURVEY);
		fluentWaitElementVisible(confirmExportSurveyButton);
		//should only ever be 1 visible cancel button
		driver.getVisibleElements(cancelSurveyDialogButton).get(0).click();
		clickLink(hideLink);
		return true;
	}
	
	/**
	 *
	 * <B>Prerequisite:</B> Assume survey is created successfully.
	 * <br><B>Description:</B> getSurveyMoreOption returns the selector for a particular option within the expanded entry
	 * of a specific survey in the survey list.
	 * @param survey
	 * @param option
	 * @return String - Survey option
	 */
	
	// MoreLink
	public String getSurveyMoreOption(BaseSurvey survey, MoreLink option) {
		return new String(
				"//a[text()='"
						+ survey.getName()
						+ "']/ancestor::tr[1]/following-sibling::tr[@class='lotusDetails'][1]//a[@role='button' and text()='"
						+ option.getOption() + "']");
	}
	
	/**
     *
     * <B>Prerequisite:</B> Assume survey is created successfully.
     * <br><B>Description:</B> click on more link associated with surveys and click on option displayed in the more link
     * @param survey
     * @param option
     * 
     */

	public void clickSurveyMoreOption(BaseSurvey survey, MoreLink option) {
		// Ensure we are on the SurveyList page
		fluentWaitElementVisible(getSurveyLinkInList(survey.getName()));
		log.info("INFO: Clicking 'More' link for our Survey '"
				+ survey.getName() + "' and then clicking option '"
				+ option.getOption() + "'.");
		clickMoreLink(survey);
		driver.getSingleElement(getSurveyMoreOption(survey, option)).click();
	}
	
	/**
	 *
	 * <br>Prerequisite:</br> Assume survey is created successfully.
	 * <br>Description:</br> getSurveyStoppedUpdate return the survey stop message when user choose to stop the survey.
	 * @param survey
	 * @return String - Survey stop message.
	 */
	public String getSurveyStoppedUpdate(BaseSurvey survey) {
		return new String("//span[contains(.,'stopped the survey "
				+ survey.getName() + "')]");
	}
	
	/**
	 * <B>Prerequisite:</B> Assume you have navigated to surveys page by clicking
	 * "Create a Survey" link on Survey Summary widget from Community Overview Page
	 * <br>
	 * <B>Description:</B> Click on the Create survey link in the Survey widget and
	 * the page navigates to create survey page. Fill in Survey Name and click
	 * continue button to create survey.
	 * @param survey
	 */
	public void createSurveyFromWidget(BaseSurvey survey) {
		fluentWaitElementVisible(createSurveyNameField);
		log.info("INFO: Typing survey name into 'Name' field.");
		typeText(createSurveyNameField, survey.getName());
		log.info("INFO: Clicking 'Continue' button");
		driver.getSingleElement(createSurveyContinueButton).click();
		fluentWaitTextPresent(surveyCreatedText);
		log.info("INFO: Survey " + survey.getName() + " created.");
	}
	
	/** 
	 * <B>Prerequisite:</B> Assume you have answered the survey questionnaire.Click on more link in the right end of the survey.
	 * Then click on view results below the survey
	 * <br><B>Description:</B> .In the results page, SUMMARY tab is displayed.
	 * To close, click close button on the right corner of the page.
	 * @param survey
	 * @throws Exception
	 * @return Boolean 
	 */
	public Boolean verifySummaryTab(BaseSurvey survey) throws Exception {
		// TODO Enhance this method, and by extension the baseQuestion class to
		// store/use a 'correct' answer, so
		// we don't have to assume the first answer has been created.
		log.info("INFO: Switch to results iFrame");
		switchToFrame(viewResultsiFrame, summaryTabLink);
		log.info("INFO: Verify Summary Tab is displayed on screen");
		Assert.assertTrue(driver.getSingleElement(summaryTabLink).getText().equals("SUMMARY"));
		switchToTopFrame();
		log.info("INFO: Click on close survey button");
		driver.executeScript("scroll(0,0);");
		driver.getSingleElement(closeSurveyButton).click();
		return fluentWaitPresent(getSurveyLinkInList(survey.getName()));
	}

	/**
	 * <B>Prerequisite:</B> Assume you are in the survey you created and you clicked
	 * more link to display other options for you to play around with survey. <br>
	 * <B>Description:</B> Click copy link, and then Click on 'Cancel' button from Copy Survey Dialog box
	 * @param survey
	 * @param surveyCopy
	 * @throws Exception
	 */
	public void clickCancelButtonOncopySurvey(BaseSurvey survey, BaseSurvey surveyCopy) throws Exception {
		clickMoreLink(survey);
		driver.getSingleElement(getSurveyMoreOption(survey, MoreLink.COPY)).click();
		fluentWaitPresent(copySurveyNameField);
		clearText(copySurveyNameField);
		typeText(copySurveyNameField, surveyCopy.getName());
		fluentWaitElementVisible(cancelCopySurveyButton);
		clickLink(cancelCopySurveyButton);
		

	}
	
	/**
	 *
	 * <B>Prerequisite:</B> Assume you are in the survey you created and you clicked more link to display
	 * other options for you to play around with survey
	 * <br><B>Description:</B>Click export survey option then you Click on cancel button
	 * on the export dialog box.
	 * <br><B>Description:</B>Click export survey option then you Click on confirm button
	 * on the export dialog box.
	 * @param survey
	 * @throws Exception
	 * @return boolean - if export is successful returns 'True'
	 */
	
	public boolean cancelAndExportSurvey(BaseSurvey survey) throws Exception {
		log.info("INFO Ensuring that the export survey dialog appears");
		clickSurveyMoreOption(survey, MoreLink.EXPORT_SURVEY);
		fluentWaitElementVisible(confirmExportSurveyButton);
		//should only ever be 1 visible cancel button
		log.info("INFO Clicking Cancel button on export survey dialog box");
		driver.getVisibleElements(cancelSurveyDialogButton).get(0).click();
		clickLink(hideLink);
		log.info("INFO Clicking 'Export' button on export survey dialog");
		clickSurveyMoreOption(survey, MoreLink.EXPORT_SURVEY);
		driver.getFirstElement(confirmExportSurveyButton).click();
		return true;
	}
	
	/**
    *
    * <B>Prerequisite:</B> Assume survey is created successfully.
    * <br><B>Description:</B> click on more link associated with surveys and click on option displayed in the more link
    * @param survey
    * @param option
    * 
    */

	public void deleteSurveyFromSurveys(BaseSurvey survey, MoreLink option) {
		// Ensure we are on the SurveyList page
		fluentWaitElementVisible(getSurveyLinkInList(survey.getName()));
		log.info("INFO: Clicking 'More' link for our Survey '"
				+ survey.getName() + "' and then clicking option '"
				+ option.getOption() + "'.");
		clickMoreLink(survey);
		driver.getSingleElement(getSurveyMoreOption(survey, option)).click();
		log.info("Verify the Cancel button is present at Delete dailog box");
		Assert.assertTrue(isElementVisible(cancelButton));
		driver.getSingleElement(cancelButton).click();
		driver.navigate().refresh();
		clickMoreLink(survey);
		driver.getSingleElement(getSurveyMoreOption(survey, option)).click();
		driver.getSingleElement(deleteSurveyButton).click();
		
	}
	
	/**
    *
    * <B>Prerequisite:</B> Assume survey is created successfully.
    * <br><B>Description:</B> click on more link associated with surveys and click on option displayed in the more link
    * @param survey
    * @param option
    * 
    */

	public void stopSurveyFromSurveys(BaseSurvey survey, MoreLink option) {
		// Ensure we are on the SurveyList page
		fluentWaitElementVisible(getSurveyLinkInList(survey.getName()));
		log.info("INFO: Clicking 'More' link for our Survey '" + survey.getName() + "' and then clicking option '"
				+ option.getOption() + "'.");
		clickMoreLink(survey);
		driver.getSingleElement(getSurveyMoreOption(survey, option)).click();
		log.info("Click on Stop button at Surveys page");
		fluentWaitElementVisible(stopSurveyButton);
		clickLinkWait(stopSurveyButton);
		log.info("Verify on Cancel button at stop Surveys dialog box");
		Assert.assertTrue(isElementVisible(cancelStopSurveyButton));
		clickLinkWait(cancelStopSurveyButton);
		clickLinkWait(stopSurveyButton);
		clickLinkWait(confirmStopSurveyButton);
		fluentWaitElementVisible(surveySuccessImg);
		fluentWaitTextPresent("Your survey is closed. Click Restart to reactivate it.");
		
	}
}
