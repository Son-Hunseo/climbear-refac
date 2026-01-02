from pydantic import BaseModel
from typing import List
from dto import hold_dto

class picture_request(BaseModel):
    picture_url: str

class HoldRequest(BaseModel):
    image_url: str
    selected_hold_id_list: List[int]
    holds: List[hold_dto.Hold]