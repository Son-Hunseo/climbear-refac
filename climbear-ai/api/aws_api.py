from fastapi import APIRouter, Query
from services.s3_service import generate_presigned_put_url
from dto import response_dto

router = APIRouter(prefix="/ai/v1/image", tags=["image"])

@router.get("/presigned-url", response_model=response_dto.presigned_url_response)
def get_presigned_url(filename: str = Query(...)):
    return generate_presigned_put_url(filename)