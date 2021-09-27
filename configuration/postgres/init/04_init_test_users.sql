INSERT INTO users (username, password) VALUES ('test-admin-account', '$argon2id$v=19$m=4096,t=3,p=1$bCSMRKg2NGqs1ySgk2jcLA$k0/FmRh7kr57TZc2n0Gb3tWz+0REhveIAmdgiKAZk2s');
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (1, 2);

INSERT INTO users (username, password) VALUES ('test-user-account', '$argon2id$v=19$m=4096,t=3,p=1$bCSMRKg2NGqs1ySgk2jcLA$k0/FmRh7kr57TZc2n0Gb3tWz+0REhveIAmdgiKAZk2s');
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);
