--
-- Table structure for table `journals-activities`
--

CREATE TABLE IF NOT EXISTS `journals-activities` (
	`date` DATE,
	`time` TIME,
	`subject_email` VARCHAR(250),
	`subject_subscriber_id` VARCHAR(50),
	`subject_customer_id` VARCHAR(50),
	`action` VARCHAR(50),
	`object_type` VARCHAR(100),
	`object_id` VARCHAR(50),
	`object_name` VARCHAR(500),
	`object_customer_id` VARCHAR(50),
	`target_type` VARCHAR(100),
	`target_id` VARCHAR(50),
	`target_name` VARCHAR(500),
	`target_customer_id` VARCHAR(50),
	`outcome` VARCHAR(50),
	`reason` VARCHAR(50),
	`extra` VARCHAR(500),
	KEY `date` (`date`),
	KEY `time` (`time`),
	KEY `subject_email` (`subject_email`),
	KEY `subject_subscriber_id` (`subject_subscriber_id`),
	KEY `subject_customer_id` (`subject_customer_id`),
	KEY `action` (`action`),
	KEY `object_type` (`object_type`),
	KEY `object_id` (`object_id`),
	KEY `object_name` (`object_name`),
	KEY `object_customer_id` (`object_customer_id`),
	KEY `target_type` (`target_type`),
	KEY `target_id` (`target_id`),
	KEY `target_name` (`target_name`),
	KEY `target_customer_id` (`target_customer_id`),
	KEY `outcome` (`outcome`),
	KEY `reason` (`reason`),
	KEY `extra` (`extra`)
);

--
-- Table structure for table `journals-announcements`
--

CREATE TABLE IF NOT EXISTS `journals-announcements` (
	`date` DATE,
	`time` TIME,
	`subject_email` VARCHAR(250),
	`subject_subscriber_id` VARCHAR(50),
	`subject_customer_id` VARCHAR(50),
	`action` VARCHAR(50),
	`object_type` VARCHAR(100),
	`object_id` VARCHAR(50),
	`object_name` VARCHAR(500),
	`object_customer_id` VARCHAR(50),
	`outcome` VARCHAR(50),
	`reason` VARCHAR(50),
	`extra` VARCHAR(500),
	KEY `date` (`date`),
	KEY `time` (`time`),
	KEY `subject_email` (`subject_email`),
	KEY `subject_subscriber_id` (`subject_subscriber_id`),
	KEY `subject_customer_id` (`subject_customer_id`),
	KEY `action` (`action`),
	KEY `object_type` (`object_type`),
	KEY `object_id` (`object_id`),
	KEY `object_name` (`object_name`),
	KEY `object_customer_id` (`object_customer_id`),
	KEY `outcome` (`outcome`),
	KEY `reason` (`reason`),
	KEY `extra` (`extra`)
);

--
-- Table structure for table `journals-authentication`
--

CREATE TABLE IF NOT EXISTS `journals-authentication` (
	`date` DATE,
	`time` TIME,
	`subject_email` VARCHAR(250),
	`subject_subscriber_id` VARCHAR(50),
	`subject_customer_id` VARCHAR(50),
	`action` VARCHAR(50),
	`outcome` VARCHAR(50),
	`reason` VARCHAR(50),
	KEY `date` (`date`),
	KEY `time` (`time`),
	KEY `subject_email` (`subject_email`),
	KEY `subject_subscriber_id` (`subject_subscriber_id`),
	KEY `subject_customer_id` (`subject_customer_id`),
	KEY `action` (`action`),
	KEY `outcome` (`outcome`),
	KEY `reason` (`reason`)
);

--
-- Table structure for table `journals-blogs`
--

