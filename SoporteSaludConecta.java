// Sistema de Soporte Tecnico Empresarial - SaludConecta IPS
// Como ejecutarlo: abrir una terminal en la carpeta donde esta este archivo y escribir:
//     java SoporteSaludConecta.java
// (si tiene Java 8 primero se compila con: javac SoporteSaludConecta.java
//  y luego se ejecuta con: java SoporteSaludConecta)

import java.util.ArrayList; // para la lista de empleados y la de equipos
import java.util.LinkedList; // la uso para crear la cola
import java.util.Queue; // tipo de dato de la cola de pendientes
import java.util.Scanner; // para leer lo que se escribe en consola
import java.util.Stack; // para la pila de solicitudes solucionadas

public class SoporteSaludConecta { // clase principal, el archivo se llama igual

    static final String NOMBRE_EMPRESA = "SALUDCONECTA IPS"; // nombre de la empresa para el encabezado

    // arreglos con datos fijos que se muestran como opciones
    static final String[] AREAS = {"Admisiones", "Consulta externa", "Seguimiento ambulatorio", "Auditoria medica", "Facturacion", "Laboratorio", "Farmacia"}; // areas de la IPS
    static final String[] CATEGORIAS = {"Hardware", "Software / historia clinica", "Red e internet", "Impresoras", "Usuarios y claves"}; // tipos de problema
    static final String[] PRIORIDADES = {"Baja", "Media", "Alta", "Urgente"}; // niveles de prioridad
    static final String[] TIPOS_EQUIPO = {"Computador", "Portatil", "Impresora", "Lector de codigo de barras", "Tablet", "Telefono"}; // equipos que se manejan
    static final String[] ESTADOS_EQUIPO = {"Operativo", "En reparacion", "Fuera de servicio"}; // como puede estar un equipo
    static final String[] ESTADOS_SOLICITUD = {"Pendiente", "En atencion", "Solucionada"}; // estados de una solicitud

    static ArrayList<String[]> empleados = new ArrayList<String[]>(); // cada empleado: codigo, nombre, area, correo
    static ArrayList<String[]> equipos = new ArrayList<String[]>(); // cada equipo: codigo, tipo, marca, empleado, estado
    static Queue<String[]> colaPendientes = new LinkedList<String[]>(); // solicitudes en espera, se atienden en orden de llegada
    static Stack<String[]> pilaSolucionadas = new Stack<String[]>(); // historial, la ultima solucionada queda encima
    // cada solicitud: codigo, empleado, equipo, descripcion, prioridad, estado, categoria

    static Scanner sc = new Scanner(System.in); // un solo scanner para todo el programa

    public static void main(String[] args) { // aqui inicia el programa
        int opcion = 0; // opcion del menu, empieza en 0 para que entre al while
        while (opcion != 8) { // el menu se repite hasta que escojan salir
            mostrarMenu(); // imprime el encabezado y las opciones
            opcion = leerEntero("Digite una opcion: "); // lee la opcion (con try-catch adentro)
            switch (opcion) { // segun la opcion hace algo diferente
                case 1: // registrar empleado
                    registrarEmpleado(); // llama al metodo
                    break; // sale del switch
                case 2: // registrar equipo
                    registrarEquipo(); // llama al metodo
                    break; // sale del switch
                case 3: // crear solicitud
                    crearSolicitud(); // llama al metodo
                    break; // sale del switch
                case 4: // consultas
                    consultarRegistros(); // abre el submenu de consultas
                    break; // sale del switch
                case 5: // atender
                    atenderSolicitud(); // saca de la cola y pasa a la pila
                    break; // sale del switch
                case 6: // ver pendientes
                    mostrarPendientes(); // recorre la cola
                    break; // sale del switch
                case 7: // ver solucionadas
                    mostrarSolucionadas(); // recorre la pila
                    break; // sale del switch
                case 8: // salir
                    System.out.println("Saliendo del sistema... hasta luego."); // mensaje final
                    break; // sale del switch y el while termina
                default: // cualquier numero que no este en el menu
                    System.out.println("Esa opcion no existe, escoja de 1 a 8."); // aviso
            } // cierra el switch
        } // cierra el while
        sc.close(); // cierra el scanner
    } // cierra el main

