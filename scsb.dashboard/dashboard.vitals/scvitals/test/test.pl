#!/usr/bin/perl

use warnings;

#For more information on the use of the Text::CSV_XS library - http://search.cpan.org/dist/Text-CSV_XS-1.08/CSV_XS.pm
use Text::CSV_XS;

#LogFormat "\"%{ids}C\",%D,\"%l\",\"%u\",\"%{%Y-%m-%d}t\",\"%{%H:%M:%S}t\",\"%r\",%>s,\"%b\",\"%{Referer}i\",\"%{User-Agent}i\",\"%h\",\"%{X-Forwarded-For}i\",\"%p\",\"%H\",\"%m\",\"%U\",\"%q\",%B,\"%{CACHED_RESPONSE}o\",\"%{JSESSIONID}C\",\"%{LtpaToken2}C\",\"%X\"" bluehouse
#Taken from the Apache 2.2.3 httpd configuration file: /etc/httpd/conf/httpd.conf

my $csv = Text::CSV_XS->new({ binary => 1 });

my $logPath = '/root/scvitals/scvitals/test/apps_collabservsvt2_access_log.txt';
open LOGFILE, $logPath or die $!;
# while(<LOGFILE>){
# 	if($csv->parse($_)){
# 		my $field;
# 		my $index = 1;
# 		my @parsedLog = $csv->fields();
# 		foreach $field (@parsedLog){
# 			print $index++.'. '.$field."\n";
# 		}
# 		print "\n";
# 	}
# }
while(my $logLine = $csv->getline(LOGFILE)) {
	my $field;
	my $index = 1;
	foreach $field (@$logLine){
		print $index++.'. '.$field."\n";
	}
	$request_date = @$logLine[4];
	$request_date =~ /(\d{4})\-(\d{2})\-(\d{2})/g;
	print "Year: ".$1."\n";
	print "Month: ".$2."\n";
	print "Day: ".$3."\n";
	$x_forwarded_for = @$logLine[12];
	@client_ip_remote_host = split /,/, $x_forwarded_for;
	print "Clent IP: ".$client_ip_remote_host[0]."\n";
	print "X-Forwarded-For: ".$x_forwarded_for."\n";
	if(@$logLine[15] eq 'POST') {
		print "This is a POST!"."\n";
	}
	else {
		print "This is NOT a POST!"."\n";
	}
	print "\n";
}
$lasteof = tell(LOGFILE);
print "Last EOF: $lasteof"."\n";
close(LOGFILE);
