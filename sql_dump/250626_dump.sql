-- MySQL dump 10.13  Distrib 9.2.0, for macos14.7 (arm64)
--
-- Host: localhost    Database: diaconnmall
-- ------------------------------------------------------
-- Server version	9.2.0

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
-- Table structure for table `c_cart`
--

DROP TABLE IF EXISTS `c_cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `c_cart` (
  `id` int NOT NULL,
  `count` int NOT NULL,
  `added_at` datetime NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_cart_user` (`user_id`),
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `c_user` (`id`),
  CONSTRAINT `product_id` FOREIGN KEY (`id`) REFERENCES `c_product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `c_cart`
--

LOCK TABLES `c_cart` WRITE;
/*!40000 ALTER TABLE `c_cart` DISABLE KEYS */;
/*!40000 ALTER TABLE `c_cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `c_category`
--

DROP TABLE IF EXISTS `c_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `c_category` (
  `id` int NOT NULL,
  `nm` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `c_category`
--

LOCK TABLES `c_category` WRITE;
/*!40000 ALTER TABLE `c_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `c_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `c_order`
--

DROP TABLE IF EXISTS `c_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `c_order` (
  `id` int NOT NULL,
  `total_price` int NOT NULL,
  `regdate` datetime NOT NULL,
  `address` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `address_detail` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `phone` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `cart_id` FOREIGN KEY (`id`) REFERENCES `c_cart` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `c_order`
--

LOCK TABLES `c_order` WRITE;
/*!40000 ALTER TABLE `c_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `c_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `c_order_detail`
--

DROP TABLE IF EXISTS `c_order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `c_order_detail` (
  `id` int NOT NULL,
  `quantity` int NOT NULL,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `c_order_detail`
--

LOCK TABLES `c_order_detail` WRITE;
/*!40000 ALTER TABLE `c_order_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `c_order_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `c_pay`
--

DROP TABLE IF EXISTS `c_pay`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `c_pay` (
  `id` int NOT NULL,
  `date` datetime NOT NULL,
  `status` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `amount` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `c_pay`
--

LOCK TABLES `c_pay` WRITE;
/*!40000 ALTER TABLE `c_pay` DISABLE KEYS */;
/*!40000 ALTER TABLE `c_pay` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `c_product`
--

DROP TABLE IF EXISTS `c_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `c_product` (
  `id` int NOT NULL,
  `nm` varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
  `desc` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `count` int NOT NULL,
  `price` int NOT NULL,
  `img_url` varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
  `state` varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
  `alt_text` varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
  `is_banner` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `c_product`
--

LOCK TABLES `c_product` WRITE;
/*!40000 ALTER TABLE `c_product` DISABLE KEYS */;
INSERT INTO `c_product` VALUES (1,'배너','배너설명',1,12000,'https://mall-clone.s3.ap-northeast-2.amazonaws.com/banner/banner.png','판매중','배너이미지',_binary ''),(2,'바나나','바나나 설명',4,13000,'https://mall-clone.s3.ap-northeast-2.amazonaws.com/banana.png','판매중','이미지',_binary '\0'),(3,'핏','핏 설명',3,12500,'https://mall-clone.s3.ap-northeast-2.amazonaws.com/fit.png','판매중','이미지',_binary '\0'),(4,'글루어트','글루어트 설명',5,19900,'https://mall-clone.s3.ap-northeast-2.amazonaws.com/gluet.png','판매중','이미지',_binary '\0'),(5,'핫초코','핫초코 설명',9,9900,'https://mall-clone.s3.ap-northeast-2.amazonaws.com/hotchoco.png','판매중','이미지',_binary '\0'),(6,'베지','베지 설명',7,4300,'https://mall-clone.s3.ap-northeast-2.amazonaws.com/vege.png','판매중','이미지',_binary '\0'),(7,'요거트','요거트 설명',8,5800,'https://mall-clone.s3.ap-northeast-2.amazonaws.com/yogurt.jpg','판매중','이미지',_binary '\0');
/*!40000 ALTER TABLE `c_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `c_user`
--

DROP TABLE IF EXISTS `c_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `c_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb3_bin NOT NULL,
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `address_detail` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `signout` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `join_date` datetime NOT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `outdate_at` datetime DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `c_user`
--

LOCK TABLES `c_user` WRITE;
/*!40000 ALTER TABLE `c_user` DISABLE KEYS */;
INSERT INTO `c_user` VALUES (1,'sein','서울','금천구','sein@test.1','01094046005','N','2025-05-28 00:00:00','2025-05-26 00:00:00',NULL,'1234'),(2,'ㅎㅎ','동작구',NULL,'test@test.com','11111','N','2025-06-04 12:59:16','2025-06-04 12:59:16',NULL,'111');
/*!40000 ALTER TABLE `c_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-26  9:28:26
