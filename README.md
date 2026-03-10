<div align='center'>
    <h1>Spring Boot App Tutorial</h1>
</div>

- [YouTube Watch List](https://www.youtube.com/watch?v=5M39KMhVOWA&list=PL2Z95CSZ1N4EWw14HZ0NFD3woFcn6uiCm)

## Documentacion base de Spring Boot

Spring Boot es un framework que permite crear aplicaciones Java de forma rapida, con una configuracion inicial sencilla y una estructura clara por capas. En este proyecto ya se observan ejemplos de `@Controller`, `@RestController`, `@RequestMapping` y DTOs.

### Estructura comun de un proyecto Spring Boot

Una estructura habitual es la siguiente:

```text
src/main/java/com/miapp
|-- controllers
|-- services
|-- repositories
|-- models
|-- dto
`-- AppApplication.java
```

### 1. Controllers

Los controllers reciben las peticiones HTTP del cliente, procesan parametros y delegan la logica a los servicios.

Hay dos tipos muy usados:

#### `@Controller`

Se utiliza cuando la respuesta sera una vista HTML, por ejemplo con Thymeleaf.

```java
@Controller
public class ClienteController {

    @GetMapping("/clientes")
    public String listarClientes(Model model) {
        model.addAttribute("titulo", "Listado de clientes");
        return "clientes/lista";
    }
}
```

Puntos importantes:

- Devuelve el nombre de una vista.
- Usa `Model` para enviar datos al HTML.
- Es ideal para aplicaciones MVC renderizadas en servidor.

#### `@RestController`

Se utiliza cuando la respuesta sera JSON, comun en APIs REST.

```java
@RestController
@RequestMapping("/api/clientes")
public class ClienteRestController {

    @GetMapping
    public List<ClienteDTO> listar() {
        return List.of();
    }
}
```

Puntos importantes:

- Devuelve objetos Java que Spring convierte a JSON.
- Se usa mucho para frontend separado, apps moviles o integraciones.
- Equivale a usar `@Controller` + `@ResponseBody`.

### 2. `@RequestMapping` y anotaciones de rutas

`@RequestMapping` sirve para definir una ruta base para un controlador o una ruta especifica con metodo HTTP.

Ejemplo:

```java
@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    @GetMapping
    public List<ProductoDTO> listar() {
        return List.of();
    }

    @GetMapping("/{id}")
    public ProductoDTO obtener(@PathVariable Long id) {
        return new ProductoDTO();
    }

    @PostMapping
    public ProductoDTO crear(@RequestBody ProductoDTO dto) {
        return dto;
    }
}
```

Las anotaciones mas usadas son:

- `@GetMapping`: consultar informacion.
- `@PostMapping`: crear registros.
- `@PutMapping`: actualizar registros completos.
- `@PatchMapping`: actualizar parcialmente.
- `@DeleteMapping`: eliminar registros.
- `@PathVariable`: leer valores desde la URL.
- `@RequestParam`: leer parametros de consulta.
- `@RequestBody`: recibir datos JSON en el cuerpo.

### 3. Models

Los models representan las entidades del sistema. Si el proyecto usa base de datos con JPA, normalmente se anotan con `@Entity`.

Ejemplo:

```java
public class Cliente {
    private Long id;
    private String nombre;
    private String correo;
}
```

Si se trabaja con JPA:

```java
@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String correo;
}
```

Responsabilidad del model:

- Representar los datos del negocio.
- Definir atributos de la entidad.
- En muchos proyectos, mapear tablas de base de datos.

### 4. DTO

DTO significa `Data Transfer Object`. Se usa para transportar datos entre capas o hacia el cliente, evitando exponer directamente el model.

Ejemplo:

```java
public class ProductoDTO {
    private Long id;
    private String nombre;
    private Double precio;
}
```

Ventajas de usar DTO:

- Evita enviar informacion innecesaria.
- Permite controlar mejor la respuesta JSON.
- Reduce acoplamiento entre la API y el modelo interno.
- Facilita validaciones y transformaciones.

Ejemplo practico:

- `Producto` puede tener campos internos como costo, proveedor o fecha de auditoria.
- `ProductoDTO` puede exponer solo `id`, `nombre` y `precio`.

### 5. Services

La capa service contiene la logica de negocio. El controller no deberia concentrar reglas complejas; debe delegarlas al servicio.

```java
@Service
public class VentaService {