CREATE TABLE IF NOT EXISTS `journals-blogs` (
	`date` DATE,
	`time` TIME,
	`subject_email` VARCHAR(250),
	`subject_subscriber_id` VARCHAR(50),
	`subject_customer_id` VARCHAR(50),
	`action` VARCHAR(50),
	`object_type` VARCHAR(100),
	`object_id` VARCHAR(50),
	`object_name` VARCHAR(500),
	`object_customer_id` VARCHAR(50),
	`target_type` VARCHAR(100),
	`target_id` VARCHAR(50),
	`target_name` VARCHAR(500),
	`target_customer_id` VARCHAR(50),
	`outcome` VARCHAR(50),
	`reason` VARCHAR(50),
	`extra` VARCHAR(500),
	KEY `date` (`date`),
	KEY `time` (`time`),
	KEY `subject_email` (`subject_email`),
	KEY `subject_subscriber_id` (`subject_subscriber_id`),
	KEY `subject_customer_id` (`subject_customer_id`),
	KEY `action` (`action`),
	KEY `object_type` (`object_type`),
	KEY `object_id` (`object_id`),
	KEY `object_name` (`object_name`),
	KEY `object_customer_id` (`object_customer_id`),
	KEY `target_type` (`target_type`),
	KEY `target_id` (`target_id`),
	KEY `target_name` (`target_name`),
	KEY `target_customer_id` (`target_customer_id`),
	KEY `outcome` (`outcome`),
	KEY `reason` (`reason`),
	KEY `extra` (`extra`)
);

--
-- Table structure for table `journals-bss`
--

CREATE TABLE IF NOT EXISTS `journals-bss` (
	`date` DATE,
	`time` TIME,
	`subject_email` VARCHAR(250),
	`subject_subscriber_id` VARCHAR(50),
	`subject_customer_id` VARCHAR(50),
	`action` VARCHAR(50),
	`object_type` VARCHAR(100),
	`object_id` VARCHAR(50),
	`object_name` VARCHAR(500),
	`object_customer_id` VARCHAR(50),
	`target_type` VARCHAR(100),
	`target_id` VARCHAR(50),
	`target_name` VARCHAR(500),
	`target_customer_id` VARCHAR(50),
	`outcome` VARCHAR(50),
	`reason` VARCHAR(50),
	`extra` VARCHAR(500),
	KEY `date` (`date`),
	KEY `time` (`time`),
	KEY `subject_email` (`subject_email`),
	KEY `subject_subscriber_id` (`subject_subscriber_id`),
	KEY `subject_customer_id` (`subject_customer_id`),
	KEY `action` (`action`),
	KEY `object_type` (`object_type`),
	KEY `object_id` (`object_id`),
	KEY `object_name` (`object_name`),
	KEY `object_customer_id` (`object_customer_id`),
	KEY `target_type` (`target_type`),
	KEY `target_id` (`target_id`),
	KEY `target_name` (`target_name`),
	KEY `target_customer_id` (`target_customer_id`),
	KEY `outcome` (`outcome`),
	KEY `reason` (`reason`),
	KEY `extra` (`extra`)
);

--
-- Table structure for table `journals-communities`
--

CREATE TABLE IF NOT EXISTS `journals-communities` (
	`date` DATE,
	`time` TIME,
	`subject_email` VARCHAR(250),
	`subject_subscriber_id` VARCHAR(50),
	`subject_customer_id` VARCHAR(50),
	`action` VARCHAR(50),
	`object_type` VARCHAR(100),
	`object_id` VARCHAR(50),
	`object_name` VARCHAR(500),
	`object_customer_id` VARCHAR(50),
	`target_type` VARCHAR(100),
	`target_id` VARCHAR(50),
	`target_name` VARCHAR(500),
	`target_customer_id` VARCHAR(50),
	`outcome` VARCHAR(50),
	`reason` VARCHAR(50),
	`extra` VARCHAR(500),
	KEY `date` (`date`),
	KEY `time` (`time`),
	KEY `subject_email` (`subject_email`),
	KEY `subject_subscriber_id` (`subject_subscriber_id`),
	KEY `subject_customer_id` (`subject_customer_id`),
	KEY `action` (`action`),
	KEY `object_type` (`object_type`),
	KEY `object_id` (`object_id`),
	KEY `object_name` (`object_name`),
	KEY `object_customer_id` (`object_customer_id`),
	KEY `target_type` (`target_type`),
	KEY `target_id` (`target_id`),
	KEY `target_name` (`target_name`),
	KEY `target_customer_id` (`target_customer_id`),
	KEY `outcome` (`outcome`),
	KEY `reason` (`reason`),
	KEY `extra` (`extra`)
);

--
-- Table structure for table `journals-company_administration`
--

