# 🎵 MySoundAI

> A Spotify-inspired music streaming app powered by AI — featuring smart recommendations, Text-to-Speech support, and a seamless listening experience.

---

## ✨ Features

- 🎶 **Music Playback** — Stream and control music with a smooth, intuitive player
- 🤖 **AI Music Recommendations** — Personalized song suggestions powered by AI
- 🗣️ **Text-to-Speech (TTS)** — Natural voice narration via ElevenLabs integration
- ☁️ **Cloud Backend** — Fast and scalable FastAPI backend with Firebase support

---

## 🛠️ Tech Stack

### Android (Frontend)
| | |
|---|---|
| Language | Kotlin |
| Min SDK | API 24 (Android 7.0) |
| IDE | Android Studio |

### Backend
| | |
|---|---|
| Language | Python 3.11 |
| Framework | FastAPI |
| Server | Uvicorn |
| Database | Firebase |
| TTS | ElevenLabs API |

---

## 📁 Project Structure

```
MySoundAI/
├── android-app/        # Kotlin Android application
│   ├── app/
│   ├── build.gradle.kts
│   └── ...
├── backend-server/     # Python FastAPI backend
│   ├── app/
│   ├── main.py
│   ├── requirements.txt
│   └── .env.example
└── README.md
```

---

## 🚀 Getting Started

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/Catch277/MySoundAI.git
   cd MySoundAI/backend-server
   ```

2. **Create virtual environment**
   ```bash
   python -m venv venv
   venv\Scripts\activate      # Windows
   # source venv/bin/activate  # macOS/Linux
   ```

3. **Install dependencies**
   ```bash
   pip install -r requirements.txt
   ```

4. **Configure environment variables**
   ```bash
   cp .env.example .env
   # Edit .env and fill in your API keys
   ```

5. **Run the server**
   ```bash
   uvicorn main:app --reload
   ```
   API will be available at `http://localhost:8000`

---

### Android Setup

1. Open the `android-app` folder in **Android Studio**
2. Let Gradle sync complete
3. Update the backend API URL in the project config to point to your server
4. Run on an emulator or physical device (Android 7.0+)

---

## ⚙️ Environment Variables

Create a `.env` file in `backend-server/` based on `.env.example`:

```env
ELEVENLABS_API_KEY=your_elevenlabs_api_key
FIREBASE_CREDENTIALS=your_firebase_credentials_path
SECRET_KEY=your_secret_key
```

---

## 📡 API Docs

Once the backend is running, visit:
- Swagger UI: `http://localhost:8000/docs`
- ReDoc: `http://localhost:8000/redoc`

---

## 👤 Author

**Catch277**
- GitHub: [@Catch277](https://github.com/Catch277)
