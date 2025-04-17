import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import BarraNavegacion from '../BarraNavegacion/BarraNavegacion';
import alarmasMock from '../mocks/alarmas.json';

function ListaAlarmas() {
    const navigate = useNavigate();

    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [alarmaToDelete, setAlarmaToDelete] = useState(null);

    const handleAgregarAlarma = () => {
        navigate('/alarmas/agregarAlarma');
    };

    const handleEditarAlarma = (alarma) => {
        // Guardar datos de la alarma en sessionStorage para acceder en la página de edición
        sessionStorage.setItem('alarmaSeleccionada', JSON.stringify(alarma));
        navigate(`/alarmas/${alarma.id}`);
    };

    const handleEliminarAlarma = (alarma) => {
        setAlarmaToDelete(alarma);
        setShowDeleteModal(true);
    };

    const confirmarEliminarAlarma = () => {
        // Eliminar la alarma del estado (en una aplicación real, esto sería una llamada a una API)
        setAlarmas(prevAlarmas => prevAlarmas.filter(a => a.id !== alarmaToDelete.id));
        setShowDeleteModal(false);
        // Mostrar mensaje de éxito
        alert(`La alarma ${alarmaToDelete.id} ha sido eliminada correctamente`);
    };

    const toggleEstadoAlarma = (alarmaId) => {
        setAlarmas(prevAlarmas =>
            prevAlarmas.map(a =>
                a.id === alarmaId
                    ? { ...a, estado: a.estado === 'Activa' ? 'Inactiva' : 'Activa' }
                    : a
            )
        );
    };

    return (
        <>
            <BarraNavegacion />
            <div className="min-h-screen bg-gradient-to-b from-green-50 to-white p-6">
                <div className="max-w-4xl mx-auto rounded-lg shadow-lg p-6 mt-6 border border-green-200 bg-white">
                    {/* Título con icono */}
                    <div className="flex items-center border-b border-green-100 pb-4 mb-6">
                        <div className="bg-green-100 p-3 rounded-full mr-4">
                            <span className="text-2xl" role="img" aria-label="alarma">🔔</span>
                        </div>
                        <div>
                            <h1 className="text-2xl font-bold text-gray-800">Gestión de Alarmas</h1>
                            <p className="text-sm text-green-600">Configuración y monitoreo de alarmas del sistema</p>
                        </div>
                    </div>

                    {/* Lista de alarmas */}
                    <div className="mt-6">
                        <div className='flex items-center justify-between mb-6'>
                            <h2 className="text-xl font-semibold text-gray-700">Alarmas disponibles - {alarmasMock.length}</h2>
                            <button 
                                onClick={handleAgregarAlarma}
                                className="px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 transition-colors duration-300 flex items-center shadow-sm">
                                <span className="mr-1">+</span> Agregar alarma
                            </button>
                        </div>

                        {/* Encabezados */}
                        <div className="grid grid-cols-5 gap-4 bg-green-100 text-green-800 py-3 px-4 rounded-t-lg font-semibold">
                            <div>ID</div>
                            <div>Magnitud</div>
                            <div>Valor límite</div>
                            <div>Estado</div>
                            <div className="text-center">Acciones</div>
                        </div>

                        {/* Filas */}
                        {alarmasMock.map((alarma, index) => (
                            <div
                                key={alarma.id}
                                className={`grid grid-cols-5 gap-4 py-3 px-4 ${
                                    index % 2 === 0 ? 'bg-white' : 'bg-green-50'
                                } hover:bg-green-100 transition-colors duration-200 border-b border-green-100`}
                            >
                                <div className="text-gray-800 font-medium">{alarma.id}</div>
                                <div className="text-gray-600">{alarma.magnitud}</div>
                                <div className="text-gray-600">{alarma.valorMaximo}</div>
                                <div>
                                    <button
                                        className={`px-3 py-1 text-sm font-semibold rounded-full ${
                                        alarma.estado === 'Activa'
                                            ? 'bg-green-500 text-white hover:bg-green-600'
                                            : 'bg-red-500 text-white hover:bg-red-600'
                                        }`}
                                        onClick={() => toggleEstadoAlarma(alarma.id)}
                                    >
                                        {alarma.estado}
                                    </button>
                                </div>
                                <div className="flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-2 justify-center">
                                    <button 
                                        onClick={() => handleEditarAlarma(alarma)}
                                        className="inline-flex items-center px-2 py-1 text-xs font-semibold text-green-800 bg-green-100 rounded-full hover:bg-green-200 transition-colors duration-300">
                                        <span className="mr-1">✏️</span> Editar
                                    </button>
                                    <button 
                                        onClick={() => handleEliminarAlarma(alarma)}
                                        className="inline-flex items-center px-2 py-1 text-xs font-semibold text-red-800 bg-red-100 rounded-full hover:bg-red-200 transition-colors duration-300">
                                        <span className="mr-1">🗑️</span> Eliminar
                                    </button>
                                </div>
                            </div>
                        ))}

                        {alarmasMock.length === 0 && (
                            <div className="text-center p-8 bg-green-50 rounded-lg border border-green-100 text-gray-600">
                                <div className="text-5xl mb-4">🔔</div>
                                <p className="text-lg font-medium text-gray-700 mb-2">No hay alarmas configuradas</p>
                                <p className="text-green-600 mb-4">Agrega una nueva alarma para comenzar el monitoreo.</p>
                                <button 
                                    onClick={handleAgregarAlarma} 
                                    className="px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 transition-colors duration-300 inline-flex items-center">
                                    <span className="mr-1">+</span> Agregar alarma ahora
                                </button>
                            </div>
                        )}
                    </div>

                    {/* Footer con información */}
                    <div className="mt-8 pt-4 border-t border-green-100 text-center text-xs text-green-600">
                        <p>Sistema de Alarmas Amigochas — Última actualización: {new Date().toLocaleDateString()}</p>
                    </div>
                </div>
            </div>

            {/* Modal de confirmación de eliminación */}
            {showDeleteModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full">
                        <h3 className="text-xl font-bold text-gray-800 mb-4">Confirmar eliminación</h3>
                        <p className="text-gray-600 mb-6">
                            ¿Estás seguro de que deseas eliminar la alarma <span className="font-semibold">{alarmaToDelete?.id}</span>?
                            Esta acción no se puede deshacer.
                        </p>
                        <div className="flex justify-end space-x-3">
                            <button
                                onClick={() => setShowDeleteModal(false)}
                                className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-100 transition-colors"
                            >
                                Cancelar
                            </button>
                            <button
                                onClick={confirmarEliminarAlarma}
                                className="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 transition-colors"
                            >
                                Eliminar
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}

export default ListaAlarmas;