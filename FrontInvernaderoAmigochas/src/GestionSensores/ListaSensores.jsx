import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';
import { getSensoresByInvernadero, eliminarSensor, getAllInvernaderos } from '../services/sensorService';

function ListaSensores() {
    const navigate = useNavigate();
    const { idInvernadero } = useParams();
    
    const [sensores, setSensores] = useState([]);
    const [invernadero, setInvernadero] = useState(null);
    const [filtro, setFiltro] = useState('');
    const [confirmDelete, setConfirmDelete] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [deleteSuccess, setDeleteSuccess] = useState(false);

    useEffect(() => {
        // Si no hay ID de invernadero, redireccionar a la lista de invernaderos
        if (!idInvernadero) {
            navigate('/invernaderos');
            return;
        }
        
        // Cargar información del invernadero y sus sensores
        cargarDatos();
    }, [idInvernadero, navigate]);

    const cargarDatos = async () => {
        try {
            setLoading(true);
            setError('');
            
            // Cargar información del invernadero
            const invernaderos = await getAllInvernaderos();
            const invernaderoActual = invernaderos.find(inv => inv.id === idInvernadero);
            
            if (!invernaderoActual) {
                throw new Error(`No se encontró el invernadero con ID ${idInvernadero}`);
            }
            
            setInvernadero(invernaderoActual);
            
            // Cargar sensores del invernadero
            const sensoresData = await getSensoresByInvernadero(idInvernadero);
            setSensores(sensoresData);
        } catch (error) {
            console.error('Error al cargar datos:', error);
            setError('No se pudieron cargar los datos. Por favor, intente nuevamente.');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (idSensor) => {
        try {
            setLoading(true);
            await eliminarSensor(idSensor);
            setSensores(sensores.filter(sensor => sensor.idSensor !== idSensor));
            setDeleteSuccess(true);
            // Ocultar mensaje de éxito después de 3 segundos
            setTimeout(() => setDeleteSuccess(false), 3000);
        } catch (error) {
            console.error('Error al eliminar sensor:', error);
            setError('No se pudo eliminar el sensor. Por favor, intente nuevamente.');
        } finally {
            setLoading(false);
            setConfirmDelete(null);
        }
    };

    const filteredSensores = sensores.filter(sensor => {
        if (!filtro) return true;
        return (
            sensor.idSensor?.toLowerCase().includes(filtro.toLowerCase()) ||
            sensor.tipoSensor?.toLowerCase().includes(filtro.toLowerCase()) ||
            sensor.marca?.toLowerCase().includes(filtro.toLowerCase()) ||
            sensor.modelo?.toLowerCase().includes(filtro.toLowerCase())
        );
    });

    const volverAInvernaderos = () => {
        navigate('/invernaderos');
    };

    const handleAgregarSensor = () => {
        navigate(`/sensores/agregar/${idInvernadero}`);
    };

    const handleEditarSensor = (sensor) => {
        navigate(`/sensores/editar/${sensor.idSensor}`);
    };

    // Función para obtener el icono según el tipo de sensor
    const getIconoSensor = (tipo) => {
        if (!tipo) return '📊';
        
        switch(tipo.toLowerCase()) {
            case 'humedad':
            case 'hum':
                return '💧';
            case 'temperatura':
            case 'temc':
            case 'temf':
                return '🌡️';
            case 'co2':
                return '☁️';
            case 'luz':
                return '☀️';
            default:
                return '📊';
        }
    };

    // Estado del sensor (activo/inactivo)
    const getSensorStatus = (estado) => {
        return estado ? 'Activo' : 'Inactivo';
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gradient-to-b from-green-50 to-white flex justify-center items-center">
                <div className="bg-white p-8 rounded-lg shadow-md border border-green-200">
                    <div className="animate-pulse flex flex-col items-center">
                        <div className="h-12 w-12 bg-green-200 rounded-full mb-4"></div>
                        <div className="h-4 w-32 bg-green-100 rounded mb-3"></div>
                        <p className="text-green-600">Cargando información de sensores...</p>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <>
            <BarraNavegacion />
            <div className="min-h-screen bg-gradient-to-b from-green-50 to-white p-6">
                <div className="max-w-4xl mx-auto rounded-lg shadow-lg p-6 mt-6 border border-green-200 bg-white">
                    {/* Encabezado del invernadero */}
                    <div className="flex justify-between items-center mb-6 border-b border-green-100 pb-4">
                        <div className="flex items-center">
                            <div className="bg-green-100 p-3 rounded-full mr-4">
                                <span className="text-2xl" role="img" aria-label="invernadero">🌱</span>
                            </div>
                            <div>
                                <h1 className="text-2xl font-bold text-gray-800">{invernadero?.name}</h1>
                                <p className="text-sm text-green-600">Ubicación: {invernadero?.location} • ID: {invernadero?.id}</p>
                            </div>
                        </div>
                        <button 
                            onClick={volverAInvernaderos}
                            className="px-4 py-2 bg-green-100 rounded-md text-green-700 hover:bg-green-200 transition-colors duration-300 flex items-center shadow-sm">
                            <span className="mr-1">←</span> Volver a Invernaderos
                        </button>
                    </div>

                    {/* Error o éxito */}
                    {error && (
                        <div className="bg-red-100 text-red-700 p-3 rounded-md mb-4">
                            {error}
                        </div>
                    )}
                    
                    {deleteSuccess && (
                        <div className="bg-green-100 text-green-700 p-3 rounded-md mb-4">
                            Sensor eliminado correctamente.
                        </div>
                    )}

                    {/* Búsqueda y filtrado */}
                    <div className="mb-6">
                        <div className="relative">
                            <input
                                type="text"
                                placeholder="Buscar sensor por ID, tipo, marca..."
                                value={filtro}
                                onChange={(e) => setFiltro(e.target.value)}
                                className="w-full border border-green-200 rounded-md p-2 pl-10 focus:outline-none focus:ring-2 focus:ring-green-500"
                            />
                            <span className="absolute left-3 top-2.5 text-green-500">🔍</span>
                        </div>
                    </div>

                    {/* Lista de sensores */}
                    <div className="mt-6">
                        <div className='flex items-center justify-between mb-6'>
                            <div className="flex items-center">
                                <span className="text-green-500 mr-2">📊</span>
                                <h2 className="text-xl font-semibold text-gray-700">Sensores Disponibles</h2>
                            </div>
                            <button 
                                onClick={handleAgregarSensor} 
                                className="px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 transition-colors duration-300 flex items-center shadow-sm">
                                <span className="mr-1">+</span> Agregar sensor
                            </button>
                        </div>

                        {filteredSensores.length > 0 ? (
                            <>
                                {/* Encabezados */}
                                <div className="grid grid-cols-5 gap-4 bg-green-100 text-green-800 py-3 px-4 rounded-t-lg font-semibold">
                                    <div>ID Sensor</div>
                                    <div>Tipo</div>
                                    <div>Marca/Modelo</div>
                                    <div>Estado</div>
                                    <div className="text-center">Acciones</div>
                                </div>

                                {/* Filas */}
                                {filteredSensores.map((sensor, index) => (
                                    <div
                                        key={sensor.idSensor}
                                        className={`grid grid-cols-5 gap-4 py-3 px-4 ${
                                            index % 2 === 0 ? 'bg-white' : 'bg-green-50'
                                        } hover:bg-green-100 transition-colors duration-200 border-b border-green-100`}
                                    >
                                        <div className="text-gray-800 font-medium">{sensor.idSensor}</div>
                                        <div className="text-gray-800 font-medium flex items-center">
                                            <span className="mr-2">{getIconoSensor(sensor.tipoSensor)}</span>
                                            {sensor.tipoSensor}
                                            <span className="text-xs text-gray-500 ml-1">({sensor.magnitud})</span>
                                        </div>
                                        <div className="text-gray-600">
                                            {sensor.marca} / {sensor.modelo}
                                        </div>
                                        <div className={`flex items-center ${sensor.estado ? 'text-green-600' : 'text-red-600'}`}>
                                            <span className={`h-2.5 w-2.5 rounded-full mr-2 ${sensor.estado ? 'bg-green-500' : 'bg-red-500'}`}></span>
                                            {getSensorStatus(sensor.estado)}
                                        </div>
                                        <div className="flex flex-row space-x-2 justify-center">
                                            <button 
                                                onClick={() => handleEditarSensor(sensor)}
                                                className="inline-flex items-center px-2 py-1 text-xs font-semibold text-green-800 bg-green-100 rounded-full hover:bg-green-200 transition-colors duration-300">
                                                <span className="mr-1">✏️</span> Editar
                                            </button>
                                            <button 
                                                onClick={() => setConfirmDelete(sensor.idSensor)}
                                                className="inline-flex items-center px-2 py-1 text-xs font-semibold text-red-800 bg-red-100 rounded-full hover:bg-red-200 transition-colors duration-300">
                                                <span className="mr-1">🗑️</span> Eliminar
                                            </button>
                                        </div>
                                    </div>
                                ))}
                            </>
                        ) : (
                            <div className="text-center p-8 bg-green-50 rounded-lg border border-green-100 text-gray-600">
                                <div className="text-5xl mb-4">🌱</div>
                                <p className="text-lg font-medium text-gray-700 mb-2">No hay sensores registrados</p>
                                <p className="text-green-600 mb-4">Este invernadero aún no tiene sensores configurados.</p>
                                <button 
                                    onClick={handleAgregarSensor} 
                                    className="px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 transition-colors duration-300 inline-flex items-center">
                                    <span className="mr-1">+</span> Agregar sensor ahora
                                </button>
                            </div>
                        )}
                    </div>

                    {/* Footer con información */}
                    <div className="mt-8 pt-4 border-t border-green-100 text-center text-xs text-green-600">
                        <p>Última sincronización de datos: {new Date().toLocaleTimeString()} • {new Date().toLocaleDateString()}</p>
                    </div>
                </div>
            </div>

            {/* Modal de confirmación de eliminación */}
            {confirmDelete && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full">
                        <h3 className="text-xl font-bold text-gray-800 mb-4">Confirmar eliminación</h3>
                        <p className="text-gray-600 mb-6">
                            ¿Estás seguro de que deseas eliminar el sensor <span className="font-semibold">{confirmDelete}</span>?
                            Esta acción no se puede deshacer.
                        </p>
                        <div className="flex justify-end space-x-3">
                            <button
                                onClick={() => setConfirmDelete(null)}
                                className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-100 transition-colors"
                            >
                                Cancelar
                            </button>
                            <button
                                onClick={() => handleDelete(confirmDelete)}
                                className="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 transition-colors"
                            >
                                Eliminar
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}

export default ListaSensores;