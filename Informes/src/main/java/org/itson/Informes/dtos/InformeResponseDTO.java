package org.itson.Informes.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformeResponseDTO {
    private String tituloDelInforme;
    private Date fechaGeneracion;
    private List<String> idsInvernaderoFiltro;
    private Date fechaInicioFiltro;
    private Date fechaFinFiltro;
    private List<String> magnitudesFiltro;
    private List<LecturaDTO> lecturasEnriquecidas; // Usaremos tu LecturaDTO existente
}
