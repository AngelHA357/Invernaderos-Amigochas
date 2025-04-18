import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';

function AgregarAlarma() {
    const navigate = useNavigate();
    const [showModal, setShowModal] = useState(false);

    const [formData, setFormData] = useState({
        id: '',
        invernaderoId: '',
        magnitud: '',
        valorMinimo: '',
        valorMaximo: '',
        formaNotificacion: '',
        sensores: [],
    });

    const [errors, setErrors] = useState({});

    const sensores = [
        { id: 'SEN-0101', name: 'Sensor 1' },
        { id: 'SEN-0102', name: 'Sensor 2' },
        { id: 'SEN-0103', name: 'Sensor 3' },
    ];

    const formas_notificacion = [
        { id: 'SMS', name: 'Mensaje de texto' },
        { id: 'EMAIL', name: 'Correo electrónico' },
    ];

    const magnitudes = [
        { id: 'HUM', name: 'Humedad' },
        { id: 'TEMC', name: 'Temperatura (C°)' },
        { id: 'TEMF', name: 'Temperatura (F°)' },
        { id: 'CO2', name: 'CO2' },
    ];

    const invernaderos = [
        { id: 'INV-0101', name: 'Invernadero 1' },
        { id: 'INV-0201', name: 'Invernadero 2' },
        { id: 'INV-0301', name: 'Invernadero 3' },
    ];

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSensorChange = (e) => {
        const selectedId = e.target.value;
        if (!formData.sensores.includes(selectedId)) {
            setFormData({ ...formData, sensores: [...formData.sensores, selectedId] });
        }
    };

    const removeSensor = (id) => {
        setFormData({
            ...formData,
            sensores: formData.sensores.filter((sensorId) => sensorId !== id),
        });
    };

    const validateForm = () => {
        const newErrors = {};
        if (!formData.id) newErrors.id = 'El ID de la alarma es obligatorio.';
        if (!formData.invernaderoId) newErrors.invernaderoId = 'Debe seleccionar un invernadero.';
        if (!formData.magnitud) newErrors.magnitud = 'Debe seleccionar una magnitud.';
        if (!formData.valorMinimo) newErrors.valorMinimo = 'Debe ingresar un valor mínimo.';
        if (!formData.valorMaximo) newErrors.valorMaximo = 'Debe ingresar un valor máximo.';
        if (!formData.formaNotificacion) newErrors.formaNotificacion = 'Debe seleccionar una forma de notificación.';
        if (formData.sensores.length === 0) newErrors.sensores = 'Debe seleccionar al menos un sensor.';
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (validateForm()) {
            console.log('Datos de la alarma:', formData);
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
                            <span className="text-2xl" role="img" aria-label="alarma">🔔</span>
                        </div>
                        <div>
                            <h1 className="text-2xl font-bold text-gray-800">Agregar Alarma</h1>
                            <p className="text-sm text-green-600">Configura una nueva alarma para tu sistema</p>
                        </div>
                    </div>

                    <form onSubmit={handleSubmit}>
                        {/* Sección 1: Información básica */}
                        <div className="grid grid-cols-2 gap-4 mb-6">
                            <div>
                                <label className="block text-gray-700 font-medium mb-2">ID Alarma</label>
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

                        {/* Sección 2: Configuración de la alarma */}
                        <div className="grid grid-cols-2 gap-4 mb-6">
                            <div>
                                <label className="block text-gray-700 font-medium mb-2">Magnitud</label>
                                <select
                                    name="magnitud"
                                    value={formData.magnitud}
                                    onChange={handleChange}
                                    className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500 bg-white"
                                >
                                    <option value="">Seleccionar Magnitud</option>
                                    {magnitudes.map((magnitud) => (
                                        <option key={magnitud.id} value={magnitud.id}>
                                            {magnitud.name}
                                        </option>
                                    ))}
                                </select>
                                {errors.magnitud && <p className="text-red-500 text-xs mt-1">{errors.magnitud}</p>}
                            </div>
                            <div>
                                <label className="block text-gray-700 font-medium mb-2">Notificar a través de</label>
                                <select
                                    name="formaNotificacion"
                                    value={formData.formaNotificacion}
                                    onChange={handleChange}
                                    className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500 bg-white"
                                >
                                    <option value="">Seleccionar Forma de Notificación</option>
                                    {formas_notificacion.map((forma) => (
                                        <option key={forma.id} value={forma.id}>
                                            {forma.name}
                                        </option>
                                    ))}
                                </select>
                                {errors.formaNotificacion && <p className="text-red-500 text-xs mt-1">{errors.formaNotificacion}</p>}
                            </div>
                        </div>

                        {/* Sección 3: Valores límite */}
                        <div className="grid grid-cols-2 gap-4 mb-6">
                            <div>
                                <label className="block text-gray-700 font-medium mb-2">Valor Mínimo</label>
                                <input
                                    type="number"
                                    name="valorMinimo"
                                    value={formData.valorMinimo}
                                    onChange={handleChange}
                                    className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                                />
                                {errors.valorMinimo && <p className="text-red-500 text-xs mt-1">{errors.valorMinimo}</p>}
                            </div>
                            <div>
                                <label className="block text-gray-700 font-medium mb-2">Valor Máximo</label>
                                <input
                                    type="number"
                                    name="valorMaximo"
                                    value={formData.valorMaximo}
                                    onChange={handleChange}
                                    className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                                />
                                {errors.valorMaximo && <p className="text-red-500 text-xs mt-1">{errors.valorMaximo}</p>}
                            </div>
                        </div>

                        {/* Sección 4: Sensores */}
                        <div className="mb-6">
                            <label className="block text-gray-700 font-medium mb-2">Sensores</label>
                            <select
                                onChange={handleSensorChange}
                                className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500 bg-white"
                                value=""
                            >
                                <option value="">
                                    {formData.sensores.length > 0 ? 'Añadir otro sensor' : 'Seleccionar'}
                                </option>
                                {sensores.map((sensor) => (
                                    <option key={sensor.id} value={sensor.id}>
                                        {sensor.idd}
                                    </option>
                                ))}
                            </select>
                            <div className="mt-3 flex flex-wrap gap-2">
                                {formData.sensores.map((sensorId) => (
                                    <span
                                        key={sensorId}
                                        className="bg-gray-100 text-gray-700 px-3 py-1 rounded-full flex items-center"
                                    >
                                        {sensorId}
                                        <button
                                            type="button"
                                            className="ml-2 text-gray-500 hover:text-gray-700"
                                            onClick={() => removeSensor(sensorId)}
                                        >
                                            ✕
                                        </button>
                                    </span>
                                ))}
                            </div>
                            {errors.sensores && <p className="text-red-500 text-xs mt-1">{errors.sensores}</p>}
                        </div>

                        <div className="flex justify-center mt-6">
                            {/* Botón Agregar */}
                            <button
                                type="submit"
                                className="px-6 py-3 bg-green-600 text-white rounded-full hover:bg-green-700 transition-colors duration-300 shadow-sm flex items-center justify-center"
                            >
                                <span className="mr-2">+</span> Agregar Alarma
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
                        <p className="text-gray-600 mb-6">La alarma se ha creado correctamente.</p>
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

export default AgregarAlarma;