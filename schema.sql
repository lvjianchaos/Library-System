-- 建库
CREATE DATABASE IF NOT EXISTS library_db DEFAULT CHARACTER SET utf8mb4;

USE library_db;

-- 1. 用户表 user
DROP TABLE IF EXISTS user;
CREATE TABLE user (
                      id           BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
                      username     VARCHAR(50)  NOT NULL UNIQUE COMMENT '登录名',
                      password     VARCHAR(255) NOT NULL COMMENT '密码(加密后)',
                      role         ENUM('admin','user') NOT NULL COMMENT '角色',
                      name         VARCHAR(50)  NOT NULL COMMENT '姓名',
                      phone        VARCHAR(20)           COMMENT '手机号',
                      created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
                      updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 图书表 book
DROP TABLE IF EXISTS book;
CREATE TABLE book (
                      id            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '图书ID',
                      title         VARCHAR(200) NOT NULL COMMENT '书名',
                      author        VARCHAR(100)          COMMENT '作者',
                      description   TEXT                  COMMENT '简介',
                      isbn          VARCHAR(30)           COMMENT 'ISBN编号',
                      publish_year  INT                   COMMENT '出版年份',
                      cover_url     VARCHAR(255)          COMMENT '封面图片URL',
                      total         INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '图书总数量',
                      stock         INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '库存数量',
                      created_at    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                      updated_at    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- 3. 分类标签表 tag
DROP TABLE IF EXISTS tag;
CREATE TABLE tag (
                     id          BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
                     name        VARCHAR(50)  NOT NULL COMMENT '标签名称，如科幻、教育',
                     created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类标签表';

-- 4. 图书--分类标签映射表 book_tag
DROP TABLE IF EXISTS book_tag;
CREATE TABLE book_tag (
                          id          BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
                          book_id     BIGINT UNSIGNED NOT NULL COMMENT '图书ID',
                          tag_id      BIGINT UNSIGNED NOT NULL COMMENT '标签ID',
                          created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE ON UPDATE CASCADE,
                          FOREIGN KEY (tag_id) REFERENCES tag(id)  ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书-分类标签映射表';

-- 5. 评论表 comment
DROP TABLE IF EXISTS comment;
CREATE TABLE comment (
                         id          BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
                         user_id     BIGINT UNSIGNED NOT NULL COMMENT '评论用户ID',
                         book_id     BIGINT UNSIGNED NOT NULL COMMENT '评论图书ID',
                         content     TEXT            NOT NULL COMMENT '评论内容',
                         created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
                         FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE,
                         FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 6. 收藏图书表 favorite
DROP TABLE IF EXISTS favorite;
CREATE TABLE favorite (
                          id          BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
                          user_id     BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
                          book_id     BIGINT UNSIGNED NOT NULL COMMENT '图书ID',
                          created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
                          FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE,
                          FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏图书表';

-- 7. 预约表 reservation
DROP TABLE IF EXISTS reservation;
CREATE TABLE reservation (
                             id           BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
                             user_id      BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
                             book_id      BIGINT UNSIGNED NOT NULL COMMENT '图书ID',
                             status       TINYINT         NOT NULL DEFAULT 0 COMMENT '状态：0=排队中，1=已取消，2=已完成',
                             created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预约时间',
                             FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE,
                             FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- 8. 借阅表 borrow
DROP TABLE IF EXISTS borrow;
CREATE TABLE borrow (
                        id           BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '借阅ID',
                        user_id      BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
                        book_id      BIGINT UNSIGNED NOT NULL COMMENT '图书ID',
                        borrow_time  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借书时间',
                        due_time     DATETIME        NOT NULL COMMENT '应还时间',
                        return_time  DATETIME                 COMMENT '实际归还时间（未归还则为NULL）',
                        status       TINYINT         NOT NULL DEFAULT 0 COMMENT '状态：0=借出中，1=已归还，2=逾期',
                        created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
                        updated_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE,
                        FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅表';

-- 9.创建索引
CREATE UNIQUE INDEX idx_book_isbn ON book(isbn);

-- 10. 视图
-- 【视图】创建一个“借阅单详情视图”，自动把书名拼好
CREATE VIEW v_borrow_full_info AS
SELECT 
    br.id AS borrow_id,
    br.user_id,
    b.title AS book_title,
    b.isbn AS book_isbn,
    br.borrow_time,
    br.return_time,
    br.status
FROM borrow br
LEFT JOIN book b ON br.book_id = b.id;



-- 11.数据库备份
DELIMITER $$
-- 创建事件：每 1 天执行一次
CREATE EVENT IF NOT EXISTS event_backup_book_table
ON SCHEDULE EVERY 1 DAY
STARTS (TIMESTAMP(CURRENT_DATE) + INTERVAL 2 HOUR) -- 从今天凌晨2点开始计算
DO
BEGIN
    -- 定义变量
    DECLARE file_path VARCHAR(255);
    DECLARE sql_cmd VARCHAR(1000);
    
    -- a. 构造动态文件名 
    SET file_path = CONCAT('D:/backup/book_backup_', DATE_FORMAT(NOW(), '%Y%m%d_%H%i%s'), '.csv');
    
    -- b. 构造导出数据的 SQL 语句

    SET sql_cmd = CONCAT('SELECT * FROM library_db.book INTO OUTFILE ''', file_path, ''' 
        FIELDS TERMINATED BY '','' 
        ENCLOSED BY ''"'' 
        LINES TERMINATED BY ''\r\n''');
    
    -- c. 预处理并执行动态 SQL
    SET @stmt_sql = sql_cmd;
    PREPARE stmt FROM @stmt_sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    
END $$

DELIMITER ;

-- 12. 自主存取控制
--  创建学生账号 (密码设置为 student123)
CREATE USER 'lib_student'@'%' IDENTIFIED BY 'student123';

-- 赋予权限：学生只能 "查看" (SELECT) 图书表
GRANT SELECT ON library_db.book TO 'lib_student'@'%';

-- 学生需要能借书，所以给 borrow 表的插入权限
GRANT SELECT, INSERT ON library_db.borrow TO 'lib_student'@'%';

-- 创建管理员账号 (密码设置为 admin123)
CREATE USER 'lib_admin'@'%' IDENTIFIED BY 'admin123';

--  赋予权限：管理员可以对图书表进行 增、删、改、查
GRANT SELECT, INSERT, UPDATE, DELETE ON library_db.book TO 'lib_admin'@'%';

-- 管理员对所有表都有权限
GRANT ALL PRIVILEGES ON library_db.* TO 'lib_admin'@'%';

--  刷新权限使生效
FLUSH PRIVILEGES;

-- 13. 触发器

DELIMITER //
CREATE TRIGGER trg_auto_update_status
AFTER UPDATE ON book
FOR EACH ROW
BEGIN
    -- 只有当库存发生变化时才执行，避免无效更新
    IF OLD.stock != NEW.stock THEN
        
        -- 判断逻辑：库存 <= 0 则显示需预约，否则显示可借阅
        IF NEW.stock <= 0 THEN
            -- 使用 REPLACE INTO 语法：如果记录存在就更新，不存在就插入
            REPLACE INTO book_status_monitor (book_id, book_title, display_status, last_updated)
            VALUES (NEW.id, NEW.title, '【需预约】库存不足', NOW());
        ELSE
            REPLACE INTO book_status_monitor (book_id, book_title, display_status, last_updated)
            VALUES (NEW.id, NEW.title, '【可借阅】', NOW());
        END IF;
        
    END IF;
END //

DELIMITER ;

-- 14.存储过程
DELIMITER //

CREATE PROCEDURE proc_borrow_and_get_status(
    IN p_book_id BIGINT,       -- 输入：要借的书ID
    OUT p_result_msg VARCHAR(100) -- 输出：返回最终的状态信息
)
BEGIN
    DECLARE v_rows_affected INT;

    -- 1. 尝试扣减库存 (前提是库存 > 0)
    -- 这一步 UPDATE 会瞬间激活你的【触发器】trg_auto_update_status
    UPDATE book 
    SET stock = stock - 1 
    WHERE id = p_book_id AND stock > 0;
    
    -- 获取刚才更新影响的行数
    SELECT ROW_COUNT() INTO v_rows_affected;

    -- 2. 判断是否借阅成功
    IF v_rows_affected > 0 THEN
        -- 借阅成功了！
        -- 此时触发器已经默默把 book_status_monitor 表更新了
        -- 直接去那个表里查最新的状态返回给 Java
        SELECT CONCAT('借阅成功。当前图书状态：', display_status) 
        INTO p_result_msg
        FROM book_status_monitor
        WHERE book_id = p_book_id;
        
    ELSE
        -- 更新行数为0，说明库存本来就是0，扣减失败
        SET p_result_msg = '借阅失败：库存不足，请进行预约';
    END IF;

END //

DELIMITER ;
