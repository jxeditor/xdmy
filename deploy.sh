#!/bin/bash
# deploy.sh — 本机执行，构建前后端并部署到远程服务器
# 用法：./deploy.sh
# 前提：已配置 SSH 免密登录（ssh-copy-id root@124.223.70.175）

set -e

# ── 配置 ──────────────────────────────────────────────────────
SERVER="root@124.223.70.175"
REMOTE_BACKUP="/root/project/xdmy"       # 远程备份路径
REMOTE_APP="/opt/xdmy"                   # 实际运行路径
NGINX_CONF_REMOTE="/etc/nginx/conf.d/xdmy.conf"
SYSTEMD_REMOTE="/etc/systemd/system/xdmy.service"
PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"

# ── 颜色输出 ─────────────────────────────────────────────────
GREEN='\033[0;32m'; YELLOW='\033[1;33m'; RED='\033[0;31m'; NC='\033[0m'
step() { echo -e "\n${YELLOW}>>> [$1/$TOTAL] $2${NC}"; }
ok()   { echo -e "${GREEN}    ✓ $1${NC}"; }
TOTAL=6

# ── [1/6] 构建后端 ────────────────────────────────────────────
step 1 "构建后端 JAR"
cd "$PROJECT_ROOT"
mvn clean package -DskipTests -q
JAR_FILE=$(ls target/xdmy*.jar 2>/dev/null | head -1)
[ -z "$JAR_FILE" ] && { echo -e "${RED}ERROR: JAR not found${NC}"; exit 1; }
ok "JAR: $JAR_FILE"

# ── [2/6] 构建前端 ────────────────────────────────────────────
step 2 "构建前端静态文件"
cd "$PROJECT_ROOT/ui"
npm run build --silent
ok "dist/ 生成完毕"

# ── [3/6] 上传到远程备份路径 ──────────────────────────────────
step 3 "上传文件到远程备份路径 $SERVER:$REMOTE_BACKUP"
ssh "$SERVER" "mkdir -p $REMOTE_BACKUP/web $REMOTE_BACKUP/deploy"

# 后端 JAR
scp -q "$PROJECT_ROOT/$JAR_FILE" "$SERVER:$REMOTE_BACKUP/xdmy.jar"
ok "JAR → $REMOTE_BACKUP/xdmy.jar"

# 前端静态文件
if command -v rsync &>/dev/null; then
    rsync -az --delete "$PROJECT_ROOT/ui/dist/" "$SERVER:$REMOTE_BACKUP/web/"
    ok "前端 → $REMOTE_BACKUP/web/  (rsync)"
else
    scp -qr "$PROJECT_ROOT/ui/dist/." "$SERVER:$REMOTE_BACKUP/web/"
    ok "前端 → $REMOTE_BACKUP/web/  (scp)"
fi

# Nginx 配置 & systemd 服务文件
scp -q "$PROJECT_ROOT/xdmy.nginx.conf" "$SERVER:$REMOTE_BACKUP/deploy/xdmy.nginx.conf"
scp -q "$PROJECT_ROOT/xdmy.service"    "$SERVER:$REMOTE_BACKUP/deploy/xdmy.service"
ok "配置文件 → $REMOTE_BACKUP/deploy/"

# ── [4/6] 从备份路径同步到运行路径 ───────────────────────────
step 4 "从备份路径同步到运行路径"
ssh "$SERVER" "
set -e
mkdir -p $REMOTE_APP/logs $REMOTE_APP/web

cp -f  $REMOTE_BACKUP/xdmy.jar                    $REMOTE_APP/xdmy.jar
cp -rf $REMOTE_BACKUP/web/.                        $REMOTE_APP/web/
cp -f  $REMOTE_BACKUP/deploy/xdmy.nginx.conf       $NGINX_CONF_REMOTE
cp -f  $REMOTE_BACKUP/deploy/xdmy.service          $SYSTEMD_REMOTE
echo '  文件同步完成'
"
ok "JAR / 前端 / xdmy.conf / xdmy.service 已就位"

# ── [5/6] 校验 Nginx 配置 ─────────────────────────────────────
step 5 "校验 Nginx 配置"
ssh "$SERVER" "nginx -t" && ok "Nginx 配置语法正确"

# ── [6/6] 启动 / 重启服务 ────────────────────────────────────
step 6 "启动 / 重启远程服务"
ssh "$SERVER" "
set -e

nginx -s reload
echo '  Nginx 已热加载'

systemctl daemon-reload
systemctl enable xdmy --quiet
systemctl restart xdmy
sleep 2

if systemctl is-active --quiet xdmy; then
    echo '  xdmy 服务运行中'
else
    echo '[ERROR] xdmy 启动失败，最近日志：'
    journalctl -u xdmy -n 20 --no-pager
    exit 1
fi
"

# ── 完成 ──────────────────────────────────────────────────────
echo -e "\n${GREEN}=============================="
echo -e "  部署完成"
echo -e "==============================${NC}"
echo -e "  前端地址：${GREEN}http://124.223.70.175:8888${NC}"
echo -e "  后端接口：${GREEN}http://124.223.70.175:8080${NC}"
echo -e "  远程备份：${YELLOW}$SERVER:$REMOTE_BACKUP${NC}"