    static void mostrarMenu() { // muestra el menu principal
        System.out.println(); // linea en blanco para separar
        System.out.println("========================================================"); // borde de arriba
        System.out.println("                   " + NOMBRE_EMPRESA); // nombre de la empresa
        System.out.println("         SISTEMA DE SOPORTE TECNICO EMPRESARIAL"); // nombre del sistema
        System.out.println("========================================================"); // borde de abajo
        System.out.println("1. Registrar empleado"); // opcion 1
        System.out.println("2. Registrar equipo"); // opcion 2
        System.out.println("3. Crear solicitud"); // opcion 3
        System.out.println("4. Consultar registros"); // opcion 4
        System.out.println("5. Atender siguiente solicitud"); // opcion 5
        System.out.println("6. Mostrar solicitudes pendientes"); // opcion 6
        System.out.println("7. Mostrar solicitudes solucionadas"); // opcion 7
        System.out.println("8. Salir"); // opcion 8
    } // cierra mostrarMenu

    // ---------- opcion 1 ----------
    static void registrarEmpleado() { // guarda un empleado nuevo en el ArrayList
        System.out.println("\n** Registro de empleado **"); // titulo
        String codigo = leerTexto("Codigo del empleado: ").toUpperCase(); // lo paso a mayuscula para comparar igual
        while (buscarEmpleado(codigo) != null) { // si ya existe no lo deja seguir
            System.out.println("Ese codigo ya esta registrado, digite otro."); // aviso de duplicado
            codigo = leerTexto("Codigo del empleado: ").toUpperCase(); // pide el codigo otra vez
        } // cierra el while
        String nombre = leerTexto("Nombre completo: "); // nombre, no puede quedar vacio
        String area = escogerOpcion("Area de trabajo:", AREAS); // escoge el area del arreglo
        String correo = leerTexto("Correo electronico: "); // correo
        while (!correo.contains("@") || !correo.contains(".")) { // revision sencilla del correo
            System.out.println("El correo debe tener @ y punto, ej: ana@saludconecta.com"); // aviso
            correo = leerTexto("Correo electronico: "); // lo vuelve a pedir
        } // cierra el while
        String[] empleado = {codigo, nombre, area, correo}; // junto los datos en un arreglo
        empleados.add(empleado); // lo agrego a la lista
        System.out.println("Empleado registrado. Total de empleados: " + empleados.size()); // confirmacion
    } // cierra registrarEmpleado

    // ---------- opcion 2 ----------
    static void registrarEquipo() { // guarda un equipo y lo asigna a un empleado
        System.out.println("\n** Registro de equipo **"); // titulo
        if (empleados.isEmpty()) { // sin empleados no se puede asignar
            System.out.println("Primero debe registrar al menos un empleado."); // aviso
            return; // vuelve al menu
        } // cierra el if
        String codigo = leerTexto("Codigo del equipo: ").toUpperCase(); // codigo del equipo
        while (buscarEquipo(codigo) != null) { // no se permiten codigos repetidos
            System.out.println("Ya hay un equipo con ese codigo, digite otro."); // aviso de duplicado
            codigo = leerTexto("Codigo del equipo: ").toUpperCase(); // lo pide de nuevo
        } // cierra el while
        String tipo = escogerOpcion("Tipo de equipo:", TIPOS_EQUIPO); // tipo desde el arreglo
        String marca = leerTexto("Marca: "); // marca del equipo
        String responsable = leerTexto("Codigo del empleado responsable: ").toUpperCase(); // a quien se le asigna
        while (buscarEmpleado(responsable) == null) { // el empleado tiene que existir
            System.out.println("No existe un empleado con ese codigo."); // aviso
            responsable = leerTexto("Codigo del empleado responsable: ").toUpperCase(); // lo vuelve a pedir
        } // cierra el while
        String estado = escogerOpcion("Estado del equipo:", ESTADOS_EQUIPO); // estado desde el arreglo
        String[] equipo = {codigo, tipo, marca, responsable, estado}; // datos del equipo en un arreglo
        equipos.add(equipo); // lo agrego a la lista de equipos
        System.out.println("Equipo asignado a " + buscarEmpleado(responsable)[1] + ". Total de equipos: " + equipos.size()); // confirmacion
    } // cierra registrarEquipo

