package co.edu.unicauca.orquestador_service.dto;

import java.time.LocalDate;

import lombok.Data;


@Data
public class PrestamoDeporte {
    private String codigoEstudiante;
    private String elemento;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEstimada;
    private LocalDate fechaDevolucionReal;
}
