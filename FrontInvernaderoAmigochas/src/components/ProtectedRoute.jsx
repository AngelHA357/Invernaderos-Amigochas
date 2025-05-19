import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const ProtectedRoute = ({ children, requiredRole, requiredResource }) => {
    const { isAuthenticated, userRole, canAccess, loading } = useAuth();

    // Si está cargando, mostrar un indicador de carga
    if (loading) {
        return (
            <div className="flex justify-center items-center h-screen">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-green-500"></div>
            </div>
        );
    }

    // Redirigir al login si no está autenticado
    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    // Verificar acceso por rol específico
    if (requiredRole && userRole !== requiredRole && userRole !== 'ADMIN') {
        return <Navigate to="/acceso-denegado" replace />;
    }

    // Verificar acceso por recurso
    if (requiredResource && !canAccess(requiredResource)) {
        return <Navigate to="/acceso-denegado" replace />;
    }

    // Si pasa todas las verificaciones, mostrar el componente hijo
    return children;
};

export default ProtectedRoute;
