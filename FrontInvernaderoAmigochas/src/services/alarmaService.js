// alarmaService.js
const API_BASE_URL = '/api/v1/alarmas';

// Función auxiliar para obtener headers con autenticación
const getAuthHeaders = () => {
  const authToken = localStorage.getItem('authToken');
  return authToken ?
    { 'Authorization': `Bearer ${authToken}`, 'Content-Type': 'application/json' } :
    { 'Content-Type': 'application/json' };
};

// Función auxiliar para manejar respuestas
const handleResponse = async (response, errorMessage) => {
  if (!response.ok) {
    if (response.status === 401) {
      localStorage.removeItem('authToken');
      localStorage.removeItem('userRole');
      localStorage.removeItem('username');
      throw new Error('Sesión expirada. Por favor, inicie sesión nuevamente.');
    }

    const errorData = await response.json();
    throw new Error(errorData.mensaje || errorMessage);
  }

  return await response.json();
};

// Obtener todas las alarmas
export const fetchAlarmas = async () => {
  try {
    const response = await fetch(`http://localhost:8080${API_BASE_URL}`, {
      headers: getAuthHeaders()
    });

    return await handleResponse(response, 'Error al obtener las alarmas');
  } catch (error) {
    console.error('Error al obtener alarmas:', error);
    throw error;
  }
};

// Obtener una alarma por ID
export const fetchAlarmaPorId = async (idAlarma) => {
  try {
    const response = await fetch(`http://localhost:8080${API_BASE_URL}/${idAlarma}`, {
      headers: getAuthHeaders()
    });

    return await handleResponse(response, 'Error al obtener la alarma');
  } catch (error) {
    console.error(`Error al obtener alarma ${idAlarma}:`, error);
    throw error;
  }
};

// Registrar una nueva alarma
export const registrarAlarma = async (alarma) => {
  try {
    console.log("Enviando datos de alarma al servidor:", alarma);

    const response = await fetch(`http://localhost:8080${API_BASE_URL}/registrar`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(alarma)
    });

    return await handleResponse(response, 'Error al registrar la alarma');
  } catch (error) {
    console.error('Error al registrar alarma:', error);
    throw error;
  }
};

// Editar una alarma existente
export const editarAlarma = async (alarma) => {
  try {
    console.log("Enviando datos para editar alarma:", alarma);

    const response = await fetch(`http://localhost:8080${API_BASE_URL}/editar`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify(alarma)
    });

    return await handleResponse(response, 'Error al editar la alarma');
  } catch (error) {
    console.error('Error al editar alarma:', error);
    throw error;
  }
};

// Eliminar una alarma
export const eliminarAlarma = async (idAlarma) => {
  try {
    const response = await fetch(`http://localhost:8080${API_BASE_URL}/eliminar/${idAlarma}`, {
      method: 'DELETE',
      headers: getAuthHeaders()
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.mensaje || 'Error al eliminar la alarma');
    }

    return true;
  } catch (error) {
    console.error(`Error al eliminar la alarma ${idAlarma}:`, error);
    throw error;
  }
};

// Cambiar estado de una alarma (activar/desactivar)
export const toggleEstadoAlarma = async (idAlarma, activo) => {
  try {
    const alarmaActual = await fetchAlarmaPorId(idAlarma);

    const alarmaActualizada = {
      ...alarmaActual,
      activo: activo
    };

    const response = await fetch(`http://localhost:8080${API_BASE_URL}/editar`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify(alarmaActualizada)
    });

    return await handleResponse(response, 'Error al cambiar el estado de la alarma');
  } catch (error) {
    console.error(`Error al cambiar el estado de la alarma ${idAlarma}:`, error);
    throw error;
  }
};
