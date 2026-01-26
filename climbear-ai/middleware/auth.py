import base64
import os
from fastapi import Request, HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from jose import jwt, JWTError, ExpiredSignatureError


class JwtAuthMiddleware(HTTPBearer):
    """JWT 인증 미들웨어"""

    EXCLUDE_PATHS = ["/docs", "/openapi.json", "/ai/v1/health", "/redoc"]

    def __init__(self, auto_error: bool = True):
        super().__init__(auto_error=auto_error)
        secret_key = os.getenv("JWT_SECRET_KEY")
        if secret_key is None:
            raise ValueError("JWT_SECRET_KEY environment variable is not set")
        # Server와 동일한 Base64 인코딩
        self.secret_key = base64.b64encode(secret_key.encode()).decode()

    def _is_excluded(self, path: str) -> bool:
        """인증 제외 경로인지 확인"""
        return any(path.startswith(excluded) for excluded in self.EXCLUDE_PATHS)

    async def __call__(self, request: Request):
        if self._is_excluded(request.url.path):
            return None

        credentials: HTTPAuthorizationCredentials = await super().__call__(request)
        if credentials is None:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="인증 토큰이 필요합니다"
            )
        return self.verify_jwt(credentials.credentials)

    def verify_jwt(self, token: str) -> dict:
        """JWT 토큰 검증 및 페이로드 반환"""
        try:
            payload = jwt.decode(token, self.secret_key, algorithms=["HS512"])
            return {
                "user_id": payload.get("userId"),
                "email": payload.get("sub"),
                "token_type": payload.get("type")
            }
        except ExpiredSignatureError:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="토큰이 만료되었습니다"
            )
        except JWTError:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="유효하지 않은 토큰입니다"
            )
