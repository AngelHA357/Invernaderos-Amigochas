import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { registrarSensor, obtenerInvernaderos } from '../services/sensorService';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';

function AgregarSensor() {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [validationErrors, setValidationErrors] = useState({});
    const [success, setSuccess] = useState(false);
    const [invernadero, setInvernadero] = useState(null);
    const [sectores, setSectores] = useState([]);
    const [filas, setFilas] = useState([]);
    
    // Estado para el formulario
    const [formData, setFormData] = useState({
        idSensor: '',
        macAddress: '',
        marca: '',
        modelo: '',
        tipoSensor: '',
        magnitud: '',
        unidad: '',
        idInvernadero: '',
        sector: '',
        fila: '',
        estado: true
    });

    // Opciones para los tipos de sensores (limitados a Temperatura, Humedad y CO2)
    const tiposSensor = [
        { value: 'Temperatura', label: 'Temperatura' },
        { value: 'Humedad', label: 'Humedad' },
        { value: 'CO2', label: 'CO2' }
    ];

    // Opciones para las unidades según el tipo seleccionado
    const unidadesPorTipo = {
        'Temperatura': ['°C', '°F', 'K'],
        'Humedad': ['%', 'g/m³']
    };

    // Al iniciar, cargar el invernadero desde sessionStorage
    useEffect(() => {
        const cargarInvernadero = async () => {
            try {
                // Obtener el invernadero del sessionStorage
                const invernaderoData = sessionStorage.getItem('invernaderoSeleccionado');
                if (invernaderoData) {
                    const invData = JSON.parse(invernaderoData);
                    setInvernadero(invData);
                    setFormData(prev => ({
                        ...prev,
                        idInvernadero: invData.id
                    }));
                    setSectores(invData.sectores || []);
                    setFilas(invData.filas || []);
                } else {
                    // Si no hay invernadero seleccionado, redirigir a la lista
                    navigate('/invernaderos');
                    return;
                }
            } catch (error) {
                console.error("Error al cargar el invernadero:", error);
                setError("No se pudo cargar la información del invernadero");
            }
        };
        
        cargarInvernadero();
    }, [navigate]);

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

    // Validar campos del formulario
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
            errors.unidad = 'La unidad de medida es obligatoria';
        }
        
        if (!formData.idInvernadero) {
            errors.idInvernadero = 'No se ha detectado un invernadero seleccionado';
        }
        
        if (!formData.sector) {
            errors.sector = 'Debe seleccionar un sector';
        }
        
        if (!formData.fila) {
            errors.fila = 'Debe seleccionar una fila';
        }
        
        return errors;
    };

    // Maneja cambios en el formulario
    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        
        // Para campos checkbox usamos 'checked', para el resto 'value'
        const fieldValue = type === 'checkbox' ? checked : value;
        
        // Actualiza el estado del formulario
        setFormData(prevData => ({
            ...prevData,
            [name]: fieldValue
        }));
        
        // Limpiar errores de validación cuando el usuario corrige los campos
        if (validationErrors[name]) {
            setValidationErrors(prev => ({
                ...prev,
                [name]: ''
            }));
        }
    };

    // Maneja la selección de tipo de sensor para actualizar opciones de unidad y magnitud
    const handleTipoChange = (e) => {
        const tipo = e.target.value;
        setFormData(prevData => ({
            ...prevData,
            tipoSensor: tipo,
            magnitud: tipo, // La magnitud coincide con el tipo de sensor
            // Reseteamos la unidad o seleccionamos la primera disponible
            unidad: unidadesPorTipo[tipo] ? unidadesPorTipo[tipo][0] : ''
        }));
        
        // Limpiar errores de validación
        if (validationErrors.tipoSensor) {
            setValidationErrors(prev => ({
                ...prev,
                tipoSensor: '',
                magnitud: '',
                unidad: ''
            }));
        }
    };

    // Envía el formulario
    const handleSubmit = async (e) => {
        e.preventDefault();
        
        // Validar formulario
        const errors = validateForm();
        if (Object.keys(errors).length > 0) {
            setValidationErrors(errors);
            setError('Por favor, corrija los errores en el formulario antes de continuar.');
            return;
        }
        
        try {
            setLoading(true);
            setError('');
            
            console.log('Enviando datos:', formData);
            
            // Enviar datos al backend
            const response = await registrarSensor(formData);
            console.log('Respuesta del servidor:', response);
            
            setSuccess(true);
            
            // Redirigir después de un breve retraso - volvemos a la lista de sensores del invernadero actual
            setTimeout(() => {
                navigate(`/sensores/${formData.idInvernadero}`);
            }, 2000);
            
        } catch (error) {
            console.error('Error al registrar sensor:', error);
            setError('Ocurrió un error al registrar el sensor. Por favor, intente nuevamente.');
        } finally {
            setLoading(false);
        }
    };

    // Función para volver a la lista de sensores
    const volverASensores = () => {
        if (invernadero && invernadero.id) {
            navigate(`/sensores/${invernadero.id}`);
        } else {
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
                                <h1 className="text-2xl font-bold text-gray-800">Agregar Sensor</h1>
                                {invernadero && (
                                    <p className="text-sm text-green-600">Invernadero: {invernadero.name}</p>
                                )}
                            </div>
                        </div>
                        <button 
                            onClick={volverASensores}
                            className="px-4 py-2 bg-green-100 rounded-md text-green-700 hover:bg-green-200 transition-colors duration-300 flex items-center shadow-sm">
                            <span className="mr-1">←</span> Volver a Sensores
                        </button>
                    </div>

                    {/* Mensaje de éxito */}
                    {success && (
                        <div className="bg-green-100 text-green-700 p-4 rounded-md mb-6">
                            <p className="font-bold">¡Éxito!</p>
                            <p>El sensor se ha registrado correctamente. Redirigiendo...</p>
                        </div>
                    )}

                    {/* Mensaje de error general */}
                    {error && (
                        <div className="bg-red-100 text-red-700 p-4 rounded-md mb-6">
                            <p className="font-bold">Error</p>
                            <p>{error}</p>
                        </div>
                    )}

                    {/* Formulario */}
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
                                        placeholder="Ej: SEN-TEMP-01"
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
                                        placeholder="Ej: 00:1A:2B:3C:4D:5E"
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
                                        placeholder="Ej: AgroSense"
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
                                        placeholder="Ej: AS-200"
                                    />
                                    {validationErrors.modelo && (
                                        <p className="text-red-500 text-xs mt-1">{validationErrors.modelo}</p>
                                    )}
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-1" htmlFor="tipoSensor">
                                        Tipo de Sensor *
                                    </label>
                                    <select
                                        id="tipoSensor"
                                        name="tipoSensor"
                                        value={formData.tipoSensor}
                                        onChange={handleTipoChange}
                                        required
                                        className={`w-full border ${validationErrors.tipoSensor ? 'border-red-500' : 'border-gray-300'} rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500`}
                                    >
                                        <option value="">Seleccionar tipo</option>
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
                                        Unidad de Medida *
                                    </label>
                                    <select
                                        id="unidad"
                                        name="unidad"
                                        value={formData.unidad}
                                        onChange={handleChange}
                                        required
                                        disabled={!formData.tipoSensor}
                                        className={`w-full border ${validationErrors.unidad ? 'border-red-500' : 'border-gray-300'} rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500 disabled:bg-gray-100 disabled:text-gray-500`}
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

                        {/* Ubicación dentro del invernadero */}
                        <div className="bg-gray-50 p-4 rounded-md border border-gray-200">
                            <h2 className="text-lg font-semibold text-gray-700 mb-3">Ubicación en el Invernadero</h2>
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
                                        disabled={!invernadero || sectores.length === 0}
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
                                    {sectores.length === 0 && invernadero && (
                                        <p className="text-xs text-orange-500 mt-1">
                                            Este invernadero no tiene sectores definidos
                                        </p>
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
                                        disabled={!invernadero || filas.length === 0}
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
                                    {filas.length === 0 && invernadero && (
                                        <p className="text-xs text-orange-500 mt-1">
                                            Este invernadero no tiene filas definidas
                                        </p>
                                    )}
                                </div>
                            </div>
                        </div>

                        {/* Estado */}
                        <div className="bg-gray-50 p-4 rounded-md border border-gray-200">
                            <h2 className="text-lg font-semibold text-gray-700 mb-3">Estado</h2>
                            <div className="flex items-center">
                                <input
                                    type="checkbox"
                                    id="estado"
                                    name="estado"
                                    checked={formData.estado}
                                    onChange={handleChange}
                                    className="h-4 w-4 text-green-600 focus:ring-green-500 border-gray-300 rounded"
                                />
                                <label htmlFor="estado" className="ml-2 block text-sm text-gray-700">
                                    Activo
                                </label>
                            </div>
                            <p className="text-xs text-gray-500 mt-1">
                                Un sensor inactivo no procesará lecturas en el sistema
                            </p>
                        </div>

                        {/* Botones de acción */}
                        <div className="flex justify-end space-x-4 pt-4">
                            <button
                                type="button"
                                onClick={volverASensores}
                                className="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 transition-colors duration-300"
                            >
                                Cancelar
                            </button>
                            <button
                                type="submit"
                                disabled={loading || success}
                                className={`px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600 transition-colors duration-300 flex items-center ${(loading || success) ? 'opacity-70 cursor-not-allowed' : ''}`}
                            >
                                {loading ? (
                                    <>
                                        <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                        </svg>
                                        Guardando...
                                    </>
                                ) : success ? 'Guardado' : 'Guardar Sensor'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </>
    );
}

export default AgregarSensor;

