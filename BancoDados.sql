DROP DATABASE IF EXISTS `viagens`;
CREATE DATABASE IF NOT EXISTS `viagens`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE `viagens`;

DROP TABLE IF EXISTS `conexoes`;
DROP TABLE IF EXISTS `destinos`;

CREATE TABLE `destinos` (
    `id` CHAR(36) NOT NULL DEFAULT (UUID()),
    `nome` VARCHAR(120) NOT NULL,
    `latitude` DECIMAL(10,6) NOT NULL,
    `longitude` DECIMAL(10,6) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `conexoes` (
    `id` CHAR(36) NOT NULL DEFAULT (UUID()),
    `origem_id` CHAR(36) NOT NULL,
    `destino_id` CHAR(36) NOT NULL,
    `peso_km` DOUBLE NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_conexoes_origem` (`origem_id`),
    KEY `idx_conexoes_destino` (`destino_id`),
    CONSTRAINT `fk_conexoes_origem` FOREIGN KEY (`origem_id`) REFERENCES `destinos` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_conexoes_destino` FOREIGN KEY (`destino_id`) REFERENCES `destinos` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `destinos` (`id`, `nome`, `latitude`, `longitude`) VALUES
    ('53051df7-2e78-4d4c-8392-87342fc19b92', 'São Paulo', -23.550500, -46.633300),
    ('601ddf15-16a5-464f-b98b-26ad3dd807cf', 'New York', 40.712800, -74.006000),
    ('eedf4f7d-58d3-4ad8-ba08-4be1df7f17ce', 'Tokyo', 35.676400, 139.650000),
    ('af1be3e8-6c6d-4b59-973c-48b3a7631f68', 'Berlin', 52.520000, 13.405000),
    ('a7b3b8f3-f1a7-4c92-9f2b-18558a7d4490', 'Moscow', 55.755800, 37.617300),
    ('0341822d-5c1f-4d95-9f99-6e99d4a5d0e4', 'Sydney', -33.868800, 151.209300),
    ('b9599aee-f4f0-4df9-b830-09ee2c767235', 'Cidade do Cabo', -33.924900, 18.424100),
    ('e00c0be3-dc99-45fd-9ce7-43d4eb4d4646', 'Buenos Aires', -34.603700, -58.381600),
    ('7c77d0b4-5b29-4f6e-996a-38fd0d258e6f', 'Toronto', 43.653200, -79.383200),
    ('df4b80fb-8d2a-4c9d-b5d3-12f6c988531d', 'Nova Délhi', 28.613900, 77.209000);

INSERT INTO `conexoes` (`id`, `origem_id`, `destino_id`, `peso_km`) VALUES
    ('87e52b3b-9e1b-4d1a-90ed-3217e4d4e1e1', '53051df7-2e78-4d4c-8392-87342fc19b92', 'e00c0be3-dc99-45fd-9ce7-43d4eb4d4646', 1671.0), -- São Paulo <-> Buenos Aires
    ('f5186a84-8b5a-4f46-9716-27be98bb6ef2', '53051df7-2e78-4d4c-8392-87342fc19b92', '601ddf15-16a5-464f-b98b-26ad3dd807cf', 7686.0), -- São Paulo <-> New York
    ('e21d7712-f3a0-4200-a115-171f04dcdefa', '53051df7-2e78-4d4c-8392-87342fc19b92', 'b9599aee-f4f0-4df9-b830-09ee2c767235', 6450.0), -- São Paulo <-> Cidade do Cabo
    ('4f511b82-ef20-4890-94c7-6d219882d0e2', '601ddf15-16a5-464f-b98b-26ad3dd807cf', '7c77d0b4-5b29-4f6e-996a-38fd0d258e6f', 558.0),   -- New York <-> Toronto
    ('4982a563-1b41-4afd-9c7b-5aa67d903785', '601ddf15-16a5-464f-b98b-26ad3dd807cf', 'af1be3e8-6c6d-4b59-973c-48b3a7631f68', 6380.0), -- New York <-> Berlin
    ('9b20cf74-4a9b-46ea-92b3-3e3c5ad384f2', 'af1be3e8-6c6d-4b59-973c-48b3a7631f68', 'a7b3b8f3-f1a7-4c92-9f2b-18558a7d4490', 1608.0), -- Berlin <-> Moscow
    ('53c0abc4-5409-452a-94e5-6ddde3f5d973', 'eedf4f7d-58d3-4ad8-ba08-4be1df7f17ce', '0341822d-5c1f-4d95-9f99-6e99d4a5d0e4', 7825.0), -- Tokyo <-> Sydney
    ('fcb43868-69c0-4f1e-abfc-70bbce58c780', 'eedf4f7d-58d3-4ad8-ba08-4be1df7f17ce', 'df4b80fb-8d2a-4c9d-b5d3-12f6c988531d', 5856.0), -- Tokyo <-> Nova Délhi
    ('88f59c62-af04-48bc-8aba-ff7274d06606', '0341822d-5c1f-4d95-9f99-6e99d4a5d0e4', 'b9599aee-f4f0-4df9-b830-09ee2c767235', 11012.0), -- Sydney <-> Cidade do Cabo
    ('5e458c3c-bc10-4bd3-b3c4-94d33456b351', 'e00c0be3-dc99-45fd-9ce7-43d4eb4d4646', 'eedf4f7d-58d3-4ad8-ba08-4be1df7f17ce', 18378.0), -- Buenos Aires <-> Tokyo
    ('a26edb10-35fe-4cf6-86ff-a566ea4cf0af', 'a7b3b8f3-f1a7-4c92-9f2b-18558a7d4490', 'eedf4f7d-58d3-4ad8-ba08-4be1df7f17ce', 7476.0), -- Moscow <-> Tokyo
    ('ca8bd6b8-7f4c-4fd6-94fc-642c7323f5b3', 'af1be3e8-6c6d-4b59-973c-48b3a7631f68', 'df4b80fb-8d2a-4c9d-b5d3-12f6c988531d', 5876.0); -- Berlin <-> Nova Délhi