    public Double calcularTotal(List<DetalleVenta> detalles) {
        return detalles.stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                .sum();
    }
}
```

Responsabilidad de los services:

- Aplicar reglas del negocio.
- Validar flujos funcionales.
- Coordinar models, DTOs y repositories.
- Mantener controllers limpios.

### 6. Repositories

Aunque en este proyecto todavia no aparezcan, en Spring Boot los repositories se usan para acceder a la base de datos.

```java
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
```

Responsabilidad:

- Guardar, buscar, actualizar y eliminar datos.
- Evitar escribir SQL manual en muchos casos gracias a Spring Data JPA.

### 7. Flujo recomendado en una aplicacion Spring Boot

El flujo comun es:

1. El cliente hace una peticion HTTP.
2. El controller recibe la peticion.
3. El controller llama al service.
4. El service aplica la logica del negocio.
5. El service usa repository para persistencia si hace falta.
6. Se devuelve un DTO o una vista HTML.

## Mini proyecto de ejemplo: Cliente, Producto, Venta y DetalleVenta

A continuacion se muestra una propuesta simple de estructura para un sistema de ventas.

### Models del dominio

#### `Cliente`

```java
public class Cliente {
    private Long id;
    private String nombre;
    private String apellido;
    private String correo;
}
```

#### `Producto`

```java
public class Producto {
    private Long id;
    private String nombre;
    private Double precio;
    private Integer stock;
}
```

#### `DetalleVenta`

```java
public class DetalleVenta {
    private Long id;
    private Producto producto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}
```

#### `Venta`

```java
public class Venta {
    private Long id;
    private Cliente cliente;
    private List<DetalleVenta> detalles;
    private Double total;
}
```

### DTOs sugeridos

#### `ClienteDTO`

```java
public class ClienteDTO {
    private Long id;
    private String nombreCompleto;
    private String correo;
}
```

#### `ProductoDTO`

```java
public class ProductoDTO {
    private Long id;
    private String nombre;
    private Double precio;
}
```

#### `VentaRequestDTO`

Sirve para crear una venta desde una peticion JSON:

```java
public class VentaRequestDTO {
    private Long clienteId;
    private List<DetalleVentaRequestDTO> detalles;
}
```

```java
public class DetalleVentaRequestDTO {
    private Long productoId;
    private Integer cantidad;
}
```

#### `VentaResponseDTO`

Sirve para responder al cliente con la informacion procesada:

```java
public class VentaResponseDTO {
    private Long ventaId;
    private String cliente;
    private Double total;
    private List<String> productos;
}
```

### Services del mini proyecto

#### `ClienteService`

```java
@Service
public class ClienteService {

    public List<ClienteDTO> listarClientes() {
        return List.of();
    }
}
```

#### `ProductoService`

```java
@Service
public class ProductoService {

    public List<ProductoDTO> listarProductos() {
        return List.of();
    }
}
```

#### `VentaService`

```java
@Service
public class VentaService {

    public VentaResponseDTO registrarVenta(VentaRequestDTO request) {
        return new VentaResponseDTO();
    }
}
```

### Controllers del mini proyecto

#### API REST para clientes

```java
@RestController
@RequestMapping("/api/clientes")
public class ClienteRestController {

    private final ClienteService clienteService;

    public ClienteRestController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<ClienteDTO> listar() {
        return clienteService.listarClientes();
    }
}
```

#### API REST para productos

```java
@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    private final ProductoService productoService;

    public ProductoRestController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ProductoDTO> listar() {
        return productoService.listarProductos();
    }
}
```

#### API REST para ventas

```java
@RestController
@RequestMapping("/api/ventas")
public class VentaRestController {

    private final VentaService ventaService;

