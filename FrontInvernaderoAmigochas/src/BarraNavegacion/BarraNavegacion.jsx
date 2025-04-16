import React from 'react';
import { Link } from 'react-router-dom';
import logo from '../logo/logo.png'; 

function BarraNavegacion() {
  return (
    <nav className="bg-zinc-800 text-white flex items-center justify-between p-4">
      <div>
        <img
          src={logo}
          alt="Logo Invernaderos Amigochas"
          className="h-10 w-auto"
        />
      </div>
      <div className="flex items-center space-x-8">
        <Link to="/" className="cursor-pointer text-base hover:text-gray-300">
          Sensores
        </Link>
        <Link to="/informes" className="cursor-pointer text-base hover:text-gray-300">
          Informes
        </Link>
        <Link to="/alarmas" className="cursor-pointer text-base hover:text-gray-300">
          Alarmas
        </Link>
        <Link to="/anomalias" className="cursor-pointer text-base hover:text-gray-300">
          Anomalías
        </Link>
      </div>
    </nav>
  );
}

export default BarraNavegacion;