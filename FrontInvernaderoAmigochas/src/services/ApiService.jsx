import { useAuth } from '../context/AuthContext';

// API Service para realizar peticiones autenticadas
export const useApiService = () => {
    const { authToken, logout } = useAuth();
    const baseUrl = 'http://localhost:8080';

    // Función para realizar peticiones GET autenticadas
    const get = async (endpoint) => {
        try {
            const response = await fetch(`${baseUrl}${endpoint}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${authToken}`,
                    'Content-Type': 'application/json',
                },
            });

            if (response.status === 401) {
                // Token expirado o inválido
                logout();
                throw new Error('Sesión expirada. Por favor, inicie sesión nuevamente.');
            }

            if (!response.ok) {
                let errorMsg = `Error ${response.status}`;
                try {
                    const errorData = await response.json();
                    errorMsg = errorData.mensaje || errorMsg;
                } catch (jsonError) {
                    // Si no es JSON, intentar obtener el texto del error
                    try {
                        const textError = await response.text();
                        errorMsg = textError || errorMsg;
                    } catch (textErrorErr) {
                        errorMsg = `Error ${response.status} al procesar la respuesta.`;
                    }
                }
                throw new Error(errorMsg);
            }

            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            }
            
            return await response.text();
        } catch (error) {
            console.error(`Error en petición GET a ${endpoint}:`, error);
            throw error;
        }
    };

    // Función para realizar peticiones POST autenticadas
    const post = async (endpoint, data) => {
        try {
            const response = await fetch(`${baseUrl}${endpoint}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${authToken}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            });

            if (response.status === 401) {
                // Token expirado o inválido
                logout();
                throw new Error('Sesión expirada. Por favor, inicie sesión nuevamente.');
            }

            if (!response.ok) {
                let errorMsg = `Error ${response.status}`;
                try {
                    const errorData = await response.json();
                    errorMsg = errorData.mensaje || errorMsg;
                } catch (jsonError) {
                    // Si no es JSON, intentar obtener el texto del error
                    try {
                        const textError = await response.text();
                        errorMsg = textError || errorMsg;
                    } catch (textErrorErr) {
                        errorMsg = `Error ${response.status} al procesar la respuesta.`;
                    }
                }
                throw new Error(errorMsg);
            }

            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            }
            
            return await response.text();
        } catch (error) {
            console.error(`Error en petición POST a ${endpoint}:`, error);
            throw error;
        }
    };

    // Función para realizar peticiones PUT autenticadas
    const put = async (endpoint, data) => {
        try {
            const response = await fetch(`${baseUrl}${endpoint}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${authToken}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            });

            if (response.status === 401) {
                // Token expirado o inválido
                logout();
                throw new Error('Sesión expirada. Por favor, inicie sesión nuevamente.');
            }

            if (!response.ok) {
                let errorMsg = `Error ${response.status}`;
                try {
                    const errorData = await response.json();
                    errorMsg = errorData.mensaje || errorMsg;
                } catch (jsonError) {
                    // Si no es JSON, intentar obtener el texto del error
                    try {
                        const textError = await response.text();
                        errorMsg = textError || errorMsg;
                    } catch (textErrorErr) {
                        errorMsg = `Error ${response.status} al procesar la respuesta.`;
                    }
                }
                throw new Error(errorMsg);
            }

            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            }
            
            return await response.text();
        } catch (error) {
            console.error(`Error en petición PUT a ${endpoint}:`, error);
            throw error;
        }
    };

    // Función para realizar peticiones DELETE autenticadas
    const del = async (endpoint) => {
        try {
            const response = await fetch(`${baseUrl}${endpoint}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${authToken}`,
                    'Content-Type': 'application/json',
                },
            });

            if (response.status === 401) {
                // Token expirado o inválido
                logout();
                throw new Error('Sesión expirada. Por favor, inicie sesión nuevamente.');
            }

            if (!response.ok) {
                let errorMsg = `Error ${response.status}`;
                try {
                    const errorData = await response.json();
                    errorMsg = errorData.mensaje || errorMsg;
                } catch (jsonError) {
                    // Si no es JSON, intentar obtener el texto del error
                    try {
                        const textError = await response.text();
                        errorMsg = textError || errorMsg;
                    } catch (textErrorErr) {
                        errorMsg = `Error ${response.status} al procesar la respuesta.`;
                    }
                }
                throw new Error(errorMsg);
            }

            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            }
            
            return await response.text();
        } catch (error) {
            console.error(`Error en petición DELETE a ${endpoint}:`, error);
            throw error;
        }
    };

    return {
        get,
        post,
        put,
        delete: del
    };
};
