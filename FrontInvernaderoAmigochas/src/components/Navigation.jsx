import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Navigation() {
    const { isAuthenticated, username, userRole, canAccess, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    if (!isAuthenticated) {
        return null;
    }

    return (
        <nav className="bg-green-700 text-white shadow-md">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex justify-between h-16">
                    <div className="flex">
                        <div className="flex-shrink-0 flex items-center">
                            <Link to="/invernaderos" className="text-xl font-bold">
                                Invernaderos Amigochas
                            </Link>
                        </div>
                        <div className="ml-10 flex items-center space-x-4">
                            <Link 
                                to="/invernaderos" 
                                className="px-3 py-2 rounded-md text-sm font-medium hover:bg-green-600"
                            >
                                Invernaderos
                            </Link>
                            
                            {canAccess('gestionSensores') && (
                                <div className="relative group">
                                    <button className="px-3 py-2 rounded-md text-sm font-medium hover:bg-green-600">
                                        Gestión de Sensores
                                    </button>
                                    <div className="absolute left-0 mt-2 w-48 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 hidden group-hover:block z-10">
                                        <div className="py-1">
                                            <Link 
                                                to="/sensores/agregar" 
                                                className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                                            >
                                                Agregar Sensor
                                            </Link>
                                        </div>
                                    </div>
                                </div>
                            )}
                            
                            {canAccess('alarmas') && (
                                <Link 
                                    to="/alarmas" 
                                    className="px-3 py-2 rounded-md text-sm font-medium hover:bg-green-600"
                                >
                                    Gestión de Alarmas
                                </Link>
                            )}
                            
                            {canAccess('informes') && (
                                <Link 
                                    to="/informes" 
                                    className="px-3 py-2 rounded-md text-sm font-medium hover:bg-green-600"
                                >
                                    Informes
                                </Link>
                            )}
                            
                            {canAccess('anomalias') && (
                                <Link 
                                    to="/anomalias" 
                                    className="px-3 py-2 rounded-md text-sm font-medium hover:bg-green-600"
                                >
                                    Anomalías
                                </Link>
                            )}
                        </div>
                    </div>
                    <div className="flex items-center">
                        <div className="flex items-center">
                            <span className="text-sm mr-4">
                                {username} ({userRole})
                            </span>
                            <button
                                onClick={handleLogout}
                                className="px-3 py-1 border border-white rounded-md text-sm font-medium hover:bg-red-600 transition-colors"
                            >
                                Cerrar Sesión
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </nav>
    );
}

export default Navigation;
