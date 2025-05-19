import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function AccesoDenegado() {
    const { username, userRole } = useAuth();

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <div className="max-w-md w-full bg-white p-8 rounded-lg shadow-xl">
                <div className="flex flex-col items-center">
                    <div className="rounded-full bg-red-100 p-6 mb-4">
                        <svg className="w-16 h-16 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path>
                        </svg>
                    </div>
                    <h1 className="text-2xl font-bold text-red-600 mb-2">Acceso Denegado</h1>
                    <p className="text-gray-600 text-center mb-6">
                        Lo sentimos, {username ? username : 'usuario'}, no tienes los permisos necesarios para acceder a esta sección.
                    </p>
                    {userRole && (
                        <p className="text-sm text-gray-500 mb-6">
                            Tu rol actual es: <span className="font-semibold">{userRole}</span>
                        </p>
                    )}
                    <div className="flex space-x-4">
                        <Link
                            to="/"
                            className="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 transition-colors"
                        >
                            Ir al Inicio
                        </Link>
                        <Link
                            to="/invernaderos"
                            className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors"
                        >
                            Ir a Invernaderos
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default AccesoDenegado;
