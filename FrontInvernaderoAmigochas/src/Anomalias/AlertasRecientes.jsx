import React, { useState, useEffect } from 'react';
import Alerta from './TarjetaAlerta';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';
import invernaderosMock from '../mocks/invernaderos.json';
import sensoresMock from '../mocks/sensores.json';

function AlertasRecientes() {
  const [filtroActivo, setFiltroActivo] = useState('Todos');
  const [invernaderoSeleccionado, setInvernaderoSeleccionado] = useState('');
  const [sensorSeleccionado, setSensorSeleccionado] = useState('');
  const [sensoresDisponibles, setSensoresDisponibles] = useState([]);
  const [fechaInicio, setFechaInicio] = useState('');
  const [fechaFin, setFechaFin] = useState('');
  const [alertasFiltradas, setAlertasFiltradas] = useState([]);

  // Lista de tipos de magnitudes disponibles en el sistema
  const magnitudes = ['Todos', 'Temperatura', 'Humedad', 'CO2'];

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

  useEffect(() => {
    filtrarAlertas();
  }, [filtroActivo, invernaderoSeleccionado, sensorSeleccionado]);

  const filtrarAlertas = () => {
    let resultado = [...alertas];
    
    // Filtrar por tipo (Temperatura, Humedad, CO2, etc.)
    if (filtroActivo !== 'Todos') {
      resultado = resultado.filter(alerta => 
        alerta.descripcion.toLowerCase().includes(filtroActivo.toLowerCase())
      );
    }
    
    // Filtrar por invernadero
    if (invernaderoSeleccionado) {
      const invernadero = invernaderosMock.find(inv => inv.id === invernaderoSeleccionado);
      if (invernadero) {
        resultado = resultado.filter(alerta => 
          alerta.invernadero.toLowerCase() === invernadero.name.toLowerCase()
        );
      }
    }
    
    // Filtrar por sensor
    if (sensorSeleccionado && invernaderoSeleccionado) {
      const sensor = sensoresMock.find(s => s.id === sensorSeleccionado);
      if (sensor) {
        // Filtrar alertas según el tipo de sensor seleccionado
        resultado = resultado.filter(alerta => {
          if (sensor.type === 'Temperatura') {
            return alerta.descripcion.toLowerCase().includes('temperatura');
          } else if (sensor.type === 'Humedad') {
            return alerta.descripcion.toLowerCase().includes('humedad');
          } else if (sensor.type === 'CO2') {
            return alerta.descripcion.toLowerCase().includes('co2');
          }
          return false;
        });
      }
    }
    
    setAlertasFiltradas(resultado);
  };

  const handleFiltroChange = (e) => {
    setFiltroActivo(e.target.value);
  };

  const handleInvernaderoChange = (e) => {
    const invId = e.target.value;
    setInvernaderoSeleccionado(invId);
    setSensorSeleccionado('');
    
    // Filtrar sensores según el invernadero seleccionado
    if (invId) {
      const sensoresFiltrados = sensoresMock.filter(sensor => sensor.invernaderoId === invId);
      setSensoresDisponibles(sensoresFiltrados);
    } else {
      setSensoresDisponibles([]);
    }
  };

  return (
    <>
    <BarraNavegacion />
    <div className="flex justify-center bg-gray-50 min-h-screen">
      <div className="max-w-3xl w-full p-6">
        <h1 className="text-2xl font-bold text-gray-800 mb-1">Alertas recientes</h1>
        <p className="text-sm text-gray-500 mb-6">Últimas alertas detectadas por el sistema</p>

        {/* Barra de filtros */}
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

          {/* Combobox para filtrar por magnitudes */}
          <div className="mb-4">
            <label htmlFor="magnitud" className="block text-sm font-medium text-green-700 mb-1">Magnitud</label>
            <select 
              id="magnitud" 
              className="w-full sm:w-64 px-3 py-2 border border-green-200 rounded-md text-gray-700 focus:outline-none focus:ring-2 focus:ring-green-300 focus:border-green-300"
              value={filtroActivo}
              onChange={handleFiltroChange}
            >
              {magnitudes.map((magnitud) => (
                <option key={magnitud} value={magnitud}>{magnitud}</option>
              ))}
            </select>
          </div>

          {/* Combobox para invernaderos y sensores */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            <div>
              <label htmlFor="invernadero" className="block text-sm font-medium text-green-700 mb-1">Invernadero</label>
              <select 
                id="invernadero" 
                className="w-full px-3 py-2 border border-green-200 rounded-md text-gray-700 focus:outline-none focus:ring-2 focus:ring-green-300 focus:border-green-300"
                value={invernaderoSeleccionado}
                onChange={handleInvernaderoChange}
              >
                <option value="">Todos los invernaderos</option>
                {invernaderosMock.map(inv => (
                  <option key={inv.id} value={inv.id}>{inv.name}</option>
                ))}
              </select>
            </div>
            <div>
              <label htmlFor="sensor" className="block text-sm font-medium text-green-700 mb-1">Sensor</label>
              <select 
                id="sensor"
                className="w-full px-3 py-2 border border-green-200 rounded-md text-gray-700 focus:outline-none focus:ring-2 focus:ring-green-300 focus:border-green-300 disabled:bg-gray-100 disabled:text-gray-500"
                value={sensorSeleccionado}
                onChange={(e) => setSensorSeleccionado(e.target.value)}
                disabled={!invernaderoSeleccionado}
              >
                <option value="">Todos los sensores</option>
                {sensoresDisponibles.map(sensor => (
                  <option key={sensor.id} value={sensor.id}>{sensor.id} - {sensor.type}</option>
                ))}
              </select>
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
              No se encontraron alertas con los filtros seleccionados
            </div>
          )}
        </div>
      </div>
    </div>
    </>
  );
}

export default AlertasRecientes;