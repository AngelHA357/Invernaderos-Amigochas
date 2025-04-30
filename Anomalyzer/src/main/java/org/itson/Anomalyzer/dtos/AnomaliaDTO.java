package org.itson.Anomalyzer.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.itson.Anomalyzer.collections.Lectura;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnomaliaDTO implements Serializable {
    private String idAnomalia;
    private List<LecturaDTO> lecturas;
    private String causa;
}
