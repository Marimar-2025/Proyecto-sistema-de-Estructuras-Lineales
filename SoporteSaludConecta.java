import java.util.ArrayList; // importa ArrayList, se usa para guardar los empleados y los equipos
import java.util.LinkedList; // importa LinkedList, que es la clase con la que se crea la cola
import java.util.Queue; // importa Queue para manejar la cola de solicitudes pendientes
import java.util.Scanner; // importa Scanner para leer lo que escribe el usuario en la consola
import java.util.Stack; // importa Stack para guardar las solicitudes que ya fueron solucionadas

public class SoporteSaludConecta { // unica clase del programa, tiene el mismo nombre del archivo

    // ================== DATOS FIJOS (ARREGLOS) ==================
    static final String EMPRESA = "SALUDCONECTA IPS"; // nombre de la empresa que aparece en el encabezado del menu
    static String[] areas = {"Admisiones y citas", "Consulta externa", "Seguimiento ambulatorio", "Auditoria medica", "Facturacion", "Laboratorio clinico", "Farmacia"}; // arreglo con las areas de la IPS
    static String[] categorias = {"Hardware (equipo fisico)", "Software / Historia clinica", "Red e internet", "Impresion", "Usuarios y contrasenas"}; // arreglo con las categorias de los problemas
    static String[] prioridades = {"Baja", "Media", "Alta", "Critica - afecta atencion de pacientes"}; // arreglo con los niveles de prioridad
    static String[] tiposEquipo = {"Computador de escritorio", "Portatil", "Impresora", "Lector de codigo de barras", "Tablet", "Telefono IP"}; // arreglo con los tipos de equipos que maneja la IPS
    static String[] estadosEquipo = {"Operativo", "En revision", "Fuera de servicio"}; // arreglo con los estados posibles de un equipo

    // ================== ESTRUCTURAS DINAMICAS ==================
    static ArrayList<String[]> empleados = new ArrayList<>(); // lista de empleados, cada uno es {codigo, nombre, area, correo}
    static ArrayList<String[]> equipos = new ArrayList<>(); // lista de equipos, cada uno es {codigo, tipo, marca, codigoEmpleado, estado}
    static Queue<String[]> colaPendientes = new LinkedList<>(); // cola de solicitudes pendientes, el primero que llega es el primero que se atiende
    static Stack<String[]> pilaSolucionadas = new Stack<>(); // pila de solicitudes solucionadas, la ultima atendida queda arriba
    // cada solicitud es {codigo, codigoEmpleado, codigoEquipo, descripcion, prioridad, estado, categoria, solucion}

    static Scanner sc = new Scanner(System.in); // Scanner que se usa en todo el programa para leer del teclado

    public static void main(String[] args) { // metodo principal, aqui empieza a ejecutarse el programa
        int opcion = 0; // variable que guarda la opcion que elige el usuario
        while (opcion != 8) { // el menu se repite mientras el usuario no escoja la opcion 8 (salir)
            mostrarMenu(); // imprime el encabezado con el nombre de la empresa y las opciones
            opcion = leerEntero("Seleccione una opcion: ", 1, 8); // lee la opcion y valida que sea un numero entre 1 y 8
            switch (opcion) { // segun el numero escogido se ejecuta una accion
                case 1: // opcion 1
                    registrarEmpleado(); // registra un nuevo empleado
                    break; // termina el caso 1
                case 2: // opcion 2
                    registrarEquipo(); // registra un equipo y lo asigna a un empleado
                    break; // termina el caso 2
                case 3: // opcion 3
                    crearSolicitud(); // crea una solicitud de soporte y la manda a la cola
                    break; // termina el caso 3
                case 4: // opcion 4
                    consultarRegistros(); // abre el submenu de consultas
                    break; // termina el caso 4
                case 5: // opcion 5
                    atenderSiguiente(); // saca la primera solicitud de la cola y la pasa a la pila
                    break; // termina el caso 5
                case 6: // opcion 6
                    mostrarPendientes(); // muestra lo que hay en la cola
                    break; // termina el caso 6
                case 7: // opcion 7
                    mostrarSolucionadas(); // muestra lo que hay en la pila
                    break; // termina el caso 7
                case 8: // opcion 8
                    System.out.println("\nCerrando el sistema de soporte de " + EMPRESA + ". Hasta pronto."); // mensaje de despedida
                    break; // termina el caso 8
                default: // por si llega un valor diferente (no deberia pasar por la validacion)
                    System.out.println("Opcion no valida."); // avisa que la opcion no existe
            } // fin del switch
            if (opcion != 8) { // si no va a salir
                pausa(); // espera un Enter para que el usuario alcance a leer el resultado
            } // fin del if
        } // fin del while del menu
        sc.close(); // cierra el Scanner al terminar el programa
    } // fin del main

