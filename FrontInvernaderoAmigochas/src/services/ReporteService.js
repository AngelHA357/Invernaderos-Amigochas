
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

export const verificarReporteExistente = async (anomaliaId) => {
    try {
        const response = await axios.get(`${API_URL}/verificar/${anomaliaId}`, {
            headers: getAuthHeaders(),
        });
        return response.data;
    } catch (error) {
        console.error('Error al verificar si existe reporte:', error);
        return false;
    }
};


export const obtenerDetallesAnomalia = async (anomaliaId) => {
    try {
        const response = await axios.get(`${API_URL}/${anomaliaId}`, {
            headers: getAuthHeaders(),
        });
        return response.data;
    } catch (error) {
        console.error('Error al obtener detalles de anomalía:', error);
        throw error;
    }
};


export const obtenerReporteDeAnomalia = async (anomaliaId) => {
    try {
        const response = await axios.get(`${API_URL}/reporte-de-anomalia/${anomaliaId}`, {
            headers: getAuthHeaders(),
        });
        return response.data;
    } catch (error) {
        console.error('Error al obtener reporte de anomalía:', error);
        throw error;
    }
};


export const enviarReporte = async (reporte) => {
    try {
        const reporteFormateado = {
            fecha: new Date(),
            acciones: reporte.acciones,
            comentarios: reporte.notas,
            anomalia: reporte.anomalia,
            usuario: reporte.usuario
        };

        const response = await axios.post(`${API_URL}/registrarReporte`, reporteFormateado, {
            headers: getAuthHeaders(),
        });
        return response.data;
    } catch (error) {
        console.error('Error al enviar reporte:', error);
        throw error;
    }
};