CREATE TABLE IF NOT EXISTS `journals-company_administration` (
	`date` DATE,
	`time` TIME,
	`subject_email` VARCHAR(250),
	`subject_subscriber_id` VARCHAR(50),
	`subject_customer_id` VARCHAR(50),
	`action` VARCHAR(50),
	`object_type` VARCHAR(100),
	`object_id` VARCHAR(50),
	`object_name` VARCHAR(500),
	`object_customer_id` VARCHAR(50),
	`target_type` VARCHAR(100),
	`target_id` VARCHAR(50),
	`target_name` VARCHAR(500),
	`target_customer_id` VARCHAR(50),
	`outcome` VARCHAR(50),
	`reason` VARCHAR(50),
	`extra` VARCHAR(500),
	KEY `date` (`date`),
	KEY `time` (`time`),
	KEY `subject_email` (`subject_email`),
	KEY `subject_subscriber_id` (`subject_subscriber_id`),
	KEY `subject_customer_id` (`subject_customer_id`),
	KEY `action` (`action`),
	KEY `object_type` (`object_type`),
	KEY `object_id` (`object_id`),
	KEY `object_name` (`object_name`),
	KEY `object_customer_id` (`object_customer_id`),
	KEY `target_type` (`target_type`),
	KEY `target_id` (`target_id`),
	KEY `target_name` (`target_name`),
	KEY `target_customer_id` (`target_customer_id`),
	KEY `outcome` (`outcome`),
	KEY `reason` (`reason`),
	KEY `extra` (`extra`)
);

--
-- Table structure for table `journals-contacts`
--

CREATE TABLE IF NOT EXISTS `journals-contacts` (
	`date` DATE,
	`time` TIME,
	`subject_email` VARCHAR(250),
	`subject_subscriber_id` VARCHAR(50),
	`subject_customer_id` VARCHAR(50),
	`action` VARCHAR(50),
	`object_type` VARCHAR(100),
	`object_id` VARCHAR(50),
	`object_name` VARCHAR(500),
	`object_customer_id` VARCHAR(50),
	`outcome` VARCHAR(50),
	`reason` VARCHAR(50),
	KEY `date` (`date`),
	KEY `time` (`time`),
	KEY `subject_email` (`subject_email`),
	KEY `subject_subscriber_id` (`subject_subscriber_id`),
	KEY `subject_customer_id` (`subject_customer_id`),
	KEY `action` (`action`),
	KEY `object_type` (`object_type`),
	KEY `object_id` (`object_id`),
	KEY `object_name` (`object_name`),
	KEY `object_customer_id` (`object_customer_id`),
	KEY `outcome` (`outcome`),
	KEY `reason` (`reason`)
);

--
-- Table structure for table `journals-files`
--

CREATE TABLE IF NOT EXISTS `journals-files` (
	`date` DATE,
	`time` TIME,
	`subject_email` VARCHAR(250),
	`subject_subscriber_id` VARCHAR(50),
	`subject_customer_id` VARCHAR(50),
	`action` VARCHAR(50),
	`object_type` VARCHAR(100),
	`object_id` VARCHAR(50),
	`object_name` VARCHAR(500),
	`object_customer_id` VARCHAR(50),
	`target_type` VARCHAR(100),
	`target_id` VARCHAR(50),
	`target_name` VARCHAR(500),
	`target_customer_id` VARCHAR(50),
	`outcome` VARCHAR(50),
	`reason` VARCHAR(50),
	`extra` VARCHAR(500),
	KEY `date` (`date`),
	KEY `time` (`time`),
	KEY `subject_email` (`subject_email`),
	KEY `subject_subscriber_id` (`subject_subscriber_id`),
	KEY `subject_customer_id` (`subject_customer_id`),
	KEY `action` (`action`),
	KEY `object_type` (`object_type`),
	KEY `object_id` (`object_id`),
	KEY `object_name` (`object_name`),
	KEY `object_customer_id` (`object_customer_id`),
	KEY `target_type` (`target_type`),
	KEY `target_id` (`target_id`),
	KEY `target_name` (`target_name`),
	KEY `target_customer_id` (`target_customer_id`),
	KEY `outcome` (`outcome`),
	KEY `reason` (`reason`),
	KEY `extra` (`extra`)
);