    // ================== MENU ==================
    static void mostrarMenu() { // imprime el menu principal
        System.out.println(); // deja una linea en blanco antes del encabezado
        System.out.println("========================================================"); // linea superior del encabezado
        System.out.println("                   " + EMPRESA); // nombre de la empresa
        System.out.println("         SISTEMA DE SOPORTE TECNICO EMPRESARIAL"); // nombre del sistema
        System.out.println("========================================================"); // linea inferior del encabezado
        System.out.println(" Empleados: " + empleados.size() + " | Equipos: " + equipos.size() + " | Pendientes: " + colaPendientes.size() + " | Solucionadas: " + pilaSolucionadas.size()); // resumen rapido de cuantos registros hay
        System.out.println("--------------------------------------------------------"); // separador
        System.out.println("1. Registrar empleado"); // opcion 1
        System.out.println("2. Registrar equipo"); // opcion 2
        System.out.println("3. Crear solicitud"); // opcion 3
        System.out.println("4. Consultar registros"); // opcion 4
        System.out.println("5. Atender siguiente solicitud"); // opcion 5
        System.out.println("6. Mostrar solicitudes pendientes"); // opcion 6
        System.out.println("7. Mostrar solicitudes solucionadas"); // opcion 7
        System.out.println("8. Salir"); // opcion 8
    } // fin de mostrarMenu

    // ================== OPCION 1: EMPLEADOS ==================
    static void registrarEmpleado() { // pide los datos de un empleado y lo guarda en el ArrayList
        System.out.println("\n--- REGISTRAR EMPLEADO ---"); // titulo de la opcion
        String codigo = leerCodigo("Codigo del empleado (ej: EMP01): "); // pide el codigo del empleado
        if (buscarEmpleado(codigo) != null) { // revisa si ya hay un empleado con ese codigo
            System.out.println("Ya existe un empleado con el codigo " + codigo + ". No se puede repetir."); // avisa que esta duplicado
            return; // se devuelve al menu sin registrar
        } // fin del if
        String nombre = leerTexto("Nombre completo: "); // pide el nombre, no deja que quede vacio
        String area = elegirDeArreglo("Area de trabajo:", areas); // muestra las areas del arreglo y deja escoger una
        String correo = leerCorreo("Correo electronico: "); // pide el correo y revisa que tenga formato valido
        String[] empleado = {codigo, nombre, area, correo}; // arma el registro del empleado en un arreglo
        empleados.add(empleado); // lo agrega al ArrayList de empleados
        System.out.println("Empleado " + nombre + " registrado en el area de " + area + "."); // confirma el registro
    } // fin de registrarEmpleado

