import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';
import { obtenerDetallesAnomalia, enviarReporte, verificarReporteExistente, obtenerReporteDeAnomalia } from '../services/ReporteService';

function LevantarReporte() {
    const navigate = useNavigate();
    const { id } = useParams();
    const [alerta, setAlerta] = useState(null);
    const [reporteExistente, setReporteExistente] = useState(null);
    const [acciones, setAcciones] = useState('');
    const [notas, setNotas] = useState('');
    const [loading, setLoading] = useState(true);
    const [enviado, setEnviado] = useState(false);
    const [error, setError] = useState(null);
    const [modoVisualizacion, setModoVisualizacion] = useState(false);

    useEffect(() => {
        const mapearDatosAnomalia = (datos) => {
            if (datos.descripcion && datos.invernadero && (datos.temperatura || datos.humedad || datos.co2)) {
                return datos;
            }
            // Mapear campos del backend a los del frontend
            let tipo = datos.magnitud || datos.tipo || '';
            let valor = datos.valor;
            let unidad = datos.unidad || (tipo === 'Temperatura' ? '°C' : tipo === 'Humedad' ? '%' : tipo === 'CO2' ? 'ppm' : '');
            let fechaObj = datos.fechaHora ? new Date(datos.fechaHora) : null;
            let fecha = fechaObj ? fechaObj.toLocaleDateString('es-MX') : '';
            let hora = fechaObj ? fechaObj.toLocaleTimeString('es-MX') : '';

            let valorFormateado = valor !== null && valor !== undefined ? Number(valor).toFixed(2) : valor;
            let temperaturaValue = tipo === 'Temperatura' ? `${valorFormateado}${unidad}` : undefined;
            let humedadValue = tipo === 'Humedad' ? `${valorFormateado}${unidad}` : undefined;
            let co2Value = tipo === 'CO2' ? `${valorFormateado}${unidad}` : undefined;

            return {
                id: datos._id || datos.id || '',
                descripcion: datos.causa || datos.descripcion || '',
                invernadero: datos.nombreInvernadero || datos.invernadero || '',
                temperatura: temperaturaValue,
                humedad: humedadValue,
                co2: co2Value,
                fecha,
                hora,
                sensorId: datos.idSensor || datos.sensorId || '',
                sensorMarca: datos.marca || datos.sensorMarca || '',
                sensorModelo: datos.modelo || datos.sensorModelo || '',
                tipo,
                ...datos
            };
        };

        const cargarDatosAnomalia = async () => {
            try {
                const alertaId = id || localStorage.getItem('alertaSeleccionadaId');
                if (!alertaId) {
                    setError('No se ha especificado una alerta para generar el reporte.');
                    setLoading(false);
                    return;
                }
                console.log(`[LevantarReporte] Obteniendo detalles de anomalía con ID: ${alertaId}`);

                // Verificar si la anomalía ya tiene un reporte
                const tieneReporte = await verificarReporteExistente(alertaId);

                // Obtener los datos de la anomalía
                const datosAnomalia = await obtenerDetallesAnomalia(alertaId);
                console.log('[LevantarReporte] Datos de anomalía recibidos:', datosAnomalia);

                // Mapear los datos de la anomalía al formato del frontend
                setAlerta(mapearDatosAnomalia(datosAnomalia));

                // Si ya existe un reporte, cargarlo
                if (tieneReporte) {
                    try {
                        const datosReporte = await obtenerReporteDeAnomalia(alertaId);
                        console.log('[LevantarReporte] Reporte existente:', datosReporte);
                        setReporteExistente(datosReporte);
                        setAcciones(datosReporte.acciones || '');
                        setNotas(datosReporte.comentarios || '');
                        setModoVisualizacion(true);
                    } catch (reporteError) {
                        console.error('Error al obtener reporte existente:', reporteError);
                    }
                }

                setLoading(false);
            } catch (error) {
                console.error('Error al cargar datos de la anomalía:', error);
                setError('Error al obtener datos de la anomalía. Por favor, inténtalo más tarde.');
                setLoading(false);
            }
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
                notas: notas || "",
                fecha: new Date(),
                usuario: localStorage.getItem('user') || 'Sistema'
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
                                <h1 className="text-xl font-bold text-gray-800">
                                    {modoVisualizacion ? 'Reporte de Anomalía' : 'Generar Reporte de Anomalía'}
                                </h1>
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
                                        <span className="h-2.5 w-2.5 rounded-full bg-green-500 mr-2"></span>
                                        <span className="text-sm text-gray-800 font-medium">
                                            {modoVisualizacion ? 'Reporte generado' : 'Pendiente de resolución'}
                                        </span>
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
                            </div>
                        </div>

                        {/* Acciones realizadas */}
                        <div className="mb-8">
                            <h2 className="text-xs font-semibold text-green-700 uppercase mb-2 flex items-center">
                                <span className="mr-2">🛠️</span>
                                Acciones realizadas
                            </h2>
                            {modoVisualizacion ? (
                                <div className="w-full h-24 border border-green-200 bg-green-50 rounded-lg p-3 text-sm text-gray-800 overflow-auto">
                                    {acciones || <span className="text-gray-400 italic">No se registraron acciones</span>}
                                </div>
                            ) : (
                                <textarea
                                    className="w-full h-24 border border-green-200 rounded-lg p-3 text-sm text-gray-800 resize-none focus:outline-none focus:ring-2 focus:ring-green-500"
                                    placeholder="Describe las acciones realizadas para resolver esta anomalía..."
                                    value={acciones}
                                    onChange={(e) => setAcciones(e.target.value)}
                                    required
                                />
                            )}
                        </div>

                        {/* Notas o comentarios */}
                        <div className="mb-8">
                            <h2 className="text-xs font-semibold text-green-700 uppercase mb-2 flex items-center">
                                <span className="mr-2">📝</span>
                                Notas o comentarios
                            </h2>
                            {modoVisualizacion ? (
                                <div className="w-full h-24 border border-green-200 bg-green-50 rounded-lg p-3 text-sm text-gray-800 overflow-auto">
                                    {notas || <span className="text-gray-400 italic">No se registraron comentarios</span>}
                                </div>
                            ) : (
                                <textarea
                                    className="w-full h-24 border border-green-200 rounded-lg p-3 text-sm text-gray-800 resize-none focus:outline-none focus:ring-2 focus:ring-green-500"
                                    placeholder="Agrega notas adicionales o comentarios relevantes..."
                                    value={notas}
                                    onChange={(e) => setNotas(e.target.value)}
                                />
                            )}
                        </div>

                        {/* Botones de acción */}
                        <div className="flex justify-end space-x-4">
                            <button 
                                type="button"
                                onClick={volverAAlertasRecientes}
                                className="bg-white border border-green-300 text-green-700 px-6 py-2 rounded-lg hover:bg-green-50 transition-colors duration-300">
                                {modoVisualizacion ? 'Volver' : 'Cancelar'}
                            </button>

                            {!modoVisualizacion && (
                                <button
                                    type="submit"
                                    className="bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700 transition-colors duration-300 flex items-center shadow-md">
                                    <span className="mr-2">📤</span> Enviar reporte
                                </button>
                            )}
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
