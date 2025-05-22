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
            fechaInicio: inicio,
            fechaFin: fin
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
                    throw new Error('Sesión expirada. Por favor, inicie sesión nuevamente.');
                }
                if (response.status === 403) {
                    throw new Error('No tienes permisos para ver estas alertas.');
                }
                if (response.status === 502 || response.status === 503) {
                    throw new Error('El servicio de alertas no está disponible en este momento. Intenta más tarde.');
                }
                let errorMsg = `Error ${response.status} al obtener alertas.`;
                try {
                    const errorData = await response.json();
                    errorMsg = errorData.message || errorData.mensaje || errorMsg;
                } catch (e) { }
                throw new Error(errorMsg);
            }


            if (response.status === 204) {
                setAlertas([]);
                setLoadingAlertas(false);
                return;
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
                        descripcion: descripcionTarjeta,
                        detalleDescripcion: anomaliaBackend.causa,
                        tiempo: tiempoRelativo,
                        temperatura: anomaliaBackend.magnitud === 'Temperatura' ? `${Number(anomaliaBackend.valor).toFixed(2)} ${anomaliaBackend.unidad || '°C'}` : undefined,
                        humedad: anomaliaBackend.magnitud === 'Humedad' ? `${anomaliaBackend.valor} ${anomaliaBackend.unidad || '%'}` : undefined,
                        co2: anomaliaBackend.magnitud === 'CO2' ? `${anomaliaBackend.valor} ${anomaliaBackend.unidad || 'ppm'}` : undefined,
                        fecha: fechaHoraAnomalia.toLocaleDateString(),
                        hora: fechaHoraAnomalia.toLocaleTimeString(),
                        umbral: 'N/A',
                        sensorId: anomaliaBackend.idSensor,
                        sensorMarca: anomaliaBackend.marca,
                        sensorModelo: anomaliaBackend.modelo,
                        ultimaCalibracion: 'N/A',
                        duracion: 'N/A',
                        tipo: anomaliaBackend.magnitud,
                        tieneReporte: anomaliaBackend.tieneReporte || false 
                    };
                });
                setAlertas(alertasMapeadas);
            } else {
                console.warn("La respuesta de anomalías no es un array:", data);
                setAlertas([]);
            }

        } catch (err) {
            // --- NUEVO BLOQUE PARA MANEJAR ERROR DE RED ---
            if (err instanceof TypeError && err.message.match(/Failed to fetch|NetworkError/i)) {
                setErrorAlertas("No se pudo conectar con el servicio de alertas. El sistema podría estar en mantenimiento o sin conexión. Intenta más tarde.");
            } else {
                setErrorAlertas(err.message || "Error cargando alertas.");
            }
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
    }, []);

    // useEffect para recargar alertas cuando cambian las fechas (y las fechas son válidas)
    useEffect(() => {
        if (fechaInicio && fechaFin) {
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
                        <div className="flex flex-wrap gap-3 items-center">
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
                        </div>
                        {new Date(fechaInicio) > new Date(fechaFin) && !errorAlertas && (
                            <p className="text-red-500 text-xs mt-2">La fecha de inicio no puede ser posterior a la fecha de fin.</p>
                        )}
                    </div>
                    
                    {errorAlertas && <div className="mb-4 p-3 bg-red-100 text-red-700 border border-red-300 rounded-md">{errorAlertas}</div>}

                    {loadingAlertas && <div className="p-6 text-center text-gray-500">Cargando alertas...</div>}

                    <div className="bg-white rounded-lg shadow-sm border border-green-100 divide-y divide-green-100">
                        {!loadingAlertas && alertas.length > 0 ? (
                            alertas.map((alerta) => (
                                <Alerta
                                    key={alerta.id}
                                    id={alerta.id}
                                    invernadero={alerta.invernadero}
                                    descripcion={alerta.descripcion}
                                    detalleDescripcion={alerta.detalleDescripcion}
                                    tiempo={alerta.tiempo}
                                    tipo={alerta.tipo}
                                    tieneReporte={alerta.tieneReporte}
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