    // ================== OPCION 2: EQUIPOS ==================
    static void registrarEquipo() { // pide los datos de un equipo y lo guarda en el ArrayList
        System.out.println("\n--- REGISTRAR EQUIPO ---"); // titulo de la opcion
        if (empleados.isEmpty()) { // si todavia no hay empleados no hay a quien asignar el equipo
            System.out.println("No hay empleados registrados. Primero registre un empleado (opcion 1)."); // mensaje de estructura vacia
            return; // vuelve al menu
        } // fin del if
        String codigo = leerCodigo("Codigo del equipo (ej: EQ01): "); // pide el codigo del equipo
        if (buscarEquipo(codigo) != null) { // revisa si ese codigo ya existe
            System.out.println("Ya existe un equipo con el codigo " + codigo + ". No se puede repetir."); // avisa que esta duplicado
            return; // vuelve al menu
        } // fin del if
        String codEmpleado = leerCodigo("Codigo del empleado responsable: "); // pide a quien se le asigna el equipo
        String[] responsable = buscarEmpleado(codEmpleado); // busca el empleado en la lista
        if (responsable == null) { // si no lo encuentra
            System.out.println("El empleado " + codEmpleado + " no existe. Verifique en Consultar registros."); // avisa que no existe
            return; // vuelve al menu sin registrar
        } // fin del if
        String tipo = elegirDeArreglo("Tipo de equipo:", tiposEquipo); // escoge el tipo desde el arreglo
        String marca = leerTexto("Marca: "); // pide la marca, no puede ir vacia
        String estado = elegirDeArreglo("Estado del equipo:", estadosEquipo); // escoge el estado desde el arreglo
        String[] equipo = {codigo, tipo, marca, codEmpleado, estado}; // arma el registro del equipo
        equipos.add(equipo); // lo agrega al ArrayList de equipos
        System.out.println(tipo + " " + marca + " asignado a " + responsable[1] + " (" + responsable[2] + ")."); // confirma el registro
    } // fin de registrarEquipo

    // ================== OPCION 3: SOLICITUDES ==================
    static void crearSolicitud() { // crea una solicitud de soporte y la agrega a la cola
        System.out.println("\n--- CREAR SOLICITUD DE SOPORTE ---"); // titulo de la opcion
        if (empleados.isEmpty() || equipos.isEmpty()) { // se necesitan empleados y equipos para crear solicitudes
            System.out.println("Debe haber al menos un empleado y un equipo registrados antes de crear solicitudes."); // mensaje de estructuras vacias
            return; // vuelve al menu
        } // fin del if
        String codigo = leerCodigo("Codigo de la solicitud (ej: SOL01): "); // pide el codigo de la solicitud
        if (buscarSolicitud(codigo) != null) { // revisa en la cola y en la pila si ya existe
            System.out.println("Ya existe una solicitud con el codigo " + codigo + "."); // avisa que esta duplicado
            return; // vuelve al menu
        } // fin del if
        String codEmpleado = leerCodigo("Codigo del empleado que reporta: "); // pide el empleado que hace la solicitud
        String[] empleado = buscarEmpleado(codEmpleado); // busca el empleado
        if (empleado == null) { // si no existe
            System.out.println("El empleado " + codEmpleado + " no esta registrado."); // mensaje de error
            return; // vuelve al menu
        } // fin del if
        String codEquipo = leerCodigo("Codigo del equipo con el problema: "); // pide el equipo que falla
        String[] equipo = buscarEquipo(codEquipo); // busca el equipo
        if (equipo == null) { // si no existe
            System.out.println("El equipo " + codEquipo + " no esta registrado."); // mensaje de error
            return; // vuelve al menu
        } // fin del if
        if (!equipo[3].equals(codEmpleado)) { // revisa que el equipo sea del empleado que reporta
            System.out.println("El equipo " + codEquipo + " esta asignado al empleado " + equipo[3] + ", no a " + codEmpleado + "."); // avisa la inconsistencia
            return; // vuelve al menu
        } // fin del if
        String categoria = elegirDeArreglo("Categoria del problema:", categorias); // escoge la categoria desde el arreglo
        String descripcion = leerTexto("Descripcion del problema: "); // pide la descripcion, no puede ir vacia
        String prioridad = elegirDeArreglo("Nivel de prioridad:", prioridades); // escoge la prioridad desde el arreglo
        String[] solicitud = {codigo, codEmpleado, codEquipo, descripcion, prioridad, "Pendiente", categoria, ""}; // arma la solicitud con estado Pendiente
        colaPendientes.add(solicitud); // la agrega al final de la cola
        System.out.println("Solicitud " + codigo + " creada. Queda en la posicion " + colaPendientes.size() + " de la cola."); // confirma y dice el turno
    } // fin de crearSolicitud

