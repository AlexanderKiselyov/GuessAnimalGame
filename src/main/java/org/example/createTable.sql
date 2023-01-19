CREATE TABLE IF NOT EXISTS "animal_tree" (
    "id" BIGINT NOT NULL,
    "question" VARCHAR(255) NOT NULL,
    "animal" VARCHAR(255) NOT NULL,
    CONSTRAINT "pk_animal_tree" PRIMARY KEY ("id")
);