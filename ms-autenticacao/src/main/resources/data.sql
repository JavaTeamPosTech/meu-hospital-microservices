INSERT INTO public.usuarios
(id, ativo, cpf, created_at, data_nascimento, email, especialidade, nome, registro_profissional, "role", senha, telefone, updated_at)
VALUES('23e92504-7c46-469f-bbe9-e38e1ce5c47f'::uuid, true, '12345678955', '2025-10-04 18:51:33.344', '1980-05-15', 'paciente1@email.com', NULL, 'Pedro Paciente da Silva', NULL, 'PACIENTE', '$2a$10$6TZGgAbIk5aMiYx8qSEJHuqqRVH54WT0f7thIL5fS9pQ3MUvFOVpO', '(11)987653355', '2025-10-04 18:51:33.344') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.usuarios
(id, ativo, cpf, created_at, data_nascimento, email, especialidade, nome, registro_profissional, "role", senha, telefone, updated_at)
VALUES('8391675a-ef9d-4127-bbde-0d0a37a496c5'::uuid, true, '98765432101', '2025-10-04 18:51:42.909', '1995-11-20', 'paciente2@email.com', NULL, 'Ana Clara Souza', NULL, 'PACIENTE', '$2a$10$a7mtr6wu.kZUQdcHTV0sG.GVSeE.QtO6tOorN5pPAXBOQ1mbES6m2', '(21)998765432', '2025-10-04 18:51:42.909') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.usuarios
(id, ativo, cpf, created_at, data_nascimento, email, especialidade, nome, registro_profissional, "role", senha, telefone, updated_at)
VALUES('7c2208cb-2d93-44a0-bad0-95a9d5bb28e5'::uuid, true, '45678912344', '2025-10-04 18:51:53.529', '2001-08-01', 'paciente3@email.com', NULL, 'Lucas Fernando Torres', NULL, 'PACIENTE', '$2a$10$/Zk7ppiECKNkUPi2NzsCa.6/u/RLDXNEivhUn1gOhmsHvpmZrps82', '(31)988887777', '2025-10-04 18:51:53.529') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.usuarios
(id, ativo, cpf, created_at, data_nascimento, email, especialidade, nome, registro_profissional, "role", senha, telefone, updated_at)
VALUES('d2a0d5c9-e7fa-492f-9dea-24fd19b4b93c'::uuid, true, '65432198709', '2025-10-04 18:52:05.930', '1975-03-10', 'paciente4@email.com', NULL, 'Mariana Rodrigues Lima', NULL, 'PACIENTE', '$2a$10$JKQ6YAlWa0c1TLDi8EBPouEhe8ymu/yY0S4N6DknpLQ.BB.POf1aC', '(41)991234567', '2025-10-04 18:52:05.931') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.usuarios
(id, ativo, cpf, created_at, data_nascimento, email, especialidade, nome, registro_profissional, "role", senha, telefone, updated_at)
VALUES('929db4f8-5abe-4db3-bd7d-8a8aed64e548'::uuid, true, '32165498711', '2025-10-04 18:52:14.669', '1999-01-25', 'paciente5@email.com', NULL, 'João Pedro Alves', NULL, 'PACIENTE', '$2a$10$yLGPmMu08Q./5EiDLhmMoeAMYD3SuPVClf.6/7kRgRSKIGXIvWwsi', '(51)980000000', '2025-10-04 18:52:14.669') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.usuarios
(id, ativo, cpf, created_at, data_nascimento, email, especialidade, nome, registro_profissional, "role", senha, telefone, updated_at)
VALUES('668dca87-262e-4136-a58d-d3299188165a'::uuid, true, '11122233344', '2025-10-04 18:52:30.846', NULL, 'medico1@hospital.com', 'CARDIOLOGIA', 'Carlos Oliveira', 'CRM/PE-12345', 'MEDICO', '$2a$10$YEq2bmChrd5JPgSkncsMJeUnS94tfle5tAO5SVHGupOqcC2GgFsm.', '(81)991112222', '2025-10-04 18:52:30.846') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.usuarios
(id, ativo, cpf, created_at, data_nascimento, email, especialidade, nome, registro_profissional, "role", senha, telefone, updated_at)
VALUES('dd5c5f72-46cf-4f1e-903f-fe9fffb6c8f8'::uuid, true, '22233344455', '2025-10-04 18:52:41.595', NULL, 'medico2@hospital.com', 'ORTOPEDIA', 'Elisa Santos', 'CRM/SP-67890', 'MEDICO', '$2a$10$DojjJhjZ9D7krWyHwv.0d.QuZRRvSH/2qc9kxnf6AnttC.eSuFvI2', '(11)977776666', '2025-10-04 18:52:41.595') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.usuarios
(id, ativo, cpf, created_at, data_nascimento, email, especialidade, nome, registro_profissional, "role", senha, telefone, updated_at)
VALUES('390e7edb-2b9f-49ab-ac1d-6b783051ce37'::uuid, true, '33344455566', '2025-10-04 18:52:53.369', NULL, 'medico3@hospital.com', 'CLÍNICO GERAL', 'Fernando Costa', 'CRM/SC-54321', 'MEDICO', '$2a$10$bHi9Gas7G2ugRi5u0YoCA.gxCU2Q3XhaNjHb8Tgvo9bB4eacbq6aW', '(48)955554444', '2025-10-04 18:52:53.369') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.usuarios
(id, ativo, cpf, created_at, data_nascimento, email, especialidade, nome, registro_profissional, "role", senha, telefone, updated_at)
VALUES('870aa1dd-64e6-40a6-92e8-662c49fecea9'::uuid, true, '44455566677', '2025-10-04 18:53:06.375', NULL, 'enfermeiro1@hospital.com', NULL, 'Julia Almeida', 'COREN-87654', 'ENFERMEIRO', '$2a$10$4FyFLT9Q7PFFRg06OUiUkOro.iEtyca4XIT7up6vsc4P0PZGmeD9u', '(11)944443333', '2025-10-04 18:53:06.375') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.usuarios
(id, ativo, cpf, created_at, data_nascimento, email, especialidade, nome, registro_profissional, "role", senha, telefone, updated_at)
VALUES('58d78cce-312a-46c0-806e-13ce6c1a9a09'::uuid, true, '55566677788', '2025-10-04 18:53:12.967', NULL, 'enfermeiro2@hospital.com', NULL, 'Ricardo Lopes', 'COREN-98765', 'ENFERMEIRO', '$2a$10$EcPF0PpBfwgAqkMxnaaMnOdTSfZr9g..l8JPnNcQo50h7dqN2Q8mi', '(21)933332222', '2025-10-04 18:53:12.967') ON CONFLICT (id) DO NOTHING;