    // ================== OPCION 4: CONSULTAS ==================
    static void consultarRegistros() { // submenu para buscar y listar informacion
        int op = 0; // opcion del submenu
        while (op != 6) { // se repite hasta que escoja volver
            System.out.println("\n--- CONSULTAR REGISTROS ---"); // titulo del submenu
            System.out.println("1. Buscar empleado por codigo"); // opcion 1 del submenu
            System.out.println("2. Buscar equipo por codigo"); // opcion 2 del submenu
            System.out.println("3. Buscar solicitud por codigo"); // opcion 3 del submenu
            System.out.println("4. Mostrar todos los empleados"); // opcion 4 del submenu
            System.out.println("5. Mostrar todos los equipos"); // opcion 5 del submenu
            System.out.println("6. Volver al menu principal"); // opcion 6 del submenu
            op = leerEntero("Opcion: ", 1, 6); // lee la opcion validada
            switch (op) { // evalua la opcion
                case 1: // buscar empleado
                    String[] emp = buscarEmpleado(leerCodigo("Codigo del empleado: ")); // pide el codigo y lo busca
                    if (emp == null) { // si no lo encontro
                        System.out.println("No se encontro ningun empleado con ese codigo."); // mensaje de no encontrado
                    } else { // si lo encontro
                        mostrarEmpleado(emp); // muestra sus datos y sus equipos
                    } // fin del if
                    break; // termina el caso 1
                case 2: // buscar equipo
                    String[] eq = buscarEquipo(leerCodigo("Codigo del equipo: ")); // pide el codigo y lo busca
                    if (eq == null) { // si no lo encontro
                        System.out.println("No se encontro ningun equipo con ese codigo."); // mensaje de no encontrado
                    } else { // si lo encontro
                        mostrarEquipo(eq); // muestra los datos del equipo
                    } // fin del if
                    break; // termina el caso 2
                case 3: // buscar solicitud
                    String[] sol = buscarSolicitud(leerCodigo("Codigo de la solicitud: ")); // pide el codigo y lo busca en cola y pila
                    if (sol == null) { // si no la encontro
                        System.out.println("No se encontro ninguna solicitud con ese codigo."); // mensaje de no encontrado
                    } else { // si la encontro
                        mostrarSolicitud(sol); // muestra los datos de la solicitud
                    } // fin del if
                    break; // termina el caso 3
                case 4: // listar empleados
                    listarEmpleados(); // recorre el ArrayList de empleados
                    break; // termina el caso 4
                case 5: // listar equipos
                    listarEquipos(); // recorre el ArrayList de equipos
                    break; // termina el caso 5
                default: // opcion 6
                    break; // no hace nada y el while termina
            } // fin del switch
        } // fin del while del submenu
    } // fin de consultarRegistros

    static void listarEmpleados() { // muestra todos los empleados en forma de tabla
        if (empleados.isEmpty()) { // si la lista esta vacia
            System.out.println("No hay empleados registrados todavia."); // mensaje de lista vacia
            return; // sale del metodo
        } // fin del if
        System.out.println("\nCODIGO     NOMBRE                        AREA                       CORREO"); // encabezado de la tabla
        for (String[] e : empleados) { // recorre cada empleado de la lista
            System.out.printf("%-10s %-29s %-26s %s%n", e[0], e[1], e[2], e[3]); // imprime una fila con los datos alineados
        } // fin del for
        System.out.println("Total de empleados: " + empleados.size()); // muestra cuantos hay
    } // fin de listarEmpleados

