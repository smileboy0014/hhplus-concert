CREATE DATABASE IF NOT EXISTS concert DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;;
ALTER DATABASE concert CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

USE concert;

-- place 테이블 생성
CREATE TABLE IF NOT EXISTS place
(
    place_id   BIGINT NOT NULL AUTO_INCREMENT,
    name       VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    total_seat INT    NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (place_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- concert 테이블 생성
CREATE TABLE IF NOT EXISTS concert
(
    concert_id BIGINT NOT NULL AUTO_INCREMENT,
    name       VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
#     place_id   BIGINT,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (concert_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- concert_date 테이블 생성
CREATE TABLE IF NOT EXISTS concert_date
(
    concert_date_id BIGINT NOT NULL AUTO_INCREMENT,
    concert_id      BIGINT,
    place_id        BIGINT,
    concert_date    VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    created_at      DATETIME(6),
    updated_at      DATETIME(6),
    PRIMARY KEY (concert_date_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- seat 테이블 생성
CREATE TABLE IF NOT EXISTS seat
(
    seat_id         BIGINT NOT NULL AUTO_INCREMENT,
    concert_date_id BIGINT,
    seat_number     INT    NOT NULL,
    price           DECIMAL(38, 2),
    status          ENUM ('AVAILABLE','UNAVAILABLE'),
    ticket_class    VARCHAR(255),
    created_at      DATETIME(6),
    updated_at      DATETIME(6),
    PRIMARY KEY (seat_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- user 테이블 생성
CREATE TABLE IF NOT EXISTS users
(
    user_id    BIGINT NOT NULL AUTO_INCREMENT,
    balance    DECIMAL(38, 2),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (user_id)
) ENGINE = InnoDB;

-- reservation 테이블 생성
CREATE TABLE IF NOT EXISTS reservation
(
    reservation_id  BIGINT NOT NULL AUTO_INCREMENT,
    concert_id      BIGINT,
    concert_date_id BIGINT,
    seat_id         BIGINT,
    user_id         BIGINT,
    concert_name    VARCHAR(255),
    concert_date    VARCHAR(255),
    seat_number     INT    NOT NULL,
#     status          VARCHAR(255),
    status          ENUM ('CANCEL','TEMPORARY_RESERVED','COMPLETED'),
    reserved_at     DATETIME(6),
    created_at      DATETIME(6),
    updated_at      DATETIME(6),
    PRIMARY KEY (reservation_id)
) ENGINE = InnoDB;

-- payment 테이블 생성
CREATE TABLE IF NOT EXISTS payment
(
    payment_id     BIGINT NOT NULL AUTO_INCREMENT,
    reservation_id BIGINT,
    payment_price  DECIMAL(38, 2),
    price          DECIMAL(38, 2),
#     status         VARCHAR(255),
    status         ENUM ('CANCEL','WAIT','COMPLETE','REFUND'),
    paid_at        DATETIME(6),
    created_at     DATETIME(6),
    updated_at     DATETIME(6),
    PRIMARY KEY (payment_id)
) ENGINE = InnoDB;

-- waiting_queue 테이블 생성
CREATE TABLE IF NOT EXISTS waiting_queue
(
    waiting_queue_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id          BIGINT,
    token            VARCHAR(255),
#     status           VARCHAR(255),
    status           ENUM ('WAIT','ACTIVE','EXPIRED'),
    request_time     TIMESTAMP,
    active_time      TIMESTAMP,
    PRIMARY KEY (waiting_queue_id)
) ENGINE = InnoDB;