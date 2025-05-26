## Ejecución
Software necesario:
- IntelliJ Idea
- Docker
- Node.js

Para correr el programa es necesario realizar los siguientes pasos:
1. Abrir los proyectos
- Autenticacion
- APIGateway
- ReportesAnomalias
- GestionSensores
- Informes
- Alarmas
- Anomalyzer

2. En cada proyecto, en el logo de Maven que está en la barra de la derecha, picarle y luego hacer clic en las opciones *Reload Project* y *Generate Sources and Update Folders* en ese orden. Esto para generar los archivos necesarios para la comunicación via gRPC.

3. Abrir el resto de proyectos en IntelliJ Idea, VSCode o en terminal.
- Alarmator
- ExposicionDatos
- Gateway
- Lecturas
- Login
- Simulador

4. Ir a la carpeta raíz de cada proyecto, incluidos los previamente mencionados y ejecutar el comando en consola
```
  ./mvnw package -DskipTests 
```
Esto con la finalidad de enpaquetar cada proyecto y que pueda correr correctamente una vez Dockerizado.

5. Ir a la carpeta raíz del proyecto (/Invernaderos-Amigochas) y ejecutar el siguiente comando:
```
  docker-compose up --build
```
6. Y listo, ya estaría todo el proyecto corriendo en docker, solo faltaría ejecutar el front, para ello, solo es cuestión de ir a la carpeta de dicho proyecto y ejecutar los siguientes comandos:
```
  npm install
```
y
```
  npm run dev
```
Tener en consideración que necesita tener Node.js instalado.