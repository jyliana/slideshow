DROP TABLE IF EXISTS images;
DROP TABLE IF EXISTS slideshows;
DROP TABLE IF EXISTS slideshow_image;
DROP TABLE IF EXISTS proof_of_play;

CREATE TABLE images (
    id           BIGSERIAL    PRIMARY KEY,
    duration     INTEGER      NOT NULL,
    url          VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE slideshows(
    id           BIGSERIAL     PRIMARY KEY,
    created_at   TIMESTAMP     NOT NULL,
    name         VARCHAR(255)  NOT NULL
);

CREATE TABLE slideshow_image (
    image_id     INTEGER       NOT NULL,
    slideshow_id INTEGER       NOT NULL,
    added_date   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (image_id, slideshow_id),
    FOREIGN KEY (image_id) REFERENCES images (id),
    FOREIGN KEY (slideshow_id) REFERENCES slideshows (id)
);

CREATE TABLE proof_of_play(
    id           BIGSERIAL     PRIMARY KEY,
    image_id     INTEGER       NOT NULL,
    slideshow_id INTEGER       NOT NULL,
    played_at    TIMESTAMP     NOT NULL  DEFAULT CURRENT_TIMESTAMP
);
