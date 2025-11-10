USE registrosDB;

-- Quitar la restricción existente en Registros
ALTER TABLE `Registros`
DROP FOREIGN KEY `Registros_ibfk_1`;

-- Crear la nueva con ON DELETE CASCADE y ON UPDATE CASCADE
ALTER TABLE `Registros`
ADD CONSTRAINT `Registros_ibfk_1`
FOREIGN KEY (`nombre_empleado`)
REFERENCES `Empleados` (`nombre_empleado`)
ON DELETE CASCADE
ON UPDATE CASCADE;

-- Quitar la restricción existente en Notas
ALTER TABLE `Notas`
DROP FOREIGN KEY `Notas_ibfk_1`;

-- Crear la nueva con ON DELETE CASCADE y ON UPDATE CASCADE
ALTER TABLE `Notas`
ADD CONSTRAINT `Notas_ibfk_1`
FOREIGN KEY (`id_registro`)
REFERENCES `Registros` (`id_registro`)
ON DELETE CASCADE
ON UPDATE CASCADE;
