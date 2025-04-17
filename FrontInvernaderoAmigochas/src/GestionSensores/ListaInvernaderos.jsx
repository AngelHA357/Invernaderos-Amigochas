import React from 'react';
import { useNavigate } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';
import invernaderosMock from '../mocks/invernaderos.json';

function ListaInvernaderos() {
  const navigate = useNavigate();

  const handleVerSensores = (invernadero) => {
    sessionStorage.setItem('invernaderoSeleccionado', JSON.stringify(invernadero));
    navigate(`/sensores/${invernadero.id}`);
  };

  return (
    <>
      <BarraNavegacion />
      <div className="min-h-screen bg-gradient-to-b from-green-50 to-white p-6">
        <div className="max-w-4xl mx-auto rounded-lg shadow-lg p-6 mt-6 border border-green-200 bg-white">
          {/* Título con icono */}
          <div className="flex items-center border-b border-green-100 pb-4 mb-6">
            <div className="bg-green-100 p-3 rounded-full mr-4">
              <span className="text-2xl" role="img" aria-label="invernadero">🌿</span>
            </div>
            <div>
              <h1 className="text-2xl font-bold text-gray-800">Gestión de Invernaderos</h1>
              <p className="text-sm text-green-600">Monitoreo y control de tus espacios verdes</p>
            </div>
          </div>

          {/* Lista de invernaderos */}
          <div className="mt-6">
            <h2 className="text-lg font-semibold text-gray-700 mb-4">Listado de Invernaderos</h2>
            
            {/* Encabezados */}
            <div className="grid grid-cols-4 gap-4 bg-green-100 text-green-800 py-3 px-4 rounded-t-lg font-semibold">
              <div>ID</div>
              <div>Invernadero</div>
              <div>Ubicación</div>
              <div>Acciones</div>
            </div>

            {/* Filas */}
            {invernaderosMock.map((invernadero, index) => (
              <div
                key={invernadero.id}
                className={`grid grid-cols-4 gap-4 py-4 px-4 ${
                  index % 2 === 0 ? 'bg-white' : 'bg-green-50'
                } hover:bg-green-100 transition-colors duration-200 border-b border-green-100`}
              >
                <div className="text-gray-800 font-medium">{invernadero.id}</div>
                <div className="text-gray-800 font-medium">{invernadero.name}</div>
                <div className="text-gray-600 flex items-center">
                  <span className="mr-1">📍</span> {invernadero.location}
                </div>
                <div>
                  <button 
                    onClick={() => handleVerSensores(invernadero)}
                    className="inline-flex items-center px-4 py-2 text-sm font-semibold text-white bg-green-600 rounded-full hover:bg-green-700 transition-colors duration-300 shadow-sm">
                    <span className="mr-1">🔍</span> Ver sensores
                  </button>
                </div>
              </div>
            ))}
          </div>
          
          {/* Footer con información */}
          <div className="mt-8 pt-4 border-t border-green-100 text-center text-xs text-green-600">
            <p>Sistema de Monitoreo Amigochas — Última actualización: {new Date().toLocaleDateString()}</p>
          </div>
        </div>
      </div>
    </>
  );
}

export default ListaInvernaderos;