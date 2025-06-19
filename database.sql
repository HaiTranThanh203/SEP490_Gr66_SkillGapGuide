-- drop database skill_gap_guide
Create database skill_gap_guide;
use skill_gap_guide;
-- Bảng lưu các vai trò (ví dụ: admin, user)
CREATE TABLE Role (
    role_id INT NOT NULL AUTO_INCREMENT,
    name NVARCHAR(255) NOT NULL,
    PRIMARY KEY (role_id)
);
CREATE TABLE user_status (
    status_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên trạng thái, ví dụ: VERIFIED, NOT_VERIFIED, BANNED'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO user_status (name) VALUES
('NOT_VERIFIED'),
('VERIFIED'),
('BANNED');
-- Bảng lưu các gói đăng ký
CREATE TABLE Subscription (
    subscription_id INT NOT NULL AUTO_INCREMENT,
    type int NOT NULL,
    status NVARCHAR(255) NOT NULL,
    PRIMARY KEY (subscription_id)
);

-- Bảng lưu danh mục công việc
CREATE TABLE JobCategory (
    job_category_id INT NOT NULL AUTO_INCREMENT,
    name NVARCHAR(255) NOT NULL,
    PRIMARY KEY (job_category_id)
);

-- Bảng lưu các kỹ năng chung
CREATE TABLE Skill (
    skill_id INT NOT NULL AUTO_INCREMENT,
    name NVARCHAR(255) NOT NULL,
    PRIMARY KEY (skill_id)
);

-- Bảng lưu các khóa học
CREATE TABLE Course (
    course_id INT NOT NULL AUTO_INCREMENT,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(255) NOT NULL,
    provider NVARCHAR(255) NOT NULL,
    url NVARCHAR(255) NOT NULL,
    PRIMARY KEY (course_id)
);

-- Bảng Người dùng (User), liên kết đến Role và Subscription
CREATE TABLE User (
    user_id INT NOT NULL AUTO_INCREMENT,
    email NVARCHAR(255) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    subscription_id INT NOT NULL,
    phone NVARCHAR(255) NOT NULL,

    avatar NVARCHAR(255) DEFAULT NULL,

     provider VARCHAR(10) DEFAULT 'LOCAL',
    status_id INT NOT NULL,
	PRIMARY KEY (user_id),
	FOREIGN KEY (status_id) REFERENCES user_status(status_id),
    FOREIGN KEY (role_id) REFERENCES Role(role_id),
    FOREIGN KEY (subscription_id) REFERENCES Subscription(subscription_id)
);

-- Bảng Thanh toán (Payment), liên kết đến User
CREATE TABLE Payment (
    payment_id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    amount DOUBLE NOT NULL,
    date datetime NOT NULL,
	payment_method VARCHAR(100),
    transaction_code VARCHAR(100),
    qr_code_url VARCHAR(255),
    status NVARCHAR(255) NOT NULL,
    PRIMARY KEY (payment_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

-- Bảng Phản hồi (Feedback), liên kết đến User
CREATE TABLE FeedBack (
    feedback_id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    content NVARCHAR(255) NOT NULL,
    star INT NOT NULL,
        create_at DateTime,
    PRIMARY KEY (feedback_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

-- Bảng CV, liên kết đến User
CREATE TABLE CV (
    cv_id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    skill NVARCHAR(255) NOT NULL,
    exp INT NOT NULL,
    position NVARCHAR(255) NOT NULL,
    PRIMARY KEY (cv_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

-- Bảng Công việc (Job), liên kết đến JobCategory
CREATE TABLE Job (
    job_id INT NOT NULL AUTO_INCREMENT,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(7000) NOT NULL,
    company NVARCHAR(255) NOT NULL,
    category_id INT NOT NULL,
    status NVARCHAR(255) NOT NULL,
    source_url nvarchar(512) NOT NULL,
    PRIMARY KEY (job_id),
    FOREIGN KEY (category_id) REFERENCES JobCategory(job_category_id)
);
CREATE TABLE User_Favorite_Job (
    user_id INT NOT NULL,
    job_id INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, job_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (job_id) REFERENCES Job(job_id)
);

-- Bảng User_Skill (bảng nối), liên kết User và Skill
CREATE TABLE User_Skill (
    user_id INT NOT NULL,
    skill_id INT NOT NULL,
    level NVARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, skill_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (skill_id) REFERENCES Skill(skill_id)
);
CREATE TABLE User_Favorite_Missing_Skill (
    user_id INT NOT NULL,
    skill_id INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, skill_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (skill_id) REFERENCES Skill(skill_id)
);

-- Bảng User_Course (bảng nối), liên kết User và Course
CREATE TABLE User_Course (
    user_id INT NOT NULL,
    course_id INT NOT NULL,
    PRIMARY KEY (user_id, course_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (course_id) REFERENCES Course(course_id)
);
CREATE TABLE User_Favorite_Course (
    user_id INT NOT NULL,
    course_id INT NOT NULL,
    status NVARCHAR(50) ,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, course_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (course_id) REFERENCES Course(course_id)
);

-- Bảng Nội dung (Content)
CREATE TABLE StaticPage (
    id INT NOT NULL AUTO_INCREMENT,
    `name` NVARCHAR(255),
    `title` NVARCHAR(255),
    `content` NVARCHAR(1000),
    update_at datetime,
    update_by int,
    PRIMARY KEY (id)
);
CREATE TABLE SocialLink (
    id INT NOT NULL AUTO_INCREMENT,
    platform NVARCHAR(255),
    url NVARCHAR(255),
    icon NVARCHAR(255),
    update_at datetime,
    update_by int,
    PRIMARY KEY (id)
);
-- Bảng Cài đặt (Setting)
CREATE TABLE Setting (
    setting_id INT NOT NULL AUTO_INCREMENT,
    name NVARCHAR(255) NOT NULL UNIQUE,
    value NVARCHAR(255),
    PRIMARY KEY (setting_id)
);

-- Bảng Thẻ (Tag)
CREATE TABLE Tag (
    tag_id INT NOT NULL AUTO_INCREMENT,
    name NVARCHAR(255) NOT NULL,
    description NVARCHAR(255) NOT NULL,
    PRIMARY KEY (tag_id)
);
CREATE TABLE verification_token (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    expiry_date DATETIME NOT NULL,
    
    CONSTRAINT fk_verification_token_user
        FOREIGN KEY (user_id) 
        REFERENCES user(user_id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE Audit_Log (
    log_id INT NOT NULL AUTO_INCREMENT,
    user_id INT,
    action NVARCHAR(255) NOT NULL,
    entity_type NVARCHAR(100),
    entity_id INT,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (log_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);
CREATE TABLE user_subscription_history (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    subscription_id INT NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (subscription_id) REFERENCES Subscription(subscription_id)
);

INSERT INTO `skill_gap_guide`.`subscription`(`subscription_id`, `type`, `status`)VALUES    (1,     1,     'active');
INSERT INTO `skill_gap_guide`.`subscription`(`subscription_id`, `type`, `status`)VALUES    (2,     3,     'active');
INSERT INTO `skill_gap_guide`.`subscription`(`subscription_id`, `type`, `status`)VALUES    (3,     9,     'active');
INSERT INTO `skill_gap_guide`.`subscription`(`subscription_id`, `type`, `status`)VALUES    (4,     12,     'active');
INSERT INTO `skill_gap_guide`.`role`(`role_id`, `name`)VALUES    (1,     'System Admin');
INSERT INTO `skill_gap_guide`.`role`(`role_id`, `name`)VALUES    (2,     'Business Admin');
INSERT INTO `skill_gap_guide`.`role`(`role_id`, `name`)VALUES    (3,     'Free User');
INSERT INTO `skill_gap_guide`.`role`(`role_id`, `name`)VALUES		(4,'Premium User');
INSERT INTO JobCategory (name) VALUES('IT'),('Marketing'),('Finance');
INSERT INTO Skill (name) VALUES('Python'),('Communication'),('Data Analysis'),('Project Management');
INSERT INTO Course (title, description, provider, url) VALUES
('Python for Beginners', 'Khóa học Python cơ bản', 'Coursera', 'https://coursera.org/python'),
('Digital Marketing', 'Marketing số cho người mới', 'Udemy', 'https://udemy.com/digital-marketing'),
('Excel Advanced', 'Kỹ năng Excel nâng cao', 'edX', 'https://edx.org/excel');
INSERT INTO User (email, password, full_name, role_id, subscription_id, phone, avatar, provider, status_id)
VALUES
('admin@example.com', '123', 'Admin User', 1, 2, '0123456789', NULL, 'LOCAL', 2),
('user1@example.com', '123', 'Nguyen Van A', 4, 1, '0987654321', NULL, 'LOCAL', 2),
('user2@example.com', '123', 'Tran Thi B', 3, 1, '0911222333', NULL, 'LOCAL', 1);
INSERT INTO Payment (user_id, amount, date, payment_method, transaction_code, qr_code_url, status)
VALUES
(1, 499000, '2024-06-01 10:00:00', 'QR', 'TXN001', 'https://qr.example.com/1', 'SUCCESS'),
(2, 0, '2024-06-10 15:30:00', 'FREE', NULL, NULL, 'SUCCESS');
INSERT INTO FeedBack (user_id, content, star, createAt)
VALUES
(2, 'Hệ thống rất hữu ích!', 5, '2024-06-11 12:00:00'),
(3, 'Cần cải thiện giao diện.', 3, '2024-06-12 09:15:00');
INSERT INTO CV (user_id, skill, exp, position)
VALUES
(2, 'Python', 2, 'Junior Developer'),
(3, 'Marketing', 3, 'Marketing Executive');
INSERT INTO Job (title, description, company, category_id, status, source_url)
VALUES
('Data Analyst', 'Phân tích dữ liệu cho công ty A', 'Company A', 1, 'OPEN', 'https://jobs.com/a'),
('Marketing Specialist', 'Chuyên viên marketing cho công ty B', 'Company B', 2, 'OPEN', 'https://jobs.com/b');
INSERT INTO User_Favorite_Job (user_id, job_id, created_at)
VALUES
(2, 1, NOW()),
(3, 2, NOW());
INSERT INTO User_Skill (user_id, skill_id, level)
VALUES
(2, 1, 'Intermediate'),
(2, 2, 'Basic'),
(3, 2, 'Advanced');
INSERT INTO User_Favorite_Missing_Skill (user_id, skill_id, created_at)
VALUES
(2, 3, NOW()),
(3, 4, NOW());
INSERT INTO User_Course (user_id, course_id)
VALUES
(2, 1),
(3, 2);
INSERT INTO User_Favorite_Course (user_id, course_id, status, created_at)
VALUES
(2, 1, 'WANT_TO_LEARN', NOW()),
(3, 2, 'COMPLETED', NOW());
INSERT INTO StaticPage (name, title, content, update_at, update_by)
VALUES
('homepage', 'Trang chủ', 'Nội dung trang chủ...', NOW(), 1),
('about-us', 'Về chúng tôi', 'Nội dung về chúng tôi...', NOW(), 1);
INSERT INTO SocialLink (platform, url, icon, update_at, update_by)
VALUES
('Facebook', 'https://facebook.com/yourpage', 'facebook-icon.png', NOW(), 1),
('LinkedIn', 'https://linkedin.com/company/yourcompany', 'linkedin-icon.png', NOW(), 1);
INSERT INTO Setting (name, value)
VALUES
('site_name', 'Skill Gap Guide'),
('support_email', 'support@skillgap.com');
INSERT INTO Tag (name, description)
VALUES
('Remote', 'Công việc từ xa'),
('Full-time', 'Toàn thời gian');
INSERT INTO user_subscription_history (user_id, subscription_id, start_date, end_date, status) VALUES (1, 2, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'EXPIRED');
INSERT INTO user_subscription_history (user_id, subscription_id, start_date, end_date, status) VALUES (2, 1, '2024-06-01 00:00:00', '2025-05-31 23:59:59', 'ACTIVE');