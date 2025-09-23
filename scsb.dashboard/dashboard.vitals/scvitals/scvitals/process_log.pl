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

if ($#ARGV != 1) {
  print "\nMissing or Invalid command line arguments, exiting....\n\n";
  exit;
}

use FindBin ();
use lib "$FindBin::Bin";
use Config::IniFiles;
use DBI();

$cfg = Config::IniFiles->new( -file => $FindBin::Bin . "/base_config.ini" );
$dbh = DBI->connect("DBI:mysql:database=".$cfg->val("database","name").";host=".$cfg->val("database","host"), $cfg->val("database","user"), $cfg->val("database","pass"), {'RaiseError' => 1});
$dbh->do("UPDATE `logconfig` SET `pid` = ".$$." WHERE `id` = ".$ARGV[0]) or die "SQL Error: $DBI::errstr\n";
$sth = $dbh->prepare("SELECT `logconfig`.`application`,`logconfig`.`type`,`logconfig`.`logfile`,`logconfig`.`lasteof`,`logconfig`.`hostname`,`table_rotate`.`name`,`table_rotate`.`suffix` FROM `logconfig`,`table_rotate` WHERE `logconfig`.`table_id`=`table_rotate`.`id` AND `logconfig`.`id`=".$ARGV[0]);
$sth->execute() or die "SQL Error: $DBI::errstr\n";
$result = $sth->fetchrow_hashref();
$sth->finish();

$logfile = $result->{'logfile'};

if (-e $logfile) {
  open(LOGFILE, $logfile) or die "Unable to open $logfile : $! \n";
  seek(LOGFILE, 0, 2);
  $curreof = tell(LOGFILE);
  if ($curreof > $result->{'lasteof'}) {
    seek LOGFILE, $result->{'lasteof'}, 0;
  } else {
    seek LOGFILE, 0, 0;
  }
  if ($curreof != $result->{'lasteof'}) {
    $hostname = $ARGV[1];
    $module = $result->{'type'};
    eval("use parsers::$module;");
    &processLog();
    $dbh->do("UPDATE `logconfig` SET `lasteof` = ".$lasteof.", `pid` = 0 WHERE `id` = ".$ARGV[0]);
  } else {
    $dbh->do("UPDATE `logconfig` SET `pid` = 0 WHERE `id` = ".$ARGV[0]);
    print "No new messages detected, exiting...\n";
  }
  close LOGFILE;
} else {
  print "Missing $logfile file!\n";
}
$dbh->disconnect();

