from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from .. import models, schemas
from ..database import get_db

router = APIRouter(prefix="/pedidos", tags=["Pedidos"])


@router.post("/", status_code=201)
def crear_pedido(data: schemas.PedidoCreate, db: Session = Depends(get_db)):
    nuevo = models.Pedido(**data.model_dump())

    db.add(nuevo)
    db.commit()
    db.refresh(nuevo)

    return {
        "mensaje": "Pedido registrado correctamente",
        "data": schemas.PedidoOut.model_validate(nuevo)
    }


@router.get("/")
def listar_pedidos(db: Session = Depends(get_db)):
    pedidos = db.query(models.Pedido).all()

    return {
        "total": len(pedidos),
        "data": [
            schemas.PedidoOut.model_validate(item)
            for item in pedidos
        ]
    }