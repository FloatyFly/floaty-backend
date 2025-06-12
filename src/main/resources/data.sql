-- Initialize test data

-- Insert user (uses AUTO_INCREMENT)
INSERT INTO t_user (name, email, email_verified, password_hash) 
VALUES ('Floater', 'floater@example.com', TRUE, '$2a$10$uzOabWALxNO0eGVcjipPoOPv68yivI6TYmWqL7NG6AMdQyO5bvjkW');

-- Get the user ID for foreign key references
SET @user_id = LAST_INSERT_ID();

-- Update sequences for manual ID assignment
UPDATE hibernate_sequence SET next_val = 200;

-- Insert 30 launch sites across Switzerland
-- Central Switzerland (Alps)
INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (1, 'Titlis', @user_id, 46.7712, 8.4347, 3238, 'Nice and high.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (2, 'Pilatus', @user_id, 46.9779, 8.2527, 2128, 'Classic launch site with great thermals.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (3, 'Hasliberg', @user_id, 46.7317, 8.1056, 1450, 'Beautiful mountain launch with scenic views.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (4, 'Interlaken Harder', @user_id, 46.6863, 7.8632, 1322, 'Popular launch spot above Interlaken.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (5, 'Rigi', @user_id, 47.0572, 8.4853, 1798, 'Queen of the mountains with excellent thermals.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (6, 'Stanserhorn', @user_id, 46.9558, 8.3406, 1898, 'Central Swiss launch with lake views.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (7, 'Brunni', @user_id, 46.8175, 8.4097, 1860, 'Engelberg area launch with alpine scenery.', TRUE, FALSE);

-- Bernese Oberland
INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (8, 'Beatenberg', @user_id, 46.6986, 7.7586, 1150, 'Classic Bernese Oberland launch above Lake Thun.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (9, 'Niederhorn', @user_id, 46.7083, 7.7514, 1950, 'High launch with spectacular Eiger views.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (10, 'First', @user_id, 46.6667, 8.0333, 2166, 'Grindelwald First with incredible mountain panorama.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (11, 'Männlichen', @user_id, 46.6106, 7.9394, 2343, 'High alpine launch with Jungfrau views.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (12, 'Stockhorn', @user_id, 46.7058, 7.5353, 2190, 'Bernese Alps launch with thermal potential.', TRUE, FALSE);

-- Jura Mountains
INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (13, 'Chasseral', @user_id, 47.1317, 7.0567, 1607, 'Highest peak in Jura with excellent ridge lift.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (14, 'La Dôle', @user_id, 46.4247, 6.0992, 1677, 'Jura summit near Geneva with consistent thermals.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (15, 'Mont Tendre', @user_id, 46.5742, 6.3156, 1679, 'Highest Jura peak with reliable lift conditions.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (16, 'Weissenstein', @user_id, 47.2386, 7.5031, 1284, 'Jura ridge with thermal and ridge lift options.', TRUE, FALSE);

-- Eastern Switzerland
INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (17, 'Säntis', @user_id, 47.2497, 9.3433, 2502, 'Highest peak in eastern Switzerland, weather dependent.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (18, 'Flumserberg', @user_id, 47.0833, 9.2667, 1844, 'Popular eastern Swiss launch with consistent thermals.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (19, 'Pizol', @user_id, 46.9833, 9.4167, 2227, 'High alpine launch in Heidiland region.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (20, 'Chrüz', @user_id, 47.0286, 9.1717, 1264, 'Eastern Swiss launch above Walensee.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (21, 'Hoher Kasten', @user_id, 47.2681, 9.4425, 1795, 'Appenzell launch with Rhine valley views.', TRUE, FALSE);

-- Ticino (Southern Switzerland)
INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (22, 'Monte Tamaro', @user_id, 46.1028, 8.8644, 1961, 'Ticino launch with Mediterranean flying conditions.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (23, 'Monte Lema', @user_id, 46.0519, 8.8506, 1624, 'Southern Swiss launch with warm thermals.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (24, 'Cardada', @user_id, 46.1814, 8.7956, 1670, 'Locarno area launch above Lago Maggiore.', TRUE, FALSE);

-- Valais
INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (25, 'Verbier', @user_id, 46.0967, 7.2281, 2500, 'High alpine Valais launch with strong thermals.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (26, 'Saas-Fee', @user_id, 46.1044, 7.9289, 3000, 'Glacial launch with extreme alpine conditions.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (27, 'Zermatt', @user_id, 46.0207, 7.7491, 2500, 'Matterhorn area launch for experienced pilots.', TRUE, FALSE);

-- Additional Central/Eastern launches
INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (28, 'Fronalpstock', @user_id, 47.0444, 8.6017, 1922, 'Schwyz launch with Lake Lucerne views.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (29, 'Chäserrugg', @user_id, 47.0833, 9.0833, 2262, 'Toggenburg launch with eastern Alpine views.', TRUE, FALSE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (30, 'Kronberg', @user_id, 47.3056, 9.3417, 1663, 'Appenzell launch near Austrian border.', TRUE, FALSE);

-- Insert 20 landing sites across Switzerland
-- Central Switzerland landings
INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (31, 'Engelberg', @user_id, 46.8186, 8.4081, 1015, 'Yanik\'s home base.', FALSE, TRUE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (32, 'Lucerne Valley', @user_id, 47.0502, 8.3093, 436, 'Wide open landing field near the lake.', FALSE, TRUE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (33, 'Brunnen', @user_id, 46.9958, 8.6036, 435, 'Lake Lucerne landing with mountain backdrop.', FALSE, TRUE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (34, 'Alpnach', @user_id, 46.9456, 8.2719, 441, 'Central Swiss valley landing near Pilatus.', FALSE, TRUE);

-- Bernese Oberland landings
INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (35, 'Grindelwald', @user_id, 46.6244, 8.0411, 1034, 'Stunning alpine landing with mountain backdrop.', FALSE, TRUE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (36, 'Interlaken', @user_id, 46.6863, 7.8632, 566, 'Popular tourist area landing between lakes.', FALSE, TRUE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (37, 'Thun', @user_id, 46.7580, 7.6280, 560, 'Lake Thun landing with urban convenience.', FALSE, TRUE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (38, 'Spiez', @user_id, 46.6853, 7.6914, 628, 'Scenic lake landing in Bernese Oberland.', FALSE, TRUE);

-- Jura landings
INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (39, 'Neuchâtel', @user_id, 46.9929, 6.9310, 432, 'Lake Neuchâtel landing in Jura foothills.', FALSE, TRUE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (40, 'Biel/Bienne', @user_id, 47.1369, 7.2462, 434, 'Bilingual city landing near Jura mountains.', FALSE, TRUE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (41, 'Yverdon', @user_id, 46.7786, 6.6411, 435, 'Western Swiss landing near Lake Neuchâtel.', FALSE, TRUE);

-- Eastern Switzerland landings
INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (42, 'Sargans', @user_id, 47.0444, 9.4428, 482, 'Rhine valley landing in eastern Switzerland.', FALSE, TRUE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (43, 'Rapperswil', @user_id, 47.2267, 8.8189, 408, 'Lake Zurich landing with medieval charm.', FALSE, TRUE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (44, 'St. Gallen', @user_id, 47.4245, 9.3767, 676, 'Eastern Swiss city landing near Appenzell.', FALSE, TRUE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (45, 'Appenzell', @user_id, 47.3317, 9.4108, 780, 'Traditional Swiss landing in rolling hills.', FALSE, TRUE);

-- Ticino landings
INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (46, 'Locarno', @user_id, 46.1728, 8.7991, 196, 'Lago Maggiore landing with Mediterranean feel.', FALSE, TRUE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (47, 'Lugano', @user_id, 46.0037, 8.9511, 273, 'Southern Swiss lake landing near Italian border.', FALSE, TRUE);

-- Valais landings
INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (48, 'Sion', @user_id, 46.2292, 7.3608, 515, 'Valais capital landing in Rhône valley.', FALSE, TRUE);

INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (49, 'Martigny', @user_id, 46.1019, 7.0750, 471, 'Western Valais landing near France border.', FALSE, TRUE);

-- Zurich area landing
INSERT INTO t_spot (id, name, fk_user_id, latitude, longitude, altitude, description, is_launch_site, is_landing_site)
VALUES (50, 'Zurich Kloten', @user_id, 47.4647, 8.5492, 432, 'Metropolitan landing near international airport.', FALSE, TRUE);

-- Insert 5 gliders
INSERT INTO t_glider (id, fk_user_id, manufacturer, model)
VALUES (51, @user_id, 'Skywalk', 'Cumeo2 (95)');

INSERT INTO t_glider (id, fk_user_id, manufacturer, model)
VALUES (52, @user_id, 'Advance', 'Iota (24)');

INSERT INTO t_glider (id, fk_user_id, manufacturer, model)
VALUES (53, @user_id, 'Ozone', 'Rush 5 (29)');

INSERT INTO t_glider (id, fk_user_id, manufacturer, model)
VALUES (54, @user_id, 'Nova', 'Phantom (27)');

INSERT INTO t_glider (id, fk_user_id, manufacturer, model)
VALUES (55, @user_id, 'Gin', 'Atlas 2 (26)');

-- Insert 100 flights with seasonal variations across all regions
INSERT INTO t_flight (id, date_time, duration, description, fk_user_id, fk_launch_site_id, fk_landing_site_id, fk_glider_id)
VALUES
-- Winter flights (December-February) - shorter durations, fewer flights
(56, '2023-12-15 12:30:00', 25, 'Short winter flight in stable conditions. Cold but clear with light winds.', @user_id, 2, 31, 51),
(57, '2023-12-22 13:15:00', 30, 'Quick Christmas week flight. Snow-covered landscape looked magical from above.', @user_id, 3, 32, 52),
(58, '2024-01-08 11:45:00', 20, 'Brief winter session. Limited thermals but good for maintaining currency.', @user_id, 1, 31, 53),
(59, '2024-01-18 14:00:00', 35, 'Decent winter conditions with some ridge lift. Beautiful snow-capped peaks all around.', @user_id, 2, 35, 51),
(60, '2024-02-05 12:20:00', 28, 'Cold but flyable day. Short flight to keep skills sharp during winter break.', @user_id, 13, 39, 54),
(61, '2024-02-14 13:30:00', 40, 'Valentine\'s Day flight! Clear skies and surprisingly good conditions for February.', @user_id, 1, 31, 55),
(62, '2024-02-25 11:00:00', 32, 'Late winter flight with improving conditions. Spring is starting to show signs.', @user_id, 16, 40, 52),

-- Spring flights (March-May) - increasing duration and frequency
(63, '2024-03-05 10:30:00', 55, 'First proper spring flight! Thermals starting to develop, great to be back in the air.', @user_id, 1, 31, 51),
(64, '2024-03-12 14:15:00', 70, 'Excellent spring conditions with building cumulus. Thermals getting stronger each week.', @user_id, 18, 42, 53),
(65, '2024-03-18 09:45:00', 85, 'Morning flight with perfect visibility. Alps crystal clear, thermals building nicely.', @user_id, 4, 35, 54),
(66, '2024-03-22 15:20:00', 45, 'Late afternoon flight. Wind picked up but managed some good thermal climbs.', @user_id, 14, 39, 52),
(67, '2024-03-28 11:30:00', 95, 'Outstanding spring day! Strong thermals, climbed to 2900m with excellent conditions.', @user_id, 1, 32, 55),
(68, '2024-04-03 08:30:00', 75, 'Early morning flight before work. Perfect start to the day with gentle thermals.', @user_id, 2, 31, 51),
(69, '2024-04-08 13:45:00', 105, 'Long spring flight with multiple thermal climbs. Conditions just keep getting better.', @user_id, 17, 44, 53),
(70, '2024-04-14 10:15:00', 80, 'Fantastic conditions for practicing cross-country techniques. Great thermal spacing.', @user_id, 8, 36, 54),
(71, '2024-04-19 16:00:00', 60, 'Evening flight with golden light. Thermals weakening but still very flyable.', @user_id, 22, 46, 52),
(72, '2024-04-25 12:20:00', 115, 'Epic spring flight! Climbed to cloud base multiple times, perfect thermal conditions.', @user_id, 1, 32, 55),
(73, '2024-05-02 09:00:00', 90, 'May Day flight with excellent visibility. Spring conditions at their finest.', @user_id, 25, 48, 51),
(74, '2024-05-08 14:30:00', 85, 'Strong thermals with good convergence. Practiced some advanced thermal techniques.', @user_id, 15, 41, 53),
(75, '2024-05-15 11:45:00', 120, 'Two-hour flight! Best thermal day so far this season, absolutely perfect conditions.', @user_id, 10, 35, 54),
(76, '2024-05-22 15:10:00', 75, 'Late spring flight with building cumulus. Conditions transitioning to summer patterns.', @user_id, 19, 42, 52),
(77, '2024-05-28 08:45:00', 100, 'Memorial Day flight. Early morning with light winds developing into excellent thermals.', @user_id, 6, 32, 55),

-- Summer flights (June-August) - longest durations, most frequent flights
(78, '2024-06-05 07:30:00', 150, 'Epic summer dawn patrol! 2.5 hours of perfect flying, thermals all the way to base.', @user_id, 1, 32, 51),
(79, '2024-06-10 10:15:00', 135, 'Outstanding summer conditions. Multiple climbs to 3200m, incredible visibility.', @user_id, 9, 36, 53),
(80, '2024-06-14 13:45:00', 110, 'Strong summer thermals with good spacing. Perfect for cross-country progression.', @user_id, 18, 43, 54),
(81, '2024-06-18 09:20:00', 125, 'Morning flight extended into epic thermal session. Summer flying at its best!', @user_id, 23, 47, 52),
(82, '2024-06-22 16:30:00', 95, 'Solstice flight! Long evening session with beautiful golden hour lighting.', @user_id, 13, 39, 55),
(83, '2024-06-26 08:00:00', 140, 'Early summer flight with fantastic conditions. Thermals firing from first launch.', @user_id, 2, 31, 51),
(84, '2024-06-30 12:15:00', 160, 'End of June epic! Nearly 3 hours of perfect thermal flying, cloud base at 3500m.', @user_id, 26, 48, 53),
(85, '2024-07-04 11:30:00', 145, 'Independence Day flight! Perfect summer conditions with strong, consistent thermals.', @user_id, 11, 35, 54),
(86, '2024-07-08 07:45:00', 130, 'Dawn patrol success! Early morning thermals developed beautifully throughout flight.', @user_id, 20, 42, 52),
(87, '2024-07-12 14:20:00', 115, 'Hot summer day with strong thermals. Climbed efficiently to cloud base multiple times.', @user_id, 24, 46, 55),
(88, '2024-07-16 09:45:00', 155, 'Peak summer conditions! 2.5+ hours of incredible flying, thermals everywhere.', @user_id, 27, 49, 51),
(89, '2024-07-20 15:15:00', 105, 'Late afternoon summer flight. Strong thermals persisting well into evening.', @user_id, 29, 44, 53),
(90, '2024-07-24 08:30:00', 135, 'Morning flight that turned into cross-country adventure. Summer thermals are amazing!', @user_id, 5, 33, 54),
(91, '2024-07-28 13:00:00', 120, 'Perfect summer thermal day. Consistent lift and great cloud development.', @user_id, 12, 37, 52),
(92, '2024-08-02 10:00:00', 140, 'August flying at its finest! Strong thermals, clear skies, perfect conditions.', @user_id, 1, 31, 55),
(93, '2024-08-06 16:45:00', 110, 'Late summer afternoon with persistent thermals. Golden hour flying was spectacular.', @user_id, 21, 45, 51),
(94, '2024-08-10 09:15:00', 150, 'Epic summer flight! Climbed to 3400m multiple times, incredible Alpine views.', @user_id, 4, 36, 53),
(95, '2024-08-14 12:30:00', 125, 'Mid-August perfection. Strong thermals with excellent convergence zones.', @user_id, 28, 50, 54),
(96, '2024-08-18 07:00:00', 165, 'Dawn to late morning epic! Nearly 3 hours of perfect thermal flying.', @user_id, 7, 34, 52),
(97, '2024-08-22 14:45:00', 115, 'Late summer flight with great thermal activity. Conditions still very strong.', @user_id, 30, 45, 55),
(98, '2024-08-26 11:20:00', 135, 'End of August flight with excellent thermals. Summer conditions holding strong.', @user_id, 16, 40, 51),
(99, '2024-08-30 08:45:00', 145, 'Final August flight! Perfect summer send-off with incredible thermal conditions.', @user_id, 1, 32, 53),

-- Fall flights (September-November) - moderate durations, good conditions
(100, '2024-09-03 10:30:00', 95, 'Early fall flight with stable thermals. Autumn colors starting to show below.', @user_id, 2, 35, 54),
(101, '2024-09-07 13:15:00', 85, 'September flying still excellent. Thermals consistent but not as strong as summer.', @user_id, 14, 39, 52),
(102, '2024-09-12 09:45:00', 105, 'Beautiful fall conditions with clear air. Visibility incredible with crisp atmosphere.', @user_id, 18, 42, 55),
(103, '2024-09-16 15:20:00', 75, 'Afternoon fall flight. Thermals weakening but still very enjoyable flying.', @user_id, 22, 46, 51),
(104, '2024-09-21 11:00:00', 90, 'Autumn equinox flight! Perfect fall weather with stunning foliage below.', @user_id, 3, 31, 53),
(105, '2024-09-25 14:30:00', 80, 'Late September with good thermal activity. Fall flying season in full swing.', @user_id, 19, 43, 54),
(106, '2024-09-29 10:15:00', 100, 'End of September flight. Still getting good thermals and excellent visibility.', @user_id, 8, 37, 52),
(107, '2024-10-03 12:45:00', 70, 'October flying with moderate thermals. Autumn colors spectacular from altitude.', @user_id, 15, 41, 55),
(108, '2024-10-08 13:30:00', 85, 'Mid-fall flight with decent thermal activity. Crisp air and beautiful views.', @user_id, 6, 33, 51),
(109, '2024-10-12 11:30:00', 75, 'Columbus Day flight! Fall conditions with moderate but reliable thermals.', @user_id, 20, 44, 53),
(110, '2024-10-16 14:15:00', 65, 'Mid-October with weakening thermals but still flyable conditions.', @user_id, 23, 47, 54);

-- Insert IGC data first
INSERT INTO t_igc_data (id, uploaded_at, file_name, file_size, checksum, data)
VALUES (111, '2024-10-21 11:30:00', '2024-10-21_autumn_flight.igc', 15847, 'a1b2c3d4e5f6789012345678901234567890abcd',
'AXXX IGC Logger
HFDTE211024
HFFXA500
HFPLTPILOTINCHARGE:John Doe
HFGTYGLIDERTYPE:Paraglider EN-B
HFGIDGLIDERID:ABC-123
HFDTM100GPSDATUM:WGS-1984
HFRFWFIRMWAREVERSION:1.0
HFRHWHARDWAREVERSION:1.0
HFFTYFRTYPE:Logger
HFGPSRECEIVER:Built-in
HFPRSPRESSALTSENSOR:Built-in
HFCIDCOMPETITIONID:
HFCCLCOMPETITIONCLASS:
B1045004715123N00824567EA0089000894
B1045154715124N00824568EA0089100895
B1045304715125N00824569EA0089200896
B1045454715126N00824570EA0089300897
B1046004715127N00824571EA0089400898
B1046154715128N00824572EA0089500899
B1046304715129N00824573EA0089600900
B1046454715130N00824574EA0089700901
B1047004715131N00824575EA0089800902
B1047154715132N00824576EA0089900903
B1047304715133N00824577EA0090000904
B1047454715134N00824578EA0090100905
B1048004715135N00824579EA0090200906
B1048154715136N00824580EA0090300907
B1048304715137N00824581EA0090400908
B1125004715180N00824620EA0095001050
G12345678901234567890ABCDEF
');

-- Insert flight with reference to IGC data
INSERT INTO t_flight (id, date_time, duration, description, fk_user_id, fk_launch_site_id, fk_landing_site_id, fk_glider_id, fk_igc_data_id)
VALUES (111, '2024-10-21 10:45:00', 80, 'Late October flight. Thermals getting lighter but autumn flying still great.', @user_id, 13, 40, 52, 111);

UPDATE hibernate_sequence SET next_val = 112;