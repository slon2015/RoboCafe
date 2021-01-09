INSERT INTO security_object (id, role_id, invalidated) VALUES ('f7568e75-edfd-4257-a7af-0429302a2700', 'admin', false);
INSERT INTO security_object (id, role_id, invalidated) VALUES ('f7568e75-edfd-4257-a7af-0429302a2701', 'worker', false);

INSERT INTO additional_permissions (object_id, additional_permissions_name) VALUES ('f7568e75-edfd-4257-a7af-0429302a2701', 'hall_managment');
INSERT INTO additional_permissions (object_id, additional_permissions_name) VALUES ('f7568e75-edfd-4257-a7af-0429302a2701', 'hall_view');
INSERT INTO additional_permissions (object_id, additional_permissions_name) VALUES ('f7568e75-edfd-4257-a7af-0429302a2701', 'payment_managment');


INSERT INTO authentication (id, login, password, so_id) VALUES (1, 'admin', 'admin', 'f7568e75-edfd-4257-a7af-0429302a2700');
INSERT INTO authentication (id, login, password, so_id) VALUES (2, 'hall_admin', 'password', 'f7568e75-edfd-4257-a7af-0429302a2701');