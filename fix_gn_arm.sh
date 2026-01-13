#!/bin/bash
set -x

cd ~/cobalt/cobalt

# 禁用 RBE
echo "use_remoteexec = false" >> out/android-arm_qa/args.gn

# 重新生成
gn gen out/android-arm_qa

# 列出可能的主要目标
echo "=== Searching for main build targets ==="
ninja -C out/android-arm_qa -t targets all | grep -E "^(all|cobalt|gn_all)" | grep -v "\.o:"

echo ""
echo "=== Searching for cobalt groups ==="
gn ls out/android-arm_qa --type=group | grep -i cobalt | head -20
