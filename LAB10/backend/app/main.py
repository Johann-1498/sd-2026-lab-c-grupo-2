from fastapi import FastAPI

from .database import engine
from . import models

from .routers import inventarios
from .routers import pedidos
from .routers import temperaturas
from .routers import envios
from .routers import vehiculos

models.Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="API FedEx Perú - Lab 10 Replicación",
    description="Backend base para registrar datos críticos de FedEx Perú usando FastAPI y PostgreSQL.",
    version="1.0.0"
)


@app.get("/")
def inicio():
    return {
        "mensaje": "API FedEx Perú funcionando correctamente",
        "base_datos": "PostgreSQL",
        "modulos": [
            "inventarios",
            "pedidos",
            "temperaturas",
            "envios",
            "vehiculos"
        ]
    }


@app.get("/health")
def health_check():
    return {
        "status": "OK",
        "servicio": "backend-fedex",
        "base_datos": "PostgreSQL"
    }


app.include_router(inventarios.router)
app.include_router(pedidos.router)
app.include_router(temperaturas.router)
app.include_router(envios.router)
app.include_router(vehiculos.router)