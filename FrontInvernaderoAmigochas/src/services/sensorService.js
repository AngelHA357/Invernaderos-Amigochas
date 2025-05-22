const API_BASE_URL = '/api/v1/gestionSensores';


export const obtenerInvernaderos = async () => {
  // Verificar si hay un token en localStorage
  const authToken = localStorage.getItem('authToken');
  
  try {
    let response;
    
    if (authToken) {
      // Si hay token, hacer petición autenticada
      response = await fetch(`http://localhost:8080${API_BASE_URL}/invernaderos`, {
        headers: {
          'Authorization': `Bearer ${authToken}`,
          'Content-Type': 'application/json',
        }
      });
    } else {
      response = await fetch(`http://localhost:8080${API_BASE_URL}/invernaderos`);
    }
    
    if (!response.ok) {
      if (response.status === 401) {
        localStorage.removeItem('authToken');
        localStorage.removeItem('userRole');
        localStorage.removeItem('username');
        throw new Error('Sesión expirada. Por favor, inicie sesión nuevamente.');
      }
      
      const errorData = await response.json();
      throw new Error(errorData.mensaje || 'Error al obtener invernaderos');
    }
    
    const data = await response.json();

    // Transformar correctamente el formato de la respuesta
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


export const obtenerSensoresPorInvernadero = async (invernaderoId) => {
  try {
    const authToken = localStorage.getItem('authToken');
    const headers = authToken ?
      { 'Authorization': `Bearer ${authToken}`, 'Content-Type': 'application/json' } :
      { 'Content-Type': 'application/json' };


    const url = `http://localhost:8080${API_BASE_URL}/invernadero/${invernaderoId}/sensores`;
    console.log(`Consultando sensores en: ${url}`);

    const response = await fetch(url, { headers });

    if (!response.ok) {
      if (response.status === 401) {
        localStorage.removeItem('authToken');
        localStorage.removeItem('userRole');
        localStorage.removeItem('username');
        throw new Error('Sesión expirada. Por favor, inicie sesión nuevamente.');
      }

      let errorMessage = 'Error al obtener sensores del invernadero';
      try {
        const errorData = await response.json();
        errorMessage = errorData.mensaje || errorMessage;
      } catch (e) {

      }

      throw new Error(errorMessage);
    }
    
    const data = await response.json();
    
    if (!data || !Array.isArray(data)) {
      throw new Error('Formato de respuesta inesperado al obtener sensores');
    }

    // Transformar formato de respuesta para frontend según la estructura del DTO en el backend
    return data.map(sensor => ({
      id: sensor.idSensor,
      invernaderoId: sensor.idInvernadero,
      type: sensor.tipoSensor,
      status: sensor.estado === true || sensor.estado === 'true' ? 'Activo' : 'Inactivo',
      marca: sensor.marca,
      modelo: sensor.modelo,
      macAddress: sensor.macAddress,
      magnitud: sensor.magnitud, // Asegurarnos de usar el campo correcto para la magnitud
      unidad: sensor.unidad,     // Añadir explícitamente el campo unidad
      sector: sensor.sector,
      fila: sensor.fila,
      _id: sensor._id
    }));
  } catch (error) {
    console.error(`Error al obtener sensores para el invernadero ${invernaderoId}:`, error);
    throw error;
  }
};


export const obtenerSensorPorId = async (sensorId) => {
  try {
    const authToken = localStorage.getItem('authToken');
    const headers = authToken ?
      { 'Authorization': `Bearer ${authToken}`, 'Content-Type': 'application/json' } :
      { 'Content-Type': 'application/json' };

    const response = await fetch(`http://localhost:8080${API_BASE_URL}/${sensorId}`, {
      headers
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.mensaje || 'Error al obtener sensor');
    }
    
    const sensor = await response.json();
    

    return {
      id: sensor.idSensor,
      invernaderoId: sensor.idInvernadero,
      type: sensor.tipoSensor,
      status: sensor.estado ? 'Activo' : 'Inactivo',
      marca: sensor.marca,
      modelo: sensor.modelo,
      macAddress: sensor.macAddress,
      magnitud: sensor.magnitud,
      unidad: sensor.unidad,    // Añadir el campo unidad explícitamente
      sector: sensor.sector,
      fila: sensor.fila,
      _id: sensor._id
    };
  } catch (error) {
    console.error(`Error al obtener sensor ${sensorId}:`, error);
    throw error;
  }
};


export const registrarSensor = async (sensorData) => {
  try {
    const authToken = localStorage.getItem('authToken');

    // Transformar datos para el backend
    const sensorPayload = {
      idSensor: sensorData.idSensor || sensorData.id,
      macAddress: sensorData.macAddress,
      marca: sensorData.marca,
      modelo: sensorData.modelo,
      tipoSensor: sensorData.tipoSensor || sensorData.type,
      magnitud: sensorData.magnitud,
      unidad: sensorData.unidad,    // Añadir el campo unidad explícitamente
      idInvernadero: sensorData.idInvernadero || sensorData.invernaderoId,
      sector: sensorData.sector,
      fila: sensorData.fila,
      estado: sensorData.estado !== undefined ? sensorData.estado : (sensorData.status === 'Activo')
    };
    
    console.log('Enviando al backend (POST):', sensorPayload);
    
    const response = await fetch(`http://localhost:8080${API_BASE_URL}/registrar`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(authToken && { 'Authorization': `Bearer ${authToken}` })
      },
      body: JSON.stringify(sensorPayload)
    });
    
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.mensaje || 'Error al registrar sensor');
    }
    
    const sensor = await response.json();
    
    // Transformar respuesta para frontend
    return {
      id: sensor.idSensor,
      invernaderoId: sensor.idInvernadero,
      type: sensor.tipoSensor,
      status: sensor.estado ? 'Activo' : 'Inactivo',
      marca: sensor.marca,
      modelo: sensor.modelo,
      macAddress: sensor.macAddress,
      magnitud: sensor.magnitud,
      unidad: sensor.unidad,    // Añadir el campo unidad explícitamente
      sector: sensor.sector,
      fila: sensor.fila,
      _id: sensor._id
    };
  } catch (error) {
    console.error('Error al registrar sensor:', error);
    throw error;
  }
};

