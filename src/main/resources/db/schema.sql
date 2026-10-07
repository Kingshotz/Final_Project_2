CREATE TABLE IF NOT EXISTS consumer_loan_cases (
    id              INT          PRIMARY KEY,
    description     VARCHAR(255) NOT NULL,
    amount          BIGINT       NOT NULL,
    months          INT          NOT NULL,
    expected_result VARCHAR(20)  NOT NULL,
    active          BOOLEAN      DEFAULT TRUE NOT NULL
);
