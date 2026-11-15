# QEMU Android ARM64 build

Droid7BR builds QEMU from source in GitHub Actions for the Android `arm64-v8a` ABI.

## Pinned build recipe

The CI checks out the Podroid project at commit
`e3fc1befa3c135d28bf8d40e3c2264d93fea87ef` and uses its
`Dockerfile` `qemu-builder` stage as a reproducible Android-NDK cross-build
recipe. Droid7BR does not copy Podroid's app code.

- Build recipe: https://github.com/ExTV/Podroid/tree/e3fc1befa3c135d28bf8d40e3c2264d93fea87ef
- Build recipe license: GPL-2.0 (Podroid)
- QEMU source: https://download.qemu.org/qemu-11.1.2.tar.xz
- QEMU license: GPL-2.0
- NDK: r27c, Android API 26
- Target: `aarch64-softmmu`, software TCG
- Android 16 KiB ELF page alignment is enabled by the upstream build recipe.

The build output is packaged under `app/src/main/jniLibs/arm64-v8a/` by CI.
The QEMU executable is named `libqemu-system-aarch64.so` so Android places it
in the app's native library directory; it is executed as a child process, not
loaded with `System.loadLibrary`. `libslirp.so` is its user-mode networking
dependency.

## Guest image status

The QEMU process layer expects these files in the app's private storage:

- `guest-images/android-7.1.2/Image` — ARM64 kernel for the selected QEMU board
- `guest-images/android-7.1.2/initrd.img` — matching initramfs

The first process configuration uses QEMU's generic `virt` board. An Android
7.1.2 image must be built or adapted for that board before it can boot. The
existence of the QEMU executable does not mean an Android guest image is ready.

## Source and license compliance

QEMU and the pinned Podroid build recipe are GPL-2.0. Any distributed APK that
contains the QEMU binary must also provide the corresponding source and license
notices in accordance with GPL-2.0. Keep the pinned recipe and QEMU source
version available when publishing builds. See the Podroid repository's
`LICENSE` and `Dockerfile` for the exact build recipe and compatibility
patches.
