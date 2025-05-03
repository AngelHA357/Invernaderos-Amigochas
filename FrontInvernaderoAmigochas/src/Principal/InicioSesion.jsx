import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import logo from '../recursos/logo2.png';
import fondo from '../recursos/fondo.png';

function InicioSesion() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({ usuario: '', contrasena: '' });
    const [formErrors, setFormErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const [loginError, setLoginError] = useState(null);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const validateForm = () => {
        const newErrors = {};
        if (!formData.usuario) newErrors.usuario = 'El usuario es obligatorio.';
        if (!formData.contrasena) newErrors.contrasena = 'La contraseña es obligatoria.';
        setFormErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            setLoginError(null);
            return;
        }

        setLoading(true);
        setLoginError(null);

        const gatewayUrl = 'http://localhost:8080';
        const loginEndpoint = '/api/v1/login';
        const url = `${gatewayUrl}${loginEndpoint}`;

        console.log(`Intentando login a: ${url} con usuario: ${formData.usuario}`);

        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    username: formData.usuario,
                    password: formData.contrasena
                }),
            });
        
            if (!response.ok) {
                let errorMsg = `Error ${response.status}`;
                try {
                    const errorData = await response.json();
                    errorMsg = errorData.mensaje || errorMsg;
                } catch (jsonError) {
                    try {
                       const textError = await response.text();
                       errorMsg = textError || errorMsg;
                    } catch(textErrorErr) {
                       errorMsg = `Error ${response.status} al procesar la respuesta.`;
                    }
                    console.error("Respuesta de error no es JSON:", jsonError);
                }
                throw new Error(errorMsg); // Lanza el error para el .catch()
            }
        
            console.log('Inicio de sesión exitoso (Respuesta 200 OK recibida)');
            navigate('/invernaderos');
        
        } catch (error) {
            console.error('Error en inicio de sesión:', error);
            setLoginError(error.message || 'Ocurrió un error. Intente de nuevo.');
        } finally {
            setLoading(false);
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
                        {formErrors.usuario && <p className="text-red-500 text-xs mt-1">{formErrors.usuario}</p>}
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
                        {formErrors.contrasena && <p className="text-red-500 text-xs mt-1">{formErrors.contrasena}</p>}
                    </div>
                    {loginError && (
                        <p className="text-red-500 text-center text-sm mb-4">{loginError}</p>
                    )}
                    <button
                        type="submit"
                        disabled={loading}
                        className={`w-full bg-green-600 text-white py-3 rounded-lg font-bold hover:bg-green-700 transition-colors duration-300 shadow-md ${loading ? 'opacity-70 cursor-wait' : ''}`}
                    >
                        {loading ? 'Ingresando...' : 'Iniciar Sesión'}
                    </button>
                </form>
            </div>
        </div>
    );
}

export default InicioSesion;