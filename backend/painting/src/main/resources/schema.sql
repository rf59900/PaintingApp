CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username varchar(45) NOT NULL,
    password varchar(250) NOT NULL,
    joined TIMESTAMP NOT NULL,
    average_rating DOUBLE PRECISION,
    roles varchar(250),
    refresh_token varchar(500)
);

CREATE TABLE IF NOT EXISTS painting (
    id bigint GENERATED ALWAYS AS IDENTITY,
    title varchar(45) NOT NULL,
    description varchar(45),
    created TIMESTAMP NOT NULL,
    rating DOUBLE PRECISION,
    image_name varchar(450),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS painted (
    painter INT REFERENCES users (id),
    painting bigint REFERENCES painting (id)
);