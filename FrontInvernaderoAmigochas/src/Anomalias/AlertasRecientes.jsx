import React, { useState, useEffect } from 'react';
import Alerta from './TarjetaAlerta'; // Asegúrate que el nombre del archivo sea correcto
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';
import { useNavigate } from 'react-router-dom';

function AlertasRecientes() {
    const navigate = useNavigate();
    const [fechaInicio, setFechaInicio] = useState('');
    const [fechaFin, setFechaFin] = useState('');
    const [alertas, setAlertas] = useState([]); 
    const [loadingAlertas, setLoadingAlertas] = useState(false);
    const [errorAlertas, setErrorAlertas] = useState(null);

    // Helper function to get auth headers
    const getAuthHeaders = () => {
        const authToken = localStorage.getItem('authToken');
        const headers = {
            'Content-Type': 'application/json',
        };
        if (authToken) {
            headers['Authorization'] = `Bearer ${authToken}`;
        }
        return headers;
    };
    
    const fetchAlertas = async (inicio, fin) => {
        if (!inicio || !fin) {
            // No hacer fetch si las fechas no están seteadas (evitar llamadas en la carga inicial antes de que el primer useEffect las setee)
            return;
        }
        if (new Date(inicio) > new Date(fin)) {
            setErrorAlertas("La fecha de inicio no puede ser posterior a la fecha de fin.");
            setAlertas([]);
            return;
        }

        setLoadingAlertas(true);
        setErrorAlertas(null);
        setAlertas([]); 

        const gatewayUrl = 'http://localhost:8080'; 
        const endpoint = `${gatewayUrl}/api/v1/anomalyzer/anomalias`; 
        
        const params = new URLSearchParams({
            fechaInicio: inicio, // Formato YYYY-MM-DD
            fechaFin: fin        // Formato YYYY-MM-DD
        });
        const url = `${endpoint}?${params.toString()}`;
        
        console.log(`[AlertasRecientes] Fetching alertas desde: ${url}`);

        try {
            const authHeaders = getAuthHeaders();
            const response = await fetch(url, { headers: authHeaders });

            if (!response.ok) {
                if (response.status === 401) {
                    localStorage.removeItem('authToken');
                    localStorage.removeItem('userRole');
                    localStorage.removeItem('username');
                    // navigate('/login'); // Opcional: redirigir al login
                    throw new Error('Sesión expirada. Por favor, inicie sesión nuevamente.');
                }
                let errorMsg = `Error ${response.status} al obtener alertas.`;
                try {
                    const errorData = await response.json();
                    errorMsg = errorData.message || errorData.mensaje || errorMsg; // Ajustar según el campo de error del backend
                } catch (e) { /* No hacer nada si el cuerpo no es JSON o si la conversión falla */ }
                throw new Error(errorMsg);
            }

            const data = await response.json(); // Esperamos List<AnomaliaResponseDTO>
            console.log("[AlertasRecientes] Datos crudos de anomalías recibidos:", JSON.stringify(data, null, 2));

            if (Array.isArray(data)) {
                const alertasMapeadas = data.map(anomaliaBackend => {
                    const fechaHoraAnomalia = new Date(anomaliaBackend.fechaHora); // El backend envía Date, que se serializa a string ISO
                    
                    let descripcionTarjeta = anomaliaBackend.causa || `Anomalía de ${anomaliaBackend.magnitud || 'desconocida'}`;
                    if (anomaliaBackend.valor) {
                         descripcionTarjeta += ` (Valor: ${anomaliaBackend.valor} ${anomaliaBackend.unidad || ''})`;
                    }

                    // Calcular tiempo relativo (simplificado) o usar fecha/hora
                    // Para un tiempo relativo más preciso, considera una librería como date-fns o moment.js (o luxon)
                    const ahora = new Date();
                    const diffMs = ahora - fechaHoraAnomalia;
                    const diffMins = Math.round(diffMs / 60000);
                    const diffHoras = Math.round(diffMins / 60);
                    let tiempoRelativo;
                    if (diffMins < 1) {
                        tiempoRelativo = 'Hace instantes';
                    } else if (diffMins < 60) {
                        tiempoRelativo = `Hace ${diffMins} minuto${diffMins > 1 ? 's' : ''}`;
                    } else if (diffHoras < 24) {
                        tiempoRelativo = `Hace ${diffHoras} hora${diffHoras > 1 ? 's' : ''}`;
                    } else {
                        tiempoRelativo = `El ${fechaHoraAnomalia.toLocaleDateString()}`;
                    }

                    return {
                        id: anomaliaBackend.id,
                        invernadero: anomaliaBackend.nombreInvernadero || 'N/A',
                        descripcion: descripcionTarjeta, // Descripción principal para el título de la tarjeta
                        detalleDescripcion: anomaliaBackend.causa, // Podría ser una descripción más detallada
                        tiempo: tiempoRelativo, 
                        // Valores específicos para mostrar si es necesario (ya incluidos en descripcionTarjeta)
                        temperatura: anomaliaBackend.magnitud === 'Temperatura' ? `${anomaliaBackend.valor} ${anomaliaBackend.unidad || '°C'}` : undefined,
                        humedad: anomaliaBackend.magnitud === 'Humedad' ? `${anomaliaBackend.valor} ${anomaliaBackend.unidad || '%'}` : undefined,
                        co2: anomaliaBackend.magnitud === 'CO2' ? `${anomaliaBackend.valor} ${anomaliaBackend.unidad || 'ppm'}` : undefined,
                        // ... otras magnitudes
                        fecha: fechaHoraAnomalia.toLocaleDateString(),
                        hora: fechaHoraAnomalia.toLocaleTimeString(),
                        umbral: 'N/A', // Este campo no parece venir de Anomalia.java
                        sensorId: anomaliaBackend.idSensor,
                        sensorMarca: anomaliaBackend.marca,
                        sensorModelo: anomaliaBackend.modelo,
                        ultimaCalibracion: 'N/A', // Este campo no parece venir de Anomalia.java
                        duracion: 'N/A', // Este campo no parece venir de Anomalia.java
                        tipo: anomaliaBackend.magnitud, // Para la lógica de colores/iconos en TarjetaAlerta
                        tieneReporte: anomaliaBackend.tieneReporte || false 
                    };
                });
                setAlertas(alertasMapeadas);
            } else {
                console.warn("La respuesta de anomalías no es un array:", data);
                setAlertas([]);
            }

        } catch (err) {
            console.error("Error al obtener alertas:", err);
            setErrorAlertas(err.message || "Error cargando alertas.");
            setAlertas([]);
        } finally {
            setLoadingAlertas(false);
        }
    };
    
    // useEffect para la carga inicial de fechas
    useEffect(() => {
        const today = new Date();
        const formattedToday = today.toISOString().slice(0, 10);
        
        const weekAgo = new Date();
        weekAgo.setDate(today.getDate() - 7);
        const formattedWeekAgo = weekAgo.toISOString().slice(0, 10);

        // Establecer fechas solo si no están ya seteadas para evitar loop infinito con el siguiente useEffect
        if (!fechaInicio && !fechaFin) {
            setFechaInicio(formattedWeekAgo);
            setFechaFin(formattedToday);
        }
    }, []); // Dependencia vacía para que se ejecute solo una vez al montar

    // useEffect para recargar alertas cuando cambian las fechas (y las fechas son válidas)
    useEffect(() => {
        if (fechaInicio && fechaFin) { // Asegurarse que ambas fechas estén seteadas
            fetchAlertas(fechaInicio, fechaFin);
        }
    }, [fechaInicio, fechaFin]); 


    return (
        <>
            <BarraNavegacion />
            <div className="flex justify-center bg-gray-50 min-h-screen">
                <div className="max-w-3xl w-full p-6">
                    <h1 className="text-2xl font-bold text-gray-800 mb-1">Alertas recientes</h1>
                    <p className="text-sm text-gray-500 mb-6">Últimas alertas detectadas por el sistema</p>

                    <div className="bg-white p-4 rounded-lg shadow-sm mb-6 border-l-4 border-green-500">
                        <div className="flex flex-wrap gap-3 items-center"> {/* Ajuste para alinear botón si se añade */}
                            <div className="flex items-center">
                                <span className="mr-2 text-gray-700">Desde:</span>
                                <input 
                                    type="date" 
                                    className="px-3 py-1 border border-green-200 rounded-md text-gray-700 focus:outline-none focus:ring-2 focus:ring-green-300 focus:border-green-300"
                                    value={fechaInicio}
                                    onChange={(e) => setFechaInicio(e.target.value)}
                                    disabled={loadingAlertas}
                                />
                            </div>
                            <div className="flex items-center">
                                <span className="mr-2 text-gray-700">Hasta:</span>
                                <input 
                                    type="date" 
                                    className="px-3 py-1 border border-green-200 rounded-md text-gray-700 focus:outline-none focus:ring-2 focus:ring-green-300 focus:border-green-300"
                                    value={fechaFin}
                                    onChange={(e) => setFechaFin(e.target.value)}
                                    disabled={loadingAlertas}
                                />
                            </div>
                            {/* Podrías añadir un botón de "Buscar" si prefieres no recargar en cada cambio de fecha
                            <button 
                                onClick={() => fetchAlertas(fechaInicio, fechaFin)} 
                                disabled={loadingAlertas || !fechaInicio || !fechaFin || new Date(fechaInicio) > new Date(fechaFin)}
                                className="px-4 py-1.5 bg-green-600 text-white rounded-md hover:bg-green-700 disabled:bg-gray-300 text-sm">
                                Aplicar Filtro
                            </button>
                            */}
                        </div>
                         {/* Mostrar error de validación de fechas aquí si es el único error y no hay errorAlertas general */}
                        {new Date(fechaInicio) > new Date(fechaFin) && !errorAlertas && (
                            <p className="text-red-500 text-xs mt-2">La fecha de inicio no puede ser posterior a la fecha de fin.</p>
                        )}
                    </div>
                    
                    {errorAlertas && <div className="mb-4 p-3 bg-red-100 text-red-700 border border-red-300 rounded-md">{errorAlertas}</div>}

                    {loadingAlertas && <div className="p-6 text-center text-gray-500">Cargando alertas...</div>}

                    <div className="bg-white rounded-lg shadow-sm border border-green-100 divide-y divide-green-100"> {/* Añadido divide para separar tarjetas */}
                        {!loadingAlertas && alertas.length > 0 ? (
                            alertas.map((alerta) => (
                                <Alerta
                                    key={alerta.id} // Usar el ID único de la anomalía
                                    // Pasar todas las propiedades mapeadas a TarjetaAlerta
                                    // TarjetaAlerta deberá estar preparada para recibir estas props
                                    id={alerta.id}
                                    invernadero={alerta.invernadero}
                                    descripcion={alerta.descripcion} // Título de la tarjeta
                                    detalleDescripcion={alerta.detalleDescripcion} // Causa, si se quiere mostrar
                                    tiempo={alerta.tiempo} // Tiempo relativo o fecha/hora formateada
                                    tipo={alerta.tipo} // Para colores e iconos
                                    tieneReporte={alerta.tieneReporte}
                                    // Podrías querer pasar más datos si TarjetaAlerta los usa
                                    // Por ejemplo, para un modal de detalles:
                                    // sensorId={alerta.sensorId}
                                    // sensorMarca={alerta.sensorMarca}
                                    // sensorModelo={alerta.sensorModelo}
                                    // valorOriginal={alerta.valorOriginal}
                                    // unidadOriginal={alerta.unidadOriginal}
                                    // fechaOriginal={alerta.fecha} // Ya está como fecha
                                    // horaOriginal={alerta.hora}   // Ya está como hora
                                />
                            ))
                        ) : (
                            !loadingAlertas && !errorAlertas && (
                                <div className="p-6 text-center text-gray-500">
                                    No se encontraron alertas para el rango de fechas seleccionado.
                                </div>
                            )
                        )}
                    </div>
                </div>
            </div>
        </>
    );
}

export default AlertasRecientes;