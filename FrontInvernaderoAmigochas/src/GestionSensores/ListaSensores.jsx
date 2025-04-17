import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';
import sensoresMock from '../mocks/sensores.json';

function ListaSensores() {
    const { invernaderoId } = useParams();
    const navigate = useNavigate();
    const [invernadero, setInvernadero] = useState(null);
    
    const sensoresDelInvernadero = sensoresMock.filter(sensor => sensor.invernaderoId === invernaderoId);

    useEffect(() => {
        const invernaderoData = sessionStorage.getItem('invernaderoSeleccionado');
        if (invernaderoData) {
            setInvernadero(JSON.parse(invernaderoData));
        } else {
            navigate('/');
        }
    }, [invernaderoId, navigate]);

    const volverAInvernaderos = () => {
        navigate('/');
    };

    // Función para obtener el icono según el tipo de sensor
    const getIconoSensor = (tipo) => {
        switch(tipo.toLowerCase()) {
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

    if (!invernadero) {
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
                <div className="max-w-4xl mx-auto rounded-lg shadow-lg p-6 mt-6 border border-green-200 bg-white">
                    {/* Encabezado del invernadero */}
                    <div className="flex justify-between items-center mb-6 border-b border-green-100 pb-4">
                        <div className="flex items-center">
                            <div className="bg-green-100 p-3 rounded-full mr-4">
                                <span className="text-2xl" role="img" aria-label="invernadero">🌱</span>
                            </div>
                            <div>
                                <h1 className="text-2xl font-bold text-gray-800">{invernadero.name}</h1>
                                <p className="text-sm text-green-600">Ubicación: {invernadero.location} • ID: {invernadero.id}</p>
                            </div>
                        </div>
                        <button 
                            onClick={volverAInvernaderos}
                            className="px-4 py-2 bg-green-100 rounded-md text-green-700 hover:bg-green-200 transition-colors duration-300 flex items-center shadow-sm">
                            <span className="mr-1">←</span> Volver a Invernaderos
                        </button>
                    </div>

                    {/* Lista de sensores */}
                    <div className="mt-6">
                        <div className='flex items-center justify-between mb-6'>
                            <div className="flex items-center">
                                <span className="text-green-500 mr-2">📊</span>
                                <h2 className="text-xl font-semibold text-gray-700">Sensores Disponibles</h2>
                            </div>
                            <button className="px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 transition-colors duration-300 flex items-center shadow-sm">
                                <span className="mr-1">+</span> Agregar sensor
                            </button>
                        </div>

                        {sensoresDelInvernadero.length > 0 ? (
                            <>
                                {/* Encabezados */}
                                <div className="grid grid-cols-4 gap-4 bg-green-100 text-green-800 py-3 px-4 rounded-t-lg font-semibold">
                                    <div>ID Sensor</div>
                                    <div>Tipo</div>
                                    <div>Estado</div>
                                    <div>Acciones</div>
                                </div>

                                {/* Filas */}
                                {sensoresDelInvernadero.map((sensor, index) => (
                                    <div
                                        key={sensor.id}
                                        className={`grid grid-cols-4 gap-4 py-3 px-4 ${
                                            index % 2 === 0 ? 'bg-white' : 'bg-green-50'
                                        } hover:bg-green-100 transition-colors duration-200 border-b border-green-100`}
                                    >
                                        <div className="text-gray-800 font-medium">{sensor.id}</div>
                                        <div className="text-gray-800 font-medium flex items-center">
                                            <span className="mr-2">{getIconoSensor(sensor.type)}</span>
                                            {sensor.type}
                                        </div>
                                        <div className={`flex items-center ${sensor.status === 'Activo' ? 'text-green-600' : 'text-red-600'}`}>
                                            <span className={`h-2.5 w-2.5 rounded-full mr-2 ${sensor.status === 'Activo' ? 'bg-green-500' : 'bg-red-500'}`}></span>
                                            {sensor.status}
                                        </div>
                                        <div>
                                            <div className="flex space-x-2">
                                                <button className="inline-flex items-center px-3 py-1 text-sm font-semibold text-green-800 bg-green-100 rounded-full hover:bg-green-200 transition-colors duration-300">
                                                    <span className="mr-1">✏️</span> Editar
                                                </button>
                                                <button className="inline-flex items-center px-3 py-1 text-sm font-semibold text-red-800 bg-red-100 rounded-full hover:bg-red-200 transition-colors duration-300">
                                                    <span className="mr-1">🗑️</span> Eliminar
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </>
                        ) : (
                            <div className="text-center p-8 bg-green-50 rounded-lg border border-green-100 text-gray-600">
                                <div className="text-5xl mb-4">🌱</div>
                                <p className="text-lg font-medium text-gray-700 mb-2">No hay sensores registrados</p>
                                <p className="text-green-600 mb-4">Este invernadero aún no tiene sensores configurados.</p>
                                <button className="px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 transition-colors duration-300 inline-flex items-center">
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
        </>
    );
}

export default ListaSensores;