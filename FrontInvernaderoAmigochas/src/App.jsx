import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import ListaInvernaderos from './GestionSensores/ListaInvernaderos.jsx';
import ListaSensores from './GestionSensores/ListaSensores.jsx';
import GenerarInforme from './Informes/GenerarInforme.jsx';
import ListaAlarmas from './GestionAlarmas/ListaAlarmas.jsx';
import AgregarSensor from './GestionSensores/AgregarSensor.jsx';
import EditarSensor from './GestionSensores/EditarSensor.jsx';
import AgregarAlarma from './GestionAlarmas/AgregarAlarma.jsx';

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<ListaInvernaderos />} />
                <Route path="/sensores/:invernaderoId" element={<ListaSensores />} />
                <Route path="/informes" element={<GenerarInforme />} />
                <Route path="/alarmas" element={<ListaAlarmas />} />
                <Route path="/alarmas/agregarAlarma" element={<AgregarAlarma />} />
                <Route path="/sensores/:invernaderoId/agregarSensor" element={<AgregarSensor />} />
                <Route path="/sensores/:invernaderoId/:sensorId" element={<EditarSensor />} />
                <Route path="/anomalias" element={<div>Página de Anomalías</div>} />
            </Routes>
        </Router>
    );
}

export default App;