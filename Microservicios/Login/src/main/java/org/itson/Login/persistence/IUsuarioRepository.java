package org.itson.Login.persistence;

import org.bson.types.ObjectId;
import org.itson.Login.collections.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUsuarioRepository extends MongoRepository<Usuario, ObjectId> {

    Optional<Usuario> findByUsername(String username);

}