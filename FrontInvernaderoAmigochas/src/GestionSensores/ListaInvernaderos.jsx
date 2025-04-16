import React from 'react';
import { useNavigate } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';

function ListaInvernaderos() {
  const navigate = useNavigate();
  const invernaderos = [
    { id: 'INV-0101', name: 'Invernadero 1', location: 'Sector 1, Fila 1'},
    { id: 'INV-0201', name: 'Invernadero 2', location: 'Sector 1, Fila 2'},
    { id: 'INV-0301', name: 'Invernadero 3', location: 'Sector 1, Fila 3'},
    { id: 'INV-0401', name: 'Invernadero 4', location: 'Sector 1, Fila 4'},
    { id: 'INV-0501', name: 'Invernadero 5', location: 'Sector 1, Fila 5'},
  ];

  const handleVerSensores = (invernadero) => {
    sessionStorage.setItem('invernaderoSeleccionado', JSON.stringify(invernadero));
    navigate(`/sensores/${invernadero.id}`);
  };

  return (
    <>
      <BarraNavegacion />
        <div className="max-w-4xl mx-auto rounded-lg shadow-md p-6 mt-10 border border-zinc-600">
          {/* Título */}
          <h1 className="text-2xl font-bold text-gray-800">Gestión de Sensores - Invernaderos</h1>

          {/* Lista de invernaderos */}
          <div className="mt-6">
            {/* Encabezados */}
            <div className="grid grid-cols-4 gap-4 bg-gray-200 text-gray-700 py-3 px-4 rounded-t-lg font-semibold">
              <div>ID</div>
              <div>Invernadero</div>
              <div>Estado</div>
              <div>Acciones</div>
            </div>

            {/* Filas */}
            {invernaderos.map((invernadero, index) => (
              <div
                key={invernadero.id}
                className={`grid grid-cols-4 gap-4 py-3 px-4 border-b ${index === invernaderos.length - 1 ? 'border-b-0' : ''
                  }`}
              >
                <div className="text-gray-800">{invernadero.id}</div>
                <div className="text-gray-800">{invernadero.name}</div>
                <div className="text-gray-600">{invernadero.location}</div>
                <div>
                  <button 
                    onClick={() => handleVerSensores(invernadero)}
                    className="inline-block px-3 py-1 text-sm font-semibold text-green-800 bg-green-100 rounded-full hover:underline">
                    Ver sensores
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

    </>
  );
}

export default ListaInvernaderos;