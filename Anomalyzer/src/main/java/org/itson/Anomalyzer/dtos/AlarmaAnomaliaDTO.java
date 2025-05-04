package org.itson.Anomalyzer.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmaAnomaliaDTO {
    private AlarmaDTO alarma;
    private AnomaliaDTO anomalia;
}
