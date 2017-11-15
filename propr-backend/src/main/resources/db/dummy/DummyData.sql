BEGIN TRANSACTION;

INSERT INTO users (user_id, username, email, password, enabled, firstname, lastname) VALUES
  (1, 'peter.vdbroek', 'peter@example.com', '$2a$10$WXhYLzz3/Fp3hBkk.vvVd.bhDcusdWLr4AZGSs.au8pwoYwAAOCbe', TRUE,
   'Peter',
   'van den Broek'),
  (2, 'john.otteraar', 'john@example.com', '$2a$10$WXhYLzz3/Fp3hBkk.vvVd.bhDcusdWLr4AZGSs.au8pwoYwAAOCbe', TRUE, 'John',
   'Otteraar'),
  (3, 'katie.verzicht', 'katie@example.com', '$2a$10$WXhYLzz3/Fp3hBkk.vvVd.bhDcusdWLr4AZGSs.au8pwoYwAAOCbe', TRUE,
   'Katie',
   'Verzicht'),
  (4, 'veerle.dewit', 'veerle@example.com', '$2a$10$WXhYLzz3/Fp3hBkk.vvVd.bhDcusdWLr4AZGSs.au8pwoYwAAOCbe', TRUE,
   'Veerle',
   'de Wit'),
  (5, 'anna.vanstraaten', 'anna@example.com', '$2a$10$WXhYLzz3/Fp3hBkk.vvVd.bhDcusdWLr4AZGSs.au8pwoYwAAOCbe', TRUE,
   'Anne',
   'van Straaten'),
  (6, 'freek.bakker', 'freek@example.com', '$2a$10$WXhYLzz3/Fp3hBkk.vvVd.bhDcusdWLr4AZGSs.au8pwoYwAAOCbe', TRUE,
   'Freek',
   'Bakker'),
  (7, 'carlo.vandeberg', 'carlo@example.com', '$2a$10$WXhYLzz3/Fp3hBkk.vvVd.bhDcusdWLr4AZGSs.au8pwoYwAAOCbe', TRUE,
   'Carlo',
   'van de Berg'),
  (8, 'rick.vancollenburg', 'rick@example.com', '$2a$10$WXhYLzz3/Fp3hBkk.vvVd.bhDcusdWLr4AZGSs.au8pwoYwAAOCbe', TRUE,
   'Rick',
   'van Collenburg'),
  (9, 'haye.bohm', 'haye@example.com', '$2a$10$WXhYLzz3/Fp3hBkk.vvVd.bhDcusdWLr4AZGSs.au8pwoYwAAOCbe', TRUE, 'Haye',
   'Bohm'),
  (10, 'marlies.overdijk', 'marlies@example.com', '$2a$10$WXhYLzz3/Fp3hBkk.vvVd.bhDcusdWLr4AZGSs.au8pwoYwAAOCbe', TRUE,
   'Marlies', 'Overdijk');

INSERT INTO groups (id, name, invite_code, admin) VALUES
  (11, 'Deuvelenstraat 65', '0011223344', 1),
  (12, 'Sportvereniging Goalies', '0011223344', 4),
  (13, 'TEAM 6', '0011223344', 6),
  (14, 'DEVTEAM', 'DEVWW', 9);

INSERT INTO users_groups (group_id, user_id) VALUES
  (11, 1),
  (11, 2),
  (11, 3),

  (12, 4),
  (12, 5),

  (13, 6),
  (13, 7),

  (14, 9),
  (14, 8),
  (14, 10);

INSERT INTO task (id, name, description, assignee, "group", due_date) VALUES
  -- Deuvelenstraat
  (15, 'Badkamer schoonmaken', 'Badkamer schoonmaken, leegmaken doucheputje', 1, 11, '01-12-2017'),
  (16, 'WC schoonmaken', 'WC schoonmaken, bijvullen TP', 2, 11, '02-12-2017'),
  (17, 'Stofzuigen eerste verdieping', 'Stofzuigen', 3, 11, '03-12-2017'),
  (18, 'Stofzuigen tweede verdieping', 'Stofzuigen', 2, 11, '04-12-2017'),

  -- Sportvereniging
  (19, 'Ballen verzamelen', '', 4, 12, '05-12-2017'),
  (20, 'Grasmaaien', '', 5, 12, '06-12-2017'),
  (21, 'Bitterballen aanvullen', '', 4, 12, '07-12-2017'),
  (22, 'Clubhuis opruimen', '', 5, 12, '08-12-2017'),

  -- TEAM 6
  (23, 'Trainingsprogramma uitwerken', '', 6, 13, '09-12-2017'),
  (24, 'Uitje verzinnen', '', 6, 13, '10-12-2017'),
  (25, 'Datumprikker maken', '', 7, 13, '11-12-2017'),

  -- DEVTEAM
  (26, 'Software maken', 'Hopelijk meteen zonder bugs', 9, 14, '11-12-2017'),
  (27, 'Eerste demo', '', 8, 14, '11-12-2017'),
  (28, 'Tweede demo', '', 8, 14, '11-12-2017'),
  (29, 'Release', '', 10, 14, '11-12-2017'),
  (30, 'Meer software maken', 'Dit keer goed, zonder bugs', 9, 14, '01-01-2018'),
  (31, 'Tests schrijven', 'Zonder goede tests, geen goede software', 9, 14, '05-01-2018'),
  (32, 'Database nakijken', 'Wat als er oude data instaat?', 9, 14, '06-02-2018'),
  (33, 'Design verbeteren', 'Het ziet er nu uit als shit', 9, 14, '20-02-2018'),
  (34, 'Interfaces extraheren', 'Voorkomen van dubbel werk', 9, 14, '23-02-2018');


ALTER SEQUENCE hibernate_sequence RESTART WITH 1111;
COMMIT;
