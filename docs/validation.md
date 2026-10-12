# Validação da versão 0.2.0

- JDK 17, SDK/Build Tools 35, Gradle 8.11.1.
- Oito testes unitários aprovados: sete de domínio e um de apresentação/configurações.
- Cinco testes instrumentados aprovados, sem falhas ou testes ignorados.
- Lint executado sem erros bloqueantes; cinco avisos de recursos/compatibilidade e sugestões de APIs.
- Build debug concluído e assinatura do APK verificada. A assinatura é a mesma do APK local 0.1.0, permitindo instalação como atualização.
- Manifest sem permissões de internet, câmera ou microfone. A permissão interna de receiver gerada pelo AndroidX é uma permissão de assinatura, não uma solicitação de acesso ao usuário.
- Três arquivos Ogg/Vorbis originais decodificados no Android e reproduzidos até a conclusão. Durações: gato 0,779 s; cachorro 2,610 s; vaca 2,194 s. Hashes e fontes registrados em `audio-credits.json`.

Os testes instrumentados usam Android 10/API 29 e tela de 720 × 1280 pixels. Movimento reduzido e som inicialmente desligado estabilizam as jornadas; o teste da associação auditiva ativa o som e verifica que o modelo permanece oculto até o acerto. A suíte completa aprovou os cinco testes abaixo. Após ajustar o espaço das novas telas para manter o botão de avançar visível, as jornadas de alfabeto e sons foram repetidas no APK final. São cobertos:

1. Reprodução completa das três gravações locais com MediaPlayer.
2. Exploração do alfabeto até Z, seleção da figura/palavra e acerto em um desafio de letras.
3. Rodada completa de cores e barreira de acesso às configurações.
4. Orientação quando o som está desligado e revelação do animal somente após o acerto.
5. Encaixe de formas por arraste.

As capturas são do app executado no Android, não mockups. O teste de reprodução verifica decodificação e callbacks em emulador sem saída física de som; volume, alto-falante e inteligibilidade devem ser avaliados no celular. A narração das letras exige uma voz offline em português instalada no Android; os sons de animais não dependem dela.

Não houve publicação na Play Store. Build e testes foram executados localmente. O workflow para repetir a validação no GitHub está incluído; a execução remota continua dependendo da criação e do acesso ao repositório `jardim-das-descobertas`.
