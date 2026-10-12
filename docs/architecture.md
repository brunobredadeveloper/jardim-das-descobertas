# Decisões de arquitetura

## Dependências

`app` depende de `domain`; o domínio não depende de Android, Compose ou armazenamento. A composição fica em `GardenFactory`. `GardenGame` depende de `ChallengeCatalog`, não de um catálogo concreto. `GardenViewModel` depende do caso de uso e de `SettingsRepository`. Estado da sessão é imutável.

## Invariantes

Um desafio contém seu alvo e não repete opções. Alvos de outra atividade não modificam a sessão. Um acerto não pode ser contado novamente por dois toques rápidos. Uma tentativa diferente não avança nem remove descobertas. A sessão só avança por ação explícita e conclui após três desafios.

## SOLID

SRP separa regras, apresentação e adapters. OCP permite trocar catálogo e preferências por novos adapters. LSP preserva os contratos de criação e gravação, exercitados com fakes. ISP mantém dois contratos pequenos e independentes. DIP injeta os contratos na composição, sem acesso global a serviços pelo ViewModel.

## Alternativas de interação

O arraste considera coordenadas em relação à raiz do layout; o encaixe recebe o centro do item solto. Toque está sempre disponível como alternativa. As atividades visuais oferecem modelos sem exigir fala instalada. A atividade de sons não revela o modelo até o acerto e exige som ativado pelo responsável. O alfabeto usa um catálogo independente com 26 letras, nomes falados, palavras e figuras. Animações são desativadas no modo de movimento reduzido. Não há lógica de vitória baseada em animações ou strings da UI.

## Voz e ciclo de vida

São selecionadas somente vozes em português que declaram não exigir conexão. A indisponibilidade é exibida aos responsáveis; as atividades visuais continuam jogáveis e os sons de animais não dependem do TTS. Parar o app interrompe a fala, e fechar a composição libera o motor TTS. Preferências ficam locais; não há conta de criança ou Firebase.

## Gravações e ciclo de vida

`AnimalAudioPlayer` encapsula MediaPlayer, recursos locais e foco transitório de áudio. Uma única reprodução fica ativa; repetir ou navegar libera o player anterior. Callbacks de players antigos são ignorados por identidade. Preparação assíncrona e falhas têm estado explícito para a UI. A coordenação interrompe a fala antes de reproduzir uma gravação, e não fala o nome do alvo antes de acertar. O observador de ciclo de vida interrompe ambos ao entrar em segundo plano.

## Limites da versão

Não há narração humana embutida; são incluídas três gravações reais de animais. A configuração de volume do Android e o alto-falante permanecem necessários para a atividade de sons. Os personagens são vetores desenhados em Canvas, animados por Compose. A área de responsáveis não oferece proteção forte. O processo encerrado não restaura uma rodada inacabada; voltar oferece uma nova sessão curta, e configurações são preservadas.
