package co.edu.unicauca.deporte_service.DTOs;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendienteDeporteDTO {
    private String implemento;
    private LocalDate fechaPrestamo;
    private LocalDate fechaEstimada;
    private LocalDate fechaReal;
    // Getters y Setters generados por Lombok
}
