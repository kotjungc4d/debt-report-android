# 📱 คู่มือสร้างและติดตั้งไฟล์ DebtReport.apk บน Android

โปรเจกต์ **DebtReport** (com.fintech.debtreport v1.2.0) รองรับการรันบนมือถือ Android เต็มรูปแบบ 3 วิธี:

---

## ⚡ วิธีที่ 1: ติดตั้งเป็น Android WebAPK ทันที (ไม่ต้องคอมไพล์ · แนะนำ)
1. เปิดลิงก์แอปบนมือถือ Android ด้วยเบราว์เซอร์ **Google Chrome**:
   `https://ais-dev-re33l6aaftlp3h5fymvshi-747894951104.asia-northeast1.run.app`
2. กดปุ่ม **"ติดตั้งแอป Android (WebAPK)"** ด้านบนของการ์ดหลัก หรือกดเมนูมุมขวาบนของ Chrome (⋮) > **"ติดตั้งแอป" (Install app)**
3. ระบบ Android จะสร้างและติดตั้งแพ็กเกจ **WebAPK** ลงในหน้ารวมแอป (App Drawer) และหน้าจอหลักทันที
   - แสดงผลเต็มจอไร้แถบเบราว์เซอร์ (`display: standalone`)
   - รองรับระบบสั่น Haptic Feedback (`navigator.vibrate`) และเสียงสังเคราะห์ PCM 16-bit
   - ทำงานแบบ Offline 100% ด้วย Service Worker & LocalStorage DataStore

---

## ☁️ วิธีที่ 2: สร้างไฟล์ `.apk` อัตโนมัติผ่าน GitHub Actions (ฟรี ไม่ต้องลงโปรแกรมในคอม)
ในชุดไฟล์ ZIP นี้มีไฟล์ **`.github/workflows/build-android-apk.yml`** เตรียมไว้ให้แล้ว:
1. สร้าง Repository ใหม่บน GitHub แล้วอัปโหลดไฟล์ทั้งหมดในโฟลเดอร์นี้ขึ้นไป
2. ไปที่แท็บ **Actions** บน GitHub > เลือกเวิร์กโฟลว์ **"Build Android APK (DebtReport)"** > กด **Run workflow**
3. รอประมาณ 2 นาที แล้วดาวน์โหลดไฟล์ **`DebtReport-debug.apk`** จากส่วน **Artifacts** ไปติดตั้งลงมือถือ Android ได้ทันที!

---

## 💻 วิธีที่ 3: คอมไพล์ไฟล์ `.apk` ในเครื่องด้วย Android Studio หรือ Capacitor
### ทางเลือก A: เปิดโปรเจกต์ Android Native / WebView ใน Android Studio
1. แตกไฟล์ ZIP นี้ แล้วเปิดโฟลเดอร์ใน **Android Studio**
2. เลือกเมนู **Build > Build Bundle(s) / APK(s) > Build APK(s)**
3. ไฟล์ APK จะอยู่ที่ `app/build/outputs/apk/debug/app-debug.apk`

### ทางเลือก B: สร้างด้วย Capacitor CLI จากโปรเจกต์เว็บ
```bash
npm install @capacitor/core @capacitor/cli @capacitor/android
npx cap add android
npm run build
npx cap sync android
npx cap open android
```
