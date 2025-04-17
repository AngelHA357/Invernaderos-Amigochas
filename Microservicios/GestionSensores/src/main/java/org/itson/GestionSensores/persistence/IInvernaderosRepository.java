package org.itson.GestionSensores.persistence;

import org.itson.GestionSensores.entities.Invernadero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interfaz que define los métodos de acceso a datos disponibles para interactuar con la tabla de invernaderos de la base de datos. Como hereda de JpaRepository, ya cuenta con métodos predefinidos (findAll, findById, deleteById...)
 */
@Repository
public interface IInvernaderosRepository extends JpaRepository<Invernadero, Long> {
    /**
     * Método que busca un invernadero dado su nombre.
     * @param nombre Nombre del invernadero a buscar.
     * @return El invernadero encontrado. Null en caso contrario.
     */
    Optional<Invernadero> findByNombreIgnoreCase(String nombre);
}