    static void listarEquipos() { // muestra todos los equipos en forma de tabla
        if (equipos.isEmpty()) { // si la lista esta vacia
            System.out.println("No hay equipos registrados todavia."); // mensaje de lista vacia
            return; // sale del metodo
        } // fin del if
        System.out.println("\nCODIGO     TIPO                        MARCA        RESPONSABLE   ESTADO"); // encabezado de la tabla
        for (String[] q : equipos) { // recorre cada equipo de la lista
            System.out.printf("%-10s %-27s %-12s %-13s %s%n", q[0], q[1], q[2], q[3], q[4]); // imprime una fila con los datos alineados
        } // fin del for
        System.out.println("Total de equipos: " + equipos.size()); // muestra cuantos hay
    } // fin de listarEquipos

    static void mostrarEmpleado(String[] e) { // muestra el detalle de un empleado
        System.out.println("\nCodigo : " + e[0]); // codigo
        System.out.println("Nombre : " + e[1]); // nombre completo
        System.out.println("Area   : " + e[2]); // area de trabajo
        System.out.println("Correo : " + e[3]); // correo electronico
        System.out.println("Equipos a cargo:"); // titulo de la lista de equipos
        int cantidad = 0; // contador de equipos del empleado
        for (String[] q : equipos) { // recorre todos los equipos
            if (q[3].equals(e[0])) { // si el equipo le pertenece a este empleado
                System.out.println("  - " + q[0] + " " + q[1] + " " + q[2] + " (" + q[4] + ")"); // lo muestra
                cantidad++; // suma uno al contador
            } // fin del if
        } // fin del for
        if (cantidad == 0) { // si no tiene equipos
            System.out.println("  (no tiene equipos asignados)"); // lo indica
        } // fin del if
    } // fin de mostrarEmpleado

    static void mostrarEquipo(String[] q) { // muestra el detalle de un equipo
        String[] responsable = buscarEmpleado(q[3]); // busca el empleado responsable para mostrar su nombre
        System.out.println("\nCodigo      : " + q[0]); // codigo
        System.out.println("Tipo        : " + q[1]); // tipo de equipo
        System.out.println("Marca       : " + q[2]); // marca
        System.out.println("Responsable : " + q[3] + " - " + responsable[1] + " (" + responsable[2] + ")"); // codigo y nombre del responsable
        System.out.println("Estado      : " + q[4]); // estado del equipo
    } // fin de mostrarEquipo

    static void mostrarSolicitud(String[] s) { // muestra el detalle de una solicitud
        System.out.println("----------------------------------------"); // separador
        System.out.println("Solicitud  : " + s[0] + "   [" + s[5] + "]"); // codigo y estado
        System.out.println("Empleado   : " + s[1] + " - " + buscarEmpleado(s[1])[1]); // codigo y nombre del empleado
        System.out.println("Equipo     : " + s[2] + " - " + buscarEquipo(s[2])[1]); // codigo y tipo del equipo
        System.out.println("Categoria  : " + s[6]); // categoria del problema
        System.out.println("Problema   : " + s[3]); // descripcion
        System.out.println("Prioridad  : " + s[4]); // nivel de prioridad
        if (!s[7].isEmpty()) { // si ya tiene una solucion registrada
            System.out.println("Solucion   : " + s[7]); // la muestra
        } // fin del if
    } // fin de mostrarSolicitud