    public VentaRestController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public VentaResponseDTO registrar(@RequestBody VentaRequestDTO request) {
        return ventaService.registrarVenta(request);
    }
}
```

### Ejemplo de peticion JSON para registrar una venta

```json
{
  "clienteId": 1,
  "detalles": [
    {
      "productoId": 2,
      "cantidad": 3
    },
    {
      "productoId": 5,
      "cantidad": 1
    }
  ]
}
```

### Ejemplo de respuesta JSON

```json
{
  "ventaId": 10,
  "cliente": "Juan Perez",
  "total": 185000.0,
  "productos": [
    "Teclado",
    "Monitor"
  ]
}
```

## Resumen conceptual

- `Model`: representa datos del negocio.
- `DTO`: representa datos que se transfieren entre capas o al cliente.
- `Controller`: retorna vistas HTML.
- `RestController`: retorna JSON.
- `Service`: contiene la logica del negocio.
- `Repository`: accede a la base de datos.
- `@RequestMapping`: define rutas base o generales.
- `@GetMapping`, `@PostMapping`, etc.: definen acciones HTTP especificas.

## Recomendaciones de buenas practicas

- No colocar toda la logica en el controller.
- Usar DTOs para entradas y salidas de la API.
- Separar bien responsabilidades por capas.
- Nombrar rutas REST en plural: `/api/clientes`, `/api/productos`, `/api/ventas`.
- Validar datos de entrada con anotaciones como `@Valid`.
- Mantener los controllers cortos y los services enfocados en reglas del negocio.

## Relacion con este proyecto

Este repositorio ya muestra varios conceptos importantes:

- `EjemploController`: uso de `@Controller`, `Model` y vistas Thymeleaf.
- `EjemploRestController`: uso de `@RestController` y `@RequestMapping("/api")`.
- `ClaseDTO`: ejemplo basico de DTO.
- `Empleados`: ejemplo simple de model.

Con esa base, el siguiente paso natural es crear paquetes adicionales como `services` y `repositories`, y luego construir modulos reales como `clientes`, `productos` y `ventas`.

## Guia para construir una mini app funcional

Si la meta ya no es solo aprender anotaciones sino levantar una aplicacion completa, estas son las piezas minimas que deberia tener el proyecto.

### Dependencias recomendadas

Para una mini app funcional de ventas con base de datos, validaciones y seguridad, lo normal es trabajar con estas dependencias:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

En tu `pom.xml` actual ya existen dependencias web y Thymeleaf, pero para una app funcional de negocio faltaria normalmente:

- `spring-boot-starter-data-jpa`
- `spring-boot-starter-validation`
- `spring-boot-starter-security`
- Un driver de base de datos como MySQL o PostgreSQL

### Paquetes recomendados

```text
src/main/java/com/miapp
|-- config
|-- controllers
|-- dto
|-- exceptions
|-- models
|-- repositories
|-- security
|-- services
`-- AppApplication.java
```

Cada paquete deberia tener una responsabilidad clara:

- `config`: configuraciones generales.
- `controllers`: entrada HTTP.
- `dto`: objetos de entrada y salida.
- `exceptions`: manejo de errores.
- `models`: entidades del negocio.
- `repositories`: acceso a datos.
- `security`: autenticacion y autorizacion.
- `services`: reglas del negocio.

## Dominio del mini sistema de ventas

Para el caso de `cliente`, `producto`, `venta` y `detalle_venta`, un diseño simple puede ser este.

### Entidades principales

#### `Cliente`

```java
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;

    @Column(unique = true, nullable = false)
    private String correo;
}
```

#### `Producto`

```java
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Double precio;
    private Integer stock;
}
```

#### `Venta`

```java
@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private LocalDateTime fecha;
    private Double total;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    private List<DetalleVenta> detalles;
}
```

#### `DetalleVenta`

```java
@Entity
@Table(name = "detalle_ventas")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}
```

### Relacion entre entidades

- Un `Cliente` puede tener muchas `Venta`.
- Una `Venta` pertenece a un solo `Cliente`.
- Una `Venta` tiene muchos `DetalleVenta`.
- Un `DetalleVenta` referencia un `Producto`.

## Capa DTO para entrada y salida

Para una app seria no conviene exponer entidades directamente.

### DTOs de entrada

```java
public class ClienteRequestDTO {
    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @Email
    @NotBlank
    private String correo;
}
```

```java
public class DetalleVentaRequestDTO {
    @NotNull
    private Long productoId;

    @NotNull
    @Min(1)
    private Integer cantidad;
}
```

```java
public class VentaRequestDTO {
    @NotNull
    private Long clienteId;

    @NotEmpty
    private List<DetalleVentaRequestDTO> detalles;
}
```

### DTOs de salida

```java
public class ClienteResponseDTO {
    private Long id;
    private String nombreCompleto;
    private String correo;
}
```

```java
public class ProductoResponseDTO {
    private Long id;
    private String nombre;
    private Double precio;
    private Integer stock;
}
```

```java
public class VentaResponseDTO {
    private Long ventaId;
    private String cliente;
    private LocalDateTime fecha;
    private Double total;
}
```

## Repositories

Con Spring Data JPA:

```java
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCorreo(String correo);
}
```

```java
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
```

