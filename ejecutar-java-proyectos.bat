@echo off
REM Este script compila y ejecuta los proyectos Gateway y SimuladorSensores

setlocal enabledelayedexpansion

REM Configuración de rutas
set REPO_ROOT=%~dp0
set GATEWAY_DIR=%REPO_ROOT%Gateway
set SIMULADOR_DIR=%REPO_ROOT%SimuladorSensores

REM Verificar si se recibió un parámetro (nombre del proyecto a ejecutar)
if not "%1"=="" (
    if "%1"=="Gateway" goto RunGateway
    if "%1"=="SimuladorSensores" goto RunSimulador
    echo Proyecto no reconocido: %1
    echo Proyectos válidos: Gateway, SimuladorSensores
    goto EOF
)

REM Si no se recibió un parámetro, mostrar el menú
goto Menu

REM Función para ejecutar SimuladorSensores
:RunSimulador
echo Ejecutando SimuladorSensores...
cd %SIMULADOR_DIR%

REM Buscamos todos los JARs de dependencias
set CLASSPATH=%SIMULADOR_DIR%\target\classes
for %%f in (%USERPROFILE%\.m2\repository\org\eclipse\paho\org.eclipse.paho.client.mqttv3\1.2.5\*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%f
)
for %%f in (%USERPROFILE%\.m2\repository\com\google\code\gson\gson\2.10.1\*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%f
)

echo Ejecutando SimuladorSensores con classpath: !CLASSPATH!
echo IMPORTANTE: Se abrirá una ventana de consola para que puedas ingresar el tipo de sensor.
echo Por favor, responde a las preguntas en esa ventana.
echo.

REM Intentar usar Java usando la variable JAVA_HOME si está definida
if defined JAVA_HOME (
    echo Usando Java desde JAVA_HOME: %JAVA_HOME%
    set JAVA_CMD="%JAVA_HOME%\bin\java"
) else (
    echo ADVERTENCIA: JAVA_HOME no está definido, intentando usar 'java' del PATH
    set JAVA_CMD=java
)

echo Comando Java: !JAVA_CMD!
pause

REM Intentar encontrar Java en ubicaciones comunes si JAVA_HOME no está definido
if not defined JAVA_HOME (
    echo Usando Java específico en: C:\Program Files\Java\jdk-21
    set JAVA_CMD="C:\Program Files\Java\jdk-21\bin\java.exe"
)

REM Ejecutar en una nueva ventana cmd para permitir interacción con el usuario
echo Ejecutando con: !JAVA_CMD!
start cmd /c "cd /d "%SIMULADOR_DIR%" && !JAVA_CMD! -cp "%CLASSPATH%" org.itson.simuladorsensores.sensores.Main"

if not "%1"=="" goto EOF
goto Menu

REM Función para ejecutar Gateway
:RunGateway
echo Ejecutando Gateway...
cd %GATEWAY_DIR%

REM Verificar si el proyecto está compilado
if not exist "%GATEWAY_DIR%\target\classes\org\itson\gateway\Gateway.class" (
    echo No se encuentra la clase compilada. Intentando compilar el proyecto...
    
    REM Intentar compilar usando javac directamente
    if exist "%GATEWAY_DIR%\src\main\java\org\itson\gateway\Gateway.java" (
        echo Compilando usando javac...
        
        REM Crear directorios si no existen
        if not exist "%GATEWAY_DIR%\target\classes\org\itson\gateway" mkdir "%GATEWAY_DIR%\target\classes\org\itson\gateway"
        
        REM Intentar usar Javac usando la variable JAVA_HOME si está definida
        if defined JAVA_HOME (
            echo Usando Javac desde JAVA_HOME: %JAVA_HOME%
            set JAVAC_CMD="%JAVA_HOME%\bin\javac"
        ) else (
            echo ADVERTENCIA: JAVA_HOME no está definido, intentando usar 'javac' del PATH
            set JAVAC_CMD=javac
        )
        
        echo Comando Javac: !JAVAC_CMD!
        
        REM Compilar
        !JAVAC_CMD! -d "%GATEWAY_DIR%\target\classes" -cp "%GATEWAY_DIR%\src\main\java" "%GATEWAY_DIR%\src\main\java\org\itson\gateway\Gateway.java"
        
        if errorlevel 1 (
            echo Error al compilar. Abre el proyecto en tu IDE y compílalo manualmente.
            pause
            goto Menu
        )
    ) else (
        echo No se encuentra el archivo fuente. Verifica la estructura del proyecto.
        echo Ruta buscada: %GATEWAY_DIR%\src\main\java\org\itson\gateway\Gateway.java
        pause
        goto Menu
    )
)

REM Buscamos todos los JARs de dependencias
set CLASSPATH=%GATEWAY_DIR%\target\classes
REM Incluir DTOs en el classpath de ejecución
if exist "%REPO_ROOT%\DTOs\target\classes" (
    set CLASSPATH=!CLASSPATH!;"%REPO_ROOT%\DTOs\target\classes"
    echo Añadido DTOs al classpath de ejecución
)
for %%f in (%USERPROFILE%\.m2\repository\org\eclipse\paho\org.eclipse.paho.client.mqttv3\1.2.5\*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%f
)
for %%f in (%USERPROFILE%\.m2\repository\com\google\code\gson\gson\2.10.1\*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%f
)
for %%f in (%USERPROFILE%\.m2\repository\com\rabbitmq\amqp-client\5.20.0\*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%f
)
for %%f in (%USERPROFILE%\.m2\repository\org\slf4j\slf4j-simple\1.7.36\*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%f
)
for %%f in (%USERPROFILE%\.m2\repository\org\slf4j\slf4j-api\1.7.36\*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%f
)

echo Ejecutando con classpath: !CLASSPATH!
echo Clase principal: org.itson.gateway.Gateway

REM Intentar usar Java usando la variable JAVA_HOME si está definida
if defined JAVA_HOME (
    echo Usando Java desde JAVA_HOME: %JAVA_HOME%
    set JAVA_CMD="%JAVA_HOME%\bin\java"
) else (
    echo ADVERTENCIA: JAVA_HOME no está definido, intentando usar 'java' del PATH
    set JAVA_CMD=java
)

echo Comando Java: !JAVA_CMD!

REM Intentar encontrar Java en ubicaciones comunes si JAVA_HOME no está definido
if not defined JAVA_HOME (
    echo Usando Java específico en: C:\Program Files\Java\jdk-21
    set JAVA_CMD="C:\Program Files\Java\jdk-21\bin\java.exe"
)

REM Ejecutar la clase principal
!JAVA_CMD! -cp "!CLASSPATH!" org.itson.gateway.Gateway
if not "%1"=="" goto EOF
goto Menu

REM Menú principal
:Menu
echo.
echo EJECUTAR PROYECTOS
echo -----------------
echo 1. Ejecutar SimuladorSensores
echo 2. Ejecutar Gateway
echo 3. Salir
echo.
set /p OPCION=Selecciona una opción (1-3): 

if "%OPCION%"=="1" goto RunSimulador
if "%OPCION%"=="2" goto RunGateway
if "%OPCION%"=="3" goto :EOF

echo Opción no válida. Intente de nuevo.
goto Menu

:EOF