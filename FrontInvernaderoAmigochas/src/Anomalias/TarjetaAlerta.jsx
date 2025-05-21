import React from 'react';
import { useNavigate } from 'react-router-dom';

// Props: tipo (para color/icono), tieneReporte, y 'tiempo' es una descripción
function TarjetaAlerta({ id, invernadero, descripcion, tipo, tiempo }) {
    const navigate = useNavigate();

    let colorBorde = 'border-yellow-400'; // Default
    let colorFondoHover = 'hover:bg-yellow-50';
    let colorBoton = 'border-yellow-400 text-yellow-700 hover:bg-yellow-500 hover:text-white focus:ring-yellow-300';
    let icono = '⚠️';

    if (tipo) {
        const tipoLowerCase = tipo.toLowerCase();
        if (tipoLowerCase.includes('temperatura')) {
            colorBorde = 'border-red-500';
            colorFondoHover = 'hover:bg-red-50';
            colorBoton = 'border-red-400 text-red-700 hover:bg-red-500 hover:text-white focus:ring-red-300';
            icono = '🔥';
        } else if (tipoLowerCase.includes('humedad')) {
            colorBorde = 'border-blue-500';
            colorFondoHover = 'hover:bg-blue-50';
            colorBoton = 'border-blue-400 text-blue-700 hover:bg-blue-500 hover:text-white focus:ring-blue-300';
            icono = '💧';
        } else if (tipoLowerCase.includes('co2')) {
            colorBorde = 'border-purple-500';
            colorFondoHover = 'hover:bg-purple-50';
            colorBoton = 'border-purple-400 text-purple-700 hover:bg-purple-500 hover:text-white focus:ring-purple-300';
            icono = '☁️';
        } else if (tipoLowerCase.includes('luz') || tipoLowerCase.includes('luminosidad')) {
            colorBorde = 'border-amber-500';
            colorFondoHover = 'hover:bg-amber-50';
            colorBoton = 'border-amber-400 text-amber-700 hover:bg-amber-500 hover:text-white focus:ring-amber-300';
            icono = '💡';
        } else if (tipoLowerCase.includes('ph')) {
            colorBorde = 'border-green-500';
            colorFondoHover = 'hover:bg-green-50';
            colorBoton = 'border-green-400 text-green-700 hover:bg-green-500 hover:text-white focus:ring-green-300';
            icono = '🧪';
        }
    }

    const handleReporteClick = () => {
        localStorage.setItem('alertaSeleccionadaId', id); 
        navigate(`/anomalias/${id}`);
    };
    
    return (
        <div className={`border-l-4 ${colorBorde} p-4 bg-white ${colorFondoHover} transition-colors duration-200 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3`}>
            <div className="flex items-start flex-grow">
                <span className="text-2xl mr-3 mt-1">{icono}</span>
                <div className="flex-grow">
                    <h3 className="text-md font-semibold text-gray-800">
                        {descripcion} 
                        {invernadero !== 'N/A' && <span className="text-sm font-normal text-gray-600"> en {invernadero}</span>}
                    </h3>
                    <p className="text-xs text-gray-500 mt-0.5">{tiempo}</p> 
                </div>
            </div>
            <button 
                className={`px-3 py-1.5 border rounded-md text-xs font-medium ${colorBoton} active:bg-opacity-80 transition-all duration-150 focus:outline-none focus:ring-2 whitespace-nowrap`}
                onClick={handleReporteClick}
            >
                Levantar reporte
            </button>
        </div>
    );
}

export default TarjetaAlerta;