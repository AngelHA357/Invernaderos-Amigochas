package org.itson.GestionSensores.collections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "invernaderos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Invernadero {
    @Id
    private String _id;
    @Indexed(unique = true)
    private String nombre;
    private List<String> sectores;
    private List<String> filas;
}