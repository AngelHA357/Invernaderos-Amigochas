import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';
import { getAllInvernaderos } from '../services/sensorService';

function ListaInvernaderos() {
    const [invernaderos, setInvernaderos] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [filtro, setFiltro] = useState('');

    useEffect(() => {
        cargarInvernaderos();
    }, []);

    const cargarInvernaderos = async () => {
        try {
            setLoading(true);
            setError('');
            const data = await getAllInvernaderos();
            setInvernaderos(data);
        } catch (error) {
            console.error('Error al cargar invernaderos:', error);
            setError('No se pudieron cargar los invernaderos. Por favor, intente nuevamente.');
        } finally {
            setLoading(false);
        }
    };

    const filteredInvernaderos = invernaderos.filter(invernadero => {
        if (!filtro) return true;
        return (
            invernadero.id.toLowerCase().includes(filtro.toLowerCase()) ||
            invernadero.nombre.toLowerCase().includes(filtro.toLowerCase()) ||
            invernadero.ubicacion.toLowerCase().includes(filtro.toLowerCase())
        );
    });

    if (loading) {
        return (
            <div className="min-h-screen bg-gradient-to-b from-green-50 to-white flex justify-center items-center">
                <div className="bg-white p-8 rounded-lg shadow-md border border-green-200">
                    <div className="animate-pulse flex flex-col items-center">
                        <div className="h-12 w-12 bg-green-200 rounded-full mb-4"></div>
                        <div className="h-4 w-32 bg-green-100 rounded mb-3"></div>
                        <p className="text-green-600">Cargando información de invernaderos...</p>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <>
            <BarraNavegacion />
            <div className="min-h-screen bg-gradient-to-b from-green-50 to-white p-6">
                <div className="max-w-6xl mx-auto bg-white p-6 rounded-lg shadow-lg border border-green-200">
                    {/* Título y descripción */}
                    <div className="flex items-center mb-6">
                        <div className="bg-green-100 p-3 rounded-full mr-4">
                            <span className="text-2xl" role="img" aria-label="invernadero">🌱</span>
                        </div>
                        <div>
                            <h1 className="text-2xl font-bold text-gray-800">Invernaderos</h1>
                            <p className="text-sm text-green-600">Gestiona los invernaderos y sus sensores</p>
                        </div>
                    </div>

                    {/* Mensaje de error */}
                    {error && (
                        <div className="bg-red-100 text-red-700 p-3 rounded-md mb-4">
                            {error}
                        </div>
                    )}

                    {/* Búsqueda y filtrado */}
                    <div className="mb-6">
                        <div className="relative">
                            <input
                                type="text"
                                placeholder="Buscar invernadero por ID, nombre o ubicación..."
                                value={filtro}
                                onChange={(e) => setFiltro(e.target.value)}
                                className="w-full border border-green-200 rounded-md p-2 pl-10 focus:outline-none focus:ring-2 focus:ring-green-500"
                            />
                            <span className="absolute left-3 top-2.5 text-green-500">🔍</span>
                        </div>
                    </div>

                    {/* Grid de tarjetas de invernadero */}
                    {filteredInvernaderos.length > 0 ? (
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                            {filteredInvernaderos.map((invernadero) => (
                                <Link
                                    key={invernadero.id}
                                    to={`/invernadero/${invernadero.id}/sensores`}
                                    className="block bg-white p-5 rounded-lg border border-green-200 hover:shadow-md transition-shadow duration-300 hover:border-green-300"
                                >
                                    <div className="bg-green-100 w-12 h-12 rounded-full flex items-center justify-center mb-4">
                                        <span className="text-2xl" role="img" aria-label="invernadero">🌿</span>
                                    </div>
                                    <h2 className="text-xl font-semibold text-gray-800 mb-2">{invernadero.nombre}</h2>
                                    <div className="text-sm text-gray-600 mb-1">
                                        <span className="font-medium text-gray-700">ID:</span> {invernadero.id}
                                    </div>
                                    <div className="text-sm text-gray-600 mb-1">
                                        <span className="font-medium text-gray-700">Ubicación:</span> {invernadero.ubicacion}
                                    </div>
                                    <div className="text-sm text-gray-600">
                                        <span className="font-medium text-gray-700">Estado:</span>{' '}
                                        <span className={invernadero.estado ? 'text-green-600' : 'text-red-600'}>
                                            {invernadero.estado ? 'Activo' : 'Inactivo'}
                                        </span>
                                    </div>
                                    <div className="mt-4 flex justify-between items-center">
                                        <span className="text-sm text-green-600 font-medium flex items-center">
                                            <span className="mr-1 text-lg">📊</span> Ver sensores
                                        </span>
                                        <span className="bg-green-500 text-white text-xs rounded-full py-1 px-2">
                                            {invernadero.totalSensores || 0} sensores
                                        </span>
                                    </div>
                                </Link>
                            ))}
                        </div>
                    ) : (
                        <div className="text-center p-8 bg-green-50 rounded-lg border border-green-100 text-gray-600">
                            <div className="text-5xl mb-4">🌱</div>
                            <p className="text-lg font-medium text-gray-700 mb-2">No hay invernaderos disponibles</p>
                            <p className="text-green-600 mb-4">No se encontraron invernaderos que coincidan con tu búsqueda.</p>
                            <button 
                                onClick={() => setFiltro('')} 
                                className="px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 transition-colors duration-300 inline-flex items-center">
                                <span className="mr-1">🔄</span> Mostrar todos
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </>
    );
}

export default ListaInvernaderos;