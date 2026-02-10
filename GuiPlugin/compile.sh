#!/bin/bash

PLUGIN_NAME="GuiPlugin"
PAPER_API="/home/f/Desktop/mc-server/libraries/io/papermc/paper/paper-api/1.21.11-R0.1-SNAPSHOT/paper-api-1.21.11-R0.1-SNAPSHOT.jar"

# Build classpath with all required dependencies
CLASSPATH="$PAPER_API"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/kyori/adventure-api/4.25.0/adventure-api-4.25.0.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/kyori/adventure-key/4.25.0/adventure-key-4.25.0.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/kyori/adventure-text-serializer-plain/4.25.0/adventure-text-serializer-plain-4.25.0.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/kyori/adventure-text-serializer-gson/4.25.0/adventure-text-serializer-gson-4.25.0.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/kyori/adventure-text-serializer-legacy/4.25.0/adventure-text-serializer-legacy-4.25.0.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/kyori/adventure-text-serializer-json/4.25.0/adventure-text-serializer-json-4.25.0.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/kyori/adventure-text-minimessage/4.25.0/adventure-text-minimessage-4.25.0.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/kyori/adventure-text-logger-slf4j/4.25.0/adventure-text-logger-slf4j-4.25.0.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/kyori/adventure-text-serializer-ansi/4.25.0/adventure-text-serializer-ansi-4.25.0.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/kyori/adventure-text-serializer-commons/4.25.0/adventure-text-serializer-commons-4.25.0.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/kyori/examination-api/1.3.0/examination-api-1.3.0.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/kyori/examination-string/1.3.0/examination-string-1.3.0.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/md-5/bungeecord-chat/1.21-R0.2-deprecated+build.21/bungeecord-chat-1.21-R0.2-deprecated+build.21.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/org/slf4j/slf4j-api/2.0.17/slf4j-api-2.0.17.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/com/google/code/gson/gson/2.13.2/gson-2.13.2.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/com/google/errorprone/error_prone_annotations/2.41.0/error_prone_annotations-2.41.0.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/net/kyori/option/1.1.0/option-1.1.0.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/it/unimi/dsi/fastutil/8.5.18/fastutil-8.5.18.jar"
CLASSPATH="$CLASSPATH:/home/f/Desktop/mc-server/libraries/org/joml/joml/1.10.8/joml-1.10.8.jar"

echo "Classpath: $CLASSPATH"

SRC_DIR="/home/f/Desktop/mc-server/plugins-source/GuiPlugin/src"
BUILD_DIR="/home/f/Desktop/mc-server/plugins-source/GuiPlugin/target"
OUTPUT_DIR="/home/f/Desktop/mc-server/plugins"

# Clean and create build directory
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/classes"

# Find all Java files
echo "Finding Java files..."
find "$SRC_DIR/main/java" -name "*.java" > "$BUILD_DIR/sources.txt"

# Compile Java files
echo "Compiling..."
javac -cp "$CLASSPATH" -d "$BUILD_DIR/classes" @$BUILD_DIR/sources.txt

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

# Create jar - include everything in classes directory
echo "Creating JAR..."
cd "$BUILD_DIR/classes"
jar cvf "$BUILD_DIR/$PLUGIN_NAME.jar" .

# Copy to plugins folder
cp "$BUILD_DIR/$PLUGIN_NAME.jar" "$OUTPUT_DIR/"

echo "Done! Plugin copied to: $OUTPUT_DIR/$PLUGIN_NAME.jar"
