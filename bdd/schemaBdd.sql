-- ============================================================================
-- ChallengeAI - Script de création de base de données PostgreSQL
-- Version: 1.1 - MVP avec pseudo
-- Auteur: Clarence Dugain
-- Date: 2025-03-16
-- ============================================================================

-- Suppression des tables existantes (pour réinitialisation)
DROP TABLE IF EXISTS user_achievement CASCADE;
DROP TABLE IF EXISTS notification CASCADE;
DROP TABLE IF EXISTS daily_challenge CASCADE;
DROP TABLE IF EXISTS achievement CASCADE;
DROP TABLE IF EXISTS user_preferences CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TYPE IF EXISTS challenge_status CASCADE;
DROP TYPE IF EXISTS notification_type CASCADE;

-- ============================================================================
-- TYPES ENUM
-- ============================================================================

-- Statuts possibles pour un défi
CREATE TYPE challenge_status AS ENUM ('pending', 'completed', 'skipped');

-- Types de notifications
CREATE TYPE notification_type AS ENUM ('reminder', 'achievement', 'streak');

-- ============================================================================
-- TABLE: users
-- ============================================================================
-- Stocke les comptes utilisateurs
-- Note: "users" au pluriel pour éviter le mot réservé "user"
-- ============================================================================

CREATE TABLE users (
                       id UUID DEFAULT gen_random_uuid(),
                       email VARCHAR(255) NOT NULL,
                       pseudo VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,  -- Hash bcrypt/argon2 (60+ caractères)
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                       PRIMARY KEY (id),
                       UNIQUE (email),
                       UNIQUE (pseudo)
);

-- Index pour optimiser la recherche par email (connexion)
CREATE INDEX idx_users_email ON users(email);

-- Index pour optimiser la recherche par pseudo
CREATE INDEX idx_users_pseudo ON users(pseudo);  -- ✅ AJOUTÉ

-- ============================================================================
-- TABLE: user_preferences
-- ============================================================================
-- Stocke les préférences personnalisées de chaque utilisateur
-- Relation 1-1 avec users
-- ============================================================================

