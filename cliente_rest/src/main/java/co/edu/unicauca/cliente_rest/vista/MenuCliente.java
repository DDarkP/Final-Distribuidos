package co.edu.unicauca.cliente_rest.vista;

import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import co.edu.unicauca.cliente_rest.service.ClienteService;

@Component
public class MenuCliente implements CommandLineRunner {

    private final ClienteService clienteService;

    public MenuCliente(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

    while (!salir) {
    System.out.println("\n--- Menú del Cliente Estudiante ---");
    System.out.println("1. Determinar deudas sincrónicas");
    System.out.println("2. Determinar deudas asíncronas");
    System.out.println("3. Salir");
    System.out.print("Seleccione una opción: ");
    int opcion = scanner.nextInt();
    scanner.nextLine(); // Limpiar buffer

    switch (opcion) {
        case 1:
            System.out.print("Ingrese código del estudiante: ");
            String codigoSync = scanner.nextLine();
            String resultadoSync = clienteService.verificarPazSalvo(codigoSync);
            System.out.println("\nResultado Síncrono:");
            System.out.println(resultadoSync);
            break;

        case 2:
            System.out.print("Ingrese código del estudiante: ");
            String codigoAsync = scanner.nextLine();
            // Llamar método asíncrono y esperar resultado
            String resultadoAsync = clienteService.verificarPazSalvoAsync(codigoAsync);
            System.out.println("\nResultado Asíncrono:");
            System.out.println(resultadoAsync);
            break;

        case 3:
            salir = true;
            System.out.println("Saliendo...");
            break;

        default:
            System.out.println("Opción no válida.");
    }
}
        scanner.close();
    }
}
