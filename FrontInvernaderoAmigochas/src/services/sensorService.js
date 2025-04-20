// sensorService.js
const API_BASE_URL = '/api/v1/gestionSensores';

/**
 * Obtiene todos los invernaderos
 * @returns {Promise<Array>} Array de invernaderos
 */
export const obtenerInvernaderos = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/invernaderos`);
    
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.mensaje || 'Error al obtener invernaderos');
    }
    
    const data = await response.json();
    console.log('Datos recibidos de la API:', data); // Log para depuración
    
    // Transformar correctamente el formato de la respuesta para que sea compatible con el frontend
    return data.map(inv => ({
      id: inv._id,
      name: inv.nombre,
      sectores: inv.sectores || [],
      filas: inv.filas || []
    }));
  } catch (error) {
    console.error('Error al obtener invernaderos:', error);
    throw error;
  }
};

/**
 * Obtiene todos los sensores asociados a un invernadero específico
 * @param {string} invernaderoId - ID del invernadero
 * @returns {Promise<Array>} Array de sensores
 */
export const obtenerSensoresPorInvernadero = async (invernaderoId) => {
  try {
    const response = await fetch(`${API_BASE_URL}/invernadero/${invernaderoId}/sensores`);
    
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.mensaje || 'Error al obtener sensores del invernadero');
    }
    
    const data = await response.json();
    
    // Transformar correctamente el formato de la respuesta para que sea compatible con el frontend
    return data.map(sensor => ({
      id: sensor.idSensor,
      invernaderoId: sensor.idInvernadero,
      type: sensor.tipoSensor,
      status: sensor.estado ? 'Activo' : 'Inactivo',
      marca: sensor.marca,
      modelo: sensor.modelo,
      macAddress: sensor.macAddress,
      magnitud: sensor.magnitud,
      sector: sensor.sector,
      fila: sensor.fila,
      _id: sensor._id  // Guardamos el ID de MongoDB por si lo necesitamos
    }));
  } catch (error) {
    console.error(`Error al obtener sensores para el invernadero ${invernaderoId}:`, error);
    throw error;
  }
};

/**
 * Obtiene un sensor específico por su ID
 * @param {string} sensorId - ID del sensor
 * @returns {Promise<Object>} Objeto sensor
 */
export const obtenerSensorPorId = async (sensorId) => {
  try {
    const response = await fetch(`${API_BASE_URL}/${sensorId}`);
    
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.mensaje || 'Error al obtener sensor');
    }
    
    const sensor = await response.json();
    
    // Transformar correctamente el formato de la respuesta para que sea compatible con el frontend
    return {
      id: sensor.idSensor,
      invernaderoId: sensor.idInvernadero,
      type: sensor.tipoSensor,
      status: sensor.estado ? 'Activo' : 'Inactivo',
      marca: sensor.marca,
      modelo: sensor.modelo,
      macAddress: sensor.macAddress,
      magnitud: sensor.magnitud,
      sector: sensor.sector,
      fila: sensor.fila,
      _id: sensor._id
    };
  } catch (error) {
    console.error(`Error al obtener sensor ${sensorId}:`, error);
    throw error;
  }
};

/**
 * Registra un nuevo sensor
 * @param {Object} sensorData - Datos del sensor a registrar
 * @returns {Promise<Object>} Objeto sensor registrado
 */
export const registrarSensor = async (sensorData) => {
  try {
    // Transformar correctamente los datos para que sean compatibles con el backend
    const sensorPayload = {
      idSensor: sensorData.idSensor,
      macAddress: sensorData.macAddress,
      marca: sensorData.marca,
      modelo: sensorData.modelo,
      tipoSensor: sensorData.tipoSensor,
      magnitud: sensorData.magnitud,
      idInvernadero: sensorData.idInvernadero,
      sector: sensorData.sector,
      fila: sensorData.fila,
      estado: sensorData.estado
    };
    
    console.log('Enviando al backend (POST):', sensorPayload);
    
    const response = await fetch(`${API_BASE_URL}/registrar`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(sensorPayload)
    });
    
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.mensaje || 'Error al registrar sensor');
    }
    
    const sensor = await response.json();
    
    // Transformar correctamente la respuesta para que sea compatible con el frontend
    return {
      id: sensor.idSensor,
      invernaderoId: sensor.idInvernadero,
      type: sensor.tipoSensor,
      status: sensor.estado ? 'Activo' : 'Inactivo',
      marca: sensor.marca,
      modelo: sensor.modelo,
      macAddress: sensor.macAddress,
      magnitud: sensor.magnitud,
      sector: sensor.sector,
      fila: sensor.fila,
      _id: sensor._id
    };
  } catch (error) {
    console.error('Error al registrar sensor:', error);
    throw error;
  }
};

/**
 * Edita un sensor existente
 * @param {Object} sensorData - Datos actualizados del sensor
 * @returns {Promise<Object>} Objeto sensor actualizado
 */
export const editarSensor = async (sensorData) => {
  try {
    // Transformar correctamente los datos para que sean compatibles con el backend
    const sensorPayload = {
      _id: sensorData._id, // ID de MongoDB (requerido para la actualización)
      idSensor: sensorData.idSensor,
      macAddress: sensorData.macAddress,
      marca: sensorData.marca,
      modelo: sensorData.modelo,
      tipoSensor: sensorData.tipoSensor,
      magnitud: sensorData.magnitud,
      idInvernadero: sensorData.idInvernadero,
      sector: sensorData.sector,
      fila: sensorData.fila,
      estado: sensorData.estado
    };
    
    console.log('Enviando al backend (PUT):', sensorPayload);
    
    const response = await fetch(`${API_BASE_URL}/editar`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(sensorPayload)
    });
    
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.mensaje || 'Error al editar sensor');
    }
    
    const sensor = await response.json();
    
    // Transformar correctamente la respuesta para que sea compatible con el frontend
    return {
      id: sensor.idSensor,
      invernaderoId: sensor.idInvernadero,
      type: sensor.tipoSensor,
      status: sensor.estado ? 'Activo' : 'Inactivo',
      marca: sensor.marca,
      modelo: sensor.modelo,
      macAddress: sensor.macAddress,
      magnitud: sensor.magnitud,
      sector: sensor.sector,
      fila: sensor.fila,
      _id: sensor._id
    };
  } catch (error) {
    console.error('Error al editar sensor:', error);
    throw error;
  }
};

/**
 * Elimina un sensor por su ID
 * @param {string} sensorId - ID del sensor a eliminar
 * @returns {Promise<void>}
 */
export const eliminarSensor = async (sensorId) => {
  try {
    const response = await fetch(`${API_BASE_URL}/eliminar/${sensorId}`, {
      method: 'DELETE',
    });
    
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.mensaje || 'Error al eliminar sensor');
    }
    
    return true; // Operación exitosa
  } catch (error) {
    console.error(`Error al eliminar sensor ${sensorId}:`, error);
    throw error;
  }
};