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