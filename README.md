## 编译
```shell
cd $PROJECT_PATH/xdmy
mvn clean package

cd $PROJECT_PATH/xdmy/ui
vue build
```

## 上传
```shell
scp $PROJECT_PATH/xdmy/target/xdmy.jar root@124.223.70.175:/root
scp -r $PROJECT_PATH/xdmy/ui/dist root@124.223.70.175:/root 
```

## 发布
```shell
ssh root@124.223.70.175
cat start-xdmy.sh
echo "" > /root/nohup.out
rm -rf /usr/share/nginx/html/dist
cp -R /root/dist /usr/share/nginx/html/
nginx -s reload
ps -ef|grep xdmy.jar|grep -v grep|awk '{print $2}'|xargs kill -9
java -jar /root/xdmy.jar

nohup sh start-xdmy.sh &
```