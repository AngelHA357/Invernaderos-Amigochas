import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import logo from '../recursos/logo2.png'; 
import fondo from '../recursos/fondo.png';
import usuariosMock from '../mocks/usuarios.json';

function InicioSesion() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({ usuario: '', contrasena: '' });
    const [errors, setErrors] = useState({});

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const validateForm = () => {
        const newErrors = {};
        if (!formData.usuario) newErrors.usuario = 'El usuario es obligatorio.';
        if (!formData.contrasena) newErrors.contrasena = 'La contraseña es obligatoria.';
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (validateForm()) {
            // Validar credenciales con usuariosMock
            const usuarioValido = usuariosMock.find(
                (usuario) =>
                    usuario.id === formData.usuario && usuario.contrasenia === formData.contrasena
            );

            if (usuarioValido) {
                console.log('Inicio de sesión exitoso:', formData);
                navigate('/invernaderos'); // Redirige a la página principal
            } else {
                setErrors({ general: 'Usuario o contraseña incorrectos.' });
            }
        }
    };

    return (
        <div
            className="min-h-screen flex items-center justify-center"
            style={{
                backgroundImage: `url(${fondo})`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
            }}
        >
            <div className="max-w-md w-full bg-white p-8 rounded-lg shadow-2xl border border-green-300">
                {/* Encabezado */}
                <div className="text-center mb-8">
                    <img
                        src={logo}
                        alt="Logo"
                        className="mx-auto h-auto w-auto" // Logo más grande y centrado
                    />
                    <h1 className="text-3xl font-bold text-gray-800 mt-6">Bienvenido</h1>
                    <p className="text-sm text-gray-600">Por favor, inicia sesión para continuar</p>
                </div>

                {/* Formulario */}
                <form onSubmit={handleSubmit}>
                    <div className="mb-6">
                        <label className="block text-gray-700 font-medium mb-2">Usuario</label>
                        <input
                            type="text"
                            name="usuario"
                            value={formData.usuario}
                            onChange={handleChange}
                            className="w-full border border-gray-300 rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-green-500"
                            placeholder="Ingresa tu usuario"
                        />
                        {errors.usuario && <p className="text-red-500 text-xs mt-1">{errors.usuario}</p>}
                    </div>
                    <div className="mb-6">
                        <label className="block text-gray-700 font-medium mb-2">Contraseña</label>
                        <input
                            type="password"
                            name="contrasena"
                            value={formData.contrasena}
                            onChange={handleChange}
                            className="w-full border border-gray-300 rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-green-500"
                            placeholder="Ingresa tu contraseña"
                        />
                        {errors.contrasena && <p className="text-red-500 text-xs mt-1">{errors.contrasena}</p>}
                    </div>
                    {errors.general && (
                        <p className="text-red-500 text-center text-sm mb-4">{errors.general}</p>
                    )}
                    <button
                        type="submit"
                        className="w-full bg-green-600 text-white py-3 rounded-lg font-bold hover:bg-green-700 transition-colors duration-300 shadow-md"
                    >
                        Iniciar Sesión
                    </button>
                </form>
            </div>
        </div>
    );
}

export default InicioSesion;