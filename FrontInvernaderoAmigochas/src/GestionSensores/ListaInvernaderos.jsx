import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';
import { obtenerInvernaderos } from '../services/sensorService';

function ListaInvernaderos() {
  const navigate = useNavigate();
  const [invernaderos, setInvernaderos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const cargarInvernaderos = async () => {
      try {
        setLoading(true);
        const data = await obtenerInvernaderos();
        setInvernaderos(data);
        setError('');
      } catch (err) {
        setError('Error al cargar los invernaderos. Por favor, intente de nuevo más tarde.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    cargarInvernaderos();
  }, []);

  const handleVerSensores = (invernadero) => {
    sessionStorage.setItem('invernaderoSeleccionado', JSON.stringify(invernadero));
    navigate(`/sensores/${invernadero.id}`);
  };

  if (loading) {
    return (
      <>
        <BarraNavegacion />
        <div className="min-h-screen bg-gradient-to-b from-green-50 to-white flex justify-center items-center">
          <div className="bg-white p-8 rounded-lg shadow-md border border-green-200">
            <div className="animate-pulse flex flex-col items-center">
              <div className="h-12 w-12 bg-green-200 rounded-full mb-4"></div>
              <div className="h-4 w-32 bg-green-100 rounded mb-3"></div>
              <p className="text-green-600">Cargando invernaderos...</p>
            </div>
          </div>
        </div>
      </>
    );
  }

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

          {/* Mensaje de error si existe */}
          {error && (
            <div className="mb-6 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative">
              <strong className="font-bold">Error: </strong>
              <span className="block sm:inline">{error}</span>
            </div>
          )}

          {/* Lista de invernaderos */}
          <div className="mt-6">
            <h2 className="text-lg font-semibold text-gray-700 mb-4">Listado de Invernaderos</h2>
            
            {invernaderos.length > 0 ? (
              <>
                {/* Encabezados */}
                <div className="grid grid-cols-4 gap-4 bg-green-100 text-green-800 py-3 px-4 rounded-t-lg font-semibold">
                  <div>Invernadero</div>
                  <div>Sectores</div>
                  <div>Filas</div>
                  <div>Acciones</div>
                </div>

                {/* Filas */}
                {invernaderos.map((invernadero, index) => (
                  <div
                    key={invernadero.id}
                    className={`grid grid-cols-4 gap-4 py-4 px-4 ${
                      index % 2 === 0 ? 'bg-white' : 'bg-green-50'
                    } hover:bg-green-100 transition-colors duration-200 border-b border-green-100`}
                  >
                    <div className="text-gray-800 font-medium">{invernadero.name}</div>
                    <div className="text-gray-600">
                      {invernadero.sectores && invernadero.sectores.length > 0 ? (
                        <div className="flex flex-wrap gap-1">
                          {invernadero.sectores.map((sector, idx) => (
                            <span key={idx} className="inline-block bg-green-100 text-green-800 text-xs px-2 py-1 rounded-full">
                              {sector}
                            </span>
                          ))}
                        </div>
                      ) : (
                        <span className="text-gray-400">No definidos</span>
                      )}
                    </div>
                    <div className="text-gray-600">
                      {invernadero.filas && invernadero.filas.length > 0 ? (
                        <div className="flex flex-wrap gap-1">
                          {invernadero.filas.map((fila, idx) => (
                            <span key={idx} className="inline-block bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded-full">
                              {fila}
                            </span>
                          ))}
                        </div>
                      ) : (
                        <span className="text-gray-400">No definidas</span>
                      )}
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
              </>
            ) : (
              <div className="text-center p-8 bg-green-50 rounded-lg border border-green-100 text-gray-600">
                <div className="text-5xl mb-4">🌱</div>
                <p className="text-lg font-medium text-gray-700 mb-2">No hay invernaderos registrados</p>
                <p className="text-green-600 mb-4">No se encontraron invernaderos en el sistema.</p>
              </div>
            )}
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