```java
public interface VentaRepository extends JpaRepository<Venta, Long> {
}
```

## JPA e Hibernate

En este tipo de proyecto tambien aplican directamente `JPA` e `Hibernate`, porque son la base de la persistencia cuando trabajas con Spring Boot y base de datos relacional.

### Que es JPA

`JPA` significa `Java Persistence API`. No es una implementacion concreta sino una especificacion para mapear objetos Java a tablas de base de datos.

Con JPA puedes:

- Mapear clases Java como entidades.
- Definir claves primarias.
- Relacionar tablas con objetos.
- Consultar datos sin escribir tanto SQL manual.

### Que es Hibernate

`Hibernate` es una implementacion de JPA. En la practica, cuando agregas `spring-boot-starter-data-jpa`, Spring Boot suele usar Hibernate como proveedor ORM por defecto.

Hibernate se encarga de:

- Convertir objetos Java en registros de base de datos.
- Generar SQL.
- Manejar relaciones entre entidades.
- Gestionar el contexto de persistencia.
- Ayudar con operaciones `save`, `find`, `update` y `delete`.

### Como se conectan Spring Boot, JPA y Hibernate

El flujo general es este:

1. Spring Boot configura la conexion a la base de datos.
2. Spring Data JPA expone repositories.
3. Hibernate implementa internamente el mapeo ORM.
4. Tus entidades Java se guardan y consultan como tablas relacionales.

### Anotaciones mas usadas en JPA/Hibernate

- `@Entity`: marca una clase como entidad persistente.
- `@Table`: define el nombre de la tabla.
- `@Id`: marca la clave primaria.
- `@GeneratedValue`: indica como se genera el ID.
- `@Column`: personaliza columnas.
- `@OneToMany`: relacion uno a muchos.
- `@ManyToOne`: relacion muchos a uno.
- `@OneToOne`: relacion uno a uno.
- `@ManyToMany`: relacion muchos a muchos.
- `@JoinColumn`: define la columna FK.
- `@Enumerated`: persistir enums.
- `@Temporal` o tipos `LocalDate`/`LocalDateTime`: fechas.
- `@Transient`: campo no persistido.

### Ejemplo practico con relaciones

Una `Venta` tiene muchos `DetalleVenta`, y cada detalle apunta a un `Producto`.

```java
@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles;
}
```

```java
@Entity
@Table(name = "detalle_ventas")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
}
```

### `JpaRepository`

`JpaRepository` simplifica mucho el acceso a datos. Sin escribir implementaciones manuales ya puedes:

- `findAll()`
- `findById(id)`
- `save(entidad)`
- `deleteById(id)`
- `existsById(id)`

Y ademas puedes crear consultas por nombre de metodo:

```java
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByStockLessThan(Integer stock);
}
```

### Ciclo de vida de una entidad

En Hibernate una entidad suele pasar por estos estados:

- `transient`: el objeto existe en memoria pero no en BD.
- `managed`: Hibernate lo esta gestionando dentro del contexto de persistencia.
- `detached`: el objeto existe pero ya no esta gestionado activamente.
- `removed`: marcado para eliminacion.

Esto importa porque Hibernate decide cuando sincronizar cambios con la base de datos.

### `@Transactional`

Cuando una operacion de negocio afecta varias tablas, se usa `@Transactional`.

```java
@Transactional
public VentaResponseDTO registrarVenta(VentaRequestDTO request) {
    // guarda venta
    // guarda detalles
    // actualiza stock
    return response;
}
```

Si algo falla dentro de la transaccion, Spring puede revertir todos los cambios para evitar datos inconsistentes.

### Diferencia entre JPA, Hibernate y Spring Data JPA

- `JPA`: especificacion.
- `Hibernate`: implementacion ORM de JPA.
- `Spring Data JPA`: capa que simplifica repositories e integracion con Spring.

### Buenas practicas con JPA/Hibernate

- Evita exponer entidades JPA directamente en respuestas REST si puedes usar DTOs.
- Usa `fetch` con criterio; no cargues relaciones pesadas sin necesidad.
- Declara relaciones correctamente para evitar inconsistencias.
- Usa `@Transactional` en operaciones de negocio complejas.
- No metas logica de persistencia dentro del controller.
- Para produccion, usa migraciones en vez de depender de `ddl-auto=update`.

### Como aplica esto a este proyecto

En tu mini app:

