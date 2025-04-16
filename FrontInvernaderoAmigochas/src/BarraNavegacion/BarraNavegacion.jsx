import React, { useState } from 'react';
import logo from '../logo/logo.png'; 

function BarraNavegacion() {
  const [activeItem, setActiveItem] = useState('Sensores');

  const handleMenuClick = (item) => {
    setActiveItem(item);
  };

  const menuItems = ['Alarmas', 'Sensores', 'Informes', 'Anomalías'];

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
        {menuItems.map((item) => (
          <div
            key={item}
            onClick={() => handleMenuClick(item)}
            className={`cursor-pointer text-base ${
              activeItem === item
                ? 'bg-green-500 rounded-full px-4 py-2 font-semibold'
                : 'hover:text-gray-300'
            }`}
          >
            {item}
          </div>
        ))}
      </div>
    </nav>
  );
}

export default BarraNavegacion;