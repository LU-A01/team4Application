FROM ubuntu:20.04
MAINTAINER Kaichi Sasaki

# 環境変数を設定
ENV ANDROID_NDK_HOME=/opt/android-ndk-r21e
ENV ANDROID_API_LEVEL=30
ENV ANDROID_ABI=arm64-v8a
ENV DEBIAN_FRONTEND=noninteractive

# 必要なパッケージをインストール
RUN apt-get update && \
    apt-get install -y --fix-missing \
    git \
    wget \
    unzip \
    openjdk-11-jdk \
    python3 \
    python3-pip \
    build-essential \
    cmake \
    ninja-build \
    android-sdk \
    android-tools-adb \
    android-tools-fastboot \
    && rm -rf /var/lib/apt/lists/*

# Pythonパッケージのインストール
RUN pip3 install --upgrade pip
RUN pip3 install numpy six wheel tensorflow

# Android NDKをダウンロードして展開
RUN wget https://dl.google.com/android/repository/android-ndk-r21e-linux-x86_64.zip && \
    unzip android-ndk-r21e-linux-x86_64.zip -d /opt && \
    rm android-ndk-r21e-linux-x86_64.zip

# TensorFlowリポジトリをクローン
RUN git clone https://github.com/tensorflow/tensorflow.git /tensorflow

# TensorFlowのブランチを指定（例えばv2.5.0）
WORKDIR /tensorflow
RUN git checkout v2.5.0

# ビルド用のスクリプトを作成
RUN mkdir -p /tensorflow/tflite_build

# ビルドの準備
WORKDIR /tensorflow/tensorflow/lite/tools/make

# スクリプトをコンテナにコピー
COPY build_aarch64.sh /tensorflow/build_aarch64.sh

# スクリプトに実行権限を付与
RUN chmod +x /tensorflow/build_aarch64.sh

# TensorFlow Lite for Androidのビルド
RUN /tensorflow/build_aarch64.sh

# Android SDKとGradleをインストール
RUN wget https://services.gradle.org/distributions/gradle-6.5-bin.zip -P /tmp && \
    unzip -d /opt/gradle /tmp/gradle-6.5-bin.zip && \
    rm /tmp/gradle-6.5-bin.zip

# Gradle環境変数の設定
ENV GRADLE_HOME=/opt/gradle/gradle-6.5
ENV PATH=$PATH:$GRADLE_HOME/bin

# Android SDKのダウンロードとセットアップ
RUN wget https://dl.google.com/android/repository/commandlinetools-linux-7302050_latest.zip && \
    mkdir -p /android/sdk/cmdline-tools && \
    unzip commandlinetools-linux-7302050_latest.zip -d /android/sdk/cmdline-tools && \
    rm commandlinetools-linux-7302050_latest.zip && \
    mv /android/sdk/cmdline-tools/cmdline-tools /android/sdk/cmdline-tools/latest

# Android SDKのライセンスを自動承認
RUN yes | /android/sdk/cmdline-tools/latest/bin/sdkmanager --licenses

# 必要なSDKをインストール
RUN /android/sdk/cmdline-tools/latest/bin/sdkmanager "platforms;android-30" "build-tools;30.0.3" "platform-tools"
