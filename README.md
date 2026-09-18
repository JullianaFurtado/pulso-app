# Pulso

Aplicativo Android para controle de hábitos saudáveis — hidratação, alimentação e atividades físicas — desenvolvido como parte da Atividade Extensionista II (UNINTER), sob o tema **"Promovendo o Autocuidado por Meio da Tecnologia"** (ODS 03 — Saúde e Bem-Estar).

O projeto tem como comunidade de referência os moradores de Nova Iguaçu (RJ), com potencial de uso por escolas, grupos comunitários e unidades básicas de saúde interessados na promoção de hábitos saudáveis.

## Funcionalidades

- **Hidratação** — registro do consumo diário de água
- **Refeições** — controle e histórico de alimentação
- **Treinos** — registro de atividades físicas
- **Estatísticas** — acompanhamento visual dos hábitos ao longo do tempo
- **Perfil do usuário** — dados e preferências pessoais
- **Lembretes** — notificações configuráveis para manter a rotina de hidratação

## Tecnologias

- **Kotlin** + **Jetpack Compose** (UI declarativa)
- **Room (SQLite)** — persistência local dos dados
- **WorkManager** — agendamento dos lembretes de hidratação
- **Coroutines** — operações assíncronas
- Desenvolvido com **Google AI Studio**

## Requisitos

- Android Studio (versão recente)
- minSdk 24 / targetSdk 36
- JDK 11

## Como executar

1. Clone o repositório:
   ```bash
   git clone https://github.com/enfps/pulso-app.git
   ```
2. Abra o projeto no Android Studio.
3. Aguarde a sincronização do Gradle.
4. Execute em um emulador ou dispositivo físico (`Run > Run 'app'`).

## Testes

O projeto inclui testes unitários, testes com Robolectric e testes de screenshot (Roborazzi):

```bash
./gradlew test
```

## Estrutura do projeto

```
app/src/main/java/com/example/
├── ui/                  # Telas em Compose (Dashboard, Água, Refeições, Treinos, Estatísticas, Perfil)
├── AppDatabase.kt       # Configuração do Room
├── Entities.kt          # Entidades (WaterLog, ActivityLog, MealLog)
├── PulsoDao.kt          # Data Access Objects
├── PulsoViewModel.kt    # Lógica de estado e regras de negócio
└── WaterReminderWorker.kt  # Lembretes via WorkManager
```

## Autores

- Julliana Silva Furtado
- Luis Eduardo Macedo Leite

Projeto acadêmico desenvolvido para o Centro Universitário Internacional UNINTER — Escola Superior Politécnica.
