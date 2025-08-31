#!/bin/bash

echo "🌿 Building LeafCare Android App..."

# Clean build
echo "🧹 Cleaning previous build..."
./gradlew clean

# Build debug APK
echo "🔨 Building debug APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    
    # Check if device is connected
    echo "📱 Checking device connection..."
    adb devices | grep -q "device$"
    
    if [ $? -eq 0 ]; then
        echo "📱 Device found! Installing app..."
        
        # Uninstall previous version
        echo "🗑️ Uninstalling previous version..."
        adb uninstall com.example.green
        
        # Install new version
        echo "📦 Installing new version..."
        adb install app/build/outputs/apk/debug/app-debug.apk
        
        if [ $? -eq 0 ]; then
            echo "✅ Installation successful!"
            echo "🚀 Launching LeafCare..."
            adb shell am start -n com.example.green/.MainActivity
            echo "🎉 LeafCare is now running on your device!"
            echo ""
            echo "📋 App Info:"
            echo "   - Package: com.example.green"
            echo "   - Version: 1.0"
            echo "   - Target SDK: 35 (Android 15)"
            echo "   - Min SDK: 21 (Android 5.0)"
        else
            echo "❌ Installation failed!"
            exit 1
        fi
    else
        echo "❌ No device connected! Please connect your Android device via USB and enable USB debugging."
        exit 1
    fi
else
    echo "❌ Build failed!"
    exit 1
fi
