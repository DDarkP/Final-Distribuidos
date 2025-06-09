package co.edu.unicauca.deporte_service.Model;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrestamoDeporte {
    private String codigoEstudiante;
    private String implemento;
    private LocalDate fechaPrestamo;
    private LocalDate fechaEstimada;
    private LocalDate fechaReal;
    // Getters y Setters generados por Lombok
}
