import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import ListaInvernaderos from './GestionSensores/ListaInvernaderos.jsx';
import ListaSensores from './GestionSensores/ListaSensores.jsx';

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<ListaInvernaderos />} />
                <Route path="/sensores/:invernaderoId" element={<ListaSensores />} />
            </Routes>
        </Router>
    );
}

export default App;