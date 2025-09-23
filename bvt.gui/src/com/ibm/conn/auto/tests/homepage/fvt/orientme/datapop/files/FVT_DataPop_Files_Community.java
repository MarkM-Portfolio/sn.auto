package com.ibm.conn.auto.tests.homepage.fvt.orientme.datapop.files;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.configs.DataPopSetup;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityFileEvents;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2017                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author 	Anthony Cox
 * Date:	28th February 2017
 */

public class FVT_DataPop_Files_Community extends DataPopSetup {

	private APICommunitiesHandler communitiesAPIUser1;
	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseCommunity baseCommunity;
	private BaseFile baseStandaloneFile;
	private Community publicCommunity;
	private FileEntry publicFile;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		setFilename(getClass().getSimpleName());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		getTestCaseData().addUserAssignmentData(listOfStandardUsers);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a public community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		getTestCaseData().addCreateCommunityData(publicCommunity, baseCommunity);
		
		// User 1 will now upload a public standalone file (to be shared with the community later)
		baseStandaloneFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.addFile(baseStandaloneFile, testUser1, filesAPIUser1);
		getTestCaseData().addCreateFileData(publicFile, null, testUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_File_UploadFile() {
		
		// User 1 will now upload a file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		CommunityFileEvents.addFile(publicCommunity, baseFile, testUser1, filesAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_File_UpdateFileVersion() {
		
		// User 1 will now upload a file to the community and will update the version of that file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file3, ".jpg", ShareLevel.EVERYONE);
		BaseFile baseFileUpdateVersion = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		CommunityFileEvents.addAndUpdateFileVersion(publicCommunity, baseFile, baseFileUpdateVersion, testUser1, filesAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_File_LikeFile() {
		
		// User 1 will now upload a file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file5, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFile(publicCommunity, baseFile, testUser1, filesAPIUser1);
		
		// User 1 will now like / recommend the file
		CommunityFileEvents.likeFile(publicCommunity, communityFile, testUser1, filesAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_File_ShareFile() {
		
		// User 1 will now share a standalone file with the community
		CommunityFileEvents.shareFileWithCommunity(publicCommunity, publicFile, Role.OWNER, testUser1, filesAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_File_CommentOnFile() {
		
		// User 1 will now upload a file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file6, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFile(publicCommunity, baseFile, testUser1, filesAPIUser1);
		
		// User 1 will post a comment to the file
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addComment(publicCommunity, communityFile, user1Comment, testUser1, filesAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_File_UpdateCommentOnFile() {
		
		// User 1 will now upload a file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file7, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFile(publicCommunity, baseFile, testUser1, filesAPIUser1);
		
		// User 1 will post a comment to the file and will update the comment
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		String user1CommentEdited = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addCommentAndUpdateComment(publicCommunity, communityFile, user1Comment, user1CommentEdited, testUser1, filesAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_File_CommentOnFileWithMentions() {
		
		// User 1 will now upload a file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file8, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFile(publicCommunity, baseFile, testUser1, filesAPIUser1);
		
		// User 1 will now post a comment to the file with mentions to User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		CommunityFileEvents.addCommentWithMentions(publicCommunity, communityFile, mentions, testUser1, filesAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_File_FolderCreated() {
		
		// User 1 will now create a folder in the community
		BaseFile baseFolder = FileBaseBuilder.buildBaseFile(Helper.genStrongRand(), "", ShareLevel.EVERYONE);
		CommunityFileEvents.addFolder(publicCommunity, baseFolder, testUser1, filesAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_File_FileAddedToFolder() {
		
		// User 1 will now create a folder in the community
		BaseFile baseFolder = FileBaseBuilder.buildBaseFile(Helper.genStrongRand(), "", ShareLevel.EVERYONE);
		FileEntry communityFolder = CommunityFileEvents.addFolder(publicCommunity, baseFolder, testUser1, filesAPIUser1);
				
		// User 1 will now upload a file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file9, ".jpg", ShareLevel.EVERYONE);
		FileEntry publicFile = CommunityFileEvents.addFile(publicCommunity, baseFile, testUser1, filesAPIUser1);	
				
		// User 1 will now add the file to the community folder
		CommunityFileEvents.addFileToFolder(testUser1, filesAPIUser1, publicFile, communityFolder);
	}
}