# ************************************************************
# Sequel Pro SQL dump
# Version 4500
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: portal-testing.am10.uni-tuebingen.de (MySQL 5.5.52-MariaDB)
# Database: qbic_usermanagement_db
# Generation Time: 2017-04-06 14:41:04 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table experiments
# ------------------------------------------------------------

CREATE TABLE `experiments` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `openbis_experiment_identifier` varchar(64) NOT NULL DEFAULT '' COMMENT 'full openBIS identifier: /SPACE_CODE/PROJECT_CODE/EXPERIMENT_CODE/',
  PRIMARY KEY (`id`),
  KEY `openbis_experiment_identifier` (`openbis_experiment_identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



# Dump of table experiments_persons
# ------------------------------------------------------------

CREATE TABLE `experiments_persons` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `experiment_id` int(11) unsigned NOT NULL DEFAULT '0',
  `person_id` int(11) unsigned NOT NULL DEFAULT '0',
  `experiment_role` set('Analyst','Contact') NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `person_id` (`person_id`),
  KEY `experiment_id` (`experiment_id`),
  CONSTRAINT `experiments_persons_ibfk_1` FOREIGN KEY (`person_id`) REFERENCES `persons` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



# Dump of table labelprinter
# ------------------------------------------------------------

CREATE TABLE `labelprinter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `location` varchar(250) NOT NULL,
  `url` varchar(250) NOT NULL,
  `status` varchar(45) NOT NULL,
  `type` set('LABEL PRINTER','A4 PRINTER') NOT NULL DEFAULT '',
  `admin_only` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table organizations
# ------------------------------------------------------------

CREATE TABLE `organizations` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `group_name` varchar(64) DEFAULT NULL,
  `group_acronym` varchar(6) DEFAULT NULL,
  `umbrella_organization` varchar(64) DEFAULT NULL,
  `institute` varchar(128) DEFAULT NULL,
  `faculty` enum('Faculty of Science','Medical Faculty','Central Units','Other','Ext. Industry','Ext. Academics') DEFAULT NULL,
  `head` int(10) unsigned DEFAULT NULL,
  `main_contact` int(11) unsigned DEFAULT NULL,
  `street` varchar(64) DEFAULT NULL,
  `zip_code` int(5) DEFAULT NULL,
  `city` varchar(64) DEFAULT NULL,
  `country` varchar(64) DEFAULT NULL,
  `webpage` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `main_contact` (`main_contact`),
  KEY `head` (`head`),
  CONSTRAINT `organizations_ibfk_1` FOREIGN KEY (`main_contact`) REFERENCES `persons` (`id`),
  CONSTRAINT `organizations_ibfk_2` FOREIGN KEY (`head`) REFERENCES `persons` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table persons
# ------------------------------------------------------------

CREATE TABLE `persons` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(8) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `title` enum('Dr.','Prof.','Herr','Frau','Mr.','Ms.') CHARACTER SET utf8 DEFAULT NULL,
  `first_name` varchar(35) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `family_name` varchar(35) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `email` varchar(64) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `phone` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;



# Dump of table persons_organizations
# ------------------------------------------------------------

CREATE TABLE `persons_organizations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `person_id` int(11) unsigned NOT NULL,
  `organization_id` int(11) unsigned NOT NULL,
  `occupation` enum('Head','Member','Doctoral Student','Secretary','Technical Assistant') DEFAULT 'Member',
  PRIMARY KEY (`id`),
  KEY `organization_id` (`organization_id`),
  KEY `person_id` (`person_id`),
  CONSTRAINT `persons_organizations_ibfk_2` FOREIGN KEY (`organization_id`) REFERENCES `organizations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table printer_project_association
# ------------------------------------------------------------

CREATE TABLE `printer_project_association` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `printer_id` int(11) NOT NULL,
  `project_id` int(11) NOT NULL,
  `status` set('ACTIVE','INACTIVE') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_printer_project_association_1_idx` (`printer_id`),
  KEY `project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table projects
# ------------------------------------------------------------

CREATE TABLE `projects` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `openbis_project_identifier` varchar(64) NOT NULL DEFAULT '' COMMENT 'full openBIS identifier: /SPACE_CODE/SAMPLE_CODE',
  `short_title` varchar(180) DEFAULT '',
  `long_description` mediumtext,
  PRIMARY KEY (`id`),
  KEY `openbis_project_identifier` (`openbis_project_identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table projects_persons
# ------------------------------------------------------------

CREATE TABLE `projects_persons` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) unsigned NOT NULL DEFAULT '0',
  `person_id` int(11) unsigned NOT NULL DEFAULT '0',
  `project_role` set('PI','Contact','Manager') NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `person_id` (`person_id`),
  KEY `project_id` (`project_id`),
  CONSTRAINT `projects_persons_ibfk_2` FOREIGN KEY (`person_id`) REFERENCES `persons` (`id`),
  CONSTRAINT `projects_persons_ibfk_3` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
