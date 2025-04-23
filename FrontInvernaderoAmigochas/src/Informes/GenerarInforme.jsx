import React, { useState, useEffect } from 'react';
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

    // Estados para el formulario
    const [selectedInvernaderos, setSelectedInvernaderos] = useState([]);
    const [selectedMagnitudes, setSelectedMagnitudes] = useState([]);
    const [startDate, setStartDate] = useState('2023-03-02');
    const [endDate, setEndDate] = useState('2023-03-08');
    const [formData, setFormData] = useState(null);
    const [isSubmitted, setIsSubmitted] = useState(false);
    const [lecturas, setLecturas] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Efecto para cargar las lecturas cuando se envía el formulario
    useEffect(() => {
        if (isSubmitted) {
            fetchLecturas();
        }
    }, [isSubmitted]);

    // Función para obtener las lecturas del sistema
    const fetchLecturas = async () => {
        setLoading(true);
        try {
            const response = await fetch('http://localhost:8080/api/v1/lecturas');
            if (!response.ok) {
                throw new Error('Error al obtener las lecturas');
            }
            const data = await response.json();
            setLecturas(data);
        } catch (err) {
            setError(err.message);
            console.error('Error:', err);
        } finally {
            setLoading(false);
        }
    };

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

    const handleStartDateChange = (e) => {
        setStartDate(e.target.value);
    };

    const handleEndDateChange = (e) => {
        setEndDate(e.target.value);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        
        const data = {
            fechaInicio: startDate,
            fechaFin: endDate,
            magnitudes: selectedMagnitudes,
            invernaderos: selectedInvernaderos
        };
        
        setFormData(data);
        setIsSubmitted(true);
        console.log('Datos del informe:', data);
        
        alert('Informe generado correctamente');
    };

    // Función para exportar datos a CSV
    const exportToCSV = () => {
        if (!lecturas || lecturas.length === 0) {
            alert('No hay datos para exportar');
            return;
        }

        // Crear las cabeceras del CSV
        let headers = ['ID Sensor', 'MAC Address', 'Marca', 'Modelo', 'Magnitud', 'Unidad', 'Valor', 'Fecha/Hora', 'Invernadero', 'Sector', 'Fila'];
        
        // Crear las filas de datos
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
                lectura.invernadero || '',
                lectura.sector || '',
                lectura.fila || ''
            ].map(value => `"${value}"`).join(',');
            
            csvContent += row + '\n';
        });
        
        // Crear un objeto Blob para el archivo CSV
        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const url = URL.createObjectURL(blob);
        
        // Crear un enlace para descargar el archivo
        const link = document.createElement('a');
        link.setAttribute('href', url);
        link.setAttribute('download', `informe_lecturas_${new Date().toISOString().slice(0, 10)}.csv`);
        link.style.visibility = 'hidden';
        
        // Añadir el enlace al DOM y activar la descarga
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

    return (
        <>
            <BarraNavegacion />
            <div className="min-h-screen bg-gradient-to-b from-green-50 to-white p-6">
                <div className="max-w-7xl mx-auto px-4">
                    {/* Título con icono */}
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
                        {/* Tarjeta de la izquierda */}
                        <div className="col-span-1 bg-white p-6 rounded-lg shadow-lg border border-green-200">
                            <div className="flex items-center mb-4 pb-2 border-b border-green-100">
                                <span className="text-green-500 mr-2">⚙️</span>
                                <h2 className="text-xl font-semibold text-gray-800">Configurar Informe</h2>
                            </div>
                            <p className="text-gray-600 mb-5">Establece los parámetros para tu análisis</p>

                            <form onSubmit={handleSubmit}>
                                {/* Rango de fechas */}
                                <div className="mb-6 bg-green-50 p-4 rounded-lg border border-green-100">
                                    <h3 className="text-sm font-semibold text-green-700 mb-3 flex items-center">
                                        <span className="mr-1">📅</span> Rango de fechas
                                    </h3>
                                    <div className="flex space-x-4">
                                        <div className="flex flex-col">
                                            <label className="text-gray-700 text-sm font-medium">Fecha de inicio:</label>
                                            <input
                                                type="date"
                                                className="border border-green-200 rounded-md p-2 mt-1 focus:outline-none focus:ring-2 focus:ring-green-500"
                                                value={startDate}
                                                onChange={handleStartDateChange}
                                                required
                                            />
                                        </div>
                                        <div className="flex flex-col">
                                            <label className="text-gray-700 text-sm font-medium">Fecha de fin:</label>
                                            <input
                                                type="date"
                                                className="border border-green-200 rounded-md p-2 mt-1 focus:outline-none focus:ring-2 focus:ring-green-500"
                                                value={endDate}
                                                onChange={handleEndDateChange}
                                                required
                                            />
                                        </div>
                                    </div>
                                </div>

                                {/* Magnitud */}
                                <div className="mb-6 bg-green-50 p-4 rounded-lg border border-green-100">
                                    <h3 className="text-sm font-semibold text-green-700 mb-3 flex items-center">
                                        <span className="mr-1">📏</span> Magnitud
                                    </h3>
                                    <select
                                        className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500 bg-white"
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
                                        {selectedMagnitudes.map((magnitud) => {
                                            const colors = {
                                                'Humedad': 'bg-blue-100 text-blue-700',
                                                'Temperatura': 'bg-red-100 text-red-700',
                                                'CO2': 'bg-gray-100 text-gray-700'
                                            };
                                            const colorClass = colors[magnitud.name] || 'bg-green-100 text-green-700';
                                            
                                            return (
                                                <span
                                                    key={magnitud.id}
                                                    className={`${colorClass} px-3 py-1 rounded-full flex items-center`}
                                                >
                                                    {magnitud.name}
                                                    <button
                                                        type="button"
                                                        className="ml-2 text-gray-500 hover:text-gray-700"
                                                        onClick={() => removeMagnitud(magnitud.id)}
                                                    >
                                                        ✕
                                                    </button>
                                                </span>
                                            );
                                        })}
                                    </div>
                                    {selectedMagnitudes.length === 0 && (
                                        <p className="text-red-500 text-xs mt-1">Debe seleccionar al menos una magnitud</p>
                                    )}
                                    <input 
                                        type="hidden" 
                                        name="magnitudes" 
                                        value={JSON.stringify(selectedMagnitudes)} 
                                    />
                                </div>

                                {/* Invernadero */}
                                <div className="mb-6 bg-green-50 p-4 rounded-lg border border-green-100">
                                    <h3 className="text-sm font-semibold text-green-700 mb-3 flex items-center">
                                        <span className="mr-1">🌱</span> Invernadero
                                    </h3>
                                    <select
                                        className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500 bg-white"
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
                                                className="bg-green-100 text-green-700 px-3 py-1 rounded-full flex items-center"
                                            >
                                                {invernadero.name}
                                                <button
                                                    type="button"
                                                    className="ml-2 text-gray-500 hover:text-gray-700"
                                                    onClick={() => removeInvernadero(invernadero.id)}
                                                >
                                                    ✕
                                                </button>
                                            </span>
                                        ))}
                                    </div>
                                    {selectedInvernaderos.length === 0 && (
                                        <p className="text-red-500 text-xs mt-1">Debe seleccionar al menos un invernadero</p>
                                    )}
                                    <input 
                                        type="hidden" 
                                        name="invernaderos" 
                                        value={JSON.stringify(selectedInvernaderos)} 
                                    />
                                </div>

                                {/* Botón Generar Informe */}
                                <button 
                                    type="submit" 
                                    className="w-full px-4 py-2 bg-green-600 text-white rounded-full hover:bg-green-700 transition-colors duration-300 shadow-sm flex items-center justify-center"
                                    disabled={selectedMagnitudes.length === 0 || selectedInvernaderos.length === 0}
                                >
                                    <span className="mr-2">📊</span> Generar Informe
                                </button>
                            </form>
                        </div>

                        {/* Tarjeta de la derecha */}
                        <div className="col-span-2 bg-white p-6 rounded-lg shadow-lg border border-green-200">
                            <div className="flex justify-between items-center mb-4 pb-2 border-b border-green-100">
                                <div className="flex items-center">
                                    <span className="text-green-500 mr-2">📈</span>
                                    <h2 className="text-xl font-semibold text-gray-800">
                                        Visualización de Datos
                                    </h2>
                                </div>
                                <button 
                                    type="button"
                                    className={`flex items-center px-4 py-2 rounded-md ${!isSubmitted ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'bg-green-100 text-green-700 hover:bg-green-200'} transition-colors duration-300`}
                                    disabled={!isSubmitted}
                                    onClick={exportToCSV}
                                >
                                    <span className="mr-1">Exportar</span>
                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 16v2a2 2 0 002 2h12a2 2 0 002-2v-2M16 12l-4 4m0 0l-4-4m4 4V4"></path>
                                    </svg>
                                </button>
                            </div>
                            <p className="text-gray-600 mb-4">
                                {isSubmitted 
                                    ? `Datos de ${formData?.magnitudes.map(m => m.name).join(' y ')} del ${new Date(startDate).toLocaleDateString()} al ${new Date(endDate).toLocaleDateString()}`
                                    : 'Configura los parámetros y genera un informe para visualizar los datos'
                                }
                            </p>

                            {isSubmitted ? (
                                <>
                                    <div className="flex space-x-2 mb-4">
                                        {formData?.magnitudes.map(magnitud => {
                                            const colors = {
                                                'Humedad': 'bg-blue-100 text-blue-700 hover:bg-blue-200',
                                                'Temperatura': 'bg-red-100 text-red-700 hover:bg-red-200',
                                                'CO2': 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                                            };
                                            const colorClass = colors[magnitud.name] || 'bg-green-100 text-green-700 hover:bg-green-200';
                                            
                                            return (
                                                <button 
                                                    key={magnitud.id}
                                                    type="button" 
                                                    className={`px-4 py-2 ${colorClass} rounded-full transition-colors duration-300`}
                                                >
                                                    {magnitud.name}
                                                </button>
                                            );
                                        })}
                                    </div>

                                    {loading ? (
                                        <div className="flex justify-center items-center h-80">
                                            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-500"></div>
                                        </div>
                                    ) : error ? (
                                        <div className="p-4 bg-red-50 border border-red-100 rounded-lg text-red-500">
                                            <p className="font-medium">Error al cargar datos</p>
                                            <p>{error}</p>
                                        </div>
                                    ) : (
                                        <>
                                            {/* Tabla de lecturas */}
                                            <div className="overflow-x-auto mb-4">
                                                <table className="min-w-full divide-y divide-green-200">
                                                    <thead className="bg-green-50">
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
                                                                    <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-700">{lectura.invernadero}</td>
                                                                    <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-700">{lectura.sector}</td>
                                                                    <td className="px-4 py-2 whitespace-nowrap text-sm text-gray-700">{lectura.fila}</td>
                                                                </tr>
                                                            ))
                                                        ) : (
                                                            <tr>
                                                                <td colSpan="11" className="px-4 py-8 text-center text-gray-500">
                                                                    No se encontraron lecturas para los parámetros seleccionados
                                                                </td>
                                                            </tr>
                                                        )}
                                                    </tbody>
                                                </table>
                                            </div>

                                            {/* Gráfico simulado */}
                                            <div className="w-full max-w-md h-24 bg-white rounded-lg shadow-inner overflow-hidden flex items-end p-2 mt-2">
                                                <div className="h-50% w-4 bg-green-200 mx-1 rounded-t-sm"></div>
                                                <div className="h-60% w-4 bg-green-300 mx-1 rounded-t-sm"></div>
                                                <div className="h-40% w-4 bg-green-200 mx-1 rounded-t-sm"></div>
                                                <div className="h-70% w-4 bg-green-400 mx-1 rounded-t-sm"></div>
                                                <div className="h-85% w-4 bg-green-500 mx-1 rounded-t-sm"></div>
                                                <div className="h-65% w-4 bg-green-400 mx-1 rounded-t-sm"></div>
                                                <div className="h-55% w-4 bg-green-300 mx-1 rounded-t-sm"></div>
                                                <div className="h-45% w-4 bg-green-200 mx-1 rounded-t-sm"></div>
                                                <div className="h-35% w-4 bg-green-200 mx-1 rounded-t-sm"></div>
                                                <div className="h-25% w-4 bg-green-100 mx-1 rounded-t-sm"></div>
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
                                                    <span className="text-gray-700 text-sm">{invernadero.name}</span>
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
                                        <div className="flex items-center mb-2">
                                            <span className="w-6 h-6 bg-green-100 rounded-full flex items-center justify-center mr-2 text-green-500">1</span>
                                            <span>Selecciona rango de fechas</span>
                                        </div>
                                        <div className="flex items-center mb-2">
                                            <span className="w-6 h-6 bg-green-100 rounded-full flex items-center justify-center mr-2 text-green-500">2</span>
                                            <span>Elige las magnitudes a analizar</span>
                                        </div>
                                        <div className="flex items-center">
                                            <span className="w-6 h-6 bg-green-100 rounded-full flex items-center justify-center mr-2 text-green-500">3</span>
                                            <span>Selecciona los invernaderos</span>
                                        </div>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                    
                    {/* Footer con información */}
                    <div className="mt-4 text-center text-xs text-green-600">
                        <p>Sistema de Informes Amigochas — Última actualización: {new Date().toLocaleDateString()}</p>
                    </div>
                </div>  
            </div>
        </>
    );
}

export default GenerarInforme;