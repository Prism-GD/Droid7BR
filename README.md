# Droid7BR

**Droid7BR** é um projeto independente para criar e executar máquinas virtuais Android em dispositivos Android.

O objetivo é oferecer uma experiência de smartphone Android dentro de uma VM, com armazenamento persistente, aplicativos Google e acesso root por meio do Magisk.

> **Status:** em desenvolvimento inicial. A VM ainda não está pronta para uso. Compatibilidade, desempenho e processo de instalação da imagem do Android estão sendo avaliados.

## 🎯 Objetivo do projeto

Criar um aplicativo Android que permita configurar, iniciar e utilizar uma máquina virtual baseada no **Android 7.1.2 Nougat (API 25)**.

A interface da VM será semelhante à de um celular tradicional:

- Tela em orientação vertical (retrato).
- Barra de status com horário, bateria, Wi-Fi e notificações.
- Launcher Android clássico com ícones e grade de aplicativos.
- Barra de navegação inferior: Voltar, Início e Recentes.
- Área de armazenamento persistente para manter arquivos e dados entre inicializações.

## 🧩 Recursos planejados

- [ ] Aplicativo Android com interface em Kotlin e Jetpack Compose.
- [ ] Validar o build e a execução do QEMU ARM64 (pipeline de compilação e controlador TCG adicionados).
- [ ] Inicialização de uma imagem Android 7.1.2.
- [ ] Integração de Google Apps (GApps) compatíveis com a imagem.
- [ ] Acesso root dentro da VM por meio do Magisk.
- [ ] Armazenamento persistente por máquina virtual.
- [ ] Controles para iniciar, desligar e reiniciar a VM.
- [ ] Configurações de memória, armazenamento e resolução.
- [ ] Interface de smartphone em orientação vertical.

## 🛠️ Tecnologias e componentes

| Componente | Uso |
| --- | --- |
| Kotlin | Desenvolvimento do aplicativo Android |
| Jetpack Compose | Interface do aplicativo |
| QEMU | Motor de virtualização |
| Android 7.1.2 | Sistema convidado planejado |
| GApps | Aplicativos e serviços Google compatíveis |
| Magisk | Gerenciamento de root dentro da VM |

O QEMU ARM64 é compilado pelo GitHub Actions a partir do código-fonte e empacotado no APK. A execução e a compatibilidade do guest ainda precisam ser validadas; o kernel e o initrd Android 7.1.2 para a placa virtual ainda não foram produzidos. O suporte a aceleração por hardware depende dos recursos disponíveis no dispositivo e no kernel; sem essa aceleração, a emulação pode ser bastante lenta.

## 📱 Compatibilidade

A compatibilidade será definida durante os testes. O fato de um aparelho ser ARM64 não garante, por si só, que ele consiga executar uma VM Android ARM64 com aceleração por hardware.

## ⚠️ Aviso sobre GApps e Magisk

GApps e Magisk precisam ser compatíveis com a versão e a arquitetura da imagem Android utilizada. A integração será testada antes de ser considerada funcional. A distribuição de pacotes de terceiros também deverá respeitar suas respectivas licenças.

## 🚧 Desenvolvimento

O projeto está em fase de protótipo. A integração do processo QEMU foi iniciada, mas a imagem Android convidada ainda não está pronta. Recursos marcados como planejados ainda não devem ser considerados implementados.

## 📄 Licença

A licença do projeto e as licenças dos componentes de terceiros serão documentadas conforme a implementação avançar.
