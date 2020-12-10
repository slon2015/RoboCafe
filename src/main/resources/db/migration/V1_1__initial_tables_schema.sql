CREATE TABLE `authentication` (
  `id` int(11) NOT NULL,
  `login` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `so_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `permission` (
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `security_role` (
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `security_object` (
  `id` varchar(255) NOT NULL,
  `domain_id` varchar(255) DEFAULT NULL,
  `invalidated` bit(1) NOT NULL,
  `role_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKk0a2bb6vkx1mmo8hkl69sqm5m` (`role_id`),
  CONSTRAINT `FKk0a2bb6vkx1mmo8hkl69sqm5m` FOREIGN KEY (`role_id`) REFERENCES `security_role` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `additional_permissions` (
  `object_id` varchar(255) NOT NULL,
  `additional_permissions_name` varchar(255) NOT NULL,
  PRIMARY KEY (`object_id`,`additional_permissions_name`),
  KEY `FKyc9bex8kncfxg2bighnpkdyq` (`additional_permissions_name`),
  CONSTRAINT `FKcqvr7bedsu9lk3h0v4pvlnv2h` FOREIGN KEY (`object_id`) REFERENCES `security_object` (`id`),
  CONSTRAINT `FKyc9bex8kncfxg2bighnpkdyq` FOREIGN KEY (`additional_permissions_name`) REFERENCES `permission` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `afiche` (
  `id` varchar(255) NOT NULL,
  `html_content` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `cafe_sessions` (
  `id` varchar(255) NOT NULL,
  `finished` bit(1) NOT NULL,
  `party_id` varchar(255) DEFAULT NULL,
  `table_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `person_ref_data` (
  `person_id` varchar(255) NOT NULL,
  PRIMARY KEY (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `person_ref_data_completed_order_ids` (
  `person_ref_data_person_id` varchar(255) NOT NULL,
  `completed_order_ids` varchar(255) DEFAULT NULL,
  KEY `FKe33ou276k540ioq39i8k1dgq8` (`person_ref_data_person_id`),
  CONSTRAINT `FKe33ou276k540ioq39i8k1dgq8` FOREIGN KEY (`person_ref_data_person_id`) REFERENCES `person_ref_data` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `person_ref_data_order_ids` (
  `person_ref_data_person_id` varchar(255) NOT NULL,
  `order_ids` varchar(255) DEFAULT NULL,
  KEY `FKc79tqjey48c6c2uibm9nnrhx6` (`person_ref_data_person_id`),
  CONSTRAINT `FKc79tqjey48c6c2uibm9nnrhx6` FOREIGN KEY (`person_ref_data_person_id`) REFERENCES `person_ref_data` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `cafe_sessions_persons` (
  `session_id` varchar(255) NOT NULL,
  `persons_person_id` varchar(255) NOT NULL,
  PRIMARY KEY (`session_id`,`persons_person_id`),
  UNIQUE KEY `UK_l30qtrn1ypa864yns3u9xbw5n` (`persons_person_id`),
  CONSTRAINT `FKcmt3p0mysyeybwcyjtfqq2ux0` FOREIGN KEY (`session_id`) REFERENCES `cafe_sessions` (`id`),
  CONSTRAINT `FKm5gpbrf0c856isio10ejengdj` FOREIGN KEY (`persons_person_id`) REFERENCES `person_ref_data` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `cafe_table` (
  `id` varchar(255) NOT NULL,
  `max_persons` int(11) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `table_number` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `chat` (
  `id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `chat_member` (
  `id` varchar(255) NOT NULL,
  `party_id` varchar(255) DEFAULT NULL,
  `person_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `chat_chat_members` (
  `chat_id` varchar(255) NOT NULL,
  `party_id` varchar(255) NOT NULL,
  `person_id` varchar(255) NOT NULL,
  PRIMARY KEY (`chat_id`,`party_id`,`person_id`),
  CONSTRAINT `FKilui4yhwoepdpt7i4ldqrqvj8` FOREIGN KEY (`chat_id`) REFERENCES `chat` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `menu_order` (
  `id` varchar(255) NOT NULL,
  `close_cause` varchar(255) DEFAULT NULL,
  `closed` bit(1) NOT NULL,
  `party_id` varchar(255) DEFAULT NULL,
  `person_id` varchar(255) DEFAULT NULL,
  `price` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `menu_position` (
  `id` varchar(255) NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `menu_position_categories` (
  `position_id` varchar(255) NOT NULL,
  `categories` varchar(255) DEFAULT NULL,
  KEY `FKja91ywhqwddvlnv5vy52lmtmj` (`position_id`),
  CONSTRAINT `FKja91ywhqwddvlnv5vy52lmtmj` FOREIGN KEY (`position_id`) REFERENCES `menu_position` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `message` (
  `id` varchar(255) NOT NULL,
  `text` varchar(255) DEFAULT NULL,
  `party_id` varchar(255) NOT NULL,
  `person_id` varchar(255) NOT NULL,
  `chat_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmejd0ykokrbuekwwgd5a5xt8a` (`chat_id`),
  CONSTRAINT `FKmejd0ykokrbuekwwgd5a5xt8a` FOREIGN KEY (`chat_id`) REFERENCES `chat` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `order_position` (
  `id` varchar(255) NOT NULL,
  `menu_position_id` varchar(255) DEFAULT NULL,
  `order_status` varchar(255) DEFAULT NULL,
  `order_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqtbagcmj5qwbogms9uvasge1f` (`order_id`),
  CONSTRAINT `FKqtbagcmj5qwbogms9uvasge1f` FOREIGN KEY (`order_id`) REFERENCES `menu_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `party` (
  `id` varchar(255) NOT NULL,
  `end_time` datetime(6) DEFAULT NULL,
  `max_members` int(11) NOT NULL,
  `table_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `payment` (
  `id` varchar(255) NOT NULL,
  `amount` double NOT NULL,
  `party_id` varchar(255) DEFAULT NULL,
  `person_id` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `person` (
  `id` varchar(255) NOT NULL,
  `balance` double NOT NULL,
  `party_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK31w8k0vvrlkpwaiveeebfwmg8` (`party_id`),
  CONSTRAINT `FK31w8k0vvrlkpwaiveeebfwmg8` FOREIGN KEY (`party_id`) REFERENCES `party` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `role_default_permissions` (
  `role_name` varchar(255) NOT NULL,
  `permission_name` varchar(255) NOT NULL,
  PRIMARY KEY (`role_name`,`permission_name`),
  KEY `FKp1lstuyob4f3j4eqifkv4dnge` (`permission_name`),
  CONSTRAINT `FKiv1545fdf8bjaf51hx6d17c1c` FOREIGN KEY (`role_name`) REFERENCES `security_role` (`name`),
  CONSTRAINT `FKp1lstuyob4f3j4eqifkv4dnge` FOREIGN KEY (`permission_name`) REFERENCES `permission` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `session_chat_ids` (
  `session_id` varchar(255) NOT NULL,
  `chat_ids` varchar(255) DEFAULT NULL,
  KEY `FKavch1p5unlbiaxi8cbusngvdx` (`session_id`),
  CONSTRAINT `FKavch1p5unlbiaxi8cbusngvdx` FOREIGN KEY (`session_id`) REFERENCES `cafe_sessions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE SEQUENCE `hibernate_sequence`;