--
-- Table structure for table `journals-forms`
--

CREATE TABLE IF NOT EXISTS `journals-forms` (
	`date` DATE,
	`time` TIME,
	`subject_email` VARCHAR(250),
	`subject_subscriber_id` VARCHAR(50),
	`subject_customer_id` VARCHAR(50),
	`action` VARCHAR(50),
	`object_type` VARCHAR(100),
	`object_id` VARCHAR(50),
	`object_name` VARCHAR(500),
	`object_customer_id` VARCHAR(50),
	`target_type` VARCHAR(100),
	`target_id` VARCHAR(50),
	`target_name` VARCHAR(500),
	`target_customer_id` VARCHAR(50),
	`outcome` VARCHAR(50),
	`reason` VARCHAR(50),
	`extra` VARCHAR(500),
	KEY `date` (`date`),
	KEY `time` (`time`),
	KEY `subject_email` (`subject_email`),
	KEY `subject_subscriber_id` (`subject_subscriber_id`),
	KEY `subject_customer_id` (`subject_customer_id`),
	KEY `action` (`action`),
	KEY `object_type` (`object_type`),
	KEY `object_id` (`object_id`),
	KEY `object_name` (`object_name`),
	KEY `object_customer_id` (`object_customer_id`),
	KEY `target_type` (`target_type`),
	KEY `target_id` (`target_id`),
	KEY `target_name` (`target_name`),
	KEY `target_customer_id` (`target_customer_id`),
	KEY `outcome` (`outcome`),
	KEY `reason` (`reason`),
	KEY `extra` (`extra`)
);

--
-- Table structure for table `journals-forums`
--

CREATE TABLE IF NOT EXISTS `journals-forums` (
	`date` DATE,
	`time` TIME,
	`subject_email` VARCHAR(250),
	`subject_subscriber_id` VARCHAR(50),
	`subject_customer_id` VARCHAR(50),
	`action` VARCHAR(50),
	`object_type` VARCHAR(100),
	`object_id` VARCHAR(50),
	`object_name` VARCHAR(500),
	`object_customer_id` VARCHAR(50),
	`target_type` VARCHAR(100),
	`target_id` VARCHAR(50),
	`target_name` VARCHAR(500),
	`target_customer_id` VARCHAR(50),
	`outcome` VARCHAR(50),
	`reason` VARCHAR(50),
	`extra` VARCHAR(500),
	KEY `date` (`date`),
	KEY `time` (`time`),
	KEY `subject_email` (`subject_email`),
	KEY `subject_subscriber_id` (`subject_subscriber_id`),
	KEY `subject_customer_id` (`subject_customer_id`),
	KEY `action` (`action`),
	KEY `object_type` (`object_type`),
	KEY `object_id` (`object_id`),
	KEY `object_name` (`object_name`),
	KEY `object_customer_id` (`object_customer_id`),
	KEY `target_type` (`target_type`),
	KEY `target_id` (`target_id`),
	KEY `target_name` (`target_name`),
	KEY `target_customer_id` (`target_customer_id`),
	KEY `outcome` (`outcome`),
	KEY `reason` (`reason`),
	KEY `extra` (`extra`)
);

--
-- Table structure for table `journals-inotes`
--

CREATE TABLE IF NOT EXISTS `journals-inotes` (
	`date` DATE,
	`time` TIME,
	`subject_email` VARCHAR(250),
	`subject_subscriber_id` VARCHAR(50),
	`subject_customer_id` VARCHAR(50),
	`action` VARCHAR(50),
	`outcome` VARCHAR(50),
	`reason` VARCHAR(50),
	KEY `date` (`date`),
	KEY `time` (`time`),
	KEY `subject_email` (`subject_email`),
	KEY `subject_subscriber_id` (`subject_subscriber_id`),
	KEY `subject_customer_id` (`subject_customer_id`),
	KEY `action` (`action`),
	KEY `outcome` (`outcome`),
	KEY `reason` (`reason`)
);

--
-- Table structure for table `journals-meetings`
--

