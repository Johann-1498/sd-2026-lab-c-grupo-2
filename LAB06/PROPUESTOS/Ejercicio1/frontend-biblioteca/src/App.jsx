import { useState, useEffect } from 'react'
import './App.css'

function App() {
  const [libros, setLibros] = useState([]);
  const [nuevoLibro, setNuevoLibro] = useState("");
  const [idBusqueda, setIdBusqueda] = useState("");
  const [resultadoBusqueda, setResultadoBusqueda] = useState(null);
  const [mensaje, setMensaje] = useState({ texto: "", tipo: "" });

  const API_URL = "http://localhost:8080/libros";

  const mostrarMensaje = (texto, tipo) => {
    setMensaje({ texto, tipo });
    setTimeout(() => setMensaje({ texto: "", tipo: "" }), 3000);
  };

  const listarLibros = async () => {
    try {
      const res = await fetch(API_URL);
      const data = await res.json();
      setLibros(data);
    } catch (e) {
      mostrarMensaje("Error de conexión con el servidor", "error");
    }
  };

  useEffect(() => { listarLibros(); }, []);

  const registrarLibro = async (e) => {
    e.preventDefault();
    if (!nuevoLibro.trim()) return;
    await fetch(API_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'text/plain' },
      body: nuevoLibro
    });
    setNuevoLibro("");
    listarLibros();
    mostrarMensaje("Libro registrado correctamente", "success");
  };

  const buscarPorId = async () => {
    if (idBusqueda === "") return;
    const res = await fetch(`${API_URL}/${idBusqueda}`);
    const data = await res.text();
    if (data.includes("Error")) {
      setResultadoBusqueda({ texto: "No encontrado", error: true });
    } else {
      setResultadoBusqueda({ texto: data, error: false });
    }
  };

  const eliminarLibro = async (index) => {
    await fetch(`${API_URL}/${index}`, { method: 'DELETE' });
    listarLibros();
    setResultadoBusqueda(null);
    mostrarMensaje("Libro eliminado", "info");
  };

  return (
    <div className="dashboard">
      <header className="main-header">
        <div className="logo">
          <span className="icon">📂</span>
          <h1>BIBLIO<span>TECH</span></h1>
        </div>
        {mensaje.texto && <div className={`toast ${mensaje.tipo}`}>{mensaje.texto}</div>}
      </header>

      <main className="content">
        {/* Columna Izquierda: Acciones */}
        <aside className="actions-panel">
          <section className="action-card">
            <h3>Nuevo Registro</h3>
            <form onSubmit={registrarLibro} className="form-group">
              <input 
                type="text" 
                placeholder="Nombre del título..." 
                value={nuevoLibro}
                onChange={(e) => setNuevoLibro(e.target.value)}
              />
              <button type="submit" className="btn-primary">Guardar Libro</button>
            </form>
          </section>

          <section className="action-card">
            <h3>Consultar Archivo</h3>
            <div className="form-group">
              <div className="search-input">
                <input 
                  type="number" 
                  placeholder="ID del libro" 
                  value={idBusqueda}
                  onChange={(e) => setIdBusqueda(e.target.value)}
                />
                <button onClick={buscarPorId} className="btn-secondary">Buscar</button>
              </div>
              {resultadoBusqueda && (
                <div className={`search-badge ${resultadoBusqueda.error ? 'error' : ''}`}>
                  {resultadoBusqueda.texto}
                </div>
              )}
            </div>
          </section>
        </aside>

        {/* Columna Derecha: Listado */}
        <section className="list-panel">
          <div className="list-header">
            <h2>Colección General</h2>
            <button onClick={listarLibros} className="btn-icon">🔄</button>
          </div>
          
          <div className="grid-books">
            {libros.length === 0 ? (
              <p className="empty">No hay registros disponibles</p>
            ) : (
              libros.map((libro, index) => (
                <div key={index} className="book-card">
                  <div className="book-id">ID: {index}</div>
                  <p className="book-title">{libro}</p>
                  <button onClick={() => eliminarLibro(index)} className="btn-delete">
                    Eliminar
                  </button>
                </div>
              ))
            )}
          </div>
        </section>
      </main>
    </div>
  )
}

export default App