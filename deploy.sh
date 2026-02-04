#!/bin/bash

# 设置GPG密钥ID和密码（从安全存储中读取）
export GPG_KEY_ID="61DC8D6EA6D3217A" 
#61DC8D6EA6D3217A
export GPG_PASSPHRASE="AAAAbbbb1111"

# 解决任何潜在的代理问题
export MAVEN_OPTS="-Djava.net.useSystemProxies=true"

# 关键：设置GPG TTY环境变量
export GPG_TTY=$(tty)

# 诊断步骤：测试GPG签名
echo "Testing GPG signature..."
echo "test" | gpg --batch --no-tty --passphrase "$GPG_PASSPHRASE" --local-user "$GPG_KEY_ID" --clearsign

# 主部署命令
mvn clean deploy -U -B -Dgpg.keyname="$GPG_KEY_ID" -Dgpg.passphrase="$GPG_PASSPHRASE" -Dgpg.executable="gpg" -Dgpg.useagent=false
