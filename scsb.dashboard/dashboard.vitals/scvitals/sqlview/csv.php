<?php
/*************************************************
 *  Licensed Materials -- Property of IBM
 *
 *  (c) Copyright IBM Corporation, 2010, 2014.
 *      ALL RIGHTS RESERVED.
 *
 *  US Government Users Restricted Rights -
 *  Use, duplication or disclosure restricted by
 *  GSA ADP Schedule Contract with IBM Corp.
 *************************************************/

require_once('config.php');

if (isset($_REQUEST['sql'])) {
  header('Content-type: text/plain');
  $link = mysqli_connect($dbhost, $dbuser, $dbpass, $dbname) or die(mysqli_error($link));
  mysqli_query($link, "SET NAMES 'utf8'");
  $result = mysqli_query($link, $_REQUEST['sql']) or die(mysqli_error($link));
  $numres = mysqli_num_rows($result);
  if ($numres != 0) {
    $numcol = mysqli_num_fields($result);
    for ($i = 0; $i < $numcol; $i++) {
      print '"'.str_replace('"', '\\"', stripslashes(mysqli_fetch_field($result)->name)).'"';
      if ($i < ($numcol - 1)) {
        print ',';
      }
    }
    print "\n";
    for ($i = 0; $i < mysqli_num_rows($result); $i++) {
      $row_array = mysqli_fetch_row($result);
      for ($j = 0; $j < mysqli_num_fields($result); $j++) {
        if ($row_array[$j] != '') {
          print '"'.str_replace('"', '\\"', stripslashes($row_array[$j])).'"';
        }
        if ($j < ($numcol - 1)) {
          print ',';
        }
      }
      print "\n";
    }
    mysqli_free_result($result);
  }
  else {
    print "There were no rows in the resultset\n";
  }
  mysqli_close($link);
}

?>
