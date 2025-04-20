import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';
import { getSensorById, editarSensor, getAllInvernaderos } from '../services/sensorService';

function EditarSensor() {
    const navigate = useNavigate();
    const { id } = useParams();
    
    const [invernaderos, setInvernaderos] = useState([]);
    const [invernaderoSeleccionado, setInvernaderoSeleccionado] = useState(null);
    
    const [formData, setFormData] = useState({
        idSensor: '',
        macAddress: '',
        marca: '',
        modelo: '',
        tipoSensor: '',
        magnitud: '',
        sector: '',
        fila: '',
        estado: true
    });

    const [errors, setErrors] = useState({});
    const [showModal, setShowModal] = useState(false);
    const [loading, setLoading] = useState(true);
    const [apiError, setApiError] = useState('');
    
    useEffect(() => {
        const cargarDatos = async () => {
            try {
                setLoading(true);
                
                // Cargar invernaderos
                const invernaderosList = await getAllInvernaderos();
                setInvernaderos(invernaderosList);
                
                // Cargar datos del sensor
                if (id) {
                    const sensorData = await getSensorById(id);
                    
                    setFormData({
                        idSensor: sensorData.idSensor,
                        macAddress: sensorData.macAddress || '',
                        marca: sensorData.marca,
                        modelo: sensorData.modelo,
                        tipoSensor: sensorData.tipoSensor,
                        magnitud: sensorData.magnitud,
                        sector: sensorData.sector,
                        fila: sensorData.fila,
                        estado: sensorData.estado
                    });
                    
                    // Buscar el invernadero al que pertenece el sensor
                    const inv = invernaderosList.find(inv => inv.id === sensorData.idInvernadero);
                    if (inv) {
                        setInvernaderoSeleccionado(inv);
                    }
                }
            } catch (error) {
                console.error('Error al cargar datos:', error);
                setApiError('No se pudieron cargar los datos. Por favor, intente nuevamente.');
            } finally {
                setLoading(false);
            }
        };
        
        cargarDatos();
    }, [id]);
    
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
        
        // Actualizar magnitud automáticamente según el tipo de sensor
        if (name === 'tipoSensor') {
            let magnitud = '%';
            switch(value) {
                case 'Humedad':
                    magnitud = '%';
                    break;
                case 'Temperatura':
                    magnitud = '°C';
                    break;
                case 'CO2':
                    magnitud = 'ppm';
                    break;
                case 'Luz':
                    magnitud = 'lux';
                    break;
                default:
                    magnitud = '';
            }
            setFormData(prev => ({ ...prev, magnitud }));
        }
    };
    
    const validateForm = () => {
        const newErrors = {};
        if (!formData.idSensor) newErrors.idSensor = 'El ID del sensor es obligatorio.';
        if (!formData.macAddress) newErrors.macAddress = 'La dirección MAC es obligatoria.';
        if (!formData.sector) newErrors.sector = 'Debe seleccionar un sector.';
        if (!formData.fila) newErrors.fila = 'Debe seleccionar una fila.';
        if (!formData.tipoSensor) newErrors.tipoSensor = 'Debe seleccionar un tipo de sensor.';
        if (!formData.marca) newErrors.marca = 'Debe seleccionar una marca.';
        if (!formData.modelo) newErrors.modelo = 'Debe seleccionar un modelo.';
        if (!invernaderoSeleccionado) newErrors.invernadero = 'Debe seleccionar un invernadero.';
        
        // Validar formato de MAC address (XX:XX:XX:XX:XX:XX)
        const macRegex = /^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$/;
        if (formData.macAddress && !macRegex.test(formData.macAddress)) {
            newErrors.macAddress = 'La dirección MAC debe tener el formato XX:XX:XX:XX:XX:XX';
        }
        
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (validateForm()) {
            setLoading(true);
            setApiError('');
            
            try {
                const sensorData = {
                    idSensor: formData.idSensor,
                    macAddress: formData.macAddress,
                    marca: formData.marca,
                    modelo: formData.modelo,
                    tipoSensor: formData.tipoSensor,
                    magnitud: formData.magnitud,
                    idInvernadero: invernaderoSeleccionado.id,
                    sector: formData.sector,
                    fila: formData.fila,
                    estado: formData.estado
                };
                
                const response = await editarSensor(sensorData);
                console.log('Sensor actualizado:', response);
                setShowModal(true);
            } catch (error) {
                console.error('Error al actualizar el sensor:', error);
                setApiError('No se pudo actualizar el sensor. Por favor, inténtelo de nuevo.');
            } finally {
                setLoading(false);
            }
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
                                    name="idSensor"
                                    value={formData.idSensor}
                                    onChange={handleChange}
                                    className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                                />
                                {errors.idSensor && <p className="text-red-500 text-xs mt-1">{errors.idSensor}</p>}
                            </div>
                            <div>
                                <label className="block text-gray-700 font-medium mb-2">Invernadero</label>
                                <select
                                    name="idInvernadero"
                                    value={invernaderoSeleccionado ? invernaderoSeleccionado.id : ''}
                                    onChange={(e) => {
                                        const inv = invernaderos.find(inv => inv.id === e.target.value);
                                        setInvernaderoSeleccionado(inv);
                                    }}
                                    className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500 bg-white"
                                >
                                    <option value="">Seleccionar Invernadero</option>
                                    {invernaderos.map((invernadero) => (
                                        <option key={invernadero.id} value={invernadero.id}>
                                            {invernadero.name}
                                        </option>
                                    ))}
                                </select>
                                {errors.invernadero && <p className="text-red-500 text-xs mt-1">{errors.invernadero}</p>}
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
                                    {invernaderoSeleccionado &&
                                        invernaderoSeleccionado.sectors.map((sector, index) => (
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
                                    {invernaderoSeleccionado &&
                                        formData.sector &&
                                        invernaderoSeleccionado.sectors.find((sec) => sec.sector === formData.sector)
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
                                    name="tipoSensor"
                                    value={formData.tipoSensor}
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
                                {errors.tipoSensor && <p className="text-red-500 text-xs mt-1">{errors.tipoSensor}</p>}
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

                        <div className="mb-6">
                            <label className="block text-gray-700 font-medium mb-2">MAC Address</label>
                            <input
                                type="text"
                                name="macAddress"
                                value={formData.macAddress}
                                onChange={handleChange}
                                className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                            />
                            {errors.macAddress && <p className="text-red-500 text-xs mt-1">{errors.macAddress}</p>}
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