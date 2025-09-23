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

#For more information on the Text::CSV_XS library - http://search.cpan.org/dist/Text-CSV_XS-1.08/CSV_XS.pm
use Text::CSV_XS;

#LogFormat "\"%{ids}C\",%D,\"%l\",\"%u\",\"%{%Y-%m-%d}t\",\"%{%H:%M:%S}t\",\"%r\",%>s,\"%b\",\"%{Referer}i\",\"%{User-Agent}i\",\"%h\",\"%{X-Forwarded-For}i\",\"%p\",\"%H\",\"%m\",\"%U\",\"%q\",%B,\"%{CACHED_RESPONSE}o\",\"%{JSESSIONID}C\",\"%{LtpaToken2}C\",\"%X\"" bluehouse
#Taken from the Apache 2.2.3 httpd configuration file: /etc/httpd/conf/httpd.conf

#Creates a new Text::CSV_XS object for parsing the log.
$csv = Text::CSV_XS->new({ binary => 1 });

sub processLog {  
  while (<LOGFILE>) { 
    chomp;
    #Get and parse log line into an array reference using the Text::CSV_XS object and check for success.
    $csv->parse($_);
    @parsedLogLine = $csv->fields();
    #For code clarity and readability, assign array values to temporary variables.
    $ids = $parsedLogLine[0];
    $subscriber_id;
    $customer_id;
    if($ids eq '-') {
      $subscriber_id = 0;
      $customer_id = 0;
    }
    else {
      @ids_split = split /:/, $ids;
      $subscriber_id = $ids_split[0];
      $customer_id = $ids_split[1];
    }
    $response_time = $parsedLogLine[1];
    $remote_logname = $parsedLogLine[2];
    $remote_user = $parsedLogLine[3];
    $request_date = $parsedLogLine[4];
    $request_time = $parsedLogLine[5];
    $status_code = $parsedLogLine[7];
    $response_size = $parsedLogLine[8];
    $request_referer = $parsedLogLine[9];
    $user_agent = $parsedLogLine[10];
    $remote_host = $parsedLogLine[11];
    #X-Forwarded_For used to extract the client's IP.
    $x_forwarded_for = $parsedLogLine[12];
    @x_forwarded_for_split = split /,/, $x_forwarded_for;
    $client_ip = $x_forwarded_for_split[0];
    $server_port = $parsedLogLine[13];
    $request_protocol = $parsedLogLine[14];
    $request_method = $parsedLogLine[15];
    $url_path = $parsedLogLine[16];
    $query_string = $parsedLogLine[17];
    $cached_response = $parsedLogLine[19];
    $jsession_id = $parsedLogLine[20];
    $ltpa_token = $parsedLogLine[21];
    $connection_status = $parsedLogLine[22];
    #Parse the date to extract the yyyy/mmm/dd.
    $request_date =~ /(\d{4})\-(\d{2})\-(\d{2})/g;
    $dd = $3;
    if ($dd) {
      $dbh->do("INSERT DELAYED INTO `".$result->{'name'}.$dd."` (`customer_id`,`subscriber_id`,`proxy_node`,`response_time`,`remote_logname`,`remote_user`,`request_date`,`request_time`,`status_code`,`response_size`,`request_referer`,`user_agent`,`remote_host`,`x_forwarded_for`,`client_ip`,`server_port`,`request_protocol`,`request_method`,`url_path`,`query_string`,`cached_response`,`jsession_id`,`ltpa_token`,`connection_status`) VALUES (".$dbh->quote($customer_id).",".$dbh->quote($subscriber_id).",".$dbh->quote($hostname).",".$dbh->quote($response_time).",".$dbh->quote($remote_logname).",".$dbh->quote($remote_user).",".$dbh->quote($request_date).",".$dbh->quote($request_time).",".$dbh->quote($status_code).",".$dbh->quote($response_size).",".$dbh->quote($request_referer).",".$dbh->quote($user_agent).",".$dbh->quote($remote_host).",".$dbh->quote($x_forwarded_for).",".$dbh->quote($client_ip).",".$dbh->quote($server_port).",".$dbh->quote($request_protocol).",".$dbh->quote($request_method).",".$dbh->quote($url_path).",".$dbh->quote($query_string).",".$dbh->quote($cached_response).",".$dbh->quote($jsession_id).",".$dbh->quote($ltpa_token).",".$dbh->quote($connection_status).")");
    }
  }
  #Verify if the rp1a server logs are being evaluated. 
  #Since this log stores information for one day, when data is extracted, the lasteof should be reset to zero for the next read to start from the beginning of the log. 
  if($hostname eq 'rp1a.bht6.swg.usma.ibm.com') {
    $lasteof = 0;
  }
  else {
    $lasteof = tell(LOGFILE);
  }
  
}
