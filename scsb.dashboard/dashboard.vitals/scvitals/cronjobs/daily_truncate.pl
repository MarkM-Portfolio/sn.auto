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

use warnings;
use FindBin ();
use lib "$FindBin::Bin";
use Config::IniFiles;
use DBI();

$cfg = Config::IniFiles->new( -file => $FindBin::Bin . "/../scvitals/base_config.ini" );

($junk,$junk,$junk,$tomorrow,$junk,$junk,$junk,$junk,$junk) = localtime(time+86400);

$dbh = DBI->connect("DBI:mysql:database=".$cfg->val("database","name").";host=".$cfg->val("database","host"), $cfg->val("database","user"), $cfg->val("database","pass"), {'RaiseError' => 1});

$dbh->do("TRUNCATE TABLE `collabservsvt2accesslog".sprintf("%02d",$tomorrow)."`");

$dbh->disconnect();
