from pydantic import BaseModel
from typing import List

class classify_response(BaseModel):
    selected: List[int]
    not_selected: List[int]

class presigned_url_response(BaseModel):
    url: str