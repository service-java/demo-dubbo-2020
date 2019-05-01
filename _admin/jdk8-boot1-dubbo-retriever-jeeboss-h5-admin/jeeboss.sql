/*
SQLyog Ultimate v11.26 (32 bit)
MySQL - 5.6.34-log : Database - jeeboss
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`jeeboss` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `jeeboss`;

/*Table structure for table `monitor_histogram` */

DROP TABLE IF EXISTS `monitor_histogram`;

CREATE TABLE `monitor_histogram` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stat_day` date DEFAULT NULL,
  `moment` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `application` varchar(50) DEFAULT NULL,
  `name` varchar(200) DEFAULT NULL,
  `ip` varchar(50) DEFAULT NULL,
  `min` bigint(20) DEFAULT NULL,
  `max` bigint(20) DEFAULT NULL,
  `mean` double DEFAULT NULL,
  `std_dev` double DEFAULT NULL,
  `percentile_999` double DEFAULT NULL,
  `percentile_99` double DEFAULT NULL,
  `percentile_98` double DEFAULT NULL,
  `percentile_95` double DEFAULT NULL,
  `percentile_75` double DEFAULT NULL,
  `median` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10445 DEFAULT CHARSET=utf8;

/*Data for the table `monitor_histogram` */

/*Table structure for table `monitor_meter` */

DROP TABLE IF EXISTS `monitor_meter`;

CREATE TABLE `monitor_meter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stat_day` date DEFAULT NULL,
  `moment` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `application` varchar(50) DEFAULT NULL,
  `name` varchar(200) DEFAULT NULL,
  `ip` varchar(50) DEFAULT NULL,
  `mean` double DEFAULT NULL,
  `count` bigint(20) DEFAULT NULL,
  `one_minute_rate` double DEFAULT NULL,
  `five_minute_rate` double DEFAULT NULL,
  `fifteen_minute_rate` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10445 DEFAULT CHARSET=utf8;

/*Data for the table `monitor_meter` */

/*Table structure for table `sys_org` */

DROP TABLE IF EXISTS `sys_org`;

CREATE TABLE `sys_org` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) NOT NULL DEFAULT '0',
  `name` varchar(50) NOT NULL DEFAULT '""',
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*Data for the table `sys_org` */

insert  into `sys_org`(`id`,`parent_id`,`name`,`create_date`,`sort`) values (1,0,'顶级机构','2018-01-16 15:34:45',0),(2,1,'顶二级机构','2018-01-16 15:47:27',2),(5,0,'广州分公司','2018-01-17 15:36:22',2),(6,5,'广州子公司','2018-01-17 15:36:39',1);

/*Table structure for table `sys_resource` */

DROP TABLE IF EXISTS `sys_resource`;

CREATE TABLE `sys_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) NOT NULL DEFAULT '0',
  `name` varchar(50) NOT NULL DEFAULT '""',
  `type` int(1) NOT NULL DEFAULT '0' COMMENT '类型,0:菜单 1:功能',
  `url` varchar(200) NOT NULL DEFAULT '""',
  `permission` varchar(200) NOT NULL DEFAULT '""' COMMENT '权限表示',
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sort` int(11) NOT NULL DEFAULT '0',
  `icon` varchar(50) NOT NULL DEFAULT '""',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

/*Data for the table `sys_resource` */

insert  into `sys_resource`(`id`,`parent_id`,`name`,`type`,`url`,`permission`,`create_date`,`sort`,`icon`) values (1,0,'资源管理',0,'\"\"','\"\"','2018-01-08 11:03:54',2,'fa fa-list'),(2,1,'资源列表',0,'resource_list','\"\"','2018-01-08 11:04:03',2,'fa fa-database'),(8,0,'角色管理',0,'','role','2018-01-15 17:39:54',2,'fa fa-cogs'),(9,8,'角色列表',0,'role_list','role:list','2018-01-15 17:40:23',1,'fa fa-sitemap'),(10,0,'机构管理',0,'','org','2018-01-15 17:42:05',3,'fa fa-group'),(11,10,'机构列表',0,'org_list','org:list','2018-01-15 17:43:31',1,'fa fa-object-group'),(12,0,'任务管理',0,'task','task','2018-01-17 16:07:52',4,'fa fa-hourglass'),(13,12,'任务列表',0,'job_list','job:list','2018-01-17 16:08:17',1,'fa fa-hourglass-2'),(14,0,'用户管理',0,'user','user','2018-01-18 11:22:10',5,'fa fa-user'),(15,14,'用户列表',0,'user_list','user:list','2018-01-18 11:22:37',1,'fa fa-users'),(16,1,'用户授权',1,'','resource:user:authorize','2018-01-24 17:15:21',3,'\"\"'),(17,0,'会话管理',0,'session','session','2018-01-24 18:26:15',6,'fa fa-user-plus'),(18,17,'会话列表',0,'session_list','session:list','2018-01-24 18:26:41',1,'fa fa-user-secret'),(19,0,'服务监控',0,'monitor','monitor','2018-01-26 15:51:54',7,'fa fa-line-chart'),(20,19,'服务调用监控',0,'monitor_meter','monitor:meter','2018-01-26 15:52:30',0,'fa fa-area-chart'),(21,19,'服务调用耗时',0,'monitor_histogram','monitor:histogram','2018-01-30 11:29:07',1,'fa fa-bar-chart'),(22,0,'字典管理',0,'','sys:dict','2018-03-05 11:53:06',3,'fa fa-list-ul'),(23,22,'字典列表',0,'dict_list','sys:dict:list','2018-03-05 11:54:16',1,'fa fa-list-alt');

/*Table structure for table `sys_role` */

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '""',
  `org_id` int(11) NOT NULL DEFAULT '0',
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `permission` varchar(50) DEFAULT '""' COMMENT '权限表示',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Data for the table `sys_role` */

insert  into `sys_role`(`id`,`name`,`org_id`,`create_date`,`permission`) values (1,'超级管理员',1,'2018-01-10 15:19:26','admin'),(2,'角色2',1,'2018-01-10 15:19:31','\"\"');

/*Table structure for table `sys_role_resource` */

DROP TABLE IF EXISTS `sys_role_resource`;

CREATE TABLE `sys_role_resource` (
  `role_id` int(11) NOT NULL,
  `resource_id` int(11) NOT NULL,
  PRIMARY KEY (`role_id`,`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_role_resource` */

insert  into `sys_role_resource`(`role_id`,`resource_id`) values (1,1),(1,2),(1,8),(1,9),(1,10),(1,11),(1,14),(1,15),(1,16),(1,17),(1,18),(1,19),(1,20),(1,21),(1,22),(1,23),(2,1),(2,2),(2,8),(2,9),(2,10),(2,11),(2,12),(2,13),(2,14),(2,15),(2,16),(2,17),(2,18);

/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '""',
  `nickname` varchar(50) DEFAULT '""',
  `age` int(11) NOT NULL DEFAULT '0',
  `sex` int(1) NOT NULL DEFAULT '0' COMMENT '性别,0:女 1:男',
  `email` varchar(100) NOT NULL DEFAULT '""',
  `password` varchar(64) NOT NULL DEFAULT '""' COMMENT '密码',
  `phone` varchar(11) NOT NULL DEFAULT '""' COMMENT '电话',
  `photo` varchar(200) NOT NULL DEFAULT '""' COMMENT '头像',
  `org_id` int(11) NOT NULL DEFAULT '0' COMMENT '组织机构',
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*Data for the table `sys_user` */

insert  into `sys_user`(`id`,`name`,`nickname`,`age`,`sex`,`email`,`password`,`phone`,`photo`,`org_id`,`create_date`) values (6,'admin','admin',23,1,'hanjiehu06@163.com','25d55ad283aa400af464c76d713c07ad','13650973336','http://ypstatic.zksite.com/52/3f/69e39d99a5004984acf5ca0b9fee4a99.jpg',1,'2018-01-25 12:09:14');

/*Table structure for table `sys_user_role` */

DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_user_role` */

insert  into `sys_user_role`(`user_id`,`role_id`) values (6,1),(6,2);

DROP TABLE IF EXISTS `sys_dict`;

CREATE TABLE `sys_dict` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value` varchar(100) NOT NULL DEFAULT '""',
  `label` varchar(100) NOT NULL DEFAULT '""' COMMENT '标签',
  `type` varchar(100) NOT NULL DEFAULT '""' COMMENT '类型',
  `sort` int(11) NOT NULL COMMENT '序号',
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Data for the table `sys_dict` */

insert  into `sys_dict`(`id`,`value`,`label`,`type`,`sort`,`create_date`,`update_date`) values (2,'0','女','gender',0,'2018-03-05 14:41:43','2018-03-05 14:41:43'),(3,'1','男','gender',1,'2018-03-05 15:04:39','2018-03-05 15:04:39'),(4,'11','生产','dev',1,'2018-03-05 15:05:10','2018-03-05 15:05:10');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
