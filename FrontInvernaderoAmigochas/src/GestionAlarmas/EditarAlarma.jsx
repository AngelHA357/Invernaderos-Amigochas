import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';
import { fetchAlarmaPorId, editarAlarma } from '../services/alarmaService';
import { obtenerInvernaderos, obtenerSensoresPorInvernadero } from '../services/sensorService';

function EditarAlarma() {
  const navigate = useNavigate();
  const { alarmaId } = useParams(); // Usar el parámetro correcto de la ruta
  const [formData, setFormData] = useState({
    idAlarma: '',
    invernaderoId: '',
    invernaderoNombre: '',
    valorMinimo: '',
    valorMaximo: '',
    medioNotificacion: '',
    sensores: [],
    magnitud: '',
    unidad: '',
    activo: true,
  });
  const [errors, setErrors] = useState({});
  const [showModal, setShowModal] = useState(false);
  const [invernaderos, setInvernaderos] = useState([]);
  const [sensores, setSensores] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingInvernaderos, setLoadingInvernaderos] = useState(true);
  const [loadingSensores, setLoadingSensores] = useState(false);
  const [error, setError] = useState(null);

  const formas_notificacion = [
    { id: 'SMS', name: 'Mensaje de texto' },
    { id: 'EMAIL', name: 'Correo electrónico' },
  ];

  // Cargar invernaderos al montar el componente
  useEffect(() => {
    const loadInvernaderos = async () => {
      try {
        const data = await obtenerInvernaderos();
        setInvernaderos(data);
      } catch (err) {
        alert('Error al cargar los invernaderos. Inténtalo de nuevo.');
      } finally {
        setLoadingInvernaderos(false);
      }
    };
    loadInvernaderos();
  }, []);

  // Cargar datos de la alarma al montar el componente
  useEffect(() => {
    const loadAlarma = async () => {
      try {
        setLoading(true);
        console.log("Cargando alarma con ID:", alarmaId); // Depuración
        const alarma = await fetchAlarmaPorId(alarmaId);
        console.log("Datos de alarma recibidos:", alarma); // Depuración
        
        // Buscar el invernadero que corresponde al nombre guardado
        let invernaderoId = '';
        if (invernaderos.length > 0 && alarma.invernadero) {
          const invernaderoEncontrado = invernaderos.find(inv => inv.name === alarma.invernadero);
          if (invernaderoEncontrado) {
            invernaderoId = invernaderoEncontrado.id;
          }
        }

        setFormData({
          idAlarma: alarma.idAlarma,
          invernaderoId: invernaderoId,
          invernaderoNombre: alarma.invernadero,
          valorMinimo: alarma.valorMinimo,
          valorMaximo: alarma.valorMaximo,
          medioNotificacion: alarma.medioNotificacion,
          sensores: alarma.sensores || [],
          magnitud: alarma.magnitud,
          unidad: alarma.unidad,
          activo: alarma.activo,
        });
      } catch (err) {
        console.error('Error al cargar alarma:', err);
        setError('No se pudo cargar la alarma. Inténtalo de nuevo.');
      } finally {
        setLoading(false);
      }
    };
    
    // Solo cargar la alarma cuando los invernaderos ya estén cargados
    if (!loadingInvernaderos && alarmaId) {
      loadAlarma();
    }
  }, [alarmaId, invernaderos, loadingInvernaderos]);

  // Cargar sensores cuando cambia el invernadero seleccionado
  useEffect(() => {
    const loadSensores = async () => {
      if (formData.invernaderoId) {
        setLoadingSensores(true);
        try {
          const data = await obtenerSensoresPorInvernadero(formData.invernaderoId);
          setSensores(data);
          
          // Actualizar el nombre del invernadero cuando cambia la selección
          const invernaderoSeleccionado = invernaderos.find(inv => inv.id === formData.invernaderoId);
          if (invernaderoSeleccionado) {
            setFormData(prev => ({
              ...prev,
              invernaderoNombre: invernaderoSeleccionado.name
            }));
          }
        } catch (err) {
          console.error('Error al cargar sensores:', err);
          alert('Error al cargar los sensores. Inténtalo de nuevo.');
        } finally {
          setLoadingSensores(false);
        }
      } else {
        setSensores([]);
      }
    };
    loadSensores();
  }, [formData.invernaderoId, invernaderos]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSensorChange = (e) => {
    const selectedId = e.target.value;
    if (selectedId && !formData.sensores.includes(selectedId)) {
      setFormData({ ...formData, sensores: [...formData.sensores, selectedId] });
    }
  };

  const removeSensor = (id) => {
    setFormData({
      ...formData,
      sensores: formData.sensores.filter((sensorId) => sensorId !== id),
    });
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.idAlarma) newErrors.idAlarma = 'El ID de la alarma es obligatorio.';
    if (!formData.invernaderoId) newErrors.invernaderoId = 'Debe seleccionar un invernadero.';
    if (!formData.valorMinimo) newErrors.valorMinimo = 'Debe ingresar un valor mínimo.';
    if (!formData.valorMaximo) newErrors.valorMaximo = 'Debe ingresar un valor máximo.';
    if (!formData.medioNotificacion)
      newErrors.medioNotificacion = 'Debe seleccionar una forma de notificación.';
    if (formData.sensores.length === 0) newErrors.sensores = 'Debe seleccionar al menos un sensor.';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (validateForm()) {
      try {
        // Intentar obtener magnitud y unidad del primer sensor seleccionado
        // Si no hay un sensor correspondiente en la lista actual, usamos los valores guardados anteriormente
        let magnitud = formData.magnitud;
        let unidad = formData.unidad;

        const primerSensorId = formData.sensores[0];
        const primerSensor = sensores.find(s => s.id === primerSensorId);

        if (primerSensor) {
          magnitud = primerSensor.type;
          unidad = primerSensor.magnitud;
        }

        const alarmaDTO = {
          idAlarma: formData.idAlarma,
          magnitud: magnitud,
          sensores: formData.sensores,
          invernadero: formData.invernaderoNombre, // Usar el nombre del invernadero, no el ID
          valorMinimo: parseFloat(formData.valorMinimo),
          valorMaximo: parseFloat(formData.valorMaximo),
          unidad: unidad,
          medioNotificacion: formData.medioNotificacion,
          activo: formData.activo,
        };
        
        console.log('Enviando datos para actualizar:', alarmaDTO);
        await editarAlarma(alarmaDTO);
        setShowModal(true);
      } catch (err) {
        console.error('Error al actualizar la alarma:', err);
        alert('Error al actualizar la alarma: ' + err.message);
      }
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-b from-green-50 to-white flex justify-center items-center">
        <div className="bg-white p-8 rounded-lg shadow-md border border-green-200">
          <div className="animate-pulse flex flex-col items-center">
            <div className="h-12 w-12 bg-green-200 rounded-full mb-4"></div>
            <div className="h-4 w-32 bg-green-100 rounded mb-3"></div>
            <p className="text-green-600">Cargando información de la alarma...</p>
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
            <h2 className="text-xl font-bold text-red-700 mb-2">Error de carga</h2>
            <p className="text-gray-600 mb-4">{error}</p>
            <div className="flex space-x-3">
              <button
                onClick={() => navigate('/alarmas')}
                className="px-4 py-2 bg-gray-300 text-gray-700 rounded-full hover:bg-gray-400 transition-colors duration-300"
              >
                Volver a alarmas
              </button>
              <button
                onClick={() => window.location.reload()}
                className="px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 transition-colors duration-300"
              >
                Intentar nuevamente
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <>
      <BarraNavegacion />
      <div className="min-h-screen bg-gradient-to-b from-green-50 to-white p-6">
        <div className="max-w-4xl mx-auto bg-white p-6 rounded-lg shadow-lg border border-green-200">
          <div className="flex items-center mb-6">
            <div className="bg-green-100 p-3 rounded-full mr-4">
              <span className="text-2xl" role="img" aria-label="alarma">
                🔔
              </span>
            </div>
            <div>
              <h1 className="text-2xl font-bold text-gray-800">Editar Alarma</h1>
              <p className="text-sm text-green-600">Actualiza los datos de la alarma seleccionada</p>
            </div>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="grid grid-cols-2 gap-4 mb-6">
              <div>
                <label className="block text-gray-700 font-medium mb-2">ID Alarma</label>
                <input
                  type="text"
                  name="idAlarma"
                  value={formData.idAlarma}
                  onChange={handleChange}
                  className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                  disabled
                />
                {errors.idAlarma && <p className="text-red-500 text-xs mt-1">{errors.idAlarma}</p>}
              </div>
              <div>
                <label className="block text-gray-700 font-medium mb-2">Invernadero</label>
                <select
                  name="invernaderoId"
                  value={formData.invernaderoId}
                  onChange={handleChange}
                  className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500 bg-white"
                  disabled={loadingInvernaderos}
                >
                  <option value="">Seleccionar Invernadero</option>
                  {invernaderos.map((invernadero) => (
                    <option key={invernadero.id} value={invernadero.id}>
                      {invernadero.name}
                    </option>
                  ))}
                </select>
                {errors.invernaderoId && <p className="text-red-500 text-xs mt-1">{errors.invernaderoId}</p>}
                {loadingInvernaderos && <p className="text-gray-500 text-xs mt-1">Cargando invernaderos...</p>}
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4 mb-6">
              <div>
                <label className="block text-gray-700 font-medium mb-2">Valor Mínimo</label>
                <input
                  type="number"
                  name="valorMinimo"
                  value={formData.valorMinimo}
                  onChange={handleChange}
                  className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                />
                {errors.valorMinimo && <p className="text-red-500 text-xs mt-1">{errors.valorMinimo}</p>}
              </div>
              <div>
                <label className="block text-gray-700 font-medium mb-2">Valor Máximo</label>
                <input
                  type="number"
                  name="valorMaximo"
                  value={formData.valorMaximo}
                  onChange={handleChange}
                  className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                />
                {errors.valorMaximo && <p className="text-red-500 text-xs mt-1">{errors.valorMaximo}</p>}
              </div>
            </div>

            <div className="mb-6">
              <label className="block text-gray-700 font-medium mb-2">Notificar a través de</label>
              <select
                name="medioNotificacion"
                value={formData.medioNotificacion}
                onChange={handleChange}
                className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500 bg-white"
              >
                <option value="">Seleccionar Forma de Notificación</option>
                {formas_notificacion.map((forma) => (
                  <option key={forma.id} value={forma.id}>
                    {forma.name}
                  </option>
                ))}
              </select>
              {errors.medioNotificacion && (
                <p className="text-red-500 text-xs mt-1">{errors.medioNotificacion}</p>
              )}
            </div>

            <div className="mb-6">
              <label className="block text-gray-700 font-medium mb-2">Sensores</label>
              <select
                onChange={handleSensorChange}
                className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500 bg-white"
                value=""
                disabled={loadingSensores || !formData.invernaderoId}
              >
                <option value="">
                  {formData.sensores.length > 0 ? 'Añadir otro sensor' : 'Seleccionar'}
                </option>
                {sensores.map((sensor) => (
                  <option key={sensor.id} value={sensor.id}>
                    {sensor.id} ({sensor.type}, {sensor.magnitud})
                  </option>
                ))}
              </select>
              {loadingSensores && <p className="text-gray-500 text-xs mt-1">Cargando sensores...</p>}
              <div className="mt-3 flex flex-wrap gap-2">
                {formData.sensores.map((sensorId) => {
                  const sensor = sensores.find((s) => s.id === sensorId);
                  return (
                    <span
                      key={sensorId}
                      className="bg-gray-100 text-gray-700 px-3 py-1 rounded-full flex items-center"
                    >
                      {sensor ? `${sensor.id} (${sensor.type})` : sensorId}
                      <button
                        type="button"
                        className="ml-2 text-gray-500 hover:text-gray-700"
                        onClick={() => removeSensor(sensorId)}
                      >
                        ✕
                      </button>
                    </span>
                  );
                })}
              </div>
              {errors.sensores && <p className="text-red-500 text-xs mt-1">{errors.sensores}</p>}
            </div>

            <div className="flex justify-center mt-6">
              <button
                type="submit"
                className="px-6 py-3 bg-green-600 text-white rounded-full hover:bg-green-700 transition-colors duration-300 shadow-sm flex items-center justify-center"
              >
                <span className="mr-2">✔</span> Actualizar Alarma
              </button>
            </div>
          </form>
        </div>
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full">
            <h3 className="text-xl font-bold text-gray-800 mb-4">¡Éxito!</h3>
            <p className="text-gray-600 mb-6">La alarma se ha actualizado correctamente.</p>
            <div className="flex justify-center">
              <button
                onClick={() => {
                  setShowModal(false);
                  navigate('/alarmas');
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

export default EditarAlarma;

