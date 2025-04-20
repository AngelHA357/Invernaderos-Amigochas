const API_URL = 'http://localhost:8080/api/v1/gestionSensores';

/**
 * Obtiene todos los sensores desde el backend
 * @returns {Promise} Promesa con los datos de los sensores
 */
export const getAllSensores = async () => {
  try {
    const response = await fetch(API_URL);
    if (!response.ok) {
      throw new Error('Error al obtener los sensores');
    }
    return await response.json();
  } catch (error) {
    console.error('Error en la petición:', error);
    throw error;
  }
};

/**
 * Obtiene un sensor por su ID
 * @param {string} id ID del sensor a buscar
 * @returns {Promise} Promesa con los datos del sensor
 */
export const getSensorById = async (id) => {
  try {
    const response = await fetch(`${API_URL}/${id}`);
    if (!response.ok) {
      throw new Error('Error al obtener el sensor');
    }
    return await response.json();
  } catch (error) {
    console.error('Error en la petición:', error);
    throw error;
  }
};

/**
 * Obtiene todos los invernaderos del sistema
 * @returns {Promise} Promesa con la lista de invernaderos
 */
export const getAllInvernaderos = async () => {
  try {
    const response = await fetch(`${API_URL}/invernaderos`);
    if (!response.ok) {
      throw new Error('Error al obtener los invernaderos');
    }
    return await response.json();
  } catch (error) {
    console.error('Error en la petición:', error);
    throw error;
  }
};

/**
 * Obtiene todos los sensores de un invernadero específico
 * @param {string} idInvernadero ID del invernadero
 * @returns {Promise} Promesa con los datos de los sensores del invernadero
 */
export const getSensoresByInvernadero = async (idInvernadero) => {
  try {
    const response = await fetch(`${API_URL}/invernadero/${idInvernadero}/sensores`);
    if (!response.ok) {
      throw new Error('Error al obtener los sensores del invernadero');
    }
    return await response.json();
  } catch (error) {
    console.error('Error en la petición:', error);
    throw error;
  }
};

/**
 * Registra un nuevo sensor en el sistema
 * @param {Object} sensorData Datos del sensor a registrar
 * @returns {Promise} Promesa con la respuesta del servidor
 */
export const registrarSensor = async (sensorData) => {
  try {
    const response = await fetch(`${API_URL}/registrar`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(sensorData),
    });
    
    if (!response.ok) {
      throw new Error('Error al registrar el sensor');
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error en la petición:', error);
    throw error;
  }
};

/**
 * Actualiza un sensor existente
 * @param {Object} sensorData Datos del sensor a actualizar
 * @returns {Promise} Promesa con la respuesta del servidor
 */
export const editarSensor = async (sensorData) => {
  try {
    const response = await fetch(`${API_URL}/editar`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(sensorData),
    });
    
    if (!response.ok) {
      throw new Error('Error al editar el sensor');
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error en la petición:', error);
    throw error;
  }
};

/**
 * Elimina un sensor por su ID
 * @param {string} id ID del sensor a eliminar
 * @returns {Promise} Promesa con la respuesta del servidor
 */
export const eliminarSensor = async (id) => {
  try {
    const response = await fetch(`${API_URL}/eliminar/${id}`, {
      method: 'DELETE',
    });
    
    if (!response.ok) {
      throw new Error('Error al eliminar el sensor');
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error en la petición:', error);
    throw error;
  }
};