- `Cliente`, `Producto`, `Venta`, `DetalleVenta` y `Usuario` deberian ser entidades JPA.
- Los paquetes `repositories` deberian extender `JpaRepository`.
- Hibernate se encargaria del mapeo objeto-relacional.
- Los services usarian transacciones para registrar ventas y actualizar stock.

## Services y logica del negocio

La capa service debe resolver reglas reales del sistema.

### Ejemplo de reglas importantes

- Verificar que el cliente exista antes de vender.
- Verificar que el producto exista.
- Verificar que haya stock suficiente.
- Calcular subtotal por item.
- Calcular total de la venta.
- Descontar stock al registrar la venta.

### Ejemplo de `VentaService`

```java
@Service
public class VentaService {

    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;

    public VentaService(
            ClienteRepository clienteRepository,
            ProductoRepository productoRepository,
            VentaRepository ventaRepository) {
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
    }

    @Transactional
    public VentaResponseDTO registrarVenta(VentaRequestDTO request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        List<DetalleVenta> detalles = new ArrayList<>();
        double total = 0;

        for (DetalleVentaRequestDTO item : request.getDetalles()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para " + producto.getNombre());
            }

            double subtotal = producto.getPrecio() * item.getCantidad();

            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(subtotal);

            producto.setStock(producto.getStock() - item.getCantidad());

            detalles.add(detalle);
            total += subtotal;
        }

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(total);
        venta.setDetalles(detalles);

        Venta ventaGuardada = ventaRepository.save(venta);

        VentaResponseDTO response = new VentaResponseDTO();
        response.setVentaId(ventaGuardada.getId());
        response.setCliente(cliente.getNombre() + " " + cliente.getApellido());
        response.setFecha(ventaGuardada.getFecha());
        response.setTotal(ventaGuardada.getTotal());
        return response;
    }
}
```

## Controllers para una app funcional

### Controller MVC

Sirve si quieres renderizar vistas con Thymeleaf.

```java
@Controller
@RequestMapping("/ventas")
public class VentaController {

    @GetMapping("/nueva")
    public String formularioNuevaVenta(Model model) {
        model.addAttribute("ventaForm", new VentaRequestDTO());
        return "ventas/nueva";
    }
}
```

### REST Controller

Sirve para una API JSON.

```java
@RestController
@RequestMapping("/api/ventas")
public class VentaRestController {

    private final VentaService ventaService;

    public VentaRestController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public ResponseEntity<VentaResponseDTO> registrar(@Valid @RequestBody VentaRequestDTO request) {
        return ResponseEntity.ok(ventaService.registrarVenta(request));
    }
}
```

## Base de datos y configuracion

### `application.properties`

Ejemplo con MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ventas_db
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.thymeleaf.cache=false
```

Campos importantes:

- `spring.datasource.*`: configuracion de conexion.
- `spring.jpa.hibernate.ddl-auto=update`: crea o ajusta tablas en desarrollo.
- `spring.jpa.show-sql=true`: muestra SQL en consola.

En produccion, normalmente no se usa `ddl-auto=update`; se prefieren migraciones con Flyway o Liquibase.

## Validaciones

Spring Boot permite validar DTOs con Jakarta Validation.

```java
public class ProductoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    private Double precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
}
```

En el controller:

```java
@PostMapping
public ResponseEntity<ProductoResponseDTO> crear(@Valid @RequestBody ProductoRequestDTO request) {
    return ResponseEntity.ok(productoService.crear(request));
}
```

Anotaciones frecuentes:

- `@NotNull`
- `@NotBlank`
- `@NotEmpty`
- `@Email`
- `@Min`
- `@Max`
- `@Positive`
- `@Size`

## Manejo global de errores

No es buena practica devolver excepciones crudas. Lo correcto es manejar errores de forma centralizada.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
```

Idealmente se crean excepciones propias:

- `ResourceNotFoundException`
- `BusinessException`
- `UnauthorizedOperationException`

## Spring Security

Si la app es funcional y maneja ventas, clientes y productos, la seguridad deja de ser opcional.

### Que aporta Spring Security

- Protege rutas.
- Obliga autenticacion.
- Permite roles y permisos.
- Gestiona login y logout.
- Integra cifrado de contrasenas.
- Protege contra ataques comunes como CSRF cuando se usa sesion.

### Dependencia

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### Modelo de usuario y roles

Para una mini app de ventas, un modelo simple seria:

#### `Usuario`

```java
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;
}
```

Roles recomendados:

