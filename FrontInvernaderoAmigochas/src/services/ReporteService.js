/**
 * Servicio para manejar las operaciones relacionadas con reportes de anomalías
 */
import axios from 'axios';

// URL base para el microservicio de reportes de anomalías
const API_URL = 'http://localhost:8080/api/v1/reportesAnomalias';

// Helper para obtener los headers de autenticación
const getAuthHeaders = () => {
    const token = localStorage.getItem('authToken');
    return {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
    };
};

/**
 * Obtiene los detalles de una anomalía por su ID
 * @param {string} anomaliaId - ID de la anomalía
 * @returns {Promise} Promesa con los datos de la anomalía
 */
export const obtenerDetallesAnomalia = async (anomaliaId) => {
    try {
        // Usando el endpoint correcto para obtener una anomalía por ID
        const response = await axios.get(`${API_URL}/${anomaliaId}`, {
            headers: getAuthHeaders(),
        });
        return response.data;
    } catch (error) {
        console.error('Error al obtener detalles de anomalía:', error);
        throw error;
    }
};

/**
 * Envía un reporte de anomalía al servidor
 * @param {Object} reporte - Datos del reporte
 * @returns {Promise} Promesa con la respuesta del servidor
 */
export const enviarReporte = async (reporte) => {
    try {
        // Formato correcto según el backend:
        const reporteFormateado = {
            fecha: new Date(),
            acciones: reporte.acciones,
            comentarios: reporte.notas,
            anomalia: reporte.anomalia, // Debe ser un objeto AnomaliaDTO completo
            usuario: reporte.usuario
        };

        // Endpoint correcto para registrar reporte
        const response = await axios.post(`${API_URL}/registrarReporte`, reporteFormateado, {
            headers: getAuthHeaders(),
        });
        return response.data;
    } catch (error) {
        console.error('Error al enviar reporte:', error);
        throw error;
    }
};

