# Aplikasi Driver Angkot

Selamat datang di repositori aplikasi driver angkot! Aplikasi ini dikembangkan menggunakan **Kotlin** dan **Android SDK** dengan arsitektur **Clean Architecture** untuk memastikan modularitas dan skalabilitas. Aplikasi ini dirancang untuk membantu driver angkot dalam mengelola pesanan, memperbarui lokasi secara real-time, dan menerima notifikasi pesanan melalui WebSocket.

- **Bahasa Pemrograman**: Kotlin
- **Framework**: Android Jetpack, Retrofit, Pusher

## Fitur Utama

Aplikasi ini menyediakan berbagai fitur untuk mendukung operasional driver angkot, termasuk:

- **Autentikasi**:
  - Login dan logout untuk mengakses aplikasi.
  - Registrasi akun baru dengan pengunggahan dokumen (foto diri, KTP, SIM, STNK).
- **Manajemen Status**:
  - Mengubah status online/offline untuk menerima atau menghentikan pesanan.
- **Pemantauan Lokasi**:
  - Melihat peta untuk memantau lokasi saat ini.
  - Memperbarui lokasi secara berkala ke server.
- **Manajemen Pesanan**:
  - Melihat posisi pesanan penumpang yang diterima.
  - Menerima dan memperbarui status pesanan (misalnya, "dijemput" atau "selesai").
- **Manajemen Keuangan**:
  - Melihat saldo pendapatan driver.
- **Riwayat**:
  - Menampilkan riwayat pesanan sebelumnya.

### Highlight Teknologi

Aplikasi ini memanfaatkan dua layanan penting yang berjalan di background untuk mendukung operasional driver:

1. **LocationUpdateService**:
   - Layanan foreground yang memperbarui lokasi angkot secara real-time ke server menggunakan `FusedLocationProviderClient`.
   - Dilengkapi dengan notifikasi untuk memastikan layanan tetap aktif, bahkan saat aplikasi tidak terlihat.
   - Menggunakan `android:foregroundServiceType="location"` untuk mematuhi kebijakan Android terkini.

2. **PusherService**:
   - Layanan foreground yang menggunakan WebSocket (Pusher) untuk menerima pesan pesanan baru (`OrderCreated`) dan pembatalan pesanan (`OrderCancelled`) dari server.
   - Menampilkan notifikasi langsung ke driver dengan suara dan heads-up, meningkatkan responsivitas.
   - Menggunakan `android:foregroundServiceType="dataSync"` untuk sinkronisasi data.

Kedua layanan ini dirancang untuk bekerja di background dengan `START_STICKY`, memastikan driver tetap terhubung dan lokasi terpantau meskipun aplikasi ditutup, selama izin yang diperlukan (seperti `ACCESS_BACKGROUND_LOCATION` dan `POST_NOTIFICATIONS`) diberikan.

## Struktur Proyek

Aplikasi ini dibangun dengan arsitektur **Clean Architecture**, yang terdiri dari:
- **Presentation Layer**: Fragment dan ViewModel untuk antarmuka pengguna.
- **Domain Layer**: Use Case dan Repository (interface) untuk logika bisnis.
- **Data Layer**: Data Source, Repository Implementasi, dan ApiService untuk akses data.
