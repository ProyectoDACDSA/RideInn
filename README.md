# RideInn

> **RideInn** es una plataforma modular que **combina trayectos y alojamientos** mediante el consumo en tiempo real de las APIs públicas de **BlaBlaCar** y **Xotelo**. El proyecto está desarrollado en **Java 21**, siguiendo principios de **Clean Code** y **Arquitectura Hexagonal**, usando **ActiveMQ** como sistema de mensajería, persistiendo la información en **SQLite y archivos planos**, y visualizándola a través de línea de comandos, con **CLI**.

---

## Autores
> Adriana Peñate → [@adrianapenate](https://github.com/adrianapenate)

> Sofía Travieso → [@sofiatravieso](https://github.com/sofiatravieso)

---

## Índice

1. [Propuesta de valor](#propuesta-de-valor)  
2. [Funcionalidades](#funcionalidades)
3. [Justificación de APIs y persistencia](#justificación-de-apis-y-persistencia)
4. [Tecnologías](#tecnologías) 
5. [Arquitectura](#arquitectura)
6. [Principios y patrones por módulo](#principios-y-patrones-por-módulo)
7. [Estructura de eventos generados](#estructura-de-eventos-generados)
8. [Formato de mensajes publicados](#formato-de-mensajes-publicados)
9. [Instalación y compilación](#instalación-y-compilación)
10. [Variables de entorno](#variables-de-entorno)
11. [Ejecución del sistema](#ejecución-del-sistema)
12. [Uso de la GUI](#uso-de-la-gui)
13. [Pruebas](#pruebas)

---

## 1. Propuesta de valor

Nuestra solución ofrece una planificación integral que conecta rutas y hospedajes en una sola plataforma. Se apoya en datos actualizados en tiempo real, gracias a la consulta periódica de APIs. Además, el sistema es modular y escalable, permitiendo integrar nuevas fuentes fácilmente mediante adapters y topics adicionales. Todo esto se construye sobre una arquitectura profesional y educativa, con buenas prácticas de desarrollo en Java.

---

## 2. Funcionalidades

- **Consulta de trayectos asequibles**  
  Utiliza la API de BlaBlaCar para acceder a rutas.

- **Búsqueda de alojamiento**  
  Integra la API pública de Xotelo para encontrar opciones de hospedaje.

- **Publicación de eventos en ActiveMQ**  
  - Los eventos se organizan en los topics `Xotelo` y `Blablacar`.  
  - Facilita la comunicación y actualización en tiempo real.

- **Almacenamiento persistente**  
  - Los mensajes se guardan en ficheros `.events`.  
  - También se almacenan en una base de datos SQLite.

- **Interfaz de línea de comandos (CLI)**  
  Permite introducir diversos filtros para personalizar la búsqueda según necesidades específicas.

---

## 3. Justificación de APIs y persistencia

- **BlaBlaCar** → API enfocada en viajes colaborativos y económicos.  
- **Xotelo** → fuente abierta de información hotelera, ideal para integraciones.  
- **Event Store + SQLite** → permite análisis histórico, depuración y recuperación ante fallos.

---

## 4. Tecnologías

- Java 21  
- Apache Maven 3.6 o superior  
- ActiveMQ 5.17.6  
- SQLite
- Gson
- Git

---

## 5. Arquitectura

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

## 6. Principios y patrones por módulo

| Módulo | Patrón | Función principal | Principios Aplicados |
|--------|--------|-------------------|-------------------------| 
| `blablacar-feeder` | Adapter + Publisher | Publica trayectos en el topic `Blablacar` | SRP, DIP, Inmutabilidad, Open/Closed |
| `xotelo-feeder` | Adapter + Publisher | Publica hospedajes en el topic `Xotelo` | SRP, DIP, Inmutabilidad, Open/Closed |
| `event-store-builder` | Consumer | Registra todos los eventos como archivos `.events` | SRP, DIP, DRY Open/Closed |
| `travel-packages` | Consumer + CLI | Persiste en SQLite y ofrece la interfaz de usuario | SRP, DIP, DRY, Inmutabilidad, Open/Closed|

---

## 7. Estructura de eventos generados
```
eventstore/
└── Xotelo/ | Blablacar/
    └── HHSS/YYYYMMDD.events
```

---

## 8. Formato de mensajes publicados

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

## 9. Instalación y compilación

```bash
git clone https://github.com/ProyectoDACDSA/RideInn
cd RideInn
mvn clean install
```

---

## 10. Variables de entorno
| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `BLABLACAR_API_KEY` | Token BlaBlaCar | `123qwe` |
| `DB_URL` | URL database | `database.db` |

---

## 11. Cómo ejecutar el proyecto

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

### 2. Ejecutar las clases

1. Main de event-store-builder.
2. Main de blablacar-feeder con su variable de entorno `BLABLACAR_API_KEY`.
3. Main de xotelo-feeder.
4. DatamartApplication de travel-packages con su variable de entoro `DB_URL`.
5. Main de travel-packages con su variable de entoro `DB_URL`.

Las cuatro primeras clases se mantendrán en ejecución, mientras que el main de travel-packages se podrá ir ejecutando y parando, dependiendo de si queremos buscar un pack de viaje o no.

---

## 12. Flujo de la CLI paso a paso

1. **Seleccionar Opcion 1: Recomendaciones Actuales**  
   - Tienes diferentes filtros que ayudarán a guiar la búsqueda del usuario
2. **Seleccionar Opción 2: Viajes Mejor Valorados**  
   - A partir de filtros, se muestran bajo la búsqueda las opciones mejor valoradas 

---

## 13. Tests

```bash
mvn test
```
Se ejecutan tests unitarios (JUnit) en cada módulo.