    // ---------- opcion 3 ----------
    static void crearSolicitud() { // crea una solicitud y la mete a la cola
        System.out.println("\n** Nueva solicitud de soporte **"); // titulo
        if (empleados.isEmpty() || equipos.isEmpty()) { // necesita empleados y equipos
            System.out.println("Debe haber empleados y equipos registrados para crear una solicitud."); // aviso
            return; // vuelve al menu
        } // cierra el if
        String codigo = leerTexto("Codigo de la solicitud: ").toUpperCase(); // codigo de la solicitud
        while (buscarSolicitud(codigo) != null) { // busca en la cola y en la pila para no repetir
            System.out.println("Ese codigo de solicitud ya existe, digite otro."); // aviso de duplicado
            codigo = leerTexto("Codigo de la solicitud: ").toUpperCase(); // lo pide otra vez
        } // cierra el while
        String codEmpleado = leerTexto("Codigo del empleado que reporta: ").toUpperCase(); // quien pide el soporte
        while (buscarEmpleado(codEmpleado) == null) { // valida que el empleado exista
            System.out.println("Ese empleado no esta registrado."); // aviso
            codEmpleado = leerTexto("Codigo del empleado que reporta: ").toUpperCase(); // lo pide de nuevo
        } // cierra el while
        String codEquipo = leerTexto("Codigo del equipo con falla: ").toUpperCase(); // equipo que falla
        while (buscarEquipo(codEquipo) == null) { // valida que el equipo exista
            System.out.println("Ese equipo no esta registrado."); // aviso
            codEquipo = leerTexto("Codigo del equipo con falla: ").toUpperCase(); // lo pide de nuevo
        } // cierra el while
        String categoria = escogerOpcion("Categoria del problema:", CATEGORIAS); // categoria desde el arreglo
        String descripcion = leerTexto("Describa el problema: "); // descripcion de la falla
        String prioridad = escogerOpcion("Prioridad:", PRIORIDADES); // prioridad desde el arreglo
        String[] solicitud = {codigo, codEmpleado, codEquipo, descripcion, prioridad, ESTADOS_SOLICITUD[0], categoria}; // queda en estado Pendiente
        colaPendientes.offer(solicitud); // entra al final de la cola
        System.out.println("Solicitud creada. Esta en el turno " + colaPendientes.size() + " de la cola."); // confirmacion
    } // cierra crearSolicitud

    // ---------- opcion 4 ----------
    static void consultarRegistros() { // submenu de busquedas y listados
        int op = 0; // opcion del submenu
        while (op != 6) { // se repite hasta que escoja volver
            System.out.println("\n--- Consultar registros ---"); // titulo
            System.out.println("1. Buscar empleado por codigo"); // opcion 1
            System.out.println("2. Buscar equipo por codigo"); // opcion 2
            System.out.println("3. Buscar solicitud por codigo"); // opcion 3
            System.out.println("4. Ver todos los empleados"); // opcion 4
            System.out.println("5. Ver todos los equipos"); // opcion 5
            System.out.println("6. Volver"); // opcion 6
            op = leerEntero("Opcion: "); // lee la opcion con try-catch
            switch (op) { // evalua la opcion del submenu
                case 1: // buscar empleado
                    if (empleados.isEmpty()) { // revisa si la lista esta vacia
                        System.out.println("No hay empleados registrados."); // aviso
                    } else { // si hay empleados
                        String[] emp = buscarEmpleado(leerTexto("Codigo a buscar: ").toUpperCase()); // busca por codigo
                        if (emp == null) { // no lo encontro
                            System.out.println("No se encontro ese empleado."); // aviso
                        } else { // si lo encontro
                            mostrarEmpleado(emp); // muestra los datos
                        } // cierra el if interno
                    } // cierra el if
                    break; // sale del switch
                case 2: // buscar equipo
                    if (equipos.isEmpty()) { // revisa si la lista esta vacia
                        System.out.println("No hay equipos registrados."); // aviso
                    } else { // si hay equipos
                        String[] eq = buscarEquipo(leerTexto("Codigo a buscar: ").toUpperCase()); // busca por codigo
                        if (eq == null) { // no lo encontro
                            System.out.println("No se encontro ese equipo."); // aviso
                        } else { // si lo encontro
                            mostrarEquipo(eq); // muestra los datos
                        } // cierra el if interno
                    } // cierra el if
                    break; // sale del switch
                case 3: // buscar solicitud
                    if (colaPendientes.isEmpty() && pilaSolucionadas.isEmpty()) { // no hay solicitudes en ningun lado
                        System.out.println("No hay solicitudes registradas."); // aviso
                    } else { // si hay solicitudes
                        String[] sol = buscarSolicitud(leerTexto("Codigo a buscar: ").toUpperCase()); // busca en cola y pila
                        if (sol == null) { // no la encontro
                            System.out.println("No se encontro esa solicitud."); // aviso
                        } else { // si la encontro
                            mostrarSolicitud(sol); // muestra los datos
                        } // cierra el if interno
                    } // cierra el if
                    break; // sale del switch
                case 4: // listar empleados
                    if (empleados.isEmpty()) { // lista vacia
                        System.out.println("No hay empleados registrados."); // aviso
                    } // cierra el if
                    for (int i = 0; i < empleados.size(); i++) { // recorre el ArrayList de empleados
                        mostrarEmpleado(empleados.get(i)); // muestra cada uno
                    } // cierra el for
                    break; // sale del switch
                case 5: // listar equipos
                    if (equipos.isEmpty()) { // lista vacia
                        System.out.println("No hay equipos registrados."); // aviso
                    } // cierra el if
                    for (int i = 0; i < equipos.size(); i++) { // recorre el ArrayList de equipos
                        mostrarEquipo(equipos.get(i)); // muestra cada uno
                    } // cierra el for
                    break; // sale del switch
                case 6: // volver
                    break; // no hace nada, el while termina
                default: // numero que no esta en el submenu
                    System.out.println("Opcion no valida, escoja de 1 a 6."); // aviso
            } // cierra el switch
        } // cierra el while
    } // cierra consultarRegistros

