INSERT INTO specialties (id, code, label, description) VALUES
(1, 'CARDIO', 'Cardiologie', NULL),
(2, 'GEN', 'Médecine générale', NULL),
(3, 'PED', 'Pédiatrie', NULL);

INSERT INTO doctors (id, nom, prenom, email, specialty_id, telephone, service, registration_number, department, active, created_at) VALUES
(1, 'Bernard', 'Claire', 'claire.bernard@ehealth.local', 1, '0612345678', 'Cardio A', 'RPSS-001', 'Cardiologie', TRUE, NOW()),
(2, 'Petit', 'Marc', 'marc.petit@ehealth.local', 2, '0623456789', 'Consultations', 'RPSS-002', 'Médecine générale', TRUE, NOW()),
(3, 'Dubois', 'Sophie', 'sophie.dubois@ehealth.local', 3, '0634567890', 'Pédiatrie', 'RPSS-003', 'Pédiatrie', TRUE, NOW());

INSERT INTO doctor_availabilities (id, doctor_id, day_of_week, heure_debut, heure_fin) VALUES
(1, 1, 'MONDAY', '09:00:00', '12:00:00'),
(2, 1, 'MONDAY', '14:00:00', '18:00:00');

-- H2 : les INSERT avec id explicites ne mettent pas à jour le compteur IDENTITY.
-- Sans cela, le prochain save() JPA réutilise id=1 → erreur SQL → HTTP 500.
ALTER TABLE specialties ALTER COLUMN id RESTART WITH 4;
ALTER TABLE doctors ALTER COLUMN id RESTART WITH 4;
ALTER TABLE doctor_availabilities ALTER COLUMN id RESTART WITH 3;
