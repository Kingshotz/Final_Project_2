MERGE INTO consumer_loan_cases (id, description, amount, months, expected_result, active) KEY (id) VALUES
    (1, 'Minimum amount and shortest period',  200,    3, 'VALID',            TRUE),
    (2, 'Maximum amount and longest period',   80000, 48, 'VALID',            TRUE),
    (3, 'Typical three year loan',             12345, 36, 'VALID',            TRUE),
    (4, 'Amount below minimum',                199,   12, 'AMOUNT_TOO_LOW',   TRUE),
    (5, 'Amount above maximum',                80001, 12, 'AMOUNT_TOO_HIGH',  TRUE),
    (6, 'Period shorter than minimum',         5000,   2, 'PERIOD_TOO_SHORT', TRUE),
    (7, 'Period longer than maximum',          5000,  49, 'PERIOD_TOO_LONG',  TRUE),
    (8, 'Disabled example - skipped by query', 1000,   6, 'VALID',            FALSE);