CREATE TABLE user_preferences (
                                  id UUID DEFAULT gen_random_uuid(),
                                  user_id UUID NOT NULL,
                                  interests JSONB,  -- Ex: ["sport", "musique", "technologie"]
                                  goals JSONB,      -- Ex: ["perdre du poids", "apprendre l'anglais"]
                                  daily_time INTEGER DEFAULT 10,  -- Temps disponible quotidien en minutes
                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                  PRIMARY KEY (id),
                                  UNIQUE (user_id),  -- Un user ne peut avoir qu'une seule ligne de préférences
                                  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================================
-- TABLE: daily_challenge
-- ============================================================================
-- Stocke les défis quotidiens générés par IA pour chaque utilisateur
-- Relation N-1 avec users (un user peut avoir plusieurs challenges)
-- ============================================================================

CREATE TABLE daily_challenge (
                                 id UUID DEFAULT gen_random_uuid(),
                                 user_id UUID NOT NULL,
                                 challenge_text TEXT NOT NULL,  -- Texte du défi généré par IA (pas de limite)
                                 challenge_date DATE NOT NULL,  -- Date du défi
                                 status challenge_status NOT NULL DEFAULT 'pending',
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 completed_at TIMESTAMP,  -- NULL tant que non complété

                                 PRIMARY KEY (id),
                                 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Index composite pour requête fréquente: "défis du jour pour un user"
CREATE INDEX idx_daily_challenge_user_date ON daily_challenge(user_id, challenge_date);

-- Index pour filtrer par statut (stats, historique)
CREATE INDEX idx_daily_challenge_status ON daily_challenge(status);

-- ============================================================================
-- TABLE: achievement
-- ============================================================================
-- Catalogue des badges/récompenses débloquables
-- ============================================================================

CREATE TABLE achievement (
                             id UUID DEFAULT gen_random_uuid(),
                             name VARCHAR(100) NOT NULL,
                             description TEXT,
                             icon VARCHAR(255),  -- Nom d'icône ou URL
                             unlock_criteria TEXT,  -- Critères de déblocage en texte clair

                             PRIMARY KEY (id),
                             UNIQUE (name)  -- Pas deux achievements avec le même nom
);

-- ============================================================================
-- TABLE: user_achievement
-- ============================================================================
-- Table de liaison N-N entre users et achievements
-- Stocke quels users ont débloqué quels achievements
-- ============================================================================

CREATE TABLE user_achievement (
                                  user_id UUID NOT NULL,
                                  achievement_id UUID NOT NULL,
                                  unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                  PRIMARY KEY (user_id, achievement_id),  -- Clé composite
                                  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                  FOREIGN KEY (achievement_id) REFERENCES achievement(id) ON DELETE CASCADE
);

-- Index pour requête: "tous les achievements d'un user"
CREATE INDEX idx_user_achievement_user ON user_achievement(user_id);

-- Index pour requête: "tous les users qui ont un achievement donné"
CREATE INDEX idx_user_achievement_achievement ON user_achievement(achievement_id);

-- ============================================================================
-- TABLE: notification
-- ============================================================================
-- Stocke les notifications envoyées aux utilisateurs
-- ============================================================================

CREATE TABLE notification (
                              id UUID DEFAULT gen_random_uuid(),
                              user_id UUID NOT NULL,
                              message TEXT NOT NULL,
                              type notification_type NOT NULL,
                              sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              read_at TIMESTAMP,  -- NULL = non lu

                              PRIMARY KEY (id),
                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Index pour requête: "notifications non lues d'un user"
CREATE INDEX idx_notification_user_read ON notification(user_id, read_at);

-- ============================================================================
-- DONNÉES INITIALES - Achievements par défaut
-- ============================================================================

INSERT INTO achievement (name, description, icon, unlock_criteria) VALUES
                                                                       ('Premier pas', 'Complète ton premier défi', '🎯', 'Complete 1 challenge'),
                                                                       ('Régularité', 'Complète 3 défis en 3 jours consécutifs', '📅', 'Streak of 3 days'),
                                                                       ('Semaine parfaite', '7 jours de streak consécutifs', '🔥', 'Streak of 7 days'),
                                                                       ('Marathonien', 'Complète 30 défis au total', '🏃', 'Complete 30 challenges'),
                                                                       ('Centurion', 'Complète 100 défis au total', '💯', 'Complete 100 challenges');

-- ============================================================================
-- VUES UTILES (optionnel mais pratique)
-- ============================================================================

-- Vue pour obtenir les statistiques d'un user
CREATE OR REPLACE VIEW user_stats AS
SELECT
    u.id AS user_id,
    u.email,
    u.pseudo,  -- ✅ AJOUTÉ
    COUNT(dc.id) FILTER (WHERE dc.status = 'completed') AS total_completed,
    COUNT(dc.id) FILTER (WHERE dc.status = 'skipped') AS total_skipped,
    COUNT(dc.id) FILTER (WHERE dc.status = 'pending') AS total_pending,
    COUNT(DISTINCT dc.challenge_date) FILTER (WHERE dc.status = 'completed') AS active_days,
    COUNT(ua.achievement_id) AS achievements_unlocked
FROM users u
         LEFT JOIN daily_challenge dc ON u.id = dc.user_id
         LEFT JOIN user_achievement ua ON u.id = ua.user_id
GROUP BY u.id, u.email, u.pseudo;  -- ✅ MODIFIÉ

-- ============================================================================
-- COMMENTAIRES SUR LES TABLES
-- ============================================================================

COMMENT ON TABLE users IS 'Comptes utilisateurs de l''application';
COMMENT ON COLUMN users.pseudo IS 'Nom d''utilisateur unique visible publiquement';  -- ✅ AJOUTÉ
COMMENT ON TABLE user_preferences IS 'Préférences personnalisées pour génération IA';
COMMENT ON TABLE daily_challenge IS 'Défis quotidiens générés par IA';
COMMENT ON TABLE achievement IS 'Catalogue des badges débloquables';
COMMENT ON TABLE user_achievement IS 'Association users ↔ achievements';
COMMENT ON TABLE notification IS 'Notifications push/in-app';

-- ============================================================================
-- FIN DU SCRIPT
-- ============================================================================

-- Vérification
SELECT
    'Tables créées avec succès!' AS status,
    COUNT(*) AS nombre_tables
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_type = 'BASE TABLE';