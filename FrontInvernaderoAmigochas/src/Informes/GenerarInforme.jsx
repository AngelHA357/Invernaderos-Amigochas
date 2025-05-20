import React, { useState, useEffect } from 'react';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';
import { useNavigate } from 'react-router-dom';

function GenerarInforme() {
    const navigate = useNavigate();

    // Estados para el formulario
    const [selectedInvernaderos, setSelectedInvernaderos] = useState([]);
    const [selectedMagnitudes, setSelectedMagnitudes] = useState([]);
    const [startDate, setStartDate] = useState('2025-05-10'); // Valor por defecto
    const [endDate, setEndDate] = useState('2025-05-25');     // Valor por defecto
    const [formData, setFormData] = useState(null);
    const [isSubmitted, setIsSubmitted] = useState(false);
    const [lecturas, setLecturas] = useState([]);
    const [loading, setLoading] = useState(false); // Para la carga del informe principal
    const [error, setError] = useState(null); // Para errores generales del formulario o del informe
    const [listaInvernaderos, setListaInvernaderos] = useState([]);
    const [loadingInvernaderos, setLoadingInvernaderos] = useState(false);
    const [errorInvernaderos, setErrorInvernaderos] = useState(null);
    const [listaMagnitudes, setListaMagnitudes] = useState([]);
    const [loadingMagnitudes, setLoadingMagnitudes] = useState(false);
    const [errorMagnitudes, setErrorMagnitudes] = useState(null);

    // Helper function to get auth headers
    const getAuthHeaders = () => {
        const authToken = localStorage.getItem('authToken');
        const headers = {
            'Content-Type': 'application/json',
        };
        if (authToken) {
            headers['Authorization'] = `Bearer ${authToken}`;
        }
        return headers;
    };

    // Efecto para cargar opciones de filtros (invernaderos y magnitudes)
    useEffect(() => {
        const cargarOpciones = async () => {
            setLoadingInvernaderos(true);
            setLoadingMagnitudes(true);
            setErrorInvernaderos(null);
            setErrorMagnitudes(null);
            setListaInvernaderos([]);
            // No limpiar listaMagnitudes aquí para mantener simulación si es necesario en el otro fetch
            // setListaMagnitudes([]); 

            const gatewayUrl = 'http://localhost:8080'; // O tu URL del API Gateway
            const authHeaders = getAuthHeaders();

            // Función para manejar errores comunes y el 401
            const handleAuthError = (errorMsgPrefix, status) => {
                if (status === 401) {
                    console.error(`Error 401: Sesión expirada o no autorizado. ${errorMsgPrefix}`);
                    localStorage.removeItem('authToken');
                    localStorage.removeItem('userRole');
                    localStorage.removeItem('username');
                    // navigate('/login'); // Opcional: redirigir al login
                    return 'Sesión expirada. Por favor, inicie sesión nuevamente.';
                }
                return `${errorMsgPrefix} (Código: ${status})`;
            };

            try {
                const [invernaderosResponse, magnitudesResponse] = await Promise.all([
                    // AQUÍ EL CAMBIO: Obtener invernaderos desde el microservicio de Informes
                    fetch(`${gatewayUrl}/api/v1/informes/invernaderos-desde-lecturas`, { headers: authHeaders }), 
                    // Endpoint para magnitudes (asumiendo que ya está o usará fallback)
                    fetch(`${gatewayUrl}/api/v1/gestionSensores/magnitudes-disponibles-informes`, { headers: authHeaders })
                ]);

                // Manejo de respuesta de Invernaderos (desde el nuevo endpoint de Informes)
                if (!invernaderosResponse.ok) {
                    const errorBody = await invernaderosResponse.clone().text().catch(() => 'Cuerpo del error no legible');
                    const errorMsg = handleAuthError(`Error al cargar invernaderos desde Informes. Respuesta: ${errorBody}`, invernaderosResponse.status);
                    setErrorInvernaderos(errorMsg);
                } else {
                    const invernaderosData = await invernaderosResponse.json();
                    console.log("Respuesta de /invernaderos-desde-lecturas:", JSON.stringify(invernaderosData, null, 2)); // <--- AÑADE ESTO
                    setListaInvernaderos(invernaderosData || []);
                    setErrorInvernaderos(null);
                }

                // Manejo de respuesta de Magnitudes (sin cambios respecto a la versión anterior que te di)
                if (!magnitudesResponse.ok) {
                    if (magnitudesResponse.status === 401) {
                        const errorMsg = handleAuthError('Error al cargar magnitudes', magnitudesResponse.status);
                        setErrorMagnitudes(errorMsg);
                        setListaMagnitudes([]); 
                    } else { 
                        const errorBody = await magnitudesResponse.clone().text().catch(() => 'Cuerpo del error no legible');
                        console.warn(`Error ${magnitudesResponse.status} al cargar magnitudes desde '/magnitudes-disponibles-informes'. Respuesta: ${errorBody}`);
                        console.warn("Usando datos de respaldo para magnitudes: ['Temperatura', 'Humedad']");
                        setListaMagnitudes(["Temperatura", "Humedad"]); 
                        setErrorMagnitudes(`Error ${magnitudesResponse.status}. Usando datos de respaldo.`);
                    }
                } else {
                    const magnitudesData = await magnitudesResponse.json(); 
                    if (Array.isArray(magnitudesData)) {
                        setListaMagnitudes(magnitudesData);
                        setErrorMagnitudes(null); 
                    } else {
                        console.warn("Respuesta OK de '/magnitudes-disponibles-informes' pero no es un array. Respuesta:", magnitudesData);
                        console.warn("Usando datos de respaldo para magnitudes: ['Temperatura', 'Humedad']");
                        setListaMagnitudes(["Temperatura", "Humedad"]); 
                        setErrorMagnitudes("Formato de magnitudes inesperado. Usando datos de respaldo.");
                    }
                }

            } catch (generalError) { 
                console.error("Error general en cargarOpciones:", generalError.message);
                const generalErrorMessage = generalError.message || 'Error de red o al procesar la solicitud.';
                if (!errorInvernaderos && listaInvernaderos.length === 0) setErrorInvernaderos(generalErrorMessage);
                if (!errorMagnitudes && listaMagnitudes.length === 0) setErrorMagnitudes(generalErrorMessage);
            } finally {
                setLoadingInvernaderos(false);
                setLoadingMagnitudes(false);
            }
        };

        cargarOpciones();
    }, [navigate]); // navigate como dependencia

    const fetchInformeData = async () => {
        // Validaciones que antes estaban en handleSubmit
        if (selectedInvernaderos.length === 0 || selectedMagnitudes.length === 0) {
            setError('Por favor, seleccione al menos un invernadero y una magnitud.');
            setLecturas([]);
            setIsSubmitted(false);
            return;
        }
        if (new Date(startDate) > new Date(endDate)) {
            setError('La fecha de inicio no puede ser posterior a la fecha de fin.');
            setLecturas([]);
            setIsSubmitted(false);
            return;
        }

        // Actualizar formData ANTES de la llamada para que la UI lo refleje
        const currentFormData = {
            invernaderos: [...selectedInvernaderos],
            magnitudes: selectedMagnitudes.map(name => ({ name })), // Convertir a array de objetos para consistencia en UI
            startDate: startDate,
            endDate: endDate
        };
        setFormData(currentFormData);

        setLoading(true);
        setError(null); // Limpiar error general del formulario antes de nuevo intento
        setLecturas([]);

        const handleAuthErrorInforme = (errorMsgPrefix, status) => {
            if (status === 401) {
                console.error(`Error 401: Sesión expirada o no autorizado para informes. ${errorMsgPrefix}`);
                localStorage.removeItem('authToken');
                localStorage.removeItem('userRole');
                localStorage.removeItem('username');
                // navigate('/login'); // Opcional
                return 'Sesión expirada. Por favor, inicie sesión nuevamente.';
            }
            return `${errorMsgPrefix} (Código: ${status})`;
        };

        try {
            const formattedStartDate = `${startDate}T00:00:00Z`;
            const formattedEndDate = `${endDate}T23:59:59Z`;

            const invernaderoIds = selectedInvernaderos.map(inv => inv.id);
            const magnitudNames = selectedMagnitudes; // Es un array de strings

            const params = new URLSearchParams();
            params.append('fechaInicio', formattedStartDate);
            params.append('fechaFin', formattedEndDate);
            invernaderoIds.forEach(id => params.append('idsInvernadero', id));
            magnitudNames.forEach(name => params.append('magnitudes', name));
            const queryString = params.toString();

            const gatewayUrl = 'http://localhost:8080';
            const endpointPath = '/api/v1/informes/filtradas';
            const authHeaders = getAuthHeaders();
            const url = `${gatewayUrl}${endpointPath}?${queryString}`;

            console.log("Llamando a API Informes:", url);
            const response = await fetch(url, { headers: authHeaders });

            if (!response.ok) {
                let errorMessageText = `Error: ${response.status} ${response.statusText}`;
                if (response.status === 401) {
                    errorMessageText = handleAuthErrorInforme('Error al obtener informe', response.status);
                } else {
                    try {
                        const errorData = await response.json();
                        errorMessageText = errorData.mensaje || `Error ${response.status}: ${errorData.error || response.statusText}`;
                    } catch (e) {
                        // No hacer nada si no hay JSON en el error, usar el statusText
                    }
                }
                throw new Error(errorMessageText);
            }

            const data = await response.json();
            if (data && Array.isArray(data.lecturasEnriquecidas)) {
            setLecturas(data.lecturasEnriquecidas); 
        } else {
            // Si data no existe o data.lecturasEnriquecidas no es un array (inesperado si la API funciona bien)
            console.warn("El campo 'lecturasEnriquecidas' no es un array o no existe en la respuesta:", data);
            setLecturas([]); // Establecer como array vacío para evitar errores de .map
        }
            setIsSubmitted(true);

        } catch (err) {
            console.error('Error al obtener informe:', err);
            setError(err.message || 'Ocurrió un error al cargar el informe.');
            setLecturas([]);
            setIsSubmitted(false);
        } finally {
            setLoading(false);
        }
    };

    const handleInvernaderoChange = (e) => {
    const selectedId = e.target.value;
    console.log("[handleInvernaderoChange] Valor seleccionado del <select> (selectedId):", selectedId); // Log 1

    if (!selectedId) {
        console.log("[handleInvernaderoChange] selectedId está vacío, retornando."); // Log 2
        return;
    }

        // Imprime la lista completa de invernaderos para comparar
        console.log("[handleInvernaderoChange] listaInvernaderos actual:", JSON.stringify(listaInvernaderos, null, 2)); // Log 3

        const invernadero = listaInvernaderos.find((inv) => {
            // Log para cada item en la búsqueda
            // console.log(`[handleInvernaderoChange] Comparando inv.id: '${inv.id}' (tipo: <span class="math-inline">\{typeof inv\.id\}\) con selectedId\: '</span>{selectedId}' (tipo: ${typeof selectedId})`);
            return inv.id === selectedId;
        });

        console.log("[handleInvernaderoChange] Invernadero encontrado con .find():", invernadero); // Log 4

        if (invernadero) {
            console.log("[handleInvernaderoChange] Invernadero encontrado. Verificando si ya está seleccionado..."); // Log 5
            const yaSeleccionado = selectedInvernaderos.some((inv) => inv.id === selectedId);
            console.log("[handleInvernaderoChange] ¿Ya seleccionado?:", yaSeleccionado); // Log 6

            if (!yaSeleccionado) {
                console.log("[handleInvernaderoChange] Agregando invernadero:", invernadero); // Log 7
                setSelectedInvernaderos([...selectedInvernaderos, invernadero]);
            } else {
                console.log("[handleInvernaderoChange] El invernadero ya estaba en selectedInvernaderos."); // Log 8
            }
        } else {
            console.warn("[handleInvernaderoChange] No se encontró el invernadero en listaInvernaderos con el ID:", selectedId); // Log 9
        }

        // e.target.value = ""; // Comenta esto temporalmente para ver si el valor persiste en el select
    };

    const removeInvernadero = (idToRemove) => {
        setSelectedInvernaderos(selectedInvernaderos.filter((inv) => inv.id !== idToRemove));
    };

    const handleMagnitudChange = (e) => {
        const selectedName = e.target.value;
        if (!selectedName) return;
        if (!selectedMagnitudes.includes(selectedName)) {
            setSelectedMagnitudes([...selectedMagnitudes, selectedName]);
        }
        e.target.value = ""; // Resetear el select
    };

    const removeMagnitud = (nameToRemove) => {
        setSelectedMagnitudes(selectedMagnitudes.filter((magName) => magName !== nameToRemove));
    };

    const handleStartDateChange = (e) => {
        setStartDate(e.target.value);
    };

    const handleEndDateChange = (e) => {
        setEndDate(e.target.value);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        // Las validaciones principales ahora están en fetchInformeData
        console.log("--- Iniciando Generación de Informe ---");
        fetchInformeData();
    };

    const exportToCSV = () => {
        if (!lecturas || lecturas.length === 0) {
            alert('No hay datos para exportar');
            return;
        }
        let headers = ['ID Sensor', 'MAC Address', 'Marca', 'Modelo', 'Magnitud', 'Unidad', 'Valor', 'Fecha/Hora', 'Invernadero', 'Sector', 'Fila'];
        let csvContent = headers.join(',') + '\n';
        lecturas.forEach(lectura => {
            const fechaFormateada = new Date(lectura.fechaHora).toLocaleString();
            const row = [
                lectura.idSensor || '',
                lectura.macAddress || '',
                lectura.marca || '',
                lectura.modelo || '',
                lectura.magnitud || '',
                lectura.unidad || '',
                lectura.valor || '',
                fechaFormateada,
                lectura.nombreInvernadero || '',
                lectura.sector || '',
                lectura.fila || ''
            ].map(value => `"${String(value).replace(/"/g, '""')}"`).join(','); // Escapar comillas dobles en valores
            csvContent += row + '\n';
        });
        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.setAttribute('href', url);
        link.setAttribute('download', `informe_lecturas_${new Date().toISOString().slice(0, 10)}.csv`);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

    return (
        <>
            <BarraNavegacion />
            <div className="min-h-screen bg-gradient-to-b from-green-50 to-white p-6">
                <div className="max-w-7xl mx-auto px-4">
                    <div className="flex items-center mb-6 pt-4">
                        <div className="bg-green-100 p-3 rounded-full mr-4">
                            <span className="text-2xl" role="img" aria-label="informe">📊</span>
                        </div>
                        <div>
                            <h1 className="text-2xl font-bold text-gray-800">Informes de Invernaderos</h1>
                            <p className="text-sm text-green-600">Analiza y exporta datos de tus cultivos</p>
                        </div>
                    </div>

                    <div className="grid grid-cols-3 gap-6">
                        <div className="col-span-1 bg-white p-6 rounded-lg shadow-lg border border-green-200">
                            <div className="flex items-center mb-4 pb-2 border-b border-green-100">
                                <span className="text-green-500 mr-2">⚙️</span>
                                <h2 className="text-xl font-semibold text-gray-800">Configurar Informe</h2>
                            </div>
                            <p className="text-gray-600 mb-5">Establece los parámetros para tu análisis</p>

                            <form onSubmit={handleSubmit}>
                                <div className="mb-6 bg-green-50 p-4 rounded-lg border border-green-100">
                                    <h3 className="text-sm font-semibold text-green-700 mb-3 flex items-center">
                                        <span className="mr-1">📅</span> Rango de fechas
                                    </h3>
                                    <div className="flex space-x-4">
                                        <div className="flex flex-col">
                                            <label className="text-gray-700 text-sm font-medium">Fecha de inicio:</label>
                                            <input type="date" className="border border-green-200 rounded-md p-2 mt-1 focus:outline-none focus:ring-2 focus:ring-green-500" value={startDate} onChange={handleStartDateChange} required />
                                        </div>
                                        <div className="flex flex-col">
                                            <label className="text-gray-700 text-sm font-medium">Fecha de fin:</label>
                                            <input type="date" className="border border-green-200 rounded-md p-2 mt-1 focus:outline-none focus:ring-2 focus:ring-green-500" value={endDate} onChange={handleEndDateChange} required />
                                        </div>
                                    </div>
                                </div>

                                <div className="mb-6 bg-green-50 p-4 rounded-lg border border-green-100">
                                    <h3 className="text-sm font-semibold text-green-700 mb-3 flex items-center">
                                        <span className="mr-1">📏</span> Magnitud
                                    </h3>
                                    {errorMagnitudes && !loadingMagnitudes && <p className="text-red-500 text-xs mb-2">{errorMagnitudes}</p>}
                                    <select
                                        className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500 bg-white"
                                        onChange={handleMagnitudChange} value="" disabled={loadingMagnitudes || (listaMagnitudes.length === 0 && !errorMagnitudes && !loadingMagnitudes)}>
                                        <option value="">
                                            {selectedMagnitudes.length > 0 ? 'Añadir otra magnitud' : (loadingMagnitudes ? 'Cargando...' : ((!errorMagnitudes && listaMagnitudes.length === 0) ? 'No hay magnitudes disponibles' : 'Seleccionar'))}
                                        </option>
                                        {!loadingMagnitudes && !errorMagnitudes && listaMagnitudes.map((nombreMagnitud) => (
                                            <option key={nombreMagnitud} value={nombreMagnitud}>{nombreMagnitud}</option>
                                        ))}
                                    </select>
                                    <div className="mt-3 flex flex-wrap gap-2">
                                        {selectedMagnitudes.map((magName) => {
                                            const colors = { 'Humedad': 'bg-blue-100 text-blue-700', 'Temperatura': 'bg-red-100 text-red-700', 'CO2': 'bg-gray-100 text-gray-700' };
                                            const colorClass = colors[magName] || 'bg-green-100 text-green-700';
                                            return (
                                                <span key={magName} className={`${colorClass} px-3 py-1 rounded-full flex items-center`}>
                                                    {magName}
                                                    <button type="button" className="ml-2 text-gray-500 hover:text-gray-700" onClick={() => removeMagnitud(magName)}>✕</button>
                                                </span>
                                            );
                                        })}
                                    </div>
                                    {selectedMagnitudes.length === 0 && <p className="text-red-500 text-xs mt-1">Debe seleccionar al menos una magnitud</p> }
                                    <input type="hidden" name="magnitudes" value={JSON.stringify(selectedMagnitudes)} />
                                </div>

                                <div className="mb-6 bg-green-50 p-4 rounded-lg border border-green-100">
                                    <h3 className="text-sm font-semibold text-green-700 mb-3 flex items-center">
                                        <span className="mr-1">🌱</span> Invernadero
                                    </h3>
                                    {errorInvernaderos && !loadingInvernaderos && <p className="text-red-500 text-xs mb-2">{errorInvernaderos}</p>}
                                    <select
                                        className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500 bg-white"
                                        onChange={handleInvernaderoChange} value="" disabled={loadingInvernaderos || (listaInvernaderos.length === 0 && !errorInvernaderos && !loadingInvernaderos)}>
                                        <option value="">
                                            {selectedInvernaderos.length > 0 ? 'Añadir otro invernadero' : (loadingInvernaderos ? 'Cargando...' : ((!errorInvernaderos && listaInvernaderos.length === 0) ? 'No hay invernaderos disponibles' : 'Seleccionar'))}
                                        </option>
                                        {!loadingInvernaderos && !errorInvernaderos && listaInvernaderos
                                            .filter(inv => inv && inv.id && inv.nombre && inv.nombre.trim() !== "") // Filtro robusto
                                            .map((invernadero) => (
                                                <option key={invernadero.id} value={invernadero.id}>
                                                    {invernadero.nombre}
                                                </option>
                                            ))}
                                    </select>
                                    <div className="mt-3 flex flex-wrap gap-2">
                                        {selectedInvernaderos.map((invernadero) => (
                                            <span key={invernadero.id} className="bg-green-100 text-green-700 px-3 py-1 rounded-full flex items-center">
                                                {invernadero.nombre} {/* Usar .nombre */}
                                                <button type="button" className="ml-2 text-gray-500 hover:text-gray-700" onClick={() => removeInvernadero(invernadero.id)}>✕</button>
                                            </span>
                                        ))}
                                    </div>
                                    {selectedInvernaderos.length === 0 && <p className="text-red-500 text-xs mt-1">Debe seleccionar al menos un invernadero</p>}
                                    <input type="hidden" name="invernaderos" value={JSON.stringify(selectedInvernaderos)} />
                                </div>

                                <button type="submit"
                                    className="w-full px-4 py-2 bg-green-600 text-white rounded-full hover:bg-green-700 transition-colors duration-300 shadow-sm flex items-center justify-center"
                                    disabled={loading || loadingInvernaderos || loadingMagnitudes || selectedMagnitudes.length === 0 || selectedInvernaderos.length === 0}>
                                    <span className="mr-2">📊</span> Generar Informe
                                </button>
                                {error && <p className="text-red-500 text-sm mt-2 text-center">{error}</p>}
                            </form>
                        </div>

                        <div className="col-span-2 bg-white p-6 rounded-lg shadow-lg border border-green-200">
                            <div className="flex justify-between items-center mb-4 pb-2 border-b border-green-100">
                                <div className="flex items-center">
                                    <span className="text-green-500 mr-2">📈</span>
                                    <h2 className="text-xl font-semibold text-gray-800">Visualización de Datos</h2>
                                </div>
                                <button type="button"
                                    className={`flex items-center px-4 py-2 rounded-md ${!isSubmitted || lecturas.length === 0 ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'bg-green-100 text-green-700 hover:bg-green-200'} transition-colors duration-300`}
                                    disabled={!isSubmitted || lecturas.length === 0} onClick={exportToCSV}>
                                    <span className="mr-1">Exportar</span>
                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 16v2a2 2 0 002 2h12a2 2 0 002-2v-2M16 12l-4 4m0 0l-4-4m4 4V4"></path>
                                    </svg>
                                </button>
                            </div>
                            <p className="text-gray-600 mb-4">
                                {isSubmitted && formData
                                    ? `Datos de ${formData.magnitudes.map(m => m.name).join(' y ')} del ${new Date(formData.startDate).toLocaleDateString()} al ${new Date(formData.endDate).toLocaleDateString()}`
                                    : 'Configura los parámetros y genera un informe para visualizar los datos'
                                }
                            </p>

                            {isSubmitted ? (
                                <>
                                    <div className="flex space-x-2 mb-4">
                                        {formData?.magnitudes.map(magnitud => { // magnitud es {name: 'string'}
                                            const colors = { 'Humedad': 'bg-blue-100 text-blue-700 hover:bg-blue-200', 'Temperatura': 'bg-red-100 text-red-700 hover:bg-red-200', 'CO2': 'bg-gray-100 text-gray-700 hover:bg-gray-200' };
                                            const colorClass = colors[magnitud.name] || 'bg-green-100 text-green-700 hover:bg-green-200';
                                            return (
                                                <button key={magnitud.name} type="button" className={`px-4 py-2 ${colorClass} rounded-full transition-colors duration-300`}>
                                                    {magnitud.name}
                                                </button>
                                            );
                                        })}
                                    </div>

                                    {loading ? (
                                        <div className="flex justify-center items-center h-80"><div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-500"></div></div>
                                    ) : error && !lecturas.length ? ( // Mostrar error solo si no hay lecturas, priorizar mostrar tabla aunque haya error previo no fatal
                                        <div className="p-4 bg-red-50 border border-red-100 rounded-lg text-red-500">
                                            <p className="font-medium">Error al cargar datos</p>
                                            <p>{error}</p>
                                        </div>
                                    ) : (
                                        <>
                                            <div className="overflow-x-auto mb-4 max-h-96"> {/* Ajusta max-h-96 (24rem o 384px) según necesites */}
                                                <table className="min-w-full divide-y divide-green-200">
                                                    <thead className="bg-green-50 sticky top-0 z-10"> {/* sticky top-0 para que el header se quede fijo al hacer scroll vertical */}
                                                        <tr>
                                                            <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-green-700 uppercase tracking-wider">ID Sensor</th>
                                                            <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-green-700 uppercase tracking-wider">MAC Address</th>
                                                            <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-green-700 uppercase tracking-wider">Marca</th>
                                                            <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-green-700 uppercase tracking-wider">Modelo</th>
                                                            <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-green-700 uppercase tracking-wider">Magnitud</th>
                                                            <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-green-700 uppercase tracking-wider">Unidad</th>
                                                            <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-green-700 uppercase tracking-wider">Valor</th>
                                                            <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-green-700 uppercase tracking-wider">Fecha/Hora</th>
                                                            <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-green-700 uppercase tracking-wider">Invernadero</th>
                                                            <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-green-700 uppercase tracking-wider">Sector</th>
                                                            <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-green-700 uppercase tracking-wider">Fila</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody className="bg-white divide-y divide-green-100">
                                                        {lecturas.length > 0 ? (
                                                            lecturas.map((lectura, index) => (
                                                                <tr key={index} className={index % 2 === 0 ? 'bg-white' : 'bg-green-50'}>
                                                                    <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-700">{lectura.idSensor}</td>
                                                                    <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-700">{lectura.macAddress}</td>
                                                                    <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-700">{lectura.marca}</td>
                                                                    <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-700">{lectura.modelo}</td>
                                                                    <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-700">{lectura.magnitud}</td>
                                                                    <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-700">{lectura.unidad}</td>
                                                                    <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-700">{lectura.valor}</td>
                                                                    <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-700">
                                                                        {new Date(lectura.fechaHora).toLocaleDateString()} {new Date(lectura.fechaHora).toLocaleTimeString()}
                                                                    </td>
                                                                    <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-700">{lectura.nombreInvernadero}</td>
                                                                    <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-700">{lectura.sector}</td>
                                                                    <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-700">{lectura.fila}</td>
                                                                </tr>
                                                            ))
                                                        ) : (
                                                            <tr><td colSpan="11" className="px-4 py-8 text-center text-gray-500">No se encontraron lecturas para los parámetros seleccionados</td></tr>
                                                        )}
                                                    </tbody>
                                                </table>
                                            </div>
                                            <div className="w-full max-w-md h-24 bg-white rounded-lg shadow-inner overflow-hidden flex items-end p-2 mt-2">
                                                {/* Placeholder de gráfico */}
                                                <div className="h-1/2 w-4 bg-green-200 mx-1 rounded-t-sm"></div> <div className="h-3/5 w-4 bg-green-300 mx-1 rounded-t-sm"></div> <div className="h-2/5 w-4 bg-green-200 mx-1 rounded-t-sm"></div> <div className="h-7/10 w-4 bg-green-400 mx-1 rounded-t-sm"></div> <div className="h-4/5 w-4 bg-green-500 mx-1 rounded-t-sm"></div> <div className="h-13/20 w-4 bg-green-400 mx-1 rounded-t-sm"></div> <div className="h-11/20 w-4 bg-green-300 mx-1 rounded-t-sm"></div> <div className="h-9/20 w-4 bg-green-200 mx-1 rounded-t-sm"></div> <div className="h-7/20 w-4 bg-green-200 mx-1 rounded-t-sm"></div> <div className="h-1/4 w-4 bg-green-100 mx-1 rounded-t-sm"></div>
                                            </div>
                                        </>
                                    )}
                                    <div className="flex space-x-4 mt-6 p-3 bg-white border border-green-100 rounded-lg shadow-sm">
                                        <div className="text-sm text-gray-700 mr-2 font-medium">Leyenda:</div>
                                        {formData?.invernaderos.map((invernadero, index) => {
                                            const colors = ['bg-green-500', 'bg-blue-500', 'bg-yellow-500', 'bg-red-500', 'bg-purple-500'];
                                            const colorClass = colors[index % colors.length];
                                            return (
                                                <div key={invernadero.id} className="flex items-center">
                                                    <span className={`w-3 h-3 ${colorClass} rounded-full mr-2`}></span>
                                                    <span className="text-gray-700 text-sm">{invernadero.nombre}</span> {/* Usar .nombre */}
                                                </div>
                                            );
                                        })}
                                    </div>
                                </>
                            ) : (
                                <div className="h-80 bg-green-50 border border-green-100 flex flex-col items-center justify-center rounded-lg">
                                    <div className="text-6xl mb-4 text-green-200">📊</div>
                                    <p className="text-gray-700 font-medium mb-2">No hay datos para mostrar</p>
                                    <p className="text-green-600 mb-6">Configura los parámetros y genera un informe</p>
                                    <div className="flex flex-col items-center text-gray-500 text-sm max-w-md text-center">
                                        <div className="flex items-center mb-2"><span className="w-6 h-6 bg-green-100 rounded-full flex items-center justify-center mr-2 text-green-500">1</span><span>Selecciona rango de fechas</span></div>
                                        <div className="flex items-center mb-2"><span className="w-6 h-6 bg-green-100 rounded-full flex items-center justify-center mr-2 text-green-500">2</span><span>Elige las magnitudes a analizar</span></div>
                                        <div className="flex items-center"><span className="w-6 h-6 bg-green-100 rounded-full flex items-center justify-center mr-2 text-green-500">3</span><span>Selecciona los invernaderos</span></div>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>

                    <div className="mt-4 text-center text-xs text-green-600">
                        <p>Sistema de Informes Amigochas — Última actualización: {new Date().toLocaleDateString()}</p>
                    </div>
                </div>
            </div>
        </>
    );
}

export default GenerarInforme;