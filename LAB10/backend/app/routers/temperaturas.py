from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from .. import models, schemas
from ..database import get_db

router = APIRouter(prefix="/temperaturas", tags=["Temperaturas"])


@router.post("/", status_code=201)
def registrar_temperatura(data: schemas.TemperaturaCreate, db: Session = Depends(get_db)):
    alerta = "Temperatura normal"

    if data.temperatura > 8:
        alerta = "ALERTA: temperatura mayor a 8°C"
    elif data.temperatura < 0:
        alerta = "ALERTA: temperatura menor a 0°C"

    nueva = models.Temperatura(
        almacen=data.almacen,
        temperatura=data.temperatura,
        sede=data.sede,
        alerta=alerta
    )

    db.add(nueva)
    db.commit()
    db.refresh(nueva)

    return {
        "mensaje": "Temperatura registrada correctamente",
        "data": schemas.TemperaturaOut.model_validate(nueva)
    }


@router.get("/")
def listar_temperaturas(db: Session = Depends(get_db)):
    temperaturas = db.query(models.Temperatura).all()

    return {
        "total": len(temperaturas),
        "data": [schemas.TemperaturaOut.model_validate(item) for item in temperaturas]
    }