- `ROLE_ADMIN`: administra usuarios, productos y reportes.
- `ROLE_VENDEDOR`: registra ventas y consulta clientes/productos.

### Repository de usuario

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
}
```

### `UserDetailsService`

Spring Security necesita una forma de cargar el usuario desde base de datos.

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRole().replace("ROLE_", ""))
                .build();
    }
}
```

### Configuracion de seguridad

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/css/**", "/js/**").permitAll()
                .requestMatchers("/api/productos/**").hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers("/api/ventas/**").hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

Puntos importantes:

- `SecurityFilterChain` define que rutas se pueden usar y con que rol.
- `PasswordEncoder` cifra contrasenas.
- `formLogin` es util si la app usa Thymeleaf y sesion.

### Crear contrasenas seguras

Nunca se debe guardar una contrasena en texto plano.

```java
String passwordCifrado = passwordEncoder.encode("123456");
```

Se recomienda usar `BCryptPasswordEncoder`.

### Control por roles

Tambien puedes proteger metodos en servicios o controllers:

```java
@PreAuthorize("hasRole('ADMIN')")
public void eliminarProducto(Long id) {
}
```

Para eso normalmente se habilita seguridad a nivel metodo:

```java
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
}
```

### Login con Thymeleaf

Si tu app usa vistas, puedes tener un formulario simple:

```html
<form th:action="@{/login}" method="post">
    <input type="text" name="username" placeholder="Usuario">
    <input type="password" name="password" placeholder="Contrasena">
    <button type="submit">Ingresar</button>
</form>
```

### Si la app fuera 100% API REST

En una API pura normalmente se usa autenticacion con JWT en lugar de login por formulario y sesion. Para aprender primero, `formLogin` es mas sencillo. Para un frontend separado o movil, JWT suele ser mejor opcion.

### Reglas minimas de seguridad para este mini proyecto

- Solo usuarios autenticados pueden registrar ventas.
- Solo `ADMIN` puede crear, actualizar o eliminar usuarios.
- `ADMIN` y `VENDEDOR` pueden consultar productos.
- Solo `ADMIN` puede eliminar productos.
- Las contrasenas deben almacenarse cifradas.
- Las rutas publicas deben ser pocas y bien definidas.

## Vistas con Thymeleaf

Si se quiere una mini app visual, Thymeleaf puede cubrir:

- Login
- Listado de clientes
- Listado de productos
- Formulario de nueva venta
- Historial de ventas

Ejemplo de tabla:

```html
<table>
    <thead>
        <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Precio</th>
        </tr>
    </thead>
    <tbody>
        <tr th:each="producto : ${productos}">
            <td th:text="${producto.id}"></td>
            <td th:text="${producto.nombre}"></td>
            <td th:text="${producto.precio}"></td>
        </tr>
    </tbody>
</table>
```

## Flujo completo de una venta

1. El usuario inicia sesion.
2. Spring Security valida credenciales.
3. El usuario con rol permitido accede al modulo de ventas.
4. El controller recibe el formulario o JSON.
5. El DTO valida los datos de entrada.
6. El service consulta cliente y productos.
7. El service valida stock y calcula totales.
8. El repository guarda venta y detalle.
9. Se actualiza stock de productos.
10. La app devuelve una vista o respuesta JSON.

## Que deberia tener una version minima pero seria de esta app

- CRUD de clientes.
- CRUD de productos.
- Registro de ventas.
- Calculo de total y subtotales.
- Validacion de stock.
- Login.
- Roles y permisos.
- Manejo de errores.
- Base de datos persistente.
- DTOs para entrada y salida.
- Seguridad con contrasenas cifradas.

## Orden recomendado para construirla

1. Crear entidades `Cliente`, `Producto`, `Venta`, `DetalleVenta` y `Usuario`.
2. Crear repositories JPA.
3. Crear DTOs de request y response.
4. Crear services con reglas del negocio.
5. Crear controllers MVC o REST.
6. Configurar base de datos.
7. Agregar validaciones.
8. Agregar manejo global de errores.
9. Configurar Spring Security.
10. Crear vistas Thymeleaf o probar endpoints con Postman.

## Recomendaciones finales

- Empieza con una version simple y funcional.
- No mezcles logica de negocio dentro del controller.
- No expongas entidades directamente si puedes usar DTOs.
- No guardes contrasenas sin cifrar.
- Si vas a crecer el proyecto, agrega pruebas unitarias y de integracion.
- Si vas a desplegarlo, separa configuracion de desarrollo y produccion.
