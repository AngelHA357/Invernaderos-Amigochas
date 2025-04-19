import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import logo from '../recursos/logo.png'; 

function BarraNavegacion() {
  const location = useLocation();
  const navigate = useNavigate();
  const path = location.pathname;
  
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
    console.log('Sesión cerrada');
    navigate('/');
  };
  
  return (
    <nav className="bg-zinc-800 text-white flex items-center justify-between p-4 shadow-lg">
      <div className="flex items-center">
        <Link to="/invernaderos" className="flex items-center">
          <img
            src={logo}
            alt="Logo Invernaderos Amigochas"
            className="h-12 w-auto mr-3"
          />
        </Link>
      </div>
      
      <div className="flex items-center space-x-2">
        <Link to="/invernaderos" className={navLinkClass("/invernaderos")}>
          Sensores
        </Link>
        <Link to="/informes" className={navLinkClass("/informes")}>
          Informes
        </Link>
        <Link to="/alarmas" className={navLinkClass("/alarmas")}>
          Alarmas
        </Link>
        <Link to="/anomalias" className={navLinkClass("/anomalias")}>
          Anomalías
        </Link>
        <button
          onClick={handleLogout}
          className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-600 transition-all duration-200 font-medium"
        >
          Cerrar Sesión
        </button>
      </div>
    </nav>
  );
}

export default BarraNavegacion;