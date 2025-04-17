import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';

function EditarAlarma() {
    const navigate = useNavigate();
    const [showModal, setShowModal] = useState(false);

    const sensores = [
        { id: 'SEN-0101', invernaderoId: 'INV-0101', invernaderoNombre: 'Invernadero 1', type: 'Temperatura (C°)', sector: 'Sector 1', marca: 'Steren', fila: 'Fila 1', modelo: 'Steren Sen 1234', status: 'Activo' },
        { id: 'SEN-0102', invernaderoId: 'INV-0101', invernaderoNombre: 'Invernadero 1', type: 'Temperatura (F°)', sector: 'Sector 2', marca: 'Omron', fila: 'Fila 2', modelo: 'Omron Lite', status: 'Inactivo' },
        { id: 'SEN-0103', invernaderoId: 'INV-0201', invernaderoNombre: 'Invernadero 2', type: 'Humedad', sector: 'Sector 5', marca: 'Cognex', fila: 'Fila 6', modelo: 'COGY1000', status: 'Activo' },
        { id: 'SEN-0104', invernaderoId: 'INV-0301', invernaderoNombre: 'Invernadero 3', type: 'CO2', sector: 'Sector 4', marca: 'Banner', fila: 'Fila 1', modelo: 'Banner X1300', status: 'Activo' },
        { id: 'SEN-0105', invernaderoId: 'INV-0401', invernaderoNombre: 'Invernadero 4', type: 'Temperatura (F°)', sector: 'Sector 6', marca: 'Bosch', fila: 'Fila 7', modelo: 'Bosch HU 2327', status: 'Activo' },
        { id: 'SEN-0106', invernaderoId: 'INV-0101', invernaderoNombre: 'Invernadero 1', type: 'Humedad', sector: 'Sector 9', marca: 'Bosch', fila: 'Fila 7', modelo: 'Bosch TE 2373', status: 'Inactivo' }
    ];

    const [selectedSensores, setSelectedSensores] = useState([]);

    const handleSensorChange = (e) => {
        const selectedId = e.target.value;
        const sensor = sensores.find((sensor) => sensor.id === selectedId);
        if (sensor && !selectedSensores.some((sensor) => sensor.id === selectedId)) {
            setSelectedSensores([...selectedSensores, sensor]);
        }
    };

    const removeSensor = (id) => {
        setSelectedSensores(selectedSensores.filter((sensor) => sensor.id !== id));
    };

    const formas_notificacion = [
        { id: 'SMS', name: 'Mensaje de texto' },
        { id: 'EMAIL', name: 'Correo electrónico' }
    ];

    const magnitud = [
        { id: 'HUM', name: 'Humedad' },
        { id: 'TEMC', name: 'Temperatura (C°)' },
        { id: 'TEMF', name: 'Temperatura (F°)' },
        { id: 'CO2', name: 'CO2' }
    ];

    const invernaderos = [
        {
            id: 'INV-0101',
            name: 'Invernadero 1',
            sectors: [
                { sector: 'Sector 1', rows: ['Fila 1', 'Fila 2', 'Fila 3'] },
                { sector: 'Sector 2', rows: ['Fila 1', 'Fila 2'] }
            ]
        },
        {
            id: 'INV-0201',
            name: 'Invernadero 2',
            sectors: [
                { sector: 'Sector 1', rows: ['Fila 1', 'Fila 2'] },
                { sector: 'Sector 3', rows: ['Fila 1', 'Fila 2', 'Fila 3', 'Fila 4'] }
            ]
        },
        {
            id: 'INV-0301',
            name: 'Invernadero 3',
            sectors: [
                { sector: 'Sector 2', rows: ['Fila 1', 'Fila 2', 'Fila 3'] },
                { sector: 'Sector 4', rows: ['Fila 1'] }
            ]
        },
        {
            id: 'INV-0401',
            name: 'Invernadero 4',
            sectors: [
                { sector: 'Sector 1', rows: ['Fila 1', 'Fila 2', 'Fila 3', 'Fila 4'] },
                { sector: 'Sector 5', rows: ['Fila 1', 'Fila 2'] }
            ]
        },
        {
            id: 'INV-0501',
            name: 'Invernadero 5',
            sectors: [
                { sector: 'Sector 3', rows: ['Fila 1', 'Fila 2', 'Fila 3'] },
                { sector: 'Sector 6', rows: ['Fila 1', 'Fila 2', 'Fila 3', 'Fila 4', 'Fila 5'] }
            ]
        },
    ];

    return (
        <>
            <BarraNavegacion />
            <div className="max-w-4xl mx-auto rounded-lg shadow-md p-6 mt-10 border border-zinc-600">
                {/* Título */}
                <h1 className="text-2xl font-bold text-gray-800">Editar Alarma</h1>

                <div className="mt-6">
                    <div className='flex space-x-4 ml-14 mr-14 justify-between'>
                        <div className="flex flex-col">
                            <h3 className="text-lg font-semibold text-gray-700 mb-2">ID Alarma</h3>
                            <input type="text" className="w-80 border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500" />
                        </div>
                        <div className="flex flex-col">
                            <h3 className="text-lg font-semibold text-gray-700 mb-2">Invernadero</h3>
                            <select
                                className="w-80 border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                            >
                                <option value="">Seleccionar Invernadero</option>
                                {invernaderos.map((invernadero) => (
                                    <option key={invernadero.id} value={invernadero.id}>
                                        {invernadero.name}
                                    </option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div className='flex space-x-4 ml-14 mr-14 mt-4 justify-between'>
                        <div className="flex flex-col">
                            <h3 className="text-lg font-semibold text-gray-700 mb-2">Magnitud</h3>
                            <select
                                className="w-80 border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                            >
                                <option value="">Seleccionar Magnitud</option>
                                {magnitud.map((sensor) => (
                                    <option key={sensor.id} value={sensor.id}>
                                        {sensor.name}
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div className="flex flex-col">
                            <h3 className="text-lg font-semibold text-gray-700 mb-2">Valor mínimo</h3>
                            <input type="number" className="w-80 border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500" />
                        </div>
                    </div>

                    <div className='flex space-x-4 ml-14 mr-14 mt-4 justify-between'>
                        <div className="flex flex-col">
                            <h3 className="text-lg font-semibold text-gray-700 mb-2">Notificar a través de</h3>
                            <select
                                className="w-80 border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                            >
                                <option value="">Seleccionar Forma de Notificación</option>
                                {formas_notificacion.map((forma) => (
                                    <option key={forma.id} value={forma.id}>
                                        {forma.name}
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div className="flex flex-col">
                            <h3 className="text-lg font-semibold text-gray-700 mb-2">Valor máximo</h3>
                            <input type="number" className="w-80 border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500" />
                        </div>
                    </div>

                    <div className='flex space-x-4 ml-14 mr-14 mt-4 justify-between'>
                        <div className="flex flex-col">
                            <h3 className="text-lg font-semibold text-gray-700 mb-2">Sensores</h3>
                            <select
                                className="w-80 border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                                onChange={handleSensorChange}
                                value=""
                            >
                                <option value="">
                                    {selectedSensores.length > 0 ? 'Añadir otro sensor' : 'Seleccionar'}
                                </option>
                                {sensores.map((sensor) => (
                                    <option key={sensor.id} value={sensor.id}>
                                        {sensor.id}
                                    </option>
                                ))}
                            </select>
                            <div className="mt-3 flex flex-wrap gap-2">
                                {selectedSensores.map((sensor) => (
                                    <span
                                        key={sensor.id}
                                        className="bg-gray-100 text-gray-700 px-3 py-1 rounded-full flex items-center"
                                    >
                                        {sensor.id}
                                        <button
                                            className="ml-2 text-gray-500 hover:text-gray-700"
                                            onClick={() => removeSensor(sensor.id)}
                                        >
                                            ✕
                                        </button>
                                    </span>
                                ))}
                            </div>
                        </div>
                    </div>
                    <div className='flex space-x-4 ml-14 mr-14 mt-4 justify-end'>
                        <div className="flex justify-center space-x-4 mt-6">
                            <button
                                onClick={() => navigate(-1)}
                                className="text-lg px-10 py-4 bg-red-500 text-white rounded-full hover:bg-red-600 font-bold">Cancelar</button>
                            <button
                                onClick={() => { setShowModal(true); }}
                                className="text-lg px-10 py-4 bg-green-500 text-white rounded-full hover:bg-green-600 font-bold">Actualizar</button>
                        </div>
                    </div>
                </div>
            </div>

            {/* Modal de éxito */}
            {showModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full">
                        <h3 className="text-xl font-bold text-gray-800 mb-4">¡Éxito!</h3>
                        <p className="text-gray-600 mb-6">La alarma se ha actualizado correctamente.</p>
                        <div className="flex justify-center">
                            <button
                                onClick={() => {
                                    setShowModal(false);
                                    navigate(-1);
                                }}
                                className="px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 font-bold"
                            >
                                Confirmar
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}

export default EditarAlarma;