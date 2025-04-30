package org.itson.ReportesAnomalias.collections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Document(collection = "anomalias")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Anomalia {

    @Id
    private ObjectId _id;
    private List<Lectura> lecturas;
    private Date fechaHora;
    private String causa;

}
