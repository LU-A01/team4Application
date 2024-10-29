#!/bin/bash

# TensorFlow Liteをビルドするためのスクリプト

# 環境変数の設定（必要に応じて変更）
export ANDROID_NDK_HOME=/opt/android-ndk-r21e
export ANDROID_API_LEVEL=30
export ANDROID_ABI=arm64-v8a

# ビルドディレクトリの作成
mkdir -p /tensorflow/tflite_build
cd /tensorflow/tflite_build

# TensorFlow Liteのソースコードを取得
git clone https://github.com/tensorflow/tensorflow.git

# 必要な依存関係をインストール（これが必要な場合）
cd tensorflow/tensorflow/lite
bazel build -c opt //tensorflow/lite:libtensorflowlite.so

# ビルドの成果物を適切なディレクトリにコピー
# ここで必要に応じて成果物を指定する
cp -r bazel-bin/tensorflow/lite/libtensorflowlite.so /tensorflow/tflite_build/

echo "TensorFlow Liteのビルドが完了しました。"
