import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import ListaInvernaderos from './GestionSensores/ListaInvernaderos.jsx';
import ListaSensores from './GestionSensores/ListaSensores.jsx';
import GenerarInforme from './Informes/GenerarInforme.jsx';
import AlertasRecientes from './Anomalias/AlertasRecientes.jsx';
import LevantarReporte from './Anomalias/LevantarReporte.jsx';
import ListaAlarmas from './GestionAlarmas/ListaAlarmas.jsx';
import AgregarAlarma from './GestionAlarmas/AgregarAlarma.jsx';
import AgregarSensor from './GestionSensores/AgregarSensor.jsx';
import EditarSensor from './GestionSensores/EditarSensor.jsx';
import EditarAlarma from './GestionAlarmas/EditarAlarma.jsx';
import InicioSesion from './Principal/InicioSesion.jsx';
import { AuthProvider } from './context/AuthContext.jsx';
import ProtectedRoute from './components/ProtectedRoute.jsx';
import AccesoDenegado from './components/AccesoDenegado.jsx';

function App() {
    return (
        <AuthProvider>
            <Router>
                <Routes>
                    {/* Rutas públicas */}
                    <Route path="/" element={<InicioSesion />} />
                    <Route path="/login" element={<InicioSesion />} />
                    <Route path="/acceso-denegado" element={<AccesoDenegado />} />
                    
                    {/* Ruta de invernaderos (accesible para todos los usuarios autenticados) */}
                    <Route path="/invernaderos" element={
                        <ProtectedRoute>
                            <ListaInvernaderos />
                        </ProtectedRoute>
                    } />
                    
                    {/* Rutas para operadores (gestionSensores y gestionAlarmas) */}
                    <Route path="/sensores/:invernaderoId" element={
                        <ProtectedRoute requiredResource="gestionSensores">
                            <ListaSensores />
                        </ProtectedRoute>
                    } />
                    <Route path="/sensores/agregar" element={
                        <ProtectedRoute requiredResource="gestionSensores">
                            <AgregarSensor />
                        </ProtectedRoute>
                    } />
                    <Route path="/sensores/editar/:sensorId" element={
                        <ProtectedRoute requiredResource="gestionSensores">
                            <EditarSensor />
                        </ProtectedRoute>
                    } />
                    <Route path="/alarmas" element={
                        <ProtectedRoute requiredResource="alarmas">
                            <ListaAlarmas />
                        </ProtectedRoute>
                    } />
                    <Route path="/alarmas/agregarAlarma" element={
                        <ProtectedRoute requiredResource="alarmas">
                            <AgregarAlarma />
                        </ProtectedRoute>
                    } />
                    <Route path="/alarmas/:alarmaId" element={
                        <ProtectedRoute requiredResource="alarmas">
                            <EditarAlarma />
                        </ProtectedRoute>
                    } />
                    
                    {/* Rutas para analistas (informes y anomalias) */}
                    <Route path="/informes" element={
                        <ProtectedRoute requiredResource="informes">
                            <GenerarInforme />
                        </ProtectedRoute>
                    } />
                    <Route path="/anomalias" element={
                        <ProtectedRoute requiredResource="anomalias">
                            <AlertasRecientes />
                        </ProtectedRoute>
                    } />
                    <Route path="/anomalias/:alertaId" element={
                        <ProtectedRoute requiredResource="anomalias">
                            <LevantarReporte />
                        </ProtectedRoute>
                    } />
                    
                    {/* Ruta de fallback */}
                    <Route path="*" element={<Navigate to="/" />} />
                </Routes>
            </Router>
        </AuthProvider>
    );
}

export default App;