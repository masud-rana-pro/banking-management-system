-- SBMS baseline schema and reference dataset for MySQL 8.
-- Transient authentication, delivery, session and audit records are intentionally excluded.

--
-- ------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_name` varchar(150) NOT NULL,
  `account_number` varchar(40) NOT NULL,
  `account_status` enum('ACTIVE','CLOSED','PENDING','SUSPENDED') NOT NULL,
  `account_type_id` bigint NOT NULL,
  `activated_at` datetime(6) DEFAULT NULL,
  `available_balance` decimal(18,2) NOT NULL,
  `branch_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `currency_code` varchar(10) NOT NULL,
  `current_balance` decimal(18,2) NOT NULL,
  `customer_id` bigint NOT NULL,
  `opened_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `closed_date` date DEFAULT NULL,
  `profit_ratio_id` bigint DEFAULT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `opening_request_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account_account_number` (`account_number`),
  UNIQUE KEY `uk_account_number` (`account_number`),
  KEY `FKgw84mgpacw9htdxcs2j1p7u6j` (`account_type_id`),
  KEY `FKnnwpo0lfq4xai1rs6887sx02k` (`customer_id`),
  KEY `FKnvumwpphgqx9e5a9qy18sg8iq` (`opening_request_id`),
  CONSTRAINT `FKgw84mgpacw9htdxcs2j1p7u6j` FOREIGN KEY (`account_type_id`) REFERENCES `account_type` (`id`),
  CONSTRAINT `FKnnwpo0lfq4xai1rs6887sx02k` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `FKnvumwpphgqx9e5a9qy18sg8iq` FOREIGN KEY (`opening_request_id`) REFERENCES `account_opening_request` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

INSERT INTO `account` VALUES (1,'Md. Hasan Ali Savings Account','ACC-000001','ACTIVE',1,'2026-04-16 10:28:29.797236',0.00,2,'2026-04-16 09:35:12.757807','BDT',0.00,1,'2026-04-16 09:35:12.757807','2026-05-01 14:08:50.000000',NULL,1,'Existing active account normalized for account lifecycle review','ACTIVE',1),(2,'Customar Savings Account','ACC-000002','PENDING',2,NULL,0.00,2,'2026-04-16 12:04:24.443168','BDT',0.00,2,'2026-04-16 12:04:24.443168','2026-05-01 14:08:50.000000',NULL,NULL,'Existing pending activation account normalized for account lifecycle review','ACTIVE',2),(3,'Customer 03 - Mudarabah Savings Plus','ACC-000003','ACTIVE',3,NULL,5071.88,1,'2026-05-01 14:08:50.000000','BDT',5071.88,3,'2026-04-14 10:00:00.000000','2026-06-06 16:00:53.969026',NULL,3,'Approved savings account','ACTIVE',3),(4,'Customer 04 - SME Current Wadia','ACC-000004','SUSPENDED',4,NULL,7600.00,1,'2026-05-01 14:08:50.000000','BDT',8000.00,4,'2026-04-15 10:00:00.000000','2026-05-01 14:08:50.000000',NULL,NULL,'Blocked pending business verification','ACTIVE',NULL),(5,'Customer 05 - Corporate Mudarabah','ACC-000005','SUSPENDED',5,NULL,15000.00,2,'2026-05-01 14:08:50.000000','BDT',15000.00,5,'2026-04-16 10:00:00.000000','2026-05-01 14:08:50.000000',NULL,5,'Frozen pending compliance review','ACTIVE',NULL),(6,'Customer 06 - Hajj Savings','ACC-000006','CLOSED',6,NULL,0.00,1,'2026-05-01 14:08:50.000000','BDT',2500.00,6,'2026-04-17 10:00:00.000000','2026-05-01 14:08:50.000000','2026-04-28',6,'Closed after customer instruction','ACTIVE',NULL),(7,'Customer 07 - Umrah Savings','ACC-000007','ACTIVE',7,NULL,3015.75,2,'2026-05-01 14:08:50.000000','BDT',3015.75,7,'2026-04-18 10:00:00.000000','2026-06-06 16:00:53.906026',NULL,7,'Approved Umrah savings account','ACTIVE',7),(8,'Customer 08 - Student Savings','ACC-000008','PENDING',8,NULL,700.00,1,'2026-05-01 14:08:50.000000','BDT',700.00,8,'2026-04-19 10:00:00.000000','2026-05-01 14:08:50.000000',NULL,8,'Pending guardian confirmation','ACTIVE',NULL),(9,'Customer 09 - Payroll Savings','ACC-000009','ACTIVE',9,NULL,4300.00,3,'2026-05-01 14:08:50.000000','BDT',4500.00,9,'2026-04-20 10:00:00.000000','2026-05-01 14:08:50.000000',NULL,9,'Payroll savings account active','ACTIVE',NULL),(10,'Customer 10 - Business Current','ACC-000010','SUSPENDED',10,NULL,11800.00,2,'2026-05-01 14:08:50.000000','BDT',12000.00,10,'2026-04-21 10:00:00.000000','2026-05-01 14:08:50.000000',NULL,NULL,'Blocked business current temporarily','ACTIVE',10),(11,'Customer 11 - Women Savings','ACC-000011','ACTIVE',11,NULL,1816.24,1,'2026-05-01 14:08:50.000000','BDT',1816.24,11,'2026-04-22 10:00:00.000000','2026-06-13 03:56:31.521654',NULL,11,'Women savings account active','ACTIVE',NULL),(12,'Customer 12 - Senior Citizen Savings','ACC-000012','SUSPENDED',12,NULL,2100.00,3,'2026-05-01 14:08:51.000000','BDT',2200.00,12,'2026-04-23 10:00:00.000000','2026-05-01 14:08:51.000000',NULL,12,'Frozen due to signature update','ACTIVE',NULL),(13,'Customer 13 - NRB FC Savings','ACC-000013','ACTIVE',13,NULL,552.27,2,'2026-05-01 14:08:51.000000','USD',552.27,13,'2026-04-24 10:00:00.000000','2026-06-06 16:00:53.946026',NULL,13,'NRB FC savings active','ACTIVE',13),(14,'Customer 14 - Monthly Deposit Lite','ACC-000014','CLOSED',14,NULL,0.00,1,'2026-05-01 14:08:51.000000','BDT',900.00,14,'2026-04-25 10:00:00.000000','2026-05-01 14:08:51.000000','2026-04-29',14,'Monthly deposit account closed after maturity','ACTIVE',NULL),(15,'Customer 15 - Digital Savings','ACC-000015','ACTIVE',15,NULL,983.83,3,'2026-05-01 14:08:51.000000','BDT',1003.83,15,'2026-04-26 10:00:00.000000','2026-06-06 16:00:54.006027',NULL,15,'Digital savings account active','ACTIVE',15),(16,'branch Customer 013244 - Digital Savings','ACC-000016','PENDING',15,NULL,1000.00,1,'2026-05-16 01:32:46.487750','BDT',1000.00,19,'2026-05-16 01:32:46.487750','2026-05-16 01:32:46.487750',NULL,NULL,'branch account opening request','ACTIVE',16),(17,'branch Customer 013629 - Digital Savings','ACC-000017','ACTIVE',15,NULL,1000.00,1,'2026-05-16 01:36:31.741067','BDT',1000.00,20,'2026-05-16 01:36:31.741067','2026-05-16 01:36:44.009063',NULL,NULL,'branch account activation','ACTIVE',17),(18,'branch Customer 080710 - Digital Savings','ACC-000018','ACTIVE',15,NULL,1000.00,1,'2026-05-16 08:07:13.452828','BDT',1000.00,21,'2026-05-16 08:07:13.452828','2026-05-16 08:07:30.442108',NULL,NULL,'branch account activation','ACTIVE',18),(19,'branch Customer 080951 - Digital Savings','ACC-000019','ACTIVE',15,NULL,1500.00,1,'2026-05-16 08:09:53.815420','BDT',1500.00,22,'2026-05-16 08:09:53.815420','2026-06-13 03:46:57.634793',NULL,NULL,'branch account activation','ACTIVE',19),(20,'Arman Mamun - Senior Citizen Savings','ACC-000020','PENDING',12,NULL,2000.00,3,'2026-06-07 04:09:25.862704','BDT',2000.00,12,'2026-06-07 04:09:25.862704','2026-06-07 04:09:25.862704',NULL,NULL,'Submitted senior citizen savings request','ACTIVE',12),(21,'operational Banking Flow 20260613034352 - Digital Savings','ACC-000021','ACTIVE',15,NULL,5300.00,1,'2026-06-13 03:44:24.327424','BDT',5300.00,23,'2026-06-13 03:44:24.327424','2026-06-13 03:46:57.634793',NULL,NULL,'Activated during Step 4 operational branch processing','ACTIVE',20);

--
-- Table structure for table `account_opening_request`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_opening_request` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_type_id` bigint NOT NULL,
  `branch_id` bigint NOT NULL,
  `requested_date` date NOT NULL DEFAULT '2026-04-01',
  `created_at` datetime(6) NOT NULL,
  `customer_id` bigint NOT NULL,
  `ops_verified_at` datetime(6) DEFAULT NULL,
  `ops_verified_by` bigint DEFAULT NULL,
  `rejection_reason` varchar(255) DEFAULT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `request_no` varchar(40) NOT NULL,
  `request_status` enum('APPROVED','DRAFT','REJECTED','SENT_BACK','SUBMITTED','VERIFIED') NOT NULL,
  `submitted_at` datetime(6) DEFAULT NULL,
  `teller_reviewed_at` datetime(6) DEFAULT NULL,
  `teller_reviewed_by` bigint DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `approved_at` datetime(6) DEFAULT NULL,
  `approved_by` varchar(100) DEFAULT NULL,
  `initial_deposit_amount` decimal(18,2) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `verified_at` datetime(6) DEFAULT NULL,
  `verified_by` varchar(100) DEFAULT NULL,
  `applicant_image_name` varchar(180) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account_opening_request_request_no` (`request_no`),
  UNIQUE KEY `uk_account_opening_request_no` (`request_no`),
  KEY `FK91gifgp8kgh7slc798c2glwn` (`account_type_id`),
  KEY `FKhanp2oknvyrqv5r99orubg94i` (`customer_id`),
  CONSTRAINT `FK91gifgp8kgh7slc798c2glwn` FOREIGN KEY (`account_type_id`) REFERENCES `account_type` (`id`),
  CONSTRAINT `FKhanp2oknvyrqv5r99orubg94i` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_opening_request`
--

INSERT INTO `account_opening_request` VALUES (1,1,2,'2026-04-01','2026-04-16 09:21:26.398912',1,'2026-04-16 09:31:23.314336',1,NULL,'Existing request normalized for account lifecycle review','AOR-0001','APPROVED','2026-04-16 09:21:26.382917','2026-04-16 09:30:05.653579',1,'2026-06-05 04:09:40.000000','2026-05-01 14:08:50.000000','SYSTEM_REVIEWER',0.00,'ACTIVE','2026-05-01 14:08:50.000000','SYSTEM_REVIEWER','100067_Nur Nabi.jpg'),(2,2,2,'2026-04-01','2026-04-16 11:45:14.540264',2,'2026-04-16 11:47:08.517009',1,NULL,'Existing request normalized for account lifecycle review','AOR-0002','APPROVED','2026-04-16 11:45:14.540264','2026-04-16 11:47:01.251504',1,'2026-06-05 04:09:40.000000','2026-05-01 14:08:50.000000','SYSTEM_REVIEWER',0.00,'ACTIVE','2026-05-01 14:08:50.000000','SYSTEM_REVIEWER','100068_Nazmus Sakib.jpg'),(3,3,1,'2026-04-01','2026-05-01 14:08:50.000000',3,NULL,NULL,NULL,'Approved savings request','AOR-0003','APPROVED',NULL,NULL,NULL,'2026-06-05 04:09:40.000000','2026-05-01 14:08:50.000000','SYSTEM_REVIEWER',5000.00,'ACTIVE','2026-05-01 14:08:50.000000','SYSTEM_REVIEWER','100069_IMG_20260515_193640 - Sarna Aphrodite.jpg'),(4,4,1,'2026-04-02','2026-05-01 14:08:50.000000',4,NULL,NULL,NULL,'Verified and waiting for final approval','AOR-0004','VERIFIED',NULL,NULL,NULL,'2026-06-05 04:09:40.000000',NULL,NULL,8000.00,'ACTIVE','2026-05-01 14:08:50.000000','SYSTEM_REVIEWER','100070_Tushar Ahmed.jpg'),(5,5,2,'2026-04-03','2026-05-01 14:08:50.000000',5,NULL,NULL,NULL,'Submitted corporate opening request','AOR-0005','SUBMITTED',NULL,NULL,NULL,'2026-06-05 04:09:40.000000',NULL,NULL,15000.00,'ACTIVE',NULL,NULL,'100071_Samira Saba.jpg'),(6,6,1,'2026-04-04','2026-05-01 14:08:50.000000',6,NULL,NULL,NULL,'Draft Hajj savings request','AOR-0006','DRAFT',NULL,NULL,NULL,'2026-06-05 04:09:40.000000',NULL,NULL,2000.00,'ACTIVE',NULL,NULL,'100072_Zarin Islam.jpg'),(7,7,2,'2026-04-05','2026-05-01 14:08:50.000000',7,NULL,NULL,NULL,'Approved Umrah savings request','AOR-0007','APPROVED',NULL,NULL,NULL,'2026-06-05 04:09:40.000000','2026-05-01 14:08:50.000000','SYSTEM_REVIEWER',3000.00,'ACTIVE','2026-05-01 14:08:50.000000','SYSTEM_REVIEWER','100073_Fiaaz Ajmaeen.jpg'),(8,8,1,'2026-04-06','2026-05-01 14:08:50.000000',8,NULL,NULL,NULL,'Need corrected guardian document before approval','AOR-0008','SENT_BACK',NULL,NULL,NULL,'2026-06-05 04:09:40.000000',NULL,NULL,700.00,'ACTIVE','2026-05-01 14:08:50.000000','SYSTEM_REVIEWER','100074_Nur Nabi.jpg'),(9,9,3,'2026-04-07','2026-05-01 14:08:50.000000',9,NULL,NULL,NULL,'Rejected because employer proof mismatch','AOR-0009','REJECTED',NULL,NULL,NULL,'2026-06-05 04:09:40.000000','2026-05-01 14:08:50.000000','SYSTEM_REVIEWER',2500.00,'ACTIVE','2026-05-01 14:08:50.000000','SYSTEM_REVIEWER','100075_IMG_20260321_091445~3 - Md. Akib Uddin.jpg'),(10,10,2,'2026-04-08','2026-05-01 14:08:50.000000',10,NULL,NULL,NULL,'Approved business current request','AOR-0010','APPROVED',NULL,NULL,NULL,'2026-06-05 04:09:40.000000','2026-05-01 14:08:50.000000','SYSTEM_REVIEWER',12000.00,'ACTIVE','2026-05-01 14:08:50.000000','SYSTEM_REVIEWER','100076_Jahidul Islam.jpg'),(11,11,1,'2026-04-09','2026-05-01 14:08:50.000000',11,NULL,NULL,NULL,'Verified and pending branch approval','AOR-0011','VERIFIED',NULL,NULL,NULL,'2026-06-05 04:09:40.000000',NULL,NULL,1500.00,'ACTIVE','2026-05-01 14:08:50.000000','SYSTEM_REVIEWER','100077_88 - Mahadi Hasan (1).jpg'),(12,12,3,'2026-04-10','2026-05-01 14:08:50.000000',12,NULL,NULL,NULL,'Submitted senior citizen savings request','AOR-0012','APPROVED',NULL,NULL,NULL,'2026-06-07 04:09:25.827689','2026-06-07 04:09:25.825656','admin01',2000.00,'ACTIVE','2026-06-07 04:09:21.689391','admin01','100078_Jahidul Islam.jpg'),(13,13,2,'2026-04-11','2026-05-01 14:08:50.000000',13,NULL,NULL,NULL,'Approved NRB FC savings request','AOR-0013','APPROVED',NULL,NULL,NULL,'2026-06-05 04:09:40.000000','2026-05-01 14:08:50.000000','SYSTEM_REVIEWER',500.00,'ACTIVE','2026-05-01 14:08:50.000000','SYSTEM_REVIEWER','100079_Tushar Ahmed.jpg'),(14,14,1,'2026-04-12','2026-05-01 14:08:50.000000',14,NULL,NULL,NULL,'Draft monthly deposit request','AOR-0014','DRAFT',NULL,NULL,NULL,'2026-06-05 04:09:40.000000',NULL,NULL,900.00,'ACTIVE',NULL,NULL,'100080_Fiaaz Ajmaeen.jpg'),(15,15,3,'2026-04-13','2026-05-01 14:08:50.000000',15,NULL,NULL,NULL,'Approved digital savings request','AOR-0015','APPROVED',NULL,NULL,NULL,'2026-06-05 04:09:40.000000','2026-05-01 14:08:50.000000','SYSTEM_REVIEWER',1000.00,'ACTIVE','2026-05-01 14:08:50.000000','SYSTEM_REVIEWER','100081_IMG_20260515_193640 - Sarna Aphrodite.jpg'),(16,15,1,'2026-05-16','2026-05-16 01:32:46.117639',19,NULL,NULL,NULL,'branch account opening request','AOR-0016','APPROVED',NULL,NULL,NULL,'2026-06-05 04:09:40.000000','2026-05-16 01:32:46.479750','branch.manager01',1000.00,'ACTIVE','2026-05-16 01:32:46.397637','ops.officer01','100082_Arifa Khanam.jpg'),(17,15,1,'2026-05-16','2026-05-16 01:36:31.376056',20,NULL,NULL,NULL,'branch account opening request','AOR-0017','APPROVED',NULL,NULL,NULL,'2026-06-05 04:09:40.000000','2026-05-16 01:36:31.733059','branch.manager01',1000.00,'ACTIVE','2026-05-16 01:36:31.640060','ops.officer01','100083_Muhammad Ratul.jpg'),(18,15,1,'2026-05-16','2026-05-16 08:07:13.060820',21,NULL,NULL,NULL,'branch account opening request','AOR-0018','APPROVED',NULL,NULL,NULL,'2026-06-05 04:09:40.000000','2026-05-16 08:07:13.446818','branch.manager01',1000.00,'ACTIVE','2026-05-16 08:07:13.353819','ops.officer01','100084_masud.jpg'),(19,15,1,'2026-05-16','2026-05-16 08:09:53.407422',22,NULL,NULL,NULL,'branch account opening request','AOR-0019','APPROVED',NULL,NULL,NULL,'2026-06-07 03:56:23.130061','2026-06-07 03:56:23.130061','admin01',1000.00,'ACTIVE','2026-06-07 03:56:17.649049','admin01','100085_Nazmus Sakib.jpg'),(20,15,1,'2026-06-13','2026-06-13 03:44:24.170424',23,NULL,NULL,NULL,'operational account opening flow','AOR-0020','APPROVED',NULL,NULL,NULL,'2026-06-13 03:44:24.318424','2026-06-13 03:44:24.317553','admin01',1500.00,'ACTIVE','2026-06-13 03:44:24.271426','admin01','operational-customer.png');

--
-- Table structure for table `account_status_history`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_status_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_id` bigint NOT NULL,
  `action_at` datetime(6) NOT NULL,
  `action_by` bigint DEFAULT NULL,
  `from_status` enum('ACTIVE','CLOSED','PENDING','SUSPENDED') DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `to_status` enum('ACTIVE','CLOSED','PENDING','SUSPENDED') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_status_history`
--

INSERT INTO `account_status_history` VALUES (1,1,'2026-04-16 09:35:12.768802',1,NULL,'\"Standard savings account opening\"','PENDING'),(2,1,'2026-04-16 10:28:29.798236',1,'PENDING','Activated from UI','ACTIVE'),(3,2,'2026-04-16 12:04:24.509169',1,NULL,'\"Account Created\"','PENDING');

--
-- Table structure for table `account_type`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_type` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_category` varchar(100) NOT NULL,
  `account_subcategory` varchar(100) DEFAULT NULL,
  `code` varchar(50) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `minimum_balance` decimal(18,2) DEFAULT NULL,
  `name` varchar(150) NOT NULL,
  `profit_applicable` bit(1) NOT NULL,
  `psr_required` bit(1) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `currency_code` varchar(10) NOT NULL,
  `minimum_opening_balance` decimal(18,2) NOT NULL,
  `shariah_contract_type` enum('IJARAH','MUDARABAH','MURABAHA','QARD','WADIAH') NOT NULL,
  `type_code` varchar(30) NOT NULL,
  `type_name` varchar(100) NOT NULL,
  `withdrawal_allowed` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account_type_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_type`
--

INSERT INTO `account_type` VALUES (1,'SAVINGS','MUDARABAH_BASIC','ACT-001','2026-04-15 07:09:46.368288',1000.00,'Mudarabah Savings Basic',_binary '',_binary '','ACTIVE','2026-05-01 14:08:50.000000','BDT',1000.00,'MUDARABAH','ACT-001','Mudarabah Savings Basic',_binary ''),(2,'CURRENT','WADIAH_PERSONAL','ACT-002','2026-04-16 11:01:06.960752',2000.00,'Wadia Current Personal',_binary '\0',_binary '\0','ACTIVE','2026-05-01 14:08:50.000000','BDT',2000.00,'WADIAH','ACT-002','Wadia Current Personal',_binary ''),(3,'SAVINGS','MUDARABAH_PLUS','ACT-003','2026-05-01 14:08:50.000000',3000.00,'Mudarabah Savings Plus',_binary '',_binary '','ACTIVE','2026-05-01 14:08:50.000000','BDT',3000.00,'MUDARABAH','ACT-003','Mudarabah Savings Plus',_binary ''),(4,'CURRENT','SME_WADIAH','ACT-004','2026-05-01 14:08:50.000000',5000.00,'SME Current Wadia',_binary '\0',_binary '\0','ACTIVE','2026-05-01 14:08:50.000000','BDT',5000.00,'WADIAH','ACT-004','SME Current Wadia',_binary ''),(5,'CURRENT','CORPORATE_MUDARABAH','ACT-005','2026-05-01 14:08:50.000000',10000.00,'Corporate Mudarabah',_binary '',_binary '','ACTIVE','2026-05-01 14:08:50.000000','BDT',10000.00,'MUDARABAH','ACT-005','Corporate Mudarabah',_binary ''),(6,'SAVINGS','HAJJ','ACT-006','2026-05-01 14:08:50.000000',1500.00,'Hajj Savings',_binary '',_binary '','ACTIVE','2026-05-01 14:08:50.000000','BDT',1500.00,'MUDARABAH','ACT-006','Hajj Savings',_binary '\0'),(7,'SAVINGS','UMRAH','ACT-007','2026-05-01 14:08:50.000000',1500.00,'Umrah Savings',_binary '',_binary '','ACTIVE','2026-05-01 14:08:50.000000','BDT',1500.00,'MUDARABAH','ACT-007','Umrah Savings',_binary '\0'),(8,'SAVINGS','STUDENT','ACT-008','2026-05-01 14:08:50.000000',500.00,'Student Savings',_binary '',_binary '','ACTIVE','2026-05-01 14:08:50.000000','BDT',500.00,'MUDARABAH','ACT-008','Student Savings',_binary ''),(9,'SAVINGS','PAYROLL','ACT-009','2026-05-01 14:08:50.000000',1000.00,'Payroll Savings',_binary '',_binary '','ACTIVE','2026-05-01 14:08:50.000000','BDT',1000.00,'MUDARABAH','ACT-009','Payroll Savings',_binary ''),(10,'CURRENT','BUSINESS','ACT-010','2026-05-01 14:08:50.000000',7500.00,'Business Current',_binary '\0',_binary '\0','ACTIVE','2026-05-01 14:08:50.000000','BDT',7500.00,'WADIAH','ACT-010','Business Current',_binary ''),(11,'SAVINGS','WOMEN','ACT-011','2026-05-01 14:08:50.000000',800.00,'Women Savings',_binary '',_binary '','ACTIVE','2026-05-01 14:08:50.000000','BDT',800.00,'MUDARABAH','ACT-011','Women Savings',_binary ''),(12,'SAVINGS','SENIOR_CITIZEN','ACT-012','2026-05-01 14:08:50.000000',700.00,'Senior Citizen Savings',_binary '',_binary '','ACTIVE','2026-05-01 14:08:50.000000','BDT',700.00,'MUDARABAH','ACT-012','Senior Citizen Savings',_binary ''),(13,'SAVINGS','NRB_FC','ACT-013','2026-05-01 14:08:50.000000',100.00,'NRB FC Savings',_binary '',_binary '','ACTIVE','2026-05-01 14:08:50.000000','USD',100.00,'MUDARABAH','ACT-013','NRB FC Savings',_binary ''),(14,'DEPOSIT','MONTHLY_DEPOSIT','ACT-014','2026-05-01 14:08:50.000000',500.00,'Monthly Deposit Lite',_binary '',_binary '','ACTIVE','2026-05-01 14:08:50.000000','BDT',500.00,'MUDARABAH','ACT-014','Monthly Deposit Lite',_binary '\0'),(15,'SAVINGS','DIGITAL','ACT-015','2026-05-01 14:08:50.000000',300.00,'Digital Savings',_binary '',_binary '','ACTIVE','2026-05-01 14:08:50.000000','BDT',300.00,'MUDARABAH','ACT-015','Digital Savings',_binary '');

--
-- Table structure for table `account_types`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_types` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_category` varchar(100) NOT NULL,
  `account_subcategory` varchar(100) DEFAULT NULL,
  `code` varchar(50) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `minimum_balance` decimal(18,2) DEFAULT NULL,
  `name` varchar(150) NOT NULL,
  `profit_applicable` bit(1) NOT NULL,
  `psr_required` bit(1) NOT NULL,
  `status` enum('ACTIVE','INACTIVE') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account_types_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_types`
--

INSERT INTO `account_types` VALUES (1,'DEPOSIT','SAVINGS','SAV-MUD','2026-04-01 09:00:00.000000',1000.00,'Mudaraba Savings Account',_binary '',_binary '\0','ACTIVE','2026-04-01 09:00:00.000000'),(2,'DEPOSIT','CURRENT','CUR-WAD','2026-04-01 09:05:00.000000',5000.00,'Al-Wadiah Current Account',_binary '\0',_binary '\0','ACTIVE','2026-04-01 09:05:00.000000'),(3,'DEPOSIT','TERM','TERM-MUD','2026-04-01 09:10:00.000000',25000.00,'Mudaraba Term Deposit',_binary '',_binary '','ACTIVE','2026-04-01 09:10:00.000000'),(4,'FINANCING','MSME','MSME-MUR','2026-04-01 09:15:00.000000',10000.00,'MSME Murabaha Finance Account',_binary '\0',_binary '','ACTIVE','2026-04-01 09:15:00.000000'),(5,'FINANCING','AGRICULTURE','AGRI-SAL','2026-04-01 09:20:00.000000',5000.00,'Agriculture Salam Finance Account',_binary '\0',_binary '','ACTIVE','2026-04-01 09:20:00.000000');

--
-- Table structure for table `audit_log`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(100) NOT NULL,
  `action_at` datetime(6) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `entity_id` bigint DEFAULT NULL,
  `entity_type` varchar(100) DEFAULT NULL,
  `ip_address` varchar(50) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `username` varchar(100) DEFAULT NULL,
  `action_name` varchar(80) NOT NULL,
  `module_name` varchar(80) NOT NULL,
  `new_value_json` text,
  `old_value_json` text,
  `performed_at` datetime(6) NOT NULL,
  `performed_by` varchar(120) NOT NULL,
  `reference_id` bigint DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=425 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_log`
--


--
-- Table structure for table `balance_snapshot`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `balance_snapshot` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `average_balance` decimal(18,2) NOT NULL,
  `closing_balance` decimal(18,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `snapshot_date` date NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `account_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKn5e2rt97li0eqh2q0uocnejlk` (`account_id`),
  CONSTRAINT `FKn5e2rt97li0eqh2q0uocnejlk` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `balance_snapshot`
--

INSERT INTO `balance_snapshot` VALUES (1,24000.00,25000.00,'2026-04-10 17:00:00.000000','2026-04-10','ACTIVE','2026-04-10 17:00:00.000000',1),(2,17500.00,18000.00,'2026-04-12 17:05:00.000000','2026-04-12','ACTIVE','2026-04-12 17:05:00.000000',2),(3,31500.00,32000.00,'2026-04-05 17:10:00.000000','2026-04-05','ACTIVE','2026-04-05 17:10:00.000000',3),(4,8800.00,9000.00,'2026-04-20 17:15:00.000000','2026-04-20','ACTIVE','2026-04-20 17:15:00.000000',4),(5,44500.00,45000.00,'2026-04-08 17:20:00.000000','2026-04-08','ACTIVE','2026-04-08 17:20:00.000000',5),(6,12000.00,12500.00,'2025-12-31 17:25:00.000000','2025-12-31','ACTIVE','2025-12-31 17:25:00.000000',6),(7,15800.00,16000.00,'2026-04-03 17:30:00.000000','2026-04-03','ACTIVE','2026-04-03 17:30:00.000000',7),(8,7200.00,7500.00,'2026-04-15 17:35:00.000000','2026-04-15','ACTIVE','2026-04-15 17:35:00.000000',8),(9,28000.00,28500.00,'2025-12-31 17:40:00.000000','2025-12-31','ACTIVE','2025-12-31 17:40:00.000000',9),(10,51000.00,52000.00,'2026-04-18 17:45:00.000000','2026-04-18','ACTIVE','2026-04-18 17:45:00.000000',10),(11,13850.00,14000.00,'2026-04-06 17:50:00.000000','2026-04-06','ACTIVE','2026-04-06 17:50:00.000000',11),(12,21400.00,22000.00,'2026-04-01 17:55:00.000000','2026-04-01','ACTIVE','2026-04-01 17:55:00.000000',12),(13,6600.00,6800.00,'2026-04-04 18:00:00.000000','2026-04-04','ACTIVE','2026-04-04 18:00:00.000000',13),(14,9600.00,9800.00,'2026-04-25 18:05:00.000000','2026-04-25','ACTIVE','2026-04-25 18:05:00.000000',14),(15,15250.00,15500.00,'2026-04-07 18:10:00.000000','2026-04-07','ACTIVE','2026-04-07 18:10:00.000000',15),(16,8000.00,8000.00,'2026-05-01 18:37:53.366784','2026-05-01','ACTIVE','2026-05-01 18:37:53.366784',4),(17,900.00,900.00,'2026-05-01 18:37:53.444771','2026-05-01','ACTIVE','2026-05-01 18:37:53.444771',14),(18,25054.00,25108.33,'2026-05-10 17:00:00.000000','2026-05-10','ACTIVE','2026-05-10 17:00:00.000000',1),(19,32234.00,32468.75,'2026-05-05 17:10:00.000000','2026-05-05','ACTIVE','2026-05-05 17:10:00.000000',3),(20,45143.00,45286.46,'2026-05-08 17:20:00.000000','2026-05-08','ACTIVE','2026-05-08 17:20:00.000000',5),(21,15950.00,16086.33,'2026-05-03 17:30:00.000000','2026-05-03','ACTIVE','2026-05-03 17:30:00.000000',7),(22,7365.00,7529.94,'2026-05-15 17:35:00.000000','2026-05-15','ACTIVE','2026-05-15 17:35:00.000000',8),(23,8000.00,8000.00,'2026-06-06 16:00:53.820028','2026-06-06','ACTIVE','2026-06-06 16:00:53.820028',4),(24,900.00,900.00,'2026-06-06 16:00:53.860029','2026-06-06','ACTIVE','2026-06-06 16:00:53.860029',14),(25,3000.00,3000.00,'2026-06-06 16:00:53.877025','2026-06-06','ACTIVE','2026-06-06 16:00:53.877025',7),(26,550.00,550.00,'2026-06-06 16:00:53.928029','2026-06-06','ACTIVE','2026-06-06 16:00:53.928029',13),(27,5000.00,5000.00,'2026-06-06 16:00:53.959023','2026-06-06','ACTIVE','2026-06-06 16:00:53.959023',3),(28,1800.00,1800.00,'2026-06-06 16:00:53.977023','2026-06-06','ACTIVE','2026-06-06 16:00:53.977023',11),(29,1000.00,1000.00,'2026-06-06 16:00:53.996025','2026-06-06','ACTIVE','2026-06-06 16:00:53.996025',15),(30,15000.00,15000.00,'2026-06-06 16:00:54.015024','2026-06-06','ACTIVE','2026-06-06 16:00:54.015024',5),(31,0.00,0.00,'2026-06-06 16:00:54.025025','2026-06-06','ACTIVE','2026-06-06 16:00:54.025025',1),(32,700.00,700.00,'2026-06-06 16:00:54.041254','2026-06-06','ACTIVE','2026-06-06 16:00:54.041254',8),(33,700.00,700.00,'2026-06-13 03:55:43.831507','2026-06-13','ACTIVE','2026-06-13 03:55:43.831507',8),(34,900.00,900.00,'2026-06-13 03:56:09.133189','2026-06-13','ACTIVE','2026-06-13 03:56:09.133189',14),(35,1808.10,1808.10,'2026-06-13 03:56:31.504650','2026-07-06','ACTIVE','2026-06-13 03:56:31.504650',11);

--
-- Table structure for table `branch`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `branch` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address_line_1` varchar(255) DEFAULT NULL,
  `address_line_2` varchar(255) DEFAULT NULL,
  `branch_code` varchar(30) NOT NULL,
  `branch_name` varchar(150) NOT NULL,
  `city` varchar(255) DEFAULT NULL,
  `contact_number` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `district` varchar(255) DEFAULT NULL,
  `email` varchar(120) DEFAULT NULL,
  `manager_user_id` bigint DEFAULT NULL,
  `opening_date` date DEFAULT NULL,
  `postal_code` varchar(20) DEFAULT NULL,
  `status` varchar(30) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `address_line1` varchar(255) NOT NULL,
  `address_line2` varchar(255) DEFAULT NULL,
  `branch_short_name` varchar(50) DEFAULT NULL,
  `branch_type` varchar(50) NOT NULL,
  `country_id` bigint DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `delete_reason` varchar(500) DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `deleted_by` bigint DEFAULT NULL,
  `district_id` bigint DEFAULT NULL,
  `division_id` bigint DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `mobile` varchar(20) DEFAULT NULL,
  `opened_date` date DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `routing_no` varchar(30) NOT NULL,
  `swift_code` varchar(30) DEFAULT NULL,
  `upazila_id` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_branch_code` (`branch_code`),
  UNIQUE KEY `uk_branch_routing_no` (`routing_no`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `branch`
--

INSERT INTO `branch` VALUES (1,NULL,NULL,'BR001','Dhaka Main Branch',NULL,NULL,NULL,'2026-04-25 00:01:34.108343',NULL,'user001@example.com',1,NULL,'1000','ACTIVE',NULL,'Motijheel Commercial Area','Dhaka','Dhaka Main','MAIN',1,NULL,NULL,NULL,NULL,1,1,_binary '\0','01700000001','2024-01-01','0222334455','100100001','SBMSBDDH001',1,NULL),(2,NULL,NULL,'BR002','Gulshan Corporate Branch',NULL,NULL,NULL,'2026-04-25 00:02:08.732544',NULL,'user002@example.com',2,NULL,'1212','ACTIVE',NULL,'Gulshan Avenue','Gulshan 1','Gulshan','CORPORATE',1,NULL,NULL,NULL,NULL,1,1,_binary '\0','01700000002','2024-01-05','0222334456','100100002','SBMSBDDH002',2,NULL),(3,NULL,NULL,'BR003','Dhanmondi Branch',NULL,NULL,NULL,'2026-04-25 00:02:18.590544',NULL,'user003@example.com',3,NULL,'1209','ACTIVE',NULL,'Road 27, Dhanmondi','Dhaka','Dhanmondi','URBAN',1,NULL,NULL,NULL,NULL,1,1,_binary '\0','01700000003','2024-01-10','0222334457','100100003','SBMSBDDH003',3,NULL),(4,NULL,NULL,'BR004','Uttara Branch',NULL,NULL,NULL,'2026-04-25 00:02:36.001251',NULL,'user004@example.com',4,NULL,'1230','ACTIVE',NULL,'Sector 7, Uttara','Dhaka','Uttara','URBAN',1,NULL,NULL,NULL,NULL,1,1,_binary '\0','01700000004','2024-01-15','0222334458','100100004','SBMSBDDH004',4,NULL),(5,NULL,NULL,'BR005','Mirpur Branch',NULL,NULL,NULL,'2026-04-25 00:02:47.055056',NULL,'user005@example.com',5,NULL,'1216','ACTIVE',NULL,'Mirpur 10','Dhaka','Mirpur','URBAN',1,NULL,NULL,NULL,NULL,1,1,_binary '\0','01700000005','2024-02-01','0222334459','100100005','SBMSBDDH005',5,NULL),(6,NULL,NULL,'BR006','Chattogram Agrabad Branch',NULL,NULL,NULL,'2026-04-25 00:02:56.435816',NULL,'user006@example.com',6,NULL,'4100','ACTIVE',NULL,'Agrabad Commercial Area','Chattogram','Agrabad','MAIN',1,NULL,NULL,NULL,NULL,2,2,_binary '\0','01700000006','2024-02-05','0312233445','200100001','SBMSBDCG001',6,NULL),(7,NULL,NULL,'BR007','Chattogram EPZ Branch',NULL,NULL,NULL,'2026-04-25 00:03:07.048075',NULL,'user007@example.com',7,NULL,'4223','ACTIVE',NULL,'EPZ Road','Chattogram','CEPZ','INDUSTRIAL',1,NULL,NULL,NULL,NULL,2,2,_binary '\0','01700000007','2024-02-10','0312233446','200100002','SBMSBDCG002',7,NULL),(8,NULL,NULL,'BR008','Sylhet Zindabazar Branch',NULL,NULL,NULL,'2026-04-25 00:03:20.311683',NULL,'user008@example.com',8,NULL,'3100','ACTIVE',NULL,'Zindabazar','Sylhet','Zindabazar','URBAN',1,NULL,NULL,NULL,NULL,3,3,_binary '\0','01700000008','2024-02-15','0821223344','300100001','SBMSBDSY001',8,NULL),(9,NULL,NULL,'BR009','Rajshahi Branch',NULL,NULL,NULL,'2026-04-25 00:03:30.144999',NULL,'user009@example.com',9,NULL,'6000','INACTIVE',NULL,'Shaheb Bazar','Rajshahi','Rajshahi','URBAN',1,NULL,'Archived from UI','2026-04-30 16:03:38.353756',NULL,4,4,_binary '\0','01700000009','2024-03-01','0721223344','400100001','SBMSBDRJ001',9,NULL),(10,NULL,NULL,'BR010','Khulna Branch',NULL,NULL,NULL,'2026-04-25 00:03:39.853467',NULL,'user010@example.com',10,NULL,'9000','ACTIVE',NULL,'KDA Avenue','Khulna','Khulna','URBAN',1,NULL,NULL,NULL,NULL,5,5,_binary '\0','01700000010','2024-03-05','0412233445','500100001','SBMSBDKL001',10,NULL),(11,NULL,NULL,'BR011','Barishal Branch',NULL,NULL,NULL,'2026-04-25 00:03:49.539931',NULL,'user011@example.com',11,NULL,'8200','ACTIVE',NULL,'Sadar Road','Barishal','Barishal','URBAN',1,NULL,NULL,NULL,NULL,6,6,_binary '\0','01700000011','2024-03-10','0431223344','600100001','SBMSBDBR001',11,NULL),(12,NULL,NULL,'BR012','Rangpur Branch',NULL,NULL,NULL,'2026-04-25 00:03:59.254359',NULL,'user012@example.com',12,NULL,'5400','ACTIVE',NULL,'Station Road','Rangpur','Rangpur','URBAN',1,NULL,NULL,NULL,NULL,7,7,_binary '\0','01700000012','2024-03-15','0521223344','700100001','SBMSBDRP001',12,NULL),(13,NULL,NULL,'BR013','Mymensingh Branch',NULL,NULL,NULL,'2026-04-25 00:04:17.351274',NULL,'user013@example.com',13,NULL,'2200','ACTIVE',NULL,'Ganginarpar','Mymensingh','Mymensingh','URBAN',1,NULL,NULL,NULL,NULL,8,8,_binary '\0','01700000013','2024-03-20','0912233445','800100001','SBMSBDMY001',13,NULL),(14,NULL,NULL,'BR014','Narayanganj Branch',NULL,NULL,NULL,'2026-04-25 00:04:26.023954',NULL,'user014@example.com',14,NULL,'1400','ACTIVE',NULL,'Chashara','Narayanganj','Narayanganj','URBAN',1,NULL,NULL,NULL,NULL,9,1,_binary '\0','01700000014','2024-04-01','0671223344','100100006','SBMSBDDH006',14,NULL),(15,NULL,NULL,'BR015','Gazipur Branch',NULL,NULL,NULL,'2026-04-25 00:04:34.988545',NULL,'user015@example.com',15,NULL,'1700','ACTIVE',NULL,'Joydebpur','Gazipur','Gazipur','INDUSTRIAL',1,NULL,NULL,NULL,NULL,10,1,_binary '\0','01700000015','2024-04-05','0681223344','100100007','SBMSBDDH007',15,NULL),(16,NULL,NULL,'BR016','Cumilla Branch',NULL,NULL,NULL,'2026-04-25 00:04:57.569539',NULL,'user016@example.com',16,NULL,'3500','ACTIVE',NULL,'Kandirpar','Cumilla','Cumilla','URBAN',1,NULL,NULL,NULL,NULL,11,2,_binary '\0','01700000016','2024-04-10','0812233445','200100003','SBMSBDCG003',16,NULL),(17,NULL,NULL,'BR017','Coxs Bazar Branch',NULL,NULL,NULL,'2026-04-25 00:05:11.680445',NULL,'user017@example.com',17,NULL,'4700','ACTIVE',NULL,'Main Road','Coxs Bazar','Coxs Bazar','URBAN',1,NULL,NULL,NULL,NULL,12,2,_binary '\0','01700000017','2024-04-15','0341223344','200100004','SBMSBDCG004',17,NULL),(18,NULL,NULL,'BR018','Bogura Branch',NULL,NULL,NULL,'2026-04-25 00:05:20.480034',NULL,'user018@example.com',18,NULL,'5800','ACTIVE',NULL,'Satmatha','Bogura','Bogura','URBAN',1,NULL,NULL,NULL,NULL,13,4,_binary '\0','01700000018','2024-05-01','0512233445','400100002','SBMSBDRJ002',18,NULL),(19,NULL,NULL,'BR019','Jashore Branch',NULL,NULL,NULL,'2026-04-25 00:05:29.985809',NULL,'user019@example.com',19,NULL,'7400','ACTIVE',NULL,'RN Road','Jashore','Jashore','URBAN',1,NULL,NULL,NULL,NULL,14,5,_binary '\0','01700000019','2024-05-05','0421223344','500100002','SBMSBDKL002',19,NULL),(20,NULL,NULL,'BR020','Faridpur Branch',NULL,NULL,NULL,'2026-04-25 00:05:39.581994',NULL,'user020@example.com',20,NULL,'7800','ACTIVE',NULL,'Goalchamot','Faridpur','Faridpur','URBAN',1,NULL,NULL,NULL,NULL,15,1,_binary '\0','01700000020','2024-05-10','0631223344','100100008','SBMSBDDH008',20,NULL);

--
-- Table structure for table `branch_cash_ledger`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `branch_cash_ledger` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `balance_after` decimal(18,2) NOT NULL,
  `branch_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `credit_amount` decimal(18,2) NOT NULL,
  `debit_amount` decimal(18,2) NOT NULL,
  `entry_type` varchar(30) NOT NULL,
  `ledger_date` date NOT NULL,
  `reference_no` varchar(100) DEFAULT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `source_type` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `branch_cash_ledger`
--

INSERT INTO `branch_cash_ledger` VALUES (1,250000.00,1,'2026-05-08 15:04:47.000000',3,250000.00,0.00,'CREDIT','2026-05-08','M4-LEDGER-001','Opening vault cash loaded for Dhaka Main Branch','VAULT_OPENING'),(2,905000.00,2,'2026-05-08 15:04:47.000000',3,0.00,95000.00,'DEBIT','2026-05-08','M4-LEDGER-002','Bulk withdrawal settled at cash counter','CASH_WITHDRAW'),(3,1180000.00,3,'2026-05-08 15:04:47.000000',3,180000.00,0.00,'CREDIT','2026-05-08','M4-LEDGER-003','High-value deposit posted before noon','CASH_DEPOSIT'),(4,760000.00,4,'2026-05-08 15:04:47.000000',3,0.00,140000.00,'DEBIT','2026-05-08','M4-LEDGER-004','Inter-branch cash dispatch memo prepared for Uttara Branch','INTER_BRANCH_TRANSFER'),(5,1245000.00,5,'2026-05-08 15:04:47.000000',3,145000.00,0.00,'CREDIT','2026-05-08','M4-LEDGER-005','Mirpur Branch received approved transfer from Dhaka Main','INTER_BRANCH_RECEIPT'),(6,670000.00,6,'2026-05-08 15:04:47.000000',3,0.00,65000.00,'DEBIT','2026-05-08','M4-LEDGER-006','Teller settlement remitted to vault before close','TELLER_SETTLEMENT'),(7,788000.00,7,'2026-05-08 15:04:47.000000',3,88000.00,0.00,'CREDIT','2026-05-08','M4-LEDGER-007','Recovered ATM surplus brought into branch vault','ATM_SURPLUS'),(8,727000.00,8,'2026-05-08 15:04:47.000000',3,0.00,73000.00,'DEBIT','2026-05-08','M4-LEDGER-008','Large retail withdrawal served at branch cash desk','CASH_WITHDRAW'),(9,899000.00,9,'2026-05-08 15:04:47.000000',3,99000.00,0.00,'CREDIT','2026-05-08','M4-LEDGER-009','Rajshahi Branch corporate deposit added to ledger','CASH_DEPOSIT'),(10,438000.00,10,'2026-05-08 15:04:47.000000',3,0.00,12000.00,'DEBIT','2026-05-08','M4-LEDGER-010','Petty cash issued for branch service operations','PETTY_CASH'),(11,1035000.00,11,'2026-05-08 15:04:47.000000',3,135000.00,0.00,'CREDIT','2026-05-08','M4-LEDGER-011','Barishal Branch same-day merchant deposit posted','CASH_DEPOSIT'),(12,3685000.00,12,'2026-05-08 15:04:47.000000',3,0.00,45000.00,'DEBIT','2026-05-08','M4-LEDGER-012','Vault adjustment after day-end reconciliation','VAULT_CLOSE'),(13,3205000.00,13,'2026-05-08 15:04:47.000000',3,125000.00,0.00,'CREDIT','2026-05-08','M4-LEDGER-013','Received cash from Narayanganj for corporate salary load','INTER_BRANCH_RECEIPT'),(14,2575000.00,14,'2026-05-08 15:04:47.000000',3,0.00,110000.00,'DEBIT','2026-05-08','M4-LEDGER-014','Narayanganj cash dispatch awaiting approval trail archive','INTER_BRANCH_TRANSFER'),(15,4450000.00,15,'2026-05-08 15:04:47.000000',3,160000.00,0.00,'CREDIT','2026-05-08','M4-LEDGER-015','Gazipur Branch final vault balancing surplus posted','VAULT_CLOSE');

--
-- Table structure for table `branch_staff_assignment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `branch_staff_assignment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assigned_at` datetime(6) NOT NULL,
  `assigned_by` bigint DEFAULT NULL,
  `branch_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `removed_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_branch_staff_assignment_branch_user_active` (`branch_id`,`user_id`,`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `branch_staff_assignment`
--

INSERT INTO `branch_staff_assignment` VALUES (1,'2026-04-18 08:45:20.228956',1,1,'2026-04-18 08:45:20.230960',_binary '',NULL,10),(2,'2026-04-01 09:00:00.000000',3,1,'2026-04-01 09:00:00.000000',_binary '',NULL,4),(3,'2026-04-01 09:10:00.000000',3,2,'2026-04-01 09:10:00.000000',_binary '',NULL,5),(4,'2026-04-01 09:20:00.000000',3,3,'2026-04-01 09:20:00.000000',_binary '',NULL,11),(5,'2026-04-01 09:30:00.000000',3,4,'2026-04-01 09:30:00.000000',_binary '',NULL,12),(6,'2026-04-01 09:40:00.000000',3,5,'2026-04-01 09:40:00.000000',_binary '',NULL,7);

--
-- Table structure for table `branch_statement_request`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `branch_statement_request` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date_from` date NOT NULL,
  `date_to` date NOT NULL,
  `generated_at` datetime(6) DEFAULT NULL,
  `request_no` varchar(40) NOT NULL,
  `request_status` enum('DOWNLOADED','FAILED','GENERATED','REQUESTED') NOT NULL,
  `requested_at` datetime(6) NOT NULL,
  `requested_by` varchar(120) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `branch_id` bigint NOT NULL,
  `generated_file_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_branch_statement_request_no` (`request_no`),
  KEY `FKmay679krb081yirddn2fvqyj0` (`branch_id`),
  KEY `FKlb35bp7wmwj6n5fd8l89dbyci` (`generated_file_id`),
  CONSTRAINT `FKlb35bp7wmwj6n5fd8l89dbyci` FOREIGN KEY (`generated_file_id`) REFERENCES `file_reference` (`id`),
  CONSTRAINT `FKmay679krb081yirddn2fvqyj0` FOREIGN KEY (`branch_id`) REFERENCES `branch` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `branch_statement_request`
--

INSERT INTO `branch_statement_request` VALUES (1,'2026-01-01','2026-01-19','2026-05-01 23:13:51.824475','BSR-00001','GENERATED','2026-05-01 23:13:51.673476','SYSTEM','ACTIVE',1,16),(2,'2026-01-04','2026-01-22','2026-05-01 23:13:51.893477','BSR-00002','GENERATED','2026-05-01 23:13:51.871477','SYSTEM','ACTIVE',2,17),(3,'2026-01-07','2026-01-25','2026-05-01 23:13:51.960478','BSR-00003','GENERATED','2026-05-01 23:13:51.937483','SYSTEM','ACTIVE',3,18),(4,'2026-01-10','2026-01-28','2026-05-01 23:13:52.029478','BSR-00004','GENERATED','2026-05-01 23:13:52.004482','SYSTEM','ACTIVE',4,19),(5,'2026-01-13','2026-01-31','2026-05-01 23:13:52.092476','BSR-00005','GENERATED','2026-05-01 23:13:52.071480','SYSTEM','ACTIVE',5,20),(6,'2026-01-16','2026-02-03','2026-05-01 23:13:52.162478','BSR-00006','GENERATED','2026-05-01 23:13:52.138474','SYSTEM','ACTIVE',6,21),(7,'2026-01-19','2026-02-06','2026-05-01 23:13:52.236475','BSR-00007','GENERATED','2026-05-01 23:13:52.210476','SYSTEM','ACTIVE',7,22),(8,'2026-01-22','2026-02-09','2026-05-01 23:13:52.304476','BSR-00008','GENERATED','2026-05-01 23:13:52.280475','SYSTEM','ACTIVE',8,23),(9,'2026-01-25','2026-02-12','2026-05-01 23:13:52.370483','BSR-00009','GENERATED','2026-05-01 23:13:52.350480','SYSTEM','ACTIVE',9,24),(10,'2026-01-28','2026-02-15','2026-05-01 23:13:52.431487','BSR-00010','GENERATED','2026-05-01 23:13:52.410478','SYSTEM','ACTIVE',10,25),(11,'2026-01-31','2026-02-18','2026-05-01 23:13:52.490480','BSR-00011','DOWNLOADED','2026-05-01 23:13:52.470472','SYSTEM','ACTIVE',11,26),(12,'2026-02-03','2026-02-21','2026-05-01 23:13:52.552479','BSR-00012','DOWNLOADED','2026-05-01 23:13:52.532480','SYSTEM','ACTIVE',12,27),(13,'2026-02-06','2026-02-24','2026-05-01 23:13:52.614478','BSR-00013','DOWNLOADED','2026-05-01 23:13:52.594478','SYSTEM','ACTIVE',13,28),(14,'2026-02-09','2026-02-27','2026-05-01 23:13:52.692475','BSR-00014','DOWNLOADED','2026-05-01 23:13:52.666483','SYSTEM','ACTIVE',14,29),(15,'2026-02-12','2026-03-02','2026-05-01 23:13:52.769475','BSR-00015','DOWNLOADED','2026-05-01 23:13:52.750483','SYSTEM','ACTIVE',15,30),(16,'2026-05-01','2026-05-05','2026-06-13 03:02:28.934448','BSR-00016','DOWNLOADED','2026-05-05 08:59:10.315655','SYSTEM','ACTIVE',20,191),(17,'2026-06-13','2026-06-13','2026-06-13 03:49:47.057612','BSR-00017','DOWNLOADED','2026-06-13 03:49:46.475034','admin01','ACTIVE',1,196);

--
-- Table structure for table `branch_transfer`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `branch_transfer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(18,2) NOT NULL,
  `approved_at` datetime(6) DEFAULT NULL,
  `approved_by` bigint DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `from_branch_id` bigint NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `requested_by` bigint DEFAULT NULL,
  `status` enum('APPROVED','PENDING','REJECTED') NOT NULL,
  `to_branch_id` bigint NOT NULL,
  `transfer_date` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `branch_transfer`
--

INSERT INTO `branch_transfer` VALUES (1,250000.00,'2026-05-08 11:30:00.000000',3,'2026-05-08 11:00:00.000000',1,'Vault cash support transfer from Dhaka Main to Gulshan Corporate',4,'APPROVED',2,'2026-05-08'),(2,180000.00,'2026-05-09 10:45:00.000000',3,'2026-05-09 10:00:00.000000',1,'Mirpur weekend cash replenishment approved from Dhaka Main',4,'APPROVED',5,'2026-05-09'),(3,125000.00,'2026-05-12 15:00:00.000000',3,'2026-05-12 14:10:00.000000',2,'Corporate branch excess cash transfer to Dhanmondi',5,'APPROVED',3,'2026-05-12'),(4,90000.00,NULL,NULL,'2026-06-02 12:00:00.000000',3,'Pending Uttara cash top-up request for June operations',11,'PENDING',4,'2026-06-02');

--
-- Table structure for table `branch_user_assignment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `branch_user_assignment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assignment_role` varchar(50) NOT NULL,
  `branch_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `from_date` date NOT NULL,
  `is_primary` bit(1) DEFAULT NULL,
  `status` varchar(30) DEFAULT NULL,
  `to_date` date DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `branch_user_assignment`
--

INSERT INTO `branch_user_assignment` VALUES (1,'BRANCH_MANAGER',1,'2026-04-25 02:12:05.034152','2026-04-25',_binary '','ACTIVE',NULL,NULL,1),(2,'BRANCH_MANAGER',2,'2026-04-25 02:12:56.045084','2026-01-05',_binary '','ACTIVE',NULL,NULL,102),(3,'BRANCH_MANAGER',3,'2026-04-25 02:13:06.459367','2026-01-10',_binary '','ACTIVE',NULL,NULL,103),(4,'BRANCH_MANAGER',4,'2026-04-25 02:13:20.943342','2026-01-12',_binary '','ACTIVE',NULL,NULL,104),(5,'BRANCH_MANAGER',5,'2026-04-25 02:13:33.817621','2026-01-15',_binary '','ACTIVE',NULL,NULL,105),(6,'TELLER',1,'2026-04-25 02:13:47.933080','2026-02-01',_binary '','ACTIVE',NULL,NULL,201),(7,'TELLER',1,'2026-04-25 02:13:56.410431','2026-02-03',_binary '\0','ACTIVE',NULL,NULL,202),(8,'TELLER',2,'2026-04-25 02:14:06.443782','2026-02-05',_binary '','ACTIVE',NULL,NULL,203),(9,'TELLER',3,'2026-04-25 02:14:39.124330','2026-02-07',_binary '','ACTIVE',NULL,NULL,204),(10,'TELLER',4,'2026-04-25 02:14:48.188375','2026-02-10',_binary '','ACTIVE',NULL,NULL,205),(11,'OPERATIONS_OFFICER',1,'2026-04-25 02:14:59.080850','2026-03-01',_binary '','ACTIVE',NULL,NULL,301),(12,'OPERATIONS_OFFICER',2,'2026-04-25 02:15:08.863622','2026-03-03',_binary '','ACTIVE',NULL,NULL,302),(13,'OPERATIONS_OFFICER',3,'2026-04-25 02:15:20.580447','2026-03-05',_binary '','ACTIVE',NULL,NULL,303),(14,'OPERATIONS_OFFICER',4,'2026-04-25 02:15:28.664226','2026-03-08',_binary '','ACTIVE',NULL,NULL,304),(15,'OPERATIONS_OFFICER',5,'2026-04-25 02:15:38.109587','2026-03-10',_binary '','ACTIVE',NULL,NULL,305),(16,'CASH_OFFICER',1,'2026-04-25 02:15:52.187828','2026-04-01',_binary '\0','INACTIVE','2026-06-01',NULL,401),(17,'CASH_OFFICER',2,'2026-04-25 02:16:01.152309','2026-04-05',_binary '','ACTIVE',NULL,NULL,402),(18,'AUDITOR',3,'2026-04-25 02:16:10.973287','2026-04-07',_binary '\0','ACTIVE',NULL,NULL,403),(19,'AUDITOR',4,'2026-04-25 02:16:25.173544','2026-04-09',_binary '\0','INACTIVE',NULL,NULL,404),(20,'SUPERVISOR',5,'2026-04-25 02:16:36.891668','2026-04-12',_binary '','ACTIVE',NULL,NULL,405);

--
-- Table structure for table `branches`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `branches` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address_line_1` varchar(200) DEFAULT NULL,
  `address_line_2` varchar(200) DEFAULT NULL,
  `branch_code` varchar(50) NOT NULL,
  `branch_name` varchar(150) NOT NULL,
  `city` varchar(100) DEFAULT NULL,
  `contact_number` varchar(30) DEFAULT NULL,
  `country` varchar(80) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `district` varchar(100) DEFAULT NULL,
  `email` varchar(150) DEFAULT NULL,
  `manager_user_id` bigint DEFAULT NULL,
  `opening_date` date DEFAULT NULL,
  `postal_code` varchar(20) DEFAULT NULL,
  `status` enum('ACTIVE','INACTIVE') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_branches_branch_code` (`branch_code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `branches`
--

INSERT INTO `branches` VALUES (1,NULL,NULL,'DHAKA01','Dhaka Main Branch','Dhaka','01700000021',NULL,'2026-04-13 04:25:56.174303','Dhaka','user021@example.com',NULL,NULL,NULL,'INACTIVE','2026-04-13 04:45:45.143729'),(2,NULL,NULL,'DHAKA02','Mirpur-2 Branch','Dhaka','01700000021',NULL,'2026-04-13 04:45:32.149358','Dhaka','user022@example.com',NULL,NULL,NULL,'ACTIVE','2026-04-13 04:45:32.149358'),(3,NULL,NULL,'CTM01','Chattogram Main Branch','Chattogram','01700000022',NULL,'2026-04-13 14:08:00.762278','Chattogram','user023@example.com',NULL,NULL,NULL,'ACTIVE','2026-04-13 14:08:00.762278'),(4,NULL,NULL,'KHU01','Khulna Main Branch','Khulna ','019266647971',NULL,'2026-04-13 14:15:19.859069','Khulna','user024@example.com',NULL,NULL,NULL,'ACTIVE','2026-04-13 14:15:19.859069'),(5,'Motijheel','Head Office Area','DHK001','Dhaka Main Branch','Dhaka','01700000023','Bangladesh','2026-04-18 08:12:37.992517','Dhaka','user025@example.com',1,'2026-04-18','1000','ACTIVE','2026-04-18 08:12:37.992517');

--
-- Table structure for table `card`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `block_reason` varchar(500) DEFAULT NULL,
  `card_ref_no` varchar(40) NOT NULL,
  `card_status` enum('ACTIVE','BLOCKED','EXPIRED','PENDING_ACTIVATION','RENEWED','REPLACED') NOT NULL,
  `card_type` enum('ATM_CARD','DEBIT_CARD','PREPAID_CARD','VIRTUAL_CARD') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `expiry_date` date NOT NULL,
  `issue_date` date NOT NULL,
  `masked_card_no` varchar(30) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `account_id` bigint NOT NULL,
  `customer_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_card_ref_no` (`card_ref_no`),
  UNIQUE KEY `uk_card_masked_no` (`masked_card_no`),
  KEY `FK8v67eys6tqflsm6hrdgru2phu` (`account_id`),
  KEY `FKep9gakg0tgnl37wylb6qg9dnt` (`customer_id`),
  CONSTRAINT `FK8v67eys6tqflsm6hrdgru2phu` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`),
  CONSTRAINT `FKep9gakg0tgnl37wylb6qg9dnt` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card`
--

INSERT INTO `card` VALUES (1,NULL,'CRD-90001','ACTIVE','DEBIT_CARD','2026-05-01 19:05:42.000000','2031-01-05','2026-01-05','4000-****-9001-0001','ACTIVE','2026-05-01 19:05:42.000000',1,1),(2,NULL,'CRD-90002','PENDING_ACTIVATION','ATM_CARD','2026-05-01 19:05:42.000000','2031-01-18','2026-01-18','4000-****-9002-0001','ACTIVE','2026-05-01 19:05:42.000000',1,1),(3,'ATM cash-out mismatch','CRD-90003','BLOCKED','DEBIT_CARD','2026-05-01 19:05:42.000000','2031-02-02','2026-02-02','4000-****-9003-0007','ACTIVE','2026-05-01 19:05:42.000000',7,7),(4,NULL,'CRD-90004','ACTIVE','PREPAID_CARD','2026-05-01 19:05:42.000000','2031-02-14','2026-02-14','4000-****-9004-0007','ACTIVE','2026-05-01 19:05:42.000000',7,7),(5,NULL,'CRD-90005','ACTIVE','VIRTUAL_CARD','2026-05-01 19:05:42.000000','2031-02-26','2026-02-26','4000-****-9005-0001','ACTIVE','2026-05-01 19:05:42.000000',1,1),(6,NULL,'CRD-90006','PENDING_ACTIVATION','ATM_CARD','2026-05-01 19:05:42.000000','2031-03-03','2026-03-03','4000-****-9006-0007','ACTIVE','2026-05-01 19:05:42.000000',7,7),(7,NULL,'CRD-90007','ACTIVE','DEBIT_CARD','2026-05-01 19:05:42.000000','2031-03-10','2026-03-10','4000-****-9007-0001','ACTIVE','2026-05-01 19:05:42.000000',1,1),(8,'PIN retry exceeded','CRD-90008','BLOCKED','ATM_CARD','2026-05-01 19:05:42.000000','2031-03-22','2026-03-22','4000-****-9008-0007','ACTIVE','2026-05-01 19:05:42.000000',7,7),(9,NULL,'CRD-90009','ACTIVE','PREPAID_CARD','2026-05-01 19:05:42.000000','2031-04-01','2026-04-01','4000-****-9009-0001','ACTIVE','2026-05-01 19:05:42.000000',1,1),(10,NULL,'CRD-90010','PENDING_ACTIVATION','VIRTUAL_CARD','2026-05-01 19:05:42.000000','2031-04-09','2026-04-09','4000-****-9010-0007','ACTIVE','2026-05-01 19:05:42.000000',7,7),(11,NULL,'CRD-90011','ACTIVE','ATM_CARD','2026-05-01 19:05:42.000000','2031-04-15','2026-04-15','4000-****-9011-0001','ACTIVE','2026-05-01 19:05:42.000000',1,1),(12,NULL,'CRD-90012','PENDING_ACTIVATION','DEBIT_CARD','2026-05-01 19:05:42.000000','2031-04-20','2026-04-20','4000-****-9012-0007','ACTIVE','2026-05-01 19:05:42.000000',7,7),(13,NULL,'CRD-90013','ACTIVE','ATM_CARD','2026-05-01 19:05:42.000000','2026-04-20','2025-04-10','4000-****-9013-0001','ACTIVE','2026-05-01 19:05:42.000000',1,1),(14,'Cardholder request','CRD-90014','BLOCKED','DEBIT_CARD','2026-05-01 19:05:42.000000','2031-04-24','2026-04-24','4000-****-9014-0007','ACTIVE','2026-05-01 19:05:42.000000',7,7),(15,NULL,'CRD-90015','PENDING_ACTIVATION','VIRTUAL_CARD','2026-05-01 19:05:42.000000','2031-04-28','2026-04-28','4000-****-9015-0001','ACTIVE','2026-05-01 19:05:42.000000',1,1);

--
-- Table structure for table `card_event_log`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card_event_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `event_date` datetime(6) NOT NULL,
  `event_type` enum('ACTIVATED','ATM_TRANSACTION','ATM_USAGE_ALERT','BLOCKED','CDM_TRANSACTION','CDM_USAGE_ALERT','ISSUED','RENEWED','REPLACED','UNBLOCKED') NOT NULL,
  `performed_by` varchar(120) DEFAULT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `card_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq614d9v2tq3fvbwlfimc5nfi4` (`card_id`),
  CONSTRAINT `FKq614d9v2tq3fvbwlfimc5nfi4` FOREIGN KEY (`card_id`) REFERENCES `card` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_event_log`
--

INSERT INTO `card_event_log` VALUES (1,'2026-05-01 19:05:42.000000','2026-01-05 10:15:00.000000','ACTIVATED','SYSTEM','Card activated after issuance','ACTIVE',1),(2,'2026-05-01 19:05:42.000000','2026-01-18 09:10:00.000000','ISSUED','SYSTEM','Card waiting for activation','ACTIVE',2),(3,'2026-05-01 19:05:42.000000','2026-02-03 16:30:00.000000','BLOCKED','OPS_USER','Blocked due to ATM cash-out mismatch','ACTIVE',3),(4,'2026-05-01 19:05:42.000000','2026-02-15 11:45:00.000000','ATM_TRANSACTION','ATM-001','Cash withdrawal completed','ACTIVE',4),(5,'2026-05-01 19:05:42.000000','2026-02-27 13:05:00.000000','CDM_TRANSACTION','CDM-001','Cash deposit completed','ACTIVE',5),(6,'2026-05-01 19:05:42.000000','2026-03-03 09:20:00.000000','ISSUED','SYSTEM','Issued and queued for customer activation','ACTIVE',6),(7,'2026-05-01 19:05:42.000000','2026-03-11 20:10:00.000000','ATM_USAGE_ALERT','ATM-004','Late-night large withdrawal alert','ACTIVE',7),(8,'2026-05-01 19:05:42.000000','2026-03-23 12:40:00.000000','BLOCKED','OPS_USER','Blocked after PIN retry exceeded threshold','ACTIVE',8),(9,'2026-05-01 19:05:42.000000','2026-04-01 14:55:00.000000','CDM_USAGE_ALERT','CDM-003','Repeated deposit reversal alert','ACTIVE',9),(10,'2026-05-01 19:05:42.000000','2026-04-09 09:00:00.000000','ISSUED','SYSTEM','Virtual card created and pending activation','ACTIVE',10),(11,'2026-05-01 19:05:42.000000','2026-04-15 17:20:00.000000','ATM_TRANSACTION','ATM-002','Cash withdrawal completed','ACTIVE',11),(12,'2026-05-01 19:05:42.000000','2026-04-20 10:30:00.000000','ISSUED','SYSTEM','Issued and pending branch handover','ACTIVE',12),(13,'2026-05-01 19:05:42.000000','2026-04-21 18:15:00.000000','ATM_USAGE_ALERT','ATM-005','Expiry proximity usage alert','ACTIVE',13),(14,'2026-05-01 19:05:42.000000','2026-04-24 15:40:00.000000','BLOCKED','OPS_USER','Blocked upon cardholder request','ACTIVE',14),(15,'2026-05-01 19:05:42.000000','2026-04-28 09:35:00.000000','ISSUED','SYSTEM','Virtual card issued and queued for activation','ACTIVE',15);

--
-- Table structure for table `card_pin_event`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card_pin_event` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `event_date` datetime(6) NOT NULL,
  `event_type` enum('PIN_BLOCKED','PIN_CHANGE','PIN_GENERATED','PIN_RESET','WRONG_PIN') NOT NULL,
  `performed_by` varchar(120) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `card_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK19k67tupm79bf05y6ix7aehxa` (`card_id`),
  CONSTRAINT `FK19k67tupm79bf05y6ix7aehxa` FOREIGN KEY (`card_id`) REFERENCES `card` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_pin_event`
--

INSERT INTO `card_pin_event` VALUES (1,'2026-05-01 19:05:42.000000','2026-01-05 10:16:00.000000','PIN_GENERATED','SYSTEM','ACTIVE',1),(2,'2026-05-01 19:05:42.000000','2026-01-18 09:15:00.000000','PIN_GENERATED','SYSTEM','ACTIVE',2),(3,'2026-05-01 19:05:42.000000','2026-02-03 16:10:00.000000','WRONG_PIN','ATM-006','ACTIVE',3),(4,'2026-05-01 19:05:42.000000','2026-02-16 10:00:00.000000','PIN_CHANGE','SELF_SERVICE','ACTIVE',4),(5,'2026-05-01 19:05:42.000000','2026-02-27 16:30:00.000000','PIN_RESET','CALL_CENTER','ACTIVE',5),(6,'2026-05-01 19:05:42.000000','2026-03-03 09:22:00.000000','PIN_GENERATED','SYSTEM','ACTIVE',6),(7,'2026-05-01 19:05:42.000000','2026-03-12 08:45:00.000000','PIN_CHANGE','SELF_SERVICE','ACTIVE',7),(8,'2026-05-01 19:05:42.000000','2026-03-23 12:20:00.000000','PIN_BLOCKED','ATM-008','ACTIVE',8),(9,'2026-05-01 19:05:42.000000','2026-04-01 15:15:00.000000','PIN_RESET','CALL_CENTER','ACTIVE',9),(10,'2026-05-01 19:05:42.000000','2026-04-09 09:05:00.000000','PIN_GENERATED','SYSTEM','ACTIVE',10),(11,'2026-05-01 19:05:42.000000','2026-04-15 17:40:00.000000','PIN_CHANGE','SELF_SERVICE','ACTIVE',11),(12,'2026-05-01 19:05:42.000000','2026-04-20 10:35:00.000000','PIN_GENERATED','SYSTEM','ACTIVE',12),(13,'2026-05-01 19:05:42.000000','2026-04-21 18:10:00.000000','WRONG_PIN','ATM-005','ACTIVE',13),(14,'2026-05-01 19:05:42.000000','2026-04-24 16:00:00.000000','PIN_RESET','CALL_CENTER','ACTIVE',14),(15,'2026-05-01 19:05:42.000000','2026-04-28 09:38:00.000000','PIN_GENERATED','SYSTEM','ACTIVE',15);

--
-- Table structure for table `cash_transaction`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cash_transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(18,2) NOT NULL,
  `branch_id` bigint NOT NULL,
  `cash_direction` enum('IN','OUT') NOT NULL,
  `cash_type` enum('CASH','CHEQUE') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `teller_user_id` bigint DEFAULT NULL,
  `transaction_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmvt3awd6rbao877hmww5w5pf5` (`transaction_id`),
  CONSTRAINT `FKmvt3awd6rbao877hmww5w5pf5` FOREIGN KEY (`transaction_id`) REFERENCES `transaction_journal` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cash_transaction`
--

INSERT INTO `cash_transaction` VALUES (1,15000.00,1,'IN','CASH','2026-05-01 09:00:00.000000','Cash received for deposit voucher',101,1),(2,2500.00,2,'OUT','CASH','2026-05-01 09:15:00.000000','Cash handed to customer before reversal',102,2),(3,5000.00,1,'OUT','CASH','2026-05-01 09:30:00.000000','Transfer source cash trail',101,3),(4,8000.00,2,'IN','CHEQUE','2026-05-01 09:45:00.000000','Cheque received for clearing',102,4),(5,3200.00,1,'IN','CASH','2026-05-01 10:00:00.000000','Counter savings deposit',103,5),(6,1400.00,2,'OUT','CASH','2026-05-01 10:15:00.000000','Counter cash payout',104,6),(7,2200.00,1,'OUT','CASH','2026-05-01 10:30:00.000000','Transfer funding note',101,7),(8,18000.00,2,'IN','CHEQUE','2026-05-01 10:45:00.000000','Cheque accepted for business current',102,8),(9,4300.00,1,'IN','CASH','2026-05-01 11:00:00.000000','Women savings deposit note',105,9),(10,1600.00,3,'OUT','CASH','2026-05-01 11:15:00.000000','Senior savings cash payout',106,10),(11,300.00,2,'OUT','CASH','2026-05-01 11:30:00.000000','Transfer debit cash note',103,11),(12,1100.00,3,'IN','CHEQUE','2026-05-01 11:45:00.000000','Cheque pending review',107,12),(13,2500.00,2,'IN','CASH','2026-05-01 12:00:00.000000','Reversal cash recovery',102,13),(14,5000.00,1,'IN','CASH','2026-05-01 12:10:00.000000','Reversal recovery for transfer',101,14),(15,125000.00,2,'IN','CASH','2026-05-01 12:25:00.000000','Large cash deposit received',107,15),(16,500.00,1,'IN','CASH','2026-05-16 01:36:37.887065','branch processing opening deposit',54,16),(17,500.00,1,'IN','CASH','2026-05-16 08:07:21.719752','branch processing opening deposit',56,18),(18,500.00,1,'IN','CASH','2026-05-16 08:10:00.847418','branch processing opening deposit',58,20),(19,5000.00,1,'IN','CASH','2026-06-13 03:44:55.033998','Step 4 operational deposit',58,22),(20,700.00,1,'OUT','CASH','2026-06-13 03:45:25.681334','Step 4 operational withdrawal',58,23);

--
-- Table structure for table `charity_beneficiary`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `charity_beneficiary` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(1000) DEFAULT NULL,
  `beneficiary_code` varchar(40) NOT NULL,
  `beneficiary_name` varchar(160) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `mobile` varchar(40) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `proof_document_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_charity_beneficiary_code` (`beneficiary_code`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `charity_beneficiary`
--

INSERT INTO `charity_beneficiary` VALUES (1,'Badda, Dhaka','BEN-00001','Ayesha Welfare Family','2026-04-02 09:00:00.000000','01700000024','ACTIVE','2026-04-02 09:00:00.000000',NULL),(2,'Mirpur, Dhaka','BEN-00002','Rahman Community Clinic','2026-04-02 09:05:00.000000','01700000025','ACTIVE','2026-04-02 09:05:00.000000',NULL),(3,'Jatrabari, Dhaka','BEN-00003','Noor Shelter Support','2026-04-02 09:10:00.000000','01700000026','ACTIVE','2026-04-02 09:10:00.000000',NULL),(4,'Keraniganj, Dhaka','BEN-00004','Alor Path Student Aid','2026-04-02 09:15:00.000000','01700000027','ACTIVE','2026-04-02 09:15:00.000000',NULL),(5,'Savar, Dhaka','BEN-00005','Mizan Elder Support','2026-04-02 09:20:00.000000','01700000028','ACTIVE','2026-04-02 09:20:00.000000',NULL),(6,'Gazipur Sadar, Gazipur','BEN-00006','Green Village Relief','2026-04-02 09:25:00.000000','01700000029','ACTIVE','2026-04-02 09:25:00.000000',NULL),(7,'Narayanganj','BEN-00007','Safa Medical Outreach','2026-04-02 09:30:00.000000','01700000030','ACTIVE','2026-04-02 09:30:00.000000',NULL),(8,'Uttara, Dhaka','BEN-00008','Iqra Women Support Cell','2026-04-02 09:35:00.000000','01700000031','ACTIVE','2026-04-02 09:35:00.000000',NULL),(9,'Cumilla Town','BEN-00009','Tawhid Rural Care','2026-04-02 09:40:00.000000','01700000032','ACTIVE','2026-04-02 09:40:00.000000',NULL),(10,'Mymensingh','BEN-00010','Baraka Widow Support','2026-04-02 09:45:00.000000','01700000033','ACTIVE','2026-04-02 09:45:00.000000',NULL),(11,'Narsingdi','BEN-00011','Madrasa Meal Program','2026-04-02 09:50:00.000000','01700000034','ACTIVE','2026-04-02 09:50:00.000000',NULL),(12,'Tangail','BEN-00012','Sadaqah Care Trust','2026-04-02 09:55:00.000000','01700000035','ACTIVE','2026-04-02 09:55:00.000000',NULL),(13,'Kishoreganj','BEN-00013','Nobojibon Health Link','2026-04-02 10:00:00.000000','01700000036','ACTIVE','2026-04-02 10:00:00.000000',NULL),(14,'Manikganj','BEN-00014','Ar Rahmah Winter Drive','2026-04-02 10:05:00.000000','01700000037','ARCHIVED','2026-04-10 15:10:00.000000',NULL),(15,'Munshiganj','BEN-00015','Community Skill Uplift','2026-04-02 10:10:00.000000','01700000038','ARCHIVED','2026-04-10 15:15:00.000000',NULL);

--
-- Table structure for table `charity_fund`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `charity_fund` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `balance_after` decimal(18,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `credit_amount` decimal(18,2) NOT NULL,
  `debit_amount` decimal(18,2) NOT NULL,
  `fund_date` date NOT NULL,
  `reference_id` bigint DEFAULT NULL,
  `remarks` varchar(1000) DEFAULT NULL,
  `source_type` enum('DONATION','LATE_FEE','PAYOUT','ZAKAT_DEDUCTION') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `charity_fund`
--

INSERT INTO `charity_fund` VALUES (1,13500.00,'2026-04-10 11:30:00.000000',13500.00,0.00,'2026-04-10',1,'Zakat deduction for CUST001 year 2025','ZAKAT_DEDUCTION'),(2,24750.00,'2026-04-10 11:45:00.000000',11250.00,0.00,'2026-04-10',2,'Zakat deduction for CUST002 year 2025','ZAKAT_DEDUCTION'),(3,41000.00,'2026-04-10 12:30:00.000000',16250.00,0.00,'2026-04-10',5,'Zakat deduction for CUS-000003 year 2025','ZAKAT_DEDUCTION'),(4,50750.00,'2026-04-10 13:00:00.000000',9750.00,0.00,'2026-04-10',7,'Zakat deduction for CUS-000005 year 2025','ZAKAT_DEDUCTION'),(5,65125.00,'2026-04-10 13:30:00.000000',14375.00,0.00,'2026-04-10',9,'Zakat deduction for CUS-000007 year 2025','ZAKAT_DEDUCTION'),(6,77625.00,'2026-04-10 14:00:00.000000',12500.00,0.00,'2026-04-10',11,'Zakat deduction for CUS-000009 year 2025','ZAKAT_DEDUCTION'),(7,88250.00,'2026-04-10 14:30:00.000000',10625.00,0.00,'2026-04-10',13,'Zakat deduction for CUS-000011 year 2025','ZAKAT_DEDUCTION'),(8,103750.00,'2026-04-10 14:45:00.000000',15500.00,0.00,'2026-04-10',14,'Zakat deduction for CUS-000012 year 2026','ZAKAT_DEDUCTION'),(9,112500.00,'2026-04-10 15:00:00.000000',8750.00,0.00,'2026-04-10',15,'Zakat deduction for CUS-000013 year 2025','ZAKAT_DEDUCTION'),(10,132500.00,'2026-04-11 09:00:00.000000',20000.00,0.00,'2026-04-11',NULL,'Voluntary sadaqah donation transferred into charity fund.','DONATION'),(11,147500.00,'2026-04-11 09:15:00.000000',15000.00,0.00,'2026-04-11',NULL,'Board approved community donation credited for payout pool.','DONATION'),(12,150750.00,'2026-04-11 09:30:00.000000',3250.00,0.00,'2026-04-11',101,'Late fee rerouted to charity fund per Shariah policy.','LATE_FEE'),(13,168750.00,'2026-04-11 09:45:00.000000',18000.00,0.00,'2026-04-11',NULL,'Special Ramadan donation credited by head office.','DONATION'),(14,170850.00,'2026-04-11 10:00:00.000000',2100.00,0.00,'2026-04-11',102,'Penalty recovery routed to charity only.','LATE_FEE'),(15,179850.00,'2026-04-11 10:15:00.000000',9000.00,0.00,'2026-04-11',NULL,'Charity top-up received from branch welfare drive.','DONATION'),(16,176350.00,'2026-04-12 10:00:00.000000',0.00,3500.00,'2026-04-12',1,'Charity payout to BEN-00001 - Ayesha Welfare Family','PAYOUT'),(17,172150.00,'2026-04-12 10:15:00.000000',0.00,4200.00,'2026-04-12',2,'Charity payout to BEN-00002 - Rahman Community Clinic','PAYOUT'),(18,167150.00,'2026-04-12 10:30:00.000000',0.00,5000.00,'2026-04-12',3,'Charity payout to BEN-00003 - Noor Shelter Support','PAYOUT'),(19,164400.00,'2026-04-12 10:45:00.000000',0.00,2750.00,'2026-04-12',4,'Charity payout to BEN-00004 - Alor Path Student Aid','PAYOUT'),(20,158000.00,'2026-04-12 11:00:00.000000',0.00,6400.00,'2026-04-12',5,'Charity payout to BEN-00005 - Mizan Elder Support','PAYOUT'),(21,150800.00,'2026-04-12 11:15:00.000000',0.00,7200.00,'2026-04-12',6,'Charity payout to BEN-00006 - Green Village Relief','PAYOUT'),(22,147000.00,'2026-04-12 11:30:00.000000',0.00,3800.00,'2026-04-12',7,'Charity payout to BEN-00007 - Safa Medical Outreach','PAYOUT'),(23,142450.00,'2026-04-12 11:45:00.000000',0.00,4550.00,'2026-04-12',8,'Charity payout to BEN-00008 - Iqra Women Support Cell','PAYOUT'),(24,137350.00,'2026-04-12 12:00:00.000000',0.00,5100.00,'2026-04-12',9,'Charity payout to BEN-00009 - Tawhid Rural Care','PAYOUT'),(25,131150.00,'2026-04-12 12:15:00.000000',0.00,6200.00,'2026-04-12',10,'Charity payout to BEN-00010 - Baraka Widow Support','PAYOUT'),(26,123750.00,'2026-04-12 12:30:00.000000',0.00,7400.00,'2026-04-12',11,'Charity payout to BEN-00011 - Madrasa Meal Program','PAYOUT'),(27,120850.00,'2026-04-12 12:45:00.000000',0.00,2900.00,'2026-04-12',12,'Charity payout to BEN-00012 - Sadaqah Care Trust','PAYOUT'),(28,117500.00,'2026-04-12 13:00:00.000000',0.00,3350.00,'2026-04-12',13,'Charity payout to BEN-00013 - Nobojibon Health Link','PAYOUT'),(29,112700.00,'2026-04-12 13:15:00.000000',0.00,4800.00,'2026-04-12',14,'Charity payout to BEN-00001 - Ayesha Welfare Family','PAYOUT'),(30,107100.00,'2026-04-12 13:30:00.000000',0.00,5600.00,'2026-04-12',15,'Charity payout to BEN-00002 - Rahman Community Clinic','PAYOUT');

--
-- Table structure for table `charity_payout`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `charity_payout` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(18,2) NOT NULL,
  `approved_by` varchar(160) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `payout_date` date NOT NULL,
  `remarks` varchar(1000) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `beneficiary_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1tngwh8cp8kvu7hsb0bp8w3uo` (`beneficiary_id`),
  CONSTRAINT `FK1tngwh8cp8kvu7hsb0bp8w3uo` FOREIGN KEY (`beneficiary_id`) REFERENCES `charity_beneficiary` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `charity_payout`
--

INSERT INTO `charity_payout` VALUES (1,3500.00,'Zakat Officer 01','2026-04-12 10:00:00.000000','2026-04-12','Monthly family food assistance disbursed from zakat fund.','ACTIVE',1),(2,4200.00,'Zakat Officer 02','2026-04-12 10:15:00.000000','2026-04-12','Medical support released for clinic treatment inventory.','ACTIVE',2),(3,5000.00,'Zakat Officer 03','2026-04-12 10:30:00.000000','2026-04-12','Emergency housing and shelter payout approved.','ACTIVE',3),(4,2750.00,'Zakat Officer 04','2026-04-12 10:45:00.000000','2026-04-12','Student educational materials distributed to verified list.','ACTIVE',4),(5,6400.00,'Zakat Officer 05','2026-04-12 11:00:00.000000','2026-04-12','Senior citizen support cycle released for medicine coverage.','ACTIVE',5),(6,7200.00,'Zakat Officer 06','2026-04-12 11:15:00.000000','2026-04-12','Village relief pack and cash assistance disbursed.','ACTIVE',6),(7,3800.00,'Zakat Officer 07','2026-04-12 11:30:00.000000','2026-04-12','Rural medical camp logistics funded from charity pool.','ACTIVE',7),(8,4550.00,'Zakat Officer 08','2026-04-12 11:45:00.000000','2026-04-12','Women support allowance approved after branch verification.','ACTIVE',8),(9,5100.00,'Zakat Officer 09','2026-04-12 12:00:00.000000','2026-04-12','Rural hardship payout delivered to field coordinator.','ACTIVE',9),(10,6200.00,'Zakat Officer 10','2026-04-12 12:15:00.000000','2026-04-12','Widow household assistance released for quarter one.','ACTIVE',10),(11,7400.00,'Zakat Officer 11','2026-04-12 12:30:00.000000','2026-04-12','Madrasa meal support funded for one monthly cycle.','ACTIVE',11),(12,2900.00,'Zakat Officer 12','2026-04-12 12:45:00.000000','2026-04-12','Small grant approved for immediate social care need.','ACTIVE',12),(13,3350.00,'Zakat Officer 13','2026-04-12 13:00:00.000000','2026-04-12','Healthcare mobility support released after approval.','ACTIVE',13),(14,4800.00,'Zakat Officer 14','2026-04-12 13:15:00.000000','2026-04-12','Follow-up food and shelter support provided to family case.','ACTIVE',1),(15,5600.00,'Zakat Officer 15','2026-04-12 13:30:00.000000','2026-04-12','Additional treatment support released for clinic beneficiary.','ACTIVE',2);

--
-- Table structure for table `cheque_clearing`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cheque_clearing` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(18,2) NOT NULL,
  `cheque_no` varchar(40) NOT NULL,
  `cheque_status` enum('CLEARED','RECEIVED','RETURNED') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `credit_account_id` bigint NOT NULL,
  `drawee_bank` varchar(150) NOT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `transaction_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrayv6q2vcwtse9hd90efolb4m` (`transaction_id`),
  CONSTRAINT `FKrayv6q2vcwtse9hd90efolb4m` FOREIGN KEY (`transaction_id`) REFERENCES `transaction_journal` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cheque_clearing`
--

INSERT INTO `cheque_clearing` VALUES (1,15000.00,'CHQ-0001','CLEARED','2026-05-01 09:00:00.000000',1,'Dhaka Main Branch','reference cheque detail for ref 1',1),(2,2500.00,'CHQ-0002','RETURNED','2026-05-01 09:15:00.000000',2,'Gulshan Corporate Branch','Returned cheque linked with reversed withdrawal',2),(3,5000.00,'CHQ-0003','CLEARED','2026-05-01 09:30:00.000000',4,'Dhanmondi Branch','Transfer-linked cheque trace',3),(4,8000.00,'CHQ-0004','CLEARED','2026-05-01 09:45:00.000000',5,'Uttara Branch','Actual cheque clearing reference',4),(5,3200.00,'CHQ-0005','RECEIVED','2026-05-01 10:00:00.000000',6,'Mirpur Branch','Deposit-linked received cheque',5),(6,1400.00,'CHQ-0006','RETURNED','2026-05-01 10:15:00.000000',7,'Agrabad Branch','Withdrawal linked cheque return',6),(7,2200.00,'CHQ-0007','CLEARED','2026-05-01 10:30:00.000000',9,'CEPZ Branch','Transfer reference cheque',7),(8,18000.00,'CHQ-0008','CLEARED','2026-05-01 10:45:00.000000',10,'Sylhet Branch','Business current cheque clearing',8),(9,4300.00,'CHQ-0009','CLEARED','2026-05-01 11:00:00.000000',11,'Rajshahi Branch','Deposit cheque support',9),(10,1600.00,'CHQ-0010','RETURNED','2026-05-01 11:15:00.000000',12,'Khulna Branch','Withdrawal control reference',10),(11,300.00,'CHQ-0011','CLEARED','2026-05-01 11:30:00.000000',14,'Barishal Branch','Transfer cheque note',11),(12,1100.00,'CHQ-0012','RECEIVED','2026-05-01 11:45:00.000000',15,'Rangpur Branch','Pending review cheque reference',12),(13,2500.00,'CHQ-0013','CLEARED','2026-05-01 12:00:00.000000',2,'Mymensingh Branch','Reversal linked cheque',13),(14,5000.00,'CHQ-0014','CLEARED','2026-05-01 12:10:00.000000',3,'Narayanganj Branch','Transfer reversal cheque trace',14),(15,125000.00,'CHQ-0015','RECEIVED','2026-05-01 12:25:00.000000',5,'Gazipur Branch','Suspicious large cheque deposit',15);

--
-- Table structure for table `contact_verification_status`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contact_verification_status` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `contact_type` varchar(20) NOT NULL,
  `contact_value` varchar(160) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `is_primary` bit(1) NOT NULL,
  `is_verified` bit(1) NOT NULL,
  `reference_id` bigint NOT NULL,
  `reference_module` varchar(60) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `verification_method` varchar(50) DEFAULT NULL,
  `verified_at` datetime(6) DEFAULT NULL,
  `verified_by` varchar(120) DEFAULT NULL,
  `last_verification_request_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4b1vwrbwd638yuh1sdi3w4gew` (`last_verification_request_id`),
  CONSTRAINT `FK4b1vwrbwd638yuh1sdi3w4gew` FOREIGN KEY (`last_verification_request_id`) REFERENCES `otp_verification_request` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contact_verification_status`
--

INSERT INTO `contact_verification_status` VALUES (1,'EMAIL','user021@example.com','2026-05-08 15:11:52.000000',_binary '',_binary '',1,'CUSTOMER','ACTIVE','2026-05-08 15:11:52.000000','EMAIL_OTP','2026-04-10 09:02:00.000000','SYSTEM',1),(2,'MOBILE','01700000039','2026-05-08 15:11:52.000000',_binary '',_binary '',2,'CUSTOMER','ACTIVE','2026-05-08 15:11:52.000000','SMS_OTP','2026-04-10 09:11:00.000000','SYSTEM',2),(3,'EMAIL','user026@example.com','2026-05-08 15:11:52.000000',_binary '',_binary '\0',3,'CUSTOMER','ACTIVE','2026-05-08 15:11:52.000000','EMAIL_OTP',NULL,NULL,3),(4,'MOBILE','01700000040','2026-05-08 15:11:52.000000',_binary '',_binary '\0',4,'CUSTOMER','ACTIVE','2026-05-08 15:11:52.000000','SMS_OTP',NULL,NULL,4),(5,'EMAIL','user027@example.com','2026-05-08 15:11:52.000000',_binary '',_binary '\0',5,'CUSTOMER','ACTIVE','2026-05-08 15:11:52.000000','EMAIL_OTP',NULL,NULL,8),(6,'MOBILE','01700000041','2026-05-08 15:11:52.000000',_binary '',_binary '\0',6,'CUSTOMER','ACTIVE','2026-05-08 15:11:52.000000','SMS_OTP',NULL,NULL,9),(7,'EMAIL','user028@example.com','2026-05-08 15:11:52.000000',_binary '',_binary '',7,'CUSTOMER','ACTIVE','2026-05-08 15:11:52.000000','EMAIL_OTP','2026-04-10 10:31:00.000000','SYSTEM',10),(8,'MOBILE','01700000042','2026-05-08 15:11:52.000000',_binary '',_binary '',8,'CUSTOMER','ACTIVE','2026-05-08 15:11:52.000000','SMS_OTP','2026-04-10 10:41:00.000000','SYSTEM',11),(9,'EMAIL','user029@example.com','2026-05-08 15:11:52.000000',_binary '',_binary '\0',9,'CUSTOMER','ACTIVE','2026-05-08 15:11:52.000000','EMAIL_OTP',NULL,NULL,14),(10,'MOBILE','01700000043','2026-05-08 15:11:52.000000',_binary '',_binary '\0',10,'CUSTOMER','ACTIVE','2026-05-08 15:11:52.000000','SMS_OTP',NULL,NULL,15);

--
-- Table structure for table `contract`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contract` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `contract_no` varchar(40) NOT NULL,
  `contract_status` enum('ACTIVE','DRAFT','LOCKED') NOT NULL,
  `contract_text` longtext NOT NULL,
  `contract_type` enum('ACCOUNT_OPENING','CARD_ISSUE','DEPOSIT_SCHEME','FINANCING','GENERAL') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `customer_signed_at` datetime(6) DEFAULT NULL,
  `reference_id` bigint NOT NULL,
  `reference_module` varchar(80) NOT NULL,
  `remarks` varchar(1000) DEFAULT NULL,
  `shariah_signed_at` datetime(6) DEFAULT NULL,
  `signed_by_customer` varchar(160) DEFAULT NULL,
  `signed_by_shariah` varchar(160) DEFAULT NULL,
  `signed_date` datetime(6) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `customer_id` bigint NOT NULL,
  `template_id` bigint NOT NULL,
  `supporting_document_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_contract_no` (`contract_no`),
  KEY `FKq28qogy68douoc4gkgcy3ow9p` (`customer_id`),
  KEY `FK2iec0nkhuhpg280m79mwxi0jm` (`template_id`),
  CONSTRAINT `FK2iec0nkhuhpg280m79mwxi0jm` FOREIGN KEY (`template_id`) REFERENCES `contract_template` (`id`),
  CONSTRAINT `FKq28qogy68douoc4gkgcy3ow9p` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contract`
--

INSERT INTO `contract` VALUES (1,'CTR-00001','DRAFT','Account opening draft contract for Md. Masud Rana linked to request 1. Awaiting customer signature.','ACCOUNT_OPENING','2026-02-02 09:00:00.000000',NULL,1,'ACCOUNT_OPENING','Draft contract waiting for customer review',NULL,NULL,NULL,NULL,'ACTIVE','2026-02-02 09:00:00.000000',1,1,NULL),(2,'CTR-00002','ACTIVE','Murabaha contract for Md. Woalinur linked to financing application 1. Customer signed, waiting for shariah sign.','FINANCING','2026-02-02 09:20:00.000000','2026-02-03 10:15:00.000000',1,'FINANCING','Customer accepted financing terms',NULL,'Customer Signer 02',NULL,NULL,'ACTIVE','2026-02-03 10:15:00.000000',2,2,NULL),(3,'CTR-00003','LOCKED','Ijarah contract for Md. Rahim Uddin linked to financing application 2. Fully executed and locked.','FINANCING','2026-02-02 09:40:00.000000','2026-02-03 11:00:00.000000',2,'FINANCING','Final executed lease contract','2026-02-04 12:10:00.000000','Customer Signer 03','Shariah Signer 03','2026-02-04 12:10:00.000000','ACTIVE','2026-02-04 12:10:00.000000',3,3,NULL),(4,'CTR-00004','ACTIVE','Deposit scheme enrollment contract for Sadia Islam linked to enrollment 1. Customer signed and pending final board sign.','DEPOSIT_SCHEME','2026-02-02 10:00:00.000000','2026-02-03 13:20:00.000000',1,'DEPOSIT_SCHEME','Customer signed savings scheme terms',NULL,'Customer Signer 04',NULL,NULL,'ACTIVE','2026-02-03 13:20:00.000000',4,4,NULL),(5,'CTR-00005','LOCKED','Card issue undertaking for Al-Amin Traders linked to card issue 1. Fully signed and locked.','CARD_ISSUE','2026-02-02 10:20:00.000000','2026-02-03 14:00:00.000000',1,'CARD_ISSUE','Corporate card issue finalized','2026-02-04 15:30:00.000000','Customer Signer 05','Shariah Signer 05','2026-02-04 15:30:00.000000','ACTIVE','2026-02-04 15:30:00.000000',5,5,NULL),(6,'CTR-00006','DRAFT','General service contract for Tanvir Ahmed prepared from general reference 6. Awaiting review edits.','GENERAL','2026-02-02 10:40:00.000000',NULL,6,'GENERAL','Draft advisory scope under review',NULL,NULL,NULL,NULL,'ACTIVE','2026-02-02 10:40:00.000000',6,6,NULL),(7,'CTR-00007','ACTIVE','Premium account opening contract for Nadia Rahman linked to request 7. Customer signed only.','ACCOUNT_OPENING','2026-02-02 11:00:00.000000','2026-02-03 16:10:00.000000',7,'ACCOUNT_OPENING','Customer accepted premium account obligations',NULL,'Customer Signer 07',NULL,NULL,'ACTIVE','2026-02-03 16:10:00.000000',7,7,NULL),(8,'CTR-00008','LOCKED','Working capital financing contract for Rafiq Hasan linked to financing application 8. Finalized and locked.','FINANCING','2026-02-02 11:20:00.000000','2026-02-03 17:00:00.000000',8,'FINANCING','Final working capital contract executed','2026-02-04 17:45:00.000000','Customer Signer 08','Shariah Signer 08','2026-02-04 17:45:00.000000','ACTIVE','2026-02-04 17:45:00.000000',8,8,NULL),(9,'CTR-00009','DRAFT','Long-term savings scheme contract for Farhana Akter created from enrollment 9 and awaiting first signature.','DEPOSIT_SCHEME','2026-02-02 11:40:00.000000',NULL,9,'DEPOSIT_SCHEME','Draft contract waiting for customer acknowledgment',NULL,NULL,NULL,NULL,'ACTIVE','2026-02-02 11:40:00.000000',9,9,NULL),(10,'CTR-00010','ACTIVE','Medical equipment financing contract for Shakil Ahmed linked to financing application 10. Customer signed and active.','FINANCING','2026-02-02 12:00:00.000000','2026-02-03 18:20:00.000000',10,'FINANCING','Customer signed medical financing deal',NULL,'Customer Signer 10',NULL,NULL,'ACTIVE','2026-02-03 18:20:00.000000',10,10,NULL),(11,'CTR-00011','LOCKED','Corporate card contract for Jannatul Ferdous linked to card issue 11. Shariah and customer signatures completed.','CARD_ISSUE','2026-02-02 12:20:00.000000','2026-02-03 19:10:00.000000',11,'CARD_ISSUE','Card issue contract locked','2026-02-04 19:45:00.000000','Customer Signer 11','Shariah Signer 11','2026-02-04 19:45:00.000000','ACTIVE','2026-02-04 19:45:00.000000',11,11,NULL),(12,'CTR-00012','DRAFT','Shariah advisory service contract for Mahmudul Hasan from general reference 12. Drafted for review.','GENERAL','2026-02-02 12:40:00.000000',NULL,12,'GENERAL','Draft service scope not yet signed',NULL,NULL,NULL,NULL,'ACTIVE','2026-02-02 12:40:00.000000',12,12,NULL),(13,'CTR-00013','ACTIVE','SME account opening contract for Tasnia Ahmed linked to request 13 with customer signature completed.','ACCOUNT_OPENING','2026-02-02 13:00:00.000000','2026-02-03 20:00:00.000000',13,'ACCOUNT_OPENING','Customer signature captured for SME account opening',NULL,'Customer Signer 13',NULL,NULL,'ACTIVE','2026-02-03 20:00:00.000000',13,13,NULL),(14,'CTR-00014','LOCKED','Commodity financing contract for Kamrul Islam linked to financing application 14. Fully signed and stored as final.','FINANCING','2026-02-02 13:20:00.000000','2026-02-03 20:40:00.000000',14,'FINANCING','Commodity financing fully executed','2026-02-04 21:30:00.000000','Customer Signer 14','Shariah Signer 14','2026-02-04 21:30:00.000000','ACTIVE','2026-02-04 21:30:00.000000',14,14,NULL),(15,'CTR-00015','ACTIVE','General undertaking for Rokeya Begum linked to general reference 15. Customer sign exists and final lock pending.','GENERAL','2026-02-02 13:40:00.000000','2026-02-03 21:10:00.000000',15,'GENERAL','General undertaking customer-signed',NULL,'Customer Signer 15',NULL,NULL,'ACTIVE','2026-02-03 21:10:00.000000',15,15,NULL);

--
-- Table structure for table `contract_template`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contract_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `contract_type` enum('ACCOUNT_OPENING','CARD_ISSUE','DEPOSIT_SCHEME','FINANCING','GENERAL') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `template_body` longtext NOT NULL,
  `template_code` varchar(40) NOT NULL,
  `template_name` varchar(160) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `version_no` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_contract_template_code` (`template_code`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contract_template`
--

INSERT INTO `contract_template` VALUES (1,'ACCOUNT_OPENING','2026-02-01 09:00:00.000000','ACTIVE','Account opening agreement for {{customerName}} ({{customerCode}}) under contract {{contractNo}} dated {{today}}.','CTM-00001','Account Opening Standard','2026-02-01 09:00:00.000000',1),(2,'FINANCING','2026-02-01 09:05:00.000000','ACTIVE','Murabaha financing agreement for {{customerName}} against {{referenceModule}} #{{referenceId}} under contract {{contractNo}}.','CTM-00002','Financing Murabaha Core','2026-02-01 09:05:00.000000',1),(3,'FINANCING','2026-02-01 09:10:00.000000','ACTIVE','Ijarah rental contract for {{customerName}} generated as {{contractNo}} from template {{templateName}}.','CTM-00003','Financing Ijarah Lease','2026-02-01 09:10:00.000000',2),(4,'DEPOSIT_SCHEME','2026-02-01 09:15:00.000000','ACTIVE','Deposit scheme enrollment contract for {{customerName}} linked to reference {{referenceId}}.','CTM-00004','Deposit Scheme Enrollment','2026-02-01 09:15:00.000000',1),(5,'CARD_ISSUE','2026-02-01 09:20:00.000000','ACTIVE','Card issuance acknowledgement for {{customerName}} under contract {{contractNo}}.','CTM-00005','Card Issue Undertaking','2026-02-01 09:20:00.000000',1),(6,'GENERAL','2026-02-01 09:25:00.000000','ACTIVE','General service contract generated for {{customerName}} with source {{referenceModule}} / {{referenceId}}.','CTM-00006','General Service Contract','2026-02-01 09:25:00.000000',1),(7,'ACCOUNT_OPENING','2026-02-01 09:30:00.000000','ACTIVE','Premium account opening clauses for {{customerName}} under agreement {{contractNo}}.','CTM-00007','Account Opening Premium','2026-02-01 09:30:00.000000',2),(8,'FINANCING','2026-02-01 09:35:00.000000','ACTIVE','Working capital financing contract prepared for {{customerName}} under reference {{referenceId}}.','CTM-00008','Working Capital Financing','2026-02-01 09:35:00.000000',1),(9,'DEPOSIT_SCHEME','2026-02-01 09:40:00.000000','ACTIVE','Long-term savings scheme contract for {{customerName}} generated on {{today}}.','CTM-00009','Savings Scheme Long Term','2026-02-01 09:40:00.000000',1),(10,'FINANCING','2026-02-01 09:45:00.000000','ACTIVE','Medical equipment financing agreement for {{customerName}} with contract no {{contractNo}}.','CTM-00010','Medical Equipment Financing','2026-02-01 09:45:00.000000',1),(11,'CARD_ISSUE','2026-02-01 09:50:00.000000','ACTIVE','Corporate card issue contract for {{customerName}} using template {{templateName}}.','CTM-00011','Corporate Card Contract','2026-02-01 09:50:00.000000',2),(12,'GENERAL','2026-02-01 09:55:00.000000','ACTIVE','Shariah advisory service contract for {{customerName}} against general reference {{referenceId}}.','CTM-00012','Shariah Advisory Service','2026-02-01 09:55:00.000000',1),(13,'ACCOUNT_OPENING','2026-02-01 10:00:00.000000','ACTIVE','SME account opening agreement for {{customerName}} prepared under {{contractNo}}.','CTM-00013','SME Account Opening','2026-02-01 10:00:00.000000',1),(14,'FINANCING','2026-02-01 10:05:00.000000','ACTIVE','Commodity financing contract for {{customerName}} generated from financing case {{referenceId}}.','CTM-00014','Commodity Financing Contract','2026-02-01 10:05:00.000000',1),(15,'GENERAL','2026-02-01 10:10:00.000000','ACTIVE','Final general undertaking executed by {{customerName}} under contract {{contractNo}}.','CTM-00015','General Undertaking','2026-02-01 10:10:00.000000',1);

--
-- Table structure for table `contract_version`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contract_version` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `change_note` varchar(1000) DEFAULT NULL,
  `change_type` varchar(40) NOT NULL,
  `changed_by` varchar(160) DEFAULT NULL,
  `contract_text` longtext NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `version_no` int NOT NULL,
  `contract_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqmnfve2g8fqfw1uvahr64ci51` (`contract_id`),
  CONSTRAINT `FKqmnfve2g8fqfw1uvahr64ci51` FOREIGN KEY (`contract_id`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contract_version`
--

INSERT INTO `contract_version` VALUES (1,'Initial generated draft','GENERATED','SYSTEM_GENERATOR','Account opening draft contract for Md. Masud Rana linked to request 1. Awaiting customer signature.','2026-02-02 09:00:00.000000','ACTIVE',1,1),(2,'Initial generated draft','GENERATED','SYSTEM_GENERATOR','Murabaha contract for Md. Woalinur linked to financing application 1. Customer signed, waiting for shariah sign.','2026-02-02 09:20:00.000000','ACTIVE',1,2),(3,'Customer signed financing terms','CUSTOMER_SIGN','Customer Signer 02','Murabaha contract for Md. Woalinur linked to financing application 1. Customer signed, waiting for shariah sign.','2026-02-03 10:15:00.000000','ACTIVE',2,2),(4,'Initial generated draft','GENERATED','SYSTEM_GENERATOR','Ijarah contract for Md. Rahim Uddin linked to financing application 2. Fully executed and locked.','2026-02-02 09:40:00.000000','ACTIVE',1,3),(5,'Customer signed lease terms','CUSTOMER_SIGN','Customer Signer 03','Ijarah contract for Md. Rahim Uddin linked to financing application 2. Fully executed and locked.','2026-02-03 11:00:00.000000','ACTIVE',2,3),(6,'Shariah sign completed','SHARIAH_SIGN','Shariah Signer 03','Ijarah contract for Md. Rahim Uddin linked to financing application 2. Fully executed and locked.','2026-02-04 12:10:00.000000','ACTIVE',3,3),(7,'Initial generated draft','GENERATED','SYSTEM_GENERATOR','Deposit scheme enrollment contract for Sadia Islam linked to enrollment 1. Customer signed and pending final board sign.','2026-02-02 10:00:00.000000','ACTIVE',1,4),(8,'Customer accepted savings scheme terms','CUSTOMER_SIGN','Customer Signer 04','Deposit scheme enrollment contract for Sadia Islam linked to enrollment 1. Customer signed and pending final board sign.','2026-02-03 13:20:00.000000','ACTIVE',2,4),(9,'Initial generated draft','GENERATED','SYSTEM_GENERATOR','Card issue undertaking for Al-Amin Traders linked to card issue 1. Fully signed and locked.','2026-02-02 10:20:00.000000','ACTIVE',1,5),(10,'Customer signed card undertaking','CUSTOMER_SIGN','Customer Signer 05','Card issue undertaking for Al-Amin Traders linked to card issue 1. Fully signed and locked.','2026-02-03 14:00:00.000000','ACTIVE',2,5),(11,'Shariah sign completed','SHARIAH_SIGN','Shariah Signer 05','Card issue undertaking for Al-Amin Traders linked to card issue 1. Fully signed and locked.','2026-02-04 15:30:00.000000','ACTIVE',3,5),(12,'Initial generated draft','GENERATED','SYSTEM_GENERATOR','General service contract for Tanvir Ahmed prepared from general reference 6. Awaiting review edits.','2026-02-02 10:40:00.000000','ACTIVE',1,6),(13,'Initial generated draft','GENERATED','SYSTEM_GENERATOR','Premium account opening contract for Nadia Rahman linked to request 7. Customer signed only.','2026-02-02 11:00:00.000000','ACTIVE',1,7),(14,'Customer signed premium account terms','CUSTOMER_SIGN','Customer Signer 07','Premium account opening contract for Nadia Rahman linked to request 7. Customer signed only.','2026-02-03 16:10:00.000000','ACTIVE',2,7),(15,'Initial generated draft','GENERATED','SYSTEM_GENERATOR','Working capital financing contract for Rafiq Hasan linked to financing application 8. Finalized and locked.','2026-02-02 11:20:00.000000','ACTIVE',1,8),(16,'Customer signed working capital contract','CUSTOMER_SIGN','Customer Signer 08','Working capital financing contract for Rafiq Hasan linked to financing application 8. Finalized and locked.','2026-02-03 17:00:00.000000','ACTIVE',2,8),(17,'Shariah sign completed','SHARIAH_SIGN','Shariah Signer 08','Working capital financing contract for Rafiq Hasan linked to financing application 8. Finalized and locked.','2026-02-04 17:45:00.000000','ACTIVE',3,8),(18,'Initial generated draft','GENERATED','SYSTEM_GENERATOR','Long-term savings scheme contract for Farhana Akter created from enrollment 9 and awaiting first signature.','2026-02-02 11:40:00.000000','ACTIVE',1,9),(19,'Initial generated draft','GENERATED','SYSTEM_GENERATOR','Medical equipment financing contract for Shakil Ahmed linked to financing application 10. Customer signed and active.','2026-02-02 12:00:00.000000','ACTIVE',1,10),(20,'Customer signed medical financing deal','CUSTOMER_SIGN','Customer Signer 10','Medical equipment financing contract for Shakil Ahmed linked to financing application 10. Customer signed and active.','2026-02-03 18:20:00.000000','ACTIVE',2,10),(21,'Initial generated draft','GENERATED','SYSTEM_GENERATOR','Corporate card contract for Jannatul Ferdous linked to card issue 11. Shariah and customer signatures completed.','2026-02-02 12:20:00.000000','ACTIVE',1,11),(22,'Customer signed card contract','CUSTOMER_SIGN','Customer Signer 11','Corporate card contract for Jannatul Ferdous linked to card issue 11. Shariah and customer signatures completed.','2026-02-03 19:10:00.000000','ACTIVE',2,11),(23,'Shariah sign completed','SHARIAH_SIGN','Shariah Signer 11','Corporate card contract for Jannatul Ferdous linked to card issue 11. Shariah and customer signatures completed.','2026-02-04 19:45:00.000000','ACTIVE',3,11),(24,'Initial generated draft','GENERATED','SYSTEM_GENERATOR','Shariah advisory service contract for Mahmudul Hasan from general reference 12. Drafted for review.','2026-02-02 12:40:00.000000','ACTIVE',1,12),(25,'Initial generated draft','GENERATED','SYSTEM_GENERATOR','SME account opening contract for Tasnia Ahmed linked to request 13 with customer signature completed.','2026-02-02 13:00:00.000000','ACTIVE',1,13),(26,'Customer signature captured for SME account opening','CUSTOMER_SIGN','Customer Signer 13','SME account opening contract for Tasnia Ahmed linked to request 13 with customer signature completed.','2026-02-03 20:00:00.000000','ACTIVE',2,13),(27,'Initial generated draft','GENERATED','SYSTEM_GENERATOR','Commodity financing contract for Kamrul Islam linked to financing application 14. Fully signed and stored as final.','2026-02-02 13:20:00.000000','ACTIVE',1,14),(28,'Customer signed commodity financing deal','CUSTOMER_SIGN','Customer Signer 14','Commodity financing contract for Kamrul Islam linked to financing application 14. Fully signed and stored as final.','2026-02-03 20:40:00.000000','ACTIVE',2,14),(29,'Shariah sign completed','SHARIAH_SIGN','Shariah Signer 14','Commodity financing contract for Kamrul Islam linked to financing application 14. Fully signed and stored as final.','2026-02-04 21:30:00.000000','ACTIVE',3,14),(30,'Initial generated draft','GENERATED','SYSTEM_GENERATOR','General undertaking for Rokeya Begum linked to general reference 15. Customer sign exists and final lock pending.','2026-02-02 13:40:00.000000','ACTIVE',1,15),(31,'Customer signed general undertaking','CUSTOMER_SIGN','Customer Signer 15','General undertaking for Rokeya Begum linked to general reference 15. Customer sign exists and final lock pending.','2026-02-03 21:10:00.000000','ACTIVE',2,15);

--
-- Table structure for table `customer`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `customer_code` varchar(30) NOT NULL,
  `customer_status` enum('ACTIVE','BLOCKED','CLOSED','DRAFT','PENDING_KYC','REJECTED') NOT NULL,
  `date_of_birth` date DEFAULT NULL,
  `email` varchar(120) DEFAULT NULL,
  `full_name` varchar(150) NOT NULL,
  `gender` enum('FEMALE','MALE','OTHER') DEFAULT NULL,
  `mobile` varchar(30) DEFAULT NULL,
  `national_id` varchar(50) DEFAULT NULL,
  `onboarding_status` enum('COMPLETED','PENDING') NOT NULL,
  `passport_no` varchar(50) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `branch_id` bigint NOT NULL,
  `created_by` varchar(100) DEFAULT NULL,
  `customer_type` enum('CORPORATE','INDIVIDUAL','JOINT','SME') NOT NULL,
  `father_name` varchar(150) DEFAULT NULL,
  `marital_status` enum('DIVORCED','MARRIED','SINGLE','WIDOWED') DEFAULT NULL,
  `monthly_income` decimal(18,2) DEFAULT NULL,
  `mother_name` varchar(150) DEFAULT NULL,
  `nationality` varchar(80) DEFAULT NULL,
  `occupation` varchar(120) DEFAULT NULL,
  `source_of_funds` varchar(500) DEFAULT NULL,
  `spouse_name` varchar(150) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `updated_by` varchar(100) DEFAULT NULL,
  `email_verified` bit(1) NOT NULL,
  `mobile_verified` bit(1) NOT NULL,
  `profile_image_name` varchar(180) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_customer_customer_code` (`customer_code`),
  UNIQUE KEY `uk_customer_code` (`customer_code`),
  UNIQUE KEY `uk_customer_mobile` (`mobile`),
  UNIQUE KEY `uk_customer_national_id` (`national_id`),
  UNIQUE KEY `uk_customer_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` VALUES (1,'2026-04-15 11:29:47.231526','CUST001','ACTIVE','2001-01-03','user021@example.com','Md. Akib Uddin','MALE','10575634380','463472883','PENDING','23846398247239','2026-06-05 04:09:39.000000',NULL,0,NULL,'CORPORATE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ACTIVE','PROFILE_SYNC',_binary '',_binary '\0','100045_IMG_20260321_091445~3 - Md. Akib Uddin.jpg'),(2,'2026-04-16 11:33:50.330067','CUST002','ACTIVE','1997-06-16','user030@example.com','Muhit Hasan','MALE','01700000039','234235525','PENDING','234355252','2026-06-05 04:09:39.000000',NULL,0,NULL,'CORPORATE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ACTIVE','PROFILE_SYNC',_binary '\0',_binary '','100046_Muhit Hasan.jpg'),(3,'2026-04-27 11:28:48.095969','CUS-000001','PENDING_KYC','1990-05-10','user026@example.com','Rakibul Hasan','MALE','01700000044',NULL,'COMPLETED',NULL,'2026-06-05 04:09:39.000000',NULL,1,'SYSTEM','INDIVIDUAL','Abdul Karim','MARRIED',50000.00,'Salma Begum','Bangladeshi','Business','Trading Business',NULL,'ACTIVE','PROFILE_SYNC',_binary '\0',_binary '\0','100047_Rakibul Hasan.jpg'),(4,'2026-04-27 11:29:16.159969','CUS-000002','PENDING_KYC','1995-08-12','user031@example.com','Abdullah Naser Zayed','FEMALE','01700000040',NULL,'COMPLETED',NULL,'2026-06-05 04:09:39.000000',NULL,1,'SYSTEM','INDIVIDUAL','Mizanur Rahman','SINGLE',20000.00,'Rokeya Begum','Bangladeshi','Student','Freelancing',NULL,'ACTIVE','PROFILE_SYNC',_binary '\0',_binary '\0','100048_Zayed - Abdullah Naser Zayed.jpg'),(5,'2026-04-27 11:29:31.123972','CUS-000003','REJECTED',NULL,'user027@example.com','Samira Saba',NULL,'01700000045',NULL,'COMPLETED',NULL,'2026-06-05 04:09:39.000000',NULL,2,'SYSTEM','SME',NULL,NULL,150000.00,NULL,NULL,'Wholesale Business','Business Profit',NULL,'ACTIVE','PROFILE_SYNC',_binary '\0',_binary '\0','100049_Samira Saba.jpg'),(6,'2026-04-27 11:30:31.962084','CUS-000004','PENDING_KYC','1992-03-15','user032@example.com','Rakibul Hasan','MALE','01700000041',NULL,'COMPLETED',NULL,'2026-06-05 04:09:39.000000',NULL,1,'SYSTEM','INDIVIDUAL','Abdur Rashid','MARRIED',75000.00,'Nasima Begum','Bangladeshi','Engineer','Job Salary',NULL,'ACTIVE','PROFILE_SYNC',_binary '\0',_binary '\0','100050_Rakibul Hasan.jpg'),(7,'2026-04-27 11:30:41.606064','CUS-000005','ACTIVE','1998-11-20','user028@example.com','M Olinur','FEMALE','01700000046',NULL,'COMPLETED',NULL,'2026-06-05 04:09:39.000000',NULL,2,'SYSTEM','INDIVIDUAL','Habibur Rahman','SINGLE',35000.00,'Shahnaz Begum','Bangladeshi','Teacher','Salary',NULL,'ACTIVE','PROFILE_SYNC',_binary '',_binary '\0','100051_rsz_1p- - M Olinur.jpg'),(8,'2026-04-27 11:30:52.656118','CUS-000006','PENDING_KYC','1985-06-25','user033@example.com','Mahadi Hasan','MALE','01700000042',NULL,'COMPLETED',NULL,'2026-06-05 04:09:39.000000',NULL,1,'SYSTEM','INDIVIDUAL','Abdul Jalil','MARRIED',120000.00,'Sufia Khatun','Bangladeshi','Businessman','Business',NULL,'ACTIVE','PROFILE_SYNC',_binary '\0',_binary '','100052_88 - Mahadi Hasan (1).jpg'),(9,'2026-04-27 11:31:05.836471','CUS-000007','PENDING_KYC','1993-09-10','user029@example.com','Muhit Hasan','FEMALE','01700000047',NULL,'COMPLETED',NULL,'2026-06-05 04:09:39.000000',NULL,3,'SYSTEM','INDIVIDUAL','Abdul Majid','MARRIED',60000.00,'Rahima Begum','Bangladeshi','Bank Officer','Salary',NULL,'ACTIVE','PROFILE_SYNC',_binary '\0',_binary '\0','100053_Muhit Hasan.jpg'),(10,'2026-04-27 11:31:17.198544','CUS-000008','PENDING_KYC','1991-01-05','user034@example.com','Arman Mamun','MALE','01700000043',NULL,'COMPLETED',NULL,'2026-06-05 04:09:39.000000',NULL,2,'SYSTEM','INDIVIDUAL','Abul Kashem','SINGLE',25000.00,'Jahanara Begum','Bangladeshi','Driver','Driving',NULL,'ACTIVE','PROFILE_SYNC',_binary '\0',_binary '\0','100054_Arman Mamun.jpg'),(11,'2026-04-27 11:31:36.358509','CUS-000009','PENDING_KYC','1997-04-18','user035@example.com','Md. Firoz','FEMALE','01700000048',NULL,'COMPLETED',NULL,'2026-06-05 04:09:39.000000',NULL,1,'SYSTEM','INDIVIDUAL','Nurul Islam','SINGLE',45000.00,'Sufia Begum','Bangladeshi','Freelancer','Online Work',NULL,'ACTIVE','PROFILE_SYNC',_binary '\0',_binary '\0','100055_Md. Firoz.jpg'),(12,'2026-04-27 11:31:46.190438','CUS-000010','ACTIVE','1989-07-22','user036@example.com','Arman Mamun','MALE','01700000049',NULL,'COMPLETED',NULL,'2026-06-05 04:09:39.000000',NULL,3,'SYSTEM','INDIVIDUAL','Abdul Hamid','MARRIED',55000.00,'Amena Khatun','Bangladeshi','Govt Job','Salary','','ACTIVE','PROFILE_SYNC',_binary '\0',_binary '\0','100056_Arman Mamun.jpg'),(13,'2026-04-27 11:32:07.556756','CUS-000011','REJECTED','2000-02-11','user037@example.com','Samira Saba','FEMALE','01700000050',NULL,'COMPLETED',NULL,'2026-06-05 04:09:39.000000',NULL,2,'SYSTEM','INDIVIDUAL','Rashid Ahmed','SINGLE',15000.00,'Parvin Begum','Bangladeshi','Student','Tuition',NULL,'ACTIVE','PROFILE_SYNC',_binary '\0',_binary '\0','100057_Samira Saba.jpg'),(14,'2026-04-27 11:32:20.317159','CUS-000012','PENDING_KYC','1987-12-30','user038@example.com','M Olinur','MALE','01700000051',NULL,'COMPLETED',NULL,'2026-06-05 04:09:39.000000',NULL,1,'SYSTEM','INDIVIDUAL','Abdul Gani','MARRIED',40000.00,'Momena Begum','Bangladeshi','Shop Owner','Business',NULL,'ACTIVE','PROFILE_SYNC',_binary '\0',_binary '\0','100058_rsz_1p- - M Olinur.jpg'),(15,'2026-04-27 11:32:31.978103','CUS-000013','BLOCKED','1994-10-08','user039@example.com','Faiaz Ajmeen','FEMALE','01700000052',NULL,'COMPLETED',NULL,'2026-06-13 02:06:13.705181',NULL,3,'SYSTEM','INDIVIDUAL','Abdul Mannan','MARRIED',20000.00,'Jahanara Khatun','Bangladeshi','Tailor','Sewing','','ACTIVE','SYSTEM',_binary '\0',_binary '\0','38cac110-2a32-4acc-9c0f-37789b299ea7_Fiaaz Ajmaeen.jpg'),(16,'2026-05-16 01:30:13.947270','CUS-000014','PENDING_KYC','1992-01-15','user040@example.com','Saifur Saif','MALE','01700000053',NULL,'COMPLETED',NULL,'2026-06-05 04:09:39.000000',NULL,1,'branch.staff.013012','INDIVIDUAL','Abdul Karim','SINGLE',55000.00,'Rokeya Begum','Bangladeshi','Small Business','Business Income','','ACTIVE','PROFILE_SYNC',_binary '\0',_binary '\0','100060_Saifur Saif.jpg'),(17,'2026-05-16 01:30:50.530703','CUS-000015','PENDING_KYC','1992-01-15','user041@example.com','Muna Akter','MALE','01700000054',NULL,'COMPLETED',NULL,'2026-06-05 04:09:39.000000',NULL,1,'branch.staff.013048','INDIVIDUAL','Abdul Karim','SINGLE',55000.00,'Rokeya Begum','Bangladeshi','Small Business','Business Income','','ACTIVE','PROFILE_SYNC',_binary '\0',_binary '\0','100061_Muna Akter.jpg'),(18,'2026-05-16 01:31:24.932344','CUS-000016','PENDING_KYC','1992-01-15','user042@example.com','Nur Nabi','MALE','01700000055',NULL,'COMPLETED',NULL,'2026-06-05 04:09:39.000000',NULL,1,'branch.staff.013122','INDIVIDUAL','Abdul Karim','SINGLE',55000.00,'Rokeya Begum','Bangladeshi','Small Business','Business Income','','ACTIVE','PROFILE_SYNC',_binary '\0',_binary '\0','100062_Nur Nabi.jpg'),(19,'2026-05-16 01:32:45.640636','CUS-000017','PENDING_KYC','1992-01-15','user043@example.com','Fabiha Anbar','MALE','01700000056',NULL,'COMPLETED',NULL,'2026-06-05 04:09:40.000000',NULL,1,'branch.staff.013244','INDIVIDUAL','Abdul Karim','SINGLE',55000.00,'Rokeya Begum','Bangladeshi','Small Business','Business Income','','ACTIVE','PROFILE_SYNC',_binary '\0',_binary '\0','100063_Fabiha Anbar.jpg'),(20,'2026-05-16 01:36:30.554070','CUS-000018','ACTIVE','1992-01-15','user044@example.com','Samia Rahman','MALE','01700000057',NULL,'COMPLETED',NULL,'2026-06-05 04:09:40.000000',NULL,1,'branch.staff.013629','INDIVIDUAL','Abdul Karim','SINGLE',55000.00,'Rokeya Begum','Bangladeshi','Small Business','Business Income','','ACTIVE','PROFILE_SYNC',_binary '\0',_binary '\0','100064_Samia Rahman.jpg'),(21,'2026-05-16 08:07:12.094822','CUS-000019','ACTIVE','1992-01-15','user045@example.com','S.M. Tasrif Zaman','MALE','01700000058',NULL,'COMPLETED',NULL,'2026-06-05 04:09:40.000000',NULL,1,'branch.staff.080710','INDIVIDUAL','Abdul Karim','SINGLE',55000.00,'Rokeya Begum','Bangladeshi','Small Business','Business Income','','ACTIVE','PROFILE_SYNC',_binary '\0',_binary '\0','100065__20190816_150404 - S.M. Tasrif Zaman.jpg'),(22,'2026-05-16 08:09:52.595422','CUS-000020','ACTIVE','1992-01-15','user046@example.com','Zarin Islam','MALE','01700000059',NULL,'COMPLETED',NULL,'2026-06-05 04:09:40.000000',NULL,1,'branch.staff.080951','INDIVIDUAL','Abdul Karim','SINGLE',55000.00,'Rokeya Begum','Bangladesh','Small Business','Business Income','','ACTIVE','PROFILE_SYNC',_binary '\0',_binary '\0','100066_Zarin Islam.jpg'),(23,'2026-06-13 03:43:53.108662','CUS-000021','ACTIVE','1992-03-18','user047@example.com','Abdullah Naser Zayed','MALE','01700000060',NULL,'COMPLETED',NULL,'2026-06-13 11:21:00.363111',NULL,1,'SYSTEM','INDIVIDUAL','Abdul Karim','MARRIED',85000.00,'Fatema Begum','Bangladesh','Small Business Owner','Retail business income','','ACTIVE','SYSTEM',_binary '\0',_binary '\0','981afb52-eb8e-437a-9268-1a56dc7236a9_Zayed - Abdullah Naser Zayed.jpg');

--
-- Table structure for table `customer_address`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_address` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address_type` enum('OFFICE','PERMANENT','PRESENT','REGISTERED') NOT NULL,
  `city` varchar(100) DEFAULT NULL,
  `country` varchar(80) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `customer_id` bigint NOT NULL,
  `district` varchar(100) DEFAULT NULL,
  `line_1` varchar(200) NOT NULL,
  `line_2` varchar(200) DEFAULT NULL,
  `postal_code` varchar(20) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `address_line1` varchar(250) NOT NULL,
  `address_line2` varchar(250) DEFAULT NULL,
  `country_id` bigint DEFAULT NULL,
  `district_id` bigint DEFAULT NULL,
  `division_id` bigint DEFAULT NULL,
  `is_primary` bit(1) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `upazila_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKr9ofa0ydsgbaqmt9leb3v5eii` (`customer_id`),
  CONSTRAINT `FKr9ofa0ydsgbaqmt9leb3v5eii` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_address`
--

INSERT INTO `customer_address` VALUES (1,'PRESENT','Dhaka','Bangladesh','2026-04-15 11:29:47.272529',1,'Dhaka District','House 12, Road 4, Dhanmondi','Near main road','1207','2026-05-01 09:18:33.000000','House 12, Road 4, Dhanmondi','Near main road',1,1001,101,_binary '','ACTIVE',10001),(2,'PRESENT','Dhaka','Bangladesh','2026-04-16 11:33:50.343065',2,'Gazipur','Plot 18, Sector 7, Uttara','North side lane','1230','2026-05-01 09:18:33.000000','Plot 18, Sector 7, Uttara','North side lane',1,1002,101,_binary '','ACTIVE',10003),(3,'PRESENT','Dhaka','Bangladesh','2026-05-01 09:18:33.000000',3,'Dhaka District','House 21, Road 3, Dhanmondi','Block A','1209','2026-05-01 09:18:33.000000','House 21, Road 3, Dhanmondi','Block A',1,1001,101,_binary '','ACTIVE',10001),(4,'PERMANENT','Dhaka','Bangladesh','2026-05-01 09:18:33.000000',4,'Dhaka District','Village Home 7, Keraniganj','Post Office para','1310','2026-05-01 09:18:33.000000','Village Home 7, Keraniganj','Post Office para',1,1001,101,_binary '\0','ACTIVE',10002),(5,'OFFICE','Dhaka','Bangladesh','2026-05-01 09:18:33.000000',5,'Dhaka District','Shop 44, Islami Market','Ground floor','1100','2026-05-01 09:18:33.000000','Shop 44, Islami Market','Ground floor',1,1001,101,_binary '\0','ACTIVE',10001),(6,'PRESENT','Dhaka','Bangladesh','2026-05-01 09:18:33.000000',6,'Gazipur','Flat 5B, House 22, Uttara','Sector 10','1230','2026-05-01 09:18:33.000000','Flat 5B, House 22, Uttara','Sector 10',1,1002,101,_binary '','ACTIVE',10003),(7,'PRESENT','Dhaka','Bangladesh','2026-05-01 09:18:33.000000',7,'Dhaka District','House 33, Mirpur DOHS','West gate','1216','2026-05-01 09:18:33.000000','House 33, Mirpur DOHS','West gate',1,1001,101,_binary '','ACTIVE',10002),(8,'REGISTERED','Chattogram','Bangladesh','2026-05-01 09:18:33.000000',8,'Chattogram District','Corporate Registry Address, Agrabad','Suite 12','4100','2026-05-01 09:18:33.000000','Corporate Registry Address, Agrabad','Suite 12',1,1003,102,_binary '\0','ACTIVE',10004),(9,'PRESENT','Khulna','Bangladesh','2026-05-01 09:18:33.000000',9,'Khulna District','House 8, Sonadanga','Lane 5','9000','2026-05-01 09:18:33.000000','House 8, Sonadanga','Lane 5',1,1004,103,_binary '','ACTIVE',10005),(10,'PERMANENT','Dhaka','Bangladesh','2026-05-01 09:18:33.000000',10,'Dhaka District','Village Bari, Mohammadpur','North cluster','1207','2026-05-01 09:18:33.000000','Village Bari, Mohammadpur','North cluster',1,1001,101,_binary '\0','ACTIVE',10001),(11,'OFFICE','Dhaka','Bangladesh','2026-05-01 09:18:33.000000',11,'Dhaka District','Office 3C, Motijheel','Commerce tower','1000','2026-05-01 09:18:33.000000','Office 3C, Motijheel','Commerce tower',1,1001,101,_binary '\0','ACTIVE',10001),(12,'PRESENT','Dhaka','Bangladesh','2026-05-01 09:18:33.000000',12,'Dhaka District','House 14, Banasree','Road 9','1219','2026-05-01 09:18:33.000000','House 14, Banasree','Road 9',1,1001,101,_binary '','ACTIVE',10002),(13,'REGISTERED','Dhaka','Bangladesh','2026-05-01 09:18:33.000000',13,'Dhaka District','Trade Center, Gulshan','Level 4','1212','2026-05-01 09:18:33.000000','Trade Center, Gulshan','Level 4',1,1001,101,_binary '\0','ACTIVE',10002),(14,'PRESENT','Dhaka','Bangladesh','2026-05-01 12:20:01.000000',14,'Dhaka District','House 77, Mohammadpur','East block','1207','2026-05-01 12:20:01.000000','House 77, Mohammadpur','East block',1,1001,101,_binary '','ACTIVE',10001),(15,'REGISTERED','Chattogram','Bangladesh','2026-05-01 12:20:01.000000',15,'Chattogram District','Registered Office, GEC Circle','Floor 6','4000','2026-05-01 12:20:01.000000','Registered Office, GEC Circle','Floor 6',1,1003,102,_binary '','ACTIVE',10004);

--
-- Table structure for table `customer_document`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_document` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `document_no` varchar(80) DEFAULT NULL,
  `document_type` enum('BANK_STATEMENT','BIRTH_CERTIFICATE','DRIVING_LICENSE','NID','OTHER','PASSPORT','PHOTO','TIN','TRADE_LICENSE','UTILITY_BILL') NOT NULL,
  `expiry_date` date DEFAULT NULL,
  `file_reference_id` varchar(120) DEFAULT NULL,
  `issue_date` date DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `verified_flag` bit(1) NOT NULL,
  `customer_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5146mcr0wlbs9a4lr0kx7uusr` (`customer_id`),
  CONSTRAINT `FK5146mcr0wlbs9a4lr0kx7uusr` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_document`
--

INSERT INTO `customer_document` VALUES (13,'2026-05-01 13:16:50.000000','KDOC-001','NID',NULL,'KYC-DOC-001','2017-01-10','ACTIVE',_binary '',1),(14,'2026-05-01 13:16:50.000000','KDOC-002','TRADE_LICENSE','2028-12-31','KYC-DOC-002','2023-01-01','ACTIVE',_binary '',2),(15,'2026-05-01 13:16:50.000000','KDOC-003','TIN',NULL,'KYC-DOC-003','2020-02-14','ACTIVE',_binary '\0',3),(16,'2026-05-01 13:16:50.000000','KDOC-004','UTILITY_BILL','2027-03-05','KYC-DOC-004','2026-03-05','ACTIVE',_binary '\0',4),(17,'2026-05-01 13:16:50.000000','KDOC-005','BANK_STATEMENT','2026-12-31','KYC-DOC-005','2026-01-01','ACTIVE',_binary '',5),(18,'2026-05-01 13:16:50.000000','KDOC-006','NID',NULL,'KYC-DOC-006','2018-08-08','ACTIVE',_binary '\0',6),(19,'2026-05-01 13:16:50.000000','KDOC-007','PASSPORT','2032-03-15','KYC-DOC-007','2022-03-15','ACTIVE',_binary '',7),(20,'2026-05-01 13:16:50.000000','KDOC-008','TRADE_LICENSE','2029-12-31','KYC-DOC-008','2024-01-01','ACTIVE',_binary '',8),(21,'2026-05-01 13:16:50.000000','KDOC-009','NID',NULL,'KYC-DOC-009','2016-09-19','ACTIVE',_binary '\0',9),(22,'2026-05-01 13:16:50.000000','KDOC-010','DRIVING_LICENSE','2030-04-25','KYC-DOC-010','2020-04-25','ACTIVE',_binary '\0',10),(23,'2026-05-01 13:16:50.000000','KDOC-011','BANK_STATEMENT','2027-02-01','KYC-DOC-011','2026-02-01','ACTIVE',_binary '',11),(24,'2026-05-01 13:16:50.000000','KDOC-012','PASSPORT','2031-03-19','KYC-DOC-012','2021-03-19','ACTIVE',_binary '',12),(25,'2026-05-01 13:16:50.000000','KDOC-013','OTHER','2026-12-31','KYC-DOC-013','2026-01-10','ACTIVE',_binary '\0',13),(26,'2026-05-01 13:16:50.000000','KDOC-014','UTILITY_BILL','2027-02-12','KYC-DOC-014','2026-02-12','ACTIVE',_binary '\0',14),(27,'2026-05-01 13:16:50.000000','KDOC-015','NID',NULL,'KYC-DOC-015','2019-02-20','ACTIVE',_binary '\0',15),(28,'2026-05-05 10:45:29.323430','','NID',NULL,'',NULL,'ACTIVE',_binary '\0',14),(29,'2026-05-16 01:36:31.016063','19999013629','NID','2030-01-01','branch-KYC-013629','2020-01-01','ACTIVE',_binary '\0',20),(30,'2026-05-16 08:07:12.471821','19999080710','NID','2030-01-01','branch-KYC-080710','2020-01-01','ACTIVE',_binary '\0',21),(31,'2026-05-16 08:09:53.017415','19999080951','NID','2030-01-01','branch-KYC-080951','2020-01-01','ACTIVE',_binary '\0',22),(32,'2026-06-06 04:08:44.726289','19999080952','BANK_STATEMENT',NULL,'7acea8ac-6ae0-4b9e-b7f4-57409e8b1f51.pdf',NULL,'ACTIVE',_binary '\0',22),(33,'2026-06-06 12:00:30.712261','19999080711','BANK_STATEMENT',NULL,'e54227ba-be60-4b7b-a2f1-44d70e4180ab_Customer-Statement-CSR-00022.pdf',NULL,'ACTIVE',_binary '\0',21),(34,'2026-06-13 03:43:53.273076','NID20260613034352','NID','2031-01-01','operational-NID-20260613034352.pdf','2021-01-01','ACTIVE',_binary '',23),(35,'2026-06-13 10:11:57.691491','STEP6-DOC-VERIFY','BANK_STATEMENT','2027-06-13','491661e9-4825-493f-8423-1ba601158519_CSR-00016.pdf','2026-06-13','ACTIVE',_binary '',23);

--
-- Table structure for table `customer_identity`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_identity` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `document_no` varchar(80) NOT NULL,
  `document_type` enum('BIRTH_CERTIFICATE','DRIVING_LICENSE','NID','PASSPORT','TIN','TRADE_LICENSE') NOT NULL,
  `expiry_date` date DEFAULT NULL,
  `issue_country` varchar(80) DEFAULT NULL,
  `issue_date` date DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `verified_flag` bit(1) NOT NULL,
  `customer_id` bigint NOT NULL,
  `image_file_name` varchar(180) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_customer_identity_document` (`document_type`,`document_no`),
  KEY `FK72uctdk6ano6wkp1ljexrn41w` (`customer_id`),
  CONSTRAINT `FK72uctdk6ano6wkp1ljexrn41w` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_identity`
--

INSERT INTO `customer_identity` VALUES (1,'2026-05-01 09:18:33.000000','NID-900000001','NID',NULL,'Bangladesh','2016-01-15','ACTIVE',_binary '',1,'100086_Tushar Ahmed.jpg'),(2,'2026-05-01 09:18:33.000000','PASS-900000002','PASSPORT','2031-06-30','Bangladesh','2021-06-30','ACTIVE',_binary '',2,'100087_Omar Faruk.jpg'),(3,'2026-05-01 09:18:33.000000','NID-900000003','NID',NULL,'Bangladesh','2018-03-18','ACTIVE',_binary '\0',3,'100088_Ismita Saha.jpg'),(4,'2026-05-01 09:18:33.000000','TIN-900000004','TIN',NULL,'Bangladesh','2020-02-14','ACTIVE',_binary '',4,'100089_Md. Firoz.jpg'),(5,'2026-05-01 09:18:33.000000','TRADE-900000005','TRADE_LICENSE','2028-12-31','Bangladesh','2023-01-01','ACTIVE',_binary '',5,'100090_Zayed - Abdullah Naser Zayed.jpg'),(6,'2026-05-01 09:18:33.000000','DL-900000006','DRIVING_LICENSE','2030-09-10','Bangladesh','2020-09-10','ACTIVE',_binary '\0',6,'100091_IMG_20260515_193640 - Sarna Aphrodite.jpg'),(7,'2026-05-01 09:18:33.000000','NID-900000007','NID',NULL,'Bangladesh','2017-08-22','ACTIVE',_binary '',7,'100092_Arman Mamun.jpg'),(8,'2026-05-01 09:18:33.000000','PASS-900000008','PASSPORT','2032-04-05','Bangladesh','2022-04-05','ACTIVE',_binary '\0',8,'dc51ef8e-ac19-4760-81ce-4fa901efbdee_Customer-Statement-CSR-00022.pdf'),(9,'2026-05-01 09:18:33.000000','BC-900000009','BIRTH_CERTIFICATE',NULL,'Bangladesh','2007-07-07','ACTIVE',_binary '',9,'100094_Zayed - Abdullah Naser Zayed.jpg'),(10,'2026-05-01 09:18:33.000000','NID-900000010','NID',NULL,'Bangladesh','2015-10-11','ACTIVE',_binary '',10,'100095_Saifur Saif.jpg'),(11,'2026-05-01 09:18:33.000000','TIN-900000011','TIN',NULL,'Bangladesh','2019-11-15','ACTIVE',_binary '\0',11,'100096_Samira Saba.jpg'),(12,'2026-05-01 09:18:33.000000','DL-900000012','DRIVING_LICENSE','2031-03-19','Bangladesh','2021-03-19','ACTIVE',_binary '',12,'100097_rsz_1p- - M Olinur.jpg'),(13,'2026-05-01 09:18:33.000000','PASS-900000013','PASSPORT','2030-01-25','Bangladesh','2020-01-25','ACTIVE',_binary '',13,'100098_Abdullah  - ABDULLAH AL MAHMUD (1).jpg'),(14,'2026-05-01 12:20:01.000000','NID-900000014','NID',NULL,'Bangladesh','2019-02-20','ACTIVE',_binary '',14,'100099_Md. Firoz.jpg'),(15,'2026-05-01 12:20:01.000000','TRADE-900000015','TRADE_LICENSE','2029-12-31','Bangladesh','2024-01-01','ACTIVE',_binary '',15,'100100_Ismita Saha.jpg');

--
-- Table structure for table `customer_statement_request`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_statement_request` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date_from` date NOT NULL,
  `date_to` date NOT NULL,
  `generated_at` datetime(6) DEFAULT NULL,
  `request_no` varchar(40) NOT NULL,
  `request_status` enum('DOWNLOADED','FAILED','GENERATED','REQUESTED') NOT NULL,
  `requested_at` datetime(6) NOT NULL,
  `requested_by` varchar(120) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `account_id` bigint NOT NULL,
  `customer_id` bigint NOT NULL,
  `generated_file_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_customer_statement_request_no` (`request_no`),
  KEY `FKsabufjmp2aqdue6q1u7vd9ku8` (`account_id`),
  KEY `FKda7sowts8g5sr5olu6pohccut` (`customer_id`),
  KEY `FKr5vjnwqgg82d3c1kwj6my35bc` (`generated_file_id`),
  CONSTRAINT `FKda7sowts8g5sr5olu6pohccut` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `FKr5vjnwqgg82d3c1kwj6my35bc` FOREIGN KEY (`generated_file_id`) REFERENCES `file_reference` (`id`),
  CONSTRAINT `FKsabufjmp2aqdue6q1u7vd9ku8` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_statement_request`
--

INSERT INTO `customer_statement_request` VALUES (1,'2026-01-01','2026-01-21','2026-05-01 23:13:50.656477','CSR-00001','GENERATED','2026-05-01 23:13:50.551480','SYSTEM','ACTIVE',1,1,1),(2,'2026-01-05','2026-01-25','2026-05-01 23:13:50.835477','CSR-00002','GENERATED','2026-05-01 23:13:50.816475','SYSTEM','ACTIVE',7,7,2),(3,'2026-01-09','2026-01-29','2026-05-01 23:13:50.898473','CSR-00003','GENERATED','2026-05-01 23:13:50.881476','SYSTEM','ACTIVE',1,1,3),(4,'2026-01-13','2026-02-02','2026-05-01 23:13:50.967476','CSR-00004','GENERATED','2026-05-01 23:13:50.949476','SYSTEM','ACTIVE',7,7,4),(5,'2026-01-17','2026-02-06','2026-05-01 23:13:51.032475','CSR-00005','GENERATED','2026-05-01 23:13:51.018477','SYSTEM','ACTIVE',1,1,5),(6,'2026-01-21','2026-02-10','2026-05-01 23:13:51.086475','CSR-00006','GENERATED','2026-05-01 23:13:51.071477','SYSTEM','ACTIVE',7,7,6),(7,'2026-01-25','2026-02-14','2026-05-01 23:13:51.140473','CSR-00007','GENERATED','2026-05-01 23:13:51.126478','SYSTEM','ACTIVE',1,1,7),(8,'2026-01-29','2026-02-18','2026-05-01 23:13:51.204477','CSR-00008','GENERATED','2026-05-01 23:13:51.188478','SYSTEM','ACTIVE',7,7,8),(9,'2026-02-02','2026-02-22','2026-05-01 23:13:51.300474','CSR-00009','GENERATED','2026-05-01 23:13:51.284473','SYSTEM','ACTIVE',1,1,9),(10,'2026-02-06','2026-02-26','2026-05-01 23:13:51.354475','CSR-00010','GENERATED','2026-05-01 23:13:51.340475','SYSTEM','ACTIVE',7,7,10),(11,'2026-02-10','2026-03-02','2026-05-01 23:13:51.409475','CSR-00011','DOWNLOADED','2026-05-01 23:13:51.390474','SYSTEM','ACTIVE',1,1,11),(12,'2026-02-14','2026-03-06','2026-05-01 23:13:51.459476','CSR-00012','DOWNLOADED','2026-05-01 23:13:51.447473','SYSTEM','ACTIVE',7,7,12),(13,'2026-02-18','2026-03-10','2026-05-01 23:13:51.519476','CSR-00013','DOWNLOADED','2026-05-01 23:13:51.498472','SYSTEM','ACTIVE',1,1,13),(14,'2026-02-22','2026-03-14','2026-05-01 23:13:51.569477','CSR-00014','DOWNLOADED','2026-05-01 23:13:51.557476','SYSTEM','ACTIVE',7,7,14),(15,'2026-02-26','2026-03-18','2026-05-01 23:13:51.618476','CSR-00015','DOWNLOADED','2026-05-01 23:13:51.606476','SYSTEM','ACTIVE',1,1,15),(16,'2026-05-09','2026-05-16','2026-05-16 01:32:54.246747','CSR-00016','GENERATED','2026-05-16 01:32:53.641742','ops.officer01','ACTIVE',16,19,87),(17,'2026-05-09','2026-05-16','2026-05-16 01:36:44.514064','CSR-00017','GENERATED','2026-05-16 01:36:44.132060','ops.officer01','ACTIVE',17,20,89),(18,'2026-05-09','2026-05-16','2026-05-16 08:07:31.084109','CSR-00018','GENERATED','2026-05-16 08:07:30.558105','ops.officer01','ACTIVE',18,21,91),(19,'2026-05-09','2026-05-16','2026-05-16 08:10:09.353415','CSR-00019','GENERATED','2026-05-16 08:10:08.885417','ops.officer01','ACTIVE',19,22,93),(20,'2026-04-01','2026-06-05','2026-06-05 04:54:05.675954','CSR-00020','GENERATED','2026-06-05 04:54:03.005266','admin01','ACTIVE',1,1,160),(21,'2026-04-01','2026-06-05','2026-06-05 05:01:08.744016','CSR-00021','DOWNLOADED','2026-06-05 05:01:05.126397','admin01','ACTIVE',2,2,161),(22,'2026-01-01','2026-06-06','2026-06-06 11:57:09.425498','CSR-00022','DOWNLOADED','2026-06-06 11:57:07.406352','admin01','ACTIVE',18,21,184),(23,'2026-06-13','2026-06-13','2026-06-13 03:49:46.394001','CSR-00023','DOWNLOADED','2026-06-13 03:49:44.883927','admin01','ACTIVE',21,23,194);

--
-- Table structure for table `deposit_scheme`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deposit_scheme` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `minimum_installment` decimal(18,2) NOT NULL,
  `profit_frequency` enum('HALF_YEARLY','MONTHLY','QUARTERLY','YEARLY') NOT NULL,
  `profit_ratio` decimal(10,4) NOT NULL,
  `scheme_code` varchar(40) NOT NULL,
  `scheme_name` varchar(150) NOT NULL,
  `scheme_type` enum('EDUCATION_SAVINGS','GENERAL_SAVINGS','HAJJ_SAVINGS','MARRIAGE_SAVINGS','MONTHLY_SAVINGS','PENSION_SAVINGS') NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `tenure_months` int NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_deposit_scheme_code` (`scheme_code`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deposit_scheme`
--

INSERT INTO `deposit_scheme` VALUES (1,'2026-05-02 00:01:55.957384',1000.00,'MONTHLY',4.5000,'DPS-00001','Monthly Saver Plus','MONTHLY_SAVINGS','ACTIVE',6,'2026-05-02 00:01:55.957384'),(2,'2026-05-02 00:01:56.059383',1500.00,'QUARTERLY',5.2500,'DPS-00002','Hajj Vision Plan','HAJJ_SAVINGS','ARCHIVED',12,'2026-05-02 00:01:56.059383'),(3,'2026-05-02 00:01:56.090383',1200.00,'HALF_YEARLY',5.1000,'DPS-00003','Education Future Care','EDUCATION_SAVINGS','ACTIVE',18,'2026-05-02 00:01:56.090383'),(4,'2026-05-02 00:01:56.122383',2000.00,'YEARLY',5.7500,'DPS-00004','Marriage Goal Deposit','MARRIAGE_SAVINGS','ACTIVE',24,'2026-05-02 00:01:56.122383'),(5,'2026-05-02 00:01:56.153395',1800.00,'MONTHLY',4.9500,'DPS-00005','Pension Comfort','PENSION_SAVINGS','ARCHIVED',12,'2026-05-02 00:01:56.153395'),(6,'2026-05-02 00:01:56.184386',900.00,'MONTHLY',4.2500,'DPS-00006','General Saver Mini','GENERAL_SAVINGS','ACTIVE',6,'2026-05-02 00:01:56.184386'),(7,'2026-05-02 00:01:56.214383',1100.00,'QUARTERLY',4.8500,'DPS-00007','Umrah Preparation Scheme','HAJJ_SAVINGS','ACTIVE',9,'2026-05-02 15:21:35.724807'),(8,'2026-05-02 00:01:56.241386',800.00,'MONTHLY',4.4000,'DPS-00008','Student Growth Deposit','EDUCATION_SAVINGS','ACTIVE',12,'2026-05-02 00:01:56.241386'),(9,'2026-05-02 00:01:56.271386',1300.00,'HALF_YEARLY',5.0000,'DPS-00009','Family Monthly Nest','MONTHLY_SAVINGS','ACTIVE',18,'2026-05-02 00:01:56.271386'),(10,'2026-05-02 00:01:56.301384',700.00,'MONTHLY',4.0000,'DPS-00010','Micro Saver Plan','GENERAL_SAVINGS','ARCHIVED',6,'2026-05-02 00:15:33.724339'),(11,'2026-05-02 00:01:56.330386',1400.00,'QUARTERLY',5.3500,'DPS-00011','Women Prosperity Deposit','GENERAL_SAVINGS','ACTIVE',12,'2026-05-02 00:01:56.330386'),(12,'2026-05-02 00:01:56.358384',1600.00,'HALF_YEARLY',5.6000,'DPS-00012','Senior Peace Scheme','PENSION_SAVINGS','ACTIVE',24,'2026-05-02 00:01:56.358384'),(13,'2026-05-02 00:01:56.387384',1250.00,'QUARTERLY',4.7000,'DPS-00013','NRB Family Support','GENERAL_SAVINGS','ACTIVE',9,'2026-05-02 00:01:56.387384'),(14,'2026-05-02 00:01:56.415384',2200.00,'MONTHLY',5.9000,'DPS-00014','Marriage Premium Shield','MARRIAGE_SAVINGS','ARCHIVED',12,'2026-05-02 00:15:09.166585'),(15,'2026-05-02 00:01:56.441383',950.00,'MONTHLY',4.6500,'DPS-00015','Digital Saver Flex','MONTHLY_SAVINGS','ACTIVE',6,'2026-05-02 15:21:28.173556');

--
-- Table structure for table `deposit_scheme_enrollment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deposit_scheme_enrollment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `early_withdrawal_requested` bit(1) NOT NULL,
  `early_withdrawal_requested_at` datetime(6) DEFAULT NULL,
  `enrollment_no` varchar(40) NOT NULL,
  `enrollment_status` enum('ACTIVE','CLOSED','EARLY_WITHDRAWAL_REQUESTED','EARLY_WITHDRAWN','MATURED') NOT NULL,
  `installment_amount` decimal(18,2) NOT NULL,
  `maturity_amount` decimal(18,2) DEFAULT NULL,
  `maturity_date` date NOT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `start_date` date NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `customer_id` bigint NOT NULL,
  `linked_account_id` bigint NOT NULL,
  `scheme_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_deposit_scheme_enrollment_no` (`enrollment_no`),
  KEY `FKt3vv8lv3s5trmy4ytgoe3eaoy` (`customer_id`),
  KEY `FK8iynwdt4o8h6q8pi18un7234l` (`linked_account_id`),
  KEY `FK2famwud7hmqpvuonap8w16lr4` (`scheme_id`),
  CONSTRAINT `FK2famwud7hmqpvuonap8w16lr4` FOREIGN KEY (`scheme_id`) REFERENCES `deposit_scheme` (`id`),
  CONSTRAINT `FK8iynwdt4o8h6q8pi18un7234l` FOREIGN KEY (`linked_account_id`) REFERENCES `account` (`id`),
  CONSTRAINT `FKt3vv8lv3s5trmy4ytgoe3eaoy` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deposit_scheme_enrollment`
--

INSERT INTO `deposit_scheme_enrollment` VALUES (1,'2026-05-02 00:01:56.529384',_binary '\0',NULL,'DSE-00001','MATURED',1500.00,9033.78,'2025-07-01','setup matured enrollment','2025-01-01','ACTIVE','2026-05-02 00:01:56.592384',1,1,1),(2,'2026-05-02 00:01:56.659388',_binary '\0',NULL,'DSE-00002','MATURED',1800.00,21694.56,'2026-02-01','setup matured enrollment','2025-02-01','ACTIVE','2026-05-02 00:01:56.709385',3,3,2),(3,'2026-05-02 00:01:56.748387',_binary '\0',NULL,'DSE-00003','ACTIVE',1600.00,28922.40,'2026-09-01','setup matured enrollment','2025-03-01','ACTIVE','2026-05-02 00:01:56.808386',7,7,3),(4,'2026-05-02 00:01:56.854387',_binary '\0',NULL,'DSE-00004','ACTIVE',2400.00,57876.00,'2027-04-01','setup matured enrollment','2025-04-01','ACTIVE','2026-05-02 00:01:56.938384',9,9,4),(5,'2026-05-02 00:01:57.020716',_binary '\0',NULL,'DSE-00005','MATURED',2000.00,24099.00,'2026-05-01','setup matured enrollment','2025-05-01','ACTIVE','2026-05-02 00:01:57.083228',11,11,5),(6,'2026-05-02 00:01:57.124226',_binary '','2026-05-02 00:01:58.000000','DSE-00006','EARLY_WITHDRAWAL_REQUESTED',1000.00,6021.24,'2026-03-01','setup early withdrawal request','2025-09-01','ACTIVE','2026-05-02 00:01:57.164227',13,13,6),(7,'2026-05-02 00:01:57.216228',_binary '\0',NULL,'DSE-00007','ACTIVE',1250.00,11295.45,'2026-07-01','Active due enrollment','2025-10-01','ACTIVE','2026-05-02 00:01:57.245227',15,15,7),(8,'2026-05-02 00:01:57.297234',_binary '\0',NULL,'DSE-00008','ACTIVE',900.00,10839.60,'2026-11-01','Active due enrollment','2025-11-01','ACTIVE','2026-05-02 00:01:57.356230',1,1,8),(9,'2026-05-02 00:01:57.399232',_binary '\0',NULL,'DSE-00009','ACTIVE',1500.00,27112.50,'2027-06-01','Active due enrollment','2025-12-01','ACTIVE','2026-05-02 00:01:57.490227',3,3,9),(10,'2026-05-02 00:01:57.523226',_binary '','2026-05-02 00:01:58.000000','DSE-00010','EARLY_WITHDRAWAL_REQUESTED',850.00,5116.98,'2026-07-01','setup early withdrawal request','2026-01-01','ACTIVE','2026-05-02 00:01:57.552226',7,7,10),(11,'2026-05-02 00:01:57.583227',_binary '\0',NULL,'DSE-00011','ACTIVE',1700.00,20490.96,'2027-02-01','Fresh enrollment','2026-02-01','ACTIVE','2026-05-02 00:01:57.620226',9,9,11),(12,'2026-05-02 00:01:57.652230',_binary '\0',NULL,'DSE-00012','ACTIVE',1900.00,45812.88,'2028-03-01','Fresh enrollment','2026-03-01','ACTIVE','2026-05-02 00:01:57.711229',11,11,12),(13,'2026-05-02 00:01:57.752226',_binary '\0',NULL,'DSE-00013','ACTIVE',1300.00,11745.81,'2026-10-15','Quarterly enrollment','2026-01-15','ACTIVE','2026-05-02 00:01:57.778228',13,13,13),(14,'2026-05-02 00:01:57.810226',_binary '','2026-05-02 00:01:58.000000','DSE-00014','EARLY_WITHDRAWAL_REQUESTED',2500.00,30147.48,'2027-02-15','setup early withdrawal request','2026-02-15','ACTIVE','2026-05-02 00:01:57.859229',15,15,14),(15,'2026-05-02 00:01:57.892228',_binary '\0',NULL,'DSE-00015','ACTIVE',1100.00,6625.56,'2026-09-15','Digital enrollment','2026-03-15','ACTIVE','2026-05-02 00:01:57.937230',1,1,15);

--
-- Table structure for table `deposit_scheme_profit_distribution`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deposit_scheme_profit_distribution` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `credited_account_id` bigint DEFAULT NULL,
  `distribution_date` date NOT NULL,
  `distribution_no` int NOT NULL,
  `distribution_status` enum('DISTRIBUTED','PENDING','SKIPPED') NOT NULL,
  `period_from` date NOT NULL,
  `period_to` date NOT NULL,
  `profit_amount` decimal(18,2) NOT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `enrollment_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKm4q04nsnji3jd8gqkay38sn8k` (`enrollment_id`),
  CONSTRAINT `FKm4q04nsnji3jd8gqkay38sn8k` FOREIGN KEY (`enrollment_id`) REFERENCES `deposit_scheme_enrollment` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deposit_scheme_profit_distribution`
--

INSERT INTO `deposit_scheme_profit_distribution` VALUES (1,'2026-05-02 00:01:56.556384',1,'2025-01-01',1,'DISTRIBUTED','2025-01-01','2025-01-01',5.63,'Auto-generated from scheme enrollment','ACTIVE',1),(2,'2026-05-02 00:01:56.560382',1,'2025-02-01',2,'DISTRIBUTED','2025-02-01','2025-02-01',5.63,'Auto-generated from scheme enrollment','ACTIVE',1),(3,'2026-05-02 00:01:56.566388',1,'2025-03-01',3,'DISTRIBUTED','2025-03-01','2025-03-01',5.63,'Auto-generated from scheme enrollment','ACTIVE',1),(4,'2026-05-02 00:01:56.569384',1,'2025-04-01',4,'DISTRIBUTED','2025-04-01','2025-04-01',5.63,'Auto-generated from scheme enrollment','ACTIVE',1),(5,'2026-05-02 00:01:56.571384',1,'2025-05-01',5,'DISTRIBUTED','2025-05-01','2025-05-01',5.63,'Auto-generated from scheme enrollment','ACTIVE',1),(6,'2026-05-02 00:01:56.574385',1,'2025-06-01',6,'DISTRIBUTED','2025-06-01','2025-06-01',5.63,'Auto-generated from scheme enrollment','ACTIVE',1),(7,'2026-05-02 00:01:56.693387',3,'2025-04-01',1,'DISTRIBUTED','2025-02-01','2025-04-01',23.64,'Auto-generated from scheme enrollment','ACTIVE',2),(8,'2026-05-02 00:01:56.696384',3,'2025-07-01',2,'DISTRIBUTED','2025-05-01','2025-07-01',23.64,'Auto-generated from scheme enrollment','ACTIVE',2),(9,'2026-05-02 00:01:56.699387',3,'2025-10-01',3,'DISTRIBUTED','2025-08-01','2025-10-01',23.64,'Auto-generated from scheme enrollment','ACTIVE',2),(10,'2026-05-02 00:01:56.702386',3,'2026-01-01',4,'DISTRIBUTED','2025-11-01','2026-01-01',23.64,'Auto-generated from scheme enrollment','ACTIVE',2),(11,'2026-05-02 00:01:56.794384',7,'2025-08-01',1,'DISTRIBUTED','2025-03-01','2025-08-01',40.80,'Auto-generated from scheme enrollment','ACTIVE',3),(12,'2026-05-02 00:01:56.798389',7,'2026-02-01',2,'DISTRIBUTED','2025-09-01','2026-02-01',40.80,'Auto-generated from scheme enrollment','ACTIVE',3),(13,'2026-05-02 00:01:56.801388',7,'2026-08-01',3,'PENDING','2026-03-01','2026-08-01',40.80,'Auto-generated from scheme enrollment','ACTIVE',3),(14,'2026-05-02 00:01:56.916386',9,'2026-03-01',1,'DISTRIBUTED','2025-04-01','2026-03-01',138.00,'Auto-generated from scheme enrollment','ACTIVE',4),(15,'2026-05-02 00:01:56.918384',9,'2027-03-01',2,'PENDING','2026-04-01','2027-03-01',138.00,'Auto-generated from scheme enrollment','ACTIVE',4),(16,'2026-05-02 00:01:57.053229',11,'2025-05-01',1,'DISTRIBUTED','2025-05-01','2025-05-01',8.25,'Auto-generated from scheme enrollment','ACTIVE',5),(17,'2026-05-02 00:01:57.055234',11,'2025-06-01',2,'DISTRIBUTED','2025-06-01','2025-06-01',8.25,'Auto-generated from scheme enrollment','ACTIVE',5),(18,'2026-05-02 00:01:57.056229',11,'2025-07-01',3,'DISTRIBUTED','2025-07-01','2025-07-01',8.25,'Auto-generated from scheme enrollment','ACTIVE',5),(19,'2026-05-02 00:01:57.058229',11,'2025-08-01',4,'DISTRIBUTED','2025-08-01','2025-08-01',8.25,'Auto-generated from scheme enrollment','ACTIVE',5),(20,'2026-05-02 00:01:57.060235',11,'2025-09-01',5,'DISTRIBUTED','2025-09-01','2025-09-01',8.25,'Auto-generated from scheme enrollment','ACTIVE',5),(21,'2026-05-02 00:01:57.063232',11,'2025-10-01',6,'DISTRIBUTED','2025-10-01','2025-10-01',8.25,'Auto-generated from scheme enrollment','ACTIVE',5),(22,'2026-05-02 00:01:57.065228',11,'2025-11-01',7,'DISTRIBUTED','2025-11-01','2025-11-01',8.25,'Auto-generated from scheme enrollment','ACTIVE',5),(23,'2026-05-02 00:01:57.067236',11,'2025-12-01',8,'DISTRIBUTED','2025-12-01','2025-12-01',8.25,'Auto-generated from scheme enrollment','ACTIVE',5),(24,'2026-05-02 00:01:57.069227',11,'2026-01-01',9,'DISTRIBUTED','2026-01-01','2026-01-01',8.25,'Auto-generated from scheme enrollment','ACTIVE',5),(25,'2026-05-02 00:01:57.071236',11,'2026-02-01',10,'DISTRIBUTED','2026-02-01','2026-02-01',8.25,'Auto-generated from scheme enrollment','ACTIVE',5),(26,'2026-05-02 00:01:57.073229',11,'2026-03-01',11,'DISTRIBUTED','2026-03-01','2026-03-01',8.25,'Auto-generated from scheme enrollment','ACTIVE',5),(27,'2026-05-02 00:01:57.075234',11,'2026-04-01',12,'DISTRIBUTED','2026-04-01','2026-04-01',8.25,'Auto-generated from scheme enrollment','ACTIVE',5),(28,'2026-05-02 00:01:57.141227',13,'2025-09-01',1,'SKIPPED','2025-09-01','2025-09-01',3.54,'Auto-generated from scheme enrollment','ACTIVE',6),(29,'2026-05-02 00:01:57.142227',13,'2025-10-01',2,'DISTRIBUTED','2025-10-01','2025-10-01',3.54,'Auto-generated from scheme enrollment','ACTIVE',6),(30,'2026-05-02 00:01:57.146230',13,'2025-11-01',3,'DISTRIBUTED','2025-11-01','2025-11-01',3.54,'Auto-generated from scheme enrollment','ACTIVE',6),(31,'2026-05-02 00:01:57.149229',13,'2025-12-01',4,'DISTRIBUTED','2025-12-01','2025-12-01',3.54,'Auto-generated from scheme enrollment','ACTIVE',6),(32,'2026-05-02 00:01:57.151232',13,'2026-01-01',5,'DISTRIBUTED','2026-01-01','2026-01-01',3.54,'Auto-generated from scheme enrollment','ACTIVE',6),(33,'2026-05-02 00:01:57.152232',13,'2026-02-01',6,'DISTRIBUTED','2026-02-01','2026-02-01',3.54,'Auto-generated from scheme enrollment','ACTIVE',6),(34,'2026-05-02 00:01:57.235229',15,'2025-12-01',1,'DISTRIBUTED','2025-10-01','2025-12-01',15.15,'Auto-generated from scheme enrollment','ACTIVE',7),(35,'2026-05-02 00:01:57.237228',15,'2026-03-01',2,'DISTRIBUTED','2026-01-01','2026-03-01',15.15,'Auto-generated from scheme enrollment','ACTIVE',7),(36,'2026-05-02 00:01:57.239231',15,'2026-06-01',3,'PENDING','2026-04-01','2026-06-01',15.15,'Auto-generated from scheme enrollment','ACTIVE',7),(37,'2026-05-02 00:01:57.325228',1,'2025-11-01',1,'DISTRIBUTED','2025-11-01','2025-11-01',3.30,'Auto-generated from scheme enrollment','ACTIVE',8),(38,'2026-05-02 00:01:57.327228',1,'2025-12-01',2,'DISTRIBUTED','2025-12-01','2025-12-01',3.30,'Auto-generated from scheme enrollment','ACTIVE',8),(39,'2026-05-02 00:01:57.331229',1,'2026-01-01',3,'DISTRIBUTED','2026-01-01','2026-01-01',3.30,'Auto-generated from scheme enrollment','ACTIVE',8),(40,'2026-05-02 00:01:57.333231',1,'2026-02-01',4,'DISTRIBUTED','2026-02-01','2026-02-01',3.30,'Auto-generated from scheme enrollment','ACTIVE',8),(41,'2026-05-02 00:01:57.334228',1,'2026-03-01',5,'DISTRIBUTED','2026-03-01','2026-03-01',3.30,'Auto-generated from scheme enrollment','ACTIVE',8),(42,'2026-05-02 00:01:57.336225',1,'2026-04-01',6,'DISTRIBUTED','2026-04-01','2026-04-01',3.30,'Auto-generated from scheme enrollment','ACTIVE',8),(43,'2026-05-02 00:01:57.338227',1,'2026-05-01',7,'DISTRIBUTED','2026-05-01','2026-05-01',3.30,'Auto-generated from scheme enrollment','ACTIVE',8),(44,'2026-05-02 00:01:57.341228',1,'2026-06-01',8,'PENDING','2026-06-01','2026-06-01',3.30,'Auto-generated from scheme enrollment','ACTIVE',8),(45,'2026-05-02 00:01:57.342226',1,'2026-07-01',9,'PENDING','2026-07-01','2026-07-01',3.30,'Auto-generated from scheme enrollment','ACTIVE',8),(46,'2026-05-02 00:01:57.344227',1,'2026-08-01',10,'PENDING','2026-08-01','2026-08-01',3.30,'Auto-generated from scheme enrollment','ACTIVE',8),(47,'2026-05-02 00:01:57.348230',1,'2026-09-01',11,'PENDING','2026-09-01','2026-09-01',3.30,'Auto-generated from scheme enrollment','ACTIVE',8),(48,'2026-05-02 00:01:57.350225',1,'2026-10-01',12,'PENDING','2026-10-01','2026-10-01',3.30,'Auto-generated from scheme enrollment','ACTIVE',8),(49,'2026-05-02 00:01:57.467229',3,'2026-05-01',1,'DISTRIBUTED','2025-12-01','2026-05-01',37.50,'Auto-generated from scheme enrollment','ACTIVE',9),(50,'2026-05-02 00:01:57.469226',3,'2026-11-01',2,'PENDING','2026-06-01','2026-11-01',37.50,'Auto-generated from scheme enrollment','ACTIVE',9),(51,'2026-05-02 00:01:57.472226',3,'2027-05-01',3,'PENDING','2026-12-01','2027-05-01',37.50,'Auto-generated from scheme enrollment','ACTIVE',9),(52,'2026-05-02 00:01:57.537229',7,'2026-01-01',1,'SKIPPED','2026-01-01','2026-01-01',2.83,'Auto-generated from scheme enrollment','ACTIVE',10),(53,'2026-05-02 00:01:57.539228',7,'2026-02-01',2,'DISTRIBUTED','2026-02-01','2026-02-01',2.83,'Auto-generated from scheme enrollment','ACTIVE',10),(54,'2026-05-02 00:01:57.540228',7,'2026-03-01',3,'DISTRIBUTED','2026-03-01','2026-03-01',2.83,'Auto-generated from scheme enrollment','ACTIVE',10),(55,'2026-05-02 00:01:57.542231',7,'2026-04-01',4,'DISTRIBUTED','2026-04-01','2026-04-01',2.83,'Auto-generated from scheme enrollment','ACTIVE',10),(56,'2026-05-02 00:01:57.544228',7,'2026-05-01',5,'DISTRIBUTED','2026-05-01','2026-05-01',2.83,'Auto-generated from scheme enrollment','ACTIVE',10),(57,'2026-05-02 00:01:57.546226',7,'2026-06-01',6,'PENDING','2026-06-01','2026-06-01',2.83,'Auto-generated from scheme enrollment','ACTIVE',10),(58,'2026-05-02 00:01:57.608228',9,'2026-04-01',1,'DISTRIBUTED','2026-02-01','2026-04-01',22.74,'Auto-generated from scheme enrollment','ACTIVE',11),(59,'2026-05-02 00:01:57.610225',9,'2026-07-01',2,'PENDING','2026-05-01','2026-07-01',22.74,'Auto-generated from scheme enrollment','ACTIVE',11),(60,'2026-05-02 00:01:57.612229',9,'2026-10-01',3,'PENDING','2026-08-01','2026-10-01',22.74,'Auto-generated from scheme enrollment','ACTIVE',11),(61,'2026-05-02 00:01:57.615229',9,'2027-01-01',4,'PENDING','2026-11-01','2027-01-01',22.74,'Auto-generated from scheme enrollment','ACTIVE',11),(62,'2026-05-02 00:01:57.697231',11,'2026-08-01',1,'PENDING','2026-03-01','2026-08-01',53.22,'Auto-generated from scheme enrollment','ACTIVE',12),(63,'2026-05-02 00:01:57.699233',11,'2027-02-01',2,'PENDING','2026-09-01','2027-02-01',53.22,'Auto-generated from scheme enrollment','ACTIVE',12),(64,'2026-05-02 00:01:57.701228',11,'2027-08-01',3,'PENDING','2027-03-01','2027-08-01',53.22,'Auto-generated from scheme enrollment','ACTIVE',12),(65,'2026-05-02 00:01:57.702225',11,'2028-02-01',4,'PENDING','2027-09-01','2028-02-01',53.22,'Auto-generated from scheme enrollment','ACTIVE',12),(66,'2026-05-02 00:01:57.770228',13,'2026-03-15',1,'DISTRIBUTED','2026-01-15','2026-03-15',15.27,'Auto-generated from scheme enrollment','ACTIVE',13),(67,'2026-05-02 00:01:57.771225',13,'2026-06-15',2,'PENDING','2026-04-15','2026-06-15',15.27,'Auto-generated from scheme enrollment','ACTIVE',13),(68,'2026-05-02 00:01:57.773227',13,'2026-09-15',3,'PENDING','2026-07-15','2026-09-15',15.27,'Auto-generated from scheme enrollment','ACTIVE',13),(69,'2026-05-02 00:01:57.834230',15,'2026-02-15',1,'SKIPPED','2026-02-15','2026-02-15',12.29,'Auto-generated from scheme enrollment','ACTIVE',14),(70,'2026-05-02 00:01:57.836229',15,'2026-03-15',2,'DISTRIBUTED','2026-03-15','2026-03-15',12.29,'Auto-generated from scheme enrollment','ACTIVE',14),(71,'2026-05-02 00:01:57.838228',15,'2026-04-15',3,'DISTRIBUTED','2026-04-15','2026-04-15',12.29,'Auto-generated from scheme enrollment','ACTIVE',14),(72,'2026-05-02 00:01:57.839225',15,'2026-05-15',4,'PENDING','2026-05-15','2026-05-15',12.29,'Auto-generated from scheme enrollment','ACTIVE',14),(73,'2026-05-02 00:01:57.841229',15,'2026-06-15',5,'PENDING','2026-06-15','2026-06-15',12.29,'Auto-generated from scheme enrollment','ACTIVE',14),(74,'2026-05-02 00:01:57.843228',15,'2026-07-15',6,'PENDING','2026-07-15','2026-07-15',12.29,'Auto-generated from scheme enrollment','ACTIVE',14),(75,'2026-05-02 00:01:57.845229',15,'2026-08-15',7,'PENDING','2026-08-15','2026-08-15',12.29,'Auto-generated from scheme enrollment','ACTIVE',14),(76,'2026-05-02 00:01:57.847225',15,'2026-09-15',8,'PENDING','2026-09-15','2026-09-15',12.29,'Auto-generated from scheme enrollment','ACTIVE',14),(77,'2026-05-02 00:01:57.849228',15,'2026-10-15',9,'PENDING','2026-10-15','2026-10-15',12.29,'Auto-generated from scheme enrollment','ACTIVE',14),(78,'2026-05-02 00:01:57.851231',15,'2026-11-15',10,'PENDING','2026-11-15','2026-11-15',12.29,'Auto-generated from scheme enrollment','ACTIVE',14),(79,'2026-05-02 00:01:57.852230',15,'2026-12-15',11,'PENDING','2026-12-15','2026-12-15',12.29,'Auto-generated from scheme enrollment','ACTIVE',14),(80,'2026-05-02 00:01:57.854230',15,'2027-01-15',12,'PENDING','2027-01-15','2027-01-15',12.29,'Auto-generated from scheme enrollment','ACTIVE',14),(81,'2026-05-02 00:01:57.909229',1,'2026-03-15',1,'DISTRIBUTED','2026-03-15','2026-03-15',4.26,'Auto-generated from scheme enrollment','ACTIVE',15),(82,'2026-05-02 00:01:57.912230',1,'2026-04-15',2,'DISTRIBUTED','2026-04-15','2026-04-15',4.26,'Auto-generated from scheme enrollment','ACTIVE',15),(83,'2026-05-02 00:01:57.919230',1,'2026-05-15',3,'PENDING','2026-05-15','2026-05-15',4.26,'Auto-generated from scheme enrollment','ACTIVE',15),(84,'2026-05-02 00:01:57.922231',1,'2026-06-15',4,'PENDING','2026-06-15','2026-06-15',4.26,'Auto-generated from scheme enrollment','ACTIVE',15),(85,'2026-05-02 00:01:57.925230',1,'2026-07-15',5,'PENDING','2026-07-15','2026-07-15',4.26,'Auto-generated from scheme enrollment','ACTIVE',15),(86,'2026-05-02 00:01:57.927231',1,'2026-08-15',6,'PENDING','2026-08-15','2026-08-15',4.26,'Auto-generated from scheme enrollment','ACTIVE',15);

--
-- Table structure for table `deposit_scheme_schedule`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deposit_scheme_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `due_date` date NOT NULL,
  `installment_amount` decimal(18,2) NOT NULL,
  `installment_no` int NOT NULL,
  `paid_at` datetime(6) DEFAULT NULL,
  `payment_status` enum('OVERDUE','PAID','PENDING') NOT NULL,
  `profit_amount` decimal(18,2) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `total_due_amount` decimal(18,2) NOT NULL,
  `enrollment_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrjxqlaxh2psb5ok18vupbtl0r` (`enrollment_id`),
  CONSTRAINT `FKrjxqlaxh2psb5ok18vupbtl0r` FOREIGN KEY (`enrollment_id`) REFERENCES `deposit_scheme_enrollment` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=187 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deposit_scheme_schedule`
--

INSERT INTO `deposit_scheme_schedule` VALUES (1,'2026-05-02 00:01:56.538385','2025-01-01',1500.00,1,NULL,'OVERDUE',5.63,'ACTIVE',1505.63,1),(2,'2026-05-02 00:01:56.542384','2025-02-01',1500.00,2,NULL,'OVERDUE',5.63,'ACTIVE',1505.63,1),(3,'2026-05-02 00:01:56.545385','2025-03-01',1500.00,3,'2025-03-01 00:00:00.000000','PAID',5.63,'ACTIVE',1505.63,1),(4,'2026-05-02 00:01:56.547384','2025-04-01',1500.00,4,NULL,'OVERDUE',5.63,'ACTIVE',1505.63,1),(5,'2026-05-02 00:01:56.550385','2025-05-01',1500.00,5,NULL,'OVERDUE',5.63,'ACTIVE',1505.63,1),(6,'2026-05-02 00:01:56.552385','2025-06-01',1500.00,6,'2025-06-01 00:00:00.000000','PAID',5.63,'ACTIVE',1505.63,1),(7,'2026-05-02 00:01:56.663387','2025-02-01',1800.00,1,NULL,'OVERDUE',7.88,'ACTIVE',1807.88,2),(8,'2026-05-02 00:01:56.666386','2025-03-01',1800.00,2,NULL,'OVERDUE',7.88,'ACTIVE',1807.88,2),(9,'2026-05-02 00:01:56.669385','2025-04-01',1800.00,3,'2025-04-01 00:00:00.000000','PAID',7.88,'ACTIVE',1807.88,2),(10,'2026-05-02 00:01:56.671386','2025-05-01',1800.00,4,NULL,'OVERDUE',7.88,'ACTIVE',1807.88,2),(11,'2026-05-02 00:01:56.673386','2025-06-01',1800.00,5,NULL,'OVERDUE',7.88,'ACTIVE',1807.88,2),(12,'2026-05-02 00:01:56.675384','2025-07-01',1800.00,6,'2025-07-01 00:00:00.000000','PAID',7.88,'ACTIVE',1807.88,2),(13,'2026-05-02 00:01:56.678384','2025-08-01',1800.00,7,NULL,'OVERDUE',7.88,'ACTIVE',1807.88,2),(14,'2026-05-02 00:01:56.682388','2025-09-01',1800.00,8,NULL,'OVERDUE',7.88,'ACTIVE',1807.88,2),(15,'2026-05-02 00:01:56.685386','2025-10-01',1800.00,9,'2025-10-01 00:00:00.000000','PAID',7.88,'ACTIVE',1807.88,2),(16,'2026-05-02 00:01:56.687384','2025-11-01',1800.00,10,NULL,'OVERDUE',7.88,'ACTIVE',1807.88,2),(17,'2026-05-02 00:01:56.689388','2025-12-01',1800.00,11,NULL,'OVERDUE',7.88,'ACTIVE',1807.88,2),(18,'2026-05-02 00:01:56.691382','2026-01-01',1800.00,12,'2026-01-01 00:00:00.000000','PAID',7.88,'ACTIVE',1807.88,2),(19,'2026-05-02 00:01:56.751383','2025-03-01',1600.00,1,NULL,'OVERDUE',6.80,'ACTIVE',1606.80,3),(20,'2026-05-02 00:01:56.753384','2025-04-01',1600.00,2,NULL,'OVERDUE',6.80,'ACTIVE',1606.80,3),(21,'2026-05-02 00:01:56.755384','2025-05-01',1600.00,3,'2025-05-01 00:00:00.000000','PAID',6.80,'ACTIVE',1606.80,3),(22,'2026-05-02 00:01:56.757384','2025-06-01',1600.00,4,NULL,'OVERDUE',6.80,'ACTIVE',1606.80,3),(23,'2026-05-02 00:01:56.759383','2025-07-01',1600.00,5,NULL,'OVERDUE',6.80,'ACTIVE',1606.80,3),(24,'2026-05-02 00:01:56.762387','2025-08-01',1600.00,6,'2025-08-01 00:00:00.000000','PAID',6.80,'ACTIVE',1606.80,3),(25,'2026-05-02 00:01:56.765384','2025-09-01',1600.00,7,NULL,'OVERDUE',6.80,'ACTIVE',1606.80,3),(26,'2026-05-02 00:01:56.767385','2025-10-01',1600.00,8,NULL,'OVERDUE',6.80,'ACTIVE',1606.80,3),(27,'2026-05-02 00:01:56.768383','2025-11-01',1600.00,9,'2025-11-01 00:00:00.000000','PAID',6.80,'ACTIVE',1606.80,3),(28,'2026-05-02 00:01:56.770383','2025-12-01',1600.00,10,NULL,'OVERDUE',6.80,'ACTIVE',1606.80,3),(29,'2026-05-02 00:01:56.772384','2026-01-01',1600.00,11,NULL,'OVERDUE',6.80,'ACTIVE',1606.80,3),(30,'2026-05-02 00:01:56.773388','2026-02-01',1600.00,12,'2026-02-01 00:00:00.000000','PAID',6.80,'ACTIVE',1606.80,3),(31,'2026-05-02 00:01:56.776384','2026-03-01',1600.00,13,NULL,'OVERDUE',6.80,'ACTIVE',1606.80,3),(32,'2026-05-02 00:01:56.779387','2026-04-01',1600.00,14,NULL,'OVERDUE',6.80,'ACTIVE',1606.80,3),(33,'2026-05-02 00:01:56.783399','2026-05-01',1600.00,15,'2026-05-01 00:00:00.000000','PAID',6.80,'ACTIVE',1606.80,3),(34,'2026-05-02 00:01:56.785384','2026-06-01',1600.00,16,NULL,'PENDING',6.80,'ACTIVE',1606.80,3),(35,'2026-05-02 00:01:56.787381','2026-07-01',1600.00,17,NULL,'PENDING',6.80,'ACTIVE',1606.80,3),(36,'2026-05-02 00:01:56.790385','2026-08-01',1600.00,18,NULL,'PENDING',6.80,'ACTIVE',1606.80,3),(37,'2026-05-02 00:01:56.856383','2025-04-01',2400.00,1,NULL,'OVERDUE',11.50,'ACTIVE',2411.50,4),(38,'2026-05-02 00:01:56.858386','2025-05-01',2400.00,2,NULL,'OVERDUE',11.50,'ACTIVE',2411.50,4),(39,'2026-05-02 00:01:56.860386','2025-06-01',2400.00,3,'2025-06-01 00:00:00.000000','PAID',11.50,'ACTIVE',2411.50,4),(40,'2026-05-02 00:01:56.865385','2025-07-01',2400.00,4,NULL,'OVERDUE',11.50,'ACTIVE',2411.50,4),(41,'2026-05-02 00:01:56.868384','2025-08-01',2400.00,5,NULL,'OVERDUE',11.50,'ACTIVE',2411.50,4),(42,'2026-05-02 00:01:56.870385','2025-09-01',2400.00,6,'2025-09-01 00:00:00.000000','PAID',11.50,'ACTIVE',2411.50,4),(43,'2026-05-02 00:01:56.872384','2025-10-01',2400.00,7,NULL,'OVERDUE',11.50,'ACTIVE',2411.50,4),(44,'2026-05-02 00:01:56.874383','2025-11-01',2400.00,8,NULL,'OVERDUE',11.50,'ACTIVE',2411.50,4),(45,'2026-05-02 00:01:56.876383','2025-12-01',2400.00,9,'2025-12-01 00:00:00.000000','PAID',11.50,'ACTIVE',2411.50,4),(46,'2026-05-02 00:01:56.878385','2026-01-01',2400.00,10,NULL,'OVERDUE',11.50,'ACTIVE',2411.50,4),(47,'2026-05-02 00:01:56.880383','2026-02-01',2400.00,11,NULL,'OVERDUE',11.50,'ACTIVE',2411.50,4),(48,'2026-05-02 00:01:56.882385','2026-03-01',2400.00,12,'2026-03-01 00:00:00.000000','PAID',11.50,'ACTIVE',2411.50,4),(49,'2026-05-02 00:01:56.884384','2026-04-01',2400.00,13,NULL,'OVERDUE',11.50,'ACTIVE',2411.50,4),(50,'2026-05-02 00:01:56.886385','2026-05-01',2400.00,14,NULL,'OVERDUE',11.50,'ACTIVE',2411.50,4),(51,'2026-05-02 00:01:56.888383','2026-06-01',2400.00,15,NULL,'PENDING',11.50,'ACTIVE',2411.50,4),(52,'2026-05-02 00:01:56.890383','2026-07-01',2400.00,16,NULL,'PENDING',11.50,'ACTIVE',2411.50,4),(53,'2026-05-02 00:01:56.892388','2026-08-01',2400.00,17,NULL,'PENDING',11.50,'ACTIVE',2411.50,4),(54,'2026-05-02 00:01:56.894384','2026-09-01',2400.00,18,NULL,'PENDING',11.50,'ACTIVE',2411.50,4),(55,'2026-05-02 00:01:56.896390','2026-10-01',2400.00,19,NULL,'PENDING',11.50,'ACTIVE',2411.50,4),(56,'2026-05-02 00:01:56.899386','2026-11-01',2400.00,20,NULL,'PENDING',11.50,'ACTIVE',2411.50,4),(57,'2026-05-02 00:01:56.906383','2026-12-01',2400.00,21,NULL,'PENDING',11.50,'ACTIVE',2411.50,4),(58,'2026-05-02 00:01:56.908383','2027-01-01',2400.00,22,NULL,'PENDING',11.50,'ACTIVE',2411.50,4),(59,'2026-05-02 00:01:56.910383','2027-02-01',2400.00,23,NULL,'PENDING',11.50,'ACTIVE',2411.50,4),(60,'2026-05-02 00:01:56.913387','2027-03-01',2400.00,24,NULL,'PENDING',11.50,'ACTIVE',2411.50,4),(61,'2026-05-02 00:01:57.022743','2025-05-01',2000.00,1,NULL,'OVERDUE',8.25,'ACTIVE',2008.25,5),(62,'2026-05-02 00:01:57.024742','2025-06-01',2000.00,2,NULL,'OVERDUE',8.25,'ACTIVE',2008.25,5),(63,'2026-05-02 00:01:57.026745','2025-07-01',2000.00,3,'2025-07-01 00:00:00.000000','PAID',8.25,'ACTIVE',2008.25,5),(64,'2026-05-02 00:01:57.031725','2025-08-01',2000.00,4,NULL,'OVERDUE',8.25,'ACTIVE',2008.25,5),(65,'2026-05-02 00:01:57.035719','2025-09-01',2000.00,5,NULL,'OVERDUE',8.25,'ACTIVE',2008.25,5),(66,'2026-05-02 00:01:57.038473','2025-10-01',2000.00,6,'2025-10-01 00:00:00.000000','PAID',8.25,'ACTIVE',2008.25,5),(67,'2026-05-02 00:01:57.040467','2025-11-01',2000.00,7,NULL,'OVERDUE',8.25,'ACTIVE',2008.25,5),(68,'2026-05-02 00:01:57.042473','2025-12-01',2000.00,8,NULL,'OVERDUE',8.25,'ACTIVE',2008.25,5),(69,'2026-05-02 00:01:57.044468','2026-01-01',2000.00,9,'2026-01-01 00:00:00.000000','PAID',8.25,'ACTIVE',2008.25,5),(70,'2026-05-02 00:01:57.047469','2026-02-01',2000.00,10,NULL,'OVERDUE',8.25,'ACTIVE',2008.25,5),(71,'2026-05-02 00:01:57.049470','2026-03-01',2000.00,11,NULL,'OVERDUE',8.25,'ACTIVE',2008.25,5),(72,'2026-05-02 00:01:57.051226','2026-04-01',2000.00,12,'2026-04-01 00:00:00.000000','PAID',8.25,'ACTIVE',2008.25,5),(73,'2026-05-02 00:01:57.126235','2025-09-01',1000.00,1,NULL,'OVERDUE',3.54,'ACTIVE',1003.54,6),(74,'2026-05-02 00:01:57.127232','2025-10-01',1000.00,2,NULL,'OVERDUE',3.54,'ACTIVE',1003.54,6),(75,'2026-05-02 00:01:57.131230','2025-11-01',1000.00,3,'2025-11-01 00:00:00.000000','PAID',3.54,'ACTIVE',1003.54,6),(76,'2026-05-02 00:01:57.135234','2025-12-01',1000.00,4,NULL,'OVERDUE',3.54,'ACTIVE',1003.54,6),(77,'2026-05-02 00:01:57.137228','2026-01-01',1000.00,5,NULL,'OVERDUE',3.54,'ACTIVE',1003.54,6),(78,'2026-05-02 00:01:57.139226','2026-02-01',1000.00,6,'2026-02-01 00:00:00.000000','PAID',3.54,'ACTIVE',1003.54,6),(79,'2026-05-02 00:01:57.219226','2025-10-01',1250.00,1,NULL,'OVERDUE',5.05,'ACTIVE',1255.05,7),(80,'2026-05-02 00:01:57.221231','2025-11-01',1250.00,2,NULL,'OVERDUE',5.05,'ACTIVE',1255.05,7),(81,'2026-05-02 00:01:57.223232','2025-12-01',1250.00,3,'2025-12-01 00:00:00.000000','PAID',5.05,'ACTIVE',1255.05,7),(82,'2026-05-02 00:01:57.224227','2026-01-01',1250.00,4,NULL,'OVERDUE',5.05,'ACTIVE',1255.05,7),(83,'2026-05-02 00:01:57.226231','2026-02-01',1250.00,5,NULL,'OVERDUE',5.05,'ACTIVE',1255.05,7),(84,'2026-05-02 00:01:57.228226','2026-03-01',1250.00,6,'2026-03-01 00:00:00.000000','PAID',5.05,'ACTIVE',1255.05,7),(85,'2026-05-02 00:01:57.230230','2026-04-01',1250.00,7,NULL,'OVERDUE',5.05,'ACTIVE',1255.05,7),(86,'2026-05-02 00:01:57.232228','2026-05-01',1250.00,8,NULL,'OVERDUE',5.05,'ACTIVE',1255.05,7),(87,'2026-05-02 00:01:57.234228','2026-06-01',1250.00,9,NULL,'PENDING',5.05,'ACTIVE',1255.05,7),(88,'2026-05-02 00:01:57.301231','2025-11-01',900.00,1,NULL,'OVERDUE',3.30,'ACTIVE',903.30,8),(89,'2026-05-02 00:01:57.303228','2025-12-01',900.00,2,NULL,'OVERDUE',3.30,'ACTIVE',903.30,8),(90,'2026-05-02 00:01:57.304227','2026-01-01',900.00,3,'2026-01-01 00:00:00.000000','PAID',3.30,'ACTIVE',903.30,8),(91,'2026-05-02 00:01:57.306226','2026-02-01',900.00,4,NULL,'OVERDUE',3.30,'ACTIVE',903.30,8),(92,'2026-05-02 00:01:57.308228','2026-03-01',900.00,5,NULL,'OVERDUE',3.30,'ACTIVE',903.30,8),(93,'2026-05-02 00:01:57.309229','2026-04-01',900.00,6,'2026-04-01 00:00:00.000000','PAID',3.30,'ACTIVE',903.30,8),(94,'2026-05-02 00:01:57.314229','2026-05-01',900.00,7,NULL,'OVERDUE',3.30,'ACTIVE',903.30,8),(95,'2026-05-02 00:01:57.316229','2026-06-01',900.00,8,NULL,'PENDING',3.30,'ACTIVE',903.30,8),(96,'2026-05-02 00:01:57.318225','2026-07-01',900.00,9,NULL,'PENDING',3.30,'ACTIVE',903.30,8),(97,'2026-05-02 00:01:57.319226','2026-08-01',900.00,10,NULL,'PENDING',3.30,'ACTIVE',903.30,8),(98,'2026-05-02 00:01:57.321227','2026-09-01',900.00,11,NULL,'PENDING',3.30,'ACTIVE',903.30,8),(99,'2026-05-02 00:01:57.323226','2026-10-01',900.00,12,NULL,'PENDING',3.30,'ACTIVE',903.30,8),(100,'2026-05-02 00:01:57.417226','2025-12-01',1500.00,1,NULL,'OVERDUE',6.25,'ACTIVE',1506.25,9),(101,'2026-05-02 00:01:57.424227','2026-01-01',1500.00,2,NULL,'OVERDUE',6.25,'ACTIVE',1506.25,9),(102,'2026-05-02 00:01:57.426230','2026-02-01',1500.00,3,'2026-02-01 00:00:00.000000','PAID',6.25,'ACTIVE',1506.25,9),(103,'2026-05-02 00:01:57.428227','2026-03-01',1500.00,4,NULL,'OVERDUE',6.25,'ACTIVE',1506.25,9),(104,'2026-05-02 00:01:57.434229','2026-04-01',1500.00,5,NULL,'OVERDUE',6.25,'ACTIVE',1506.25,9),(105,'2026-05-02 00:01:57.436229','2026-05-01',1500.00,6,'2026-05-01 00:00:00.000000','PAID',6.25,'ACTIVE',1506.25,9),(106,'2026-05-02 00:01:57.438229','2026-06-01',1500.00,7,NULL,'PENDING',6.25,'ACTIVE',1506.25,9),(107,'2026-05-02 00:01:57.439232','2026-07-01',1500.00,8,NULL,'PENDING',6.25,'ACTIVE',1506.25,9),(108,'2026-05-02 00:01:57.441228','2026-08-01',1500.00,9,NULL,'PENDING',6.25,'ACTIVE',1506.25,9),(109,'2026-05-02 00:01:57.443230','2026-09-01',1500.00,10,NULL,'PENDING',6.25,'ACTIVE',1506.25,9),(110,'2026-05-02 00:01:57.447232','2026-10-01',1500.00,11,NULL,'PENDING',6.25,'ACTIVE',1506.25,9),(111,'2026-05-02 00:01:57.450230','2026-11-01',1500.00,12,NULL,'PENDING',6.25,'ACTIVE',1506.25,9),(112,'2026-05-02 00:01:57.452230','2026-12-01',1500.00,13,NULL,'PENDING',6.25,'ACTIVE',1506.25,9),(113,'2026-05-02 00:01:57.454229','2027-01-01',1500.00,14,NULL,'PENDING',6.25,'ACTIVE',1506.25,9),(114,'2026-05-02 00:01:57.456229','2027-02-01',1500.00,15,NULL,'PENDING',6.25,'ACTIVE',1506.25,9),(115,'2026-05-02 00:01:57.458230','2027-03-01',1500.00,16,NULL,'PENDING',6.25,'ACTIVE',1506.25,9),(116,'2026-05-02 00:01:57.460228','2027-04-01',1500.00,17,NULL,'PENDING',6.25,'ACTIVE',1506.25,9),(117,'2026-05-02 00:01:57.464227','2027-05-01',1500.00,18,NULL,'PENDING',6.25,'ACTIVE',1506.25,9),(118,'2026-05-02 00:01:57.526231','2026-01-01',850.00,1,NULL,'OVERDUE',2.83,'ACTIVE',852.83,10),(119,'2026-05-02 00:01:57.527229','2026-02-01',850.00,2,NULL,'OVERDUE',2.83,'ACTIVE',852.83,10),(120,'2026-05-02 00:01:57.530233','2026-03-01',850.00,3,'2026-03-01 00:00:00.000000','PAID',2.83,'ACTIVE',852.83,10),(121,'2026-05-02 00:01:57.532228','2026-04-01',850.00,4,NULL,'OVERDUE',2.83,'ACTIVE',852.83,10),(122,'2026-05-02 00:01:57.534227','2026-05-01',850.00,5,NULL,'OVERDUE',2.83,'ACTIVE',852.83,10),(123,'2026-05-02 00:01:57.535230','2026-06-01',850.00,6,NULL,'PENDING',2.83,'ACTIVE',852.83,10),(124,'2026-05-02 00:01:57.585229','2026-02-01',1700.00,1,NULL,'OVERDUE',7.58,'ACTIVE',1707.58,11),(125,'2026-05-02 00:01:57.586225','2026-03-01',1700.00,2,NULL,'OVERDUE',7.58,'ACTIVE',1707.58,11),(126,'2026-05-02 00:01:57.588230','2026-04-01',1700.00,3,'2026-04-01 00:00:00.000000','PAID',7.58,'ACTIVE',1707.58,11),(127,'2026-05-02 00:01:57.589227','2026-05-01',1700.00,4,NULL,'OVERDUE',7.58,'ACTIVE',1707.58,11),(128,'2026-05-02 00:01:57.592227','2026-06-01',1700.00,5,NULL,'PENDING',7.58,'ACTIVE',1707.58,11),(129,'2026-05-02 00:01:57.595229','2026-07-01',1700.00,6,NULL,'PENDING',7.58,'ACTIVE',1707.58,11),(130,'2026-05-02 00:01:57.598230','2026-08-01',1700.00,7,NULL,'PENDING',7.58,'ACTIVE',1707.58,11),(131,'2026-05-02 00:01:57.600229','2026-09-01',1700.00,8,NULL,'PENDING',7.58,'ACTIVE',1707.58,11),(132,'2026-05-02 00:01:57.602227','2026-10-01',1700.00,9,NULL,'PENDING',7.58,'ACTIVE',1707.58,11),(133,'2026-05-02 00:01:57.603226','2026-11-01',1700.00,10,NULL,'PENDING',7.58,'ACTIVE',1707.58,11),(134,'2026-05-02 00:01:57.605227','2026-12-01',1700.00,11,NULL,'PENDING',7.58,'ACTIVE',1707.58,11),(135,'2026-05-02 00:01:57.606229','2027-01-01',1700.00,12,NULL,'PENDING',7.58,'ACTIVE',1707.58,11),(136,'2026-05-02 00:01:57.654230','2026-03-01',1900.00,1,NULL,'OVERDUE',8.87,'ACTIVE',1908.87,12),(137,'2026-05-02 00:01:57.656229','2026-04-01',1900.00,2,NULL,'OVERDUE',8.87,'ACTIVE',1908.87,12),(138,'2026-05-02 00:01:57.657229','2026-05-01',1900.00,3,'2026-05-01 00:00:00.000000','PAID',8.87,'ACTIVE',1908.87,12),(139,'2026-05-02 00:01:57.658229','2026-06-01',1900.00,4,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(140,'2026-05-02 00:01:57.660228','2026-07-01',1900.00,5,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(141,'2026-05-02 00:01:57.662229','2026-08-01',1900.00,6,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(142,'2026-05-02 00:01:57.664226','2026-09-01',1900.00,7,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(143,'2026-05-02 00:01:57.666230','2026-10-01',1900.00,8,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(144,'2026-05-02 00:01:57.668231','2026-11-01',1900.00,9,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(145,'2026-05-02 00:01:57.669226','2026-12-01',1900.00,10,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(146,'2026-05-02 00:01:57.671230','2027-01-01',1900.00,11,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(147,'2026-05-02 00:01:57.672227','2027-02-01',1900.00,12,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(148,'2026-05-02 00:01:57.674225','2027-03-01',1900.00,13,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(149,'2026-05-02 00:01:57.675231','2027-04-01',1900.00,14,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(150,'2026-05-02 00:01:57.677227','2027-05-01',1900.00,15,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(151,'2026-05-02 00:01:57.680230','2027-06-01',1900.00,16,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(152,'2026-05-02 00:01:57.681225','2027-07-01',1900.00,17,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(153,'2026-05-02 00:01:57.683230','2027-08-01',1900.00,18,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(154,'2026-05-02 00:01:57.685231','2027-09-01',1900.00,19,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(155,'2026-05-02 00:01:57.686229','2027-10-01',1900.00,20,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(156,'2026-05-02 00:01:57.689229','2027-11-01',1900.00,21,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(157,'2026-05-02 00:01:57.691230','2027-12-01',1900.00,22,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(158,'2026-05-02 00:01:57.692231','2028-01-01',1900.00,23,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(159,'2026-05-02 00:01:57.694228','2028-02-01',1900.00,24,NULL,'PENDING',8.87,'ACTIVE',1908.87,12),(160,'2026-05-02 00:01:57.754227','2026-01-15',1300.00,1,NULL,'OVERDUE',5.09,'ACTIVE',1305.09,13),(161,'2026-05-02 00:01:57.756227','2026-02-15',1300.00,2,NULL,'OVERDUE',5.09,'ACTIVE',1305.09,13),(162,'2026-05-02 00:01:57.757227','2026-03-15',1300.00,3,'2026-03-15 00:00:00.000000','PAID',5.09,'ACTIVE',1305.09,13),(163,'2026-05-02 00:01:57.759228','2026-04-15',1300.00,4,NULL,'OVERDUE',5.09,'ACTIVE',1305.09,13),(164,'2026-05-02 00:01:57.760225','2026-05-15',1300.00,5,NULL,'PENDING',5.09,'ACTIVE',1305.09,13),(165,'2026-05-02 00:01:57.763235','2026-06-15',1300.00,6,NULL,'PENDING',5.09,'ACTIVE',1305.09,13),(166,'2026-05-02 00:01:57.765229','2026-07-15',1300.00,7,NULL,'PENDING',5.09,'ACTIVE',1305.09,13),(167,'2026-05-02 00:01:57.767225','2026-08-15',1300.00,8,NULL,'PENDING',5.09,'ACTIVE',1305.09,13),(168,'2026-05-02 00:01:57.768225','2026-09-15',1300.00,9,NULL,'PENDING',5.09,'ACTIVE',1305.09,13),(169,'2026-05-02 00:01:57.813228','2026-02-15',2500.00,1,NULL,'OVERDUE',12.29,'ACTIVE',2512.29,14),(170,'2026-05-02 00:01:57.815229','2026-03-15',2500.00,2,NULL,'OVERDUE',12.29,'ACTIVE',2512.29,14),(171,'2026-05-02 00:01:57.817233','2026-04-15',2500.00,3,'2026-04-15 00:00:00.000000','PAID',12.29,'ACTIVE',2512.29,14),(172,'2026-05-02 00:01:57.819227','2026-05-15',2500.00,4,NULL,'PENDING',12.29,'ACTIVE',2512.29,14),(173,'2026-05-02 00:01:57.820229','2026-06-15',2500.00,5,NULL,'PENDING',12.29,'ACTIVE',2512.29,14),(174,'2026-05-02 00:01:57.822230','2026-07-15',2500.00,6,NULL,'PENDING',12.29,'ACTIVE',2512.29,14),(175,'2026-05-02 00:01:57.824229','2026-08-15',2500.00,7,NULL,'PENDING',12.29,'ACTIVE',2512.29,14),(176,'2026-05-02 00:01:57.825228','2026-09-15',2500.00,8,NULL,'PENDING',12.29,'ACTIVE',2512.29,14),(177,'2026-05-02 00:01:57.827228','2026-10-15',2500.00,9,NULL,'PENDING',12.29,'ACTIVE',2512.29,14),(178,'2026-05-02 00:01:57.829229','2026-11-15',2500.00,10,NULL,'PENDING',12.29,'ACTIVE',2512.29,14),(179,'2026-05-02 00:01:57.831227','2026-12-15',2500.00,11,NULL,'PENDING',12.29,'ACTIVE',2512.29,14),(180,'2026-05-02 00:01:57.833231','2027-01-15',2500.00,12,NULL,'PENDING',12.29,'ACTIVE',2512.29,14),(181,'2026-05-02 00:01:57.895231','2026-03-15',1100.00,1,NULL,'OVERDUE',4.26,'ACTIVE',1104.26,15),(182,'2026-05-02 00:01:57.900229','2026-04-15',1100.00,2,NULL,'OVERDUE',4.26,'ACTIVE',1104.26,15),(183,'2026-05-02 00:01:57.901228','2026-05-15',1100.00,3,NULL,'PENDING',4.26,'ACTIVE',1104.26,15),(184,'2026-05-02 00:01:57.903229','2026-06-15',1100.00,4,NULL,'PENDING',4.26,'ACTIVE',1104.26,15),(185,'2026-05-02 00:01:57.905228','2026-07-15',1100.00,5,NULL,'PENDING',4.26,'ACTIVE',1104.26,15),(186,'2026-05-02 00:01:57.907229','2026-08-15',1100.00,6,NULL,'PENDING',4.26,'ACTIVE',1104.26,15);

--
-- Table structure for table `eod_summary`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `eod_summary` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `branch_id` bigint NOT NULL,
  `business_date` date NOT NULL,
  `generated_at` datetime(6) NOT NULL,
  `generated_by` bigint DEFAULT NULL,
  `total_deposits` decimal(18,2) NOT NULL,
  `total_transactions` int NOT NULL,
  `total_withdrawals` decimal(18,2) NOT NULL,
  `vault_closing_balance` decimal(18,2) NOT NULL,
  `vault_opening_balance` decimal(18,2) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_eod_summary_branch_business_date` (`branch_id`,`business_date`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eod_summary`
--

INSERT INTO `eod_summary` VALUES (1,1,'2026-05-08','2026-05-08 18:00:00.000000',4,780000.00,42,420000.00,8810000.00,8450000.00),(2,2,'2026-05-08','2026-05-08 18:05:00.000000',5,540000.00,31,315000.00,4605000.00,4380000.00),(3,3,'2026-05-12','2026-05-12 18:00:00.000000',11,630000.00,28,260000.00,3570000.00,3200000.00),(4,1,'2026-05-31','2026-05-31 18:30:00.000000',4,1250000.00,67,840000.00,9130000.00,8720000.00),(5,2,'2026-05-31','2026-05-31 18:35:00.000000',5,890000.00,48,520000.00,4880000.00,4510000.00),(6,1,'2026-06-03','2026-06-03 18:15:00.000000',4,510000.00,24,275000.00,9365000.00,9130000.00);

--
-- Table structure for table `file_reference`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `file_reference` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `file_name` varchar(150) NOT NULL,
  `file_path` varchar(500) NOT NULL,
  `file_size` bigint NOT NULL,
  `file_type` varchar(100) NOT NULL,
  `module_name` varchar(80) NOT NULL,
  `original_file_name` varchar(200) NOT NULL,
  `reference_id` bigint DEFAULT NULL,
  `reference_table` varchar(100) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file_reference`
--

INSERT INTO `file_reference` VALUES (1,'2026-05-01 23:13:50.648473','CSR-00001.html','generated-statements\\CSR-00001.html',999,'text/html','CUSTOMER_STATEMENT','Customer-Statement-CSR-00001.html',1,'customer_statement_request','ACTIVE'),(2,'2026-05-01 23:13:50.832473','CSR-00002.html','generated-statements\\CSR-00002.html',1003,'text/html','CUSTOMER_STATEMENT','Customer-Statement-CSR-00002.html',2,'customer_statement_request','ACTIVE'),(3,'2026-05-01 23:13:50.894476','CSR-00003.html','generated-statements\\CSR-00003.html',999,'text/html','CUSTOMER_STATEMENT','Customer-Statement-CSR-00003.html',3,'customer_statement_request','ACTIVE'),(4,'2026-05-01 23:13:50.965475','CSR-00004.html','generated-statements\\CSR-00004.html',1003,'text/html','CUSTOMER_STATEMENT','Customer-Statement-CSR-00004.html',4,'customer_statement_request','ACTIVE'),(5,'2026-05-01 23:13:51.030477','CSR-00005.html','generated-statements\\CSR-00005.html',999,'text/html','CUSTOMER_STATEMENT','Customer-Statement-CSR-00005.html',5,'customer_statement_request','ACTIVE'),(6,'2026-05-01 23:13:51.083475','CSR-00006.html','generated-statements\\CSR-00006.html',1003,'text/html','CUSTOMER_STATEMENT','Customer-Statement-CSR-00006.html',6,'customer_statement_request','ACTIVE'),(7,'2026-05-01 23:13:51.138474','CSR-00007.html','generated-statements\\CSR-00007.html',999,'text/html','CUSTOMER_STATEMENT','Customer-Statement-CSR-00007.html',7,'customer_statement_request','ACTIVE'),(8,'2026-05-01 23:13:51.200476','CSR-00008.html','generated-statements\\CSR-00008.html',1003,'text/html','CUSTOMER_STATEMENT','Customer-Statement-CSR-00008.html',8,'customer_statement_request','ACTIVE'),(9,'2026-05-01 23:13:51.296478','CSR-00009.html','generated-statements\\CSR-00009.html',999,'text/html','CUSTOMER_STATEMENT','Customer-Statement-CSR-00009.html',9,'customer_statement_request','ACTIVE'),(10,'2026-05-01 23:13:51.352479','CSR-00010.html','generated-statements\\CSR-00010.html',1003,'text/html','CUSTOMER_STATEMENT','Customer-Statement-CSR-00010.html',10,'customer_statement_request','ACTIVE'),(11,'2026-05-01 23:13:51.405475','CSR-00011.html','generated-statements\\CSR-00011.html',999,'text/html','CUSTOMER_STATEMENT','Customer-Statement-CSR-00011.html',11,'customer_statement_request','ACTIVE'),(12,'2026-05-01 23:13:51.457477','CSR-00012.html','generated-statements\\CSR-00012.html',1003,'text/html','CUSTOMER_STATEMENT','Customer-Statement-CSR-00012.html',12,'customer_statement_request','ACTIVE'),(13,'2026-05-01 23:13:51.513476','CSR-00013.html','generated-statements\\CSR-00013.html',999,'text/html','CUSTOMER_STATEMENT','Customer-Statement-CSR-00013.html',13,'customer_statement_request','ACTIVE'),(14,'2026-05-01 23:13:51.567479','CSR-00014.html','generated-statements\\CSR-00014.html',1003,'text/html','CUSTOMER_STATEMENT','Customer-Statement-CSR-00014.html',14,'customer_statement_request','ACTIVE'),(15,'2026-05-01 23:13:51.616472','CSR-00015.html','generated-statements\\CSR-00015.html',999,'text/html','CUSTOMER_STATEMENT','Customer-Statement-CSR-00015.html',15,'customer_statement_request','ACTIVE'),(16,'2026-05-01 23:13:51.822486','BSR-00001.html','generated-statements\\BSR-00001.html',990,'text/html','BRANCH_STATEMENT','Branch-Statement-BSR-00001.html',1,'branch_statement_request','ACTIVE'),(17,'2026-05-01 23:13:51.890478','BSR-00002.html','generated-statements\\BSR-00002.html',997,'text/html','BRANCH_STATEMENT','Branch-Statement-BSR-00002.html',2,'branch_statement_request','ACTIVE'),(18,'2026-05-01 23:13:51.957475','BSR-00003.html','generated-statements\\BSR-00003.html',989,'text/html','BRANCH_STATEMENT','Branch-Statement-BSR-00003.html',3,'branch_statement_request','ACTIVE'),(19,'2026-05-01 23:13:52.026476','BSR-00004.html','generated-statements\\BSR-00004.html',986,'text/html','BRANCH_STATEMENT','Branch-Statement-BSR-00004.html',4,'branch_statement_request','ACTIVE'),(20,'2026-05-01 23:13:52.090478','BSR-00005.html','generated-statements\\BSR-00005.html',986,'text/html','BRANCH_STATEMENT','Branch-Statement-BSR-00005.html',5,'branch_statement_request','ACTIVE'),(21,'2026-05-01 23:13:52.158474','BSR-00006.html','generated-statements\\BSR-00006.html',998,'text/html','BRANCH_STATEMENT','Branch-Statement-BSR-00006.html',6,'branch_statement_request','ACTIVE'),(22,'2026-05-01 23:13:52.234476','BSR-00007.html','generated-statements\\BSR-00007.html',994,'text/html','BRANCH_STATEMENT','Branch-Statement-BSR-00007.html',7,'branch_statement_request','ACTIVE'),(23,'2026-05-01 23:13:52.301482','BSR-00008.html','generated-statements\\BSR-00008.html',997,'text/html','BRANCH_STATEMENT','Branch-Statement-BSR-00008.html',8,'branch_statement_request','ACTIVE'),(24,'2026-05-01 23:13:52.368486','BSR-00009.html','generated-statements\\BSR-00009.html',988,'text/html','BRANCH_STATEMENT','Branch-Statement-BSR-00009.html',9,'branch_statement_request','ACTIVE'),(25,'2026-05-01 23:13:52.429476','BSR-00010.html','generated-statements\\BSR-00010.html',986,'text/html','BRANCH_STATEMENT','Branch-Statement-BSR-00010.html',10,'branch_statement_request','ACTIVE'),(26,'2026-05-01 23:13:52.488479','BSR-00011.html','generated-statements\\BSR-00011.html',988,'text/html','BRANCH_STATEMENT','Branch-Statement-BSR-00011.html',11,'branch_statement_request','ACTIVE'),(27,'2026-05-01 23:13:52.550476','BSR-00012.html','generated-statements\\BSR-00012.html',987,'text/html','BRANCH_STATEMENT','Branch-Statement-BSR-00012.html',12,'branch_statement_request','ACTIVE'),(28,'2026-05-01 23:13:52.613473','BSR-00013.html','generated-statements\\BSR-00013.html',990,'text/html','BRANCH_STATEMENT','Branch-Statement-BSR-00013.html',13,'branch_statement_request','ACTIVE'),(29,'2026-05-01 23:13:52.690474','BSR-00014.html','generated-statements\\BSR-00014.html',991,'text/html','BRANCH_STATEMENT','Branch-Statement-BSR-00014.html',14,'branch_statement_request','ACTIVE'),(30,'2026-05-01 23:13:52.768486','BSR-00015.html','generated-statements\\BSR-00015.html',987,'text/html','BRANCH_STATEMENT','Branch-Statement-BSR-00015.html',15,'branch_statement_request','ACTIVE'),(46,'2026-05-02 16:18:14.000000','report-setup-01.html','generated-reports/report-setup-01.html',2048,'text/html','REPORTS','report-setup-01.html',NULL,'report_request_log','ACTIVE'),(47,'2026-05-02 16:18:14.000000','report-setup-02.html','generated-reports/report-setup-02.html',2084,'text/html','REPORTS','report-setup-02.html',NULL,'report_request_log','ACTIVE'),(48,'2026-05-02 16:18:14.000000','report-setup-03.html','generated-reports/report-setup-03.html',2120,'text/html','REPORTS','report-setup-03.html',NULL,'report_request_log','ACTIVE'),(49,'2026-05-02 16:18:14.000000','report-setup-04.html','generated-reports/report-setup-04.html',2140,'text/html','REPORTS','report-setup-04.html',NULL,'report_request_log','ACTIVE'),(50,'2026-05-02 16:18:14.000000','report-setup-05.html','generated-reports/report-setup-05.html',2160,'text/html','REPORTS','report-setup-05.html',NULL,'report_request_log','ACTIVE'),(51,'2026-05-02 16:18:14.000000','report-setup-06.html','generated-reports/report-setup-06.html',2200,'text/html','REPORTS','report-setup-06.html',NULL,'report_request_log','ACTIVE'),(52,'2026-05-02 16:18:14.000000','report-setup-07.csv','generated-reports/report-setup-07.csv',1220,'text/csv','REPORTS','report-setup-07.csv',NULL,'report_request_log','ACTIVE'),(53,'2026-05-02 16:18:14.000000','report-setup-08.csv','generated-reports/report-setup-08.csv',1240,'text/csv','REPORTS','report-setup-08.csv',NULL,'report_request_log','ACTIVE'),(54,'2026-05-02 16:18:14.000000','report-setup-09.html','generated-reports/report-setup-09.html',2060,'text/html','REPORTS','report-setup-09.html',NULL,'report_request_log','ACTIVE'),(55,'2026-05-02 16:18:14.000000','report-setup-10.csv','generated-reports/report-setup-10.csv',1280,'text/csv','REPORTS','report-setup-10.csv',NULL,'report_request_log','ACTIVE'),(56,'2026-05-02 16:18:14.000000','report-setup-11.html','generated-reports/report-setup-11.html',2050,'text/html','REPORTS','report-setup-11.html',NULL,'report_request_log','ACTIVE'),(57,'2026-05-02 16:18:14.000000','report-setup-12.csv','generated-reports/report-setup-12.csv',1300,'text/csv','REPORTS','report-setup-12.csv',NULL,'report_request_log','ACTIVE'),(58,'2026-05-02 16:18:14.000000','report-setup-13.html','generated-reports/report-setup-13.html',2180,'text/html','REPORTS','report-setup-13.html',NULL,'report_request_log','ACTIVE'),(59,'2026-05-02 16:18:14.000000','report-setup-14.csv','generated-reports/report-setup-14.csv',1320,'text/csv','REPORTS','report-setup-14.csv',NULL,'report_request_log','ACTIVE'),(60,'2026-05-02 16:18:14.000000','report-setup-15.html','generated-reports/report-setup-15.html',2220,'text/html','REPORTS','report-setup-15.html',NULL,'report_request_log','ACTIVE'),(61,'2026-05-02 16:37:10.682802','operational-31.html','generated-reports\\operational-31.html',1039,'text/html','REPORTS','operational-31.html',31,'report_request_log','ACTIVE'),(62,'2026-05-02 16:37:23.667133','operational-32.html','generated-reports\\operational-32.html',1423,'text/html','REPORTS','operational-32.html',32,'report_request_log','ACTIVE'),(63,'2026-05-04 15:16:58.815266','branch-33.html','generated-reports\\branch-33.html',1341,'text/html','REPORTS','branch-33.html',33,'report_request_log','ACTIVE'),(64,'2026-05-04 15:17:12.852269','branch-34.html','generated-reports\\branch-34.html',1341,'text/html','REPORTS','branch-34.html',34,'report_request_log','ACTIVE'),(65,'2026-05-04 15:17:16.820095','branch-35.html','generated-reports\\branch-35.html',1341,'text/html','REPORTS','branch-35.html',35,'report_request_log','ACTIVE'),(66,'2026-05-04 15:17:19.420272','branch-36.html','generated-reports\\branch-36.html',1341,'text/html','REPORTS','branch-36.html',36,'report_request_log','ACTIVE'),(67,'2026-05-05 08:59:10.736654','BSR-00016.html','generated-statements\\BSR-00016.html',988,'text/html','BRANCH_STATEMENT','Branch-Statement-BSR-00016.html',16,'branch_statement_request','ACTIVE'),(68,'2026-05-05 11:45:19.610401','profit_distribution-37.html','generated-reports\\profit_distribution-37.html',1189,'text/html','REPORTS','profit_distribution-37.html',37,'report_request_log','ACTIVE'),(69,'2026-05-08 17:54:03.919777','operational-38.html','generated-reports\\operational-38.html',1423,'text/html','REPORTS','operational-38.html',38,'report_request_log','ACTIVE'),(70,'2026-05-08 17:54:21.483898','operational-39.html','generated-reports\\operational-39.html',1423,'text/html','REPORTS','operational-39.html',39,'report_request_log','ACTIVE'),(71,'2026-05-08 17:54:38.161712','profit_distribution-40.html','generated-reports\\profit_distribution-40.html',1188,'text/html','REPORTS','profit_distribution-40.html',40,'report_request_log','ACTIVE'),(72,'2026-05-08 17:54:46.846076','financing_portfolio-41.html','generated-reports\\financing_portfolio-41.html',1036,'text/html','REPORTS','financing_portfolio-41.html',41,'report_request_log','ACTIVE'),(73,'2026-05-08 17:54:57.042761','par-42.html','generated-reports\\par-42.html',1571,'text/html','REPORTS','par-42.html',42,'report_request_log','ACTIVE'),(74,'2026-05-15 09:20:33.081553','branch-43.html','generated-reports\\branch-43.html',4406096,'text/html','REPORTS','branch-43.html',43,'report_request_log','ACTIVE'),(75,'2026-05-15 09:22:11.627874','branch-44.html','generated-reports\\branch-44.html',4406096,'text/html','REPORTS','branch-44.html',44,'report_request_log','ACTIVE'),(76,'2026-05-15 12:58:05.795924','operational-45.html','generated-reports\\operational-45.html',4407149,'text/html','REPORTS','operational-45.html',45,'report_request_log','ACTIVE'),(77,'2026-05-15 19:46:39.324357','profit_distribution-46.html','generated-reports\\profit_distribution-46.html',4408936,'text/html','REPORTS','profit_distribution-46.html',46,'report_request_log','ACTIVE'),(78,'2026-05-15 19:47:10.966652','par-47.html','generated-reports\\par-47.html',4411386,'text/html','REPORTS','par-47.html',47,'report_request_log','ACTIVE'),(79,'2026-05-15 19:48:26.968079','shariah_audit-48.html','generated-reports\\shariah_audit-48.html',4408018,'text/html','REPORTS','shariah_audit-48.html',48,'report_request_log','ACTIVE'),(80,'2026-05-15 19:48:47.208488','branch-49.html','generated-reports\\branch-49.html',4409618,'text/html','REPORTS','branch-49.html',49,'report_request_log','ACTIVE'),(81,'2026-05-16 01:28:04.790681','kpi-52.pdf','generated-reports\\kpi-52.pdf',3295406,'application/pdf','REPORTS','kpi-52.pdf',52,'report_request_log','ACTIVE'),(82,'2026-05-16 01:29:05.428787','kpi-53.pdf','generated-reports\\kpi-53.pdf',3295405,'application/pdf','REPORTS','kpi-53.pdf',53,'report_request_log','ACTIVE'),(83,'2026-05-16 01:29:39.405758','kpi-54.pdf','generated-reports\\kpi-54.pdf',3295406,'application/pdf','REPORTS','kpi-54.pdf',54,'report_request_log','ACTIVE'),(84,'2026-05-16 01:30:15.099273','kpi-55.pdf','generated-reports\\kpi-55.pdf',3295404,'application/pdf','REPORTS','kpi-55.pdf',55,'report_request_log','ACTIVE'),(85,'2026-05-16 01:30:51.657697','kpi-56.pdf','generated-reports\\kpi-56.pdf',3295406,'application/pdf','REPORTS','kpi-56.pdf',56,'report_request_log','ACTIVE'),(86,'2026-05-16 01:31:25.962340','kpi-57.pdf','generated-reports\\kpi-57.pdf',3295407,'application/pdf','REPORTS','kpi-57.pdf',57,'report_request_log','ACTIVE'),(87,'2026-05-16 01:32:54.245746','CSR-00016.pdf','generated-statements\\CSR-00016.pdf',3294264,'application/pdf','CUSTOMER_STATEMENT','Customer-Statement-CSR-00016.pdf',16,'customer_statement_request','ACTIVE'),(88,'2026-05-16 01:33:00.874744','kpi-58.pdf','generated-reports\\kpi-58.pdf',3295409,'application/pdf','REPORTS','kpi-58.pdf',58,'report_request_log','ACTIVE'),(89,'2026-05-16 01:36:44.510055','CSR-00017.pdf','generated-statements\\CSR-00017.pdf',3294416,'application/pdf','CUSTOMER_STATEMENT','Customer-Statement-CSR-00017.pdf',17,'customer_statement_request','ACTIVE'),(90,'2026-05-16 01:36:51.201059','kpi-59.pdf','generated-reports\\kpi-59.pdf',3295407,'application/pdf','REPORTS','kpi-59.pdf',59,'report_request_log','ACTIVE'),(91,'2026-05-16 08:07:31.080106','CSR-00018.pdf','generated-statements\\CSR-00018.pdf',3294416,'application/pdf','CUSTOMER_STATEMENT','Customer-Statement-CSR-00018.pdf',18,'customer_statement_request','ACTIVE'),(92,'2026-05-16 08:07:39.237434','kpi-60.pdf','generated-reports\\kpi-60.pdf',3295408,'application/pdf','REPORTS','kpi-60.pdf',60,'report_request_log','ACTIVE'),(93,'2026-05-16 08:10:09.352413','CSR-00019.pdf','generated-statements\\CSR-00019.pdf',3294417,'application/pdf','CUSTOMER_STATEMENT','Customer-Statement-CSR-00019.pdf',19,'customer_statement_request','ACTIVE'),(94,'2026-05-16 08:10:16.783416','kpi-61.pdf','generated-reports\\kpi-61.pdf',3295411,'application/pdf','REPORTS','kpi-61.pdf',61,'report_request_log','ACTIVE'),(95,'2026-05-17 01:23:59.288737','operational-62.html','generated-reports\\operational-62.html',4410686,'text/html','REPORTS','operational-62.html',62,'report_request_log','ACTIVE'),(96,'2026-05-17 01:24:11.557765','profit_distribution-63.html','generated-reports\\profit_distribution-63.html',4408406,'text/html','REPORTS','profit_distribution-63.html',63,'report_request_log','ACTIVE'),(97,'2026-05-17 01:24:13.834076','financing_portfolio-64.html','generated-reports\\financing_portfolio-64.html',4408025,'text/html','REPORTS','financing_portfolio-64.html',64,'report_request_log','ACTIVE'),(98,'2026-05-17 01:24:20.789732','par-65.html','generated-reports\\par-65.html',4411390,'text/html','REPORTS','par-65.html',65,'report_request_log','ACTIVE'),(99,'2026-05-17 01:24:23.111930','shariah_audit-66.html','generated-reports\\shariah_audit-66.html',4408025,'text/html','REPORTS','shariah_audit-66.html',66,'report_request_log','ACTIVE'),(100,'2026-05-17 01:24:24.503131','branch-67.html','generated-reports\\branch-67.html',4409623,'text/html','REPORTS','branch-67.html',67,'report_request_log','ACTIVE'),(101,'2026-05-17 01:24:31.842776','operational-68.html','generated-reports\\operational-68.html',4410686,'text/html','REPORTS','operational-68.html',68,'report_request_log','ACTIVE'),(102,'2026-05-17 01:24:43.712857','profit_distribution-69.html','generated-reports\\profit_distribution-69.html',4408406,'text/html','REPORTS','profit_distribution-69.html',69,'report_request_log','ACTIVE'),(103,'2026-05-17 01:24:45.533768','financing_portfolio-70.html','generated-reports\\financing_portfolio-70.html',4408025,'text/html','REPORTS','financing_portfolio-70.html',70,'report_request_log','ACTIVE'),(104,'2026-05-17 01:24:55.769384','shariah_audit-71.html','generated-reports\\shariah_audit-71.html',4408022,'text/html','REPORTS','shariah_audit-71.html',71,'report_request_log','ACTIVE'),(105,'2026-05-17 08:28:33.492689','operational-72.html','generated-reports\\operational-72.html',4410686,'text/html','REPORTS','operational-72.html',72,'report_request_log','ACTIVE'),(106,'2026-05-17 08:29:27.161164','profit_distribution-73.html','generated-reports\\profit_distribution-73.html',4408406,'text/html','REPORTS','profit_distribution-73.html',73,'report_request_log','ACTIVE'),(107,'2026-05-17 08:29:46.663168','financing_portfolio-74.html','generated-reports\\financing_portfolio-74.html',4408025,'text/html','REPORTS','financing_portfolio-74.html',74,'report_request_log','ACTIVE'),(108,'2026-05-17 08:30:25.931400','par-75.html','generated-reports\\par-75.html',4411390,'text/html','REPORTS','par-75.html',75,'report_request_log','ACTIVE'),(109,'2026-05-17 08:30:49.874930','shariah_audit-76.html','generated-reports\\shariah_audit-76.html',4408025,'text/html','REPORTS','shariah_audit-76.html',76,'report_request_log','ACTIVE'),(110,'2026-05-17 08:31:08.380002','branch-77.html','generated-reports\\branch-77.html',4409623,'text/html','REPORTS','branch-77.html',77,'report_request_log','ACTIVE'),(111,'2026-05-17 08:31:38.025001','branch-78.html','generated-reports\\branch-78.html',4409623,'text/html','REPORTS','branch-78.html',78,'report_request_log','ACTIVE'),(112,'2026-05-17 08:49:30.988141','operational-79.html','generated-reports\\operational-79.html',4410686,'text/html','REPORTS','operational-79.html',79,'report_request_log','ACTIVE'),(113,'2026-05-17 08:49:39.302142','profit_distribution-80.html','generated-reports\\profit_distribution-80.html',4408406,'text/html','REPORTS','profit_distribution-80.html',80,'report_request_log','ACTIVE'),(114,'2026-05-17 08:49:42.893956','financing_portfolio-81.html','generated-reports\\financing_portfolio-81.html',4408025,'text/html','REPORTS','financing_portfolio-81.html',81,'report_request_log','ACTIVE'),(115,'2026-05-17 08:49:44.361450','par-82.html','generated-reports\\par-82.html',4411387,'text/html','REPORTS','par-82.html',82,'report_request_log','ACTIVE'),(116,'2026-05-17 08:49:47.166163','shariah_audit-83.html','generated-reports\\shariah_audit-83.html',4408025,'text/html','REPORTS','shariah_audit-83.html',83,'report_request_log','ACTIVE'),(117,'2026-05-17 08:49:49.096730','branch-84.html','generated-reports\\branch-84.html',4409623,'text/html','REPORTS','branch-84.html',84,'report_request_log','ACTIVE'),(118,'2026-05-17 09:00:00.901929','operational-85.html','generated-reports\\operational-85.html',4410686,'text/html','REPORTS','operational-85.html',85,'report_request_log','ACTIVE'),(119,'2026-05-17 09:03:21.335797','operational-86.html','generated-reports\\operational-86.html',4410686,'text/html','REPORTS','operational-86.html',86,'report_request_log','ACTIVE'),(120,'2026-05-17 09:08:41.305616','operational-87.html','generated-reports\\operational-87.html',4410686,'text/html','REPORTS','operational-87.html',87,'report_request_log','ACTIVE'),(121,'2026-05-17 09:09:44.949195','operational-88.html','generated-reports\\operational-88.html',4410686,'text/html','REPORTS','operational-88.html',88,'report_request_log','ACTIVE'),(122,'2026-05-17 09:14:10.690505','operational-89.html','generated-reports\\operational-89.html',4410686,'text/html','REPORTS','operational-89.html',89,'report_request_log','ACTIVE'),(123,'2026-05-17 09:18:08.415764','operational-90.html','generated-reports\\operational-90.html',4410686,'text/html','REPORTS','operational-90.html',90,'report_request_log','ACTIVE'),(124,'2026-05-17 09:18:56.593388','operational-91.html','generated-reports\\operational-91.html',4410686,'text/html','REPORTS','operational-91.html',91,'report_request_log','ACTIVE'),(125,'2026-05-17 09:19:18.869629','operational-92.html','generated-reports\\operational-92.html',4410686,'text/html','REPORTS','operational-92.html',92,'report_request_log','ACTIVE'),(126,'2026-05-17 09:19:20.514632','operational-93.html','generated-reports\\operational-93.html',4410686,'text/html','REPORTS','operational-93.html',93,'report_request_log','ACTIVE'),(127,'2026-05-17 09:21:21.141713','operational-94.html','generated-reports\\operational-94.html',4410686,'text/html','REPORTS','operational-94.html',94,'report_request_log','ACTIVE'),(128,'2026-05-17 09:28:41.719420','operational-95.html','generated-reports\\operational-95.html',4410686,'text/html','REPORTS','operational-95.html',95,'report_request_log','ACTIVE'),(129,'2026-05-17 09:29:29.368964','operational-96.html','generated-reports\\operational-96.html',4410683,'text/html','REPORTS','operational-96.html',96,'report_request_log','ACTIVE'),(130,'2026-05-17 09:30:06.248194','operational-97.html','generated-reports\\operational-97.html',4410686,'text/html','REPORTS','operational-97.html',97,'report_request_log','ACTIVE'),(131,'2026-05-18 08:35:02.879584','financing_portfolio-98.html','generated-reports\\financing_portfolio-98.html',4408022,'text/html','REPORTS','financing_portfolio-98.html',98,'report_request_log','ACTIVE'),(132,'2026-05-18 08:43:36.705618','management_pl-99.html','generated-reports\\management_pl-99.html',4411761,'text/html','REPORTS','management_pl-99.html',99,'report_request_log','ACTIVE'),(133,'2026-05-18 08:46:24.195365','financing_portfolio-100.html','generated-reports\\financing_portfolio-100.html',4408025,'text/html','REPORTS','financing_portfolio-100.html',100,'report_request_log','ACTIVE'),(134,'2026-05-18 08:46:26.469368','par-101.html','generated-reports\\par-101.html',4411390,'text/html','REPORTS','par-101.html',101,'report_request_log','ACTIVE'),(135,'2026-05-18 08:46:28.150371','shariah_audit-102.html','generated-reports\\shariah_audit-102.html',4408025,'text/html','REPORTS','shariah_audit-102.html',102,'report_request_log','ACTIVE'),(136,'2026-05-18 08:46:30.154370','management_pl-103.html','generated-reports\\management_pl-103.html',4411764,'text/html','REPORTS','management_pl-103.html',103,'report_request_log','ACTIVE'),(137,'2026-05-18 08:46:33.460368','profit_distribution-104.html','generated-reports\\profit_distribution-104.html',4408406,'text/html','REPORTS','profit_distribution-104.html',104,'report_request_log','ACTIVE'),(138,'2026-05-18 08:46:55.093659','management_pl-105.html','generated-reports\\management_pl-105.html',4411764,'text/html','REPORTS','management_pl-105.html',105,'report_request_log','ACTIVE'),(139,'2026-05-18 08:47:04.863946','financing_portfolio-106.html','generated-reports\\financing_portfolio-106.html',4408025,'text/html','REPORTS','financing_portfolio-106.html',106,'report_request_log','ACTIVE'),(140,'2026-05-18 08:47:10.490586','management_pl-107.html','generated-reports\\management_pl-107.html',4411764,'text/html','REPORTS','management_pl-107.html',107,'report_request_log','ACTIVE'),(141,'2026-05-18 08:58:02.534114','management_pl-108.html','generated-reports\\management_pl-108.html',4411764,'text/html','REPORTS','management_pl-108.html',108,'report_request_log','ACTIVE'),(142,'2026-05-18 09:37:54.425471','management_pl-109.html','generated-reports\\management_pl-109.html',4411798,'text/html','REPORTS','management_pl-109.html',109,'report_request_log','ACTIVE'),(143,'2026-05-18 09:38:49.757540','management_pl-110.html','generated-reports\\management_pl-110.html',4411798,'text/html','REPORTS','management_pl-110.html',110,'report_request_log','ACTIVE'),(144,'2026-05-18 09:42:45.473088','management_pl-111.html','generated-reports\\management_pl-111.html',4411798,'text/html','REPORTS','management_pl-111.html',111,'report_request_log','ACTIVE'),(145,'2026-05-18 09:47:28.343935','management_pl-112.html','generated-reports\\management_pl-112.html',4411798,'text/html','REPORTS','management_pl-112.html',112,'report_request_log','ACTIVE'),(146,'2026-05-18 09:48:26.310095','financing_portfolio-113.html','generated-reports\\financing_portfolio-113.html',4408025,'text/html','REPORTS','financing_portfolio-113.html',113,'report_request_log','ACTIVE'),(147,'2026-05-18 09:48:29.670229','management_pl-114.html','generated-reports\\management_pl-114.html',4411772,'text/html','REPORTS','management_pl-114.html',114,'report_request_log','ACTIVE'),(148,'2026-05-18 09:59:51.516570','management_pl-115.html','generated-reports\\management_pl-115.html',4411769,'text/html','REPORTS','management_pl-115.html',115,'report_request_log','ACTIVE'),(149,'2026-05-18 10:25:18.080902','management_pl-116.html','generated-reports\\management_pl-116.html',4411798,'text/html','REPORTS','management_pl-116.html',116,'report_request_log','ACTIVE'),(150,'2026-05-18 10:26:31.161171','profit_distribution-117.html','generated-reports\\profit_distribution-117.html',4408406,'text/html','REPORTS','profit_distribution-117.html',117,'report_request_log','ACTIVE'),(151,'2026-05-18 10:26:35.165763','operational-118.html','generated-reports\\operational-118.html',4410686,'text/html','REPORTS','operational-118.html',118,'report_request_log','ACTIVE'),(153,'2026-06-05 02:24:13.431992','profit_distribution-120.html','generated-reports\\profit_distribution-120.html',4408410,'text/html','REPORTS','profit_distribution-120.html',120,'report_request_log','ACTIVE'),(154,'2026-06-05 02:27:58.683925','management_pl-121.html','generated-reports\\management_pl-121.html',4411823,'text/html','REPORTS','management_pl-121.html',121,'report_request_log','ACTIVE'),(155,'2026-06-05 02:28:00.347015','financing_portfolio-122.html','generated-reports\\financing_portfolio-122.html',4408025,'text/html','REPORTS','financing_portfolio-122.html',122,'report_request_log','ACTIVE'),(156,'2026-06-05 02:28:05.798098','par-123.html','generated-reports\\par-123.html',4411390,'text/html','REPORTS','par-123.html',123,'report_request_log','ACTIVE'),(157,'2026-06-05 02:28:12.728660','shariah_audit-124.html','generated-reports\\shariah_audit-124.html',4408025,'text/html','REPORTS','shariah_audit-124.html',124,'report_request_log','ACTIVE'),(158,'2026-06-05 02:28:19.749144','branch-125.html','generated-reports\\branch-125.html',4408457,'text/html','REPORTS','branch-125.html',125,'report_request_log','ACTIVE'),(159,'2026-06-05 02:28:22.399780','management_pl-126.html','generated-reports\\management_pl-126.html',4411823,'text/html','REPORTS','management_pl-126.html',126,'report_request_log','ACTIVE'),(160,'2026-06-05 04:54:05.673953','CSR-00020.pdf','generated-statements\\CSR-00020.pdf',3295419,'application/pdf','CUSTOMER_STATEMENT','Customer-Statement-CSR-00020.pdf',20,'customer_statement_request','ACTIVE'),(161,'2026-06-05 05:01:08.739464','CSR-00021.pdf','generated-statements\\CSR-00021.pdf',3294409,'application/pdf','CUSTOMER_STATEMENT','Customer-Statement-CSR-00021.pdf',21,'customer_statement_request','ACTIVE'),(162,'2026-06-05 16:57:41.619967','management_pl-126.html','generated-reports\\management_pl-126.html',4411820,'text/html','REPORTS','management_pl-126.html',126,'report_request_log','ACTIVE'),(163,'2026-06-05 17:13:04.498279','trial_balance-171.html','generated-reports\\trial_balance-171.html',4413172,'text/html','REPORTS','trial_balance-171.html',171,'report_request_log','ACTIVE'),(164,'2026-06-05 17:13:11.761033','ledger_profit_loss-172.html','generated-reports\\ledger_profit_loss-172.html',4413278,'text/html','REPORTS','ledger_profit_loss-172.html',172,'report_request_log','ACTIVE'),(165,'2026-06-05 17:13:19.009346','operational-173.html','generated-reports\\operational-173.html',4410683,'text/html','REPORTS','operational-173.html',173,'report_request_log','ACTIVE'),(166,'2026-06-05 17:13:26.124425','management_pl-174.html','generated-reports\\management_pl-174.html',4411823,'text/html','REPORTS','management_pl-174.html',174,'report_request_log','ACTIVE'),(167,'2026-06-05 17:14:25.113264','operational-175.html','generated-reports\\operational-175.html',4410686,'text/html','REPORTS','operational-175.html',175,'report_request_log','ACTIVE'),(168,'2026-06-05 17:14:33.031283','profit_distribution-176.html','generated-reports\\profit_distribution-176.html',4408949,'text/html','REPORTS','profit_distribution-176.html',176,'report_request_log','ACTIVE'),(169,'2026-06-05 17:14:41.161678','management_pl-177.html','generated-reports\\management_pl-177.html',4411823,'text/html','REPORTS','management_pl-177.html',177,'report_request_log','ACTIVE'),(170,'2026-06-05 17:14:49.022164','trial_balance-178.html','generated-reports\\trial_balance-178.html',4413172,'text/html','REPORTS','trial_balance-178.html',178,'report_request_log','ACTIVE'),(171,'2026-06-05 17:14:56.906096','ledger_profit_loss-179.html','generated-reports\\ledger_profit_loss-179.html',4413278,'text/html','REPORTS','ledger_profit_loss-179.html',179,'report_request_log','ACTIVE'),(172,'2026-06-05 17:15:04.827864','financing_portfolio-180.html','generated-reports\\financing_portfolio-180.html',4411249,'text/html','REPORTS','financing_portfolio-180.html',180,'report_request_log','ACTIVE'),(173,'2026-06-05 17:15:12.033101','par-181.html','generated-reports\\par-181.html',4411396,'text/html','REPORTS','par-181.html',181,'report_request_log','ACTIVE'),(174,'2026-06-05 17:15:19.565332','shariah_audit-182.html','generated-reports\\shariah_audit-182.html',4412043,'text/html','REPORTS','shariah_audit-182.html',182,'report_request_log','ACTIVE'),(175,'2026-06-05 17:15:26.696417','branch-183.html','generated-reports\\branch-183.html',4409623,'text/html','REPORTS','branch-183.html',183,'report_request_log','ACTIVE'),(176,'2026-06-05 17:15:34.484752','kpi-184.html','generated-reports\\kpi-184.html',4409913,'text/html','REPORTS','kpi-184.html',184,'report_request_log','ACTIVE'),(177,'2026-06-05 17:15:41.775853','growth-185.html','generated-reports\\growth-185.html',4409357,'text/html','REPORTS','growth-185.html',185,'report_request_log','ACTIVE'),(178,'2026-06-05 17:15:48.909148','loan_recovery-186.html','generated-reports\\loan_recovery-186.html',4411513,'text/html','REPORTS','loan_recovery-186.html',186,'report_request_log','ACTIVE'),(179,'2026-06-05 17:15:56.140953','monthly_closing-187.html','generated-reports\\monthly_closing-187.html',4409808,'text/html','REPORTS','monthly_closing-187.html',187,'report_request_log','ACTIVE'),(180,'2026-06-05 17:17:21.496345','trial_balance-188.html','generated-reports\\trial_balance-188.html',4413169,'text/html','REPORTS','trial_balance-188.html',188,'report_request_log','ACTIVE'),(181,'2026-06-05 17:17:45.714159','trial_balance-189.html','generated-reports\\trial_balance-189.html',4413172,'text/html','REPORTS','trial_balance-189.html',189,'report_request_log','ACTIVE'),(182,'2026-06-05 23:37:55.008474','trial_balance-192.html','generated-reports\\trial_balance-192.html',4413172,'text/html','REPORTS','trial_balance-192.html',192,'report_request_log','ACTIVE'),(183,'2026-06-06 10:32:42.166777','ledger_profit_loss-199.html','generated-reports\\ledger_profit_loss-199.html',4413278,'text/html','REPORTS','ledger_profit_loss-199.html',199,'report_request_log','ACTIVE'),(184,'2026-06-06 11:57:09.384497','CSR-00022.pdf','generated-statements\\CSR-00022.pdf',3294417,'application/pdf','CUSTOMER_STATEMENT','Customer-Statement-CSR-00022.pdf',22,'customer_statement_request','ACTIVE'),(185,'2026-06-07 12:07:17.612911','operational-216.html','generated-reports\\operational-216.html',4408987,'text/html','REPORTS','operational-216.html',216,'report_request_log','ACTIVE'),(186,'2026-06-07 12:15:14.529142','operational-219.html','generated-reports\\operational-219.html',4410683,'text/html','REPORTS','operational-219.html',219,'report_request_log','ACTIVE'),(187,'2026-06-07 12:29:33.002000','management_pl-232.html','generated-reports\\management_pl-232.html',4411820,'text/html','REPORTS','management_pl-232.html',232,'report_request_log','ACTIVE'),(188,'2026-06-07 12:29:55.183297','management_pl-233.html','generated-reports\\management_pl-233.html',4411823,'text/html','REPORTS','management_pl-233.html',233,'report_request_log','ACTIVE'),(189,'2026-06-13 02:20:28.984597','management_pl-235.html','generated-reports\\management_pl-235.html',4412887,'text/html','REPORTS','management_pl-235.html',235,'report_request_log','ACTIVE'),(190,'2026-06-13 02:27:17.232233','management_pl-237.html','generated-reports\\management_pl-237.html',4412895,'text/html','REPORTS','management_pl-237.html',237,'report_request_log','ACTIVE'),(191,'2026-06-13 03:02:28.920444','BSR-00016.html','generated-statements\\BSR-00016.html',4409894,'text/html','BRANCH_STATEMENT','Branch-Statement-BSR-00016.html',16,'branch_statement_request','ACTIVE'),(192,'2026-06-13 03:04:35.038254','monthly_closing-252.html','generated-reports\\monthly_closing-252.html',4410929,'text/html','REPORTS','monthly_closing-252.html',252,'report_request_log','ACTIVE'),(193,'2026-06-13 03:49:46.362999','CSR-00023.pdf','generated-statements\\CSR-00023.pdf',3287963,'application/pdf','CUSTOMER_STATEMENT','Customer-Statement-CSR-00023.pdf',23,'customer_statement_request','ACTIVE'),(194,'2026-06-13 03:49:46.392001','CSR-00023.pdf','generated-statements\\CSR-00023.pdf',3287963,'application/pdf','CUSTOMER_STATEMENT','Customer-Statement-CSR-00023.pdf',23,'customer_statement_request','ACTIVE'),(195,'2026-06-13 03:49:46.846611','BSR-00017.html','generated-statements\\BSR-00017.html',4410668,'text/html','BRANCH_STATEMENT','Branch-Statement-BSR-00017.html',17,'branch_statement_request','ACTIVE'),(196,'2026-06-13 03:49:47.053610','BSR-00017.html','generated-statements\\BSR-00017.html',4410668,'text/html','BRANCH_STATEMENT','Branch-Statement-BSR-00017.html',17,'branch_statement_request','ACTIVE'),(197,'2026-06-13 04:10:06.511067','ledger_profit_loss-274.html','generated-reports\\ledger_profit_loss-274.html',4414404,'text/html','REPORTS','ledger_profit_loss-274.html',274,'report_request_log','ACTIVE'),(198,'2026-06-13 04:10:08.059210','management_pl-275.pdf','generated-reports\\management_pl-275.pdf',3289055,'application/pdf','REPORTS','management_pl-275.pdf',275,'report_request_log','ACTIVE'),(199,'2026-06-13 04:10:11.407812','operational-276.xlsx','generated-reports\\operational-276.xlsx',4643,'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet','REPORTS','operational-276.xlsx',276,'report_request_log','ACTIVE');

--
-- Table structure for table `financing_application`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `financing_application` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `application_no` varchar(40) NOT NULL,
  `application_status` enum('ACTIVE','APPROVED','ASSET_VERIFIED','CLOSED','DISBURSED','DOC_CHECK','DRAFT','REJECTED','RETURNED','SHARIAH_REVIEW','SUBMITTED') NOT NULL,
  `approved_at` datetime(6) DEFAULT NULL,
  `approved_by` varchar(120) DEFAULT NULL,
  `asset_description` varchar(500) NOT NULL,
  `branch_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `purpose` varchar(500) NOT NULL,
  `remarks` varchar(1000) DEFAULT NULL,
  `requested_amount` decimal(18,2) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `submitted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `customer_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `supporting_document_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_financing_application_no` (`application_no`),
  KEY `FK1nxmyyn1w55eiw72eyy1f91md` (`customer_id`),
  KEY `FKlfqm0m7ilrtme4aqgywwlcnma` (`product_id`),
  CONSTRAINT `FK1nxmyyn1w55eiw72eyy1f91md` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `FKlfqm0m7ilrtme4aqgywwlcnma` FOREIGN KEY (`product_id`) REFERENCES `financing_product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `financing_application`
--

INSERT INTO `financing_application` VALUES (1,'FNA-00001','SUBMITTED',NULL,NULL,'Motorbike purchase for courier service delivery support',1,'2026-01-08 10:00:00.000000','Transport support for small delivery business','Documents received and waiting for verification',250000.00,'ACTIVE','2026-01-10 09:20:00.000000','2026-01-10 09:20:00.000000',1,1,NULL),(2,'FNA-00002','ASSET_VERIFIED',NULL,NULL,'Pickup van for district logistics support',2,'2026-01-09 10:15:00.000000','Expand local transport capacity for trading activity','Asset visit completed and quotation matched',420000.00,'ACTIVE','2026-01-11 09:40:00.000000','2026-01-14 12:10:00.000000',2,2,NULL),(3,'FNA-00003','SHARIAH_REVIEW',NULL,NULL,'Display freezer and retail shelving for grocery outlet',1,'2026-01-10 11:00:00.000000','Retail outlet asset enhancement before Ramadan peak season','Moved to shariah review after asset confirmation',180000.00,'ACTIVE','2026-01-12 10:00:00.000000','2026-01-15 11:30:00.000000',3,3,NULL),(4,'FNA-00004','APPROVED','2026-01-18 15:20:00.000000','SYSTEM_REVIEWER','Rice milling machine on lease support',1,'2026-01-11 09:30:00.000000','Production capacity improvement for seasonal demand','Approved for disbursement queue',520000.00,'ACTIVE','2026-01-13 09:10:00.000000','2026-01-18 15:20:00.000000',4,4,NULL),(5,'FNA-00005','APPROVED','2026-01-19 16:00:00.000000','SYSTEM_REVIEWER','Mini truck lease for supply chain movement',2,'2026-01-12 09:45:00.000000','Fleet support for corporate distribution route','Approved after review board confirmation',780000.00,'ACTIVE','2026-01-14 10:10:00.000000','2026-01-19 16:00:00.000000',5,5,NULL),(6,'FNA-00006','DISBURSED','2026-01-20 13:15:00.000000','SYSTEM_REVIEWER','setup, fertilizer and irrigation pump package',1,'2026-01-13 10:05:00.000000','Advance crop support before planting season','Disbursed and schedule generated',210000.00,'ACTIVE','2026-01-15 09:30:00.000000','2026-01-21 10:20:00.000000',6,6,NULL),(7,'FNA-00007','ACTIVE','2026-01-21 14:30:00.000000','SYSTEM_REVIEWER','Brick and steel procurement for site work',2,'2026-01-14 10:25:00.000000','Construction support for client warehouse expansion','Repayment started and one overdue installment exists',680000.00,'ACTIVE','2026-01-16 10:10:00.000000','2026-03-20 17:45:00.000000',7,7,NULL),(8,'FNA-00008','ACTIVE','2026-01-22 15:05:00.000000','SYSTEM_REVIEWER','Wholesale goods inventory for shared venture',1,'2026-01-15 10:45:00.000000','Trade inventory support for Musharaka cycle','Partial repayment collected and monitoring continues',460000.00,'ACTIVE','2026-01-17 11:00:00.000000','2026-03-28 13:15:00.000000',8,8,NULL),(9,'FNA-00009','CLOSED','2026-01-23 11:40:00.000000','SYSTEM_REVIEWER','Working capital float for distribution partnership',3,'2026-01-16 11:10:00.000000','Short-cycle trade support with scheduled recovery','All installments settled and financing closed',340000.00,'ACTIVE','2026-01-18 09:15:00.000000','2026-04-18 16:45:00.000000',9,9,NULL),(10,'FNA-00010','CLOSED','2026-01-24 12:10:00.000000','SYSTEM_REVIEWER','Dental unit and suction machine purchase',2,'2026-01-17 11:35:00.000000','Medical setup support for neighborhood clinic','Closed after full schedule settlement',290000.00,'ACTIVE','2026-01-19 09:40:00.000000','2026-04-15 14:10:00.000000',10,10,NULL),(11,'FNA-00011','APPROVED','2026-01-25 15:30:00.000000','SYSTEM_REVIEWER','Laptop and projector package for training center',1,'2026-01-18 12:00:00.000000','Education support asset financing for skills center','Approved and awaiting customer account selection',145000.00,'ACTIVE','2026-01-20 10:30:00.000000','2026-01-25 15:30:00.000000',11,11,NULL),(12,'FNA-00012','ASSET_VERIFIED',NULL,NULL,'Standby generator for backup power continuity',3,'2026-01-19 12:20:00.000000','Energy backup financing for small production unit','Submitted and document review pending',380000.00,'ACTIVE','2026-01-21 09:10:00.000000','2026-06-07 10:50:45.430895',12,12,NULL),(13,'FNA-00013','ASSET_VERIFIED',NULL,NULL,'Counter redesign and shelving for retail shop',2,'2026-01-20 12:40:00.000000','Store improvement to increase customer capacity','Verified and waiting for review board slot',160000.00,'ACTIVE','2026-01-22 09:50:00.000000','2026-01-24 15:00:00.000000',13,13,NULL),(14,'FNA-00014','DISBURSED','2026-01-28 14:20:00.000000','SYSTEM_REVIEWER','Bulk food commodity purchase ahead of seasonal demand',1,'2026-01-21 13:05:00.000000','Commodity financing support for wholesale sale cycle','Recently disbursed and first installment not yet due',510000.00,'ACTIVE','2026-01-23 10:20:00.000000','2026-01-29 11:45:00.000000',14,14,NULL),(15,'FNA-00015','ACTIVE','2026-01-30 15:00:00.000000','SYSTEM_REVIEWER','Workshop structure and finishing materials',3,'2026-01-22 13:25:00.000000','Workshop completion financing under Istisna structure','Collection active with mixed repayment performance',730000.00,'ACTIVE','2026-01-24 10:45:00.000000','2026-04-10 12:15:00.000000',15,15,NULL),(16,'FNA-00016','ACTIVE','2026-06-13 03:53:41.286953','shariah.board01','operational retail shop renovation materials',1,'2026-06-13 03:53:40.937074','Retail shop renovation financing for operational','Step 5 first installment collection',100000.00,'ACTIVE','2026-06-13 03:53:41.075674','2026-06-13 03:54:55.269754',23,13,'operational-financing-20260613035340.pdf'),(17,'FNA-00017','CLOSED','2026-06-13 03:59:02.418839','shariah.board01','Corporate inventory and retail stock procurement',1,'2026-06-13 03:59:02.244865','Inventory financing for high-volume operational portfolio','Full corporate facility settlement for operational profit recognition',6000000.00,'ACTIVE','2026-06-13 03:59:02.289720','2026-06-13 04:00:03.758252',23,16,'corporate-murabaha-20260613035901.pdf');

--
-- Table structure for table `financing_asset_verification`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `financing_asset_verification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `asset_value` decimal(18,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `verification_note` varchar(1000) NOT NULL,
  `verified_at` datetime(6) NOT NULL,
  `verified_by` varchar(120) NOT NULL,
  `application_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKrn3nu76xg5g9r4hp8kbld61d5` (`application_id`),
  CONSTRAINT `FKohwmh4rbpxl2bb5v6eh06j1vm` FOREIGN KEY (`application_id`) REFERENCES `financing_application` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `financing_asset_verification`
--

INSERT INTO `financing_asset_verification` VALUES (1,248000.00,'2026-01-14 10:10:00.000000','ACTIVE','Quotation and market price verified with supplier visit','2026-01-14 10:10:00.000000','Verifier A',1),(2,418000.00,'2026-01-14 12:10:00.000000','ACTIVE','Vehicle quotation, chassis details and dealer statement confirmed','2026-01-14 12:10:00.000000','Verifier B',2),(3,178000.00,'2026-01-15 11:20:00.000000','ACTIVE','Retail asset list and supplier invoice reviewed','2026-01-15 11:20:00.000000','Verifier C',3),(4,515000.00,'2026-01-17 10:15:00.000000','ACTIVE','Lease asset inspected at factory floor','2026-01-17 10:15:00.000000','Verifier D',4),(5,772000.00,'2026-01-18 13:05:00.000000','ACTIVE','Fleet lease proposal and insurance quote checked','2026-01-18 13:05:00.000000','Verifier E',5),(6,208000.00,'2026-01-19 09:45:00.000000','ACTIVE','Agro support pack verified with field officer note','2026-01-19 09:45:00.000000','Verifier F',6),(7,674000.00,'2026-01-20 10:05:00.000000','ACTIVE','Construction materials valuation completed on-site','2026-01-20 10:05:00.000000','Verifier G',7),(8,455000.00,'2026-01-21 10:50:00.000000','ACTIVE','Inventory valuation cross-checked against vendor bills','2026-01-21 10:50:00.000000','Verifier H',8),(9,338000.00,'2026-01-22 11:30:00.000000','ACTIVE','Working capital need aligned with trade cycle estimation','2026-01-22 11:30:00.000000','Verifier I',9),(10,287000.00,'2026-01-23 12:00:00.000000','ACTIVE','Medical equipment invoice and installation letter confirmed','2026-01-23 12:00:00.000000','Verifier J',10),(11,142000.00,'2026-01-24 10:25:00.000000','ACTIVE','Training center equipment list verified','2026-01-24 10:25:00.000000','Verifier K',11),(12,375000.00,'2026-01-24 16:40:00.000000','ACTIVE','Generator quotation and service warranty reviewed','2026-06-07 10:50:45.423941','Verifier L',12),(13,157000.00,'2026-01-25 11:45:00.000000','ACTIVE','Renovation BOQ and contractor note validated','2026-01-25 11:45:00.000000','Verifier M',13),(14,505000.00,'2026-01-27 10:00:00.000000','ACTIVE','Commodity purchase estimate verified with supplier ledger','2026-01-27 10:00:00.000000','Verifier N',14),(15,724000.00,'2026-01-28 09:20:00.000000','ACTIVE','Workshop development budget and material list checked','2026-01-28 09:20:00.000000','Verifier O',15),(16,125000.00,'2026-06-13 03:53:41.165670','ACTIVE','Asset and business risk verified for operational','2026-06-13 03:53:41.165670','ops.officer01',16),(17,7200000.00,'2026-06-13 03:59:02.334861','ACTIVE','Inventory invoices and business cashflow verified','2026-06-13 03:59:02.334861','ops.officer01',17);

--
-- Table structure for table `financing_disbursement`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `financing_disbursement` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `credited_account_id` bigint NOT NULL,
  `disbursed_amount` decimal(18,2) NOT NULL,
  `disbursed_by` varchar(120) NOT NULL,
  `disbursement_date` date NOT NULL,
  `disbursement_no` varchar(40) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `application_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_financing_disbursement_no` (`disbursement_no`),
  UNIQUE KEY `UKh9sqn6lrsecpdhgy69mtamo5b` (`application_id`),
  CONSTRAINT `FKqbr21jfdsmt67x4so3k9xlvit` FOREIGN KEY (`application_id`) REFERENCES `financing_application` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `financing_disbursement`
--

INSERT INTO `financing_disbursement` VALUES (1,'2026-01-21 10:20:00.000000',6,210000.00,'Disburser A','2026-01-21','FND-00001','ACTIVE',6),(2,'2026-01-22 11:10:00.000000',7,680000.00,'Disburser B','2026-01-22','FND-00002','ACTIVE',7),(3,'2026-01-23 11:40:00.000000',8,460000.00,'Disburser C','2026-01-23','FND-00003','ACTIVE',8),(4,'2026-01-24 12:15:00.000000',9,340000.00,'Disburser D','2026-01-24','FND-00004','ACTIVE',9),(5,'2026-01-25 13:00:00.000000',10,290000.00,'Disburser E','2026-01-25','FND-00005','ACTIVE',10),(6,'2026-01-29 11:45:00.000000',14,510000.00,'Disburser F','2026-01-29','FND-00006','ACTIVE',14),(7,'2026-01-31 12:15:00.000000',15,730000.00,'Disburser G','2026-01-31','FND-00007','ACTIVE',15),(8,'2026-06-13 03:54:25.092588',21,100000.00,'investment.officer02','2026-06-13','FND-00008','ACTIVE',16),(9,'2026-06-13 03:59:33.115332',21,6000000.00,'investment.officer02','2026-06-13','FND-00009','ACTIVE',17);

--
-- Table structure for table `financing_product`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `financing_product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `financing_type` enum('IJARAH','ISTISNA','MUDARABA','MURABAHA','MUSHARAKA','SALAM') NOT NULL,
  `maximum_amount` decimal(18,2) NOT NULL,
  `minimum_amount` decimal(18,2) NOT NULL,
  `product_code` varchar(40) NOT NULL,
  `product_name` varchar(150) NOT NULL,
  `profit_rule` varchar(500) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `tenure_months` int NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_financing_product_code` (`product_code`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `financing_product`
--

INSERT INTO `financing_product` VALUES (1,'2026-01-02 09:00:00.000000','MURABAHA',500000.00,50000.00,'FNP-00001','Murabaha SME Asset','Flat 11.50% Murabaha markup with equal monthly installments','ACTIVE',12,'2026-01-02 09:00:00.000000'),(2,'2026-01-02 09:05:00.000000','MURABAHA',700000.00,80000.00,'FNP-00002','Murabaha Transport Support','Flat 12.00% Murabaha markup with monthly recovery','ACTIVE',18,'2026-01-02 09:05:00.000000'),(3,'2026-01-02 09:10:00.000000','MURABAHA',350000.00,40000.00,'FNP-00003','Murabaha Retail Asset','Flat 10.75% markup with equal schedule recovery','ACTIVE',10,'2026-01-02 09:10:00.000000'),(4,'2026-01-02 09:15:00.000000','IJARAH',900000.00,150000.00,'FNP-00004','Ijarah Equipment Lease','Lease rental equivalent to 13.25% yearly profit rate','ACTIVE',24,'2026-01-02 09:15:00.000000'),(5,'2026-01-02 09:20:00.000000','IJARAH',1200000.00,250000.00,'FNP-00005','Ijarah Fleet Lease','Lease rental equivalent to 12.80% yearly profit rate','ACTIVE',30,'2026-01-02 09:20:00.000000'),(6,'2026-01-02 09:25:00.000000','SALAM',450000.00,60000.00,'FNP-00006','Salam Agro Input','Advance commodity purchase support at 9.80% expected margin','ACTIVE',9,'2026-01-02 09:25:00.000000'),(7,'2026-01-02 09:30:00.000000','ISTISNA',1000000.00,200000.00,'FNP-00007','Istisna Construction Support','Istisna production margin 14.10% over delivery period','ACTIVE',20,'2026-01-02 09:30:00.000000'),(8,'2026-01-02 09:35:00.000000','MUSHARAKA',800000.00,100000.00,'FNP-00008','Musharaka Trade Expansion','Shared profit allocation benchmark 15.25% for monthly settlement','ACTIVE',16,'2026-01-02 09:35:00.000000'),(9,'2026-01-02 09:40:00.000000','MUDARABA',650000.00,90000.00,'FNP-00009','Mudaraba Working Capital','Mudaraba partner share benchmark 14.40% with scheduled recovery','ACTIVE',14,'2026-01-02 09:40:00.000000'),(10,'2026-01-02 09:45:00.000000','MURABAHA',550000.00,75000.00,'FNP-00010','Murabaha Medical Equipment','Flat 11.90% Murabaha markup with equal installments','ACTIVE',12,'2026-01-02 09:45:00.000000'),(11,'2026-01-02 09:50:00.000000','MURABAHA',300000.00,30000.00,'FNP-00011','Murabaha Education Asset','Student asset support at 9.50% annualized markup','ACTIVE',8,'2026-01-02 09:50:00.000000'),(12,'2026-01-02 09:55:00.000000','IJARAH',650000.00,120000.00,'FNP-00012','Ijarah Generator Lease','Lease rental equivalent to 12.35% yearly profit rate','ACTIVE',18,'2026-01-02 09:55:00.000000'),(13,'2026-01-02 10:00:00.000000','MURABAHA',480000.00,55000.00,'FNP-00013','Murabaha Shop Renovation','Flat 10.60% markup for renovation asset financing','ACTIVE',11,'2026-01-02 10:00:00.000000'),(14,'2026-01-02 10:05:00.000000','SALAM',720000.00,95000.00,'FNP-00014','Salam Commodity Purchase','Advance commodity purchase margin 10.90% with staged recovery','ACTIVE',13,'2026-01-02 10:05:00.000000'),(15,'2026-01-02 10:10:00.000000','ISTISNA',950000.00,180000.00,'FNP-00015','Istisna Workshop Build','Production support margin 13.80% with milestone recovery','ACTIVE',22,'2026-01-02 10:10:00.000000'),(16,'2026-06-13 03:59:02.151918','MURABAHA',10000000.00,1000000.00,'FNP-SHOW-20260613035901','Corporate Murabaha Inventory Facility','Corporate inventory Murabaha markup 18.75% with monthly repayment','ACTIVE',12,'2026-06-13 03:59:02.151918');

--
-- Table structure for table `financing_schedule`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `financing_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `charity_amount` decimal(18,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `due_date` date NOT NULL,
  `installment_no` int NOT NULL,
  `paid_amount` decimal(18,2) NOT NULL,
  `paid_date` date DEFAULT NULL,
  `principal_amount` decimal(18,2) NOT NULL,
  `profit_amount` decimal(18,2) NOT NULL,
  `schedule_status` enum('OVERDUE','PAID','PARTIAL','PENDING') NOT NULL,
  `application_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKem6dtskxj5ue9no7crk706lqe` (`application_id`),
  CONSTRAINT `FKem6dtskxj5ue9no7crk706lqe` FOREIGN KEY (`application_id`) REFERENCES `financing_application` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `financing_schedule`
--

INSERT INTO `financing_schedule` VALUES (1,0.00,'2026-01-21 10:25:00.000000','2026-02-21',1,0.00,NULL,70000.00,7000.00,'PENDING',6),(2,0.00,'2026-01-21 10:25:00.000000','2026-03-21',2,0.00,NULL,70000.00,7000.00,'PENDING',6),(3,0.00,'2026-01-21 10:25:00.000000','2026-04-21',3,0.00,NULL,70000.00,7000.00,'PENDING',6),(4,0.00,'2026-01-21 10:25:00.000000','2026-05-21',4,0.00,NULL,70000.00,7000.00,'PENDING',6),(5,0.00,'2026-01-22 11:15:00.000000','2026-02-22',1,180000.00,'2026-02-22',170000.00,10000.00,'PAID',7),(6,0.00,'2026-01-22 11:15:00.000000','2026-03-22',2,100000.00,'2026-03-25',170000.00,10000.00,'PARTIAL',7),(7,1700.00,'2026-01-22 11:15:00.000000','2026-04-22',3,0.00,NULL,170000.00,10000.00,'OVERDUE',7),(8,0.00,'2026-01-22 11:15:00.000000','2026-05-22',4,0.00,NULL,170000.00,10000.00,'PENDING',7),(9,0.00,'2026-01-23 11:45:00.000000','2026-02-23',1,120000.00,'2026-02-23',115000.00,5000.00,'PAID',8),(10,0.00,'2026-01-23 11:45:00.000000','2026-03-23',2,60000.00,'2026-03-24',115000.00,5000.00,'PARTIAL',8),(11,1150.00,'2026-01-23 11:45:00.000000','2026-04-23',3,0.00,NULL,115000.00,5000.00,'OVERDUE',8),(12,0.00,'2026-01-23 11:45:00.000000','2026-05-23',4,0.00,NULL,115000.00,5000.00,'PENDING',8),(13,0.00,'2026-01-24 12:20:00.000000','2026-02-24',1,90000.00,'2026-02-24',85000.00,5000.00,'PAID',9),(14,0.00,'2026-01-24 12:20:00.000000','2026-03-24',2,90000.00,'2026-03-24',85000.00,5000.00,'PAID',9),(15,0.00,'2026-01-24 12:20:00.000000','2026-04-24',3,90000.00,'2026-04-20',85000.00,5000.00,'PAID',9),(16,0.00,'2026-01-24 12:20:00.000000','2026-05-24',4,90000.00,'2026-05-10',85000.00,5000.00,'PAID',9),(17,0.00,'2026-01-25 13:05:00.000000','2026-02-25',1,76000.00,'2026-02-25',72000.00,4000.00,'PAID',10),(18,0.00,'2026-01-25 13:05:00.000000','2026-03-25',2,76000.00,'2026-03-25',72000.00,4000.00,'PAID',10),(19,0.00,'2026-01-25 13:05:00.000000','2026-04-25',3,76000.00,'2026-04-21',72000.00,4000.00,'PAID',10),(20,0.00,'2026-01-25 13:05:00.000000','2026-05-25',4,76000.00,'2026-05-12',72000.00,4000.00,'PAID',10),(21,0.00,'2026-01-29 11:50:00.000000','2026-02-28',1,0.00,NULL,127500.00,9500.00,'PENDING',14),(22,0.00,'2026-01-29 11:50:00.000000','2026-03-28',2,0.00,NULL,127500.00,9500.00,'PENDING',14),(23,0.00,'2026-01-29 11:50:00.000000','2026-04-28',3,0.00,NULL,127500.00,9500.00,'PENDING',14),(24,0.00,'2026-01-29 11:50:00.000000','2026-05-28',4,0.00,NULL,127500.00,9500.00,'PENDING',14),(25,0.00,'2026-01-31 12:20:00.000000','2026-02-28',1,190000.00,'2026-02-28',182500.00,7500.00,'PAID',15),(26,0.00,'2026-01-31 12:20:00.000000','2026-03-31',2,90000.00,'2026-03-31',182500.00,7500.00,'PARTIAL',15),(27,1825.00,'2026-01-31 12:20:00.000000','2026-04-30',3,0.00,NULL,182500.00,7500.00,'OVERDUE',15),(28,0.00,'2026-01-31 12:20:00.000000','2026-05-31',4,0.00,NULL,182500.00,7500.00,'PENDING',15),(29,0.00,'2026-06-13 03:54:25.100588','2026-07-13',1,10054.55,'2026-06-13',9090.91,963.64,'PAID',16),(30,0.00,'2026-06-13 03:54:25.105590','2026-08-13',2,0.00,NULL,9090.91,963.64,'PENDING',16),(31,0.00,'2026-06-13 03:54:25.107590','2026-09-13',3,0.00,NULL,9090.91,963.64,'PENDING',16),(32,0.00,'2026-06-13 03:54:25.109588','2026-10-13',4,0.00,NULL,9090.91,963.64,'PENDING',16),(33,0.00,'2026-06-13 03:54:25.111589','2026-11-13',5,0.00,NULL,9090.91,963.64,'PENDING',16),(34,0.00,'2026-06-13 03:54:25.113587','2026-12-13',6,0.00,NULL,9090.91,963.64,'PENDING',16),(35,0.00,'2026-06-13 03:54:25.116586','2027-01-13',7,0.00,NULL,9090.91,963.64,'PENDING',16),(36,0.00,'2026-06-13 03:54:25.118588','2027-02-13',8,0.00,NULL,9090.91,963.64,'PENDING',16),(37,0.00,'2026-06-13 03:54:25.120588','2027-03-13',9,0.00,NULL,9090.91,963.64,'PENDING',16),(38,0.00,'2026-06-13 03:54:25.122588','2027-04-13',10,0.00,NULL,9090.91,963.64,'PENDING',16),(39,0.00,'2026-06-13 03:54:25.126588','2027-05-13',11,0.00,NULL,9090.91,963.64,'PENDING',16),(40,0.00,'2026-06-13 03:59:33.120326','2026-07-13',1,593750.00,'2026-06-13',500000.00,93750.00,'PAID',17),(41,0.00,'2026-06-13 03:59:33.122333','2026-08-13',2,593750.00,'2026-06-13',500000.00,93750.00,'PAID',17),(42,0.00,'2026-06-13 03:59:33.124330','2026-09-13',3,593750.00,'2026-06-13',500000.00,93750.00,'PAID',17),(43,0.00,'2026-06-13 03:59:33.126329','2026-10-13',4,593750.00,'2026-06-13',500000.00,93750.00,'PAID',17),(44,0.00,'2026-06-13 03:59:33.129327','2026-11-13',5,593750.00,'2026-06-13',500000.00,93750.00,'PAID',17),(45,0.00,'2026-06-13 03:59:33.131328','2026-12-13',6,593750.00,'2026-06-13',500000.00,93750.00,'PAID',17),(46,0.00,'2026-06-13 03:59:33.133327','2027-01-13',7,593750.00,'2026-06-13',500000.00,93750.00,'PAID',17),(47,0.00,'2026-06-13 03:59:33.135329','2027-02-13',8,593750.00,'2026-06-13',500000.00,93750.00,'PAID',17),(48,0.00,'2026-06-13 03:59:33.137327','2027-03-13',9,593750.00,'2026-06-13',500000.00,93750.00,'PAID',17),(49,0.00,'2026-06-13 03:59:33.138327','2027-04-13',10,593750.00,'2026-06-13',500000.00,93750.00,'PAID',17),(50,0.00,'2026-06-13 03:59:33.140326','2027-05-13',11,593750.00,'2026-06-13',500000.00,93750.00,'PAID',17),(51,0.00,'2026-06-13 03:59:33.142331','2027-06-13',12,593750.00,'2026-06-13',500000.00,93750.00,'PAID',17);

--
-- Table structure for table `fund_transfer`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fund_transfer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `from_account_id` bigint NOT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `to_account_id` bigint NOT NULL,
  `transfer_mode` enum('BEFTN','INTERNAL','RTGS') NOT NULL,
  `transaction_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjfgsd5rdtmwo4iqc4nps7qgg3` (`transaction_id`),
  CONSTRAINT `FKjfgsd5rdtmwo4iqc4nps7qgg3` FOREIGN KEY (`transaction_id`) REFERENCES `transaction_journal` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fund_transfer`
--

INSERT INTO `fund_transfer` VALUES (1,'2026-05-01 09:00:00.000000',1,'Deposit-linked internal memo',2,'INTERNAL',1),(2,'2026-05-01 09:15:00.000000',2,'Withdrawal linked transfer reference',1,'INTERNAL',2),(3,'2026-05-01 09:30:00.000000',3,'Core transfer reference',4,'INTERNAL',3),(4,'2026-05-01 09:45:00.000000',5,'Cheque settlement trace',4,'BEFTN',4),(5,'2026-05-01 10:00:00.000000',6,'Deposit cross note',5,'INTERNAL',5),(6,'2026-05-01 10:15:00.000000',7,'Withdrawal trace',6,'INTERNAL',6),(7,'2026-05-01 10:30:00.000000',8,'Transfer reference two',9,'RTGS',7),(8,'2026-05-01 10:45:00.000000',10,'Cheque settlement business current',5,'BEFTN',8),(9,'2026-05-01 11:00:00.000000',11,'Deposit note three',10,'INTERNAL',9),(10,'2026-05-01 11:15:00.000000',12,'Withdrawal note three',11,'INTERNAL',10),(11,'2026-05-01 11:30:00.000000',13,'NRB FC transfer',14,'INTERNAL',11),(12,'2026-05-01 11:45:00.000000',15,'Cheque review linkage',13,'BEFTN',12),(13,'2026-05-01 12:00:00.000000',2,'Reversal mirror entry',1,'INTERNAL',13),(14,'2026-05-01 12:10:00.000000',4,'Transfer reversal mirror entry',3,'INTERNAL',14),(15,'2026-05-01 12:25:00.000000',5,'High value deposit cross-check',2,'RTGS',15),(16,'2026-06-13 03:45:56.612264',21,'Step 4 operational internal transfer',19,'INTERNAL',24);

--
-- Table structure for table `gl_account`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gl_account` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_code` varchar(30) NOT NULL,
  `account_name` varchar(150) NOT NULL,
  `account_type` varchar(20) NOT NULL,
  `allow_posting` bit(1) NOT NULL,
  `branch_scoped` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `parent_account_code` varchar(30) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_gl_account_code` (`account_code`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gl_account`
--

INSERT INTO `gl_account` VALUES (1,'1010','Cash In Hand','ASSET',_binary '',_binary '','2026-05-18 01:00:48.124074',NULL,'ACTIVE',NULL),(2,'1020','Bank Balance','ASSET',_binary '',_binary '\0','2026-05-18 01:00:48.177071',NULL,'ACTIVE',NULL),(3,'1200','Financing Receivable','ASSET',_binary '',_binary '','2026-05-18 01:00:48.201069',NULL,'ACTIVE',NULL),(4,'2010','Customer Deposit Liability','LIABILITY',_binary '',_binary '','2026-05-18 01:00:48.219070',NULL,'ACTIVE',NULL),(5,'2020','Profit Payable to Depositors','LIABILITY',_binary '',_binary '\0','2026-05-18 01:00:48.241067',NULL,'ACTIVE',NULL),(6,'3010','Retained Earnings','EQUITY',_binary '',_binary '\0','2026-05-18 01:00:48.259068',NULL,'ACTIVE',NULL),(7,'4010','Financing Income','INCOME',_binary '',_binary '','2026-05-18 01:00:48.277068',NULL,'ACTIVE',NULL),(8,'4020','Fee And Commission Income','INCOME',_binary '',_binary '','2026-05-18 01:00:48.295068',NULL,'ACTIVE',NULL),(9,'5010','Profit Distribution Expense','EXPENSE',_binary '',_binary '\0','2026-05-18 01:00:48.313069',NULL,'ACTIVE',NULL),(10,'5020','Operating Expense','EXPENSE',_binary '',_binary '','2026-05-18 01:00:48.336069',NULL,'ACTIVE',NULL),(11,'4030','Deposit Scheme Management Income','INCOME',_binary '',_binary '\0','2026-04-01 10:00:00.000000',NULL,'ACTIVE','2026-04-01 10:00:00.000000'),(12,'4040','Card And Terminal Service Income','INCOME',_binary '',_binary '\0','2026-04-01 10:05:00.000000',NULL,'ACTIVE','2026-04-01 10:05:00.000000'),(13,'4050','Investment Return Income','INCOME',_binary '',_binary '\0','2026-04-01 10:10:00.000000',NULL,'ACTIVE','2026-04-01 10:10:00.000000'),(14,'5030','Salary And Benefits Expense','EXPENSE',_binary '',_binary '\0','2026-04-01 10:15:00.000000',NULL,'ACTIVE','2026-04-01 10:15:00.000000'),(15,'5040','Rent And Utilities Expense','EXPENSE',_binary '',_binary '\0','2026-04-01 10:20:00.000000',NULL,'ACTIVE','2026-04-01 10:20:00.000000'),(16,'5050','Technology And System Expense','EXPENSE',_binary '',_binary '\0','2026-04-01 10:25:00.000000',NULL,'ACTIVE','2026-04-01 10:25:00.000000'),(17,'5060','Audit Compliance And Marketing Expense','EXPENSE',_binary '',_binary '\0','2026-04-01 10:30:00.000000',NULL,'ACTIVE','2026-04-01 10:30:00.000000');

--
-- Table structure for table `gl_journal`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gl_journal` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `branch_id` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(80) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `journal_date` date NOT NULL,
  `journal_type` varchar(50) NOT NULL,
  `source_reference_id` bigint NOT NULL,
  `source_reference_no` varchar(100) DEFAULT NULL,
  `source_type` varchar(50) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=211 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gl_journal`
--

INSERT INTO `gl_journal` VALUES (103,3,'2026-05-18 08:42:58.193619','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00015','2026-04-07','PROFIT_POSTING',15,'PRF-00015','PROFIT_POSTING','ACTIVE'),(104,2,'2026-05-18 08:42:58.276624','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00013','2026-04-04','PROFIT_POSTING',13,'PRF-00013','PROFIT_POSTING','ACTIVE'),(105,1,'2026-05-18 08:42:58.341623','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00011','2026-04-06','PROFIT_POSTING',11,'PRF-00011','PROFIT_POSTING','ACTIVE'),(106,3,'2026-05-18 08:42:58.409624','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00009','2025-12-31','PROFIT_POSTING',9,'PRF-00009','PROFIT_POSTING','ACTIVE'),(107,1,'2026-05-18 08:42:58.474624','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00008','2026-04-15','PROFIT_POSTING',8,'PRF-00008','PROFIT_POSTING','ACTIVE'),(108,2,'2026-05-18 08:42:58.542622','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00007','2026-04-03','PROFIT_POSTING',7,'PRF-00007','PROFIT_POSTING','ACTIVE'),(109,2,'2026-05-18 08:42:58.603620','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00005','2026-04-08','PROFIT_POSTING',5,'PRF-00005','PROFIT_POSTING','ACTIVE'),(110,1,'2026-05-18 08:42:58.676622','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00003','2026-04-05','PROFIT_POSTING',3,'PRF-00003','PROFIT_POSTING','ACTIVE'),(111,2,'2026-05-18 08:42:58.743620','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00001','2026-04-10','PROFIT_POSTING',1,'PRF-00001','PROFIT_POSTING','ACTIVE'),(112,2,'2026-05-18 08:42:58.853624','SYSTEM_BOOTSTRAP','Realized financing income for schedule 1','2026-02-22','FINANCING_INCOME',5,'FNA-00007-INS-1','FINANCING_INCOME','ACTIVE'),(113,1,'2026-05-18 08:42:58.917620','SYSTEM_BOOTSTRAP','Realized financing income for schedule 1','2026-02-23','FINANCING_INCOME',9,'FNA-00008-INS-1','FINANCING_INCOME','ACTIVE'),(114,3,'2026-05-18 08:42:58.975620','SYSTEM_BOOTSTRAP','Realized financing income for schedule 1','2026-02-24','FINANCING_INCOME',13,'FNA-00009-INS-1','FINANCING_INCOME','ACTIVE'),(115,3,'2026-05-18 08:42:59.046618','SYSTEM_BOOTSTRAP','Realized financing income for schedule 2','2026-03-24','FINANCING_INCOME',14,'FNA-00009-INS-2','FINANCING_INCOME','ACTIVE'),(116,3,'2026-05-18 08:42:59.103623','SYSTEM_BOOTSTRAP','Realized financing income for schedule 3','2026-04-20','FINANCING_INCOME',15,'FNA-00009-INS-3','FINANCING_INCOME','ACTIVE'),(117,3,'2026-05-18 08:42:59.154618','SYSTEM_BOOTSTRAP','Realized financing income for schedule 4','2026-05-10','FINANCING_INCOME',16,'FNA-00009-INS-4','FINANCING_INCOME','ACTIVE'),(118,2,'2026-05-18 08:42:59.272621','SYSTEM_BOOTSTRAP','Realized financing income for schedule 1','2026-02-25','FINANCING_INCOME',17,'FNA-00010-INS-1','FINANCING_INCOME','ACTIVE'),(119,2,'2026-05-18 08:42:59.339624','SYSTEM_BOOTSTRAP','Realized financing income for schedule 2','2026-03-25','FINANCING_INCOME',18,'FNA-00010-INS-2','FINANCING_INCOME','ACTIVE'),(120,2,'2026-05-18 08:42:59.397618','SYSTEM_BOOTSTRAP','Realized financing income for schedule 3','2026-04-21','FINANCING_INCOME',19,'FNA-00010-INS-3','FINANCING_INCOME','ACTIVE'),(121,2,'2026-05-18 08:42:59.446616','SYSTEM_BOOTSTRAP','Realized financing income for schedule 4','2026-05-12','FINANCING_INCOME',20,'FNA-00010-INS-4','FINANCING_INCOME','ACTIVE'),(122,3,'2026-05-18 08:42:59.497617','SYSTEM_BOOTSTRAP','Realized financing income for schedule 1','2026-02-28','FINANCING_INCOME',25,'FNA-00015-INS-1','FINANCING_INCOME','ACTIVE'),(123,2,'2026-05-18 09:12:54.902280','SYSTEM_BOOTSTRAP','Management expense posting for COMPLIANCE_AUDIT','2026-05-05','MANAGEMENT_EXPENSE',14,'BR002-CMP-0526','MANAGEMENT_EXPENSE','ACTIVE'),(124,3,'2026-05-18 09:12:54.902280','SYSTEM_BOOTSTRAP','Management expense posting for OFFICE_ADMIN','2026-05-07','MANAGEMENT_EXPENSE',15,'BR003-ADM-0526','MANAGEMENT_EXPENSE','ACTIVE'),(125,4,'2026-05-18 09:12:54.902280','SYSTEM_BOOTSTRAP','Management expense posting for IT_SYSTEMS','2026-05-09','MANAGEMENT_EXPENSE',16,'BR004-ITS-0526','MANAGEMENT_EXPENSE','ACTIVE'),(126,6,'2026-05-18 09:12:54.902280','SYSTEM_BOOTSTRAP','Management expense posting for RENT_UTILITY','2026-05-10','MANAGEMENT_EXPENSE',17,'BR006-UTL-0526','MANAGEMENT_EXPENSE','ACTIVE'),(127,NULL,'2026-05-18 09:12:54.902280','SYSTEM_BOOTSTRAP','Management expense posting for SALARY_ALLOWANCE','2026-05-12','MANAGEMENT_EXPENSE',18,'HO-SAL-0526','MANAGEMENT_EXPENSE','ACTIVE'),(128,8,'2026-05-18 09:12:54.902280','SYSTEM_BOOTSTRAP','Management expense posting for OTHER_OPERATING','2026-05-14','MANAGEMENT_EXPENSE',19,'BR008-OPS-0526','MANAGEMENT_EXPENSE','ACTIVE'),(135,1,'2026-04-30 17:00:00.000000','SYSTEM','April realized financing income - Dhaka Main Branch','2026-04-30','FINANCING_INCOME',6001,'GAP-FIN-APR-B1','FINANCING_INCOME','ACTIVE'),(136,2,'2026-04-30 17:05:00.000000','SYSTEM','April realized financing income - Motijheel Branch','2026-04-30','FINANCING_INCOME',6002,'GAP-FIN-APR-B2','FINANCING_INCOME','ACTIVE'),(137,3,'2026-04-30 17:10:00.000000','SYSTEM','April realized financing income - Gulshan Branch','2026-04-30','FINANCING_INCOME',6003,'GAP-FIN-APR-B3','FINANCING_INCOME','ACTIVE'),(138,1,'2026-04-30 17:15:00.000000','SYSTEM','April transaction fee and service commission income','2026-04-30','FEE_COMMISSION',6101,'GAP-FEE-APR','FEE_COMMISSION','ACTIVE'),(139,2,'2026-04-30 17:20:00.000000','SYSTEM','April card and terminal service income','2026-04-30','CARD_SERVICE_INCOME',6102,'GAP-CARD-APR','CARD_SERVICE_INCOME','ACTIVE'),(140,1,'2026-04-30 17:25:00.000000','SYSTEM','April deposit scheme management income','2026-04-30','DEPOSIT_INCOME',6103,'GAP-DEP-APR','DEPOSIT_INCOME','ACTIVE'),(141,1,'2026-05-31 17:00:00.000000','SYSTEM','May realized financing income - Dhaka Main Branch','2026-05-31','FINANCING_INCOME',7001,'GAP-FIN-MAY-B1','FINANCING_INCOME','ACTIVE'),(142,2,'2026-05-31 17:05:00.000000','SYSTEM','May realized financing income - Motijheel Branch','2026-05-31','FINANCING_INCOME',7002,'GAP-FIN-MAY-B2','FINANCING_INCOME','ACTIVE'),(143,3,'2026-05-31 17:10:00.000000','SYSTEM','May realized financing income - Gulshan Branch','2026-05-31','FINANCING_INCOME',7003,'GAP-FIN-MAY-B3','FINANCING_INCOME','ACTIVE'),(144,1,'2026-05-31 17:15:00.000000','SYSTEM','May transaction fee and service commission income','2026-05-31','FEE_COMMISSION',7101,'GAP-FEE-MAY','FEE_COMMISSION','ACTIVE'),(145,2,'2026-05-31 17:20:00.000000','SYSTEM','May card and ATM service income','2026-05-31','CARD_SERVICE_INCOME',7102,'GAP-CARD-MAY','CARD_SERVICE_INCOME','ACTIVE'),(146,1,'2026-05-31 17:25:00.000000','SYSTEM','May deposit scheme management income','2026-05-31','DEPOSIT_INCOME',7103,'GAP-DEP-MAY','DEPOSIT_INCOME','ACTIVE'),(147,1,'2026-06-03 17:00:00.000000','SYSTEM','June realized financing income - Dhaka Main Branch','2026-06-03','FINANCING_INCOME',8001,'GAP-FIN-JUN-B1','FINANCING_INCOME','ACTIVE'),(148,2,'2026-06-03 17:05:00.000000','SYSTEM','June realized financing income - Motijheel Branch','2026-06-03','FINANCING_INCOME',8002,'GAP-FIN-JUN-B2','FINANCING_INCOME','ACTIVE'),(149,1,'2026-06-03 17:10:00.000000','SYSTEM','June transaction fee income','2026-06-03','FEE_COMMISSION',8101,'GAP-FEE-JUN','FEE_COMMISSION','ACTIVE'),(150,1,'2026-05-31 18:00:00.000000','ops.officer01','May salary expense GL posting','2026-05-31','EXPENSE_POSTING',7201,'GAP-EXP-MAY-SAL','MANAGEMENT_EXPENSE','ACTIVE'),(151,2,'2026-05-31 18:05:00.000000','ops.officer01','May rent and utilities expense GL posting','2026-05-31','EXPENSE_POSTING',7202,'GAP-EXP-MAY-RENT','MANAGEMENT_EXPENSE','ACTIVE'),(152,1,'2026-06-03 18:00:00.000000','ops.officer01','June technology expense GL posting','2026-06-03','EXPENSE_POSTING',8201,'GAP-EXP-JUN-TECH','MANAGEMENT_EXPENSE','ACTIVE'),(166,1,'2026-06-05 02:22:22.706031','SYSTEM_BOOTSTRAP','Management expense posting for TECHNOLOGY','2026-06-03','MANAGEMENT_EXPENSE',34,'TECH-JUN-26','MANAGEMENT_EXPENSE','ACTIVE'),(167,1,'2026-06-05 02:22:22.791143','SYSTEM_BOOTSTRAP','Management expense posting for TECHNOLOGY','2026-05-31','MANAGEMENT_EXPENSE',33,'TECH-MAY-26','MANAGEMENT_EXPENSE','ACTIVE'),(168,3,'2026-06-05 02:22:22.826011','SYSTEM_BOOTSTRAP','Management expense posting for SALARY','2026-05-31','MANAGEMENT_EXPENSE',32,'PAY-MAY-26-B3','MANAGEMENT_EXPENSE','ACTIVE'),(169,1,'2026-06-05 02:22:22.865812','SYSTEM_BOOTSTRAP','Management expense posting for SALARY','2026-05-31','MANAGEMENT_EXPENSE',31,'PAY-MAY-26-B1','MANAGEMENT_EXPENSE','ACTIVE'),(170,1,'2026-06-05 02:22:22.919840','SYSTEM_BOOTSTRAP','Management expense posting for TECHNOLOGY','2026-04-30','MANAGEMENT_EXPENSE',30,'TECH-APR-26','MANAGEMENT_EXPENSE','ACTIVE'),(171,5,'2026-06-05 02:22:22.958295','SYSTEM_BOOTSTRAP','Management expense posting for SALARY','2026-04-30','MANAGEMENT_EXPENSE',29,'PAY-APR-26-B5','MANAGEMENT_EXPENSE','ACTIVE'),(172,4,'2026-06-05 02:22:22.989685','SYSTEM_BOOTSTRAP','Management expense posting for SALARY','2026-04-30','MANAGEMENT_EXPENSE',28,'PAY-APR-26-B4','MANAGEMENT_EXPENSE','ACTIVE'),(173,1,'2026-06-05 02:22:23.021703','SYSTEM_BOOTSTRAP','Management expense posting for RENT','2026-03-31','MANAGEMENT_EXPENSE',27,'RNT-MAR-26-B1','MANAGEMENT_EXPENSE','ACTIVE'),(174,2,'2026-06-05 02:22:23.055342','SYSTEM_BOOTSTRAP','Management expense posting for SALARY','2026-03-31','MANAGEMENT_EXPENSE',26,'PAY-MAR-26-B2','MANAGEMENT_EXPENSE','ACTIVE'),(175,1,'2026-06-05 02:22:23.086345','SYSTEM_BOOTSTRAP','Management expense posting for SALARY','2026-03-31','MANAGEMENT_EXPENSE',25,'PAY-MAR-26-B1','MANAGEMENT_EXPENSE','ACTIVE'),(176,1,'2026-06-05 02:22:23.173451','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00022','2026-05-15','PROFIT_POSTING',22,'PRF-00022','PROFIT_POSTING','ACTIVE'),(177,2,'2026-06-05 02:22:23.221540','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00021','2026-05-03','PROFIT_POSTING',21,'PRF-00021','PROFIT_POSTING','ACTIVE'),(178,2,'2026-06-05 02:22:23.266142','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00020','2026-05-08','PROFIT_POSTING',20,'PRF-00020','PROFIT_POSTING','ACTIVE'),(179,1,'2026-06-05 02:22:23.313054','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00019','2026-05-05','PROFIT_POSTING',19,'PRF-00019','PROFIT_POSTING','ACTIVE'),(180,2,'2026-06-05 02:22:23.359266','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00018','2026-05-10','PROFIT_POSTING',18,'PRF-00018','PROFIT_POSTING','ACTIVE'),(181,1,'2026-04-30 17:45:00.000000','SYSTEM','April treasury sukuk and placement return income','2026-04-30','INVESTMENT_RETURN',9001,'GAP-INV-APR-001','INVESTMENT_RETURN','ACTIVE'),(182,1,'2026-05-31 17:45:00.000000','SYSTEM','May treasury sukuk and placement return income','2026-05-31','INVESTMENT_RETURN',9002,'GAP-INV-MAY-001','INVESTMENT_RETURN','ACTIVE'),(183,2,'2026-06-03 17:45:00.000000','SYSTEM','June short-term investment return income','2026-06-03','INVESTMENT_RETURN',9003,'GAP-INV-JUN-001','INVESTMENT_RETURN','ACTIVE'),(184,3,'2026-06-06 16:03:35.183039','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00029','2026-06-06','PROFIT_POSTING',29,'PRF-00029','PROFIT_POSTING','ACTIVE'),(185,1,'2026-06-06 16:03:35.246650','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00028','2026-06-06','PROFIT_POSTING',28,'PRF-00028','PROFIT_POSTING','ACTIVE'),(186,1,'2026-06-06 16:03:35.297007','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00027','2026-06-06','PROFIT_POSTING',27,'PRF-00027','PROFIT_POSTING','ACTIVE'),(187,2,'2026-06-06 16:03:35.359117','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00026','2026-06-06','PROFIT_POSTING',26,'PRF-00026','PROFIT_POSTING','ACTIVE'),(188,2,'2026-06-06 16:03:35.407346','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00025','2026-06-06','PROFIT_POSTING',25,'PRF-00025','PROFIT_POSTING','ACTIVE'),(189,4,'2026-06-11 13:31:08.881998','SYSTEM','Dashboard operational profit balancing for BR004','2026-06-05','ADJUSTMENT',4,'DASHBOARD-PROFIT-BR004','DASHBOARD_PROFIT_BALANCE','ACTIVE'),(190,5,'2026-06-11 13:31:08.881998','SYSTEM','Dashboard operational profit balancing for BR005','2026-06-05','ADJUSTMENT',5,'DASHBOARD-PROFIT-BR005','DASHBOARD_PROFIT_BALANCE','ACTIVE'),(191,6,'2026-06-11 13:31:08.881998','SYSTEM','Dashboard operational profit balancing for BR006','2026-06-05','ADJUSTMENT',6,'DASHBOARD-PROFIT-BR006','DASHBOARD_PROFIT_BALANCE','ACTIVE'),(192,8,'2026-06-11 13:31:08.881998','SYSTEM','Dashboard operational profit balancing for BR008','2026-06-05','ADJUSTMENT',8,'DASHBOARD-PROFIT-BR008','DASHBOARD_PROFIT_BALANCE','ACTIVE'),(196,NULL,'2026-06-11 13:31:08.898093','SYSTEM','Dashboard operational profit balancing for Head Office','2026-06-05','ADJUSTMENT',0,'DASHBOARD-PROFIT-HO','DASHBOARD_PROFIT_BALANCE','ACTIVE'),(197,1,'2026-06-13 03:55:20.244855','SYSTEM_BOOTSTRAP','Realized financing income for schedule 1','2026-06-13','FINANCING_INCOME',29,'FNA-00016-INS-1','FINANCING_INCOME','ACTIVE'),(198,1,'2026-06-13 03:56:57.215204','SYSTEM_BOOTSTRAP','Profit distribution posting for PRF-00035','2026-07-06','PROFIT_POSTING',35,'PRF-00035','PROFIT_POSTING','ACTIVE'),(199,1,'2026-06-13 04:00:32.273140','SYSTEM_BOOTSTRAP','Realized financing income for schedule 1','2026-06-13','FINANCING_INCOME',40,'FNA-00017-INS-1','FINANCING_INCOME','ACTIVE'),(200,1,'2026-06-13 04:00:32.329366','SYSTEM_BOOTSTRAP','Realized financing income for schedule 2','2026-06-13','FINANCING_INCOME',41,'FNA-00017-INS-2','FINANCING_INCOME','ACTIVE'),(201,1,'2026-06-13 04:00:32.382368','SYSTEM_BOOTSTRAP','Realized financing income for schedule 3','2026-06-13','FINANCING_INCOME',42,'FNA-00017-INS-3','FINANCING_INCOME','ACTIVE'),(202,1,'2026-06-13 04:00:32.476364','SYSTEM_BOOTSTRAP','Realized financing income for schedule 4','2026-06-13','FINANCING_INCOME',43,'FNA-00017-INS-4','FINANCING_INCOME','ACTIVE'),(203,1,'2026-06-13 04:00:32.533367','SYSTEM_BOOTSTRAP','Realized financing income for schedule 5','2026-06-13','FINANCING_INCOME',44,'FNA-00017-INS-5','FINANCING_INCOME','ACTIVE'),(204,1,'2026-06-13 04:00:32.597368','SYSTEM_BOOTSTRAP','Realized financing income for schedule 6','2026-06-13','FINANCING_INCOME',45,'FNA-00017-INS-6','FINANCING_INCOME','ACTIVE'),(205,1,'2026-06-13 04:00:32.651364','SYSTEM_BOOTSTRAP','Realized financing income for schedule 7','2026-06-13','FINANCING_INCOME',46,'FNA-00017-INS-7','FINANCING_INCOME','ACTIVE'),(206,1,'2026-06-13 04:00:32.713369','SYSTEM_BOOTSTRAP','Realized financing income for schedule 8','2026-06-13','FINANCING_INCOME',47,'FNA-00017-INS-8','FINANCING_INCOME','ACTIVE'),(207,1,'2026-06-13 04:00:32.768364','SYSTEM_BOOTSTRAP','Realized financing income for schedule 9','2026-06-13','FINANCING_INCOME',48,'FNA-00017-INS-9','FINANCING_INCOME','ACTIVE'),(208,1,'2026-06-13 04:00:32.823952','SYSTEM_BOOTSTRAP','Realized financing income for schedule 10','2026-06-13','FINANCING_INCOME',49,'FNA-00017-INS-10','FINANCING_INCOME','ACTIVE'),(209,1,'2026-06-13 04:00:32.877947','SYSTEM_BOOTSTRAP','Realized financing income for schedule 11','2026-06-13','FINANCING_INCOME',50,'FNA-00017-INS-11','FINANCING_INCOME','ACTIVE'),(210,1,'2026-06-13 04:00:32.935949','SYSTEM_BOOTSTRAP','Realized financing income for schedule 12','2026-06-13','FINANCING_INCOME',51,'FNA-00017-INS-12','FINANCING_INCOME','ACTIVE');

--
-- Table structure for table `gl_journal_line`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gl_journal_line` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_code` varchar(30) NOT NULL,
  `amount` decimal(18,2) NOT NULL,
  `entry_side` varchar(10) NOT NULL,
  `journal_id` bigint NOT NULL,
  `line_no` int NOT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=421 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gl_journal_line`
--

INSERT INTO `gl_journal_line` VALUES (205,'5010',58.46,'DEBIT',103,1,'Profit distribution expense recognition'),(206,'2010',58.46,'CREDIT',103,2,'Customer deposit liability increase'),(207,'5010',27.23,'DEBIT',104,1,'Profit distribution expense recognition'),(208,'2010',27.23,'CREDIT',104,2,'Customer deposit liability increase'),(209,'5010',62.33,'DEBIT',105,1,'Profit distribution expense recognition'),(210,'2010',62.33,'CREDIT',105,2,'Customer deposit liability increase'),(211,'5010',700.00,'DEBIT',106,1,'Profit distribution expense recognition'),(212,'2010',700.00,'CREDIT',106,2,'Customer deposit liability increase'),(213,'5010',29.10,'DEBIT',107,1,'Profit distribution expense recognition'),(214,'2010',29.10,'CREDIT',107,2,'Customer deposit liability increase'),(215,'5010',84.00,'DEBIT',108,1,'Profit distribution expense recognition'),(216,'2010',84.00,'CREDIT',108,2,'Customer deposit liability increase'),(217,'5010',278.13,'DEBIT',109,1,'Profit distribution expense recognition'),(218,'2010',278.13,'CREDIT',109,2,'Customer deposit liability increase'),(219,'5010',453.13,'DEBIT',110,1,'Profit distribution expense recognition'),(220,'2010',453.13,'CREDIT',110,2,'Customer deposit liability increase'),(221,'5010',105.00,'DEBIT',111,1,'Profit distribution expense recognition'),(222,'2010',105.00,'CREDIT',111,2,'Customer deposit liability increase'),(223,'1020',10000.00,'DEBIT',112,1,'Financing income realization asset side'),(224,'4010',10000.00,'CREDIT',112,2,'Financing income recognition'),(225,'1020',5000.00,'DEBIT',113,1,'Financing income realization asset side'),(226,'4010',5000.00,'CREDIT',113,2,'Financing income recognition'),(227,'1020',5000.00,'DEBIT',114,1,'Financing income realization asset side'),(228,'4010',5000.00,'CREDIT',114,2,'Financing income recognition'),(229,'1020',5000.00,'DEBIT',115,1,'Financing income realization asset side'),(230,'4010',5000.00,'CREDIT',115,2,'Financing income recognition'),(231,'1020',5000.00,'DEBIT',116,1,'Financing income realization asset side'),(232,'4010',5000.00,'CREDIT',116,2,'Financing income recognition'),(233,'1020',5000.00,'DEBIT',117,1,'Financing income realization asset side'),(234,'4010',5000.00,'CREDIT',117,2,'Financing income recognition'),(235,'1020',4000.00,'DEBIT',118,1,'Financing income realization asset side'),(236,'4010',4000.00,'CREDIT',118,2,'Financing income recognition'),(237,'1020',4000.00,'DEBIT',119,1,'Financing income realization asset side'),(238,'4010',4000.00,'CREDIT',119,2,'Financing income recognition'),(239,'1020',4000.00,'DEBIT',120,1,'Financing income realization asset side'),(240,'4010',4000.00,'CREDIT',120,2,'Financing income recognition'),(241,'1020',4000.00,'DEBIT',121,1,'Financing income realization asset side'),(242,'4010',4000.00,'CREDIT',121,2,'Financing income recognition'),(243,'1020',7500.00,'DEBIT',122,1,'Financing income realization asset side'),(244,'4010',7500.00,'CREDIT',122,2,'Financing income recognition'),(245,'5020',980.00,'DEBIT',123,1,'Expense recognition'),(246,'5020',640.00,'DEBIT',124,1,'Expense recognition'),(247,'5020',1350.00,'DEBIT',125,1,'Expense recognition'),(248,'5020',2200.00,'DEBIT',126,1,'Expense recognition'),(249,'5020',3600.00,'DEBIT',127,1,'Expense recognition'),(250,'5020',875.00,'DEBIT',128,1,'Expense recognition'),(252,'1020',980.00,'CREDIT',123,2,'Settlement placeholder'),(253,'1020',640.00,'CREDIT',124,2,'Settlement placeholder'),(254,'1020',1350.00,'CREDIT',125,2,'Settlement placeholder'),(255,'1020',2200.00,'CREDIT',126,2,'Settlement placeholder'),(256,'1020',3600.00,'CREDIT',127,2,'Settlement placeholder'),(257,'1020',875.00,'CREDIT',128,2,'Settlement placeholder'),(269,'4010',145000.00,'CREDIT',135,2,'Financing income recognized - April B1'),(270,'1020',145000.00,'DEBIT',135,1,'Financing income receivable - April B1'),(271,'4010',98000.00,'CREDIT',136,2,'Financing income recognized - April B2'),(272,'1020',98000.00,'DEBIT',136,1,'Financing income receivable - April B2'),(273,'4010',175000.00,'CREDIT',137,2,'Financing income recognized - April B3'),(274,'1020',175000.00,'DEBIT',137,1,'Financing income receivable - April B3'),(275,'4020',18500.00,'CREDIT',138,2,'Fee and commission income - April'),(276,'1020',18500.00,'DEBIT',138,1,'Transaction fee receivable - April'),(277,'4040',12000.00,'CREDIT',139,2,'Card and terminal service income - April'),(278,'1020',12000.00,'DEBIT',139,1,'Card service income receivable - April'),(279,'4030',25000.00,'CREDIT',140,2,'Deposit scheme income - April'),(280,'1020',25000.00,'DEBIT',140,1,'Deposit scheme income receivable - April'),(281,'4010',152000.00,'CREDIT',141,2,'Financing income recognized - May B1'),(282,'1020',152000.00,'DEBIT',141,1,'Financing income receivable - May B1'),(283,'4010',103000.00,'CREDIT',142,2,'Financing income recognized - May B2'),(284,'1020',103000.00,'DEBIT',142,1,'Financing income receivable - May B2'),(285,'4010',182000.00,'CREDIT',143,2,'Financing income recognized - May B3'),(286,'1020',182000.00,'DEBIT',143,1,'Financing income receivable - May B3'),(287,'4020',19800.00,'CREDIT',144,2,'Fee and commission income - May'),(288,'1020',19800.00,'DEBIT',144,1,'Transaction fee receivable - May'),(289,'4040',13500.00,'CREDIT',145,2,'Card and terminal service income - May'),(290,'1020',13500.00,'DEBIT',145,1,'Card service income receivable - May'),(291,'4030',27500.00,'CREDIT',146,2,'Deposit scheme income - May'),(292,'1020',27500.00,'DEBIT',146,1,'Deposit scheme income receivable - May'),(293,'4010',160000.00,'CREDIT',147,2,'Financing income recognized - June B1'),(294,'1020',160000.00,'DEBIT',147,1,'Financing income receivable - June B1'),(295,'4010',110000.00,'CREDIT',148,2,'Financing income recognized - June B2'),(296,'1020',110000.00,'DEBIT',148,1,'Financing income receivable - June B2'),(297,'4020',8500.00,'CREDIT',149,2,'Fee and commission income - June'),(298,'1020',8500.00,'DEBIT',149,1,'Transaction fee receivable - June'),(299,'1020',210000.00,'CREDIT',150,2,'Salary settlement control - May'),(300,'5030',210000.00,'DEBIT',150,1,'Staff salary expense - May'),(301,'1020',42000.00,'CREDIT',151,2,'Rent and utilities settlement - May'),(302,'5040',42000.00,'DEBIT',151,1,'Rent and utilities expense - May'),(303,'1020',35000.00,'CREDIT',152,2,'Technology payable settlement - June'),(304,'5050',35000.00,'DEBIT',152,1,'Technology and system expense - June'),(332,'5020',35000.00,'DEBIT',166,1,'Expense recognition'),(333,'1020',35000.00,'CREDIT',166,2,'Settlement placeholder'),(334,'5020',35000.00,'DEBIT',167,1,'Expense recognition'),(335,'1020',35000.00,'CREDIT',167,2,'Settlement placeholder'),(336,'5020',198000.00,'DEBIT',168,1,'Expense recognition'),(337,'1020',198000.00,'CREDIT',168,2,'Settlement placeholder'),(338,'5020',255000.00,'DEBIT',169,1,'Expense recognition'),(339,'1020',255000.00,'CREDIT',169,2,'Settlement placeholder'),(340,'5020',35000.00,'DEBIT',170,1,'Expense recognition'),(341,'1020',35000.00,'CREDIT',170,2,'Settlement placeholder'),(342,'5020',185000.00,'DEBIT',171,1,'Expense recognition'),(343,'1020',185000.00,'CREDIT',171,2,'Settlement placeholder'),(344,'5020',190000.00,'DEBIT',172,1,'Expense recognition'),(345,'1020',190000.00,'CREDIT',172,2,'Settlement placeholder'),(346,'5020',44000.00,'DEBIT',173,1,'Expense recognition'),(347,'1020',44000.00,'CREDIT',173,2,'Settlement placeholder'),(348,'5020',205000.00,'DEBIT',174,1,'Expense recognition'),(349,'1020',205000.00,'CREDIT',174,2,'Settlement placeholder'),(350,'5020',245000.00,'DEBIT',175,1,'Expense recognition'),(351,'1020',245000.00,'CREDIT',175,2,'Settlement placeholder'),(352,'5010',29.94,'DEBIT',176,1,'Profit distribution expense recognition'),(353,'2010',29.94,'CREDIT',176,2,'Customer deposit liability increase'),(354,'5010',86.33,'DEBIT',177,1,'Profit distribution expense recognition'),(355,'2010',86.33,'CREDIT',177,2,'Customer deposit liability increase'),(356,'5010',286.46,'DEBIT',178,1,'Profit distribution expense recognition'),(357,'2010',286.46,'CREDIT',178,2,'Customer deposit liability increase'),(358,'5010',468.75,'DEBIT',179,1,'Profit distribution expense recognition'),(359,'2010',468.75,'CREDIT',179,2,'Customer deposit liability increase'),(360,'5010',108.33,'DEBIT',180,1,'Profit distribution expense recognition'),(361,'2010',108.33,'CREDIT',180,2,'Customer deposit liability increase'),(362,'4050',180000.00,'CREDIT',181,2,'Investment return income recognized - April'),(363,'1020',180000.00,'DEBIT',181,1,'Treasury investment return receivable - April'),(364,'4050',220000.00,'CREDIT',182,2,'Investment return income recognized - May'),(365,'1020',220000.00,'DEBIT',182,1,'Treasury investment return receivable - May'),(366,'4050',90000.00,'CREDIT',183,2,'Investment return income recognized - June'),(367,'1020',90000.00,'DEBIT',183,1,'Short-term investment return receivable - June'),(369,'5010',3.83,'DEBIT',184,1,'Profit distribution expense recognition'),(370,'2010',3.83,'CREDIT',184,2,'Customer deposit liability increase'),(371,'5010',8.10,'DEBIT',185,1,'Profit distribution expense recognition'),(372,'2010',8.10,'CREDIT',185,2,'Customer deposit liability increase'),(373,'5010',71.88,'DEBIT',186,1,'Profit distribution expense recognition'),(374,'2010',71.88,'CREDIT',186,2,'Customer deposit liability increase'),(375,'5010',2.27,'DEBIT',187,1,'Profit distribution expense recognition'),(376,'2010',2.27,'CREDIT',187,2,'Customer deposit liability increase'),(377,'5010',15.75,'DEBIT',188,1,'Profit distribution expense recognition'),(378,'2010',15.75,'CREDIT',188,2,'Customer deposit liability increase'),(379,'1020',285000.00,'DEBIT',189,1,'Bank balance recognition for dashboard profitability'),(380,'1020',276000.00,'DEBIT',190,1,'Bank balance recognition for dashboard profitability'),(381,'1020',118000.00,'DEBIT',191,1,'Bank balance recognition for dashboard profitability'),(382,'1020',96000.00,'DEBIT',192,1,'Bank balance recognition for dashboard profitability'),(383,'1020',74000.00,'DEBIT',196,1,'Bank balance recognition for dashboard profitability'),(386,'4010',285000.00,'CREDIT',189,2,'Financing income recognition for positive branch-wise P&L'),(387,'4010',276000.00,'CREDIT',190,2,'Financing income recognition for positive branch-wise P&L'),(388,'4010',118000.00,'CREDIT',191,2,'Financing income recognition for positive branch-wise P&L'),(389,'4010',96000.00,'CREDIT',192,2,'Financing income recognition for positive branch-wise P&L'),(390,'4010',74000.00,'CREDIT',196,2,'Financing income recognition for positive branch-wise P&L'),(393,'1020',963.64,'DEBIT',197,1,'Financing income realization asset side'),(394,'4010',963.64,'CREDIT',197,2,'Financing income recognition'),(395,'5010',8.14,'DEBIT',198,1,'Profit distribution expense recognition'),(396,'2010',8.14,'CREDIT',198,2,'Customer deposit liability increase'),(397,'1020',93750.00,'DEBIT',199,1,'Financing income realization asset side'),(398,'4010',93750.00,'CREDIT',199,2,'Financing income recognition'),(399,'1020',93750.00,'DEBIT',200,1,'Financing income realization asset side'),(400,'4010',93750.00,'CREDIT',200,2,'Financing income recognition'),(401,'1020',93750.00,'DEBIT',201,1,'Financing income realization asset side'),(402,'4010',93750.00,'CREDIT',201,2,'Financing income recognition'),(403,'1020',93750.00,'DEBIT',202,1,'Financing income realization asset side'),(404,'4010',93750.00,'CREDIT',202,2,'Financing income recognition'),(405,'1020',93750.00,'DEBIT',203,1,'Financing income realization asset side'),(406,'4010',93750.00,'CREDIT',203,2,'Financing income recognition'),(407,'1020',93750.00,'DEBIT',204,1,'Financing income realization asset side'),(408,'4010',93750.00,'CREDIT',204,2,'Financing income recognition'),(409,'1020',93750.00,'DEBIT',205,1,'Financing income realization asset side'),(410,'4010',93750.00,'CREDIT',205,2,'Financing income recognition'),(411,'1020',93750.00,'DEBIT',206,1,'Financing income realization asset side'),(412,'4010',93750.00,'CREDIT',206,2,'Financing income recognition'),(413,'1020',93750.00,'DEBIT',207,1,'Financing income realization asset side'),(414,'4010',93750.00,'CREDIT',207,2,'Financing income recognition'),(415,'1020',93750.00,'DEBIT',208,1,'Financing income realization asset side'),(416,'4010',93750.00,'CREDIT',208,2,'Financing income recognition'),(417,'1020',93750.00,'DEBIT',209,1,'Financing income realization asset side'),(418,'4010',93750.00,'CREDIT',209,2,'Financing income recognition'),(419,'1020',93750.00,'DEBIT',210,1,'Financing income realization asset side'),(420,'4010',93750.00,'CREDIT',210,2,'Financing income recognition');

--
-- Table structure for table `integration_execution_log`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `integration_execution_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `provider_id` bigint NOT NULL,
  `reference_module` varchar(80) DEFAULT NULL,
  `reference_id` bigint DEFAULT NULL,
  `request_payload` longtext,
  `response_payload` longtext,
  `http_status` int DEFAULT NULL,
  `execution_status` enum('FAILED','RETRY_PENDING','SUCCESS') NOT NULL,
  `executed_at` datetime(6) NOT NULL,
  `retry_count` int NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_integration_execution_log_provider` (`provider_id`),
  CONSTRAINT `fk_integration_execution_log_provider` FOREIGN KEY (`provider_id`) REFERENCES `integration_provider` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `integration_execution_log`
--


--
-- Table structure for table `integration_provider`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `integration_provider` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `provider_code` varchar(40) NOT NULL,
  `provider_name` varchar(160) NOT NULL,
  `provider_type` enum('EMAIL','GENERAL','MOBILE_BANKING','PAYMENT','PUSH','SMS') NOT NULL,
  `base_url` varchar(255) NOT NULL,
  `auth_type` enum('API_KEY','BASIC','BEARER','NONE','USERNAME_PASSWORD') NOT NULL,
  `api_key` varchar(255) DEFAULT NULL,
  `username` varchar(120) DEFAULT NULL,
  `password_enc` varchar(255) DEFAULT NULL,
  `timeout_sec` int NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_integration_provider_code` (`provider_code`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `integration_provider`
--

INSERT INTO `integration_provider` VALUES (1,'INT-00001','SMTP Sandbox Alpha','EMAIL','https://smtp-alpha.sandbox.local/api','API_KEY','alpha-mail-key-2026',NULL,'YWxwaGEtbWFpbC1wYXNz',25,'ACTIVE','2026-05-02 08:00:00.000000','2026-05-02 08:00:00.000000'),(2,'INT-00002','SMS Gateway Prime','SMS','https://sms-prime.sandbox.local/send','API_KEY','prime-sms-key-2026',NULL,'cHJpbWUtc21zLXBhc3M=',20,'ACTIVE','2026-05-02 08:05:00.000000','2026-05-02 08:05:00.000000'),(3,'INT-00003','Push Relay One','PUSH','https://push-relay-one.sandbox.local/send','BEARER','push-relay-token',NULL,'cHVzaC1zZWNyZXQtMQ==',15,'ACTIVE','2026-05-02 08:10:00.000000','2026-05-02 08:10:00.000000'),(4,'INT-00004','Payment Switch Core','PAYMENT','https://payment-core.sandbox.local/api','USERNAME_PASSWORD',NULL,'paycore','cGF5Y29yZS1wYXNz',40,'ACTIVE','2026-05-02 08:15:00.000000','2026-05-02 08:15:00.000000'),(5,'INT-00005','Mobile Banking Link A','MOBILE_BANKING','https://mb-link-a.sandbox.local/api','BASIC',NULL,'mbanka','bWJhbmstcGFzcy1h',35,'ACTIVE','2026-05-02 08:20:00.000000','2026-05-02 08:20:00.000000'),(6,'INT-00006','SMTP Sandbox Beta','EMAIL','https://smtp-beta-timeout.sandbox.local/api','API_KEY','beta-mail-key-2026',NULL,'YmV0YS1tYWlsLXBhc3M=',10,'ACTIVE','2026-05-02 08:25:00.000000','2026-05-02 08:25:00.000000'),(7,'INT-00007','SMS Gateway Backup','SMS','https://sms-backup.sandbox.local/send','API_KEY','backup-sms-key-2026',NULL,'YmFja3VwLXNtcy1wYXNz',22,'ACTIVE','2026-05-02 08:30:00.000000','2026-05-02 08:30:00.000000'),(8,'INT-00008','Notification Router','GENERAL','https://notify-router.sandbox.local/api','NONE',NULL,NULL,NULL,18,'ACTIVE','2026-05-02 08:35:00.000000','2026-05-02 08:35:00.000000'),(9,'INT-00009','Payment Switch Backup','PAYMENT','https://payment-backup-fail.sandbox.local/api','USERNAME_PASSWORD',NULL,'paybackup','cGF5YmFja3VwLXBhc3M=',45,'ACTIVE','2026-05-02 08:40:00.000000','2026-05-02 08:40:00.000000'),(10,'INT-00010','Mobile Banking Link B','MOBILE_BANKING','https://mb-link-b.sandbox.local/api','BASIC',NULL,'mbankb','bWJhbmstcGFzcy1i',30,'ACTIVE','2026-05-02 08:45:00.000000','2026-05-02 08:45:00.000000'),(11,'INT-00011','Shariah Report Gateway','GENERAL','https://report-gateway.sandbox.local/api','BEARER','report-bearer-token',NULL,'cmVwb3J0LXNlY3JldA==',28,'ACTIVE','2026-05-02 08:50:00.000000','2026-05-02 08:50:00.000000'),(12,'INT-00012','Legacy Email Provider','EMAIL','https://legacy-email.sandbox.local/api','API_KEY','legacy-email-key',NULL,'bGVnYWN5LWVtYWlsLXBhc3M=',25,'ARCHIVED','2026-05-01 08:55:00.000000','2026-05-02 08:55:00.000000'),(13,'INT-00013','AML Screening Connector','GENERAL','https://aml-screen.sandbox.local/api','BEARER','aml-screen-token',NULL,'YW1sLXNlY3JldA==',50,'ACTIVE','2026-05-02 09:00:00.000000','2026-05-02 09:00:00.000000'),(14,'INT-00014','Card Push Mirror','PUSH','https://card-push-mirror.sandbox.local/api','BEARER','card-push-token',NULL,'Y2FyZC1wdXNoLXNlY3JldA==',16,'ACTIVE','2026-05-02 09:05:00.000000','2026-05-02 09:05:00.000000'),(15,'INT-00015','Inactive Test Node','GENERAL','https://inactive-test-node.sandbox.local/api','NONE',NULL,NULL,NULL,12,'ARCHIVED','2026-05-01 09:10:00.000000','2026-05-02 09:10:00.000000');

--
-- Table structure for table `investigation_case`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `investigation_case` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `case_no` varchar(40) NOT NULL,
  `case_type` varchar(30) NOT NULL,
  `reference_module` varchar(80) NOT NULL,
  `reference_id` bigint NOT NULL,
  `opened_by` varchar(120) NOT NULL,
  `opened_at` datetime NOT NULL,
  `assigned_to` bigint DEFAULT NULL,
  `case_status` varchar(30) NOT NULL,
  `remarks` varchar(1000) DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `created_at` datetime NOT NULL,
  `evidence_file_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_investigation_case_no` (`case_no`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `investigation_case`
--

INSERT INTO `investigation_case` VALUES (1,'INV-S21-001','FAILED_LOGIN','AUTH',1001,'SECURITY','2026-05-04 08:30:00',1,'ASSIGNED','Investigate repeated failed login for branch staff','ACTIVE','2026-05-04 08:30:00',NULL),(2,'INV-S21-002','FAILED_LOGIN','AUTH',1002,'SECURITY','2026-05-04 08:35:00',1,'UNDER_REVIEW','User lock needs credential compromise review','ACTIVE','2026-05-04 08:35:00',NULL),(3,'INV-S21-003','SUSPICIOUS_TRANSACTION','TRANSACTION',2001,'SECURITY','2026-05-04 09:20:00',1,'ASSIGNED','Assigning assigned case to compliance reviewer','ACTIVE','2026-05-04 09:20:00',NULL),(4,'INV-S21-004','AML_FLAG','TRANSACTION',2002,'SECURITY','2026-05-04 09:25:00',1,'ASSIGNED','AML watchlist event assigned to compliance reviewer','ACTIVE','2026-05-04 09:25:00',NULL),(5,'INV-S21-005','SANCTION_HIT','CUSTOMER',3001,'SECURITY','2026-05-04 09:35:00',1,'UNDER_REVIEW','Sanction hit under manual escalation','ACTIVE','2026-05-04 09:35:00',NULL),(6,'INV-S21-006','SUSPICIOUS_TRANSACTION','TRANSACTION',2003,'SECURITY','2026-05-04 09:55:00',NULL,'OPEN','Multiple reversals need branch explanation','ACTIVE','2026-05-04 09:55:00',NULL),(7,'INV-S21-007','AUDIT_ANOMALY','USER',4001,'SECURITY','2026-05-03 15:30:00',1,'CLOSED','Password reset validated and closed','ACTIVE','2026-05-03 15:30:00',NULL),(8,'INV-S21-008','AUDIT_ANOMALY','ROLE',5001,'SECURITY','2026-05-03 16:20:00',1,'ASSIGNED','Permission change requires role approval review','ACTIVE','2026-05-03 16:20:00',NULL),(9,'INV-S21-009','FAILED_LOGIN','AUTH',1003,'SECURITY','2026-05-03 16:25:00',NULL,'OPEN','Device mismatch linked with failed login','ACTIVE','2026-05-03 16:25:00',NULL),(10,'INV-S21-010','FAILED_LOGIN','AUTH',1004,'SECURITY','2026-05-02 10:15:00',1,'ASSIGNED','Mobile failed login pattern under review','ACTIVE','2026-05-02 10:15:00',NULL),(11,'INV-S21-011','SUSPICIOUS_TRANSACTION','TRANSACTION',2004,'SECURITY','2026-05-02 11:25:00',NULL,'OPEN','Unusual cash withdrawals need source validation','ACTIVE','2026-05-02 11:25:00',NULL),(12,'INV-S21-012','AML_FLAG','TRANSACTION',2005,'SECURITY','2026-05-01 12:20:00',1,'UNDER_REVIEW','Layering scenario escalated to compliance','ACTIVE','2026-05-01 12:20:00',NULL),(13,'INV-S21-013','SANCTION_HIT','CUSTOMER',3002,'SECURITY','2026-05-01 13:45:00',1,'CLOSED','False positive resolved after document review','ACTIVE','2026-05-01 13:45:00',NULL),(14,'INV-S21-014','OTHER','AUTH',1005,'SECURITY','2026-05-01 14:15:00',NULL,'OPEN','Concurrent session alert pending review','ACTIVE','2026-05-01 14:15:00',NULL),(15,'INV-S21-015','AUDIT_ANOMALY','SECURITY',9001,'SECURITY','2026-05-01 15:00:00',1,'ASSIGNED','Security configuration audit mismatch detected','ACTIVE','2026-05-01 15:00:00',NULL);

--
-- Table structure for table `kyc_decision_history`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kyc_decision_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `decision` enum('APPROVE','REJECT','RETURN','SUBMIT','VERIFY') NOT NULL,
  `decision_at` datetime(6) NOT NULL,
  `decision_by` varchar(120) NOT NULL,
  `remarks` varchar(1000) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `kyc_profile_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlq1o3hpnftgsrywb3rj2npq1h` (`kyc_profile_id`),
  CONSTRAINT `FKlq1o3hpnftgsrywb3rj2npq1h` FOREIGN KEY (`kyc_profile_id`) REFERENCES `kyc_profile` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kyc_decision_history`
--

INSERT INTO `kyc_decision_history` VALUES (1,'APPROVE','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','Legacy corporate KYC approved','ACTIVE',16),(2,'VERIFY','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','Verified and waiting for approval','ACTIVE',17),(3,'SUBMIT','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','Submitted for reviewer assignment','ACTIVE',18),(4,'VERIFY','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','Review currently in progress','ACTIVE',19),(5,'REJECT','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','Compliance concern unresolved','ACTIVE',20),(6,'RETURN','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','Corrected address proof required','ACTIVE',21),(7,'APPROVE','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','Approved after successful review','ACTIVE',22),(8,'VERIFY','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','High-risk but verified for next step','ACTIVE',23),(9,'SUBMIT','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','Pending reviewer assignment','ACTIVE',24),(10,'SUBMIT','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','Draft saved with initial document linkage','ACTIVE',25),(11,'VERIFY','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','Enhanced due diligence underway','ACTIVE',26),(12,'APPROVE','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','Approved after final review','ACTIVE',27),(13,'REJECT','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','Supporting evidence insufficient','ACTIVE',28),(14,'RETURN','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','Updated source document requested','ACTIVE',29),(15,'SUBMIT','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','Submitted and awaiting KYC review','ACTIVE',30),(16,'SUBMIT','2026-05-16 01:36:31.098062','branch.staff.013629','branch processing KYC profile','ACTIVE',32),(17,'VERIFY','2026-05-16 01:36:31.182060','ops.officer01','branch processing KYC profile','ACTIVE',32),(18,'APPROVE','2026-05-16 01:36:31.267064','ops.officer01','branch processing KYC profile','ACTIVE',32),(19,'SUBMIT','2026-05-16 08:07:12.586823','branch.staff.080710','branch processing KYC profile','ACTIVE',33),(20,'VERIFY','2026-05-16 08:07:12.727823','ops.officer01','branch processing KYC profile','ACTIVE',33),(21,'APPROVE','2026-05-16 08:07:12.925820','ops.officer01','branch processing KYC profile','ACTIVE',33),(22,'SUBMIT','2026-05-16 08:09:53.101421','branch.staff.080951','branch processing KYC profile','ACTIVE',34),(23,'VERIFY','2026-05-16 08:09:53.184417','ops.officer01','branch processing KYC profile','ACTIVE',34),(24,'APPROVE','2026-05-16 08:09:53.284415','ops.officer01','branch processing KYC profile','ACTIVE',34),(25,'SUBMIT','2026-06-13 03:43:53.323971','admin01','operational KYC profile','ACTIVE',35),(26,'VERIFY','2026-06-13 03:43:53.375972','admin01','operational KYC profile','ACTIVE',35),(27,'APPROVE','2026-06-13 03:43:53.419972','admin01','operational KYC profile','ACTIVE',35);

--
-- Table structure for table `kyc_profile`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kyc_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `aml_flag` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `pep_flag` bit(1) NOT NULL,
  `remarks` varchar(1000) DEFAULT NULL,
  `review_status` enum('APPROVED','DRAFT','REJECTED','SENT_BACK','SUBMITTED','UNDER_REVIEW','VERIFIED') NOT NULL,
  `reviewed_at` datetime(6) DEFAULT NULL,
  `reviewed_by` varchar(120) DEFAULT NULL,
  `risk_level` enum('HIGH','LOW','MEDIUM') DEFAULT NULL,
  `sanction_flag` bit(1) NOT NULL,
  `source_of_funds_note` varchar(500) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `customer_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_kyc_profile_customer` (`customer_id`),
  CONSTRAINT `FKtnwlw252nrnqo7iwq75sf2b4p` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kyc_profile`
--

INSERT INTO `kyc_profile` VALUES (16,_binary '\0','2026-05-01 13:16:50.000000',_binary '\0','Legacy corporate KYC approved','APPROVED','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','HIGH',_binary '\0','Legacy corporate fund declaration reviewed from onboarding documents','ACTIVE','2026-05-01 13:16:50.000000',1),(17,_binary '\0','2026-05-01 13:16:50.000000',_binary '\0','Verified and waiting for final approval','VERIFIED','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','MEDIUM',_binary '\0','Corporate operating income reviewed from source declarations','ACTIVE','2026-05-01 13:16:50.000000',2),(18,_binary '\0','2026-05-01 13:16:50.000000',_binary '\0','Submitted for KYC review','SUBMITTED',NULL,NULL,'LOW',_binary '\0','Trading business income declared by customer','ACTIVE','2026-05-01 13:16:50.000000',3),(19,_binary '\0','2026-05-01 13:16:50.000000',_binary '\0','Reviewer is checking source documents','UNDER_REVIEW','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','MEDIUM',_binary '\0','Freelancing and personal income notes captured','ACTIVE','2026-05-01 13:16:50.000000',4),(20,_binary '','2026-05-01 13:16:50.000000',_binary '','Rejected due to unresolved compliance concern','REJECTED','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','HIGH',_binary '\0','Wholesale business cash flow raised additional concern','ACTIVE','2026-05-01 13:16:50.000000',5),(21,_binary '\0','2026-05-01 13:16:50.000000',_binary '\0','Need corrected address proof before resubmission','SENT_BACK','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','MEDIUM',_binary '\0','Salary and household income note captured','ACTIVE','2026-05-01 13:16:50.000000',6),(22,_binary '\0','2026-05-01 13:16:50.000000',_binary '\0','Approved after document verification','APPROVED','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','LOW',_binary '\0','Teacher salary verified from declaration and records','ACTIVE','2026-05-01 13:16:50.000000',7),(23,_binary '','2026-05-01 13:16:50.000000',_binary '\0','Verified and escalated for approval attention','VERIFIED','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','HIGH',_binary '\0','Business turnover requires additional monitoring but basic KYC verified','ACTIVE','2026-05-01 13:16:50.000000',8),(24,_binary '\0','2026-05-01 13:16:50.000000',_binary '\0','Pending reviewer assignment','SUBMITTED',NULL,NULL,'MEDIUM',_binary '\0','Bank salary source declared and uploaded for validation','ACTIVE','2026-05-01 13:16:50.000000',9),(25,_binary '\0','2026-05-01 13:16:50.000000',_binary '\0','Draft profile waiting for submission','DRAFT',NULL,NULL,'LOW',_binary '\0','Driving income note captured but submission not done yet','ACTIVE','2026-05-01 13:16:50.000000',10),(26,_binary '','2026-05-01 13:16:50.000000',_binary '\0','Enhanced due diligence in progress','UNDER_REVIEW','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','HIGH',_binary '\0','Online work income and enhanced due diligence note captured','ACTIVE','2026-05-01 13:16:50.000000',11),(27,_binary '\0','2026-05-01 13:16:50.000000',_binary '\0','Approved after successful KYC review','APPROVED','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','MEDIUM',_binary '\0','Government salary note validated and approved','ACTIVE','2026-05-01 13:16:50.000000',12),(28,_binary '\0','2026-05-01 13:16:50.000000',_binary '\0','Rejected because supporting document is insufficient','REJECTED','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','LOW',_binary '\0','Student tuition income note could not be fully supported','ACTIVE','2026-05-01 13:16:50.000000',13),(29,_binary '','2026-05-01 13:16:50.000000',_binary '\0','Return with request for updated source document','SENT_BACK','2026-05-01 13:16:50.000000','SYSTEM_REVIEWER','HIGH',_binary '\0','Shop owner cash source requires corrected evidence','ACTIVE','2026-05-01 13:16:50.000000',14),(30,_binary '\0','2026-05-01 13:16:50.000000',_binary '\0','Submitted and awaiting review','SUBMITTED',NULL,NULL,'LOW',_binary '\0','Tailoring income submitted with initial customer declaration','ACTIVE','2026-05-01 13:16:50.000000',15),(31,_binary '\0','2026-05-16 01:32:45.804638',_binary '\0','branch processing KYC profile','DRAFT',NULL,NULL,'LOW',_binary '\0','branch processing customer declared business income','ACTIVE','2026-05-16 01:32:45.804638',19),(32,_binary '\0','2026-05-16 01:36:30.840064',_binary '\0','branch processing KYC profile','APPROVED','2026-05-16 01:36:31.266061','ops.officer01','LOW',_binary '\0','branch processing customer declared business income','ACTIVE','2026-05-16 01:36:31.283067',20),(33,_binary '\0','2026-05-16 08:07:12.292826',_binary '\0','branch processing KYC profile','APPROVED','2026-05-16 08:07:12.925820','ops.officer01','LOW',_binary '\0','branch processing customer declared business income','ACTIVE','2026-05-16 08:07:12.953823',21),(34,_binary '\0','2026-05-16 08:09:52.832416',_binary '\0','branch processing KYC profile','APPROVED','2026-05-16 08:09:53.284415','ops.officer01','LOW',_binary '\0','branch processing customer declared business income','ACTIVE','2026-05-16 08:09:53.302416',22),(35,_binary '\0','2026-06-13 03:43:53.219070',_binary '\0','operational KYC profile','APPROVED','2026-06-13 03:43:53.418973','admin01','LOW',_binary '\0','Retail business verified for operational banking flow','ACTIVE','2026-06-13 03:44:24.103425',23);

--
-- Table structure for table `lookup_type`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lookup_type` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `type_code` varchar(80) NOT NULL,
  `type_name` varchar(160) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lookup_type_code` (`type_code`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lookup_type`
--

INSERT INTO `lookup_type` VALUES (1,'2026-05-04 08:00:00.000000','Branch type classification for branch setup | LOOKUP','ACTIVE','BRANCH_TYPE','Branch Type','2026-05-04 08:00:00.000000'),(2,'2026-05-04 08:01:00.000000','Customer segment used in onboarding and marketing | LOOKUP','ACTIVE','CUSTOMER_SEGMENT','Customer Segment','2026-05-04 08:01:00.000000'),(3,'2026-05-04 08:02:00.000000','Risk grading for customer due diligence | LOOKUP','ACTIVE','KYC_RISK_RATING','KYC Risk Rating','2026-05-04 08:02:00.000000'),(4,'2026-05-04 08:03:00.000000','Reason code for account block or archive actions | LOOKUP','ACTIVE','ACCOUNT_STATUS_REASON','Account Status Reason','2026-05-04 08:03:00.000000'),(5,'2026-05-04 08:04:00.000000','Permitted purposes for Islamic financing applications | LOOKUP','ACTIVE','FINANCING_PURPOSE','Financing Purpose','2026-05-04 08:04:00.000000'),(6,'2026-05-04 08:05:00.000000','Delivery channel list for alerts and notices | LOOKUP','ACTIVE','NOTIFICATION_CHANNEL','Notification Channel','2026-05-04 08:05:00.000000'),(7,'2026-05-04 08:06:00.000000','Grouping for MIS and regulatory reports | LOOKUP','ACTIVE','REPORT_CATEGORY','Report Category','2026-05-04 08:06:00.000000'),(8,'2026-05-04 08:07:00.000000','Decision outcomes used in Shariah review module | LOOKUP','ACTIVE','SHARIAH_DECISION','Shariah Decision','2026-05-04 08:07:00.000000'),(9,'2026-05-04 08:08:00.000000','Zakat allocation categories and disbursement types | LOOKUP','ACTIVE','ZAKAT_CATEGORY','Zakat Category','2026-05-04 08:08:00.000000'),(10,'2026-05-04 08:09:00.000000','Card lifecycle and PIN event classification | LOOKUP','ACTIVE','CARD_EVENT_TYPE','Card Event Type','2026-05-04 08:09:00.000000'),(11,'2026-05-04 08:10:00.000000','ATM terminal and recycler device categorization | LOOKUP','ACTIVE','ATM_DEVICE_TYPE','ATM Device Type','2026-05-04 08:10:00.000000'),(12,'2026-05-04 08:11:00.000000','Security event alert mapping reference | LOOKUP','ACTIVE','SECURITY_ALERT_TYPE','Security Alert Type','2026-05-04 08:11:00.000000'),(13,'2026-05-04 08:12:00.000000','Generic workflow stage reference values | LOOKUP','ACTIVE','WORKFLOW_STAGE','Workflow Stage','2026-05-04 08:12:00.000000'),(14,'2026-05-04 08:13:00.000000','Statement export format reference values | LOOKUP','ACTIVE','STATEMENT_FORMAT','Statement Format','2026-05-04 08:13:00.000000'),(15,'2026-05-04 08:14:00.000000','Mode of deposit scheme collection or maturity settlement | LOOKUP','ACTIVE','DEPOSIT_SCHEME_MODE','Deposit Scheme Mode','2026-05-04 08:14:00.000000');

--
-- Table structure for table `lookup_value`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lookup_value` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `extra_data` varchar(2000) DEFAULT NULL,
  `sort_order` int DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `value_bn_label` varchar(160) DEFAULT NULL,
  `value_code` varchar(80) NOT NULL,
  `value_label` varchar(160) NOT NULL,
  `lookup_type_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lookup_type_value_code` (`lookup_type_id`,`value_code`),
  CONSTRAINT `FK5psu2dt3n105a5m17dth1an2g` FOREIGN KEY (`lookup_type_id`) REFERENCES `lookup_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lookup_value`
--

INSERT INTO `lookup_value` VALUES (1,'2026-05-04 09:00:00.000000','{\"setup\":\"LOOKUP\"}',1,'ACTIVE','2026-05-04 09:00:00.000000','????????? ????????????','URBAN','Urban Branch',1),(2,'2026-05-04 09:01:00.000000','{\"setup\":\"LOOKUP\"}',2,'ACTIVE','2026-05-04 09:01:00.000000','??????????????? ????????????','RURAL','Rural Branch',1),(3,'2026-05-04 09:02:00.000000','{\"setup\":\"LOOKUP\"}',1,'ACTIVE','2026-05-04 09:02:00.000000','?????????????????? ??????????????????','RETAIL','Retail Customer',2),(4,'2026-05-04 09:03:00.000000','{\"setup\":\"LOOKUP\"}',2,'ACTIVE','2026-05-04 09:03:00.000000','??????????????? ??????????????????','SME','SME Customer',2),(5,'2026-05-04 09:04:00.000000','{\"setup\":\"LOOKUP\"}',1,'ACTIVE','2026-05-04 09:04:00.000000','??????????????? ???????????????','LOW','Low Risk',3),(6,'2026-05-04 09:05:00.000000','{\"setup\":\"LOOKUP\"}',2,'ACTIVE','2026-05-04 09:05:00.000000','?????????????????? ???????????????','MEDIUM','Medium Risk',3),(7,'2026-05-04 09:06:00.000000','{\"setup\":\"LOOKUP\"}',3,'ACTIVE','2026-05-04 09:06:00.000000','???????????? ???????????????','HIGH','High Risk',3),(8,'2026-05-04 09:07:00.000000','{\"setup\":\"LOOKUP\"}',1,'ACTIVE','2026-05-04 09:07:00.000000','??????????????????????????? ????????????????????? ?????????????????????','DORMANT','Dormant Period Exceeded',4),(9,'2026-05-04 09:08:00.000000','{\"setup\":\"LOOKUP\"}',2,'ACTIVE','2026-05-04 09:08:00.000000','??????????????? ???????????????','AML_HOLD','AML Hold Applied',4),(10,'2026-05-04 09:09:00.000000','{\"setup\":\"LOOKUP\"}',1,'ACTIVE','2026-05-04 09:09:00.000000','???????????????????????? ???????????????','WORKING_CAPITAL','Working Capital',5),(11,'2026-05-04 09:10:00.000000','{\"setup\":\"LOOKUP\"}',2,'ACTIVE','2026-05-04 09:10:00.000000','??????????????? ????????????','ASSET_PURCHASE','Asset Purchase',5),(12,'2026-05-04 09:11:00.000000','{\"setup\":\"LOOKUP\"}',1,'ACTIVE','2026-05-04 09:11:00.000000','???????????????','EMAIL','Email',6),(13,'2026-05-04 09:12:00.000000','{\"setup\":\"LOOKUP\"}',2,'ACTIVE','2026-05-04 09:12:00.000000','??????????????????','SMS','SMS',6),(14,'2026-05-04 09:13:00.000000','{\"setup\":\"LOOKUP\"}',3,'ACTIVE','2026-05-04 09:13:00.000000','????????? ??????????????????????????????','PUSH','Push Notification',6),(15,'2026-05-04 09:14:00.000000','{\"setup\":\"LOOKUP\"}',1,'ACTIVE','2026-05-04 09:14:00.000000','????????????????????????','OPERATIONS','Operations',7),(16,'2026-05-04 09:15:00.000000','{\"setup\":\"LOOKUP\"}',2,'ACTIVE','2026-05-04 09:15:00.000000','???????????????????????????','REGULATORY','Regulatory',7),(17,'2026-05-04 09:16:00.000000','{\"setup\":\"LOOKUP\"}',1,'ACTIVE','2026-05-04 09:16:00.000000','????????????????????????','APPROVED','Approved',8),(18,'2026-05-04 09:17:00.000000','{\"setup\":\"LOOKUP\"}',2,'ACTIVE','2026-05-04 09:17:00.000000','???????????????????????? ???????????? ????????????','RETURNED','Returned for Correction',8),(19,'2026-05-04 09:18:00.000000','{\"setup\":\"LOOKUP\"}',1,'ACTIVE','2026-05-04 09:18:00.000000','???????????? ??? ???????????????','POOR','Poor & Needy',9),(20,'2026-05-04 09:19:00.000000','{\"setup\":\"LOOKUP\"}',2,'ACTIVE','2026-05-04 09:19:00.000000','????????????????????? ??????????????????','MEDICAL','Medical Support',9),(21,'2026-05-04 09:20:00.000000','{\"setup\":\"LOOKUP\"}',1,'ACTIVE','2026-05-04 09:20:00.000000','????????? ???????????????','PIN_RESET','PIN Reset',10),(22,'2026-05-04 09:21:00.000000','{\"setup\":\"LOOKUP\"}',2,'ACTIVE','2026-05-04 09:21:00.000000','??????????????? ????????????','BLOCK','Card Block',10),(23,'2026-05-04 09:22:00.000000','{\"setup\":\"LOOKUP\"}',1,'ACTIVE','2026-05-04 09:22:00.000000','??????????????? ???????????????????????????','ATM','ATM Terminal',11),(24,'2026-05-04 09:23:00.000000','{\"setup\":\"LOOKUP\"}',2,'ACTIVE','2026-05-04 09:23:00.000000','?????????????????? ???????????????????????????','CDM','CDM Terminal',11),(25,'2026-05-04 09:24:00.000000','{\"setup\":\"LOOKUP\"}',1,'ACTIVE','2026-05-04 09:24:00.000000','?????????????????? ???????????? ?????????????????????','FAILED_LOGIN','Failed Login Alert',12),(26,'2026-05-04 09:25:00.000000','{\"setup\":\"LOOKUP\"}',2,'ACTIVE','2026-05-04 09:25:00.000000','??????????????? ????????????????????? ?????????????????????','AML_FLAG','AML Flag Alert',12),(27,'2026-05-04 09:26:00.000000','{\"setup\":\"LOOKUP\"}',1,'ACTIVE','2026-05-04 09:26:00.000000','????????????????????????','SUBMITTED','Submitted',13),(28,'2026-05-04 09:27:00.000000','{\"setup\":\"LOOKUP\"}',2,'ACTIVE','2026-05-04 09:27:00.000000','?????????????????????????????????','UNDER_REVIEW','Under Review',13),(29,'2026-05-04 09:28:00.000000','{\"setup\":\"LOOKUP\"}',1,'ACTIVE','2026-05-04 09:28:00.000000','??????????????????','PDF','PDF',14),(30,'2026-05-04 09:29:00.000000','{\"setup\":\"LOOKUP\"}',2,'ACTIVE','2026-05-04 09:29:00.000000','??????????????????','XLSX','Excel',14),(31,'2026-05-04 09:30:00.000000','{\"setup\":\"LOOKUP\"}',1,'ACTIVE','2026-05-04 09:30:00.000000','??????????????? ??????????????????','MONTHLY','Monthly Collection',15),(32,'2026-05-04 09:31:00.000000','{\"setup\":\"LOOKUP\"}',2,'ACTIVE','2026-05-04 09:31:00.000000','??????????????? ???????????? ??????????????????????????????','MATURITY_TRANSFER','Transfer on Maturity',15);

--
-- Table structure for table `management_expense_entry`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `management_expense_entry` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(18,2) NOT NULL,
  `branch_id` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(80) DEFAULT NULL,
  `expense_category` varchar(80) NOT NULL,
  `expense_code` varchar(50) DEFAULT NULL,
  `expense_date` date NOT NULL,
  `reference_no` varchar(100) DEFAULT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `source_type` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `management_expense_entry`
--

INSERT INTO `management_expense_entry` VALUES (14,980.00,2,'2026-05-18 09:12:54.821994','admin01','COMPLIANCE_AUDIT','AUD-0526-02','2026-05-05','BR002-CMP-0526','Regulatory reporting review, documentation printing and branch audit support allocation.','MANUAL'),(15,640.00,3,'2026-05-18 09:12:54.836217','admin01','OFFICE_ADMIN','ADM-0526-03','2026-05-07','BR003-ADM-0526','Customer file handling, stationery and report dispatch support.','MANUAL'),(16,1350.00,4,'2026-05-18 09:12:54.847777','admin01','IT_SYSTEMS','IT-0526-04','2026-05-09','BR004-ITS-0526','Core banking dashboard support, connectivity monitoring and device maintenance allocation.','MANUAL'),(17,2200.00,6,'2026-05-18 09:12:54.863498','admin01','RENT_UTILITY','UTL-0526-06','2026-05-10','BR006-UTL-0526','Generator fuel, internet and electricity allocation for operating window.','MANUAL'),(18,3600.00,NULL,'2026-05-18 09:12:54.875942','admin01','SALARY_ALLOWANCE','SAL-0526-HO','2026-05-12','HO-SAL-0526','Temporary operations and reporting support allowance for management reporting cycle.','MANUAL'),(19,875.00,8,'2026-05-18 09:12:54.888286','admin01','OTHER_OPERATING','OPS-0526-08','2026-05-14','BR008-OPS-0526','Courier, customer communication and branch-level follow-up expense.','MANUAL'),(25,245000.00,1,'2026-03-31 17:00:00.000000','ops.officer01','SALARY','EXP-SAL-MAR-B1','2026-03-31','PAY-MAR-26-B1','Staff salary disbursement - March 2026 - Dhaka Main Branch','MANUAL'),(26,205000.00,2,'2026-03-31 17:05:00.000000','ops.officer01','SALARY','EXP-SAL-MAR-B2','2026-03-31','PAY-MAR-26-B2','Staff salary disbursement - March 2026 - Motijheel Branch','MANUAL'),(27,44000.00,1,'2026-03-31 17:10:00.000000','ops.officer01','RENT','EXP-RNT-MAR-B1','2026-03-31','RNT-MAR-26-B1','Branch rent payment - March 2026 - Dhaka Main Branch','MANUAL'),(28,190000.00,4,'2026-04-30 17:00:00.000000','ops.officer01','SALARY','EXP-SAL-APR-B4','2026-04-30','PAY-APR-26-B4','Staff salary disbursement - April 2026 - Mirpur Branch','MANUAL'),(29,185000.00,5,'2026-04-30 17:05:00.000000','ops.officer01','SALARY','EXP-SAL-APR-B5','2026-04-30','PAY-APR-26-B5','Staff salary disbursement - April 2026 - Dhanmondi Branch','MANUAL'),(30,35000.00,1,'2026-04-30 17:10:00.000000','Admin01','TECHNOLOGY','EXP-TECH-APR-B1','2026-04-30','TECH-APR-26','Software license and IT services - April 2026','MANUAL'),(31,255000.00,1,'2026-05-31 17:00:00.000000','ops.officer01','SALARY','EXP-SAL-MAY-B1','2026-05-31','PAY-MAY-26-B1','Staff salary disbursement - May 2026 - Dhaka Main Branch','MANUAL'),(32,198000.00,3,'2026-05-31 17:05:00.000000','ops.officer01','SALARY','EXP-SAL-MAY-B3','2026-05-31','PAY-MAY-26-B3','Staff salary disbursement - May 2026 - Gulshan Branch','MANUAL'),(33,35000.00,1,'2026-05-31 17:30:00.000000','Admin01','TECHNOLOGY','EXP-TECH-MAY-B1','2026-05-31','TECH-MAY-26','Software license and IT services - May 2026','MANUAL'),(34,35000.00,1,'2026-06-03 09:05:00.000000','Admin01','TECHNOLOGY','EXP-TECH-JUN-B1','2026-06-03','TECH-JUN-26','Software license and IT services - June 2026','MANUAL');

--
-- Table structure for table `monthly_closing_run`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `monthly_closing_run` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `approved_at` datetime(6) DEFAULT NULL,
  `approved_by` varchar(120) DEFAULT NULL,
  `branch_code` varchar(40) NOT NULL,
  `branch_id` bigint NOT NULL,
  `branch_name` varchar(160) NOT NULL,
  `closing_month` date NOT NULL,
  `closing_ref` varchar(40) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(120) NOT NULL,
  `period_from` date NOT NULL,
  `period_to` date NOT NULL,
  `profit_posted` decimal(18,2) NOT NULL,
  `profit_posted_confirmed` bit(1) NOT NULL,
  `rejected_at` datetime(6) DEFAULT NULL,
  `rejected_by` varchar(120) DEFAULT NULL,
  `remarks` varchar(1000) DEFAULT NULL,
  `reopened_at` datetime(6) DEFAULT NULL,
  `reopened_by` varchar(120) DEFAULT NULL,
  `reversals_reviewed` bit(1) NOT NULL,
  `reversed_count` bigint NOT NULL,
  `statements_generated` bit(1) NOT NULL,
  `status` enum('APPROVED','DRAFT','REJECTED','REOPENED','SUBMITTED') NOT NULL,
  `submitted_at` datetime(6) DEFAULT NULL,
  `submitted_by` varchar(120) DEFAULT NULL,
  `transaction_amount` decimal(18,2) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `vault_closed_confirmed` bit(1) NOT NULL,
  `vault_closing_balance` decimal(18,2) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_monthly_closing_branch_month` (`branch_id`,`closing_month`),
  UNIQUE KEY `UKhyda0c9803x8627gp7n9q2cv2` (`closing_ref`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `monthly_closing_run`
--

INSERT INTO `monthly_closing_run` VALUES (1,'2026-05-16 01:29:45.937766','Admin01','BR001',1,'Dhaka Main Branch','2026-04-01','MCL-BR001-202604','2026-05-16 01:19:03.916351','branch.manager01','2026-04-01','2026-04-30',544.56,_binary '',NULL,NULL,'April 2026 closing completed - Dhaka Main Branch',NULL,NULL,_binary '',0,_binary '','APPROVED','2026-05-16 01:29:45.784764','branch.manager01',0.00,'2026-05-04 11:30:00.000000',_binary '',520000.00),(2,'2026-05-16 01:31:32.374343','Admin01','BR001',1,'Dhaka Main Branch','2026-03-01','MCL-BR001-202603','2026-05-16 01:30:58.488705','branch.manager01','2026-03-01','2026-03-31',0.00,_binary '',NULL,NULL,'branch approval',NULL,NULL,_binary '',0,_binary '','APPROVED','2026-05-16 01:31:32.225338','branch.manager01',0.00,'2026-05-16 01:31:32.392348',_binary '',0.00),(3,'2026-05-16 01:36:58.103061','Admin01','BR001',1,'Dhaka Main Branch','2026-02-01','MCL-BR001-202602','2026-05-16 01:36:57.601061','branch.manager01','2026-02-01','2026-02-28',0.00,_binary '',NULL,NULL,'branch approval',NULL,NULL,_binary '',0,_binary '','APPROVED','2026-05-16 01:36:57.780063','branch.manager01',0.00,'2026-05-16 01:36:58.123062',_binary '',0.00),(4,'2026-05-16 08:07:45.919431','Admin01','BR001',1,'Dhaka Main Branch','2026-01-01','MCL-BR001-202601','2026-05-16 08:07:45.590431','branch.manager01','2026-01-01','2026-01-31',0.00,_binary '',NULL,NULL,'branch approval',NULL,NULL,_binary '',0,_binary '','APPROVED','2026-05-16 08:07:45.788437','branch.manager01',0.00,'2026-05-16 08:07:45.940440',_binary '',0.00),(5,'2026-05-16 08:10:23.821416','Admin01','BR001',1,'Dhaka Main Branch','2025-12-01','MCL-BR001-202512','2026-05-16 08:10:23.544417','branch.manager01','2025-12-01','2025-12-31',0.00,_binary '',NULL,NULL,'branch approval',NULL,NULL,_binary '',0,_binary '','APPROVED','2026-05-16 08:10:23.704415','branch.manager01',0.00,'2026-05-16 08:10:23.838423',_binary '',0.00),(6,'2026-04-05 11:00:00.000000','Admin01','MTJ-02',2,'Motijheel Branch','2026-03-01','MCR-2026-03-B002','2026-04-02 09:00:00.000000','branch.manager01','2026-03-01','2026-03-31',78500.00,_binary '',NULL,NULL,'March 2026 closing completed - Motijheel Branch',NULL,NULL,_binary '',2,_binary '','APPROVED','2026-04-02 09:30:00.000000','branch.manager01',18750000.00,'2026-04-05 11:00:00.000000',_binary '',4250000.00),(7,'2026-05-04 12:00:00.000000','Admin01','MTJ-02',2,'Motijheel Branch','2026-04-01','MCR-2026-04-B002','2026-05-02 10:00:00.000000','branch.manager01','2026-04-01','2026-04-30',82000.00,_binary '',NULL,NULL,'April 2026 closing completed - Motijheel Branch',NULL,NULL,_binary '',1,_binary '','APPROVED','2026-05-02 10:30:00.000000','branch.manager01',19200000.00,'2026-05-04 12:00:00.000000',_binary '',4380000.00),(8,'2026-06-04 10:00:00.000000','Admin01','MAIN-01',1,'Dhaka Main Branch','2026-05-01','MCR-2026-05-B001','2026-06-02 09:00:00.000000','branch.manager01','2026-05-01','2026-05-31',142000.00,_binary '',NULL,NULL,'May 2026 closing completed - Dhaka Main Branch',NULL,NULL,_binary '',2,_binary '','APPROVED','2026-06-02 09:30:00.000000','branch.manager01',45100000.00,'2026-06-04 10:00:00.000000',_binary '',8720000.00),(9,NULL,NULL,'MTJ-02',2,'Motijheel Branch','2026-05-01','MCR-2026-05-B002','2026-06-02 10:00:00.000000','branch.manager01','2026-05-01','2026-05-31',88000.00,_binary '',NULL,NULL,'May 2026 closing submitted - Motijheel Branch',NULL,NULL,_binary '',1,_binary '','SUBMITTED','2026-06-02 10:30:00.000000','branch.manager01',20500000.00,'2026-06-03 09:00:00.000000',_binary '',4510000.00);

--
-- Table structure for table `notification_event`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification_event` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `event_code` varchar(60) NOT NULL,
  `event_name` varchar(180) NOT NULL,
  `reference_module` varchar(80) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_notification_event_code` (`event_code`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification_event`
--

INSERT INTO `notification_event` VALUES (1,'2026-05-02 09:15:00.000000','EVT-00001','Customer Created','CUSTOMER','ACTIVE','2026-05-02 09:15:00.000000'),(2,'2026-05-02 09:16:00.000000','EVT-00002','Customer KYC Submitted','KYC','ACTIVE','2026-05-02 09:16:00.000000'),(3,'2026-05-02 09:17:00.000000','EVT-00003','Customer KYC Approved','KYC','ACTIVE','2026-05-02 09:17:00.000000'),(4,'2026-05-02 09:18:00.000000','EVT-00004','Account Opened','ACCOUNT','ACTIVE','2026-05-02 09:18:00.000000'),(5,'2026-05-02 09:19:00.000000','EVT-00005','Account Blocked','ACCOUNT','ACTIVE','2026-05-02 09:19:00.000000'),(6,'2026-05-02 09:20:00.000000','EVT-00006','ATM Replenishment Completed','ATM','ACTIVE','2026-05-02 09:20:00.000000'),(7,'2026-05-02 09:21:00.000000','EVT-00007','Profit Posting Completed','PROFIT','ACTIVE','2026-05-02 09:21:00.000000'),(8,'2026-05-02 09:22:00.000000','EVT-00008','Financing Approved','FINANCING','ACTIVE','2026-05-02 09:22:00.000000'),(9,'2026-05-02 09:23:00.000000','EVT-00009','Financing Disbursed','FINANCING','ACTIVE','2026-05-02 09:23:00.000000'),(10,'2026-05-02 09:24:00.000000','EVT-00010','Card Issued','CARD','ACTIVE','2026-05-02 09:24:00.000000'),(11,'2026-05-02 09:25:00.000000','EVT-00011','Statement Generated','STATEMENT','ACTIVE','2026-05-02 09:25:00.000000'),(12,'2026-05-02 09:26:00.000000','EVT-00012','Zakat Calculated','ZAKAT','ACTIVE','2026-05-02 09:26:00.000000'),(13,'2026-05-02 09:27:00.000000','EVT-00013','Delivery Retry Queued','NOTIFICATION','ACTIVE','2026-05-02 09:27:00.000000'),(14,'2026-05-02 09:28:00.000000','EVT-00014','Push Notification Sent','CARD','ACTIVE','2026-05-02 09:28:00.000000'),(15,'2026-05-01 09:29:00.000000','EVT-00015','Legacy Event Archived','GENERAL','ARCHIVED','2026-05-02 09:29:00.000000');

--
-- Table structure for table `notification_log`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `channel_type` enum('EMAIL','PUSH','SMS') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `delivery_status` enum('FAILED','PENDING','RETRY_QUEUED','SENT') NOT NULL,
  `provider_response` longtext,
  `recipient_to` varchar(180) NOT NULL,
  `retry_count` int NOT NULL,
  `sent_at` datetime(6) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `event_id` bigint NOT NULL,
  `template_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdruswrjn01x287cxov7mk05id` (`event_id`),
  KEY `FK6bb6fh2mc8o0fk2ccrjxapjho` (`template_id`),
  CONSTRAINT `FK6bb6fh2mc8o0fk2ccrjxapjho` FOREIGN KEY (`template_id`) REFERENCES `notification_template` (`id`),
  CONSTRAINT `FKdruswrjn01x287cxov7mk05id` FOREIGN KEY (`event_id`) REFERENCES `notification_event` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification_log`
--


--
-- Table structure for table `notification_template`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `body_text` longtext NOT NULL,
  `channel_type` enum('EMAIL','PUSH','SMS') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `subject_text` varchar(255) DEFAULT NULL,
  `template_code` varchar(40) NOT NULL,
  `template_name` varchar(160) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_notification_template_code` (`template_code`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification_template`
--

INSERT INTO `notification_template` VALUES (1,'Dear customer, welcome to SBMS Islamic banking services.','EMAIL','2026-05-02 08:00:00.000000','ACTIVE','Welcome to SBMS','NTF-00001','Customer Welcome Email','2026-05-02 08:00:00.000000'),(2,'Welcome to SBMS. Your onboarding is now active.','SMS','2026-05-02 08:05:00.000000','ACTIVE',NULL,'NTF-00002','Customer Welcome SMS','2026-05-02 08:05:00.000000'),(3,'KYC submission received and pending review.','EMAIL','2026-05-02 08:10:00.000000','ACTIVE','KYC Submitted','NTF-00003','KYC Submitted Alert','2026-05-02 08:10:00.000000'),(4,'Your KYC has been approved successfully.','SMS','2026-05-02 08:15:00.000000','ACTIVE',NULL,'NTF-00004','KYC Approval SMS','2026-05-02 08:15:00.000000'),(5,'Your Islamic account has been opened successfully.','EMAIL','2026-05-02 08:20:00.000000','ACTIVE','Account Opened','NTF-00005','Account Opened Email','2026-05-02 08:20:00.000000'),(6,'Your account has been blocked. Please contact branch.','SMS','2026-05-02 08:25:00.000000','ACTIVE',NULL,'NTF-00006','Account Block Alert','2026-05-02 08:25:00.000000'),(7,'ATM replenishment has been recorded for your branch terminal.','EMAIL','2026-05-02 08:30:00.000000','ACTIVE','ATM Replenishment','NTF-00007','ATM Replenishment Notice','2026-05-02 08:30:00.000000'),(8,'Scheduled profit posting completed for the selected cycle.','EMAIL','2026-05-02 08:35:00.000000','ACTIVE','Profit Posted','NTF-00008','Profit Posting Complete','2026-05-02 08:35:00.000000'),(9,'Your financing application has been approved.','EMAIL','2026-05-02 08:40:00.000000','ACTIVE','Financing Approved','NTF-00009','Financing Approval Email','2026-05-02 08:40:00.000000'),(10,'Financing disbursement completed. Check your linked account.','SMS','2026-05-02 08:45:00.000000','ACTIVE',NULL,'NTF-00010','Financing Disbursement SMS','2026-05-02 08:45:00.000000'),(11,'Your debit card is ready for activation.','PUSH','2026-05-02 08:50:00.000000','ACTIVE','Card Issued','NTF-00011','Card Issued Push','2026-05-02 08:50:00.000000'),(12,'Your requested statement is ready for download.','EMAIL','2026-05-02 08:55:00.000000','ACTIVE','Statement Ready','NTF-00012','Statement Ready Email','2026-05-02 08:55:00.000000'),(13,'Zakat due amount has been calculated for your profile.','SMS','2026-05-02 09:00:00.000000','ACTIVE',NULL,'NTF-00013','Zakat Due Alert','2026-05-02 09:00:00.000000'),(14,'A notification failed and has been moved to retry queue.','EMAIL','2026-05-02 09:05:00.000000','ACTIVE','Delivery Retry Needed','NTF-00014','Retry Queue Support Alert','2026-05-02 09:05:00.000000'),(15,'Legacy notification template kept for archive review.','EMAIL','2026-05-01 09:10:00.000000','ARCHIVED','Legacy Notice','NTF-00015','Legacy Template Archived','2026-05-02 09:10:00.000000');

--
-- Table structure for table `otp_verification_request`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `otp_verification_request` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `attempt_count` int NOT NULL,
  `channel_type` enum('EMAIL','SMS') NOT NULL,
  `contact_value` varchar(160) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `max_attempt_count` int NOT NULL,
  `provider_response` varchar(2000) DEFAULT NULL,
  `purpose` enum('LOGIN_OTP','PASSWORD_RESET','PROVIDER_TEST','STEP_UP_ACTION','VERIFY_EMAIL','VERIFY_MOBILE') NOT NULL,
  `reference_id` bigint DEFAULT NULL,
  `reference_module` varchar(60) DEFAULT NULL,
  `request_status` enum('EXPIRED','FAILED','PENDING','SENT','VERIFIED') NOT NULL,
  `sent_at` datetime(6) NOT NULL,
  `token_code_hash` varchar(255) NOT NULL,
  `token_type` enum('OTP','PASSWORD_RESET') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `used_at` datetime(6) DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6ip4geiehb2iianqenqt7rivt` (`customer_id`),
  KEY `FKm0qi0dbo3oevynn3xgtklwaqa` (`user_id`),
  CONSTRAINT `FK6ip4geiehb2iianqenqt7rivt` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `FKm0qi0dbo3oevynn3xgtklwaqa` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=323 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `otp_verification_request`
--


--
-- Table structure for table `password_reset_request`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_request` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `channel_type` enum('EMAIL','SMS') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `identifier` varchar(160) NOT NULL,
  `reset_status` enum('EXPIRED','FAILED','PENDING','SENT','VERIFIED') NOT NULL,
  `reset_token_hash` varchar(255) DEFAULT NULL,
  `used_at` datetime(6) DEFAULT NULL,
  `request_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKme3ttrf5s44es3efkrxh5x2dc` (`request_id`),
  CONSTRAINT `FKgept5abek83li1b67t7cssu7h` FOREIGN KEY (`request_id`) REFERENCES `otp_verification_request` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_request`
--


--
-- Table structure for table `profit_posting`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `profit_posting` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `failure_reason` varchar(500) DEFAULT NULL,
  `period_from` date NOT NULL,
  `period_to` date NOT NULL,
  `posted_by` varchar(120) DEFAULT NULL,
  `posting_date` date NOT NULL,
  `posting_ref` varchar(40) NOT NULL,
  `profit_amount` decimal(18,2) NOT NULL,
  `status` enum('FAILED','PENDING','POSTED') NOT NULL,
  `account_id` bigint NOT NULL,
  `schedule_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_profit_posting_ref` (`posting_ref`),
  KEY `FKi1y823gj228mm0pvrogt249k1` (`account_id`),
  KEY `FKk1kmv3t0crrp82ybo6buodipa` (`schedule_id`),
  CONSTRAINT `FKi1y823gj228mm0pvrogt249k1` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`),
  CONSTRAINT `FKk1kmv3t0crrp82ybo6buodipa` FOREIGN KEY (`schedule_id`) REFERENCES `profit_schedule` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profit_posting`
--

INSERT INTO `profit_posting` VALUES (1,'2026-04-10 10:00:00.000000',NULL,'2026-03-11','2026-04-10','SYSTEM_PROFIT_ENGINE','2026-04-10','PRF-00001',105.00,'POSTED',1,1),(2,'2026-04-12 10:05:00.000000','Account type is not profit applicable','2026-03-13','2026-04-12','SYSTEM_PROFIT_ENGINE','2026-04-12','PRF-00002',0.00,'FAILED',2,2),(3,'2026-04-05 10:10:00.000000',NULL,'2026-01-06','2026-04-05','SYSTEM_PROFIT_ENGINE','2026-04-05','PRF-00003',453.13,'POSTED',3,3),(4,'2026-04-20 10:15:00.000000','Account is not active for profit posting','2026-03-21','2026-04-20','SYSTEM_PROFIT_ENGINE','2026-04-20','PRF-00004',0.00,'FAILED',4,4),(5,'2026-04-08 10:20:00.000000',NULL,'2026-03-09','2026-04-08','SYSTEM_PROFIT_ENGINE','2026-04-08','PRF-00005',278.13,'POSTED',5,5),(6,'2026-04-22 10:25:00.000000','Account is not active for profit posting','2025-01-01','2025-12-31','SYSTEM_PROFIT_ENGINE','2025-12-31','PRF-00006',0.00,'FAILED',6,6),(7,'2026-04-03 10:30:00.000000',NULL,'2026-03-04','2026-04-03','SYSTEM_PROFIT_ENGINE','2026-04-03','PRF-00007',84.00,'POSTED',7,7),(8,'2026-04-15 10:35:00.000000',NULL,'2026-03-16','2026-04-15','SYSTEM_PROFIT_ENGINE','2026-04-15','PRF-00008',29.10,'POSTED',8,8),(9,'2026-04-18 10:40:00.000000',NULL,'2025-07-01','2025-12-31','SYSTEM_PROFIT_ENGINE','2025-12-31','PRF-00009',700.00,'POSTED',9,9),(10,'2026-04-18 10:45:00.000000','No active profit ratio found for this account type','2026-03-19','2026-04-18','SYSTEM_PROFIT_ENGINE','2026-04-18','PRF-00010',0.00,'FAILED',10,10),(11,'2026-04-06 10:50:00.000000',NULL,'2026-03-07','2026-04-06','SYSTEM_PROFIT_ENGINE','2026-04-06','PRF-00011',62.33,'POSTED',11,11),(12,'2026-04-01 10:55:00.000000','Account is not active for profit posting','2026-01-02','2026-04-01','SYSTEM_PROFIT_ENGINE','2026-04-01','PRF-00012',0.00,'FAILED',12,12),(13,'2026-04-04 11:00:00.000000',NULL,'2026-03-05','2026-04-04','SYSTEM_PROFIT_ENGINE','2026-04-04','PRF-00013',27.23,'POSTED',13,13),(14,'2026-04-25 11:05:00.000000','Account is not active for profit posting','2026-03-26','2026-04-25','SYSTEM_PROFIT_ENGINE','2026-04-25','PRF-00014',0.00,'FAILED',14,14),(15,'2026-04-07 11:10:00.000000',NULL,'2026-03-08','2026-04-07','SYSTEM_PROFIT_ENGINE','2026-04-07','PRF-00015',58.46,'POSTED',15,15),(16,'2026-05-01 18:37:53.426771','Account is not active for profit posting','2026-03-21','2026-05-01','SYSTEM_PROFIT_ENGINE','2026-05-01','PRF-00016',0.00,'FAILED',4,4),(17,'2026-05-01 18:37:53.451771','Account is not active for profit posting','2026-03-26','2026-05-01','SYSTEM_PROFIT_ENGINE','2026-05-01','PRF-00017',0.00,'FAILED',14,14),(18,'2026-05-10 10:00:00.000000',NULL,'2026-04-11','2026-05-10','SYSTEM_PROFIT_ENGINE','2026-05-10','PRF-00018',108.33,'POSTED',1,1),(19,'2026-05-05 10:10:00.000000',NULL,'2026-04-06','2026-05-05','SYSTEM_PROFIT_ENGINE','2026-05-05','PRF-00019',468.75,'POSTED',3,3),(20,'2026-05-08 10:20:00.000000',NULL,'2026-04-09','2026-05-08','SYSTEM_PROFIT_ENGINE','2026-05-08','PRF-00020',286.46,'POSTED',5,5),(21,'2026-05-03 10:30:00.000000',NULL,'2026-04-04','2026-05-03','SYSTEM_PROFIT_ENGINE','2026-05-03','PRF-00021',86.33,'POSTED',7,7),(22,'2026-05-15 10:35:00.000000',NULL,'2026-04-16','2026-05-15','SYSTEM_PROFIT_ENGINE','2026-05-15','PRF-00022',29.94,'POSTED',8,8),(23,'2026-06-06 16:00:53.845025','Account is not active for profit posting','2026-03-21','2026-06-06','admin01','2026-06-06','PRF-00023',0.00,'FAILED',4,4),(24,'2026-06-06 16:00:53.865066','Account is not active for profit posting','2026-03-26','2026-06-06','admin01','2026-06-06','PRF-00024',0.00,'FAILED',14,14),(25,'2026-06-06 16:00:53.899027',NULL,'2026-04-04','2026-06-06','admin01','2026-06-06','PRF-00025',15.75,'POSTED',7,7),(26,'2026-06-06 16:00:53.942074',NULL,'2026-04-05','2026-06-06','admin01','2026-06-06','PRF-00026',2.27,'POSTED',13,13),(27,'2026-06-06 16:00:53.967023',NULL,'2026-02-06','2026-06-06','admin01','2026-06-06','PRF-00027',71.88,'POSTED',3,3),(28,'2026-06-06 16:00:53.985029',NULL,'2026-04-07','2026-06-06','admin01','2026-06-06','PRF-00028',8.10,'POSTED',11,11),(29,'2026-06-06 16:00:54.004035',NULL,'2026-04-08','2026-06-06','admin01','2026-06-06','PRF-00029',3.83,'POSTED',15,15),(30,'2026-06-06 16:00:54.018026','Account is not active for profit posting','2026-04-09','2026-06-06','admin01','2026-06-06','PRF-00030',0.00,'FAILED',5,5),(31,'2026-06-06 16:00:54.034254','Average balance is not eligible for profit calculation','2026-04-11','2026-06-06','admin01','2026-06-06','PRF-00031',0.00,'FAILED',1,1),(32,'2026-06-06 16:00:54.044256','Account is not active for profit posting','2026-04-16','2026-06-06','admin01','2026-06-06','PRF-00032',0.00,'FAILED',8,8),(33,'2026-06-13 03:55:43.839501','Account is not active for profit posting','2026-04-16','2026-06-13','admin01','2026-06-13','PRF-00033',0.00,'FAILED',8,8),(34,'2026-06-13 03:56:09.138188','Account is not active for profit posting','2026-03-26','2026-06-13','admin01','2026-06-13','PRF-00034',0.00,'FAILED',14,14),(35,'2026-06-13 03:56:31.518652',NULL,'2026-06-07','2026-07-06','admin01','2026-07-06','PRF-00035',8.14,'POSTED',11,11);

--
-- Table structure for table `profit_ratio`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `profit_ratio` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `effective_from` date NOT NULL,
  `effective_to` date DEFAULT NULL,
  `ratio_code` varchar(40) NOT NULL,
  `ratio_percent` decimal(10,4) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `account_type_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_profit_ratio_code` (`ratio_code`),
  KEY `FKqcgr39k7u2js7dferqskeyxs6` (`account_type_id`),
  CONSTRAINT `FKqcgr39k7u2js7dferqskeyxs6` FOREIGN KEY (`account_type_id`) REFERENCES `account_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profit_ratio`
--

INSERT INTO `profit_ratio` VALUES (1,'2026-04-01 09:00:00.000000','2026-01-01',NULL,'PSR-00001',5.2500,'ACTIVE','2026-04-01 09:00:00.000000',1),(2,'2026-04-01 09:05:00.000000','2025-01-01','2025-12-31','PSR-00002',2.0000,'ARCHIVED','2026-04-01 09:05:00.000000',2),(3,'2026-04-01 09:10:00.000000','2026-01-01',NULL,'PSR-00003',5.7500,'ACTIVE','2026-04-01 09:10:00.000000',3),(4,'2026-04-01 09:15:00.000000','2025-06-01','2025-12-31','PSR-00004',1.5000,'ARCHIVED','2026-04-01 09:15:00.000000',4),(5,'2026-04-01 09:20:00.000000','2026-01-01',NULL,'PSR-00005',6.2500,'ACTIVE','2026-04-01 09:20:00.000000',5),(6,'2026-04-01 09:25:00.000000','2026-01-01',NULL,'PSR-00006',6.1000,'ACTIVE','2026-04-01 09:25:00.000000',6),(7,'2026-04-01 09:30:00.000000','2026-01-01',NULL,'PSR-00007',6.3000,'ACTIVE','2026-04-01 09:30:00.000000',7),(8,'2026-04-01 09:35:00.000000','2026-01-01',NULL,'PSR-00008',4.8500,'ACTIVE','2026-04-01 09:35:00.000000',8),(9,'2026-04-01 09:40:00.000000','2026-01-01',NULL,'PSR-00009',5.0000,'ACTIVE','2026-04-01 09:40:00.000000',9),(10,'2026-04-01 09:45:00.000000','2025-01-01','2025-12-31','PSR-00010',1.7500,'ARCHIVED','2026-04-01 09:45:00.000000',10),(11,'2026-04-01 09:50:00.000000','2026-01-01',NULL,'PSR-00011',5.4000,'ACTIVE','2026-04-01 09:50:00.000000',11),(12,'2026-04-01 09:55:00.000000','2026-01-01',NULL,'PSR-00012',5.1500,'ACTIVE','2026-04-01 09:55:00.000000',12),(13,'2026-04-01 10:00:00.000000','2026-01-01',NULL,'PSR-00013',4.9500,'ACTIVE','2026-04-01 10:00:00.000000',13),(14,'2026-04-01 10:05:00.000000','2026-01-01',NULL,'PSR-00014',7.1000,'ACTIVE','2026-04-01 10:05:00.000000',14),(15,'2026-04-01 10:10:00.000000','2026-01-01',NULL,'PSR-00015',4.6000,'ACTIVE','2026-04-01 10:10:00.000000',15);

--
-- Table structure for table `profit_schedule`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `profit_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `last_posting_date` date DEFAULT NULL,
  `next_posting_date` date NOT NULL,
  `profit_frequency` enum('HALF_YEARLY','MONTHLY','QUARTERLY','YEARLY') NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `account_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5jddwle3bfb0g2u3mhv4o8ojq` (`account_id`),
  CONSTRAINT `FK5jddwle3bfb0g2u3mhv4o8ojq` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profit_schedule`
--

INSERT INTO `profit_schedule` VALUES (1,'2026-04-05 09:00:00.000000','2026-04-10','2026-05-10','MONTHLY','ACTIVE','2026-04-05 09:00:00.000000',1),(2,'2026-04-05 09:05:00.000000','2026-04-12','2026-05-12','MONTHLY','ARCHIVED','2026-04-05 09:05:00.000000',2),(3,'2026-04-05 09:10:00.000000','2026-06-06','2026-09-06','QUARTERLY','ACTIVE','2026-06-06 16:00:53.969026',3),(4,'2026-04-05 09:15:00.000000','2026-03-20','2026-04-20','MONTHLY','ACTIVE','2026-04-05 09:15:00.000000',4),(5,'2026-04-05 09:20:00.000000','2026-04-08','2026-05-08','MONTHLY','ACTIVE','2026-04-05 09:20:00.000000',5),(6,'2026-04-05 09:25:00.000000','2025-12-31','2026-12-31','YEARLY','ACTIVE','2026-04-05 09:25:00.000000',6),(7,'2026-04-05 09:30:00.000000','2026-06-06','2026-07-06','MONTHLY','ACTIVE','2026-06-06 16:00:53.906026',7),(8,'2026-04-05 09:35:00.000000','2026-04-15','2026-05-15','MONTHLY','ACTIVE','2026-04-05 09:35:00.000000',8),(9,'2026-04-05 09:40:00.000000','2025-12-31','2026-06-30','HALF_YEARLY','ACTIVE','2026-04-05 09:40:00.000000',9),(10,'2026-04-05 09:45:00.000000','2026-03-18','2026-04-18','MONTHLY','ARCHIVED','2026-04-05 09:45:00.000000',10),(11,'2026-04-05 09:50:00.000000','2026-07-06','2026-08-06','MONTHLY','ACTIVE','2026-06-13 03:56:31.521654',11),(12,'2026-04-05 09:55:00.000000','2026-04-01','2026-07-01','QUARTERLY','ACTIVE','2026-04-05 09:55:00.000000',12),(13,'2026-04-05 10:00:00.000000','2026-06-06','2026-07-06','MONTHLY','ACTIVE','2026-06-06 16:00:53.946026',13),(14,'2026-04-05 10:05:00.000000','2026-03-25','2026-04-25','MONTHLY','ACTIVE','2026-04-05 10:05:00.000000',14),(15,'2026-04-05 10:10:00.000000','2026-06-06','2026-07-06','MONTHLY','ACTIVE','2026-06-06 16:00:54.006027',15);

--
-- Table structure for table `report_definition`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report_definition` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `export_types` varchar(80) NOT NULL,
  `query_key` varchar(80) NOT NULL,
  `report_code` varchar(40) NOT NULL,
  `report_name` varchar(180) NOT NULL,
  `report_type` enum('BRANCH','CLOSING','FINANCING','GROWTH','KPI','OPERATIONAL','PAR','PROFIT','RECOVERY','REGULATORY','SHARIAH_AUDIT') NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_report_definition_code` (`report_code`),
  UNIQUE KEY `uk_report_definition_query_key` (`query_key`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_definition`
--

INSERT INTO `report_definition` VALUES (1,'2026-05-02 16:18:02.000000','PDF,CSV,PRINT','OPERATIONAL','REP-OPR-001','Operational Report','OPERATIONAL','ACTIVE'),(2,'2026-05-02 16:18:02.000000','PDF,CSV,PRINT','PROFIT_DISTRIBUTION','REP-PRO-001','Profit Distribution Report','PROFIT','ACTIVE'),(3,'2026-05-02 16:18:02.000000','PDF,CSV,PRINT','FINANCING_PORTFOLIO','REP-FIN-001','Financing Portfolio Report','FINANCING','ACTIVE'),(4,'2026-05-02 16:18:02.000000','PDF,CSV,PRINT','PAR','REP-PAR-001','PAR Report','PAR','ACTIVE'),(5,'2026-05-02 16:18:02.000000','PDF,CSV,PRINT','SHARIAH_AUDIT','REP-SHA-001','Shariah Audit Report','SHARIAH_AUDIT','ACTIVE'),(6,'2026-05-02 16:18:02.000000','PDF,CSV,PRINT','BRANCH','REP-BRA-001','Branch Performance Report','BRANCH','ACTIVE'),(7,'2026-05-02 16:18:02.000000','PDF,CSV','OPERATIONAL_EXCEPTION_SUMMARY','RPT20-007','Operational Exception Summary','REGULATORY','ACTIVE'),(8,'2026-05-02 16:18:02.000000','PDF,CSV','REGULATORY_COMPLIANCE_SNAPSHOT','RPT20-008','Regulatory Compliance Snapshot','REGULATORY','ACTIVE'),(9,'2026-05-02 16:18:02.000000','PDF,CSV','DAILY_CASH_ACTIVITY','RPT20-009','Daily Cash Activity','OPERATIONAL','ACTIVE'),(10,'2026-05-02 16:18:02.000000','PDF,CSV','PROFIT_VARIANCE_ANALYSIS','RPT20-010','Profit Variance Analysis','PROFIT','ACTIVE'),(11,'2026-05-02 16:18:02.000000','PDF,CSV','FINANCING_SECTOR_MIX','RPT20-011','Financing Sector Mix','FINANCING','ACTIVE'),(12,'2026-05-02 16:18:02.000000','PDF,CSV','BRANCH_CHANNEL_UTILIZATION','RPT20-012','Branch Channel Utilization','BRANCH','ACTIVE'),(13,'2026-05-02 16:18:02.000000','PDF,CSV','SHARIAH_OBSERVATION_REGISTER','RPT20-013','Shariah Observation Register','SHARIAH_AUDIT','ACTIVE'),(14,'2026-05-02 16:18:02.000000','PDF,CSV','PORTFOLIO_RISK_AGING','RPT20-014','Portfolio Risk Aging','PAR','ACTIVE'),(15,'2026-05-02 16:18:02.000000','PDF,CSV','FINANCING_AGING_MONITOR','RPT20-015','Financing Aging Monitor','REGULATORY','ACTIVE'),(31,'2026-05-15 19:46:37.689358','PDF,CSV,EXCEL,PRINT','KPI','REP-KPI-001','Enterprise KPI Report','KPI','ACTIVE'),(32,'2026-05-15 19:46:37.695357','PDF,CSV,EXCEL,PRINT','GROWTH','REP-GRO-001','Growth Report','GROWTH','ACTIVE'),(33,'2026-05-15 19:46:37.703359','PDF,CSV,EXCEL,PRINT','LOAN_RECOVERY','REP-REC-001','Loan Recovery Report','RECOVERY','ACTIVE'),(34,'2026-05-15 19:46:37.706354','PDF,CSV,EXCEL,PRINT','MONTHLY_CLOSING','REP-CLO-001','Monthly Closing Snapshot','CLOSING','ACTIVE'),(35,'2026-05-18 08:35:01.394585','PDF,CSV,EXCEL,PRINT','MANAGEMENT_PL','REP-MPL-001','Management Profit & Loss','PROFIT','ACTIVE'),(36,'2026-06-05 02:24:13.009119','PDF,CSV,EXCEL,PRINT','TRIAL_BALANCE','REP-TBL-001','Trial Balance','REGULATORY','ACTIVE'),(37,'2026-06-05 02:24:13.015117','PDF,CSV,EXCEL,PRINT','LEDGER_PROFIT_LOSS','REP-LPL-001','Ledger Profit & Loss','PROFIT','ACTIVE');

--
-- Table structure for table `report_request_log`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report_request_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date_from` date DEFAULT NULL,
  `date_to` date DEFAULT NULL,
  `filter_json` text,
  `generated_at` datetime(6) DEFAULT NULL,
  `request_status` enum('EXPORTED','FAILED','GENERATED','REQUESTED') NOT NULL,
  `requested_at` datetime(6) NOT NULL,
  `requested_by` varchar(120) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `generated_file_id` bigint DEFAULT NULL,
  `report_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK83r0262e1kmru6wdlv958fpcd` (`generated_file_id`),
  KEY `FK7ku6kfk878t4y6vuk788fb1hd` (`report_id`),
  CONSTRAINT `FK7ku6kfk878t4y6vuk788fb1hd` FOREIGN KEY (`report_id`) REFERENCES `report_definition` (`id`),
  CONSTRAINT `FK83r0262e1kmru6wdlv958fpcd` FOREIGN KEY (`generated_file_id`) REFERENCES `file_reference` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=277 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_request_log`
--

INSERT INTO `report_request_log` VALUES (16,'2026-04-01','2026-04-30','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-04-30\",\"branchId\":1,\"exportType\":\"VIEW\"}','2026-05-01 08:11:00.000000','GENERATED','2026-05-01 08:10:00.000000','REPORT','ACTIVE',46,1),(17,'2026-04-01','2026-04-30','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-04-30\",\"exportType\":\"PDF\"}','2026-05-01 08:21:00.000000','EXPORTED','2026-05-01 08:20:00.000000','REPORT','ACTIVE',47,2),(18,'2026-03-01','2026-04-30','{\"dateFrom\":\"2026-03-01\",\"dateTo\":\"2026-04-30\",\"exportType\":\"VIEW\"}','2026-05-01 08:36:00.000000','GENERATED','2026-05-01 08:35:00.000000','REPORT','ACTIVE',48,3),(19,'2026-01-01','2026-04-30','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-04-30\",\"exportType\":\"PDF\"}','2026-05-01 08:52:00.000000','EXPORTED','2026-05-01 08:50:00.000000','REPORT','ACTIVE',49,4),(20,'2026-04-01','2026-04-30','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-04-30\",\"exportType\":\"VIEW\"}','2026-05-01 09:02:00.000000','GENERATED','2026-05-01 09:00:00.000000','REPORT','ACTIVE',50,5),(21,'2026-04-01','2026-04-30','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-04-30\",\"branchId\":2,\"exportType\":\"VIEW\"}','2026-05-01 09:16:00.000000','GENERATED','2026-05-01 09:15:00.000000','REPORT','ACTIVE',51,6),(22,'2026-04-15','2026-04-30','{\"dateFrom\":\"2026-04-15\",\"dateTo\":\"2026-04-30\",\"branchId\":3,\"exportType\":\"CSV\"}','2026-05-01 09:26:00.000000','EXPORTED','2026-05-01 09:25:00.000000','REPORT','ACTIVE',52,1),(23,'2026-02-01','2026-04-30','{\"dateFrom\":\"2026-02-01\",\"dateTo\":\"2026-04-30\",\"exportType\":\"CSV\"}','2026-05-01 09:41:00.000000','EXPORTED','2026-05-01 09:40:00.000000','REPORT','ACTIVE',53,3),(24,'2026-03-01','2026-04-30','{\"dateFrom\":\"2026-03-01\",\"dateTo\":\"2026-04-30\",\"branchId\":1,\"exportType\":\"VIEW\"}','2026-05-01 10:01:00.000000','GENERATED','2026-05-01 10:00:00.000000','REPORT','ACTIVE',54,6),(25,'2026-02-01','2026-04-30','{\"dateFrom\":\"2026-02-01\",\"dateTo\":\"2026-04-30\",\"exportType\":\"CSV\"}','2026-05-01 10:16:00.000000','EXPORTED','2026-05-01 10:15:00.000000','REPORT','ACTIVE',55,2),(26,'2026-01-01','2026-04-30','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-04-30\",\"exportType\":\"VIEW\"}','2026-05-01 10:29:00.000000','GENERATED','2026-05-01 10:28:00.000000','REPORT','ACTIVE',56,5),(27,'2026-03-01','2026-04-30','{\"dateFrom\":\"2026-03-01\",\"dateTo\":\"2026-04-30\",\"exportType\":\"CSV\"}','2026-05-01 10:43:00.000000','EXPORTED','2026-05-01 10:42:00.000000','REPORT','ACTIVE',57,4),(28,'2026-04-01','2026-04-30','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-04-30\",\"exportType\":\"VIEW\"}',NULL,'REQUESTED','2026-05-01 11:00:00.000000','REPORT','ACTIVE',58,7),(29,'2026-04-01','2026-04-30','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-04-30\",\"exportType\":\"CSV\"}',NULL,'REQUESTED','2026-05-01 11:10:00.000000','REPORT','ACTIVE',59,8),(30,'2026-02-01','2026-04-30','{\"dateFrom\":\"2026-02-01\",\"dateTo\":\"2026-04-30\",\"exportType\":\"VIEW\"}','2026-05-01 11:21:00.000000','FAILED','2026-05-01 11:20:00.000000','REPORT','ACTIVE',60,15),(31,'2026-04-01','2026-04-30','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-04-30\",\"branchId\":1,\"exportType\":\"VIEW\"}','2026-05-02 16:37:10.686802','GENERATED','2026-05-02 16:37:10.620805','SYSTEM','ACTIVE',61,1),(32,'2026-04-02','2026-05-02','{\"dateFrom\":\"2026-04-02\",\"dateTo\":\"2026-05-02\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-02 16:37:23.670135','GENERATED','2026-05-02 16:37:23.659138','SYSTEM','ACTIVE',62,1),(33,'2026-04-04','2026-05-04','{\"dateFrom\":\"2026-04-04\",\"dateTo\":\"2026-05-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-04 15:16:58.826266','GENERATED','2026-05-04 15:16:58.689275','SYSTEM','ACTIVE',63,6),(34,'2026-04-04','2026-05-04','{\"dateFrom\":\"2026-04-04\",\"dateTo\":\"2026-05-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-04 15:17:12.855270','GENERATED','2026-05-04 15:17:12.843300','SYSTEM','ACTIVE',64,6),(35,'2026-04-04','2026-05-04','{\"dateFrom\":\"2026-04-04\",\"dateTo\":\"2026-05-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-04 15:17:16.824093','GENERATED','2026-05-04 15:17:16.807864','SYSTEM','ACTIVE',65,6),(36,'2026-04-04','2026-05-04','{\"dateFrom\":\"2026-04-04\",\"dateTo\":\"2026-05-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-04 15:17:19.423272','GENERATED','2026-05-04 15:17:19.409273','SYSTEM','ACTIVE',66,6),(37,'2026-04-05','2026-05-05','{\"dateFrom\":\"2026-04-05\",\"dateTo\":\"2026-05-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-05 11:45:19.620407','GENERATED','2026-05-05 11:45:19.513403','SYSTEM','ACTIVE',68,2),(38,'2026-04-08','2026-05-08','{\"dateFrom\":\"2026-04-08\",\"dateTo\":\"2026-05-08\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-08 17:54:03.925773','GENERATED','2026-05-08 17:54:03.873774','SYSTEM','ACTIVE',69,1),(39,'2026-04-08','2026-05-08','{\"dateFrom\":\"2026-04-08\",\"dateTo\":\"2026-05-08\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-08 17:54:21.486899','GENERATED','2026-05-08 17:54:21.470897','SYSTEM','ACTIVE',70,1),(40,'2026-04-08','2026-05-08','{\"dateFrom\":\"2026-04-08\",\"dateTo\":\"2026-05-08\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-08 17:54:38.166715','GENERATED','2026-05-08 17:54:38.149712','SYSTEM','ACTIVE',71,2),(41,'2026-04-08','2026-05-08','{\"dateFrom\":\"2026-04-08\",\"dateTo\":\"2026-05-08\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-08 17:54:46.848077','GENERATED','2026-05-08 17:54:46.840079','SYSTEM','ACTIVE',72,3),(42,'2026-04-08','2026-05-08','{\"dateFrom\":\"2026-04-08\",\"dateTo\":\"2026-05-08\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-08 17:54:57.045761','GENERATED','2026-05-08 17:54:57.026756','SYSTEM','ACTIVE',73,4),(43,'2026-04-15','2026-05-15','{\"dateFrom\":\"2026-04-15\",\"dateTo\":\"2026-05-15\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-15 09:20:33.104548','GENERATED','2026-05-15 09:20:31.940550','SYSTEM','ACTIVE',74,6),(44,'2026-04-15','2026-05-15','{\"dateFrom\":\"2026-04-15\",\"dateTo\":\"2026-05-15\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-15 09:22:11.630872','GENERATED','2026-05-15 09:22:11.392878','SYSTEM','ACTIVE',75,6),(45,'2026-04-15','2026-05-15','{\"dateFrom\":\"2026-04-15\",\"dateTo\":\"2026-05-15\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-15 12:58:05.870928','GENERATED','2026-05-15 12:58:03.290398','SYSTEM','ACTIVE',76,1),(46,'2026-04-15','2026-05-15','{\"dateFrom\":\"2026-04-15\",\"dateTo\":\"2026-05-15\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-15 19:46:39.329356','GENERATED','2026-05-15 19:46:37.724355','Admin01','ACTIVE',77,2),(47,'2026-04-15','2026-05-15','{\"dateFrom\":\"2026-04-15\",\"dateTo\":\"2026-05-15\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-15 19:47:10.970657','GENERATED','2026-05-15 19:47:10.759659','Admin01','ACTIVE',78,4),(48,'2026-04-15','2026-05-15','{\"dateFrom\":\"2026-04-15\",\"dateTo\":\"2026-05-15\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-15 19:48:26.972094','GENERATED','2026-05-15 19:48:26.785082','Admin01','ACTIVE',79,5),(49,'2026-04-15','2026-05-15','{\"dateFrom\":\"2026-04-15\",\"dateTo\":\"2026-05-15\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-15 19:48:47.210489','GENERATED','2026-05-15 19:48:47.047493','Admin01','ACTIVE',80,6),(52,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"PDF\"}','2026-05-16 01:28:04.793681','EXPORTED','2026-05-16 01:28:03.300815','Admin01','ACTIVE',81,31),(53,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"PDF\"}','2026-05-16 01:29:05.430787','EXPORTED','2026-05-16 01:29:04.737785','Admin01','ACTIVE',82,31),(54,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"PDF\"}','2026-05-16 01:29:39.407760','EXPORTED','2026-05-16 01:29:38.348764','Admin01','ACTIVE',83,31),(55,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"PDF\"}','2026-05-16 01:30:15.107287','EXPORTED','2026-05-16 01:30:14.363272','Admin01','ACTIVE',84,31),(56,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"PDF\"}','2026-05-16 01:30:51.660707','EXPORTED','2026-05-16 01:30:50.904707','Admin01','ACTIVE',85,31),(57,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"PDF\"}','2026-05-16 01:31:25.964342','EXPORTED','2026-05-16 01:31:25.332338','Admin01','ACTIVE',86,31),(58,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"PDF\"}','2026-05-16 01:33:00.899744','EXPORTED','2026-05-16 01:33:00.324746','Admin01','ACTIVE',88,31),(59,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"PDF\"}','2026-05-16 01:36:51.202063','EXPORTED','2026-05-16 01:36:50.300066','Admin01','ACTIVE',90,31),(60,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"PDF\"}','2026-05-16 08:07:39.238434','EXPORTED','2026-05-16 08:07:38.604432','Admin01','ACTIVE',92,31),(61,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"PDF\"}','2026-05-16 08:10:16.784418','EXPORTED','2026-05-16 08:10:16.298417','Admin01','ACTIVE',94,31),(62,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 01:23:59.291737','GENERATED','2026-05-17 01:23:58.377742','admin01','ACTIVE',95,1),(63,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 01:24:11.558763','GENERATED','2026-05-17 01:24:11.415767','admin01','ACTIVE',96,2),(64,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 01:24:13.842073','GENERATED','2026-05-17 01:24:13.734080','admin01','ACTIVE',97,3),(65,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 01:24:20.794732','GENERATED','2026-05-17 01:24:20.683727','admin01','ACTIVE',98,4),(66,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 01:24:23.113930','GENERATED','2026-05-17 01:24:23.009939','admin01','ACTIVE',99,5),(67,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 01:24:24.504131','GENERATED','2026-05-17 01:24:24.410138','admin01','ACTIVE',100,6),(68,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 01:24:31.843776','GENERATED','2026-05-17 01:24:31.765772','admin01','ACTIVE',101,1),(69,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 01:24:43.715856','GENERATED','2026-05-17 01:24:43.645860','admin01','ACTIVE',102,2),(70,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 01:24:45.534772','GENERATED','2026-05-17 01:24:45.436773','admin01','ACTIVE',103,3),(71,'2026-04-16','2026-05-16','{\"dateFrom\":\"2026-04-16\",\"dateTo\":\"2026-05-16\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 01:24:55.772385','GENERATED','2026-05-17 01:24:55.709393','admin01','ACTIVE',104,5),(72,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 08:28:33.495694','GENERATED','2026-05-17 08:28:32.613289','admin01','ACTIVE',105,1),(73,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 08:29:27.164171','GENERATED','2026-05-17 08:29:26.983165','admin01','ACTIVE',106,2),(74,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 08:29:46.666165','GENERATED','2026-05-17 08:29:46.513168','admin01','ACTIVE',107,3),(75,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 08:30:25.933400','GENERATED','2026-05-17 08:30:25.670405','admin01','ACTIVE',108,4),(76,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 08:30:49.876932','GENERATED','2026-05-17 08:30:49.678931','admin01','ACTIVE',109,5),(77,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 08:31:08.384999','GENERATED','2026-05-17 08:31:08.187004','admin01','ACTIVE',110,6),(78,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 08:31:38.028997','GENERATED','2026-05-17 08:31:37.783999','admin01','ACTIVE',111,6),(79,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 08:49:30.990164','GENERATED','2026-05-17 08:49:30.866142','admin01','ACTIVE',112,1),(80,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 08:49:39.304147','GENERATED','2026-05-17 08:49:39.212146','admin01','ACTIVE',113,2),(81,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 08:49:42.895956','GENERATED','2026-05-17 08:49:42.777979','admin01','ACTIVE',114,3),(82,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 08:49:44.363448','GENERATED','2026-05-17 08:49:44.284451','admin01','ACTIVE',115,4),(83,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 08:49:47.169164','GENERATED','2026-05-17 08:49:47.067161','admin01','ACTIVE',116,5),(84,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 08:49:49.099737','GENERATED','2026-05-17 08:49:49.009744','admin01','ACTIVE',117,6),(85,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 09:00:00.902935','GENERATED','2026-05-17 09:00:00.794934','admin01','ACTIVE',118,1),(86,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 09:03:21.337796','GENERATED','2026-05-17 09:03:21.240803','admin01','ACTIVE',119,1),(87,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 09:08:41.306615','GENERATED','2026-05-17 09:08:41.198622','admin01','ACTIVE',120,1),(88,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 09:09:44.952195','GENERATED','2026-05-17 09:09:44.566198','admin01','ACTIVE',121,1),(89,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 09:14:10.692505','GENERATED','2026-05-17 09:14:10.584505','admin01','ACTIVE',122,1),(90,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 09:18:08.417763','GENERATED','2026-05-17 09:18:08.241766','admin01','ACTIVE',123,1),(91,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 09:18:56.602388','GENERATED','2026-05-17 09:18:56.385387','admin01','ACTIVE',124,1),(92,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 09:19:18.874631','GENERATED','2026-05-17 09:19:18.662627','admin01','ACTIVE',125,1),(93,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 09:19:20.546635','GENERATED','2026-05-17 09:19:20.225635','admin01','ACTIVE',126,1),(94,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 09:21:21.143712','GENERATED','2026-05-17 09:21:20.976718','admin01','ACTIVE',127,1),(95,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 09:28:41.720424','GENERATED','2026-05-17 09:28:41.584425','admin01','ACTIVE',128,1),(96,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 09:29:29.371965','GENERATED','2026-05-17 09:29:29.228963','admin01','ACTIVE',129,1),(97,'2026-04-17','2026-05-17','{\"dateFrom\":\"2026-04-17\",\"dateTo\":\"2026-05-17\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-17 09:30:06.250197','GENERATED','2026-05-17 09:30:06.054194','admin01','ACTIVE',130,1),(98,'2026-04-18','2026-05-18','{\"dateFrom\":\"2026-04-18\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 08:35:02.894586','GENERATED','2026-05-18 08:35:01.493586','admin01','ACTIVE',131,3),(99,'2026-04-18','2026-05-18','{\"dateFrom\":\"2026-04-18\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 08:43:36.708620','GENERATED','2026-05-18 08:43:36.199622','admin01','ACTIVE',132,35),(100,'2026-04-18','2026-05-18','{\"dateFrom\":\"2026-04-18\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 08:46:24.200373','GENERATED','2026-05-18 08:46:24.021375','admin01','ACTIVE',133,3),(101,'2026-04-18','2026-05-18','{\"dateFrom\":\"2026-04-18\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 08:46:26.471367','GENERATED','2026-05-18 08:46:26.341371','admin01','ACTIVE',134,4),(102,'2026-04-18','2026-05-18','{\"dateFrom\":\"2026-04-18\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 08:46:28.152373','GENERATED','2026-05-18 08:46:28.022370','admin01','ACTIVE',135,5),(103,'2026-04-18','2026-05-18','{\"dateFrom\":\"2026-04-18\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 08:46:30.184374','GENERATED','2026-05-18 08:46:29.998371','admin01','ACTIVE',136,35),(104,'2026-04-18','2026-05-18','{\"dateFrom\":\"2026-04-18\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 08:46:33.466369','GENERATED','2026-05-18 08:46:33.359375','admin01','ACTIVE',137,2),(105,'2026-04-18','2026-05-18','{\"dateFrom\":\"2026-04-18\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 08:46:55.096663','GENERATED','2026-05-18 08:46:54.960666','admin01','ACTIVE',138,35),(106,'2026-04-18','2026-05-18','{\"dateFrom\":\"2026-04-18\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 08:47:04.865951','GENERATED','2026-05-18 08:47:04.793950','admin01','ACTIVE',139,3),(107,'2026-04-18','2026-05-18','{\"dateFrom\":\"2026-04-18\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 08:47:10.491586','GENERATED','2026-05-18 08:47:10.397591','admin01','ACTIVE',140,35),(108,'2026-04-18','2026-05-18','{\"dateFrom\":\"2026-04-18\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 08:58:02.542113','GENERATED','2026-05-18 08:58:02.400115','admin01','ACTIVE',141,35),(109,'2026-01-01','2026-05-18','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 09:37:54.438470','GENERATED','2026-05-18 09:37:52.337470','admin01','ACTIVE',142,35),(110,'2026-01-01','2026-05-18','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 09:38:49.760539','GENERATED','2026-05-18 09:38:49.340543','admin01','ACTIVE',143,35),(111,'2026-01-01','2026-05-18','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 09:42:45.477091','GENERATED','2026-05-18 09:42:45.174088','admin01','ACTIVE',144,35),(112,'2026-01-01','2026-05-18','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 09:47:28.356939','GENERATED','2026-05-18 09:47:28.166936','admin01','ACTIVE',145,35),(113,'2026-04-18','2026-05-18','{\"dateFrom\":\"2026-04-18\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 09:48:26.313098','GENERATED','2026-05-18 09:48:26.159121','admin01','ACTIVE',146,3),(114,'2026-04-18','2026-05-18','{\"dateFrom\":\"2026-04-18\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 09:48:29.674230','GENERATED','2026-05-18 09:48:29.547235','admin01','ACTIVE',147,35),(115,'2026-04-18','2026-05-18','{\"dateFrom\":\"2026-04-18\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 09:59:51.519577','GENERATED','2026-05-18 09:59:51.279572','admin01','ACTIVE',148,35),(116,'2026-01-01','2026-05-18','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 10:25:18.083900','GENERATED','2026-05-18 10:25:17.863902','admin01','ACTIVE',149,35),(117,'2026-04-18','2026-05-18','{\"dateFrom\":\"2026-04-18\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 10:26:31.165174','GENERATED','2026-05-18 10:26:31.015173','admin01','ACTIVE',150,2),(118,'2026-04-18','2026-05-18','{\"dateFrom\":\"2026-04-18\",\"dateTo\":\"2026-05-18\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-05-18 10:26:35.173765','GENERATED','2026-05-18 10:26:34.983768','admin01','ACTIVE',151,1),(120,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 02:24:13.444993','GENERATED','2026-06-05 02:24:13.039297','admin01','ACTIVE',153,2),(121,'2026-01-01','2026-06-04','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 02:27:58.698923','GENERATED','2026-06-05 02:27:57.849145','admin01','ACTIVE',154,35),(122,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 02:28:00.349013','GENERATED','2026-06-05 02:28:00.189861','admin01','ACTIVE',155,3),(123,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 02:28:05.810501','GENERATED','2026-06-05 02:28:05.636039','admin01','ACTIVE',156,4),(124,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 02:28:12.730661','GENERATED','2026-06-05 02:28:12.582671','admin01','ACTIVE',157,5),(125,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 02:28:19.753143','GENERATED','2026-06-05 02:28:19.567600','admin01','ACTIVE',158,6),(126,'2026-01-01','2026-06-04','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 16:57:41.669817','GENERATED','2026-06-05 02:28:22.236031','admin01','ACTIVE',162,35),(127,'2026-04-01','2026-06-05','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 04:52:53.776474','GENERATED','2026-06-05 04:52:53.168020','admin01','ACTIVE',NULL,37),(128,'2026-04-01','2026-06-05','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:01:05.764388','GENERATED','2026-06-05 05:01:04.968279','admin01','ACTIVE',NULL,37),(129,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:06:44.198692','GENERATED','2026-06-05 05:06:44.183935','admin01','ACTIVE',NULL,1),(130,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:06:46.926815','GENERATED','2026-06-05 05:06:46.906022','admin01','ACTIVE',NULL,2),(131,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:06:47.455449','GENERATED','2026-06-05 05:06:47.429786','admin01','ACTIVE',NULL,35),(132,'2026-01-01','2026-06-04','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:06:58.919191','GENERATED','2026-06-05 05:06:58.885861','admin01','ACTIVE',NULL,35),(133,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:07:01.002607','GENERATED','2026-06-05 05:07:01.002607','admin01','ACTIVE',NULL,3),(134,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:07:06.926596','GENERATED','2026-06-05 05:07:06.901956','admin01','ACTIVE',NULL,4),(135,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:07:09.176474','GENERATED','2026-06-05 05:07:09.176474','admin01','ACTIVE',NULL,6),(136,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:07:10.177280','GENERATED','2026-06-05 05:07:10.166254','admin01','ACTIVE',NULL,5),(137,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:07:12.487190','GENERATED','2026-06-05 05:07:12.476708','admin01','ACTIVE',NULL,6),(138,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:15:23.246743','GENERATED','2026-06-05 05:15:23.241719','admin01','ACTIVE',NULL,2),(139,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:23:26.275663','GENERATED','2026-06-05 05:23:26.263697','admin01','ACTIVE',NULL,2),(140,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:26:55.619297','GENERATED','2026-06-05 05:26:55.613302','admin01','ACTIVE',NULL,2),(141,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:27:07.264447','GENERATED','2026-06-05 05:27:07.227447','admin01','ACTIVE',NULL,35),(142,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:27:10.557339','GENERATED','2026-06-05 05:27:10.543196','admin01','ACTIVE',NULL,2),(143,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:27:12.942929','GENERATED','2026-06-05 05:27:12.927283','admin01','ACTIVE',NULL,35),(144,'2026-01-01','2026-06-04','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:27:31.329934','GENERATED','2026-06-05 05:27:31.303395','admin01','ACTIVE',NULL,35),(145,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:27:34.504421','GENERATED','2026-06-05 05:27:34.493405','admin01','ACTIVE',NULL,3),(146,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:27:36.916229','GENERATED','2026-06-05 05:27:36.914937','admin01','ACTIVE',NULL,3),(147,'2026-01-01','2026-06-04','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:27:54.035224','GENERATED','2026-06-05 05:27:53.960131','admin01','ACTIVE',NULL,35),(148,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:27:56.212199','GENERATED','2026-06-05 05:27:56.212199','admin01','ACTIVE',NULL,3),(149,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:28:01.261106','GENERATED','2026-06-05 05:28:01.261106','admin01','ACTIVE',NULL,4),(150,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:28:03.681893','GENERATED','2026-06-05 05:28:03.660991','admin01','ACTIVE',NULL,5),(151,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:28:05.493391','GENERATED','2026-06-05 05:28:05.493391','admin01','ACTIVE',NULL,6),(152,'2026-05-05','2026-06-04','{\"dateFrom\":\"2026-05-05\",\"dateTo\":\"2026-06-04\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 05:28:16.479728','GENERATED','2026-06-05 05:28:16.472527','admin01','ACTIVE',NULL,6),(153,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 16:16:00.764062','GENERATED','2026-06-05 16:16:00.547295','admin01','ACTIVE',NULL,35),(154,'2026-05-06','2026-06-05','{\"dateFrom\":\"2026-05-06\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 16:16:08.757340','GENERATED','2026-06-05 16:16:08.749339','admin01','ACTIVE',NULL,3),(155,'2026-05-06','2026-06-05','{\"dateFrom\":\"2026-05-06\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 16:16:11.248350','GENERATED','2026-06-05 16:16:11.239348','admin01','ACTIVE',NULL,4),(156,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 16:16:44.237367','GENERATED','2026-06-05 16:16:44.168369','admin01','ACTIVE',NULL,35),(157,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 16:22:17.229876','GENERATED','2026-06-05 16:22:17.200876','admin01','ACTIVE',NULL,35),(158,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 16:22:44.372720','GENERATED','2026-06-05 16:22:44.337723','admin01','ACTIVE',NULL,35),(159,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 16:29:01.709989','GENERATED','2026-06-05 16:29:01.670988','admin01','ACTIVE',NULL,35),(160,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 16:29:10.570824','GENERATED','2026-06-05 16:29:10.414592','admin01','ACTIVE',NULL,37),(161,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 16:29:18.082811','GENERATED','2026-06-05 16:29:17.942185','admin01','ACTIVE',NULL,37),(162,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 16:29:53.616550','GENERATED','2026-06-05 16:29:53.484546','admin01','ACTIVE',NULL,37),(163,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 16:31:10.749087','GENERATED','2026-06-05 16:31:10.595088','admin01','ACTIVE',NULL,37),(164,'2026-05-06','2026-06-05','{\"dateFrom\":\"2026-05-06\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 17:03:00.819109','GENERATED','2026-06-05 17:03:00.802428','admin01','ACTIVE',NULL,35),(165,'2026-05-06','2026-06-05','{\"dateFrom\":\"2026-05-06\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 17:03:07.873818','GENERATED','2026-06-05 17:03:07.852317','admin01','ACTIVE',NULL,2),(166,'2026-05-06','2026-06-05','{\"dateFrom\":\"2026-05-06\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 17:03:18.020718','GENERATED','2026-06-05 17:03:18.015214','admin01','ACTIVE',NULL,2),(167,'2026-05-06','2026-06-05','{\"dateFrom\":\"2026-05-06\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 17:03:40.131681','GENERATED','2026-06-05 17:03:40.108581','admin01','ACTIVE',NULL,35),(168,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 17:03:58.372853','GENERATED','2026-06-05 17:03:58.225892','admin01','ACTIVE',NULL,36),(169,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 17:05:15.157395','GENERATED','2026-06-05 17:05:15.026315','admin01','ACTIVE',NULL,36),(170,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 17:05:21.001199','GENERATED','2026-06-05 17:05:20.823215','admin01','ACTIVE',NULL,36),(171,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 17:13:04.498279','EXPORTED','2026-06-05 17:13:04.281409','admin01','ACTIVE',163,36),(172,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 17:13:11.761033','EXPORTED','2026-06-05 17:13:11.461717','admin01','ACTIVE',164,37),(173,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 17:13:19.009346','EXPORTED','2026-06-05 17:13:18.876706','admin01','ACTIVE',165,1),(174,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 17:13:26.124425','EXPORTED','2026-06-05 17:13:25.974511','admin01','ACTIVE',166,35),(175,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 17:14:25.113264','EXPORTED','2026-06-05 17:14:25.000669','admin01','ACTIVE',167,1),(176,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 17:14:33.044937','EXPORTED','2026-06-05 17:14:32.894713','admin01','ACTIVE',168,2),(177,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 17:14:41.161678','EXPORTED','2026-06-05 17:14:40.939192','admin01','ACTIVE',169,35),(178,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 17:14:49.022164','EXPORTED','2026-06-05 17:14:48.756468','admin01','ACTIVE',170,36),(179,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 17:14:56.920398','EXPORTED','2026-06-05 17:14:56.637049','admin01','ACTIVE',171,37),(180,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 17:15:04.827864','EXPORTED','2026-06-05 17:15:04.731547','admin01','ACTIVE',172,3),(181,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 17:15:12.033101','EXPORTED','2026-06-05 17:15:11.950464','admin01','ACTIVE',173,4),(182,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 17:15:19.565332','EXPORTED','2026-06-05 17:15:19.448274','admin01','ACTIVE',174,5),(183,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 17:15:26.696417','EXPORTED','2026-06-05 17:15:26.563265','admin01','ACTIVE',175,6),(184,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 17:15:34.484752','EXPORTED','2026-06-05 17:15:34.394216','admin01','ACTIVE',176,31),(185,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 17:15:41.776236','EXPORTED','2026-06-05 17:15:41.642969','admin01','ACTIVE',177,32),(186,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 17:15:48.920664','EXPORTED','2026-06-05 17:15:48.809345','admin01','ACTIVE',178,33),(187,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 17:15:56.140953','EXPORTED','2026-06-05 17:15:56.024416','admin01','ACTIVE',179,34),(188,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 17:17:21.498325','EXPORTED','2026-06-05 17:17:21.305060','admin01','ACTIVE',180,36),(189,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 17:17:45.714159','EXPORTED','2026-06-05 17:17:45.533816','admin01','ACTIVE',181,36),(190,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 17:37:44.849279','GENERATED','2026-06-05 17:37:44.804277','admin01','ACTIVE',NULL,35),(191,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 17:39:21.122272','GENERATED','2026-06-05 17:39:21.089612','admin01','ACTIVE',NULL,35),(192,'2026-01-01','2026-06-05','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-05 23:37:55.010477','EXPORTED','2026-06-05 23:37:54.369138','admin01','ACTIVE',182,36),(193,'2026-05-06','2026-06-05','{\"dateFrom\":\"2026-05-06\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 23:56:07.114856','GENERATED','2026-06-05 23:56:07.084858','admin01','ACTIVE',NULL,1),(194,'2026-05-06','2026-06-05','{\"dateFrom\":\"2026-05-06\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 23:56:23.655728','GENERATED','2026-06-05 23:56:23.648714','admin01','ACTIVE',NULL,3),(195,'2026-05-06','2026-06-05','{\"dateFrom\":\"2026-05-06\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 23:56:34.993978','GENERATED','2026-06-05 23:56:34.931972','admin01','ACTIVE',NULL,35),(196,'2026-05-06','2026-06-05','{\"dateFrom\":\"2026-05-06\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-05 23:56:47.122436','GENERATED','2026-06-05 23:56:47.099441','admin01','ACTIVE',NULL,35),(197,'2026-01-01','2026-06-06','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-06\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-06 10:32:08.192438','GENERATED','2026-06-06 10:32:08.085444','admin01','ACTIVE',NULL,35),(198,'2026-01-01','2026-06-06','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-06\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-06 10:32:36.780799','GENERATED','2026-06-06 10:32:36.754803','admin01','ACTIVE',NULL,35),(199,'2026-01-01','2026-06-06','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-06\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-06 10:32:42.173782','EXPORTED','2026-06-06 10:32:41.806140','admin01','ACTIVE',183,37),(200,'2026-01-01','2026-06-06','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-06\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-06 10:38:37.549182','GENERATED','2026-06-06 10:38:37.512175','admin01','ACTIVE',NULL,35),(201,'2026-01-01','2026-06-06','{\"dateFrom\":\"2026-01-01\",\"dateTo\":\"2026-06-06\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-06 10:43:10.229173','GENERATED','2026-06-06 10:43:10.165179','admin01','ACTIVE',NULL,35),(202,'2026-05-07','2026-06-06','{\"dateFrom\":\"2026-05-07\",\"dateTo\":\"2026-06-06\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-06 16:02:40.059779','GENERATED','2026-06-06 16:02:40.034782','admin01','ACTIVE',NULL,1),(203,'2026-05-07','2026-06-06','{\"dateFrom\":\"2026-05-07\",\"dateTo\":\"2026-06-06\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-06 16:02:50.782344','GENERATED','2026-06-06 16:02:50.776342','admin01','ACTIVE',NULL,2),(204,'2026-05-07','2026-06-06','{\"dateFrom\":\"2026-05-07\",\"dateTo\":\"2026-06-06\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-06 16:02:59.253352','GENERATED','2026-06-06 16:02:59.249377','admin01','ACTIVE',NULL,2),(205,'2026-05-07','2026-06-06','{\"dateFrom\":\"2026-05-07\",\"dateTo\":\"2026-06-06\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-06 16:03:12.991296','GENERATED','2026-06-06 16:03:12.948292','admin01','ACTIVE',NULL,35),(206,'2026-05-07','2026-06-06','{\"dateFrom\":\"2026-05-07\",\"dateTo\":\"2026-06-06\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-06 16:03:17.679552','GENERATED','2026-06-06 16:03:17.674551','admin01','ACTIVE',NULL,2),(207,'2026-05-07','2026-06-06','{\"dateFrom\":\"2026-05-07\",\"dateTo\":\"2026-06-06\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-06 16:03:18.401417','GENERATED','2026-06-06 16:03:18.380419','admin01','ACTIVE',NULL,35),(208,'2026-05-08','2026-06-07','{\"dateFrom\":\"2026-05-08\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:05:34.626380','GENERATED','2026-06-07 12:05:34.549764','admin01','ACTIVE',NULL,1),(209,'2026-05-09','2026-06-07','{\"dateFrom\":\"2026-05-09\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:06:04.097386','GENERATED','2026-06-07 12:06:04.091406','admin01','ACTIVE',NULL,1),(210,'2025-12-31','2026-06-07','{\"dateFrom\":\"2025-12-31\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:06:07.080318','GENERATED','2026-06-07 12:06:07.074327','admin01','ACTIVE',NULL,1),(211,'2025-06-30','2026-06-07','{\"dateFrom\":\"2025-06-30\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:06:09.868369','GENERATED','2026-06-07 12:06:09.862362','admin01','ACTIVE',NULL,1),(212,'2026-06-07','2026-06-07','{\"dateFrom\":\"2026-06-07\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:06:12.008299','GENERATED','2026-06-07 12:06:12.004303','admin01','ACTIVE',NULL,1),(213,'2026-05-09','2026-06-07','{\"dateFrom\":\"2026-05-09\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:06:14.507521','GENERATED','2026-06-07 12:06:14.502522','admin01','ACTIVE',NULL,1),(214,'2026-05-09','2026-06-07','{\"dateFrom\":\"2026-05-09\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:06:17.905137','GENERATED','2026-06-07 12:06:17.900140','admin01','ACTIVE',NULL,1),(215,'2026-05-09','2026-06-07','{\"dateFrom\":\"2026-05-09\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:06:58.804990','GENERATED','2026-06-07 12:06:58.800016','admin01','ACTIVE',NULL,1),(216,'2026-05-09','2026-06-07','{\"dateFrom\":\"2026-05-09\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-07 12:07:17.624554','EXPORTED','2026-06-07 12:07:17.369066','admin01','ACTIVE',185,1),(217,'2026-05-09','2026-06-07','{\"dateFrom\":\"2026-05-09\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:14:54.120603','GENERATED','2026-06-07 12:14:54.106599','admin01','ACTIVE',NULL,1),(218,'2025-12-31','2026-06-07','{\"dateFrom\":\"2025-12-31\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:14:58.828812','GENERATED','2026-06-07 12:14:58.823814','admin01','ACTIVE',NULL,1),(219,'2025-12-31','2026-06-07','{\"dateFrom\":\"2025-12-31\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:15:14.532146','GENERATED','2026-06-07 12:15:01.388908','admin01','ACTIVE',186,1),(220,'2026-05-08','2026-06-07','{\"dateFrom\":\"2026-05-08\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:15:39.918175','GENERATED','2026-06-07 12:15:39.902172','admin01','ACTIVE',NULL,2),(221,'2026-05-08','2026-06-07','{\"dateFrom\":\"2026-05-08\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:15:57.165600','GENERATED','2026-06-07 12:15:57.109601','admin01','ACTIVE',NULL,35),(222,'2026-05-08','2026-06-07','{\"dateFrom\":\"2026-05-08\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:18:49.785907','GENERATED','2026-06-07 12:18:49.756907','admin01','ACTIVE',NULL,35),(223,'2026-05-08','2026-06-07','{\"dateFrom\":\"2026-05-08\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:19:11.603462','GENERATED','2026-06-07 12:19:11.578461','admin01','ACTIVE',NULL,35),(224,'2026-05-08','2026-06-07','{\"dateFrom\":\"2026-05-08\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:19:28.493766','GENERATED','2026-06-07 12:19:28.465759','admin01','ACTIVE',NULL,35),(225,'2026-05-08','2026-06-07','{\"dateFrom\":\"2026-05-08\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:20:21.720017','GENERATED','2026-06-07 12:20:21.697023','admin01','ACTIVE',NULL,35),(226,'2026-05-08','2026-06-07','{\"dateFrom\":\"2026-05-08\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:22:36.744251','GENERATED','2026-06-07 12:22:36.721249','admin01','ACTIVE',NULL,35),(227,'2026-05-08','2026-06-07','{\"dateFrom\":\"2026-05-08\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:22:58.156298','GENERATED','2026-06-07 12:22:58.129568','admin01','ACTIVE',NULL,35),(228,'2026-05-08','2026-06-07','{\"dateFrom\":\"2026-05-08\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:23:11.212546','GENERATED','2026-06-07 12:23:11.185545','admin01','ACTIVE',NULL,35),(229,'2026-05-08','2026-06-07','{\"dateFrom\":\"2026-05-08\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:23:46.847281','GENERATED','2026-06-07 12:23:46.809278','admin01','ACTIVE',NULL,35),(230,'2026-05-08','2026-06-07','{\"dateFrom\":\"2026-05-08\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:29:03.195593','GENERATED','2026-06-07 12:29:03.148448','admin01','ACTIVE',NULL,35),(231,'2025-06-30','2026-06-07','{\"dateFrom\":\"2025-06-30\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:29:08.452628','GENERATED','2026-06-07 12:29:08.422627','admin01','ACTIVE',NULL,35),(232,'2025-06-30','2026-06-07','{\"dateFrom\":\"2025-06-30\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-07 12:29:33.003997','GENERATED','2026-06-07 12:29:14.791014','admin01','ACTIVE',187,35),(233,'2025-06-30','2026-06-07','{\"dateFrom\":\"2025-06-30\",\"dateTo\":\"2026-06-07\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-07 12:29:55.185300','EXPORTED','2026-06-07 12:29:55.072306','admin01','ACTIVE',188,35),(234,'2026-05-13','2026-06-12','{\"dateFrom\":\"2026-05-13\",\"dateTo\":\"2026-06-12\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 02:20:00.392976','GENERATED','2026-06-13 02:20:00.285750','admin01','ACTIVE',NULL,35),(235,'2026-05-13','2026-06-12','{\"dateFrom\":\"2026-05-13\",\"dateTo\":\"2026-06-12\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 02:20:28.991597','GENERATED','2026-06-13 02:20:10.527558','admin01','ACTIVE',189,35),(236,'2026-05-13','2026-06-12','{\"dateFrom\":\"2026-05-13\",\"dateTo\":\"2026-06-12\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 02:27:01.567934','GENERATED','2026-06-13 02:27:01.505933','admin01','ACTIVE',NULL,35),(237,'2026-05-13','2026-06-12','{\"dateFrom\":\"2026-05-13\",\"dateTo\":\"2026-06-12\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 02:27:17.236112','GENERATED','2026-06-13 02:27:07.552404','admin01','ACTIVE',190,35),(238,'2026-05-13','2026-06-12','{\"dateFrom\":\"2026-05-13\",\"dateTo\":\"2026-06-12\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 02:28:15.693717','GENERATED','2026-06-13 02:28:15.674949','admin01','ACTIVE',NULL,35),(239,'2026-04-01','2026-06-05','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 03:00:09.363014','GENERATED','2026-06-13 03:00:09.145760','admin01','ACTIVE',NULL,37),(240,'2026-04-01','2026-06-05','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 03:04:15.492956','GENERATED','2026-06-13 03:04:15.476462','admin01','ACTIVE',NULL,1),(241,'2026-04-01','2026-06-05','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 03:04:15.813712','GENERATED','2026-06-13 03:04:15.803712','admin01','ACTIVE',NULL,2),(242,'2026-04-01','2026-06-05','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 03:04:15.976713','GENERATED','2026-06-13 03:04:15.954714','admin01','ACTIVE',NULL,35),(243,'2026-04-01','2026-06-05','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 03:04:16.336711','GENERATED','2026-06-13 03:04:16.133710','admin01','ACTIVE',NULL,36),(244,'2026-04-01','2026-06-05','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 03:04:16.805402','GENERATED','2026-06-13 03:04:16.579742','admin01','ACTIVE',NULL,37),(245,'2026-04-01','2026-06-05','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 03:04:16.891403','GENERATED','2026-06-13 03:04:16.886401','admin01','ACTIVE',NULL,3),(246,'2026-04-01','2026-06-05','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 03:04:17.120938','GENERATED','2026-06-13 03:04:17.111939','admin01','ACTIVE',NULL,4),(247,'2026-04-01','2026-06-05','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 03:04:17.225477','GENERATED','2026-06-13 03:04:17.219477','admin01','ACTIVE',NULL,5),(248,'2026-04-01','2026-06-05','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 03:04:17.323476','GENERATED','2026-06-13 03:04:17.314479','admin01','ACTIVE',NULL,6),(249,'2026-04-01','2026-06-05','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 03:04:17.549460','GENERATED','2026-06-13 03:04:17.540462','admin01','ACTIVE',NULL,31),(250,'2026-04-01','2026-06-05','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 03:04:17.818694','GENERATED','2026-06-13 03:04:17.809468','admin01','ACTIVE',NULL,32),(251,'2026-04-01','2026-06-05','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 03:04:17.919697','GENERATED','2026-06-13 03:04:17.908697','admin01','ACTIVE',NULL,33),(252,'2026-04-01','2026-06-05','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-05\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 03:04:35.040243','GENERATED','2026-06-13 03:04:18.033693','admin01','ACTIVE',192,34),(253,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 03:55:43.898068','GENERATED','2026-06-13 03:55:43.663423','admin01','ACTIVE',NULL,37),(254,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 03:55:45.491529','GENERATED','2026-06-13 03:55:45.483530','admin01','ACTIVE',NULL,3),(255,'2026-04-01','2026-07-06','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-07-06\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 03:57:55.794200','GENERATED','2026-06-13 03:57:55.789201','admin01','ACTIVE',NULL,2),(256,'2026-04-01','2026-07-06','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-07-06\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 03:57:55.872201','GENERATED','2026-06-13 03:57:55.833205','admin01','ACTIVE',NULL,35),(257,'2026-04-01','2026-07-06','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-07-06\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 03:57:57.225096','GENERATED','2026-06-13 03:57:57.218927','admin01','ACTIVE',NULL,4),(258,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 04:00:03.807252','GENERATED','2026-06-13 04:00:03.792234','admin01','ACTIVE',NULL,35),(259,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 04:00:32.075140','GENERATED','2026-06-13 04:00:32.071141','admin01','ACTIVE',NULL,3),(260,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 04:00:33.205945','GENERATED','2026-06-13 04:00:33.185948','admin01','ACTIVE',NULL,35),(261,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 04:09:16.768471','GENERATED','2026-06-13 04:09:16.752424','admin01','ACTIVE',NULL,1),(262,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 04:09:16.850682','GENERATED','2026-06-13 04:09:16.842685','admin01','ACTIVE',NULL,2),(263,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 04:09:16.921682','GENERATED','2026-06-13 04:09:16.899684','admin01','ACTIVE',NULL,35),(264,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 04:09:17.198825','GENERATED','2026-06-13 04:09:16.965687','admin01','ACTIVE',NULL,36),(265,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 04:09:17.444602','GENERATED','2026-06-13 04:09:17.238824','admin01','ACTIVE',NULL,37),(266,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 04:09:17.485602','GENERATED','2026-06-13 04:09:17.482600','admin01','ACTIVE',NULL,3),(267,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 04:09:17.528607','GENERATED','2026-06-13 04:09:17.522606','admin01','ACTIVE',NULL,4),(268,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 04:09:17.580616','GENERATED','2026-06-13 04:09:17.573601','admin01','ACTIVE',NULL,5),(269,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 04:09:17.644605','GENERATED','2026-06-13 04:09:17.633602','admin01','ACTIVE',NULL,6),(270,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 04:09:17.700603','GENERATED','2026-06-13 04:09:17.692601','admin01','ACTIVE',NULL,31),(271,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 04:09:17.738600','GENERATED','2026-06-13 04:09:17.732600','admin01','ACTIVE',NULL,32),(272,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 04:09:17.774601','GENERATED','2026-06-13 04:09:17.767603','admin01','ACTIVE',NULL,33),(273,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"VIEW\"}','2026-06-13 04:09:17.816388','GENERATED','2026-06-13 04:09:17.807215','admin01','ACTIVE',NULL,34),(274,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"PRINT\"}','2026-06-13 04:10:06.518071','EXPORTED','2026-06-13 04:10:06.080823','admin01','ACTIVE',197,37),(275,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"PDF\"}','2026-06-13 04:10:08.061208','EXPORTED','2026-06-13 04:10:06.794073','admin01','ACTIVE',198,35),(276,'2026-04-01','2026-06-13','{\"dateFrom\":\"2026-04-01\",\"dateTo\":\"2026-06-13\",\"branchId\":null,\"exportType\":\"EXCEL\"}','2026-06-13 04:10:11.409812','EXPORTED','2026-06-13 04:10:08.111212','admin01','ACTIVE',199,1);

--
-- Table structure for table `revoked_token`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `revoked_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expires_at` datetime(6) NOT NULL,
  `revoked_at` datetime(6) NOT NULL,
  `token_id` varchar(100) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `username` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_revoked_token_jti` (`token_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `revoked_token`
--


--
-- Table structure for table `role_permission`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `module_name` varchar(100) NOT NULL,
  `action_name` varchar(100) NOT NULL,
  `permission_code` varchar(150) NOT NULL,
  `display_name` varchar(180) NOT NULL,
  `allow_flag` bit(1) NOT NULL DEFAULT b'1',
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(120) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_role_permission_role_id` (`role_id`),
  KEY `idx_role_permission_code` (`permission_code`),
  CONSTRAINT `fk_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=923 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permission`
--

INSERT INTO `role_permission` VALUES (318,1,'ADMIN','ACCESS','ADMIN_DASHBOARD_ACCESS','Admin Dashboard - Dashboard Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(319,1,'ROLE_MANAGEMENT','ACCESS','ROLE_MANAGEMENT_ACCESS','Role Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(320,1,'ROLE_MANAGEMENT','VIEW','ROLE_VIEW','Role Management - View Roles',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(321,1,'ROLE_MANAGEMENT','CREATE','ROLE_CREATE','Role Management - Create Role',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(322,1,'ROLE_MANAGEMENT','EDIT','ROLE_EDIT','Role Management - Edit Role',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(323,1,'ROLE_MANAGEMENT','ARCHIVE','ROLE_ARCHIVE','Role Management - Archive Role',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(324,1,'ROLE_MANAGEMENT','RESTORE','ROLE_RESTORE','Role Management - Restore Role',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(325,1,'ROLE_MANAGEMENT','MAP_PERMISSIONS','ROLE_MAP_PERMISSIONS','Role Management - Map Permissions',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(326,1,'USER_MANAGEMENT','ACCESS','USER_MANAGEMENT_ACCESS','User Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(327,1,'USER_MANAGEMENT','VIEW','USER_VIEW','User Management - View Users',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(328,1,'USER_MANAGEMENT','CREATE','USER_CREATE','User Management - Create User',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(329,1,'USER_MANAGEMENT','EDIT','USER_EDIT','User Management - Edit User',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(330,1,'USER_MANAGEMENT','ARCHIVE','USER_ARCHIVE','User Management - Archive User',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(331,1,'USER_MANAGEMENT','RESTORE','USER_RESTORE','User Management - Restore User',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(332,1,'USER_MANAGEMENT','LOCK','USER_LOCK','User Management - Lock User',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(333,1,'USER_MANAGEMENT','UNLOCK','USER_UNLOCK','User Management - Unlock User',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(334,1,'USER_MANAGEMENT','RESET_PASSWORD','USER_RESET_PASSWORD','User Management - Reset Password',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(335,1,'USER_MANAGEMENT','ASSIGN_ROLE','USER_ASSIGN_ROLE','User Management - Assign Role',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(336,1,'LOOKUP_CONFIG','ACCESS','LOOKUP_CONFIG_ACCESS','Lookup / Config - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(337,1,'BRANCH_MANAGEMENT','ACCESS','BRANCH_MANAGEMENT_ACCESS','Branch Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(338,1,'ATM_CDM','ACCESS','ATM_CDM_ACCESS','ATM / CDM - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(339,1,'CUSTOMER_MANAGEMENT','ACCESS','CUSTOMER_MANAGEMENT_ACCESS','Customer Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(340,1,'KYC_MANAGEMENT','ACCESS','KYC_MANAGEMENT_ACCESS','KYC Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(341,1,'ACCOUNT_MANAGEMENT','ACCESS','ACCOUNT_MANAGEMENT_ACCESS','Account Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(342,1,'TRANSACTIONS','ACCESS','TRANSACTIONS_ACCESS','Transactions - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(343,1,'PROFIT_MANAGEMENT','ACCESS','PROFIT_MANAGEMENT_ACCESS','Profit Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(344,1,'CARD_MANAGEMENT','ACCESS','CARD_MANAGEMENT_ACCESS','Card Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(345,1,'STATEMENTS','ACCESS','STATEMENTS_ACCESS','Statements - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(346,1,'DEPOSIT_SCHEMES','ACCESS','DEPOSIT_SCHEMES_ACCESS','Deposit Schemes - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(347,1,'FINANCING','ACCESS','FINANCING_ACCESS','Financing - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(348,1,'CONTRACTS','ACCESS','CONTRACTS_ACCESS','Contracts - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(349,1,'SHARIAH_REVIEW','ACCESS','SHARIAH_REVIEW_ACCESS','Shariah Review - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(350,1,'ZAKAT_CHARITY','ACCESS','ZAKAT_CHARITY_ACCESS','Zakat & Charity - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(351,1,'NOTIFICATION_ALERTS','ACCESS','NOTIFICATION_ALERTS_ACCESS','Notification & Alerts - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(352,1,'INTEGRATION_MANAGEMENT','ACCESS','INTEGRATION_MANAGEMENT_ACCESS','Integration Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(353,1,'REPORTING_REGULATORY','ACCESS','REPORTING_REGULATORY_ACCESS','Reporting & Regulatory - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(354,1,'SECURITY_AUDIT','ACCESS','SECURITY_AUDIT_ACCESS','Security / Audit - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(355,1,'WORKFLOW_SUPPORT','ACCESS','WORKFLOW_SUPPORT_ACCESS','Workflow Support - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(356,1,'VERIFICATION','ACCESS','VERIFICATION_ACCESS','Verification - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(357,1,'CALCULATION_ENGINE','ACCESS','CALCULATION_ENGINE_ACCESS','Calculation Engine - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(358,2,'ADMIN','ACCESS','ADMIN_DASHBOARD_ACCESS','Admin Dashboard - Dashboard Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(359,2,'BRANCH_MANAGEMENT','ACCESS','BRANCH_MANAGEMENT_ACCESS','Branch Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(360,2,'ATM_CDM','ACCESS','ATM_CDM_ACCESS','ATM / CDM - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(361,2,'CUSTOMER_MANAGEMENT','ACCESS','CUSTOMER_MANAGEMENT_ACCESS','Customer Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(362,2,'KYC_MANAGEMENT','ACCESS','KYC_MANAGEMENT_ACCESS','KYC Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(363,2,'ACCOUNT_MANAGEMENT','ACCESS','ACCOUNT_MANAGEMENT_ACCESS','Account Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(364,2,'TRANSACTIONS','ACCESS','TRANSACTIONS_ACCESS','Transactions - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(365,2,'STATEMENTS','ACCESS','STATEMENTS_ACCESS','Statements - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(366,2,'CARD_MANAGEMENT','ACCESS','CARD_MANAGEMENT_ACCESS','Card Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(367,2,'WORKFLOW_SUPPORT','ACCESS','WORKFLOW_SUPPORT_ACCESS','Workflow Support - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(368,3,'ADMIN','ACCESS','ADMIN_DASHBOARD_ACCESS','Admin Dashboard - Dashboard Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(369,3,'BRANCH_MANAGEMENT','ACCESS','BRANCH_MANAGEMENT_ACCESS','Branch Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(370,3,'ATM_CDM','ACCESS','ATM_CDM_ACCESS','ATM / CDM - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(371,3,'CUSTOMER_MANAGEMENT','ACCESS','CUSTOMER_MANAGEMENT_ACCESS','Customer Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(372,3,'ACCOUNT_MANAGEMENT','ACCESS','ACCOUNT_MANAGEMENT_ACCESS','Account Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(373,3,'TRANSACTIONS','ACCESS','TRANSACTIONS_ACCESS','Transactions - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(374,3,'STATEMENTS','ACCESS','STATEMENTS_ACCESS','Statements - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(375,4,'ADMIN','ACCESS','ADMIN_DASHBOARD_ACCESS','Admin Dashboard - Dashboard Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(376,4,'BRANCH_MANAGEMENT','ACCESS','BRANCH_MANAGEMENT_ACCESS','Branch Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(377,4,'ATM_CDM','ACCESS','ATM_CDM_ACCESS','ATM / CDM - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(378,4,'CUSTOMER_MANAGEMENT','ACCESS','CUSTOMER_MANAGEMENT_ACCESS','Customer Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(379,4,'KYC_MANAGEMENT','ACCESS','KYC_MANAGEMENT_ACCESS','KYC Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(380,4,'ACCOUNT_MANAGEMENT','ACCESS','ACCOUNT_MANAGEMENT_ACCESS','Account Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(381,4,'TRANSACTIONS','ACCESS','TRANSACTIONS_ACCESS','Transactions - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(382,4,'PROFIT_MANAGEMENT','ACCESS','PROFIT_MANAGEMENT_ACCESS','Profit Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(383,4,'CARD_MANAGEMENT','ACCESS','CARD_MANAGEMENT_ACCESS','Card Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(384,4,'STATEMENTS','ACCESS','STATEMENTS_ACCESS','Statements - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(385,4,'NOTIFICATION_ALERTS','ACCESS','NOTIFICATION_ALERTS_ACCESS','Notification & Alerts - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(386,4,'WORKFLOW_SUPPORT','ACCESS','WORKFLOW_SUPPORT_ACCESS','Workflow Support - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(387,4,'VERIFICATION','ACCESS','VERIFICATION_ACCESS','Verification - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(388,6,'ADMIN','ACCESS','ADMIN_DASHBOARD_ACCESS','Admin Dashboard - Dashboard Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(389,6,'ACCOUNT_MANAGEMENT','ACCESS','ACCOUNT_MANAGEMENT_ACCESS','Account Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(390,6,'PROFIT_MANAGEMENT','ACCESS','PROFIT_MANAGEMENT_ACCESS','Profit Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(391,6,'DEPOSIT_SCHEMES','ACCESS','DEPOSIT_SCHEMES_ACCESS','Deposit Schemes - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(392,6,'FINANCING','ACCESS','FINANCING_ACCESS','Financing - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(393,6,'CONTRACTS','ACCESS','CONTRACTS_ACCESS','Contracts - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(394,6,'SHARIAH_REVIEW','ACCESS','SHARIAH_REVIEW_ACCESS','Shariah Review - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(395,6,'REPORTING_REGULATORY','ACCESS','REPORTING_REGULATORY_ACCESS','Reporting & Regulatory - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(396,6,'WORKFLOW_SUPPORT','ACCESS','WORKFLOW_SUPPORT_ACCESS','Workflow Support - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(397,6,'CALCULATION_ENGINE','ACCESS','CALCULATION_ENGINE_ACCESS','Calculation Engine - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(398,7,'ADMIN','ACCESS','ADMIN_DASHBOARD_ACCESS','Admin Dashboard - Dashboard Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(399,7,'SHARIAH_REVIEW','ACCESS','SHARIAH_REVIEW_ACCESS','Shariah Review - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(400,7,'ZAKAT_CHARITY','ACCESS','ZAKAT_CHARITY_ACCESS','Zakat & Charity - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(401,7,'REPORTING_REGULATORY','ACCESS','REPORTING_REGULATORY_ACCESS','Reporting & Regulatory - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(402,7,'WORKFLOW_SUPPORT','ACCESS','WORKFLOW_SUPPORT_ACCESS','Workflow Support - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(403,7,'CALCULATION_ENGINE','ACCESS','CALCULATION_ENGINE_ACCESS','Calculation Engine - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(404,5,'ADMIN','ACCESS','ADMIN_DASHBOARD_ACCESS','Admin Dashboard - Dashboard Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(405,5,'CUSTOMER_MANAGEMENT','ACCESS','CUSTOMER_MANAGEMENT_ACCESS','Customer Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(406,5,'ACCOUNT_MANAGEMENT','ACCESS','ACCOUNT_MANAGEMENT_ACCESS','Account Management - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(407,5,'STATEMENTS','ACCESS','STATEMENTS_ACCESS','Statements - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(408,5,'DEPOSIT_SCHEMES','ACCESS','DEPOSIT_SCHEMES_ACCESS','Deposit Schemes - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(409,5,'FINANCING','ACCESS','FINANCING_ACCESS','Financing - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(410,5,'CONTRACTS','ACCESS','CONTRACTS_ACCESS','Contracts - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(411,5,'ZAKAT_CHARITY','ACCESS','ZAKAT_CHARITY_ACCESS','Zakat & Charity - Module Access',_binary '','2026-05-08 09:17:45.000000','MODULE_1'),(412,1,'TRANSACTIONS','DEPOSIT','TRANSACTION_DEPOSIT','Transactions - Post Cash Deposit',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(413,1,'TRANSACTIONS','WITHDRAW','TRANSACTION_WITHDRAW','Transactions - Post Cash Withdrawal',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(414,1,'TRANSACTIONS','TRANSFER','TRANSACTION_TRANSFER','Transactions - Post Fund Transfer',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(415,1,'TRANSACTIONS','CHEQUE_CLEARING','TRANSACTION_CHEQUE_CLEARING','Transactions - Post Cheque Clearing',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(416,1,'TRANSACTIONS','STANDING_INSTRUCTION_CREATE','TRANSACTION_STANDING_INSTRUCTION_CREATE','Transactions - Create Standing Instruction',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(417,1,'TRANSACTIONS','REVERSE','TRANSACTION_REVERSE','Transactions - Reverse Transaction',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(418,1,'FINANCING','VERIFY','FINANCING_VERIFY','Financing - Verify Financing Application',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(419,1,'FINANCING','REVIEW','FINANCING_REVIEW','Financing - Send for Shariah Review',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(420,1,'FINANCING','APPROVE','FINANCING_APPROVE','Financing - Approve Financing Application',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(421,1,'FINANCING','REJECT','FINANCING_REJECT','Financing - Reject Financing Application',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(422,1,'FINANCING','RETURN','FINANCING_RETURN','Financing - Return Financing Application',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(423,1,'FINANCING','DISBURSE','FINANCING_DISBURSE','Financing - Disburse Financing',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(424,1,'FINANCING','COLLECT_PAYMENT','FINANCING_COLLECT_PAYMENT','Financing - Collect Financing Repayment',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(425,1,'CONTRACTS','GENERATE','CONTRACT_GENERATE','Contracts - Generate Contract',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(426,1,'CONTRACTS','CUSTOMER_SIGN','CONTRACT_CUSTOMER_SIGN','Contracts - Capture Customer Signature',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(427,1,'CONTRACTS','SHARIAH_SIGN','CONTRACT_SHARIAH_SIGN','Contracts - Capture Shariah Signature',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(428,1,'SHARIAH_REVIEW','CHECKLIST_SAVE','SHARIAH_CHECKLIST_SAVE','Shariah Review - Save Shariah Checklist',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(429,1,'SHARIAH_REVIEW','APPROVE','SHARIAH_APPROVE','Shariah Review - Approve Shariah Review',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(430,1,'SHARIAH_REVIEW','REJECT','SHARIAH_REJECT','Shariah Review - Reject Shariah Review',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(431,1,'SHARIAH_REVIEW','RETURN','SHARIAH_RETURN','Shariah Review - Return Shariah Review',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(432,1,'ZAKAT_CHARITY','CALCULATE','ZAKAT_CALCULATE','Zakat & Charity - Run Zakat Calculation',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(433,1,'ZAKAT_CHARITY','PAYOUT_CREATE','CHARITY_PAYOUT_CREATE','Zakat & Charity - Create Charity Payout',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(434,3,'TRANSACTIONS','DEPOSIT','TRANSACTION_DEPOSIT','Transactions - Post Cash Deposit',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(435,3,'TRANSACTIONS','WITHDRAW','TRANSACTION_WITHDRAW','Transactions - Post Cash Withdrawal',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(436,3,'TRANSACTIONS','TRANSFER','TRANSACTION_TRANSFER','Transactions - Post Fund Transfer',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(437,3,'TRANSACTIONS','CHEQUE_CLEARING','TRANSACTION_CHEQUE_CLEARING','Transactions - Post Cheque Clearing',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(438,3,'TRANSACTIONS','STANDING_INSTRUCTION_CREATE','TRANSACTION_STANDING_INSTRUCTION_CREATE','Transactions - Create Standing Instruction',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(439,4,'TRANSACTIONS','DEPOSIT','TRANSACTION_DEPOSIT','Transactions - Post Cash Deposit',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(440,4,'TRANSACTIONS','WITHDRAW','TRANSACTION_WITHDRAW','Transactions - Post Cash Withdrawal',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(441,4,'TRANSACTIONS','TRANSFER','TRANSACTION_TRANSFER','Transactions - Post Fund Transfer',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(442,4,'TRANSACTIONS','CHEQUE_CLEARING','TRANSACTION_CHEQUE_CLEARING','Transactions - Post Cheque Clearing',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(443,4,'TRANSACTIONS','STANDING_INSTRUCTION_CREATE','TRANSACTION_STANDING_INSTRUCTION_CREATE','Transactions - Create Standing Instruction',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(444,4,'ZAKAT_CHARITY','CALCULATE','ZAKAT_CALCULATE','Zakat & Charity - Run Zakat Calculation',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(445,4,'ZAKAT_CHARITY','PAYOUT_CREATE','CHARITY_PAYOUT_CREATE','Zakat & Charity - Create Charity Payout',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(446,6,'FINANCING','VERIFY','FINANCING_VERIFY','Financing - Verify Financing Application',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(447,6,'FINANCING','REVIEW','FINANCING_REVIEW','Financing - Send for Shariah Review',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(448,6,'FINANCING','APPROVE','FINANCING_APPROVE','Financing - Approve Financing Application',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(449,6,'FINANCING','REJECT','FINANCING_REJECT','Financing - Reject Financing Application',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(450,6,'FINANCING','RETURN','FINANCING_RETURN','Financing - Return Financing Application',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(451,6,'FINANCING','COLLECT_PAYMENT','FINANCING_COLLECT_PAYMENT','Financing - Collect Financing Repayment',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(452,6,'CONTRACTS','GENERATE','CONTRACT_GENERATE','Contracts - Generate Contract',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(453,6,'CONTRACTS','CUSTOMER_SIGN','CONTRACT_CUSTOMER_SIGN','Contracts - Capture Customer Signature',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(454,2,'FINANCING','DISBURSE','FINANCING_DISBURSE','Financing - Disburse Financing',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(455,7,'SHARIAH_REVIEW','CHECKLIST_SAVE','SHARIAH_CHECKLIST_SAVE','Shariah Review - Save Shariah Checklist',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(456,7,'SHARIAH_REVIEW','APPROVE','SHARIAH_APPROVE','Shariah Review - Approve Shariah Review',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(457,7,'SHARIAH_REVIEW','REJECT','SHARIAH_REJECT','Shariah Review - Reject Shariah Review',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(458,7,'SHARIAH_REVIEW','RETURN','SHARIAH_RETURN','Shariah Review - Return Shariah Review',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(459,7,'CONTRACTS','SHARIAH_SIGN','CONTRACT_SHARIAH_SIGN','Contracts - Capture Shariah Signature',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(460,5,'CONTRACTS','CUSTOMER_SIGN','CONTRACT_CUSTOMER_SIGN','Contracts - Capture Customer Signature',_binary '','2026-05-08 14:06:33.000000','RBAC_HARDENING'),(475,2,'FINANCING','ACCESS','FINANCING_ACCESS','Financing - Module Access',_binary '','2026-05-08 14:10:36.000000','RBAC_HARDENING'),(476,4,'ZAKAT_CHARITY','ACCESS','ZAKAT_CHARITY_ACCESS','Zakat & Charity - Module Access',_binary '','2026-05-08 14:10:36.000000','RBAC_HARDENING'),(478,2,'BRANCH_MANAGEMENT','ASSIGN_USER','BRANCH_ASSIGN_USER','Branch Management - Assign Branch User',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(479,2,'BRANCH_MANAGEMENT','TELLER_LIMIT_MANAGE','BRANCH_TELLER_LIMIT_MANAGE','Branch Management - Manage Teller Limit',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(480,2,'BRANCH_MANAGEMENT','VAULT_MANAGE','BRANCH_VAULT_MANAGE','Branch Management - Manage Vault',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(481,2,'ATM_CDM','CASH_BIN_CREATE','ATM_CASH_BIN_CREATE','ATM / CDM - Create Cash Bin',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(482,2,'ATM_CDM','CASH_BIN_EDIT','ATM_CASH_BIN_EDIT','ATM / CDM - Edit Cash Bin',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(483,2,'ATM_CDM','REPLENISHMENT_CREATE','ATM_REPLENISHMENT_CREATE','ATM / CDM - Create Replenishment',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(484,2,'ATM_CDM','RECONCILIATION_CREATE','ATM_RECONCILIATION_CREATE','ATM / CDM - Create Reconciliation',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(485,2,'CUSTOMER_MANAGEMENT','CREATE','CUSTOMER_CREATE','Customer Management - Create Customer',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(486,2,'CUSTOMER_MANAGEMENT','EDIT','CUSTOMER_EDIT','Customer Management - Edit Customer',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(487,2,'CUSTOMER_MANAGEMENT','ACTIVATE','CUSTOMER_ACTIVATE','Customer Management - Activate Customer',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(488,2,'CUSTOMER_MANAGEMENT','BLOCK','CUSTOMER_BLOCK','Customer Management - Block Customer',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(489,2,'CUSTOMER_MANAGEMENT','ADDRESS_MANAGE','CUSTOMER_ADDRESS_MANAGE','Customer Management - Manage Customer Address',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(490,2,'CUSTOMER_MANAGEMENT','IDENTITY_MANAGE','CUSTOMER_IDENTITY_MANAGE','Customer Management - Manage Customer Identity',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(491,2,'KYC_MANAGEMENT','CREATE','KYC_CREATE','KYC Management - Create KYC Profile',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(492,2,'KYC_MANAGEMENT','EDIT','KYC_EDIT','KYC Management - Edit KYC Profile',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(493,2,'KYC_MANAGEMENT','SUBMIT','KYC_SUBMIT','KYC Management - Submit KYC Profile',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(494,2,'KYC_MANAGEMENT','VERIFY','KYC_VERIFY','KYC Management - Verify KYC Profile',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(495,2,'KYC_MANAGEMENT','DOCUMENT_UPLOAD','KYC_DOCUMENT_UPLOAD','KYC Management - Upload KYC Document',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(496,2,'ACCOUNT_MANAGEMENT','REQUEST_CREATE','ACCOUNT_REQUEST_CREATE','Account Management - Create Account Opening Request',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(497,2,'ACCOUNT_MANAGEMENT','REQUEST_EDIT','ACCOUNT_REQUEST_EDIT','Account Management - Edit Account Opening Request',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(498,2,'ACCOUNT_MANAGEMENT','REQUEST_SUBMIT','ACCOUNT_REQUEST_SUBMIT','Account Management - Submit Account Opening Request',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(499,2,'ACCOUNT_MANAGEMENT','REQUEST_VERIFY','ACCOUNT_REQUEST_VERIFY','Account Management - Verify Account Opening Request',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(500,2,'ACCOUNT_MANAGEMENT','ACCOUNT_ACTIVATE','ACCOUNT_ACTIVATE','Account Management - Activate Account',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(501,2,'ACCOUNT_MANAGEMENT','ACCOUNT_BLOCK','ACCOUNT_BLOCK','Account Management - Block Account',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(502,2,'ACCOUNT_MANAGEMENT','ACCOUNT_FREEZE','ACCOUNT_FREEZE','Account Management - Freeze Account',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(503,2,'CARD_MANAGEMENT','CREATE','CARD_CREATE','Card Management - Create Card',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(504,2,'CARD_MANAGEMENT','EDIT','CARD_EDIT','Card Management - Edit Card',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(505,2,'CARD_MANAGEMENT','ACTIVATE','CARD_ACTIVATE','Card Management - Activate Card',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(506,2,'CARD_MANAGEMENT','BLOCK','CARD_BLOCK','Card Management - Block Card',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(507,2,'CARD_MANAGEMENT','UNBLOCK','CARD_UNBLOCK','Card Management - Unblock Card',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(508,2,'CARD_MANAGEMENT','RENEW','CARD_RENEW','Card Management - Renew Card',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(509,2,'CARD_MANAGEMENT','PIN_EVENT','CARD_PIN_EVENT','Card Management - Record Card PIN Event',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(510,2,'STATEMENTS','CUSTOMER_REQUEST','STATEMENT_CUSTOMER_REQUEST','Statements - Request Customer Statement',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(511,2,'STATEMENTS','BRANCH_REQUEST','STATEMENT_BRANCH_REQUEST','Statements - Request Branch Statement',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(512,2,'FINANCING','APPLICATION_CREATE','FINANCING_APPLICATION_CREATE','Financing - Create Financing Application',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(513,2,'FINANCING','APPLICATION_EDIT','FINANCING_APPLICATION_EDIT','Financing - Edit Financing Application',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(514,2,'FINANCING','APPLICATION_SUBMIT','FINANCING_APPLICATION_SUBMIT','Financing - Submit Financing Application',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(515,3,'CUSTOMER_MANAGEMENT','CREATE','CUSTOMER_CREATE','Customer Management - Create Customer',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(516,3,'CUSTOMER_MANAGEMENT','EDIT','CUSTOMER_EDIT','Customer Management - Edit Customer',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(517,3,'CUSTOMER_MANAGEMENT','ADDRESS_MANAGE','CUSTOMER_ADDRESS_MANAGE','Customer Management - Manage Customer Address',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(518,3,'CUSTOMER_MANAGEMENT','IDENTITY_MANAGE','CUSTOMER_IDENTITY_MANAGE','Customer Management - Manage Customer Identity',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(519,3,'ACCOUNT_MANAGEMENT','REQUEST_CREATE','ACCOUNT_REQUEST_CREATE','Account Management - Create Account Opening Request',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(520,3,'ACCOUNT_MANAGEMENT','REQUEST_EDIT','ACCOUNT_REQUEST_EDIT','Account Management - Edit Account Opening Request',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(521,3,'ACCOUNT_MANAGEMENT','REQUEST_SUBMIT','ACCOUNT_REQUEST_SUBMIT','Account Management - Submit Account Opening Request',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(522,3,'STATEMENTS','CUSTOMER_REQUEST','STATEMENT_CUSTOMER_REQUEST','Statements - Request Customer Statement',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(523,4,'BRANCH_MANAGEMENT','ASSIGN_USER','BRANCH_ASSIGN_USER','Branch Management - Assign Branch User',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(524,4,'BRANCH_MANAGEMENT','TELLER_LIMIT_MANAGE','BRANCH_TELLER_LIMIT_MANAGE','Branch Management - Manage Teller Limit',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(525,4,'BRANCH_MANAGEMENT','VAULT_MANAGE','BRANCH_VAULT_MANAGE','Branch Management - Manage Vault',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(526,4,'ATM_CDM','TERMINAL_CREATE','ATM_TERMINAL_CREATE','ATM / CDM - Create ATM/CDM Terminal',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(527,4,'ATM_CDM','TERMINAL_EDIT','ATM_TERMINAL_EDIT','ATM / CDM - Edit ATM/CDM Terminal',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(528,4,'ATM_CDM','CASH_BIN_CREATE','ATM_CASH_BIN_CREATE','ATM / CDM - Create Cash Bin',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(529,4,'ATM_CDM','CASH_BIN_EDIT','ATM_CASH_BIN_EDIT','ATM / CDM - Edit Cash Bin',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(530,4,'ATM_CDM','REPLENISHMENT_CREATE','ATM_REPLENISHMENT_CREATE','ATM / CDM - Create Replenishment',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(531,4,'ATM_CDM','RECONCILIATION_CREATE','ATM_RECONCILIATION_CREATE','ATM / CDM - Create Reconciliation',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(532,4,'CUSTOMER_MANAGEMENT','CREATE','CUSTOMER_CREATE','Customer Management - Create Customer',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(533,4,'CUSTOMER_MANAGEMENT','EDIT','CUSTOMER_EDIT','Customer Management - Edit Customer',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(534,4,'CUSTOMER_MANAGEMENT','ACTIVATE','CUSTOMER_ACTIVATE','Customer Management - Activate Customer',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(535,4,'CUSTOMER_MANAGEMENT','BLOCK','CUSTOMER_BLOCK','Customer Management - Block Customer',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(536,4,'CUSTOMER_MANAGEMENT','ADDRESS_MANAGE','CUSTOMER_ADDRESS_MANAGE','Customer Management - Manage Customer Address',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(537,4,'CUSTOMER_MANAGEMENT','IDENTITY_MANAGE','CUSTOMER_IDENTITY_MANAGE','Customer Management - Manage Customer Identity',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(538,4,'KYC_MANAGEMENT','CREATE','KYC_CREATE','KYC Management - Create KYC Profile',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(539,4,'KYC_MANAGEMENT','EDIT','KYC_EDIT','KYC Management - Edit KYC Profile',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(540,4,'KYC_MANAGEMENT','SUBMIT','KYC_SUBMIT','KYC Management - Submit KYC Profile',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(541,4,'KYC_MANAGEMENT','VERIFY','KYC_VERIFY','KYC Management - Verify KYC Profile',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(542,4,'KYC_MANAGEMENT','APPROVE','KYC_APPROVE','KYC Management - Approve KYC Profile',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(543,4,'KYC_MANAGEMENT','REJECT','KYC_REJECT','KYC Management - Reject KYC Profile',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(544,4,'KYC_MANAGEMENT','RETURN','KYC_RETURN','KYC Management - Return KYC Profile',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(545,4,'KYC_MANAGEMENT','DOCUMENT_UPLOAD','KYC_DOCUMENT_UPLOAD','KYC Management - Upload KYC Document',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(546,4,'CARD_MANAGEMENT','CREATE','CARD_CREATE','Card Management - Create Card',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(547,4,'CARD_MANAGEMENT','EDIT','CARD_EDIT','Card Management - Edit Card',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(548,4,'CARD_MANAGEMENT','ACTIVATE','CARD_ACTIVATE','Card Management - Activate Card',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(549,4,'CARD_MANAGEMENT','BLOCK','CARD_BLOCK','Card Management - Block Card',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(550,4,'CARD_MANAGEMENT','UNBLOCK','CARD_UNBLOCK','Card Management - Unblock Card',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(551,4,'CARD_MANAGEMENT','REPLACE','CARD_REPLACE','Card Management - Replace Card',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(552,4,'CARD_MANAGEMENT','RENEW','CARD_RENEW','Card Management - Renew Card',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(553,4,'CARD_MANAGEMENT','PIN_EVENT','CARD_PIN_EVENT','Card Management - Record Card PIN Event',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(554,4,'PROFIT_MANAGEMENT','RATIO_CREATE','PROFIT_RATIO_CREATE','Profit Management - Create Profit Ratio',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(555,4,'PROFIT_MANAGEMENT','RATIO_EDIT','PROFIT_RATIO_EDIT','Profit Management - Edit Profit Ratio',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(556,4,'PROFIT_MANAGEMENT','SCHEDULE_CREATE','PROFIT_SCHEDULE_CREATE','Profit Management - Create Profit Schedule',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(557,4,'PROFIT_MANAGEMENT','POSTING_RUN','PROFIT_POSTING_RUN','Profit Management - Run Profit Posting',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(558,4,'STATEMENTS','CUSTOMER_REQUEST','STATEMENT_CUSTOMER_REQUEST','Statements - Request Customer Statement',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(559,4,'STATEMENTS','BRANCH_REQUEST','STATEMENT_BRANCH_REQUEST','Statements - Request Branch Statement',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(560,4,'NOTIFICATION_ALERTS','TEMPLATE_CREATE','NOTIFICATION_TEMPLATE_CREATE','Notification & Alerts - Create Notification Template',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(561,4,'NOTIFICATION_ALERTS','TEMPLATE_EDIT','NOTIFICATION_TEMPLATE_EDIT','Notification & Alerts - Edit Notification Template',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(562,4,'NOTIFICATION_ALERTS','EVENT_RULE_CREATE','NOTIFICATION_EVENT_RULE_CREATE','Notification & Alerts - Create Notification Event Rule',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(563,4,'NOTIFICATION_ALERTS','RETRY','NOTIFICATION_RETRY','Notification & Alerts - Retry Notification Delivery',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(564,4,'VERIFICATION','SEND_EMAIL_OTP','VERIFICATION_SEND_EMAIL_OTP','Verification - Send Email Verification OTP',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(565,4,'VERIFICATION','SEND_MOBILE_OTP','VERIFICATION_SEND_MOBILE_OTP','Verification - Send Mobile Verification OTP',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(566,4,'VERIFICATION','VERIFY_OTP','VERIFICATION_VERIFY_OTP','Verification - Verify OTP',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(567,4,'VERIFICATION','RESEND_OTP','VERIFICATION_RESEND_OTP','Verification - Resend OTP',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(568,4,'VERIFICATION','PROVIDER_TEST','VERIFICATION_PROVIDER_TEST','Verification - Run Verification Provider Test',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(569,4,'ZAKAT_CHARITY','PROFILE_CREATE','ZAKAT_PROFILE_CREATE','Zakat & Charity - Create Zakat Profile',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(570,4,'ZAKAT_CHARITY','PROFILE_EDIT','ZAKAT_PROFILE_EDIT','Zakat & Charity - Edit Zakat Profile',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(571,4,'ZAKAT_CHARITY','BENEFICIARY_CREATE','CHARITY_BENEFICIARY_CREATE','Zakat & Charity - Create Charity Beneficiary',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(572,4,'ZAKAT_CHARITY','BENEFICIARY_EDIT','CHARITY_BENEFICIARY_EDIT','Zakat & Charity - Edit Charity Beneficiary',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(573,4,'ZAKAT_CHARITY','BENEFICIARY_ARCHIVE','CHARITY_BENEFICIARY_ARCHIVE','Zakat & Charity - Archive Charity Beneficiary',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(574,4,'ZAKAT_CHARITY','BENEFICIARY_RESTORE','CHARITY_BENEFICIARY_RESTORE','Zakat & Charity - Restore Charity Beneficiary',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(575,6,'CALCULATION_ENGINE','SIMULATE','CALCULATION_SIMULATE','Calculation Engine - Run Calculation Simulation',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(576,6,'ACCOUNT_MANAGEMENT','REQUEST_CREATE','ACCOUNT_REQUEST_CREATE','Account Management - Create Account Opening Request',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(577,6,'ACCOUNT_MANAGEMENT','REQUEST_EDIT','ACCOUNT_REQUEST_EDIT','Account Management - Edit Account Opening Request',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(578,6,'ACCOUNT_MANAGEMENT','REQUEST_SUBMIT','ACCOUNT_REQUEST_SUBMIT','Account Management - Submit Account Opening Request',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(579,6,'ACCOUNT_MANAGEMENT','REQUEST_VERIFY','ACCOUNT_REQUEST_VERIFY','Account Management - Verify Account Opening Request',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(580,6,'ACCOUNT_MANAGEMENT','REQUEST_APPROVE','ACCOUNT_REQUEST_APPROVE','Account Management - Approve Account Opening Request',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(581,6,'ACCOUNT_MANAGEMENT','REQUEST_REJECT','ACCOUNT_REQUEST_REJECT','Account Management - Reject Account Opening Request',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(582,6,'ACCOUNT_MANAGEMENT','REQUEST_RETURN','ACCOUNT_REQUEST_RETURN','Account Management - Return Account Opening Request',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(583,6,'DEPOSIT_SCHEMES','SCHEME_CREATE','DEPOSIT_SCHEME_CREATE','Deposit Schemes - Create Deposit Scheme',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(584,6,'DEPOSIT_SCHEMES','SCHEME_EDIT','DEPOSIT_SCHEME_EDIT','Deposit Schemes - Edit Deposit Scheme',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(585,6,'DEPOSIT_SCHEMES','SCHEME_ARCHIVE','DEPOSIT_SCHEME_ARCHIVE','Deposit Schemes - Archive Deposit Scheme',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(586,6,'DEPOSIT_SCHEMES','SCHEME_RESTORE','DEPOSIT_SCHEME_RESTORE','Deposit Schemes - Restore Deposit Scheme',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(587,6,'DEPOSIT_SCHEMES','ENROLLMENT_CREATE','DEPOSIT_SCHEME_ENROLLMENT_CREATE','Deposit Schemes - Create Deposit Scheme Enrollment',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(588,6,'FINANCING','PRODUCT_CREATE','FINANCING_PRODUCT_CREATE','Financing - Create Financing Product',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(589,6,'FINANCING','PRODUCT_EDIT','FINANCING_PRODUCT_EDIT','Financing - Edit Financing Product',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(590,6,'FINANCING','PRODUCT_ARCHIVE','FINANCING_PRODUCT_ARCHIVE','Financing - Archive Financing Product',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(591,6,'FINANCING','PRODUCT_RESTORE','FINANCING_PRODUCT_RESTORE','Financing - Restore Financing Product',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(592,6,'FINANCING','APPLICATION_CREATE','FINANCING_APPLICATION_CREATE','Financing - Create Financing Application',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(593,6,'FINANCING','APPLICATION_EDIT','FINANCING_APPLICATION_EDIT','Financing - Edit Financing Application',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(594,6,'FINANCING','APPLICATION_SUBMIT','FINANCING_APPLICATION_SUBMIT','Financing - Submit Financing Application',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(595,6,'FINANCING','DISBURSE','FINANCING_DISBURSE','Financing - Disburse Financing',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(596,6,'PROFIT_MANAGEMENT','RATIO_CREATE','PROFIT_RATIO_CREATE','Profit Management - Create Profit Ratio',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(597,6,'PROFIT_MANAGEMENT','RATIO_EDIT','PROFIT_RATIO_EDIT','Profit Management - Edit Profit Ratio',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(598,6,'PROFIT_MANAGEMENT','RATIO_ARCHIVE','PROFIT_RATIO_ARCHIVE','Profit Management - Archive Profit Ratio',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(599,6,'PROFIT_MANAGEMENT','RATIO_RESTORE','PROFIT_RATIO_RESTORE','Profit Management - Restore Profit Ratio',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(600,6,'PROFIT_MANAGEMENT','SCHEDULE_CREATE','PROFIT_SCHEDULE_CREATE','Profit Management - Create Profit Schedule',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(601,6,'PROFIT_MANAGEMENT','SCHEDULE_ARCHIVE','PROFIT_SCHEDULE_ARCHIVE','Profit Management - Archive Profit Schedule',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(602,6,'PROFIT_MANAGEMENT','SCHEDULE_RESTORE','PROFIT_SCHEDULE_RESTORE','Profit Management - Restore Profit Schedule',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(603,6,'PROFIT_MANAGEMENT','POSTING_RUN','PROFIT_POSTING_RUN','Profit Management - Run Profit Posting',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(604,6,'CONTRACTS','TEMPLATE_CREATE','CONTRACT_TEMPLATE_CREATE','Contracts - Create Contract Template',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(605,6,'CONTRACTS','TEMPLATE_EDIT','CONTRACT_TEMPLATE_EDIT','Contracts - Edit Contract Template',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(606,6,'CONTRACTS','TEMPLATE_ARCHIVE','CONTRACT_TEMPLATE_ARCHIVE','Contracts - Archive Contract Template',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(607,6,'CONTRACTS','TEMPLATE_RESTORE','CONTRACT_TEMPLATE_RESTORE','Contracts - Restore Contract Template',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(608,5,'STATEMENTS','CUSTOMER_REQUEST','STATEMENT_CUSTOMER_REQUEST','Statements - Request Customer Statement',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(609,5,'DEPOSIT_SCHEMES','ENROLLMENT_CREATE','DEPOSIT_SCHEME_ENROLLMENT_CREATE','Deposit Schemes - Create Deposit Scheme Enrollment',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(610,5,'ZAKAT_CHARITY','PROFILE_CREATE','ZAKAT_PROFILE_CREATE','Zakat & Charity - Create Zakat Profile',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(611,5,'ZAKAT_CHARITY','PROFILE_EDIT','ZAKAT_PROFILE_EDIT','Zakat & Charity - Edit Zakat Profile',_binary '','2026-05-09 01:28:28.137629','RBAC_DEFAULT_MATRIX'),(733,1,'LOOKUP_CONFIG','TYPE_CREATE','LOOKUP_TYPE_CREATE','Lookup / Config - Create Lookup Type',_binary '','2026-05-09 01:40:59.540948','SYSTEM_SYNC'),(734,1,'LOOKUP_CONFIG','TYPE_EDIT','LOOKUP_TYPE_EDIT','Lookup / Config - Edit Lookup Type',_binary '','2026-05-09 01:40:59.814948','SYSTEM_SYNC'),(735,1,'LOOKUP_CONFIG','TYPE_ARCHIVE','LOOKUP_TYPE_ARCHIVE','Lookup / Config - Archive Lookup Type',_binary '','2026-05-09 01:40:59.843949','SYSTEM_SYNC'),(736,1,'LOOKUP_CONFIG','TYPE_RESTORE','LOOKUP_TYPE_RESTORE','Lookup / Config - Restore Lookup Type',_binary '','2026-05-09 01:40:59.861947','SYSTEM_SYNC'),(737,1,'LOOKUP_CONFIG','VALUE_CREATE','LOOKUP_VALUE_CREATE','Lookup / Config - Create Lookup Value',_binary '','2026-05-09 01:40:59.881037','SYSTEM_SYNC'),(738,1,'LOOKUP_CONFIG','VALUE_EDIT','LOOKUP_VALUE_EDIT','Lookup / Config - Edit Lookup Value',_binary '','2026-05-09 01:40:59.897950','SYSTEM_SYNC'),(739,1,'LOOKUP_CONFIG','VALUE_ARCHIVE','LOOKUP_VALUE_ARCHIVE','Lookup / Config - Archive Lookup Value',_binary '','2026-05-09 01:40:59.921026','SYSTEM_SYNC'),(740,1,'LOOKUP_CONFIG','VALUE_RESTORE','LOOKUP_VALUE_RESTORE','Lookup / Config - Restore Lookup Value',_binary '','2026-05-09 01:40:59.954023','SYSTEM_SYNC'),(741,1,'BRANCH_MANAGEMENT','CREATE','BRANCH_CREATE','Branch Management - Create Branch',_binary '','2026-05-09 01:40:59.975031','SYSTEM_SYNC'),(742,1,'BRANCH_MANAGEMENT','EDIT','BRANCH_EDIT','Branch Management - Edit Branch',_binary '','2026-05-09 01:41:00.001205','SYSTEM_SYNC'),(743,1,'BRANCH_MANAGEMENT','ARCHIVE','BRANCH_ARCHIVE','Branch Management - Archive Branch',_binary '','2026-05-09 01:41:00.033024','SYSTEM_SYNC'),(744,1,'BRANCH_MANAGEMENT','RESTORE','BRANCH_RESTORE','Branch Management - Restore Branch',_binary '','2026-05-09 01:41:00.050025','SYSTEM_SYNC'),(745,1,'BRANCH_MANAGEMENT','ASSIGN_USER','BRANCH_ASSIGN_USER','Branch Management - Assign Branch User',_binary '','2026-05-09 01:41:00.082024','SYSTEM_SYNC'),(746,1,'BRANCH_MANAGEMENT','TELLER_LIMIT_MANAGE','BRANCH_TELLER_LIMIT_MANAGE','Branch Management - Manage Teller Limit',_binary '','2026-05-09 01:41:00.248024','SYSTEM_SYNC'),(747,1,'BRANCH_MANAGEMENT','VAULT_MANAGE','BRANCH_VAULT_MANAGE','Branch Management - Manage Vault',_binary '','2026-05-09 01:41:00.400024','SYSTEM_SYNC'),(748,1,'ATM_CDM','TERMINAL_CREATE','ATM_TERMINAL_CREATE','ATM / CDM - Create ATM/CDM Terminal',_binary '','2026-05-09 01:41:00.508024','SYSTEM_SYNC'),(749,1,'ATM_CDM','TERMINAL_EDIT','ATM_TERMINAL_EDIT','ATM / CDM - Edit ATM/CDM Terminal',_binary '','2026-05-09 01:41:00.574024','SYSTEM_SYNC'),(750,1,'ATM_CDM','TERMINAL_ARCHIVE','ATM_TERMINAL_ARCHIVE','ATM / CDM - Archive ATM/CDM Terminal',_binary '','2026-05-09 01:41:00.697026','SYSTEM_SYNC'),(751,1,'ATM_CDM','TERMINAL_RESTORE','ATM_TERMINAL_RESTORE','ATM / CDM - Restore ATM/CDM Terminal',_binary '','2026-05-09 01:41:00.832023','SYSTEM_SYNC'),(752,1,'ATM_CDM','CASH_BIN_CREATE','ATM_CASH_BIN_CREATE','ATM / CDM - Create Cash Bin',_binary '','2026-05-09 01:41:00.849025','SYSTEM_SYNC'),(753,1,'ATM_CDM','CASH_BIN_EDIT','ATM_CASH_BIN_EDIT','ATM / CDM - Edit Cash Bin',_binary '','2026-05-09 01:41:00.866026','SYSTEM_SYNC'),(754,1,'ATM_CDM','CASH_BIN_ARCHIVE','ATM_CASH_BIN_ARCHIVE','ATM / CDM - Archive Cash Bin',_binary '','2026-05-09 01:41:00.898026','SYSTEM_SYNC'),(755,1,'ATM_CDM','CASH_BIN_RESTORE','ATM_CASH_BIN_RESTORE','ATM / CDM - Restore Cash Bin',_binary '','2026-05-09 01:41:00.920024','SYSTEM_SYNC'),(756,1,'ATM_CDM','REPLENISHMENT_CREATE','ATM_REPLENISHMENT_CREATE','ATM / CDM - Create Replenishment',_binary '','2026-05-09 01:41:00.943026','SYSTEM_SYNC'),(757,1,'ATM_CDM','RECONCILIATION_CREATE','ATM_RECONCILIATION_CREATE','ATM / CDM - Create Reconciliation',_binary '','2026-05-09 01:41:00.966026','SYSTEM_SYNC'),(758,1,'CUSTOMER_MANAGEMENT','CREATE','CUSTOMER_CREATE','Customer Management - Create Customer',_binary '','2026-05-09 01:41:00.987028','SYSTEM_SYNC'),(759,1,'CUSTOMER_MANAGEMENT','EDIT','CUSTOMER_EDIT','Customer Management - Edit Customer',_binary '','2026-05-09 01:41:01.009024','SYSTEM_SYNC'),(760,1,'CUSTOMER_MANAGEMENT','ARCHIVE','CUSTOMER_ARCHIVE','Customer Management - Archive Customer',_binary '','2026-05-09 01:41:01.026024','SYSTEM_SYNC'),(761,1,'CUSTOMER_MANAGEMENT','RESTORE','CUSTOMER_RESTORE','Customer Management - Restore Customer',_binary '','2026-05-09 01:41:01.044023','SYSTEM_SYNC'),(762,1,'CUSTOMER_MANAGEMENT','ACTIVATE','CUSTOMER_ACTIVATE','Customer Management - Activate Customer',_binary '','2026-05-09 01:41:01.061023','SYSTEM_SYNC'),(763,1,'CUSTOMER_MANAGEMENT','BLOCK','CUSTOMER_BLOCK','Customer Management - Block Customer',_binary '','2026-05-09 01:41:01.082024','SYSTEM_SYNC'),(764,1,'CUSTOMER_MANAGEMENT','ADDRESS_MANAGE','CUSTOMER_ADDRESS_MANAGE','Customer Management - Manage Customer Address',_binary '','2026-05-09 01:41:01.099025','SYSTEM_SYNC'),(765,1,'CUSTOMER_MANAGEMENT','IDENTITY_MANAGE','CUSTOMER_IDENTITY_MANAGE','Customer Management - Manage Customer Identity',_binary '','2026-05-09 01:41:01.126027','SYSTEM_SYNC'),(766,1,'KYC_MANAGEMENT','CREATE','KYC_CREATE','KYC Management - Create KYC Profile',_binary '','2026-05-09 01:41:01.160025','SYSTEM_SYNC'),(767,1,'KYC_MANAGEMENT','EDIT','KYC_EDIT','KYC Management - Edit KYC Profile',_binary '','2026-05-09 01:41:01.181292','SYSTEM_SYNC'),(768,1,'KYC_MANAGEMENT','SUBMIT','KYC_SUBMIT','KYC Management - Submit KYC Profile',_binary '','2026-05-09 01:41:01.199026','SYSTEM_SYNC'),(769,1,'KYC_MANAGEMENT','VERIFY','KYC_VERIFY','KYC Management - Verify KYC Profile',_binary '','2026-05-09 01:41:01.215023','SYSTEM_SYNC'),(770,1,'KYC_MANAGEMENT','APPROVE','KYC_APPROVE','KYC Management - Approve KYC Profile',_binary '','2026-05-09 01:41:01.233023','SYSTEM_SYNC'),(771,1,'KYC_MANAGEMENT','REJECT','KYC_REJECT','KYC Management - Reject KYC Profile',_binary '','2026-05-09 01:41:01.257025','SYSTEM_SYNC'),(772,1,'KYC_MANAGEMENT','RETURN','KYC_RETURN','KYC Management - Return KYC Profile',_binary '','2026-05-09 01:41:01.274025','SYSTEM_SYNC'),(773,1,'KYC_MANAGEMENT','DOCUMENT_UPLOAD','KYC_DOCUMENT_UPLOAD','KYC Management - Upload KYC Document',_binary '','2026-05-09 01:41:01.292024','SYSTEM_SYNC'),(774,1,'ACCOUNT_MANAGEMENT','TYPE_CREATE','ACCOUNT_TYPE_CREATE','Account Management - Create Account Type',_binary '','2026-05-09 01:41:01.309028','SYSTEM_SYNC'),(775,1,'ACCOUNT_MANAGEMENT','TYPE_EDIT','ACCOUNT_TYPE_EDIT','Account Management - Edit Account Type',_binary '','2026-05-09 01:41:01.326027','SYSTEM_SYNC'),(776,1,'ACCOUNT_MANAGEMENT','TYPE_ARCHIVE','ACCOUNT_TYPE_ARCHIVE','Account Management - Archive Account Type',_binary '','2026-05-09 01:41:01.343023','SYSTEM_SYNC'),(777,1,'ACCOUNT_MANAGEMENT','TYPE_RESTORE','ACCOUNT_TYPE_RESTORE','Account Management - Restore Account Type',_binary '','2026-05-09 01:41:01.365025','SYSTEM_SYNC'),(778,1,'ACCOUNT_MANAGEMENT','REQUEST_CREATE','ACCOUNT_REQUEST_CREATE','Account Management - Create Account Opening Request',_binary '','2026-05-09 01:41:01.381027','SYSTEM_SYNC'),(779,1,'ACCOUNT_MANAGEMENT','REQUEST_EDIT','ACCOUNT_REQUEST_EDIT','Account Management - Edit Account Opening Request',_binary '','2026-05-09 01:41:01.400026','SYSTEM_SYNC'),(780,1,'ACCOUNT_MANAGEMENT','REQUEST_SUBMIT','ACCOUNT_REQUEST_SUBMIT','Account Management - Submit Account Opening Request',_binary '','2026-05-09 01:41:01.424035','SYSTEM_SYNC'),(781,1,'ACCOUNT_MANAGEMENT','REQUEST_VERIFY','ACCOUNT_REQUEST_VERIFY','Account Management - Verify Account Opening Request',_binary '','2026-05-09 01:41:01.448025','SYSTEM_SYNC'),(782,1,'ACCOUNT_MANAGEMENT','REQUEST_APPROVE','ACCOUNT_REQUEST_APPROVE','Account Management - Approve Account Opening Request',_binary '','2026-05-09 01:41:01.466024','SYSTEM_SYNC'),(783,1,'ACCOUNT_MANAGEMENT','REQUEST_REJECT','ACCOUNT_REQUEST_REJECT','Account Management - Reject Account Opening Request',_binary '','2026-05-09 01:41:01.483024','SYSTEM_SYNC'),(784,1,'ACCOUNT_MANAGEMENT','REQUEST_RETURN','ACCOUNT_REQUEST_RETURN','Account Management - Return Account Opening Request',_binary '','2026-05-09 01:41:01.499023','SYSTEM_SYNC'),(785,1,'ACCOUNT_MANAGEMENT','ACCOUNT_ACTIVATE','ACCOUNT_ACTIVATE','Account Management - Activate Account',_binary '','2026-05-09 01:41:01.517025','SYSTEM_SYNC'),(786,1,'ACCOUNT_MANAGEMENT','ACCOUNT_BLOCK','ACCOUNT_BLOCK','Account Management - Block Account',_binary '','2026-05-09 01:41:01.976028','SYSTEM_SYNC'),(787,1,'ACCOUNT_MANAGEMENT','ACCOUNT_FREEZE','ACCOUNT_FREEZE','Account Management - Freeze Account',_binary '','2026-05-09 01:41:01.993025','SYSTEM_SYNC'),(788,1,'ACCOUNT_MANAGEMENT','ACCOUNT_CLOSE','ACCOUNT_CLOSE','Account Management - Close Account',_binary '','2026-05-09 01:41:02.009027','SYSTEM_SYNC'),(789,1,'PROFIT_MANAGEMENT','RATIO_CREATE','PROFIT_RATIO_CREATE','Profit Management - Create Profit Ratio',_binary '','2026-05-09 01:41:02.026025','SYSTEM_SYNC'),(790,1,'PROFIT_MANAGEMENT','RATIO_EDIT','PROFIT_RATIO_EDIT','Profit Management - Edit Profit Ratio',_binary '','2026-05-09 01:41:02.043025','SYSTEM_SYNC'),(791,1,'PROFIT_MANAGEMENT','RATIO_ARCHIVE','PROFIT_RATIO_ARCHIVE','Profit Management - Archive Profit Ratio',_binary '','2026-05-09 01:41:02.060025','SYSTEM_SYNC'),(792,1,'PROFIT_MANAGEMENT','RATIO_RESTORE','PROFIT_RATIO_RESTORE','Profit Management - Restore Profit Ratio',_binary '','2026-05-09 01:41:02.076027','SYSTEM_SYNC'),(793,1,'PROFIT_MANAGEMENT','SCHEDULE_CREATE','PROFIT_SCHEDULE_CREATE','Profit Management - Create Profit Schedule',_binary '','2026-05-09 01:41:02.093027','SYSTEM_SYNC'),(794,1,'PROFIT_MANAGEMENT','SCHEDULE_ARCHIVE','PROFIT_SCHEDULE_ARCHIVE','Profit Management - Archive Profit Schedule',_binary '','2026-05-09 01:41:02.109026','SYSTEM_SYNC'),(795,1,'PROFIT_MANAGEMENT','SCHEDULE_RESTORE','PROFIT_SCHEDULE_RESTORE','Profit Management - Restore Profit Schedule',_binary '','2026-05-09 01:41:02.126027','SYSTEM_SYNC'),(796,1,'PROFIT_MANAGEMENT','POSTING_RUN','PROFIT_POSTING_RUN','Profit Management - Run Profit Posting',_binary '','2026-05-09 01:41:02.143026','SYSTEM_SYNC'),(797,1,'CARD_MANAGEMENT','CREATE','CARD_CREATE','Card Management - Create Card',_binary '','2026-05-09 01:41:02.166025','SYSTEM_SYNC'),(798,1,'CARD_MANAGEMENT','EDIT','CARD_EDIT','Card Management - Edit Card',_binary '','2026-05-09 01:41:02.190025','SYSTEM_SYNC'),(799,1,'CARD_MANAGEMENT','ARCHIVE','CARD_ARCHIVE','Card Management - Archive Card',_binary '','2026-05-09 01:41:02.210027','SYSTEM_SYNC'),(800,1,'CARD_MANAGEMENT','RESTORE','CARD_RESTORE','Card Management - Restore Card',_binary '','2026-05-09 01:41:02.227026','SYSTEM_SYNC'),(801,1,'CARD_MANAGEMENT','ACTIVATE','CARD_ACTIVATE','Card Management - Activate Card',_binary '','2026-05-09 01:41:02.244025','SYSTEM_SYNC'),(802,1,'CARD_MANAGEMENT','BLOCK','CARD_BLOCK','Card Management - Block Card',_binary '','2026-05-09 01:41:02.263025','SYSTEM_SYNC'),(803,1,'CARD_MANAGEMENT','UNBLOCK','CARD_UNBLOCK','Card Management - Unblock Card',_binary '','2026-05-09 01:41:02.281025','SYSTEM_SYNC'),(804,1,'CARD_MANAGEMENT','REPLACE','CARD_REPLACE','Card Management - Replace Card',_binary '','2026-05-09 01:41:02.300026','SYSTEM_SYNC'),(805,1,'CARD_MANAGEMENT','RENEW','CARD_RENEW','Card Management - Renew Card',_binary '','2026-05-09 01:41:02.323028','SYSTEM_SYNC'),(806,1,'CARD_MANAGEMENT','PIN_EVENT','CARD_PIN_EVENT','Card Management - Record Card PIN Event',_binary '','2026-05-09 01:41:02.357024','SYSTEM_SYNC'),(807,1,'STATEMENTS','CUSTOMER_REQUEST','STATEMENT_CUSTOMER_REQUEST','Statements - Request Customer Statement',_binary '','2026-05-09 01:41:02.375025','SYSTEM_SYNC'),(808,1,'STATEMENTS','BRANCH_REQUEST','STATEMENT_BRANCH_REQUEST','Statements - Request Branch Statement',_binary '','2026-05-09 01:41:02.392026','SYSTEM_SYNC'),(809,1,'DEPOSIT_SCHEMES','SCHEME_CREATE','DEPOSIT_SCHEME_CREATE','Deposit Schemes - Create Deposit Scheme',_binary '','2026-05-09 01:41:02.409027','SYSTEM_SYNC'),(810,1,'DEPOSIT_SCHEMES','SCHEME_EDIT','DEPOSIT_SCHEME_EDIT','Deposit Schemes - Edit Deposit Scheme',_binary '','2026-05-09 01:41:02.426024','SYSTEM_SYNC'),(811,1,'DEPOSIT_SCHEMES','SCHEME_ARCHIVE','DEPOSIT_SCHEME_ARCHIVE','Deposit Schemes - Archive Deposit Scheme',_binary '','2026-05-09 01:41:02.444024','SYSTEM_SYNC'),(812,1,'DEPOSIT_SCHEMES','SCHEME_RESTORE','DEPOSIT_SCHEME_RESTORE','Deposit Schemes - Restore Deposit Scheme',_binary '','2026-05-09 01:41:02.465025','SYSTEM_SYNC'),(813,1,'DEPOSIT_SCHEMES','ENROLLMENT_CREATE','DEPOSIT_SCHEME_ENROLLMENT_CREATE','Deposit Schemes - Create Deposit Scheme Enrollment',_binary '','2026-05-09 01:41:02.482025','SYSTEM_SYNC'),(814,1,'FINANCING','PRODUCT_CREATE','FINANCING_PRODUCT_CREATE','Financing - Create Financing Product',_binary '','2026-05-09 01:41:02.499024','SYSTEM_SYNC'),(815,1,'FINANCING','PRODUCT_EDIT','FINANCING_PRODUCT_EDIT','Financing - Edit Financing Product',_binary '','2026-05-09 01:41:02.515028','SYSTEM_SYNC'),(816,1,'FINANCING','PRODUCT_ARCHIVE','FINANCING_PRODUCT_ARCHIVE','Financing - Archive Financing Product',_binary '','2026-05-09 01:41:02.538027','SYSTEM_SYNC'),(817,1,'FINANCING','PRODUCT_RESTORE','FINANCING_PRODUCT_RESTORE','Financing - Restore Financing Product',_binary '','2026-05-09 01:41:02.559027','SYSTEM_SYNC'),(818,1,'FINANCING','APPLICATION_CREATE','FINANCING_APPLICATION_CREATE','Financing - Create Financing Application',_binary '','2026-05-09 01:41:02.576025','SYSTEM_SYNC'),(819,1,'FINANCING','APPLICATION_EDIT','FINANCING_APPLICATION_EDIT','Financing - Edit Financing Application',_binary '','2026-05-09 01:41:02.593027','SYSTEM_SYNC'),(820,1,'FINANCING','APPLICATION_SUBMIT','FINANCING_APPLICATION_SUBMIT','Financing - Submit Financing Application',_binary '','2026-05-09 01:41:02.652024','SYSTEM_SYNC'),(821,1,'FINANCING','APPLICATION_ARCHIVE','FINANCING_APPLICATION_ARCHIVE','Financing - Archive Financing Application',_binary '','2026-05-09 01:41:02.681025','SYSTEM_SYNC'),(822,1,'FINANCING','APPLICATION_RESTORE','FINANCING_APPLICATION_RESTORE','Financing - Restore Financing Application',_binary '','2026-05-09 01:41:02.699024','SYSTEM_SYNC'),(823,1,'CONTRACTS','TEMPLATE_CREATE','CONTRACT_TEMPLATE_CREATE','Contracts - Create Contract Template',_binary '','2026-05-09 01:41:02.715023','SYSTEM_SYNC'),(824,1,'CONTRACTS','TEMPLATE_EDIT','CONTRACT_TEMPLATE_EDIT','Contracts - Edit Contract Template',_binary '','2026-05-09 01:41:02.732025','SYSTEM_SYNC'),(825,1,'CONTRACTS','TEMPLATE_ARCHIVE','CONTRACT_TEMPLATE_ARCHIVE','Contracts - Archive Contract Template',_binary '','2026-05-09 01:41:02.747026','SYSTEM_SYNC'),(826,1,'CONTRACTS','TEMPLATE_RESTORE','CONTRACT_TEMPLATE_RESTORE','Contracts - Restore Contract Template',_binary '','2026-05-09 01:41:02.764025','SYSTEM_SYNC'),(827,1,'ZAKAT_CHARITY','PROFILE_CREATE','ZAKAT_PROFILE_CREATE','Zakat & Charity - Create Zakat Profile',_binary '','2026-05-09 01:41:02.781168','SYSTEM_SYNC'),(828,1,'ZAKAT_CHARITY','PROFILE_EDIT','ZAKAT_PROFILE_EDIT','Zakat & Charity - Edit Zakat Profile',_binary '','2026-05-09 01:41:02.797025','SYSTEM_SYNC'),(829,1,'ZAKAT_CHARITY','BENEFICIARY_CREATE','CHARITY_BENEFICIARY_CREATE','Zakat & Charity - Create Charity Beneficiary',_binary '','2026-05-09 01:41:02.814026','SYSTEM_SYNC'),(830,1,'ZAKAT_CHARITY','BENEFICIARY_EDIT','CHARITY_BENEFICIARY_EDIT','Zakat & Charity - Edit Charity Beneficiary',_binary '','2026-05-09 01:41:02.831164','SYSTEM_SYNC'),(831,1,'ZAKAT_CHARITY','BENEFICIARY_ARCHIVE','CHARITY_BENEFICIARY_ARCHIVE','Zakat & Charity - Archive Charity Beneficiary',_binary '','2026-05-09 01:41:02.850024','SYSTEM_SYNC'),(832,1,'ZAKAT_CHARITY','BENEFICIARY_RESTORE','CHARITY_BENEFICIARY_RESTORE','Zakat & Charity - Restore Charity Beneficiary',_binary '','2026-05-09 01:41:02.868026','SYSTEM_SYNC'),(833,1,'NOTIFICATION_ALERTS','TEMPLATE_CREATE','NOTIFICATION_TEMPLATE_CREATE','Notification & Alerts - Create Notification Template',_binary '','2026-05-09 01:41:02.892025','SYSTEM_SYNC'),(834,1,'NOTIFICATION_ALERTS','TEMPLATE_EDIT','NOTIFICATION_TEMPLATE_EDIT','Notification & Alerts - Edit Notification Template',_binary '','2026-05-09 01:41:02.908025','SYSTEM_SYNC'),(835,1,'NOTIFICATION_ALERTS','TEMPLATE_ARCHIVE','NOTIFICATION_TEMPLATE_ARCHIVE','Notification & Alerts - Archive Notification Template',_binary '','2026-05-09 01:41:02.925025','SYSTEM_SYNC'),(836,1,'NOTIFICATION_ALERTS','TEMPLATE_RESTORE','NOTIFICATION_TEMPLATE_RESTORE','Notification & Alerts - Restore Notification Template',_binary '','2026-05-09 01:41:02.941028','SYSTEM_SYNC'),(837,1,'NOTIFICATION_ALERTS','EVENT_RULE_CREATE','NOTIFICATION_EVENT_RULE_CREATE','Notification & Alerts - Create Notification Event Rule',_binary '','2026-05-09 01:41:02.957031','SYSTEM_SYNC'),(838,1,'NOTIFICATION_ALERTS','RETRY','NOTIFICATION_RETRY','Notification & Alerts - Retry Notification Delivery',_binary '','2026-05-09 01:41:02.975025','SYSTEM_SYNC'),(839,1,'INTEGRATION_MANAGEMENT','PROVIDER_CREATE','INTEGRATION_PROVIDER_CREATE','Integration Management - Create Integration Provider',_binary '','2026-05-09 01:41:02.998025','SYSTEM_SYNC'),(840,1,'INTEGRATION_MANAGEMENT','PROVIDER_EDIT','INTEGRATION_PROVIDER_EDIT','Integration Management - Edit Integration Provider',_binary '','2026-05-09 01:41:03.042024','SYSTEM_SYNC'),(841,1,'INTEGRATION_MANAGEMENT','PROVIDER_ARCHIVE','INTEGRATION_PROVIDER_ARCHIVE','Integration Management - Archive Integration Provider',_binary '','2026-05-09 01:41:03.059025','SYSTEM_SYNC'),(842,1,'INTEGRATION_MANAGEMENT','PROVIDER_RESTORE','INTEGRATION_PROVIDER_RESTORE','Integration Management - Restore Integration Provider',_binary '','2026-05-09 01:41:03.075025','SYSTEM_SYNC'),(843,1,'INTEGRATION_MANAGEMENT','PROVIDER_TEST','INTEGRATION_PROVIDER_TEST','Integration Management - Test Integration Provider',_binary '','2026-05-09 01:41:03.097024','SYSTEM_SYNC'),(844,1,'INTEGRATION_MANAGEMENT','LOG_RETRY','INTEGRATION_LOG_RETRY','Integration Management - Retry Integration Execution',_binary '','2026-05-09 01:41:03.114024','SYSTEM_SYNC'),(845,1,'SECURITY_AUDIT','INVESTIGATION_ASSIGN','SECURITY_INVESTIGATION_ASSIGN','Security / Audit - Assign Investigation Case',_binary '','2026-05-09 01:41:03.131164','SYSTEM_SYNC'),(846,1,'SECURITY_AUDIT','INVESTIGATION_CLOSE','SECURITY_INVESTIGATION_CLOSE','Security / Audit - Close Investigation Case',_binary '','2026-05-09 01:41:03.147026','SYSTEM_SYNC'),(847,1,'VERIFICATION','SEND_EMAIL_OTP','VERIFICATION_SEND_EMAIL_OTP','Verification - Send Email Verification OTP',_binary '','2026-05-09 01:41:03.164025','SYSTEM_SYNC'),(848,1,'VERIFICATION','SEND_MOBILE_OTP','VERIFICATION_SEND_MOBILE_OTP','Verification - Send Mobile Verification OTP',_binary '','2026-05-09 01:41:03.181025','SYSTEM_SYNC'),(849,1,'VERIFICATION','VERIFY_OTP','VERIFICATION_VERIFY_OTP','Verification - Verify OTP',_binary '','2026-05-09 01:41:03.199024','SYSTEM_SYNC'),(850,1,'VERIFICATION','RESEND_OTP','VERIFICATION_RESEND_OTP','Verification - Resend OTP',_binary '','2026-05-09 01:41:03.220026','SYSTEM_SYNC'),(851,1,'VERIFICATION','EXPIRE_OTP','VERIFICATION_EXPIRE_OTP','Verification - Expire OTP',_binary '','2026-05-09 01:41:03.242026','SYSTEM_SYNC'),(852,1,'VERIFICATION','MARK_FAILED','VERIFICATION_MARK_FAILED','Verification - Mark OTP Failed',_binary '','2026-05-09 01:41:03.258030','SYSTEM_SYNC'),(853,1,'VERIFICATION','PROVIDER_TEST','VERIFICATION_PROVIDER_TEST','Verification - Run Verification Provider Test',_binary '','2026-05-09 01:41:03.275027','SYSTEM_SYNC'),(854,1,'CALCULATION_ENGINE','SIMULATE','CALCULATION_SIMULATE','Calculation Engine - Run Calculation Simulation',_binary '','2026-05-09 01:41:03.300024','SYSTEM_SYNC'),(855,1,'REPORTING_REGULATORY','MONTHLY_CLOSING_CREATE','MONTHLY_CLOSING_CREATE','Reporting & Regulatory - Create Monthly Closing Run',_binary '','2026-05-15 13:17:14.607621','SYSTEM_SYNC'),(856,1,'REPORTING_REGULATORY','MONTHLY_CLOSING_SUBMIT','MONTHLY_CLOSING_SUBMIT','Reporting & Regulatory - Submit Monthly Closing Run',_binary '','2026-05-15 13:17:14.660626','SYSTEM_SYNC'),(857,1,'REPORTING_REGULATORY','MONTHLY_CLOSING_APPROVE','MONTHLY_CLOSING_APPROVE','Reporting & Regulatory - Approve Monthly Closing Run',_binary '','2026-05-15 13:17:14.695626','SYSTEM_SYNC'),(858,1,'REPORTING_REGULATORY','MONTHLY_CLOSING_REJECT','MONTHLY_CLOSING_REJECT','Reporting & Regulatory - Reject Monthly Closing Run',_binary '','2026-05-15 13:17:14.725628','SYSTEM_SYNC'),(859,1,'REPORTING_REGULATORY','MONTHLY_CLOSING_REOPEN','MONTHLY_CLOSING_REOPEN','Reporting & Regulatory - Reopen Monthly Closing Run',_binary '','2026-05-15 13:17:14.761622','SYSTEM_SYNC'),(860,5,'TRANSACTIONS','ACCESS','TRANSACTIONS_ACCESS','Transactions - Module Access',_binary '','2026-05-15 14:12:23.770063','SYSTEM_BOOTSTRAP'),(861,5,'CARD_MANAGEMENT','ACCESS','CARD_MANAGEMENT_ACCESS','Card Management - Module Access',_binary '','2026-05-15 14:12:23.789066','SYSTEM_BOOTSTRAP'),(862,25,'CUSTOMER_MANAGEMENT','ADDRESS_MANAGE','CUSTOMER_ADDRESS_MANAGE','Customer Management - Manage Customer Address',_binary '','2026-05-15 14:12:23.829075','SYSTEM_BOOTSTRAP'),(863,25,'ACCOUNT_MANAGEMENT','REQUEST_EDIT','ACCOUNT_REQUEST_EDIT','Account Management - Edit Account Opening Request',_binary '','2026-05-15 14:12:23.847063','SYSTEM_BOOTSTRAP'),(864,25,'KYC_MANAGEMENT','ACCESS','KYC_MANAGEMENT_ACCESS','KYC Management - Module Access',_binary '','2026-05-15 14:12:23.866065','SYSTEM_BOOTSTRAP'),(865,25,'KYC_MANAGEMENT','DOCUMENT_UPLOAD','KYC_DOCUMENT_UPLOAD','KYC Management - Upload KYC Document',_binary '','2026-05-15 14:12:23.883062','SYSTEM_BOOTSTRAP'),(866,25,'KYC_MANAGEMENT','EDIT','KYC_EDIT','KYC Management - Edit KYC Profile',_binary '','2026-05-15 14:12:23.900063','SYSTEM_BOOTSTRAP'),(867,25,'KYC_MANAGEMENT','CREATE','KYC_CREATE','KYC Management - Create KYC Profile',_binary '','2026-05-15 14:12:23.916063','SYSTEM_BOOTSTRAP'),(868,25,'ACCOUNT_MANAGEMENT','REQUEST_CREATE','ACCOUNT_REQUEST_CREATE','Account Management - Create Account Opening Request',_binary '','2026-05-15 14:12:23.968067','SYSTEM_BOOTSTRAP'),(869,25,'CUSTOMER_MANAGEMENT','EDIT','CUSTOMER_EDIT','Customer Management - Edit Customer',_binary '','2026-05-15 14:12:24.002063','SYSTEM_BOOTSTRAP'),(870,25,'ACCOUNT_MANAGEMENT','REQUEST_SUBMIT','ACCOUNT_REQUEST_SUBMIT','Account Management - Submit Account Opening Request',_binary '','2026-05-15 14:12:24.018064','SYSTEM_BOOTSTRAP'),(871,25,'CUSTOMER_MANAGEMENT','CREATE','CUSTOMER_CREATE','Customer Management - Create Customer',_binary '','2026-05-15 14:12:24.036069','SYSTEM_BOOTSTRAP'),(872,25,'ACCOUNT_MANAGEMENT','ACCESS','ACCOUNT_MANAGEMENT_ACCESS','Account Management - Module Access',_binary '','2026-05-15 14:12:24.053069','SYSTEM_BOOTSTRAP'),(873,25,'CUSTOMER_MANAGEMENT','ACCESS','CUSTOMER_MANAGEMENT_ACCESS','Customer Management - Module Access',_binary '','2026-05-15 14:12:24.069066','SYSTEM_BOOTSTRAP'),(874,25,'CUSTOMER_MANAGEMENT','IDENTITY_MANAGE','CUSTOMER_IDENTITY_MANAGE','Customer Management - Manage Customer Identity',_binary '','2026-05-15 14:12:24.087069','SYSTEM_BOOTSTRAP'),(875,3,'CARD_MANAGEMENT','ACCESS','CARD_MANAGEMENT_ACCESS','Card Management - Module Access',_binary '','2026-05-15 14:12:24.110067','SYSTEM_BOOTSTRAP'),(876,3,'CARD_MANAGEMENT','PIN_EVENT','CARD_PIN_EVENT','Card Management - Record Card PIN Event',_binary '','2026-05-15 14:12:24.131063','SYSTEM_BOOTSTRAP'),(877,4,'CONTRACTS','GENERATE','CONTRACT_GENERATE','Contracts - Generate Contract',_binary '','2026-05-15 14:12:24.159067','SYSTEM_BOOTSTRAP'),(878,4,'ACCOUNT_MANAGEMENT','REQUEST_REJECT','ACCOUNT_REQUEST_REJECT','Account Management - Reject Account Opening Request',_binary '','2026-05-15 14:12:24.177068','SYSTEM_BOOTSTRAP'),(879,4,'TRANSACTIONS','REVERSE','TRANSACTION_REVERSE','Transactions - Reverse Transaction',_binary '','2026-05-15 14:12:24.193064','SYSTEM_BOOTSTRAP'),(880,4,'ACCOUNT_MANAGEMENT','ACCOUNT_CLOSE','ACCOUNT_CLOSE','Account Management - Close Account',_binary '','2026-05-15 14:12:24.216064','SYSTEM_BOOTSTRAP'),(881,4,'ACCOUNT_MANAGEMENT','REQUEST_APPROVE','ACCOUNT_REQUEST_APPROVE','Account Management - Approve Account Opening Request',_binary '','2026-05-15 14:12:24.252067','SYSTEM_BOOTSTRAP'),(882,4,'ACCOUNT_MANAGEMENT','ACCOUNT_BLOCK','ACCOUNT_BLOCK','Account Management - Block Account',_binary '','2026-05-15 14:12:24.268065','SYSTEM_BOOTSTRAP'),(883,4,'ACCOUNT_MANAGEMENT','REQUEST_VERIFY','ACCOUNT_REQUEST_VERIFY','Account Management - Verify Account Opening Request',_binary '','2026-05-15 14:12:24.286067','SYSTEM_BOOTSTRAP'),(884,4,'ACCOUNT_MANAGEMENT','REQUEST_RETURN','ACCOUNT_REQUEST_RETURN','Account Management - Return Account Opening Request',_binary '','2026-05-15 14:12:24.308063','SYSTEM_BOOTSTRAP'),(885,4,'ACCOUNT_MANAGEMENT','ACCOUNT_ACTIVATE','ACCOUNT_ACTIVATE','Account Management - Activate Account',_binary '','2026-05-15 14:12:24.324062','SYSTEM_BOOTSTRAP'),(886,4,'CONTRACTS','ACCESS','CONTRACTS_ACCESS','Contracts - Module Access',_binary '','2026-05-15 14:12:24.339067','SYSTEM_BOOTSTRAP'),(887,4,'ACCOUNT_MANAGEMENT','ACCOUNT_FREEZE','ACCOUNT_FREEZE','Account Management - Freeze Account',_binary '','2026-05-15 14:12:24.355064','SYSTEM_BOOTSTRAP'),(888,4,'REPORTING_REGULATORY','ACCESS','REPORTING_REGULATORY_ACCESS','Reporting & Regulatory - Module Access',_binary '','2026-05-15 14:12:24.370066','SYSTEM_BOOTSTRAP'),(889,7,'CONTRACTS','ACCESS','CONTRACTS_ACCESS','Contracts - Module Access',_binary '','2026-05-15 14:12:24.406075','SYSTEM_BOOTSTRAP'),(890,2,'ACCOUNT_MANAGEMENT','REQUEST_REJECT','ACCOUNT_REQUEST_REJECT','Account Management - Reject Account Opening Request',_binary '','2026-05-15 14:12:24.441065','SYSTEM_BOOTSTRAP'),(891,2,'TRANSACTIONS','REVERSE','TRANSACTION_REVERSE','Transactions - Reverse Transaction',_binary '','2026-05-15 14:12:24.457071','SYSTEM_BOOTSTRAP'),(892,2,'REPORTING_REGULATORY','ACCESS','REPORTING_REGULATORY_ACCESS','Reporting & Regulatory - Module Access',_binary '','2026-05-15 14:12:24.476064','SYSTEM_BOOTSTRAP'),(893,2,'ACCOUNT_MANAGEMENT','REQUEST_RETURN','ACCOUNT_REQUEST_RETURN','Account Management - Return Account Opening Request',_binary '','2026-05-15 14:12:24.491064','SYSTEM_BOOTSTRAP'),(894,2,'ACCOUNT_MANAGEMENT','REQUEST_APPROVE','ACCOUNT_REQUEST_APPROVE','Account Management - Approve Account Opening Request',_binary '','2026-05-15 14:12:24.509065','SYSTEM_BOOTSTRAP'),(895,2,'REPORTING_REGULATORY','MONTHLY_CLOSING_CREATE','MONTHLY_CLOSING_CREATE','Reporting & Regulatory - Create Monthly Closing Run',_binary '','2026-05-15 14:12:24.525063','SYSTEM_BOOTSTRAP'),(896,2,'REPORTING_REGULATORY','MONTHLY_CLOSING_SUBMIT','MONTHLY_CLOSING_SUBMIT','Reporting & Regulatory - Submit Monthly Closing Run',_binary '','2026-05-15 14:12:24.540064','SYSTEM_BOOTSTRAP'),(897,26,'REPORTING_REGULATORY','ACCESS','REPORTING_REGULATORY_ACCESS','Reporting & Regulatory - Module Access',_binary '','2026-05-15 14:12:24.607064','SYSTEM_BOOTSTRAP'),(898,26,'STATEMENTS','ACCESS','STATEMENTS_ACCESS','Statements - Module Access',_binary '','2026-05-15 14:12:24.744063','SYSTEM_BOOTSTRAP'),(899,27,'REPORTING_REGULATORY','MONTHLY_CLOSING_APPROVE','MONTHLY_CLOSING_APPROVE','Reporting & Regulatory - Approve Monthly Closing Run',_binary '','2026-05-15 14:12:24.806065','SYSTEM_BOOTSTRAP'),(900,27,'SHARIAH_REVIEW','ACCESS','SHARIAH_REVIEW_ACCESS','Shariah Review - Module Access',_binary '','2026-05-15 14:12:24.893065','SYSTEM_BOOTSTRAP'),(901,27,'KYC_MANAGEMENT','ACCESS','KYC_MANAGEMENT_ACCESS','KYC Management - Module Access',_binary '','2026-05-15 14:12:24.919066','SYSTEM_BOOTSTRAP'),(902,27,'REPORTING_REGULATORY','ACCESS','REPORTING_REGULATORY_ACCESS','Reporting & Regulatory - Module Access',_binary '','2026-05-15 14:12:24.940064','SYSTEM_BOOTSTRAP'),(903,27,'REPORTING_REGULATORY','MONTHLY_CLOSING_REJECT','MONTHLY_CLOSING_REJECT','Reporting & Regulatory - Reject Monthly Closing Run',_binary '','2026-05-15 14:12:24.960061','SYSTEM_BOOTSTRAP'),(904,27,'SECURITY_AUDIT','ACCESS','SECURITY_AUDIT_ACCESS','Security / Audit - Module Access',_binary '','2026-05-15 14:12:24.977062','SYSTEM_BOOTSTRAP'),(905,27,'REPORTING_REGULATORY','MONTHLY_CLOSING_REOPEN','MONTHLY_CLOSING_REOPEN','Reporting & Regulatory - Reopen Monthly Closing Run',_binary '','2026-05-15 14:12:24.993066','SYSTEM_BOOTSTRAP'),(906,28,'STATEMENTS','ACCESS','STATEMENTS_ACCESS','Statements - Module Access',_binary '','2026-05-15 14:12:25.072064','SYSTEM_BOOTSTRAP'),(907,28,'SECURITY_AUDIT','ACCESS','SECURITY_AUDIT_ACCESS','Security / Audit - Module Access',_binary '','2026-05-15 14:12:25.093065','SYSTEM_BOOTSTRAP'),(908,28,'REPORTING_REGULATORY','ACCESS','REPORTING_REGULATORY_ACCESS','Reporting & Regulatory - Module Access',_binary '','2026-05-15 14:12:25.109064','SYSTEM_BOOTSTRAP'),(909,28,'VERIFICATION','ACCESS','VERIFICATION_ACCESS','Verification - Module Access',_binary '','2026-05-15 14:12:25.124063','SYSTEM_BOOTSTRAP'),(910,29,'FINANCING','ACCESS','FINANCING_ACCESS','Financing - Module Access',_binary '','2026-05-15 14:12:25.162063','SYSTEM_BOOTSTRAP'),(911,29,'FINANCING','COLLECT_PAYMENT','FINANCING_COLLECT_PAYMENT','Financing - Collect Financing Repayment',_binary '','2026-05-15 14:12:25.177064','SYSTEM_BOOTSTRAP'),(912,29,'REPORTING_REGULATORY','ACCESS','REPORTING_REGULATORY_ACCESS','Reporting & Regulatory - Module Access',_binary '','2026-05-15 14:12:25.192069','SYSTEM_BOOTSTRAP'),(913,30,'REPORTING_REGULATORY','MONTHLY_CLOSING_REOPEN','MONTHLY_CLOSING_REOPEN','Reporting & Regulatory - Reopen Monthly Closing Run',_binary '','2026-05-15 14:12:25.236065','SYSTEM_BOOTSTRAP'),(914,30,'PROFIT_MANAGEMENT','POSTING_RUN','PROFIT_POSTING_RUN','Profit Management - Run Profit Posting',_binary '','2026-05-15 14:12:25.256060','SYSTEM_BOOTSTRAP'),(915,30,'PROFIT_MANAGEMENT','ACCESS','PROFIT_MANAGEMENT_ACCESS','Profit Management - Module Access',_binary '','2026-05-15 14:12:25.270058','SYSTEM_BOOTSTRAP'),(916,30,'REPORTING_REGULATORY','MONTHLY_CLOSING_APPROVE','MONTHLY_CLOSING_APPROVE','Reporting & Regulatory - Approve Monthly Closing Run',_binary '','2026-05-15 14:12:25.285063','SYSTEM_BOOTSTRAP'),(917,30,'REPORTING_REGULATORY','ACCESS','REPORTING_REGULATORY_ACCESS','Reporting & Regulatory - Module Access',_binary '','2026-05-15 14:12:25.346070','SYSTEM_BOOTSTRAP'),(918,25,'STATEMENTS','CUSTOMER_REQUEST','STATEMENT_CUSTOMER_REQUEST','Statements - Request Customer Statement',_binary '','2026-05-15 14:29:31.585982','SYSTEM_BOOTSTRAP'),(919,25,'STATEMENTS','ACCESS','STATEMENTS_ACCESS','Statements - Module Access',_binary '','2026-05-15 14:29:31.620984','SYSTEM_BOOTSTRAP'),(920,26,'STATEMENTS','BRANCH_REQUEST','STATEMENT_BRANCH_REQUEST','Statements - Request Branch Statement',_binary '','2026-05-15 14:29:31.709980','SYSTEM_BOOTSTRAP'),(921,30,'REPORTING_REGULATORY','MONTHLY_CLOSING_REJECT','MONTHLY_CLOSING_REJECT','Reporting & Regulatory - Reject Monthly Closing Run',_binary '','2026-05-15 14:29:31.750982','SYSTEM_BOOTSTRAP'),(922,25,'KYC_MANAGEMENT','SUBMIT','KYC_SUBMIT','KYC Management - Submit KYC Profile',_binary '','2026-05-16 01:34:28.000000','SYSTEM_ADMIN');

--
-- Table structure for table `roles`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `status` enum('ACTIVE','INACTIVE') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `created_by` varchar(120) DEFAULT NULL,
  `updated_by` varchar(120) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKch1113horj4qr56f91omojv8` (`code`),
  UNIQUE KEY `uk_roles_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

INSERT INTO `roles` VALUES (1,'SYSTEM_ADMIN','2026-04-12 15:19:19.298624','Full administrative control over role, user, configuration and module access.','System Admin','ACTIVE','2026-04-12 15:19:19.298624','MODULE_1','MODULE_1'),(2,'BRANCH_MANAGER','2026-04-12 15:20:28.731438','Oversees branch operations, staff, vault and branch performance.','Branch Manager','ACTIVE','2026-05-02 00:16:34.265852','MODULE_1','MODULE_1'),(3,'TELLER','2026-04-12 15:20:42.619890','Handles teller operations, cash movement and customer-assisted transactions.','Teller','ACTIVE','2026-04-12 15:20:42.619890','MODULE_1','MODULE_1'),(4,'OPERATIONS_OFFICER','2026-04-12 15:20:54.409585','Manages customer, KYC, account and transaction operations.','Operations Officer','ACTIVE','2026-04-12 15:20:54.409585','MODULE_1','MODULE_1'),(5,'CUSTOMER','2026-04-12 15:21:07.589375','Customer-facing access to statements, schemes, requests and self-service journeys.','Customer','ACTIVE','2026-05-02 00:16:38.526982','MODULE_1','MODULE_1'),(6,'INVESTMENT_OFFICER','2026-04-12 15:24:28.811644','Manages investment, financing, profit and contract operations.','Investment Officer','ACTIVE','2026-04-12 15:24:28.811644','MODULE_1','MODULE_1'),(7,'SHARIAH_BOARD_MEMBER','2026-04-12 15:24:42.365210','Reviews shariah compliance, decisions and supporting reports.','Shariah Board Member','ACTIVE','2026-04-12 15:24:42.365210','MODULE_1','MODULE_1'),(23,'AOP_TEST_ROLE_20260509','2026-05-09 15:57:38.363200','Temporary role for AOP verification','AOP Test Role','INACTIVE','2026-05-09 16:00:45.967757','SYSTEM','SYSTEM'),(24,'AOP_TEST_ROLE_160023','2026-05-09 16:00:23.968618','Temporary role for AOP verification','AOP Test Role 160023','ACTIVE','2026-05-15 18:41:59.239426','SYSTEM','SYSTEM'),(25,'BRANCH_STAFF','2026-05-15 14:12:23.808063','Collect onboarding documents and assist branch-side operational intake','Branch Staff','ACTIVE','2026-05-15 14:12:23.808063','SYSTEM_BOOTSTRAP','SYSTEM_BOOTSTRAP'),(26,'MIS_OFFICER','2026-05-15 14:12:24.585069','Generate and monitor enterprise reports, exports and monthly summaries','MIS Officer','ACTIVE','2026-05-15 14:12:24.585069','SYSTEM_BOOTSTRAP','SYSTEM_BOOTSTRAP'),(27,'COMPLIANCE_OFFICER','2026-05-15 14:12:24.763065','Review compliance-sensitive KYC, shariah, reporting and security outcomes','Compliance Officer','ACTIVE','2026-05-15 14:12:24.763065','SYSTEM_BOOTSTRAP','SYSTEM_BOOTSTRAP'),(28,'INTERNAL_AUDITOR','2026-05-15 14:12:25.043065','Review exported reports, statements and audit evidence across modules','Internal Auditor','ACTIVE','2026-05-15 14:12:25.043065','SYSTEM_BOOTSTRAP','SYSTEM_BOOTSTRAP'),(29,'RECOVERY_OFFICER','2026-05-15 14:12:25.143061','Track delinquent financing accounts and collect recovery payments','Recovery Officer','ACTIVE','2026-05-15 14:12:25.143061','SYSTEM_BOOTSTRAP','SYSTEM_BOOTSTRAP'),(30,'TREASURY_FINANCE_OFFICER','2026-05-15 14:12:25.210062','Run profit, treasury-style oversight and month-end financial sign-off support','Treasury / Finance Officer','ACTIVE','2026-05-15 14:12:25.210062','SYSTEM_BOOTSTRAP','SYSTEM_BOOTSTRAP');

--
-- Table structure for table `security_event_log`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `security_event_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(500) DEFAULT NULL,
  `event_at` datetime(6) NOT NULL,
  `event_type` varchar(100) NOT NULL,
  `ip_address` varchar(80) DEFAULT NULL,
  `success` bit(1) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `username` varchar(100) DEFAULT NULL,
  `device_info` varchar(255) DEFAULT NULL,
  `event_code` varchar(50) NOT NULL,
  `event_name` varchar(160) NOT NULL,
  `event_time` datetime(6) NOT NULL,
  `reference_id` bigint DEFAULT NULL,
  `reference_module` varchar(80) DEFAULT NULL,
  `remarks` varchar(1000) DEFAULT NULL,
  `severity_level` enum('CRITICAL','HIGH','LOW','MEDIUM') NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `security_event_log`
--

INSERT INTO `security_event_log` VALUES (1,'Repeated password failure | SECURITY','2026-05-04 08:10:00.000000','FAILED_LOGIN','10.10.1.10',_binary '\0',1,'system','Chrome / Windows 11','FAILED_LOGIN','Failed Login Attempt','2026-05-04 08:10:00.000000',1001,'AUTH','Repeated password failure | SECURITY','HIGH','ACTIVE'),(2,'Five failed attempts from same IP | SECURITY','2026-05-04 08:14:00.000000','FAILED_LOGIN','10.10.1.11',_binary '\0',1,'system','Edge / Windows 11','FAILED_LOGIN','Failed Login Attempt','2026-05-04 08:14:00.000000',1002,'AUTH','Five failed attempts from same IP | SECURITY','CRITICAL','ACTIVE'),(3,'Account locked after security threshold | SECURITY','2026-05-04 08:15:00.000000','LOCKED_USER','10.10.1.11',_binary '\0',1,'system','Edge / Windows 11','LOCKED_USER','User Locked After Failed Login','2026-05-04 08:15:00.000000',1002,'AUTH','Account locked after security threshold | SECURITY','CRITICAL','ACTIVE'),(4,'High-value transfer pattern detected | SECURITY','2026-05-04 09:05:00.000000','SUSPICIOUS_TXN','10.20.5.14',_binary '\0',NULL,NULL,'API Gateway','SUSPICIOUS_TXN','Suspicious High Value Transfer','2026-05-04 09:05:00.000000',2001,'TRANSACTION','High-value transfer pattern detected | SECURITY','HIGH','ACTIVE'),(5,'Transaction matched AML watch rule | SECURITY','2026-05-04 09:10:00.000000','AML_FLAG','10.20.5.14',_binary '\0',NULL,NULL,'API Gateway','AML_FLAG','AML Pattern Match','2026-05-04 09:10:00.000000',2002,'TRANSACTION','Transaction matched AML watch rule | SECURITY','CRITICAL','ACTIVE'),(6,'Name matched sanction screening list | SECURITY','2026-05-04 09:20:00.000000','SANCTION_HIT','10.20.5.14',_binary '\0',NULL,NULL,'API Gateway','SANCTION_HIT','Sanction Screening Hit','2026-05-04 09:20:00.000000',3001,'CUSTOMER','Name matched sanction screening list | SECURITY','CRITICAL','ACTIVE'),(7,'Reversal spike observed on same teller | SECURITY','2026-05-04 09:45:00.000000','MULTIPLE_REVERSAL','10.20.6.15',_binary '\0',NULL,NULL,'Branch Switch','MULTIPLE_REVERSAL','Multiple Reversal Pattern','2026-05-04 09:45:00.000000',2003,'TRANSACTION','Reversal spike observed on same teller | SECURITY','HIGH','ACTIVE'),(8,'Administrative password reset triggered | SECURITY','2026-05-03 15:20:00.000000','PASSWORD_RESET','10.10.1.20',_binary '',1,'system','Firefox / Windows 11','PASSWORD_RESET','Admin Password Reset','2026-05-03 15:20:00.000000',4001,'USER','Administrative password reset triggered | SECURITY','MEDIUM','ACTIVE'),(9,'Sensitive permission map changed | SECURITY','2026-05-03 16:00:00.000000','PERMISSION_CHANGE','10.10.1.21',_binary '',1,'system','Chrome / Windows 11','PERMISSION_CHANGE','Role Permission Updated','2026-05-03 16:00:00.000000',5001,'ROLE','Sensitive permission map changed | SECURITY','MEDIUM','ACTIVE'),(10,'Login attempted from unusual device | SECURITY','2026-05-03 16:10:00.000000','DEVICE_MISMATCH','10.10.1.22',_binary '\0',1,'system','Safari / macOS','DEVICE_MISMATCH','Device Fingerprint Mismatch','2026-05-03 16:10:00.000000',1003,'AUTH','Login attempted from unusual device | SECURITY','HIGH','ACTIVE'),(11,'Suspicious repeated mobile login failure | SECURITY','2026-05-02 10:00:00.000000','FAILED_LOGIN','10.10.1.23',_binary '\0',1,'system','Chrome / Android','FAILED_LOGIN','Failed Login Attempt','2026-05-02 10:00:00.000000',1004,'AUTH','Suspicious repeated mobile login failure | SECURITY','HIGH','ACTIVE'),(12,'Rapid cash withdrawal sequence detected | SECURITY','2026-05-02 11:15:00.000000','SUSPICIOUS_TXN','10.20.7.16',_binary '\0',NULL,NULL,'ATM Switch','SUSPICIOUS_TXN','Unusual Cash Withdrawal Pattern','2026-05-02 11:15:00.000000',2004,'TRANSACTION','Rapid cash withdrawal sequence detected | SECURITY','HIGH','ACTIVE'),(13,'Layering pattern detected in txn set | SECURITY','2026-05-01 12:05:00.000000','AML_FLAG','10.20.7.16',_binary '\0',NULL,NULL,'ATM Switch','AML_FLAG','AML Scenario Triggered','2026-05-01 12:05:00.000000',2005,'TRANSACTION','Layering pattern detected in txn set | SECURITY','CRITICAL','ACTIVE'),(14,'Customer screening returned hit | SECURITY','2026-05-01 13:30:00.000000','SANCTION_HIT','10.10.1.24',_binary '\0',NULL,NULL,'Back Office','SANCTION_HIT','Sanction Screening Hit','2026-05-01 13:30:00.000000',3002,'CUSTOMER','Customer screening returned hit | SECURITY','CRITICAL','ACTIVE'),(15,'Concurrent session detected | SECURITY','2026-05-01 14:00:00.000000','SESSION_ALERT','10.10.1.25',_binary '\0',1,'system','Chrome / Windows 11','SESSION_ALERT','Concurrent Session Alert','2026-05-01 14:00:00.000000',1005,'AUTH','Concurrent session detected | SECURITY','MEDIUM','ACTIVE');

--
-- Table structure for table `shariah_checklist_item`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shariah_checklist_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `item_code` varchar(40) NOT NULL,
  `item_name` varchar(160) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shariah_checklist_item_code` (`item_code`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shariah_checklist_item`
--

INSERT INTO `shariah_checklist_item` VALUES (1,'2026-04-01 09:00:00.000000','Review whether the business purpose aligns with approved Shariah banking objectives.','SCI-00001','Purpose Alignment Review','ACTIVE','2026-04-01 09:00:00.000000'),(2,'2026-04-01 09:05:00.000000','Ensure the financed or linked asset is halal, lawful and supported by source evidence.','SCI-00002','Asset Halal Validation','ACTIVE','2026-04-01 09:05:00.000000'),(3,'2026-04-01 09:10:00.000000','Check whether the contract clauses follow approved Islamic structure and wording.','SCI-00003','Contract Clause Compliance','ACTIVE','2026-04-01 09:10:00.000000'),(4,'2026-04-01 09:15:00.000000','Review markup, rental, sharing or profit rule against approved product guidance.','SCI-00004','Profit Method Review','ACTIVE','2026-04-01 09:15:00.000000'),(5,'2026-04-01 09:20:00.000000','Confirm document completeness before final board decision is recorded.','SCI-00005','Documentation Completeness','ACTIVE','2026-04-01 09:20:00.000000'),(6,'2026-04-01 09:25:00.000000','Check collateral or supporting security for prohibited elements or wording.','SCI-00006','Collateral Compliance','ACTIVE','2026-04-01 09:25:00.000000'),(7,'2026-04-01 09:30:00.000000','Verify charity late fee wording and disclosure are correctly applied.','SCI-00007','Charity Penalty Disclosure','ACTIVE','2026-04-01 09:30:00.000000'),(8,'2026-04-01 09:35:00.000000','Confirm customer declaration and disclosures are visible and traceable.','SCI-00008','Customer Disclosure Review','ACTIVE','2026-04-01 09:35:00.000000'),(9,'2026-04-01 09:40:00.000000','Check ownership and possession evidence where required before financing execution.','SCI-00009','Ownership Evidence Check','ACTIVE','2026-04-01 09:40:00.000000'),(10,'2026-04-01 09:45:00.000000','Validate supplier or vendor document support for linked purchase transaction.','SCI-00010','Supplier Validation','ACTIVE','2026-04-01 09:45:00.000000'),(11,'2026-04-01 09:50:00.000000','Review risk mitigation controls and approval note before case closure.','SCI-00011','Risk Mitigation Review','ACTIVE','2026-04-01 09:50:00.000000'),(12,'2026-04-01 09:55:00.000000','Ensure takaful or protection note is compliant where the product requires it.','SCI-00012','Takaful Compliance Note','ACTIVE','2026-04-01 09:55:00.000000'),(13,'2026-04-01 10:00:00.000000','Check early settlement wording and rebate note are fair and compliant.','SCI-00013','Early Settlement Clause','ACTIVE','2026-04-01 10:00:00.000000'),(14,'2026-04-01 10:05:00.000000','Verify late payment treatment routes charity amount outside bank income.','SCI-00014','Late Fee Charity Routing','ACTIVE','2026-04-01 10:05:00.000000'),(15,'2026-04-01 10:10:00.000000','Ensure final board note and certificate language are ready for archive and report use.','SCI-00015','Board Note Completion','ACTIVE','2026-04-01 10:10:00.000000');

--
-- Table structure for table `shariah_review_case`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shariah_review_case` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `case_no` varchar(40) NOT NULL,
  `case_status` enum('APPROVED','PENDING_REVIEW','REJECTED','RETURNED') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `reference_id` bigint NOT NULL,
  `reference_module` varchar(80) NOT NULL,
  `remarks` varchar(1000) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `submitted_at` datetime(6) NOT NULL,
  `submitted_by` varchar(160) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shariah_review_case_no` (`case_no`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shariah_review_case`
--

INSERT INTO `shariah_review_case` VALUES (1,'SHR-00001','PENDING_REVIEW','2026-04-02 09:00:00.000000',1,'FINANCING','Murabaha transport case submitted and waiting for full board checklist completion.','ACTIVE','2026-04-02 09:00:00.000000','Review Officer 01','2026-04-02 09:00:00.000000'),(2,'SHR-00002','APPROVED','2026-04-02 09:15:00.000000',2,'FINANCING','Board approved after confirming vehicle asset, pricing and ownership evidence.','ACTIVE','2026-04-02 09:15:00.000000','Review Officer 02','2026-04-03 11:00:00.000000'),(3,'SHR-00003','REJECTED','2026-04-02 09:30:00.000000',3,'FINANCING','Rejected because asset quotation source and supporting document trail were inconsistent.','ACTIVE','2026-04-02 09:30:00.000000','Review Officer 03','2026-04-03 11:20:00.000000'),(4,'SHR-00004','RETURNED','2026-04-02 09:45:00.000000',4,'FINANCING','Returned for correction to add customer disclosure and revised charity clause wording.','ACTIVE','2026-04-02 09:45:00.000000','Review Officer 04','2026-04-03 11:40:00.000000'),(5,'SHR-00005','APPROVED','2026-04-02 10:00:00.000000',5,'CONTRACT','Contract wording cleared and certificate can be generated for archive.','ACTIVE','2026-04-02 10:00:00.000000','Review Officer 05','2026-04-03 12:00:00.000000'),(6,'SHR-00006','PENDING_REVIEW','2026-04-02 10:15:00.000000',6,'CONTRACT','Checklist updated and board note still pending before decision.','ACTIVE','2026-04-02 10:15:00.000000','Review Officer 06','2026-04-03 12:15:00.000000'),(7,'SHR-00007','APPROVED','2026-04-02 10:30:00.000000',7,'FINANCING','Approved after collateral and supplier validation completed.','ACTIVE','2026-04-02 10:30:00.000000','Review Officer 07','2026-04-03 12:30:00.000000'),(8,'SHR-00008','RETURNED','2026-04-02 10:45:00.000000',8,'CONTRACT','Returned for correction because the template omitted updated board note language.','ACTIVE','2026-04-02 10:45:00.000000','Review Officer 08','2026-04-03 12:45:00.000000'),(9,'SHR-00009','APPROVED','2026-04-02 11:00:00.000000',1,'DEPOSIT_SCHEME','Savings scheme case approved after disclosure and charity clause check.','ACTIVE','2026-04-02 11:00:00.000000','Review Officer 09','2026-04-03 13:00:00.000000'),(10,'SHR-00010','REJECTED','2026-04-02 11:15:00.000000',10,'FINANCING','Rejected due to unsupported supplier invoice and incomplete ownership note.','ACTIVE','2026-04-02 11:15:00.000000','Review Officer 10','2026-04-03 13:15:00.000000'),(11,'SHR-00011','PENDING_REVIEW','2026-04-02 11:30:00.000000',11,'CONTRACT','Customer and contract documents loaded, waiting for board review slot.','ACTIVE','2026-04-02 11:30:00.000000','Review Officer 11','2026-04-02 11:30:00.000000'),(12,'SHR-00012','APPROVED','2026-04-02 11:45:00.000000',1,'CARD_ISSUE','Card issue undertaking approved with updated disclosure wording.','ACTIVE','2026-04-02 11:45:00.000000','Review Officer 12','2026-04-03 13:45:00.000000'),(13,'SHR-00013','RETURNED','2026-04-02 12:00:00.000000',13,'ACCOUNT_OPENING','Returned to collect revised early settlement clause note and customer declaration.','ACTIVE','2026-04-02 12:00:00.000000','Review Officer 13','2026-04-03 14:00:00.000000'),(14,'SHR-00014','REJECTED','2026-04-02 12:15:00.000000',14,'FINANCING','Rejected because financing purpose narrative was not aligned with approved structure.','ACTIVE','2026-04-02 12:15:00.000000','Review Officer 14','2026-04-03 14:15:00.000000'),(15,'SHR-00015','PENDING_REVIEW','2026-04-02 12:30:00.000000',15,'GENERAL','General advisory case submitted for board comment and annual report inclusion.','ACTIVE','2026-04-02 12:30:00.000000','Review Officer 15','2026-04-02 12:30:00.000000');

--
-- Table structure for table `shariah_review_checklist`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shariah_review_checklist` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `note` varchar(1000) DEFAULT NULL,
  `selected_flag` bit(1) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `item_id` bigint NOT NULL,
  `case_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7dp9n0cy4xfgekcpl24vniedd` (`item_id`),
  KEY `FK3xc5s31u7mh0s4ka1ud2y8avf` (`case_id`),
  CONSTRAINT `FK3xc5s31u7mh0s4ka1ud2y8avf` FOREIGN KEY (`case_id`) REFERENCES `shariah_review_case` (`id`),
  CONSTRAINT `FK7dp9n0cy4xfgekcpl24vniedd` FOREIGN KEY (`item_id`) REFERENCES `shariah_checklist_item` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shariah_review_checklist`
--

INSERT INTO `shariah_review_checklist` VALUES (1,'2026-04-02 09:05:00.000000','Purpose note aligned with approved Murabaha transport activity.',_binary '','ACTIVE','2026-04-02 09:05:00.000000',1,1),(2,'2026-04-02 09:06:00.000000','Asset description verified against transport quotation.',_binary '','ACTIVE','2026-04-02 09:06:00.000000',2,1),(3,'2026-04-02 09:07:00.000000','Document set complete before final board sitting.',_binary '','ACTIVE','2026-04-02 09:07:00.000000',5,1),(4,'2026-04-02 09:20:00.000000','Purpose and transport use case align with policy.',_binary '','ACTIVE','2026-04-02 09:20:00.000000',1,2),(5,'2026-04-02 09:21:00.000000','Ownership evidence and chassis quotation matched.',_binary '','ACTIVE','2026-04-02 09:21:00.000000',9,2),(6,'2026-04-02 09:22:00.000000','Supplier records validated before approval.',_binary '','ACTIVE','2026-04-02 09:22:00.000000',10,2),(7,'2026-04-02 09:35:00.000000','Purpose acceptable but quotation trail was weak.',_binary '','ACTIVE','2026-04-02 09:35:00.000000',1,3),(8,'2026-04-02 09:36:00.000000','Asset note captured but invoice evidence incomplete.',_binary '','ACTIVE','2026-04-02 09:36:00.000000',2,3),(9,'2026-04-02 09:37:00.000000','Supplier validation failed during review.',_binary '','ACTIVE','2026-04-02 09:37:00.000000',10,3),(10,'2026-04-02 09:50:00.000000','Purpose and customer note aligned but disclosure revision needed.',_binary '','ACTIVE','2026-04-02 09:50:00.000000',1,4),(11,'2026-04-02 09:51:00.000000','Charity fee wording required updated customer-facing language.',_binary '','ACTIVE','2026-04-02 09:51:00.000000',7,4),(12,'2026-04-02 09:52:00.000000','Customer declaration needed correction and resubmission.',_binary '','ACTIVE','2026-04-02 09:52:00.000000',8,4),(13,'2026-04-02 10:05:00.000000','Contract clause wording aligned with approved board template.',_binary '','ACTIVE','2026-04-02 10:05:00.000000',3,5),(14,'2026-04-02 10:06:00.000000','Profit or fee treatment reviewed and acceptable.',_binary '','ACTIVE','2026-04-02 10:06:00.000000',4,5),(15,'2026-04-02 10:07:00.000000','Board note completion confirmed for certificate issue.',_binary '','ACTIVE','2026-04-02 10:07:00.000000',15,5),(16,'2026-04-02 10:20:00.000000','Contract text prepared for board review.',_binary '','ACTIVE','2026-04-02 10:20:00.000000',3,6),(17,'2026-04-02 10:21:00.000000','Risk mitigation note captured.',_binary '','ACTIVE','2026-04-02 10:21:00.000000',11,6),(18,'2026-04-02 10:22:00.000000','Board note completion pending final meeting.',_binary '','ACTIVE','2026-04-02 10:22:00.000000',15,6),(19,'2026-04-02 10:35:00.000000','Collateral wording accepted by board.',_binary '','ACTIVE','2026-04-02 10:35:00.000000',6,7),(20,'2026-04-02 10:36:00.000000','Supplier and vendor records reviewed.',_binary '','ACTIVE','2026-04-02 10:36:00.000000',10,7),(21,'2026-04-02 10:37:00.000000','Risk note aligned with approval condition.',_binary '','ACTIVE','2026-04-02 10:37:00.000000',11,7),(22,'2026-04-02 10:50:00.000000','Contract clause review found outdated certificate wording.',_binary '','ACTIVE','2026-04-02 10:50:00.000000',3,8),(23,'2026-04-02 10:51:00.000000','Board note required updated final archive language.',_binary '','ACTIVE','2026-04-02 10:51:00.000000',15,8),(24,'2026-04-02 10:52:00.000000','Customer disclosure needs to reflect latest template revision.',_binary '','ACTIVE','2026-04-02 10:52:00.000000',8,8),(25,'2026-04-02 11:05:00.000000','Deposit scheme purpose and disclosure are compliant.',_binary '','ACTIVE','2026-04-02 11:05:00.000000',1,9),(26,'2026-04-02 11:06:00.000000','Charity clause routed correctly outside bank income.',_binary '','ACTIVE','2026-04-02 11:06:00.000000',14,9),(27,'2026-04-02 11:07:00.000000','Board note captured for annual report use.',_binary '','ACTIVE','2026-04-02 11:07:00.000000',15,9),(28,'2026-04-02 11:20:00.000000','Supplier invoice review exposed inconsistency.',_binary '','ACTIVE','2026-04-02 11:20:00.000000',10,10),(29,'2026-04-02 11:21:00.000000','Ownership evidence was incomplete at rejection time.',_binary '','ACTIVE','2026-04-02 11:21:00.000000',9,10),(30,'2026-04-02 11:22:00.000000','Document completeness remained below threshold.',_binary '','ACTIVE','2026-04-02 11:22:00.000000',5,10),(31,'2026-04-02 11:35:00.000000','Contract checklist prepared and queued for board.',_binary '','ACTIVE','2026-04-02 11:35:00.000000',3,11),(32,'2026-04-02 11:36:00.000000','Documentation captured and waiting decision.',_binary '','ACTIVE','2026-04-02 11:36:00.000000',5,11),(33,'2026-04-02 11:37:00.000000','Board note section left open for final meeting.',_binary '','ACTIVE','2026-04-02 11:37:00.000000',15,11),(34,'2026-04-02 11:50:00.000000','Card contract clause and disclosure comply with board guidance.',_binary '','ACTIVE','2026-04-02 11:50:00.000000',3,12),(35,'2026-04-02 11:51:00.000000','Customer declaration visible and compliant.',_binary '','ACTIVE','2026-04-02 11:51:00.000000',8,12),(36,'2026-04-02 11:52:00.000000','Board note completed for certificate archive.',_binary '','ACTIVE','2026-04-02 11:52:00.000000',15,12),(37,'2026-04-02 12:05:00.000000','Account opening declaration required correction.',_binary '','ACTIVE','2026-04-02 12:05:00.000000',8,13),(38,'2026-04-02 12:06:00.000000','Early settlement wording needed updated note.',_binary '','ACTIVE','2026-04-02 12:06:00.000000',13,13),(39,'2026-04-02 12:07:00.000000','Board returned case for corrected customer wording.',_binary '','ACTIVE','2026-04-02 12:07:00.000000',15,13),(40,'2026-04-02 12:20:00.000000','Purpose wording did not align with approved structure.',_binary '','ACTIVE','2026-04-02 12:20:00.000000',1,14),(41,'2026-04-02 12:21:00.000000','Profit method note conflicted with approved rule.',_binary '','ACTIVE','2026-04-02 12:21:00.000000',4,14),(42,'2026-04-02 12:22:00.000000','Risk mitigation note was insufficient for approval.',_binary '','ACTIVE','2026-04-02 12:22:00.000000',11,14),(43,'2026-04-02 12:35:00.000000','General advisory purpose logged for board comment.',_binary '','ACTIVE','2026-04-02 12:35:00.000000',1,15),(44,'2026-04-02 12:36:00.000000','Document completeness acceptable for initial queue.',_binary '','ACTIVE','2026-04-02 12:36:00.000000',5,15),(45,'2026-04-02 12:37:00.000000','Board note to be finalized after review meeting.',_binary '','ACTIVE','2026-04-02 12:37:00.000000',15,15);

--
-- Table structure for table `shariah_review_decision`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shariah_review_decision` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `decision` enum('APPROVED','CHECKLIST_UPDATED','REJECTED','RETURNED','SUBMITTED') NOT NULL,
  `decision_at` datetime(6) NOT NULL,
  `decision_by` varchar(160) NOT NULL,
  `remarks` varchar(1000) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `case_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK47x9grvfjjvrdywojnncrj56o` (`case_id`),
  CONSTRAINT `FK47x9grvfjjvrdywojnncrj56o` FOREIGN KEY (`case_id`) REFERENCES `shariah_review_case` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shariah_review_decision`
--

INSERT INTO `shariah_review_decision` VALUES (1,'2026-04-02 09:00:00.000000','SUBMITTED','2026-04-02 09:00:00.000000','Review Officer 01','Case submitted to Shariah board queue.','ACTIVE',1),(2,'2026-04-03 11:00:00.000000','APPROVED','2026-04-03 11:00:00.000000','Board Member A','Approved after full transport asset and purpose validation.','ACTIVE',2),(3,'2026-04-03 11:20:00.000000','REJECTED','2026-04-03 11:20:00.000000','Board Member B','Rejected due to inconsistent quotation trail.','ACTIVE',3),(4,'2026-04-03 11:40:00.000000','RETURNED','2026-04-03 11:40:00.000000','Board Member C','Returned for revised disclosure wording and correction note.','ACTIVE',4),(5,'2026-04-03 12:00:00.000000','APPROVED','2026-04-03 12:00:00.000000','Board Member D','Approved and certificate language is ready.','ACTIVE',5),(6,'2026-04-03 12:15:00.000000','CHECKLIST_UPDATED','2026-04-03 12:15:00.000000','Board Member E','Checklist reviewed and waiting final meeting note.','ACTIVE',6),(7,'2026-04-03 12:30:00.000000','APPROVED','2026-04-03 12:30:00.000000','Board Member F','Approved after collateral and supplier validation.','ACTIVE',7),(8,'2026-04-03 12:45:00.000000','RETURNED','2026-04-03 12:45:00.000000','Board Member G','Returned to update contract template note.','ACTIVE',8),(9,'2026-04-03 13:00:00.000000','APPROVED','2026-04-03 13:00:00.000000','Board Member H','Approved for savings scheme execution.','ACTIVE',9),(10,'2026-04-03 13:15:00.000000','REJECTED','2026-04-03 13:15:00.000000','Board Member I','Rejected because supplier evidence remained incomplete.','ACTIVE',10),(11,'2026-04-02 11:30:00.000000','SUBMITTED','2026-04-02 11:30:00.000000','Review Officer 11','Case submitted and waiting board agenda.','ACTIVE',11),(12,'2026-04-03 13:45:00.000000','APPROVED','2026-04-03 13:45:00.000000','Board Member J','Approved with compliant disclosure and certificate note.','ACTIVE',12),(13,'2026-04-03 14:00:00.000000','RETURNED','2026-04-03 14:00:00.000000','Board Member K','Returned to collect revised customer declaration.','ACTIVE',13),(14,'2026-04-03 14:15:00.000000','REJECTED','2026-04-03 14:15:00.000000','Board Member L','Rejected because purpose wording did not align with approved structure.','ACTIVE',14),(15,'2026-04-02 12:30:00.000000','SUBMITTED','2026-04-02 12:30:00.000000','Review Officer 15','General case submitted for board comment.','ACTIVE',15);

--
-- Table structure for table `standing_instruction`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `standing_instruction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(18,2) NOT NULL,
  `branch_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `frequency` varchar(30) NOT NULL,
  `from_account_id` bigint NOT NULL,
  `instruction_code` varchar(40) NOT NULL,
  `instruction_status` enum('ACTIVE','CANCELLED','EXECUTED','PAUSED') NOT NULL,
  `next_execution_date` date DEFAULT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `schedule_date` date NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `to_account_id` bigint NOT NULL,
  `transfer_mode` enum('BEFTN','INTERNAL','RTGS') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_standing_instruction_code` (`instruction_code`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `standing_instruction`
--

INSERT INTO `standing_instruction` VALUES (1,1000.00,1,'2026-05-01 13:00:00.000000','MONTHLY',1,'SI-0001','ACTIVE','2026-05-02','Monthly family transfer','2026-05-02','ACTIVE',2,'INTERNAL'),(2,1500.00,2,'2026-05-01 13:01:00.000000','WEEKLY',2,'SI-0002','ACTIVE','2026-05-03','Weekly repayment transfer','2026-05-03','ACTIVE',3,'INTERNAL'),(3,800.00,1,'2026-05-01 13:02:00.000000','MONTHLY',3,'SI-0003','PAUSED','2026-05-04','Paused corporate sweep','2026-05-04','ACTIVE',4,'RTGS'),(4,2500.00,2,'2026-05-01 13:03:00.000000','MONTHLY',4,'SI-0004','ACTIVE','2026-05-05','SME supplier settlement','2026-05-05','ACTIVE',5,'BEFTN'),(5,3000.00,2,'2026-05-01 13:04:00.000000','DAILY',5,'SI-0005','ACTIVE','2026-05-06','Corporate to savings provisioning','2026-05-06','ACTIVE',6,'INTERNAL'),(6,700.00,1,'2026-05-01 13:05:00.000000','MONTHLY',6,'SI-0006','EXECUTED','2026-05-07','Executed hajj contribution setup','2026-05-07','ACTIVE',7,'INTERNAL'),(7,650.00,2,'2026-05-01 13:06:00.000000','MONTHLY',7,'SI-0007','ACTIVE','2026-05-08','Umrah to student helper transfer','2026-05-08','ACTIVE',8,'INTERNAL'),(8,400.00,1,'2026-05-01 13:07:00.000000','WEEKLY',8,'SI-0008','ACTIVE','2026-05-09','Weekly payroll savings top-up','2026-05-09','ACTIVE',9,'INTERNAL'),(9,1200.00,3,'2026-05-01 13:08:00.000000','MONTHLY',9,'SI-0009','PAUSED','2026-05-10','Paused payroll settlement','2026-05-10','ACTIVE',10,'BEFTN'),(10,5000.00,2,'2026-05-01 13:09:00.000000','MONTHLY',10,'SI-0010','ACTIVE','2026-05-11','Business current monthly support','2026-05-11','ACTIVE',11,'RTGS'),(11,900.00,1,'2026-05-01 13:10:00.000000','MONTHLY',11,'SI-0011','ACTIVE','2026-05-12','Women to senior care transfer','2026-05-12','ACTIVE',12,'INTERNAL'),(12,1100.00,3,'2026-05-01 13:11:00.000000','MONTHLY',12,'SI-0012','CANCELLED','2026-05-13','Cancelled cross-currency support','2026-05-13','ACTIVE',13,'BEFTN'),(13,100.00,2,'2026-05-01 13:12:00.000000','MONTHLY',13,'SI-0013','ACTIVE','2026-05-14','NRB FC to deposit lite setup','2026-05-14','ACTIVE',14,'INTERNAL'),(14,450.00,1,'2026-05-01 13:13:00.000000','MONTHLY',14,'SI-0014','ACTIVE','2026-05-15','Deposit lite to digital savings','2026-05-15','ACTIVE',15,'INTERNAL'),(15,1300.00,3,'2026-05-01 13:14:00.000000','MONTHLY',15,'SI-0015','ACTIVE','2026-05-16','Digital savings high value standing setup','2026-05-16','ACTIVE',1,'RTGS');

--
-- Table structure for table `step_up_verification_challenge`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `step_up_verification_challenge` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action_code` varchar(120) NOT NULL,
  `consumed_at` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `target_id` bigint DEFAULT NULL,
  `target_module` varchar(120) NOT NULL,
  `token_expires_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `verification_token` varchar(120) DEFAULT NULL,
  `verified_at` datetime(6) DEFAULT NULL,
  `request_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8ij027onr29xl1t7pn4ojmq7m` (`request_id`),
  KEY `FKfoob0edu17yqahx9g4hiracu8` (`user_id`),
  CONSTRAINT `FK8ij027onr29xl1t7pn4ojmq7m` FOREIGN KEY (`request_id`) REFERENCES `otp_verification_request` (`id`),
  CONSTRAINT `FKfoob0edu17yqahx9g4hiracu8` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `step_up_verification_challenge`
--


--
-- Table structure for table `teller_limit`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teller_limit` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `approved_at` datetime(6) DEFAULT NULL,
  `approved_by` bigint DEFAULT NULL,
  `branch_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `daily_deposit_limit` decimal(18,2) NOT NULL,
  `daily_withdraw_limit` decimal(18,2) NOT NULL,
  `limit_date` date NOT NULL,
  `single_txn_limit` decimal(18,2) NOT NULL,
  `status` varchar(30) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teller_limit`
--

INSERT INTO `teller_limit` VALUES (1,'2026-04-25 07:57:27.699867',101,1,'2026-04-25 07:57:27.704865',500000.00,300000.00,'2026-04-25',50000.00,'ACTIVE',201),(2,'2026-04-26 03:30:14.029789',1,1,'2026-04-26 03:30:14.035789',100000.00,80000.00,'2026-04-26',20000.00,'ACTIVE',201),(3,'2026-04-26 03:30:47.633932',1,2,'2026-04-26 03:30:47.634934',200000.00,150000.00,'2026-04-27',40000.00,'ACTIVE',203),(4,'2026-04-26 03:32:52.279009',1,1,'2026-04-26 03:32:52.279009',120000.00,90000.00,'2026-04-27',25000.00,'ACTIVE',201),(5,'2026-04-26 03:33:01.463600',1,1,'2026-04-26 03:33:01.464599',130000.00,100000.00,'2026-04-27',30000.00,'ACTIVE',202),(6,'2026-04-26 03:33:14.040014',1,2,'2026-04-26 03:33:14.041015',220000.00,170000.00,'2026-04-28',45000.00,'ACTIVE',203),(7,'2026-04-26 03:33:22.227384',1,3,'2026-04-26 03:33:22.227384',280000.00,230000.00,'2026-04-28',55000.00,'ACTIVE',204),(8,'2026-04-26 03:33:32.096992',1,4,'2026-04-26 03:33:32.097994',260000.00,210000.00,'2026-04-29',50000.00,'ACTIVE',205),(9,'2026-04-26 03:33:43.812287',1,2,'2026-04-26 03:33:43.813289',210000.00,160000.00,'2026-04-29',40000.00,'ACTIVE',203),(10,'2026-04-26 03:33:51.793012',1,3,'2026-04-26 03:33:51.793012',300000.00,250000.00,'2026-04-30',60000.00,'ACTIVE',204),(11,'2026-04-26 03:34:03.916028',1,4,'2026-04-26 03:34:03.916028',270000.00,220000.00,'2026-04-30',55000.00,'ACTIVE',205),(12,'2026-05-08 15:04:47.000000',3,12,'2026-05-08 15:04:47.000000',600000.00,450000.00,'2026-05-08',150000.00,'ACTIVE',11),(13,'2026-05-08 15:04:47.000000',3,13,'2026-05-08 15:04:47.000000',550000.00,400000.00,'2026-05-08',140000.00,'ACTIVE',12),(14,'2026-05-08 15:04:47.000000',3,14,'2026-05-08 15:04:47.000000',720000.00,500000.00,'2026-05-08',175000.00,'ACTIVE',13),(15,'2026-05-08 15:04:47.000000',3,15,'2026-05-08 15:04:47.000000',800000.00,650000.00,'2026-05-08',200000.00,'ACTIVE',16);

--
-- Table structure for table `terminal`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `terminal` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `branch_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `install_date` date DEFAULT NULL,
  `ip_address` varchar(50) DEFAULT NULL,
  `location_note` varchar(255) DEFAULT NULL,
  `serial_no` varchar(100) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','INACTIVE','MAINTENANCE','OUT_OF_SERVICE') NOT NULL,
  `terminal_code` varchar(50) NOT NULL,
  `terminal_name` varchar(150) NOT NULL,
  `terminal_type` enum('ATM','ATM_CDM','CDM') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `vendor_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK8vymb75c1oc8h1g31de1hn4ei` (`terminal_code`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `terminal`
--

INSERT INTO `terminal` VALUES (1,1,'2026-04-27 06:55:35.863297','2024-01-10','192.168.1.10','Main entrance','SN-ATM-001','ACTIVE','ATM001','Dhaka Main ATM','ATM',NULL,'NCR'),(2,1,'2026-04-27 06:55:50.957042','2024-02-15','192.168.1.11','Inside branch','SN-CDM-001','ACTIVE','CDM001','Dhaka CDM Booth','CDM',NULL,'Diebold'),(3,2,'2026-04-27 06:56:01.954876','2024-03-01','192.168.2.10','Roadside booth','SN-ATM-002','ACTIVE','ATM002','Gulshan ATM','ATM',NULL,'NCR'),(4,2,'2026-04-27 06:56:12.003164','2024-04-05','192.168.2.20','Shopping mall','SN-HYBRID-001','ACTIVE','ATMCDM001','Banani Smart Machine','ATM_CDM',NULL,'GRG'),(5,3,'2026-04-27 06:56:21.884145','2024-05-10','192.168.3.10','Bus stand','SN-ATM-003','MAINTENANCE','ATM003','Uttara ATM','ATM',NULL,'NCR'),(6,3,'2026-04-27 06:56:31.634070','2024-06-15','192.168.3.11','Inside branch','SN-CDM-002','ACTIVE','CDM002','Uttara CDM','CDM',NULL,'Diebold'),(7,4,'2026-04-27 06:56:40.458781','2024-07-01','192.168.4.10','University gate','SN-ATM-004','OUT_OF_SERVICE','ATM004','Dhanmondi ATM','ATM',NULL,'NCR'),(8,5,'2026-04-27 06:56:51.329298','2024-08-20','192.168.5.10','Market area','SN-ATM-005','ACTIVE','ATM005','Mirpur ATM','ATM',NULL,'GRG'),(9,5,'2026-04-27 06:57:28.044087','2024-09-10','192.168.5.11','Branch lobby','SN-CDM-003','ACTIVE','CDM003','Mirpur CDM','CDM',NULL,'Diebold'),(10,6,'2026-04-27 06:57:37.411220','2024-10-01','192.168.6.10','Port area','SN-ATM-006','ACTIVE','ATM006','Chattogram ATM','ATM',NULL,'NCR'),(11,1,'2026-04-27 09:29:21.600850','2024-11-01','192.168.1.20','Head office main gate','SN-ATM-007','ACTIVE','ATM007','Motijheel ATM Booth','ATM',NULL,'NCR'),(12,2,'2026-05-01 08:51:11.000000','2026-03-18','10.50.12.12','North Atrium Booth','SN-M5-ATM-012','ACTIVE','ATMM5-012','North Atrium ATM','ATM','2026-05-01 08:51:11.000000','NCR'),(13,4,'2026-05-01 08:51:11.000000','2026-03-26','10.50.13.13','Corporate Annex Zone','SN-M5-CDM-013','ACTIVE','CDMM5-013','Corporate Annex CDM','CDM','2026-05-05 10:16:32.549704','GRG');

--
-- Table structure for table `terminal_cash_bin`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `terminal_cash_bin` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bin_no` varchar(30) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `current_count` int NOT NULL,
  `denomination` decimal(19,2) NOT NULL,
  `max_capacity` int NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','FULL','INACTIVE','LOW_CASH') NOT NULL,
  `terminal_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `terminal_cash_bin`
--

INSERT INTO `terminal_cash_bin` VALUES (1,'BIN-A','2026-04-27 09:47:46.575674',900,1000.00,2000,'ACTIVE',1),(2,'BIN-B','2026-04-27 09:48:51.106179',1000,500.00,3000,'ACTIVE',1),(3,'BIN-1','2026-04-27 09:49:02.670180',2500,1000.00,2500,'FULL',2),(4,'BIN-2','2026-04-27 09:49:11.753182',1500,200.00,4000,'ACTIVE',2),(5,'CASH-01','2026-04-27 09:49:19.988180',500,1000.00,1500,'ACTIVE',3),(6,'CASH-02','2026-04-27 09:49:28.749182',1900,500.00,2000,'ACTIVE',3),(7,'MAIN-01','2026-04-27 09:49:38.300182',3000,1000.00,3000,'FULL',4),(8,'CDM-01','2026-04-27 09:49:55.998177',500,1000.00,2000,'ACTIVE',6),(9,'HIGH-01','2026-04-27 09:50:54.096466',3950,1000.00,4000,'ACTIVE',8),(10,'LOW-01','2026-04-27 09:51:05.795465',550,50.00,6000,'ACTIVE',9),(11,'M5-A1','2026-05-01 08:51:11.000000',180,500.00,2000,'LOW_CASH',12),(12,'M5-A2','2026-05-01 08:51:11.000000',1100,1000.00,2200,'ACTIVE',12),(13,'M5-C1','2026-05-01 08:51:11.000000',2400,200.00,2400,'FULL',13);

--
-- Table structure for table `terminal_reconciliation`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `terminal_reconciliation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `approved_at` datetime(6) DEFAULT NULL,
  `approved_by` bigint DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `physical_amount` decimal(19,2) NOT NULL,
  `recon_date` date NOT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `status` enum('APPROVED','MATCHED','VARIANCE_FOUND') NOT NULL,
  `system_amount` decimal(19,2) NOT NULL,
  `terminal_id` bigint NOT NULL,
  `variance_amount` decimal(19,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `terminal_reconciliation`
--

INSERT INTO `terminal_reconciliation` VALUES (1,NULL,NULL,'2026-05-01 08:51:11.000000',250000.00,'2026-04-15','Balanced end-of-day reconciliation','MATCHED',250000.00,1,0.00),(2,NULL,NULL,'2026-05-01 08:51:11.000000',179500.00,'2026-04-16','Minor shortage identified during vault balancing','VARIANCE_FOUND',180000.00,2,-500.00),(3,'2026-05-01 08:51:11.000000',3,'2026-05-01 08:51:11.000000',320000.00,'2026-04-17','Approved after physical and system amount matched','APPROVED',320000.00,3,0.00),(4,NULL,NULL,'2026-05-01 08:51:11.000000',497500.00,'2026-04-18','Expected cassette gap recorded for follow-up','VARIANCE_FOUND',500000.00,4,-2500.00),(5,'2026-05-01 08:51:11.000000',1,'2026-05-01 08:51:11.000000',150000.00,'2026-04-19','Maintenance-day balance approved by supervisor','APPROVED',150000.00,5,0.00),(6,NULL,NULL,'2026-05-01 08:51:11.000000',88000.00,'2026-04-20','Matched after cash loading close review','MATCHED',88000.00,6,0.00),(7,NULL,NULL,'2026-05-01 08:51:11.000000',124000.00,'2026-04-21','Out-of-service terminal requires variance investigation','VARIANCE_FOUND',125000.00,7,-1000.00),(8,'2026-05-01 08:51:11.000000',2,'2026-05-01 08:51:11.000000',412000.00,'2026-04-22','Large branch balance verified and approved','APPROVED',412000.00,8,0.00),(9,NULL,NULL,'2026-05-01 08:51:11.000000',235000.00,'2026-04-23','Matched Mirpur ATM cash review','MATCHED',235000.00,9,0.00),(10,NULL,NULL,'2026-05-01 08:51:11.000000',556500.00,'2026-04-24','Slight overage recorded during branch cash close','VARIANCE_FOUND',555000.00,10,1500.00),(11,'2026-05-01 08:51:11.000000',3,'2026-05-01 08:51:11.000000',268000.00,'2026-04-25','Motijheel booth end-of-day approved','APPROVED',268000.00,11,0.00),(12,NULL,NULL,'2026-05-01 08:51:11.000000',218000.00,'2026-04-26','New terminal matched after soft launch monitoring','MATCHED',218000.00,12,0.00),(13,NULL,NULL,'2026-05-01 08:51:11.000000',96400.00,'2026-04-27','Inactive CDM variance entered for operator review','VARIANCE_FOUND',98000.00,13,-1600.00);

--
-- Table structure for table `terminal_replenishment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `terminal_replenishment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount_added` decimal(19,2) NOT NULL,
  `bin_no` varchar(30) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `denomination` decimal(19,2) NOT NULL,
  `performed_by` bigint NOT NULL,
  `quantity_added` int NOT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `replenishment_date` date NOT NULL,
  `status` enum('CANCELLED','COMPLETED') NOT NULL,
  `terminal_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `terminal_replenishment`
--

INSERT INTO `terminal_replenishment` VALUES (1,100000.00,'BIN-A','2026-04-27 15:12:02.356060',1000.00,1,100,'Scheduled refill after cash low alert','2026-04-27','COMPLETED',1),(2,150000.00,'BIN-A','2026-04-27 15:12:36.339158',1000.00,2,150,'Morning refill','2026-04-27','COMPLETED',1),(3,100000.00,'BIN-A','2026-04-27 15:26:52.360983',1000.00,1,100,'Safe refill for BIN-A','2026-04-27','COMPLETED',1),(4,100000.00,'BIN-B','2026-04-27 15:27:03.559989',500.00,1,200,'Low cash refill for BIN-B','2026-04-27','COMPLETED',1),(5,60000.00,'BIN-2','2026-04-27 15:27:17.253470',200.00,2,300,'Routine refill for BIN-2','2026-04-27','COMPLETED',2),(6,200000.00,'CASH-01','2026-04-27 15:27:33.860021',1000.00,2,200,'Cash low refill for CASH-01','2026-04-27','COMPLETED',3),(7,50000.00,'CASH-02','2026-04-27 15:27:43.600721',500.00,3,100,'Small top-up for CASH-02','2026-04-27','COMPLETED',3),(8,500000.00,'CDM-01','2026-04-27 15:27:52.636084',1000.00,3,500,'Initial refill for empty CDM bin','2026-04-27','COMPLETED',6),(9,50000.00,'HIGH-01','2026-04-27 15:28:01.334786',1000.00,4,50,'Small refill near capacity','2026-04-27','COMPLETED',8),(10,25000.00,'LOW-01','2026-04-27 15:28:13.576815',50.00,4,500,'Low denomination refill','2026-04-27','COMPLETED',9),(11,50000.00,'BIN-A','2026-04-27 15:28:25.693340',1000.00,5,50,'Second safe top-up for BIN-A','2026-04-28','COMPLETED',1),(12,40000.00,'BIN-2','2026-04-27 15:28:38.523196',200.00,5,200,'Second routine refill for BIN-2','2026-04-28','COMPLETED',2),(13,600000.00,'M5-A2','2026-05-01 08:51:11.000000',1000.00,3,600,'Manual module 5 setup replenishment entry','2026-04-29','COMPLETED',12);

--
-- Table structure for table `transaction_journal`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction_journal` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(18,2) NOT NULL,
  `approved_by` varchar(100) DEFAULT NULL,
  `branch_id` bigint NOT NULL,
  `channel_type` enum('BRANCH_COUNTER','CHEQUE_COUNTER','INTERNAL_TRANSFER','SCHEDULED','SYSTEM') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `narration` varchar(500) DEFAULT NULL,
  `posted_by` varchar(100) NOT NULL,
  `reversal_flag` bit(1) NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `terminal_id` bigint DEFAULT NULL,
  `transaction_date` datetime(6) NOT NULL,
  `transaction_ref` varchar(40) NOT NULL,
  `transaction_status` enum('FAILED','PENDING_REVIEW','POSTED','REVERSED') NOT NULL,
  `transaction_type` enum('CHEQUE_CLEARING','DEPOSIT','REVERSAL','STANDING_INSTRUCTION','TRANSFER','WITHDRAWAL') NOT NULL,
  `credit_account_id` bigint DEFAULT NULL,
  `debit_account_id` bigint DEFAULT NULL,
  `parent_transaction_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_transaction_journal_ref` (`transaction_ref`),
  KEY `FKemoj074j3cpadhqsgsjf9ody5` (`credit_account_id`),
  KEY `FKnbbgwreqolw24i2lqtqt68swh` (`debit_account_id`),
  KEY `FKpjjg2j9nggkf04fgx7x0rxco4` (`parent_transaction_id`),
  CONSTRAINT `FKemoj074j3cpadhqsgsjf9ody5` FOREIGN KEY (`credit_account_id`) REFERENCES `account` (`id`),
  CONSTRAINT `FKnbbgwreqolw24i2lqtqt68swh` FOREIGN KEY (`debit_account_id`) REFERENCES `account` (`id`),
  CONSTRAINT `FKpjjg2j9nggkf04fgx7x0rxco4` FOREIGN KEY (`parent_transaction_id`) REFERENCES `transaction_journal` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_journal`
--

INSERT INTO `transaction_journal` VALUES (1,15000.00,'SYSTEM_SUPERVISOR',1,'BRANCH_COUNTER','2026-05-01 09:00:00.000000','Branch counter cash deposit for account 1','SYSTEM_TELLER',_binary '\0','ACTIVE',101,'2026-05-01 09:00:00.000000','TXN-000001','POSTED','DEPOSIT',1,NULL,NULL),(2,2500.00,'SYSTEM_SUPERVISOR',2,'BRANCH_COUNTER','2026-05-01 09:15:00.000000','Customer withdrawal at Gulshan branch','SYSTEM_TELLER',_binary '','ACTIVE',102,'2026-05-01 09:15:00.000000','TXN-000002','REVERSED','WITHDRAWAL',NULL,2,NULL),(3,5000.00,'SYSTEM_SUPERVISOR',1,'INTERNAL_TRANSFER','2026-05-01 09:30:00.000000','Internal transfer between customer accounts','SYSTEM_TELLER',_binary '','ACTIVE',NULL,'2026-05-01 09:30:00.000000','TXN-000003','REVERSED','TRANSFER',4,3,NULL),(4,8000.00,'SYSTEM_SUPERVISOR',2,'CHEQUE_COUNTER','2026-05-01 09:45:00.000000','Cheque clearing credit for corporate account','SYSTEM_TELLER',_binary '\0','ACTIVE',NULL,'2026-05-01 09:45:00.000000','TXN-000004','POSTED','CHEQUE_CLEARING',5,NULL,NULL),(5,3200.00,'SYSTEM_SUPERVISOR',1,'BRANCH_COUNTER','2026-05-01 10:00:00.000000','Savings cash deposit','SYSTEM_TELLER',_binary '\0','ACTIVE',103,'2026-05-01 10:00:00.000000','TXN-000005','POSTED','DEPOSIT',6,NULL,NULL),(6,1400.00,'SYSTEM_SUPERVISOR',2,'BRANCH_COUNTER','2026-05-01 10:15:00.000000','Umrah savings withdrawal','SYSTEM_TELLER',_binary '\0','ACTIVE',104,'2026-05-01 10:15:00.000000','TXN-000006','POSTED','WITHDRAWAL',NULL,7,NULL),(7,2200.00,'SYSTEM_SUPERVISOR',1,'INTERNAL_TRANSFER','2026-05-01 10:30:00.000000','Student to payroll transfer reference','SYSTEM_TELLER',_binary '\0','ACTIVE',NULL,'2026-05-01 10:30:00.000000','TXN-000007','POSTED','TRANSFER',9,8,NULL),(8,18000.00,'SYSTEM_SUPERVISOR',2,'CHEQUE_COUNTER','2026-05-01 10:45:00.000000','Business current cheque clearing','SYSTEM_TELLER',_binary '\0','ACTIVE',NULL,'2026-05-01 10:45:00.000000','TXN-000008','POSTED','CHEQUE_CLEARING',10,NULL,NULL),(9,4300.00,'SYSTEM_SUPERVISOR',1,'BRANCH_COUNTER','2026-05-01 11:00:00.000000','Women savings deposit reference','SYSTEM_TELLER',_binary '\0','ACTIVE',105,'2026-05-01 11:00:00.000000','TXN-000009','POSTED','DEPOSIT',11,NULL,NULL),(10,1600.00,'SYSTEM_SUPERVISOR',3,'BRANCH_COUNTER','2026-05-01 11:15:00.000000','Senior citizen cash withdrawal','SYSTEM_TELLER',_binary '\0','ACTIVE',106,'2026-05-01 11:15:00.000000','TXN-000010','POSTED','WITHDRAWAL',NULL,12,NULL),(11,300.00,'SYSTEM_SUPERVISOR',2,'INTERNAL_TRANSFER','2026-05-01 11:30:00.000000','NRB FC to monthly deposit transfer','SYSTEM_TELLER',_binary '\0','ACTIVE',NULL,'2026-05-01 11:30:00.000000','TXN-000011','POSTED','TRANSFER',14,13,NULL),(12,1100.00,'SYSTEM_SUPERVISOR',3,'CHEQUE_COUNTER','2026-05-01 11:45:00.000000','Digital savings cheque in review','SYSTEM_TELLER',_binary '\0','ACTIVE',NULL,'2026-05-01 11:45:00.000000','TXN-000012','PENDING_REVIEW','CHEQUE_CLEARING',15,NULL,NULL),(13,2500.00,'SYSTEM_SUPERVISOR',2,'SYSTEM','2026-05-01 12:00:00.000000','Reversal of TXN-000002 due to teller entry correction','SYSTEM_TELLER',_binary '','ACTIVE',NULL,'2026-05-01 12:00:00.000000','TXN-000013','POSTED','REVERSAL',2,NULL,2),(14,5000.00,'SYSTEM_SUPERVISOR',1,'SYSTEM','2026-05-01 12:10:00.000000','Reversal of TXN-000003 after duplicate transfer detect','SYSTEM_TELLER',_binary '','ACTIVE',NULL,'2026-05-01 12:10:00.000000','TXN-000014','POSTED','REVERSAL',3,4,3),(15,125000.00,'SYSTEM_SUPERVISOR',2,'BRANCH_COUNTER','2026-05-01 12:25:00.000000','High value suspicious deposit reference','SYSTEM_TELLER',_binary '\0','ACTIVE',107,'2026-05-01 12:25:00.000000','TXN-000015','POSTED','DEPOSIT',5,NULL,NULL),(16,500.00,'SYSTEM_SUPERVISOR',1,'BRANCH_COUNTER','2026-05-16 01:36:37.878062','branch processing opening deposit','teller.013629',_binary '','ACTIVE',NULL,'2026-05-16 01:36:37.000000','TXN-000016','REVERSED','DEPOSIT',17,NULL,NULL),(17,500.00,'SYSTEM_SUPERVISOR',1,'SYSTEM','2026-05-16 01:36:44.002059','Reversal of TXN-000016: branch reversal validation','ops.officer01',_binary '','ACTIVE',NULL,'2026-05-16 01:36:44.002059','TXN-000017','POSTED','REVERSAL',NULL,17,16),(18,500.00,'SYSTEM_SUPERVISOR',1,'BRANCH_COUNTER','2026-05-16 08:07:21.714753','branch processing opening deposit','teller.080710',_binary '','ACTIVE',NULL,'2026-05-16 08:07:21.000000','TXN-000018','REVERSED','DEPOSIT',18,NULL,NULL),(19,500.00,'SYSTEM_SUPERVISOR',1,'SYSTEM','2026-05-16 08:07:30.431109','Reversal of TXN-000018: branch reversal validation','ops.officer01',_binary '','ACTIVE',NULL,'2026-05-16 08:07:30.431109','TXN-000019','POSTED','REVERSAL',NULL,18,18),(20,500.00,'SYSTEM_SUPERVISOR',1,'BRANCH_COUNTER','2026-05-16 08:10:00.845416','branch processing opening deposit','teller.080951',_binary '','ACTIVE',NULL,'2026-05-16 08:10:00.000000','TXN-000020','REVERSED','DEPOSIT',19,NULL,NULL),(21,500.00,'SYSTEM_SUPERVISOR',1,'SYSTEM','2026-05-16 08:10:08.634421','Reversal of TXN-000020: branch reversal validation','ops.officer01',_binary '','ACTIVE',NULL,'2026-05-16 08:10:08.634421','TXN-000021','POSTED','REVERSAL',NULL,19,20),(22,5000.00,'SYSTEM_SUPERVISOR',1,'BRANCH_COUNTER','2026-06-13 03:44:55.026193','Step 4 operational deposit','admin01',_binary '\0','ACTIVE',1,'2026-06-13 03:43:52.000000','TXN-000022','POSTED','DEPOSIT',21,NULL,NULL),(23,700.00,'SYSTEM_SUPERVISOR',1,'BRANCH_COUNTER','2026-06-13 03:45:25.678334','Step 4 operational withdrawal','admin01',_binary '\0','ACTIVE',1,'2026-06-13 03:43:52.000000','TXN-000023','POSTED','WITHDRAWAL',NULL,21,NULL),(24,500.00,'SYSTEM_SUPERVISOR',1,'INTERNAL_TRANSFER','2026-06-13 03:45:56.609270','Step 4 operational internal transfer','admin01',_binary '\0','ACTIVE',NULL,'2026-06-13 03:43:52.000000','TXN-000024','POSTED','TRANSFER',19,21,NULL);

--
-- Table structure for table `transaction_reversal`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction_reversal` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `approved_at` datetime(6) DEFAULT NULL,
  `approved_by` varchar(100) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `reason` varchar(500) NOT NULL,
  `requested_at` datetime(6) NOT NULL,
  `requested_by` varchar(100) NOT NULL,
  `status` enum('APPROVED','PENDING','REJECTED') NOT NULL,
  `original_transaction_id` bigint NOT NULL,
  `reversal_transaction_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKv99p0xcd3te67hm9rul49p3u` (`original_transaction_id`),
  KEY `FK1teb8mb7quf6xu4ofbox9dwk4` (`reversal_transaction_id`),
  CONSTRAINT `FK1teb8mb7quf6xu4ofbox9dwk4` FOREIGN KEY (`reversal_transaction_id`) REFERENCES `transaction_journal` (`id`),
  CONSTRAINT `FKv99p0xcd3te67hm9rul49p3u` FOREIGN KEY (`original_transaction_id`) REFERENCES `transaction_journal` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_reversal`
--

INSERT INTO `transaction_reversal` VALUES (1,NULL,NULL,'2026-05-01 12:30:00.000000','Pending review on high cash deposit validation','2026-05-01 12:30:00.000000','SYSTEM_TELLER','PENDING',1,NULL),(2,'2026-05-01 12:40:00.000000','SYSTEM_SUPERVISOR','2026-05-01 12:31:00.000000','Approved reversal for wrong cash payout','2026-05-01 12:31:00.000000','SYSTEM_TELLER','APPROVED',2,13),(3,'2026-05-01 12:41:00.000000','SYSTEM_SUPERVISOR','2026-05-01 12:32:00.000000','Approved reversal after duplicate transfer detect','2026-05-01 12:32:00.000000','SYSTEM_TELLER','APPROVED',3,14),(4,NULL,NULL,'2026-05-01 12:33:00.000000','Pending cheque dispute review','2026-05-01 12:33:00.000000','SYSTEM_TELLER','PENDING',4,NULL),(5,NULL,NULL,'2026-05-01 12:34:00.000000','Pending teller narration correction','2026-05-01 12:34:00.000000','SYSTEM_TELLER','PENDING',5,NULL),(6,'2026-05-01 12:45:00.000000','SYSTEM_SUPERVISOR','2026-05-01 12:35:00.000000','Approved operational correction','2026-05-01 12:35:00.000000','SYSTEM_TELLER','APPROVED',6,NULL),(7,NULL,NULL,'2026-05-01 12:36:00.000000','Pending branch maker-checker','2026-05-01 12:36:00.000000','SYSTEM_TELLER','PENDING',7,NULL),(8,'2026-05-01 12:46:00.000000','SYSTEM_SUPERVISOR','2026-05-01 12:37:00.000000','Approved after cheque clearing return','2026-05-01 12:37:00.000000','SYSTEM_TELLER','APPROVED',8,NULL),(9,'2026-05-01 12:47:00.000000','SYSTEM_SUPERVISOR','2026-05-01 12:38:00.000000','Approved duplicate deposit cleanup','2026-05-01 12:38:00.000000','SYSTEM_TELLER','APPROVED',9,NULL),(10,'2026-05-01 12:48:00.000000','SYSTEM_SUPERVISOR','2026-05-01 12:39:00.000000','Approved teller cash shortage adjustment','2026-05-01 12:39:00.000000','SYSTEM_TELLER','APPROVED',10,NULL),(11,NULL,NULL,'2026-05-01 12:40:00.000000','Rejected because source branch mismatch not proven','2026-05-01 12:40:00.000000','SYSTEM_TELLER','REJECTED',11,NULL),(12,NULL,NULL,'2026-05-01 12:41:00.000000','Rejected until cheque finality available','2026-05-01 12:41:00.000000','SYSTEM_TELLER','REJECTED',12,NULL),(13,NULL,NULL,'2026-05-01 12:42:00.000000','Rejected, reversal voucher itself cannot reverse again','2026-05-01 12:42:00.000000','SYSTEM_TELLER','REJECTED',13,NULL),(14,NULL,NULL,'2026-05-01 12:43:00.000000','Rejected, reversal voucher itself cannot reverse again','2026-05-01 12:43:00.000000','SYSTEM_TELLER','REJECTED',14,NULL),(15,NULL,NULL,'2026-05-01 12:44:00.000000','Pending compliance review for high value cash deposit','2026-05-01 12:44:00.000000','SYSTEM_TELLER','PENDING',15,NULL),(16,'2026-05-16 01:36:44.003063','SYSTEM_SUPERVISOR','2026-05-16 01:36:44.004062','branch reversal validation','2026-05-16 01:36:44.003063','ops.officer01','APPROVED',16,17),(17,'2026-05-16 08:07:30.433108','SYSTEM_SUPERVISOR','2026-05-16 08:07:30.433108','branch reversal validation','2026-05-16 08:07:30.433108','ops.officer01','APPROVED',18,19),(18,'2026-05-16 08:10:08.636419','SYSTEM_SUPERVISOR','2026-05-16 08:10:08.636419','branch reversal validation','2026-05-16 08:10:08.636419','ops.officer01','APPROVED',20,21);

--
-- Table structure for table `user_role`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(120) DEFAULT NULL,
  `role_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt7e7djp752sqn6w22i6ocqy6q` (`role_id`),
  KEY `FKj345gk1bovqvfame88rcx7yyx` (`user_id`),
  CONSTRAINT `FKj345gk1bovqvfame88rcx7yyx` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKt7e7djp752sqn6w22i6ocqy6q` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

INSERT INTO `user_role` VALUES (63,'2026-05-08 09:13:27.000000','MODULE_2',1,3),(64,'2026-05-08 09:13:27.000000','MODULE_2',1,9),(65,'2026-05-08 09:13:27.000000','MODULE_2',1,20),(66,'2026-05-08 09:13:27.000000','MODULE_2',2,4),(67,'2026-05-08 09:13:27.000000','MODULE_2',3,2),(68,'2026-05-08 09:13:27.000000','MODULE_2',3,14),(69,'2026-05-08 09:13:27.000000','MODULE_2',4,1),(70,'2026-05-08 09:13:27.000000','MODULE_2',4,5),(71,'2026-05-08 09:13:27.000000','MODULE_2',4,10),(72,'2026-05-08 09:13:27.000000','MODULE_2',4,11),(73,'2026-05-08 09:13:27.000000','MODULE_2',4,12),(74,'2026-05-08 09:13:27.000000','MODULE_2',4,13),(75,'2026-05-08 09:13:27.000000','MODULE_2',4,16),(76,'2026-05-08 09:13:27.000000','MODULE_2',4,17),(77,'2026-05-08 09:13:27.000000','MODULE_2',5,6),(78,'2026-05-08 09:13:27.000000','MODULE_2',6,7),(79,'2026-05-08 09:13:27.000000','MODULE_2',6,15),(80,'2026-05-08 09:13:27.000000','MODULE_2',6,18),(81,'2026-05-08 09:13:27.000000','MODULE_2',6,19),(82,'2026-05-08 09:13:27.000000','MODULE_2',7,8),(83,'2026-05-13 06:12:39.379472','Admin01',1,9),(84,'2026-05-13 06:34:52.798212','Admin01',1,3),(85,'2026-05-14 19:22:01.753760','Admin01',6,19),(86,'2026-05-14 19:41:49.967885','Admin01',4,17),(87,'2026-05-16 01:19:00.555888','Admin01',25,35),(88,'2026-05-16 01:19:00.985879','Admin01',3,36),(89,'2026-05-16 01:24:23.488100','Admin01',25,37),(90,'2026-05-16 01:24:24.075100','Admin01',3,38),(91,'2026-05-16 01:28:01.686714','Admin01',25,39),(92,'2026-05-16 01:28:02.170444','Admin01',3,40),(93,'2026-05-16 01:29:03.060515','Admin01',25,41),(94,'2026-05-16 01:29:03.649510','Admin01',3,42),(95,'2026-05-16 01:29:36.570759','Admin01',25,43),(96,'2026-05-16 01:29:37.249763','Admin01',3,44),(97,'2026-05-16 01:30:12.564272','Admin01',25,45),(98,'2026-05-16 01:30:13.112273','Admin01',3,46),(99,'2026-05-16 01:30:48.915703','Admin01',25,47),(100,'2026-05-16 01:30:49.750706','Admin01',3,48),(101,'2026-05-16 01:31:22.626342','Admin01',25,49),(102,'2026-05-16 01:31:23.744344','Admin01',3,50),(103,'2026-05-16 01:32:44.548755','Admin01',25,51),(104,'2026-05-16 01:32:45.047759','Admin01',3,52),(105,'2026-05-16 01:36:29.492060','Admin01',25,53),(106,'2026-05-16 01:36:29.802059','Admin01',3,54),(107,'2026-05-16 08:07:10.959817','Admin01',25,55),(108,'2026-05-16 08:07:11.379826','Admin01',3,56),(109,'2026-05-16 08:09:51.309373','Admin01',25,57),(110,'2026-05-16 08:09:51.985373','Admin01',3,58),(111,'2026-06-05 04:31:11.176817','admin01',1,3),(112,'2026-06-06 09:27:33.706792','admin01',3,44),(113,'2026-06-06 09:29:38.214879','admin01',3,48),(114,'2026-06-06 09:31:28.181214','admin01',4,12),(115,'2026-06-07 15:16:10.717037','admin01',7,8),(116,'2026-06-13 11:12:06.431973','admin01',25,51),(117,'2026-06-23 06:06:59.790466','admin01',1,9);

--
-- Table structure for table `user_session`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_session` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `device_info` varchar(255) DEFAULT NULL,
  `ip_address` varchar(80) DEFAULT NULL,
  `jwt_id` varchar(180) DEFAULT NULL,
  `login_time` datetime(6) NOT NULL,
  `logout_time` datetime(6) DEFAULT NULL,
  `status` varchar(40) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlr29o11uswdgcnn8swu3q15f8` (`user_id`),
  CONSTRAINT `FKlr29o11uswdgcnn8swu3q15f8` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=260 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_session`
--


--
-- Table structure for table `users`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(150) DEFAULT NULL,
  `failed_login_count` int DEFAULT NULL,
  `full_name` varchar(150) DEFAULT NULL,
  `last_login_at` datetime(6) DEFAULT NULL,
  `locked_at` datetime(6) DEFAULT NULL,
  `mobile` varchar(30) DEFAULT NULL,
  `password_changed_at` datetime(6) DEFAULT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `status` enum('ACTIVE','INACTIVE','LOCKED') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_type` enum('CUSTOMER','STAFF') NOT NULL,
  `username` varchar(100) DEFAULT NULL,
  `role_id` bigint DEFAULT NULL,
  `branch_id` bigint DEFAULT NULL,
  `must_change_password` bit(1) NOT NULL,
  `email_verified` bit(1) NOT NULL,
  `mobile_verified` bit(1) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `created_by` varchar(120) DEFAULT NULL,
  `designation` varchar(120) DEFAULT NULL,
  `employee_no` varchar(60) DEFAULT NULL,
  `is_locked` bit(1) NOT NULL,
  `updated_by` varchar(120) DEFAULT NULL,
  `user_code` varchar(40) DEFAULT NULL,
  `profile_image_name` varchar(180) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  UNIQUE KEY `idx_users_username` (`username`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK63cf888pmqtt5tipcne79xsbm` (`mobile`),
  KEY `fk_users_role` (`role_id`),
  CONSTRAINT `fk_users_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

INSERT INTO `users` VALUES (1,'2026-04-13 14:55:54.131397','erterw',0,'Arman Mamun','2026-05-07 09:12:59.000000',NULL,'ewrtewrt','2026-05-08 09:47:11.000000','6cf0ea55e5fd5e692e007b16339a83f4319370cdb8b6193c1630820119cbba50','ACTIVE','2026-06-05 04:09:39.000000','STAFF','retwer',4,1,_binary '\0',_binary '\0',_binary '\0',_binary '','MODULE_2','Bank Officer','EMP-0001',_binary '\0','PROFILE_SYNC','USR-00001','100001_Arman Mamun.jpg'),(2,'2026-04-13 14:58:12.007905','asfasfasf',0,'Saifur Saif','2026-05-06 09:12:59.000000',NULL,'fasdfasdf','2026-05-08 09:47:11.000000','6cf0ea55e5fd5e692e007b16339a83f4319370cdb8b6193c1630820119cbba50','ACTIVE','2026-06-05 04:09:39.000000','STAFF','fasf',3,2,_binary '\0',_binary '\0',_binary '\0',_binary '','MODULE_2','Bank Officer','EMP-0002',_binary '\0','PROFILE_SYNC','USR-00002','100002_Saifur Saif.jpg'),(3,'2026-04-13 15:26:57.308483','user021@example.com',0,'Md. Masud Rana','2026-06-23 04:03:27.565189',NULL,'01700000021','2026-06-05 02:54:31.000000','$2a$10$Sc90gxCi6uEduv1CyzXheu8IDeUMKqJMwlhTp1pSGzhz2FmMEXvya','ACTIVE','2026-06-23 04:03:27.617200','STAFF','admin01',1,1,_binary '\0',_binary '',_binary '',_binary '','MODULE_2','Super Admin','EMP-0003',_binary '\0','admin01','USR-00003','3f7b81d9-171f-4806-be0e-eb23c1f265ce.jpg'),(4,'2026-05-08 08:59:44.000000','user048@example.com',0,'Nur Nabi','2026-05-16 08:16:05.675612',NULL,'01700000061','2026-06-05 02:54:31.000000','$2a$10$Sc90gxCi6uEduv1CyzXheu8IDeUMKqJMwlhTp1pSGzhz2FmMEXvya','ACTIVE','2026-06-05 04:09:39.000000','STAFF','branch.manager01',2,1,_binary '\0',_binary '',_binary '',_binary '','MODULE_2','Branch Manager','EMP-0004',_binary '\0','PROFILE_SYNC','USR-00004','100004_Nur Nabi.jpg'),(5,'2026-05-08 08:59:44.000000','user049@example.com',0,'Samira Saba','2026-05-16 08:18:27.096111',NULL,'01700000062','2026-06-05 02:54:31.000000','$2a$10$Sc90gxCi6uEduv1CyzXheu8IDeUMKqJMwlhTp1pSGzhz2FmMEXvya','ACTIVE','2026-06-05 04:09:39.000000','STAFF','ops.officer01',4,1,_binary '\0',_binary '',_binary '',_binary '','MODULE_2','Operations Officer','EMP-0005',_binary '\0','PROFILE_SYNC','USR-00005','100005_Samira Saba.jpg'),(6,'2026-05-08 08:59:44.000000','user050@example.com',0,'Nur Nabi','2026-05-16 08:09:50.351369',NULL,'01700000063','2026-06-05 02:54:31.000000','$2a$10$Sc90gxCi6uEduv1CyzXheu8IDeUMKqJMwlhTp1pSGzhz2FmMEXvya','ACTIVE','2026-06-05 04:09:39.000000','CUSTOMER','customer.1',5,NULL,_binary '\0',_binary '',_binary '',_binary '','MODULE_2','Retail Customer','CUS-0006',_binary '\0','PROFILE_SYNC','USR-00006','100006_Nur Nabi.jpg'),(7,'2026-05-08 08:59:44.000000','user051@example.com',0,'Alamgir Kabir','2026-05-16 08:18:26.268108',NULL,'01700000064','2026-06-05 02:54:31.000000','$2a$10$Sc90gxCi6uEduv1CyzXheu8IDeUMKqJMwlhTp1pSGzhz2FmMEXvya','ACTIVE','2026-06-05 04:09:39.000000','STAFF','investment.officer02',6,3,_binary '\0',_binary '',_binary '',_binary '','MODULE_2','Investment Officer','EMP-0007',_binary '\0','PROFILE_SYNC','USR-00007','100007_Alamgir Kabir.jpg'),(8,'2026-05-08 08:59:44.000000','user052@example.com',0,'Akib Uddin','2026-06-23 03:17:59.816227',NULL,'01700000065','2026-06-05 02:54:31.000000','$2a$10$Sc90gxCi6uEduv1CyzXheu8IDeUMKqJMwlhTp1pSGzhz2FmMEXvya','ACTIVE','2026-06-23 03:17:59.834232','STAFF','shariah.board01',7,3,_binary '\0',_binary '',_binary '',_binary '','MODULE_2','Shariah Board Member','EMP-0008',_binary '\0','shariah.board01','USR-00008','62c7b4fc-668a-4d68-b63d-9c2ee5c828cf_IMG_20260321_091445_3 - Md. Akib Uddin.jpg'),(9,'2026-05-08 08:59:44.000000','user053@example.com',0,'Rana','2026-05-03 08:59:44.000000',NULL,'01700000066','2026-05-08 09:47:11.000000','6cf0ea55e5fd5e692e007b16339a83f4319370cdb8b6193c1630820119cbba50','ACTIVE','2026-06-23 06:06:59.832479','STAFF','audit.reviewer01',1,4,_binary '\0',_binary '',_binary '',_binary '','MODULE_2','Audit Reviewer','EMP-0009',_binary '\0','admin01','USR-00009','27ba70b5-989f-4c60-b2ae-800fb457d1b2_WhatsApp Image 2026-06-21 at 11.47.38 AM _2_.jpeg'),(10,'2026-05-08 08:59:44.000000','user054@example.com',0,'Abdullah Naser Zayed','2026-05-02 08:59:44.000000',NULL,'01700000067','2026-05-08 09:47:11.000000','6cf0ea55e5fd5e692e007b16339a83f4319370cdb8b6193c1630820119cbba50','ACTIVE','2026-06-05 04:09:39.000000','STAFF','compliance.analyst01',4,4,_binary '\0',_binary '',_binary '',_binary '','MODULE_2','Compliance Analyst','EMP-0010',_binary '\0','PROFILE_SYNC','USR-00010','100010_Zayed - Abdullah Naser Zayed.jpg'),(11,'2026-05-08 08:59:44.000000','user055@example.com',0,'Fabiha Anbar','2026-05-01 08:59:44.000000',NULL,'01700000068','2026-05-08 09:47:11.000000','6cf0ea55e5fd5e692e007b16339a83f4319370cdb8b6193c1630820119cbba50','ACTIVE','2026-06-05 04:09:39.000000','STAFF','cso.staff01',4,5,_binary '\0',_binary '',_binary '',_binary '','MODULE_2','Customer Service Officer','EMP-0011',_binary '\0','PROFILE_SYNC','USR-00011','100011_Fabiha Anbar.jpg'),(12,'2026-05-08 08:59:44.000000','user056@example.com',0,'Nazmus Sakib','2026-04-30 08:59:44.000000',NULL,'01700000069','2026-05-08 09:47:11.000000','6cf0ea55e5fd5e692e007b16339a83f4319370cdb8b6193c1630820119cbba50','ACTIVE','2026-06-06 09:31:28.205992','STAFF','kyc.reviewer01',4,5,_binary '\0',_binary '',_binary '',_binary '','MODULE_2','KYC Reviewer','EMP-0012',_binary '\0','admin01','USR-00012','06a62ba7-b517-47c1-b909-74fbbcb5a78c.jpg'),(13,'2026-05-08 08:59:44.000000','user057@example.com',0,'Nur Nabi','2026-04-29 08:59:44.000000',NULL,'01700000070','2026-05-08 09:47:11.000000','6cf0ea55e5fd5e692e007b16339a83f4319370cdb8b6193c1630820119cbba50','ACTIVE','2026-06-05 04:09:39.000000','STAFF','acct.supervisor01',4,6,_binary '\0',_binary '',_binary '',_binary '','MODULE_2','Account Supervisor','EMP-0013',_binary '\0','PROFILE_SYNC','USR-00013','100013_Nur Nabi.jpg'),(14,'2026-05-08 08:59:44.000000','user058@example.com',4,'Md. Firoz','2026-04-28 08:59:44.000000',NULL,'01700000041','2026-04-26 08:59:44.000000','b6bc7b58510319a151d168ba3d5aecb3ac0a9708d06dd930f37fbc89b6cdc697','LOCKED','2026-06-05 04:09:39.000000','STAFF','txn.supervisor01',3,6,_binary '\0',_binary '',_binary '',_binary '\0','MODULE_2','Transaction Supervisor','EMP-0014',_binary '','PROFILE_SYNC','USR-00014','100014_Md. Firoz.jpg'),(15,'2026-05-08 08:59:44.000000','user059@example.com',0,'Arman Mamun','2026-04-27 08:59:44.000000',NULL,'01700000046','2026-05-08 09:47:11.000000','6cf0ea55e5fd5e692e007b16339a83f4319370cdb8b6193c1630820119cbba50','ACTIVE','2026-06-05 04:09:39.000000','STAFF','profit.officer01',6,7,_binary '\0',_binary '',_binary '',_binary '','MODULE_2','Profit Officer','EMP-0015',_binary '\0','PROFILE_SYNC','USR-00015','100015_Arman Mamun.jpg'),(16,'2026-05-08 08:59:44.000000','user060@example.com',0,'Samia Rahman','2026-04-26 08:59:44.000000',NULL,'01700000042','2026-05-08 09:47:11.000000','6cf0ea55e5fd5e692e007b16339a83f4319370cdb8b6193c1630820119cbba50','ACTIVE','2026-06-05 04:09:39.000000','STAFF','card.ops01',4,7,_binary '\0',_binary '',_binary '',_binary '','MODULE_2','Card Operations Officer','EMP-0016',_binary '\0','PROFILE_SYNC','USR-00016','100016_Samia Rahman.jpg'),(17,'2026-05-08 08:59:44.000000','user061@example.com',0,'Ismita Saha','2026-04-25 08:59:44.000000',NULL,'01700000071','2026-05-08 09:47:11.000000','6cf0ea55e5fd5e692e007b16339a83f4319370cdb8b6193c1630820119cbba50','ACTIVE','2026-06-05 04:09:39.000000','STAFF','statement.desk01',4,8,_binary '\0',_binary '',_binary '',_binary '','MODULE_2','Statement Desk Officer','EMP-0017',_binary '\0','PROFILE_SYNC','USR-00017','100017_Ismita Saha.jpg'),(18,'2026-05-08 08:59:44.000000','user062@example.com',0,'ABDULLAH AL MAHMUD','2026-04-24 08:59:44.000000',NULL,'01700000043','2026-05-08 09:47:11.000000','6cf0ea55e5fd5e692e007b16339a83f4319370cdb8b6193c1630820119cbba50','ACTIVE','2026-06-05 04:09:39.000000','STAFF','deposit.scheme01',6,8,_binary '\0',_binary '',_binary '',_binary '','MODULE_2','Deposit Scheme Officer','EMP-0018',_binary '\0','PROFILE_SYNC','USR-00018','100018_Abdullah  - ABDULLAH AL MAHMUD (1).jpg'),(19,'2026-05-08 08:59:44.000000','user063@example.com',0,'Alamgir Kabir','2026-04-23 08:59:44.000000',NULL,'01700000072','2026-05-08 09:47:11.000000','6cf0ea55e5fd5e692e007b16339a83f4319370cdb8b6193c1630820119cbba50','ACTIVE','2026-06-05 04:09:39.000000','STAFF','contract.review01',6,9,_binary '\0',_binary '',_binary '',_binary '','MODULE_2','Contract Reviewer','EMP-0019',_binary '\0','PROFILE_SYNC','USR-00019','100019_Alamgir Kabir.jpg'),(20,'2026-05-08 08:59:44.000000','user064@example.com',0,'Samira Saba','2026-04-22 08:59:44.000000',NULL,'01700000049','2026-04-18 08:59:44.000000','b6bc7b58510319a151d168ba3d5aecb3ac0a9708d06dd930f37fbc89b6cdc697','ACTIVE','2026-06-05 04:09:39.000000','STAFF','notify.admin01',1,10,_binary '\0',_binary '',_binary '',_binary '','MODULE_2','Notification Administrator','EMP-0020',_binary '\0','PROFILE_SYNC','USR-00020','100020_Samira Saba.jpg'),(35,'2026-05-16 01:19:00.552875','user065@example.com',0,'Jamilur Rahman',NULL,NULL,'0171011900',NULL,'$2a$10$ANBqIukFnV9i.Im3cWpzyO1MykWJQ1Kvqjx8PFupLkRSrK1iW2K06','ACTIVE','2026-06-05 04:09:39.000000','STAFF','branch.staff.011900',25,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Branch Staff','BS-011900',_binary '\0','PROFILE_SYNC','USR-00021','100021_Jamilur Rahman.jpg'),(36,'2026-05-16 01:19:00.980882','user066@example.com',0,'S.M. Tasrif Zaman',NULL,NULL,'0181011900',NULL,'$2a$10$AaOv3Oz136UEjVtyygkAEee1gzF5EHOEPYu628B53ikuhUOH8GBam','ACTIVE','2026-06-05 04:09:39.000000','STAFF','teller.011900',3,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Teller','TL-011900',_binary '\0','PROFILE_SYNC','USR-00022','100022__20190816_150404 - S.M. Tasrif Zaman.jpg'),(37,'2026-05-16 01:24:23.481089','user067@example.com',0,'Tushar Ahmed','2026-05-16 01:24:24.662097',NULL,'0171012423',NULL,'$2a$10$aLEiaGwqUoxaca1.n4MMqejOvACVDnler5Bp/FJ7dZDyW6gE5Rqo.','ACTIVE','2026-06-05 04:09:39.000000','STAFF','branch.staff.012423',25,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Branch Staff','BS-012423',_binary '\0','PROFILE_SYNC','USR-00023','100023_Tushar Ahmed.jpg'),(38,'2026-05-16 01:24:24.072099','user068@example.com',0,'Fabiha Anbar','2026-05-16 01:24:24.866098',NULL,'0181012423',NULL,'$2a$10$uEETr7CjHKXRw1MKS.noieEk6jzP32wxnmuaC2Hji/tRp7MdptCtG','ACTIVE','2026-06-05 04:09:39.000000','STAFF','teller.012423',3,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Teller','TL-012423',_binary '\0','PROFILE_SYNC','USR-00024','100024_Fabiha Anbar.jpg'),(39,'2026-05-16 01:28:01.680704','user069@example.com',0,'Samia Rahman','2026-05-16 01:28:02.708815',NULL,'0171012801',NULL,'$2a$10$Vh6BDqnme29Yc.jLVMUZmeEh8ilRF5UxGTOr2CKjY.bh2Q.uQ3qQa','ACTIVE','2026-06-05 04:09:39.000000','STAFF','branch.staff.012801',25,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Branch Staff','BS-012801',_binary '\0','PROFILE_SYNC','USR-00025','100025_Samia Rahman.jpg'),(40,'2026-05-16 01:28:02.167441','user070@example.com',0,'ABDULLAH AL MAHMUD','2026-05-16 01:28:02.923815',NULL,'0181012801',NULL,'$2a$10$xNym8BvYyXTFgqEHXeMMRO9jvKIFaoCVNWbliQCOPIgWTnj02WGXi','ACTIVE','2026-06-05 04:09:39.000000','STAFF','teller.012801',3,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Teller','TL-012801',_binary '\0','PROFILE_SYNC','USR-00026','100026_Abdullah  - ABDULLAH AL MAHMUD (1).jpg'),(41,'2026-05-16 01:29:03.057512','user071@example.com',0,'Alamgir Kabir','2026-05-16 01:29:04.294786',NULL,'0171012902',NULL,'$2a$10$kkt.WLcFwlFjHSBNHqpfpOl8RQd0arTanzus8JaDq1d18AgAvrLhG','ACTIVE','2026-06-05 04:09:39.000000','STAFF','branch.staff.012902',25,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Branch Staff','BS-012902',_binary '\0','PROFILE_SYNC','USR-00027','100027_Alamgir Kabir.jpg'),(42,'2026-05-16 01:29:03.648501','user072@example.com',0,'Jamilur Rahman','2026-05-16 01:29:04.491785',NULL,'0181012902',NULL,'$2a$10$u2ksCGe/MkBERnEGge.youhO7/LT9w15wIqLI2yGdovs9DCM3NT.e','ACTIVE','2026-06-05 04:09:39.000000','STAFF','teller.012902',3,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Teller','TL-012902',_binary '\0','PROFILE_SYNC','USR-00028','100028_Jamilur Rahman.jpg'),(43,'2026-05-16 01:29:36.569754','user073@example.com',0,'Fabiha Anbar','2026-05-16 01:29:37.877761',NULL,'0171012936',NULL,'$2a$10$1hWkF2uJztG8b0QshxM6ReSTFabj11wUYhuEsL8Z5ikWM.eHD.19G','ACTIVE','2026-06-05 04:09:39.000000','STAFF','branch.staff.012936',25,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Branch Staff','BS-012936',_binary '\0','PROFILE_SYNC','USR-00029','100029_Fabiha Anbar.jpg'),(44,'2026-05-16 01:29:37.247761','user074@example.com',0,'Jamilur Rahman','2026-05-16 01:29:38.079766',NULL,'0181012936',NULL,'$2a$10$RAoJ9D9c75T3KThq907g0eCiWJgi/4zr1COZslzp.nJQQzHGdv7G6','ACTIVE','2026-06-06 09:27:33.723786','STAFF','teller.012936',3,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Teller','TL-012936',_binary '\0','admin01','USR-00030','38c5fa59-b680-40ff-bc16-2c1495a4c187.jpg'),(45,'2026-05-16 01:30:12.563264','user075@example.com',0,'Zarin Islam','2026-05-16 01:30:13.696277',NULL,'01700000073',NULL,'$2a$10$cWyFSvML0w1EyiliPkbMZeVXl1NgBA9AaspOt9a1fsNJ.9yxy8AKK','ACTIVE','2026-06-05 04:09:39.000000','STAFF','branch.staff.013012',25,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Branch Staff','BS-013012',_binary '\0','PROFILE_SYNC','USR-00031','100031_Zarin Islam.jpg'),(46,'2026-05-16 01:30:13.110267','user076@example.com',0,'Md. Akib Uddin','2026-05-16 01:30:13.894272',NULL,'01700000074',NULL,'$2a$10$xE0Vo0ETqJqMJ/7IceBy7Osk1AToxf2q6u6Uw1Az2RGGjFpzJhE1G','INACTIVE','2026-06-10 18:47:41.549089','STAFF','teller.013012',3,1,_binary '',_binary '\0',_binary '\0',_binary '\0','Admin01','Teller','TL-013012',_binary '\0','SYSTEM','USR-00032','100032_IMG_20260321_091445~3 - Md. Akib Uddin.jpg'),(47,'2026-05-16 01:30:48.912704','user077@example.com',0,'Muhammad Ratul','2026-05-16 01:30:50.285706',NULL,'01700000075',NULL,'$2a$10$2sjkrNphdj/.sqPeGpwnAe5xduq7M/4cBgRckyZ2RGWvoz5UjdfGi','ACTIVE','2026-06-05 04:09:39.000000','STAFF','branch.staff.013048',25,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Branch Staff','BS-013048',_binary '\0','PROFILE_SYNC','USR-00033','100033_Muhammad Ratul.jpg'),(48,'2026-05-16 01:30:49.749697','user078@example.com',0,'Sania Afrin','2026-05-16 01:30:50.482704',NULL,'01700000076',NULL,'$2a$10$6LhnsvvDwUneMFgFZY7wOup1EoBz7sB7lC702NKz6GiqVk9QcwMMK','ACTIVE','2026-06-06 09:29:38.232875','STAFF','teller.013048',3,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Teller','TL-013048',_binary '\0','admin01','USR-00034','db0829ce-01ed-4236-a785-9f76126ac1cb.jpg'),(49,'2026-05-16 01:31:22.624341','user079@example.com',0,'Arifa Khanam','2026-05-16 01:31:24.659342',NULL,'01700000077',NULL,'$2a$10$nrnGaZDXZJhdX4Gp0DYIQ.oBCOZ0AGzkqtUcIzAsv4W/O6mk.Qepa','ACTIVE','2026-06-05 04:09:39.000000','STAFF','branch.staff.013122',25,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Branch Staff','BS-013122',_binary '\0','PROFILE_SYNC','USR-00035','100035_Arifa Khanam.jpg'),(50,'2026-05-16 01:31:23.742342','user080@example.com',0,'Fabiha Anbar','2026-05-16 01:31:24.880338',NULL,'01700000078',NULL,'$2a$10$l1XepHIW0BrxOT0UUlYMz.RTgBelD.hk5fS2KxS1E5AnJrwyRJy4e','ACTIVE','2026-06-05 04:09:39.000000','STAFF','teller.013122',3,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Teller','TL-013122',_binary '\0','PROFILE_SYNC','USR-00036','100036_Fabiha Anbar.jpg'),(51,'2026-05-16 01:32:44.547751','user081@example.com',0,'Muhit Hassan','2026-05-16 01:32:45.412763',NULL,'01700000079',NULL,'$2a$10$gA8HjAKTiLX3sZEWzavR5OWiDfaURaPNhOZDtryFEqvU94UAqL5ay','ACTIVE','2026-06-13 11:12:06.463973','STAFF','branch.staff.013244',25,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Branch Staff','BS-013244',_binary '\0','admin01','USR-00037','e938996b-f720-4867-911c-7dc31d1c2887_Muhit Hasan.jpg'),(52,'2026-05-16 01:32:45.045753','user082@example.com',0,'Jamilur Rahman','2026-05-16 01:32:45.602643',NULL,'01700000080',NULL,'$2a$10$3IYUXAKbQukJ3Ys9dWc0Pe6ZgwsZ4FMXMgpGKGtxHWuGatT6g8sPi','ACTIVE','2026-06-05 04:09:39.000000','STAFF','teller.013244',3,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Teller','TL-013244',_binary '\0','PROFILE_SYNC','USR-00038','100038_Jamilur Rahman.jpg'),(53,'2026-05-16 01:36:29.490052','user083@example.com',0,'M Olinur','2026-05-16 01:36:30.177061',NULL,'01700000081',NULL,'$2a$10$283l.j6h8qMlErsNE.lMtOGX..OP8hRRaN4D/TXyAvzTbBaLHHogC','ACTIVE','2026-06-05 04:09:39.000000','STAFF','branch.staff.013629',25,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Branch Staff','BS-013629',_binary '\0','PROFILE_SYNC','USR-00039','100039_rsz_1p- - M Olinur.jpg'),(54,'2026-05-16 01:36:29.800062','user084@example.com',0,'Jahidul Islam','2026-05-16 01:36:30.413061',NULL,'01700000082',NULL,'$2a$10$fTXbRzRjpWH0x0FYv7kGtOp26Bas7wyfzRQd8PpXdxafk2o9kgbqi','ACTIVE','2026-06-05 04:09:39.000000','STAFF','teller.013629',3,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Teller','TL-013629',_binary '\0','PROFILE_SYNC','USR-00040','100040_Jahidul Islam.jpg'),(55,'2026-05-16 08:07:10.957814','user085@example.com',0,'Muna Akter','2026-05-16 08:07:11.822821',NULL,'01700000083',NULL,'$2a$10$JypwdLDQKOkzuyQWdmIJGeFnxWx6Gz63.6Xxvv.SInAWpwJTzYyFG','ACTIVE','2026-06-05 04:09:39.000000','STAFF','branch.staff.080710',25,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Branch Staff','BS-080710',_binary '\0','PROFILE_SYNC','USR-00041','100041_Muna Akter.jpg'),(56,'2026-05-16 08:07:11.377812','user086@example.com',0,'S.M. Tasrif Zaman','2026-05-16 08:07:12.036821',NULL,'01700000084',NULL,'$2a$10$SFzlc/xdGW2HTY5jMK4YHu6XjZ84G/RNbX054IN5/EgsZZPcBVxvm','ACTIVE','2026-06-05 04:09:39.000000','STAFF','teller.080710',3,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Teller','TL-080710',_binary '\0','PROFILE_SYNC','USR-00042','100042__20190816_150404 - S.M. Tasrif Zaman.jpg'),(57,'2026-05-16 08:09:51.307367','user087@example.com',0,'Zarin Islam','2026-05-16 08:09:52.340367',NULL,'01700000085',NULL,'$2a$10$fwxxYMWqTl3U094OO3//BO2LacfcoGlNn2UnS65cX4QEnzfeuVEhW','ACTIVE','2026-06-05 04:09:39.000000','STAFF','branch.staff.080951',25,1,_binary '',_binary '\0',_binary '\0',_binary '','Admin01','Branch Staff','BS-080951',_binary '\0','PROFILE_SYNC','USR-00043','100043_Zarin Islam.jpg'),(58,'2026-05-16 08:09:51.984366','user088@example.com',0,'Md. Firoz','2026-05-16 08:09:52.554417',NULL,'01700000086','2026-06-05 02:54:31.000000','$2a$10$Sc90gxCi6uEduv1CyzXheu8IDeUMKqJMwlhTp1pSGzhz2FmMEXvya','ACTIVE','2026-06-05 04:09:39.000000','STAFF','teller.080951',3,1,_binary '\0',_binary '\0',_binary '\0',_binary '','Admin01','Teller','TL-080951',_binary '\0','PROFILE_SYNC','USR-00044','100044_Md. Firoz.jpg');

--
-- Table structure for table `vault_balance`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vault_balance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `balance_date` date NOT NULL,
  `branch_id` bigint NOT NULL,
  `closing_balance` decimal(18,2) NOT NULL DEFAULT '0.00',
  `created_at` datetime(6) NOT NULL,
  `is_closed` tinyint(1) NOT NULL DEFAULT '0',
  `opening_balance` decimal(18,2) NOT NULL DEFAULT '0.00',
  `closed_at` datetime(6) DEFAULT NULL,
  `closed_by` bigint DEFAULT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `status` varchar(30) DEFAULT 'ACTIVE',
  `total_cash_in` decimal(18,2) NOT NULL DEFAULT '0.00',
  `total_cash_out` decimal(18,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_vault_balance_branch_date` (`branch_id`,`balance_date`),
  UNIQUE KEY `uk_vault_branch_date` (`branch_id`,`balance_date`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vault_balance`
--

INSERT INTO `vault_balance` VALUES (1,'2026-04-19',1,100000.00,'2026-04-19 05:59:30.057095',0,100000.00,NULL,NULL,NULL,NULL,0.00,0.00),(2,'2026-04-26',1,500000.00,'2026-04-26 06:46:45.758298',0,500000.00,NULL,NULL,'Opening vault - Dhaka Main','ACTIVE',0.00,0.00),(3,'2026-04-26',2,350000.00,'2026-04-26 06:46:58.005322',0,350000.00,NULL,NULL,'Opening vault - Chattogram','ACTIVE',0.00,0.00),(4,'2026-04-26',3,420000.00,'2026-04-26 06:47:11.618331',0,420000.00,NULL,NULL,'Opening vault - Sylhet','ACTIVE',0.00,0.00),(5,'2026-04-27',1,520000.00,'2026-04-26 06:47:22.648327',0,520000.00,NULL,NULL,'Next day opening','ACTIVE',0.00,0.00),(6,'2026-04-27',2,370000.00,'2026-04-26 06:47:33.208325',0,370000.00,NULL,NULL,'Next day opening','ACTIVE',0.00,0.00),(7,'2026-04-27',3,410000.00,'2026-04-26 06:47:43.447380',0,410000.00,NULL,NULL,'Daily vault setup','ACTIVE',0.00,0.00),(8,'2026-04-26',4,600000.00,'2026-04-26 06:47:54.874327',0,600000.00,NULL,NULL,'Corporate branch vault','ACTIVE',0.00,0.00),(9,'2026-04-26',5,280000.00,'2026-04-26 06:48:03.903187',0,280000.00,NULL,NULL,'Industrial branch vault','ACTIVE',0.00,0.00),(10,'2026-04-27',4,610000.00,'2026-04-26 06:48:11.415360',0,610000.00,NULL,NULL,'Corporate next day','ACTIVE',0.00,0.00),(11,'2026-04-27',5,300000.00,'2026-04-26 06:48:21.115426',0,300000.00,NULL,NULL,'Industrial next day','ACTIVE',0.00,0.00),(12,'2026-05-08',12,3730000.00,'2026-05-08 15:04:47.000000',1,3500000.00,'2026-05-08 15:04:47.000000',12,'Closed after standard EOD balancing','ACTIVE',650000.00,420000.00),(13,'2026-05-08',13,3080000.00,'2026-05-08 15:04:47.000000',1,2850000.00,'2026-05-08 15:04:47.000000',13,'Corporate branch vault closed after cash receipt consolidation','ACTIVE',480000.00,250000.00),(14,'2026-05-08',14,2685000.00,'2026-05-08 15:04:47.000000',0,2600000.00,NULL,NULL,'Vault remains open pending branch manager sign-off','ACTIVE',395000.00,310000.00),(15,'2026-05-08',15,4290000.00,'2026-05-08 15:04:47.000000',1,4150000.00,'2026-05-08 15:04:47.000000',15,'Branch closed vault after same-day remittance reconciliation','ACTIVE',720000.00,580000.00);

--
-- Table structure for table `vault_transaction`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vault_transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(18,2) NOT NULL,
  `branch_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `reference_no` varchar(100) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `transaction_type` enum('DEPOSIT_IN','INTER_BRANCH_IN','INTER_BRANCH_OUT','WITHDRAWAL_OUT') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vault_transaction`
--

INSERT INTO `vault_transaction` VALUES (1,250000.00,1,'2026-05-08 11:35:00.000000','VTX-20260508-001','Cash dispatched to Gulshan Corporate Branch for approved transfer','INTER_BRANCH_OUT'),(2,250000.00,2,'2026-05-08 12:10:00.000000','VTX-20260508-002','Cash received from Dhaka Main Branch','INTER_BRANCH_IN'),(3,180000.00,1,'2026-05-09 10:50:00.000000','VTX-20260509-001','Cash dispatched to Mirpur Branch for replenishment','INTER_BRANCH_OUT'),(4,180000.00,5,'2026-05-09 11:30:00.000000','VTX-20260509-002','Cash received from Dhaka Main Branch','INTER_BRANCH_IN'),(5,315000.00,3,'2026-05-31 16:40:00.000000','VTX-20260531-001','End-of-day high value deposit cash lodged into vault','DEPOSIT_IN'),(6,95000.00,2,'2026-05-31 15:20:00.000000','VTX-20260531-002','Customer withdrawal cash issued from branch vault','WITHDRAWAL_OUT');

--
-- Table structure for table `verification_attempt_log`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `verification_attempt_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `attempt_status` varchar(40) NOT NULL,
  `attempt_type` varchar(40) NOT NULL,
  `attempt_value_masked` varchar(160) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(120) DEFAULT NULL,
  `device_info` varchar(255) DEFAULT NULL,
  `ip_address` varchar(80) DEFAULT NULL,
  `remarks` varchar(1000) DEFAULT NULL,
  `request_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmt9huo3fl5l6lcbjrhl3ggsu7` (`request_id`),
  CONSTRAINT `FKmt9huo3fl5l6lcbjrhl3ggsu7` FOREIGN KEY (`request_id`) REFERENCES `otp_verification_request` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=662 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `verification_attempt_log`
--


--
-- Table structure for table `verification_channel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `verification_channel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `channel_code` varchar(40) NOT NULL,
  `channel_name` varchar(120) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `provider_name` varchar(120) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKb3jgk39kewny7qmmgmu13x2og` (`channel_code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `verification_channel`
--

INSERT INTO `verification_channel` VALUES (1,'EMAIL','Email Verification Channel','2026-04-01 09:00:00.000000','LOCAL_EMAIL_SIMULATOR','ACTIVE','2026-04-01 09:00:00.000000'),(2,'SMS','SMS Verification Channel','2026-04-01 09:05:00.000000','LOCAL_SMS_SIMULATOR','ACTIVE','2026-04-01 09:05:00.000000');

--
-- Table structure for table `verification_dispatch_queue`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `verification_dispatch_queue` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `channel_type` enum('EMAIL','SMS') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `dispatch_status` enum('EXPIRED','FAILED','PENDING','SENT','VERIFIED') NOT NULL,
  `dispatched_at` datetime(6) DEFAULT NULL,
  `provider_name` varchar(120) DEFAULT NULL,
  `provider_response` varchar(2000) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `request_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKoy1ts9sfhk6y8x4vpu62jr25m` (`request_id`),
  CONSTRAINT `FKoy1ts9sfhk6y8x4vpu62jr25m` FOREIGN KEY (`request_id`) REFERENCES `otp_verification_request` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=346 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `verification_dispatch_queue`
--


--
-- Table structure for table `verification_template`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `verification_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `channel_type` enum('EMAIL','SMS') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `purpose` enum('LOGIN_OTP','PASSWORD_RESET','PROVIDER_TEST','STEP_UP_ACTION','VERIFY_EMAIL','VERIFY_MOBILE') NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `subject_line` varchar(200) DEFAULT NULL,
  `template_body` varchar(2000) NOT NULL,
  `template_code` varchar(40) NOT NULL,
  `template_name` varchar(150) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK27uqkkqwfsebjfxopt4ies1rv` (`template_code`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `verification_template`
--

INSERT INTO `verification_template` VALUES (1,'EMAIL','2026-04-01 09:10:00.000000','VERIFY_EMAIL','ACTIVE','Verify your email address','Dear customer, your email verification OTP is {{otp}} and expires in 5 minutes.','VT-EMAIL-01','Customer Email Verification','2026-04-01 09:10:00.000000'),(2,'SMS','2026-04-01 09:12:00.000000','VERIFY_MOBILE','ACTIVE',NULL,'Your mobile verification OTP is {{otp}}. It will expire in 5 minutes.','VT-SMS-01','Customer Mobile Verification','2026-04-01 09:12:00.000000'),(3,'EMAIL','2026-04-01 09:14:00.000000','PASSWORD_RESET','ACTIVE','Reset your password','Use OTP {{otp}} to reset your password. This request expires in 5 minutes.','VT-EMAIL-02','Password Reset Email','2026-04-01 09:14:00.000000'),(4,'SMS','2026-04-01 09:16:00.000000','PASSWORD_RESET','ACTIVE',NULL,'Password reset OTP {{otp}} is valid for 5 minutes.','VT-SMS-02','Password Reset SMS','2026-04-01 09:16:00.000000'),(5,'EMAIL','2026-04-01 09:18:00.000000','PROVIDER_TEST','ACTIVE','Provider test email','Local provider test OTP {{otp}} generated for validation.','VT-EMAIL-03','Provider Test Email','2026-04-01 09:18:00.000000'),(6,'SMS','2026-04-01 09:20:00.000000','PROVIDER_TEST','ACTIVE',NULL,'Local provider test OTP {{otp}} generated for SMS validation.','VT-SMS-03','Provider Test SMS','2026-04-01 09:20:00.000000'),(7,'EMAIL','2026-04-01 09:22:00.000000','VERIFY_MOBILE','ACTIVE','Staff alert','Cross-check contact verification OTP {{otp}} for operational review only.','VT-EMAIL-04','Operational Support Email','2026-04-01 09:22:00.000000'),(8,'SMS','2026-04-01 09:24:00.000000','VERIFY_EMAIL','ACTIVE',NULL,'Operational mirror OTP {{otp}} generated for audit simulation.','VT-SMS-04','Operational Support SMS','2026-04-01 09:24:00.000000'),(9,'EMAIL','2026-05-08 18:36:43.000000','LOGIN_OTP','ACTIVE','Your secure login OTP','Assalamu Alaikum, your SBMS login OTP is {{otp}}. This code will expire in {{expiresMinutes}} minutes. If you did not try to sign in, please contact support immediately.','VT-EMAIL-LOGIN-01','Login OTP Email','2026-05-08 18:36:43.000000'),(10,'SMS','2026-05-08 18:36:43.000000','LOGIN_OTP','ACTIVE',NULL,'SBMS login OTP: {{otp}}. Valid for {{expiresMinutes}} minutes. Do not share this code with anyone.','VT-SMS-LOGIN-01','Login OTP SMS','2026-05-08 18:36:43.000000');

--
-- Table structure for table `workflow_comment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workflow_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment_at` datetime(6) NOT NULL,
  `comment_by` varchar(120) NOT NULL,
  `comment_text` varchar(1000) NOT NULL,
  `module_name` varchar(80) NOT NULL,
  `reference_id` bigint NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workflow_comment`
--

INSERT INTO `workflow_comment` VALUES (1,'2026-05-04 08:12:00.000000','SYSTEM','Customer profile documents were received and checked at branch desk. | SYSTEM','CUSTOMER',1,'ACTIVE'),(2,'2026-05-04 08:23:00.000000','SYSTEM','Risk screening completed before forwarding to review desk. | SYSTEM','KYC',1,'ACTIVE'),(3,'2026-05-04 08:42:00.000000','SYSTEM','Linked customer and KYC profile matched successfully. | SYSTEM','ACCOUNT',1,'ACTIVE'),(4,'2026-05-04 09:03:00.000000','SYSTEM','Committee approval remarks attached with recommendation sheet. | SYSTEM','FINANCING',1,'ACTIVE'),(5,'2026-05-04 09:18:00.000000','SYSTEM','Checklist item mismatch noted for revised contract wording. | SYSTEM','SHARIAH',1,'ACTIVE'),(6,'2026-05-04 09:33:00.000000','SYSTEM','Branch clarification required for missing CCTV reference. | SYSTEM','SECURITY_INVESTIGATION',1,'ACTIVE'),(7,'2026-05-04 09:53:00.000000','SYSTEM','Draft ownership assigned to legal support queue. | SYSTEM','CONTRACT',1,'ACTIVE'),(8,'2026-05-04 10:08:00.000000','SYSTEM','File export completed and queued for regulatory dispatch. | SYSTEM','REPORTS',1,'ACTIVE'),(9,'2026-05-04 10:22:00.000000','SYSTEM','Retry failed due to upstream SMTP timeout. | SYSTEM','NOTIFICATION',1,'ACTIVE'),(10,'2026-05-04 10:48:00.000000','SYSTEM','Reconciliation variance cleared by operations officer. | SYSTEM','ATM',1,'ACTIVE'),(11,'2026-05-04 11:02:00.000000','SYSTEM','Payout request requires final beneficiary approval. | SYSTEM','ZAKAT',1,'ACTIVE'),(12,'2026-05-04 11:23:00.000000','SYSTEM','Disbursement supporting file archived and locked. | SYSTEM','FINANCING',2,'ACTIVE'),(13,'2026-05-04 11:38:00.000000','SYSTEM','Nominee photo ID mismatch caused rejection. | SYSTEM','DEPOSIT_SCHEME',1,'ACTIVE'),(14,'2026-05-04 11:53:00.000000','SYSTEM','Approval completed after second-level address verification. | SYSTEM','KYC',2,'ACTIVE'),(15,'2026-05-04 12:08:00.000000','SYSTEM','Posting queue assignment completed for maker-checker stage. | SYSTEM','ACCOUNT',2,'ACTIVE');

--
-- Table structure for table `workflow_history`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workflow_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action_at` datetime(6) NOT NULL,
  `action_by` varchar(120) NOT NULL,
  `action_name` varchar(80) NOT NULL,
  `from_status` varchar(40) DEFAULT NULL,
  `module_name` varchar(80) NOT NULL,
  `reference_id` bigint NOT NULL,
  `remarks` varchar(1000) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED','PENDING') NOT NULL,
  `to_status` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workflow_history`
--

INSERT INTO `workflow_history` VALUES (1,'2026-05-04 08:10:00.000000','SYSTEM','SUBMIT_PROFILE','DRAFT','CUSTOMER',1,'Customer onboarding submitted from branch desk | SYSTEM','ACTIVE','SUBMITTED'),(2,'2026-05-04 08:20:00.000000','SYSTEM','ROUTE_FOR_REVIEW','SUBMITTED','KYC',1,'KYC profile routed to compliance review | SYSTEM','ACTIVE','PENDING_REVIEW'),(3,'2026-05-04 08:40:00.000000','OPS_OFFICER','VERIFY_ACCOUNT','PENDING_REVIEW','ACCOUNT',1,'Account opening request validated against KYC and branch profile | SYSTEM','ACTIVE','VERIFIED'),(4,'2026-05-04 09:00:00.000000','INVESTMENT_OFFICER','APPROVE_APPLICATION','UNDER_REVIEW','FINANCING',1,'Financing application approved after review committee sign-off | SYSTEM','ACTIVE','APPROVED'),(5,'2026-05-04 09:15:00.000000','SHARIAH_BOARD_MEMBER','START_REVIEW','SUBMITTED','SHARIAH',1,'Shariah case picked for detailed review and checklist validation | SYSTEM','ACTIVE','UNDER_REVIEW'),(6,'2026-05-04 09:30:00.000000','SYSTEM','RETURN_CASE','ASSIGNED','SECURITY_INVESTIGATION',1,'Investigation returned for branch clarification and supporting evidence | SYSTEM','ACTIVE','RETURNED'),(7,'2026-05-04 09:50:00.000000','SYSTEM','ASSIGN_DRAFT','DRAFT','CONTRACT',1,'Contract drafting task assigned to legal workflow pool | SYSTEM','ACTIVE','ASSIGNED'),(8,'2026-05-04 10:05:00.000000','OPS_OFFICER','EXPORT_REPORT','APPROVED','REPORTS',1,'Regulatory export finished and file reference logged | SYSTEM','ACTIVE','COMPLETED'),(9,'2026-05-04 10:20:00.000000','SYSTEM','RETRY_DELIVERY','FAILED','NOTIFICATION',1,'Notification delivery retried but provider failure persisted | SYSTEM','ACTIVE','FAILED'),(10,'2026-05-04 10:45:00.000000','OPS_OFFICER','POST_RECONCILIATION','VERIFIED','ATM',1,'ATM reconciliation posted to terminal journal and cash ledger | SYSTEM','ACTIVE','POSTED'),(11,'2026-05-04 11:00:00.000000','SYSTEM','SUBMIT_PAYOUT','DRAFT','ZAKAT',1,'Charity payout request submitted for operational approval | SYSTEM','ACTIVE','PENDING'),(12,'2026-05-04 11:20:00.000000','BRANCH_MANAGER','CLOSE_DISBURSEMENT','APPROVED','FINANCING',2,'Disbursement file closed after release confirmation | SYSTEM','ACTIVE','CLOSED'),(13,'2026-05-04 11:35:00.000000','OPS_OFFICER','REJECT_ENROLLMENT','UNDER_REVIEW','DEPOSIT_SCHEME',1,'Enrollment rejected because nominee verification remained incomplete | SYSTEM','ACTIVE','REJECTED'),(14,'2026-05-04 11:50:00.000000','SYSTEM','APPROVE_PROFILE','UNDER_REVIEW','KYC',2,'KYC profile approved after identity and address confirmation | SYSTEM','ACTIVE','APPROVED'),(15,'2026-05-04 12:05:00.000000','SYSTEM','ASSIGN_FOR_POSTING','VERIFIED','ACCOUNT',2,'Account assigned to maker-checker posting queue | SYSTEM','ACTIVE','ASSIGNED');

--
-- Table structure for table `zakat_profile`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `zakat_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `calculation_status` enum('BELOW_NISAB','CALCULATED','DEDUCTED','PROFILED') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `eligible_asset_amount` decimal(18,2) NOT NULL,
  `nisab_amount` decimal(18,2) NOT NULL,
  `remarks` varchar(1000) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `zakat_amount` decimal(18,2) NOT NULL,
  `zakat_year` int NOT NULL,
  `customer_id` bigint NOT NULL,
  `proof_document_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_zakat_profile_customer_year` (`customer_id`,`zakat_year`),
  CONSTRAINT `FK9do7aokj4ao2x3y2fwq94jbk0` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `zakat_profile`
--

INSERT INTO `zakat_profile` VALUES (1,'DEDUCTED','2026-04-01 09:00:00.000000',540000.00,85000.00,'Primary zakat profile settled through yearly deduction workflow.','2026-04-10 11:30:00.000000',13500.00,2025,1,NULL),(2,'DEDUCTED','2026-04-01 09:15:00.000000',450000.00,85000.00,'Business reserve adjusted and zakat routed to charity fund.','2026-04-10 11:45:00.000000',11250.00,2025,2,NULL),(3,'BELOW_NISAB','2026-04-01 09:30:00.000000',72000.00,85000.00,'Retail savings remained below nisab threshold for the year.','2026-04-10 12:00:00.000000',0.00,2025,3,NULL),(4,'BELOW_NISAB','2026-04-01 09:45:00.000000',81000.00,90000.00,'Personal assets reviewed and kept for reminder tracking only.','2026-04-10 12:15:00.000000',0.00,2026,4,NULL),(5,'DEDUCTED','2026-04-01 10:00:00.000000',650000.00,85000.00,'Corporate zakat amount deducted after asset review.','2026-04-10 12:30:00.000000',16250.00,2025,5,NULL),(6,'CALCULATED','2026-04-01 10:15:00.000000',390000.00,90000.00,'Calculated and waiting finance confirmation before deduction.','2026-04-10 12:45:00.000000',9750.00,2026,6,NULL),(7,'DEDUCTED','2026-04-01 10:30:00.000000',390000.00,85000.00,'Salary and deposit mix cleared for deduction posting.','2026-04-10 13:00:00.000000',9750.00,2025,7,NULL),(8,'PROFILED','2026-04-01 10:45:00.000000',580000.00,90000.00,'Asset profile opened and waiting calculation run by officer.','2026-04-10 13:15:00.000000',0.00,2026,8,NULL),(9,'DEDUCTED','2026-04-01 11:00:00.000000',575000.00,90000.00,'Customer requested immediate deduction after yearly review.','2026-04-10 13:30:00.000000',14375.00,2025,9,NULL),(10,'CALCULATED','2026-04-01 11:15:00.000000',440000.00,90000.00,'Calculated amount waiting customer approval for deduction.','2026-04-10 13:45:00.000000',11000.00,2026,10,NULL),(11,'DEDUCTED','2026-04-01 11:30:00.000000',500000.00,90000.00,'Trade inventory exposure converted into posted zakat amount.','2026-04-10 14:00:00.000000',12500.00,2025,11,NULL),(12,'PROFILED','2026-04-01 11:45:00.000000',315000.00,90000.00,'New profile captured and waiting verified eligible asset review.','2026-04-10 14:15:00.000000',0.00,2026,12,NULL),(13,'DEDUCTED','2026-04-01 12:00:00.000000',425000.00,90000.00,'Family zakat pool cleared and routed to charity fund.','2026-04-10 14:30:00.000000',10625.00,2025,13,NULL),(14,'DEDUCTED','2026-04-01 12:15:00.000000',620000.00,90000.00,'High balance assets reviewed and deducted for current cycle.','2026-04-10 14:45:00.000000',15500.00,2026,14,NULL),(15,'DEDUCTED','2026-04-01 12:30:00.000000',350000.00,90000.00,'Community savings portfolio settled through yearly zakat process.','2026-04-10 15:00:00.000000',8750.00,2025,15,NULL);
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
