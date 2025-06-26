# RideInn

> **RideInn** es una plataforma modular que **combina trayectos económicos y alojamientos** mediante el consumo en tiempo real de las APIs públicas de **BlaBlaCar** y **Xotelo**.  
> El proyecto está desarrollado en **Java 21**, siguiendo principios de **Clean Code** y **Arquitectura Hexagonal**, usando **ActiveMQ** como sistema de mensajería, persistiendo la información en **SQLite y archivos planos**, y visualizándola a través de línea de comandos, con **CLI**.

---

## Autores

| Nombre | GitHub |
|--------|--------|
| Adriana Peñate | [@adrianapenate](https://github.com/adrianapenate) |
| Sofía Travieso | [@sofiatravieso](https://github.com/sofiatravieso) |

---

## Índice

1. [Propuesta de valor](#propuesta-de-valor)  
2. [Funcionalidades](#funcionalidades)  
3. [Arquitectura](#arquitectura)  
4. [Módulos](#módulos)  
5. [Justificación de APIs y persistencia](#justificación-de-apis-y-persistencia)  
6. [Requisitos previos](#requisitos-previos)  
7. [Instalación y compilación](#instalación-y-compilación)  
8. [Variables de entorno](#variables-de-entorno)  
9. [Ejecución del sistema](#ejecución-del-sistema)  
10. [Uso de la GUI](#uso-de-la-gui)  
11. [Estructura de almacenamiento](#estructura-de-almacenamiento)  
12. [Tecnologías](#tecnologías)  
13. [Pruebas](#pruebas)

---

## Propuesta de valor

- **Planificación completa** → ofrece una solución todo-en-uno que vincula rutas y hospedaje.  
- **Datos actualizados** → se consultan las APIs periódicamente, brindando datos en vivo.  
- **Escalable y modular** → cualquier nueva fuente se integra con un adapter y un topic adicional.  
- **Educativo y profesional** → arquitectura clara con buenas prácticas en Java.

---

## Funcionalidades

- **Consulta de trayectos** asequibles mediante la API de BlaBlaCar.  
- **Búsqueda de alojamiento** usando la API pública de Xotelo.  
- **Publicación de eventos** en ActiveMQ (topics `Xotelo` y `Blablacar`).  
- **Almacenamiento** de todos los mensajes en ficheros `.events` y base de datos SQLite.  
- **CLI** que permite introudcir diversos filtros.

---

## Arquitectura

El proyecto sigue los principios de **Clean Code** así como de **Arquitectura Hexagonal**, mejorando la infraestructura y presentación.  
Componentes independientes se comunican mediante mensajería asíncrona (ActiveMQ).

- **Feeders**: recogen y procesan datos desde APIs externas (BlaBlaCar, Xotelo).  
- **Event Store**: almacena los datos crudos recibidos en formato `.events`.  
- **Business Unit**: fusiona datos históricos y en vivo, persiste en SQLite y muestra resultados en CLI.

---
## Diagrama de cajas (FALTA)
![Diagrama de cajas](enlace)
---
## Diagrama feederss (FALTA)
![Diagrama ](enlace)
![Diagrama ](enlace)
---
## Diagrama business unit (FALTA)
![Diagrama ](enlace)
---
## Diagrama event store builder (FALTA)
![Diagrama ](enlace)


---

## Módulos

| Módulo | Patrón | Función principal |
|--------|--------|-------------------|
| `blablacar-feeder` | Adapter + Publisher | Publica trayectos en el topic `Blablacar` |
| `xotelo-feeder` | Adapter + Publisher | Publica hospedajes en el topic `Xotelo` |
| `event-store` | Consumer | Registra todos los eventos como archivos `.events` |
| `travel-packages` | Consumer + CLI | Persiste en SQLite y ofrece la interfaz de usuario |

---

## Justificación de APIs y persistencia

- **BlaBlaCar** → API enfocada en viajes colaborativos y económicos.  
- **Xotelo** → fuente abierta de información hotelera, ideal para integraciones.  
- **Event Store + SQLite** → permite análisis histórico, depuración y recuperación ante fallos.

---

## Requisitos previos

- Java 21  
- Apache Maven 3.6 o superior  
- ActiveMQ 5.17.6  
- Conexión estable a Internet

---

## Instalación y compilación

```bash
git clone https://github.com/ProyectoDACDSA/RideInn
cd RideInn
mvn clean install
```

---

## Variables de entorno
| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `BLABLACAR_API_KEY` | Token BlaBlaCar | `abc123` |
| `DB_URL` | URL DATABASE | `database.db` |

---

## Formatos de mensajes publicados

### Evento BlaBlaCar (`Blablacar`)

```json
{
  "ts":1750887303220,
  "ss":"Blablacar",
  "origin":"Toulouse",
  "destination":"Lyon",
  "departureTime":"2025-10-02T20:20+02:00",
  "price":24.98,
  "avalable":true
}
```

### Evento Xotelo (`Xotelo`)

```json
{
  "ts":1750887802931,
  "ss":"Xotelo",
  "hotelName":"Hôtel Parksaône",
  "key":"g187265-d12188472",
  "accommodationType":"Hotel",
  "url":"https://www.tripadvisor.com/Hotel_Review-g187265-d12188472-Reviews-Hotel_Parksaone-Lyon_Rhone_Auvergne_Rhone_Alpes.html",
  "rating":4.5,
  "averagePricePerNight":144,
  "city":"Lyon"
}
```

---

## Cómo ejecutar el proyecto

### 1. Iniciar ActiveMQ

Descarga el .zip en <https://activemq.apache.org/components/classic/download/classic-05-17-06> y para ejecutarlo dependiendo tu sistema se ejecuta con una instrucción diferente.

Antes de ejecutar esta instrucción desde la consola tienes que estar dentro de la carpeta.

Windows:
```
bin\activemq start
```
Linux / macOS:
```
./activemq start
```
### 2. Verificar que está activo

Abrir un navegador y entrar en: <http://localhost:8161/>
>(Para inciar sesión: usuario admin / contraseña admin).

---

## Flujo de la CLI paso a paso

1. **Seleccionar Opcion 1: Recomendaciones Actuales**  
   - Tienes diferentes filtros que ayudarán a guiar la búsqueda del usuario
   - Al mostrar los resultados por pantalla, se permite buscar viajes de vuelta de destino a origen
2. **Seleccionar Opción 2: Viajes Mejor Valorados**  
   - A partir de filtros, se muestran bajo la búsqueda las opciones mejor valoradas 

Con tres clics el usuario descubre un recomendaciones, pudiendo elegir la opción de viaje más económica, bajo los filtros aplicados.

---

## Estructura de archivos generados
```
eventstore/
└── Xotelo/ | Blablacar/
    └── HHSS/YYYYMMDD.events

datamart.db
```
> Cada línea de un `.events` es un objeto JSON serializado.

---

## Principios y patrones por módulo (FALTA)
| Módulo | Patrones | Principios                      |
|--------|----------|---------------------------------|
| Feeders | Adapter, Publisher (eventos con ActiveMQ) | SRP, inmutabilidad, Open/Closed |
| Event Store | Consumer, Event Sourcing (almacenamiento en fichero) | Open/Closed, SRP                |
| Business Unit | Facade (controladores), MVC (GUI) | DRY, SRP                   |

---

## Tecnologías
Java 21 · Maven · ActiveMQ · SQLite · Gson · Git

---

## Tests

```bash
mvn test
```
Se ejecutan tests unitarios (JUnit) en cada módulo.
