import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';

function ListaSensores() {
    const { invernaderoId } = useParams();
    const navigate = useNavigate();
    const [invernadero, setInvernadero] = useState(null);
    
    const sensores = [
        { id: 'SEN-0101', invernaderoId: 'INV-0101', type: 'Temperatura', status: 'Activo' },
        { id: 'SEN-0102', invernaderoId: 'INV-0101', type: 'Humedad', status: 'Activo' },
        { id: 'SEN-0103', invernaderoId: 'INV-0201', type: 'Temperatura', status: 'Activo' },
        { id: 'SEN-0104', invernaderoId: 'INV-0301', type: 'CO2', status: 'Activo' },
        { id: 'SEN-0105', invernaderoId: 'INV-0401', type: 'CO2', status: 'Activo' },
        { id: 'SEN-0106', invernaderoId: 'INV-0101', type: 'Humedad', status: 'Inactivo' },
    ];

    const sensoresDelInvernadero = sensores.filter(sensor => sensor.invernaderoId === invernaderoId);

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

    if (!invernadero) {
        return <div className="text-center p-10">Cargando...</div>;
    }

    return (
        <>
            <BarraNavegacion />
            <div className="max-w-4xl mx-auto rounded-lg shadow-md p-6 mt-10 border border-zinc-600">
                {/* Nombre del invernadero */}
                <div className="flex justify-between items-center mb-10">
                    <h1 className="text-2xl font-bold text-gray-800">Sensores del {invernadero.name}</h1>
                    <button 
                        onClick={volverAInvernaderos}
                        className="px-4 py-2 bg-gray-200 rounded-md text-gray-700 hover:bg-gray-300">
                        Volver a Invernaderos
                    </button>
                </div>

                {/* Lista de sensores */}
                <div className="mt-6">
                    <div className='flex items-center justify-between mb-6'>
                    <h2 className="text-xl font-semibold text-gray-700 mb-4">Sensores Disponibles</h2>
                    <button className="px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 font-bold">Agregar</button>
                    </div>

                    {sensoresDelInvernadero.length > 0 ? (
                        <>
                            {/* Encabezados */}
                            <div className="grid grid-cols-4 gap-4 bg-gray-200 text-gray-700 py-3 px-4 rounded-t-lg font-semibold">
                                <div>ID Sensor</div>
                                <div>Tipo</div>
                                <div>Estado</div>
                                <div>Acciones</div>
                            </div>

                            {/* Filas */}
                            {sensoresDelInvernadero.map((sensor, index) => (
                                <div
                                    key={sensor.id}
                                    className={`grid grid-cols-4 gap-4 py-3 px-4 border-b ${index === sensoresDelInvernadero.length - 1 ? 'border-b-0' : ''}`}
                                >
                                    <div className="text-gray-800">{sensor.id}</div>
                                    <div className="text-gray-800">{sensor.type}</div>
                                    <div className={`text-gray-600 ${sensor.status === 'Activo' ? 'text-green-600' : 'text-red-600'}`}>
                                        {sensor.status}
                                    </div>
                                    <div className="flex space-x-2">
                                        <button className="inline-block px-3 py-1 text-sm font-semibold text-green-800 bg-green-100 rounded-full hover:underline">
                                            Editar
                                        </button>
                                        <button className="inline-block px-3 py-1 text-sm font-semibold text-red-800 bg-red-100 rounded-full hover:underline">
                                            Eliminar
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </>
                    ) : (
                        <div className="text-center p-5 bg-gray-50 rounded-md text-gray-600">
                            No hay sensores registrados para este invernadero.
                        </div>
                    )}
                </div>
            </div>
        </>
    );
}

export default ListaSensores;