from pydantic import BaseModel
from typing import List

class Coordinate(BaseModel):
    x: int
    y: int

class Hold(BaseModel):
    hold_id: int
    average: Coordinate
    coordinates: List[Coordinate]