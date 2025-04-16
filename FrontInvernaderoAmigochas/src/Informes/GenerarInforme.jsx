import React, { useState } from 'react';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';

function GenerarInforme() {
    const invernaderos = [
        { id: 'INV-0101', name: 'Invernadero 1', location: 'Sector 1, Fila 1' },
        { id: 'INV-0201', name: 'Invernadero 2', location: 'Sector 1, Fila 2' },
        { id: 'INV-0301', name: 'Invernadero 3', location: 'Sector 1, Fila 3' },
        { id: 'INV-0401', name: 'Invernadero 4', location: 'Sector 1, Fila 4' },
        { id: 'INV-0501', name: 'Invernadero 5', location: 'Sector 1, Fila 5' },
    ];

    const magnitudes = [
        { id: 'HUM', name: 'Humedad' },
        { id: 'TEM', name: 'Temperatura' },
        { id: 'CO2', name: 'CO2' },
    ];

    const [selectedInvernaderos, setSelectedInvernaderos] = useState([]);
    const [selectedMagnitudes, setSelectedMagnitudes] = useState([]);

    const handleInvernaderoChange = (e) => {
        const selectedId = e.target.value;
        const invernadero = invernaderos.find((inv) => inv.id === selectedId);
        if (invernadero && !selectedInvernaderos.some((inv) => inv.id === selectedId)) {
            setSelectedInvernaderos([...selectedInvernaderos, invernadero]);
        }
    };

    const removeInvernadero = (id) => {
        setSelectedInvernaderos(selectedInvernaderos.filter((inv) => inv.id !== id));
    };

    const handleMagnitudChange = (e) => {
        const selectedId = e.target.value;
        const magnitud = magnitudes.find((mag) => mag.id === selectedId);
        if (magnitud && !selectedMagnitudes.some((mag) => mag.id === selectedId)) {
            setSelectedMagnitudes([...selectedMagnitudes, magnitud]);
        }
    };

    const removeMagnitud = (id) => {
        setSelectedMagnitudes(selectedMagnitudes.filter((mag) => mag.id !== id));
    };

    return (
        <>
            <BarraNavegacion />
            <div className="bg-gray-100 min-h-screen pt-0">
                <div className="max-w-7xl mx-auto px-4">
                    <h1 className="text-2xl font-bold text-gray-800 mb-6 pt-6">Informes de invernaderos</h1>

                    <div className="grid grid-cols-3 gap-6">
                        {/* Tarjeta de la izquierda */}
                        <div className="col-span-1 bg-white p-6 rounded-lg shadow-md">
                            <h2 className="text-xl font-semibold text-gray-800 mb-2">Generar Informe</h2>
                            <p className="text-gray-600 mb-5">Configura los parámetros para tu informe</p>

                            {/* Rango de fechas */}
                            <div className="mb-6">
                                <h3 className="text-sm font-semibold text-gray-700 mb-2">Rango de fechas</h3>
                                <div className="flex space-x-4">
                                    <div className="flex flex-col">
                                        <label className="text-gray-700 text-sm font-medium">Fecha de inicio:</label>
                                        <input
                                            type="date"
                                            className="border border-gray-300 rounded-md p-2 mt-1 focus:outline-none focus:ring-2 focus:ring-green-500"
                                            defaultValue="2023-03-02"
                                        />
                                    </div>
                                    <div className="flex flex-col">
                                        <label className="text-gray-700 text-sm font-medium">Fecha de fin:</label>
                                        <input
                                            type="date"
                                            className="border border-gray-300 rounded-md p-2 mt-1 focus:outline-none focus:ring-2 focus:ring-green-500"
                                            defaultValue="2023-03-08"
                                        />
                                    </div>
                                </div>
                            </div>

                            {/* Magnitud */}
                            <div className="mb-6">
                                <h3 className="text-sm font-semibold text-gray-700 mb-2">Magnitud</h3>
                                <select
                                    className="w-full border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                                    onChange={handleMagnitudChange}
                                    value=""
                                >
                                    <option value="">
                                        {selectedMagnitudes.length > 0 ? 'Añadir otra magnitud' : 'Seleccionar'}
                                    </option>
                                    {magnitudes.map((magnitud) => (
                                        <option key={magnitud.id} value={magnitud.id}>
                                            {magnitud.name}
                                        </option>
                                    ))}
                                </select>
                                <div className="mt-3 flex flex-wrap gap-2">
                                    {selectedMagnitudes.map((magnitud) => (
                                        <span
                                            key={magnitud.id}
                                            className="bg-gray-100 text-gray-700 px-3 py-1 rounded-full flex items-center"
                                        >
                                            {magnitud.name}
                                            <button
                                                className="ml-2 text-gray-500 hover:text-gray-700"
                                                onClick={() => removeMagnitud(magnitud.id)}
                                            >
                                                ✕
                                            </button>
                                        </span>
                                    ))}
                                </div>
                            </div>

                            {/* Invernadero */}
                            <div className="mb-6">
                                <h3 className="text-sm font-semibold text-gray-700 mb-2">Invernadero</h3>
                                <select
                                    className="w-full border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                                    onChange={handleInvernaderoChange}
                                    value=""
                                >
                                    <option value="">
                                        {selectedInvernaderos.length > 0 ? 'Añadir otro invernadero' : 'Seleccionar'}
                                    </option>
                                    {invernaderos.map((invernadero) => (
                                        <option key={invernadero.id} value={invernadero.id}>
                                            {invernadero.name} - {invernadero.location}
                                        </option>
                                    ))}
                                </select>
                                <div className="mt-3 flex flex-wrap gap-2">
                                    {selectedInvernaderos.map((invernadero) => (
                                        <span
                                            key={invernadero.id}
                                            className="bg-gray-100 text-gray-700 px-3 py-1 rounded-full flex items-center"
                                        >
                                            {invernadero.name}
                                            <button
                                                className="ml-2 text-gray-500 hover:text-gray-700"
                                                onClick={() => removeInvernadero(invernadero.id)}
                                            >
                                                ✕
                                            </button>
                                        </span>
                                    ))}
                                </div>
                            </div>

                            {/* Botón Generar Informe */}
                            <button className="w-full px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600">
                                Generar Informe
                            </button>
                        </div>

                        {/* Tarjeta de la derecha */}
                        <div className="col-span-2 bg-white p-6 rounded-lg shadow-md">
                            <div className="flex justify-between items-center mb-4">
                                <h2 className="text-xl font-semibold text-gray-800">
                                    Informe
                                </h2>
                                <button className="flex items-center text-gray-600 hover:text-gray-800">
                                    <span className="mr-1">Exportar</span>
                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 16v2a2 2 0 002 2h12a2 2 0 002-2v-2M16 12l-4 4m0 0l-4-4m4 4V4"></path>
                                    </svg>
                                </button>
                            </div>
                            <p className="text-gray-600 mb-4">
                                Datos de humedad y temperatura del 02/03/2023 al 08/03/2023
                            </p>

                            <div className="flex space-x-2 mb-4">
                                <button className="px-4 py-2 bg-gray-100 text-gray-700 rounded-md">Humedad</button>
                                <button className="px-4 py-2 bg-gray-100 text-gray-700 rounded-md">Temperatura</button>
                            </div>

                            <div className="h-64 bg-gray-100 flex items-center justify-center rounded-md">
                                <p className="text-gray-500">
                                </p>
                            </div>

                            <div className="flex space-x-4 mt-4">
                                <div className="flex items-center">
                                    <span className="w-4 h-4 bg-blue-500 rounded-full mr-2"></span>
                                    <span className="text-gray-700">Inv 1 - SEN-0101</span>
                                </div>
                                <div className="flex items-center">
                                    <span className="w-4 h-4 bg-green-500 rounded-full mr-2"></span>
                                    <span className="text-gray-700">Inv 1 - SEN-0102</span>
                                </div>
                                <div className="flex items-center">
                                    <span className="w-4 h-4 bg-yellow-500 rounded-full mr-2"></span>
                                    <span className="text-gray-700">Inv 1 - SEN-0103</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>  
            </div>
        </>
    );
}

export default GenerarInforme;