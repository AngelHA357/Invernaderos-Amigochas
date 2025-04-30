package org.itson.ReportesAnomalias.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnomaliaDTO {

    private String idAnomalia;
    private List<LecturaDTO> lecturas;
    private Date fechaHora;
    private String causa;

}
