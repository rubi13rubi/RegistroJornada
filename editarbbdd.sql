USE registrosDB;

-- Quitar la restriccion existente en Registros
ALTER TABLE `Registros`
DROP FOREIGN KEY `Registros_ibfk_1`;

-- Crear la nueva con ON DELETE CASCADE y ON UPDATE CASCADE
ALTER TABLE `Registros`
ADD CONSTRAINT `Registros_ibfk_1`
FOREIGN KEY (`nombre_empleado`)
REFERENCES `Empleados` (`nombre_empleado`)
ON DELETE CASCADE
ON UPDATE CASCADE;

-- Quitar la restriccion existente en Notas
ALTER TABLE `Notas`
DROP FOREIGN KEY `Notas_ibfk_1`;

-- Crear la nueva con ON DELETE CASCADE y ON UPDATE CASCADE
ALTER TABLE `Notas`
ADD CONSTRAINT `Notas_ibfk_1`
FOREIGN KEY (`id_registro`)
REFERENCES `Registros` (`id_registro`)
ON DELETE CASCADE
ON UPDATE CASCADE;

-- Columna de stamp para Empleados
ALTER TABLE `Empleados` 
ADD COLUMN `cookie_stamp` VARCHAR(36) NULL AFTER `hash_pw`;

-- Generar uuid para Empleados
UPDATE `Empleados` 
SET `cookie_stamp` = UUID() 
WHERE `cookie_stamp` IS NULL;

-- Hacer not null stamp de Empleados
ALTER TABLE `Empleados` 
MODIFY COLUMN `cookie_stamp` VARCHAR(36) NOT NULL;


-- Columna de stamp para encargados
ALTER TABLE `Encargados` 
ADD COLUMN `cookie_stamp` VARCHAR(36) NULL AFTER `hash_pw`;

-- Generar uuid para Encargados
UPDATE `Encargados` 
SET `cookie_stamp` = UUID() 
WHERE `cookie_stamp` IS NULL;

-- Hacer not null stamp de Encargados
ALTER TABLE `Encargados` 
MODIFY COLUMN `cookie_stamp` VARCHAR(36) NOT NULL;