# Plano de integração do QEMU no Android

## Objetivo

Executar um processo `qemu-system-aarch64` dentro do processo/ambiente permitido do aplicativo Android, com arquivos da VM no armazenamento privado do Droid7BR.

## Estratégia em etapas

1. Validar a compilação de uma biblioteca JNI Android com NDK/CMake.
2. Compilar uma versão mínima do QEMU para o ABI Android `arm64-v8a`, inicialmente com alvo de sistema `aarch64-softmmu`.
3. Desabilitar componentes e dependências opcionais não necessários ao primeiro protótipo.
4. Empacotar o executável e suas bibliotecas/dependências de forma compatível com as regras de execução do Android.
5. Implementar criação e controle do processo QEMU, encerramento limpo e captura de logs.
6. Definir o hardware virtual e o formato de disco suportados pela imagem convidada.
7. Só então testar uma imagem Android convidada compatível.

## Pontos que ainda precisam ser provados

- O QEMU upstream precisa ser compilado e executado no ambiente Android; uma compilação Linux ARM64 não é automaticamente executável no Android.
- O NDK fornece toolchain Android, mas as dependências do QEMU também precisam ser compiladas para o mesmo ABI/API level.
- A existência de `/dev/kvm` não garante acesso ou compatibilidade com o QEMU. TCG é a alternativa de emulação por software, com impacto de desempenho.
- Android 7.1.2 precisa de uma imagem compatível com a máquina virtual escolhida. Uma imagem Android-x86 em host ARM64 exigiria tradução/emulação de arquitetura e não é a escolha inicial.
- A aceleração gráfica, áudio, rede, entrada por toque e integração de clipboard serão etapas separadas.
- GApps e Magisk só serão adicionados depois que a imagem base iniciar de forma repetível.

## Referências técnicas

- QEMU build system: https://www.qemu.org/docs/master/devel/build-system
- QEMU configure options: https://github.com/qemu/qemu/blob/master/configure
- Meson cross compilation: https://mesonbuild.com/Cross-compilation.html
- Android NDK: https://developer.android.com/ndk

## Estado

A ponte JNI e o projeto CMake inicial existem. O QEMU ainda não está compilado nem integrado. O gerenciador de armazenamento cria discos RAW esparsos por instância; isso não equivale a uma imagem Android inicializável.
