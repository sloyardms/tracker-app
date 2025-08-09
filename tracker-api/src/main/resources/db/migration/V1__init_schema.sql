-- ========================================
-- V1__init_schema.sql
-- Flyway Migration Script
-- Description: Create bookmarks schema with users, groups, tags, bookmarks, notes, images, and relations
-- ========================================

-- Create users table
CREATE TABLE users (
    user_uuid UUID NOT NULL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    dark_mode BOOLEAN NOT NULL DEFAULT FALSE,
    bookmark_keep_original_image BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP(0) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(0) WITH TIME ZONE NOT NULL,
    CONSTRAINT users_username_unique UNIQUE (username)
);

-- Create groups table
CREATE TABLE groups (
    group_uuid UUID NOT NULL PRIMARY KEY,
    user_uuid UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NULL,
    created_at TIMESTAMP(0) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(0) WITH TIME ZONE NOT NULL,
    CONSTRAINT groups_user_uuid_name_unique UNIQUE (user_uuid, name)
);

-- Create tags table
CREATE TABLE tags (
    tag_uuid UUID NOT NULL PRIMARY KEY,
    user_uuid UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(0) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(0) WITH TIME ZONE NOT NULL,
    CONSTRAINT tags_user_uuid_name_unique UNIQUE (user_uuid, name)
);

CREATE INDEX tags_user_uuid_index ON tags(user_uuid);

-- Create bookmarks table
CREATE TABLE bookmarks (
    bookmark_uuid UUID NOT NULL PRIMARY KEY,
    user_uuid UUID NOT NULL,
    group_uuid UUID NULL,
    title TEXT NULL,
    url TEXT NULL,
    description TEXT NULL,
    favorited BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP(0) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(0) WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP(0) WITH TIME ZONE NULL,
    CONSTRAINT bookmarks_user_uuid_title_unique UNIQUE (user_uuid, title),
    CONSTRAINT bookmarks_user_uuid_url_unique UNIQUE (user_uuid, url)
);

CREATE INDEX bookmarks_user_uuid_index ON bookmarks(user_uuid);
CREATE INDEX bookmarks_group_uuid_index ON bookmarks(group_uuid);

-- Create bookmark_tags table
CREATE TABLE bookmark_tags (
    bookmark_uuid UUID NOT NULL,
    tag_uuid UUID NOT NULL,
    CONSTRAINT bookmark_tags_bookmark_uuid_tag_uuid_unique UNIQUE (bookmark_uuid, tag_uuid)
);

-- Create bookmark_notes table
CREATE TABLE bookmark_notes (
    note_uuid UUID NOT NULL PRIMARY KEY,
    bookmark_uuid UUID NOT NULL,
    note TEXT NOT NULL,
    created_at TIMESTAMP(0) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(0) WITH TIME ZONE NOT NULL
);

CREATE INDEX bookmark_notes_bookmark_uuid_index ON bookmark_notes(bookmark_uuid);

-- Create bookmark_images table
CREATE TABLE bookmark_images (
    image_uuid UUID NOT NULL PRIMARY KEY,
    bookmark_uuid UUID NOT NULL,
    thumbnail_path VARCHAR(255) NOT NULL,
    thumbnail_mimetype VARCHAR(255) NOT NULL,
    original_image_path VARCHAR(255) NULL,
    original_image_mimetype VARCHAR(255) NULL,
    created_at TIMESTAMP(0) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(0) WITH TIME ZONE NOT NULL
);

CREATE INDEX bookmark_images_bookmark_uuid_index ON bookmark_images(bookmark_uuid);

-- Create bookmark_notes_images table
CREATE TABLE bookmark_notes_images (
    image_uuid UUID NOT NULL PRIMARY KEY,
    note_uuid UUID NOT NULL,
    thumbnail_path VARCHAR(255) NOT NULL,
    thumbnail_mimetype VARCHAR(255) NOT NULL,
    original_image_path VARCHAR(255) NULL,
    original_image_mimetype VARCHAR(255) NULL,
    created_at TIMESTAMP(0) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(0) WITH TIME ZONE NOT NULL
);

CREATE INDEX bookmark_notes_images_note_uuid_index ON bookmark_notes_images(note_uuid);

-- Create tag_usage_summary table
CREATE TABLE tag_usage_summary (
    user_uuid UUID NOT NULL,
    group_uuid UUID NOT NULL,
    tag_uuid UUID NOT NULL,
    bookmark_count INTEGER NOT NULL,
    PRIMARY KEY (user_uuid, group_uuid, tag_uuid)
);

ALTER TABLE tag_usage_summary
    ADD CONSTRAINT bookmark_count_non_negative CHECK (bookmark_count >= 0);

-- ========================================
-- Add Foreign Key Constraints
-- ========================================

ALTER TABLE groups
    ADD CONSTRAINT groups_user_uuid_foreign FOREIGN KEY (user_uuid) REFERENCES users(user_uuid);

ALTER TABLE tags
    ADD CONSTRAINT tags_user_uuid_foreign FOREIGN KEY (user_uuid) REFERENCES users(user_uuid);

ALTER TABLE bookmarks
    ADD CONSTRAINT bookmarks_user_uuid_foreign FOREIGN KEY (user_uuid) REFERENCES users(user_uuid);
ALTER TABLE bookmarks
    ADD CONSTRAINT bookmarks_group_uuid_foreign FOREIGN KEY (group_uuid) REFERENCES groups(group_uuid) ON DELETE SET NULL;

ALTER TABLE bookmark_tags
    ADD CONSTRAINT bookmark_tags_bookmark_uuid_foreign FOREIGN KEY (bookmark_uuid) REFERENCES bookmarks(bookmark_uuid);
ALTER TABLE bookmark_tags
    ADD CONSTRAINT bookmark_tags_tag_uuid_foreign FOREIGN KEY (tag_uuid) REFERENCES tags(tag_uuid);

ALTER TABLE tag_usage_summary
    ADD CONSTRAINT tag_usage_summary_user_uuid_foreign FOREIGN KEY (user_uuid) REFERENCES users(user_uuid);
ALTER TABLE tag_usage_summary
    ADD CONSTRAINT tag_usage_summary_group_uuid_foreign FOREIGN KEY (group_uuid) REFERENCES groups(group_uuid);
ALTER TABLE tag_usage_summary
    ADD CONSTRAINT tag_usage_summary_tag_uuid_foreign FOREIGN KEY (tag_uuid) REFERENCES tags(tag_uuid);

ALTER TABLE bookmark_notes
    ADD CONSTRAINT bookmark_notes_bookmark_uuid_foreign FOREIGN KEY (bookmark_uuid) REFERENCES bookmarks(bookmark_uuid) ON DELETE CASCADE;

ALTER TABLE bookmark_images
    ADD CONSTRAINT bookmark_images_bookmark_uuid_foreign FOREIGN KEY (bookmark_uuid) REFERENCES bookmarks(bookmark_uuid) ON DELETE CASCADE;

ALTER TABLE bookmark_notes_images
    ADD CONSTRAINT bookmark_notes_images_note_uuid_foreign FOREIGN KEY (note_uuid) REFERENCES bookmark_notes(note_uuid) ON DELETE CASCADE;
