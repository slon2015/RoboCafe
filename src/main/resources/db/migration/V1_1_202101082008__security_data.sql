
INSERT INTO security_role (name) VALUES ('table');
INSERT INTO security_role (name) VALUES ('party');
INSERT INTO security_role (name) VALUES ('person');
INSERT INTO security_role (name) VALUES ('worker');
INSERT INTO security_role (name) VALUES ('admin');

INSERT INTO permission (name) VALUES ('start_chat');
INSERT INTO permission (name) VALUES ('send_massage_to_chat');
INSERT INTO permission (name) VALUES ('tables_managment');
INSERT INTO permission (name) VALUES ('kitchen_managment');
INSERT INTO permission (name) VALUES ('kitchen_view');
INSERT INTO permission (name) VALUES ('hall_managment');
INSERT INTO permission (name) VALUES ('hall_view');
INSERT INTO permission (name) VALUES ('payment_managment');

INSERT INTO role_default_permissions (role_name, permission_name) VALUES ('person', 'start_chat');
INSERT INTO role_default_permissions (role_name, permission_name) VALUES ('person', 'send_massage_to_chat');

INSERT INTO role_default_permissions (role_name, permission_name) VALUES ('admin', 'tables_managment');