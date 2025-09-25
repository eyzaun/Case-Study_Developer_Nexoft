## Proje Yapısı (Net ve Okunur)

Bu dosya, projede neyin nerede olduğunu hızlıca görmeniz için hazırlandı. Derleme çıktıları ve geçici klasörler gösterilmez.

```
Case-Study_Developer_Nexoft/
├─ app/
│  ├─ build.gradle.kts                 # Uygulama modülü ayarları (SDK 35, bağımlılıklar)
│  ├─ proguard-rules.pro               # R8/ProGuard ayarları
│  └─ src/
│     ├─ main/
│     │  ├─ AndroidManifest.xml
│     │  ├─ java/com/nexoft/phonebook/
│     │  │  ├─ PhoneBookApplication.kt # Hilt Application
│     │  │  ├─ MainActivity.kt         # Giriş noktası + Navigation Host
│     │  │  ├─ data/                   # Veri katmanı
│     │  │  │  ├─ remote/              # Retrofit API ve DTO'lar
│     │  │  │  ├─ local/               # Room DB, DAO, Entity'ler
│     │  │  │  ├─ mapper/              # DTO/Entity ↔ Domain eşlemeleri
│     │  │  │  └─ repository/          # ContactRepositoryImpl (kaynak birleştirme)
│     │  │  ├─ domain/                 # Saf domain: model, arayüz, use-case'ler
│     │  │  ├─ presentation/           # Jetpack Compose UI
│     │  │  │  ├─ components/          # Tekrar kullanılabilir UI parçaları (Toast, SearchBar…)
│     │  │  │  ├─ navigation/          # PhoneBookNavigation
│     │  │  │  └─ screens/
│     │  │  │     ├─ contacts/         # ContactsScreen
│     │  │  │     └─ addcontact/       # AddEditContactScreen, AddSuccessScreen
│     │  │  ├─ presentation/viewmodel/ # Contacts, AddEdit, Profile ViewModel'leri
│     │  │  ├─ ui/theme/               # Tema sistemi (Color/Dimens/Shape/Theme/Type)
│     │  │  └─ utils/                  # Yardımcılar (DeviceContactsHelper, Formatter…)
│     │  └─ res/
│     │     ├─ values/                 # strings.xml, themes.xml, colors.xml
│     │     ├─ raw/                    # Lottie animasyonları (success_animation.json, done.json)
│     │     └─ mipmap/, drawable/, xml/ # İkon ve konfigürasyonlar
│     ├─ test/                         # Örnek unit testler
│     └─ androidTest/                  # Örnek instrumented testler
├─ docs/
│  ├─ TECHNICAL_OVERVIEW.md            # Kısa teknik notlar
│  └─ privacy-policy.md                # Örnek politika
├─ .github/
│  └─ workflows/android.yml            # CI: push'ta debug APK, tag'te release
├─ build.gradle.kts                    # Kök Gradle yapılandırması
├─ settings.gradle.kts                 # Modül tanımları
├─ gradle.properties                   # Gradle/Proje bayrakları
├─ gradle/
│  ├─ libs.versions.toml               # Versiyon katalogu
│  └─ wrapper/
│     ├─ gradle-wrapper.jar
│     └─ gradle-wrapper.properties
├─ gradlew, gradlew.bat                # Gradle wrapper komutları
└─ README.md                           # Kısa proje özeti ve kurulum
```

### Klasörler Ne İşe Yarar?
- app/src/main/java/com/nexoft/phonebook/data: Uzaktan (Retrofit) ve yerel (Room) veri kaynakları, eşlemeler ve repository implementasyonu.
- app/src/main/java/com/nexoft/phonebook/domain: Domain modeli, repository arayüzü ve use-case'ler (iş kuralları).
- app/src/main/java/com/nexoft/phonebook/presentation: Compose ekranlar, navigation ve UI bileşenleri.
- app/src/main/java/com/nexoft/phonebook/presentation/viewmodel: ViewModel'ler ve UI durum yönetimi.
- app/src/main/java/com/nexoft/phonebook/ui/theme: Renk, tipografi, şekil ve tema tanımları.
- app/src/main/java/com/nexoft/phonebook/utils: Telefon numarası formatlama, cihaz kişileriyle entegrasyon vb. yardımcılar.
- app/src/main/res: String'ler, temalar, renkler, ikonlar ve animasyonlar.
- docs: Teknik notlar ve metin dokümanları.
- .github/workflows: CI yapılandırmaları.

### Ekran ↔ ViewModel Eşleşmeleri
- ContactsScreen → ContactsViewModel
- AddEditContactScreen → AddEditContactViewModel
- Profile (profile.kt) → ProfileViewModel

### Veri Akışı (Kısaca)
API (Retrofit) ↔ Repository ↔ Room önbellek → Domain use‑case → ViewModel → Compose UI.
Cihaz kişisi bilgisi, DeviceContactsHelper ile birleştirilerek listedeki öğelere yansıtılır.

### Sık Arananlar
- Renkleri/temayı değiştirme: `app/src/main/java/.../ui/theme/` içindeki `Color.kt`, `Theme.kt`, `Type.kt`, `Shape.kt`, `Dimens.kt`
- Metinler: `app/src/main/res/values/strings.xml`
- Navigasyon: `app/src/main/java/.../presentation/navigation/PhoneBookNavigation.kt`
- Liste öğeleri ve alt bileşenler: `presentation/components/`

Bu dosya “tek bakışta” proje haritasıdır. Detaylı teknik açıklamalar için `docs/TECHNICAL_OVERVIEW.md` dosyasına bakabilirsiniz.