export const editarSensor = async (sensorData) => {
  try {
    const authToken = localStorage.getItem('authToken');

    // Transformar datos para backend
    const sensorPayload = {
      _id: sensorData._id, // ID de MongoDB (requerido para la actualización)
      idSensor: sensorData.idSensor || sensorData.id, // Aceptar ambos formatos
      macAddress: sensorData.macAddress,
      marca: sensorData.marca,
      modelo: sensorData.modelo,
      tipoSensor: sensorData.tipoSensor || sensorData.type, // Aceptar ambos formatos
      magnitud: sensorData.magnitud,
      unidad: sensorData.unidad,    // Añadir el campo unidad explícitamente
      idInvernadero: sensorData.idInvernadero || sensorData.invernaderoId, // Aceptar ambos formatos
      sector: sensorData.sector,
      fila: sensorData.fila,
      estado: sensorData.estado !== undefined ? sensorData.estado : (sensorData.status === 'Activo')
    };
    
    console.log('Enviando al backend (PUT):', sensorPayload);
    
    const response = await fetch(`http://localhost:8080${API_BASE_URL}/editar`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...(authToken && { 'Authorization': `Bearer ${authToken}` })
      },
      body: JSON.stringify(sensorPayload)
    });
    
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.mensaje || 'Error al editar sensor');
    }
    
    const sensor = await response.json();
    
    // Transformar respuesta para frontend
    return {
      id: sensor.idSensor,
      invernaderoId: sensor.idInvernadero,
      type: sensor.tipoSensor,
      status: sensor.estado ? 'Activo' : 'Inactivo',
      marca: sensor.marca,
      modelo: sensor.modelo,
      macAddress: sensor.macAddress,
      magnitud: sensor.magnitud,
      unidad: sensor.unidad,    // Añadir el campo unidad explícitamente
      sector: sensor.sector,
      fila: sensor.fila,
      _id: sensor._id
    };
  } catch (error) {
    console.error('Error al editar sensor:', error);
    throw error;
  }
};


export const eliminarSensor = async (sensorId) => {
  try {
    const authToken = localStorage.getItem('authToken');

    const response = await fetch(`http://localhost:8080${API_BASE_URL}/eliminar/${sensorId}`, {
      method: 'DELETE',
      headers: {
        ...(authToken && { 'Authorization': `Bearer ${authToken}` })
      }
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

