import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';
import { obtenerSensorPorId, editarSensor } from '../services/sensorService';

function EditarSensor() {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [sectores, setSectores] = useState([]);
    const [filas, setFilas] = useState([]);
    const [error, setError] = useState('');
    const [validationErrors, setValidationErrors] = useState({});
    const [showModal, setShowModal] = useState(false);
    const [invernaderoSeleccionado, setInvernaderoSeleccionado] = useState(null);
    
    // Obtener sensor del sessionStorage para datos iniciales
    const sensorSeleccionado = JSON.parse(sessionStorage.getItem('sensorSeleccionado')) || {};
    console.log("Sensor a editar:", sensorSeleccionado); // Verificar los datos del sensor

    // Opciones para los tipos de sensores (limitados a Temperatura, Humedad y CO2)
    const tiposSensor = [
        { value: 'Temperatura', label: 'Temperatura' },
        { value: 'Humedad', label: 'Humedad' }
    ];

    // Opciones para las unidades según el tipo seleccionado
    const unidadesPorTipo = {
        'Temperatura': ['°C', '°F', 'K'],
        'Humedad': ['%', 'g/m³']
    };

    // Estado para el formulario - Inicializado con los valores del sensor seleccionado
    const [formData, setFormData] = useState({
        _id: sensorSeleccionado._id || '',
        idSensor: sensorSeleccionado.id || '',
        macAddress: sensorSeleccionado.macAddress || '',
        marca: sensorSeleccionado.marca || '',
        modelo: sensorSeleccionado.modelo || '',
        tipoSensor: sensorSeleccionado.magnitud || '', // Usar magnitud como tipo
        magnitud: sensorSeleccionado.magnitud || '',   // La magnitud es lo mismo que tipoSensor
        unidad: sensorSeleccionado.unidad || '',       // Unidad de medida
        sector: sensorSeleccionado.sector || '',
        fila: sensorSeleccionado.fila || '',
    });

    // Cargar datos del invernadero y configuración desde sessionStorage
    useEffect(() => {
        const cargarDatos = async () => {
            try {
                setLoading(true);
                
                // Obtener datos del invernadero seleccionado del sessionStorage
                const invernaderoData = sessionStorage.getItem('invernaderoSeleccionado');
                if (invernaderoData) {
                    const invData = JSON.parse(invernaderoData);
                    setInvernaderoSeleccionado(invData);
                    setSectores(invData.sectores || []);
                    setFilas(invData.filas || []);
                } else {
                    setError('No se encontró información del invernadero. Por favor, vuelva a la lista de invernaderos.');
                }
            } catch (err) {
                console.error('Error al cargar datos:', err);
                setError('Error al cargar los datos necesarios. Por favor, inténtelo de nuevo.');
            } finally {
                setLoading(false);
            }
        };
        
        cargarDatos();
    }, []);

    // Validar formato de MAC Address
    const isValidMACAddress = (mac) => {
        // Formato XX:XX:XX:XX:XX:XX o XX-XX-XX-XX-XX-XX
        const macRegex = /^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$/;
        return macRegex.test(mac);
    };

    // Validar ID de sensor (alfanumérico, guiones y guiones bajos permitidos)
    const isValidSensorId = (id) => {
        const idRegex = /^[a-zA-Z0-9-_]+$/;
        return idRegex.test(id);
    };

    // Manejar cambios generales en el formulario
    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        
        const fieldValue = type === 'checkbox' ? checked : value;
        
        setFormData(prevData => ({
            ...prevData,
            [name]: fieldValue
        }));

        // Limpiar errores de validación
        if (validationErrors[name]) {
            setValidationErrors(prev => ({
                ...prev,
                [name]: ''
            }));
        }
    };

    // Manejar cambio en el tipo de sensor (magnitud)
    const handleTipoChange = (e) => {
        const tipo = e.target.value;
        setFormData(prevData => ({
            ...prevData,
            tipoSensor: tipo,
            magnitud: tipo // La magnitud coincide con el tipo seleccionado
        }));

        // Limpiar errores
        if (validationErrors.tipoSensor) {
            setValidationErrors(prev => ({
                ...prev,
                tipoSensor: '',
                magnitud: ''
            }));
        }
    };

    // Validar el formulario
    const validateForm = () => {
        const errors = {};
        
        if (!formData.idSensor) {
            errors.idSensor = 'El ID del sensor es obligatorio';
        } else if (!isValidSensorId(formData.idSensor)) {
            errors.idSensor = 'El ID solo puede contener letras, números, guiones y guiones bajos';
        }
        
        if (!formData.macAddress) {
            errors.macAddress = 'La dirección MAC es obligatoria';
        } else if (!isValidMACAddress(formData.macAddress)) {
            errors.macAddress = 'El formato de MAC Address debe ser XX:XX:XX:XX:XX:XX o XX-XX-XX-XX-XX-XX';
        }
        
        if (!formData.marca) {
            errors.marca = 'La marca es obligatoria';
        } else if (formData.marca.length < 2) {
            errors.marca = 'La marca debe tener al menos 2 caracteres';
        }
        
        if (!formData.modelo) {
            errors.modelo = 'El modelo es obligatorio';
        }
        
        if (!formData.tipoSensor) {
            errors.tipoSensor = 'El tipo de sensor es obligatorio';
        }
        
        if (!formData.unidad) {
            errors.unidad = 'La unidad es obligatoria';
        }
        
        if (!formData.sector) {
            errors.sector = 'Debe seleccionar un sector';
        }
        
        if (!formData.fila) {
            errors.fila = 'Debe seleccionar una fila';
        }
        
        return errors;
    };

    // Enviar el formulario
    const handleSubmit = async (e) => {
        e.preventDefault();
        
        // Validar formulario
        const formErrors = validateForm();
        if (Object.keys(formErrors).length > 0) {
            setValidationErrors(formErrors);
            setError('Por favor, complete todos los campos requeridos y corrija los errores.');
            return;
        }
        
        try {
            setLoading(true);
            setError('');
            
            // Preparar datos para la API en el formato exacto que espera el backend
            const sensorData = {
                _id: formData._id,
                idSensor: formData.idSensor,
                macAddress: formData.macAddress,
                marca: formData.marca,
                modelo: formData.modelo,
                tipoSensor: formData.tipoSensor,
                magnitud: formData.magnitud,
                unidad: formData.unidad,
                idInvernadero: invernaderoSeleccionado?.id,
                sector: formData.sector,
                fila: formData.fila,
            };
            
            console.log('Enviando datos para editar:', sensorData);
            
            // Llamar a la API para editar el sensor
            await editarSensor(sensorData);
            
            // Actualizar el sensorSeleccionado en sessionStorage
            const sensorActualizado = {
                ...sensorSeleccionado,
                id: formData.idSensor,
                macAddress: formData.macAddress,
                marca: formData.marca,
                modelo: formData.modelo,
                type: formData.tipoSensor,
                magnitud: formData.magnitud,
                unidad: formData.unidad,
                invernaderoId: invernaderoSeleccionado?.id,
                sector: formData.sector,
                fila: formData.fila,
            };
            sessionStorage.setItem('sensorSeleccionado', JSON.stringify(sensorActualizado));
            
            // Mostrar mensaje de éxito
            setShowModal(true);
            
        } catch (err) {
            console.error('Error al actualizar sensor:', err);
            setError(`Error al actualizar el sensor: ${err.message}`);
        } finally {
            setLoading(false);
        }
    };

    // Manejar el botón volver a sensores
    const handleVolverASensores = () => {
        // Usar el invernadero seleccionado del sessionStorage para navegar
        if (invernaderoSeleccionado) {
            navigate(`/sensores/${invernaderoSeleccionado.id}`);
        } else {
            // Si no hay invernadero seleccionado, ir a la lista general
            navigate('/invernaderos');
        }
    };

    return (
        <>
            <BarraNavegacion />
            <div className="min-h-screen bg-gradient-to-b from-green-50 to-white p-6">
                <div className="max-w-3xl mx-auto bg-white p-6 rounded-lg shadow-lg border border-green-200">
                    {/* Encabezado */}
                    <div className="flex items-center justify-between mb-6">
                        <div className="flex items-center">
                            <div className="bg-green-100 p-3 rounded-full mr-4">
                                <span className="text-2xl" role="img" aria-label="sensor">📡</span>
                            </div>
                            <div>
                                <h1 className="text-2xl font-bold text-gray-800">Editar Sensor</h1>
                                {sensorSeleccionado && (
                                    <p className="text-sm text-green-600">ID: {sensorSeleccionado.id}</p>
                                )}
                                {invernaderoSeleccionado && (
                                    <p className="text-sm text-green-600">Invernadero: {invernaderoSeleccionado.name}</p>
                                )}
                            </div>
                        </div>
                        <button 
                            onClick={handleVolverASensores}
                            className="px-4 py-2 bg-green-100 rounded-md text-green-700 hover:bg-green-200 transition-colors duration-300 flex items-center shadow-sm">
                            <span className="mr-1">←</span> Volver a Sensores
                        </button>
                    </div>

                    {/* Mensaje de error */}
                    {error && (
                        <div className="bg-red-100 text-red-700 p-4 rounded-md mb-6">
                            <p className="font-bold">Error</p>
                            <p>{error}</p>
                        </div>
                    )}

                    {/* Formulario de edición */}
                    <form onSubmit={handleSubmit} className="space-y-6">
                        {/* Identificación del sensor */}
                        <div className="bg-gray-50 p-4 rounded-md border border-gray-200">
                            <h2 className="text-lg font-semibold text-gray-700 mb-3">Identificación del Sensor</h2>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1" htmlFor="idSensor">
                                        ID del Sensor *
                                    </label>
                                    <input
                                        type="text"
                                        id="idSensor"
                                        name="idSensor"
                                        value={formData.idSensor}
                                        onChange={handleChange}
                                        required
                                        className={`w-full border ${validationErrors.idSensor ? 'border-red-500' : 'border-gray-300'} rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500`}
                                    />
                                    {validationErrors.idSensor && (
                                        <p className="text-red-500 text-xs mt-1">{validationErrors.idSensor}</p>
                                    )}
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1" htmlFor="macAddress">
                                        Dirección MAC *
                                    </label>
                                    <input
                                        type="text"
                                        id="macAddress"
                                        name="macAddress"
                                        value={formData.macAddress}
                                        onChange={handleChange}
                                        required
                                        className={`w-full border ${validationErrors.macAddress ? 'border-red-500' : 'border-gray-300'} rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500`}
                                    />
                                    {validationErrors.macAddress && (
                                        <p className="text-red-500 text-xs mt-1">{validationErrors.macAddress}</p>
                                    )}
                                    <p className="text-xs text-gray-500 mt-1">
                                        Formato: XX:XX:XX:XX:XX:XX ó XX-XX-XX-XX-XX-XX
                                    </p>
                                </div>
                            </div>
                        </div>

                        {/* Características del sensor */}
                        <div className="bg-gray-50 p-4 rounded-md border border-gray-200">
                            <h2 className="text-lg font-semibold text-gray-700 mb-3">Características del Sensor</h2>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1" htmlFor="marca">
                                        Marca *
                                    </label>
                                    <input
                                        type="text"
                                        id="marca"
                                        name="marca"
                                        value={formData.marca}
                                        onChange={handleChange}
                                        required
                                        className={`w-full border ${validationErrors.marca ? 'border-red-500' : 'border-gray-300'} rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500`}
                                    />
                                    {validationErrors.marca && (
                                        <p className="text-red-500 text-xs mt-1">{validationErrors.marca}</p>
                                    )}
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1" htmlFor="modelo">
                                        Modelo *
                                    </label>
                                    <input
                                        type="text"
                                        id="modelo"
                                        name="modelo"
                                        value={formData.modelo}
                                        onChange={handleChange}
                                        required
                                        className={`w-full border ${validationErrors.modelo ? 'border-red-500' : 'border-gray-300'} rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500`}
                                    />
                                    {validationErrors.modelo && (
                                        <p className="text-red-500 text-xs mt-1">{validationErrors.modelo}</p>
                                    )}
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1" htmlFor="tipoSensor">
                                        Magnitud *
                                    </label>
                                    <select
                                        id="tipoSensor"
                                        name="tipoSensor"
                                        value={formData.tipoSensor}
                                        onChange={handleTipoChange}
                                        required
                                        className={`w-full border ${validationErrors.tipoSensor ? 'border-red-500' : 'border-gray-300'} rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500`}
                                    >
                                        <option value="">Seleccionar magnitud</option>
                                        {tiposSensor.map(tipo => (
                                            <option key={tipo.value} value={tipo.value}>
                                                {tipo.label}
                                            </option>
                                        ))}
                                    </select>
                                    {validationErrors.tipoSensor && (
                                        <p className="text-red-500 text-xs mt-1">{validationErrors.tipoSensor}</p>
                                    )}
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1" htmlFor="unidad">
                                        Unidad *
                                    </label>
                                    <select
                                        id="unidad"
                                        name="unidad"
                                        value={formData.unidad}
                                        onChange={handleChange}
                                        required
                                        className={`w-full border ${validationErrors.unidad ? 'border-red-500' : 'border-gray-300'} rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500`}
                                    >
                                        <option value="">Seleccionar unidad</option>
                                        {formData.tipoSensor && unidadesPorTipo[formData.tipoSensor]?.map(unidad => (
                                            <option key={unidad} value={unidad}>
                                                {unidad}
                                            </option>
                                        ))}
                                    </select>
                                    {validationErrors.unidad && (
                                        <p className="text-red-500 text-xs mt-1">{validationErrors.unidad}</p>
                                    )}
                                </div>
                            </div>
                        </div>

                        {/* Selección de ubicación */}
                        <div className="bg-green-50 p-4 rounded-md border border-green-200">
                            <h2 className="text-lg font-semibold text-gray-700 mb-3">Ubicación</h2>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1" htmlFor="sector">
                                        Sector *
                                    </label>
                                    <select
                                        id="sector"
                                        name="sector"
                                        value={formData.sector}
                                        onChange={handleChange}
                                        required
                                        className={`w-full border ${validationErrors.sector ? 'border-red-500' : 'border-gray-300'} rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500`}
                                        disabled={sectores.length === 0}
                                    >
                                        <option value="">Seleccionar sector</option>
                                        {sectores.map((sector, idx) => (
                                            <option key={idx} value={sector}>
                                                {sector}
                                            </option>
                                        ))}
                                    </select>
                                    {validationErrors.sector && (
                                        <p className="text-red-500 text-xs mt-1">{validationErrors.sector}</p>
                                    )}
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1" htmlFor="fila">
                                        Fila *
                                    </label>
                                    <select
                                        id="fila"
                                        name="fila"
                                        value={formData.fila}
                                        onChange={handleChange}
                                        required
                                        className={`w-full border ${validationErrors.fila ? 'border-red-500' : 'border-gray-300'} rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500`}
                                        disabled={filas.length === 0}
                                    >
                                        <option value="">Seleccionar fila</option>
                                        {filas.map((fila, idx) => (
                                            <option key={idx} value={fila}>
                                                {fila}
                                            </option>
                                        ))}
                                    </select>
                                    {validationErrors.fila && (
                                        <p className="text-red-500 text-xs mt-1">{validationErrors.fila}</p>
                                    )}
                                </div>
                            </div>
                        </div>


                        {/* Botones de acción */}
                        <div className="flex justify-end space-x-4 pt-4">
                            <button
                                type="button"
                                onClick={handleVolverASensores}
                                className="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 transition-colors duration-300"
                            >
                                Cancelar
                            </button>
                            <button
                                type="submit"
                                disabled={loading}
                                className={`px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600 transition-colors duration-300 flex items-center ${loading ? 'opacity-70 cursor-not-allowed' : ''}`}
                            >
                                {loading ? (
                                    <>
                                        <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                        </svg>
                                        Actualizando...
                                    </>
                                ) : 'Guardar Cambios'}
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
                                    handleVolverASensores();
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

