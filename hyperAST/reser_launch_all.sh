cargo build --release
mkdir $1
mkdir $2

# https://github.com/apache/maven/commit/be2b7f890d98af20eb0753650b6605a68a97ac05
sleep 1
target/release/reserializing apache/maven "" be2b7f890d98af20eb0753650b6605a68a97ac05 "" > $1/maven 2> $2/maven &
# https://github.com/INRIA/spoon/commit/56e12a0c0e0e69ea70863011b4f4ca3305e0542b
sleep 1
target/release/reserializing INRIA/spoon "" 56e12a0c0e0e69ea70863011b4f4ca3305e0542b "" > $1/spoon 2> $2/spoon &
# https://github.com/quarkusio/quarkus/commit/5ac8332061fbbd4f11d5f280ff12b65fe7308540
sleep 1
target/release/reserializing quarkusio/quarkus "" 5ac8332061fbbd4f11d5f280ff12b65fe7308540 "" > $1/quarkus 2> $2/quarkus &
# https://github.com/apache/logging-log4j2/commit/ebfc8945a5dd77b617f4667647ed4b740323acc8
sleep 1
target/release/reserializing apache/logging-log4j2 "" ebfc8945a5dd77b617f4667647ed4b740323acc8 "" > $1/logging-log4j2 2> $2/logging-log4j2 &
# https://github.com/javaparser/javaparser/commit/046bf8be251189452ad6b25bf9107a1a2167ce6f
sleep 1
target/release/reserializing javaparser/javaparser "" 046bf8be251189452ad6b25bf9107a1a2167ce6f "" > $1/javaparser 2> $2/javaparser &
# https://github.com/apache/spark/commit/885f4733c413bdbb110946361247fbbd19f6bba9
sleep 1
target/release/reserializing apache/spark "" 885f4733c413bdbb110946361247fbbd19f6bba9 "" > $1/spark 2> $2/spark &
# https://github.com/google/gson/commit/f79ea208b1a42d0ee9e921dcfb3694221a2037ed
sleep 1
target/release/reserializing google/gson "" f79ea208b1a42d0ee9e921dcfb3694221a2037ed "" > $1/gson 2> $2/gson &
# https://github.com/junit-team/junit4/commit/cc7c500584fcb85eaf98c568b7441ceac6dd335c
sleep 1
target/release/reserializing junit-team/junit4 "" cc7c500584fcb85eaf98c568b7441ceac6dd335c "" > $1/junit4 2> $2/junit4 &
# https://github.com/jenkinsci/jenkins/commit/be6713661c120c222c17026e62401191bdc4035c
sleep 1
target/release/reserializing jenkinsci/jenkins "" be6713661c120c222c17026e62401191bdc4035c "" > $1/jenkins 2> $2/jenkins &
# https://github.com/apache/dubbo/commit/e831b464837ae5d2afac9841559420aeaef6c52b
sleep 1
target/release/reserializing apache/dubbo "" e831b464837ae5d2afac9841559420aeaef6c52b "" > $1/dubbo 2> $2/dubbo &
# https://github.com/apache/skywalking/commit/38a9d4701730e674c9646173dbffc1173623cf24
sleep 1
target/release/reserializing apache/skywalking "" 38a9d4701730e674c9646173dbffc1173623cf24 "" > $1/skywalking 2> $2/skywalking &
# https://github.com/apache/flink/commit/d67338a140bf1b744d95a514b82824bba5b16105
sleep 1
target/release/reserializing apache/flink "" d67338a140bf1b744d95a514b82824bba5b16105 "" > $1/flink 2> $2/flink &
# https://github.com/aws/aws-sdk-java/commit/4f416d58e07c15875cf73ac873d2fe20e89ad6df
sleep 1
#target/release/reserializing aws/aws-sdk-java "" 4f416d58e07c15875cf73ac873d2fe20e89ad6df "" > $1/aws-sdk-java 2> $2/aws-sdk-java &
# https://github.com/aws/aws-sdk-java/commit/1409b1fbac5436fdc62b56442487df351592db17
# target/release/reserializing aws/aws-sdk-java "" 1409b1fbac5436fdc62b56442487df351592db17 "" > $1/aws-sdk-java 2> $2/aws-sdk-java &
# https://github.com/aws/aws-sdk-java/commit/0b01b6c8139e050b36ef79418986cdd8d9704998
# target/release/reserializing aws/aws-sdk-java "" 0b01b6c8139e050b36ef79418986cdd8d9704998 "" > $1/aws-sdk-java 2> $2/aws-sdk-java &
# https://github.com/aws/aws-sdk-java-v2/commit/edea5de18755962cb864cb4c88652ec8748d877c
sleep 1
target/release/reserializing aws/aws-sdk-java-v2 "" edea5de18755962cb864cb4c88652ec8748d877c "" > $1/aws-sdk-java-v2 2> $2/aws-sdk-java-v2 &
# https://github.com/aws/aws-toolkit-eclipse/commit/85417f68e1eb6d90d46e145229e390cf55a4a554
# mkdir $1/aws-toolkit-eclipse
# sleep 1
# target/release/reserializing aws/aws-toolkit-eclipse "" 85417f68e1eb6d90d46e145229e390cf55a4a554 "" > $1/aws-toolkit-eclipse 2> $2/aws-toolkit-eclipse &
# https://github.com/netty/netty/commit/c2b846750dd2131d65aa25c8cf66bf3649b248f9
sleep 1
target/release/reserializing netty/netty "" c2b846750dd2131d65aa25c8cf66bf3649b248f9 "" > $1/netty 2> $2/netty &
# # https://github.com/alibaba/fastjson/commit/f56b5d895f97f4cc3bd787c600a3ee67ba56d4db
# mkdir $1/fastjson 
# sleep 1
# target/release/reserializing alibaba/fastjson "" f56b5d895f97f4cc3bd787c600a3ee67ba56d4db "" > $1/fastjson 2> $2/fastjson &
# https://github.com/alibaba/arthas/commit/c661d2d24892ce8a09a783ca3ba82eda90a66a85
sleep 1
target/release/reserializing alibaba/arthas "" c661d2d24892ce8a09a783ca3ba82eda90a66a85 "" > $1/arthas 2> $2/arthas &
# https://github.com/google/guava/commit/30a8aed9b9e263dd23cf546befed9552779c9cbc
sleep 1
# # target/release/reserializing google/guava "" 30a8aed9b9e263dd23cf546befed9552779c9cbc "" > $1/guava 2> $2/guava &
# # https://github.com/google/guava/commit/da1aa6184f15a070e327cccb0172f7567448bfa1
# # target/release/reserializing google/guava "" da1aa6184f15a070e327cccb0172f7567448bfa1 "" > $1/guava 2> $2/guava &
# # https://github.com/google/guava/commit/b30a7120f901b4a367b8a9839a8b8ba62457fbdf
# target/release/reserializing google/guava "" b30a7120f901b4a367b8a9839a8b8ba62457fbdf "" > $1/guava 2> $2/guava &

