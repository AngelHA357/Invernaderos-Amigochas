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
 * Verifica si una anomalía ya tiene un reporte asociado
 * @param {string} anomaliaId - ID de la anomalía
 * @returns {Promise<boolean>} Promesa que resuelve a true si existe un reporte, false en caso contrario
 */
export const verificarReporteExistente = async (anomaliaId) => {
    try {
        // Llamada al endpoint para verificar si existe reporte
        const response = await axios.get(`${API_URL}/verificar/${anomaliaId}`, {
            headers: getAuthHeaders(),
        });
        return response.data; // Se espera que el backend retorne un boolean
    } catch (error) {
        console.error('Error al verificar si existe reporte:', error);
        // Si falla la llamada, asumimos que no hay reporte o retornamos false
        return false;
    }
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
 * Obtiene un reporte existente para una anomalía específica
 * @param {string} anomaliaId - ID de la anomalía
 * @returns {Promise<Object>} Promesa con los datos del reporte
 */
export const obtenerReporteDeAnomalia = async (anomaliaId) => {
    try {
        // Llamada al endpoint para obtener el reporte asociado a la anomalía
        const response = await axios.get(`${API_URL}/reporte-de-anomalia/${anomaliaId}`, {
            headers: getAuthHeaders(),
        });
        return response.data;
    } catch (error) {
        console.error('Error al obtener reporte de anomalía:', error);
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

