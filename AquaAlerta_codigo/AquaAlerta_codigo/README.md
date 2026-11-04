# AquaAlerta Mallorquín

**Sistema de alerta temprana para la calidad del agua de la Ciénaga de Mallorquín**

## a. Datos del proyecto

- **Programa:** Ingeniería de Datos e Inteligencia Artificial — Facultad de Ingeniería.
- **Proyecto:** Proyecto Integrador / Proyecto de aula, semestre 3 (2026-2).
- **ODS:** ODS 14, Vida submarina.
- **Cursos articulados:** Estructura de Datos y Base de Datos.
- **Docentes:** Omar Gutiérrez Acosta y Juan Carlos Calabria Sarmiento.

| Integrante | Rol | Responsabilidades principales |
|---|---|---|
| Rebeca Torres Díaz | Coordinadora | Planificación, cronograma, comunicación con el docente; implementación del grafo y recorrido BFS; póster e informe. |
| Merly Chacón González | Desarrolladora de estructuras | Implementación de nodos, cola circular, cola de prioridad y lista doblemente enlazada; justificación y complejidad. |
| Luisa Fernanda Gonzalez Fandiño | Desarrolladora de aplicación | Modelo y scripts Oracle, conexión JDBC y DAO, interfaz gráfica y pruebas. |

## b. Descripción

AquaAlerta Mallorquín es un prototipo académico que permite registrar y consultar lecturas simuladas de calidad del agua en distintos puntos de la Ciénaga de Mallorquín, identificar lecturas que superan umbrales configurados y organizar las alertas por severidad y estado. El sistema busca facilitar la revisión de eventos potencialmente preocupantes y la consulta de su historial, apoyando la comprensión y el seguimiento de la calidad del agua. Los datos incluidos son de prueba y no representan mediciones oficiales.

## c. Instrucciones de ejecución

### Requisitos

- Java Development Kit (JDK) 17.
- Apache NetBeans con soporte para proyectos Java Ant.
- Oracle Database XE 21c o superior (o una instancia Oracle compatible).
- Driver JDBC de Oracle (Oracle JDBC / `ojdbc`; versión compatible con la base de datos y Java 17), agregado a las bibliotecas del proyecto en NetBeans.

### Crear y poblar la base de datos

1. Inicie Oracle y cree/seleccione un esquema de usuario para el proyecto.
2. Abra Oracle SQL Developer o APEX SQL Workshop con ese usuario.
3. Ejecute `sql/01_crear_esquema.sql` completo para crear tablas, restricciones, índices y la vista `V_ALERTA_DETALLE`.
4. Ejecute `sql/02_datos_prueba.sql` para insertar puntos, parámetros, conexiones, lecturas y alertas simuladas.
5. `sql/03_consultas.sql` contiene consultas de referencia; ejecútelas individualmente cuando necesite revisar resultados.

Los scripts de creación eliminan los objetos del mismo nombre antes de recrearlos. Ejecútelos únicamente en el esquema destinado al proyecto, pues se borran los datos existentes de esas tablas.

### Configurar y ejecutar en NetBeans

1. Abra en NetBeans la carpeta `AquaAlerta_codigo` como proyecto existente.
2. En `src/pia/conexion/ConexionBD.java`, configure `URL`, `USUARIO` y `CLAVE` según su instalación. La URL predeterminada es `jdbc:oracle:thin:@localhost:1521:XE`; en Oracle XE puede variar según el servicio configurado (por ejemplo, `XEPDB1`).
3. Añada el archivo `ojdbc` a **Libraries** del proyecto (clic derecho en el proyecto → Properties → Libraries → Add JAR/Folder).
4. Verifique que la base de datos esté activa y que el usuario tenga permisos sobre las tablas y la vista creadas.
5. Ejecute el proyecto. La clase principal configurada es `pia.gui.Main`.

**Nota de seguridad:** no publique contraseñas reales en GitHub. Para una entrega académica, deje credenciales de ejemplo o configure las credenciales localmente antes de ejecutar.

## d. Estructuras implementadas

| Estructura | Clase Java | Entidad o tabla gestionada | Uso en el prototipo |
|---|---|---|---|
| Cola circular | `pia.estructuras.ColaCircularLecturas` | `LECTURA` / `pia.entidades.Lectura` | Mantiene un conjunto acotado de lecturas recientes; al llenarse, reemplaza la más antigua. |
| Cola de prioridad (heap) | `pia.estructuras.ColaPrioridadAlertas` | `ALERTA` / `pia.entidades.Alerta` | Prioriza alertas por severidad: crítica, media y baja. |
| Lista doblemente enlazada | `pia.estructuras.ListaDobleAlertas` | `ALERTA` (atendidas) / `pia.entidades.Alerta` | Organiza y permite recorrer el historial de alertas atendidas. |
| Grafo de lista de adyacencia | `pia.estructuras.Grafo`, `NodoVertice`, `NodoArista` | `PUNTO_MONITOREO`, `CONEXION_PUNTO` / `pia.entidades.PuntoMonitoreo` | Representa la conectividad entre puntos y permite recorrerla mediante BFS. |

## Organización del repositorio

- `src/pia/`: código fuente Java organizado en paquetes (`conexion`, `entidades`, `estructuras`, `gui`).
- `sql/`: scripts DDL, DML y consultas de referencia.
- `nbproject/`, `build.xml`, `manifest.mf`: configuración del proyecto NetBeans/Ant.
