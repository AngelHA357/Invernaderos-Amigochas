import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';

function AgregarSensor() {
    const navigate = useNavigate();

    const marcas = [
        {
            id: 1,
            nombre: 'Bosch',
            modelos: ['Bosch TE 2373', 'Bosch CO 7362', 'Bosch HU 2327']
        },
        {
            id: 2,
            nombre: 'Banner',
            modelos: ['Banner 3000', 'Banner X1300']
        },
        {
            id: 3,
            nombre: 'Cognex',
            modelos: ['COGX4000', 'COGY1000', 'COGZ9000', 'COGW8000']
        },
        {
            id: 4,
            nombre: 'Steren',
            modelos: ['Steren Sen 1234', 'Steren Sen 4382']
        },
        {
            id: 5,
            nombre: 'Omron',
            modelos: ['Omron Pro', 'Omron Lite', 'Omron Ultra']
        }
    ];

    const tipo = [
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

    const [selectedMarca, setSelectedMarca] = useState("");
    const [selectedTipo, setSelectedTipo] = useState("");
    const [selectedInvernadero, setSelectedInvernadero] = useState("");
    const [selectedSector, setSelectedSector] = useState("");
    const [showModal, setShowModal] = useState(false);

    return (
        <>
            <BarraNavegacion />
            <div className="max-w-4xl mx-auto rounded-lg shadow-md p-6 mt-10 border border-zinc-600">
                {/* Título */}
                <h1 className="text-2xl font-bold text-gray-800">Agregar Sensor</h1>

                <div className="mt-6">
                    <div className='flex space-x-4 ml-14 mr-14 justify-between'>
                        <div className="flex flex-col">
                            <h3 className="text-lg font-semibold text-gray-700 mb-2">ID Sensor</h3>
                            <input type="text" className="w-80 border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500" />
                        </div>
                        <div className="flex flex-col">
                            <h3 className="text-lg font-semibold text-gray-700 mb-2">Invernadero</h3>
                            <select
                                className="w-80 border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                                onChange={(e) => {
                                    setSelectedInvernadero(e.target.value);
                                    setSelectedSector(""); // Reset sector when invernadero changes
                                }}
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
                            <h3 className="text-lg font-semibold text-gray-700 mb-2">Tipo de Sensor</h3>
                            <select
                                className="w-80 border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                                onChange={(e) => setSelectedTipo(e.target.value)}
                            >
                                <option value="">Seleccionar Tipo de Sensor</option>
                                {tipo.map((sensor) => (
                                    <option key={sensor.id} value={sensor.id}>
                                        {sensor.name}
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div className="flex flex-col">
                            <h3 className="text-lg font-semibold text-gray-700 mb-2">Sector</h3>
                            <select
                                className="w-80 border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                                onChange={(e) => setSelectedSector(e.target.value)}
                            >
                                <option value="">Seleccionar Sector</option>
                                {selectedInvernadero &&
                                    invernaderos
                                        .find((invernadero) => invernadero.id === selectedInvernadero)
                                        ?.sectors.map((sector, index) => (
                                            <option key={index} value={sector.sector}>
                                                {sector.sector}
                                            </option>
                                        ))}
                            </select>
                        </div>
                    </div>

                    <div className='flex space-x-4 ml-14 mr-14 mt-4 justify-between'>
                        <div className="flex flex-col">
                            <h3 className="text-lg font-semibold text-gray-700 mb-2">Marca</h3>
                            <select
                                className="w-80 border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                                onChange={(e) => setSelectedMarca(e.target.value)}
                            >
                                <option value="">Seleccionar Marca</option>
                                {marcas.map((marca) => (
                                    <option key={marca.id} value={marca.id}>
                                        {marca.nombre}
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div className="flex flex-col">
                            <h3 className="text-lg font-semibold text-gray-700 mb-2">Fila</h3>
                            <select
                                className="w-80 border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                            >
                                <option value="">Seleccionar Fila</option>
                                {selectedInvernadero &&
                                    selectedSector &&
                                    invernaderos
                                        .find((invernadero) => invernadero.id === selectedInvernadero)
                                        ?.sectors.find((sector) => sector.sector === selectedSector)
                                        ?.rows.map((fila, index) => (
                                            <option key={index} value={fila}>
                                                {fila}
                                            </option>
                                        ))}
                            </select>
                        </div>
                    </div>

                    <div className='flex space-x-4 ml-14 mr-14 mt-4 justify-between'>
                        <div className="flex flex-col">
                            <h3 className="text-lg font-semibold text-gray-700 mb-2">Modelo</h3>
                            <select
                                className="w-80 border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                            >
                                <option value="">Seleccionar Modelo</option>
                                {selectedMarca &&
                                    marcas
                                        .find((marca) => marca.id === parseInt(selectedMarca))
                                        ?.modelos.map((modelo, index) => (
                                            <option key={index} value={modelo}>
                                                {modelo}
                                            </option>
                                        ))}
                            </select>
                        </div>
                        <div className="flex justify-center space-x-4 mt-6">
                            <button
                                onClick={() => navigate('/')}
                                className="text-lg px-10 py-2 bg-red-500 text-white rounded-full hover:bg-red-600 font-bold">Cancelar</button>
                            <button
                                onClick={() => {setShowModal(true);}}
                                className="text-lg px-10 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 font-bold">Agregar</button>
                        </div>
                    </div>
                </div>
            </div>

            {/* Modal de éxito */}
            {showModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full">
                        <h3 className="text-xl font-bold text-gray-800 mb-4">¡Éxito!</h3>
                        <p className="text-gray-600 mb-6">El sensor se ha creado correctamente.</p>
                        <div className="flex justify-center">
                            <button
                                onClick={() => {
                                    setShowModal(false);
                                    navigate('/'); // O la ruta que desees
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

export default AgregarSensor;