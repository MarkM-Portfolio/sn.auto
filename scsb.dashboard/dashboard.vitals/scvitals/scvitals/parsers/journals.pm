#################################################
#  Licensed Materials -- Property of IBM
#
#  (c) Copyright IBM Corporation, 2010, 2014.
#      ALL RIGHTS RESERVED.
#
#  US Government Users Restricted Rights -
#  Use, duplication or disclosure restricted by
#  GSA ADP Schedule Contract with IBM Corp.
#################################################

#Format:
#date user email (id=subscriberId, customerId=customerId) performed ACTION [on object (type=TYPE, id=OBJECTID, name="name", customerId=customerId)] [targeted at (type=TYPE, id=TARGETID, name="name", customerId=customerId)] with outcome OUTCOME [reason=REASON][(EXTRA)]

sub processLog {  
  while (<LOGFILE>) { 
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
    #Insert into the appropriate database table according to the application.
    if ($result->{'application'} eq 'activities') {
      $dbh->do("INSERT DELAYED INTO `".$result->{'name'}."` (`date`,`time`,`subject_email`,`subject_subscriber_id`,`subject_customer_id`,`action`,`object_type`,`object_id`,`object_name`,`object_customer_id`,`target_type`,`target_id`,`target_name`,`target_customer_id`,`outcome`,`reason`,`extra`) VALUES (".$dbh->quote($date).",".$dbh->quote($time).",".$dbh->quote($subject_email).",".$dbh->quote($subject_subscriber_id).",".$dbh->quote($subject_customer_id).",".$dbh->quote($action).",".$dbh->quote($object_type).",".$dbh->quote($object_id).",".$dbh->quote($object_name).",".$dbh->quote($object_customer_id).",".$dbh->quote($target_id).",".$dbh->quote($target_name).",".$dbh->quote($target_type).",".$dbh->quote($target_customer_id).",".$dbh->quote($outcome).",".$dbh->quote($reason).",".$dbh->quote($extra).")");
    }
    elsif ($result->{'application'} eq 'announcements') {
      $dbh->do("INSERT DELAYED INTO `".$result->{'name'}."` (`date`,`time`,`subject_email`,`subject_subscriber_id`,`subject_customer_id`,`action`,`object_type`,`object_id`,`object_name`,`object_customer_id`,`outcome`,`reason`,`extra`) VALUES (".$dbh->quote($date).",".$dbh->quote($time).",".$dbh->quote($subject_email).",".$dbh->quote($subject_subscriber_id).",".$dbh->quote($subject_customer_id).",".$dbh->quote($action).",".$dbh->quote($object_type).",".$dbh->quote($object_id).",".$dbh->quote($object_name).",".$dbh->quote($object_customer_id).",".$dbh->quote($outcome).",".$dbh->quote($reason).",".$dbh->quote($extra).")");
    }
    elsif ($result->{'application'} eq 'authentication') {
      $dbh->do("INSERT DELAYED INTO `".$result->{'name'}."` (`date`,`time`,`subject_email`,`subject_subscriber_id`,`subject_customer_id`,`action`,`outcome`,`reason`) VALUES (".$dbh->quote($date).",".$dbh->quote($time).",".$dbh->quote($subject_email).",".$dbh->quote($subject_subscriber_id).",".$dbh->quote($subject_customer_id).",".$dbh->quote($action).",".$dbh->quote($outcome).",".$dbh->quote($reason).")");
    }
    elsif ($result->{'application'} eq 'blogs') {
      $dbh->do("INSERT DELAYED INTO `".$result->{'name'}."` (`date`,`time`,`subject_email`,`subject_subscriber_id`,`subject_customer_id`,`action`,`object_type`,`object_id`,`object_name`,`object_customer_id`,`target_type`,`target_id`,`target_name`,`target_customer_id`,`outcome`,`reason`,`extra`) VALUES (".$dbh->quote($date).",".$dbh->quote($time).",".$dbh->quote($subject_email).",".$dbh->quote($subject_subscriber_id).",".$dbh->quote($subject_customer_id).",".$dbh->quote($action).",".$dbh->quote($object_type).",".$dbh->quote($object_id).",".$dbh->quote($object_name).",".$dbh->quote($object_customer_id).",".$dbh->quote($target_id).",".$dbh->quote($target_name).",".$dbh->quote($target_type).",".$dbh->quote($target_customer_id).",".$dbh->quote($outcome).",".$dbh->quote($reason).",".$dbh->quote($extra).")");
    }
    elsif ($result->{'application'} eq 'bss') {
      $dbh->do("INSERT DELAYED INTO `".$result->{'name'}."` (`date`,`time`,`subject_email`,`subject_subscriber_id`,`subject_customer_id`,`action`,`object_type`,`object_id`,`object_name`,`object_customer_id`,`target_type`,`target_id`,`target_name`,`target_customer_id`,`outcome`,`reason`,`extra`) VALUES (".$dbh->quote($date).",".$dbh->quote($time).",".$dbh->quote($subject_email).",".$dbh->quote($subject_subscriber_id).",".$dbh->quote($subject_customer_id).",".$dbh->quote($action).",".$dbh->quote($object_type).",".$dbh->quote($object_id).",".$dbh->quote($object_name).",".$dbh->quote($object_customer_id).",".$dbh->quote($target_id).",".$dbh->quote($target_name).",".$dbh->quote($target_type).",".$dbh->quote($target_customer_id).",".$dbh->quote($outcome).",".$dbh->quote($reason).",".$dbh->quote($extra).")");
    }
    elsif ($result->{'application'} eq 'communities') {
      $dbh->do("INSERT DELAYED INTO `".$result->{'name'}."` (`date`,`time`,`subject_email`,`subject_subscriber_id`,`subject_customer_id`,`action`,`object_type`,`object_id`,`object_name`,`object_customer_id`,`target_type`,`target_id`,`target_name`,`target_customer_id`,`outcome`,`reason`,`extra`) VALUES (".$dbh->quote($date).",".$dbh->quote($time).",".$dbh->quote($subject_email).",".$dbh->quote($subject_subscriber_id).",".$dbh->quote($subject_customer_id).",".$dbh->quote($action).",".$dbh->quote($object_type).",".$dbh->quote($object_id).",".$dbh->quote($object_name).",".$dbh->quote($object_customer_id).",".$dbh->quote($target_id).",".$dbh->quote($target_name).",".$dbh->quote($target_type).",".$dbh->quote($target_customer_id).",".$dbh->quote($outcome).",".$dbh->quote($reason).",".$dbh->quote($extra).")");
    }
    elsif ($result->{'application'} eq 'company_administration') {
      $dbh->do("INSERT DELAYED INTO `".$result->{'name'}."` (`date`,`time`,`subject_email`,`subject_subscriber_id`,`subject_customer_id`,`action`,`object_type`,`object_id`,`object_name`,`object_customer_id`,`target_type`,`target_id`,`target_name`,`target_customer_id`,`outcome`,`reason`,`extra`) VALUES (".$dbh->quote($date).",".$dbh->quote($time).",".$dbh->quote($subject_email).",".$dbh->quote($subject_subscriber_id).",".$dbh->quote($subject_customer_id).",".$dbh->quote($action).",".$dbh->quote($object_type).",".$dbh->quote($object_id).",".$dbh->quote($object_name).",".$dbh->quote($object_customer_id).",".$dbh->quote($target_id).",".$dbh->quote($target_name).",".$dbh->quote($target_type).",".$dbh->quote($target_customer_id).",".$dbh->quote($outcome).",".$dbh->quote($reason).",".$dbh->quote($extra).")");
    }
    elsif ($result->{'application'} eq 'contacts') {
      $dbh->do("INSERT DELAYED INTO `".$result->{'name'}."` (`date`,`time`,`subject_email`,`subject_subscriber_id`,`subject_customer_id`,`action`,`object_type`,`object_id`,`object_name`,`object_customer_id`,`outcome`,`reason`) VALUES (".$dbh->quote($date).",".$dbh->quote($time).",".$dbh->quote($subject_email).",".$dbh->quote($subject_subscriber_id).",".$dbh->quote($subject_customer_id).",".$dbh->quote($action).",".$dbh->quote($object_type).",".$dbh->quote($object_id).",".$dbh->quote($object_name).",".$dbh->quote($object_customer_id).",".$dbh->quote($outcome).",".$dbh->quote($reason).")");
    }
    elsif ($result->{'application'} eq 'files') {
      $dbh->do("INSERT DELAYED INTO `".$result->{'name'}."` (`date`,`time`,`subject_email`,`subject_subscriber_id`,`subject_customer_id`,`action`,`object_type`,`object_id`,`object_name`,`object_customer_id`,`target_type`,`target_id`,`target_name`,`target_customer_id`,`outcome`,`reason`,`extra`) VALUES (".$dbh->quote($date).",".$dbh->quote($time).",".$dbh->quote($subject_email).",".$dbh->quote($subject_subscriber_id).",".$dbh->quote($subject_customer_id).",".$dbh->quote($action).",".$dbh->quote($object_type).",".$dbh->quote($object_id).",".$dbh->quote($object_name).",".$dbh->quote($object_customer_id).",".$dbh->quote($target_id).",".$dbh->quote($target_name).",".$dbh->quote($target_type).",".$dbh->quote($target_customer_id).",".$dbh->quote($outcome).",".$dbh->quote($reason).",".$dbh->quote($extra).")");
    }
    elsif ($result->{'application'} eq 'forms') {
      $dbh->do("INSERT DELAYED INTO `".$result->{'name'}."` (`date`,`time`,`subject_email`,`subject_subscriber_id`,`subject_customer_id`,`action`,`object_type`,`object_id`,`object_name`,`object_customer_id`,`target_type`,`target_id`,`target_name`,`target_customer_id`,`outcome`,`reason`,`extra`) VALUES (".$dbh->quote($date).",".$dbh->quote($time).",".$dbh->quote($subject_email).",".$dbh->quote($subject_subscriber_id).",".$dbh->quote($subject_customer_id).",".$dbh->quote($action).",".$dbh->quote($object_type).",".$dbh->quote($object_id).",".$dbh->quote($object_name).",".$dbh->quote($object_customer_id).",".$dbh->quote($target_id).",".$dbh->quote($target_name).",".$dbh->quote($target_type).",".$dbh->quote($target_customer_id).",".$dbh->quote($outcome).",".$dbh->quote($reason).",".$dbh->quote($extra).")");
    }
    elsif ($result->{'application'} eq 'forums') {
      $dbh->do("INSERT DELAYED INTO `".$result->{'name'}."` (`date`,`time`,`subject_email`,`subject_subscriber_id`,`subject_customer_id`,`action`,`object_type`,`object_id`,`object_name`,`object_customer_id`,`target_type`,`target_id`,`target_name`,`target_customer_id`,`outcome`,`reason`,`extra`) VALUES (".$dbh->quote($date).",".$dbh->quote($time).",".$dbh->quote($subject_email).",".$dbh->quote($subject_subscriber_id).",".$dbh->quote($subject_customer_id).",".$dbh->quote($action).",".$dbh->quote($object_type).",".$dbh->quote($object_id).",".$dbh->quote($object_name).",".$dbh->quote($object_customer_id).",".$dbh->quote($target_id).",".$dbh->quote($target_name).",".$dbh->quote($target_type).",".$dbh->quote($target_customer_id).",".$dbh->quote($outcome).",".$dbh->quote($reason).",".$dbh->quote($extra).")");
    }
    elsif ($result->{'application'} eq 'inotes') {
      $dbh->do("INSERT DELAYED INTO `".$result->{'name'}."` (`date`,`time`,`subject_email`,`subject_subscriber_id`,`subject_customer_id`,`action`,`outcome`,`reason`) VALUES (".$dbh->quote($date).",".$dbh->quote($time).",".$dbh->quote($subject_email).",".$dbh->quote($subject_subscriber_id).",".$dbh->quote($subject_customer_id).",".$dbh->quote($action).",".$dbh->quote($outcome).",".$dbh->quote($reason).")");
    }
    elsif ($result->{'application'} eq 'meetings') {
      $dbh->do("INSERT DELAYED INTO `".$result->{'name'}."` (`date`,`time`,`subject_email`,`subject_subscriber_id`,`subject_customer_id`,`action`,`object_type`,`object_id`,`object_name`,`object_customer_id`,`target_type`,`target_id`,`target_name`,`target_customer_id`,`outcome`,`reason`,`extra`) VALUES (".$dbh->quote($date).",".$dbh->quote($time).",".$dbh->quote($subject_email).",".$dbh->quote($subject_subscriber_id).",".$dbh->quote($subject_customer_id).",".$dbh->quote($action).",".$dbh->quote($object_type).",".$dbh->quote($object_id).",".$dbh->quote($object_name).",".$dbh->quote($object_customer_id).",".$dbh->quote($target_id).",".$dbh->quote($target_name).",".$dbh->quote($target_type).",".$dbh->quote($target_customer_id).",".$dbh->quote($outcome).",".$dbh->quote($reason).",".$dbh->quote($extra).")");
    }
    elsif ($result->{'application'} eq 'profiles') {
      $dbh->do("INSERT DELAYED INTO `".$result->{'name'}."` (`date`,`time`,`subject_email`,`subject_subscriber_id`,`subject_customer_id`,`action`,`object_type`,`object_id`,`object_name`,`object_customer_id`,`target_type`,`target_id`,`target_name`,`target_customer_id`,`outcome`,`reason`) VALUES (".$dbh->quote($date).",".$dbh->quote($time).",".$dbh->quote($subject_email).",".$dbh->quote($subject_subscriber_id).",".$dbh->quote($subject_customer_id).",".$dbh->quote($action).",".$dbh->quote($object_type).",".$dbh->quote($object_id).",".$dbh->quote($object_name).",".$dbh->quote($object_customer_id).",".$dbh->quote($target_id).",".$dbh->quote($target_name).",".$dbh->quote($target_type).",".$dbh->quote($target_customer_id).",".$dbh->quote($outcome).",".$dbh->quote($reason).")");
    }
    elsif ($result->{'application'} eq 'sametime') {
      $dbh->do("INSERT DELAYED INTO `".$result->{'name'}."` (`date`,`time`,`subject_email`,`subject_subscriber_id`,`subject_customer_id`,`action`,`target_type`,`target_id`,`target_name`,`target_customer_id`,`outcome`,`extra`) VALUES (".$dbh->quote($date).",".$dbh->quote($time).",".$dbh->quote($subject_email).",".$dbh->quote($subject_subscriber_id).",".$dbh->quote($subject_customer_id).",".$dbh->quote($action).",".$dbh->quote($target_id).",".$dbh->quote($target_name).",".$dbh->quote($target_type).",".$dbh->quote($target_customer_id).",".$dbh->quote($outcome).",".$dbh->quote($extra).")");
    }
    elsif ($result->{'application'} eq 'surveys') {
      $dbh->do("INSERT DELAYED INTO `".$result->{'name'}."` (`date`,`time`,`subject_email`,`subject_subscriber_id`,`subject_customer_id`,`action`,`object_type`,`object_id`,`object_name`,`object_customer_id`,`target_type`,`target_id`,`target_name`,`target_customer_id`,`outcome`,`reason`) VALUES (".$dbh->quote($date).",".$dbh->quote($time).",".$dbh->quote($subject_email).",".$dbh->quote($subject_subscriber_id).",".$dbh->quote($subject_customer_id).",".$dbh->quote($action).",".$dbh->quote($object_type).",".$dbh->quote($object_id).",".$dbh->quote($object_name).",".$dbh->quote($object_customer_id).",".$dbh->quote($target_id).",".$dbh->quote($target_name).",".$dbh->quote($target_type).",".$dbh->quote($target_customer_id).",".$dbh->quote($outcome).",".$dbh->quote($reason).")");
    }
    elsif ($result->{'application'} eq 'theming') {
      $dbh->do("INSERT DELAYED INTO `".$result->{'name'}."` (`date`,`time`,`subject_email`,`subject_subscriber_id`,`subject_customer_id`,`action`,`object_type`,`object_id`,`object_name`,`object_customer_id`,`outcome`,`reason`,`extra`) VALUES (".$dbh->quote($date).",".$dbh->quote($time).",".$dbh->quote($subject_email).",".$dbh->quote($subject_subscriber_id).",".$dbh->quote($subject_customer_id).",".$dbh->quote($action).",".$dbh->quote($object_type).",".$dbh->quote($object_id).",".$dbh->quote($object_name).",".$dbh->quote($object_customer_id).",".$dbh->quote($outcome).",".$dbh->quote($reason).",".$dbh->quote($extra).")");
    }
    elsif ($result->{'application'} eq 'wikis') {
      $dbh->do("INSERT DELAYED INTO `".$result->{'name'}."` (`date`,`time`,`subject_email`,`subject_subscriber_id`,`subject_customer_id`,`action`,`object_type`,`object_id`,`object_name`,`object_customer_id`,`target_type`,`target_id`,`target_name`,`target_customer_id`,`outcome`,`reason`,`extra`) VALUES (".$dbh->quote($date).",".$dbh->quote($time).",".$dbh->quote($subject_email).",".$dbh->quote($subject_subscriber_id).",".$dbh->quote($subject_customer_id).",".$dbh->quote($action).",".$dbh->quote($object_type).",".$dbh->quote($object_id).",".$dbh->quote($object_name).",".$dbh->quote($object_customer_id).",".$dbh->quote($target_id).",".$dbh->quote($target_name).",".$dbh->quote($target_type).",".$dbh->quote($target_customer_id).",".$dbh->quote($outcome).",".$dbh->quote($reason).",".$dbh->quote($extra).")");
    }
    else {
      print "ERROR: Application not recognized!\n";
    }
  }
  #Since these logs store information for one day, when data is extracted, the lasteof should be reset to zero for the next read to start from the beginning of the log. 
  $lasteof = 0;  
}
