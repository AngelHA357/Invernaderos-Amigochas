package org.itson.Login.collections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String username;

    private String password;
    
    private String role;  // ADMIN, OPERATOR, ANALYST
    
    private String nombre;
    
    private String apellido;
    
    private String email;

}
