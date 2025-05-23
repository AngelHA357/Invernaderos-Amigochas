import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { verificarReporteExistente } from '../services/ReporteService';

function TarjetaAlerta({ id, invernadero, descripcion, tipo, tiempo }) {
    const navigate = useNavigate();
    const [tieneReporte, setTieneReporte] = useState(false);
    const [cargando, setCargando] = useState(false);

    // Verificar si la anomalía ya tiene un reporte asociado
    useEffect(() => {
        const verificarReporte = async () => {
            setCargando(true);
            try {
                const reporteExiste = await verificarReporteExistente(id);
                setTieneReporte(reporteExiste);
            } catch (error) {
                console.error('Error al verificar reporte existente:', error);
            } finally {
                setCargando(false);
            }
        };

        verificarReporte();
    }, [id]);

    let colorBorde = 'border-yellow-400';
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
        }
    }

    const handleReporteClick = () => {
        localStorage.setItem('alertaSeleccionadaId', id); 
        // Si tiene reporte, navegar a la vista de reporte, de lo contrario a crear uno
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
                disabled={cargando}
            >
                {cargando ? (
                    <span className="inline-flex items-center">
                        <svg className="animate-spin -ml-1 mr-2 h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        Verificando...
                    </span>
                ) : tieneReporte ? 'Ver reporte' : 'Levantar reporte'}
            </button>
        </div>
    );
}

export default TarjetaAlerta;

