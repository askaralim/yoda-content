-- Test database schema for content table
CREATE TABLE IF NOT EXISTS content (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    feature_data BOOLEAN DEFAULT FALSE,
    published BOOLEAN DEFAULT FALSE,
    category_id BIGINT,
    publish_date TIMESTAMP,
    expire_date TIMESTAMP,
    hit_counter INT DEFAULT 0,
    score INT DEFAULT 0,
    site_id INT,
    natural_key VARCHAR(255),
    title VARCHAR(500),
    short_description TEXT,
    description TEXT,
    page_title VARCHAR(500),
    featured_image VARCHAR(500),
    update_by VARCHAR(100),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by VARCHAR(100),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
