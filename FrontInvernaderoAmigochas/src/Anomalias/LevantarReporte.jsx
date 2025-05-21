import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';
import { obtenerDetallesAnomalia, enviarReporte } from '../services/ReporteService';

function LevantarReporte() {
    const navigate = useNavigate();
    const { id } = useParams();
    const [alerta, setAlerta] = useState(null);
    const [acciones, setAcciones] = useState('');
    const [notas, setNotas] = useState('');
    const [loading, setLoading] = useState(true);
    const [enviado, setEnviado] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        const cargarDatosAnomalia = async () => {
            try {
                // Corregido: Usar 'alertaSeleccionadaId' en lugar de 'alertaSeleccionada'
                // que es el nombre correcto usado en TarjetaAlerta.jsx
                const alertaId = id || localStorage.getItem('alertaSeleccionadaId');

                if (!alertaId) {
                    setError('No se ha especificado una alerta para generar el reporte.');
                    setLoading(false);
                    return;
                }


                console.log(`[LevantarReporte] Obteniendo detalles de anomalía con ID: ${alertaId}`);

                // Obtener detalles de la anomalía desde el backend
                const datosAnomalia = await obtenerDetallesAnomalia(alertaId);
                console.log('[LevantarReporte] Datos de anomalía recibidos:', datosAnomalia);

                setAlerta(datosAnomalia);
                setLoading(false);
            } catch (error) {
                console.error('Error al cargar datos de la anomalía:', error);

                // Si falla la conexión con el backend, usamos datos mockeados temporalmente
                cargarDatosMockeados();
            }
        };

        // Función para cargar datos mockeados (eliminar cuando el backend esté listo)
        const cargarDatosMockeados = () => {
            const alertaId = id || localStorage.getItem('alertaSeleccionada');

            // Datos de prueba adaptados al formato del backend
            const alertasMock = [
                {
                    _id: { $oid: "ALT-001" },
                    idSensor: "SEN-0105",
                    macAddress: "00:11:22:33:44:55",
                    marca: "Stercn",
                    modelo: "LM35",
                    magnitud: "Temperatura",
                    unidad: "°C",
                    valor: 28.0,
                    fechaHora: new Date("2025-04-17T10:15:32"),
                    idInvernadero: "INV-003",
                    nombreInvernadero: "Invernadero 3",
                    sector: "A",
                    fila: "5",
                    causa: "Temperatura alta",
                    // Campos adicionales para mantener compatibilidad con UI existente
                    id: "ALT-001",
                    invernadero: "Invernadero 3",
                    descripcion: "Temperatura alta",
                    tiempo: "5 minutos",
                    temperatura: "28°C",
                    fecha: "17/04/2025",
                    hora: "10:15:32 a.m.",
                    umbral: "25°C",
                    sensorId: "SEN-0105",
                    sensorMarca: "Stercn",
                    sensorModelo: "LM35",
                    ultimaCalibracion: "24/08/2024",
                    duracion: "3 minutos",
                    tipo: "Temperatura"
                },
                {
                    _id: { $oid: "ALT-002" },
                    idSensor: "SEN-0205",
                    macAddress: "00:11:22:33:44:66",
                    marca: "DFRobot",
                    modelo: "DHT11",
                    magnitud: "Humedad",
                    unidad: "%",
                    valor: 50.0,
                    fechaHora: new Date("2025-04-17T09:45:10"),
                    idInvernadero: "INV-001",
                    nombreInvernadero: "Invernadero 1",
                    sector: "B",
                    fila: "3",
                    causa: "Humedad baja",
                    // Campos adicionales para mantener compatibilidad con UI existente
                    id: "ALT-002",
                    invernadero: "Invernadero 1",
                    descripcion: "Humedad baja",
                    tiempo: "15 minutos",
                    humedad: "50%",
                    fecha: "17/04/2025",
                    hora: "09:45:10 a.m.",
                    umbral: "65%",
                    sensorId: "SEN-0205",
                    sensorMarca: "DFRobot",
                    sensorModelo: "DHT11",
                    ultimaCalibracion: "15/10/2024",
                    duracion: "15 minutos",
                    tipo: "Humedad"
                },
                // ... resto de alertas mock
            ];

            const alertaEncontrada = alertasMock.find(a => a.id === alertaId || a._id.$oid === alertaId);
            if (alertaEncontrada) {
                setAlerta(alertaEncontrada);
            } else {
                setError('No se encontró la alerta especificada.');
            }
            setLoading(false);
        };

        cargarDatosAnomalia();
    }, [id]);

    const obtenerIconoAlerta = (tipo) => {
        if (tipo === 'Temperatura') return '🔥';
        if (tipo === 'Humedad') return '💧';
        if (tipo === 'CO2') return '☁️';
        return '⚠️';
    };
    
    const obtenerColorAlerta = (tipo) => {
        if (tipo === 'Temperatura') return 'text-red-500 bg-red-50';
        if (tipo === 'Humedad') return 'text-blue-500 bg-blue-50';
        if (tipo === 'CO2') return 'text-gray-500 bg-gray-50';
        return 'text-yellow-500 bg-yellow-50';
    };
    
    const volverAAlertasRecientes = () => {
        navigate('/anomalias');
    };
    
    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!alerta || !acciones) {
            alert('Por favor completa los campos requeridos');
            return;
        }

        setLoading(true);

        try {
            // Crear objeto de reporte según la estructura que espera el backend
            const reporteData = {
                anomalia: {
                    _id: alerta._id, // Usamos el ID original si lo tenemos
                    idSensor: alerta.idSensor || alerta.sensorId,
                    macAddress: alerta.macAddress || "No disponible",
                    marca: alerta.marca || alerta.sensorMarca,
                    modelo: alerta.modelo || alerta.sensorModelo,
                    magnitud: alerta.magnitud || alerta.tipo,
                    unidad: alerta.unidad || (alerta.tipo === "Temperatura" ? "°C" : alerta.tipo === "Humedad" ? "%" : alerta.tipo === "CO2" ? "ppm" : ""),
                    valor: alerta.valor || parseFloat(alerta.temperatura || alerta.humedad || alerta.co2 || "0"),
                    fechaHora: alerta.fechaHora || new Date(),
                    idInvernadero: alerta.idInvernadero || "INV-001",
                    nombreInvernadero: alerta.nombreInvernadero || alerta.invernadero,
                    sector: alerta.sector || "N/A",
                    fila: alerta.fila || "N/A",
                    causa: alerta.causa || alerta.descripcion
                },
                acciones: acciones,
                notas: notas || "", // comentarios en el modelo del backend
                fecha: new Date(),
                usuario: localStorage.getItem('usuario') || 'Sistema'
            };

            // Enviar el reporte al backend
            await enviarReporte(reporteData);

            setEnviado(true);
            setTimeout(() => {
                navigate('/anomalias');
            }, 3000);
        } catch (error) {
            console.error('Error al enviar el reporte:', error);
            alert('Ocurrió un error al enviar el reporte. Inténtalo de nuevo.');
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <>
                <BarraNavegacion />
                <div className="min-h-screen bg-gradient-to-b from-green-50 to-white flex justify-center items-center">
                    <div className="bg-white p-8 rounded-lg shadow-md border border-green-200">
                        <div className="animate-pulse flex flex-col items-center">
                            <div className="h-12 w-12 bg-green-200 rounded-full mb-4"></div>
                            <div className="h-4 w-32 bg-green-100 rounded mb-3"></div>
                            <p className="text-green-600">Cargando información de la alerta...</p>
                        </div>
                    </div>
                </div>
            </>
        );
    }

    if (!alerta) {
        return (
            <>
                <BarraNavegacion />
                <div className="min-h-screen bg-gradient-to-b from-green-50 to-white flex justify-center items-center">
                    <div className="bg-white p-8 rounded-lg shadow-md border border-green-200 max-w-md">
                        <div className="text-center">
                            <div className="text-5xl mb-4">🔎</div>
                            <h2 className="text-xl font-semibold text-gray-800 mb-2">Alerta no encontrada</h2>
                            <p className="text-gray-600 mb-6">No se encontró la alerta solicitada en nuestros registros.</p>
                            <button 
                                onClick={volverAAlertasRecientes}
                                className="px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 transition-colors duration-300 inline-flex items-center">
                                <span className="mr-1">←</span> Volver a Alertas
                            </button>
                        </div>
                    </div>
                </div>
            </>
        );
    }
    
    if (enviado) {
        return (
            <>
                <BarraNavegacion />
                <div className="min-h-screen bg-gradient-to-b from-green-50 to-white flex justify-center items-center">
                    <div className="bg-white p-8 rounded-lg shadow-md border border-green-200 max-w-md">
                        <div className="text-center">
                            <div className="text-5xl mb-4 text-green-500">✅</div>
                            <h2 className="text-xl font-semibold text-gray-800 mb-2">¡Reporte enviado con éxito!</h2>
                            <p className="text-gray-600 mb-6">Tu reporte para la alerta {alerta.id} ha sido procesado correctamente.</p>
                            <p className="text-sm text-green-600 mb-4">Redirigiendo a la lista de alertas...</p>
                            <div className="w-full bg-gray-200 rounded-full h-2.5 mb-4">
                                <div className="bg-green-600 h-2.5 rounded-full animate-pulse" style={{ width: '100%' }}></div>
                            </div>
                        </div>
                    </div>
                </div>
            </>
        );
    }

    return (
        <>
            <BarraNavegacion />
            <div className="min-h-screen bg-gradient-to-b from-green-50 to-white p-6">
                <div className="max-w-4xl mx-auto bg-white rounded-lg shadow-lg border border-green-200 p-6">
                    {/* Encabezado con botón de volver */}
                    <div className="flex justify-between items-center mb-6 border-b border-green-100 pb-4">
                        <div className="flex items-center">
                            <div className={`p-3 rounded-full mr-4 ${obtenerColorAlerta(alerta.tipo)}`}>
                                <span className="text-2xl" role="img" aria-label="alerta">{obtenerIconoAlerta(alerta.tipo)}</span>
                            </div>
                            <div>
                                <h1 className="text-xl font-bold text-gray-800">Reporte de Anomalía</h1>
                                <p className="text-sm text-green-600">Alerta ID: {alerta.id} • {alerta.fecha}</p>
                            </div>
                        </div>
                        <button 
                            onClick={volverAAlertasRecientes}
                            className="px-4 py-2 bg-green-100 rounded-md text-green-700 hover:bg-green-200 transition-colors duration-300 flex items-center shadow-sm">
                            <span className="mr-1">←</span> Volver a Alertas
                        </button>
                    </div>

                    <form onSubmit={handleSubmit}>
                        {/* Banner de alerta */}
                        <div className={`mb-6 p-4 rounded-lg ${obtenerColorAlerta(alerta.tipo)} border ${alerta.tipo === 'Temperatura' ? 'border-red-200' : alerta.tipo === 'Humedad' ? 'border-blue-200' : 'border-gray-200'}`}>
                            <div className="flex items-center">
                                <span className="text-2xl mr-3">{obtenerIconoAlerta(alerta.tipo)}</span>
                                <div>
                                    <h2 className="font-semibold text-gray-800">{alerta.descripcion} en {alerta.invernadero}</h2>
                                    <p className="text-sm">
                                        {alerta.tipo === 'Temperatura' ? `Temperatura: ${alerta.temperatura}` : 
                                         alerta.tipo === 'Humedad' ? `Humedad: ${alerta.humedad}` : 
                                         alerta.tipo === 'CO2' ? `CO2: ${alerta.co2}` : 'Valor anómalo'} 
                                        | Umbral: {alerta.umbral} | Duración: {alerta.duracion}
                                    </p>
                                </div>
                            </div>
                        </div>

                        {/* Detalles de la anomalía */}
                        <div className="mb-8 bg-green-50 p-5 rounded-lg border border-green-100">
                            <h2 className="text-base font-semibold text-gray-700 mb-4 flex items-center">
                                <span className="text-green-500 mr-2">📋</span>
                                Detalles de la anomalía
                            </h2>
                            <div className="grid grid-cols-2 gap-4">
                                <div className="bg-white p-3 rounded-md border border-green-100">
                                    <p className="text-xs text-green-600 uppercase">Tipo de anomalía</p>
                                    <p className="text-sm text-gray-800 font-medium">{alerta.descripcion}</p>
                                </div>
                                <div className="bg-white p-3 rounded-md border border-green-100">
                                    <p className="text-xs text-green-600 uppercase">Fecha</p>
                                    <div className="flex items-center">
                                        <span className="text-sm text-gray-800 font-medium">{alerta.fecha}</span>
                                        <span className="ml-2 text-gray-400">📅</span>
                                    </div>
                                </div>
                                <div className="bg-white p-3 rounded-md border border-green-100">
                                    <p className="text-xs text-green-600 uppercase">Valor detectado</p>
                                    <p className="text-sm text-gray-800 font-medium">
                                        {alerta.temperatura || alerta.humedad || alerta.co2 || "N/A"}
                                    </p>
                                </div>
                                <div className="bg-white p-3 rounded-md border border-green-100">
                                    <p className="text-xs text-green-600 uppercase">Umbral establecido</p>
                                    <p className="text-sm text-gray-800 font-medium">{alerta.umbral}</p>
                                </div>
                                <div className="bg-white p-3 rounded-md border border-green-100">
                                    <p className="text-xs text-green-600 uppercase">Duración</p>
                                    <div className="flex items-center">
                                        <span className="text-sm text-gray-800 font-medium">{alerta.duracion}</span>
                                        <span className="ml-2 text-gray-400">⏱️</span>
                                    </div>
                                </div>
                                <div className="bg-white p-3 rounded-md border border-green-100">
                                    <p className="text-xs text-green-600 uppercase">Hora</p>
                                    <div className="flex items-center">
                                        <span className="text-sm text-gray-800 font-medium">{alerta.hora}</span>
                                        <span className="ml-2 text-gray-400">⏰</span>
                                    </div>
                                </div>
                                <div className="bg-white p-3 rounded-md border border-green-100">
                                    <p className="text-xs text-green-600 uppercase">Lugar</p>
                                    <div className="flex items-center">
                                        <span className="text-sm text-gray-800 font-medium">{alerta.invernadero}</span>
                                        <span className="ml-2 text-gray-400">🏡</span>
                                    </div>
                                </div>
                                <div className="bg-white p-3 rounded-md border border-green-100">
                                    <p className="text-xs text-green-600 uppercase">Estado actual</p>
                                    <div className="flex items-center">
                                        <span className="h-2.5 w-2.5 rounded-full bg-yellow-500 mr-2"></span>
                                        <span className="text-sm text-gray-800 font-medium">Pendiente de resolución</span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* Datos del sensor */}
                        <div className="mb-8 bg-white p-5 rounded-lg border border-green-200">
                            <h2 className="text-base font-semibold text-gray-700 mb-4 flex items-center">
                                <span className="text-green-500 mr-2">⚙️</span>
                                Datos del sensor
                            </h2>
                            <div className="grid grid-cols-4 gap-4">
                                <div>
                                    <p className="text-xs text-green-600 uppercase mb-1">ID Sensor</p>
                                    <p className="text-sm text-gray-800 font-medium">{alerta.sensorId}</p>
                                </div>
                                <div>
                                    <p className="text-xs text-green-600 uppercase mb-1">Marca</p>
                                    <p className="text-sm text-gray-800 font-medium">{alerta.sensorMarca}</p>
                                </div>
                                <div>
                                    <p className="text-xs text-green-600 uppercase mb-1">Modelo</p>
                                    <p className="text-sm text-gray-800 font-medium">{alerta.sensorModelo}</p>
                                </div>
                                <div>
                                    <p className="text-xs text-green-600 uppercase mb-1">Última calibración</p>
                                    <div className="flex items-center">
                                        <span className="text-sm text-gray-800 font-medium">{alerta.ultimaCalibracion}</span>
                                        <span className="ml-2 text-gray-400">📅</span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* Métricas */}
                        <div className="mb-8">
                            <h2 className="text-base font-semibold text-gray-700 mb-4 flex items-center">
                                <span className="text-green-500 mr-2">📊</span>
                                Métricas
                            </h2>
                            <div className="h-64 bg-green-50 border border-green-100 rounded-lg flex flex-col items-center justify-center p-4">
                                <div className="text-5xl mb-4 text-green-300">📈</div>
                                <p className="text-gray-500 mb-2">Visualización de datos históricos</p>
                                <div className="w-full max-w-md h-24 bg-white rounded-lg shadow-inner overflow-hidden flex items-end p-2">
                                    <div className="h-50% w-4 bg-green-200 mx-1 rounded-t-sm"></div>
                                    <div className="h-60% w-4 bg-green-300 mx-1 rounded-t-sm"></div>
                                    <div className="h-40% w-4 bg-green-200 mx-1 rounded-t-sm"></div>
                                    <div className="h-70% w-4 bg-green-400 mx-1 rounded-t-sm"></div>
                                    <div className="h-90% w-4 bg-red-400 mx-1 rounded-t-sm"></div>
                                    <div className="h-80% w-4 bg-red-300 mx-1 rounded-t-sm"></div>
                                    <div className="h-60% w-4 bg-green-300 mx-1 rounded-t-sm"></div>
                                    <div className="h-50% w-4 bg-green-200 mx-1 rounded-t-sm"></div>
                                    <div className="h-40% w-4 bg-green-200 mx-1 rounded-t-sm"></div>
                                    <div className="h-30% w-4 bg-green-100 mx-1 rounded-t-sm"></div>
                                </div>
                            </div>
                        </div>

                        {/* Acciones realizadas */}
                        <div className="mb-8">
                            <h2 className="text-xs font-semibold text-green-700 uppercase mb-2 flex items-center">
                                <span className="mr-2">🛠️</span>
                                Acciones realizadas
                            </h2>
                            <textarea
                                className="w-full h-24 border border-green-200 rounded-lg p-3 text-sm text-gray-800 resize-none focus:outline-none focus:ring-2 focus:ring-green-500"
                                placeholder="Describe las acciones realizadas para resolver esta anomalía..."
                                value={acciones}
                                onChange={(e) => setAcciones(e.target.value)}
                                required
                            />
                        </div>

                        {/* Notas o comentarios */}
                        <div className="mb-8">
                            <h2 className="text-xs font-semibold text-green-700 uppercase mb-2 flex items-center">
                                <span className="mr-2">📝</span>
                                Notas o comentarios
                            </h2>
                            <textarea
                                className="w-full h-24 border border-green-200 rounded-lg p-3 text-sm text-gray-800 resize-none focus:outline-none focus:ring-2 focus:ring-green-500"
                                placeholder="Agrega notas adicionales o comentarios relevantes..."
                                value={notas}
                                onChange={(e) => setNotas(e.target.value)}
                            />
                        </div>

                        {/* Botones de acción */}
                        <div className="flex justify-end space-x-4">
                            <button 
                                type="button"
                                onClick={volverAAlertasRecientes}
                                className="bg-white border border-green-300 text-green-700 px-6 py-2 rounded-lg hover:bg-green-50 transition-colors duration-300">
                                Cancelar
                            </button>
                            <button 
                                type="submit"
                                className="bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700 transition-colors duration-300 flex items-center shadow-md">
                                <span className="mr-2">📤</span> Enviar reporte
                            </button>
                        </div>
                    </form>
                </div>
                {/* Footer con información */}
                <div className="max-w-4xl mx-auto mt-4 text-center text-xs text-green-600">
                    <p>Los reportes son revisados por el equipo técnico en un plazo máximo de 24 horas</p>
                </div>
            </div>
        </>
    );
}

export default LevantarReporte;
