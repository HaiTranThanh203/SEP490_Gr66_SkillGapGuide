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
-- Bảng lưu các khóa học
CREATE TABLE Course (
                        course_id INT NOT NULL AUTO_INCREMENT,
                        title NVARCHAR(255) NOT NULL,
                        rating NVARCHAR(255),
                        difficulty NVARCHAR(2000),
                        description NVARCHAR(6000) NOT NULL,
                        provider NVARCHAR(255) NOT NULL,
                        url NVARCHAR(500),
                        status NVARCHAR(50) ,
                        create_at DateTime,
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
                    id INT NOT NULL AUTO_INCREMENT,
                    user_id int,
                    file_name varchar(255) NOT NULL,
                    file_path varchar(255) NOT NULL,
                    file_type varchar(255) NOT NULL,
                    upload_date datetime NOT NULL,
                    PRIMARY KEY (id),
                    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

-- Bảng Công việc (Job), liên kết đến JobCategory
CREATE TABLE Job (
                     job_id INT NOT NULL AUTO_INCREMENT,
                     cv_id int,
                     title NVARCHAR(255) NOT NULL,
                     description NVARCHAR(7000) NOT NULL,
                     company NVARCHAR(255) NOT NULL,
                     status NVARCHAR(255) NOT NULL,
                     source_url nvarchar(512) NULL,
                     PRIMARY KEY (job_id),
                     FOREIGN KEY (cv_id) REFERENCES CV(id)
);
CREATE TABLE User_Favorite_Job (
                                   id INT NOT NULL auto_increment,
                                   user_id INT NOT NULL,
                                   job_id INT NOT NULL,
                                   created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                   PRIMARY KEY (id),
                                   FOREIGN KEY (user_id) REFERENCES User(user_id),
                                   FOREIGN KEY (job_id) REFERENCES Job(job_id)
);
CREATE TABLE job_des_file (
                              id INT NOT NULL AUTO_INCREMENT,
                              user_id int,
                              job_id int,
                              file_name varchar(255) NOT NULL,
                              file_path varchar(255) NOT NULL,
                              file_type varchar(255) NOT NULL,
                              upload_date datetime NOT NULL,
                              PRIMARY KEY (id),
                              FOREIGN KEY (user_id) REFERENCES User(user_id),
                              FOREIGN KEY (job_id) REFERENCES job(job_id) on delete cascade
);
-- Bảng User_Course (bảng nối), liên kết User và Course
CREATE TABLE User_Course (
                             id INT NOT NULL auto_increment,
                             user_id INT NOT NULL,
                             course_id INT NOT NULL,
                             status NVARCHAR(50) ,
                             PRIMARY KEY (id),
                             FOREIGN KEY (user_id) REFERENCES User(user_id),
                             FOREIGN KEY (course_id) REFERENCES Course(course_id)
);
CREATE TABLE User_Favorite_Course (
                                      id INT NOT NULL auto_increment,
                                      user_id INT NOT NULL,
                                      course_id INT NOT NULL,
                                      status NVARCHAR(50) ,
                                      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                      PRIMARY KEY (id),
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
-- Bảng Cài đặt (Setting)
CREATE TABLE Setting (
                         setting_id INT NOT NULL AUTO_INCREMENT,
                         name NVARCHAR(255) NOT NULL UNIQUE,
                         value NVARCHAR(255),
                         PRIMARY KEY (setting_id)
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

-- job category
CREATE TABLE occupation_groups(  -- domain or aria
                                  id INT AUTO_INCREMENT PRIMARY KEY,
                                  name VARCHAR(100) NOT NULL,
                                  status nvarchar(100)
);

CREATE TABLE occupation (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            occupation_groups_id INT NOT NULL,
                            status nvarchar(100),
                            FOREIGN KEY (occupation_groups_id) REFERENCES occupation_groups(id)
);

CREATE TABLE specializations (
                                 id INT AUTO_INCREMENT PRIMARY KEY,
                                 name VARCHAR(100) NOT NULL,
                                 occupation_id INT NOT NULL,
                                 status nvarchar(100),
                                 url_topcv varchar(200),
                                 FOREIGN KEY (occupation_id) REFERENCES occupation(id)
);
CREATE TABLE job_specializations (
                                     job_id INT NOT NULL,
                                     specialization_id INT NOT NULL,
                                     PRIMARY KEY (job_id, specialization_id),
                                     FOREIGN KEY (job_id) REFERENCES job(job_id),
                                     FOREIGN KEY (specialization_id) REFERENCES specializations(id)
);
create table user_cv_skills(
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               skill nvarchar(100),
                               cv_id int ,
                               FOREIGN KEY (cv_id) REFERENCES CV(id)
);
create table user_cv_skills_embedding(
                                         id INT AUTO_INCREMENT PRIMARY KEY,
                                         skill nvarchar(100),
                                         embedding_json JSON
);
create table job_des_skills(
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               skill nvarchar(100),
                               job_id int ,
                               FOREIGN KEY (job_id) REFERENCES job(job_id) on delete cascade
);
create table job_des_skills_embedding(
                                         id INT AUTO_INCREMENT PRIMARY KEY,
                                         skill nvarchar(100),
                                         embedding_json JSON
);
create table job_cv_skills_score(
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    job_skill int ,
                                    cv_skill int ,
                                    score double,
                                    FOREIGN KEY (job_skill) REFERENCES job_des_skills(id) on delete cascade,
                                    FOREIGN KEY (cv_skill) REFERENCES user_cv_skills(id)
);
INSERT INTO `skill_gap_guide`.`subscription`(`subscription_id`, `type`, `status`)VALUES    (1,     1,     'active');
INSERT INTO `skill_gap_guide`.`subscription`(`subscription_id`, `type`, `status`)VALUES    (2,     3,     'active');
INSERT INTO `skill_gap_guide`.`subscription`(`subscription_id`, `type`, `status`)VALUES    (3,     9,     'active');
INSERT INTO `skill_gap_guide`.`subscription`(`subscription_id`, `type`, `status`)VALUES    (4,     12,     'active');
INSERT INTO `skill_gap_guide`.`role`(`role_id`, `name`)VALUES    (1,     'System Admin');
INSERT INTO `skill_gap_guide`.`role`(`role_id`, `name`)VALUES    (2,     'Business Admin');
INSERT INTO `skill_gap_guide`.`role`(`role_id`, `name`)VALUES    (3,     'Free User');
INSERT INTO `skill_gap_guide`.`role`(`role_id`, `name`)VALUES		(4,'Premium User');
INSERT INTO Course (title, rating, difficulty, description, provider, url, status, create_at)
VALUES
    ('Introduction to Python', 4.5, 'Beginner', 'Learn the basics of Python programming.', 'Coursera', 'https://www.coursera.org/python', 'Active', '2025-06-25 10:00:00'),
    ('Data Structures and Algorithms', 4.8, 'Intermediate', 'Deep dive into data structures and algorithms.', 'Udemy', 'https://www.udemy.com/dsa', 'Active', '2025-06-20 15:30:00'),
    ('Machine Learning Fundamentals', 4.7, 'Advanced', 'Understand the concepts of machine learning.', 'edX', 'https://www.edx.org/ml', 'Active', '2025-06-15 09:00:00'),
    ('Web Development Bootcamp', 4.6, 'Beginner', 'Comprehensive guide to web development.', 'Pluralsight', 'https://www.pluralsight.com/webdev', 'Active', '2025-06-10 14:20:00'),
    ('Cloud Computing Basics', 4.4, 'Beginner', 'Introduction to cloud computing technologies.', 'AWS', 'https://www.aws.training/cloud', 'Inactive', '2025-06-05 11:10:00'),
    ('Deep Learning Specialization', 4.9, 'Advanced', 'Master deep learning with hands-on projects.', 'Coursera', 'https://www.coursera.org/dl', 'Active', '2025-06-01 08:50:00'),
    ('Cybersecurity Essentials', 4.3, 'Intermediate', 'Learn the key concepts of cybersecurity.', 'Microsoft', 'https://learn.microsoft.com/cybersecurity', 'Inactive', '2025-05-30 12:00:00'),
    ('Digital Marketing 101', 4.2, 'Beginner', 'Basics of digital marketing strategies.', 'LinkedIn Learning', 'https://www.linkedin.com/digitalmarketing', 'Active', '2025-05-25 13:30:00'),
    ('Artificial Intelligence Overview', 4.6, 'Intermediate', 'Overview of artificial intelligence concepts.', 'Google', 'https://ai.google/ai-overview', 'Active', '2025-05-20 16:40:00'),
    ('Blockchain for Developers', 4.5, 'Advanced', 'Comprehensive guide to blockchain development.', 'IBM', 'https://developer.ibm.com/blockchain', 'Active', '2025-05-15 18:10:00');
INSERT INTO User (email, password, full_name, role_id, subscription_id, phone, avatar, provider, status_id)
VALUES
    ('admin@example.com', '$2a$10$ZgjCwtbfKU8YWtJeVjcc8.VVCQIe8XAnCbulK3Su41AFATlQn.cE6', 'Admin User', 1, 2, '0123456789', NULL, 'LOCAL', 2),
    ('user1@example.com', '$2a$10$ZgjCwtbfKU8YWtJeVjcc8.VVCQIe8XAnCbulK3Su41AFATlQn.cE6', 'Nguyen Van A', 4, 1, '0987654321', NULL, 'LOCAL', 2),
    ('user2@example.com', '$2a$10$ZgjCwtbfKU8YWtJeVjcc8.VVCQIe8XAnCbulK3Su41AFATlQn.cE6', 'Tran Thi B', 3, 1, '0911222333', NULL, 'LOCAL', 1);
INSERT INTO Payment (user_id, amount, date, payment_method, transaction_code, qr_code_url, status)
VALUES
    (1, 499000, '2024-06-01 10:00:00', 'QR', 'TXN001', 'https://qr.example.com/1', 'SUCCESS'),
    (2, 0, '2024-06-10 15:30:00', 'FREE', NULL, NULL, 'SUCCESS');
INSERT INTO FeedBack (user_id, content, star, create_at)
VALUES
    (2, 'Hệ thống rất hữu ích!', 5, '2024-06-11 12:00:00'),
    (3, 'Cần cải thiện giao diện.', 3, '2024-06-12 09:15:00');

INSERT INTO Job (title, description, company, status, source_url)
VALUES
    ('Data Analyst', 'Phân tích dữ liệu cho công ty A', 'Company A',  'OPEN', 'https://jobs.com/a'),
    ('Marketing Specialist', 'Chuyên viên marketing cho công ty B', 'Company B', 'OPEN', 'https://jobs.com/b');
INSERT INTO User_Favorite_Job (user_id, job_id, created_at)
VALUES
    (2, 1, NOW()),
    (3, 2, NOW());

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
INSERT INTO Setting (name, value)
VALUES
    ('site_name', 'Skill Gap Guide'),
    ('support_email', 'support@skillgap.com');

INSERT INTO user_subscription_history (user_id, subscription_id, start_date, end_date, status) VALUES (1, 2, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'EXPIRED');
INSERT INTO user_subscription_history (user_id, subscription_id, start_date, end_date, status) VALUES (2, 1, '2024-06-01 00:00:00', '2025-05-31 23:59:59', 'ACTIVE');
INSERT INTO `skill_gap_guide`.`staticpage`
(`name`,`title`,`content`,`update_at`,`update_by`)
VALUES
    ('Home','Năm Bắt Đầu','2019','2024-06-17',1),
    ('Home','Tên trang','SkillGapGuide','2024-06-17',1),
    ('Home','Số điện thoại liên hệ','559282 - 978','2024-06-17',1),
    ('AboutUs','Về chúng tôi','SkillGapGuide là một dự án nghiên cứu nhằm giải quyết khoảng cách ngày càng tăng giữa những gì người tìm việc cung cấp và những gì nhà tuyển dụng mong đợi. Ra đời từ phản hồi thu thập được trong các chương trình định hướng nghề nghiệp và thực tập, dự án này xem xét lý do tại sao nhiều sinh viên mới tốt nghiệp và những người muốn thay đổi nghề nghiệp gặp khó khăn trong việc đáp ứng các yêu cầu công việc. Bằng cách phân tích các tin đăng tuyển dụng, CV và nhu cầu của ngành, chúng tôi mong muốn cung cấp những hiểu biết rõ ràng về sự không phù hợp của kỹ năng và giúp nâng cao sự sẵn sàng cho nghề nghiệp.','2024-06-17',1),
    ('AboutUs','Sứ mệnh','Sứ mệnh của chúng tôi là làm nổi bật những khoảng trống kỹ năng ngăn cản người tìm việc đạt được mục tiêu của họ. Thông qua nghiên cứu dựa trên dữ liệu, chúng tôi mong muốn hỗ trợ sinh viên, nhà giáo dục và cố vấn nghề nghiệp trong việc tìm hiểu nhu cầu thị trường lao động và định hình các hệ thống hướng dẫn tốt hơn. Chúng tôi tin rằng những con đường rõ ràng hơn sẽ dẫn đến những lựa chọn nghề nghiệp mạnh mẽ hơn, tự tin hơn.','2024-06-17',1),
    ('SocialLink','Facebook','https://www.facebook.com/yourpage','2024-06-17',1),
    ('SocialLink','Instagram','https://www.facebook.com/yourpage','2024-06-17',1),
    ('SocialLink','Gmail','https://www.facebook.com/yourpage','2024-06-17',1),
    ('Privacy','Thông tin thu thập','Khi bạn sử dụng trang web của chúng tôi, chúng tôi có thể thu thập các loại thông tin sau:
Họ tên, địa chỉ email
CV hoặc danh sách kỹ năng bạn cung cấp
Hành vi sử dụng trang web (ví dụ: khóa học bạn quan tâm, thời gian truy cập)','2024-06-17',1),
    ('Privacy','Mục đích sử dụng thông tin','Thông tin của bạn được sử dụng để:
Phân tích kỹ năng hiện có và xác định kỹ năng còn thiếu
Đề xuất các khóa học phù hợp để nâng cao kỹ năng
Cải thiện chất lượng dịch vụ và trải nghiệm người dùng
Gửi thông báo liên quan đến khóa học, cập nhật hoặc ưu đãi (nếu bạn đồng ý)','2024-06-17',1),
    ('Terms','Chấp nhận điều khoản','Bằng cách truy cập và sử dụng website của chúng tôi, bạn đồng ý tuân thủ các điều khoản dưới đây.','2024-06-17',1)
;