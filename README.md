# CoreON
인트라넷 구축 프로넥트 CoreON 26.1.21 ~

Member테이블

CREATE TABLE `member` (
	`employee_no` INT NOT NULL AUTO_INCREMENT,
	
	`id` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_general_ci',
	
	`pw` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_general_ci',
	
	`username` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_general_ci',
	
	`email` VARCHAR(120) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	
	`mobile` VARCHAR(20) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	
	`dept` VARCHAR(100) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	
	`position` VARCHAR(100) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	
	`role` ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER' COLLATE 'utf8mb4_general_ci',
	
	PRIMARY KEY (`employee_no`) USING BTREE,
	
	UNIQUE INDEX `uk_member_id` (`id`) USING BTREE,
	
	UNIQUE INDEX `uk_member_email` (`email`) USING BTREE
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=10003
;
