package co.edu.unicauca.financiera_service.model;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Deuda {
    private String codigoEstudiante;
    private double monto;
    private String motivo;
    private LocalDate fechaGeneracion;
    private LocalDate fechaLimite;
    private String estado; // "pendiente", "pagada"

    public Deuda(String codigoEstudiante, double monto, String motivo, LocalDate fechaGeneracion, LocalDate fechaLimite, String estado) {
        this.codigoEstudiante = codigoEstudiante;
        this.monto = monto;
        this.motivo = motivo;
        this.fechaGeneracion = fechaGeneracion;
        this.fechaLimite = fechaLimite;
        this.estado = estado;
    }
}
