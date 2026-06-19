from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from .. import models, schemas
from ..database import get_db

router = APIRouter(prefix="/envios", tags=["Envios"])

ESTADOS_VALIDOS = ["CREADO", "EN_TRANSITO", "ENTREGADO", "CANCELADO"]


@router.post("/", status_code=201)
def registrar_envio(data: schemas.EnvioCreate, db: Session = Depends(get_db)):
    estado = data.estado.upper()

    if estado not in ESTADOS_VALIDOS:
        raise HTTPException(
            status_code=400,
            detail=f"Estado inválido. Estados permitidos: {ESTADOS_VALIDOS}"
        )

    nuevo = models.Envio(
        pedido_id=data.pedido_id,
        estado=estado,
        ubicacion=data.ubicacion,
        sede=data.sede
    )

    db.add(nuevo)
    db.commit()
    db.refresh(nuevo)

    return {
        "mensaje": "Estado de envío registrado correctamente",
        "data": schemas.EnvioOut.model_validate(nuevo)
    }


@router.get("/")
def listar_envios(db: Session = Depends(get_db)):
    envios = db.query(models.Envio).all()

    return {
        "total": len(envios),
        "data": [schemas.EnvioOut.model_validate(item) for item in envios]
    }