from fastapi import FastAPI, Depends
from fastapi.middleware.cors import CORSMiddleware
from api.hold_api import router as hold_router
from api.aws_api import router as aws_router
from middleware.auth import JwtAuthMiddleware

# 로컬에서 .env를 불러오려면 아래 2줄 주석 해제
# from dotenv import load_dotenv
# load_dotenv()

jwt_auth = JwtAuthMiddleware()

app = FastAPI(title="Climbear AI Server")

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 라우터에 인증 의존성 주입
app.include_router(hold_router, dependencies=[Depends(jwt_auth)])
app.include_router(aws_router, dependencies=[Depends(jwt_auth)])