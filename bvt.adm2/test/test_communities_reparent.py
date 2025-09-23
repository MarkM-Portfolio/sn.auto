import unittest
import sys, os
import re
import java
import math
from java.util import Date
from java.text import DateFormat
from java.util import Vector


lc_admin_dir = os.environ.get("IC_ADMIN_DIR", "bin_lc_admin")
if not lc_admin_dir in sys.path:
    sys.path.append(lc_admin_dir)
if not globals().has_key('CommunitiesConfigService'):
    execfile(os.path.join(lc_admin_dir, 'communitiesAdmin.py'))

class CommunitiesAdminReparentTest(unittest.TestCase):
    def setUp(self):
        self.members1 = [
        {'name': 'ajones101', 'mail': 'ajones101@janet.iris.com'},
        {'name': 'ajones102', 'mail': 'ajones102@janet.iris.com'}
        ]
        self.owners1 = [
        {'name': 'ajones1', 'mail': 'ajones1@janet.iris.com'},
        {'name': 'ajones2', 'mail': 'ajones2@janet.iris.com'}
        ]
        self.members2 = [
        {'name': 'ajones105', 'mail': 'ajones105@janet.iris.com'},
        {'name': 'ajones106', 'mail': 'ajones106@janet.iris.com'}
        ]
        self.owners2 = [
        {'name': 'Amy Jones5', 'mail': 'ajones5@janet.iris.com'},
        {'name': 'Amy Jones6', 'mail': 'ajones6@janet.iris.com'}
        ]

######################################################################################
## Tests for communities with same membership
######################################################################################

    def test_simple_reparent(self):

        print "################################################################"
        print "####################### test_simple_reparent ###################"
        print "################################################################"

        ## Create parent
        parent = self.createCommunity(self.owners1, self.members1)
        parentName = parent.get(0).get("name")
        ## Create community to re-parent
        child = self.createCommunity(self.owners1, self.members1)
        childName = child.get(0).get("name")

        ## Do re-parenting
        self.reParent(child, parent)

        ## Check that child has parent uuid
        child = CommunitiesService.fetchCommByName(childName)
        self.assertEqual(child.get(0).get("parentUuid"), parent.get(0).get("uuid"))

        ## Now un-parent the child and check it has no parent
        self.unParent(child)
        child = CommunitiesService.fetchCommByName(childName)
        self.assertEqual(child.get(0).containsKey("parentUuid").toString(), "0");

    ######################################################################################
    ## Tests for communities with different membership
    ######################################################################################
    def test_reparent_different_members(self):
        print "################################################################"
        print "################# test_reparent_different_members ##############"
        print "################################################################"
        ## Create parent
        parent = self.createCommunity(self.owners1, self.members1)
        parentName = parent.get(0).get("name")
        parentOwnersBeforeReparent = self.getOwnerIds(parent)

        ## Create community to re-parent
        child = self.createCommunity(self.owners2, self.members2)
        childName = child.get(0).get("name")
        childMembersBeforeReparent = self.getMemberIds(child)
        childOwnersBeforeReparent = self.getOwnerIds(child)

        ## Do re-parenting
        self.reParent(child, parent)

        ## Check that child has parent uuid
        child = CommunitiesService.fetchCommByName(childName)
        self.assertEqual(child.get(0).get("parentUuid"), parent.get(0).get("uuid"))

        ## Check that original owners of parent are now owners in child
        childOwnersAfterReparent = self.getOwnerIds(child)
        self.assertEqual(self.checkVectorIsContainedIn(parentOwnersBeforeReparent, childOwnersAfterReparent),1)

        ## Check that original owners and members of child are now members in parent
        childCombinedMembersBeforeReparent = self.combineVectors(childOwnersBeforeReparent, childMembersBeforeReparent)
        parentMembersAfterReparent = self.getMemberIds(parent)
        self.assertEqual(self.checkVectorIsContainedIn(childCombinedMembersBeforeReparent, parentMembersAfterReparent), 1)


    ## Returns 1 iff vec1 is contained in vec2
    def checkVectorIsContainedIn(self, vec1, vec2):
        result = 1
        if (vec1.size() > vec2.size()):
            result = 0
        else:
            for vec in vec1:
                if not vec2.contains(vec):
                    result = 0;
                    break;

        return(result)

    ## Returns 1 iff arrays contain same strings.
    def checkVectorsAreEqual(self, vec1, vec2):

        result = 1
        if (vec1.size() != vec2.size()):
            print vec1.size().toString() + ", " + vec2.size().toString()
            result = 0
        else:
            for vec in vec1:
                if  not vec2.contains(vec):
                    result = 0
                    break
        return(result);

    ## Combines vectors 'arr1' and 'arr2'.  Returned result as a new vector
    def combineVectors(self, arr1, arr2):
        result = Vector(
        )
        for arr in arr1:
            result.add(arr)

        for arr in arr2:
            result.add(arr)

        return result;


    ## Returns Vector of owner ids in community
    def getOwnerIds(self, community):
        return self.getIds(community, "OWNER")

    ## Returns Vector of member ids in community
    def getMemberIds(self, community):
        return self.getIds(community, "MEMBER")

    ## Returns vector of members of given type in community
    def getIds(self, community, memberType):
        result = Vector()
        allMembers = CommunitiesService.fetchMember(community).get(0).get("memberList")
        for member in allMembers:
            if member[2] == memberType:
                result.add(member[1])

        return result

    ##
    ##  Makes 'child' a child of 'parent'
    ##
    def reParent(self, child, parent):
        CommunitiesService.moveCommunityToSubcommunity(parent.get(0).get("uuid"), child.get(0).get("uuid"))

    ##
    ## Moves 'child' to be top level community
    ##
    def unParent(self, child):
        CommunitiesService.moveSubcommunityToCommunity(child.get(0).get("uuid"))

    ##
    ## Creates community of given type and adds owners and members.
    ## Returns Vector representing Community
    ##
    def createCommunity(self, owners, members):
        communityName = self.createUniqueName("reparentTest")

        CommunitiesService.createCommunityWithEmail(communityName, owners[0].get("mail"), 1, "empty.xml")
        print "### Created community with name:  " + communityName

        self.addMembersToCommunity(communityName, owners, 1)
        self.addMembersToCommunity(communityName, members, 0)

        community = CommunitiesService.fetchCommByName(communityName)
        print "community = ", community

        return community

    def createUniqueName(self, root):
        currentDate = Date()
        return root + currentDate.getTime().toString()

    def addMembersToCommunity(self, communityName, members, type):
        memberArray = []
        for member in members:
            memberArray.append(member.get("mail"))

        CommunitiesService.addMembersToCommunityByEmail(communityName, type, memberArray)

def suite():
    suite = unittest.TestSuite()
    suite.addTest(unittest.makeSuite(CommunitiesAdminReparentTest))
    return suite

if __name__ == '__main__':
    # unitest.main() is not supported by wsadmin.sh
    #unittest.main()
    # the Jython implementation from WAS doesn't follow the normal Python/Jython
    # behavior, the sys.argv doesn't contain the name of the script itself, so
    # we need to workaround that to find the script name.

    suite = unittest.TestLoader().loadTestsFromTestCase(CommunitiesAdminReparentTest)
    result = unittest.TextTestRunner(verbosity=2).run(suite)
    if len(result.errors) > 0 or len(result.failures) > 0:
        sys.exit(1)
