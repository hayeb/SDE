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

INSERT INTO task_definition (id, name, description, "group", weight, period_type, frequency) VALUES
	-- Deuvelenstraat
	(15, 'Badkamer schoonmaken', 'Badkamer schoonmaken, leegmaken doucheputje', 11, 'MEDIUM', 'WEEK', 1),
	(16, 'WC schoonmaken', 'WC schoonmaken, bijvullen TP', 11, 'MEDIUM', 'WEEK', 1),
	(17, 'Stofzuigen eerste verdieping', 'Stofzuigen', 11, 'HEAVY', 'MONTH', 2),
	(18, 'Stofzuigen tweede verdieping', 'Stofzuigen', 11, 'HEAVY', 'MONTH', 2),

	-- Sportvereniging
	(19, 'Ballen verzamelen', '', 12, 'LIGHT', 'WEEK', 2),
	(20, 'Grasmaaien', '', 12, 'MEDIUM', 'MONTH', 2),
	(21, 'Bitterballen aanvullen', '', 12, 'HEAVY', 'WEEK', 1),
	(22, 'Clubhuis opruimen', '', 12, 'MEDIUM', 'WEEK', 1),

	-- TEAM 6
	(23, 'Trainingsprogramma uitwerken', '', 13, 'LIGHT', 'WEEK', 1),
	(24, 'Uitje verzinnen', '', 13, 'HEAVY', 'YEAR', 1),
	(25, 'Datumprikker maken', '', 13, 'LIGHT', 'YEAR', 1),

	-- DEVTEAM
	(26, 'Software maken', 'Hopelijk meteen zonder bugs', 14, 'HEAVY', 'MONTH', 4),
	(27, 'Interne demo', '', 14, 'LIGHT', 'WEEK', 1),
	(28, 'Externe demo', '', 14, 'LIGHT', 'MONTH', 2),
	(29, 'Release plaatsen', '', 14, 'MEDIUM', 'MONTH', 2),
	(30, 'Meer software maken', 'Dit keer goed, zonder bugs', 14, 'HEAVY', 'MONTH', 4),
	(31, 'Tests schrijven', 'Zonder goede tests, geen goede software', 14, 'HEAVY', 'MONTH', 1),
	(32, 'Database nakijken', 'Wat als er oude data instaat?', 14, 'MEDIUM', 'MONTH', 1),
	(33, 'Design verbeteren', 'Het ziet er nu uit als shit', 14, 'MEDIUM', 'MONTH', 1),
	(34, 'Interfaces extraheren', 'Voorkomen van dubbel werk', 14, 'LIGHT', 'MONTH', 1);

INSERT INTO assigned_task (id, assignee, definition, due_date, status) VALUES
	-- Deuvelenstraat
	(35, 1, 15, '01-12-2017', 'TODO'),
	(36, 2, 16, '02-12-2017', 'TODO'),
	(37, 3, 17, '03-12-2017', 'TODO'),
	(38, 2, 18, '04-12-2017', 'TODO'),

	-- Sportvereniging
	(39, 4, 19, '05-12-2017', 'TODO'),
	(40, 5, 20, '06-12-2017', 'TODO'),
	(41, 4, 21, '07-12-2017', 'TODO'),
	(42, 5, 22, '08-12-2017', 'TODO'),

	-- TEAM
	(43, 6, 23, '09-12-2017', 'TODO'),
	(44, 6, 24, '10-12-2017', 'TODO'),
	(45, 7, 25, '11-12-2017', 'TODO'),

	-- DEVTE
	(46, 9, 26, '01-02-2017', 'OVERDUE'),
	(47, 8, 27, '01-03-2017', 'OVERDUE'),
	(48, 8, 28, '11-12-2017', 'DONE'),
	(49, 10, 29, '11-12-2017', 'TODO'),
	(50, 9, 30, '01-01-2018', 'TODO'),
	(51, 9, 31, '05-01-2018', 'TODO'),
	(52, 9, 32, '06-02-2018', 'TODO'),
	(53, 9, 33, '20-02-2018', 'TODO'),
	(54, 9, 34, '23-02-2018', 'TODO');


ALTER SEQUENCE hibernate_sequence RESTART WITH 1111;
COMMIT;
