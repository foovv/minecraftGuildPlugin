#!/bin/bash

PLUGIN_NAME="GildiePlugin"
PAPER_API="/home/f/Desktop/mc-server/libraries/io/papermc/paper/paper-api/1.21.11-R0.1-SNAPSHOT/paper-api-1.21.11-R0.1-SNAPSHOT.jar"

# Build classpath
CLASSPATH="$PAPER_API"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/com/google/code/gson/gson/2.13.2/gson-2.13.2.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/kyori/adventure-api/4.25.0/adventure-api-4.25.0.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/kyori/adventure-key/4.25.0/adventure-key-4.25.0.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/kyori/adventure-text-serializer-plain/4.25.0/adventure-text-serializer-plain-4.25.0.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/kyori/adventure-text-serializer-legacy/4.25.0/adventure-text-serializer-legacy-4.25.0.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/md-5/bungeecord-chat/1.21-R0.2-deprecated+build.21/bungeecord-chat-1.21-R0.2-deprecated+build.21.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/kyori/examination-api/1.3.0/examination-api-1.3.0.jar"


SRC_DIR="/home/f/Desktop/mc-server/plugins-source/GildiePlugin/src"
BUILD_DIR="/home/f/Desktop/mc-server/plugins-source/GildiePlugin/target"
OUTPUT_DIR="/home/f/Desktop/mc-server/plugins"

# Clean and create build directory
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/classes"

# Find all Java files
echo "Finding Java files..."
find "$SRC_DIR/main/java" -name "*.java" > "$BUILD_DIR/sources.txt"

# Compile Java files
echo "Compiling..."
javac -d "$BUILD_DIR/classes" -cp "$CLASSPATH" @$BUILD_DIR/sources.txt

# Check if compilation succeeded
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

# Copy resources to classes directory
if [ -d "$SRC_DIR/main/resources" ]; then
    echo "Copying resources..."
    cp -r "$SRC_DIR/main/resources/"* "$BUILD_DIR/classes/"
fi

# Create jar
echo "Creating JAR..."
cd "$BUILD_DIR/classes"
jar cvf "$BUILD_DIR/$PLUGIN_NAME.jar" .

# Copy to plugins folder
cp "$BUILD_DIR/$PLUGIN_NAME.jar" "$OUTPUT_DIR/"

echo "Done! Plugin copied to: $OUTPUT_DIR/$PLUGIN_NAME.jar"
