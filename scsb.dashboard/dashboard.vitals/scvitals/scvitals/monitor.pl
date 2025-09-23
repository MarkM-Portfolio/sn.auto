#!/usr/bin/perl

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

# use warnings;

if ($#ARGV != 0) {
  print "\nMissing or Invalid command line arguments, exiting....\n\n";
  exit;
}

use FindBin ();
use lib "$FindBin::Bin";
use Config::IniFiles;
use DBI();

$cfg = Config::IniFiles->new( -file => $FindBin::Bin . "/base_config.ini" );
$dbh = DBI->connect("DBI:mysql:database=".$cfg->val("database","name").";host=".$cfg->val("database","host"), $cfg->val("database","user"), $cfg->val("database","pass"), {'RaiseError' => 1});
$sth = $dbh->prepare("SELECT `id`,`pid` FROM `logconfig` WHERE `process`=1 AND `enabled`=1 AND `hostname`='".$ARGV[0]."'");
$sth->execute() or die "SQL Error: $DBI::errstr\n";

while ($ref = $sth->fetchrow_hashref()) {
  $id = $ref->{'id'};
  $pid = $ref->{'pid'};
  if ($pid != 0) {
    $exists = kill(0, $pid) ? $pid : undef;
  }
  if (($pid==0) || (!defined($exists))) {
    print "Starting Job ID $id\n";
    system("/usr/bin/perl ".$FindBin::Bin."/process_log.pl ".$id." ".$ARGV[0]." >/dev/null 2>&1 &");
  } else {
    print "Job ID $id still processing previous iteration with Process ID $pid\n";
  }
}
$sth->finish();
$dbh->disconnect();
