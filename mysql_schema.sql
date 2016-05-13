-- MySQL dump 10.14  Distrib 5.5.47-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: qbic_usermanagement_db
-- ------------------------------------------------------
-- Server version	5.5.47-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `qbic_usermanagement_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `qbic_usermanagement_db` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `qbic_usermanagement_db`;

--
-- Table structure for table `experiments`
--

DROP TABLE IF EXISTS `experiments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `experiments` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `openbis_experiment_identifier` varchar(64) NOT NULL DEFAULT '' COMMENT 'full openBIS identifier: /SPACE_CODE/PROJECT_CODE/EXPERIMENT_CODE/',
  PRIMARY KEY (`id`),
  KEY `openbis_experiment_identifier` (`openbis_experiment_identifier`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `experiments_persons`
--

DROP TABLE IF EXISTS `experiments_persons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `experiments_persons` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `experiment_id` int(11) unsigned NOT NULL DEFAULT '0',
  `person_id` int(11) unsigned NOT NULL DEFAULT '0',
  `experiment_role` set('Analyst','Contact') NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `person_id` (`person_id`),
  KEY `experiment_id` (`experiment_id`),
  CONSTRAINT `experiments_persons_ibfk_1` FOREIGN KEY (`person_id`) REFERENCES `persons` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
--
-- Table structure for table `organizations`
--

DROP TABLE IF EXISTS `organizations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organizations` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `group_name` varchar(64) DEFAULT NULL,
  `group_acronym` varchar(6) DEFAULT NULL,
  `umbrella_organization` varchar(64) DEFAULT NULL,
  `institute` varchar(128) DEFAULT NULL,
  `faculty` enum('Faculty of Science','Medical Faculty','Central Units','Other') DEFAULT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `persons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;


DROP TABLE IF EXISTS `persons_organizations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `persons_organizations` (
  `person_id` int(11) unsigned NOT NULL,
  `organization_id` int(11) unsigned NOT NULL,
  `occupation` enum('Head','Member','Doctoral Student','Secretary','Technical Assistant') DEFAULT 'Member',
  PRIMARY KEY (`person_id`,`organization_id`),
  KEY `organization_id` (`organization_id`),
  CONSTRAINT `persons_organizations_ibfk_1` FOREIGN KEY (`person_id`) REFERENCES `persons` (`id`),
  CONSTRAINT `persons_organizations_ibfk_2` FOREIGN KEY (`organization_id`) REFERENCES `organizations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `projects`
--

DROP TABLE IF EXISTS `projects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `projects` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `openbis_project_identifier` varchar(64) NOT NULL DEFAULT '' COMMENT 'full openBIS identifier: /SPACE_CODE/SAMPLE_CODE',
  `short_title` varchar(180) DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `openbis_project_identifier` (`openbis_project_identifier`)
) ENGINE=InnoDB AUTO_INCREMENT=354 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `projects_persons`
--

DROP TABLE IF EXISTS `projects_persons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `projects_persons` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) unsigned NOT NULL DEFAULT '0',
  `person_id` int(11) unsigned NOT NULL DEFAULT '0',
  `project_role` set('PI','Contact') NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `person_id` (`person_id`),
  KEY `project_id` (`project_id`),
  CONSTRAINT `projects_persons_ibfk_2` FOREIGN KEY (`person_id`) REFERENCES `persons` (`id`),
  CONSTRAINT `projects_persons_ibfk_3` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=512 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-05-11 14:29:02