    // ================== OPCION 5: ATENDER (COLA -> PILA) ==================
    static void atenderSiguiente() { // atiende la primera solicitud de la cola
        System.out.println("\n--- ATENDER SIGUIENTE SOLICITUD ---"); // titulo de la opcion
        if (colaPendientes.isEmpty()) { // si no hay nada en la cola
            System.out.println("No hay solicitudes pendientes por atender."); // mensaje de cola vacia
            return; // vuelve al menu
        } // fin del if
        String[] sol = colaPendientes.poll(); // saca la primera solicitud de la cola (la que llego primero)
        sol[5] = "En atencion"; // cambia su estado mientras el tecnico la trabaja
        mostrarSolicitud(sol); // muestra los datos del caso que se esta atendiendo
        sol[7] = leerTexto("Describa la solucion aplicada: "); // pide que se escriba lo que se hizo, no puede ir vacio
        sol[5] = "Solucionada"; // cambia el estado a Solucionada
        pilaSolucionadas.push(sol); // guarda la solicitud en la parte de arriba de la pila
        System.out.println("Solicitud " + sol[0] + " marcada como Solucionada. Quedan " + colaPendientes.size() + " pendientes."); // confirma la atencion
    } // fin de atenderSiguiente

    // ================== OPCION 6: VER COLA ==================
    static void mostrarPendientes() { // recorre la cola sin sacar elementos
        System.out.println("\n--- SOLICITUDES PENDIENTES (orden de llegada) ---"); // titulo de la opcion
        if (colaPendientes.isEmpty()) { // si la cola esta vacia
            System.out.println("La cola esta vacia, no hay solicitudes pendientes."); // mensaje de cola vacia
            return; // vuelve al menu
        } // fin del if
        int turno = 1; // numero de turno para mostrar la posicion
        for (String[] s : colaPendientes) { // recorre la cola desde el primero hasta el ultimo
            System.out.println("Turno " + turno + (turno == 1 ? "  <- siguiente en ser atendida" : "")); // muestra el turno y marca el primero
            mostrarSolicitud(s); // muestra los datos de la solicitud
            turno++; // pasa al siguiente turno
        } // fin del for
    } // fin de mostrarPendientes

    // ================== OPCION 7: VER PILA ==================
    static void mostrarSolucionadas() { // recorre la pila desde arriba hacia abajo
        System.out.println("\n--- HISTORIAL DE SOLICITUDES SOLUCIONADAS ---"); // titulo de la opcion
        if (pilaSolucionadas.isEmpty()) { // si la pila esta vacia
            System.out.println("Todavia no se ha solucionado ninguna solicitud."); // mensaje de pila vacia
            return; // vuelve al menu
        } // fin del if
        for (int i = pilaSolucionadas.size() - 1; i >= 0; i--) { // recorre desde el tope (ultima solucionada) hasta la base
            if (i == pilaSolucionadas.size() - 1) { // si es el elemento del tope
                System.out.println("(ultima solucionada)"); // lo indica
            } // fin del if
            mostrarSolicitud(pilaSolucionadas.get(i)); // muestra la solicitud de esa posicion
        } // fin del for
        System.out.println("Total solucionadas: " + pilaSolucionadas.size()); // muestra cuantas hay en la pila
    } // fin de mostrarSolucionadas

    // ================== BUSQUEDAS ==================
    static String[] buscarEmpleado(String codigo) { // busca un empleado por su codigo
        for (String[] e : empleados) { // recorre el ArrayList de empleados
            if (e[0].equals(codigo)) { // compara el codigo
                return e; // si coincide lo devuelve
            } // fin del if
        } // fin del for
        return null; // si no lo encontro devuelve null
    } // fin de buscarEmpleado

    static String[] buscarEquipo(String codigo) { // busca un equipo por su codigo
        for (String[] q : equipos) { // recorre el ArrayList de equipos
            if (q[0].equals(codigo)) { // compara el codigo
                return q; // si coincide lo devuelve
            } // fin del if
        } // fin del for
        return null; // si no lo encontro devuelve null
    } // fin de buscarEquipo

