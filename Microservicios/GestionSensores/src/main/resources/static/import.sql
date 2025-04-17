INSERT IGNORE INTO invernaderos (nombre)
VALUES ('Invernadero A'),
       ('Invernadero B'),
       ('Invernadero C'),
       ('Invernadero E'),
       ('Invernadero D');

INSERT IGNORE INTO sensores (mac_address, marca, modelo, invernadero_id)
VALUES ('AA:BB:CC:DD:EE:FF', 'SensorTech', 'ST-100', 1),
       ('11:22:33:44:55:66', 'EcoSense', 'ES-200', 2),
       ('AA:11:BB:22:CC:33', 'TempTech', 'T-300', 3),
       ('77:88:99:AA:BB:CC', 'AgroSense', 'AS-400', 4),
       ('66:55:44:33:22:11', 'GreenGrow', 'GG-500', 5);