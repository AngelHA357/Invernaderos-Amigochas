
import React from 'react';
import { useNavigate } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';

function ListaAlarmas() {
    const navigate = useNavigate();
    const [alarmas, setAlarmas] = React.useState([
        { id: "ALA-001", sensor: "SE-0101", magnitud: "Temperatura", valor_limite: "30°C", estado: "Activa" },
        { id: "ALA-002", sensor: "SE-0102", magnitud: "Humedad", valor_limite: "50%", estado: "Inactiva" },
        { id: "ALA-004", sensor: "SE-0103", magnitud: "Temperatura", valor_limite: "12°C", estado: "Inactiva" }
    ]);

    return (
        <>
            <BarraNavegacion />
            <div className="max-w-4xl mx-auto rounded-lg shadow-md p-6 mt-10 border border-zinc-600">
            {/* Título */}
            <h1 className="text-2xl font-bold text-gray-800">Alarmas - Panel de control</h1>

            <div className="mt-6">
            <div className='flex items-center justify-between mb-6'>
            <h2 className="text-xl font-semibold text-gray-700 mb-4">Alarmas disponibles - {alarmas.length}</h2>
            <button 
            onClick={() => navigate('/alarmas/agregar')}
            className="px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 font-bold">Agregar</button>
            </div>

            {/* Encabezados */}
            <div className="grid grid-cols-6 gap-4 bg-gray-200 text-gray-700 py-3 px-4 rounded-t-lg font-semibold">
            <div>ID</div>
            <div>Sensor</div>
            <div>Magnitud</div>
            <div>Valor límite</div>
            <div>Estado</div>
            <div>Acciones</div>
            </div>

            {/* Filas */}
            {alarmas.map((alarma, index) => (
            <div
                key={alarma.id}
                className={`grid grid-cols-6 gap-4 py-3 px-4 border-b ${index === alarmas.length - 1 ? 'border-b-0' : ''
                }`}
            >
                <div className="text-gray-800">{alarma.id}</div>
                <div className="text-gray-800">{alarma.sensor}</div>
                <div className="text-gray-600">{alarma.magnitud}</div>
                <div className="text-gray-600">{alarma.valor_limite}</div>
                <div>
                <button
                    className={`px-3 py-1 text-sm font-semibold rounded-full ${
                    alarma.estado === 'Activa'
                        ? 'bg-green-500 text-white hover:bg-green-600'
                        : 'bg-red-500 text-white hover:bg-red-600'
                    }`}
                    onClick={() => {
                    setAlarmas(prevAlarmas =>
                        prevAlarmas.map(a =>
                            a.id === alarma.id
                                ? { ...a, estado: a.estado === 'Activa' ? 'Inactiva' : 'Activa' }
                                : a
                        )
                    );
                    }}
                >
                    {alarma.estado}
                </button>
                </div>
                <div className="flex space-x-2 -ml-2">
                <button className="inline-block px-3 py-1 text-sm font-semibold text-green-800 bg-green-100 rounded-full hover:underline">
                Editar
                </button>
                <button className="inline-block px-3 py-1 text-sm font-semibold text-red-800 bg-red-100 rounded-full hover:underline">
                Eliminar
                </button>
                </div>
            </div>
            ))}
            </div>
            </div>
        </>
        );
}

export default ListaAlarmas;