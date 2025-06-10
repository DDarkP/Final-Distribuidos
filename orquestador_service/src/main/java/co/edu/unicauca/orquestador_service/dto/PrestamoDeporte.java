package co.edu.unicauca.orquestador_service.dto;

import java.time.LocalDate;

import lombok.Data;


@Data
public class PrestamoDeporte {
    private String codigoEstudiante;
    private String implemento;
    private LocalDate fechaPrestamo;
    private LocalDate fechaEstimada;
    private LocalDate fechaReal;
}
