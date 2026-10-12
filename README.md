# Jardim das Descobertas 🌱

Um jogo Android para explorar letras, sons de animais, bichinhos, cores e formas, pensado para uma criança de três anos brincar com um responsável. Ilustrações vetoriais originais, movimentos suaves, botões grandes e voz em português do aparelho. Sem cadastro, anúncios, compras, analytics ou permissões de rede.

## Brincadeiras

- **Alfabeto:** explore as 26 letras de A a Z, com palavra, figura e nome falado em português. Depois, encontre letras em rodadas curtas com três alternativas.
- **Sons dos bichinhos:** escute gravações reais de gato, cachorro e vaca e escolha o animal correspondente. O modelo e a narração da resposta só aparecem após o acerto. Toque para ouvir novamente.

- **Bichinhos:** encontre gato, passarinho e coelhinho, com modelo visual e orientação por voz.
- **Flores coloridas:** combine as flores azul, amarela e verde.
- **Formas divertidas:** toque ou arraste círculo, triângulo e quadrado até o encaixe. Tocar oferece alternativa ao arraste.

Cada rodada contém três descobertas. Uma tentativa diferente convida a observar novamente; não perde pontos nem tempo. Avançar exige uma escolha explícita. No alfabeto, cada rodada sorteia três letras distintas entre as 26; cada desafio oferece o alvo e dois distratores. Os alvos e sua ordem variam a cada nova brincadeira. A conclusão mostra o jardim com suas flores.

## Android e execução

Android 8.0+ (API 26), JDK 17, SDK 35, Kotlin 2.1.20, Compose e Gradle 8.11.1.

```sh
./gradlew :domain:test :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`. A esteira publica `jardim-das-descobertas-debug-apk` e executa testes instrumentados em emulador API 29.

Os **sons de animais estão embutidos em Ogg/Vorbis** e funcionam offline, independentemente da voz do aparelho. Arquivos originais sem edição, volume de reprodução reduzido. Veja [autores, fontes e licenças](docs/audio-credits.md); os créditos também aparecem offline na área de responsáveis. Se o som estiver desativado pelo responsável, a atividade de escuta mostra orientação para ativá-lo, sem permitir respostas silenciosas.

A narração de letras e instruções usa uma voz **offline em português** instalada no Android. Sem ela, palavras, letras e figuras continuam disponíveis e as gravações de animais continuam tocando. A área de responsáveis informa a disponibilidade e abre a configuração de voz do Android. O app não baixa recursos nem solicita acesso à internet. O motor de fala é um serviço do aparelho, fora do processo do app.

## Arquitetura

MVVM e injeção por construtor. O módulo `domain` contém catálogo, contratos e máquina de estados puros. `GardenViewModel` publica `GardenUiState` via StateFlow. `GardenFactory` monta as dependências, e os adapters `PreferenceSettings`, `SpeechGuide` e `AnimalAudioPlayer` isolam Android e IO. A UI observa estado, desenha os personagens e envia eventos de toque/arraste.

Patterns: State machine para a sessão, Strategy para o catálogo, Repository para configurações, Adapter para preferências/voz/gravações, Factory para composição e Observer para apresentação. Os contratos pequenos permitem testes com fakes sem Android. As escolhas não usam números de ponto flutuante como pontuação ou timers; não existe avaliação de desempenho da criança.

## Responsáveis, privacidade e acessibilidade

A área de responsáveis oferece controle de som e movimento reduzido. A conta matemática é uma barreira simples para configurações; não é autenticação ou proteção de segurança. A narração e as gravações são interrompidas ao sair do app e seus recursos são liberados quando a composição é destruída. TalkBack recebe descrições para os botões e modelos. As brincadeiras aceitam toque, e o arraste é opcional. Telas rolam em aparelhos pequenos.

Não há coleta de nome, idade, desempenho, fotos, microfone, identificadores ou eventos; não há backend. Apenas preferências de som/movimento ficam no aparelho. O jogo é recreativo, não um instrumento para avaliar ou prometer resultados de desenvolvimento. Sugestões na área de responsáveis convidam a continuar a brincadeira fora da tela.

## Validação

Unit tests cobrem o alfabeto completo, sorteios de letras, correspondência entre animais e gravações, todas as atividades, alvos disponíveis, tentativas sem punição, toque repetido, avanço explícito, conclusão e preferências restauradas. Testes instrumentados cobrem cores, arraste, a área de responsáveis, exploração até Z, um desafio de letras, bloqueio sem som, resposta oculta antes do acerto e decodificação/reprodução das três gravações. Veja [validação](docs/validation.md). O workflow executa testes, lint e build antes de disponibilizar o APK.

Criado por **breda_dev**, com ilustrações e código originais. Identidade de launcher compartilhada com os outros projetos do portfólio.