    static String[] buscarSolicitud(String codigo) { // busca una solicitud en la cola y en la pila
        for (String[] s : colaPendientes) { // primero recorre la cola de pendientes
            if (s[0].equals(codigo)) { // compara el codigo
                return s; // si coincide la devuelve
            } // fin del if
        } // fin del for
        for (String[] s : pilaSolucionadas) { // despues recorre la pila de solucionadas
            if (s[0].equals(codigo)) { // compara el codigo
                return s; // si coincide la devuelve
            } // fin del if
        } // fin del for
        return null; // si no esta en ninguna devuelve null
    } // fin de buscarSolicitud

    // ================== LECTURA Y VALIDACION DE DATOS ==================
    static int leerEntero(String mensaje, int min, int max) { // lee un numero entero dentro de un rango
        while (true) { // repite hasta que el dato sea correcto
            System.out.print(mensaje); // muestra el mensaje
            try { // intenta convertir el texto a numero
                int numero = Integer.parseInt(sc.nextLine().trim()); // lee la linea completa y la convierte a entero
                if (numero >= min && numero <= max) { // revisa que este dentro del rango permitido
                    return numero; // devuelve el numero valido
                } // fin del if
                System.out.println("  El numero debe estar entre " + min + " y " + max + "."); // mensaje si esta fuera de rango
            } catch (NumberFormatException e) { // entra aqui si escribieron letras, simbolos o nada
                System.out.println("  Dato incorrecto, solo se permiten numeros."); // avisa sin cerrar el programa
            } // fin del try-catch
        } // fin del while
    } // fin de leerEntero

    static String leerTexto(String mensaje) { // lee un texto que no puede quedar vacio
        String texto = ""; // empieza vacio para entrar al ciclo
        while (texto.isEmpty()) { // se repite mientras el texto este vacio
            System.out.print(mensaje); // muestra el mensaje
            texto = sc.nextLine().trim(); // lee la linea y quita espacios de los lados
            if (texto.isEmpty()) { // si quedo vacio
                System.out.println("  Este campo es obligatorio."); // avisa que no puede quedar vacio
            } // fin del if
        } // fin del while
        return texto; // devuelve el texto ya validado
    } // fin de leerTexto

    static String leerCodigo(String mensaje) { // lee un codigo y lo deja en mayusculas
        return leerTexto(mensaje).toUpperCase().replace(" ", ""); // asi EMP01, emp01 y "emp 01" se toman como el mismo codigo
    } // fin de leerCodigo

    static String leerCorreo(String mensaje) { // lee un correo con un formato basico valido
        String correo = leerTexto(mensaje); // lee el correo sin permitir vacio
        while (!correo.contains("@") || correo.contains(" ") || correo.indexOf(".", correo.indexOf("@")) == -1) { // debe tener @, no tener espacios y tener un punto despues del @
            System.out.println("  Correo no valido. Ejemplo: nombre@saludconecta.com"); // explica el formato esperado
            correo = leerTexto(mensaje); // vuelve a pedir el correo
        } // fin del while
        return correo.toLowerCase(); // devuelve el correo en minusculas
    } // fin de leerCorreo

    static String elegirDeArreglo(String titulo, String[] opciones) { // muestra un arreglo numerado y devuelve lo que se escoja
        System.out.println(titulo); // muestra el titulo
        for (int i = 0; i < opciones.length; i++) { // recorre el arreglo
            System.out.println("  " + (i + 1) + ". " + opciones[i]); // imprime cada opcion con su numero
        } // fin del for
        int n = leerEntero("  Escoja: ", 1, opciones.length); // lee el numero validado con try-catch
        return opciones[n - 1]; // devuelve el valor del arreglo (se resta 1 porque el arreglo empieza en 0)
    } // fin de elegirDeArreglo

    static void pausa() { // detiene el programa hasta que se presione Enter
        System.out.print("\nPresione Enter para continuar..."); // mensaje de pausa
        sc.nextLine(); // espera el Enter
    } // fin de pausa
} // fin de la clase SoporteSaludConecta