CREATE TABLE IF NOT EXISTS `journals-meetings` (
	`date` DATE,
	`time` TIME,
	`subject_email` VARCHAR(250),
	`subject_subscriber_id` VARCHAR(50),
	`subject_customer_id` VARCHAR(50),
	`action` VARCHAR(50),
	`object_type` VARCHAR(100),
	`object_id` VARCHAR(50),
	`object_name` VARCHAR(500),
	`object_customer_id` VARCHAR(50),
	`target_type` VARCHAR(100),
	`target_id` VARCHAR(50),
	`target_name` VARCHAR(500),
	`target_customer_id` VARCHAR(50),
	`outcome` VARCHAR(50),
	`reason` VARCHAR(50),
	`extra` VARCHAR(500),
	KEY `date` (`date`),
	KEY `time` (`time`),
	KEY `subject_email` (`subject_email`),
	KEY `subject_subscriber_id` (`subject_subscriber_id`),
	KEY `subject_customer_id` (`subject_customer_id`),
	KEY `action` (`action`),
	KEY `object_type` (`object_type`),
	KEY `object_id` (`object_id`),
	KEY `object_name` (`object_name`),
	KEY `object_customer_id` (`object_customer_id`),
	KEY `target_type` (`target_type`),
	KEY `target_id` (`target_id`),
	KEY `target_name` (`target_name`),
	KEY `target_customer_id` (`target_customer_id`),
	KEY `outcome` (`outcome`),
	KEY `reason` (`reason`),
	KEY `extra` (`extra`)
);

--
-- Table structure for table `journals-profiles`
--

CREATE TABLE IF NOT EXISTS `journals-profiles` (
	`date` DATE,
	`time` TIME,
	`subject_email` VARCHAR(250),
	`subject_subscriber_id` VARCHAR(50),
	`subject_customer_id` VARCHAR(50),
	`action` VARCHAR(50),
	`object_type` VARCHAR(100),
	`object_id` VARCHAR(50),
	`object_name` VARCHAR(500),
	`object_customer_id` VARCHAR(50),
	`target_type` VARCHAR(100),
	`target_id` VARCHAR(50),
	`target_name` VARCHAR(500),
	`target_customer_id` VARCHAR(50),
	`outcome` VARCHAR(50),
	`reason` VARCHAR(50),
	KEY `date` (`date`),
	KEY `time` (`time`),
	KEY `subject_email` (`subject_email`),
	KEY `subject_subscriber_id` (`subject_subscriber_id`),
	KEY `subject_customer_id` (`subject_customer_id`),
	KEY `action` (`action`),
	KEY `object_type` (`object_type`),
	KEY `object_id` (`object_id`),
	KEY `object_name` (`object_name`),
	KEY `object_customer_id` (`object_customer_id`),
	KEY `target_type` (`target_type`),
	KEY `target_id` (`target_id`),
	KEY `target_name` (`target_name`),
	KEY `target_customer_id` (`target_customer_id`),
	KEY `outcome` (`outcome`),
	KEY `reason` (`reason`)
);

--
-- Table structure for table `journals-sametime`
--

CREATE TABLE IF NOT EXISTS `journals-sametime` (
	`date` DATE,
	`time` TIME,
	`subject_email` VARCHAR(250),
	`subject_subscriber_id` VARCHAR(50),
	`subject_customer_id` VARCHAR(50),
	`action` VARCHAR(50),
	`target_type` VARCHAR(100),
	`target_id` VARCHAR(50),
	`target_name` VARCHAR(500),
	`target_customer_id` VARCHAR(50),
	`outcome` VARCHAR(50),
	`extra` VARCHAR(500),
	KEY `date` (`date`),
	KEY `time` (`time`),
	KEY `subject_email` (`subject_email`),
	KEY `subject_subscriber_id` (`subject_subscriber_id`),
	KEY `subject_customer_id` (`subject_customer_id`),
	KEY `action` (`action`),
	KEY `target_type` (`target_type`),
	KEY `target_id` (`target_id`),
	KEY `target_name` (`target_name`),
	KEY `target_customer_id` (`target_customer_id`),
	KEY `outcome` (`outcome`),
	KEY `extra` (`extra`)
);

--
-- Table structure for table `journals-surveys`
--

