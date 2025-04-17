import React from 'react';
import { useNavigate } from 'react-router-dom';

function TarjetaAlerta({ id, invernadero, descripcion, tiempo }) {
  const navigate = useNavigate();
  // Determinar el color y el icono según el tipo de alerta
  let colorBorde = 'border-yellow-500';
  let colorFondo = 'hover:bg-yellow-50';
  let colorBoton = 'border-yellow-300 text-yellow-700 hover:bg-yellow-500';
  let icono = '⚠️';

  // Personalizar según el tipo de alerta
  if (descripcion.toLowerCase().includes('temperatura')) {
    colorBorde = 'border-red-500';
    colorFondo = 'hover:bg-red-50';
    colorBoton = 'border-red-300 text-red-700 hover:bg-red-500 hover:text-white hover:border-red-500 focus:ring-red-300';
    icono = '🔥';
  } else if (descripcion.toLowerCase().includes('humedad')) {
    colorBorde = 'border-blue-500';
    colorFondo = 'hover:bg-blue-50';
    colorBoton = 'border-blue-300 text-blue-700 hover:bg-blue-500 hover:text-white hover:border-blue-500 focus:ring-blue-300';
    icono = '💧';
  } else if (descripcion.toLowerCase().includes('co2')) {
    colorBorde = 'border-purple-500';
    colorFondo = 'hover:bg-purple-50';
    colorBoton = 'border-purple-300 text-purple-700 hover:bg-purple-500 hover:text-white hover:border-purple-500 focus:ring-purple-300';
    icono = '☁️';
  } else if (descripcion.toLowerCase().includes('luz')) {
    colorBorde = 'border-amber-500';
    colorFondo = 'hover:bg-amber-50';
    colorBoton = 'border-amber-300 text-amber-700 hover:bg-amber-500 hover:text-white hover:border-amber-500 focus:ring-amber-300';
    icono = '💡';
  } else if (descripcion.toLowerCase().includes('ph')) {
    colorBorde = 'border-green-500';
    colorFondo = 'hover:bg-green-50';
    colorBoton = 'border-green-300 text-green-700 hover:bg-green-500 hover:text-white hover:border-green-500 focus:ring-green-300';
    icono = '🧪';
  }

  const handleLevantarReporte = () => {
    // Guardar el ID de la alerta en localStorage antes de navegar
    localStorage.setItem('alertaSeleccionada', id);
    navigate(`/anomalias/${id}`);
  }
  
  return (
    <div className={`border-l-4 ${colorBorde} p-4 mb-4 bg-white rounded-r-lg shadow-sm flex justify-between items-center ${colorFondo} transition-colors duration-200`}>
      <div className="flex items-start">
        <span className="mr-3">{icono}</span>
        <div>
          <h3 className="text-base font-semibold text-gray-800">
            {descripcion} en {invernadero}
          </h3>
          <p className="text-sm text-gray-500">{descripcion}</p>
          <p className="text-sm text-gray-400">Hace {tiempo}</p>
        </div>
      </div>
      <button 
      className={`px-4 py-2 border rounded-md text-sm ${colorBoton} active:bg-opacity-80 transition-all duration-200 focus:outline-none focus:ring-2`}
      onClick= {() => handleLevantarReporte()}>
        Levantar reporte
      </button>
    </div>
  );
}

export default TarjetaAlerta;
