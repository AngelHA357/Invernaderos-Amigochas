import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import ListaInvernaderos from './GestionSensores/ListaInvernaderos.jsx';
import ListaSensores from './GestionSensores/ListaSensores.jsx';
import GenerarInforme from './Informes/GenerarInforme.jsx';
import AlertasRecientes from './Anomalias/AlertasRecientes.jsx';
import LevantarReporte from './Anomalias/LevantarReporte.jsx';
import ListaAlarmas from './GestionAlarmas/ListaAlarmas.jsx';
import AgregarAlarma from './GestionAlarmas/AgregarAlarma.jsx';
import AgregarSensor from './GestionSensores/AgregarSensor.jsx';
import EditarSensor from './GestionSensores/EditarSensor.jsx';

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<ListaInvernaderos />} />
                <Route path="/sensores/:invernaderoId" element={<ListaSensores />} />
                <Route path="/sensores/agregar" element={<AgregarSensor />} />
                <Route path="/sensores/editar/:sensorId" element={<EditarSensor />} />
                <Route path="/informes" element={<GenerarInforme />} />
                <Route path="/alarmas" element={<ListaAlarmas />} />
                <Route path="/alarmas/agregarAlarma" element={<AgregarAlarma />} />
                <Route path="/anomalias" element={<AlertasRecientes />} />
                <Route path="/anomalias/:alertaId" element={<LevantarReporte />} />
            </Routes>
        </Router>
    );
}

export default App;