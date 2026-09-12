-- Ensure demo users exist with the correct password ('password') and names
-- Using INSERT ... ON CONFLICT to handle both new and existing databases

INSERT INTO users (id, email, password, first_name, last_name, role)
VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'admin@demo.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHGC2', 'Admin', 'User', 'ADMIN'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'tech@demo.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHGC2', 'Vikram', 'Singh', 'TECHNICIAN')
ON CONFLICT (email) DO UPDATE
SET password = EXCLUDED.password,
    role = EXCLUDED.role,
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name;

-- Ensure IDs are also consistent in case of email-only conflicts
UPDATE users SET id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11' WHERE email = 'admin@demo.com';
UPDATE users SET id = 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12' WHERE email = 'tech@demo.com';
