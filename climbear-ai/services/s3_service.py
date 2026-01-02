from config.aws import get_aws_client
from botocore.exceptions import BotoCoreError, ClientError
from typing import Optional
from dto import response_dto
import os

def generate_presigned_put_url(
    filename: str,
    content_type: Optional[str] = "image/jpeg",
    expires_in: int = 600,
    prefix: str = "hold_image/raw_image/"
) -> str:
    try:
        key = f"{prefix.rstrip('/')}/{filename}"
        presigned_url = get_aws_client().generate_presigned_url(
            ClientMethod='put_object',
            Params={
                'Bucket': os.getenv("BUCKET_NAME"),
                'Key': key,
                'ContentType': content_type
            },
            ExpiresIn=expires_in,
            HttpMethod='PUT'
        )
        return response_dto.presigned_url_response(url=presigned_url)
    except (BotoCoreError, ClientError) as e:
        raise RuntimeError(f"Failed to generate presigned URL: {e}")
