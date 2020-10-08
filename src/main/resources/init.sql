create database content_center;

show databases ;

USE `content_center`;

-- -----------------------------------------------------
-- Table `share`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `share` (
                                       `id` INT NOT NULL AUTO_INCREMENT COMMENT 'id',
                                       `user_id` INT NOT NULL DEFAULT 0 COMMENT '发布人id',
                                       `title` VARCHAR(80) NOT NULL DEFAULT '' COMMENT '标题',
                                       `create_time` DATETIME NOT NULL COMMENT '创建时间',
                                       `update_time` DATETIME NOT NULL COMMENT '修改时间',
                                       `is_original` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否原创 0:否 1:是',
                                       `author` VARCHAR(45) NOT NULL DEFAULT '' COMMENT '作者',
                                       `cover` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '封面',
                                       `summary` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '概要信息',
                                       `price` INT NOT NULL DEFAULT 0 COMMENT '价格（需要的积分）',
                                       `download_url` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '下载地址',
                                       `buy_count` INT NOT NULL DEFAULT 0 COMMENT '下载数 ',
                                       `show_flag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否显示 0:否 1:是',
                                       `audit_status` VARCHAR(10) NOT NULL DEFAULT 0 COMMENT '审核状态 NOT_YET: 待审核 PASSED:审核通过 REJECTED:审核不通过',
                                       `reason` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '审核不通过原因',
                                       PRIMARY KEY (`id`))
    ENGINE = InnoDB
    COMMENT = '分享表';


-- -----------------------------------------------------
-- Table `mid_user_share`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mid_user_share` (
                                                `id` INT NOT NULL AUTO_INCREMENT,
                                                `share_id` INT NOT NULL COMMENT 'share.id',
                                                `user_id` INT NOT NULL COMMENT 'user.id',
                                                PRIMARY KEY (`id`),
                                                INDEX `fk_mid_user_share_share1_idx` (`share_id` ASC) ,
                                                INDEX `fk_mid_user_share_user1_idx` (`user_id` ASC) )
    ENGINE = InnoDB
    COMMENT = '用户-分享中间表【描述用户购买的分享】';


-- -----------------------------------------------------
-- Table `notice`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `notice` (
                                        `id` INT NOT NULL AUTO_INCREMENT COMMENT 'id',
                                        `content` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '内容',
                                        `show_flag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否显示 0:否 1:是',
                                        `create_time` DATETIME NOT NULL COMMENT '创建时间',
                                        PRIMARY KEY (`id`));