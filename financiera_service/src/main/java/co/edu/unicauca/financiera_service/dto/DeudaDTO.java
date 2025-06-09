package co.edu.unicauca.financiera_service.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DeudaDTO {
    private double monto;
    private String motivo;
    private LocalDate fechaGeneracion;
    private LocalDate fechaLimite;
    private String estado;
}
