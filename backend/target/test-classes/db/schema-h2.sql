-- Minimal schema for tests (H2)
CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  real_name VARCHAR(50),
  employee_no VARCHAR(20),
  phone VARCHAR(20),
  email VARCHAR(100),
  department_id BIGINT,
  role_id BIGINT,
  avatar VARCHAR(500),
  status INT DEFAULT 1,
  create_time TIMESTAMP,
  update_time TIMESTAMP,
  last_login_time TIMESTAMP,
  login_count INT DEFAULT 0
);
