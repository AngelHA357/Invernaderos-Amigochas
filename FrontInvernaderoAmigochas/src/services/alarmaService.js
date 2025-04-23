const API_BASE_URL = '/api/v1/alarmas';

// Obtener todas las alarmas
export const fetchAlarmas = async () => {
  const response = await fetch(`${API_BASE_URL}`);
  if (!response.ok) {
    throw new Error('Error al obtener las alarmas');
  }
  return response.json();
};

// Obtener una alarma por ID
export const fetchAlarmaPorId = async (idAlarma) => {
  const response = await fetch(`${API_BASE_URL}/${idAlarma}`);
  if (!response.ok) {
    throw new Error('Error al obtener la alarma');
  }
  return response.json();
};

// Registrar una nueva alarma
export const registrarAlarma = async (alarma) => {
  const response = await fetch(`${API_BASE_URL}/registrar`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(alarma),
  });
  if (!response.ok) {
    throw new Error('Error al registrar la alarma');
  }
  return response.json();
};

// Editar una alarma existente
export const editarAlarma = async (alarma) => {
  const response = await fetch(`${API_BASE_URL}/editar`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(alarma),
  });
  if (!response.ok) {
    throw new Error('Error al actualizar la alarma');
  }
  return response.json();
};

// Eliminar una alarma
export const eliminarAlarma = async (idAlarma) => {
  const response = await fetch(`${API_BASE_URL}/eliminar/${idAlarma}`, {
    method: 'DELETE',
  });
  if (!response.ok) {
    throw new Error('Error al eliminar la alarma');
  }
  return response.json();
};

// Cambiar estado de una alarma
export const toggleEstadoAlarma = async (idAlarma, activo) => {
  // Primero, obtener todos los datos actuales de la alarma
  const alarmaActual = await fetchAlarmaPorId(idAlarma);
  
  // Crear objeto con todos los datos existentes, actualizando solo el estado
  const alarmaActualizada = {
    ...alarmaActual,
    activo: activo
  };
  
  // Enviar la alarma completa para actualizar solo el estado
  const response = await fetch(`${API_BASE_URL}/editar`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(alarmaActualizada),
  });
  
  if (!response.ok) {
    throw new Error('Error al cambiar el estado de la alarma');
  }
  
  return response.json();
};