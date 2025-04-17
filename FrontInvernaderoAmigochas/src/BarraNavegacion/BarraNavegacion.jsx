import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import logo from '../logo/logo.png'; 

function BarraNavegacion() {
  const location = useLocation();
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
  
  return (
    <nav className="bg-zinc-800 text-white flex items-center justify-between p-4 shadow-lg">
      <div className="flex items-center">
        <Link to="/" className="flex items-center">
          <img
            src={logo}
            alt="Logo Invernaderos Amigochas"
            className="h-12 w-auto mr-3"
          />
        </Link>
      </div>
      
      <div className="flex items-center space-x-2">
        <Link to="/" className={navLinkClass("/")}>
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
      </div>
    </nav>
  );
}

export default BarraNavegacion;