import java.util.Scanner;

public class Matrices {
    public static void main(String[] args) {
        menu();
    }

    
    public static void menu() { //Función para el menú de opciones
        Scanner scanner = new Scanner(System.in);
        int[][] matriz = null;
        int opc;
        
        do {
            cleanSc();
            System.out.println("------ SUMA DE MATRICES ------\n");
            System.out.println("1. Declarar matriz");
            System.out.println("2. Columnas");
            System.out.println("3. Filas");
            System.out.println("4. Salir");
            System.out.println("\nSeleccione una opción: ");
            opc = scanner.nextInt();

            switch (opc) {
                case 1:
                    matriz = declararMatriz(scanner);
                    break;
                case 2:
                    sumarColumnas(matriz, scanner);
                    break;
                case 3:
                    sumarFilas(matriz, scanner);
                    break;
                case 4:
                    System.out.println("\nSaliendo del programa...\n");
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, ingrese una opción válida.");
                    break;
            }
        } while (opc != 4); 
        scanner.close();        
    }

    public static void cleanSc() { //Función para limpiar la consola
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            System.out.println("Error al limpiar la consola: " + e.getMessage());
        }
    }

    public static void presionarEnter(Scanner scanner) {    //Función para esperar a que el usuario presione enter
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine(); 
        scanner.nextLine(); 
        cleanSc();
    }

    public static int[][] declararMatriz(Scanner scanner) { //Función para declarar una matriz
        try {
            cleanSc();
            System.out.println("---- Declarar Matriz ----\n");
            System.out.println("Ingrese el número de filas: ");
            int filas = scanner.nextInt();
            System.out.println("Ingrese el número de columnas: ");
            int columnas = scanner.nextInt();
            int[][] matriz = new int[filas][columnas];
            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    System.out.println("Elemento [" + i + "][" + j + "]: ");
                    matriz[i][j] = scanner.nextInt();
                }
            }
            System.out.println("\nMatriz declarada correctamente...\n");
            imprimirMatriz(matriz);
            presionarEnter(scanner);
            return matriz;
        } catch (Exception e) {
            System.out.println("Error al declarar la matriz: " + e.getLocalizedMessage());
            return null;
        }
    }

    public static void imprimirMatriz(int[][] matriz) { //Función para imprimir una matriz
        System.out.println("---- Matriz ----\n");
        if (matriz == null) {
            System.out.println("\nError: No se ha declarado ninguna matriz, por favor declare una matriz primero...");
            return;
        } else if (matriz.length == 0 || matriz[0].length == 0) {
            System.out.println("\nLa matriz está vacía.");
            return;
        } else {
            for (int i = 0; i < matriz.length; i++) {
                for (int j = 0; j < matriz[i].length; j++) {
                    System.out.print(matriz[i][j] + "\t");
                }
                System.out.println();
            }
        }
    }

    public static int[] sumarColumnas(int[][] matriz, Scanner scanner) { //Función para sumar las columnas de una matriz
        if (matriz == null) {
            System.out.println("\nError: No se ha declarado ninguna matriz, por favor declare una matriz primero...");
            presionarEnter(scanner);
            return null;
        } else if (matriz.length == 0) {
            System.out.println("\nLa matriz está vacía.");
            presionarEnter(scanner);
            return new int[0];
        } else {
            cleanSc();
            int columnas = matriz[0].length;
            int[] suma = new int[columnas];
            System.out.println("---- Suma de Columnas ----\n");
            imprimirMatriz(matriz);
            for (int i = 0; i < matriz.length; i++) {
                for (int j = 0; j < columnas; j++) {
                    suma[j] += matriz[i][j];
                }
            }
            System.out.println("\nResultado: \n");
            for (int s : suma) {
                System.out.print(s + "\t");
            }
            System.out.println();
            presionarEnter(scanner);
            return suma;
        }
    }

    public static int[] sumarFilas(int[][] matriz, Scanner scanner) {  //Función para sumar las filas de una matriz
        if (matriz == null) {
            System.out.println("\nError: No se ha declarado ninguna matriz, por favor declare una matriz primero...");
            presionarEnter(scanner);
            return null;
        } else if (matriz.length == 0) {
            System.out.println("\nLa matriz está vacía.");
            presionarEnter(scanner);
            return new int[0];
        } else {
            cleanSc();
            int filas = matriz.length;
            int[] suma = new int[filas];
            System.out.println("---- Suma de Filas ----\n");
            imprimirMatriz(matriz);
            for (int i = 0; i < matriz.length; i++) {
                for (int j = 0; j < matriz[i].length; j++) {
                    suma[i] += matriz[i][j];
                }
            }
            System.out.println("\nResultado: \n");
            for (int s : suma) {
                System.out.print(s + "\t");
            }
            System.out.println();
            presionarEnter(scanner);
            return suma;
        }
    }
}
