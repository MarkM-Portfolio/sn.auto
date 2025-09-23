<?php

# Author: Nadir Anwer | 11/25/2010
# Edited By: Rogelio Vazquez | 06/11/2014
# Simple PHP Web Interface to MySQL database

require_once('config.php');

print '<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
<title>'.$dbname.' @ '.$dbhost.'</title>
<meta http-equiv="Content-Type" content="text/html; charset=us-ascii">
<meta name="Owner" content="nanwer@ca.ibm.com">
<meta name="Editor" content="rvazque@us.ibm.com">
<script type="text/javascript" src="jquery-1.5.1.min.js"></script>
<script type="text/javascript" src="jquery.thfloat-0.6.min.js"></script>
<style type="text/css">
html, body {
  font-size: 84%;
  font-family: verdana,arial,helvetica,sans-serif;
}
h2 {
  font-size: 1.4em;
  height: 1.5em;
  border-bottom: 1px solid #eee;
  color: #000;
  margin: 1em 0 1.4em 0;
}
h2 span {
  float: right;
  font-size: 11px;
  color: #666;
}
table#query {
  background-color: #fff;
  width: 100%;
}
table#query td {
  padding: 3px;
}
table#query td a {
  text-decoration: none;
  font-size: 11px;
}
table#query td a:hover {
  text-decoration: underline;
  color: #f00;
}
.data {
  background-color: #eee;
}
.leftcol {
  font-weight: bold;
  text-align: right;
  vertical-align: top;
  background-color: #ddd;
}
.helpme {
  font-size: 10px;
  vertical-align: top;
  background-color: #eee;
}
#warn {
  font-size: 11px;
  color: #f00;
}
table.results {
  background-color: #fff;
  margin:10px 0pt 15px;
  font-size: 8pt;
  width: 100%;
  text-align: left;
}
table.results thead tr th {
  background-color: #666;
  color: #fff;
  font-size: 8pt;
  padding: 4px;
}
table.results tbody td {
  color: #000;
  padding: 4px;
  background-color: #eee;
  vertical-align: top;
}
table.results tr:hover td {
  background-color: #fed;
}
</style>
</head>
<body>
<h2>'.$dbname.' @ '.$dbhost.'<span>'.date("Y-m-d H:i:s T")."</span></h2>\n";
if (isset($_GET['sql'])) {
  $sql = stripslashes($_GET['sql']);
} else {
  $sql = "";
}
print '<form enctype="application/x-www-form-urlencoded" method="get" action="'.$_SERVER['PHP_SELF'].'" id="sqlform">
<table id="query">
<tr>
<td class="leftcol">SQL:</td>
<td class="data"><textarea name="sql" rows="4" cols="85">'.$sql.'</textarea></td>
<td class="helpme"><b><u>Example:</u></b><br>SELECT col1,col2<br>FROM tablename<br>WHERE col3=\'matchme\'<br>ORDER BY col2 DESC<br>LIMIT 0,10</td>
</tr>
<tr>
<td></td>
<td><input type="submit" value="Display" id="normal"> <input type="submit" value="CSV" id="getcsv"> <input type="submit" value="JSON" id="getjson"> <span id="warn">Always use LIMIT clause for large 
resultsets!</span>
</td>
<td><a href="'.$_SERVER['PHP_SELF'].'?sql=show+table+status">Show All Tables</a> | <a href="'.$_SERVER['PHP_SELF'].'?sql=show+processlist">Show Process List</a></td>
</tr>
</table>
</form>
';
if (isset($_GET['sql'])) {

  $link = mysqli_connect($dbhost, $dbuser, $dbpass, $dbname) or die(mysqli_error($link));
  $result = mysqli_query($link, $sql) or die(mysqli_error($link));
  $numres = mysqli_num_rows($result);

  if ($numres != 0) {
    print '<p>Total number of rows in the resultset: '.$numres.'</p>
<table cellspacing="1" class="results" id="window-float">
<thead>
<tr>
';
    while($field_info = mysqli_fetch_field($result)) {
      print "<th>".$field_info->name."</th>\n";
    }
    print "</tr>\n</thead>\n";
    for ($i = 0; $i < mysqli_num_rows($result); $i++) {
      print "<tr>\n";
      $row_array = mysqli_fetch_row($result);
      for ($j = 0; $j < mysqli_num_fields($result); $j++) {
        print "<td>".htmlentities($row_array[$j])."</td>\n";
      }
      print "</tr>\n";
    }
    print "</table>\n";
  } 
  else {
    print "There were no rows in the resultset\n";
  }
  mysqli_free_result($result);
  mysqli_close($link);
}
print '</body>
</html>

<script type="text/javascript">
  $("#window-float").thfloat().thfloat({side : "foot"});
  $("#normal").click(function() {
	$("#sqlform").removeAttr("action").removeAttr("target");
  });
  $("#getcsv").click(function() {
	$("#sqlform").attr("action", "csv.php").attr("target", "_csv");
  });
  $("#getjson").click(function() {
	$("#sqlform").attr("action", "json.php").attr("target", "_json");
  });
</script>';


?>

