import asyncio
from fastapi import FastAPI
from pydantic import BaseModel
from typing import List


# Khởi tạo ứng dụng FastAPI
app = FastAPI(
    title="MySoundAI API",
    description="API for MySoundAI - cung cấp nhạc theo tâm trạng và sở thích của người dùng",
    version="1.0.0"
)

# Mô hình dữ liệu cho bài hát
class Song(BaseModel):
    id: str
    title: str
    artist: str
    duration: int
    image_url: str
    audio_url: str

# Endpoint kiểm tra hệ thống
@app.get("/health", tags=["Health"])
def health_check():
    return {"status": "ok", "message": "MySoundAI API đang hoạt động!"}

@app.get("/recommendations", response_model=List[Song])
async def get_recommendations(prompt: str = "tâm trạng vui vẻ"):
    await asyncio.sleep(2)

    return [
        {
            "id": "1",
            "title": "Happy Song",
            "artist": "Artist A",
            "duration": 215000,
            "image_url": "https://example.com/happy_song.jpg",
            "audio_url": "https://example.com/happy_song.mp3"
        },
        {
            "id": "2",
            "title": "Joyful Tune",
            "artist": "Artist B",
            "duration": 215000,
            "image_url": "https://example.com/joyful_tune.jpg",
            "audio_url": "https://example.com/joyful_tune.mp3"
        },
        {
            "id": "3",
            "title": "Upbeat Melody",
            "artist": "Artist C",
            "duration": 30000,
            "image_url": "https://example.com/upbeat_melody.jpg",
            "audio_url": "https://example.com/upbeat_melody.mp3"
        }
    ]
        