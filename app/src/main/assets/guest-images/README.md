# Android 7.1.2 guest boot files

The QEMU ARM64 process backend currently expects two files in the app's private
directory `files/guest-images/android-7.1.2/`:

- `Image`: ARM64 Linux kernel configured for QEMU's generic `virt` machine.
- `initrd.img`: matching Android initramfs.

These files are not included yet. Do not rename a random Android image to these
names: the kernel, device tree, initramfs, partition layout and Android userspace
must all target the same virtual hardware.

Until a compatible Android 7.1.2 guest image is built and tested, QEMU can only
be smoke-tested with `--version`; the Start action must remain unavailable.
