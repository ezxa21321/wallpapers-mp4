# XD Wallpaper Engine
<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android_8.0+-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Platform" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Compose" />
  <img src="https://img.shields.io/badge/CI%2FCD-GitHub_Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white" alt="CI/CD" />
</p>
Легковесный и энергоэффективный движок живых обоев для Android с нативной поддержкой цикличных **MP4-видео** и собственного формата контейнеров **`.xd`** размером до **100 МБ**.
* **Высокая производительность:** Прямой рендеринг видеопотока через системный `WallpaperService` и `MediaPlayer` без лишней нагрузки на ОЗУ.
* **Энергосбережение:** Воспроизведение автоматически ставится на паузу при перекрытии другими приложениями или выключении экрана; системный звук заглушен по умолчанию.
* **Собственный формат `.xd`:** Поддержка специализированных контейнеров для удобного распространения обоев вместе с метаданными.
* **Файлы до 100 МБ:** Потоковая обработка и распаковка без риска переполнения буфера памяти (`OutOfMemoryError`).
* **Material 3 UI:** Чистый интерфейс управления на Jetpack Compose в актуальном стиле Material You.
* **Zero-Setup сборка:** Готовый пайплайн GitHub Actions для автоматической компиляции `.apk` прямо в репозитории.
