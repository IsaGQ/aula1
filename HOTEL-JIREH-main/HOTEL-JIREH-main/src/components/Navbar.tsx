import React, { useEffect, useRef, useState } from 'react';
import { Menu, X, User, LogIn, UserPlus, ShoppingCart, Trash2, Plus, Minus } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '@/context/CartContext';

const Navbar = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [showLogin, setShowLogin] = useState(false);
  const [showRegister, setShowRegister] = useState(false);

  // Login form
  const [loginCorreo, setLoginCorreo] = useState('');
  const [loginPassword, setLoginPassword] = useState('');
  const [loginLoading, setLoginLoading] = useState(false);

  // Register form
  const [regNombre, setRegNombre] = useState('');
  const [regCorreo, setRegCorreo] = useState('');
  const [regPassword, setRegPassword] = useState('');
  const [regCelular, setRegCelular] = useState('');
  const [regDocumento, setRegDocumento] = useState('');
  const [regDireccion, setRegDireccion] = useState('');
  const [regLoading, setRegLoading] = useState(false);

  const navigate = useNavigate();
  const apiBase = 'http://localhost:9090';

  // Cart context (asegúrate CartProvider envuelve App)
  const { carrito, loading: cartLoading, refreshCart, updateItem, removeItem, checkout } = useCart();

  // dropdown control
  const [cartOpen, setCartOpen] = useState(false);
  const dropdownRef = useRef(null);

  // UI loading per item id
  const [itemLoading, setItemLoading] = useState({});
  const [clearing, setClearing] = useState(false);
  const [checkoutLoading, setCheckoutLoading] = useState(false);

  useEffect(() => {
    // Open dropdown automatically when user has items
    if (carrito && carrito.reservaHabitaciones && carrito.reservaHabitaciones.length > 0) {
      setCartOpen(true);
    } else {
      setCartOpen(false);
    }
  }, [carrito]);

  // click outside closes dropdown
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setCartOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Login / Register handlers (sin cambios importantes)
  const handleLogin = async (e) => {
    e?.preventDefault();
    if (!loginCorreo || !loginPassword) return alert('Por favor ingresa correo y contraseña.');
    try {
      setLoginLoading(true);
      const res = await fetch(`${apiBase}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ correo: loginCorreo, password: loginPassword }),
      });
<<<<<<< HEAD
      const data = await res.json();
      setLoginLoading(false);
      if (res.ok && data.success) {
        localStorage.setItem('usuarioId', data.id);
        localStorage.setItem('usuarioNombre', data.nombre ?? '');
        localStorage.setItem('usuarioRol', data.rol ?? 'CLIENTE');
        alert('Inicio de sesión correcto. ¡Bienvenido ' + (data.nombre ?? '') + '!');
        setShowLogin(false);
        if (data.rol === 'ADMIN') navigate('/admin');
        try { await refreshCart(); } catch (err) {}
      } else {
        alert(data.message || 'Credenciales inválidas.');
=======

      const data = await response.json();

      if (response.ok) {
        alert('Login exitoso');
        window.open('/admin', '_blank');
        localStorage.setItem("auth", data.token);
        setFormVisible(false);
      } else {
        alert('Error al iniciar sesión' + data.message);
>>>>>>> c1a0f875f92bf93d5a58ec25010063f449105279
      }
    } catch (err) {
      setLoginLoading(false);
      console.error(err);
      alert('Error de conexión. Intenta de nuevo.');
    }
  };

  const handleRegister = async (e) => {
    e?.preventDefault();
    if (!regNombre || !regCorreo || !regPassword) return alert('Nombre, correo y contraseña son obligatorios.');
    try {
      setRegLoading(true);
      const res = await fetch(`${apiBase}/api/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          nombre: regNombre,
          correo: regCorreo,
          password: regPassword,
          celular: regCelular,
          documentoIdentidad: regDocumento,
          direccion: regDireccion
        }),
      });
      const data = await res.json();
      setRegLoading(false);
      if (res.ok && data.success) {
        alert('Registro exitoso. Ahora puedes iniciar sesión.');
        setShowRegister(false);
        setLoginCorreo(regCorreo);
      } else {
        alert(data.message || 'Error al registrar usuario.');
      }
    } catch (err) {
      setRegLoading(false);
      console.error(err);
      alert('Error de conexión. Intenta de nuevo.');
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('usuarioId');
    localStorage.removeItem('usuarioNombre');
    localStorage.removeItem('usuarioRol');
    alert('Has cerrado sesión.');
    navigate('/');
  };

  const usuarioId = localStorage.getItem('usuarioId');
  const usuarioNombre = localStorage.getItem('usuarioNombre');
  const usuarioRol = localStorage.getItem('usuarioRol');

  const itemsCount = carrito?.reservaHabitaciones?.reduce((acc, i) => acc + (i.cantidad ?? 0), 0) ?? 0;

  const obtenerUrlImagen = (imagenUrl) => {
    if (!imagenUrl) return '';
    return imagenUrl.startsWith('http') ? imagenUrl : `http://localhost:9090${imagenUrl}`;
  };

  const formatCurrency = (value) =>
    new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(value);

  const getPrecioUnitario = (habitacion) => {
    if (!habitacion) return 0;
    return habitacion.precioPorNoche ?? habitacion.precio ?? habitacion.price ?? 0;
  };

  // Increase quantity
  const handleIncrease = async (rh) => {
    const id = rh.id;
    try {
      setItemLoading(prev => ({ ...prev, [id]: true }));
      const nueva = (rh.cantidad ?? 0) + 1;
      await updateItem(rh.id, nueva); // updateItem(userId handled in context)
      // refreshCart() is called inside context methods
    } catch (err) {
      console.error(err);
      alert('No se pudo aumentar la cantidad.');
    } finally {
      setItemLoading(prev => ({ ...prev, [id]: false }));
    }
  };

  // Decrease quantity
  const handleDecrease = async (rh) => {
    const id = rh.id;
    try {
      if ((rh.cantidad ?? 0) <= 1) {
        // if quantity would go to 0, confirm remove
        if (!confirm('Quieres eliminar este item del carrito?')) return;
        await handleRemove(rh);
        return;
      }
      setItemLoading(prev => ({ ...prev, [id]: true }));
      const nueva = (rh.cantidad ?? 0) - 1;
      await updateItem(rh.id, nueva);
    } catch (err) {
      console.error(err);
      alert('No se pudo disminuir la cantidad.');
    } finally {
      setItemLoading(prev => ({ ...prev, [id]: false }));
    }
  };

  // Remove single item
  const handleRemove = async (rh) => {
    const id = rh.id;
    try {
      if (!confirm('¿Eliminar este item del carrito?')) return;
      setItemLoading(prev => ({ ...prev, [id]: true }));
      await removeItem(rh.id);
    } catch (err) {
      console.error(err);
      alert('No se pudo eliminar el item.');
    } finally {
      setItemLoading(prev => ({ ...prev, [id]: false }));
    }
  };

  // Clear entire cart
  const handleClearCart = async () => {
    if (!carrito || !carrito.reservaHabitaciones || carrito.reservaHabitaciones.length === 0) {
      return;
    }
    if (!confirm('¿Vaciar todo el carrito?')) return;
    try {
      setClearing(true);
      // remove items sequentially to avoid server overload
      for (const rh of carrito.reservaHabitaciones.slice()) {
        await removeItem(rh.id);
      }
      // refreshCart called by each removeItem -> but to be safe:
      try { await refreshCart(); } catch (e) {}
      setClearing(false);
    } catch (err) {
      console.error(err);
      setClearing(false);
      alert('No se pudo vaciar el carrito por completo.');
    }
  };

  // Checkout / pay
  const handleCheckout = async () => {
    if (!carrito || !carrito.reservaHabitaciones || carrito.reservaHabitaciones.length === 0) {
      return alert('Tu carrito está vacío.');
    }
    if (!confirm('¿Confirmar y pagar la reserva?')) return;
    try {
      setCheckoutLoading(true);
      await checkout(); // executes confirmarCarrito and refreshes
      setCartOpen(false);
      setCheckoutLoading(false);
      alert('Reserva confirmada. Se ha generado tu reserva.');
      navigate('/mis-reservas');
    } catch (err) {
      console.error(err);
      setCheckoutLoading(false);
      alert('No se pudo completar la reserva.');
    }
  };

  return (
    <nav className="fixed top-0 left-0 w-full z-50 bg-gradient-to-b from-eco-dark-green/95 to-transparent">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo & menu */}
          <div className="flex items-center gap-6">
            <Link to="/" className="flex items-center">
              <span className="text-white text-2xl md:text-3xl font-playfair font-bold tracking-wide">JIREH</span>
            </Link>
            <div className="hidden md:flex items-center gap-6">
              <Link to="/" className="text-white hover:text-eco-cream transition-colors">Inicio</Link>
              <Link to="/habitaciones" className="text-white hover:text-eco-cream transition-colors">Habitaciones</Link>
              <Link to="/servicios" className="text-white hover:text-eco-cream transition-colors">Servicios</Link>
            </div>
          </div>

          {/* Right side */}
          <div className="flex items-center gap-4">
            <div className="hidden md:block">
              <Link to="/reserva">
                <Button variant="outline" className="bg-transparent text-white border-white hover:bg-white hover:text-eco-dark-green">
                  Reservar Ahora
                </Button>
              </Link>
            </div>

            {/* Cart */}
            <div className="relative" ref={dropdownRef}>
              <button
                onClick={() => setCartOpen((s) => !s)}
                className="flex items-center gap-2 text-white relative"
                title="Ver carrito"
              >
                <ShoppingCart size={20} />
                <span className="ml-1 text-sm">{itemsCount}</span>
              </button>

              {cartOpen && (
                <div className="absolute right-0 mt-2 w-96 bg-white text-black rounded shadow-lg border p-3 z-50">
                  <div className="flex items-center justify-between mb-2">
                    <div className="text-sm font-semibold">Carrito</div>
                    <div className="text-xs text-gray-500">{itemsCount} {itemsCount === 1 ? 'item' : 'items'}</div>
                  </div>

                  {cartLoading && <div className="text-sm">Cargando...</div>}

<<<<<<< HEAD
                  {!cartLoading && (!carrito || !carrito.reservaHabitaciones || carrito.reservaHabitaciones.length === 0) && (
                    <div className="text-sm text-gray-600">Tu carrito está vacío</div>
                  )}

                  {!cartLoading && carrito && carrito.reservaHabitaciones && carrito.reservaHabitaciones.length > 0 && (
                    <div className="space-y-3 max-h-64 overflow-y-auto pr-2">
                      {carrito.reservaHabitaciones.map((rh) => {
                        const tipo = rh.habitacion?.tipo ?? 'Habitación';
                        const cantidad = rh.cantidad ?? 0;
                        const precioUnit = getPrecioUnitario(rh.habitacion);
                        const subtotal = rh.subtotal ?? (precioUnit * cantidad);
                        const loading = !!itemLoading[rh.id];
                        return (
                          <div key={rh.id} className="flex gap-3 items-start border-b pb-2">
                            {rh.habitacion?.imagenUrl && (
                              <img src={obtenerUrlImagen(rh.habitacion.imagenUrl)} alt={tipo} className="w-16 h-12 object-cover rounded" />
                            )}
                            <div className="flex-1">
                              <div className="text-sm font-medium text-eco-dark-green">{tipo}</div>
                              <div className="text-xs text-gray-600">Precio unitario: <span className="font-medium">{formatCurrency(precioUnit)}</span></div>

                              {/* quantity controls */}
                              <div className="mt-2 flex items-center gap-2">
                                <button
                                  onClick={() => handleDecrease(rh)}
                                  disabled={loading}
                                  className="p-1 border rounded text-gray-700 hover:bg-gray-100"
                                  title="Disminuir"
                                >
                                  <Minus size={14} />
                                </button>
                                <div className="px-3 py-1 border rounded text-sm">{cantidad}</div>
                                <button
                                  onClick={() => handleIncrease(rh)}
                                  disabled={loading}
                                  className="p-1 border rounded text-gray-700 hover:bg-gray-100"
                                  title="Aumentar"
                                >
                                  <Plus size={14} />
                                </button>

                                <button
                                  onClick={() => handleRemove(rh)}
                                  disabled={loading}
                                  className="ml-3 text-red-600 hover:text-red-800"
                                  title="Eliminar"
                                >
                                  <Trash2 size={16} />
                                </button>
                              </div>
                            </div>

                            <div className="text-right">
                              <div className="text-sm font-semibold">{formatCurrency(subtotal)}</div>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  )}

                  {/* Total */}
                  <div className="mt-3 border-t pt-3 flex items-center justify-between">
                    <div className="text-sm text-gray-600">Total</div>
                    <div className="text-lg font-semibold text-eco-dark-green">
                      {formatCurrency(carrito?.precioTotal ?? (carrito?.reservaHabitaciones?.reduce((s, r) => s + (r.subtotal ?? (getPrecioUnitario(r.habitacion) * (r.cantidad ?? 0))), 0) ?? 0))}
                    </div>
                  </div>

                  {/* Actions: Vaciar + Pagar */}
                  <div className="mt-3 flex gap-2">
                    <button
                      onClick={handleClearCart}
                      disabled={clearing}
                      className="px-3 py-2 border rounded text-sm"
                    >
                      {clearing ? 'Vaciando...' : 'Vaciar carrito'}
                    </button>

                    <button
                      onClick={handleCheckout}
                      disabled={checkoutLoading}
                      className="flex-1 px-3 py-2 bg-eco-dark-green text-white rounded text-sm"
                    >
                      {checkoutLoading ? 'Procesando...' : 'Pagar'}
                    </button>
                  </div>
                </div>
              )}
            </div>

            {/* Usuario / Auth UI */}
            {usuarioId ? (
              <div className="hidden md:flex items-center gap-3">
                <div className="text-white text-sm mr-2">Hola, <span className="font-semibold">{usuarioNombre || 'Usuario'}</span></div>
                <Button onClick={() => navigate('/mi-cuenta')} className="bg-eco-light-green text-white hover:bg-eco-dark-green">
                  Mi cuenta
                </Button>
                {usuarioRol === 'ADMIN' && (
                  <Button onClick={() => navigate('/admin')} className="bg-eco-cream text-eco-dark-green hover:brightness-95">
                    Panel Admin
                  </Button>
                )}
                <Button onClick={handleLogout} className="border border-white text-white bg-transparent hover:bg-white hover:text-eco-dark-green">
                  Cerrar sesión
                </Button>
              </div>
            ) : (
              <div className="hidden md:flex items-center gap-2">
                <button
                  onClick={() => { setShowLogin(true); setShowRegister(false); }}
                  className="flex items-center gap-2 text-white hover:text-eco-cream"
                  title="Iniciar sesión"
                >
                  <LogIn size={18} /> Ingresar
                </button>

                <button
                  onClick={() => { setShowRegister(true); setShowLogin(false); }}
                  className="flex items-center gap-2 text-white hover:text-eco-cream"
                  title="Registrarse"
                >
                  <UserPlus size={18} /> Registrarse
                </button>
              </div>
            )}

            {/* Mobile menu button */}
            <button className="md:hidden text-white" onClick={() => setIsOpen(!isOpen)}>
              {isOpen ? <X size={26} /> : <Menu size={26} />}
=======
        {/* Login Form */}
        {formVisible && (
          <div className="absolute top-full right-0 mt-2 bg-green-100 text-black p-4 rounded shadow-lg w-72 z-50 border border-green-300">
            <p className='ext-sm text-green-700 block mb-2 text-center'>Iniciar Sesión</p>
            <input
              type="text"
              placeholder="Usuario"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full mb-2 px-2 py-1 border rounded border-green-300"
            />
            <input
              type="password"
              placeholder="Contraseña"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full mb-2 px-2 py-1 border rounded border-green-300"
            />
            <a href="#" className="text-sm text-green-700 block mb-2 hover:underline">¿Olvidaste tu contraseña?</a>
            <a href="/registro" className="text-sm text-green-700 block mb-2 hover:underline">¿No tienes una cuenta?</a>
            <button
              onClick={handleLogin}
              className="w-full bg-green-500 text-white py-1 rounded hover:bg-green-600"
            >
              Ingresar
>>>>>>> c1a0f875f92bf93d5a58ec25010063f449105279
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Nav */}
      {isOpen && (
        <div className="md:hidden bg-eco-dark-green/95 p-4 space-y-3">
          <Link to="/" onClick={() => setIsOpen(false)} className="block text-white">Inicio</Link>
          <Link to="/habitaciones" onClick={() => setIsOpen(false)} className="block text-white">Habitaciones</Link>
          <Link to="/servicios" onClick={() => setIsOpen(false)} className="block text-white">Servicios</Link>
          <div className="flex gap-2 mt-2">
            <Button onClick={() => { setShowLogin(true); setIsOpen(false); }} className="w-1/2">Ingresar</Button>
            <Button onClick={() => { setShowRegister(true); setIsOpen(false); }} className="w-1/2">Registrarse</Button>
          </div>
          <div className="pt-2 border-t border-eco-dark-green/30">
            <button onClick={() => { setIsOpen(false); setCartOpen(true); }} className="w-full text-left text-white py-2 flex items-center gap-2">
              <ShoppingCart size={18} /> Ver carrito ({itemsCount})
            </button>
          </div>
        </div>
      )}

      {/* LOGIN MODAL */}
      {showLogin && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
          <div className="bg-white rounded-lg shadow-lg w-full max-w-md p-6">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-xl font-semibold text-eco-dark-green">Iniciar sesión</h3>
              <button onClick={() => setShowLogin(false)} className="text-gray-500 hover:text-gray-700">
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleLogin} className="space-y-3">
              <div>
                <label className="block text-sm text-gray-700">Correo</label>
                <input
                  type="email"
                  value={loginCorreo}
                  onChange={(e) => setLoginCorreo(e.target.value)}
                  className="mt-1 w-full border rounded p-2"
                  placeholder="tu@correo.com"
                  required
                />
              </div>

              <div>
                <label className="block text-sm text-gray-700">Contraseña</label>
                <input
                  type="password"
                  value={loginPassword}
                  onChange={(e) => setLoginPassword(e.target.value)}
                  className="mt-1 w-full border rounded p-2"
                  placeholder="********"
                  required
                />
              </div>

              <div className="flex items-center justify-between">
                <a href="#" className="text-sm text-eco-dark-green hover:underline">¿Olvidaste tu contraseña?</a>
                <div className="flex gap-2">
                  <button type="button" onClick={() => { setShowLogin(false); setShowRegister(true); }} className="text-sm underline">Crear cuenta</button>
                  <button type="submit" disabled={loginLoading} className="px-4 py-2 bg-eco-dark-green text-white rounded">
                    {loginLoading ? 'Ingresando...' : 'Ingresar'}
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* REGISTER MODAL */}
      {showRegister && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
          <div className="bg-white rounded-lg shadow-lg w-full max-w-lg p-6">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-xl font-semibold text-eco-dark-green">Crear cuenta</h3>
              <button onClick={() => setShowRegister(false)} className="text-gray-500 hover:text-gray-700">
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleRegister} className="space-y-3">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                <div>
                  <label className="block text-sm text-gray-700">Nombre completo</label>
                  <input
                    type="text"
                    value={regNombre}
                    onChange={(e) => setRegNombre(e.target.value)}
                    className="mt-1 w-full border rounded p-2"
                    placeholder="Tu nombre completo"
                    required
                  />
                </div>

                <div>
                  <label className="block text-sm text-gray-700">Correo</label>
                  <input
                    type="email"
                    value={regCorreo}
                    onChange={(e) => setRegCorreo(e.target.value)}
                    className="mt-1 w-full border rounded p-2"
                    placeholder="tu@correo.com"
                    required
                  />
                </div>

                <div>
                  <label className="block text-sm text-gray-700">Contraseña</label>
                  <input
                    type="password"
                    value={regPassword}
                    onChange={(e) => setRegPassword(e.target.value)}
                    className="mt-1 w-full border rounded p-2"
                    placeholder="Mínimo 6 caracteres"
                    required
                    minLength={6}
                  />
                </div>

                <div>
                  <label className="block text-sm text-gray-700">Celular</label>
                  <input
                    type="text"
                    value={regCelular}
                    onChange={(e) => setRegCelular(e.target.value)}
                    className="mt-1 w-full border rounded p-2"
                    placeholder="3001234567"
                  />
                </div>

                <div>
                  <label className="block text-sm text-gray-700">Documento</label>
                  <input
                    type="text"
                    value={regDocumento}
                    onChange={(e) => setRegDocumento(e.target.value)}
                    className="mt-1 w-full border rounded p-2"
                    placeholder="CC o NIT"
                  />
                </div>

                <div>
                  <label className="block text-sm text-gray-700">Dirección</label>
                  <input
                    type="text"
                    value={regDireccion}
                    onChange={(e) => setRegDireccion(e.target.value)}
                    className="mt-1 w-full border rounded p-2"
                    placeholder="Calle, ciudad"
                  />
                </div>
              </div>

              <div className="flex justify-end gap-2">
                <button type="button" onClick={() => { setShowRegister(false); setShowLogin(true); }} className="px-4 py-2 border rounded">
                  Ya tengo cuenta
                </button>
                <button type="submit" disabled={regLoading} className="px-4 py-2 bg-eco-dark-green text-white rounded">
                  {regLoading ? 'Registrando...' : 'Crear cuenta'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </nav>
  );
};

export default Navbar;
