import React, { createContext, useState, useContext, useEffect } from 'react';

// Crear el contexto de autenticación
const AuthContext = createContext();

// Hook personalizado para usar el contexto de autenticación
export const useAuth = () => {
    return useContext(AuthContext);
};

// Proveedor del contexto de autenticación
export const AuthProvider = ({ children }) => {
    const [authToken, setAuthToken] = useState(localStorage.getItem('authToken'));
    const [userRole, setUserRole] = useState(localStorage.getItem('userRole'));
    const [username, setUsername] = useState(localStorage.getItem('username'));
    const [isAuthenticated, setIsAuthenticated] = useState(!!authToken);
    const [loading, setLoading] = useState(true);

    // Función para iniciar sesión
    const login = (token) => {
        localStorage.setItem('authToken', token);
        setAuthToken(token);
        setIsAuthenticated(true);
        
        // Decodificar el token para obtener el rol y el nombre de usuario
        try {
            const tokenParts = token.split('.');
            if (tokenParts.length === 3) {
                const payload = JSON.parse(atob(tokenParts[1]));
                
                // Guardar el rol del usuario si existe
                if (payload.role) {
                    localStorage.setItem('userRole', payload.role);
                    setUserRole(payload.role);
                }
                
                // Guardar el nombre de usuario
                if (payload.sub) {
                    localStorage.setItem('username', payload.sub);
                    setUsername(payload.sub);
                }
            }
        } catch (error) {
            console.error('Error al decodificar el token:', error);
        }
    };

    // Función para cerrar sesión
    const logout = () => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('userRole');
        localStorage.removeItem('username');
        setAuthToken(null);
        setUserRole(null);
        setUsername(null);
        setIsAuthenticated(false);
    };

    // Función para verificar si el usuario tiene un rol específico
    const hasRole = (requiredRole) => {
        return userRole === requiredRole;
    };

    // Función para verificar si el usuario tiene acceso a un recurso específico
    const canAccess = (resource) => {
        // Mapeo de recursos a roles autorizados
        const rolePermissions = {
            'gestionSensores': ['ADMIN', 'OPERATOR'],
            'alarmas': ['ADMIN', 'OPERATOR'],
            'informes': ['ADMIN', 'ANALYST'],
            'anomalias': ['ADMIN', 'ANALYST']
        };

        // Si el usuario es ADMIN, tiene acceso a todo
        if (userRole === 'ADMIN') return true;
        
        // Verificar si el recurso existe y si el rol del usuario está permitido
        return rolePermissions[resource] && rolePermissions[resource].includes(userRole);
    };

    // Comprobar si el token está expirado
    const isTokenExpired = (token) => {
        if (!token) return true;
        
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            const expirationTime = payload.exp * 1000; // Convertir a milisegundos
            return Date.now() >= expirationTime;
        } catch (error) {
            console.error('Error al verificar la expiración del token:', error);
            return true; // Si hay error, considerar expirado
        }
    };

    // Verificar la autenticación al cargar el componente
    useEffect(() => {
        const checkAuth = () => {
            const token = localStorage.getItem('authToken');
            const role = localStorage.getItem('userRole');
            const user = localStorage.getItem('username');
            
            if (token && !isTokenExpired(token)) {
                setAuthToken(token);
                setUserRole(role);
                setUsername(user);
                setIsAuthenticated(true);
            } else {
                // Si el token está expirado, limpiar todo
                localStorage.removeItem('authToken');
                localStorage.removeItem('userRole');
                localStorage.removeItem('username');
                setAuthToken(null);
                setUserRole(null);
                setUsername(null);
                setIsAuthenticated(false);
            }
            setLoading(false);
        };

        checkAuth();
    }, []);

    // Valores que se proporcionarán a través del contexto
    const value = {
        authToken,
        userRole,
        username,
        isAuthenticated,
        loading,
        login,
        logout,
        hasRole,
        canAccess,
    };

    return (
        <AuthContext.Provider value={value}>
            {!loading && children}
        </AuthContext.Provider>
    );
};
