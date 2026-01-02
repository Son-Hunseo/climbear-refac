FROM python:3.12.9-slim

# 최적화 설정
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

WORKDIR /app

# OpenCV 실행을 위한 필수 라이브러리 설치 & git 설치
RUN apt-get update && apt-get install -y \
    libgl1 \
    libglib2.0-0 \
    git \
 && rm -rf /var/lib/apt/lists/*

RUN pip install --upgrade pip

# CUDA 설치
RUN pip3 install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu118

# 의존성 파일 복사 및 설치 (캐시 활용을 위해 분리)
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 애플리케이션 코드 복사
COPY . .

EXPOSE 8000

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000", "--workers", "4", "--proxy-headers", "--forwarded-allow-ips", "*"]