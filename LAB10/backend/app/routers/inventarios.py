from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from .. import models, schemas
from ..database import get_db

router = APIRouter(prefix="/inventarios", tags=["Inventarios"])


@router.post("/", status_code=201)
def crear_inventario(data: schemas.InventarioCreate, db: Session = Depends(get_db)):
    nuevo = models.Inventario(**data.model_dump())
    db.add(nuevo)
    db.commit()
    db.refresh(nuevo)

    return {
        "mensaje": "Inventario registrado correctamente",
        "data": schemas.InventarioOut.model_validate(nuevo)
    }


@router.get("/")
def listar_inventarios(db: Session = Depends(get_db)):
    inventarios = db.query(models.Inventario).all()

    return {
        "total": len(inventarios),
        "data": [schemas.InventarioOut.model_validate(item) for item in inventarios]
    }