CREATE TABLE IF NOT EXISTS `journals-surveys` (
	`date` DATE,
	`time` TIME,
	`subject_email` VARCHAR(250),
	`subject_subscriber_id` VARCHAR(50),
	`subject_customer_id` VARCHAR(50),
	`action` VARCHAR(50),
	`object_type` VARCHAR(100),
	`object_id` VARCHAR(50),
	`object_name` VARCHAR(500),
	`object_customer_id` VARCHAR(50),
	`target_type` VARCHAR(100),
	`target_id` VARCHAR(50),
	`target_name` VARCHAR(500),
	`target_customer_id` VARCHAR(50),
	`outcome` VARCHAR(50),
	`reason` VARCHAR(50),
	KEY `date` (`date`),
	KEY `time` (`time`),
	KEY `subject_email` (`subject_email`),
	KEY `subject_subscriber_id` (`subject_subscriber_id`),
	KEY `subject_customer_id` (`subject_customer_id`),
	KEY `action` (`action`),
	KEY `object_type` (`object_type`),
	KEY `object_id` (`object_id`),
	KEY `object_name` (`object_name`),
	KEY `object_customer_id` (`object_customer_id`),
	KEY `target_type` (`target_type`),
	KEY `target_id` (`target_id`),
	KEY `target_name` (`target_name`),
	KEY `target_customer_id` (`target_customer_id`),
	KEY `outcome` (`outcome`),
	KEY `reason` (`reason`)
);

--
-- Table structure for table `journals-theming`
--

CREATE TABLE IF NOT EXISTS `journals-theming` (
	`date` DATE,
	`time` TIME,
	`subject_email` VARCHAR(250),
	`subject_subscriber_id` VARCHAR(50),
	`subject_customer_id` VARCHAR(50),
	`action` VARCHAR(50),
	`object_type` VARCHAR(100),
	`object_id` VARCHAR(50),
	`object_name` VARCHAR(500),
	`object_customer_id` VARCHAR(50),
	`outcome` VARCHAR(50),
	`reason` VARCHAR(50),
	`extra` VARCHAR(500),
	KEY `date` (`date`),
	KEY `time` (`time`),
	KEY `subject_email` (`subject_email`),
	KEY `subject_subscriber_id` (`subject_subscriber_id`),
	KEY `subject_customer_id` (`subject_customer_id`),
	KEY `action` (`action`),
	KEY `object_type` (`object_type`),
	KEY `object_id` (`object_id`),
	KEY `object_name` (`object_name`),
	KEY `object_customer_id` (`object_customer_id`),
	KEY `outcome` (`outcome`),
	KEY `reason` (`reason`),
	KEY `extra` (`extra`)
);

--
-- Table structure for table `journals-wikis`
--

CREATE TABLE IF NOT EXISTS `journals-wikis` (
	`date` DATE,
	`time` TIME,
	`subject_email` VARCHAR(250),
	`subject_subscriber_id` VARCHAR(50),
	`subject_customer_id` VARCHAR(50),
	`action` VARCHAR(50),
	`object_type` VARCHAR(100),
	`object_id` VARCHAR(50),
	`object_name` VARCHAR(500),
	`object_customer_id` VARCHAR(50),
	`target_type` VARCHAR(100),
	`target_id` VARCHAR(50),
	`target_name` VARCHAR(500),
	`target_customer_id` VARCHAR(50),
	`outcome` VARCHAR(50),
	`reason` VARCHAR(50),
	`extra` VARCHAR(500),
	KEY `date` (`date`),
	KEY `time` (`time`),
	KEY `subject_email` (`subject_email`),
	KEY `subject_subscriber_id` (`subject_subscriber_id`),
	KEY `subject_customer_id` (`subject_customer_id`),
	KEY `action` (`action`),
	KEY `object_type` (`object_type`),
	KEY `object_id` (`object_id`),
	KEY `object_name` (`object_name`),
	KEY `object_customer_id` (`object_customer_id`),
	KEY `target_type` (`target_type`),
	KEY `target_id` (`target_id`),
	KEY `target_name` (`target_name`),
	KEY `target_customer_id` (`target_customer_id`),
	KEY `outcome` (`outcome`),
	KEY `reason` (`reason`),
	KEY `extra` (`extra`)
);
