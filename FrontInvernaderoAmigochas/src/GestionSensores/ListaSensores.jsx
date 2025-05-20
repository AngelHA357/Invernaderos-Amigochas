import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';
import { obtenerSensoresPorInvernadero, eliminarSensor, editarSensor } from '../services/sensorService';

function ListaSensores() {
    const { invernaderoId } = useParams();
    const navigate = useNavigate();
    const [invernadero, setInvernadero] = useState(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [sensorToDelete, setSensorToDelete] = useState(null);
    const [sensores, setSensores] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [sensorDetalle, setSensorDetalle] = useState(null);
    const [showDetalleModal, setShowDetalleModal] = useState(false);
    
    useEffect(() => {
        const cargarDatos = async () => {
            try {
                setLoading(true);
                setError('');
                
                // Cargar invernadero del sessionStorage
                const invernaderoData = sessionStorage.getItem('invernaderoSeleccionado');
                if (invernaderoData) {
                    setInvernadero(JSON.parse(invernaderoData));
                } else {
                    navigate('/invernaderos');
                    return;
                }
                
                // Cargar sensores de la API usando el método actualizado
                const sensoresData = await obtenerSensoresPorInvernadero(invernaderoId);
                setSensores(sensoresData);
                // No establecemos mensaje de error si no hay sensores, es una situación normal
            } catch (err) {
                console.error('Error al cargar datos:', err);
                // Solo establecemos error si es un problema de conexión u otro error grave
                if (err.message.includes('Error de conexión') || err.message.includes('Error al obtener')) {
                    setError('No pudimos cargar los sensores. Por favor, intenta de nuevo.');
                }
            } finally {
                setLoading(false);
            }
        };
        
        cargarDatos();
    }, [invernaderoId, navigate]);

    const volverAInvernaderos = () => {
        navigate('/invernaderos');
    };

    const handleAgregarSensor = () => {
        // Guardamos el ID del invernadero en sessionStorage para usarlo en el formulario de agregar
        sessionStorage.setItem('invernaderoIdSeleccionado', invernaderoId);
        navigate(`/invernaderos/${invernaderoId}/sensores/agregar`);
    };

    const handleEditarSensor = (sensor) => {
        sessionStorage.setItem('sensorSeleccionado', JSON.stringify(sensor));
        navigate(`/invernaderos/${invernaderoId}/sensores/editar/${sensor.id}`);
    };

    const handleEliminarSensor = (sensor) => {
        setSensorToDelete(sensor);
        setShowDeleteModal(true);
    };

    const confirmarEliminarSensor = async () => {
        try {
            setLoading(true);
            // Usar el ID adecuado para eliminar (preferimos _id de MongoDB)
            const idParaEliminar = sensorToDelete._id;
            await eliminarSensor(idParaEliminar);
            
            // Actualizar el estado local eliminando el sensor
            setSensores(prevSensores => prevSensores.filter(sensor => sensor._id !== sensorToDelete._id));

            setShowDeleteModal(false);
            // Mostrar mensaje de éxito
            alert(`El sensor ${sensorToDelete?.id} ha sido eliminado correctamente`);
        } catch (err) {
            console.error('Error al eliminar sensor:', err);
            alert(`Error al eliminar sensor: ${err.message}`);
        } finally {
            setLoading(false);
        }
    };

    const handleVerDetalle = (sensor) => {
        setSensorDetalle(sensor);
        setShowDetalleModal(true);
    };

    // Función para obtener el icono según el tipo de sensor
    const getIconoSensor = (tipo) => {
        switch(tipo?.toLowerCase()) {
            case 'temperatura':
                return '🌡️';
            case 'humedad':
                return '💧';
            case 'co2':
                return '☁️';
            case 'luz':
                return '☀️';
            default:
                return '📊';
        }
    };

    // Función para cambiar el estado del sensor
    const handleCambiarEstado = async (sensor) => {
        try {
            setLoading(true);
            
            // Clonar el sensor actual y cambiar su estado
            const nuevoEstado = sensor.status === 'Activo' ? 'Inactivo' : 'Activo';

            const sensorActualizado = {
                ...sensor,
                status: nuevoEstado
            };
            
            await editarSensor(sensorActualizado);
            
            // Actualizar el estado local
            setSensores(prevSensores => prevSensores.map(s => {
                if (s.id === sensor.id) {
                    return {
                        ...s,
                        status: nuevoEstado
                    };
                }
                return s;
            }));
            
        } catch (err) {
            console.error('Error al cambiar estado del sensor:', err);
            alert(`Error al cambiar el estado del sensor: ${err.message}`);
        } finally {
            setLoading(false);
        }
    };

    if (loading || !invernadero) {
        return (
            <div className="min-h-screen bg-gradient-to-b from-green-50 to-white flex justify-center items-center">
                <div className="bg-white p-8 rounded-lg shadow-md border border-green-200">
                    <div className="animate-pulse flex flex-col items-center">
                        <div className="h-12 w-12 bg-green-200 rounded-full mb-4"></div>
                        <div className="h-4 w-32 bg-green-100 rounded mb-3"></div>
                        <p className="text-green-600">Cargando información del invernadero...</p>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <>
            <BarraNavegacion />
            <div className="min-h-screen bg-gradient-to-b from-green-50 to-white p-6">
                <div className="max-w-6xl mx-auto rounded-lg shadow-lg p-6 mt-6 border border-green-200 bg-white">
                    {/* Encabezado del invernadero */}
                    <div className="flex justify-between items-center mb-6 border-b border-green-100 pb-4">
                        <div className="flex items-center">
                            <div className="bg-green-100 p-3 rounded-full mr-4">
                                <span className="text-2xl" role="img" aria-label="invernadero">🌱</span>
                            </div>
                            <div>
                                <h1 className="text-2xl font-bold text-gray-800">{invernadero?.name}</h1>
                                <div className="flex flex-wrap items-center gap-2 mt-1">
                                    <div className="flex items-center gap-1">
                                        <span className="text-sm text-green-600">Sectores:</span>
                                        {invernadero?.sectores && invernadero.sectores.length > 0 ? (
                                            <div className="flex flex-wrap gap-1">
                                                {invernadero.sectores.map((sector, idx) => (
                                                    <span key={idx} className="inline-block bg-green-100 text-green-800 text-xs px-2 py-1 rounded-full">
                                                        {sector}
                                                    </span>
                                                ))}
                                            </div>
                                        ) : (
                                            <span className="text-sm text-gray-400">No definidos</span>
                                        )}
                                    </div>
                                    <div className="flex items-center gap-1">
                                        <span className="text-sm text-green-600">Filas:</span>
                                        {invernadero?.filas && invernadero.filas.length > 0 ? (
                                            <div className="flex flex-wrap gap-1">
                                                {invernadero.filas.map((fila, idx) => (
                                                    <span key={idx} className="inline-block bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded-full">
                                                        {fila}
                                                    </span>
                                                ))}
                                            </div>
                                        ) : (
                                            <span className="text-sm text-gray-400">No definidas</span>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>
                        <button
                            onClick={volverAInvernaderos}
                            className="px-4 py-2 bg-green-100 rounded-md text-green-700 hover:bg-green-200 transition-colors duration-300 flex items-center shadow-sm">
                            <span className="mr-1">←</span> Volver a Invernaderos
                        </button>
                    </div>

                    {/* Mensaje de error si existe */}
                    {error && (
                        <div className="mb-6 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative">
                            <strong className="font-bold">Error: </strong>
                            <span className="block sm:inline">{error}</span>
                        </div>
                    )}

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

                        {sensores.length > 0 ? (
                            <div className="overflow-x-auto rounded-lg border border-gray-200">
                                {/* Tabla de sensores */}
                                <table className="min-w-full divide-y divide-gray-200">
                                    <thead className="bg-green-100">
                                        <tr>
                                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-green-800 uppercase tracking-wider">
                                                ID Sensor
                                            </th>
                                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-green-800 uppercase tracking-wider">
                                                Tipo / Magnitud
                                            </th>
                                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-green-800 uppercase tracking-wider">
                                                Ubicación
                                            </th>
                                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-green-800 uppercase tracking-wider">
                                                Fabricante
                                            </th>
                                            <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-green-800 uppercase tracking-wider">
                                                Estado
                                            </th>
                                            <th scope="col" className="px-6 py-3 text-center text-xs font-medium text-green-800 uppercase tracking-wider">
                                                Acciones
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody className="bg-white divide-y divide-gray-200">
                                        {sensores.map((sensor, index) => (
                                            <tr key={sensor.id} className={index % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                                                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-800">
                                                    {sensor.id}
                                                </td>
                                                <td className="px-6 py-4 whitespace-nowrap">
                                                    <div className="flex items-center">
                                                        <span className="flex-shrink-0 h-10 w-10 flex items-center justify-center rounded-full bg-green-100 text-xl">
                                                            {getIconoSensor(sensor.type)}
                                                        </span>
                                                        <div className="ml-3">
                                                            <div className="text-sm font-medium text-gray-900">{sensor.type}</div>
                                                            <div className="text-sm text-gray-500">{sensor.magnitud || 'N/A'}</div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td className="px-6 py-4">
                                                    <div className="text-sm text-gray-900">{sensor.sector || 'N/A'}</div>
                                                    <div className="text-sm text-blue-600">{sensor.fila || 'N/A'}</div>
                                                </td>
                                                <td className="px-6 py-4">
                                                    <div className="text-sm text-gray-900">{sensor.marca || 'N/A'}</div>
                                                    <div className="text-sm text-gray-500">{sensor.modelo || 'N/A'}</div>
                                                </td>
                                                <td className="px-6 py-4 whitespace-nowrap">
                                                    <button
                                                        onClick={() => handleCambiarEstado(sensor)}
                                                        className={`relative inline-flex items-center h-6 rounded-full w-11
                                                            ${sensor.status === 'Activo' ? 'bg-green-600' : 'bg-gray-400'}`}
                                                    >
                                                        <span className="sr-only">Cambiar estado</span>
                                                        <span
                                                            className={`inline-block w-4 h-4 transform transition ease-in-out duration-200 rounded-full bg-white
                                                                ${sensor.status === 'Activo' ? 'translate-x-6' : 'translate-x-1'}`}
                                                        />
                                                    </button>
                                                    <span className="ml-2 text-xs text-gray-500">
                                                        {sensor.status === 'Activo' ? 'Activo' : 'Inactivo'}
                                                    </span>
                                                </td>
                                                <td className="px-6 py-4 whitespace-nowrap text-center text-sm font-medium">
                                                    <div className="flex justify-center space-x-2">
                                                        <button
                                                            onClick={() => handleVerDetalle(sensor)}
                                                            className="text-blue-600 hover:text-blue-900 bg-blue-100 px-2 py-1 rounded-full">
                                                            <span className="mr-1">🔍</span> Detalles
                                                        </button>
                                                        <button
                                                            onClick={() => handleEditarSensor(sensor)}
                                                            className="text-green-600 hover:text-green-900 bg-green-100 px-2 py-1 rounded-full">
                                                            <span className="mr-1">✏️</span> Editar
                                                        </button>
                                                        <button
                                                            onClick={() => handleEliminarSensor(sensor)}
                                                            className="text-red-600 hover:text-red-900 bg-red-100 px-2 py-1 rounded-full">
                                                            <span className="mr-1">🗑️</span> Eliminar
                                                        </button>
                                                    </div>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
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
            {showDeleteModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full">
                        <h3 className="text-xl font-bold text-gray-800 mb-4">Confirmar eliminación</h3>
                        <p className="text-gray-600 mb-6">
                            ¿Estás seguro de que deseas eliminar el sensor <span className="font-semibold">{sensorToDelete?.id}</span>?
                            Esta acción no se puede deshacer.
                        </p>
                        <div className="flex justify-end space-x-3">
                            <button
                                onClick={() => setShowDeleteModal(false)}
                                className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-100 transition-colors"
                                disabled={loading}
                            >
                                Cancelar
                            </button>
                            <button
                                onClick={confirmarEliminarSensor}
                                className={`px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 transition-colors ${loading ? 'opacity-50 cursor-not-allowed' : ''}`}
                                disabled={loading}
                            >
                                {loading ? 'Eliminando...' : 'Eliminar'}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Modal de detalles del sensor */}
            {showDetalleModal && sensorDetalle && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-2xl w-full">
                        <div className="flex items-center justify-between mb-4">
                            <div className="flex items-center">
                                <span className="text-3xl mr-3">{getIconoSensor(sensorDetalle.type)}</span>
                                <h3 className="text-xl font-bold text-gray-800">Sensor {sensorDetalle.id}</h3>
                            </div>
                            <button
                                onClick={() => setShowDetalleModal(false)}
                                className="text-gray-400 hover:text-gray-600 transition-colors text-xl">
                                ✕
                            </button>
                        </div>

                        <div className="bg-gray-50 p-4 rounded-lg mb-4">
                            <div className="grid grid-cols-2 gap-6">
                                <div>
                                    <h4 className="text-md font-semibold text-gray-700 mb-3 border-b pb-2">Características</h4>
                                    <div className="space-y-2">
                                        <p className="flex justify-between">
                                            <span className="text-gray-600">Tipo:</span>
                                            <span className="font-medium">{sensorDetalle.type || 'N/A'}</span>
                                        </p>
                                        <p className="flex justify-between">
                                            <span className="text-gray-600">Magnitud:</span>
                                            <span className="font-medium">{sensorDetalle.magnitud || 'N/A'}</span>
                                        </p>
                                        <p className="flex justify-between">
                                            <span className="text-gray-600">Estado:</span>
                                            <span className={`font-medium ${sensorDetalle.status === 'Activo' ? 'text-green-600' : 'text-red-600'}`}>
                                                {sensorDetalle.status}
                                            </span>
                                        </p>
                                    </div>
                                </div>

                                <div>
                                    <h4 className="text-md font-semibold text-gray-700 mb-3 border-b pb-2">Fabricante</h4>
                                    <div className="space-y-2">
                                        <p className="flex justify-between">
                                            <span className="text-gray-600">Marca:</span>
                                            <span className="font-medium">{sensorDetalle.marca || 'N/A'}</span>
                                        </p>
                                        <p className="flex justify-between">
                                            <span className="text-gray-600">Modelo:</span>
                                            <span className="font-medium">{sensorDetalle.modelo || 'N/A'}</span>
                                        </p>
                                        <p className="flex justify-between">
                                            <span className="text-gray-600">MAC Address:</span>
                                            <span className="font-medium">{sensorDetalle.macAddress || 'N/A'}</span>
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="bg-green-50 p-4 rounded-lg">
                            <h4 className="text-md font-semibold text-gray-700 mb-3 border-b pb-2">Ubicación</h4>
                            <div className="grid grid-cols-2 gap-6">
                                <div className="space-y-2">
                                    <p className="flex justify-between">
                                        <span className="text-gray-600">Invernadero:</span>
                                        <span className="font-medium">{invernadero.name}</span>
                                    </p>
                                    <p className="flex justify-between">
                                        <span className="text-gray-600">Sector:</span>
                                        <span className="font-medium">{sensorDetalle.sector || 'N/A'}</span>
                                    </p>
                                </div>
                                <div className="space-y-2">
                                    <p className="flex justify-between">
                                        <span className="text-gray-600">Fila:</span>
                                        <span className="font-medium">{sensorDetalle.fila || 'N/A'}</span>
                                    </p>
                                </div>
                            </div>
                        </div>

                        <div className="flex justify-end mt-6">
                            <button
                                onClick={() => setShowDetalleModal(false)}
                                className="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 transition-colors">
                                Cerrar
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}

export default ListaSensores;

