import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { FaLeaf } from 'react-icons/fa';
import { useAuth } from '../context/AuthContext';

function BarraNavegacion() {
  const location = useLocation();
  const navigate = useNavigate();
  const path = location.pathname;
  const { isAuthenticated, username, userRole, canAccess, logout } = useAuth();
  
  const isActive = (route) => {
    if (route === "/" && path === "/") return true;
    if (route !== "/" && path.startsWith(route)) return true;
    return false;
  };
  
  const navLinkClass = (route) => {
    return `px-4 py-2 rounded-md transition-all duration-200 cursor-pointer text-base font-medium ${
      isActive(route) 
        ? 'bg-green-200 text-zinc-800 font-semibold' 
        : 'hover:bg-zinc-700 text-white'
    }`;
  };

  const handleLogout = () => {
    logout(); // Usar la función de logout del contexto de autenticación
    console.log('Sesión cerrada');
    navigate('/');
  };
    // Si no está autenticado, no mostrar la barra de navegación
  if (!isAuthenticated) {
    return null;
  }

  return (
    <nav className="bg-zinc-800 text-white flex items-center justify-between p-4 shadow-lg">
      <div className="flex items-center">
        <Link to="/invernaderos" className="flex items-center">
          <FaLeaf className="h-12 w-12 mr-3 text-green-400" />
          <span className="text-xl font-semibold">Invernaderos Amigochas</span>
        </Link>
      </div>
      
      <div className="flex items-center space-x-2">
        {/* Siempre mostramos el enlace a Invernaderos para todos los usuarios */}
        <Link to="/invernaderos" className={navLinkClass("/invernaderos")}>
          Invernaderos
        </Link>
        
        {/* Sensores - Solo visible para ADMIN y OPERATOR */}
        {canAccess('gestionSensores') && (
          <Link to="/sensores" className={navLinkClass("/sensores")}>
            Sensores
          </Link>
        )}
        
        {/* Alarmas - Solo visible para ADMIN y OPERATOR */}
        {canAccess('alarmas') && (
          <Link to="/alarmas" className={navLinkClass("/alarmas")}>
            Alarmas
          </Link>
        )}
        
        {/* Informes - Solo visible para ADMIN y ANALYST */}
        {canAccess('informes') && (
          <Link to="/informes" className={navLinkClass("/informes")}>
            Informes
          </Link>
        )}
        
        {/* Anomalías - Solo visible para ADMIN y ANALYST */}
        {canAccess('anomalias') && (
          <Link to="/anomalias" className={navLinkClass("/anomalias")}>
            Anomalías
          </Link>
        )}
        
        {/* Mostrar información del usuario y rol */}
        <span className="ml-2 px-3 py-1 bg-zinc-700 rounded-md text-sm">
          {username} ({userRole})
        </span>
        
        <button
          onClick={handleLogout}
          className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-all duration-200 font-medium"
        >
          Cerrar Sesión
        </button>
      </div>
    </nav>
  );
}

export default BarraNavegacion;