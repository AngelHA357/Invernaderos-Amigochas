import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';
import { fetchAlarmas, eliminarAlarma, toggleEstadoAlarma as cambiarEstadoAlarma } from '../services/alarmaService';

function ListaAlarmas() {
  const navigate = useNavigate();
  const [alarmas, setAlarmas] = useState([]);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [alarmaToDelete, setAlarmaToDelete] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showDetalleModal, setShowDetalleModal] = useState(false);
  const [alarmaDetalle, setAlarmaDetalle] = useState(null);

  // Cargar alarmas al montar el componente
  useEffect(() => {
    const loadAlarmas = async () => {
      try {
        setLoading(true);
        const data = await fetchAlarmas();
        // Mapear activo a estado para el frontend
        const mappedData = data.map((alarma) => ({
          ...alarma,
          id: alarma.idAlarma,
          estado: alarma.activo ? 'Activa' : 'Inactiva',
        }));
        setAlarmas(mappedData);
      } catch (err) {
        setError('No se pudieron cargar las alarmas. Inténtalo de nuevo.');
      } finally {
        setLoading(false);
      }
    };
    loadAlarmas();
  }, []);

  const handleAgregarAlarma = () => {
    navigate('/alarmas/agregarAlarma');
  };

  const handleEditarAlarma = (alarma) => {
    sessionStorage.setItem('alarmaSeleccionada', JSON.stringify(alarma));
    navigate(`/alarmas/${alarma.idAlarma}`);  // Usar la ruta correcta que coincide con la definición en App.jsx
  };

  const handleEliminarAlarma = (alarma) => {
    setAlarmaToDelete(alarma);
    setShowDeleteModal(true);
  };

  const confirmarEliminarAlarma = async () => {
    try {
      setLoading(true);
      await eliminarAlarma(alarmaToDelete.id);
      setAlarmas((prevAlarmas) => prevAlarmas.filter((a) => a.id !== alarmaToDelete.id));
      setShowDeleteModal(false);
      alert(`La alarma ${alarmaToDelete.id} ha sido eliminada correctamente`);
    } catch (err) {
      alert('Error al eliminar la alarma. Inténtalo de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  const handleToggleEstadoAlarma = async (alarmaId) => {
    const alarma = alarmas.find((a) => a.id === alarmaId);
    const nuevoEstado = alarma.estado === 'Activa' ? false : true;
    try {
      setLoading(true);
      await cambiarEstadoAlarma(alarmaId, nuevoEstado);
      setAlarmas((prevAlarmas) =>
        prevAlarmas.map((a) =>
          a.id === alarmaId
            ? { ...a, estado: nuevoEstado ? 'Activa' : 'Inactiva', activo: nuevoEstado }
            : a
        )
      );
    } catch (err) {
      alert('Error al cambiar el estado de la alarma. Inténtalo de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  const handleVerDetalle = (alarma) => {
    setAlarmaDetalle(alarma);
    setShowDetalleModal(true);
  };

  // Función para obtener el icono según el tipo de magnitud
  const getIconoMagnitud = (tipo) => {
    switch (tipo?.toLowerCase()) {
      case 'temperatura':
        return '🌡️';
      case 'humedad':
        return '💧';
      case 'co2':
        return '☁️';
      case 'luz':
        return '☀️';
      default:
        return '📊';
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-b from-green-50 to-white flex justify-center items-center">
        <div className="bg-white p-8 rounded-lg shadow-md border border-green-200">
          <div className="animate-pulse flex flex-col items-center">
            <div className="h-12 w-12 bg-green-200 rounded-full mb-4"></div>
            <div className="h-4 w-32 bg-green-100 rounded mb-3"></div>
            <p className="text-green-600">Cargando información de alarmas...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gradient-to-b from-green-50 to-white flex justify-center items-center">
        <div className="bg-white p-8 rounded-lg shadow-md border border-red-200 max-w-md">
          <div className="flex flex-col items-center text-center">
            <div className="text-red-500 text-5xl mb-4">⚠️</div>
            <h2 className="text-xl font-bold text-red-700 mb-2">Error de conexión</h2>
            <p className="text-gray-600 mb-4">{error}</p>
            <button
              onClick={() => window.location.reload()}
              className="px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 transition-colors duration-300"
            >
              Intentar nuevamente
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <>
      <BarraNavegacion />
      <div className="min-h-screen bg-gradient-to-b from-green-50 to-white p-6">
        <div className="max-w-6xl mx-auto rounded-lg shadow-lg p-6 mt-6 border border-green-200 bg-white">
          <div className="flex items-center border-b border-green-100 pb-4 mb-6">
            <div className="bg-green-100 p-3 rounded-full mr-4">
              <span className="text-2xl" role="img" aria-label="alarma">
                🔔
              </span>
            </div>
            <div>
              <h1 className="text-2xl font-bold text-gray-800">Gestión de Alarmas</h1>
              <p className="text-sm text-green-600">Configuración y monitoreo de alarmas del sistema</p>
            </div>
          </div>

          <div className="mt-6">
            <div className="flex items-center justify-between mb-6">
              <div className="flex items-center">
                <span className="text-green-500 mr-2">🔔</span>
                <h2 className="text-xl font-semibold text-gray-700">Alarmas disponibles - {alarmas.length}</h2>
              </div>
              <button
                onClick={handleAgregarAlarma}
                className="px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 transition-colors duration-300 flex items-center shadow-sm"
              >
                <span className="mr-1">+</span> Agregar alarma
              </button>
            </div>

            {alarmas.length > 0 ? (
              <div className="overflow-x-auto rounded-lg border border-gray-200">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-green-100">
                    <tr>
                      <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-green-800 uppercase tracking-wider">
                        ID
                      </th>
                      <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-green-800 uppercase tracking-wider">
                        Invernadero
                      </th>
                      <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-green-800 uppercase tracking-wider">
                        Magnitud / Rangos
                      </th>
                      <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-green-800 uppercase tracking-wider">
                        Sensores
                      </th>
                      <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-green-800 uppercase tracking-wider">
                        Estado
                      </th>
                      <th scope="col" className="px-6 py-3 text-center text-xs font-medium text-green-800 uppercase tracking-wider">
                        Acciones
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {alarmas.map((alarma, index) => (
                      <tr key={alarma.id} className={index % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-800">
                          {alarma.id}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                          {alarma.invernadero}
                        </td>
                        <td className="px-6 py-4">
                          <div className="flex items-center">
                            <span className="flex-shrink-0 h-10 w-10 flex items-center justify-center rounded-full bg-green-100 text-xl">
                              {getIconoMagnitud(alarma.magnitud)}
                            </span>
                            <div className="ml-3">
                              <div className="text-sm font-medium text-gray-900">{alarma.magnitud}</div>
                              <div className="text-xs text-gray-500">
                                Min: {alarma.valorMinimo} - Max: {alarma.valorMaximo} {alarma.unidad}
                              </div>
                            </div>
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          {alarma.sensores && alarma.sensores.length > 0 ? (
                            <div className="flex flex-wrap gap-1">
                              {alarma.sensores.slice(0, 2).map((sensor, idx) => (
                                <span key={idx} className="inline-block bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded-full">
                                  {sensor}
                                </span>
                              ))}
                              {alarma.sensores.length > 2 && (
                                <span className="inline-block bg-gray-100 text-gray-800 text-xs px-2 py-1 rounded-full">
                                  +{alarma.sensores.length - 2} más
                                </span>
                              )}
                            </div>
                          ) : (
                            <span className="text-sm text-gray-500">Sin sensores</span>
                          )}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <button
                            onClick={() => handleToggleEstadoAlarma(alarma.id)}
                            className={`relative inline-flex items-center h-6 rounded-full w-11 
                                ${alarma.estado === 'Activa' ? 'bg-green-600' : 'bg-gray-400'}`}
                          >
                            <span className="sr-only">Cambiar estado</span>
                            <span
                              className={`inline-block w-4 h-4 transform transition ease-in-out duration-200 rounded-full bg-white
                                ${alarma.estado === 'Activa' ? 'translate-x-6' : 'translate-x-1'}`}
                            />
                          </button>
                          <span className="ml-2 text-xs text-gray-500">
                            {alarma.estado}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-center text-sm font-medium">
                          <div className="flex justify-center space-x-2">
                            <button
                              onClick={() => handleVerDetalle(alarma)}
                              className="text-blue-600 hover:text-blue-900 bg-blue-100 px-2 py-1 rounded-full"
                            >
                              <span className="mr-1">🔍</span> Detalles
                            </button>
                            <button
                              onClick={() => handleEditarAlarma(alarma)}
                              className="text-green-600 hover:text-green-900 bg-green-100 px-2 py-1 rounded-full"
                            >
                              <span className="mr-1">✏️</span> Editar
                            </button>
                            <button
                              onClick={() => handleEliminarAlarma(alarma)}
                              className="text-red-600 hover:text-red-900 bg-red-100 px-2 py-1 rounded-full"
                            >
                              <span className="mr-1">🗑️</span> Eliminar
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <div className="text-center p-8 bg-green-50 rounded-lg border border-green-100 text-gray-600">
                <div className="text-5xl mb-4">🔔</div>
                <p className="text-lg font-medium text-gray-700 mb-2">No hay alarmas configuradas</p>
                <p className="text-green-600 mb-4">Agrega una nueva alarma para comenzar el monitoreo.</p>
                <button
                  onClick={handleAgregarAlarma}
                  className="px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 transition-colors duration-300 inline-flex items-center"
                >
                  <span className="mr-1">+</span> Agregar alarma ahora
                </button>
              </div>
            )}
          </div>

          <div className="mt-8 pt-4 border-t border-green-100 text-center text-xs text-green-600">
            <p>Sistema de Alarmas Amigochas — Última actualización: {new Date().toLocaleDateString()}</p>
          </div>
        </div>
      </div>

      {/* Modal de confirmación de eliminación */}
      {showDeleteModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full">
            <h3 className="text-xl font-bold text-gray-800 mb-4">Confirmar eliminación</h3>
            <p className="text-gray-600 mb-6">
              ¿Estás seguro de que deseas eliminar la alarma{' '}
              <span className="font-semibold">{alarmaToDelete?.id}</span>? Esta acción no se puede deshacer.
            </p>
            <div className="flex justify-end space-x-3">
              <button
                onClick={() => setShowDeleteModal(false)}
                className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-100 transition-colors"
              >
                Cancelar
              </button>
              <button
                onClick={confirmarEliminarAlarma}
                className={`px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 transition-colors ${loading ? 'opacity-50 cursor-not-allowed' : ''}`}
                disabled={loading}
              >
                {loading ? 'Eliminando...' : 'Eliminar'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal de detalles de la alarma */}
      {showDetalleModal && alarmaDetalle && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-lg max-w-2xl w-full">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center">
                <span className="text-3xl mr-3">{getIconoMagnitud(alarmaDetalle.magnitud)}</span>
                <h3 className="text-xl font-bold text-gray-800">Alarma {alarmaDetalle.id}</h3>
              </div>
              <button
                onClick={() => setShowDetalleModal(false)}
                className="text-gray-400 hover:text-gray-600 transition-colors text-xl"
              >
                ✕
              </button>
            </div>

            <div className="bg-gray-50 p-4 rounded-lg mb-4">
              <div className="grid grid-cols-2 gap-6">
                <div>
                  <h4 className="text-md font-semibold text-gray-700 mb-3 border-b pb-2">Información General</h4>
                  <div className="space-y-2">
                    <p className="flex justify-between">
                      <span className="text-gray-600">Magnitud:</span>
                      <span className="font-medium">{alarmaDetalle.magnitud || 'N/A'}</span>
                    </p>
                    <p className="flex justify-between">
                      <span className="text-gray-600">Invernadero:</span>
                      <span className="font-medium">{alarmaDetalle.invernadero || 'N/A'}</span>
                    </p>
                    <p className="flex justify-between">
                      <span className="text-gray-600">Estado:</span>
                      <span className={`font-medium ${alarmaDetalle.estado === 'Activa' ? 'text-green-600' : 'text-red-600'}`}>
                        {alarmaDetalle.estado}
                      </span>
                    </p>
                  </div>
                </div>

                <div>
                  <h4 className="text-md font-semibold text-gray-700 mb-3 border-b pb-2">Configuración</h4>
                  <div className="space-y-2">
                    <p className="flex justify-between">
                      <span className="text-gray-600">Valor Mínimo:</span>
                      <span className="font-medium">{alarmaDetalle.valorMinimo} {alarmaDetalle.unidad}</span>
                    </p>
                    <p className="flex justify-between">
                      <span className="text-gray-600">Valor Máximo:</span>
                      <span className="font-medium">{alarmaDetalle.valorMaximo} {alarmaDetalle.unidad}</span>
                    </p>
                    <p className="flex justify-between">
                      <span className="text-gray-600">Notificación:</span>
                      <span className="font-medium">{alarmaDetalle.medioNotificacion || 'N/A'}</span>
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <div className="bg-green-50 p-4 rounded-lg">
              <h4 className="text-md font-semibold text-gray-700 mb-3 border-b pb-2">Sensores Asociados</h4>
              {alarmaDetalle.sensores && alarmaDetalle.sensores.length > 0 ? (
                <div className="grid grid-cols-2 gap-2">
                  {alarmaDetalle.sensores.map((sensor, index) => (
                    <div key={index} className="flex items-center bg-white p-2 rounded-md">
                      <span className="text-lg mr-2">📊</span>
                      <span className="font-medium">{sensor}</span>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-center text-gray-500">No hay sensores asociados a esta alarma.</p>
              )}
            </div>

            <div className="flex justify-end mt-6">
              <button
                onClick={() => setShowDetalleModal(false)}
                className="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 transition-colors"
              >
                Cerrar
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}

export default ListaAlarmas;