package co.edu.unicauca.laboratorio_service.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Prestamo {
    private String codigoEstudiante;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEstimada;
    private LocalDate fechaDevolucionReal;
    private String estado; // activo, devuelto, vencido
    private String equipoPrestado; // Microscopio, Osciloscopio, etc.
}