
INSERT INTO security_role (name) VALUES ('table');
INSERT INTO security_role (name) VALUES ('party');
INSERT INTO security_role (name) VALUES ('person');
INSERT INTO security_role (name) VALUES ('worker');
INSERT INTO security_role (name) VALUES ('admin');

INSERT INTO permission (name) VALUES ('start_chat');
INSERT INTO permission (name) VALUES ('send_massage_to_chat');
INSERT INTO permission (name) VALUES ('tables_managment');

INSERT INTO role_default_permissions (role_name, permission_name) VALUES ('person', 'start_chat');
INSERT INTO role_default_permissions (role_name, permission_name) VALUES ('person', 'send_massage_to_chat');

INSERT INTO role_default_permissions (role_name, permission_name) VALUES ('admin', 'tables_managment');

INSERT INTO security_object (id, role_id, invalidated) VALUES ('f7568e75-edfd-4257-a7af-0429302a2700', 'admin', false);

INSERT INTO authentication (id, login, password, so_id) VALUES (1, 'admin', 'admin', 'f7568e75-edfd-4257-a7af-0429302a2700');