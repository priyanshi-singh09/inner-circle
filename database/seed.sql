-- Inner Circle V1 seed data
-- Safe, non-production sample data for local development.

INSERT INTO circles (name, min_age, max_age, description) VALUES
('Firefly Circle', 13, 15, 'Ages 13–15'),
('Night Owl Circle', 16, 18, 'Ages 16–18'),
('Wildflower Circle', 19, 22, 'Ages 19–22'),
('Compass Circle', 23, 27, 'Ages 23–27'),
('Anchor Grove', 28, 35, 'Ages 28–35'),
('Evergreen Circle', 36, 50, 'Ages 36–50'),
('Lighthouse Circle', 51, NULL, 'Ages 51+')
ON CONFLICT (name) DO NOTHING;

INSERT INTO topics (name, description) VALUES
('School & Learning', 'Thoughts about school, learning, exams, and growth.'),
('Work & Career', 'Work, career goals, opportunities, and professional life.'),
('Friendship', 'Thoughts about friendships and human connection.'),
('Family', 'Everyday thoughts and experiences involving family.'),
('Creativity', 'Writing, art, photography, ideas, and creative expression.'),
('Life', 'Everyday reflections, experiences, and moments.'),
('Small Wins', 'Things that made you feel proud, grateful, or happy.')
ON CONFLICT (name) DO NOTHING;
