import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';

function EditarSensor() {
    const navigate = useNavigate();
    const sensorSeleccionado = JSON.parse(sessionStorage.getItem('sensorSeleccionado')) || {};

    const marcas = [
        { id: '1', nombre: 'Bosch', modelos: ['Bosch TE 2373', 'Bosch CO 7362', 'Bosch HU 2327'] },
        { id: '2', nombre: 'Banner', modelos: ['Banner 3000', 'Banner X1300'] },
        { id: '3', nombre: 'Cognex', modelos: ['COGX4000', 'COGY1000', 'COGZ9000', 'COGW8000'] },
        { id: '4', nombre: 'Steren', modelos: ['Steren Sen 1234', 'Steren Sen 4382'] },
        { id: '5', nombre: 'Omron', modelos: ['Omron Pro', 'Omron Lite', 'Omron Ultra'] },
    ];

    const tipos = [
        { id: 'HUM', name: 'Humedad' },
        { id: 'TEMC', name: 'Temperatura (C°)' },
        { id: 'TEMF', name: 'Temperatura (F°)' },
        { id: 'CO2', name: 'CO2' },
    ];

    const invernaderos = [
        {
            id: 'INV-0101',
            name: 'Invernadero 1',
            sectors: [
                { sector: 'Sector 1', rows: ['Fila 1', 'Fila 2', 'Fila 3'] },
                { sector: 'Sector 2', rows: ['Fila 1', 'Fila 2'] },
            ],
        },
        {
            id: 'INV-0201',
            name: 'Invernadero 2',
            sectors: [
                { sector: 'Sector 1', rows: ['Fila 1', 'Fila 2'] },
                { sector: 'Sector 3', rows: ['Fila 1', 'Fila 2', 'Fila 3', 'Fila 4'] },
            ],
        },
    ];

    const [formData, setFormData] = useState({
        id: sensorSeleccionado.id || '',
        invernaderoId: sensorSeleccionado.invernaderoId || '',
        sector: sensorSeleccionado.sector || '',
        fila: sensorSeleccionado.fila || '',
        tipo: tipos.find((t) => t.name === sensorSeleccionado.type)?.id || '',
        marca: marcas.find((m) => m.nombre === sensorSeleccionado.marca)?.id || '',
        modelo: sensorSeleccionado.modelo || '',
    });

    const [errors, setErrors] = useState({});
    const [showModal, setShowModal] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const validateForm = () => {
        const newErrors = {};
        if (!formData.id) newErrors.id = 'El ID del sensor es obligatorio.';
        if (!formData.invernaderoId) newErrors.invernaderoId = 'Debe seleccionar un invernadero.';
        if (!formData.sector) newErrors.sector = 'Debe seleccionar un sector.';
        if (!formData.fila) newErrors.fila = 'Debe seleccionar una fila.';
        if (!formData.tipo) newErrors.tipo = 'Debe seleccionar un tipo de sensor.';
        if (!formData.marca) newErrors.marca = 'Debe seleccionar una marca.';
        if (!formData.modelo) newErrors.modelo = 'Debe seleccionar un modelo.';
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (validateForm()) {
            console.log('Datos actualizados:', formData);
            setShowModal(true);
        }
    };

    return (
        <>
            <BarraNavegacion />
            <div className="min-h-screen bg-gradient-to-b from-green-50 to-white p-6">
                <div className="max-w-4xl mx-auto bg-white p-6 rounded-lg shadow-lg border border-green-200">
                    {/* Título con icono */}
                    <div className="flex items-center mb-6">
                        <div className="bg-green-100 p-3 rounded-full mr-4">
                            <span className="text-2xl" role="img" aria-label="sensor">📡</span>
                        </div>
                        <div>
                            <h1 className="text-2xl font-bold text-gray-800">Editar Sensor</h1>
                            <p className="text-sm text-green-600">Actualiza los datos del sensor seleccionado</p>
                        </div>
                    </div>

                    <form onSubmit={handleSubmit}>
                        {/* Sección 1: Información básica */}
                        <div className="grid grid-cols-2 gap-4 mb-6">
                            <div>
                                <label className="block text-gray-700 font-medium mb-2">ID Sensor</label>
                                <input
                                    type="text"
                                    name="id"
                                    value={formData.id}
                                    onChange={handleChange}
                                    className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                                />
                                {errors.id && <p className="text-red-500 text-xs mt-1">{errors.id}</p>}
                            </div>
                            <div>
                                <label className="block text-gray-700 font-medium mb-2">Invernadero</label>
                                <select
                                    name="invernaderoId"
                                    value={formData.invernaderoId}
                                    onChange={handleChange}
                                    className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500 bg-white"
                                >
                                    <option value="">Seleccionar Invernadero</option>
                                    {invernaderos.map((invernadero) => (
                                        <option key={invernadero.id} value={invernadero.id}>
                                            {invernadero.name}
                                        </option>
                                    ))}
                                </select>
                                {errors.invernaderoId && <p className="text-red-500 text-xs mt-1">{errors.invernaderoId}</p>}
                            </div>
                        </div>

                        {/* Sección 2: Ubicación */}
                        <div className="grid grid-cols-2 gap-4 mb-6">
                            <div>
                                <label className="block text-gray-700 font-medium mb-2">Sector</label>
                                <select
                                    name="sector"
                                    value={formData.sector}
                                    onChange={handleChange}
                                    className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500 bg-white"
                                >
                                    <option value="">Seleccionar Sector</option>
                                    {formData.invernaderoId &&
                                        invernaderos
                                            .find((inv) => inv.id === formData.invernaderoId)
                                            ?.sectors.map((sector, index) => (
                                                <option key={index} value={sector.sector}>
                                                    {sector.sector}
                                                </option>
                                            ))}
                                </select>
                                {errors.sector && <p className="text-red-500 text-xs mt-1">{errors.sector}</p>}
                            </div>
                            <div>
                                <label className="block text-gray-700 font-medium mb-2">Fila</label>
                                <select
                                    name="fila"
                                    value={formData.fila}
                                    onChange={handleChange}
                                    className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500 bg-white"
                                >
                                    <option value="">Seleccionar Fila</option>
                                    {formData.invernaderoId &&
                                        formData.sector &&
                                        invernaderos
                                            .find((inv) => inv.id === formData.invernaderoId)
                                            ?.sectors.find((sec) => sec.sector === formData.sector)
                                            ?.rows.map((fila, index) => (
                                                <option key={index} value={fila}>
                                                    {fila}
                                                </option>
                                            ))}
                                </select>
                                {errors.fila && <p className="text-red-500 text-xs mt-1">{errors.fila}</p>}
                            </div>
                        </div>

                        {/* Sección 3: Configuración del sensor */}
                        <div className="grid grid-cols-2 gap-4 mb-6">
                            <div>
                                <label className="block text-gray-700 font-medium mb-2">Tipo de Sensor</label>
                                <select
                                    name="tipo"
                                    value={formData.tipo}
                                    onChange={handleChange}
                                    className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500 bg-white"
                                >
                                    <option value="">Seleccionar Tipo de Sensor</option>
                                    {tipos.map((t) => (
                                        <option key={t.id} value={t.id}>
                                            {t.name}
                                        </option>
                                    ))}
                                </select>
                                {errors.tipo && <p className="text-red-500 text-xs mt-1">{errors.tipo}</p>}
                            </div>
                            <div>
                                <label className="block text-gray-700 font-medium mb-2">Marca</label>
                                <select
                                    name="marca"
                                    value={formData.marca}
                                    onChange={handleChange}
                                    className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500 bg-white"
                                >
                                    <option value="">Seleccionar Marca</option>
                                    {marcas.map((marca) => (
                                        <option key={marca.id} value={marca.id}>
                                            {marca.nombre}
                                        </option>
                                    ))}
                                </select>
                                {errors.marca && <p className="text-red-500 text-xs mt-1">{errors.marca}</p>}
                            </div>
                        </div>

                        {/* Sección 4: Modelo */}
                        <div className="mb-6">
                            <label className="block text-gray-700 font-medium mb-2">Modelo</label>
                            <select
                                name="modelo"
                                value={formData.modelo}
                                onChange={handleChange}
                                className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500 bg-white"
                            >
                                <option value="">Seleccionar Modelo</option>
                                {formData.marca &&
                                    marcas
                                        .find((marca) => marca.id === formData.marca)
                                        ?.modelos.map((modelo, index) => (
                                            <option key={index} value={modelo}>
                                                {modelo}
                                            </option>
                                        ))}
                            </select>
                            {errors.modelo && <p className="text-red-500 text-xs mt-1">{errors.modelo}</p>}
                        </div>

                        <div className="flex justify-center mt-6">
                            {/* Botón Actualizar */}
                            <button
                                type="submit"
                                className="px-6 py-3 bg-green-600 text-white rounded-full hover:bg-green-700 transition-colors duration-300 shadow-sm flex items-center justify-center"
                            >
                                <span className="mr-2">✔</span> Actualizar Sensor
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            {/* Modal de éxito */}
            {showModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full">
                        <h3 className="text-xl font-bold text-gray-800 mb-4">¡Éxito!</h3>
                        <p className="text-gray-600 mb-6">El sensor se ha actualizado correctamente.</p>
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

export default EditarSensor;