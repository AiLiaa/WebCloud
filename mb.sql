/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.7.38 : Database - javaee_project
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`javaee_project` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `javaee_project`;

/*Table structure for table `tb_emailregist` */

DROP TABLE IF EXISTS `tb_emailregist`;

CREATE TABLE `tb_emailregist` (
  `email` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tb_file` */

DROP TABLE IF EXISTS `tb_file`;

CREATE TABLE `tb_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) NOT NULL,
  `file_size` double(255,0) NOT NULL,
  `file_type` varchar(255) NOT NULL,
  `file_suffix` varchar(255) NOT NULL,
  `physical_path` varchar(255) NOT NULL,
  `virtual_path` varchar(255) NOT NULL,
  `user_id` bigint(11) NOT NULL,
  `upload_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `download_times` bigint(20) NOT NULL DEFAULT '0',
  `file_img` varchar(255) DEFAULT NULL,
  `isfolder` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `file_foreign1` (`user_id`),
  CONSTRAINT `file_foreign1` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1640698939370029059 DEFAULT CHARSET=utf8;

/*Table structure for table `tb_recyclebin` */

DROP TABLE IF EXISTS `tb_recyclebin`;

CREATE TABLE `tb_recyclebin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) NOT NULL,
  `file_size` double(255,0) NOT NULL,
  `file_type` varchar(255) NOT NULL,
  `file_suffix` varchar(255) NOT NULL,
  `physical_path` varchar(255) NOT NULL,
  `virtual_path` varchar(255) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `upload_time` varchar(255) NOT NULL DEFAULT '',
  `download_times` bigint(20) NOT NULL,
  `file_img` varchar(255) NOT NULL DEFAULT '',
  `isfolder` tinyint(4) NOT NULL,
  `delete_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `bin_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `tb_recyclebin_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1609069407701577730 DEFAULT CHARSET=utf8;

/*Table structure for table `tb_user` */

DROP TABLE IF EXISTS `tb_user`;

CREATE TABLE `tb_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=1608733304287199234 DEFAULT CHARSET=utf8;

/*Table structure for table `tb_usersize` */

DROP TABLE IF EXISTS `tb_usersize`;

CREATE TABLE `tb_usersize` (
  `user_id` bigint(20) NOT NULL,
  `user_level` tinyint(255) NOT NULL,
  `user_maxsize` double NOT NULL,
  `user_usedsize` double NOT NULL,
  KEY `user_id` (`user_id`),
  CONSTRAINT `tb_usersize_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
