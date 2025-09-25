# Technical Overview (Short & Friendly)

Bu proje Kotlin + Jetpack Compose ile yazıldı. Yapı sade: ekranlar (Compose) ve ViewModel’ler, domain (use case + modeller), data (Retrofit + Room + repository). Hilt ile bağımlılık yönetimi yapılıyor.

## Kullanılan teknoloji
- UI: Compose Material3, Navigation
- DI: Hilt
- Ağ: Retrofit/OkHttp (Gson)
- Yerel veri: Room
- Medya/UX: Coil (görseller), Lottie (ekleme başarısı animasyonu)

## İstenenler ve nasıl yaptım
- Ekle/Düzenle/Sil akışları: Form doğrulamaları eklendi, resim seçimi (galeri/kamera) ve ekleme sonrası tek seferlik Lottie ekranı ile geri dönüş sağlandı.
- Kişiler listesi ve arama: Sunucudan veri çekiliyor, Room’a yazılıyor, cihaz kişileriyle eşleştirilip "telefon rehberinde var" göstergesi işleniyor. Arama geçmişi tutuluyor ve temizlenebiliyor.
- Sola kaydırma ile işlemler: Listedeki öğede sola kaydırınca Düzenle/Sil aksiyonları çıkıyor.
- Profil ekranı: Kişi detayı, "Telefonuma Kaydet" butonu (tekrar kaydetmeyi engelleyen kontrol), fotoğrafı değiştir linki ve silme onayı alt sayfası eklendi.
- Marka rengi ve UI: Talep doğrultusunda mavi vurgu rengine geçildi; üst-sağ küçük “+” butonu kullanıldı; alt kısımda özel toast gösterimleri yapıldı.

## Veri akışı (kısaca)
1) API’den kişiler alınır → domain modele dönüştürülür → Room’a yazılır.
2) Cihaz rehberi ile senkronize edilip `isInDeviceContacts` işaretlenir.
3) ViewModel’ler StateFlow ile UI’ya durumu verir; UI reaktif olarak güncellenir.
4) Ekle/Düzenle/Sil işlemlerinde repository API’yi çağırır, ardından yereli günceller.

## Repository davranışı
- Liste: Sunucudan gelen veri ile yerel tamamen yenilenir, sonrasında cihaz kişisi bayrakları birleştirilir; UI daima DB’den okur.
- Tekil kayıt: `isInDeviceContacts` bilgisi kaybolmaması için mevcut yerel veriden korunarak kayıt güncellenir.

## Derleme ve CI
- compile/target SDK 35; GitHub Actions push’ta debug APK üretir, etiketlerde release yükler; CI, debug keystore’u hazırlar.

## Çalıştırma
- Android Studio ile aç ve çalıştır.
- Komut satırı (Windows): `gradlew.bat :app:assembleDebug`
- Yayın paketi (AAB): `gradlew.bat :app:bundleRelease` → `app/build/outputs/bundle/release/app-release.aab`
