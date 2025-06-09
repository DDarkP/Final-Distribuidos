package co.edu.unicauca.orquestador_service.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Prestamo {
    private String codigoEstudiante;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEstimada;
    private LocalDate fechaDevolucionReal;
    private String estado;
    private String equipoPrestado;
}
