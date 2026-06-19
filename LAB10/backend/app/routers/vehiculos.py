from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from .. import models, schemas
from ..database import get_db

router = APIRouter(prefix="/vehiculos", tags=["Vehiculos"])


@router.post("/", status_code=201)
def registrar_vehiculo(data: schemas.VehiculoCreate, db: Session = Depends(get_db)):
    nuevo = models.Vehiculo(**data.model_dump())
    db.add(nuevo)
    db.commit()
    db.refresh(nuevo)

    return {
        "mensaje": "Ubicación de vehículo registrada correctamente",
        "data": schemas.VehiculoOut.model_validate(nuevo)
    }


@router.get("/")
def listar_vehiculos(db: Session = Depends(get_db)):
    vehiculos = db.query(models.Vehiculo).all()

    return {
        "total": len(vehiculos),
        "data": [schemas.VehiculoOut.model_validate(item) for item in vehiculos]
    }