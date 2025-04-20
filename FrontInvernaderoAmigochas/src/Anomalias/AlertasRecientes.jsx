import React, { useState, useEffect } from 'react';
import Alerta from './TarjetaAlerta';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';

function AlertasRecientes() {
  const [fechaInicio, setFechaInicio] = useState('');
  const [fechaFin, setFechaFin] = useState('');
  const [alertasFiltradas, setAlertasFiltradas] = useState([]);

  const alertas = [
    {
      id: "ALT-001",
      invernadero: 'invernadero 3',
      descripcion: 'Temperatura alta',
      tiempo: '5 minutos',
      temperatura: '28°C',
      fecha: '17/04/2025',
      hora: '10:15:32 a.m.',
      umbral: '25°C',
      sensorId: 'SEN-0105',
      sensorMarca: 'Stercn',
      sensorModelo: 'LM35',
      ultimaCalibracion: '24/08/2024',
      duracion: '3 minutos',
      tipo: 'Temperatura'
    },
    {
      id: "ALT-002",
      invernadero: 'invernadero 1',
      descripcion: 'Humedad baja',
      tiempo: '15 minutos',
      humedad: '50%',
      fecha: '17/04/2025',
      hora: '09:45:10 a.m.',
      umbral: '65%',
      sensorId: 'SEN-0205',
      sensorMarca: 'DFRobot',
      sensorModelo: 'DHT11',
      ultimaCalibracion: '15/10/2024',
      duracion: '15 minutos',
      tipo: 'Humedad'
    },
    {
      id: "ALT-003",
      invernadero: 'invernadero 2',
      descripcion: 'Temperatura alta',
      tiempo: '2 horas',
      temperatura: '27°C',
      fecha: '16/04/2025',
      hora: '04:20:45 p.m.',
      umbral: '24°C',
      sensorId: 'SEN-0108',
      sensorMarca: 'Stercn',
      sensorModelo: 'LM35',
      ultimaCalibracion: '20/09/2024',
      duracion: '45 minutos',
      tipo: 'Temperatura'
    },
    {
      id: "ALT-004",
      invernadero: 'invernadero 4',
      descripcion: 'CO2 elevado',
      tiempo: '30 minutos',
      co2: '1200 ppm',
      fecha: '16/04/2025',
      hora: '02:30:15 p.m.',
      umbral: '1000 ppm',
      sensorId: 'SEN-0305',
      sensorMarca: 'Winsen',
      sensorModelo: 'MH-Z19',
      ultimaCalibracion: '05/01/2025',
      duracion: '30 minutos',
      tipo: 'CO2'
    },
    {
      id: "ALT-005",
      invernadero: 'invernadero 3',
      descripcion: 'Humedad alta',
      tiempo: '10 minutos',
      humedad: '85%',
      fecha: '16/04/2025',
      hora: '11:10:22 a.m.',
      umbral: '80%',
      sensorId: 'SEN-0202',
      sensorMarca: 'Aosong',
      sensorModelo: 'DHT22',
      ultimaCalibracion: '30/11/2024',
      duracion: '10 minutos',
      tipo: 'Humedad'
    },
    {
      id: "ALT-006",
      invernadero: 'invernadero 2',
      descripcion: 'CO2 bajo',
      tiempo: '3 horas',
      co2: '250 ppm',
      fecha: '15/04/2025',
      hora: '08:45:30 a.m.',
      umbral: '350 ppm',
      sensorId: 'SEN-0302',
      sensorMarca: 'Winsen',
      sensorModelo: 'MH-Z19',
      ultimaCalibracion: '10/12/2024',
      duracion: '3 horas',
      tipo: 'CO2'
    },
  ];

  useEffect(() => {
    setAlertasFiltradas(alertas);
    
    const today = new Date();
    const formattedDate = today.toISOString().slice(0, 10);
    setFechaFin(formattedDate);
    
    const weekAgo = new Date();
    weekAgo.setDate(today.getDate() - 7);
    setFechaInicio(weekAgo.toISOString().slice(0, 10));
  }, []);

  return (
    <>
    <BarraNavegacion />
    <div className="flex justify-center bg-gray-50 min-h-screen">
      <div className="max-w-3xl w-full p-6">
        <h1 className="text-2xl font-bold text-gray-800 mb-1">Alertas recientes</h1>
        <p className="text-sm text-gray-500 mb-6">Últimas alertas detectadas por el sistema</p>

        {/* Barra de fechas (sin funcionalidad de filtro por ahora) */}
        <div className="bg-white p-4 rounded-lg shadow-sm mb-6 border-l-4 border-green-500">
          {/* Selector de fechas */}
          <div className="flex flex-wrap gap-3 mb-4">
            <div className="flex items-center">
              <span className="mr-2 text-gray-700">Desde:</span>
              <input 
                type="date" 
                className="px-3 py-1 border border-green-200 rounded-md text-gray-700 focus:outline-none focus:ring-2 focus:ring-green-300 focus:border-green-300"
                value={fechaInicio}
                onChange={(e) => setFechaInicio(e.target.value)}
              />
            </div>
            <div className="flex items-center">
              <span className="mr-2 text-gray-700">Hasta:</span>
              <input 
                type="date" 
                className="px-3 py-1 border border-green-200 rounded-md text-gray-700 focus:outline-none focus:ring-2 focus:ring-green-300 focus:border-green-300"
                value={fechaFin}
                onChange={(e) => setFechaFin(e.target.value)}
              />
            </div>
          </div>
        </div>

        {/* Lista de alertas */}
        <div className="bg-white rounded-lg shadow-sm border border-green-100">
          {alertasFiltradas.length > 0 ? (
            alertasFiltradas.map((alerta, index) => (
              <Alerta
                key={index}
                id={alerta.id}
                invernadero={alerta.invernadero}
                descripcion={alerta.descripcion}
                tiempo={alerta.tiempo}
              />
            ))
          ) : (
            <div className="p-6 text-center text-gray-500">
              No se encontraron alertas
            </div>
          )}
        </div>
      </div>
    </div>
    </>
  );
}

export default AlertasRecientes;