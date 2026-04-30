-- MySQL dump 10.13  Distrib 8.0.45, for Linux (x86_64)
--
-- Host: localhost    Database: gosmart
-- ------------------------------------------------------
-- Server version       8.0.45-0ubuntu0.22.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `gosmart`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `gosmart` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `gosmart`;

--
-- Table structure for table `application_system`
--

DROP TABLE IF EXISTS `application_system`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `application_system` (
  `application_system_id` bigint NOT NULL AUTO_INCREMENT,
  `system_code` varchar(50) NOT NULL,
  `system_name` varchar(100) NOT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`application_system_id`),
  UNIQUE KEY `uk_function_group_code` (`system_code`),
  KEY `idx_function_group_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application_system`
--

LOCK TABLES `application_system` WRITE;
/*!40000 ALTER TABLE `application_system` DISABLE KEYS */;
INSERT INTO `application_system` (`application_system_id`, `system_code`, `system_name`, `remark`, `status`) VALUES (1,'SIS1','System Blue',NULL,'A'),(2,'SIS2','System Red',NULL,'A'),(3,'SIS3','System Yellow',NULL,'A');
/*!40000 ALTER TABLE `application_system` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `function`
--

DROP TABLE IF EXISTS `function`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `function` (
  `function_id` bigint NOT NULL AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `function_code` varchar(50) NOT NULL,
  `function_name` varchar(100) NOT NULL,
  `path` varchar(255) NOT NULL,
  `sort_order` int DEFAULT NULL,
  `remark` varchar(250) DEFAULT NULL,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`function_id`),
  UNIQUE KEY `uk_function_group_code` (`group_id`,`function_code`),
  KEY `idx_function_code` (`function_code`),
  KEY `idx_function_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `function`
--

LOCK TABLES `function` WRITE;
/*!40000 ALTER TABLE `function` DISABLE KEYS */;
INSERT INTO `function` 
(`function_id`,`group_id`,`function_code`,`function_name`,`path`,`sort_order`,`remark`,`status`) 
VALUES
(2001,201,'PAT_REG','Registration','/patient/registration',1,'','A'),
(2002,201,'PAT_SUB','Package Subscription','/patient/subscription',2,'','A'),
(2003,201,'PAT_MED','Medical Record','/patient/medical-record',3,'','A'),
(2004,202,'CGV_REG','Registration','/caregiver/registration',1,' ','A'),
(2005,203,'APPT_REG','Registration','/appointment/registration',1,'','A'),
(2006,204,'PAYMENT','Payment','/billing/payment',1,'','A'),
(2007,205,'ANALYTICS','Analytics','/dashboard/analytics',1,'','A'),
(2008,201,'ASSIGN_CAREGIVER','Assign Caregiver','/patient/patient-caregiver',4,NULL,'A');
/*!40000 ALTER TABLE `function` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `function_group`
--

DROP TABLE IF EXISTS `function_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `function_group` (
  `group_id` bigint NOT NULL AUTO_INCREMENT,
  `application_system_id` bigint NOT NULL,
  `group_code` varchar(50) NOT NULL,
  `group_name` varchar(100) NOT NULL,
  `sort_order` int DEFAULT NULL,
  `remark` varchar(250) DEFAULT NULL,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`group_id`),
  UNIQUE KEY `uk_function_application_system_group_code` (`application_system_id`,`group_code`),
  KEY `idx_function_group_code` (`group_code`),
  KEY `idx_function_group_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `function_group`
--

LOCK TABLES `function_group` WRITE;
/*!40000 ALTER TABLE `function_group` DISABLE KEYS */;
INSERT INTO `function_group` (`group_id`, `application_system_id`, `group_code`, `group_name`, `sort_order`, `remark`, `status`) VALUES (1,1,'BLUEPA1','Parent Blue A1',1,NULL,'A'),(2,1,'BLUEPA2','Parent Blue A2',2,NULL,'A'),(6,2,'REDP1','Parent Red A1',1,NULL,'A'),(7,3,'YELLOWPA1','Parent Yellow A1',1,NULL,'A'),(8,3,'YELLOWPA2','Parent Yellow A2',2,NULL,'A'),(9,3,'YELLOWPA3','Parent Yellow A3',3,NULL,'A');
/*!40000 ALTER TABLE `function_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_function`
--

DROP TABLE IF EXISTS `user_function`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_function` (
  `user_function_id` bigint NOT NULL AUTO_INCREMENT,
  `function_id` bigint NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `create` char(1) NOT NULL DEFAULT 'Y',
  `edit` char(1) NOT NULL DEFAULT 'Y',
  `delete` char(1) NOT NULL DEFAULT 'Y',
  `view` char(1) NOT NULL DEFAULT 'Y',
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`user_function_id`),
  UNIQUE KEY `uk_user_function_user_id_function` (`user_id`,`function_id`),
  KEY `idx_user_function_function_id` (`function_id`),
  KEY `idx_user_function_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_function`
--

LOCK TABLES `user_function` WRITE;
/*!40000 ALTER TABLE `user_function` DISABLE KEYS */;
INSERT INTO `user_function` (`user_function_id`, `function_id`, `user_id`, `create`, `edit`, `delete`, `view`, `status`) VALUES (1,1,'tanc08@gmail.com','Y','Y','Y','Y','A'),(2,2,'tanc08@gmail.com','Y','Y','N','Y','A'),(3,3,'tanc08@gmail.com','N','N','N','Y','A'),(4,4,'tanc08@gmail.com','Y','N','N','Y','A'),(5,10,'tanc08@gmail.com','N','Y','N','Y','A'),(6,11,'tanc08@gmail.com','N','N','Y','Y','A'),(7,12,'tanc08@gmail.com','Y','N','N','Y','A'),(8,13,'tanc08@gmail.com','N','N','N','Y','A'),(9,14,'tanc08@gmail.com','Y','Y','Y','Y','X');
/*!40000 ALTER TABLE `user_function` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_password`
--

DROP TABLE IF EXISTS `user_password`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_password` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_dt` timestamp NULL DEFAULT NULL,
  `modify_dt` timestamp NULL DEFAULT NULL,
  `user_id` varchar(255) NOT NULL,
  `hashed_pwd` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_password_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_password`
--

LOCK TABLES `user_password` WRITE;
/*!40000 ALTER TABLE `user_password` DISABLE KEYS */;
INSERT INTO `user_password` (`id`, `create_dt`, `modify_dt`, `user_id`, `hashed_pwd`) VALUES (1,'2026-03-27 00:00:00',NULL,'tanc08@gmail.com','xrJk6saY3kzGUOV4spRv+wYvvDpCF7Dp0TVud1Pg/dk='),(2,'2026-03-27 00:00:00',NULL,'tychuen88@gmail.com','KaOxvNzPxuriKtHWdeIxvz0TwgQ5Y/ngiJPA56C937o='),(3,'2026-03-27 00:00:00',NULL,'adriananuarkamal@gmail.com','kBNlP51VNIftGpeaGLgTkwPVTOdic1W/FBZ3pMRTQ1M=');
/*!40000 ALTER TABLE `user_password` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_session`
--

DROP TABLE IF EXISTS `user_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_session` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_dt` datetime NOT NULL,
  `last_activity_dt` datetime DEFAULT NULL,
  `expire_dt` datetime DEFAULT NULL,
  `revoke_dt` datetime DEFAULT NULL,
  `revoke_reason` varchar(100) DEFAULT NULL,
  `user_id` varchar(255) NOT NULL,
  `session_id` varchar(64) NOT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `user_agent` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_session_session_id` (`session_id`),
  KEY `idx_user_session_user_id` (`user_id`),
  KEY `idx_user_session_last_activity` (`last_activity_dt`),
  KEY `idx_user_session_expire_dt` (`expire_dt`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_session`
--

LOCK TABLES `user_session` WRITE;
/*!40000 ALTER TABLE `user_session` DISABLE KEYS */;
INSERT INTO `user_session` (`id`, `create_dt`, `last_activity_dt`, `expire_dt`, `revoke_dt`, `revoke_reason`, `user_id`, `session_id`, `ip_address`, `user_agent`) VALUES (40,'2026-03-30 00:01:39','2026-03-30 00:01:40','2026-03-30 00:06:40','2026-03-30 09:48:22','multi_login','tanc08@gmail.com','2d1f004d-7f9d-495b-be55-f841f2fb539e','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(41,'2026-03-30 09:48:22','2026-03-30 09:48:22','2026-03-30 09:53:22','2026-03-30 09:48:24','logout','tanc08@gmail.com','e8b2905b-ee08-4ead-94f5-093108956825','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(42,'2026-03-30 22:40:19','2026-03-30 22:40:21','2026-03-30 22:45:21','2026-03-30 22:42:42','logout','tanc08@gmail.com','7ae38696-a8f1-4c2e-b72d-cc857079ef93','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(43,'2026-03-30 22:56:06','2026-03-30 22:56:06','2026-03-30 23:01:06','2026-03-30 22:59:16','logout','tanc08@gmail.com','2aa9f694-df1a-4660-8109-65f8dc45be9d','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(44,'2026-03-30 22:59:18','2026-03-30 22:59:18','2026-03-30 23:04:18','2026-03-30 23:00:01','logout','tanc08@gmail.com','9196db4c-71ec-4c73-a905-04c115ec7843','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(45,'2026-03-30 23:00:03','2026-03-30 23:00:03','2026-03-30 23:05:03','2026-03-30 23:00:07','logout','tanc08@gmail.com','166b7c73-9ff2-49c8-afe5-6467c7e96f2a','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(46,'2026-03-30 23:00:10','2026-03-30 23:00:10','2026-03-30 23:05:10',NULL,NULL,'tychuen88@gmail.com','2630c7a7-61dd-4c94-a309-a0c27cee0546','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(47,'2026-03-31 00:22:55','2026-03-31 00:22:59','2026-03-31 00:27:59','2026-03-31 00:23:01','logout','tanc08@gmail.com','84fe3b90-316d-4f93-b8d7-f0ea60274ccb','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(48,'2026-03-31 00:23:46','2026-03-31 00:23:46','2026-03-31 00:28:46','2026-03-31 00:23:49','logout','tanc08@gmail.com','a5bca121-5af6-4426-b27d-f96756c25bd5','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(49,'2026-03-31 10:09:19','2026-03-31 10:09:19','2026-03-31 10:14:19','2026-03-31 10:19:45','multi_login','tanc08@gmail.com','e3ee2b8a-f8bf-481b-947b-77da7c892895','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(50,'2026-03-31 10:19:45','2026-03-31 10:19:46','2026-03-31 10:24:46','2026-03-31 11:22:46','multi_login','tanc08@gmail.com','14a2834e-4168-4a38-8c16-6844f9fc3d75','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(51,'2026-03-31 11:22:46','2026-03-31 11:22:47','2026-03-31 11:27:47','2026-03-31 11:25:47','logout','tanc08@gmail.com','4e60026b-edcc-43d1-8f10-9e5ac9cec7d3','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(52,'2026-03-31 11:25:51','2026-03-31 11:25:52','2026-03-31 11:30:52','2026-03-31 11:26:41','logout','tanc08@gmail.com','d32fbdbb-8128-4265-b42f-542810bd1898','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(53,'2026-03-31 11:26:47','2026-03-31 11:29:09','2026-03-31 11:34:09','2026-03-31 11:29:12','logout','tanc08@gmail.com','d3a496dc-70f7-489b-bbc9-8d269f1c19e5','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(54,'2026-03-31 11:29:13','2026-03-31 11:29:13','2026-03-31 11:34:13','2026-03-31 13:12:49','multi_login','tanc08@gmail.com','56862bdf-3bc5-4e3f-9455-9185f411ee93','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(55,'2026-03-31 13:12:49','2026-03-31 13:14:36','2026-03-31 13:19:36','2026-03-31 13:21:56','multi_login','tanc08@gmail.com','5b365b5c-b2fc-467f-a3d8-a551a964b276','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(56,'2026-03-31 13:21:56','2026-03-31 13:28:59','2026-03-31 13:33:59','2026-03-31 13:29:00','logout','tanc08@gmail.com','1952f410-2c44-4fca-a786-eff5a707bd9e','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(57,'2026-03-31 13:29:02','2026-03-31 13:29:14','2026-03-31 13:34:14','2026-03-31 13:29:15','logout','tanc08@gmail.com','afc2473d-7829-4a34-a06c-1ae60f537cda','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(58,'2026-03-31 13:30:23','2026-03-31 13:38:14','2026-03-31 13:43:14','2026-03-31 13:44:31','multi_login','tanc08@gmail.com','a055f59c-7d75-4dd5-a3c0-e4540ec5ccc5','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(59,'2026-03-31 13:44:31','2026-03-31 13:47:13','2026-03-31 13:52:13',NULL,NULL,'tanc08@gmail.com','d007c15e-53aa-4395-9b6a-a2ed818d6526','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0');
/*!40000 ALTER TABLE `user_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medical_provider`
--

DROP TABLE IF EXISTS `medical_provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE medical_provider (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_dt` datetime NOT NULL,
  `create_by` varchar(50) NOT NULL,
  `code` varchar(10) NOT NULL,
  `name` varchar(50) NOT NULL,
  `status` char(1) NOT NULL,
  PRIMARY KEY (id),
  KEY `uk_medical_provider_code` (`code`),
  KEY `un_medical_provider_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medical_provider`
--

LOCK TABLES `medical_provider` WRITE;
/*!40000 ALTER TABLE `medical_provider` DISABLE KEYS */;
INSERT INTO `medical_provider` (`id`, `create_dt`, `create_by`, `code`, `name`, `status`) VALUES (1,'2026-04-03 11:45:00','tanc08@gmail.com','KLNLEO','Klinik Leo','A'),(2,'2026-04-03 11:45:00','tanc08@gmail.com','KCC','Kiropraktis Chiropractic Center','A');
/*!40000 ALTER TABLE `medical_provider` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_medical_provider`
--

DROP TABLE IF EXISTS `user_medical_provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_medical_provider` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_dt` datetime NOT NULL,
  `create_by` varchar(100) NOT NULL,
  `modify_dt` datetime DEFAULT NULL,
  `modify_by` varchar(100) DEFAULT NULL,
  `user_id` int NOT NULL,
  `medical_provider_id` int NOT NULL,
  `status` char(1) NOT NULL DEFAULT 'Y',
  PRIMARY KEY (`id`),
  KEY `idx_user_medical_provider_user_id` (`user_id`),
  KEY `idx_user_medical_provider_provider_id` (`medical_provider_id`),
  KEY `idx_user_medical_provider_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `caregiver`
--

DROP TABLE IF EXISTS `caregiver`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `caregiver` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `medical_provider_id` int NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `mobile_number` varchar(20) NOT NULL,
  `patient_id` int NOT NULL,
  `create_dt` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_by` varchar(100) NOT NULL,
  `modify_dt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modify_by` varchar(100) DEFAULT NULL,
  `status` char(1) DEFAULT 'A',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `medical_package`
--

DROP TABLE IF EXISTS `medical_package`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medical_package` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_dt` datetime NOT NULL,
  `create_by` varchar(100) NOT NULL,
  `medical_provider_id` int NOT NULL,
  `code` varchar(10) NOT NULL,
  `name` varchar(50) NOT NULL,
  `amount_month` decimal(8,2) NOT NULL DEFAULT '0.00',
  `amount_year` decimal(8,2) NOT NULL DEFAULT '0.00',
  `status` char(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `uk_medical_provider_id_name` (`medical_provider_id`,`name`,`code`) /*!80000 INVISIBLE */,
  KEY `idx_code` (`code`) /*!80000 INVISIBLE */,
  KEY `idx_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medical_package`
--

LOCK TABLES `medical_package` WRITE;
/*!40000 ALTER TABLE `medical_package` DISABLE KEYS */;
INSERT INTO `medical_package` (`id`, `create_dt`, `create_by`, `medical_provider_id`, `code`, `name`, `amount_month`, `amount_year`, `status`) VALUES (2,'2026-04-14 13:30:00','tanc08@gmail.com',1,'CARE','Care',65.00,600.00,'A'),(3,'2026-04-14 13:30:00','tanc08@gmail.com',1,'CAREP','Care Plus',130.00,1450.00,'A'),(4,'2026-04-14 13:30:00','tanc08@gmail.com',1,'PREM','Premium',300.00,3450.00,'A');
/*!40000 ALTER TABLE `medical_package` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medical_package_detail`
--

DROP TABLE IF EXISTS `medical_package_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medical_package_detail` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_dt` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_by` varchar(100) NOT NULL,
  `medical_package_id` int NOT NULL,
  `description` varchar(255) NOT NULL,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`id`),
  KEY `idx_medical_package_id` (`medical_package_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medical_package_detail`
--

LOCK TABLES `medical_package_detail` WRITE;
/*!40000 ALTER TABLE `medical_package_detail` DISABLE KEYS */;
INSERT INTO `medical_package_detail` (`medical_package_id`, `description`) VALUES
(2, 'Emergency button -> clinic call back within 24 hours'),
(2, '2 consults per month'),
(2, 'Medication reminders'),
(2, 'Caregiver alerts'),
(3, 'Emergency button -> 4 hours response time'),
(3, '5 consults per month'),
(3, 'Chronic monitoring'),
(3, 'Monthly BP / sugar'),
(3, 'Priority booking'),
(3, 'Doctor home visit 5% discount'),
(3, 'Lab & imaging result review'),
(3, 'Monthly health report'),
(3, 'Medication reminders'),
(3, 'Caregiver alerts'),
(4, 'Unlimited consults'),
(4, 'Same-day GP'),
(4, 'Quarterly review'),
(4, 'Annual flu vaccine'),
(4, 'Full health screening'),
(4, 'Comprehensive blood test');
/*!40000 ALTER TABLE `medical_package_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `package_subscription`
--

DROP TABLE IF EXISTS `package_subscription`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `package_subscription` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_dt` datetime NOT NULL,
  `create_by` varchar(100) NOT NULL,
  `modify_dt` datetime DEFAULT NULL,
  `modify_by` varchar(100) DEFAULT NULL,
  `patient_id` int NOT NULL,
  `medical_provider_id` int NOT NULL,
  `medical_package_id` int NOT NULL,
  `amount` decimal(8,2) NOT NULL,
  `mode` char(1) NOT NULL,
  `expiration_dt` datetime NOT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `status` char(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_patient_id` (`patient_id`) /*!80000 INVISIBLE */,
  KEY `idx_medical_provider_id` (`medical_provider_id`),
  KEY `idx_medical_package_id` (`medical_package_id`),
  KEY `idx_expiration_dt` (`expiration_dt`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;


-- =========================================
-- PATIENT -> REGISTRATION
-- =========================================

DROP TABLE IF EXISTS `patient_medical_record`;
CREATE TABLE `patient_medical_record` (
  `id` int NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL,
  `bp_systolic` int DEFAULT NULL,
  `bp_diastolic` int DEFAULT NULL,
  `bp_recorded_at` datetime DEFAULT NULL,
  `pulse` int DEFAULT NULL,
  `pulse_recorded_at` datetime DEFAULT NULL,
  `sugar_level` decimal(10,2) DEFAULT NULL,
  `sugar_test_date` datetime DEFAULT NULL,
  `spo2` int DEFAULT NULL,
  `spo2_recorded_at` datetime DEFAULT NULL,
  `temperature` decimal(5,1) DEFAULT NULL,
  `temperature_recorded_at` datetime DEFAULT NULL,
  `pain_score` int DEFAULT NULL,
  `pain_score_recorded_at` datetime DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `create_dt` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_by` varchar(100) NOT NULL,
  `modify_dt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modify_by` varchar(100) DEFAULT NULL,
  `status` char(1) DEFAULT 'A',
  PRIMARY KEY (`id`),
  KEY `idx_patient_medical_record_patient` (`patient_id`),
  KEY `idx_patient_medical_record_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `patient_registration` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_dt` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_by` varchar(100) NOT NULL,
  `medical_provider_id` int NOT NULL,
  `name` varchar(100) NOT NULL,
  `age` int DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `race` varchar(10) DEFAULT NULL,
  `ic_passport_no` varchar(20) NOT NULL,
  `mobile_no` varchar(20) DEFAULT NULL,
  `emergency_contact_name` varchar(100) DEFAULT NULL,
  `emergency_contact_no` varchar(20) DEFAULT NULL,
  `relationship` varchar(50) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `area` varchar(100) DEFAULT NULL,
  `postcode` varchar(10) DEFAULT NULL,
  `state` varchar(50) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `has_chronic_disease` char(1) DEFAULT 'N',
  `chronic_disease` varchar(500) DEFAULT NULL,
  `medicine_taken_now` varchar(500) DEFAULT NULL,
  `has_allergies` char(1) DEFAULT 'N',
  `allergy_details` varchar(500) DEFAULT NULL,
  `remark` varchar(250) DEFAULT NULL,
  `gosmart_user_id` varchar(100) DEFAULT NULL,
  `modify_dt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modify_by` varchar(100) DEFAULT NULL,
  `status` char(1) DEFAULT 'A',

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for table `patient_caregiver`
--

DROP TABLE IF EXISTS `patient_caregiver`;
CREATE TABLE `patient_caregiver` (
  `id` int NOT NULL AUTO_INCREMENT,
  `medical_provider_id` int NOT NULL,
  `create_dt` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_by` varchar(100) NOT NULL,
  `modify_dt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modify_by` varchar(100) DEFAULT NULL,
  `patient_id` int NOT NULL,
  `caregiver_id` int NOT NULL,
  `status` char(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_patient_caregiver_id_status` (`patient_id`,`caregiver_id`,`status`),
  KEY `idx_caregiver_id` (`caregiver_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping events for database 'gosmart'
--

--
-- Dumping routines for database 'gosmart'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-31 13:52:15
