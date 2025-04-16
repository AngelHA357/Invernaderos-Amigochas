import React from 'react';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';

function ListaInvernaderos() {
  const invernaderos = [
    { id: 'INV-0101', name: 'Invernadero 1', location: 'Sector 1, Fila 1', status: 'Activo' },
    { id: 'INV-0201', name: 'Invernadero 2', location: 'Sector 1, Fila 2', status: 'Activo' },
    { id: 'INV-0301', name: 'Invernadero 3', location: 'Sector 1, Fila 3', status: 'Activo' },
    { id: 'INV-0401', name: 'Invernadero 4', location: 'Sector 1, Fila 4', status: 'Activo' },
    { id: 'INV-0501', name: 'Invernadero 5', location: 'Sector 1, Fila 5', status: 'Activo' },
  ];


  return (
    <>
      <BarraNavegacion />
      <div className="p-6 bg-gray-100 min-h-screen">
        <div className="max-w-4xl mx-auto bg-white rounded-lg shadow-md p-6">
          {/* Título */}
          <h1 className="text-2xl font-bold text-gray-800">Gestión de Sensores - Invernaderos</h1>

          {/* Lista de invernaderos */}
          <div className="mt-6">
            {/* Encabezados */}
            <div className="grid grid-cols-5 gap-4 bg-gray-200 text-gray-700 py-3 px-4 rounded-t-lg font-semibold">
              <div>ID</div>
              <div>Invernadero</div>
              <div>Localización</div>
              <div>Estado</div>
              <div>Acciones</div>
            </div>

            {/* Filas */}
            {invernaderos.map((invernadero, index) => (
              <div
                key={invernadero.id}
                className={`grid grid-cols-5 gap-4 py-3 px-4 border-b ${index === invernaderos.length - 1 ? 'border-b-0' : ''
                  }`}
              >
                <div className="text-gray-800">{invernadero.id}</div>
                <div className="text-gray-800">{invernadero.name}</div>
                <div className="text-gray-600">{invernadero.location}</div>
                <div>
                  <span className="inline-block px-3 py-1 text-sm font-semibold text-green-800 bg-green-100 rounded-full">
                    {invernadero.status}
                  </span>
                </div>
                <div>
                  <button className="text-blue-500 hover:underline">Ver sensores</button>
                </div>
              </div>
            ))}
          </div>

          {/* Paginación */}
          <div className="mt-4 flex justify-between items-center">
            <p className="text-gray-500">Mostrando {invernaderos.length} de {invernaderos.length} invernaderos</p>
            <div className="space-x-2">
              <button className="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
                Anterior
              </button>
              <button className="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
                Siguiente
              </button>
            </div>
          </div>
        </div>
      </div>

    </>
  );
}

export default ListaInvernaderos;