    // ---------- opcion 5 ----------
    static void atenderSolicitud() { // atiende la primera solicitud de la cola
        if (colaPendientes.isEmpty()) { // si no hay nada en la cola no se puede atender
            System.out.println("\nNo hay solicitudes pendientes en este momento."); // aviso
            return; // vuelve al menu
        } // cierra el if
        String[] sol = colaPendientes.poll(); // saca la primera que llego
        sol[5] = ESTADOS_SOLICITUD[1]; // pasa a En atencion
        System.out.println("\nAtendiendo la siguiente solicitud:"); // titulo
        mostrarSolicitud(sol); // muestra lo que se esta atendiendo
        System.out.print("Presione Enter cuando el caso quede resuelto..."); // simula el tiempo de atencion
        sc.nextLine(); // espera el Enter
        sol[5] = ESTADOS_SOLICITUD[2]; // pasa a Solucionada
        pilaSolucionadas.push(sol); // la guarda encima de la pila
        System.out.println("Solicitud " + sol[0] + " solucionada. Quedan " + colaPendientes.size() + " pendientes."); // confirmacion
    } // cierra atenderSolicitud

    // ---------- opcion 6 ----------
    static void mostrarPendientes() { // muestra la cola sin sacar nada
        System.out.println("\n** Solicitudes pendientes **"); // titulo
        if (colaPendientes.isEmpty()) { // cola vacia
            System.out.println("La cola esta vacia."); // aviso
            return; // vuelve al menu
        } // cierra el if
        int turno = 1; // contador para mostrar el turno
        for (String[] sol : colaPendientes) { // recorre la cola del primero al ultimo
            System.out.println("Turno " + turno + ":"); // numero de turno
            mostrarSolicitud(sol); // datos de la solicitud
            turno++; // siguiente turno
        } // cierra el for
    } // cierra mostrarPendientes

    // ---------- opcion 7 ----------
    static void mostrarSolucionadas() { // muestra la pila de arriba hacia abajo
        System.out.println("\n** Solicitudes solucionadas **"); // titulo
        if (pilaSolucionadas.isEmpty()) { // pila vacia
            System.out.println("Todavia no hay solicitudes solucionadas."); // aviso
            return; // vuelve al menu
        } // cierra el if
        for (int i = pilaSolucionadas.size() - 1; i >= 0; i--) { // empieza por el tope (la mas reciente)
            mostrarSolicitud(pilaSolucionadas.get(i)); // muestra cada una
        } // cierra el for
        System.out.println("Total solucionadas: " + pilaSolucionadas.size()); // cuantas hay
    } // cierra mostrarSolucionadas

