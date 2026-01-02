from fastapi import FastAPI, Depends
from api.hold_api import router as hold_router
from api.aws_api import router as aws_router
from fastapi.security import HTTPBearer

# 로컬에서 .env를 불러오려면 아래 2줄 주석 해제
# from dotenv import load_dotenv
# load_dotenv()

auth_header = HTTPBearer(auto_error=False)

app = FastAPI(
    title="Climbear AI Server",
    dependencies=[Depends(auth_header)]
)

app.include_router(hold_router)
app.include_router(aws_router)