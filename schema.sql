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
