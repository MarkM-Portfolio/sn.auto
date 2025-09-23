#!/usr/bin/perl

use warnings;

#Format:
#date user email (id=subscriberId, customerId=customerId) performed ACTION [on object (type=TYPE, id=OBJECTID, name="name", customerId=customerId)] [targeted at (type=TYPE, id=TARGETID, name="name", customerId=customerId)] with outcome OUTCOME [reason=REASON][(EXTRA)]

#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\activities\2014-05-21.ACTIVITIES.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\activities\2014-05-30.ACTIVITIES.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\activities\2014-06-04.ACTIVITIES.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\activities\2014-06-05.ACTIVITIES.txt';

#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\auth\2014-05-19.AUTH.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\auth\2014-05-20.AUTH.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\auth\2014-05-21.AUTH.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\auth\2014-05-22.AUTH.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\auth\2014-06-10.AUTH.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\auth\2014-06-11.AUTH.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\auth\2014-06-13.AUTH.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\auth\2014-06-16.AUTH.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\auth\2014-06-17.AUTH.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\auth\2014-06-18.AUTH.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\auth\2014-06-19.AUTH.txt';

#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\blogs\2014-05-28.BLOGS.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\blogs\2014-05-30.BLOGS.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\blogs\2014-06-04.BLOGS.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\blogs\2014-06-05.BLOGS.txt';

#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\bss\2014-05-29.BSS.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\bss\2014-06-04.BSS.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\bss\2014-06-05.BSS.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\bss\2014-06-19.BSS.txt';

#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\communities\2014-05-29.COMMUNITIES.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\communities\2014-05-30.COMMUNITIES.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\communities\2014-06-04.COMMUNITIES.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\communities\2014-06-05.COMMUNITIES.txt';

#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\contact\2014-05-29.CONTACT.txt';

#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\feb\2014-05-30.FEB.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\feb\2014-06-09.FEB.txt';

#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\files2\2014-05-30.FILES2.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\files2\2014-06-04.FILES2.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\files2\2014-06-05.FILES2.txt';

#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\forums\2014-05-29.FORUMS.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\forums\2014-05-30.FORUMS.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\forums\2014-06-04.FORUMS.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\forums\2014-06-05.FORUMS.txt';

my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\meetings\2014-06-04.MEETINGS.txt';

#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\profile\2014-05-29.PROFILE.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\profile\2014-06-04.PROFILE.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\profile\2014-06-05.PROFILE.txt';

#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\sametime\2014-05-22.SAMETIME.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\sametime\2014-05-29.SAMETIME.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\sametime\2014-05-30.SAMETIME.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\sametime\2014-06-04.SAMETIME.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\sametime\2014-06-05.SAMETIME.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\sametime\2014-06-06.SAMETIME.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\sametime\2014-06-10.SAMETIME.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\sametime\2014-06-17.SAMETIME.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\sametime\2014-06-18.SAMETIME.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\sametime\2014-06-19.SAMETIME.txt';

#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\wikis\2014-05-29.WIKIS.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\wikis\2014-05-30.WIKIS.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\wikis\2014-06-04.WIKIS.txt';
#my $logPath = 'C:\Users\IBM_ADMIN\Desktop\scvitals\scvitals\test\wikis\2014-06-05.WIKIS.txt';

open LOGFILE, $logPath or die $!;

while(<LOGFILE>) {
	#Extract the individual fields for each event in the journal file
	chomp;
	my $date = "";
	my $time = "";
	my $subject_email = "";
	my $subject_subscriber_id = "";
	my $subject_customer_id = "";
	my $action = "";
	my $object_type = "";
	my $object_id = "";
	my $object_name = "";
	my $object_customer_id = "";
	my $target_type = "";
	my $target_id = "";
	my $target_name = "";
	my $target_customer_id = "";
	my $outcome = "";
	my $reason = "";
	my $extra = "";
	print "\n".$_."\n";
	#Extract the date and time in UTC (required)
	#The format is YYY-MM-DDThh:mm:ss+0000
	if($_ =~ m/^(\d{4}\-\d{2}\-\d{2})T(\d{2}:\d{2}:\d{2})\+\d{4}/) {
		$date = $1;
		$time = $2;
	}
	#Extract the subject, or person who performed the action (required)
	#Contains the email, subscriber ID, and customer ID
	if($_ =~ m/user (\S*) \(id=(\w+), customerId=(\w+)\)/) {
		$subject_email = $1;
		$subject_subscriber_id = $2;
		$subject_customer_id = $3;
	}
	#Extract the action performed by the subject (required)
	if($_ =~ m/performed (\w+)/) {
		$action = $1;
	}
	#Extract the object on which the action was performed (optional)
	#If present, it contains the object type, object ID, object name, and customer ID
	if($_ =~ m/on object \(type=(\S*), id=(\S*), name=\"(.*?)\", customerId=(\S*)\)/) {
		$object_type = $1;
		$object_id = $2;
		$object_name = $3;
		$object_customer_id = $4;
	}
	#Extract the object at which the event was targeted (optional)
	#If present, it contains the target type, target ID, target name, and customer ID
	if($_ =~ m/targeted at \(type=(\S*), id=(\S*), name=\"(.*?)\", customerId=(\S*)\)/) {
		$target_type = $1;
		$target_id = $2;
		$target_name = $3;
		$target_customer_id = $4;
	}
	#Extract the result of the action (required)
	if($_ =~ m/with outcome (\w+),?/) {
		$outcome = $1;
	}
	#Extract the reason for an event's outcome that's a FAILURE (optional)
	if($_ =~ m/reason=(\w+)\.?/) {
		$reason = $1;
	}
	#Extact other information relevant in the context of some actions (optional)
	#Composed of name="value" pairs separated by a comma and a space if applicable
	if($_ =~ m/\(((\w+=\".*?\"(, )?)+)\)$/) {
		$extra = $1;
	}
	print "Date: ".$date."\n";
	print "Time: ".$time."\n";	
	print "Email: ".$subject_email."\n";
	print "SubscriberID: ".$subject_subscriber_id."\n";
	print "CustomerID: ".$subject_customer_id."\n";	
	print "Action: ".$action."\n";
	print "Object Type: ".$object_type."\n";
	print "Object ID: ".$object_id."\n";
	print "Object Name: ".$object_name."\n";
	print "Object Customer ID: ".$object_customer_id."\n";
	print "Target Type: ".$target_type."\n";
	print "Target ID: ".$target_id."\n";
	print "Target Name: ".$target_name."\n";
	print "Target Customer ID: ".$target_customer_id."\n";
	print "Outcome: ".$outcome."\n";
	print "Reason: ".$reason."\n";
	print "Extra: ".$extra."\n";
}
close(LOGFILE);
