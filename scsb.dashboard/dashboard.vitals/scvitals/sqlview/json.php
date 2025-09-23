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

header('Content-type: text/plain');

$result = '';
$sql = '';
$numres = 0;
if (isset($_REQUEST['sql'])) {
  $sql = $_REQUEST['sql'];
  $link = mysqli_connect($dbhost, $dbuser, $dbpass, $dbname) or die(mysqli_error($link));
  $result = mysqli_query($link, $sql) or die(mysqli_error($link));
  $numres = mysqli_num_rows($result);
  $rows = array();
  if ($numres != 0) {
    while ($row = mysqli_fetch_assoc($result)) {
      $rows[] = $row;
    }
    mysqli_free_result($result);
  }
  print json_encode($rows);
  mysqli_close($link);
}

?>
