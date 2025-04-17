package org.itson.GestionSensores.persistence;

import org.itson.GestionSensores.entities.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interfaz que define los métodos de acceso a datos disponibles para interactuar con la base de datos de gestión de
 * sensores. Como hereda de JpaRepository, ya cuenta con métodos predefinidos (findAll, findById, deleteById...)
 */
@Repository
public interface IGestionSensoresRepository extends JpaRepository<Sensor, Long> {

}
