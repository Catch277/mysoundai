import asyncio
from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles
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
            "artist": "SoundHelix",
            "duration": 300000,
            "image_url": "https://i.pinimg.com/736x/ed/ad/58/edad5830245602a18c73a6843b6079ba.jpg",
            "audio_url": "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        },
        {
            "id": "2",
            "title": "Joyful Tune",
            "artist": "SoundHelix",
            "duration": 320000,
            "image_url": "https://picsum.photos/201",
            "audio_url": "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
        },
        {
            "id": "3",
            "title": "Upbeat Melody",
            "artist": "SoundHelix",
            "duration": 290000,
            "image_url": "https://picsum.photos/202",
            "audio_url": "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
        }
    ]