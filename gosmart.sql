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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `function`
--

LOCK TABLES `function` WRITE;
/*!40000 ALTER TABLE `function` DISABLE KEYS */;
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
  `group_code` varchar(50) NOT NULL,
  `group_name` varchar(100) NOT NULL,
  `sort_order` int DEFAULT NULL,
  `remark` varchar(250) DEFAULT NULL,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`group_id`),
  UNIQUE KEY `uk_function_group_code` (`group_code`),
  KEY `idx_function_group_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `function_group`
--

LOCK TABLES `function_group` WRITE;
/*!40000 ALTER TABLE `function_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `function_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_function`
--

DROP TABLE IF EXISTS `role_function`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_function` (
  `role_function_id` bigint NOT NULL AUTO_INCREMENT,
  `function_id` bigint NOT NULL,
  `role_code` varchar(50) NOT NULL,
  `role_name` varchar(100) NOT NULL,
  `remark` varchar(250) DEFAULT NULL,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`role_function_id`),
  UNIQUE KEY `uk_function_role` (`function_id`,`role_code`),
  KEY `idx_role_function_role_code` (`role_code`),
  KEY `idx_role_function_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_function`
--

LOCK TABLES `role_function` WRITE;
/*!40000 ALTER TABLE `role_function` DISABLE KEYS */;
/*!40000 ALTER TABLE `role_function` ENABLE KEYS */;
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
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
  `user_role_id` bigint NOT NULL AUTO_INCREMENT,
  `role_function_id` bigint NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `status` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`user_role_id`),
  UNIQUE KEY `uk_user_role_function_user_id` (`role_function_id`,`user_id`),
  KEY `idx_user_role_user_id` (`user_id`),
  KEY `idx_user_role_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_session`
--

LOCK TABLES `user_session` WRITE;
/*!40000 ALTER TABLE `user_session` DISABLE KEYS */;
INSERT INTO `user_session` (`id`, `create_dt`, `last_activity_dt`, `expire_dt`, `revoke_dt`, `revoke_reason`, `user_id`, `session_id`, `ip_address`, `user_agent`) VALUES (40,'2026-03-30 00:01:39','2026-03-30 00:01:40','2026-03-30 00:06:40','2026-03-30 09:48:22','multi_login','tanc08@gmail.com','2d1f004d-7f9d-495b-be55-f841f2fb539e','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0'),(41,'2026-03-30 09:48:22','2026-03-30 09:48:22','2026-03-30 09:53:22','2026-03-30 09:48:24','logout','tanc08@gmail.com','e8b2905b-ee08-4ead-94f5-093108956825','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0');
/*!40000 ALTER TABLE `user_session` ENABLE KEYS */;
UNLOCK TABLES;

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

-- Dump completed on 2026-03-30  9:52:49
