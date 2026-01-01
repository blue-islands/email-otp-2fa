# otp-web-spring

`otp-web-spring` は、**Spring Boot / Spring MVC アプリケーションにメールOTP（2FA / MFA）を後付けするための共通ライブラリ**です。

このモジュールは、Servlet 版（`otp-web-servlet`）と同じ思想で作られており、  
**既存のログイン処理（ID/パスワード認証）を変更せずに MFA を追加**できます。

---

## 提供するもの

- OTP開始（発行 → 保存 → メール送信）Controller
- OTP検証（検証 → 状態更新 → セッションに MFA 完了を記録）Controller
- MFA未完了ユーザーを OTP 画面へ誘導する Filter
- Spring Boot 用 Auto Configuration
- Web 設定用 `@ConfigurationProperties`
- メールテンプレート（差し替え可能）

> 依存モジュール
> - `otp-core` : OTP生成・検証の純ロジック
> - `otp-storage` : OTP状態の永続化（JDBC / Redis 等）
> - `otp-mail` : メール送信（Simple Java Mail）

---

## 1. 全体の動作フロー

1. アプリ側で **ID / パスワード認証に成功**
2. セッションに「パスワード認証済み」を記録
3. MFA画面 (`/mfa`) で「確認コード送信」
4. メールで OTP を受信
5. OTP 入力 → 検証成功
6. セッションに `MFA_OK=true` が入り、保護URLへアクセス可能になる

---

## 2. セッションキー（重要）

### 2.1 アプリ側がセットするキー

ID/パスワード認証成功時に、**必ず**以下をセッションにセットしてください。

- `OtpSessionKeys.USER_ID`
- `OtpSessionKeys.USER_EMAIL`
- `OtpSessionKeys.PASSWORD_OK = true`

### 推奨：ヘルパーメソッドを使用

```java
import jakarta.servlet.http.HttpSession;
import jp.livlog.otp.web.spring.OtpWebSupport;

public void onPasswordLoginSuccess(HttpSession session, String userId, String email) {
    OtpWebSupport.markPasswordOk(session, userId, email);
}
````

---

## 3. 提供されるエンドポイント

デフォルトでは以下のエンドポイントが有効になります。

| 用途             | HTTP | パス            |
| -------------- | ---- | ------------- |
| MFA画面（アプリ側で用意） | GET  | `/mfa`        |
| OTP送信          | POST | `/mfa/start`  |
| OTP検証          | POST | `/mfa/verify` |

※ パスは `application.yml` で変更可能です。

---

## 4. Spring Boot での組み込み方法

### 4.1 必須 Bean（アプリ側で用意）

`otp-web-spring` は以下の Bean を **アプリ側に要求**します。

* `OtpChallengeStore`（例：JDBC / Redis）
* `OtpMailer`（例：Simple Java Mail 実装）

#### 例：JDBC + Simple Java Mail

```java
@Configuration
public class OtpAppConfig {

    @Bean
    public OtpChallengeStore otpChallengeStore(DataSource dataSource) {
        return new JdbcOtpChallengeStore(dataSource);
    }

    @Bean
    public OtpMailer otpMailer() {
        SmtpConfig smtp = new SmtpConfig(
            "smtp.example.com", 587,
            "user", "pass",
            SmtpConfig.Transport.STARTTLS,
            "no-reply@example.com", "YourApp",
            10000, 10000, 10000
        );
        return new SimpleJavaMailOtpMailer(smtp);
    }
}
```

---

## 5. application.yml 設定例

```yaml
otp:
  web:
    app-name: "YourApp"
    mfa-page-path: "/mfa"
    start-path: "/mfa/start"
    verify-path: "/mfa/verify"
    success-redirect: "/app"
    failure-redirect: "/mfa?error=1"
    protected-path-prefixes:
      - "/app"
```

---

## 6. MFA画面（/mfa）の実装

`/mfa` 画面は **アプリ側で自由に実装**します
（Thymeleaf / JSP / 静的HTML など）。

### 6.1 OTP送信フォーム

```html
<form action="/mfa/start" method="post">
  <button type="submit">確認コードを送信</button>
</form>
```

### 6.2 OTP入力フォーム

```html
<form action="/mfa/verify" method="post">
  <label>確認コード</label>
  <input name="otp"
         inputmode="numeric"
         pattern="[0-9]{6}"
         maxlength="6"
         required />
  <button type="submit">確認</button>
</form>
```

---

## 7. Filter の挙動（MfaEnforcerFilter）

`protected-path-prefixes` に一致する URL に対して：

* `PASSWORD_OK=true` かつ `MFA_OK!=true`
  → MFA画面 (`/mfa`) にリダイレクト
* `MFA_OK=true`
  → 通過

以下の URL は常に通過します。

* `/mfa`
* `/mfa/start`
* `/mfa/verify`

---

## 8. セキュリティ運用上の注意

* OTPコードをログに出力しない
* OTP有効期限は短め（例：5分）
* 試行回数制限を有効化する
* 本番環境では HTTPS 前提
* ログイン成功時は **セッション再生成（Session Fixation 対策）** を推奨

---

## 9. よくあるミス

| 症状            | 原因                        |
| ------------- | ------------------------- |
| MFA画面に遷移しない   | Filter が有効になっていない         |
| 401 / 400 エラー | USER_ID / USER_EMAIL が未設定 |
| 何度もOTPを要求される  | MFA_OK をセットしていない          |

---

## 10. otp-web-servlet との違い

| 項目         | servlet       | spring               |
| ---------- | ------------- | -------------------- |
| 登録方法       | 手動（Listener）  | AutoConfig           |
| Controller | HttpServlet   | @RestController      |
| Filter     | javax/jakarta | OncePerRequestFilter |
| 設定         | Javaコード       | application.yml      |

---

## ライセンス / 利用

このモジュールは **横展・再利用を前提**としています。
商用・非商用問わず、プロジェクトの MFA 実装のベースとして利用できます。

