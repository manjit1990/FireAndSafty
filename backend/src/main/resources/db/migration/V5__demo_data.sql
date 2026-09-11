-- Demo Users (Password: password)
INSERT INTO users (id, email, password, first_name, last_name, role)
VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'admin@demo.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHGC2', 'Admin', 'User', 'ADMIN'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'tech@demo.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHGC2', 'Vikram', 'Singh', 'TECHNICIAN');

-- Demo Customer
INSERT INTO customers (id, name, contact_person, email, phone, billing_address)
VALUES
('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'ICC Property Management Ltd', 'Jane Doe', 'billing@icc.com', '416-555-0199', '5740 Yonge Street, North York, ON');

-- Demo Building
INSERT INTO buildings (id, customer_id, name, address, building_type)
VALUES
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'The Palm Condominium Residences', '5740 Yonge Street, North York, ON M2M 0B1', 'RESIDENTIAL');

-- Demo Work Orders for Today (Aligned with screenshots)
INSERT INTO work_orders (id, building_id, type, status, priority, scheduled_at, assigned_technician_id, dispatcher_notes)
VALUES
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'INSPECTION', 'NEW', 'MEDIUM', CURRENT_DATE + TIME '08:00:00', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'To conduct the monthly inspection.'),
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'FIRE_DRILL', 'NEW', 'MEDIUM', CURRENT_DATE + TIME '09:00:00', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'To conduct a Fire drill.'),
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a17', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'TESTING', 'NEW', 'HIGH', CURRENT_DATE + TIME '10:00:00', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'To conduct a Smoke control test.');
