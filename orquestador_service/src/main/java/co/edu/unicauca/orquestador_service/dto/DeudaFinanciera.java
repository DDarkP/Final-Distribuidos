package co.edu.unicauca.orquestador_service.dto;

import java.time.LocalDate;

import lombok.Data;


@Data
public class DeudaFinanciera {
    private String codigoEstudiante;
    private double montoAdeudado;
    private String motivo;
    private LocalDate fechaGeneracion;
    private LocalDate fechaLimite;
    private String estado; // "pendiente", "pagada", "en mora"
}
