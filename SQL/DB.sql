-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: easycoach
-- ------------------------------------------------------
-- Server version	8.0.40

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
-- Table structure for table `pagamento`
--

DROP TABLE IF EXISTS `pagamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pagamento` (
  `idPagamento` int NOT NULL AUTO_INCREMENT,
  `idPrenotazione` int NOT NULL,
  `metodoPagamento` enum('CARTA','PAYPAL','ALTRO') DEFAULT 'CARTA',
  `totalePagato` decimal(10,2) NOT NULL,
  `statusPagamento` enum('IN_ATTESA','COMPLETATO','RIFIUTATO') DEFAULT 'IN_ATTESA',
  `dataPagamento` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idPagamento`),
  KEY `idx_pagamento_prenotazione` (`idPrenotazione`),
  CONSTRAINT `pagamento_ibfk_1` FOREIGN KEY (`idPrenotazione`) REFERENCES `prenotazione` (`idPrenotazione`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pagamento`
--

LOCK TABLES `pagamento` WRITE;
/*!40000 ALTER TABLE `pagamento` DISABLE KEYS */;
/*!40000 ALTER TABLE `pagamento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prenotazione`
--

DROP TABLE IF EXISTS `prenotazione`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prenotazione` (
  `idPrenotazione` int NOT NULL AUTO_INCREMENT,
  `idUtente` int NOT NULL,
  `idTimeslot` int NOT NULL,
  `linkVideoconferenza` varchar(225) DEFAULT NULL,
  `statusPrenotazione` enum('IN_ATTESA','ATTIVA','CONCLUSA','ANNULLATA') DEFAULT 'IN_ATTESA',
  `dataPrenotazione` date NOT NULL,
  `idSessione` int NOT NULL,
  `timestampCreazione` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idPrenotazione`),
  KEY `idx_prenotazione_utente` (`idUtente`),
  KEY `idx_prenotazione_timeslot` (`idTimeslot`),
  KEY `idx_prenotazione_timeslot_data` (`idTimeslot`,`dataPrenotazione`),
  KEY `fk_prenotazione_sessione` (`idSessione`),
  CONSTRAINT `fk_prenotazione_sessione` FOREIGN KEY (`idSessione`) REFERENCES `sessione` (`idSessione`),
  CONSTRAINT `prenotazione_ibfk_1` FOREIGN KEY (`idUtente`) REFERENCES `utente` (`idUtente`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `prenotazione_ibfk_2` FOREIGN KEY (`idTimeslot`) REFERENCES `timeslot` (`idTimeslot`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prenotazione`
--

LOCK TABLES `prenotazione` WRITE;
/*!40000 ALTER TABLE `prenotazione` DISABLE KEYS */;
/*!40000 ALTER TABLE `prenotazione` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sessione`
--

DROP TABLE IF EXISTS `sessione`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sessione` (
  `idSessione` int NOT NULL AUTO_INCREMENT,
  `idUtente` int NOT NULL,
  `titolo` varchar(225) NOT NULL,
  `descrizione` varchar(1000) NOT NULL,
  `prezzo` double NOT NULL,
  `immagine` varchar(225) NOT NULL,
  `statusSessione` enum('ATTIVA','NON_DISPONIBILE','ARCHIVIATA') DEFAULT 'ATTIVA',
  PRIMARY KEY (`idSessione`),
  KEY `idx_sessione_utente` (`idUtente`),
  CONSTRAINT `sessione_ibfk_1` FOREIGN KEY (`idUtente`) REFERENCES `utente` (`idUtente`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=114 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sessione`
--

LOCK TABLES `sessione` WRITE;
/*!40000 ALTER TABLE `sessione` DISABLE KEYS */;
/*!40000 ALTER TABLE `sessione` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `timeslot`
--

DROP TABLE IF EXISTS `timeslot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `timeslot` (
  `idTimeslot` int NOT NULL AUTO_INCREMENT,
  `idSessione` int NOT NULL,
  `giorno` int NOT NULL,
  `orario` int NOT NULL,
  PRIMARY KEY (`idTimeslot`),
  KEY `idx_timeslot_sessione` (`idSessione`),
  CONSTRAINT `timeslot_ibfk_1` FOREIGN KEY (`idSessione`) REFERENCES `sessione` (`idSessione`),
  CONSTRAINT `check_giorno` CHECK (((`giorno` >= 0) and (`giorno` <= 6))),
  CONSTRAINT `check_orario` CHECK (((`orario` >= 0) and (`orario` <= 23)))
) ENGINE=InnoDB AUTO_INCREMENT=765 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `timeslot`
--

LOCK TABLES `timeslot` WRITE;
/*!40000 ALTER TABLE `timeslot` DISABLE KEYS */;
/*!40000 ALTER TABLE `timeslot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `utente`
--

DROP TABLE IF EXISTS `utente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `utente` (
  `idUtente` int NOT NULL AUTO_INCREMENT,
  `email` varchar(225) NOT NULL,
  `nome` varchar(225) NOT NULL,
  `cognome` varchar(225) NOT NULL,
  `password` varchar(225) NOT NULL,
  `ruolo` enum('ADMIN','MENTOR','MENTEE') NOT NULL,
  `isDeleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idUtente`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_utente_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `utente`
--

LOCK TABLES `utente` WRITE;
/*!40000 ALTER TABLE `utente` DISABLE KEYS */;
INSERT INTO `utente` VALUES (12,'admin@gmail.com','Admin','Admin','3e576b6cd8b378110e5ebd942630494bf468b94a8c3bac5fcc2c1f29c4ec40e4','ADMIN',0);
/*!40000 ALTER TABLE `utente` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-26 18:14:28
