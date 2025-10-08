CREATE SCHEMA IF NOT EXISTS registrosDB DEFAULT CHARACTER SET utf8;
USE registrosDB;

CREATE TABLE IF NOT EXISTS `registrosDB`.`Encargados` (
    `nombre_encargado` VARCHAR(60) NOT NULL,
    `hash_pw` CHAR(60) NOT NULL,
    PRIMARY KEY (`nombre_encargado`)
);

CREATE TABLE IF NOT EXISTS `registrosDB`.`Empleados` (
    `nombre_empleado` VARCHAR(60) NOT NULL,
    `hash_pw` CHAR(60) NOT NULL,
    PRIMARY KEY (`nombre_empleado`)
);

CREATE TABLE IF NOT EXISTS `registrosDB`.`Registros` (
    `id_registro` INT AUTO_INCREMENT,
    `nombre_empleado` VARCHAR(60) NOT NULL,
    `fecha` DATE NOT NULL,
    `hora` TIME NOT NULL,
    `tipo` ENUM('Entrada', 'Salida') NOT NULL,
    `minutos_acumulados` INT,
    PRIMARY KEY (`id_registro`),
    FOREIGN KEY (`nombre_empleado`) REFERENCES `registrosDB`.`Empleados`(`nombre_empleado`) 
        ON DELETE CASCADE  -- Elimina registros de entradas si se elimina el empleado
);

CREATE TABLE IF NOT EXISTS `registrosDB`.`Notas` (
    `id_nota` INT AUTO_INCREMENT,
    `id_registro` INT NOT NULL,
    `autor` VARCHAR(60) NOT NULL,
    `fecha` DATE NOT NULL,
    `hora` TIME NOT NULL,
    `texto` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id_nota`),
    FOREIGN KEY (`id_registro`) REFERENCES `registrosDB`.`Registros`(`id_registro`)
        ON DELETE CASCADE  -- Elimina notas si se elimina el registro
);
