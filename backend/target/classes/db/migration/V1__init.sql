-- Flyway V1: Create sys_user table
CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL,
  real_name VARCHAR(50) NOT NULL,
  employee_no VARCHAR(20),
  phone VARCHAR(20),
  email VARCHAR(100),
  department_id BIGINT,
  role_id BIGINT,
  avatar VARCHAR(255),
  status TINYINT DEFAULT 1,
  create_time DATETIME,
  update_time DATETIME,
  last_login_time DATETIME,
  login_count INT DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uq_sys_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
