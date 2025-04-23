import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';
import { registrarAlarma } from '../services/alarmaService';
import { obtenerInvernaderos, obtenerSensoresPorInvernadero } from '../services/sensorService';

function AgregarAlarma() {
  const navigate = useNavigate();
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    id: '',
    invernadero: '',
    valorMinimo: '',
    valorMaximo: '',
    medioNotificacion: '',
    sensores: [],
  });
  const [errors, setErrors] = useState({});
  const [invernaderos, setInvernaderos] = useState([]);
  const [sensores, setSensores] = useState([]);
  const [loadingInvernaderos, setLoadingInvernaderos] = useState(true);
  const [loadingSensores, setLoadingSensores] = useState(false);
  const [invernaderoSeleccionado, setInvernaderoSeleccionado] = useState(null);

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

  // Cargar sensores cuando cambia el invernadero seleccionado
  useEffect(() => {
    const loadSensores = async () => {
      if (formData.invernadero) {
        setLoadingSensores(true);
        try {
          const data = await obtenerSensoresPorInvernadero(formData.invernadero);
          setSensores(data);
          // Buscar el invernadero seleccionado para guardar su nombre
          const invernaderoObj = invernaderos.find(inv => inv.id === formData.invernadero);
          setInvernaderoSeleccionado(invernaderoObj);
          // Limpiar sensores seleccionados si cambiamos de invernadero
          setFormData((prev) => ({ ...prev, sensores: [] }));
        } catch (err) {
          alert('Error al cargar los sensores. Inténtalo de nuevo.');
        } finally {
          setLoadingSensores(false);
        }
      } else {
        setSensores([]);
        setInvernaderoSeleccionado(null);
        setFormData((prev) => ({ ...prev, sensores: [] }));
      }
    };
    loadSensores();
  }, [formData.invernadero, invernaderos]);

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
    if (!formData.id) newErrors.id = 'El ID de la alarma es obligatorio.';
    if (!formData.invernadero) newErrors.invernadero = 'Debe seleccionar un invernadero.';
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
        // Obtener magnitud y unidad del primer sensor seleccionado
        const primerSensor = sensores.find((s) => s.id === formData.sensores[0]);
        if (!primerSensor) {
          alert('Error: No se encontró el sensor seleccionado.');
          return;
        }

        const alarmaDTO = {
          idAlarma: formData.id,
          magnitud: primerSensor.type, // Magnitud del sensor
          sensores: formData.sensores,
          invernadero: invernaderoSeleccionado?.name || "Desconocido", // Usar el nombre del invernadero en lugar del ID
          valorMinimo: parseFloat(formData.valorMinimo),
          valorMaximo: parseFloat(formData.valorMaximo),
          unidad: primerSensor.magnitud, // Unidad del sensor
          medioNotificacion: formData.medioNotificacion,
          activo: true,
        };
        await registrarAlarma(alarmaDTO);
        setShowModal(true);
      } catch (err) {
        alert('Error al registrar la alarma. Inténtalo de nuevo.');
      }
    }
  };

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
              <h1 className="text-2xl font-bold text-gray-800">Agregar Alarma</h1>
              <p className="text-sm text-green-600">Configura una nueva alarma para tu sistema</p>
            </div>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="grid grid-cols-2 gap-4 mb-6">
              <div>
                <label className="block text-gray-700 font-medium mb-2">ID Alarma</label>
                <input
                  type="text"
                  name="id"
                  value={formData.id}
                  onChange={handleChange}
                  className="w-full border border-green-200 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                />
                {errors.id && <p className="text-red-500 text-xs mt-1">{errors.id}</p>}
              </div>
              <div>
                <label className="block text-gray-700 font-medium mb-2">Invernadero</label>
                <select
                  name="invernadero"
                  value={formData.invernadero}
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
                {errors.invernadero && <p className="text-red-500 text-xs mt-1">{errors.invernadero}</p>}
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
                disabled={loadingSensores || !formData.invernadero}
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
                <span className="mr-2">+</span> Agregar Alarma
              </button>
            </div>
          </form>
        </div>
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full">
            <h3 className="text-xl font-bold text-gray-800 mb-4">¡Éxito!</h3>
            <p className="text-gray-600 mb-6">La alarma se ha creado correctamente.</p>
            <div className="flex justify-center">
              <button
                onClick={() => {
                  setShowModal(false);
                  navigate(-1);
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

export default AgregarAlarma;