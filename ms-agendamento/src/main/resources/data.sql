INSERT INTO public.medicos_projection
(id, especialidade, nome, numero_registro, "role")
VALUES('668dca87-262e-4136-a58d-d3299188165a'::uuid, 'CARDIOLOGIA', 'Carlos Oliveira', 'CRM/PE-12345', 'MEDICO') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.medicos_projection
(id, especialidade, nome, numero_registro, "role")
VALUES('dd5c5f72-46cf-4f1e-903f-fe9fffb6c8f8'::uuid, 'ORTOPEDIA', 'Elisa Santos', 'CRM/SP-67890', 'MEDICO') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.medicos_projection
(id, especialidade, nome, numero_registro, "role")
VALUES('390e7edb-2b9f-49ab-ac1d-6b783051ce37'::uuid, 'CLÍNICO GERAL', 'Fernando Costa', 'CRM/SC-54321', 'MEDICO') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.consultas
(id, created_at, data_consulta, detalhes_da_consulta, medico_id, nome_medico, nome_paciente, paciente_id, status, updated_at)
VALUES('ad533a17-3282-4c53-8555-4159dc9d4fb0'::uuid, '2025-10-04 19:08:02.293', '2025-11-15 10:00:00.000', 'Avaliação inicial de risco cardíaco.', '668dca87-262e-4136-a58d-d3299188165a'::uuid, 'Carlos Oliveira', 'Pedro Paciente da Silva', '23e92504-7c46-469f-bbe9-e38e1ce5c47f'::uuid, 'AGENDADA', '2025-10-04 19:08:02.293') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.consultas
(id, created_at, data_consulta, detalhes_da_consulta, medico_id, nome_medico, nome_paciente, paciente_id, status, updated_at)
VALUES('9f3f457d-f9db-47a4-9806-b0df94f89938'::uuid, '2025-10-04 19:09:48.500', '2025-11-15 10:20:00.000', 'Verificação de rotina (teste de conflito).', '668dca87-262e-4136-a58d-d3299188165a'::uuid, 'Carlos Oliveira', 'Ana Clara Souza', '8391675a-ef9d-4127-bbde-0d0a37a496c5'::uuid, 'AGENDADA', '2025-10-04 19:09:48.500') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.consultas
(id, created_at, data_consulta, detalhes_da_consulta, medico_id, nome_medico, nome_paciente, paciente_id, status, updated_at)
VALUES('780988bd-8486-49a2-9eee-973de6aa8fbe'::uuid, '2025-10-04 19:10:32.545', '2025-11-15 10:30:00.000', 'Consulta de retorno com exames laboratoriais.', '668dca87-262e-4136-a58d-d3299188165a'::uuid, 'Carlos Oliveira', 'Lucas Fernando Torres', '7c2208cb-2d93-44a0-bad0-95a9d5bb28e5'::uuid, 'AGENDADA', '2025-10-04 19:10:32.545') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.consultas
(id, created_at, data_consulta, detalhes_da_consulta, medico_id, nome_medico, nome_paciente, paciente_id, status, updated_at)
VALUES('59047ef4-0784-4bb6-8b95-acccc8ab1d33'::uuid, '2025-10-04 19:10:41.769', '2025-11-15 14:00:00.000', 'Avaliação de lesão no joelho esquerdo.', 'dd5c5f72-46cf-4f1e-903f-fe9fffb6c8f8'::uuid, 'Elisa Santos', 'Mariana Rodrigues Lima', 'd2a0d5c9-e7fa-492f-9dea-24fd19b4b93c'::uuid, 'AGENDADA', '2025-10-04 19:10:41.769') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.consultas
(id, created_at, data_consulta, detalhes_da_consulta, medico_id, nome_medico, nome_paciente, paciente_id, status, updated_at)
VALUES('8c3183f6-e17d-4749-9954-23caafe4cbc8'::uuid, '2025-10-04 19:10:52.308', '2025-10-05 09:00:00.000', 'Consulta de rotina anual (Job de Lembrete).', '390e7edb-2b9f-49ab-ac1d-6b783051ce37'::uuid, 'Fernando Costa', 'João Pedro Alves', '929db4f8-5abe-4db3-bd7d-8a8aed64e548'::uuid, 'AGENDADA', '2025-10-04 19:10:52.308') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.consultas
(id, created_at, data_consulta, detalhes_da_consulta, medico_id, nome_medico, nome_paciente, paciente_id, status, updated_at)
VALUES('e98dd52e-e206-4bcb-ada8-5c2215733546'::uuid, '2025-10-04 19:11:03.342', '2025-11-15 15:30:00.000', 'Dor súbita no ombro.', 'dd5c5f72-46cf-4f1e-903f-fe9fffb6c8f8'::uuid, 'Elisa Santos', 'Pedro Paciente da Silva', '23e92504-7c46-469f-bbe9-e38e1ce5c47f'::uuid, 'AGENDADA', '2025-10-04 19:11:03.342') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.consultas
(id, created_at, data_consulta, detalhes_da_consulta, medico_id, nome_medico, nome_paciente, paciente_id, status, updated_at)
VALUES('e1874c4e-beba-4e8f-af94-30cb891bddae'::uuid, '2025-10-04 19:11:13.229', '2025-11-20 11:00:00.000', 'Acompanhamento de pressão arterial.', '390e7edb-2b9f-49ab-ac1d-6b783051ce37'::uuid, 'Fernando Costa', 'Ana Clara Souza', '8391675a-ef9d-4127-bbde-0d0a37a496c5'::uuid, 'AGENDADA', '2025-10-04 19:11:13.229') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.consultas
(id, created_at, data_consulta, detalhes_da_consulta, medico_id, nome_medico, nome_paciente, paciente_id, status, updated_at)
VALUES('69a58a6e-ac30-42e7-8cdc-fd1c1037ee3a'::uuid, '2025-10-04 19:11:22.981', '2025-11-21 16:00:00.000', 'Check-up geral.', '390e7edb-2b9f-49ab-ac1d-6b783051ce37'::uuid, 'Fernando Costa', 'Mariana Rodrigues Lima', 'd2a0d5c9-e7fa-492f-9dea-24fd19b4b93c'::uuid, 'AGENDADA', '2025-10-04 19:11:22.981') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.consultas
(id, created_at, data_consulta, detalhes_da_consulta, medico_id, nome_medico, nome_paciente, paciente_id, status, updated_at)
VALUES('532b016b-de33-46e9-b0d1-a0ccfed31b81'::uuid, '2025-10-04 19:11:33.637', '2025-11-22 08:30:00.000', 'Dor nas costas há duas semanas.', 'dd5c5f72-46cf-4f1e-903f-fe9fffb6c8f8'::uuid, 'Elisa Santos', 'João Pedro Alves', '929db4f8-5abe-4db3-bd7d-8a8aed64e548'::uuid, 'AGENDADA', '2025-10-04 19:11:33.637') ON CONFLICT (id) DO NOTHING;

INSERT INTO public.consultas
(id, created_at, data_consulta, detalhes_da_consulta, medico_id, nome_medico, nome_paciente, paciente_id, status, updated_at)
VALUES('4a68af61-bfaa-49dd-827f-cb3269d44edf'::uuid, '2025-10-04 19:11:40.734', '2025-12-03 09:30:00.000', 'Cancelado pela equipe em: 2025-10-04T19:18:22.087292799', '668dca87-262e-4136-a58d-d3299188165a'::uuid, 'Carlos Oliveira', 'Ana Clara Souza', '8391675a-ef9d-4127-bbde-0d0a37a496c5'::uuid, 'CANCELADA', '2025-10-04 19:18:22.094' ON CONFLICT (id) DO NOTHING;;