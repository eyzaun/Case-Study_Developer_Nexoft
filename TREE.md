# Project Tree: PhoneBook (updated)

```
PhoneBook/
├─ .gitignore
├─ .gradle/
├─ .idea/
├─ .kotlin/
├─ app/
│  ├─ .gitignore
│  ├─ build/
│  │  ├─ generated/
│  │  ├─ intermediates/
│  │  ├─ kotlin/
│  │  ├─ kspCaches/
│  │  ├─ outputs/
│  │  └─ tmp/
│  ├─ build.gradle.kts
│  ├─ proguard-rules.pro
│  └─ src/
│     ├─ androidTest/
│     ├─ main/
│     │  ├─ AndroidManifest.xml
│     │  ├─ java/
│     │  │  └─ com/
│     │  │     └─ nexoft/
│     │  │        └─ phonebook/
│     │  │           ├─ MainActivity.kt
│     │  │           ├─ PhoneBookApplication.kt
│     │  │           ├─ data/
│     │  │           │  ├─ local/
│     │  │           │  │  ├─ dao/
│     │  │           │  │  │  ├─ ContactDao.kt
│     │  │           │  │  │  └─ SearchHistoryDao.kt
│     │  │           │  │  ├─ database/
│     │  │           │  │  │  └─ PhoneBookDatabase.kt
│     │  │           │  │  └─ entity/
│     │  │           │  │     ├─ ContactEntity.kt
│     │  │           │  │     └─ SearchHistoryEntity.kt
│     │  │           │  ├─ mapper/
│     │  │           │  │  └─ ContactMapper.kt
│     │  │           │  ├─ remote/
│     │  │           │  │  ├─ api/
│     │  │           │  │  │  └─ ContactsApi.kt
│     │  │           │  │  ├─ dto/
│     │  │           │  │  │  ├─ ApiModels.kt
│     │  │           │  │  │  └─ ContactDto.kt
│     │  │           │  │  └─ interceptor/       (empty)
│     │  │           │  └─ repository/
│     │  │           │     └─ ContactRepositoryImpl.kt
│     │  │           ├─ di/
│     │  │           │  ├─ AppModule.kt
│     │  │           │  ├─ DatabaseModule.kt
│     │  │           │  ├─ NetworkModule.kt
│     │  │           │  ├─ RepositoryModule.kt
│     │  │           │  └─ UseCaseModule.kt
│     │  │           ├─ domain/
│     │  │           │  ├─ model/
│     │  │           │  │  └─ Contact.kt
│     │  │           │  ├─ repository/
│     │  │           │  │  └─ ContactRepository.kt
│     │  │           │  └─ usecase/
│     │  │           │     ├─ AddContactUseCase.kt
│     │  │           │     ├─ CheckDeviceContactsUseCase.kt
│     │  │           │     ├─ DeleteContactUseCase.kt
│     │  │           │     ├─ GetAllContactsUseCase.kt
│     │  │           │     ├─ SaveToDeviceContactsUseCase.kt
│     │  │           │     ├─ SearchContactsUseCase.kt
│     │  │           │     ├─ UpdateContactUseCase.kt
│     │  │           │     └─ UploadImageUseCase.kt
│     │  │           ├─ presentation/
│     │  │           │  ├─ components/
│     │  │           │  │  ├─ ContactListItem.kt
│     │  │           │  │  ├─ EmptyState.kt
│     │  │           │  │  ├─ GroupHeader.kt
│     │  │           │  │  └─ SearchBar.kt
│     │  │           │  ├─ navigation/
│     │  │           │  │  └─ PhoneBookNavigation.kt
│     │  │           │  ├─ screens/
│     │  │           │  │  ├─ addcontact/
│     │  │           │  │  │  └─ AddEditContactScreen.kt
│     │  │           │  │  ├─ contacts/
│     │  │           │  │  │  └─ ContactsScreen.kt
│     │  │           │  │  └─ profile/           (empty)
│     │  │           │  └─ viewmodel/
│     │  │           │     ├─ AddEditContactViewModel.kt
│     │  │           │     ├─ ContactsViewModel.kt
│     │  │           │     └─ ProfileViewModel.kt
│     │  │           ├─ ui/
│     │  │           │  └─ theme/
│     │  │           │     ├─ Color.kt
│     │  │           │     ├─ Dimens.kt
│     │  │           │     ├─ Shape.kt
│     │  │           │     ├─ Theme.kt
│     │  │           │     └─ Type.kt
│     │  │           └─ utils/                    (empty)
│     │  └─ res/
│     │     ├─ drawable/
│     │     ├─ mipmap-anydpi-v26/
│     │     ├─ mipmap-hdpi/
│     │     ├─ mipmap-mdpi/
│     │     ├─ mipmap-xhdpi/
│     │     ├─ mipmap-xxhdpi/
│     │     ├─ mipmap-xxxhdpi/
│     │     ├─ values/
│     │     │  ├─ colors.xml
│     │     │  ├─ strings.xml
│     │     │  └─ themes.xml
│     │     └─ xml/
│     │        ├─ backup_rules.xml
│     │        └─ data_extraction_rules.xml
│     └─ test/
├─ build/
│  └─ reports/
├─ build.gradle.kts
├─ gradle/
│  ├─ libs.versions.toml
│  └─ wrapper/
│     ├─ gradle-wrapper.jar
│     └─ gradle-wrapper.properties
├─ gradle.properties
├─ gradlew
├─ gradlew.bat
├─ local.properties
├─ settings.gradle.kts
└─ TREE.md
```
