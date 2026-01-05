# Mavenプラグインとしての配布設定について

このリポジトリでは、JitPack だけでは Maven プラグインとして正しく解決されない問題に対応するため、以下の設定を追加しています。

- **GitHub Packages への公開設定**: 親 `pom.xml` の `<distributionManagement>` で `github` リポジトリを指定し、GitHub Actions から `mvn deploy` すると `maven.pkg.github.com/livlog-llc/email-otp-2fa` へ成果物を配置できるようにしました。
- **プラグインリポジトリの明示**: `<pluginRepositories>` に `jitpack.io` と Maven Central を追加し、JitPack 由来の成果物も Maven プラグインとして解決できるようにしました。プラグイン座標を使う場合は `pluginRepositories` の設定が必須です。
- **自動デプロイのワークフロー**: `.github/workflows/publish.yml` を追加し、Release 公開または `workflow_dispatch` で GitHub Packages に自動デプロイします。テストは省略し、サンプルモジュール（`otp-servlet-sample` / `otp-spring-sample`）もデプロイ対象から除外しています。

## 利用時のポイント
1. GitHub Packages から取得する場合は、`~/.m2/settings.xml` に以下を追加して `GITHUB_TOKEN` を使った認証を有効化してください。
   ```xml
   <servers>
     <server>
       <id>github</id>
       <username>${env.GITHUB_ACTOR}</username>
       <password>${env.GITHUB_TOKEN}</password>
     </server>
   </servers>
   ```
2. プロジェクト側の `pom.xml` では、依存としてだけでなく **Maven プラグインとして利用する場合も** `pluginRepositories` に `jitpack.io` または `github` を追加してください。
3. このリポジトリの親 `pom.xml` で `groupId/artifactId/version` が宣言されているため、プラグインの解決先として JitPack と GitHub Packages の両方を使えます。企業内の Nexus/Artifactory などへミラーする場合も、どちらかのリポジトリを上流として登録すれば動作します。

## 公開コマンドの例
GitHub Packages へ手動でアップロードしたい場合は、親ディレクトリで以下を実行します（サンプルモジュールは除外）。

```bash
./mvnw -B -DskipTests -pl '!otp-servlet-sample,!otp-spring-sample' -am deploy
```

Release タグを作成して GitHub Actions を走らせれば、自動で同じコマンドが実行されます。