# https://github.com/apache/hadoop/commit/cee8c62498f55794f911ce62edfd4be9e88a7361
sleep 1
#target/release/reserializing apache/hadoop "" cee8c62498f55794f911ce62edfd4be9e88a7361 "" > $1/hadoop 2> $2/hadoop &
# https://github.com/apache/hadoop/commit/c65c383b7ebef48c638607f15ba35d61554982cb
#target/release/reserializing apache/hadoop "" c65c383b7ebef48c638607f15ba35d61554982cb "" > $1/hadoop 2> $2/hadoop &
# https://github.com/apache/hadoop/commit/d5e97fe4d6baf43a5576cbd1700c22b788dba01e
target/release/reserializing apache/hadoop "" d5e97fe4d6baf43a5576cbd1700c22b788dba01e "" > $1/hadoop 2> $2/hadoop &

# https://github.com/FasterXML/jackson-core/commit/3cb5ce818e476d5b0b504b1833c7d33be80e9ca4
sleep 1
target/release/reserializing FasterXML/jackson-core "" 3cb5ce818e476d5b0b504b1833c7d33be80e9ca4 "" > $1/jackson-core 2> $2/jackson-core &
# https://github.com/qos-ch/slf4j/commit/2b0e15874aaf5502c9d6e36b0b81fc6bc14a8531
sleep 1
target/release/reserializing qos-ch/slf4j "" 2b0e15874aaf5502c9d6e36b0b81fc6bc14a8531 "" > $1/slf4j 2> $2/slf4j &
# https://github.com/jacoco/jacoco/commit/62a2b556c26f0f42a2ae791a86dc39dd36d35392
sleep 1
target/release/reserializing jacoco/jacoco "" 62a2b556c26f0f42a2ae791a86dc39dd36d35392 "" > $1/jacoco 2> $2/jacoco &


