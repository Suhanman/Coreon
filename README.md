# CoreON
인트라넷 구축 프로넥트 CoreON 26.1.21 ~

Member테이블
```
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
```

Notice 테이블
```
CREATE TABLE `notice` (
	`notice_id` BIGINT NOT NULL AUTO_INCREMENT,
	`category` VARCHAR(30) NOT NULL COLLATE 'utf8mb4_general_ci',
	`title` VARCHAR(200) NOT NULL COLLATE 'utf8mb4_general_ci',
	`content` LONGTEXT NOT NULL COLLATE 'utf8mb4_general_ci',
	`publisher_dept` VARCHAR(100) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	`created_by` VARCHAR(100) NOT NULL COLLATE 'utf8mb4_general_ci',
	`created_at` DATETIME NOT NULL DEFAULT (CURRENT_TIMESTAMP),
	`updated_at` DATETIME NOT NULL DEFAULT (CURRENT_TIMESTAMP) ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`notice_id`) USING BTREE,
	INDEX `idx_notice_latest` (`created_at`) USING BTREE,
	INDEX `idx_notice_category_latest` (`category`, `created_at`) USING BTREE,
	INDEX `idx_notice_created_by` (`created_by`, `created_at`) USING BTREE,
	CONSTRAINT `fk_notice_created_by` FOREIGN KEY (`created_by`) REFERENCES `member` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=2
;

```

Notice_attachment 테이블
```
CREATE TABLE `notice_attachment` (
	`attachment_id` BIGINT NOT NULL AUTO_INCREMENT,
	`notice_id` BIGINT NOT NULL,
	`originalname` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_general_ci',
	`storedurl` VARCHAR(1024) NOT NULL COLLATE 'utf8mb4_general_ci',
	`content_type` VARCHAR(100) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	`sizebytes` BIGINT NULL DEFAULT NULL,
	`created_at` DATETIME NOT NULL DEFAULT (CURRENT_TIMESTAMP),
	PRIMARY KEY (`attachment_id`) USING BTREE,
	INDEX `idx_notice_attachment` (`notice_id`) USING BTREE,
	CONSTRAINT `fk_notice_attachment_notice` FOREIGN KEY (`notice_id`) REFERENCES `notice` (`notice_id`) ON UPDATE NO ACTION ON DELETE CASCADE
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;
```

board_attachment 테이블
```
CREATE TABLE IF NOT EXISTS board_attachment (
  attachment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  board_id BIGINT NOT NULL,

  original_name VARCHAR(255) NOT NULL,
  stored_url VARCHAR(500) NOT NULL,

  content_type VARCHAR(100) NULL,
  size_bytes BIGINT NULL,

  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  KEY idx_board_id (board_id),
  CONSTRAINT fk_attachment_board
    FOREIGN KEY (board_id) REFERENCES board_post(board_id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

```
board_pos 테이블
```

CREATE TABLE IF NOT EXISTS board_post (
  board_id BIGINT AUTO_INCREMENT PRIMARY KEY,

  category VARCHAR(50) NOT NULL,
  title VARCHAR(200) NOT NULL,
  content TEXT NULL,

  dept VARCHAR(100) NULL,

  author_employee_no BIGINT NOT NULL,
  author_name VARCHAR(100) NOT NULL,

  view_count BIGINT NOT NULL DEFAULT 0,

  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