    // ---------- metodos para mostrar ----------
    static void mostrarEmpleado(String[] e) { // imprime un empleado
        System.out.println(e[0] + " | " + e[1] + " | " + e[2] + " | " + e[3]); // codigo, nombre, area y correo
    } // cierra mostrarEmpleado

    static void mostrarEquipo(String[] q) { // imprime un equipo
        System.out.println(q[0] + " | " + q[1] + " | " + q[2] + " | Responsable: " + q[3] + " | " + q[4]); // codigo, tipo, marca, responsable y estado
    } // cierra mostrarEquipo

    static void mostrarSolicitud(String[] s) { // imprime una solicitud
        System.out.println("  Codigo: " + s[0] + "  Estado: " + s[5]); // codigo y estado
        System.out.println("  Empleado: " + s[1] + "  Equipo: " + s[2]); // quien reporta y que equipo
        System.out.println("  Categoria: " + s[6] + "  Prioridad: " + s[4]); // categoria y prioridad
        System.out.println("  Problema: " + s[3]); // descripcion
        System.out.println("  ----------------------------------"); // separador
    } // cierra mostrarSolicitud

    // ---------- busquedas ----------
    static String[] buscarEmpleado(String codigo) { // busca un empleado por codigo
        for (String[] e : empleados) { // recorre la lista
            if (e[0].equals(codigo)) { // compara el codigo
                return e; // lo encontro
            } // cierra el if
        } // cierra el for
        return null; // no existe
    } // cierra buscarEmpleado

    static String[] buscarEquipo(String codigo) { // busca un equipo por codigo
        for (String[] q : equipos) { // recorre la lista
            if (q[0].equals(codigo)) { // compara el codigo
                return q; // lo encontro
            } // cierra el if
        } // cierra el for
        return null; // no existe
    } // cierra buscarEquipo

    static String[] buscarSolicitud(String codigo) { // busca una solicitud en la cola y en la pila
        for (String[] s : colaPendientes) { // primero en las pendientes
            if (s[0].equals(codigo)) { // compara el codigo
                return s; // la encontro
            } // cierra el if
        } // cierra el for
        for (String[] s : pilaSolucionadas) { // luego en las solucionadas
            if (s[0].equals(codigo)) { // compara el codigo
                return s; // la encontro
            } // cierra el if
        } // cierra el for
        return null; // no existe
    } // cierra buscarSolicitud

    // ---------- lectura de datos ----------
    static int leerEntero(String mensaje) { // lee un numero sin que el programa se caiga
        while (true) { // repite hasta que escriban un numero
            System.out.print(mensaje); // muestra el mensaje
            try { // intenta convertir a numero
                return Integer.parseInt(sc.nextLine().trim()); // si es numero lo devuelve
            } catch (NumberFormatException e) { // si escribieron letras o nada
                System.out.println("Debe digitar un numero."); // aviso y vuelve a preguntar
            } // cierra el try-catch
        } // cierra el while
    } // cierra leerEntero

    static String leerTexto(String mensaje) { // lee un texto que no puede quedar vacio
        System.out.print(mensaje); // muestra el mensaje
        String texto = sc.nextLine().trim(); // lee y quita espacios de los lados
        while (texto.isEmpty()) { // mientras este vacio lo sigue pidiendo
            System.out.println("Este campo no puede quedar vacio."); // aviso
            System.out.print(mensaje); // repite el mensaje
            texto = sc.nextLine().trim(); // vuelve a leer
        } // cierra el while
        return texto; // devuelve el texto
    } // cierra leerTexto

    static String escogerOpcion(String titulo, String[] opciones) { // muestra un arreglo y deja escoger una opcion
        System.out.println(titulo); // titulo de la lista
        for (int i = 0; i < opciones.length; i++) { // recorre el arreglo
            System.out.println("  " + (i + 1) + ". " + opciones[i]); // muestra numero y texto
        } // cierra el for
        int n = leerEntero("  Escoja: "); // lee el numero con try-catch
        while (n < 1 || n > opciones.length) { // valida que este en el rango
            System.out.println("  Escoja un numero entre 1 y " + opciones.length + "."); // aviso
            n = leerEntero("  Escoja: "); // lo pide otra vez
        } // cierra el while
        return opciones[n - 1]; // devuelve la opcion (el arreglo empieza en 0)
    } // cierra escogerOpcion

} // cierra la clase
