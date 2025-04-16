import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import ListaInvernaderos from './GestionSensores/ListaInvernaderos.jsx';
import ListaSensores from './GestionSensores/ListaSensores.jsx';
import GenerarInforme from './Informes/GenerarInforme.jsx';

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<ListaInvernaderos />} />
                <Route path="/sensores/:invernaderoId" element={<ListaSensores />} />
                <Route path="/informes" element={<GenerarInforme />} />
                <Route path="/alarmas" element={<div>Página de Alarmas</div>} />
                <Route path="/anomalias" element={<div>Página de Anomalías</div>} />
            </Routes>
        </Router>
    );
}

export default App;