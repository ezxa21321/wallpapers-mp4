# 🎬 XD Wallpaper Engine
<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android_8.0+-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Platform" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Compose" />
  <img src="https://img.shields.io/badge/CI%2FCD-GitHub_Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white" alt="CI/CD" />
</p>
Легковесный и энергоэффективный движок живых обоев для Android с нативной поддержкой цикличных **MP4-видео** и собственного формата контейнеров **`.xd`** размером до **100 МБ**.
---
## ✨ Ключевые возможности
* **Высокая производительность:** Прямой рендеринг видеопотока через системный `WallpaperService` и `MediaPlayer` без лишней нагрузки на ОЗУ.
* **Энергосбережение:** Воспроизведение автоматически ставится на паузу при перекрытии другими приложениями или выключении экрана; системный звук заглушен по умолчанию.
* **Собственный формат `.xd`:** Поддержка специализированных контейнеров для удобного распространения обоев вместе с метаданными.
* **Файлы до 100 МБ:** Потоковая обработка и распаковка без риска переполнения буфера памяти (`OutOfMemoryError`).
* **Material 3 UI:** Чистый интерфейс управления на Jetpack Compose в актуальном стиле Material You.
* **Zero-Setup сборка:** Готовый пайплайн GitHub Actions для автоматической компиляции `.apk` прямо в репозитории.
---
## 📦 Спецификация формата `.xd`
Формат **`.xd`** представляет собой оптимизированный ZIP-контейнер со строгой структурой:
```text
my_wallpaper.xd
├── manifest.json      # Метаданные обоев
└── video.mp4          # Основной видеопоток (до 100 МБ)
```
### Пример `manifest.json`:
```json
{
  "title": "Neon City Skyline",
  "author": "Designer Name",
  "version": "1.0",
  "loop": true
}
```
> **Как создать свой `.xd` файл вручную:**  
> Переименуйте `video.mp4` в `video.mp4`, добавьте `manifest.json`, упакуйте оба файла в обычный `.zip` без сжатия или с базовым сжатием и смените расширение архива на `.xd`.
---
## 🛠 Архитектура и стек технологий

| Компонент | Назначение |
| :--- | :--- |
| **Kotlin + Coroutines** | Язык приложения и асинхронные операции ввода-вывода |
| **Jetpack Compose (M3)** | Реактивный пользовательский интерфейс |
| **Android WallpaperService** | Системный сервис интеграции живых обоев |
| **Android MediaPlayer API** | Аппаратно-ускоренное декодирование видео в `SurfaceHolder` |
| **Storage Access Framework** | Безопасная работа с файловой системой устройства |

---
## 🚀 Сборка APK через GitHub Actions
Приложение настроено на автоматическую сборку без необходимости локальной установки Android Studio:
1. Сделайте `push` изменений в ветку `main` или `master`.
2. Перейдите во вкладку **Actions** в верхней панели репозитория.
3. Откройте последний запущенный воркфлоу **Build Wallpaper App APK**.
4. После завершения задачи скачайте готовый APK из блока **Artifacts** (`XDWallpaper-Debug`).
---
## 📱 Локальная сборка (для разработчиков)
Если требуется собрать проект на компьютере:
```bash
# Клонирование репозитория
git clone [https://github.com/ВАШ_ЛОГИН/ВАШ_РЕПОЗИТОРИЙ.git](https://github.com/ВАШ_ЛОГИН/ВАШ_РЕПОЗИТОРИЙ.git)
cd XDWallpaper
# Сборка Debug APK
./gradlew assembleDebug
# Установка на подключенное устройство
./gradlew installDebug
```
Готовый файл после сборки будет находиться по пути:  
`app/build/outputs/apk/debug/app-debug.apk`
---
## 📄 Лицензия
Распространяется под лицензией [MIT](LICENSE). Вы можете свободно модифицировать и использовать этот движок в своих проектах.
